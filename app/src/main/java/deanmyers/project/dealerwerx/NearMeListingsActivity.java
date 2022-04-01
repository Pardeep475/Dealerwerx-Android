package deanmyers.project.dealerwerx;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import deanmyers.project.dealerwerx.API.APIConsumer;
import deanmyers.project.dealerwerx.API.APIResponder;
import deanmyers.project.dealerwerx.API.Listing;
import deanmyers.project.dealerwerx.Adapters.BoatListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.CarListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.EquipmentListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.ListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.MotorcycleListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.OtherListingsAdapter;
import deanmyers.project.dealerwerx.Services.BeaconService;

public class NearMeListingsActivity extends NavigationActivity implements LocationListener{

    private double MAX_RADIUS = 2000; //Meters

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 2;
    private LocationManager mLocationManager;

    private Constructor<?> classConstructor;
    private SwipeRefreshLayout srl;
    private SwipeRefreshLayout.OnRefreshListener srlListener;
    private ListView listView;

    private LinearLayout allListings;
    private LinearLayout carListings;
    private LinearLayout motorcycleListings;
    private LinearLayout boatListings;
    private LinearLayout equipmentListings;
    private LinearLayout otherListings;

    private Location location = null;

    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            Listing listing = (Listing)extras.getSerializable("listing");
            if(listing != null){
                Intent intent = new Intent(NearMeListingsActivity.this, ListingDetailActivity.class);
                intent.putExtra("listing", listing);
                startActivity(intent);
            }
        }

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Bluetooth Required")
                    .setMessage("For Near Me and vehicle notification features, you must enable Bluetooth.")
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBluetoothAdapter.enable();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();

            if(BeaconService.getInstance() == null){
                Intent service = new Intent(this, BeaconService.class);
                startService(service);
            }
        }

        setClassConstructor(ListingsAdapter.class);
        setCurrentActivity(R.id.nav_nearme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        setViewTitlePrimary("Near Me");
        setViewTitle("All Listings");

        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Get the best provider between gps, network and passive
            Criteria criteria = new Criteria();
            String mProviderName = mLocationManager.getBestProvider(criteria, true);

            // API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                // No one provider activated: prompt GPS
                if (mProviderName == null || mProviderName.equals("")) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }

                // At least one provider activated. Get the coordinates
                switch (mProviderName) {
                    case "passive":
                    case "network":
                    case "gps":
                        mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                }

                // One or both permissions are denied.
            } else {

                // The ACCESS_COARSE_LOCATION is denied, then I request it and manage the result in
                // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSION_ACCESS_COARSE_LOCATION);
                }
                // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
                // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(this,
                            new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                            MY_PERMISSION_ACCESS_FINE_LOCATION);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        srl = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        srl.setEnabled(true);

        srlListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!srl.isEnabled())
                    return;
                String accessToken = null;
                if (PreferencesManager.getUserInformation() != null) {

                    if (PreferencesManager.getUserInformation().getAccessToken() != null) {
                        accessToken = PreferencesManager.getUserInformation().getAccessToken();
                    }
                }
                APIConsumer.GetListingsAsyncTask task = APIConsumer.GetListings(accessToken, new APIResponder<Listing[]>() {
                    @Override
                    public void success(Listing[] result) {
                        ListView listView = null;
                        final ArrayList<Listing> newList;

                        listView = (ListView)findViewById(R.id.listings_list);
                        newList = new ArrayList<>();

                        try {

                            BeaconService service = BeaconService.getInstance();

                            for(final Listing i : result){
                                if(service != null && service.containsListingId(i.getId())){
                                    newList.add(i);
                                }else{
                                    if(location != null){
                                        if(i.getLat() != null && i.getLon() != null){
                                            double distance = meterDistanceBetweenPoints((float)location.getLatitude(),
                                                    (float)location.getLongitude(),
                                                    i.getLat().floatValue(),
                                                    i.getLon().floatValue()
                                            );

                                            if(distance <= MAX_RADIUS)
                                                newList.add(i);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            listView.setAdapter((ListingsAdapter)classConstructor.newInstance(NearMeListingsActivity.this, 0, newList));
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        srl.setRefreshing(false);
                    }

                    @Override
                    public void error(String errorMessage) {
                        srl.setRefreshing(false);
                        Toast.makeText(NearMeListingsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

                task.execute();
            }
        };

        srl.setOnRefreshListener(srlListener);

        listView = (ListView)findViewById(R.id.listings_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listing listing = (Listing)listView.getItemAtPosition(position);
                Intent intent = new Intent(NearMeListingsActivity.this, ListingDetailActivity.class);
                intent.putExtra("listing", listing);
                startActivity(intent);
            }
        });

        View empty = findViewById(R.id.noitems);
        listView.setEmptyView(empty);

        allListings = (LinearLayout)findViewById(R.id.action_listings_all);
        carListings = (LinearLayout)findViewById(R.id.action_listings_cars);
        motorcycleListings = (LinearLayout)findViewById(R.id.action_listings_motorcycles);
        boatListings = (LinearLayout)findViewById(R.id.action_listings_boats);
        equipmentListings = (LinearLayout)findViewById(R.id.action_listings_equipment);
        otherListings = (LinearLayout)findViewById(R.id.action_listings_other);

        setTint(allListings);

        allListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("All Listings");
                setClassConstructor(ListingsAdapter.class);
                updateListview();
                setTint(allListings);
            }
        });
        carListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Car Listings");
                setClassConstructor(CarListingsAdapter.class);
                updateListview();
                setTint(carListings);
            }
        });
        motorcycleListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Motorcycle Listings");
                setClassConstructor(MotorcycleListingsAdapter.class);
                updateListview();
                setTint(motorcycleListings);
            }
        });
        boatListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Boat Listings");
                setClassConstructor(BoatListingsAdapter.class);
                updateListview();
                setTint(boatListings);
            }
        });
        equipmentListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Equipment Listings");
                setClassConstructor(EquipmentListingsAdapter.class);
                updateListview();
                setTint(equipmentListings);
            }
        });
        otherListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Other Listings");
                setClassConstructor(OtherListingsAdapter.class);
                updateListview();
                setTint(otherListings);
            }
        });

//        APIConsumer.GetListingsAsyncTask task = APIConsumer.GetListings(PreferencesManager.getUserInformation().getAccessToken(), new APIResponder<Listing[]>() {
//            @Override
//            public void success(Listing[] result) {
//                listView.setAdapter(new ListingsAdapter(ListingsActivity.this, 0, Arrays.asList(result)));
//            }
//
//            @Override
//            public void error(String errorMessage) {
//                Toast.makeText(ListingsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        task.execute();

        updateListview();
    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        float t1 = (float)(Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2));
        float t2 = (float)(Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2));
        float t3 = (float)(Math.sin(a1)*Math.sin(b1));
        double tt = (Math.acos(t1 + t2 + t3));

        return 6366000*tt;
    }

    private boolean setClassConstructor(Class<?> className) {
        try{
            classConstructor = className.getConstructor(Context.class, int.class, List.class);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    private void setTint(LinearLayout target){
        LinearLayout[] layouts = new LinearLayout[]{allListings, carListings, motorcycleListings, boatListings, equipmentListings, otherListings};

        for(int i = 0; i < layouts.length; i++){
            AppCompatImageView imageView = (AppCompatImageView)layouts[i].getChildAt(0);
            TextView textView = (TextView)layouts[i].getChildAt(1);

            int targetColor = (layouts[i] == target) ? R.color.buttonsSelectedAccent : R.color.buttonsAccent;

            int resolvedColor = ContextCompat.getColor(this, targetColor);
            imageView.setColorFilter(resolvedColor);
            textView.setTextColor(resolvedColor);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION:
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "You must allow GPS access to use the 'Near Me' feature", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            }
        }
    }

    private void updateListview(){
        srl.post(new Runnable() {
            @Override public void run() {
                srl.setRefreshing(true);
                // directly call onRefresh() method
                srlListener.onRefresh();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if(this.location == null){
            srl.setEnabled(true);
            this.location = location;
            updateListview();
        }else{
            this.location = location;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListview();
    }
}
