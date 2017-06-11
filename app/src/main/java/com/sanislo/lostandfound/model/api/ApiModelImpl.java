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

public class ApiModelImpl implements ApiModel {
    private ApiInterface mApiInterface = ApiModule.getApiInterface();

    @Override
    public Call<List<Thing>> getThings() {
        return mApiInterface.getThings();
    }

    @Override
    public Call<List<Thing>> getThings(String sort, String order) {
        return mApiInterface.getThings(sort, order);
    }

    @Override
    public Call<List<Thing>> getThings(String sort, String order, int page) {
        return mApiInterface.getThings(sort, order, page);
    }

    @Override
    public Call<List<Thing>> getThings(Map<String, String> options) {
        return mApiInterface.getThings(options);
    }

    @Override
    public Call<List<Category>> getCategories() {
        return mApiInterface.getCategories();
    }

    @Override
    public Call<Void> updateUser(int userId, User user) {
        return mApiInterface.updateUser(userId, user);
    }

    @Override
    public Call<Void> postThing(Thing thing) {
        return mApiInterface.postThing(thing);
    }

    @Override
    public Call<Void> saveUser(User user) {
        return mApiInterface.saveUser(user);
    }

    @Override
    public Call<List<User>> getUserListByUID(String uid) {
        return mApiInterface.getUserByUIDList(uid);
    }

    @Override
    public Call<List<Thing>> getUsersThingsByType(String uid, int type) {
        return mApiInterface.getUsersThingsByType(uid, type);
    }

    @Override
    public Call<Void> removeThing(int id) {
        return mApiInterface.removeThing(id);
    }

    @Override
    public Call<Void> updateThing(int id, boolean returned) {
        return mApiInterface.updateThing(id, returned);
    }

    @Override
    public Call<Void> updateThing(int id, Thing thing) {
        return mApiInterface.updateThing(id, thing);
    }
}
