package com.example.snap_develop.model;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapModel {

    //private FusedLocationProviderClient fusedLocationClient =
    //LocationServices.getFusedLocationProviderClient(this);
    private Location location;
    private double latitude;
    private double longitude;

    @SuppressLint("MissingPermission")
    public LatLng fetchDeviceLocation(FusedLocationProviderClient fusedLocationClient) {

        latitude = 0;
        longitude = 0;

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            location = task.getResult();

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            System.out.println(
                                    "-------------------------getLastLocation:TRUE"
                                            + "---------------------------");
                            System.out.println("緯度：" + latitude + "経度：" + longitude);
                        } else {
                            System.out.println(
                                    "-------------------------getLastLocation:ELSE"
                                            + "---------------------------");
                            Log.d("debug", "計測不能");
                        }
                    }
                });
        return new LatLng(latitude, longitude);
    }
}
