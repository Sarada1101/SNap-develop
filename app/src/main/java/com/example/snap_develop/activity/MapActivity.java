package com.example.snap_develop.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.MapViewModel;
import com.example.snap_develop.viewModel.PostViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.List;

import javax.annotation.Nullable;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnCameraIdleListener {

    private GoogleMap mGoogleMap;
    private MapViewModel mMapViewModel;
    private PostViewModel mPostViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        //デバイスの現在地を取得したら実行
        mMapViewModel.getDeviceLatLng().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(@Nullable final LatLng latLng) {
                Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
                //カメラ移動、縮尺調整
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                mGoogleMap.setOnCameraIdleListener(MapActivity.this);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });

        //地図画面に表示する投稿リストを取得したら実行
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postList) {
                Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
                for (PostBean postBean : postList) {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .title(postBean.getMessage())
                            .position(postBean.getLatLng()));
                    marker.setTag(postBean.getPostPath());
                }
            }
        });

        // SupportMapFragmentを取得し、マップが使用可能になったら通知を受けることができる
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /*
    使用可能になったらマップを操作します。
    このコールバックは、マップが使用可能な状態になったときにトリガーされます。
    ここでは、マーカーや線を追加したり、リスナーを追加したり、カメラを動かしたりすることができます。
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        this.mGoogleMap = googleMap;
        if (checkPermission()) {
            //現在地取得
            FusedLocationProviderClient fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);
            mMapViewModel.fetchDeviceLocation(fusedLocationClient);
        } else {
            requestLocationPermission();
        }
        //自分の位置をMapに表示する
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        return false;
    }


    public void onCameraIdle() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        VisibleRegion visibleRegion = mMapViewModel.fetchVisibleRegion(mGoogleMap);
        mPostViewModel.fetchMapPostList(visibleRegion);
    }


    //----------位置情報取得のパーミッション関係----------//
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
        int REQUEST_PERMISSION = 1000;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
    }
}
