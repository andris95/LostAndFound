package com.sanislo.lostandfound;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

/**
 * Created by root on 27.03.17.
 */

public class LostAndFoundApplication extends Application {
    private static LostAndFoundApplication mApplication;
    private JobManager mJobManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize and create the image loader logic
        initDrawerImageLoader();
        configureJobManager();
        mApplication = this;
    }

    private void initDrawerImageLoader() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext())
                        .load(uri)
                        .placeholder(placeholder)
                        .centerCrop()
                        .into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ")
                            .backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary)
                            .sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ")
                            .backgroundColorRes(R.color.md_red_500)
                            .sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(getApplicationContext())
                .build();
        mJobManager = new JobManager(configuration);
        mJobManager.start();
    }

    public JobManager getJobManager() {
        return mJobManager;
    }

    public static LostAndFoundApplication getInstance() {
        return mApplication;
    }
}
