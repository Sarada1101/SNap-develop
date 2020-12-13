package com.example.snap_develop.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityUserupdateBinding;
import com.example.snap_develop.viewModel.UserViewModel;

import java.io.IOException;

import timber.log.Timber;

public class UserUpdateActivity extends AppCompatActivity implements View.OnClickListener {

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

        mBinding.updateIconImageButton.setOnClickListener(this);
        mBinding.updateButton.setOnClickListener(this);
        mBinding.signoutButton.setOnClickListener(this);

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
    }


    private void updateUser() {
        Timber.i(MyDebugTree.START_LOG);
        //ユーザー情報を更新する処理
        String name = mBinding.updateNameTextInputEditText.getText().toString();
        String profile = mBinding.updateProfileTextInputEditText.getText().toString();

        if (!validateForm(name, profile)) {
            return;
        }

        UserBean updateBean = new UserBean();
        updateBean.setUid(mUid);
        updateBean.setName(name);
        updateBean.setMessage(profile);
        updateBean.setIcon(mBitMap);
        updateBean.setIconName(mIconName);

        mUserViewModel.updateUser(updateBean);
    }


    private boolean validateForm(String name, String profile) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "name", name, "profile", profile));
        boolean isValidSuccess = false;

        final int MAX_NAME_LENGTH = 30;
        if (TextUtils.isEmpty(name)) {
            mBinding.updateNameTextInputEditText.setError("ユーザー名を入力してください");
        } else if (name.length() > MAX_NAME_LENGTH) {
            mBinding.updateNameTextInputEditText.setError("ユーザー名は30文字以内にしてください");
        } else {
            mBinding.updateNameTextInputEditText.setError(null);
            isValidSuccess = true;
        }

        final int MAX_PROFILE_LENGTH = 200;
        if (TextUtils.isEmpty(profile)) {
            mBinding.updateProfileTextInputEditText.setError("プロフィールを入力してください");
        } else if (name.length() > MAX_PROFILE_LENGTH) {
            mBinding.updateNameTextInputEditText.setError("プロフィールは200文字以内にしてください");
        } else {
            mBinding.updateProfileTextInputEditText.setError(null);
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
        if (resultCode == Activity.RESULT_OK) {
            Timber.i("画像取得");
            if (data != null) {
                Uri uri = data.getData();
                mIconName = uri.getLastPathSegment();
                try {
                    mBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mBinding.updateIconImageButton.setImageBitmap(mBitMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        } else if (i == R.id.timelineImageButton) {
            startActivity(new Intent(getApplication(), TimelineActivity.class));
        } else if (i == R.id.mapImageButton) {
            startActivity(new Intent(getApplication(), MapActivity.class));
        } else if (i == R.id.userImageButton) {
            startActivity(new Intent(getApplication(), UserActivity.class));
        } else if (i == R.id.signoutButton) {
            mUserViewModel.signOut();
            startActivity(new Intent(getApplication(), AuthActivity.class));
        }
    }
}
