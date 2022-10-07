package com.techflux.oyebhangarwala.activity.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.activity.LoginActivity;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.fragment.About_Us;
import com.techflux.oyebhangarwala.fragment.AddressBook;
import com.techflux.oyebhangarwala.fragment.Comments;
import com.techflux.oyebhangarwala.fragment.Home;
import com.techflux.oyebhangarwala.fragment.Order_History;
import com.techflux.oyebhangarwala.fragment.Sample;
import com.techflux.oyebhangarwala.fragment.Terms_Conditions;
import com.techflux.oyebhangarwala.manager.SessionManager;
import com.techflux.oyebhangarwala.util.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RESULT_TAKE = -1;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    HashMap<String, String> user;
    private ProgressDialog pd;
    SessionManager mSessionManager;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private de.hdodenhof.circleimageview.CircleImageView circleImageView;
    private static final String TAG = "Firebase";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creating a firebase object
        Firebase firebase = new Firebase(com.techflux.oyebhangarwala.app.Config.FIREBASE_APP);

        //Pushing a new element to firebase it will automatically create a unique id
        Firebase newFirebase = firebase.push();

        String refreshedToken = newFirebase.getKey();

        Log.d("Tag", refreshedToken);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(com.techflux.oyebhangarwala.app.Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(com.techflux.oyebhangarwala.app.Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(com.techflux.oyebhangarwala.app.Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

        displayFirebaseRegId();


        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        fragmentManager = getSupportFragmentManager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //add this line to display menu1 when the activity is loaded
        displaySelectedScreen(R.id.nav_home);

            mSessionManager = new SessionManager(getApplicationContext());
            mSessionManager.checkLogin();
            View hView = navigationView.getHeaderView(0);
            mSessionManager = new SessionManager(getApplicationContext());
            user = mSessionManager.getUserDetails();
                TextView nav_userEmail = (TextView) hView.findViewById(R.id.textView_Email);
                nav_userEmail.setText(user.get(mSessionManager.KEY_EMAIL));
                TextView nav_userName = (TextView) hView.findViewById(R.id.textView_Name);
                nav_userName.setText(user.get(mSessionManager.KEY_NAME));
                circleImageView = (de.hdodenhof.circleimageview.CircleImageView) hView.findViewById(R.id.imageView_Profile);
                Glide.with(getApplicationContext())
                .load(user.get(mSessionManager.KEY_ID))
                .centerCrop()
                .error(R.mipmap.ic_launcher).into(circleImageView);
                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_TAKE && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                circleImageView.setImageBitmap(bitmap);
                updateRegistration();
                loginData(Config.LOGINUSER_URL+user.get(mSessionManager.KEY_EMAIL));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(getFragmentManager().getBackStackEntryCount() == 1) {
            moveTaskToBack(false);
        }
        else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home)
        {
            //replacing the fragment
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new Home());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void displaySelectedScreen(int itemId) {



        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new Home());
                fragmentTransaction.commit();
                break;
            case R.id.nav_contact_us:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new Sample());
                fragmentTransaction.commit();
                break;
            case R.id.nav_address:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new AddressBook());
                fragmentTransaction.commit();
                break;
            case R.id.nav_order_history:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new Order_History());
                fragmentTransaction.commit();
                break;

            case R.id.nav_comments:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new Comments());
                fragmentTransaction.commit();
                break;

            case  R.id.nav_about_us:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new About_Us());
                fragmentTransaction.commit();
                break;

            case R.id.nav_terms:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new Terms_Conditions());
                fragmentTransaction.commit();
                break;

            case R.id.nav_logout:
                mSessionManager.logoutUser();
                // After logout redirect user to Loing Activity
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Staring Login Activity
                startActivity(i);
                break;

            case R.id.nav_exit:
                finish();
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        return true;
    }

    private void updateRegistration(){
      final String userEmail = user.get(mSessionManager.KEY_EMAIL);
      final String userImage = getStringImage(bitmap);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.User_EDIT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USEREMAIL,userEmail);
                params.put(Config.KEY_USERIMAGE,userImage);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

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
        String userEmail=null,userName = null,userId = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                userEmail = jsonObject.getString(Config.KEY_USEREMAIL);
                userName = jsonObject.getString(Config.KEY_USERNAME);
                userId = jsonObject.getString(Config.KEY_USERIMAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mSessionManager.logoutUser();
        mSessionManager.createLoginSession(userEmail,userName,userId);
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(com.techflux.oyebhangarwala.app.Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.e(TAG, "Firebase Reg Id: " + regId);
        else
            Log.e(TAG, "Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(com.techflux.oyebhangarwala.app.Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(com.techflux.oyebhangarwala.app.Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, fragment.toString());
        ft.commit();
    }

}