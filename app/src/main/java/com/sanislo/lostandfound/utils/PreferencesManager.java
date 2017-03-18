package com.sanislo.lostandfound.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by root on 06.01.17.
 */

public abstract class PreferencesManager {
    public static final String TAG = PreferencesManager.class.getSimpleName();
    public static final String PREF_NAME = "PREFERENCES";
    public static final String KEY_PUSH_TOKEN = "KEY_PUSH_TOKEN";
    public static final String KEY_USER_ID = "KEY_USER_ID";
    public static final String KEY_USER_TOKEN = "KEY_USER_TOKEN";

    private static SharedPreferences mSharedPreferences;
    private static Context mContext = null;

    private static SharedPreferences.OnSharedPreferenceChangeListener changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "onSharedPreferenceChanged: " + key);
            Log.d(TAG, "onSharedPreferenceChanged: " + sharedPreferences.getAll().get(key));
        }
    };

    private static SharedPreferences getSharedPreferences(Context context) {
        if (mSharedPreferences != null) {
            return mSharedPreferences;
        }
        mContext = context.getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences(PREF_NAME, 0);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(changeListener);
        return mSharedPreferences;
    }

    public static String getPushToken(Context context) {
        mSharedPreferences = getSharedPreferences(context);
        return mSharedPreferences.getString(KEY_PUSH_TOKEN, "");
    }

    public static void setPushToken(Context context, String token) {
        mSharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_PUSH_TOKEN, token);
        editor.apply();
    }

    //============= SETTERS ============
    public static void setUserId(Context context, String userId) {
        if (context == null) return;
        mSharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public static void setUserToken(Context context, String userToken) {
        if (context == null) return;
        mSharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_USER_TOKEN, userToken);
        editor.apply();
    }

    //============= GETTERS ============
    public static String getUserId(Context context) {
        mSharedPreferences = getSharedPreferences(context);
        return mSharedPreferences.getString(KEY_USER_ID, "");
    }

    public static String getUserToken(Context context) {
        mSharedPreferences = getSharedPreferences(context);
        return mSharedPreferences.getString(KEY_USER_TOKEN, "");
    }
}