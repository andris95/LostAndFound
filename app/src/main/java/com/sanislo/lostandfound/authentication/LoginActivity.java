package com.sanislo.lostandfound.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.firebaseModel.User;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.utils.FirebaseUtils;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.things.ThingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edt_email)
    EditText edtEmail;

    @BindView(R.id.password)
    EditText edtPassword;

    public final String TAG = LoginActivity.class.getSimpleName();
    private final int RC_GOOGLE_SIGNIN = 22228;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private String mEmail, mPassword;
    private User mUser;

    private OnCompleteListener<AuthResult> onSignInCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                PreferencesManager.setUserUID(LoginActivity.this, mFirebaseAuth.getCurrentUser().getUid());
                Intent intent = new Intent(LoginActivity.this, ThingsActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    private OnFailureListener onSignInFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, ThingsActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initGoogleSignIn();
    }

    private void initGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("313315758776-olfcv9m81n4hv7gt3tm7n1l0oedfmodv.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();
    }

    @OnClick(R.id.btn_login)
    public void onClickLogin() {
        mEmail = edtEmail.getText().toString();
        mPassword = edtPassword.getText().toString();

        if (TextUtils.isEmpty(mEmail)) {
            Toast.makeText(getApplicationContext(), "Enter mEmail address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mPassword) || mPassword.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password is empty or not in correct format!", Toast.LENGTH_SHORT).show();
            return;
        }
        signInWithEmail();
    }

    private void signInWithEmail() {
        mFirebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(onSignInCompleteListener)
                .addOnFailureListener(onSignInFailureListener);
    }

    @OnClick(R.id.btn_signup)
    public void onClickSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_login_google)
    public void onClickLoginGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_GOOGLE_SIGNIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGNIN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handeGoogleSignInResult(googleSignInResult);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: canceled");
                Log.d(TAG, "onActivityResult: " +  Auth.GoogleSignInApi.getSignInResultFromIntent(data).getStatus());
                Log.d(TAG, "onActivityResult: " +  Auth.GoogleSignInApi.getSignInResultFromIntent(data).getStatus().toString());
            }
        }
    }

    private void handeGoogleSignInResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount resultSignInAccount = googleSignInResult.getSignInAccount();
            String displayName = resultSignInAccount.getDisplayName();
            String email = resultSignInAccount.getEmail();
            String id = resultSignInAccount.getId();
            String photoURL = resultSignInAccount.getPhotoUrl().toString();
            mUser = new User.Builder().setFullName(displayName)
                    .setFirstName(resultSignInAccount.getGivenName())
                    .setLastName(resultSignInAccount.getFamilyName())
                    .setAvatarURL(photoURL)
                    .setUid(id)
                    .setEmailAddress(email)
                    .build();
            firebaseAuthWithGoogle(resultSignInAccount);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        Log.d(TAG, "firebaseAuthWithGoogle: " + authCredential.toString());
        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                createOrUpdateUser();
            }
        });
    }

    private void createOrUpdateUser() {
        FirebaseUtils.getDatabase().getReference()
                .child(FirebaseConstants.USERS)
                .child(mFirebaseAuth.getCurrentUser().getUid())
                .updateChildren(mUser.toHashMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        launchMainActivity();
                    }
                });
    }

    private void launchMainActivity() {
        Intent intent = new Intent(LoginActivity.this, ThingsActivity.class);
        startActivity(intent);
        finish();
    }
}