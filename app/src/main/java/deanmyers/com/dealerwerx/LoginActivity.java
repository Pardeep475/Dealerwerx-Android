package deanmyers.com.dealerwerx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import deanmyers.com.dealerwerx.API.UserInformation;
import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.APIConsumer.LoginAsyncTask;
import deanmyers.com.dealerwerx.Services.BeaconService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{
    private LoginAsyncTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mLogoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mEmailView = (EditText)findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        TextView mForgotPasswordView = (TextView)findViewById(R.id.forgotPassword);
        mForgotPasswordView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://dealerwerx-newpanel.us-east-1.elasticbeanstalk.com/forgot-password";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.goto_register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                   startActivityForResult(intent, 0);
               }
           }
        );

        mLoginFormView = findViewById(R.id.email_login_form);
        //mLoginFormView.setEnabled(true);
        mProgressView = findViewById(R.id.login_progress);
        mLogoView = findViewById(R.id.login_logo);


        final Animation loginBottomUp = AnimationUtils.loadAnimation(this.getApplicationContext(),
                R.anim.login_bottom_up);

        final Animation logoSlideUp = AnimationUtils.loadAnimation(this.getApplicationContext(),
                R.anim.logo_slide_up);

        Animation loginBottomHold = AnimationUtils.loadAnimation(this.getApplicationContext(),
                R.anim.login_bottom_hold);

        Animation logoSlideHold = AnimationUtils.loadAnimation(this.getApplicationContext(),
                R.anim.logo_slide_hold);

        mLogoView.startAnimation(logoSlideHold);
        mLoginFormView.startAnimation(loginBottomHold);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PreferencesManager.hasUserInformation()){
                    final UserInformation info = PreferencesManager.getUserInformation();
                    APIConsumer.ValidateTokenAsyncTask task = APIConsumer.ValidateToken(info.getAccessToken(), new APIResponder<UserInformation>() {
                        @Override
                        public void success(UserInformation result) {
                            PreferencesManager.setUserInformation(result);
                            goToListings();
                        }

                        @Override
                        public void error(String errorMessage) {
                            mEmailView.setText(info.getEmail());
                            mLogoView.startAnimation(logoSlideUp);
                            mLoginFormView.startAnimation(loginBottomUp);
                        }
                    });
                    task.execute();
                }else{
                    mLogoView.startAnimation(logoSlideUp);
                    mLoginFormView.startAnimation(loginBottomUp);
                }
            }
        }, 3000);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(PreferencesManager.hasUserInformation())
            goToListings();
    }

    private void goToListings(){
        if(BeaconService.getInstance() != null){
            try {
                BeaconService.getInstance().setEnabled(PreferencesManager.getAllowBackgroundScanning());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Intent listingsIntent = new Intent(LoginActivity.this, ListingsActivity.class);
        listingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(listingsIntent);
        finish();
    }

    private void hideSoftKeyboard(){
        if(getCurrentFocus()!=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void attemptLogin() {
        hideSoftKeyboard();
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = APIConsumer.Login(email, password, new LoginResponder());
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class LoginResponder extends APIResponder<UserInformation>{

        @Override
        public void success(UserInformation result) {
            PreferencesManager.setUserInformation(result);
            goToListings();
        }

        @Override
        public void error(String errorMessage) {
            mAuthTask = null;
            showProgress(false);

            mEmailView.setError(errorMessage);
            mEmailView.requestFocus();
        }

        @Override
        public void cancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

