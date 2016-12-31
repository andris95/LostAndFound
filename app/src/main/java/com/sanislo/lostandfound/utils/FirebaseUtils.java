package com.sanislo.lostandfound.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 24.12.16.
 */

public class FirebaseUtils {
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

    public static boolean validateEmailPwrd(Context context, String email, String password) {
        if (!isValidEmail(email)) {
            Toast.makeText(context, "Incorrect email address!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(context, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidName(Context context, String firstName, String lastName) {
        String regx = "^[\\p{L}\\s.’\\-,]+$";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(firstName);
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(context, "First Name can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!matcher.find()) {
            Toast.makeText(context, "Invalid First Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(context, "Last Name can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        matcher = pattern.matcher(lastName);
        if (!matcher.find()) {
            Toast.makeText(context, "Invalid Last Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
