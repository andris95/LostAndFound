package com.sanislo.lostandfound.view.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import butterknife.OnClick;

/**
 * Created by root on 30.03.17.
 */

public class ProfileActivity extends BaseActivity implements ProfileView {
    private String TAG = ProfileActivity.class.getSimpleName();
    private static final int RP_READ_EXTERNAL_FOR_COVER = 666;
    public static final int PICK_PROFILE_IMAGE = 777;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;

    @BindView(R.id.vp_things)
    ViewPager vpThings;

    @BindView(R.id.tab_things)
    TabLayout tabThings;

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
        //TODO UPDATE USERS UID ON LOGIN!!!!!
        String userUID = PreferencesManager.getUserUID(ProfileActivity.this);
        mProfilePresenter.getProfile(userUID);

        setupUsersThingsPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void onAvatarUpdated(String avatarURL) {
        displayUserAvatar(avatarURL);
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

    private void setupUsersThingsPager() {
        UsersThingsPagerAdapter adapter = new UsersThingsPagerAdapter(getSupportFragmentManager());
        vpThings.setAdapter(adapter);
        tabThings.setupWithViewPager(vpThings);
    }

    @OnClick(R.id.iv_avatar)
    public void onClickAvatar() {
        new MaterialDialog.Builder(this)
                .title(R.string.select_profile_image)
                .items(R.array.select_profile_image_list)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        Log.d(TAG, "onSelection: position: " + position);
                        switch (position) {
                            case 0:
                                checkGalleryPermission();
                                break;
                            case 1:
                                //TODO implement this
                                takeProfileImage();
                                break;
                        }
                    }
                })
                .show();
    }

    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            pickProfileImageFromGallery();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    RP_READ_EXTERNAL_FOR_COVER);
        }
    }

    private void pickProfileImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        Intent сhooserIntent = Intent.createChooser(intent, getString(R.string.select_profile_image));
        startActivityForResult(сhooserIntent, PICK_PROFILE_IMAGE);
    }

    private void takeProfileImage() {
        Toast.makeText(this, "To be continued", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RP_READ_EXTERNAL_FOR_COVER) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickProfileImageFromGallery();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mProfilePresenter.onActivityResult(requestCode, resultCode, data);
        //TODO FIX THIS
        mProfilePresenter.updateUserAvatar(ProfileActivity.this);
    }
}
