package deanmyers.com.dealerwerx;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.Listing;
import deanmyers.com.dealerwerx.Adapters.BoatListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.CarListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.EquipmentListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.ListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.MotorcycleListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.OtherListingsAdapter;

public class LookingForActivity extends NavigationActivity implements LocationListener{

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

    private LocationManager mLocationManager;

    final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 1;
    final int MY_PERMISSION_ACCESS_FINE_LOCATION = 2;

    final float kmRange = 50.0f;

    private Location location = null;
    private boolean firstTime = true;

    protected void onCreate(Bundle savedInstanceState) {
        setClassConstructor(ListingsAdapter.class);
        setCurrentActivity(R.id.nav_lookingfor);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookingfor);

        setViewTitlePrimary("Looking To Buy");
        setViewTitle("All Listings");

        srl = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        srlListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                APIConsumer.GetLookingForListingsAsyncTask task = APIConsumer.GetLookingForListings(PreferencesManager.getUserInformation().getAccessToken(), new APIResponder<Listing[]>() {
                    @Override
                    public void success(Listing[] result) {
                        ListView listView = (ListView)findViewById(R.id.listings_list);

                        ArrayList<Listing> newResultsList = new ArrayList<Listing>();
                        Listing[] newResults;

                        if(location != null){
                            for(int i = 0; i < result.length; i++){
                                Listing listing = result[i];
                                if(listing.getLat() == null || listing.getLon() == null)
                                    continue;

                                double distance = meterDistanceBetweenPoints(listing.getLat().floatValue(), listing.getLon().floatValue(), (float)location.getLatitude(), (float)location.getLongitude());

                                if(distance > kmRange * 1000)
                                    continue;
                                else
                                    newResultsList.add(listing);
                            }
                            newResults = newResultsList.toArray(new Listing[]{});
                        }
                        else{
                            newResults = result;
                        }
                        try {
                            listView.setAdapter((ListingsAdapter)classConstructor.newInstance(LookingForActivity.this, 0, Arrays.asList(newResults)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        srl.setRefreshing(false);
                    }

                    @Override
                    public void error(String errorMessage) {
                        srl.setRefreshing(false);
                        Toast.makeText(LookingForActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(LookingForActivity.this, ListingDetailActivity.class);
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

        try {
            mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

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
            updateListview();
        }
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
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION:
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    updateListview();
                }
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if(firstTime) {
            firstTime = false;
            updateListview();
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
    protected void onResume() {
        super.onResume();
        updateListview();
    }
}
