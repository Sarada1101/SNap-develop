package com.example.snap_develop.viewModel;

import android.app.Activity;
import android.graphics.Point;

import androidx.lifecycle.ViewModel;

import com.example.snap_develop.model.MapModel;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class MapViewModel extends ViewModel {

    MapModel mapModel = new MapModel();
    public LatLng deviceLocation;
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

    public void getDeviceLocation(Activity mapActivity) {
        deviceLocation = mapModel.fetchDeviceLocation(
                LocationServices.getFusedLocationProviderClient(mapActivity));
    }

    public int getRadius(GoogleMap map) {
        System.out.println("-------------------------getRadius---------------------------");
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
        System.out.println("-------------------------getCenter---------------------------");

        CameraPosition cameraPos = map.getCameraPosition();
        LatLng centerLatLng = new LatLng(cameraPos.target.latitude, cameraPos.target.longitude);
        Point centerPoint = map.getProjection().toScreenLocation(centerLatLng);

        double[] centerInfo = {centerPoint.x, centerPoint.y};

        System.out.println("緯度：、" + centerLatLng.latitude + "経度：" + centerLatLng.longitude);
        System.out.println("中心座標＞＞＞X：" + centerPoint.x + "、Y：" + centerPoint.y);

        return centerInfo;
    }
}
