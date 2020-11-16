package com.example.snap_develop.viewModel;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.snap_develop.model.MapModel;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapViewModel extends ViewModel {

    MapModel mapModel = new MapModel();
    public LatLng deviceLocation;

    public void getDeviceLocation(Activity mapActivity) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        deviceLocation = mapModel.fetchDeviceLocation(
                LocationServices.getFusedLocationProviderClient(mapActivity));
    }

    public int getRadius(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        return mapModel.getRadius(googleMap);
    }

    public double[] getCenter(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        return mapModel.getCenter(googleMap);
    }
}
