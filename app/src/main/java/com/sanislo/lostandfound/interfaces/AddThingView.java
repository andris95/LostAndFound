package com.sanislo.lostandfound.interfaces;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.sanislo.lostandfound.model.DescriptionPhotoItem;

import java.util.List;

/**
 * Created by root on 24.12.16.
 */

public interface AddThingView {
    void onCategoriesReady(List<String> categories);
    void onThingAdded();
    void onCoverPhotoSelected(Uri photoUri);
    void onDescriptionPhotosSelected(List<DescriptionPhotoItem> descriptionPhotoUriList);
    void onPlaceSelected(LatLng latLng);
    void onUploadStartedSimple();
    void onUploadStartedWithPhotos(int fileCount);
    void onProgress(int progress);
    void onError(int errorCode);
}
