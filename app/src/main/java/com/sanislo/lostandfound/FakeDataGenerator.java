package com.sanislo.lostandfound;

import android.content.Context;
import android.util.Log;

import com.sanislo.lostandfound.model.Location;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 02.04.17.
 */

public class FakeDataGenerator {
    private String TAG = FakeDataGenerator.class.getSimpleName();
    private Context mContext;
    private ApiModel mApiModel = new ApiModelImpl();
    private String[] mTestPhotoURLs;
    private User mUser;
    private int mCurrentUploadingThing = 0;
    private List<Thing> mFakeThingList;

    public FakeDataGenerator(Context context, User user) {
        mContext = context;
        mUser = user;
        mTestPhotoURLs = new String[] {
                "https://wallpaperscraft.com/image/joy_jennifer_lawrence_2015_105464_1920x1080.jpg",
                "https://wallpaperscraft.com/image/toyota_supra_side_view_light_97798_1280x720.jpg",
                "https://wallpaperscraft.com/image/mountains_buildings_sky_peaks_snow_107559_1440x900.jpg",
                "https://softcover.s3.amazonaws.com/636/ruby_on_rails_tutorial_4th_edition/images/figures/kitten.jpg"
        };
    }

    public void postFakeThings() {
        //mFakeThingList = generateThings();
        postThing(mFakeThingList.get(mCurrentUploadingThing));
    }

    public void postCloseFakeThings(Context context) {
        mFakeThingList = generateCloseThings(context);
        postThing(mFakeThingList.get(mCurrentUploadingThing));
    }

    private void postThing(Thing thing) {
        Call<Void> call = mApiModel.postThing(thing);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: uploaded thing NO_" + mCurrentUploadingThing);
                    mCurrentUploadingThing++;
                    if (mCurrentUploadingThing < mFakeThingList.size() - 1) {
                        postThing(mFakeThingList.get(mCurrentUploadingThing));
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private List<Thing> generateThings(User user) {
        List<Thing> thingList = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            Thing thing = new Thing();
            thing.setCategory("");
            thing.setTitle("Thing NO_" + i);
            thing.setDescription("Description NO_" + i);
            thing.setType(i % 2 == 0 ? 1 : 2);
            thing.setUserAvatar(user.getAvatarURL());
            thing.setTimestamp(new Date().getTime());

            int randomPhotoPosition = ThreadLocalRandom.current().nextInt(0, mTestPhotoURLs.length - 1);
            thing.setPhoto(mTestPhotoURLs[randomPhotoPosition]);

            thing.setUserName(mUser.getFullName());
            thing.setUserAvatar(mUser.getAvatarURL());
            thing.setUserUID(mUser.getUid());

            double lat = ThreadLocalRandom.current().nextDouble(-90, 90);
            double lng = ThreadLocalRandom.current().nextDouble(-180, 180);
            Location location = new Location();
            location.setLat(lat);
            location.setLng(lng);
            thing.setLocation(location);

            thing.setDescriptionPhotos(Arrays.asList(mTestPhotoURLs));

            thingList.add(thing);
        }
        return thingList;
    }

    private List<Thing> generateCloseThings(Context context) {
        List<Thing> thingList = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            Thing thing = new Thing();
            thing.setCategory("");
            thing.setTitle("Thing NO_" + i);
            thing.setDescription(context.getString(R.string.lorem_ipsum));
            thing.setType(i % 2 == 0 ? 1 : 2);
            thing.setTimestamp(new Date().getTime());

            int randomPhotoPosition = ThreadLocalRandom.current().nextInt(0, mTestPhotoURLs.length - 1);
            thing.setPhoto(mTestPhotoURLs[randomPhotoPosition]);

            thing.setUserName(mUser.getFullName());
            thing.setUserAvatar(mUser.getAvatarURL());
            thing.setUserUID(mUser.getUid());

            double lng = ThreadLocalRandom.current().nextDouble(18, 27);
            double lat = ThreadLocalRandom.current().nextDouble(44, 52);
            Location location = new Location();
            location.setLat(lat);
            location.setLng(lng);
            thing.setLocation(location);

            thing.setDescriptionPhotos(Arrays.asList(mTestPhotoURLs));
            thingList.add(thing);
        }
        return thingList;
    }
}
