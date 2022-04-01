package deanmyers.project.dealerwerx;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import deanmyers.project.dealerwerx.API.APIConsumer;
import deanmyers.project.dealerwerx.API.APIResponder;
import deanmyers.project.dealerwerx.API.BoatExtra;
import deanmyers.project.dealerwerx.API.CarExtra;
import deanmyers.project.dealerwerx.API.ImageMedia;
import deanmyers.project.dealerwerx.API.Listing;
import deanmyers.project.dealerwerx.API.MotorcycleExtra;
import deanmyers.project.dealerwerx.API.Vehicle;
import deanmyers.project.dealerwerx.API.VehicleExtra;
import deanmyers.project.dealerwerx.API.VehicleType;
import deanmyers.project.dealerwerx.Adapters.HintArrayAdapter;

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
    private EditText mCustomerNumber;
    private EditText mStockNumber;
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

    private final int PLACEPICKER_REQUEST = 1001;
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
        //v.findViewById(R.id.add_image_container).setVisibility(View.GONE);

        Button addImages = (Button)v.findViewById(R.id.action_addimage);
        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        setViewTitle(container, "Update " + vehicle.getType().toString() + " Listing");

        mProgressView = (ProgressBar)container.getRootView().findViewById(R.id.addlisting_progress);

        mLookingFor = (SwitchCompat)v.findViewById(R.id.lookingfor);
        mTitle = (EditText)v.findViewById(R.id.title);
        mDescription = (EditText)v.findViewById(R.id.description);
        mAskingPrice = (EditText)v.findViewById(R.id.askingPrice);
        mLowPrice = (EditText)v.findViewById(R.id.lowPrice);
        mSafeZone = (SwitchCompat)v.findViewById(R.id.safezone);
        mLocation = (EditText)v.findViewById(R.id.location);
        mCustomerNumber = (EditText)v.findViewById(R.id.customer_number);
        mStockNumber = (EditText)v.findViewById(R.id.stock_number);

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
            ((View)mCustomerNumber.getParent()).setVisibility(View.GONE);
            ((View)mStockNumber.getParent()).setVisibility(View.GONE);
            mLowPrice = mAskingPrice;
        }else{
            if(!PreferencesManager.getUserInformation().getIsAgent()){
                ((View)mCustomerNumber.getParent()).setVisibility(View.GONE);
                ((View)mStockNumber.getParent()).setVisibility(View.GONE);
            }
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

        mAskingPrice.setText(listing.getAskingPrice() < 0 ? "" : String.format(Locale.CANADA, "%.2f", listing.getAskingPrice()));
        mLowPrice.setText(listing.getLowPrice() < 0 ? "" : String.format(Locale.CANADA, "%.2f", listing.getLowPrice()));
        mLocation.setText(listing.getLocation());

        mSafeZone.setChecked(listing.isSafeZone());
        mSafeZone.setEnabled(false);
        mLookingFor.setChecked(listing.isLookingFor());
        mLookingFor.setEnabled(false);

        mSafeZone.setVisibility(View.GONE);
        mLookingFor.setVisibility(View.GONE);

        if(listing.getCustomerNumber() != null)
            mCustomerNumber.setText(String.format(Locale.CANADA, "%d", listing.getCustomerNumber()));

        if(listing.getVehicle().getStockNumber() != null)
            mStockNumber.setText(listing.getVehicle().getStockNumber());

        if(vehicle.getType() == VehicleType.Car){
            CarExtra result = (CarExtra)extra;

            mYear.setText(String.format(Locale.CANADA, "%d", result.getYear()));
            mMake.setText(inputClean(result.getMake()));
            mModel.setText(inputClean(result.getModel()));
            mTrim.setText(inputClean(result.getTrim()));
            mInteriorColor.setText(inputClean(result.getInteriorColor()));
            mExteriorColor.setText(inputClean(result.getExteriorColor()));
            mBodyStyle.setText(inputClean(result.getBodyStyle()));
            mEngine.setText(inputClean(result.getEngine()));

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
            mMake.setText(inputClean(result.getMake()));
            mModel.setText(inputClean(result.getModel()));
            mTrim.setText(inputClean(result.getTrim()));
            mColor.setText(inputClean(result.getColor()));
            mBodyStyle.setText(inputClean(result.getBodyStyle()));
            mEngine.setText(inputClean(result.getEngine()));
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
            mMake.setText(inputClean(result.getMake()));
            mModel.setText(inputClean(result.getModel()));
            mTrim.setText(inputClean(result.getTrim()));
            mColor.setText(inputClean(result.getColor()));
            mBodyStyle.setText(inputClean(result.getBodyStyle()));
            mEngine.setText(inputClean(result.getEngine()));
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


        reloadImages();

        return v;
    }

    private void reloadImages(){
        imageCount = 0;
        imageList = new Bitmap[5];

        if(getView() != null){
            final ViewGroup container = (ViewGroup) getView().findViewById(R.id.image_container);
            container.removeAllViews();
        }

        final ImageMedia[] media = oldListing.getVehicle().getMedia();

        if(media != null && media.length > 0){
            for(int i = 0; i < media.length; i++){
                final int index = i;
                APIConsumer.DownloadImageAsyncTask task = APIConsumer.DownloadImage(getActivity(), media[i].getImageUrl(), new APIResponder<Bitmap>() {
                    @Override
                    public void success(Bitmap result) {
                        addImage(result, true, media[index].getId());
                    }

                    @Override
                    public void error(String errorMessage) {
                        Toast.makeText(getActivity(), "Unable to load image", Toast.LENGTH_LONG).show();
                    }
                });
                task.execute();
            }
        }
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PHOTO_PERMISSIONS = 1;

    private String inputClean(String input){
        if(input.equals("-") || input.equals(" ") ||
                input.equals("") || input.equals("N/A") ||
                input.equals("n/a") || input.equals("na") ||
                input.equals("none") || input.equals("empty"))
            return "";
        else
            return input;
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

        if(mType == VehicleType.Equipment || mType == VehicleType.Other)
            if(TextUtils.isEmpty(mTitle.getText())){
                cancel = true;
                mFocusView = mTitle;
                mTitle.setError("You must provide a title!");
            }

        if(TextUtils.isEmpty(mDescription.getText())){
            mDescription.setText("-");
        }

        if(TextUtils.isEmpty(mAskingPrice.getText())){
            mAskingPrice.setText("-1");
        }else{
            try{
                double value = Double.parseDouble(mAskingPrice.getText().toString().trim());
            }catch(Exception e){
                cancel = true;
                mFocusView = mAskingPrice;
                mAskingPrice.setError("Invalid asking price!");
            }
        }

        Long customerNumber = null;

        if(!PreferencesManager.getUserInformation().getIsAgent() || TextUtils.isEmpty(mCustomerNumber.getText())){
            customerNumber = null;
        }else{
            try{
                customerNumber = Long.parseLong(mCustomerNumber.getText().toString().trim());
            }catch(Exception e){
                cancel = true;
                mFocusView = mCustomerNumber;
                mCustomerNumber.setError("Invalid customer number!");
            }
        }

        if(TextUtils.isEmpty(mLowPrice.getText())){
            mLowPrice.setText("-1");
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
            mLocation.setText("-");
        }

        switch (mType){
            case Car:
                if(TextUtils.isEmpty(mInteriorColor.getText())){
                    mInteriorColor.setText("-");
                }
                if(TextUtils.isEmpty(mExteriorColor.getText())){
                    cancel = true;
                    mFocusView = mExteriorColor;
                    mExteriorColor.setError("You must provide an exterior color!");
                }
                if(TextUtils.isEmpty(mDoors.getText())){
                    mDoors.setText("0");
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
                    mSeats.setText("0");
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
                if(TextUtils.isEmpty(mVin.getText())){
                    mVin.setText("-");
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
                    mTrim.setText("-");
                }
                if(TextUtils.isEmpty(mBodyStyle.getText())){
                    mBodyStyle.setText("-");
                }
                if(TextUtils.isEmpty(mEngine.getText())){
                    mEngine.setText("-");
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
                            ((String)mFuelType.getSelectedItem()).endsWith("Select One") ? "-" : ((String)mFuelType.getSelectedItem()).trim(),
                            Long.parseLong(mKilometers.getText().toString().trim()),
                            Integer.parseInt(mDoors.getText().toString().trim()),
                            Integer.parseInt(mSeats.getText().toString().trim()),
                            ((String)mTransmission.getSelectedItem()).trim(),
                            ((String)mDriveTrain.getSelectedItem()).endsWith("Select One") ? "-" : ((String)mDriveTrain.getSelectedItem()).trim(),
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
                            ((String)mFuelType.getSelectedItem()).endsWith("Select One") ? "-" : ((String)mFuelType.getSelectedItem()).trim(),
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
                            ((String)mFuelType.getSelectedItem()).endsWith("Select One") ? "-" : ((String)mFuelType.getSelectedItem()).trim(),
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

            String stockNumber = PreferencesManager.getUserInformation().getIsAgent() ? mStockNumber.getText().toString().trim() : null;

            if(stockNumber != null && stockNumber.length() == 0)
                stockNumber = null;

            Vehicle vehicle = new Vehicle(
                    title.trim(),
                    mDescription.getText().toString().trim(),
                    stockNumber,
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
                    customerNumber,
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
    private void dispatchTakePictureIntent() {
        requestPermissions(
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                REQUEST_PHOTO_PERMISSIONS);
    }

    private Intent getPickImageIntent(Context context) throws IOException {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        try {
            imageUri = Uri.fromFile(createTemporaryFile("rnd", ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "How would you like to upload your image?");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if((grantResults.length == 0) || (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                Intent takePictureIntent = null; //new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    takePictureIntent = getPickImageIntent(getActivity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (takePictureIntent != null && takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }
    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= getActivity().getExternalCacheDir();

        return File.createTempFile(part, ext, tempDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PLACEPICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(getActivity(), data);
                if(place.getAddress().length() > 0){
                    mLocation.setText(place.getAddress());
                }else{
                    mLocation.setText(place.getName());
                }
            }
        }
        else if (resultCode == RESULT_OK) {
            Uri targetUri = data == null ? imageUri : data.getData();

            Bitmap newBitmap;
            try {
                newBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));

                try{
                    String path = getPath(this.getContext(), targetUri);

                    if(path != null){
                        ExifInterface exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap newnewBitmap = rotateBitmap(newBitmap, orientation);
                        if(newnewBitmap != null){
                            newBitmap = newnewBitmap;
                        }
                    }
                }catch(Exception e){
                    int b = 0;
                }

                if (newBitmap.getHeight() > 2000) {
                    int nw = (int) (((double) 2000 / (double) newBitmap.getHeight()) * (newBitmap.getWidth()));
                    newBitmap = Bitmap.createScaledBitmap(newBitmap, nw, 2000, true);
                }

                if (newBitmap.getWidth() > 2000) {
                    int nh = (int) ((newBitmap.getHeight()) * ((double) 2000 / (double) newBitmap.getWidth()));
                    newBitmap = Bitmap.createScaledBitmap(newBitmap, 2000, nh, true);
                }

                final Dialog cropDialog = new Dialog(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                cropDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                cropDialog.setContentView(R.layout.crop_view);

                final CropImageView cropView = (CropImageView)cropDialog.findViewById(R.id.cropper);

                ImageButton cropRotateLeft = (ImageButton)cropDialog.findViewById(R.id.crop_rotate_left);
                ImageButton cropRotateRight = (ImageButton)cropDialog.findViewById(R.id.crop_rotate_right);
                ImageButton cropCancel = (ImageButton)cropDialog.findViewById(R.id.crop_cancel);
                ImageButton cropDone = (ImageButton)cropDialog.findViewById(R.id.crop_done);

                final Bitmap finalBitmap = newBitmap;

                cropRotateLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    }
                });

                cropRotateRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    }
                });

                cropCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cropDialog.dismiss();
                    }
                });

                cropDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Bitmap bitmap = cropView.getCroppedBitmap();

                            addImage(bitmap, false, 0);

                        }catch(Exception e) {

                        }
                        cropDialog.dismiss();
                    }});

                cropView.setImageBitmap(finalBitmap);

                cropDialog.show();

            } catch (Exception e) {
                //Do nothing with result
            }
        }
    }

    public void addImage(Bitmap bitmap, final boolean isApi, final int apiId){
        if(!isApi){
            final Button mAddImage = (Button) getView().findViewById(R.id.action_addimage);

            mAddImage.setEnabled(false);
            Toast.makeText(getActivity(), "Uploading image...", Toast.LENGTH_LONG).show();

            APIConsumer.UploadImageAsyncTask task = APIConsumer.UploadImage(PreferencesManager.getUserInformation().getAccessToken(),
                    oldListing.getId(),
                    bitmap,
                    new APIResponder<Listing>() {
                        @Override
                        public void success(Listing result) {
                            mAddImage.setEnabled(true);
                            Toast.makeText(getActivity(), "Image successfully uploaded!", Toast.LENGTH_LONG).show();
                            oldListing = result;
                            reloadImages();
                        }

                        @Override
                        public void error(String errorMessage) {
                            mAddImage.setEnabled(true);
                            Toast.makeText(getActivity(), "Unable to upload image", Toast.LENGTH_LONG).show();
                        }
                    });
            task.execute();
            return;
        }

        final ImageView newImageView = new ImageView(getActivity());
        final ViewGroup container = (ViewGroup) getView().findViewById(R.id.image_container);

        DisplayMetrics dimension = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dimension);
        int w = dimension.widthPixels;
        int h = dimension.heightPixels;

        final int THUMBSIZE = (Math.max(w, h) / 5) - 4;

        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap,
                THUMBSIZE, THUMBSIZE);

        int index = imageCount++;
        imageList[index] = bitmap;

        newImageView.setPadding(2, 2, 2, 2);
        newImageView.setImageBitmap(thumbImage);
        newImageView.setAdjustViewBounds(true);
        newImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        params.gravity = Gravity.CENTER_HORIZONTAL;

        newImageView.setLayoutParams(params);

        ((LinearLayout) container).setWeightSum(imageCount);

        final Bitmap finalBitmap = bitmap;
        final Button mAddImage = (Button) getView().findViewById(R.id.action_addimage);

        if (imageCount == 5)
            mAddImage.setEnabled(false);

            newImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog nagDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    nagDialog.setContentView(R.layout.preview_image);
                    Button btnClose = (Button) nagDialog.findViewById(R.id.action_close);
                    Button btnDelete = (Button) nagDialog.findViewById(R.id.action_delete);
                    ImageView ivPreview = (ImageView) nagDialog.findViewById(R.id.preview);
                    ivPreview.setImageBitmap(finalBitmap);

                    btnClose.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            nagDialog.dismiss();
                        }
                    });

                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            container.removeView(newImageView);

                            int index = -1;

                            for (int i = 0; i < imageList.length; i++) {
                                if (imageList[i] == finalBitmap) {
                                    index = i;
                                    imageList[i] = null;
                                }
                            }

                            if (index != -1) {
                                for (int i = index; i < imageList.length - 1; i++) {
                                    imageList[i] = imageList[i + 1];
                                }
                                imageList[imageList.length - 1] = null;
                                imageCount--;
                                ((LinearLayout) container).setWeightSum(imageCount);
                                mAddImage.setEnabled(true);
                                if (isApi) {
                                    Toast.makeText(getActivity(), "Deleting image...", Toast.LENGTH_LONG).show();

                                    APIConsumer.DeleteImageAsyncTask task = APIConsumer.DeleteImage(
                                            PreferencesManager.getUserInformation().getAccessToken(),
                                            oldListing.getId(),
                                            apiId,
                                            new APIResponder<Listing>() {
                                                @Override
                                                public void success(Listing result) {
                                                    oldListing = result;
                                                    Toast.makeText(getActivity(), "Image successfully deleted!", Toast.LENGTH_LONG).show();
                                                    container.removeAllViews();

                                                    reloadImages();
                                                }

                                                @Override
                                                public void error(String errorMessage) {
                                                    Toast.makeText(getActivity(), "Error deleting image", Toast.LENGTH_LONG).show();
                                                    container.removeAllViews();
                                                    reloadImages();
                                                }
                                            }
                                    );
                                    task.execute();
                                }
                            }

                            nagDialog.dismiss();
                        }
                    });
                    nagDialog.show();
                }
            });

            container.addView(newImageView);

    }
    public Uri createSaveUri() {
        return Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
