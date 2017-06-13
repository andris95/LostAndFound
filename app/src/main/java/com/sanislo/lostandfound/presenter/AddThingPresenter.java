package com.sanislo.lostandfound.presenter;

import android.content.Intent;

import com.sanislo.lostandfound.model.DescriptionPhotoItem;
import com.sanislo.lostandfound.model.Thing;

import java.util.List;

/**
 * Created by root on 24.12.16.
 */

public interface AddThingPresenter {
    void addThing(String title, String description);
    void updateThing(Thing thing, List<DescriptionPhotoItem> descriptionPhotoItemList);
    void updateDescriptionPhotosList(List<DescriptionPhotoItem> descriptionPhotoItemList);
    void removeCoverPhoto();
    void onCategoryChanged(int position);
    void onTypeChanged(String[] types, int position);
    void onResume();
    void onPause();
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
