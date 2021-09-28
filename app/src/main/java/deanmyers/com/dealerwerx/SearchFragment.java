package deanmyers.com.dealerwerx;

/**
 * Created by mac3 on 2017-01-17.
 */

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.BoatExtra;
import deanmyers.com.dealerwerx.API.CarExtra;
import deanmyers.com.dealerwerx.API.Listing;
import deanmyers.com.dealerwerx.API.MotorcycleExtra;
import deanmyers.com.dealerwerx.API.Vehicle;
import deanmyers.com.dealerwerx.API.VehicleType;

/**
 * Created by mac3 on 2016-11-17.
 */

public class SearchFragment extends TitleCompatFragment implements LocationListener{

    private ProgressBar mProgressView;
    private View mMyView;
    private Switch mCarsSwitch, mMotorcyclesSwitch, mBoatsSwitch, mEquipmentSwitch, mOtherSwitch, mSafeZoneSwitch;
    private Spinner mMinPriceSpinner, mMaxPriceSpinner, mMakeSpinner, mModelSpinner, mYearSpinner, mDistanceSpinner, mDistanceUnitsSpinner;
    private TextView mResultsTextView, mDistanceLabelTextView;

    private Button mViewButton;

    private Listing[] listings = new Listing[0];
    private Listing[] filteredListings = new Listing[0];
    private VehicleMake[] vehicleMakes = new VehicleMake[]{};
    private Double[] priceRanges = new Double[]{};
    private int[] distanceRanges = new int[]{};
    private LocationManager mLocationManager;

    final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 1;
    final int MY_PERMISSION_ACCESS_FINE_LOCATION = 2;

    private Location location = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setViewTitlePrimary("Search");
        setViewTitle("");

        if(this.mMyView != null)
            return this.mMyView;

        View v = inflater.inflate(R.layout.content_search, container, false);
        //setViewTitle(container, "Extra Information");


        mProgressView = (ProgressBar) container.getRootView().findViewById(R.id.search_progress);

        mMyView = v;

        mCarsSwitch = (Switch) v.findViewById(R.id.show_cars);
        mMotorcyclesSwitch = (Switch) v.findViewById(R.id.show_motorcycles);
        mBoatsSwitch = (Switch) v.findViewById(R.id.show_boats);
        mEquipmentSwitch = (Switch) v.findViewById(R.id.show_equipment);
        mOtherSwitch = (Switch) v.findViewById(R.id.show_other);
        mSafeZoneSwitch = (Switch) v.findViewById(R.id.show_safezone_only);

        mMinPriceSpinner = (Spinner) v.findViewById(R.id.min_price);
        mMaxPriceSpinner = (Spinner) v.findViewById(R.id.max_price);
        mMakeSpinner = (Spinner) v.findViewById(R.id.filter_make);
        mModelSpinner = (Spinner) v.findViewById(R.id.filter_model);
        mYearSpinner = (Spinner) v.findViewById(R.id.filter_year);
        mDistanceSpinner = (Spinner) v.findViewById(R.id.max_distance);
        mDistanceUnitsSpinner = (Spinner) v.findViewById(R.id.distance_units);

        mDistanceLabelTextView = (TextView)v.findViewById(R.id.distance_label);
        mResultsTextView = (TextView) v.findViewById(R.id.results_label);
        mViewButton = (Button) v.findViewById(R.id.view_button);

        CompoundButton.OnCheckedChangeListener checkChangedListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterResults();
            }
        };


        mCarsSwitch.setOnCheckedChangeListener(checkChangedListener);
        mMotorcyclesSwitch.setOnCheckedChangeListener(checkChangedListener);
        mBoatsSwitch.setOnCheckedChangeListener(checkChangedListener);
        mEquipmentSwitch.setOnCheckedChangeListener(checkChangedListener);
        mOtherSwitch.setOnCheckedChangeListener(checkChangedListener);
        mSafeZoneSwitch.setOnCheckedChangeListener(checkChangedListener);

        mViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListingsFragment fragment = new ListingsFragment();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                Bundle arguments = new Bundle();

                arguments.putSerializable("results", filteredListings);
                fragment.setArguments(arguments);

                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        if(savedInstanceState != null){
            listings = (Listing[])savedInstanceState.getSerializable("listings");

            mCarsSwitch.setChecked(savedInstanceState.getBoolean("showCars"));
            mMotorcyclesSwitch.setChecked(savedInstanceState.getBoolean("showMotorcycles"));
            mBoatsSwitch.setChecked(savedInstanceState.getBoolean("showBoats"));
            mEquipmentSwitch.setChecked(savedInstanceState.getBoolean("showEquipment"));
            mOtherSwitch.setChecked(savedInstanceState.getBoolean("showOther"));

            configureSpinners();
            filterResults();
        }else{
            showProgress(true);
            APIConsumer.GetListings(PreferencesManager.getUserInformation().getAccessToken(),
                    new APIResponder<Listing[]>() {
                        @Override
                        public void success(Listing[] result) {
                            listings = result;
                            filteredListings = result;
                            showProgress(false);
                            configureSpinners();
                            filterResults();
                        }

                        @Override
                        public void error(String errorMessage) {
                            Toast.makeText(getContext(), "Unable to get listings for search", Toast.LENGTH_LONG).show();
                        }
                    }).execute();
        }

        try {
            mLocationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);

            // Get the best provider between gps, network and passive
            Criteria criteria = new Criteria();
            String mProviderName = mLocationManager.getBestProvider(criteria, true);
            mDistanceLabelTextView.setText("Waiting For Location...");
            // API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSION_ACCESS_COARSE_LOCATION);
                }
                // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
                // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                            MY_PERMISSION_ACCESS_FINE_LOCATION);
                }

            }
        } catch (Exception e) {
            mDistanceLabelTextView.setText("Location Unavailable");
        }

        mDistanceUnitsSpinner.setVisibility(View.GONE);
        mDistanceSpinner.setVisibility(View.GONE);

        return v;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION:
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mDistanceLabelTextView.setText("Waiting For Location...");
                } else {
                    mDistanceLabelTextView.setText("Location Unavailable");
                }
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(this.location == null){
            mDistanceLabelTextView.setText("Within A Distance Of...");
            mDistanceSpinner.setVisibility(View.VISIBLE);
            mDistanceUnitsSpinner.setVisibility(View.VISIBLE);
        }

        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        if(location == null){
            mDistanceLabelTextView.setText("Location Unavailable");
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if(location == null){
            mDistanceLabelTextView.setText("Location Unavailable");
        }
    }

    private class VehicleMake {
        private String value;
        private ArrayList<VehicleModel> children = new ArrayList<>();

        public void addChild(VehicleModel model) {
            children.add(model);
        }

        public VehicleModel[] getChildren() {
            return children.toArray(new VehicleModel[]{});
        }

        public String getValue() {
            return value;
        }

        public void Sort() {
            for (VehicleModel model : children) {
                Collections.sort(model.children, new Comparator<VehicleYear>() {
                    @Override
                    public int compare(VehicleYear o1, VehicleYear o2) {
                        return o1.value - o2.value;
                    }
                });
            }

            Collections.sort(children, new Comparator<VehicleModel>() {
                @Override
                public int compare(VehicleModel o1, VehicleModel o2) {
                    return o1.value.compareTo(o2.value);
                }
            });
        }

        public VehicleMake(String value) {
            this.value = value;
        }
    }

    private class VehicleModel {
        private String value;
        private ArrayList<VehicleYear> children = new ArrayList<>();

        public void addChild(VehicleYear model) {
            children.add(model);
        }

        public VehicleYear[] getChildren() {
            return children.toArray(new VehicleYear[]{});
        }

        public String getValue() {
            return value;
        }

        public VehicleModel(String value) {
            this.value = value;
        }
    }

    private class VehicleYear {
        private int value;

        public int getValue() {
            return value;
        }


        public VehicleYear(int value) {
            this.value = value;
        }
    }

    private void configureSpinners() {
        ArrayList<VehicleMake> makeList = new ArrayList<>();

        Double maxPrice = 0.0;

        for (Listing listing : listings) {
            String makeString;
            String modelString;
            int year = -1;

            if (listing.getAskingPrice() > maxPrice) {
                maxPrice = listing.getAskingPrice();
            }

            Vehicle vehicle = listing.getVehicle();

            switch (vehicle.getType()) {
                case Car:
                    CarExtra cExtra = (CarExtra) vehicle.getExtra();
                    makeString = cExtra.getMake().toUpperCase();
                    modelString = cExtra.getModel().toUpperCase();
                    year = cExtra.getYear();
                    break;
                case Boat:
                    BoatExtra bExtra = (BoatExtra) vehicle.getExtra();
                    makeString = bExtra.getMake().toUpperCase();
                    modelString = bExtra.getModel().toUpperCase();
                    year = bExtra.getYear();
                    break;
                case Motorcycle:
                    MotorcycleExtra mExtra = (MotorcycleExtra) vehicle.getExtra();
                    makeString = mExtra.getMake().toUpperCase();
                    modelString = mExtra.getModel().toUpperCase();
                    year = mExtra.getYear();
                    break;
                default:
                    makeString = null;
                    modelString = null;
                    year = -1;
            }

            if ((makeString == null) || (modelString == null) || (year == -1)) {
                continue;
            }

            int makeIndex = -1, modelIndex = -1, yearIndex = -1;

            for (int i = 0; i < makeList.size(); i++) {
                VehicleMake make = makeList.get(i);

                if (make.value.equals(makeString)) {
                    makeIndex = i;

                    for (int u = 0; u < make.getChildren().length; u++) {
                        VehicleModel model = make.getChildren()[u];

                        if (model.value.equals(modelString)) {
                            modelIndex = u;

                            for (int p = 0; p < model.getChildren().length; p++) {
                                VehicleYear vehicleYear = model.getChildren()[p];

                                if (vehicleYear.value == year) {
                                    yearIndex = p;
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    break;
                }
            }

            if (yearIndex != -1)
                continue;

            VehicleYear vehicleYear = new VehicleYear(year);

            if (modelIndex != -1) {
                VehicleMake vehicleMake = makeList.get(makeIndex);
                VehicleModel vehicleModel = vehicleMake.getChildren()[modelIndex];

                vehicleModel.addChild(vehicleYear);
            } else if (makeIndex != -1) {
                VehicleMake vehicleMake = makeList.get(makeIndex);
                VehicleModel vehicleModel = new VehicleModel(modelString);

                vehicleModel.addChild(vehicleYear);
                vehicleMake.addChild(vehicleModel);
            } else {
                VehicleMake vehicleMake = new VehicleMake(makeString);
                VehicleModel vehicleModel = new VehicleModel(modelString);

                vehicleModel.addChild(vehicleYear);
                vehicleMake.addChild(vehicleModel);

                makeList.add(vehicleMake);
            }
        }

        for (VehicleMake vehicleMake : makeList) {
            vehicleMake.Sort();
        }

        Collections.sort(makeList, new Comparator<VehicleMake>() {
            @Override
            public int compare(VehicleMake o1, VehicleMake o2) {
                return o1.value.compareTo(o2.value);
            }
        });

        vehicleMakes = makeList.toArray(new VehicleMake[]{});

        ArrayList<String> makesNames = new ArrayList<>();
        makesNames.add("Any Make");

        for (VehicleMake vehicleMake : vehicleMakes)
            makesNames.add(vehicleMake.value);


        ArrayAdapter makeArrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                makesNames.toArray(new String[0]));

        final ArrayAdapter modelArrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Any Model"});

        final ArrayAdapter yearArrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Any Year"});

        mMakeSpinner.setAdapter(makeArrayAdapter);
        mMakeSpinner.setSelection(0);

        mModelSpinner.setAdapter(modelArrayAdapter);
        mModelSpinner.setSelection(0);

        mYearSpinner.setAdapter(yearArrayAdapter);
        mYearSpinner.setSelection(0);


        mMakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mModelSpinner.setSelection(0);
                mYearSpinner.setSelection(0);

                mYearSpinner.setAdapter(yearArrayAdapter);
                mYearSpinner.setEnabled(false);


                if (position == 0) {
                    mModelSpinner.setEnabled(false);
                    mModelSpinner.setAdapter(modelArrayAdapter);
                } else {
                    ArrayList<String> modelNames = new ArrayList<>();
                    modelNames.add("Any Model");

                    for (VehicleModel vehicleModel : vehicleMakes[position - 1].children)
                        modelNames.add(vehicleModel.value);


                    ArrayAdapter newModelArrayAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            modelNames.toArray(new String[0]));


                    mModelSpinner.setEnabled(true);
                    mModelSpinner.setAdapter(newModelArrayAdapter);
                }
                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mYearSpinner.setSelection(0);

                if (position == 0) {
                    mYearSpinner.setEnabled(false);
                    mYearSpinner.setAdapter(yearArrayAdapter);
                } else {
                    ArrayList<String> yearNames = new ArrayList<>();
                    yearNames.add("Any Year");

                    for (VehicleYear vehicleYear : vehicleMakes[mMakeSpinner.getSelectedItemPosition() - 1].getChildren()[position - 1].getChildren())
                        yearNames.add("" + vehicleYear.value);


                    ArrayAdapter newYearArrayAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            yearNames.toArray(new String[0]));


                    mYearSpinner.setEnabled(true);
                    mYearSpinner.setAdapter(newYearArrayAdapter);
                }

                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<Double> priceList = new ArrayList<>();

        priceList.add(25.0);
        priceList.add(50.0);
        priceList.add(100.0);
        priceList.add(250.0);
        priceList.add(500.0);

        maxPrice = maxPrice + 1000 - ((maxPrice % 1000));

        for (int i = 1; i < maxPrice / 1000.0; i++) {
            priceList.add(1000.0 * i);
        }

        priceRanges = priceList.toArray(new Double[0]);

        ArrayList<String> minPriceStrings = new ArrayList<>();
        ArrayList<String> maxPriceStrings = new ArrayList<>();

        minPriceStrings.add("No Minimum Price");
        maxPriceStrings.add("No Maximum Price");

        for (Double i: priceList){
            String price = String.format(Locale.CANADA, "$%.0f", i);
            minPriceStrings.add(price);
            maxPriceStrings.add(price);
        }

        final ArrayAdapter minPriceArrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                minPriceStrings.toArray(new String[0]));

        final ArrayAdapter maxPriceArrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                maxPriceStrings.toArray(new String[0]));

        mMinPriceSpinner.setAdapter(minPriceArrayAdapter);
        mMinPriceSpinner.setSelection(0);

        mMaxPriceSpinner.setAdapter(maxPriceArrayAdapter);
        mMaxPriceSpinner.setSelection(0);

        mMinPriceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int maxPosition = mMaxPriceSpinner.getSelectedItemPosition();

                if(maxPosition != 0 && position > maxPosition){
                    mMaxPriceSpinner.setSelection(position, true);
                }

                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMaxPriceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int minPosition = mMinPriceSpinner.getSelectedItemPosition();

                if(minPosition != 0 && position < minPosition){
                    mMinPriceSpinner.setSelection(position, true);
                }

                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        distanceRanges = new int[]{ 1, 2, 5, 10, 15, 20, 50, 100 };

        ArrayList<String> maxDistanceStrings = new ArrayList<>();

        maxDistanceStrings.add("Any");

        for(int i = 0; i < distanceRanges.length; i++)
            maxDistanceStrings.add(""+distanceRanges[i]);


        final ArrayAdapter maxDistanceArrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                maxDistanceStrings.toArray(new String[0]));

        final ArrayAdapter distanceUnitsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[] {"Kilometer", "Mile"});

        final ArrayAdapter pluralDistanceUnitsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[] {"Kilometers", "Miles"});

        mDistanceSpinner.setAdapter(maxDistanceArrayAdapter);
        mDistanceUnitsSpinner.setAdapter(pluralDistanceUnitsAdapter);

        mDistanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selected = mDistanceUnitsSpinner.getSelectedItemPosition();

                if(position == 1)
                    mDistanceUnitsSpinner.setAdapter(distanceUnitsAdapter);
                else
                    mDistanceUnitsSpinner.setAdapter(pluralDistanceUnitsAdapter);

                mDistanceUnitsSpinner.setSelection(selected);

                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDistanceUnitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filterResults() {
        ArrayList<Listing> newList = new ArrayList<>();

        ArrayList<VehicleType> acceptedTypes = new ArrayList<>();

        if (mCarsSwitch.isChecked())
            acceptedTypes.add(VehicleType.Car);

        if (mMotorcyclesSwitch.isChecked())
            acceptedTypes.add(VehicleType.Motorcycle);

        if (mBoatsSwitch.isChecked())
            acceptedTypes.add(VehicleType.Boat);

        if (mEquipmentSwitch.isChecked())
            acceptedTypes.add(VehicleType.Equipment);

        if (mOtherSwitch.isChecked())
            acceptedTypes.add(VehicleType.Other);


        for (Listing listing : listings) {
            if (!acceptedTypes.contains(listing.getVehicle().getType())) {
                continue;
            }

            if (mSafeZoneSwitch.isChecked() && !listing.isSafeZone()) {
                continue;
            }

            String currentMake = null;
            String currentModel = null;
            int currentYear = -1;

            Vehicle vehicle = listing.getVehicle();

            switch (vehicle.getType()) {
                case Car:
                    CarExtra cExtra = (CarExtra) vehicle.getExtra();
                    currentMake = cExtra.getMake().toUpperCase();
                    currentModel = cExtra.getModel().toUpperCase();
                    currentYear = cExtra.getYear();
                    break;
                case Boat:
                    BoatExtra bExtra = (BoatExtra) vehicle.getExtra();
                    currentMake = bExtra.getMake().toUpperCase();
                    currentModel = bExtra.getModel().toUpperCase();
                    currentYear = bExtra.getYear();
                    break;
                case Motorcycle:
                    MotorcycleExtra mExtra = (MotorcycleExtra) vehicle.getExtra();
                    currentMake = mExtra.getMake().toUpperCase();
                    currentModel = mExtra.getModel().toUpperCase();
                    currentYear = mExtra.getYear();
                    break;
                default:
                    currentMake = null;
                    currentModel = null;
                    currentYear = -1;
            }


            String selectedMake = null;
            String selectedModel = null;
            int selectedYear = -1;

            if (mMakeSpinner.getSelectedItemPosition() > 0) {
                VehicleMake vehicleMake = vehicleMakes[mMakeSpinner.getSelectedItemPosition() - 1];
                selectedMake = vehicleMake.getValue();

                if (mModelSpinner.getSelectedItemPosition() > 0) {
                    VehicleModel vehicleModel = vehicleMake.getChildren()[mModelSpinner.getSelectedItemPosition() - 1];
                    selectedModel = vehicleModel.getValue();

                    if (mYearSpinner.getSelectedItemPosition() > 0) {
                        VehicleYear vehicleYear = vehicleModel.getChildren()[mYearSpinner.getSelectedItemPosition() - 1];
                        selectedYear = vehicleYear.getValue();
                    }
                }
            }

            if(location != null){
                if(mDistanceSpinner.getSelectedItemPosition() > 0){
                    if(listing.getLat() == null || listing.getLon() == null)
                        continue;

                    double value = distanceRanges[mDistanceSpinner.getSelectedItemPosition() - 1];

                    if(mDistanceUnitsSpinner.getSelectedItemPosition() == 1){
                        value = value * 1609.34;
                    }else{
                        value = value * 1000.0;
                    }

                    double distance = meterDistanceBetweenPoints(listing.getLat().floatValue(), listing.getLon().floatValue(), (float)location.getLatitude(), (float)location.getLongitude());

                    if(distance > value)
                        continue;
                }
            }

            if ((selectedMake != null && currentMake == null) || (selectedModel != null && currentModel == null) || (selectedYear != -1 && currentYear == -1)) {
                continue;
            }

            if (selectedMake != null && !selectedMake.equals(currentMake)) {
                continue;
            }

            if (selectedModel != null && !selectedModel.equals(currentModel)) {
                continue;
            }

            if (selectedYear != -1 && selectedYear != currentYear) {
                continue;
            }

            if(mMinPriceSpinner.getSelectedItemPosition() > 0){
                if(listing.getAskingPrice() < priceRanges[mMinPriceSpinner.getSelectedItemPosition() - 1]){
                    continue;
                }
            }

            if(mMaxPriceSpinner.getSelectedItemPosition() > 0){
                if(listing.getAskingPrice() > priceRanges[mMaxPriceSpinner.getSelectedItemPosition() - 1]){
                    continue;
                }
            }

            newList.add(listing);
        }

        filteredListings = newList.toArray(new Listing[]{});

        mResultsTextView.setText(String.format(Locale.CANADA, "%d RESULT%s", filteredListings.length, filteredListings.length != 1 ? "S" : ""));
        mViewButton.setVisibility(filteredListings.length > 0 ? View.VISIBLE : View.GONE);
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMyView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMyView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMyView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMyView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
