package com.example.snap_develop.viewModel;

import android.app.Activity;

import androidx.lifecycle.ViewModel;

import com.example.snap_develop.model.MapModel;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapViewModel extends ViewModel {

    MapModel mapModel = new MapModel();
    public LatLng deviceLocation;

    public void getDeviceLocation(Activity mapActivity) {
        deviceLocation = mapModel.fetchDeviceLocation(
                LocationServices.getFusedLocationProviderClient(mapActivity));
    }

    public int getRadius(GoogleMap googleMap) {
        return mapModel.getRadius(googleMap);
    }

    public double[] getCenter(GoogleMap googleMap) {
        return mapModel.getCenter(googleMap);
    }
}
