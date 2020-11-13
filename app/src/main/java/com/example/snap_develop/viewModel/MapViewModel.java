package com.example.snap_develop.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.model.MapModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapViewModel extends ViewModel {

    MapModel mapModel = new MapModel();
    private MutableLiveData<LatLng> deviceLocationResult;

    public MutableLiveData<LatLng> getDeviceLocationResult() {
        if (deviceLocationResult == null) {
            deviceLocationResult = new MutableLiveData<>();
        }
        return deviceLocationResult;
    }

    public void getDeviceLocation(FusedLocationProviderClient fusedLocationClient, GoogleMap map) {
        System.out.println("--------------------getDeviceLocation-----------------------");
        mapModel.getDeviceLocation(fusedLocationClient, map, deviceLocationResult);
    }

    public int getRadius(GoogleMap map) {
        System.out.println("-------------------------getRadius--------------------------");
        return mapModel.getRadius(map);
    }

    public double[] getCenter(GoogleMap map) {
        System.out.println("-------------------------getCenter--------------------------");
        return mapModel.getCenter(map);
    }
}
