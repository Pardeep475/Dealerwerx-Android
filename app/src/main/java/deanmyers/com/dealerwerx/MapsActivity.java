package deanmyers.com.dealerwerx;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.Listing;
import deanmyers.com.dealerwerx.Adapters.ListingsAdapter;
import deanmyers.com.dealerwerx.Layouts.SlidingUpPanelLayout;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, SlidingUpPanelLayout.PanelSlideListener {

    private LocationManager mLocationManager;

    class ListingItem implements ClusterItem{
        private Listing listing;

        public Listing getListing(){
            return listing;
        }

        public ListingItem(Listing listing){
            this.listing = listing;
        }

        public LatLng getPosition(){
            return new LatLng(listing.getLat(), listing.getLon());
        }
    }

    private ListView mListView;
    private View mTransparentView;
    private View mWhiteSpaceView;
    private View mTransparentHeaderView;
    private View mSpaceView;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private ClusterManager<ListingItem> mClusterManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Get the best provider between gps, network and passive
            Criteria criteria = new Criteria();
            String mProviderName = mLocationManager.getBestProvider(criteria, true);

            // API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> testData = new ArrayList<String>(100);
        for (int i = 0; i < 100; i++) {
            testData.add("Item " + i);
        }
        // show white bg if there are not too many items
        // mWhiteSpaceView.setVisibility(View.VISIBLE);

        // ListView approach

        mListView = (ListView) findViewById(R.id.listings_list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView
        mTransparentView = findViewById(R.id.transparentView);
        mWhiteSpaceView = findViewById(R.id.whiteSpaceView);

        // init header view for ListView
        mTransparentHeaderView = getLayoutInflater().inflate(R.layout.transparent_header_view, mListView, false);
        mSpaceView = mTransparentHeaderView.findViewById(R.id.space);


        mSlidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSlidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSlidingUpPanelLayout.onPanelDragged(0);
            }
        });


        mListView.addHeaderView(mTransparentHeaderView);
        mListView.setAdapter(new ListingsAdapter(this, 0, Arrays.asList(new Listing[0])));

        mSlidingUpPanelLayout.setPanelHeight(0);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listing listing = (Listing)mListView.getItemAtPosition(position);
                if(listing != null){
                    Intent intent = new Intent(MapsActivity.this, ListingDetailActivity.class);
                    intent.putExtra("listing", listing);
                    startActivity(intent);
                }
            }
        });

        mListView.setVisibility(View.GONE);
        mListView.setBackgroundColor(getResources().getColor(android.R.color.white));

        collapseMap();
        mSlidingUpPanelLayout.collapsePane();
    }

    private void collapseMap() {
        mTransparentView.setVisibility(View.GONE);
    }

    private void expandMap() {
        mTransparentView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    private Listing[] items = null;

    private GoogleMap map;
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mClusterManager = new ClusterManager<ListingItem>(this, googleMap);

        map = googleMap;

        map.setOnCameraIdleListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ListingItem>() {
            @Override
            public boolean onClusterClick(Cluster<ListingItem> cluster) {
                ArrayList<Listing> listings = new ArrayList<>();

                ListingItem[] items  = cluster.getItems().toArray(new ListingItem[0]);

                for(int i = 0; i < items.length; i++){
                    listings.add(items[i].getListing());
                }

                mListView.setAdapter(new ListingsAdapter(MapsActivity.this, 0, listings));
                mListView.setVisibility(View.VISIBLE);
                mListView.setBackgroundColor(getResources().getColor(android.R.color.white));
                mSlidingUpPanelLayout.expandPane(0.25f);
                return true;
            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ListingItem>() {
            @Override
            public boolean onClusterItemClick(ListingItem listingItem) {
                mListView.setAdapter(new ListingsAdapter(MapsActivity.this, 0, Arrays.asList(new Listing[] { listingItem.getListing()})));
                mListView.setVisibility(View.VISIBLE);
                mListView.setBackgroundColor(getResources().getColor(android.R.color.white));
                mSlidingUpPanelLayout.expandPane(0.25f);
                return true;
            }
        });

//        googleMap.setMyLocationEnabled(true);
//        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                Listing listing = (Listing)items[(int)marker.getZIndex()];
//                Intent intent = new Intent(MapsActivity.this, ListingDetailActivity.class);
//                intent.putExtra("listing", listing);
//                startActivity(intent);
//            }
//        });
//
//        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                View infoWindowView = getLayoutInflater().inflate(R.layout.info_window, null);
//                final ImageView vehicleImage = (ImageView)infoWindowView.findViewById(R.id.image_vehicle);
//                TextView title = (TextView)infoWindowView.findViewById(R.id.text_title);
//                TextView location = (TextView)infoWindowView.findViewById(R.id.text_location);
//
//                int index = (int)marker.getZIndex();
//                if (items != null){
//                    Listing current = items[index];
//                    title.setText(current.getVehicle().getTitle());
//                    location.setText(current.getLocation());
//
//                    Bitmap bitmap = getBitmap(current);
//
//                    if(bitmap!= null){
//                        vehicleImage.setImageBitmap(bitmap);
//                    }else{
//                        vehicleImage.setVisibility(View.GONE);
//                    }
//                }
//
//                return infoWindowView;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                return null;
//            }
//        });

        try {
            LocationManager locationManager = mLocationManager;
            if(locationManager != null) {
                Criteria criteria = new Criteria();

                Location location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, false));
                if (location == null) {
                    criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    // Finds a provider that matches the criteria
                    String provider = locationManager.getBestProvider(criteria, true);
                    // Use the provider to get the last known location
                    location = locationManager.getLastKnownLocation(provider);
                }
                if (location == null) {
                    location = googleMap.getMyLocation();
                }
                if (location != null) {
                    hasSetLocation = true;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                }
            }
        }
        catch(Exception ex){

        }

        APIConsumer.GetListings(PreferencesManager.getUserInformation().getAccessToken(), new APIResponder<Listing[]>() {
            @Override
            public void success(Listing[] result) {
                try {
                    //final ArrayList<Listing> newList = new ArrayList<>();
                    //Geocoder coder = new Geocoder(MapsActivity.this);
                    //List<Address> address;

                    for(final Listing i : result){
//                        try {
//                            address = coder.getFromLocationName(i.getLocation(),5);
//
//                            if(!address.isEmpty()) {
//                                Address location = address.get(0);
//
//                                googleMap.addMarker(getMarkerOptions(i, new LatLng(location.getLatitude(), location.getLongitude()), newList.size()));
//                                newList.add(i);
//                            }else{
//                                APIConsumer.GetLatLonAsyncTask task = APIConsumer.GetLatLon(i.getLocation(), new APIResponder<double[]>() {
//                                    @Override
//                                    public void success(double[] result) {
//
//                                    }
//
//                                    @Override
//                                    public void error(String errorMessage) {
//
//                                    }
//                                });
//                                task.execute();
//                                task.get();
//                            }
//                        }catch(Exception e) {
//
//                        }
                        if(i.getLat() != null && i.getLon() != null) {
                            mClusterManager.addItem(new ListingItem(i));
                            //googleMap.addMarker(getMarkerOptions(i, new LatLng(i.getLat(), i.getLon()), newList.size()));
                            //newList.add(i);
                        }
                    }

                    mClusterManager.cluster();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String errorMessage) {

            }
        }).execute();
    }

    public Bitmap getBitmap(Listing listing){
        if(listing.getVehicle().getMedia() == null || listing.getVehicle().getMedia().length == 0){
            return null;
        }else{
            try {
                APIConsumer.APITaskResult<Bitmap> result = APIConsumer.DownloadImage(this, listing.getVehicle().getMedia()[0].getThumbnailUrl(), new APIResponder<Bitmap>() {
                    @Override
                    public void success(Bitmap result) {
                    }

                    @Override
                    public void error(String errorMessage) {
                    }
                }).execute().get();
                if(result.success){
                    return result.result;
                }else{
                    return null;
                }
            } catch (InterruptedException e) {
                return null;
            } catch (ExecutionException e) {
                return null;
            }
        }
    }

    public MarkerOptions getMarkerOptions(Listing listing, LatLng coords, int index){
        MarkerOptions options = new MarkerOptions();
        options.title(listing.getVehicle().getTitle());
        options.position(new LatLng(coords.latitude  + ((listing.getId()) * 0.000001 * ((listing.getId() % 2 == 0) ? 1.0 : -1.0)),
                coords.longitude + ((listing.getId()) * 0.000001 * ((listing.getId() % 4 == 0) ? 1.0 : -1.0))
        ));
        options.zIndex(index);
        return options;
    }

    private boolean hasSetLocation = false;
    @Override
    public void onLocationChanged(Location location) {
        if(map != null){
            if (!hasSetLocation){
                hasSetLocation = true;
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            }
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

}
