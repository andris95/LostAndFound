package com.sanislo.lostandfound.presenter;

import android.content.Intent;

import com.sanislo.lostandfound.model.DescriptionPhotoItem;

import java.util.List;

/**
 * Created by root on 24.12.16.
 */

public interface AddThingPresenter {
    void addThing(String title, String description);
    void updateDescriptionPhotosList(List<DescriptionPhotoItem> descriptionPhotoItemList);
    void onCategoryChanged(int category);
    void onTypeChanged(String type);
    void onResume();
    void onPause();
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
