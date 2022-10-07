package com.techflux.oyebhangarwala.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import com.google.android.gms.common.api.Status;
import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.dataModel.UserBean;
import com.techflux.oyebhangarwala.manager.SessionManager;
import com.techflux.oyebhangarwala.validation.MyValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener  {


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    SessionManager mSessionManager;
    UserBean mUserBean;
    String userName,userId;
    SharedPreferences pref;
    SharedPreferences.Editor ed;

    ArrayList<UserBean> arraylistBean=new ArrayList<UserBean>();
    private ArrayAdapter<String> adapter;

    private AutoCompleteTextView et_Name;
    private EditText et_Password,et_EmailForgotPassword;
    private Button btn_Login,btnSignOut;
    private SignInButton btnSignIn;
    private TextView tv_New_User,tv_Forget_Password;
    private CheckBox remember_me;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mUserBean = new UserBean();
        mSessionManager = new SessionManager(getApplicationContext());
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        et_Name = (AutoCompleteTextView) findViewById(R.id.et_login_username);
        et_Password = (EditText) findViewById(R.id.et_login_password);
        tv_Forget_Password = (TextView) findViewById(R.id.tv_forgot_password);
        btn_Login = (Button) findViewById(R.id.btn_login);
        tv_New_User = (TextView) findViewById(R.id.tv_user);
        remember_me = (CheckBox) findViewById(R.id.checkBox_remember);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

        Loadcredentials();

        tv_New_User.setOnClickListener(this);
        btn_Login.setOnClickListener(this);
        tv_Forget_Password.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                boolean flag = true;
                if (!MyValidator.isValidRequired(et_Name)) {
                    flag = false;

                }
                if (!MyValidator.isValidPassword(et_Password)) {
                    flag = false;
                }
                else {
                    if (flag) {
                        if(remember_me.isChecked()) {
                            String username = et_Name.getText().toString();
                            String password = et_Password.getText().toString();

                            pref = getSharedPreferences("HB", MODE_PRIVATE);
                            ed = pref.edit();
                            ed.putString(username, password);
                            ed.commit();
                        }
                        loginUser();
                    }
                }
                break;

            case R.id.tv_user:
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
                break;

            case R.id.tv_forgot_password:
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.forgot_password);
                dialog.setCancelable(false);

                ImageView closewindow = (ImageView) dialog.findViewById(R.id.fp_windowclose);
                closewindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                et_EmailForgotPassword = (EditText) dialog.findViewById(R.id.et_FP_Email);;

                Button btn_send = (Button) dialog.findViewById(R.id.fp_buttonSubmit);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean flag = true;
                        if (!MyValidator.isValidEmail(et_EmailForgotPassword)) {
                            flag = false;
                        }
                        if (flag) {
                            loginForgotPassword();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                break;

            case R.id.btn_sign_in:
                signIn();
                break;

            case R.id.btn_sign_out:
                signOut();
                Intent i = getIntent();
                finish();
                startActivity(i);
                break;
        }
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Exit...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want exit?");


        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                // Write your code here to invoke YES event
                dialog.dismiss();
                finish();
                try {
                    ((MainActivity)getApplicationContext()).finish();
                }catch (Exception e) {

                }
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void loginData(String subUrl){
        Log.d("url : ", "" + subUrl);
        pd.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(subUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("response : ", "" + jsonArray);
                displayData(jsonArray);
                pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void displayData(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                userName = jsonObject.getString(Config.KEY_USERNAME);
                userId = jsonObject.getString(Config.KEY_USERIMAGE);
                Log.d("url",userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mSessionManager.createLoginSession(et_Name.getText().toString(),userName,userId);
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }


    private void loginUser(){
        pd.show();
        final String username = et_Name.getText().toString().trim();
        final String password = et_Password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            loginData(Config.LOGINUSER_URL+username);
                            pd.dismiss();
                        }
                        else {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Invalid UserName or Password",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USEREMAIL,username);
                params.put(Config.KEY_PASSWORD,password);
                Log.d("url", String.valueOf(params));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loginForgotPassword(){
        final String username = et_EmailForgotPassword.getText().toString().trim();
        Toast.makeText(getApplicationContext(),username,Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FORGOT_PASSWORD_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            if(username.endsWith("gmail.com")){
                                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                                startActivity(intent);
                                finish();}
                            else {
                                Toast.makeText(LoginActivity.this," Check Your Email ",Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this,response.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USEREMAIL,username);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void Loadcredentials()
    {

        pref = this.getSharedPreferences("HB", MODE_PRIVATE);
        ed = pref.edit();
        Map map = pref.getAll();

        Set keySet = map.keySet();
        Iterator iterator = keySet.iterator();

        while (iterator.hasNext())
        {
            UserBean bean=new UserBean();
            String key = (String) iterator.next();
            String value = (String)map.get(key);
            Log.i("HashMap","Username==="+ key +" Psw=== " +value);

            bean.setUsername(key);
            bean.setPassword(value);

            arraylistBean.add(bean);
        }
        for (int i = 0; i < arraylistBean.size(); i++)
        {
            UserBean bean=arraylistBean.get(i);
            Log.e("ArrayList","Username=="+ bean.getUsername() +" Psw== " +bean.getPassword());
        }

        List countryList = getCountryList(arraylistBean);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,countryList);
        et_Name.setAdapter(adapter);
        et_Name.setThreshold(1);
        et_Name.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3)
            {
                Toast.makeText(getBaseContext(), ""+view.getItemAtPosition(position),Toast.LENGTH_LONG).show();
                et_Password.setText(arraylistBean.get(position).getPassword());
            }
        });
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getCountryList(ArrayList<UserBean> countries)
    {
        List list = new ArrayList();
        for (UserBean c : countries)
        {
            list.add(c.getUsername());
        }
        return list;
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
            String personId = String.valueOf(acct.getPhotoUrl());
            String email = acct.getEmail();

            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personId);
            mSessionManager.createLoginSession(email,personName,personId);
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
            btnSignOut.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
        }
    }
}
