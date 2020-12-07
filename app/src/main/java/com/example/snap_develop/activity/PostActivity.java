package com.example.snap_develop.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.databinding.ActivityPostBinding;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.MapViewModel;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Nullable;

import timber.log.Timber;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    FusedLocationProviderClient mFusedLocationClient;
    private PostViewModel mPostViewModel;
    private MapViewModel mapViewModel;
    private ActivityPostBinding mBinding;
    private static final int REQUEST_GALLERY = 0;
    private String mPhotoName = "";
    Bitmap mBitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        mBinding.postFloatingActionButton.setOnClickListener(this);
        mBinding.photoImageButton.setOnClickListener(this);

        mapViewModel.getDeviceLatLng().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(@Nullable final LatLng latLng) {
                Log.d(LogUtil.getClassName(), LogUtil.getLogMessage());
                FirebaseUser firebaseUser = new ViewModelProvider(PostActivity.this).get(
                        UserViewModel.class).getCurrentUser();
                PostBean postBean = new PostBean();
                postBean.setMessage(String.valueOf(mBinding.postTextMultiLine.getText()));
                postBean.setPhotoName(mPhotoName);
                postBean.setPhoto(mBitMap);
                postBean.setLatLng(latLng);
                postBean.setDatetime(new Date());
                postBean.setAnonymous(mBinding.anonymousSwitch.isChecked());
                postBean.setDanger(mBinding.dangerSwitch.isChecked());
                postBean.setUid(firebaseUser.getUid());
                postBean.setType("post");

                mPostViewModel.insertPost(postBean);
            }
        });
    }

    private void insertPost() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapViewModel.fetchDeviceLocation(mFusedLocationClient);
    }

    private void pickPhoto() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_GALLERY) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            Timber.i("画像取得");
            if (data != null) {
                Uri uri = data.getData();
                mPhotoName = uri.getLastPathSegment();
                try {
                    mBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mBinding.photoImageButton.setImageBitmap(mBitMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.postFloatingActionButton) {
            insertPost();
        } else if (i == R.id.photoImageButton) {
            pickPhoto();
        }
    }
}
