package com.sanislo.lostandfound.presenter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sanislo.lostandfound.view.addThing.AddThingActivity;
import com.sanislo.lostandfound.model.firebaseModel.Thing;
import com.sanislo.lostandfound.model.firebaseModel.ThingLocation;
import com.sanislo.lostandfound.model.firebaseModel.User;
import com.sanislo.lostandfound.utils.FileUtils;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.interfaces.AddThingView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by root on 24.12.16.
 */

public class AddThingPresenterImpl implements AddThingPresenter {
    public final String TAG = AddThingPresenterImpl.class.getSimpleName();
    private final int MAX_DESCRIPTION_PHOTO_NUMBER = 10;
    private final int PICK_THING_COVER_PHOTO = 111;
    private final int PICK_THING_DESCRIPTION_PHOTOS = 222;
    private final int PICK_THING_PLACE = 333;

    private Context mContext;
    private AddThingView mView;

    private User mUser;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Thing.Builder mThingBuilder;
    private String mThingKey;
    private int mCategory;

    private Place mThingPlace;
    private ThingLocation mThingLocation;

    private Uri mCoverPhotoUri;
    private long mTotalBytesToTransfer = 0;
    private long mBytesTransferred = 0;
    private LinkedList<Uri> mDescriptionPhotoUris;
    private List<String> mDescriptionPhotoPaths = new ArrayList<>();

    private ValueEventListener mUserListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mUser = dataSnapshot.getValue(User.class);
            Log.d(TAG, "onDataChange: " + mUser);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public AddThingPresenterImpl(AddThingActivity context) {
        mContext = context;
        mView = context;
        mThingBuilder = new Thing.Builder();
        initFirebase();
        getCategories();
    }

    private void initFirebase() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseUtils.getDatabase().getReference();
        mStorageReference = FirebaseUtils.getStorageRef();
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
    public void onResume() {
        mDatabaseReference.child(FirebaseConstants.USERS)
                .child(mFirebaseUser.getUid())
                .addValueEventListener(mUserListener);
    }

    @Override
    public void onPause() {
        mDatabaseReference.child(FirebaseConstants.USERS)
                .child(mFirebaseUser.getUid())
                .removeEventListener(mUserListener);
    }

    @Override
    public void addThing(String title, String description) {
        configureThing(title, description);
        startThingDataUpload();
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
    }

    private void startThingDataUpload() {
        if (mCoverPhotoUri != null) {
            uploadCoverPhoto();
        } else if (mDescriptionPhotoUris != null && !mDescriptionPhotoUris.isEmpty()) {
            uploadDescriptionPhotos();
        } else {
            setNewThingValue();
        }
    }

    private void uploadCoverPhoto() {
        UploadTask uploadCoverPhotoTask = mStorageReference.child(FirebaseConstants.THINGS)
                .child(mThingKey)
                .child(FirebaseConstants.THING_COVER_PHOTO)
                .child(FileUtils.getFileName(mContext, mCoverPhotoUri))
                .putFile(mCoverPhotoUri);
        uploadCoverPhotoTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                setPhotoPath(taskSnapshot.getStorage().getPath());
                if (mDescriptionPhotoUris != null && !mDescriptionPhotoUris.isEmpty()) {
                    uploadDescriptionPhotos();
                } else {
                    setNewThingValue();
                }
            }
        }).addOnProgressListener(mProgressListener);
    }

    private void uploadDescriptionPhotos() {
        Uri uri = mDescriptionPhotoUris.remove();
        UploadTask descriptionPhotoUploadTask = mStorageReference.child(FirebaseConstants.THINGS)
                .child(mThingKey)
                .child(FirebaseConstants.THING_DESCRIPTION_PHOTOS)
                .child(FileUtils.getFileName(mContext, uri))
                .putFile(uri);
        descriptionPhotoUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String path = taskSnapshot.getStorage().getPath();
                Log.d(TAG, "onSuccess: " + path);
                mDescriptionPhotoPaths.add(path);
                if (!mDescriptionPhotoUris.isEmpty()) {
                    uploadDescriptionPhotos();
                } else {
                    mThingBuilder.setDescriptionPhotos(mDescriptionPhotoPaths);
                    setNewThingValue();
                }
            }
        }).addOnProgressListener(mProgressListener);
    }

    private OnProgressListener<UploadTask.TaskSnapshot> mProgressListener = new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            mBytesTransferred += taskSnapshot.getBytesTransferred();
            float progress = (float) (mBytesTransferred / mTotalBytesToTransfer);
            mView.onProgress((int) progress);
        }
    };

    private void setPhotoPath(String path) {
        mThingBuilder.setPhoto(path);
    }

    private void setNewThingValue() {
        mThingBuilder.setUserName(mUser.getFullName());
        mThingBuilder.setUserAvatar(mUser.getAvatarURL());
        setLocation();
        Log.d(TAG, "setNewThingValue: " + mUser.getAvatarURL() + " / " + mUser.getFullName());
        Thing thing = mThingBuilder.build();
        DatabaseReference newThingReference = mDatabaseReference
                .child(FirebaseConstants.THINGS)
                .child(thing.getKey());
        newThingReference.setValue(thing)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mView.onThingAdded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setLocation() {
        if (mThingPlace != null) {
            LatLng latLng = mThingPlace.getLatLng();
            mThingBuilder.setLocation(latLng.latitude, latLng.longitude);
        }
    }

    private String generateNewThingKey() {
        return mDatabaseReference
                .child(FirebaseConstants.THINGS)
                .push()
                .getKey();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_THING_COVER_PHOTO) {
            if (data != null) {
                mCoverPhotoUri = data.getData();
                incrementTotalByteCount(mCoverPhotoUri);
            }
        }
        if (resultCode == RESULT_OK && requestCode == PICK_THING_DESCRIPTION_PHOTOS) {
            getDescriptionPhotoUris(data);
        }
        if (resultCode == RESULT_OK && requestCode == PICK_THING_PLACE) {
            mThingPlace = PlacePicker.getPlace(data, mContext);
            Log.d(TAG, "onActivityResult: " + mThingPlace);
        }
    }

    private void getDescriptionPhotoUris(Intent data) {
        mDescriptionPhotoUris = new LinkedList<>();
        ClipData clipData = data.getClipData();
        if (clipData == null) {
            Uri onlyUri = data.getData();
            mDescriptionPhotoUris.add(onlyUri);
            incrementTotalByteCount(onlyUri);
        } else {
            int photoCount = clipData.getItemCount();
            if (photoCount > MAX_DESCRIPTION_PHOTO_NUMBER) {
                Toast.makeText(mContext, "You can only select up to 10 photos...", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < photoCount; i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                mDescriptionPhotoUris.add(uri);
                incrementTotalByteCount(uri);
            }
        }
    }

    private void incrementTotalByteCount(Uri uri) {
        String path = FileUtils.getPath(mContext, uri);
        //if (TextUtils.isEmpty(path)) return;
        File file = new File(path);
        if (file.exists()) {
            mTotalBytesToTransfer += file.length();
            Log.d(TAG, "incrementTotalByteCount: " + mTotalBytesToTransfer);
        }
    }
}
