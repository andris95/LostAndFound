package com.sanislo.lostandfound.presenter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sanislo.lostandfound.AddThingActivity;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.view.AddThingView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by root on 24.12.16.
 */

public class AddThingPresenterImpl implements AddThingPresenter {
    public static final String TAG = AddThingPresenterImpl.class.getSimpleName();

    private AddThingView mView;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Thing.Builder mThingBuilder;
    private String mThingKey;
    private Uri mThingPhotoUri;
    private int mCategory;

    private MaterialDialog mProgressDialog;



    public AddThingPresenterImpl(AddThingView addThingView) {
        mView = addThingView;
        mThingBuilder = new Thing.Builder();
        initFirebase();
        getCategories();
    }

    private void initFirebase() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseUtils.getDatabase();
        mDatabaseReference = firebaseDatabase.getReference();
        String storageBucket = "gs://lostandfound-326c3.appspot.com";
        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storageBucket);
    }

    private void getCategories() {
        mDatabaseReference.child(FirebaseConstants.CATEGORIES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        getCategories(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getCategories(DataSnapshot dataSnapshot) {
        List<String> categories = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String category = (String) snapshot.getValue();
            categories.add(category);
        }
        mView.onCategoriesReady(categories);
    }

    @Override
    public void onCategoryChanged(int category) {
        mCategory = category;
    }

    @Override
    public void addThing(String title, String description) {
        configureThing(title, description);
        setNewThingValue();
    }

    private void configureThing(String title, String description) {
        mThingKey = generateNewThingKey();
        long timestamp = new Date().getTime();
        mThingBuilder.setKey(mThingKey)
                .setUserUID(mFirebaseUser.getUid())
                .setTitle(title)
                .setDescription(description)
                .setCategory(mCategory)
                .setTimestamp(timestamp);
        if (mThingPhotoUri != null) {
            uploadThingPhoto();
        } else {
            setNewThingValue();
        }
    }

    private void uploadThingPhoto() {
        mStorageReference.child(FirebaseConstants.THINGS)
                .child(mThingKey)
                .child(FirebaseConstants.THING_PHOTO)
                .child(mThingKey)
                .putFile(mThingPhotoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: " + taskSnapshot.getStorage().getPath());
                        setPhotoPath(taskSnapshot.getStorage().getPath());
                        setNewThingValue();
                    }
                }).addOnProgressListener(mProgressListener)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: error uploading photo " + mThingPhotoUri.toString());
                        e.printStackTrace();
                    }
        });
    }

    private OnProgressListener<UploadTask.TaskSnapshot> mProgressListener = new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            long bytesTransferred = taskSnapshot.getBytesTransferred();
            long totalByteCount = taskSnapshot.getTotalByteCount();
            float progress = (float) bytesTransferred / totalByteCount;
            progress = progress * 100;
            mView.onProgress((int) progress);
            Log.d(TAG, "onProgress: " + progress);
        }
    };

    private void setPhotoPath(String path) {
        mThingBuilder.setPhoto(path);
    }

    private void setNewThingValue() {
        Thing thing = mThingBuilder.build();
        DatabaseReference newThingReference = mDatabaseReference
                .child(FirebaseConstants.THINGS)
                .child(thing.getKey());
        newThingReference.setValue(thing)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            mView.onThingAdded();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String generateNewThingKey() {
        return mDatabaseReference
                .child(FirebaseConstants.THINGS)
                .push()
                .getKey();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == AddThingActivity.PICK_THING_PHOTO) {
            if (data != null) {
                mThingPhotoUri = data.getData();
                Log.d(TAG, "onActivityResult: " + mThingPhotoUri.toString());
            }
        }
    }
}
