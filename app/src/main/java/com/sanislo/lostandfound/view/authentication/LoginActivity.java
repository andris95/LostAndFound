package com.sanislo.lostandfound.view.authentication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.things.ThingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoginView {

    @BindView(R.id.edt_email)
    EditText edtEmail;

    @BindView(R.id.password)
    EditText edtPassword;

    @BindView(R.id.tv_logo)
    TextView tvLogo;

    public final String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private String mEmail, mPassword;
    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finishIfAuthenticated();

        mLoginPresenter = new LoginPresenterImpl(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initGoogleSignIn();
        setupLogoFont();
    }

    private void finishIfAuthenticated() {
        if (mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, ThingsActivity.class));
            finish();
        }
    }

    private void initGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("313315758776-olfcv9m81n4hv7gt3tm7n1l0oedfmodv.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();
    }

    private void setupLogoFont() {
        Typeface logoTypeface = Typeface.createFromAsset(getAssets(), "surfing_kiteboarding.ttf");
        tvLogo.setTypeface(logoTypeface);
        tvLogo.setTextSize(72);
    }

    @OnClick(R.id.btn_login)
    public void onClickLogin() {
        mEmail = edtEmail.getText().toString();
        mPassword = edtPassword.getText().toString();
        mLoginPresenter.login(mEmail, mPassword);
    }

    @OnClick(R.id.btn_signup)
    public void onClickSignUp() {
        launchSignupActivity();
    }

    private void launchSignupActivity() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_login_google)
    public void onClickLoginGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, LoginPresenter.RC_GOOGLE_SIGNIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginPresenter.onActivityResult(requestCode, resultCode, data);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(LoginActivity.this, ThingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updatePreferencesForLoggedInUser(int id, String uid) {
        PreferencesManager.setUserUID(LoginActivity.this, uid);
        PreferencesManager.setUserID(LoginActivity.this, id);
    }


    @Override
    public void onUserSignedIn(User user) {
        updatePreferencesForLoggedInUser(user.getId(), user.getUid());
        launchMainActivity();
    }

    @Override
    public void onError(String message) {
        makeToast(message);
    }

    @Override
    public void onError(int errorMessageResId) {
        makeToast(errorMessageResId);
    }

    @OnClick(R.id.btn_reset_password)
    public void onClickResetPassword() {
        String email = edtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            onError(R.string.error_email_cant_be_blank);
        } else if (isValidEmail(email)) {
            sendPasswordResetEmail(email);
        } else {
            onError(R.string.invalid_email);
        }
    }

    private void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            makeToast(R.string.check_your_mail);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: onClickResetPassword");
                        if (e != null) {
                            e.printStackTrace();
                            makeToast(e.getLocalizedMessage());
                        }
                    }
                });
    }

    private boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}