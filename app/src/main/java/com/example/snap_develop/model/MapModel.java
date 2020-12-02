package com.example.snap_develop.model;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapModel {

    private Location mLocation;

    @SuppressLint("MissingPermission")
    public void fetchDeviceLocation(FusedLocationProviderClient fusedLocationClient,
            final MutableLiveData<LatLng> deviceLatLng) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        fusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLocation = task.getResult();
                            LatLng latLng = new LatLng(mLocation.getLatitude(),
                                    mLocation.getLongitude());
                            Log.i(LogUtil.getClassName(),
                                    String.format("lat:%s , lon:%s", latLng.latitude,
                                            latLng.longitude));
                            deviceLatLng.setValue(latLng);
                        } else {
                            Log.d(LogUtil.getClassName(), String.valueOf(task.getException()));
                        }
                    }
                });
    }


//    public LatLng fetchCenterLatLng(GoogleMap googleMap) {
//        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
//        CameraPosition cameraPos = googleMap.getCameraPosition();
//        LatLng centerLatLng = new LatLng(cameraPos.target.latitude, cameraPos.target.longitude);
//        Log.d(LogUtil.getClassName(),
//                String.format("lat:%s , lon:%s", centerLatLng.latitude, centerLatLng.longitude));
//        return centerLatLng;
//    }


    //表示されている地図の北東と南西の緯度経度を取得
    public VisibleRegion fetchVisibleRegion(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        Projection projection = googleMap.getProjection();
        return projection.getVisibleRegion();
    }

    //現在地の取得を取得
//    public
}
