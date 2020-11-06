package com.example.snap_develop.viewModel;

import android.app.Activity;
import android.graphics.Point;

import androidx.lifecycle.ViewModel;

import com.example.snap_develop.model.MapModel;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapViewModel extends ViewModel {

    MapModel mapModel = new MapModel();
    public LatLng deviceLocation;

    public void getDeviceLocation(Activity mapActivity) {
        deviceLocation = mapModel.fetchDeviceLocation(
                LocationServices.getFusedLocationProviderClient(mapActivity));
    }

    public int getRadius(GoogleMap googleMap) {
        return 0;
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
