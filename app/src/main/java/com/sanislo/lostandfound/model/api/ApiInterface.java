package com.sanislo.lostandfound.model.api;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by root on 16.03.17.
 */

public interface ApiInterface {
    /*@POST(ApiConstants.API_ACTIVATE)
    Call<ImgRecognitionResponse> uploadImage(@Header(ApiConstants.API_KEY_PARAM) String token,
                                             @Header(ApiConstants.DOC_TYPE) String type,
                                             @Body RequestBody file);

    @POST(ApiConstants.API_REGISTER_ID)
    Call<RegistrationResponse> registerId(@Header(ApiConstants.API_KEY_PARAM) String token,
                                          @Body User user);

    @POST(ApiConstants.API_ACTIVATE)
    Call<Void> sendCode(@Header(ApiConstants.API_KEY_PARAM) String token,
                        @Body ActivationCodeRequest request);

    @POST(ApiConstants.API_USER_CREDENTIALS)
    Call<RegistrationResponse> sendCredentials(@Header(ApiConstants.API_KEY_PARAM) String token,
                                               @Body Credentials credentials);

    @POST(ApiConstants.API_LOGIN)
    Call<Void> login(@Header(ApiConstants.API_KEY_PARAM) String token,
                     @Body LoginModel loginModel);

    @GET(ApiConstants.API_PROFILE)
    Call<UserProfile> getProfile();

    @GET(ApiConstants.API_SPONSORED)
    Call<SponsorModel> getSponsors();*/

    @GET(ApiConstants.THINGS)
    Call<List<Thing>> getThings();

    @POST(ApiConstants.THINGS)
    Call<Void> postThing(@Body Thing thing);

    @POST(ApiConstants.USERS)
    Call<Void> saveUser(@Body User user);
}
