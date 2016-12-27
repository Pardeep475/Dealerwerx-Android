package deanmyers.com.dealerwerx;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.DebugUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
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
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;

import org.json.JSONException;

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

public class AddListingExtendedFragment extends TitleCompatFragment {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        VehicleType type = VehicleType.get(getArguments().getChar("type"));
        scavenger = getArguments().getBoolean("scavenger", false);

        if(type == null)
        return null;

        mType = type;

        VinDecodeResult result = ((AddListingActivity)getActivity()).getDecodeResult();

        int layout;

        switch(type){
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

        setViewTitle(container, "Add " + type.toString() + " Listing");

        mProgressView = (ProgressBar)container.getRootView().findViewById(R.id.addlisting_progress);

        Button addImages = (Button)v.findViewById(R.id.action_addimage);
        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

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

        mLookingFor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSafeZone.setChecked(false);
                mSafeZone.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        if(scavenger) {
            ((View)mLowPrice.getParent()).setVisibility(View.GONE);
            ((View)mSafeZone).setVisibility(View.GONE);

            mLowPrice = mAskingPrice;
        }else{
            ((View)mLookingFor).setVisibility(View.GONE);
        }

        if(type == VehicleType.Car){
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
        switch(type){
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

        if(type == VehicleType.Car && result != null){
            mTitle.setText(result.getTitle());

            mYear.setText(String.format(Locale.CANADA, "%d", result.getYear()));
            mMake.setText(result.getMake());
            mModel.setText(result.getModel());
            mTrim.setText(result.getTrim());
            mBodyStyle.setText(result.getBodyStyle());
            mEngine.setText(result.getEngine());
            mDoors.setText(String.format(Locale.CANADA, "%d", result.getDoors()));
            mVin.setText(result.getVin());

            String fuelType = result.getFuelType();

            for(int i = 0; i < mFuelType.getCount(); i++){
                String item = (String)mFuelType.getItemAtPosition(i);
                if(
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

            String transmission = result.getTransmission();

            for(int i = 0; i < mTransmission.getCount(); i++){
                String item = (String)mTransmission.getItemAtPosition(i);
                if(
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
    }

        Button submitButton = (Button)v.findViewById(R.id.submit_action);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmission();
            }
        });

        return v;
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PHOTO_PERMISSIONS = 1;

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
                    final Listing listingResult = result;
                    if(imageCount != 0){
                        for(int i = 0; i < imageCount; i++){
                            APIConsumer.UploadImageAsyncTask task1 = APIConsumer.UploadImage(
                                    PreferencesManager.getUserInformation().getAccessToken(),
                                    result.getId(),
                                    imageList[i],
                                    new APIResponder<Void>() {
                                        @Override
                                        public void success(Void result) {
                                        }

                                        @Override
                                        public void error(String errorMessage) {

                                        }
                                    }
                            );
                            task1.execute();
                            try {
                                task1.get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if(listingResult.getApprovalStatus().equals("Approved")){
                        Toast.makeText(getActivity(), "Thank you for submitting. Your listing has been approved!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(), "Thank you for submitting. Your listing is now pending approval.", Toast.LENGTH_LONG).show();
                    }
                    getActivity().finish();
                }

                @Override
                public void error(String errorMessage) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                    showProgress(false);
                }
            };

            if(scavenger)
                task = APIConsumer.CreateScavengerListing(PreferencesManager.getUserInformation().getAccessToken(),
                    listing, responder);
            else
                task = APIConsumer.CreateListing(PreferencesManager.getUserInformation().getAccessToken(),
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

        if (resultCode == RESULT_OK) {
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
                            final ImageView newImageView = new ImageView(getActivity());
                            final ViewGroup container = (ViewGroup) getView().findViewById(R.id.image_container);

                            Bitmap bitmap = cropView.getCroppedBitmap();

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
                                                Toast.makeText(getActivity(), "Image successfully deleted!", Toast.LENGTH_LONG).show();
                                            }

                                            nagDialog.dismiss();
                                        }
                                    });
                                    nagDialog.show();
                                }
                            });

                            container.addView(newImageView);
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
