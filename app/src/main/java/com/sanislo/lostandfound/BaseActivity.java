package com.sanislo.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.sanislo.lostandfound.authentication.LoginActivity;
import com.sanislo.lostandfound.authentication.SignupActivity;
import com.sanislo.lostandfound.utils.Constants;

/**
 * BaseActivity class is used as a base class for all activities in the app
 * It implements GoogleApiClient callbacks to enable "Logout" in all activities
 * and defines variables that are being shared across all activities
 */
public abstract class BaseActivity extends MvpAppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = BaseActivity.class.getSimpleName();
    protected GoogleApiClient mGoogleApiClient;
    protected GoogleSignInOptions gso;
    protected FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;
    private String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUID = mAuth.getCurrentUser().getUid();
        }

        initGoogleApiClient();
        initAuthStateListener();
    }

    private void initAuthStateListener() {
        if (!((this instanceof LoginActivity) || (this instanceof SignupActivity))) {
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        takeUserToLoginScreenOnUnAuth();
                    } else {
                        mUID = firebaseAuth.getCurrentUser().getUid();
                    }
                }
            };
        }
    }

    private void initGoogleApiClient() {
        /* Setup the Google API object to allow Google logins */
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("568185669722-4o18jpcsav47fa23e8tten89h7j48ji5.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();

        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the
         * options specified by gso.
         */
        /* Setup the Google API object to allow Google+ logins */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!((this instanceof LoginActivity) || (this instanceof SignupActivity))) {
            mAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        if (!((this instanceof LoginActivity) || (this instanceof SignupActivity))) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void takeUserToLoginScreenOnUnAuth() {
        /* Move user to LoginActivity, and remove the backstack */
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void makeToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected String getAuthenticatedUserUID() {
        return mUID;
    }
}