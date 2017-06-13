package com.sanislo.lostandfound.view.addThing;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.interfaces.AddThingView;
import com.sanislo.lostandfound.model.DescriptionPhotoItem;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.presenter.AddThingPresenter;
import com.sanislo.lostandfound.presenter.AddThingPresenterImpl;
import com.sanislo.lostandfound.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 24.12.16.
 */

public class AddThingEditableActivity extends AppCompatActivity implements AddThingView {
    public final String TAG = AddThingActivity.class.getSimpleName();
    private final int PICK_THING_COVER_PHOTO = 111;
    private final int PICK_THING_DESCRIPTION_PHOTOS = 222;
    private final int PICK_THING_PLACE = 333;
    private final int RP_READ_EXTERNAL_FOR_COVER = 666;
    private final int RP_READ_EXTERNAL_FOR_DESCRIPTION_PHOTOS = 444;
    private final int RP_FINE_LOCATION = 555;

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

    @BindView(R.id.iv_cover_photo)
    ImageView ivCoverPhoto;

    @BindView(R.id.iv_remove)
    ImageView ivRemoveCoverPhoto;

    @BindViews({R.id.ll_location, R.id.ll_cover_photo, R.id.ll_desc_photos})
    List<LinearLayout> mBottomBarItems;

    @BindView(R.id.bottom_navigation)
    LinearLayout llBottomNavigation;

    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;

    private AddThingPresenter mPresenter;
    private ArrayAdapter<String> mTypeAdapter;
    private String[] mTypeArray;
    private ArrayAdapter<String> mCategoriesAdapter;
    private MaterialDialog mProgressDialog;
    private Snackbar mErrorSnackbar;
    private Thing mThing;

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
            mPresenter.onTypeChanged(mTypeArray, position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public static final String EXTRA_THING = "EXTRA_THING";
    public static final Intent buildLaunchIntent(Context context, Thing thing) {
        Intent intent = new Intent(context, AddThingEditableActivity.class);
        intent.putExtra(EXTRA_THING, thing);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thing_custom_bottom_bar);
        ButterKnife.bind(this);
        supportPostponeEnterTransition();
        mPresenter = new AddThingPresenterImpl(this);
        fetchIntent();
        showThing(mThing);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.edit_thing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initCategories();
        initTypeSpinner();
        initDescriptionPhotosAdapter();
        initMapView();
        setBottomBarItemClickListeners();
    }

    private void setBottomBarItemClickListeners() {
        for (LinearLayout linearLayout : mBottomBarItems) {
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.ll_location:
                            onSelectPlace();
                            break;
                        case R.id.ll_cover_photo:
                            onSelectThingCoverPhoto();
                            break;
                        case R.id.ll_desc_photos:
                            onSelectThingDescriptionPhotos();
                            break;
                    }
                }
            });
        }
    }

    private void fetchIntent() {
        mThing = getIntent().getParcelableExtra(EXTRA_THING);
    }

    private void showThing(Thing thing) {
        edtTitle.setText(thing.getTitle());
        edtDescription.setText(thing.getDescription());
        Glide.with(this)
                .load(thing.getPhoto())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (e != null) e.printStackTrace();
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(ivCoverPhoto);
        displayThingMarker(mThing.getLocation().getLatLng());
        downloadDescriptionPhotos();
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
                updateThing();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCategories() {
        mCategoriesAdapter = new ArrayAdapter<String>(AddThingEditableActivity.this, android.R.layout.simple_spinner_item);
        mCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(mCategoriesAdapter);
        spCategory.setOnItemSelectedListener(mCategorySelectedListener);
    }

    private void initTypeSpinner() {
        mTypeArray = getResources().getStringArray(R.array.thing_type);
        mTypeAdapter = new ArrayAdapter<String>(AddThingEditableActivity.this,
                android.R.layout.simple_spinner_item,
                mTypeArray);
        mTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(mTypeAdapter);
        spType.setOnItemSelectedListener(mTypeSelectedListener);
        spType.setSelection(mThing.getType());
    }

    private ClickEventHook<DescriptionPhotoItem> mRemovePhotoClick = new ClickEventHook<DescriptionPhotoItem>() {
        @Override
        public void onClick(View v, int position, FastAdapter<DescriptionPhotoItem> fastAdapter, DescriptionPhotoItem item) {
            removeClickedDescriptionPhoto(position);
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

    private void removeClickedDescriptionPhoto(int position) {
        mDescriptionPhotosAdapter.select(position);
        mDescriptionPhotosAdapter.deleteAllSelectedItems();
        if (mDescriptionPhotosAdapter.getItemCount() == 0) {
            setTvSelectDescriptionPhotosVisibility(true);
        } else {
            setTvSelectDescriptionPhotosVisibility(false);
        }
    }

    private void initDescriptionPhotosAdapter() {
        mDescriptionPhotosAdapter = new FastItemAdapter();
        mDescriptionPhotosAdapter.withItemEvent(mRemovePhotoClick);
        attachDragCallback();
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddThingEditableActivity.this,
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

    private void initMapView() {
        if (mMapFragment == null) {
            GoogleMapOptions googleMapOptions = new GoogleMapOptions();
            googleMapOptions.liteMode(true);
            mMapFragment = MapFragment.newInstance(googleMapOptions);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fl_map_container, mMapFragment);
            ft.commit();
            mMapFragment.getMapAsync(mOnMapReadyCallback);
        }
    }

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d(TAG, "onMapReady: ");
            if (mGoogleMap == null) {
                mGoogleMap = googleMap;
            }
            displayThingMarker(mThing.getLocation().getLatLng());
        }
    };

    private void updateThing() {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        mThing.setTitle(title);
        mThing.setDescription(description);
        mPresenter.updateThing(mThing, mDescriptionPhotosAdapter.getAdapterItems());
    }

    private void onSelectThingCoverPhoto() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            selectThingCoverPhoto();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    RP_READ_EXTERNAL_FOR_COVER);
        }
    }

    private void displayCoverPlaceholder() {
        Glide.with(this)
                .load(R.drawable.thing_cover_placeholder)
                .into(ivCoverPhoto);
    }

    private void displayCoverPhoto(Uri coverPhotoUri) {
        Glide.with(this)
                .load(coverPhotoUri)
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        setRemoveCoverIconVisibility(true);
                        return false;
                    }
                })
                .into(ivCoverPhoto);
    }

    private void setRemoveCoverIconVisibility(boolean visible) {
        ivRemoveCoverPhoto.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void selectThingCoverPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        Intent сhooserIntent = Intent.createChooser(intent, "Select Image");
        startActivityForResult(сhooserIntent, PICK_THING_COVER_PHOTO);
    }

    public void onSelectThingDescriptionPhotos() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            selectThingDescriptionPhotos();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    RP_READ_EXTERNAL_FOR_DESCRIPTION_PHOTOS);
        }
    }

    public void onSelectPlace() {
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
        categories.add(0, getString(R.string.select_category));
        mCategoriesAdapter.addAll(categories);
        mCategoriesAdapter.notifyDataSetChanged();
        setCategory(categories);
    }

    private void setCategory(List<String> categories) {
        String thingCategory = mThing.getCategory();
        Log.d(TAG, "setCategory: " + categories);
        Log.d(TAG, "setCategory: thingCategory: " + thingCategory);
        int indexOfThingCategory = categories.indexOf(thingCategory);
        Log.d(TAG, "setCategory: indexOfThingCategory: " + indexOfThingCategory);
        if (indexOfThingCategory != -1) {
            spCategory.setSelection(indexOfThingCategory);
        }
    }

    @Override
    public void onProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }

    @Override
    public void onError(int errorMessageRes) {
        if (mErrorSnackbar == null || !mErrorSnackbar.isShownOrQueued()) {
            mErrorSnackbar = Snackbar.make(llBottomNavigation, errorMessageRes, Snackbar.LENGTH_LONG);
            mErrorSnackbar.show();
        }
    }

    @Override
    public void onThingAdded() {
        mProgressDialog.dismiss();
        this.finish();
    }

    @Override
    public void onCoverPhotoSelected(Uri coverPhotoUri) {
        displayCoverPhoto(coverPhotoUri);
    }

    @OnClick(R.id.iv_remove)
    public void onClickRemoveCoverPhoto() {
        mPresenter.removeCoverPhoto();
        displayCoverPlaceholder();
        setRemoveCoverIconVisibility(false);
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

    @Override
    public void onPlaceSelected(LatLng latLng) {
        displayThingMarker(latLng);
    }

    private void displayThingMarker(LatLng latLng) {
        if (mGoogleMap == null) return;
        mGoogleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mGoogleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f);
        mGoogleMap.moveCamera(cameraUpdate);
        Log.d(TAG, "displayThingMarker: " + latLng);
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

    private void downloadDescriptionPhotos() {
        List<String> descriptionPhotosList = mThing.getDescriptionPhotos();
        if (descriptionPhotosList == null) return;
        int descriptionPhotosCount = descriptionPhotosList.size();
        String[] descriptionPhotos = descriptionPhotosList.toArray(new String[descriptionPhotosCount]);
        for (String s : descriptionPhotos) {
            Log.d(TAG, "downloadDescriptionPhotos: s :" + s);
        }
        DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute(descriptionPhotos);
    }

    private void addDescriptionPhoto(Uri photoUri) {
        mDescriptionPhotosAdapter.add(new DescriptionPhotoItem(photoUri));
        mDescriptionPhotosAdapter.notifyDataSetChanged();
        setTvSelectDescriptionPhotosVisibility(false);
    }

    private class DownloadTask extends AsyncTask<String, File, Void> {
        public final String TAG = DownloadTask.class.getSimpleName();

        private Context mContext;
        private File mTempDirectory;
        private int mCounter = 0;

        public DownloadTask(Context context) {
            this.mContext = context;
            initTempDirectory();
        }

        private void initTempDirectory() {
            File cacheDir = mContext.getCacheDir();
            mTempDirectory = new File(cacheDir, "temp_thing_photos");
            if (!mTempDirectory.exists()) {
                mTempDirectory.mkdir();
            }
        }

        @Override
        protected Void doInBackground(String... sUrl) {
            clearCacheDir(mTempDirectory);
            for (String fileUrl : sUrl) {
                downloadFile(fileUrl);
                mCounter++;
            }
            return null;
        }

        private void clearCacheDir(File cacheDir) {
            String[] children = cacheDir.list();
            if (children != null)
            for (String s : children) {
                Log.d(TAG, "clearCacheDir: children: " + s);
                new File(s).delete();
            }
        }

        private void downloadFile(String fileUrl) {
            Log.d(TAG, "downloadFile: fileUrl: " + fileUrl);
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(fileUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                String fileName = FileUtils.getFileNameFromUrl(fileUrl);
                File file;
                if (!TextUtils.isEmpty(fileName)) {
                    file = new File(mTempDirectory, fileName + ".jpg");
                } else {
                    file = new File(mTempDirectory, mCounter + ".jpg");
                }
                Log.d(TAG, "downloadFile: " + fileName);
                Log.d(TAG, "downloadFile: " + file.getAbsolutePath());
                mCounter++;
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                    }
                    output.write(data, 0, count);
                }
                publishProgress(file);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }
                if (connection != null)
                    connection.disconnect();
            }
        }

        @Override
        protected void onProgressUpdate(File... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "onProgressUpdate: " + values[0].getAbsolutePath());
            Uri uri = Uri.fromFile(values[0]);
            addDescriptionPhoto(uri);
        }
    }
}
