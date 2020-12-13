package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.adapter.FollowListAdapter;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityFollowingListBinding;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.List;

import timber.log.Timber;

public class FollowerListActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    FollowViewModel mFollowViewModel;
    UserViewModel mUserViewModel;
    String mUid;
    ListView mListView;
    FollowListAdapter mFollowListAdapter;
    private ActivityFollowingListBinding mBinding;
    private List<UserBean> mFollowList;

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

        mUid = mUserViewModel.getCurrentUser().getUid();

        // フォロワーリストを取得したら
        mFollowViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "followList", followList));

                mFollowList = followList;
                mFollowListAdapter = new FollowListAdapter(FollowerListActivity.this, followList,
                        R.layout.activity_follow_list_row);
                mListView = mBinding.followingListView;
                mListView.setAdapter(mFollowListAdapter);
                mListView.setOnItemClickListener(FollowerListActivity.this);
            }
        });
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserBean userBean = mFollowList.get(position);
        startActivity(new Intent(getApplication(), UserActivity.class).putExtra("uid", userBean.getUid()));
    }
}
