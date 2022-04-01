package deanmyers.project.dealerwerx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import deanmyers.project.dealerwerx.API.APIConsumer;
import deanmyers.project.dealerwerx.API.APIResponder;
import deanmyers.project.dealerwerx.API.Beacon;

public class AddBeaconActivity extends TitleCompatActivity {
    private View mMyView;
    private ProgressBar mProgressView;
    private EditText mActivationCode;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbeacon);
        //doLoad(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        setViewTitlePrimary("Add Beacon");
        setViewTitle("");

        mMyView = findViewById(R.id.form_container);
        mProgressView = (ProgressBar)findViewById(R.id.addbeacon_progress);
        mActivationCode = (EditText)findViewById(R.id.code);
        mActivationCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.activationcode || id == EditorInfo.IME_NULL) {
                    activateCode();
                    return true;
                }
                return false;
            }
        });

        mSubmitButton = (Button)findViewById(R.id.submit_action);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateCode();
            }
        });
    }
    private void activateCode(){
        String code = mActivationCode.getText().toString();

        if(!Pattern.compile("^\\d{2}[A-Z]\\d[A-Z]{3}\\d$").matcher(code).matches()){
            mActivationCode.setError("Invalid activation code");
        }else{
            showProgress(true);

            APIConsumer.AddBeaconAsyncTask task = APIConsumer.AddBeacon(
                    PreferencesManager.getUserInformation().getAccessToken(),
                    code,
                    new APIResponder<Beacon>() {
                        @Override
                        public void success(Beacon result) {
                            Toast.makeText(AddBeaconActivity.this, "Successfully added beacon!", Toast.LENGTH_LONG).show();
                            showProgress(false);
                            finish();
                        }

                        @Override
                        public void error(String errorMessage) {
                            showProgress(false);
                            mActivationCode.setError(errorMessage);
                        }
                    }
            );
            task.execute();
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
}
