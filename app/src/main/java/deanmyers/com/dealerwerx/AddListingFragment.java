package deanmyers.com.dealerwerx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.VehicleType;
import deanmyers.com.dealerwerx.API.VinDecodeResult;

/**
 * Created by mac3 on 2016-11-17.
 */

public class AddListingFragment extends TitleCompatFragment {

    private Spinner mVehicleTypes;
    private EditText mVinInput;
    private Button mContinue;
    private Button mCancel;
    private ProgressBar mProgressView;
    private View mMyView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_addlisting, container, false);
        setViewTitle(container, "");
        mMyView = v;

        mVinInput = (EditText)v.findViewById(R.id.vin);
        mVehicleTypes = (Spinner)v.findViewById(R.id.vehicle_type);
        mContinue = (Button)v.findViewById(R.id.submit_action);
        mCancel = (Button)v.findViewById(R.id.cancel_action);
        mProgressView = (ProgressBar)container.getRootView().findViewById(R.id.addlisting_progress);

        ArrayList<InputFilter> filters = new ArrayList<>();
        InputFilter[] currentFilters = mVinInput.getFilters();

        filters.addAll(Arrays.asList(currentFilters));

        filters.add(new InputFilter.AllCaps());

        mVinInput.setFilters(filters.toArray(new InputFilter[]{}));
        mVinInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.vindecode || id == EditorInfo.IME_NULL) {
                    attemptVinDecode();
                    return true;
                }
                return false;
            }
        });


        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVinInput.length() == 0)
                    proceedToNext(null);
                else
                    attemptVinDecode();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        ArrayAdapter<VehicleType> spinnerArrayAdapter = new ArrayAdapter<VehicleType>(getActivity(), android.R.layout.simple_spinner_item, Arrays.asList(new VehicleType[]{
                VehicleType.Car,
                VehicleType.Motorcycle,
                VehicleType.Boat,
                VehicleType.Equipment,
                VehicleType.Other
        }));

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mVehicleTypes.setAdapter(spinnerArrayAdapter);

        return v;
    }

    private void hideSoftKeyboard(){
        if(getActivity().getCurrentFocus()!=null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
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

    private void attemptVinDecode(){
        if(mVinInput.length() != 17){
            mVinInput.setError("Invalid VIN");
            mVinInput.requestFocus();
        }else{
            showProgress(true);
            hideSoftKeyboard();
            APIConsumer.VinDecodeAsyncTask task = APIConsumer.VinDecode(PreferencesManager.getUserInformation().getAccessToken(),
                    mVinInput.getText().toString(), new APIResponder<VinDecodeResult>() {
                        @Override
                        public void success(VinDecodeResult result) {
                            showProgress(false);
                            proceedToNext(result);
                        }

                        @Override
                        public void error(String errorMessage) {
                            mVinInput.setError(errorMessage);
                            mVinInput.requestFocus();
                            showProgress(false);
                        }
                    });
            task.execute();
        }
    }

    private void proceedToNext(VinDecodeResult result){
        VehicleType forwardType;

        if(result != null)
            forwardType = VehicleType.Car;
        else
            forwardType = (VehicleType)mVehicleTypes.getSelectedItem();

        AddListingExtendedFragment fragment = new AddListingExtendedFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Bundle arguments = getArguments();
        if(arguments == null)
            arguments = new Bundle();

        arguments.putChar("type", forwardType.asChar());
        fragment.setArguments(arguments);

        ((AddListingActivity)getActivity()).setDecodeResult(result);

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
