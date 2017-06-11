package com.sanislo.lostandfound.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.firebase.auth.FirebaseAuth;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.view.things.ThingsContract;
import com.sanislo.lostandfound.view.things.ThingsPresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 11.06.17.
 */

public class UpdateThingsJob extends Job implements ThingsContract.View {
    public static final String TAG = UpdateThingsJob.class.getSimpleName();
    private User mUser;
    private ThingsPresenter mThingsPresenter;
    private ApiModel mApiModel = new ApiModelImpl();

    public UpdateThingsJob(User user) {
        super(new Params(1000)
                .requireNetwork());
        mUser = user;
        mThingsPresenter = new ThingsPresenter(this);
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "onAdded: ");
    }

    private void getMyThings() {
        Log.d(TAG, "getMyThings: ");
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mThingsPresenter.loadMyThings(myUid);
    }

    private void updateThings(List<Thing> thingList) {
        Log.d(TAG, "updateThings: " + thingList);
        for (Thing thing : thingList) {
            thing.setUserAvatar(mUser.getAvatarURL());
            thing.setUserName(mUser.getFullName());
            updateThing(thing);
        }
    }

    private void updateThing(Thing thing) {
        Call<Void> call = mApiModel.updateThing(thing.getId(), thing);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onRun() throws Throwable {
        getMyThings();
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

    @Override
    public void setPresenter(ThingsContract.Presenter presenter) {

    }

    @Override
    public void showThings(List<Thing> thingList) {
        updateThings(thingList);
    }

    @Override
    public void showError() {

    }
}
