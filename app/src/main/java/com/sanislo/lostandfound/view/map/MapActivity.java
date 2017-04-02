package com.sanislo.lostandfound.view.map;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.view.BaseActivity;

/**
 * Created by root on 02.04.17.
 */

public class MapActivity extends BaseActivity {
    private String TAG = MapActivity.class.getSimpleName();
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 999;
    private ThingsMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (checkPlayServices()) {
            initMapFragment();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private void initMapFragment() {
        mMapFragment = new ThingsMapFragment();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_map_container, mMapFragment);
        fragmentTransaction.commit();
    }
}
