package com.example.snap_develop.view.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.databinding.ActivityPostBinding;
import com.example.snap_develop.view_model.MapViewModel;
import com.example.snap_develop.view_model.PostViewModel;
import com.example.snap_develop.view_model.UserViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;

import timber.log.Timber;

public class PostActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private MapViewModel mMapViewModel;
    private ActivityPostBinding mBinding;
    private String mPhotoName = "";
    private Bitmap mBitMap;
    private static final int REQUEST_GALLERY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setTitle("投稿");

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        TabLayout.Tab tabAt = Objects.requireNonNull(mBinding.buttonTabLayout.getTabAt(MainApplication.MAP_POS));
        tabAt.select();
        Objects.requireNonNull(tabAt.getIcon()).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        mBinding.postButton.setOnClickListener(this);
        mBinding.photoImageButton.setOnClickListener(this);
        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        // 端末の位置を取得したら投稿処理をし地図画面に遷移する
        mMapViewModel.getDeviceLatLng().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(@Nullable final LatLng latLng) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "latLng", latLng));

                PostBean postBean = new PostBean();
                postBean.setMessage(String.valueOf(mBinding.postTextInputEditText.getText()));
                postBean.setPhotoName(mPhotoName);
                postBean.setPhoto(mBitMap);
                postBean.setLatLng(latLng);
                postBean.setDatetime(new Date());
                postBean.setAnonymous(mBinding.anonymousSwitch.isChecked());
                postBean.setDanger(mBinding.dangerSwitch.isChecked());
                postBean.setUid(mUserViewModel.getCurrentUser().getUid());
                postBean.setType("post");

                mPostViewModel.insertPost(postBean);
                startActivity(new Intent(getApplication(), MapActivity.class));
            }
        });
    }


    private void insertPost() {
        Timber.i(MyDebugTree.START_LOG);
        String post = Objects.requireNonNull(mBinding.postTextInputEditText.getText()).toString();
        if (!validateForm(post)) {
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mMapViewModel.fetchDeviceLocation(fusedLocationClient);
    }


    private boolean validateForm(String post) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "post", post));
        boolean isValidSuccess = false;

        int MAX_LENGTH = 200;
        if (TextUtils.isEmpty(post)) {
            mBinding.postTextInputLayout.setError("コメントを入力してください");
        } else if (post.length() > MAX_LENGTH) {
            mBinding.postTextInputLayout.setError("コメントは200文字以内にしてください");
        } else {
            mBinding.postTextInputLayout.setError(null);
            isValidSuccess = true;
        }

        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidSuccess", isValidSuccess));
        return isValidSuccess;
    }


    private void pickPhoto() {
        Timber.i(MyDebugTree.START_LOG);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(
                String.format("%s %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "requestCode", requestCode, "resultCode",
                        resultCode, "Intent", data));

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_GALLERY) {
            return;
        }
        if (data != null) {
            Uri uri = data.getData();
            mPhotoName = uri.getLastPathSegment();
            try {
                mBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mBinding.photoImageButton.setImageBitmap(mBitMap);
            } catch (IOException e) {
                Timber.e(e.toString());
            }
        }
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.postButton) {
            insertPost();
        } else if (i == R.id.photoImageButton) {
            pickPhoto();
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
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
