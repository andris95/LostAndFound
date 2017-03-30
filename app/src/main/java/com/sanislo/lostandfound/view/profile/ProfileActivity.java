package com.sanislo.lostandfound.view.profile;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.utils.PreferencesManager;
import com.sanislo.lostandfound.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 30.03.17.
 */

public class ProfileActivity extends BaseActivity implements ProfileView {
    private String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;

    @BindView(R.id.vp_things)
    ViewPager vpThings;

    private ProfilePresenter mProfilePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int transparentColor = ContextCompat.getColor(ProfileActivity.this, android.R.color.transparent);
        mCollapsingToolbarLayout.setExpandedTitleColor(transparentColor);

        mProfilePresenter = new ProfilePresenterImpl(this);
        String userUID = PreferencesManager.getUserUID(ProfileActivity.this);
        mProfilePresenter.getProfile(userUID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onProfileLoaded(User user) {
        mCollapsingToolbarLayout.setTitle(user.getFullName());
        displayUserAvatar(user.getAvatarURL());
    }

    private void displayUserAvatar(String url) {
        Glide.with(this)
                .load(url)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                        return false;
                    }
                })
                .placeholder(R.drawable.account)
                .error(R.drawable.account)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivAvatar);
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        mCollapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        mCollapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        updateBackground((FloatingActionButton) findViewById(R.id.fab), palette);
        //supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.accent));

        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }
}
