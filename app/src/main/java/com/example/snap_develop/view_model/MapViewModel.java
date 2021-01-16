package com.example.snap_develop.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.model.MapModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

import timber.log.Timber;

public class MapViewModel extends ViewModel {

    private final MapModel mapModel = new MapModel();
    private MutableLiveData<LatLng> deviceLatLng;

    public MutableLiveData<LatLng> getDeviceLatLng() {
        Timber.i(MyDebugTree.START_LOG);
        if (deviceLatLng == null) {
            deviceLatLng = new MutableLiveData<>();
        }
        return deviceLatLng;
    }


    public void fetchDeviceLocation(FusedLocationProviderClient fusedLocationClient) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "fusedLocationClient", fusedLocationClient));
        if (deviceLatLng == null) {
            deviceLatLng = new MutableLiveData<>();
        }
        mapModel.fetchDeviceLocation(fusedLocationClient, deviceLatLng);
    }


    public VisibleRegion fetchVisibleRegion(GoogleMap googleMap) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "googleMap", googleMap));
        return mapModel.fetchVisibleRegion(googleMap);
    }
}
