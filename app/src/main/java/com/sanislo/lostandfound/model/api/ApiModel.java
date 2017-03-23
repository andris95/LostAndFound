package com.sanislo.lostandfound.model.api;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;

import java.util.List;

import retrofit2.Call;

/**
 * Created by root on 16.03.17.
 */

public interface ApiModel {
    /*Call<ImgRecognitionResponse> uploadImage(String token,
                                             String type,
                                             RequestBody file);

    Call<RegistrationResponse> registerId(String token, User user);

    Call<Void> sendCode(String token, ActivationCodeRequest request);

    Call<RegistrationResponse> sendCredentials(String token, Credentials credentials);

    Call<Void> login(String token, LoginModel loginModel);

    Call<UserProfile> getProfile();

    Call<SponsorModel> getSponsors();*/

    Call<List<Thing>> getThings();
    Call<Void> postThing(Thing thing);
    Call<Void> saveUser(User user);
}
