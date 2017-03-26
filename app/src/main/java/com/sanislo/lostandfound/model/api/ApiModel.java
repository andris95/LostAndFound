package com.sanislo.lostandfound.model.api;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;

import java.util.List;

import retrofit2.Call;

/**
 * Created by root on 16.03.17.
 */

public interface ApiModel {
    Call<List<Thing>> getThings();
    Call<Void> postThing(Thing thing);
    Call<Void> saveUser(User user);
    Call<User> getUser(int id);
    Call<User> getUserByUID(String uid);
    Call<List<User>> getUserListByUID(String uid);
}
