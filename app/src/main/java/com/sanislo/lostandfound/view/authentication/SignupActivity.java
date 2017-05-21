package com.sanislo.lostandfound.view.authentication;

/**
 * Created by root on 04.09.16.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.interfaces.SignupView;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.presenter.SignupPresenter;
import com.sanislo.lostandfound.presenter.SignupPresenterImpl;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.things.ThingsActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends BaseActivity implements SignupView {
    private static final String TAG = SignupActivity.class.getSimpleName();

    @BindView(R.id.edt_email)
    EditText edtEmail;

    @BindView(R.id.edt_password)
    EditText edtPassword;

    @BindView(R.id.edt_first_name)
    EditText edtFirstName;

    @BindView(R.id.edt_last_name)
    EditText edtLastName;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;

    private FirebaseAuth mFirebaseAuth;
    private SignupPresenter mSignupPresenter;

    private String mEmail, mPassword;
    private String mFirstName;
    private String mLastName;
    private Uri mAvatarUri;
    private String mAvatarUrl;

    private MaterialDialog mSelectAvatarDialog;
    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        //setEditable(true);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSignupPresenter = new SignupPresenterImpl(this);
        initMaterialDialog();
        initSelectAvatarDialog();
        loadAvatar(null);
    }

    private void initMaterialDialog() {
        mMaterialDialog = new MaterialDialog.Builder(SignupActivity.this)
                .title(R.string.signup_progress_title)
                .content(R.string.signup_progress_content)
                .progress(true, 100)
                .cancelable(false)
                .build();
    }

    private void initSelectAvatarDialog() {
        mSelectAvatarDialog = new MaterialDialog.Builder(this)
                .title(R.string.select_avatar_image)
                .items(R.array.select_avatar_array)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                selectImageFromGallery();
                                break;
                            case 1:
                                startCropImageActivity();
                                break;
                        }
                    }
                })
                .build();
    }

    private static final int RQ_IMAGE_FROM_GALLERY = 777;
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        Intent сhooserIntent = Intent.createChooser(intent, "Select Image");
        startActivityForResult(сhooserIntent, RQ_IMAGE_FROM_GALLERY);
    }

    private void startCropImageActivity() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private void loadAvatar(Uri avatar) {
        Glide.with(this)
                .load(avatar)
                .error(R.drawable.account_signup)
                .into(ivAvatar);
    }

    @OnClick(R.id.sign_in_button)
    public void onClickSignIn() {
        finish();
    }

    @OnClick(R.id.sign_up_button)
    public void onClickSignUp() {
        //setEditable(false);
        mMaterialDialog.show();

        mFirstName = edtFirstName.getText().toString().trim();
        mLastName = edtLastName.getText().toString().trim();

        mEmail = edtEmail.getText().toString().trim();
        mPassword = edtPassword.getText().toString().trim();

        int validatedUserInput = FirebaseUtils.validateUserInput(mEmail, mPassword);
        int validatedUserName = FirebaseUtils.validateUserName(mFirstName, mLastName);
        if (validatedUserInput != -1) {
            makeToast(validatedUserInput);
            mMaterialDialog.dismiss();
        } else if (validatedUserName != -1) {
            makeToast(validatedUserName);
            mMaterialDialog.dismiss();
        } else {
            mFirebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(mOnCreateUserCompleteListener)
                    .addOnFailureListener(mCreateUserFailureListener);
        }
    }

    private OnFailureListener mCreateUserFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            e.printStackTrace();
            makeToast(e.getLocalizedMessage());
            mMaterialDialog.dismiss();
        }
    };

    private OnCompleteListener mOnCreateUserCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                if (mAvatarUri != null) {
                    uploadAvatar();
                } else {
                    saveUserData();
                }
            } else {
                mMaterialDialog.dismiss();
                Log.d(TAG, "onComplete: ERROR CREATING ACCOUNT");
                if (task.getException() != null) {
                    makeToast(task.getException().getMessage());
                }
            }
        }
    };

    private void uploadAvatar() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UploadTask uploadTask = FirebaseStorage.getInstance().getReference()
                .child(FirebaseConstants.USERS)
                .child(uid)
                .child(FirebaseConstants.AVATAR)
                .child("avatar")
                .putFile(mAvatarUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mAvatarUrl = taskSnapshot.getDownloadUrl().toString();
                saveUserData();
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                saveUserData();
            }
        });
    }

    private User mUser;
    private void saveUserData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUser = new User();
        mUser.setFirstName(mFirstName);
        mUser.setLastName(mLastName);
        mUser.setFullName(mFirstName + " " + mLastName);
        mUser.setEmailAddress(mEmail);
        mUser.setAvatarURL(mAvatarUrl);
        mUser.setUid(uid);
        mSignupPresenter.saveUser(mUser);
    }

    @Override
    public void onUserSaved() {
        Log.d(TAG, "onUserSaved: " + (FirebaseAuth.getInstance().getCurrentUser() == null));
        //at this point, the user should already be signed in!!!
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ApiModel apiModel = new ApiModelImpl();
        Call<List<User>> userCall = apiModel.getUserListByUID(uid);
        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<com.sanislo.lostandfound.model.User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    com.sanislo.lostandfound.model.User user = response.body().get(0);
                    updatePreferencesForLoggedInUser(user.getId(), uid);
                    launchMainActivity();
                }
            }

            @Override
            public void onFailure(Call<List<com.sanislo.lostandfound.model.User>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mAvatarUri = result.getUri();
                loadAvatar(mAvatarUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
                makeToast(error.getMessage());
            }
        } else if (requestCode == RQ_IMAGE_FROM_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                mAvatarUri = data.getData();
                loadAvatar(mAvatarUri);
                Log.d(TAG, "onActivityResult: " + mAvatarUri.toString());
            } else {
                makeToast("Failed to ");
            }
        }
    }

    private void updatePreferencesForLoggedInUser(int id, String uid) {
        PreferencesManager.setUserUID(SignupActivity.this, uid);
        PreferencesManager.setUserID(SignupActivity.this, id);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(SignupActivity.this, ThingsActivity.class);
        intent.putExtra(ThingsActivity.EXTRA_USER, mUser);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserSignedIn() {
        //TODO will implement authentication through rest withou firebase later
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mMaterialDialog.dismiss();
        //setEditable(true);
    }

    @OnClick(R.id.iv_avatar)
    public void onClickAvatar() {
        mSelectAvatarDialog.show();
    }
}