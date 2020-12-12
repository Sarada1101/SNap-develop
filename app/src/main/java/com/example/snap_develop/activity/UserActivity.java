package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.adapter.UserAdapter;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityUserBinding;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ListView mListView;
    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private ActivityUserBinding mBinding;
    private UserAdapter mUserAdapter;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("ユーザー情報");

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user);

        mBinding.followingButton.setOnClickListener(this);
        mBinding.followerButton.setOnClickListener(this);
        mBinding.timelineImageButton.setOnClickListener(this);
        mBinding.mapImageButton.setOnClickListener(this);
        mBinding.userImageButton.setOnClickListener(this);
        mBinding.followRequestButton.setOnClickListener(this);

        // 他人のユーザー情報を表示する時（uidがIntentに設定されている時）
        mUid = getIntent().getStringExtra("uid");
        if (mUid == null) {
            // 自分のユーザー情報を表示する時（uidがIntentに設定されていない時）
            mUid = mUserViewModel.getCurrentUser().getUid();
        }
        Timber.i(String.format("%s=%s", "mUid", mUid));

        // ユーザー情報を取得したら投稿リストを取得する
        final String finalUid = mUid;
        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                mPostViewModel.fetchPostList(finalUid);
            }
        });
        mUserViewModel.fetchUserInfo(mUid);

        // 投稿リストを取得したらリストを表示する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postList) {
                ArrayList<PostBean> dataList = (ArrayList<PostBean>) postList;
                mUserAdapter = new UserAdapter(UserActivity.this, dataList, mUserViewModel.getUser().getValue(),
                        R.layout.list_user);
                mListView = mBinding.postList;
                mListView.setAdapter(mUserAdapter);
            }
        });
        mBinding.setUserViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "menu", menu));
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "item", item));
        int id = item.getItemId();
        String currentId = mUserViewModel.getCurrentUser().getUid();
        if (id == R.id.update_user) {
            startActivity(new Intent(this, UserUpdateActivity.class));
        } else if (id == R.id.setting) {
            startActivity(new Intent(this, SettingActivity.class));
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.timelineImageButton) {
            startActivity(new Intent(UserActivity.this, TimelineActivity.class));
        } else if (i == R.id.mapImageButton) {
            startActivity(new Intent(UserActivity.this, MapActivity.class));
        } else if (i == R.id.userImageButton) {
            startActivity(new Intent(UserActivity.this, UserActivity.class));
        } else if (i == R.id.followerButton) {
            startActivity(new Intent(UserActivity.this, FollowerListActivity.class).putExtra("uid", mUid));
        } else if (i == R.id.followingButton) {
            startActivity(new Intent(UserActivity.this, FollowingListActivity.class).putExtra("uid", mUid));
        } else if (i == R.id.followRequestButton) {
            startActivity(new Intent(UserActivity.this, ApprovalPendingFollowListActivity.class));
        }
    }
}
