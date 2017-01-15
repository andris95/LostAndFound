package com.sanislo.lostandfound.presenter;

import android.content.Intent;

/**
 * Created by root on 24.12.16.
 */

public interface AddThingPresenter {
    void addThing(String title, String description);
    void onCategoryChanged(int category);
    void onResume();
    void onPause();
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
