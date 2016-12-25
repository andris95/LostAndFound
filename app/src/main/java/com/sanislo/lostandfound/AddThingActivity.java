package com.sanislo.lostandfound;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.presenter.AddThingPresenter;
import com.sanislo.lostandfound.presenter.AddThingPresenterImpl;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.view.AddThingView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 24.12.16.
 */

public class AddThingActivity extends AppCompatActivity implements AddThingView {
    public static final String TAG = AddThingActivity.class.getSimpleName();
    public static final int PICK_THING_PHOTO = 111;

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
        mPresenter.addThing(title, description);
    }

    @OnClick(R.id.btn_select_thing_photo)
    public void onClickSelectThingPhoto() {
        selectPhotoAboutThing();
    }

    private void selectPhotoAboutThing() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        Intent сhooserIntent = Intent.createChooser(getIntent, "Select Image");
        startActivityForResult(сhooserIntent, PICK_THING_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCategoriesReady(List<String> categories) {
        mCategoriesAdapter.clear();
        mCategoriesAdapter.addAll(categories);
        mCategoriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onThingAdded() {
        mProgressDialog.dismiss();
        this.finish();
    }

    @Override
    public void onProgress(int progress) {
        mProgressDialog.incrementProgress(progress);
    }
}
