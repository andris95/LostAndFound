package com.sanislo.lostandfound.view.addThing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.interfaces.AddThingView;
import com.sanislo.lostandfound.model.DescriptionPhotoItem;
import com.sanislo.lostandfound.presenter.AddThingPresenter;
import com.sanislo.lostandfound.presenter.AddThingPresenterImpl;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 24.12.16.
 */

public class AddThingActivity extends AppCompatActivity implements AddThingView {
    public final String TAG = AddThingActivity.class.getSimpleName();
    private final int PICK_THING_COVER_PHOTO = 111;
    private final int PICK_THING_DESCRIPTION_PHOTOS = 222;
    private final int PICK_THING_PLACE = 333;
    private final int RP_READ_EXTERNAL_FOR_COVER = 666;
    private final int RP_READ_EXTERNAL_FOR_DESCRIPTION_PHOTOS = 444;
    private final int RP_FINE_LOCATION = 555;

    @BindView(R.id.root_add_thing)
    CoordinatorLayout mRoot;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.sp_category)
    Spinner spCategory;

    @BindView(R.id.sp_type)
    AppCompatSpinner spType;

    @BindView(R.id.edt_thing_title)
    EditText edtTitle;

    @BindView(R.id.edt_thing_description)
    EditText edtDescription;

    @BindView(R.id.tv_select_description_photos)
    TextView tvSelectDescriptionPhotos;

    @BindView(R.id.rv_description_photos_preview)
    RecyclerView rvDescriptionPhotos;

    private boolean DEBUG = true;
    private AddThingPresenter mPresenter;
    private ArrayAdapter<String> mTypeAdapter;
    private String[] mThingArray;
    private ArrayAdapter<String> mCategoriesAdapter;
    private MaterialDialog mProgressDialog;
    private Snackbar mErrorSnackbar;

    private FastItemAdapter<DescriptionPhotoItem> mDescriptionPhotosAdapter;

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

    private AdapterView.OnItemSelectedListener mTypeSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mPresenter.onTypeChanged(mThingArray[position]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thing);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPresenter = new AddThingPresenterImpl(this);
        initCategories();
        initTypeSpinner();
        initDescriptionPhotosAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_thing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_thing:
                addThing();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    private void initCategories() {
        mCategoriesAdapter = new ArrayAdapter<String>(AddThingActivity.this, android.R.layout.simple_spinner_item);
        mCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(mCategoriesAdapter);
        spCategory.setOnItemSelectedListener(mCategorySelectedListener);
    }

    private void initTypeSpinner() {
        mThingArray = getResources().getStringArray(R.array.thing_type);
        mTypeAdapter = new ArrayAdapter<String>(AddThingActivity.this,
                android.R.layout.simple_spinner_item,
                mThingArray);
        mTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(mTypeAdapter);
        spType.setOnItemSelectedListener(mTypeSelectedListener);
    }

    private ClickEventHook<DescriptionPhotoItem> mRemoveClick = new ClickEventHook<DescriptionPhotoItem>() {
        @Override
        public void onClick(View v, int position, FastAdapter<DescriptionPhotoItem> fastAdapter, DescriptionPhotoItem item) {
            fastAdapter.select(position);
            fastAdapter.deleteAllSelectedItems();
            Log.d(TAG, "onClick: getItemCount: " + fastAdapter.getItemCount());
            if (fastAdapter.getItemCount() == 0) {
                setTvSelectDescriptionPhotosVisibility(true);
            } else {
                setTvSelectDescriptionPhotosVisibility(false);
            }
        }

        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof DescriptionPhotoItem.ViewHolder) {
                return ClickListenerHelper.toList(((DescriptionPhotoItem.ViewHolder) viewHolder).getIvRemove());
            }
            return super.onBindMany(viewHolder);
        }
    };

    private void initDescriptionPhotosAdapter() {
        mDescriptionPhotosAdapter = new FastItemAdapter();
        mDescriptionPhotosAdapter.withItemEvent(mRemoveClick);
        attachDragCallback();
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddThingActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        rvDescriptionPhotos.setLayoutManager(layoutManager);
        rvDescriptionPhotos.setAdapter(mDescriptionPhotosAdapter);
    }

    private void attachDragCallback() {
        SimpleDragCallback dragCallback = new SimpleDragCallback(SimpleDragCallback.ALL, new ItemTouchCallback() {
            @Override
            public boolean itemTouchOnMove(int oldPosition, int newPosition) {
                Collections.swap(mDescriptionPhotosAdapter.getAdapterItems(), oldPosition, newPosition); // change position
                mDescriptionPhotosAdapter.notifyAdapterItemMoved(oldPosition, newPosition);
                return true;
            }
        });
        ItemTouchHelper touchHelper = new ItemTouchHelper(dragCallback);
        touchHelper.attachToRecyclerView(rvDescriptionPhotos);
    }

    private void addThing() {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        mPresenter.updateDescriptionPhotosList(mDescriptionPhotosAdapter.getAdapterItems());
        mPresenter.addThing(title, description);
    }

    @OnClick(R.id.btn_select_thing_cover_photo)
    public void onClickSelectThingPhoto() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            selectThingCoverPhoto();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    RP_READ_EXTERNAL_FOR_COVER);
        }
    }

    private void selectThingCoverPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        Intent сhooserIntent = Intent.createChooser(intent, "Select Image");
        startActivityForResult(сhooserIntent, PICK_THING_COVER_PHOTO);
    }

    @OnClick(R.id.tv_select_description_photos)
    public void onClickSelectThingDescriptionPhotos() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            selectThingDescriptionPhotos();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    RP_READ_EXTERNAL_FOR_DESCRIPTION_PHOTOS);
        }
    }

    @OnClick(R.id.btn_select_thing_place)
    public void onClickSelectPlace() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            selectPlace();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    RP_FINE_LOCATION);
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
        if (requestCode == RP_READ_EXTERNAL_FOR_DESCRIPTION_PHOTOS) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectThingDescriptionPhotos();
            }
        }
        if (requestCode == RP_READ_EXTERNAL_FOR_COVER) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectThingCoverPhoto();
            }
        }
        if (requestCode == RP_FINE_LOCATION) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPlace();
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
    }

    @Override
    public void onError(int errorMessageRes) {
        if (mErrorSnackbar == null || !mErrorSnackbar.isShownOrQueued()) {
            mErrorSnackbar = Snackbar.make(mRoot, errorMessageRes, Snackbar.LENGTH_LONG);
            mErrorSnackbar.show();
        }
    }

    @Override
    public void onThingAdded() {
        mProgressDialog.dismiss();
        this.finish();
    }

    @Override
    public void onDescriptionPhotosSelected(List<DescriptionPhotoItem> descriptionPhotoUriList) {
        if (descriptionPhotoUriList != null && !descriptionPhotoUriList.isEmpty()) {
            mDescriptionPhotosAdapter.clear();
            mDescriptionPhotosAdapter.add(descriptionPhotoUriList);
            mDescriptionPhotosAdapter.notifyDataSetChanged();
            setTvSelectDescriptionPhotosVisibility(false);
        } else {
            setTvSelectDescriptionPhotosVisibility(true);
        }
    }

    private void setTvSelectDescriptionPhotosVisibility(boolean visible) {
        tvSelectDescriptionPhotos.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onUploadStartedSimple() {
        boolean showMinMax = false;
        boolean indeterminate = true;
        mProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.publishing_progress)
                .content(R.string.publishing_progress_description)
                .progress(indeterminate, 100, showMinMax)
                .cancelable(false)
                .show();
    }

    @Override
    public void onUploadStartedWithPhotos(int fileCount) {
        boolean showMinMax = true;
        boolean indeterminate = false;
        mProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.publishing_progress)
                .content(R.string.publishing_progress_description)
                .progress(indeterminate, fileCount, showMinMax)
                .cancelable(false)
                .show();
    }

    private void selectPlace() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PICK_THING_PLACE);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e2) {
            e2.printStackTrace();
        }
    }
}
