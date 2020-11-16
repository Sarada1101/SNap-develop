package com.example.snap_develop.model;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class MapModel {

    private Location location;

    @SuppressLint("MissingPermission")
    public void getDeviceLocation(FusedLocationProviderClient fusedLocationClient,
            final MutableLiveData<LatLng> deviceLatLng) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        Task<Location> getLastLocation = fusedLocationClient.getLastLocation();
        getLastLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    location = task.getResult();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.i(LogUtil.getClassName(),
                            String.format("lat:%s , lon:%s", latLng.latitude, latLng.longitude));
                    deviceLatLng.setValue(latLng);
                } else {
                    Log.d(LogUtil.getClassName(), String.valueOf(task.getException()));
                }
            }
        });
    }

    public int getRadius(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        Map<Float, Integer> radius = new HashMap<Float, Integer>() {
            {
                put(1.0f, 10000000);
                put(2.0f, 9000000);
                put(3.0f, 8000000);
                put(4.0f, 4000000);
                put(5.0f, 2000000);
                put(6.0f, 1000000);
                put(7.0f, 600000);
                put(8.0f, 300000);
                put(9.0f, 150000);
                put(10.0f, 70000);
                put(11.0f, 35000);
                put(12.0f, 20000);
                put(13.0f, 10000);
                put(14.0f, 5000);
                put(15.0f, 2500);
                put(16.0f, 1200);
                put(17.0f, 600);
                put(18.0f, 300);
                put(19.0f, 150);
                put(20.0f, 80);
                put(21.0f, 40);
            }
        };

        float zoom = googleMap.getCameraPosition().zoom;
        int result = 0;
        for (Map.Entry<Float, Integer> key : radius.entrySet()) {
            if (zoom == 1.0 && key.getKey() == 1.0f) {
                result = radius.get(key.getKey());
            } else if (zoom == 21.0 && key.getKey() == 21.0f) {
                result = radius.get(key.getKey());
            } else if (key.getKey() > zoom && key.getKey() - 1 <= zoom) {
                result = radius.get(key.getKey() - 1);
            }
        }
        Log.d(LogUtil.getClassName(), String.format("zoomLevel:%s , radius:%sm", zoom, result));
        return result;
    }

    public double[] getCenter(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        CameraPosition cameraPos = googleMap.getCameraPosition();
        LatLng centerLatLon = new LatLng(cameraPos.target.latitude, cameraPos.target.longitude);
        Log.d(LogUtil.getClassName(),
                String.format("lat:%s , lon:%s", centerLatLon.latitude, centerLatLon.longitude));
        return new double[]{centerLatLon.latitude, centerLatLon.longitude};
    }
}
