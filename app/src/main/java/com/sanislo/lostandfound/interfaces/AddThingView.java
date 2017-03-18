package com.sanislo.lostandfound.interfaces;

import java.util.List;

/**
 * Created by root on 24.12.16.
 */

public interface AddThingView {
    void onCategoriesReady(List<String> categories);
    void onThingAdded();
    void onProgress(int progress);
}
