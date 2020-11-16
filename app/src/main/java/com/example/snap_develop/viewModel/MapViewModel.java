package com.example.snap_develop.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.model.MapModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapViewModel extends ViewModel {

    MapModel mapModel = new MapModel();
    private MutableLiveData<LatLng> deviceLocationResult;
    public LatLng deviceLocation;

    public MutableLiveData<LatLng> getDeviceLocationResult() {
        if (deviceLocationResult == null) {
            deviceLocationResult = new MutableLiveData<>();
        }
        return deviceLocationResult;
    }

    public void getDeviceLocation(FusedLocationProviderClient fusedLocationClient, GoogleMap map) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        mapModel.getDeviceLocation(fusedLocationClient, map, deviceLocationResult);
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
