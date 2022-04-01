package deanmyers.project.dealerwerx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import deanmyers.project.dealerwerx.API.APIConsumer;
import deanmyers.project.dealerwerx.API.APIResponder;
import deanmyers.project.dealerwerx.API.UserInformation;
import deanmyers.project.dealerwerx.Adapters.HintArrayAdapter;

public class RegisterActivity extends AppCompatActivity {

    private APIConsumer.RegisterAsyncTask mAuthTask = null;

    // UI references.
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private CheckBox mAcceptTosView;
    private CheckBox mIsAgentView;
    private View mProgressView;
    private View mRegisterFormView;
    private Spinner mCountryCodeView;

    private String[] countryList = new String[]{
            "Canada",
            "United States",
            "Country - Choose One"
    };

    private String[] countryCodeList = new String[]{
            "CA",
            "US"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        mFirstNameView = (EditText)findViewById(R.id.first_name);
        mLastNameView = (EditText)findViewById(R.id.last_name);
        mEmailView = (EditText)findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordConfirmView = (EditText) findViewById(R.id.password_verify);
        mCountryCodeView = (Spinner)findViewById(R.id.country_code);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        ArrayAdapter<String> countryAdapter = new HintArrayAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                countryList
        );

        countryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCountryCodeView.setAdapter(countryAdapter);
        mCountryCodeView.setSelection(countryAdapter.getCount());

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        Button mGoBackButton = (Button) findViewById(R.id.go_back_button);
        mGoBackButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   finish();
               }
           }
        );

        mAcceptTosView = (CheckBox) findViewById(R.id.accept_tos);
        mIsAgentView = (CheckBox) findViewById(R.id.agent);
        //mAcceptTosView.setText(Html.fromHtml("I Accept the <a href=\"deanmeyers.com.dealerwerx.tos://ShowTos\">Terms of Service</a>"));
        mAcceptTosView.setMovementMethod(LinkMovementMethod.getInstance());

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptRegister() {
        hideSoftKeyboard();
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);
        mAcceptTosView.setError(null);

        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!mAcceptTosView.isChecked()){
            Toast.makeText(RegisterActivity.this, "You must accept our privacy policy", Toast.LENGTH_LONG).show();
            focusView = mAcceptTosView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if(!password.equals(passwordConfirm)){
            mPasswordConfirmView.setError("Passwords do not match");
            focusView = mPasswordConfirmView;
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

        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        if (mCountryCodeView.getSelectedItemPosition() == countryList.length - 1) {
            Toast.makeText(this, "You must select a country!", Toast.LENGTH_LONG).show();
            focusView = mCountryCodeView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = APIConsumer.Register(firstName, lastName, email, password, countryCodeList[mCountryCodeView.getSelectedItemPosition()], mIsAgentView.isChecked(), new RegisterActivity.RegisterResponder());
            mAuthTask.execute();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void hideSoftKeyboard(){
        if(getCurrentFocus()!=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    public class RegisterResponder extends APIResponder<UserInformation>{

        @Override
        public void success(UserInformation result) {
            if(mIsAgentView.isChecked()){
                Toast.makeText(RegisterActivity.this, "Thank you for registering. A Dealerwerx representitive will be in contact with you regarding your agent account activation.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(RegisterActivity.this, "User successfully registered!", Toast.LENGTH_LONG).show();
                PreferencesManager.setUserInformation(result);
            }
            finish();
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
