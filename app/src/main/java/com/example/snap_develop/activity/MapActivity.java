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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.MapViewModel;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener {


    private GoogleMap mMap;
    private MapViewModel mapViewModel = new MapViewModel();
    public LatLng deviceLocation = new LatLng(0, 0);
    FusedLocationProviderClient fusedLocationClient;
    private final int REQUEST_PERMISSION = 1000;
    AlertDialog firstAlert;

    private static final String TAG = MapActivity.class.toString();
    private GoogleMap googleMap;
    private MapViewModel mapViewModel;

    //private ActivityMapBinding mBinding = DataBindingUtil.setContentView(this,R.layout
    // .activity_map);
    //private final int REQUEST_PERMISSION = 1000;
    //private FusedLocationProviderClient fusedLocationClient;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
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

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        // SupportMapFragmentを取得し、マップが使用可能になったら通知を受けることができる

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
    public void onMapReady(GoogleMap googleMap) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        this.googleMap = googleMap;
        //checkPermission();

        LatLng sampleLocation = new LatLng(33.590188, 130.420685);
        LatLng school = new LatLng(33.583422, 130.421152);
        //マーカー設置
        this.googleMap.addMarker(new MarkerOptions().position(sampleLocation).title("現在地"));
        this.googleMap.addMarker(new MarkerOptions().position(school).title("麻生情報ビジネス専門学校"));

        //カメラ移動、縮尺調整
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sampleLocation, 16.0f));
        this.googleMap.setOnCameraIdleListener(this);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
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
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        double[] centerLatLon = mapViewModel.getCenter(googleMap);
        int radius = mapViewModel.getRadius(googleMap);
    }

    //↓↓↓↓↓↓↓↓↓↓位置情報取得のパーミッション関係↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓//
    /*public void checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            System.out.println("---------------checkPermission:True-----------------");

            return true;

            deviceLocation = mapModel.fetchDeviceLocation(fusedLocationClient);
        }
        // 拒否していた場合(初回起動も含めて)
        else {
            System.out.println("---------------checkPermission:False-----------------");

            return false;
            requestLocationPermission();
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
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            System.out.println("---------------requestLocationPermission:True-----------------");
            deviceLocation = mapModel.fetchDeviceLocation(fusedLocationClient);

            new AlertDialog.Builder(this)
                    .setTitle("title")
                    .setMessage("message")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                    .setNegativeButton("Cancel", null)
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

        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println(
                        "---------------onRequestPermissionsResult:True-----------------");
                deviceLocation = mapModel.fetchDeviceLocation(fusedLocationClient);


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

                new AlertDialog.Builder(this)
                        .setTitle("title")
                        .setMessage("message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Intent でアプリ権限の設定画面に移行
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                // BuildConfigは反映するのに時間がかかってエラーにることもある。待つ
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        }
    }*/
}
