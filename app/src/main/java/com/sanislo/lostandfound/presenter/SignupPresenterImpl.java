package com.sanislo.lostandfound.presenter;

import com.sanislo.lostandfound.interfaces.SignupView;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 18.03.17.
 */

public class SignupPresenterImpl implements SignupPresenter {
    private String TAG = SignupPresenter.class.getSimpleName();
    private ApiModel mApiModel = new ApiModelImpl();
    private SignupView mSignupView;

    public SignupPresenterImpl(SignupView signupView) {
        mSignupView = signupView;
    }

    @Override
    public void saveUser(User user) {
        Call<Void> call = mApiModel.saveUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mSignupView.onUserSaved();
                } else {
                    mSignupView.onError("Error creating user");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
