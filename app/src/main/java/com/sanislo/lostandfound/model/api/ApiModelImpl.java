package com.sanislo.lostandfound.model.api;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;

import java.util.List;

import retrofit2.Call;

/**
 * Created by root on 16.03.17.
 */

public class ApiModelImpl implements ApiModel {
    private ApiInterface mApiInterface = ApiModule.getApiInterface();

    @Override
    public Call<List<Thing>> getThings() {
        return mApiInterface.getThings();
    }

    @Override
    public Call<Void> postThing(Thing thing) {
        return mApiInterface.postThing(thing);
    }

    @Override
    public Call<Void> saveUser(User user) {
        return mApiInterface.saveUser(user);
    }

    /*@Override
    public Call<ImgRecognitionResponse> uploadImage(String token, String type, RequestBody file) {
        return mApiInterface.uploadImage(token, type, file);
    }

    @Override
    public Call<RegistrationResponse> registerId(String token, User user) {
        return mApiInterface.registerId(token, user);
    }

    @Override
    public Call<Void> sendCode(String token, ActivationCodeRequest request) {
        return mApiInterface.sendCode(token, request);
    }

    @Override
    public Call<RegistrationResponse> sendCredentials(String token, Credentials credentials) {
        return mApiInterface.sendCredentials(token, credentials);
    }

    @Override
    public Call<Void> login(String token, LoginModel loginModel) {
        return mApiInterface.login(token, loginModel);
    }

    @Override
    public Call<UserProfile> getProfile() {
        return mApiInterface.getProfile();
    }

    @Override
    public Call<SponsorModel> getSponsors() {
        return mApiInterface.getSponsors();
    }*/
}
