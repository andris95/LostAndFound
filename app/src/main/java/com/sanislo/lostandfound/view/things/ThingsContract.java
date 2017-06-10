package com.sanislo.lostandfound.view.things;

import com.sanislo.lostandfound.BasePresenter;
import com.sanislo.lostandfound.BaseView;
import com.sanislo.lostandfound.model.Thing;

import java.util.List;

/**
 * Created by root on 25.05.17.
 */

public interface ThingsContract {
    interface Presenter extends BasePresenter {
        void loadThings(int page);
        void loadMyThings(String userUid);
        void removeThing(int id);
        void updateThing(int id, boolean returned);
    }
    interface View extends BaseView<Presenter> {
        void showThings(List<Thing> thingList);
        void showError();
    }
}
