package com.sanislo.lostandfound.view.authentication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 26.05.17.
 */

public class LoginPresenterImpl implements LoginPresenter {
    public static final String TAG = LoginPresenter.class.getSimpleName();
    //private final int RC_GOOGLE_SIGNIN = 22228;

    private LoginView mView;
    private FirebaseAuth mFirebaseAuth;
    private String mEmail;
    private String mPassword;
    private FirebaseUser mFirebaseUser;

    public LoginPresenterImpl(LoginView view) {
        mView = view;
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void login(String email, String password) {
        mEmail = email;
        mPassword = password;
        /*if (TextUtils.isEmpty(email)) {
            mView.onError(R.string.error_email_cant_be_blank);
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            mView.onError("Password is empty or not in correct format!");
            return;
        }*/
        signInWithEmail();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GOOGLE_SIGNIN) {
            if (resultCode == Activity.RESULT_OK) {
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handeGoogleSignInResult(googleSignInResult);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mView.onError(R.string.error_google_signin);
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
            mFirebaseUser = new FirebaseUser.Builder().setFullName(displayName)
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
                .updateChildren(mFirebaseUser.toHashMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //TODO fix google sign in
                        // mView.onUserSignedIn(mFirebaseUser);
                    }
                });
    }

    private void signInWithEmail() {
        mFirebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(mOnSignInCompleteListener)
                .addOnFailureListener(mOnSignInFailureListener);
    }

    private OnCompleteListener<AuthResult> mOnSignInCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                getUserByUid();
            }
        }
    };

    private OnFailureListener mOnSignInFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            e.printStackTrace();
            mView.onError(e.getMessage());
        }
    };

    private void getUserByUid() {
        final String uid = mFirebaseAuth.getCurrentUser().getUid();
        ApiModel apiModel = new ApiModelImpl();
        Call<List<User>> userCall = apiModel.getUserListByUID(uid);
        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<com.sanislo.lostandfound.model.User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() != 0) {
                        User user = response.body().get(0);
                        mView.onUserSignedIn(user);
                    } else {
                        mFirebaseAuth.signOut();
                        mView.onError("User not found!");
                    }
                } else {
                    Log.d(TAG, "onResponse: " + response.message());
                    mFirebaseAuth.signOut();
                }
            }

            @Override
            public void onFailure(Call<List<com.sanislo.lostandfound.model.User>> call, Throwable t) {
                t.printStackTrace();
                mView.onError(t.getMessage());
            }
        });
    }
}
