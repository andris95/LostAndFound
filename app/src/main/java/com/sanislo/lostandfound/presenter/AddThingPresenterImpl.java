package com.sanislo.lostandfound.presenter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
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
import com.sanislo.lostandfound.interfaces.AddThingView;
import com.sanislo.lostandfound.model.Location;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.utils.Constants;
import com.sanislo.lostandfound.utils.FileUtils;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.addThing.AddThingActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private int mCategory;

    private Place mThingPlace;
    private Thing mThing;
    private Location mThingLocation;

    private Uri mCoverPhotoUri;
    private long mTotalBytesToTransfer = 0;
    private long mBytesTransferred = 0;
    private LinkedList<Uri> mDescriptionPhotoUris;
    private List<String> mDescriptionPhotoPaths = new ArrayList<>();

    private TransferUtility mTransferUtility;
    private ApiModel mApiModel = new ApiModelImpl();

    private void getUser() {
        String userUID = PreferencesManager.getUserUID(mContext);
        Log.d(TAG, "getUser: userUID: " + userUID);
        //Call<User> userCall = mApiModel.getUser(userUID);
        Call<List<User>> userCall = mApiModel.getUserListByUID(userUID);
        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    mUser = response.body().get(0);
                    Log.d(TAG, "onResponse: " + mUser);
                } else {
                    Log.d(TAG, "onResponse: error");
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }

    public AddThingPresenterImpl(AddThingActivity context) {
        mContext = context;
        mView = context;
        getUser();
        initFirebase();
        initAmazonTransferUtility();
        getCategories();
    }

    private void initAmazonTransferUtility() {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                mContext,
                "us-east-1:3e93fac9-410e-4c97-976c-ff404ab24b67", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        mTransferUtility = new TransferUtility(s3, mContext);
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

    }

    @Override
    public void onPause() {

    }

    @Override
    public void addThing(String title, String description) {
        configureThing(title, description);
        startThingDataUpload();
    }

    private long mTimetstamp;
    private void configureThing(String title, String description) {
        mThing = new Thing();
        mTimetstamp = new Date().getTime();

        if (mUser != null) {
            mThing.setUserUID(mUser.getUid());
            mThing.setUserName(mUser.getFullName());
            mThing.setUserAvatar(mUser.getAvatarURL());
            mThing.setTitle(title);
            mThing.setDescription(description);
            mThing.setTimestamp(mTimetstamp);
            mThing.setCategory(String.valueOf(mCategory));
        } else {
            throw new RuntimeException("User is not yet downloaded");
        }
    }

    private void startThingDataUpload() {
        if (mCoverPhotoUri != null) {
            uploadCoverPhoto();
        } else if (mDescriptionPhotoUris != null && !mDescriptionPhotoUris.isEmpty()) {
            uploadDescriptionPhotos();
        } else {
            postThing();
        }
    }

    private void uploadCoverPhoto() {
        UploadTask uploadCoverPhotoTask = mStorageReference
                .child(mUser.getUid())
                .child(FirebaseConstants.THINGS)
                .child(String.valueOf(mTimetstamp))
                .child(FirebaseConstants.THING_COVER_PHOTO)
                .child(FileUtils.getFileName(mContext, mCoverPhotoUri))
                .putFile(mCoverPhotoUri);
        uploadCoverPhotoTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String coverPhotoDownloadURL = taskSnapshot.getDownloadUrl().toString();
                Log.d(TAG, "onSuccess: coverPhotoDownloadURL: " + coverPhotoDownloadURL);
                mThing.setPhoto(coverPhotoDownloadURL);
                if (mDescriptionPhotoUris != null && !mDescriptionPhotoUris.isEmpty()) {
                    uploadDescriptionPhotos();
                } else {
                    postThing();
                }
            }
        }).addOnProgressListener(mProgressListener);
    }

    private void uploadDescriptionPhotos() {
        Uri uri = mDescriptionPhotoUris.remove();
        UploadTask descriptionPhotoUploadTask = mStorageReference
                .child(mUser.getUid())
                .child(FirebaseConstants.THINGS)
                .child(String.valueOf(mTimetstamp))
                .child(FirebaseConstants.THING_DESCRIPTION_PHOTOS)
                .child(FileUtils.getFileName(mContext, uri))
                .putFile(uri);
        descriptionPhotoUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String descriptionPhotoURL = taskSnapshot.getDownloadUrl().toString();
                Log.d(TAG, "onSuccess: descriptionPhotoURL: " + descriptionPhotoURL);
                mDescriptionPhotoPaths.add(descriptionPhotoURL);
                if (!mDescriptionPhotoUris.isEmpty()) {
                    uploadDescriptionPhotos();
                } else {
                    mThing.setDescriptionPhotos(mDescriptionPhotoPaths);
                    postThing();
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

    private void postThing() {
        setLocation();
        postThingData();
    }

    private void postThingData() {
        Call<Void> postThingCall = mApiModel.postThing(mThing);
        postThingCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mView.onThingAdded();
                } else {
                    Log.d(TAG, "onResponse: FAILED POSTING THING");
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setLocation() {
        if (mThingPlace != null) {
            LatLng latLng = mThingPlace.getLatLng();
            Location location = new Location();
            location.setLat(latLng.latitude);
            location.setLng(latLng.longitude);
            mThing.setLocation(location);
        }
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

    private void testAmazonS3Upload() {
        File file = new File(FileUtils.getPath(mContext, mCoverPhotoUri));
        Log.d(TAG, "uploadCoverPhoto: " + mCoverPhotoUri.getPath());
        Log.d(TAG, "uploadCoverPhoto: " + FileUtils.getPath(mContext, mCoverPhotoUri));
        Log.d(TAG, "uploadCoverPhoto: " + mCoverPhotoUri.toString());
        Log.d(TAG, "uploadCoverPhoto: " + (file == null));
        Log.d(TAG, "uploadCoverPhoto: " + file.isFile());
        Log.d(TAG, "uploadCoverPhoto: " + file.getPath());
        Log.d(TAG, "uploadCoverPhoto: " + file.getAbsolutePath());
        TransferObserver observer = mTransferUtility.upload(
                Constants.S3_BUCKET,     /* The bucket to upload to */
                FileUtils.getFileName(mContext, mCoverPhotoUri),    /* The key for the uploaded object */
                file   /* The file where the data to upload exists */
        );
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                switch (state) {
                    case COMPLETED:
                        if (mDescriptionPhotoUris != null && !mDescriptionPhotoUris.isEmpty()) {
                            uploadDescriptionPhotos();
                        } else {
                            postThing();
                        }
                        break;
                    case FAILED:
                        Log.d(TAG, "onStateChanged: FAILED TO UPLOAD TO AMAZON S3");
                        break;
                    default:
                        Log.d(TAG, "onStateChanged: " + state);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d(TAG, "onProgressChanged: " + bytesCurrent + " / " + bytesTotal);
            }

            @Override
            public void onError(int id, Exception ex) {

            }
        });
    }
}
