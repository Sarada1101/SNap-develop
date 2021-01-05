package com.example.snap_develop.view.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityFollowingListBinding;
import com.example.snap_develop.view.adapter.FollowListAdapter;
import com.example.snap_develop.view_model.FollowViewModel;
import com.example.snap_develop.view_model.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class FollowerListActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    @SuppressWarnings("FieldCanBeLocal")
    private UserViewModel mUserViewModel;
    @SuppressWarnings("FieldCanBeLocal")
    private FollowViewModel mFollowViewModel;
    private ActivityFollowingListBinding mBinding;
    private FollowListAdapter mFollowListAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list);
        setTitle("フォロワー");

        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_following_list);

        TabLayout.Tab tabAt = Objects.requireNonNull(mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS));
        tabAt.select();
        Objects.requireNonNull(tabAt.getIcon()).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        // フォロワーリストを取得したら
        mFollowViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "followList", followList));

                mFollowListAdapter = new FollowListAdapter(FollowerListActivity.this, followList);
                mRecyclerView = mBinding.followingRecyclerView;
                LinearLayoutManager llm = new LinearLayoutManager(FollowerListActivity.this);
                mRecyclerView.setLayoutManager(llm);
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(FollowerListActivity.this,
                        DividerItemDecoration.VERTICAL);
                mRecyclerView.addItemDecoration(itemDecoration);
                mRecyclerView.setAdapter(mFollowListAdapter);
            }
        });

        // 他人のユーザー情報を表示する時（uidがIntentに設定されている時）
        String uid = getIntent().getStringExtra("uid");
        if (uid == null || uid.equals(mUserViewModel.getCurrentUser().getUid())) {
            // 自分のユーザー情報を表示する時（uidがIntentに設定されていない時）
            uid = mUserViewModel.getCurrentUser().getUid();
        }
        Timber.i(String.format("%s=%s", "mUid", uid));

        mFollowViewModel.fetchFollowerList(uid);
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
