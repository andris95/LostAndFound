package com.sanislo.lostandfound.model.api;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.UserAvatarUpdateRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by root on 16.03.17.
 */

public interface ApiInterface {

    @GET(ApiConstants.THINGS)
    Call<List<Thing>> getThings();

    @GET(ApiConstants.THINGS)
    Call<List<Thing>> getThings(@Query("_sort") String sort, @Query("_order") String order);

    @GET(ApiConstants.THINGS)
    Call<List<Thing>> getThings(@Query("_sort") String sort,
                                @Query("_order") String order,
                                @Query("title") List<String> titles,
                                @Query("city") List<String> cities);

    @GET(ApiConstants.THINGS)
    Call<List<Thing>> getThings(@QueryMap Map<String, String> options);

    @POST(ApiConstants.THINGS)
    Call<Void> postThing(@Body Thing thing);

    @POST(ApiConstants.USERS)
    Call<Void> saveUser(@Body User user);

    @PUT("users/{id}")
    Call<Void> updateUserAvatar(@Path("id") int userId, @Body String avatarURL);

    @PUT("users/{id}/edit")
    Call<Void> updateUserAvatar(@Path("id") int userId, @Body UserAvatarUpdateRequest userAvatarUpdateRequest);

    @PUT("users/{id}")
    Call<Void> updateUser(@Path("id") int userId, @Body User user);

    @GET(ApiConstants.USERS + "/{id}")
    Call<User> getUser(@Path("id") int id);

    @GET(ApiConstants.USERS)
    Call<User> getUserByUID(@Query("uid") String uid);

    @GET(ApiConstants.USERS)
    Call<List<User>> getUserByUIDList(@Query("uid") String uid);

    @GET(ApiConstants.THINGS)
    Call<List<Thing>> getUsersThingsByType(@Query("uid") String uid, @Query("type") String type);
}
