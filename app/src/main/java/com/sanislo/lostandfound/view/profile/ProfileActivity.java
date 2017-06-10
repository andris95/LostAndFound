package com.sanislo.lostandfound.view.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 30.03.17.
 */

public class ProfileActivity extends BaseActivity implements ProfileContract.View {
    private String TAG = ProfileActivity.class.getSimpleName();
    private static final int RP_READ_EXTERNAL_FOR_COVER = 666;
    public static final int PICK_PROFILE_IMAGE = 777;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;

    @BindView(R.id.edt_last_name)
    EditText edtLastName;

    @BindView(R.id.edt_first_name)
    EditText edtFirstName;

    private ProfilePresenter mProfilePresenter;
    private MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        initProgressDialog();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProfilePresenter = new ProfilePresenter(this);
        String userUID = PreferencesManager.getUserUID(ProfileActivity.this);
        mProfilePresenter.loadProfile(userUID);
    }

    private void initProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.updating_profile_title)
                .content(R.string.updating_profile_content)
                .progress(true, 0)
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private User mUser;
    @Override
    public void onProfileLoaded(User user) {
        mUser = user;
        displayUserAvatar(user.getAvatarURL());
        edtFirstName.setText(user.getFirstName());
        edtLastName.setText(user.getLastName());
    }

    @Override
    public void onProfileUpdated() {
        mProgressDialog.dismiss();
        makeToast("onProfileUpdated");
    }

    @Override
    public void onError() {
        makeToast("onError");
    }

    private void displayUserAvatar(String url) {
        Glide.with(this)
                .load(url)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .placeholder(R.drawable.account)
                .error(R.drawable.account)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivAvatar);
    }

    private void displayUserAvatar(Uri avatarUri) {
        Glide.with(this)
                .load(avatarUri)
                .asBitmap()
                .placeholder(R.drawable.account)
                .error(R.drawable.account)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Uri, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ivAvatar.setImageBitmap(resource);
                        return false;
                    }
                })
                .into(ivAvatar);
    }

    @Override
    public void setPresenter(ProfileContract.Presenter presenter) {

    }

    @OnClick(R.id.iv_avatar)
    public void onClickAvatar() {
        checkGalleryPermission();
    }

    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            pickProfileImageFromGallery();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    RP_READ_EXTERNAL_FOR_COVER);
        }
    }

    private void pickProfileImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        Intent сhooserIntent = Intent.createChooser(intent, getString(R.string.select_avatar_image));
        startActivityForResult(сhooserIntent, PICK_PROFILE_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RP_READ_EXTERNAL_FOR_COVER) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickProfileImageFromGallery();
            }
        }
    }

    private Uri mProfileImageUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ProfileActivity.PICK_PROFILE_IMAGE) {
            if (data != null) {
                mProfileImageUri = data.getData();
                displayUserAvatar(mProfileImageUri);
            }
        }
    }

    @OnClick(R.id.fab_update_user)
    public void onClickUpdateUser() {
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        int validatedUserName = FirebaseUtils.validateUserName(firstName, lastName);
        if (validatedUserName != -1) {
            makeToast(validatedUserName);
        } else {
            mProgressDialog.show();
            mUser.setFirstName(firstName);
            mUser.setLastName(lastName);
            mUser.setFullName(firstName + " " + lastName);
            if (mProfileImageUri != null) {
                uploadUserAvatar();
            } else {
                mProfilePresenter.updateProfile(mUser);
            }
        }
    }

    private void uploadUserAvatar() {
        StorageReference storageReference = FirebaseUtils.getStorageRef();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UploadTask uploadTask = storageReference
                .child(FirebaseConstants.USERS)
                .child(uid)
                .child(FirebaseConstants.AVATAR)
                .child("avatar")
                .putFile(mProfileImageUri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isComplete() && task.isSuccessful()) {
                    String avatarURL = task.getResult().getDownloadUrl().toString();
                    mUser.setAvatarURL(avatarURL);
                } else {
                    onError();
                    mProgressDialog.dismiss();
                }
                mProfilePresenter.updateProfile(mUser);
            }
        });
    }
}
