package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityFollowingListBinding;
import com.example.snap_develop.view.adapter.FollowListAdapter;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.List;

import timber.log.Timber;

public class FollowerListActivity extends AppCompatActivity implements View.OnClickListener {

    private UserViewModel mUserViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityFollowingListBinding mBinding;
    private FollowListAdapter mFollowListAdapter;
    private RecyclerView mRecyclerView;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list);
        setTitle("フォロワー");

        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_following_list);

        mBinding.timelineImageButton.setOnClickListener(this);
        mBinding.mapImageButton.setOnClickListener(this);
        mBinding.userImageButton.setOnClickListener(this);

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
        mUid = getIntent().getStringExtra("uid");
        if (mUid == null || mUid.equals(mUserViewModel.getCurrentUser().getUid())) {
            // 自分のユーザー情報を表示する時（uidがIntentに設定されていない時）
            mUid = mUserViewModel.getCurrentUser().getUid();
        }
        Timber.i(String.format("%s=%s", "mUid", mUid));

        mFollowViewModel.fetchFollowerList(mUid);
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
        }
    }
}
