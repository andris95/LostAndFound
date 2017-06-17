package com.sanislo.lostandfound.presenter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.interfaces.AddThingView;
import com.sanislo.lostandfound.model.Category;
import com.sanislo.lostandfound.model.DescriptionPhotoItem;
import com.sanislo.lostandfound.model.Location;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.utils.FileUtils;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.addThing.AddThingActivity;
import com.sanislo.lostandfound.view.addThing.AddThingEditableActivity;

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

public class AddEditThingPresenterImpl implements AddEditThingPresenter {
    public final String TAG = AddEditThingPresenterImpl.class.getSimpleName();
    private final int MAX_DESCRIPTION_PHOTO_NUMBER = 10;
    private final int PICK_THING_COVER_PHOTO = 111;
    private final int PICK_THING_DESCRIPTION_PHOTOS = 222;
    private final int PICK_THING_PLACE = 333;

    private Context mContext;
    private AddThingView mView;

    private User mUser;
    private StorageReference mStorageReference;

    private Place mThingPlace;
    private Thing mThing;

    private Uri mCoverPhotoUri;
    private long mTotalBytesToTransfer = 0;
    private long mBytesTransferred = 0;
    private int mCurrentUploadCounter = 0;
    private LinkedList<Uri> mDescriptionPhotoUris;
    private List<String> mDescriptionPhotoPaths = new ArrayList<>();

    private ApiModel mApiModel = new ApiModelImpl();

    private void getUser() {
        String userUID = PreferencesManager.getUserUID(mContext);
        Log.d(TAG, "getUser: userUID: " + userUID);
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

    public AddEditThingPresenterImpl(AddThingActivity context) {
        mContext = context;
        mView = context;
        getUser();
        initFirebase();
        getCategories();
    }

    public AddEditThingPresenterImpl(AddThingEditableActivity context) {
        mContext = context;
        mView = context;
        getUser();
        initFirebase();
        getCategories();
    }

    private void initFirebase() {
        mStorageReference = FirebaseUtils.getStorageRef();
    }

    private List<String> mCategories;
    private void getCategories() {
        Call<List<Category>> call = mApiModel.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                mCategories = new ArrayList<String>();
                for (Category c : response.body()) {
                    mCategories.add(c.getName());
                }
                mView.onCategoriesReady(mCategories);
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

    private int mTypePosition = 0;
    @Override
    public void onTypeChanged(String[] types, int position) {
        mTypePosition = position;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void addThing(String title, String description) {
        if (TextUtils.isEmpty(title)) {
            mView.onError(R.string.error_empty_title);
        } else if (TextUtils.isEmpty(description)) {
            mView.onError(R.string.error_empty_description);
        } else if (!isTypeSelected()) {
            mView.onError(R.string.error_select_type);
        } else if (!isCategorySelected()) {
            mView.onError(R.string.error_select_category);
        } else {
            configureThing(title, description);
            startThingDataUpload();
        }
    }

    @Override
    public void updateThing(Thing thing, List<DescriptionPhotoItem> descriptionPhotoItemList) {
        mTypePosition = thing.getType();
        mThing = thing;
        if (TextUtils.isEmpty(mThing.getTitle())) {
            mView.onError(R.string.error_empty_title);
        } else if (TextUtils.isEmpty(mThing.getDescription())) {
            mView.onError(R.string.error_empty_description);
        } else if (!isTypeSelected()) {
            mView.onError(R.string.error_select_type);
        } else if (!isCategorySelected()) {
            mView.onError(R.string.error_select_category);

        } else {
            configureDescriptionUrisForUpdate(descriptionPhotoItemList);
            configureThingForUpdate();
            startThingDataUploadForUpdate();
        }
    }

    private void configureDescriptionUrisForUpdate(List<DescriptionPhotoItem> descriptionPhotoItemList) {
        mDescriptionPhotoUris = new LinkedList<>();
        for (DescriptionPhotoItem item : descriptionPhotoItemList) {
            mDescriptionPhotoUris.add(item.getUri());
        }
    }

    private void configureThingForUpdate() {
        mThing.setType(mTypePosition);
        Log.d(TAG, "configureThingForUpdate: " + mCategoryPosition);
        String category = mCategories.get(mCategoryPosition);
        mThing.setCategory(category);
    }

    private void startThingDataUploadForUpdate() {
        mView.onUploadStartedSimple();
        if (mCoverPhotoUri != null) {
            uploadCoverPhotoForUpdate();
        } else if (hasDescriptionPhotos()) {
            uploadDescriptionPhotosForUpdate();
        } else {
            //TODO final update
            updateThing(mThing);
        }
    }

    private void uploadCoverPhotoForUpdate() {
        UploadTask uploadCoverPhotoTask = mStorageReference
                .child(mUser.getUid())
                .child(FirebaseConstants.THINGS)
                .child(String.valueOf(mTimestamp))
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
                    uploadDescriptionPhotosForUpdate();
                } else {
                    //TODO final update
                    updateThing(mThing);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ONFAILURE");
                e.printStackTrace();
            }
        });
    }

    private void uploadDescriptionPhotosForUpdate() {
        Uri uri = mDescriptionPhotoUris.remove();
        UploadTask descriptionPhotoUploadTask = mStorageReference
                .child(mUser.getUid())
                .child(FirebaseConstants.THINGS)
                .child(String.valueOf(mTimestamp))
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
                    uploadDescriptionPhotosForUpdate();
                } else {
                    mThing.setDescriptionPhotos(mDescriptionPhotoPaths);
                    //TODO final update
                    updateThing(mThing);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ONFAILURE");
                e.printStackTrace();
            }
        });
    }

    private void updateThing(Thing thing) {
        Call<Void> call = mApiModel.updateThing(thing.getId(), thing);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                mView.onThingAdded();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private boolean isTypeSelected() {
        return mTypePosition > 0;
    }

    private boolean isCategorySelected() {
        return mCategoryPosition > 0;
    }

    @Override
    public void updateDescriptionPhotosList(List<DescriptionPhotoItem> descriptionPhotoItemList) {
        mDescriptionPhotoUris = new LinkedList<>();
        for (DescriptionPhotoItem item : descriptionPhotoItemList) {
            mDescriptionPhotoUris.add(item.getUri());
        }
    }

    @Override
    public void removeCoverPhoto() {
        mCoverPhotoUri = null;
    }

    private int mCategoryPosition = 0;
    @Override
    public void onCategoryChanged(int position) {
        mCategoryPosition = position;
    }

    private long mTimestamp;
    private void configureThing(String title, String description) {
        mThing = new Thing();
        mTimestamp = new Date().getTime();

        if (mUser != null) {
            mThing.setUserUID(mUser.getUid());
            mThing.setUserName(mUser.getFullName());
            mThing.setUserAvatar(mUser.getAvatarURL());
            mThing.setTitle(title);
            mThing.setDescription(description);
            mThing.setTimestamp(mTimestamp);
            String category = mCategories.get(mCategoryPosition);
            mThing.setCategory(category);
            mThing.setType(mTypePosition);
        }
    }

    private void startThingDataUpload() {
        notifyUploadStart();
        if (mCoverPhotoUri != null) {
            uploadCoverPhoto();
        } else if (hasDescriptionPhotos()) {
            uploadDescriptionPhotos();
        } else {
            postThing();
        }
    }

    private void notifyUploadStart() {
        if (mCoverPhotoUri != null || hasDescriptionPhotos()) {
            Log.d(TAG, "notifyUploadStart: getPhotoFileCountToUpload: " + getPhotoFileCountToUpload());
            mView.onUploadStartedWithPhotos(getPhotoFileCountToUpload());
        } else {
            mView.onUploadStartedSimple();
        }
    }

    private int getPhotoFileCountToUpload() {
        int count = (mDescriptionPhotoUris == null ? 0 : mDescriptionPhotoUris.size())
                + (mCoverPhotoUri == null ? 0 : 1);
        return count;
    }

    private boolean hasDescriptionPhotos() {
        return mDescriptionPhotoUris != null && !mDescriptionPhotoUris.isEmpty();
    }

    private void uploadCoverPhoto() {
        UploadTask uploadCoverPhotoTask = mStorageReference
                .child(mUser.getUid())
                .child(FirebaseConstants.THINGS)
                .child(String.valueOf(mTimestamp))
                .child(FirebaseConstants.THING_COVER_PHOTO)
                .child(FileUtils.getFileName(mContext, mCoverPhotoUri))
                .putFile(mCoverPhotoUri);
        uploadCoverPhotoTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String coverPhotoDownloadURL = taskSnapshot.getDownloadUrl().toString();
                Log.d(TAG, "onSuccess: coverPhotoDownloadURL: " + coverPhotoDownloadURL);
                mThing.setPhoto(coverPhotoDownloadURL);
                publishProgress();
                if (mDescriptionPhotoUris != null && !mDescriptionPhotoUris.isEmpty()) {
                    uploadDescriptionPhotos();
                } else {
                    postThing();
                }
            }
        });
    }

    private void uploadDescriptionPhotos() {
        Uri uri = mDescriptionPhotoUris.remove();
        UploadTask descriptionPhotoUploadTask = mStorageReference
                .child(mUser.getUid())
                .child(FirebaseConstants.THINGS)
                .child(String.valueOf(mTimestamp))
                .child(FirebaseConstants.THING_DESCRIPTION_PHOTOS)
                .child(FileUtils.getFileName(mContext, uri))
                .putFile(uri);
        descriptionPhotoUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String descriptionPhotoURL = taskSnapshot.getDownloadUrl().toString();
                Log.d(TAG, "onSuccess: descriptionPhotoURL: " + descriptionPhotoURL);
                mDescriptionPhotoPaths.add(descriptionPhotoURL);
                publishProgress();
                if (!mDescriptionPhotoUris.isEmpty()) {
                    uploadDescriptionPhotos();
                } else {
                    mThing.setDescriptionPhotos(mDescriptionPhotoPaths);
                    postThing();
                }
            }
        });
    }

    private void publishProgress() {
        mCurrentUploadCounter++;
        Log.d(TAG, "publishProgress: mCurrentUploadCounter: " + mCurrentUploadCounter);
        mView.onProgress(mCurrentUploadCounter);
    }

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
                    Log.d(TAG, "onResponse: mBytesTransferred: " + mBytesTransferred);
                    Log.d(TAG, "onResponse: mTotalBytesToTransfer: " + mTotalBytesToTransfer);
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
            location.setPlaceId(mThingPlace.getId());
            mThing.setLocation(location);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_THING_COVER_PHOTO) {
            if (data != null) {
                mCoverPhotoUri = data.getData();
                incrementTotalByteCount(mCoverPhotoUri);
                notifyCoverPhotoSelected();
            }
        }
        if (resultCode == RESULT_OK && requestCode == PICK_THING_DESCRIPTION_PHOTOS) {
            getDescriptionPhotoUris(data);
            notifyDescriptionPhotosSelected();
        }
        if (resultCode == RESULT_OK && requestCode == PICK_THING_PLACE) {
            mThingPlace = PlacePicker.getPlace(data, mContext);
            Log.d(TAG, "onActivityResult: " + mThingPlace);
            mView.onPlaceSelected(mThingPlace.getLatLng());
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

    private void notifyCoverPhotoSelected() {
        mView.onCoverPhotoSelected(mCoverPhotoUri);
    }

    private void notifyDescriptionPhotosSelected() {
        List<DescriptionPhotoItem> descriptionPhotoItemList = new ArrayList<>();
        for (Uri uri : mDescriptionPhotoUris) {
            descriptionPhotoItemList.add(new DescriptionPhotoItem(uri));
        }
        mView.onDescriptionPhotosSelected(descriptionPhotoItemList);
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
