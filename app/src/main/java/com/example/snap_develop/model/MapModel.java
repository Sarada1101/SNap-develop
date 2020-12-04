package com.example.snap_develop.model;

import android.annotation.SuppressLint;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.MyDebugTree;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import timber.log.Timber;

public class MapModel {

    @SuppressLint("MissingPermission")
    public void fetchDeviceLocation(FusedLocationProviderClient fusedLocationClient,
            final MutableLiveData<LatLng> deviceLatLng) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fusedLocationClient", fusedLocationClient,
                "deviceLatLng", deviceLatLng));
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Timber.i(String.format("%s=%s, %s=%s", "lat", latLng.latitude, "lng", latLng.longitude));
                    deviceLatLng.setValue(latLng);
                } else {
                    Timber.i(MyDebugTree.FAILURE_LOG);
                    Timber.e(task.getException().toString());
                }
            }
        });
    }


    //表示されている地図の北東と南西の緯度経度を取得
    public VisibleRegion fetchVisibleRegion(GoogleMap googleMap) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "googleMap", googleMap));
        Projection projection = googleMap.getProjection();
        return projection.getVisibleRegion();
    }

    //現在地の取得を取得
//    public
}
