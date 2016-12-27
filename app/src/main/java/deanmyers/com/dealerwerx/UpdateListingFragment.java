package deanmyers.com.dealerwerx;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.BoatExtra;
import deanmyers.com.dealerwerx.API.CarExtra;
import deanmyers.com.dealerwerx.API.Listing;
import deanmyers.com.dealerwerx.API.MotorcycleExtra;
import deanmyers.com.dealerwerx.API.Vehicle;
import deanmyers.com.dealerwerx.API.VehicleExtra;
import deanmyers.com.dealerwerx.API.VehicleType;
import deanmyers.com.dealerwerx.API.VinDecodeResult;
import deanmyers.com.dealerwerx.Adapters.HintArrayAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * Created by mac3 on 2016-11-17.
 */

public class UpdateListingFragment extends TitleCompatFragment {
    private VehicleType mType;

    private SwitchCompat mLookingFor;
    private EditText mTitle;
    private EditText mDescription;
    private EditText mAskingPrice;
    private EditText mLowPrice;
    private EditText mLocation;
    private SwitchCompat mSafeZone;

    private EditText mYear;
    private EditText mMake;
    private EditText mModel;
    private EditText mColor;
    private EditText mInteriorColor;
    private EditText mExteriorColor;
    private EditText mTrim;
    private EditText mBodyStyle;
    private EditText mEngine;
    private Spinner mFuelType;
    private EditText mKilometers;
    private EditText mDoors;
    private EditText mSeats;
    private Spinner mTransmission;
    private Spinner mDriveTrain;
    private EditText mVin;
    private ProgressBar mProgressView;
    private View mMyView;

    private Uri imageUri;

    private int imageCount;
    private Bitmap[] imageList = new Bitmap[5];
    private boolean scavenger;
    private Listing oldListing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Listing listing = (Listing)getArguments().getSerializable("listing");
        oldListing = listing;
        Vehicle vehicle = listing.getVehicle();


        mType = vehicle.getType();

        VehicleExtra extra = vehicle.getExtra();

        int layout;

        switch(vehicle.getType()){
            case Car:
                layout = R.layout.content_addcar;
                break;
            case Motorcycle:
                layout = R.layout.content_addmotorcycle;
                break;
            case Boat:
                layout = R.layout.content_addboat;
                break;
            case Equipment:
                layout = R.layout.content_addequipment;
                break;
            case Other:
                layout = R.layout.content_addother;
                break;
            default:
                return null;
        }

        View v = inflater.inflate(layout, container, false);
        mMyView = v;
        v.findViewById(R.id.add_image_container).setVisibility(View.GONE);

        setViewTitle(container, "Update " + vehicle.getType().toString() + " Listing");

        mProgressView = (ProgressBar)container.getRootView().findViewById(R.id.addlisting_progress);

        mLookingFor = (SwitchCompat)v.findViewById(R.id.lookingfor);
        mTitle = (EditText)v.findViewById(R.id.title);
        mDescription = (EditText)v.findViewById(R.id.description);
        mAskingPrice = (EditText)v.findViewById(R.id.askingPrice);
        mLowPrice = (EditText)v.findViewById(R.id.lowPrice);
        mSafeZone = (SwitchCompat)v.findViewById(R.id.safezone);
        mLocation = (EditText)v.findViewById(R.id.location);

        mYear = (EditText)v.findViewById(R.id.year);
        mMake = (EditText)v.findViewById(R.id.make);
        mModel = (EditText)v.findViewById(R.id.model);
        mColor = (EditText)v.findViewById(R.id.color);
        mInteriorColor = (EditText)v.findViewById(R.id.interior_color);
        mExteriorColor = (EditText)v.findViewById(R.id.exterior_color);
        mTrim = (EditText)v.findViewById(R.id.trim);
        mBodyStyle = (EditText)v.findViewById(R.id.bodystyle);
        mEngine = (EditText)v.findViewById(R.id.engine);
        mFuelType = (Spinner)v.findViewById(R.id.fueltype);
        mKilometers = (EditText)v.findViewById(R.id.kilometers);
        mDoors = (EditText)v.findViewById(R.id.doors);
        mSeats = (EditText)v.findViewById(R.id.seats);
        mTransmission = (Spinner)v.findViewById(R.id.transmission);
        mDriveTrain = (Spinner)v.findViewById(R.id.drivetrain);
        mVin = (EditText)v.findViewById(R.id.vin);

        switch(mType){
            case Car:
            case Motorcycle:
            case Boat:
                mTitle.setVisibility(View.GONE);
        }

        if(scavenger) {
            ((View)mLowPrice.getParent()).setVisibility(View.GONE);
            ((View)mSafeZone).setVisibility(View.GONE);

            mLowPrice = mAskingPrice;
        }else{
            ((View)mLookingFor).setVisibility(View.GONE);
        }

        switch(mType){
            case Car:
                ArrayAdapter<String> transmissionAdapter = new HintArrayAdapter(
                        getContext(),
                        R.layout.spinner_input,
                        new String[]{
                                "Automatic",
                                "Manual",
                                "Transmission - Select One"
                        }
                );

                transmissionAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                mTransmission.setAdapter(transmissionAdapter);
                mTransmission.setSelection(transmissionAdapter.getCount());

                ArrayAdapter<String> drivetrainAdapter = new HintArrayAdapter(
                        getContext(),
                        R.layout.spinner_input,
                        new String[]{
                                "RWD",
                                "FWD",
                                "AWD",
                                "4WD/4x4",
                                "Drivetrain - Select One"
                        }
                );

                drivetrainAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                mDriveTrain.setAdapter(drivetrainAdapter);
                mDriveTrain.setSelection(drivetrainAdapter.getCount());
            case Motorcycle:
            case Boat:
                ArrayAdapter<String> fuelTypeAdapter = new HintArrayAdapter(
                        getContext(),
                        R.layout.spinner_input,
                        new String[]{
                                "Gas",
                                "Diesel",
                                "Hybrid",
                                "Electric",
                                "Flex Fuel",
                                "Natural Gas",
                                "Fuel Type - Select One"
                        }
                );

                fuelTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                mFuelType.setAdapter(fuelTypeAdapter);
                mFuelType.setSelection(fuelTypeAdapter.getCount());
        }

        if(vehicle.getType() == VehicleType.Car){
            ArrayList<InputFilter> filters = new ArrayList<>();
            InputFilter[] currentFilters = mVin.getFilters();

            filters.addAll(Arrays.asList(currentFilters));

            filters.add(new InputFilter.AllCaps());

            mVin.setFilters(filters.toArray(new InputFilter[]{}));
            mVin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE) {
                        attemptSubmission();
                        return true;
                    }
                    return false;
                }
            });
        }

        mTitle.setText(vehicle.getTitle());
        mDescription.setText(vehicle.getDescription());

        mAskingPrice.setText(String.format(Locale.CANADA, "%.2f", listing.getAskingPrice()));
        mLowPrice.setText(String.format(Locale.CANADA, "%.2f", listing.getLowPrice()));
        mLocation.setText(listing.getLocation());

        mSafeZone.setChecked(listing.isSafeZone());
        mSafeZone.setEnabled(false);
        mLookingFor.setChecked(listing.isLookingFor());
        mLookingFor.setEnabled(false);

        mSafeZone.setVisibility(View.GONE);
        mLookingFor.setVisibility(View.GONE);

        if(vehicle.getType() == VehicleType.Car){
            CarExtra result = (CarExtra)extra;

            mYear.setText(String.format(Locale.CANADA, "%d", result.getYear()));
            mMake.setText(result.getMake());
            mModel.setText(result.getModel());
            mTrim.setText(result.getTrim());
            mInteriorColor.setText(result.getInteriorColor());
            mExteriorColor.setText(result.getExteriorColor());
            mBodyStyle.setText(result.getBodyStyle());
            mEngine.setText(result.getEngine());

            String fuelType = result.getFuelType();

            for(int i = 0; i < mFuelType.getCount(); i++){
                String item = (String)mFuelType.getItemAtPosition(i);
                if(
                        (item.equals(fuelType)) ||
                        (item.toLowerCase().contains("flex") && fuelType.toLowerCase().contains("flex")) ||
                                (item.toLowerCase().contains("natural") && fuelType.toLowerCase().contains("natural"))||
                                (item.toLowerCase().contains("gas") && fuelType.toLowerCase().contains("gas"))||
                                (item.toLowerCase().contains("hybrid") && fuelType.toLowerCase().contains("hybrid"))||
                                (item.toLowerCase().contains("electric") && fuelType.toLowerCase().contains("electric"))||
                                (item.toLowerCase().contains("diesel") && fuelType.toLowerCase().contains("diesel"))
                        ){
                    mFuelType.setSelection(i);
                    break;
                }
            }

            mKilometers.setText(String.format(Locale.CANADA, "%d", result.getKilometers()));
            mDoors.setText(String.format(Locale.CANADA, "%d", result.getDoors()));
            mSeats.setText(String.format(Locale.CANADA, "%d", result.getSeats()));

            String transmission = result.getTransmission();

            for(int i = 0; i < mTransmission.getCount(); i++){
                String item = (String)mTransmission.getItemAtPosition(i);
                if(
                        (item.equals(transmission)) ||
                        (item.toLowerCase().contains("automatic") && transmission.toLowerCase().contains("automatic"))||
                                (item.toLowerCase().contains("manual") && transmission.toLowerCase().contains("manual"))
                        ){
                    mTransmission.setSelection(i);
                    break;
                }
            }

            String driveTrain = result.getDriveTrain();

            for(int i = 0; i < mDriveTrain.getCount(); i++){
                String item = (String)mDriveTrain.getItemAtPosition(i);
                if(
                        (item.equals(driveTrain)) ||
                        (item.equals("RWD") && driveTrain.toLowerCase().contains("rear"))||
                                (item.equals("FWD") && driveTrain.toLowerCase().contains("front"))||
                                (item.equals("AWD") && driveTrain.toLowerCase().contains("all"))||
                                (item.contains("4WD") && driveTrain.toLowerCase().contains("four"))||
                                (item.contains("4x4") && driveTrain.toLowerCase().contains("4"))
                        ){
                    mDriveTrain.setSelection(i);
                    break;
                }
            }

            mVin.setText(result.getVin());
        }else if(vehicle.getType() == VehicleType.Boat){
            BoatExtra result = (BoatExtra) extra;

            mYear.setText(String.format(Locale.CANADA, "%d", result.getYear()));
            mMake.setText(result.getMake());
            mModel.setText(result.getModel());
            mTrim.setText(result.getTrim());
            mColor.setText(result.getColor());
            mBodyStyle.setText(result.getBodyStyle());
            mEngine.setText(result.getEngine());
            String fuelType = result.getFuelType();

            for(int i = 0; i < mFuelType.getCount(); i++){
                String item = (String)mFuelType.getItemAtPosition(i);
                if(
                        (item.equals(fuelType)) ||
                        (item.toLowerCase().contains("flex") && fuelType.toLowerCase().contains("flex")) ||
                                (item.toLowerCase().contains("natural") && fuelType.toLowerCase().contains("natural"))||
                                (item.toLowerCase().contains("gas") && fuelType.toLowerCase().contains("gas"))||
                                (item.toLowerCase().contains("hybrid") && fuelType.toLowerCase().contains("hybrid"))||
                                (item.toLowerCase().contains("electric") && fuelType.toLowerCase().contains("electric"))||
                                (item.toLowerCase().contains("diesel") && fuelType.toLowerCase().contains("diesel"))
                        ){
                    mFuelType.setSelection(i);
                    break;
                }
            }

            mKilometers.setText(String.format(Locale.CANADA, "%d", result.getKilometers()));
        }else if(vehicle.getType() == VehicleType.Motorcycle){
            MotorcycleExtra result = (MotorcycleExtra) extra;

            mYear.setText(String.format(Locale.CANADA, "%d", result.getYear()));
            mMake.setText(result.getMake());
            mModel.setText(result.getModel());
            mTrim.setText(result.getTrim());
            mColor.setText(result.getColor());
            mBodyStyle.setText(result.getBodyStyle());
            mEngine.setText(result.getEngine());
            String fuelType = result.getFuelType();

            for(int i = 0; i < mFuelType.getCount(); i++){
                String item = (String)mFuelType.getItemAtPosition(i);
                if(
                        (item.equals(fuelType)) ||
                        (item.toLowerCase().contains("flex") && fuelType.toLowerCase().contains("flex")) ||
                                (item.toLowerCase().contains("natural") && fuelType.toLowerCase().contains("natural"))||
                                (item.toLowerCase().contains("gas") && fuelType.toLowerCase().contains("gas"))||
                                (item.toLowerCase().contains("hybrid") && fuelType.toLowerCase().contains("hybrid"))||
                                (item.toLowerCase().contains("electric") && fuelType.toLowerCase().contains("electric"))||
                                (item.toLowerCase().contains("diesel") && fuelType.toLowerCase().contains("diesel"))
                        ){
                    mFuelType.setSelection(i);
                    break;
                }
            }

            mKilometers.setText(String.format(Locale.CANADA, "%d", result.getKilometers()));
        }

        Button submitButton = (Button)v.findViewById(R.id.submit_action);
        submitButton.setText("Update Listing");
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmission();
            }
        });

        return v;
    }

    private void hideSoftKeyboard(){
        if(getActivity().getCurrentFocus()!=null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

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

    private void attemptSubmission(){
        boolean cancel = false;
        View mFocusView = null;

        if(TextUtils.isEmpty(mTitle.getText())){
            cancel = true;
            mFocusView = mTitle;
            mTitle.setError("You must provide a title!");
        }

        if(TextUtils.isEmpty(mDescription.getText())){
            cancel = true;
            mFocusView = mDescription;
            mDescription.setError("You must provide a description!");
        }

        if(TextUtils.isEmpty(mAskingPrice.getText())){
            cancel = true;
            mFocusView = mAskingPrice;
            mAskingPrice.setError("You must provide a asking price!");
        }else{
            try{
                double value = Double.parseDouble(mAskingPrice.getText().toString().trim());
            }catch(Exception e){
                cancel = true;
                mFocusView = mAskingPrice;
                mAskingPrice.setError("Invalid asking price!");
            }
        }

        if(TextUtils.isEmpty(mLowPrice.getText())){
            cancel = true;
            mFocusView = mLowPrice;
            mLowPrice.setError("You must provide a low price!");
        }else{
            try{
                double value = Double.parseDouble(mLowPrice.getText().toString().trim());

            }catch(Exception e){
                cancel = true;
                mFocusView = mLowPrice;
                mLowPrice.setError("Invalid low price!");
            }
        }

        try{
            if(Double.parseDouble(mLowPrice.getText().toString().trim()) > Double.parseDouble(mAskingPrice.getText().toString().trim())){
                cancel = true;
                mFocusView = mLowPrice;
                mLowPrice.setError("Low price cannot be greater than your asking price!");
            }
        }catch(Exception e){

        }


        if(TextUtils.isEmpty(mLocation.getText())){
            cancel = true;
            mFocusView = mLocation;
            mLocation.setError("You must provide a location!");
        }

        switch (mType){
            case Car:
                if(TextUtils.isEmpty(mInteriorColor.getText())){
                    cancel = true;
                    mFocusView = mInteriorColor;
                    mInteriorColor.setError("You must provide an interior color!");
                }
                if(TextUtils.isEmpty(mExteriorColor.getText())){
                    cancel = true;
                    mFocusView = mExteriorColor;
                    mExteriorColor.setError("You must provide an exterior color!");
                }
                if(TextUtils.isEmpty(mDoors.getText())){
                    cancel = true;
                    mFocusView = mDoors;
                    mDoors.setError("You must provide the number of doors!");
                }else{
                    try{
                        int value = Integer.parseInt(mDoors.getText().toString().trim());
                        if(value < 1)
                            throw new Exception();
                    }catch(Exception e){
                        cancel = true;
                        mFocusView = mDoors;
                        mDoors.setError("Invalid number of doors!");
                    }
                }
                if(TextUtils.isEmpty(mSeats.getText())){
                    cancel = true;
                    mFocusView = mSeats;
                    mSeats.setError("You must provide the number of seats!");
                }else{
                    try{
                        int value = Integer.parseInt(mSeats.getText().toString().trim());
                        if(value < 1)
                            throw new Exception();
                    }catch(Exception e){
                        cancel = true;
                        mFocusView = mSeats;
                        mSeats.setError("Invalid number of seats!");
                    }
                }

                if(((String)mTransmission.getSelectedItem()).endsWith("Select One")){
                    cancel = true;
                    mFocusView = mTransmission;
                    Toast.makeText(getContext(), "You must provide the vehicles transmission type!", Toast.LENGTH_LONG).show();
                    mFocusView.requestFocus();
                    return;
                }
                if(((String)mDriveTrain.getSelectedItem()).endsWith("Select One")){
                    cancel = true;
                    mFocusView = mDriveTrain;
                    Toast.makeText(getContext(), "You must provide the vehicles drivetrain!", Toast.LENGTH_LONG).show();
                    mFocusView.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(mVin.getText())){
                    cancel = true;
                    mFocusView = mVin;
                    mVin.setError("You must provide the VIN number of the vehicle!");
                }
            case Motorcycle:
            case Boat:
                if(TextUtils.isEmpty(mYear.getText())){
                    cancel = true;
                    mFocusView = mYear;
                    mYear.setError("You must provide the year of manufacturing!");
                }else{
                    try{
                        int value = Integer.parseInt(mYear.getText().toString().trim());
                        if(value < 1900)
                            throw new Exception();
                    }catch(Exception e){
                        cancel = true;
                        mFocusView = mYear;
                        mYear.setError("Invalid year!");
                    }
                }

                if(TextUtils.isEmpty(mMake.getText())){
                    cancel = true;
                    mFocusView = mMake;
                    mMake.setError("You must provide the vehicles make!");
                }
                if(TextUtils.isEmpty(mModel.getText())){
                    cancel = true;
                    mFocusView = mModel;
                    mModel.setError("You must provide the model name!");
                }
                if(TextUtils.isEmpty(mTrim.getText())){
                    cancel = true;
                    mFocusView = mTrim;
                    mTrim.setError("You must provide the vehicles trim!");
                }
                if(TextUtils.isEmpty(mBodyStyle.getText())){
                    cancel = true;
                    mFocusView = mBodyStyle;
                    mBodyStyle.setError("You must provide the body style!");
                }
                if(TextUtils.isEmpty(mEngine.getText())){
                    cancel = true;
                    mFocusView = mEngine;
                    mEngine.setError("You must provide the engine specifications!");
                }
                if(((String)mFuelType.getSelectedItem()).endsWith("Select One")){
                    cancel = true;
                    mFocusView = mFuelType;
                    Toast.makeText(getContext(), "You must provide the vehicles fuel type!", Toast.LENGTH_LONG).show();
                    mFocusView.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(mKilometers.getText())){
                    cancel = true;
                    mFocusView = mKilometers;
                    mKilometers.setError("You must provide the approximate kilometers traveled!");
                }else{
                    try{
                        long value = Long.parseLong(mKilometers.getText().toString().trim());
                    }catch(Exception e){
                        cancel = true;
                        mFocusView = mKilometers;
                        mKilometers.setError("Invalid kilometers!");
                    }
                }
        }

        if(mType == VehicleType.Boat || mType == VehicleType.Motorcycle){
            if(TextUtils.isEmpty(mColor.getText())){
                cancel = true;
                mFocusView = mColor;
                mColor.setError("You must provide a color!");
            }
        }

        if(cancel){
            mFocusView.requestFocus();
        }else{
            VehicleExtra extra;
            switch(mType){
                case Car:
                    extra = new CarExtra(
                            Integer.parseInt(mYear.getText().toString().trim()),
                            mMake.getText().toString().trim(),
                            mModel.getText().toString().trim(),
                            mInteriorColor.getText().toString().trim(),
                            mExteriorColor.getText().toString().trim(),
                            mTrim.getText().toString().trim(),
                            mBodyStyle.getText().toString().trim(),
                            mEngine.getText().toString().trim(),
                            ((String)mFuelType.getSelectedItem()).trim(),
                            Long.parseLong(mKilometers.getText().toString().trim()),
                            Integer.parseInt(mDoors.getText().toString().trim()),
                            Integer.parseInt(mSeats.getText().toString().trim()),
                            ((String)mTransmission.getSelectedItem()).trim(),
                            ((String)mDriveTrain.getSelectedItem()).trim(),
                            mVin.getText().toString().trim()
                    );
                    break;
                case Motorcycle:
                    extra = new MotorcycleExtra(
                            Integer.parseInt(mYear.getText().toString().trim()),
                            mMake.getText().toString().trim(),
                            mModel.getText().toString().trim(),
                            mColor.getText().toString().trim(),
                            mTrim.getText().toString().trim(),
                            mBodyStyle.getText().toString().trim(),
                            mEngine.getText().toString().trim(),
                            ((String)mFuelType.getSelectedItem()).trim(),
                            Long.parseLong(mKilometers.getText().toString().trim())
                    );
                    break;
                case Boat:
                    extra = new BoatExtra(
                            Integer.parseInt(mYear.getText().toString().trim()),
                            mMake.getText().toString().trim(),
                            mModel.getText().toString().trim(),
                            mColor.getText().toString().trim(),
                            mTrim.getText().toString().trim(),
                            mBodyStyle.getText().toString().trim(),
                            mEngine.getText().toString().trim(),
                            ((String)mFuelType.getSelectedItem()).trim(),
                            Long.parseLong(mKilometers.getText().toString().trim())
                    );
                    break;
                case Equipment:
                case Other:
                default:
                    extra = null;
            }

            String title;
            if(mType == VehicleType.Equipment || mType == VehicleType.Other)
                title = mTitle.getText().toString();
            else
                title = String.format(Locale.CANADA, "%s %s %s %s",
                        mYear.getText().toString().trim(),
                        mMake.getText().toString().trim(),
                        mModel.getText().toString().trim(),
                        mTrim.getText().toString().trim()
                );

            Vehicle vehicle = new Vehicle(
                    title.trim(),
                    mDescription.getText().toString().trim(),
                    mType,
                    mType.toString(),
                    extra,
                    null
            );

            Listing listing = new Listing(
                    Double.parseDouble(mAskingPrice.getText().toString().trim()),
                    Double.parseDouble(mLowPrice.getText().toString().trim()),
                    mSafeZone.isChecked(),
                    mLookingFor.isChecked(),
                    mLocation.getText().toString().trim(),
                    vehicle
            );
            hideSoftKeyboard();
            showProgress(true);
            APIConsumer.APIAsyncTask<Listing> task;

            APIResponder<Listing> responder = new APIResponder<Listing>() {
                @Override
                public void success(Listing result) {

                    Toast.makeText(getActivity(), "Thank you for submitting. Your listing has successfully been updated!", Toast.LENGTH_LONG).show();

                    getActivity().finish();
                }

                @Override
                public void error(String errorMessage) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                    showProgress(false);
                }
            };

            task = APIConsumer.UpdateListing(PreferencesManager.getUserInformation().getAccessToken(),
                    oldListing.getId(),
                    listing, responder);

            task.execute();
        }
    }

}
