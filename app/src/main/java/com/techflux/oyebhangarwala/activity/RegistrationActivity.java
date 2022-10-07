package com.techflux.oyebhangarwala.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.manager.SessionManager;
import com.techflux.oyebhangarwala.validation.MyValidator;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    SessionManager sessionManager;
    private EditText et_Name,et_Email,et_Password,et_Confirm_Password;
    private Button btn_register;
    private CheckBox term;
    private ProgressDialog pd;
    TextView login;
    private SignInButton btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sessionManager = new SessionManager(getApplicationContext());
        btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        et_Name = (EditText) findViewById(R.id.et_register_username);
        et_Email = (EditText) findViewById(R.id.et_register_email);
        et_Password = (EditText) findViewById(R.id.et_register_password);
        et_Confirm_Password = (EditText) findViewById(R.id.et_register_confirm_password);
        login = (TextView) findViewById(R.id.tv_new_user);
        btn_register = (Button) findViewById(R.id.btn_register);
        term = (CheckBox) findViewById(R.id.checkBox_term);
        btn_register.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        pd = new  ProgressDialog(this);
        pd.setMessage("Loading...");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.tv_new_user:
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
                break;

            case R.id.btn_register:
                boolean flag = true;
                if(!(term.isChecked())){
                    flag = false;
                    Toast.makeText(getApplicationContext(),"Please Select Check box to Accept our Terms & Condition...",Toast.LENGTH_SHORT).show();
                }
                if (!MyValidator.isValidRequired(et_Name)) {
                    flag = false;
                }
                if(!MyValidator.isValidEmail(et_Email)){
                    flag = false;
                }
                if (!MyValidator.isValidRequired(et_Password)) {
                    flag = false;
                }
                if (!MyValidator.isValidRequired(et_Confirm_Password)) {
                    flag = false;
                }
                if (et_Password.equals(et_Confirm_Password)) {
                    flag = false;
                    Toast.makeText(getApplicationContext(),"Password and Confirm Password Does Not Match",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (flag) {
                        registerUser();
                    }
                }
            break;
        }
    }

    private void registerUser(){
        final String username = et_Name.getText().toString().trim();
        final String useremail = et_Email.getText().toString().trim();
        final String password = et_Password.getText().toString().trim();
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if(response.trim().equals("success")){
                            if(username.endsWith("gmail.com")){
                            Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                            startActivity(intent);
                            finish();}
                            else {
                                Toast.makeText(RegistrationActivity.this," Check Your Email ",Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(RegistrationActivity.this,response.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Toast.makeText(RegistrationActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USERNAME,username);
                params.put(Config.KEY_USEREMAIL,useremail);
                params.put(Config.KEY_PASSWORD,password);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            if(result.isSuccess()){
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personId = acct.getId();
            String email = acct.getEmail();

            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personId);

            sessionManager.createLoginSession(email,personName,personId);

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setMessage("Loading");
            pd.setIndeterminate(true);
        }

        pd.show();
    }

    private void hideProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.hide();
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }
}
