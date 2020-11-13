package com.example.snap_develop.model;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class MapModel {

    public LatLng deviceLocation = new LatLng(0, 0);
    private Location location;
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

    @SuppressLint("MissingPermission")
    public void getDeviceLocation(FusedLocationProviderClient fusedLocationClient,
            final GoogleMap mMap, final MutableLiveData<LatLng> deviceLocationResult) {

        final double[] latitude = {0};
        final double[] longitude = {0};

        Task<Location> getLastLocation = fusedLocationClient.getLastLocation();
        getLastLocation.addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            location = task.getResult();

                            latitude[0] = location.getLatitude();
                            longitude[0] = location.getLongitude();
                            System.out.println("緯度:" + latitude[0] + ",経度" + longitude[0]);

                            deviceLocation = new LatLng(latitude[0], longitude[0]);

                            mMap.addMarker(
                                    new MarkerOptions().position(deviceLocation).title("現在地"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(deviceLocation));

                            deviceLocationResult.setValue(deviceLocation);
                        } else {
                            Log.d("debug", "計測不能");

                        }
                    }
                });
    }

    public int getRadius(GoogleMap map) {
        float zoom = map.getCameraPosition().zoom;
        int result = 0;

        for (float key : radius.keySet()) {
            if (key == zoom) {
                result = radius.get(key);
            }
        }

        System.out.println("ZOOM_LEVEL:" + zoom);
        System.out.println("RADIUS:" + result);

        return result;
    }

    public double[] getCenter(GoogleMap map) {
        CameraPosition cameraPos = map.getCameraPosition();
        LatLng centerLatLng = new LatLng(cameraPos.target.latitude, cameraPos.target.longitude);
        Point centerPoint = map.getProjection().toScreenLocation(centerLatLng);

        double[] centerInfo = {centerPoint.x, centerPoint.y};

        System.out.println("緯度：、" + centerLatLng.latitude + "経度：" + centerLatLng.longitude);
        System.out.println("中心座標＞＞＞X：" + centerPoint.x + "、Y：" + centerPoint.y);

        return centerInfo;
    }
}
