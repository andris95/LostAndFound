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

    @Override
    public Call<User> getUser(int id) {
        return mApiInterface.getUser(id);
    }

    @Override
    public Call<User> getUserByUID(String uid) {
        return mApiInterface.getUserByUID(uid);
    }

    @Override
    public Call<List<User>> getUserListByUID(String uid) {
        return mApiInterface.getUserByUIDList(uid);
    }
}
