package com.sanislo.lostandfound.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.R;

/**
 * Created by root on 24.12.16.
 */

public class FirebaseUtils {
    public static final String TAG = FirebaseUtils.class.getSimpleName();
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    public static StorageReference getStorageRef() {
        String bucket = "gs://lostandfound-326c3.appspot.com";
        StorageReference storageRef = FirebaseStorage
                .getInstance()
                .getReferenceFromUrl(bucket);
        return storageRef;
    }

    public static final int validateUserInput(String email, String password) {
        if (!isValidEmail(email)) {
            return R.string.invalid_email;
        }
        if (!isValidPassword(password)) {
            return R.string.invalid_password;
        }
        return -1;
    }

    public static boolean isValidEmail(CharSequence email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        Log.d(TAG, "isValidPassword: matches?: " + password.matches(passwordRegex));
        return password.matches(passwordRegex);
    }

    public static int validateUserName(String firstName, String lastName) {
        if (!isValidName(firstName)) {
            return R.string.invalid_first_name;
        }
        if (!isValidName(lastName)) {
            return R.string.invalid_last_name;
        }
        return -1;
    }

    public static boolean isValidName(String name) {
        String regx = "^[\\p{L}\\s.’\\-,]+$";
        Log.d(TAG, "isValidName: " + name.matches(regx));
        return name.matches(regx);
    }
}
