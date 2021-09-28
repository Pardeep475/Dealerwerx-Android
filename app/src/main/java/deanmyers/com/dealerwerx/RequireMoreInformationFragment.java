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

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.UserInformation;

/**
 * Created by mac3 on 2016-11-17.
 */

public class RequireMoreInformationFragment extends TitleCompatFragment {

    private Button mContinue;
    private Button mCancel;
    private EditText mPhoneNumber;
    private ProgressBar mProgressView;
    private View mMyView;

    public TitleCompatFragment targetFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_extrainformation, container, false);
        setViewTitle(container, "Extra Information");
        mMyView = v;
        mContinue = (Button)v.findViewById(R.id.submit_action);
        mCancel = (Button)v.findViewById(R.id.cancel_action);
        mPhoneNumber = (EditText) v.findViewById(R.id.phonenumber);

        mProgressView = (ProgressBar)container.getRootView().findViewById(R.id.addlisting_progress);

        mPhoneNumber.setText("+1");
        mPhoneNumber.setSelection(2);
        mPhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == R.id.action_continue){
                    updatePhoneNumber();
                    return true;
                }

                return false;
            }
        });
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhoneNumber();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return v;
    }

    private void updatePhoneNumber(){
        hideSoftKeyboard();
        Pattern p = Pattern.compile("^([+][0-9]+)?[\\s-]?[(]?([0-9]{3})[)]?[\\s-]?([0-9]{3})[\\s-]?([0-9]{4})$");
        Matcher m = p.matcher(mPhoneNumber.getText());
        if(m.matches()){
            String group1 = m.group(1);
            String group2 = m.group(2);
            String group3 = m.group(3);
            String group4 = m.group(4);

            if(group1 == null || group1.length() == 0){
                group1 = "+1";
            }

            String phoneNumber = String.format("%s(%s)%s-%s", group1, group2, group3, group4);

            UserInformation currentInformation = PreferencesManager.getUserInformation();

            UserInformation newInformation = new UserInformation(
                    currentInformation.getId(),
                    currentInformation.getFirstName(),
                    currentInformation.getLastName(),
                    currentInformation.getEmail(),
                    phoneNumber,
                    currentInformation.getQrImageUrl(),
                    currentInformation.getIsAgent(),
                    currentInformation.getAccessToken()
            );

            showProgress(true);
            APIConsumer.UpdateUserAsyncTask task = APIConsumer.UpdateUser(newInformation, new APIResponder<UserInformation>() {
                @Override
                public void success(UserInformation result) {
                    showProgress(false);
                    PreferencesManager.setUserInformation(result);
                    proceedToNext();
                }

                @Override
                public void error(String errorMessage) {
                    showProgress(false);
                    mPhoneNumber.setError(errorMessage);
                    mPhoneNumber.requestFocus();
                }
            });
            task.execute();
        }else{
            mPhoneNumber.setError("Invalid phone number");
            mPhoneNumber.requestFocus();
        }
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

    private void proceedToNext(){

        getActivity().setResult(1);
        Toast.makeText(getActivity(), "Information submitted, thank you.", Toast.LENGTH_LONG).show();

        if(targetFragment == null){
            getActivity().finish();
            return;
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Bundle arguments = getArguments();
        if(arguments == null)
            arguments = new Bundle();

        targetFragment.setArguments(arguments);

        transaction.replace(R.id.fragment_container, targetFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
