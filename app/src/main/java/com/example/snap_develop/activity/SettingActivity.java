package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivitySettingBinding;
import com.example.snap_develop.viewModel.UserViewModel;

import timber.log.Timber;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySettingBinding mBinding;
    private String mUid;
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("設定");

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        mBinding.settingUpdateButton.setOnClickListener(this);
        mBinding.timelineImageButton.setOnClickListener(this);
        mBinding.mapImageButton.setOnClickListener(this);
        mBinding.userImageButton.setOnClickListener(this);

        mUid = mUserViewModel.getCurrentUser().getUid();

        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean));

                if (userBean.getPublicationArea().equals("public")) {
                    mBinding.publicRadioButton.setChecked(true);
                } else if (userBean.getPublicationArea().equals("followerPublic")) {
                    mBinding.followerPublicRadioButton.setChecked(true);
                } else if (userBean.getPublicationArea().equals("anonymous")) {
                    mBinding.anonymousRadioButton.setChecked(true);
                }
                mBinding.followSwitch.setChecked(userBean.isFollowNotice());
                mBinding.goodSwitch.setChecked(userBean.isGoodNotice());
                mBinding.commentSwitch.setChecked(userBean.isCommentNotice());
            }
        });
        mUserViewModel.fetchUserInfo(mUid);
    }


    private void updateSetting() {
        Timber.i(MyDebugTree.START_LOG);

        UserBean userBean = new UserBean();
        userBean.setUid(mUid);

        String publication = "public";
        int checkedRadioButtonId = mBinding.publicationRadioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.publicRadioButton) {
            publication = "public";
        } else if (checkedRadioButtonId == R.id.followerPublicRadioButton) {
            publication = "followerPublic";
        } else if (checkedRadioButtonId == R.id.anonymousRadioButton) {
            publication = "anonymous";
        }

        userBean.setPublicationArea(publication);
        userBean.setFollowNotice(mBinding.followSwitch.isChecked());
        userBean.setGoodNotice(mBinding.goodSwitch.isChecked());
        userBean.setCommentNotice(mBinding.commentSwitch.isChecked());

        mUserViewModel.updateSetting(userBean);
        Toast.makeText(SettingActivity.this, "設定を更新しました", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplication(), UserActivity.class));
    }

    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.timelineImageButton) {
            startActivity(new Intent(getApplication(), TimelineActivity.class));
        } else if (i == R.id.mapImageButton) {
            startActivity(new Intent(getApplication(), MapActivity.class));
        } else if (i == R.id.userImageButton) {
            startActivity(new Intent(getApplication(), UserActivity.class));
        } else if (i == R.id.settingUpdateButton) {
            updateSetting();
        }
    }
}
