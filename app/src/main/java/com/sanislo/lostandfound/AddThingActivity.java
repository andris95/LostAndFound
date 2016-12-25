package com.sanislo.lostandfound;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sanislo.lostandfound.presenter.AddThingPresenter;
import com.sanislo.lostandfound.presenter.AddThingPresenterImpl;
import com.sanislo.lostandfound.view.AddThingView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 24.12.16.
 */

public class AddThingActivity extends AppCompatActivity implements AddThingView {
    public static final String TAG = AddThingActivity.class.getSimpleName();
    public static final int PICK_THING_COVER_PHOTO = 111;
    public static final int PICK_THING_DESCRIPTION_PHOTOS = 222;
    public static final int RP_READ_EXTERNAL = 333;

    @BindView(R.id.sp_category)
    Spinner spCategory;

    @BindView(R.id.edt_thing_title)
    EditText edtTitle;

    @BindView(R.id.edt_thing_description)
    EditText edtDescription;

    private AddThingPresenter mPresenter;
    private ArrayAdapter<String> mCategoriesAdapter;
    private MaterialDialog mProgressDialog;

    private AdapterView.OnItemSelectedListener mCategorySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mPresenter.onCategoryChanged(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mPresenter.onCategoryChanged(AdapterView.INVALID_POSITION);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thing);
        ButterKnife.bind(this);

        mPresenter = new AddThingPresenterImpl(this);
        initCategories();
    }

    private void initCategories() {
        mCategoriesAdapter = new ArrayAdapter<String>(AddThingActivity.this, android.R.layout.simple_spinner_item);
        mCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(mCategoriesAdapter);
        spCategory.setOnItemSelectedListener(mCategorySelectedListener);
    }

    private void initProgressDialog() {
        boolean showMinMax = true;
        mProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.publishing_progress)
                .content(R.string.publishing_progress_description)
                .progress(false, 100, showMinMax)
                .cancelable(false)
                .show();
    }

    @OnClick(R.id.btn_add_thing)
    public void onClickAddThing() {
        initProgressDialog();
        addThing();
    }

    private void addThing() {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        mPresenter.addThing(getString(R.string.lorem_ipsum_title), getString(R.string.description));
    }

    @OnClick(R.id.btn_select_thing_cover_photo)
    public void onClickSelectThingPhoto() {
        selectThingCoverPhoto();
    }

    private void selectThingCoverPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        Intent сhooserIntent = Intent.createChooser(intent, "Select Image");
        startActivityForResult(сhooserIntent, PICK_THING_COVER_PHOTO);
    }

    @OnClick(R.id.btn_select_thing_photos)
    public void onClickSelectThingDescriptionPhotos() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            selectThingDescriptionPhotos();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    RP_READ_EXTERNAL);
        }
    }

    private void selectThingDescriptionPhotos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent сhooserIntent = Intent.createChooser(intent, "Select photos for description");
        startActivityForResult(сhooserIntent, PICK_THING_DESCRIPTION_PHOTOS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RP_READ_EXTERNAL) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectThingDescriptionPhotos();
            }
        }
    }

    @Override
    public void onCategoriesReady(List<String> categories) {
        mCategoriesAdapter.clear();
        mCategoriesAdapter.addAll(categories);
        mCategoriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProgress(int progress) {
        mProgressDialog.setProgress(progress);
        Log.d(TAG, "onProgress: " + mProgressDialog.getCurrentProgress());
    }

    @Override
    public void onThingAdded() {
        mProgressDialog.dismiss();
        this.finish();
    }
}
