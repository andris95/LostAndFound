package com.sanislo.lostandfound.authentication;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanislo.lostandfound.BaseActivity;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.presenter.SignupPresenter;
import com.sanislo.lostandfound.presenter.SignupPresenterImpl;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.ThingsActivity;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.SignupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
                    .addOnCompleteListener(onCreateUserCompleteListener);
        }
    }

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
    public void onUserCreated() {
        mFirebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(onSignInCompleteListener);
    }

    private OnCompleteListener onSignInCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                PreferencesManager.setUserId(getApplicationContext(), firebaseUser.getUid());
                launchMainActivity();
            }
        }
    };

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