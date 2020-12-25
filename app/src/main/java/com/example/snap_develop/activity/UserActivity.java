package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityUserBinding;
import com.example.snap_develop.view.adapter.UserAdapter;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.List;

import timber.log.Timber;

public class UserActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityUserBinding mBinding;
    private UserAdapter mUserAdapter;
    private ListView mListView;
    private List<PostBean> mPostBeanList;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("ユーザー情報");

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user);

        mBinding.followingButton.setOnClickListener(this);
        mBinding.followerButton.setOnClickListener(this);
        mBinding.timelineImageButton.setOnClickListener(this);
        mBinding.mapImageButton.setOnClickListener(this);
        mBinding.userImageButton.setOnClickListener(this);
        mBinding.followRequestButton.setOnClickListener(this);

        // ユーザー情報を取得したら投稿リストを取得する
        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean));
                mPostViewModel.fetchPostList(mUid);
            }
        });

        // 投稿リストを取得したらリストを表示する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postList", postList));
                mPostBeanList = postList;
                mUserAdapter = new UserAdapter(UserActivity.this, postList, mUserViewModel.getUser().getValue(),
                        R.layout.activity_user_list_row);
                mListView = mBinding.postListView;
                mListView.setAdapter(mUserAdapter);
                mListView.setOnItemClickListener(UserActivity.this);
            }
        });

        mFollowViewModel.getFollowing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "aBoolean", aBoolean));
                if (aBoolean) {
                    mBinding.followRequestButton.setText("フォロー済み");
                    mBinding.followRequestButton.setOnClickListener(null);
                }
            }
        });

        mFollowViewModel.getApprovalPendingFollow().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "aBoolean", aBoolean));
                if (aBoolean) {
                    mBinding.followRequestButton.setText("申請済み");
                    mBinding.followRequestButton.setOnClickListener(null);
                }
            }
        });

        // 他人のユーザー情報を表示する時（uidがIntentに設定されている時）
        mUid = getIntent().getStringExtra("uid");
        if (mUid == null || mUid.equals(mUserViewModel.getCurrentUser().getUid())) {
            // 自分のユーザー情報を表示する時（uidがIntentに設定されていない時）
            mUid = mUserViewModel.getCurrentUser().getUid();
            mBinding.followRequestButton.setText("フォロー申請/承認");
        }
        Timber.i(String.format("%s=%s", "mUid", mUid));

        mUserViewModel.fetchUserInfo(mUid);

        if (!mUserViewModel.getCurrentUser().getUid().equals(mUid)) {
            Timber.d("check");
            mFollowViewModel.checkFollowing(mUserViewModel.getCurrentUser().getUid(), mUid);
            mFollowViewModel.checkApprovalPendingFollow(mUserViewModel.getCurrentUser().getUid(), mUid);
        }

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
        if (id == R.id.update_user) {
            startActivity(new Intent(this, UserUpdateActivity.class));
        } else if (id == R.id.setting) {
            startActivity(new Intent(this, SettingActivity.class));
        }
        return true;
    }


    public void followRequest(String uid, String myUid) {
        mFollowViewModel.insertApprovalPendingFollow(myUid, uid);
        mFollowViewModel.insertApplicatedFollow(uid, myUid);
        mBinding.followRequestButton.setText("申請済み");
        // クリックリスナー解除
        mBinding.followRequestButton.setOnClickListener(null);
        Toast.makeText(this, "フォロー申請しました！", Toast.LENGTH_LONG).show();
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
            // 表示されている情報がログインしているユーザーの情報なら
            if (mUserViewModel.getCurrentUser().getUid().equals(mUid)) {
                startActivity(new Intent(UserActivity.this, ApprovalPendingFollowListActivity.class));
            } else {
                followRequest(mUid, mUserViewModel.getCurrentUser().getUid());
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PostBean postBean = mPostBeanList.get(position);
        Intent intent = new Intent(getApplication(), DisplayCommentActivity.class);

        if (postBean.getType().equals("post")) {
            intent.putExtra("postPath", postBean.getPostPath());
        } else if (postBean.getType().equals("comment")) {
            intent.putExtra("postPath", postBean.getParentPost());
        }
        startActivity(intent);
    }
}
