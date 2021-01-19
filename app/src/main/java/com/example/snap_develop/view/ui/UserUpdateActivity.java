package com.example.snap_develop.view.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityUserupdateBinding;
import com.example.snap_develop.view_model.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.Objects;

import timber.log.Timber;

public class UserUpdateActivity extends AppCompatActivity implements View.OnClickListener,
        TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private ActivityUserupdateBinding mBinding;
    private String mUid;
    private String mIconName;
    private Bitmap mBitMap;
    private static final int REQUEST_GALLERY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userupdate);
        setTitle("ユーザー情報更新");

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_userupdate);

        TabLayout.Tab tabAt = Objects.requireNonNull(mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS));
        tabAt.select();
        Objects.requireNonNull(tabAt.getIcon()).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        mBinding.updateIconImageButton.setOnClickListener(this);
        mBinding.updateButton.setOnClickListener(this);
        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        //ユーザー情報の変化が完了した後の処理
        mUserViewModel.getUpdateResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String updateResult) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "updateResult", updateResult));
                Toast.makeText(UserUpdateActivity.this, "ユーザー情報を更新しました", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplication(), UserActivity.class));
            }
        });

        mUid = mUserViewModel.getCurrentUser().getUid();
        mUserViewModel.fetchUserInfo(mUid);
        mBinding.setUserViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(this);
    }


    private void updateUser() {
        Timber.i(MyDebugTree.START_LOG);
        //ユーザー情報を更新する処理
        String name = Objects.requireNonNull(mBinding.updateNameTextInputEditText.getText()).toString();
        String profile = Objects.requireNonNull(mBinding.updateProfileTextInputEditText.getText()).toString();

        if (!validateForm(name, profile)) {
            return;
        }

        UserBean updateBean = new UserBean();
        updateBean.setUid(mUid);
        updateBean.setName(name);
        updateBean.setMessage(profile);
        if (mBitMap == null) {
            mBitMap = Objects.requireNonNull(mUserViewModel.getUser().getValue()).getIcon();
            mIconName = mUserViewModel.getUser().getValue().getIconName();
        }
        updateBean.setIcon(mBitMap);
        updateBean.setIconName(mIconName);

        mUserViewModel.updateUser(updateBean);
    }


    private boolean validateForm(String name, String profile) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "name", name, "profile", profile));
        boolean isValidName;
        boolean isValidProfile;

        final int MAX_NAME_LENGTH = 15;
        if (TextUtils.isEmpty(name)) {
            mBinding.updateNameTextInputLayout.setError("ユーザー名を入力してください");
            isValidName = false;
        } else if (name.length() > MAX_NAME_LENGTH) {
            mBinding.updateNameTextInputLayout.setError("ユーザー名は15文字以内にしてください");
            isValidName = false;
        } else {
            mBinding.updateNameTextInputLayout.setError(null);
            isValidName = true;
        }

        final int MAX_PROFILE_LENGTH = 100;
        if (TextUtils.isEmpty(profile)) {
            mBinding.updateProfileTextInputLayout.setError("プロフィールを入力してください");
            isValidProfile = false;
        } else if (profile.length() > MAX_PROFILE_LENGTH) {
            mBinding.updateProfileTextInputLayout.setError("プロフィールは100文字以内にしてください");
            isValidProfile = false;
        } else {
            mBinding.updateProfileTextInputLayout.setError(null);
            isValidProfile = true;
        }

        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidName", isValidName));
        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidProfile", isValidProfile));
        return isValidName && isValidProfile;
    }


    private void pickPhoto() {
        Timber.i(MyDebugTree.START_LOG);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @javax.annotation.Nullable Intent data) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(
                String.format("%s %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "requestCode", requestCode,
                        "resultCode",
                        resultCode, "Intent", data));

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_GALLERY) {
            return;
        }
        if (data != null) {
            Uri uri = data.getData();
            mIconName = uri.getLastPathSegment();
            try {
                mBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mBinding.updateIconImageButton.setImageBitmap(mBitMap);
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
        if (i == R.id.updateIconImageButton) {
            pickPhoto();
        } else if (i == R.id.updateButton) {
            updateUser();
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
