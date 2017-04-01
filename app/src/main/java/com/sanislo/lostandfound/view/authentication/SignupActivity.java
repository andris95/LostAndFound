package com.sanislo.lostandfound.view.authentication;

/**
 * Created by root on 04.09.16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.interfaces.SignupView;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.presenter.SignupPresenter;
import com.sanislo.lostandfound.presenter.SignupPresenterImpl;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.things.ThingsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends BaseActivity implements SignupView {

    @BindView(R.id.edt_email)
    EditText edtEmail;

    @BindView(R.id.edt_password)
    EditText edtPassword;

    @BindView(R.id.edt_first_name)
    EditText edtFirstName;

    @BindView(R.id.edt_last_name)
    EditText edtLastName;

    private FirebaseAuth mFirebaseAuth;
    private SignupPresenter mSignupPresenter;

    private String mEmail, mPassword;
    private String mFirstName;
    private String mLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        setEditable(true);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSignupPresenter = new SignupPresenterImpl(this);
    }

    @OnClick(R.id.sign_in_button)
    public void onClickSignIn() {
        finish();
    }

    @OnClick(R.id.sign_up_button)
    public void onClickSignUp() {
        setEditable(false);
        edtEmail.setInputType(InputType.TYPE_NULL);
        edtPassword.setInputType(InputType.TYPE_NULL);

        mFirstName = edtFirstName.getText().toString().trim();
        mLastName = edtLastName.getText().toString().trim();
        boolean isValidName = FirebaseUtils.isValidName(getApplicationContext(), mFirstName, mLastName);

        mEmail = edtEmail.getText().toString().trim();
        mPassword = edtPassword.getText().toString().trim();
        boolean isValidEmailPwrd = FirebaseUtils.validateEmailPwrd(getApplicationContext(), mEmail, mPassword);

        if (isValidName && isValidEmailPwrd) {
            mFirebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(onCreateUserCompleteListener)
                    .addOnFailureListener(mCreateUserFailureListener);
        }
    }

    private OnFailureListener mCreateUserFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            e.printStackTrace();
        }
    };

    private void setEditable(boolean editable) {
        if (!editable) {
            edtEmail.setInputType(InputType.TYPE_NULL);
            edtPassword.setInputType(InputType.TYPE_NULL);
        } else {
            edtEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    @OnClick(R.id.btn_reset_password)
    public void onClickResetPassword() {
        //startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
    }

    private OnCompleteListener onCreateUserCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (!task.isSuccessful()) {
                makeToast("Failed to create new user");
            } else {
                saveUserData();
            }
        }
    };

    private void saveUserData() {
        User user = new User();
        user.setFirstName(mFirstName);
        user.setLastName(mLastName);
        user.setFullName(mFirstName + " " + mLastName);
        user.setEmailAddress(mEmail);
        user.setUid(mFirebaseAuth.getCurrentUser().getUid());
        mSignupPresenter.saveUser(user);
    }

    @Override
    public void onUserSaved() {
        mFirebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(onSignInCompleteListener);
    }

    private OnCompleteListener onSignInCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                final String uid = firebaseUser.getUid();
                ApiModel apiModel = new ApiModelImpl();
                Call<List<User>> userCall = apiModel.getUserListByUID(uid);
                userCall.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<com.sanislo.lostandfound.model.User>> call, Response<List<User>> response) {
                        if (response.isSuccessful()) {
                            com.sanislo.lostandfound.model.User user = response.body().get(0);
                            updatePreferencesForLoggedInUser(user.getId(), uid);
                            launchMainActivity();
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<List<com.sanislo.lostandfound.model.User>> call, Throwable t) {

                    }
                });
                launchMainActivity();
            }
        }
    };

    private void updatePreferencesForLoggedInUser(int id, String uid) {
        PreferencesManager.setUserUID(SignupActivity.this, uid);
        PreferencesManager.setUserID(SignupActivity.this, id);
    }

    private void launchMainActivity() {
        startActivity(new Intent(SignupActivity.this, ThingsActivity.class));
        finish();
    }

    @Override
    public void onUserSignedIn() {
        //TODO will implement authentication through rest withou firebase later
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        setEditable(true);
    }
}