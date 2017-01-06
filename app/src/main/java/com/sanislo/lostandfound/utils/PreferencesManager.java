package com.sanislo.lostandfound.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by root on 06.01.17.
 */

public class PreferencesManager {
    public static final String TAG = PreferencesManager.class.getSimpleName();
    public static final String PREF_NAME = "PREFERENCES";
    public static String KEY_PUSH_TOKEN = "KEY_PUSH_TOKEN";

    private static SharedPreferences mSharedPreferences;
    private static Context mContext = null;

    private static SharedPreferences.OnSharedPreferenceChangeListener changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "onSharedPreferenceChanged: " + key);
            Log.d(TAG, "onSharedPreferenceChanged: " + sharedPreferences.getAll().get(key));
        }
    };

    private static SharedPreferences getPref(Context context) {
        if (mSharedPreferences != null) {
            return mSharedPreferences;
        }
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(PREF_NAME, 0);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(changeListener);
        return mSharedPreferences;
    }


    public static String getPushToken(Context context) {
        mSharedPreferences = getPref(context);
        return mSharedPreferences.getString(KEY_PUSH_TOKEN, "");
    }

    public static void setPushToken(Context context, String token) {
        mSharedPreferences = getPref(context);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_PUSH_TOKEN, token);
        editor.commit();
    }
}