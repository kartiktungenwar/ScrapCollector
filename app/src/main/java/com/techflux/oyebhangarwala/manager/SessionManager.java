package com.techflux.oyebhangarwala.manager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.techflux.oyebhangarwala.activity.LoginActivity;
import com.techflux.oyebhangarwala.activity.MainActivity;

import java.util.HashMap;

public class SessionManager {

    // Shared Preferences
    SharedPreferences mPref;

    // Editor for Shared mPreferences
    Editor mEditor;

    // Context
    Context mContext;

    // Shared mPref mode
    int PRIVATE_MODE = 0;

    // SharedmPref file name
    private static final String PREF_NAME = "Oye_Bhangarwala_Login";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID = "id";

    // Constructor
    public SessionManager(Context context){
        this.mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String email,String name,String id){
        // Storing login value as TRUE
        mEditor.putBoolean(IS_LOGIN, true);

        // Storing name in mPref
        mEditor.putString(KEY_EMAIL, email);

        // Storing name in mPref
        mEditor.putString(KEY_NAME, name);

        // Storing name in mPref
        mEditor.putString(KEY_ID, id);

        // commit changes
        mEditor.commit();
    }

    /**
     * Check login method wil checked user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(mContext, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            mContext.startActivity(i);
        }
    }
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, mPref.getString(KEY_NAME, null));
        // user email id
        user.put(KEY_EMAIL, mPref.getString(KEY_EMAIL, null));
        // user id
        user.put(KEY_ID, mPref.getString(KEY_ID, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        mEditor.clear();
        mEditor.commit();
    }

    /**
     * Quick checked for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return mPref.getBoolean(IS_LOGIN, false);
    }
}
