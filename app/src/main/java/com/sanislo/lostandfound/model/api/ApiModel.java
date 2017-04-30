package com.sanislo.lostandfound.model.api;

import com.sanislo.lostandfound.model.Category;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by root on 16.03.17.
 */

public interface ApiModel {
    Call<List<Thing>> getThings();
    Call<List<Thing>> getThings(String sort, String order);
    Call<List<Thing>> getThings(Map<String, String> options);
    Call<List<Category>> getCategories();
    Call<Void> updateUser(int userId, User user);
    Call<Void> postThing(Thing thing);
    Call<Void> saveUser(User user);
    Call<Void> saveUser(String uid, User user);
    Call<User> getUser(int id);
    Call<User> getUserByUID(String uid);
    Call<List<User>> getUserListByUID(String uid);
    Call<List<Thing>> getUsersThingsByType(String uid, int type);
}
