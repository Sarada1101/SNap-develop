package com.example.snap_develop.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.MapViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import javax.annotation.Nullable;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnCameraIdleListener {


    FusedLocationProviderClient fusedLocationClient;
    private final int REQUEST_PERMISSION = 1000;
    private GoogleMap googleMap;
    private MapViewModel mapViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        mapViewModel.getDeviceLatLng().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(@Nullable final LatLng latLng) {
                Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
                //カメラ移動、縮尺調整
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                googleMap.setOnCameraIdleListener(MapActivity.this);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
        //地図画面に表示する投稿リストを取得したら実行
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postList) {
                Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
                for (PostBean postBean : postList) {
                    mGoogleMap.addMarker(new MarkerOptions()
                            .title(postBean.getMessage())
                            .position(postBean.getLatLng()));
                }
            }
        });

        // SupportMapFragmentを取得し、マップが使用可能になったら通知を受けることができる
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available. This callback is triggered when the map is ready to be
     * used. This is where we can add markers or lines, add listeners or move the camera. In this
     * case, we just add a marker near Sydney, Australia. If Google Play services is not installed
     * on the device, the user will be prompted to install it inside the SupportMapFragment. This
     * method will only be triggered once the user has installed Google Play services and returned
     * to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        this.googleMap = googleMap;
        if (checkPermission()) {
            //現在地取得
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mapViewModel.getDeviceLocation(fusedLocationClient);
        } else {
            requestLocationPermission();
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    public void onCameraIdle() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        VisibleRegion visibleRegion = mMapViewModel.fetchVisibleRegion(mGoogleMap);
        mPostViewModel.fetchMapPostList(visibleRegion);
    }

    //↓↓↓↓↓↓↓↓↓↓位置情報取得のパーミッション関係↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓//
    public boolean checkPermission() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.i(LogUtil.getClassName(), "checkPermission:True");
            return true;
        } else {
            // 拒否していた場合(初回起動も含めて)
            Log.i(LogUtil.getClassName(), "checkPermission:False");
            return false;
        }
    }

    // 許可を求める
    private void requestLocationPermission() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
    }
}
