package com.sanislo.lostandfound.interfaces;

import com.sanislo.lostandfound.model.DescriptionPhotoItem;

import java.util.List;

/**
 * Created by root on 24.12.16.
 */

public interface AddThingView {
    void onCategoriesReady(List<String> categories);
    void onThingAdded();
    void onDescriptionPhotosSelected(List<DescriptionPhotoItem> descriptionPhotoUriList);
    //void onUploadStarted(UploadType uploadType, int fileCount);
    void onUploadStartedSimple();
    void onUploadStartedWithPhotos(int fileCount);
    void onProgress(int progress);
    void onError(int errorCode);
}
