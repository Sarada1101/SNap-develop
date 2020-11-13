package com.example.snap_develop.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.example.snap_develop.R;
import com.example.snap_develop.viewModel.MapViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private MapViewModel mapViewModel = new MapViewModel();
    public LatLng deviceLocation = new LatLng(0, 0);
    FusedLocationProviderClient fusedLocationClient;
    private final int REQUEST_PERMISSION = 1000;
    AlertDialog firstAlert;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapViewModel.getDeviceLocationResult().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                deviceLocation = latLng;
                System.out.println(deviceLocation);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        System.out.println("-------------------------onMapReady---------------------------");
        mMap = googleMap;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!checkPermission()) {
            requestLocationPermission();
        } else {
            //現在地取得
            mapViewModel.getDeviceLocation(fusedLocationClient, mMap);
        }

        mMap.setOnCameraIdleListener(this);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onCameraIdle() {
        System.out.println("-------------------------onCameraIdle---------------------------");
        double[] centerInfo = mapViewModel.getCenter(mMap);
        System.out.println("中心座標＞＞＞X：" + centerInfo[0] + "、Y：" + centerInfo[1]);

        mapViewModel.getRadius(mMap);
    }

    //↓↓↓↓↓↓↓↓↓↓位置情報取得のパーミッション関係↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓//
    public boolean checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            System.out.println("---------------checkPermission:True-----------------");

            return true;

        }
        // 拒否していた場合(初回起動も含めて)
        else {
            System.out.println("---------------checkPermission:False-----------------");

            return false;
        }
    }

    // 許可を求める
    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            System.out.println("---------------requestLocationPermission:True-----------------");


            firstAlert = new AlertDialog.Builder(this)
                    .setTitle("現在地情報")
                    .setMessage("アプリに現在地情報へのアクセスを許可しますか？")
                    .setPositiveButton("許可", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MapActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSION);
                        }
                    })
                    .setNegativeButton("拒否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startFinalAlert();
                        }
                    })
                    .show();
        } else {
            System.out.println("---------------requestLocationPermission:False-----------------");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    REQUEST_PERMISSION);

        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        firstAlert.dismiss();

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println(
                        "---------------onRequestPermissionsResult:True-----------------");

                // 使用が許可された時の対応
                mapViewModel.getDeviceLocation(fusedLocationClient, mMap);
            } else {
                System.out.println(
                        "---------------onRequestPermissionsResult:False-----------------");

                // 拒否された時の対応
                startFinalAlert();
            }
        }
    }

    //最終確認のアラート
    public void startFinalAlert() {
        new AlertDialog.Builder(this)
                .setTitle("最終確認")
                .setMessage("アプリに現在地情報へのアクセスを許可しなければ爆発します\n許可しますよね？")
                .setPositiveButton("許可", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ActivityCompat.requestPermissions(MapActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSION);
                    }
                })
                .setNegativeButton("拒否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapActivity.this, "爆発します", Toast.LENGTH_LONG).show();
                        MapActivity.this.finish();
                        MapActivity.this.moveTaskToBack(true);
                    }
                })
                .show();
    }

}
