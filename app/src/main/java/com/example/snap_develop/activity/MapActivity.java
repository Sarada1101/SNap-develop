package com.example.snap_develop.activity;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.snap_develop.R;
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
    //new ViewModelProvider(this).get(MapViewModel.class);

    //private MapModel mapModel = new MapModel();
    //private ActivityMapBinding mBinding = DataBindingUtil.setContentView(this,R.layout
    // .activity_map);
    //private final int REQUEST_PERMISSION = 1000;
    //private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
        mMap = googleMap;

        //checkPermission();
        System.out.println("-------------------------onMapReady---------------------------");


        LatLng sampleLocation = new LatLng(33.590188, 130.420685);
        LatLng school = new LatLng(33.583422, 130.421152);
        mMap.addMarker(new MarkerOptions().position(sampleLocation).title("現在地"));
        mMap.addMarker(new MarkerOptions().position(school).title("麻生情報ビジネス専門学校"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sampleLocation, 16.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sampleLocation));
        mMap.setOnCameraIdleListener(this);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
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
    /*public void checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            System.out.println("---------------checkPermission:True-----------------");
            deviceLocation = mapModel.fetchDeviceLocation(fusedLocationClient);
        }
        // 拒否していた場合(初回起動も含めて)
        else {
            System.out.println("---------------checkPermission:False-----------------");
            requestLocationPermission();
        }
    }

    // 許可を求める
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