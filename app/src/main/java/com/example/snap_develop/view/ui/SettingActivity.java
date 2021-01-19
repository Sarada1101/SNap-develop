package com.example.snap_develop.view.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivitySettingBinding;
import com.example.snap_develop.view_model.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import timber.log.Timber;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,
        TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private ActivitySettingBinding mBinding;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("設定");

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        TabLayout.Tab tabAt = Objects.requireNonNull(mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS));
        tabAt.select();
        Objects.requireNonNull(tabAt.getIcon()).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        mBinding.settingUpdateButton.setOnClickListener(this);
        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean));

                switch (userBean.getPublicationArea()) {
                    case "public":
                        mBinding.publicRadioButton.setChecked(true);
                        break;
                    case "followPublic":
                        mBinding.followPublicRadioButton.setChecked(true);
                        break;
                    case "anonymous":
                        mBinding.anonymousRadioButton.setChecked(true);
                        break;
                }
                mBinding.followSwitch.setChecked(userBean.isFollowNotice());
                mBinding.goodSwitch.setChecked(userBean.isGoodNotice());
                mBinding.commentSwitch.setChecked(userBean.isCommentNotice());
            }
        });

        mUid = mUserViewModel.getCurrentUser().getUid();
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
        } else if (checkedRadioButtonId == R.id.followPublicRadioButton) {
            publication = "followPublic";
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
        if (i == R.id.settingUpdateButton) {
            updateSetting();
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
