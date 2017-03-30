package com.sanislo.lostandfound.model.api;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by root on 16.03.17.
 */

public interface ApiInterface {

    @GET(ApiConstants.THINGS)
    Call<List<Thing>> getThings();

    @POST(ApiConstants.THINGS)
    Call<Void> postThing(@Body Thing thing);

    @POST(ApiConstants.USERS)
    Call<Void> saveUser(@Body User user);

    @GET(ApiConstants.USERS + "/{id}")
    Call<User> getUser(@Path("id") int id);

    @GET(ApiConstants.USERS)
    Call<User> getUserByUID(@Query("uid") String uid);

    @GET(ApiConstants.USERS)
    Call<List<User>> getUserByUIDList(@Query("uid") String uid);

    @GET(ApiConstants.THINGS)
    Call<List<Thing>> getUsersThingsByType(@Query("uid") String uid, @Query("type") String type);
}
