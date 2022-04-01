package deanmyers.project.dealerwerx.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.RemoteException;

import androidx.core.util.Pair;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import deanmyers.project.dealerwerx.API.APIConsumer;
import deanmyers.project.dealerwerx.API.APIResponder;
import deanmyers.project.dealerwerx.API.ImageMedia;
import deanmyers.project.dealerwerx.API.Listing;
import deanmyers.project.dealerwerx.DealerwerxApplication;
import deanmyers.project.dealerwerx.NearMeListingsActivity;
import deanmyers.project.dealerwerx.PreferencesManager;
import deanmyers.project.dealerwerx.R;

import static android.app.AlarmManager.ELAPSED_REALTIME;
import static android.os.SystemClock.elapsedRealtime;

/**
 * Created by mac3 on 2016-11-29.
 */

public class BeaconService extends IntentService implements BeaconConsumer {
    public static enum Distance{
        IMMEDIATE,
        NEAR,
        FAR,
    };

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private final static String TAG = "BeaconService";
    private BackgroundPowerSaver bm;
    private BeaconManager beaconManager;
    private Region region;
    private boolean enabled;
    private Timer listingTimer;
    private ConcurrentHashMap<String, Pair<Pair<Beacon, Listing>, Date>> listingLifetime;

    private static BeaconService instance;


    public static BeaconService getInstance(){
        return instance;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setEnabled(boolean value) throws RemoteException {
        enabled = value;
        if(enabled && beaconManager.isBound(this))
            beaconManager.startRangingBeaconsInRegion(region);
        else if(!enabled && beaconManager.isBound(this))
            beaconManager.stopRangingBeaconsInRegion(region);
    }

    public BeaconService() {
        super(TAG);

        instance = this;
    }

    protected void onHandleIntent(Intent intent) {

    }

    public boolean hasDetectedBeacon(String key){
        return listingLifetime.containsKey(key);
    }

    public Distance getDistance(String key){
        Pair<Pair<Beacon, Listing>, Date> data = listingLifetime.get(key);
        if(data == null)
            return null;
        Beacon b = data.first.first;

        double distance = b.getDistance();

        if(distance < 5)
            return Distance.IMMEDIATE;
        else if(distance < 10.0)
            return Distance.NEAR;
        else
            return Distance.FAR;
    }

    public boolean containsListingId(int id){
        for(Pair<Pair<Beacon,Listing>, Date> i : listingLifetime.values()){
            if(i.first != null && i.first.second.getId() == id){
                if((new Date().getTime() - i.second.getTime()) <= 2*60*1000){
                    return true;
                }else{
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        bm = new BackgroundPowerSaver(this);

        region = new Region("iBeaconAndAltRegion", null, null, null);

        if(BluetoothAdapter.getDefaultAdapter() == null){
            instance = null;
            stopSelf();
            return;
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setBackgroundBetweenScanPeriod(10000L);
        beaconManager.setBackgroundScanPeriod(5000L);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new iBeaconParser());

        beaconManager.bind(this);

        listingLifetime = new ConcurrentHashMap<String, Pair<Pair<Beacon, Listing>, Date>>();
        listingTimer = new Timer();
        listingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String[] keySet = listingLifetime.keySet().toArray(new String[]{});
                for(String i : keySet){
                    Listing listing = listingLifetime.get(i).first.second;
                    Date lastSeen = listingLifetime.get(i).second;

                    if((new Date().getTime() - lastSeen.getTime()) >= 240*60*1000){
                        listingLifetime.remove(i);
                        if(listing != null){
                            nManager.cancel(listing.getId());
                        }
                    }
                }
            }
        }, 0, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        //restartService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //restartService();
    }

    private void restartService(){
        beaconManager.unbind(this);

        Notification.Builder builder =
                new Notification.Builder(BeaconService.this)
                        .setContentTitle("Destroyed")
                        .setContentText("Destroyed")
                        .setSmallIcon(R.mipmap.ic_launcher);

        int NOTIFICATION_ID = 2;

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmService.set(ELAPSED_REALTIME, elapsedRealtime() + 1000,
                restartServicePendingIntent);
    }

    public static String beaconToKey(String uuid, int major, int minor){
        return String.format(Locale.CANADA, "{%s} {%d} (%d}", uuid, major, minor);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                beaconManager.setBackgroundMode(DealerwerxApplication.isBackground());

                if(beacons.size() > 0) {
                        for(final Beacon b : beacons){
                            String uuid = b.getId1().toString();
                            int major = b.getId2().toInt();
                            int minor = b.getId3().toInt();

                            final String key = beaconToKey(uuid, major, minor);

                            if(listingLifetime.containsKey(key)){
                                if(((new Date().getTime() - listingLifetime.get(key).second.getTime()) <= 2*60*1000)) {
                                    listingLifetime.put(key, new Pair<>(new Pair<>(b, listingLifetime.get(key).first.second), new Date()));
                                    continue;
                                }
                            }


                            APIConsumer.GetListingFromBeaconAsyncTask task = APIConsumer.GetListingFromBeacon(
                                    PreferencesManager.getUserInformation().getAccessToken(),
                                    uuid,
                                    major,
                                    minor,
                                    PreferencesManager.getBeaconLookingMode(),
                                    new APIResponder<Listing>() {
                                        @Override
                                        public void success(final Listing listing) {
                                            if(!listing.isMyPost() && !listingLifetime.containsKey(key)){
                                                Intent targetIntent = new Intent(BeaconService.this, NearMeListingsActivity.class);
                                                targetIntent.putExtra("listing", listing);
                                                PendingIntent contentIntent = PendingIntent.getActivity(BeaconService.this, listing.getId(), targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                                final Notification.Builder builder =
                                                        new Notification.Builder(BeaconService.this)
                                                                .setDefaults(Notification.DEFAULT_SOUND)
                                                                .setContentTitle("There is a vehicle nearby!")
                                                                .setContentText(listing.getVehicle().getTitle())
                                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                                .setAutoCancel(true)
                                                                .setContentIntent(contentIntent)
                                                                .setPriority(Notification.PRIORITY_HIGH)
                                                                .setVibrate(new long[]{50, 50, 100, 100, 500})
                                                                .setLights(getResources().getColor(R.color.accent_material_light_1), 500, 5000);


                                                ImageMedia[] media = listing.getVehicle().getMedia();
                                                if(media.length > 0){
                                                    APIConsumer.DownloadImageAsyncTask task1 = APIConsumer.DownloadImage(
                                                            BeaconService.this,
                                                            media[0].getThumbnailUrl(),
                                                            new APIResponder<Bitmap>() {
                                                                @Override
                                                                public void success(Bitmap result) {
                                                                    builder.setLargeIcon(result);
                                                                    builder.setStyle(new Notification.BigPictureStyle().bigPicture(result).setSummaryText(listing.getVehicle().getTitle()));

                                                                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                                    nManager.notify(listing.getId(), builder.build());
                                                                }

                                                                @Override
                                                                public void error(String errorMessage) {
                                                                }
                                                            }
                                                    );
                                                    try {
                                                        task1.execute().get();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    } catch (ExecutionException e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{
                                                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    nManager.notify(listing.getId(), builder.build());
                                                }
                                            }

                                            listingLifetime.put(key, new Pair<>(new Pair<>(b, listing), new Date()));
                                        }

                                        @Override
                                        public void error(String errorMessage) {
                                            listingLifetime.put(key, new Pair<>(new Pair<Beacon, Listing>(b, null), new Date()));
                                        }
                                    }
                            );
                            task.execute();
                    }
                }
            }
        });


        try {
            if(PreferencesManager.hasUserInformation()){
                setEnabled(PreferencesManager.getAllowBackgroundScanning());
            }

        } catch (RemoteException e) {    }
    }

    public class iBeaconParser extends BeaconParser{
        public iBeaconParser(){
            this.setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24");
        }
    }
}
