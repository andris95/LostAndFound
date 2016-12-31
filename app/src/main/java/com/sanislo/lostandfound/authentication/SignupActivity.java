package com.sanislo.lostandfound.authentication;

/**
 * Created by root on 04.09.16.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanislo.lostandfound.BaseActivity;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.ThingsActivity;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends BaseActivity {

    @BindView(R.id.edt_email)
    EditText edtEmail;

    @BindView(R.id.edt_password)
    EditText edtPassword;

    @BindView(R.id.edt_first_name)
    EditText edtFirstName;

    @BindView(R.id.edt_last_name)
    EditText edtLastName;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private String mEmail, mPassword;
    private String mFirstName;
    private String mLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mDatabase = FirebaseUtils.getDatabase().getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = mDatabase.getReference();
    }

    @OnClick(R.id.sign_in_button)
    public void onClickSignIn() {
        finish();
    }

    @OnClick(R.id.sign_up_button)
    public void onClickSignUp() {
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

    @OnClick(R.id.btn_reset_password)
    public void onClickResetPassword() {
        //startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
    }

    private OnCompleteListener onCreateUserCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (!task.isSuccessful()) {
                makeToast("Authentication failed...");
            } else {
                mFirebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                        .addOnCompleteListener(onSignInCompleteListener);
            }
        }
    };

    private OnCompleteListener onSignInCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                createNewUser(firebaseUser);
            }
        }
    };

    private void createNewUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setUid(firebaseUser.getUid());
        user.setFirstName(mFirstName);
        user.setLastName(mLastName);
        user.setFullName(mFirstName + " " + mLastName);
        mDatabaseReference.child(FirebaseConstants.USERS).child(firebaseUser.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        launchMainActivity();
                    }
                });
    }

    private void launchMainActivity() {
        startActivity(new Intent(SignupActivity.this, ThingsActivity.class));
        finish();
    }
}