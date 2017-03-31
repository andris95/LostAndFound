package com.sanislo.lostandfound.model.api;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.UserAvatarUpdateRequest;

import java.util.List;

import retrofit2.Call;

/**
 * Created by root on 16.03.17.
 */

public interface ApiModel {
    Call<List<Thing>> getThings();
    Call<List<Thing>> getThings(String sort, String order);
    Call<Void> updateUserAvatar(int userId, String avatarURL);
    Call<Void> updateUserAvatar(int userId, UserAvatarUpdateRequest userAvatarUpdateRequest);
    Call<Void> postThing(Thing thing);
    Call<Void> saveUser(User user);
    Call<User> getUser(int id);
    Call<User> getUserByUID(String uid);
    Call<List<User>> getUserListByUID(String uid);
    Call<List<Thing>> getUsersThingsByType(String uid, String type);
}
