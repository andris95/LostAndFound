package com.sanislo.lostandfound.view.authentication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
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
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.model.firebaseModel.FirebaseUser;
import com.sanislo.lostandfound.utils.FirebaseConstants;
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

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edt_email)
    EditText edtEmail;

    @BindView(R.id.password)
    EditText edtPassword;

    @BindView(R.id.tv_logo)
    TextView tvLogo;

    public final String TAG = LoginActivity.class.getSimpleName();
    private final int RC_GOOGLE_SIGNIN = 22228;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private String mEmail, mPassword;
    private FirebaseUser mUser;

    private OnCompleteListener<AuthResult> onSignInCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                getUserByUid();
            }
        }
    };

    private void getUserByUid() {
        final String uid = mFirebaseAuth.getCurrentUser().getUid();
        ApiModel apiModel = new ApiModelImpl();
        Call<List<com.sanislo.lostandfound.model.User>> userCall = apiModel.getUserListByUID(uid);
        userCall.enqueue(new Callback<List<com.sanislo.lostandfound.model.User>>() {
            @Override
            public void onResponse(Call<List<com.sanislo.lostandfound.model.User>> call, Response<List<com.sanislo.lostandfound.model.User>> response) {
                if (response.isSuccessful()) {
                    User user = response.body().get(0);
                    updatePreferencesForLoggedInUser(user.getId(), uid);
                    launchMainActivity(user);
                } else {
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<com.sanislo.lostandfound.model.User>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updatePreferencesForLoggedInUser(int id, String uid) {
        PreferencesManager.setUserUID(LoginActivity.this, uid);
        PreferencesManager.setUserID(LoginActivity.this, id);
    }

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
        setupLogoFont();
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
                makeToast("Could not sign in using Google+");
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
            mUser = new FirebaseUser.Builder().setFullName(displayName)
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
                saveOrUpdateUser();
            }
        });
    }

    private void saveOrUpdateUser() {
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void launchMainActivity(User user) {
        Intent intent = new Intent(LoginActivity.this, ThingsActivity.class);
        intent.putExtra(ThingsActivity.EXTRA_USER, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}