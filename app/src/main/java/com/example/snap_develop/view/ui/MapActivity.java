package com.example.snap_develop.view.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.view_model.MapViewModel;
import com.example.snap_develop.view_model.PostViewModel;
import com.example.snap_develop.view_model.UserViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import timber.log.Timber;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnInfoWindowClickListener,
        View.OnClickListener,
        TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private MapViewModel mMapViewModel;
    private GoogleMap mGoogleMap;
    private final static int REQUEST_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        TabLayout.Tab tabAt = Objects.requireNonNull(((TabLayout) findViewById(R.id.buttonTabLayout)).getTabAt(
                MainApplication.MAP_POS));
        tabAt.select();
        Objects.requireNonNull(tabAt.getIcon()).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        findViewById(R.id.postMapFloatingActionButton).setOnClickListener(this);
        findViewById(R.id.searchMapFloatingActionButton).setOnClickListener(this);
        ((TabLayout) findViewById(R.id.buttonTabLayout)).addOnTabSelectedListener(this);

        // SupportMapFragmentを取得し、マップが使用可能になったら通知を受けることができる
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //デバイスの現在地を取得したら実行
        mMapViewModel.getDeviceLatLng().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(@Nullable final LatLng latLng) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "latLng", latLng));
                //カメラ移動、縮尺調整
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                mGoogleMap.setOnCameraIdleListener(MapActivity.this);
            }
        });

        //地図画面に表示する投稿リストを取得したら実行
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postList", postList));
                for (PostBean postBean : postList) {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .title(postBean.getMessage())
                            .position(postBean.getLatLng()));
                    marker.setTag(postBean.getPostPath());
                }
            }
        });
    }


    /*
    使用可能になったらマップを操作します。
    このコールバックは、マップが使用可能な状態になったときにトリガーされます。
    ここでは、マーカーや線を追加したり、リスナーを追加したり、カメラを動かしたりすることができます。
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "googleMap", googleMap));
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (checkPermission()) {
            //現在地取得
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mMapViewModel.fetchDeviceLocation(fusedLocationClient);
            //自分の位置をMapに表示する
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            requestLocationPermission();
        }
        //マーカーのウィンドウにClickListener
        mGoogleMap.setOnInfoWindowClickListener(this);
    }


    public void onCameraIdle() {
        Timber.i(MyDebugTree.START_LOG);
        VisibleRegion visibleRegion = mMapViewModel.fetchVisibleRegion(mGoogleMap);
        mPostViewModel.fetchMapPostList(visibleRegion);
    }


    //----------位置情報取得のパーミッション関係----------//
    public boolean checkPermission() {
        Timber.i(MyDebugTree.START_LOG);
        // 既に許可している
        // 拒否していた場合(初回起動も含めて)
        boolean isChecked = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "isChecked", isChecked));
        return isChecked;
    }


    // 許可を求める
    private void requestLocationPermission() {
        Timber.i(MyDebugTree.START_LOG);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                REQUEST_PERMISSION);
    }


    // 結果の受け取り
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        Timber.i(MyDebugTree.START_LOG);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.i("onRequestPermissionsResult:True");

                // 使用が許可された時の対応
                //現在地取得
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                mMapViewModel.fetchDeviceLocation(fusedLocationClient);
                //自分の位置をMapに表示する
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                Timber.i("onRequestPermissionsResult:False");
            }
        }
    }


    // マーカーのウィンドウをタップ時のイベント
    @Override
    public void onInfoWindowClick(Marker marker) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "marker", marker));

        String postPath = Objects.requireNonNull(marker.getTag()).toString();
        startActivity(new Intent(getApplication(), DisplayCommentActivity.class).putExtra("postPath", postPath));
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.postMapFloatingActionButton) {
            if (mUserViewModel.getCurrentUser() == null) {
                startActivity(new Intent(getApplication(), AuthActivity.class));
            } else {
                startActivity(new Intent(getApplication(), PostActivity.class));
            }
        } else if (i == R.id.searchMapFloatingActionButton) {
            startActivity(new Intent(getApplication(), PostSearchActivity.class));
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Timber.i(MyDebugTree.START_LOG);
        switch (tab.getPosition()) {
            case MainApplication.TIMELINE_POS:
                if (mUserViewModel.getCurrentUser() == null) {
                    startActivity(new Intent(getApplication(), AuthActivity.class));
                } else {
                    startActivity(new Intent(getApplication(), TimelineActivity.class));
                }
                break;
            case MainApplication.MAP_POS:
                if (mUserViewModel.getCurrentUser() == null) {
                    startActivity(new Intent(getApplication(), AuthActivity.class));
                } else {
                    startActivity(new Intent(getApplication(), MapActivity.class));
                }
                break;
            case MainApplication.USER_POS:
                if (mUserViewModel.getCurrentUser() == null) {
                    startActivity(new Intent(getApplication(), AuthActivity.class));
                } else {
                    startActivity(new Intent(getApplication(), UserActivity.class));
                }
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Timber.i(MyDebugTree.START_LOG);
        switch (tab.getPosition()) {
            case MainApplication.TIMELINE_POS:
                startActivity(new Intent(getApplication(), TimelineActivity.class));
                break;
            case MainApplication.MAP_POS:
                startActivity(new Intent(getApplication(), MapActivity.class));
                break;
            case MainApplication.USER_POS:
                startActivity(new Intent(getApplication(), UserActivity.class));
                break;
        }
    }
}
