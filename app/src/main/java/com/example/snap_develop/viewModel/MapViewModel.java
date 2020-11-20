package com.example.snap_develop.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.model.MapModel;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

public class MapViewModel extends ViewModel {

    private final MapModel mMapModel = new MapModel();
    private MutableLiveData<LatLng> deviceLatLng;

    public MutableLiveData<LatLng> getDeviceLatLng() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        if (deviceLatLng == null) {
            deviceLatLng = new MutableLiveData<>();
        }
        return deviceLatLng;
    }


    public void fetchDeviceLocation(FusedLocationProviderClient fusedLocationClient) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        mMapModel.fetchDeviceLocation(fusedLocationClient, deviceLatLng);
    }

    public int getRadius(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        return mapModel.getRadius(googleMap);
    }

    public VisibleRegion fetchVisibleRegion(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        return mMapModel.fetchVisibleRegion(googleMap);
    }
}
