package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ListView mListView;
    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityUserBinding mBinding;
    private UserAdapter mUserAdapter;
    private String currentId;
    private String displayUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

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
        mBinding.profileUpdateButton.setOnClickListener(this);

        //現在ログイン中のユーザーID取得
        currentId = mUserViewModel.getCurrentUser().getUid();

        // 他人のユーザー情報を表示する時（uidがIntentに設定されている時）
        displayUserId = getIntent().getStringExtra("uid");
        if (displayUserId == null) {
            // 自分のユーザー情報を表示する時（uidがIntentに設定されていない時）
            displayUserId = currentId;
            mBinding.followRequestButton.setText(R.string.approvalbutten);
        }
        Timber.i(String.format("%s=%s", "uid", displayUserId));

        // ユーザー情報を取得したら投稿リストを取得する
        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                mPostViewModel.fetchPostList(displayUserId);
            }
        });
        mUserViewModel.fetchUserInfo(displayUserId);

        // 投稿リストを取得したらリストを表示する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postList) {
                ArrayList<PostBean> dataList = new ArrayList<>();
                //投稿だけを表示させる
                for (PostBean bean : postList) {
                    if (bean.getType().equals("post")) {
                        dataList.add(bean);
                    }
                }
                mUserAdapter = new UserAdapter(UserActivity.this, dataList, mUserViewModel.getUser().getValue(),
                        R.layout.list_user);
                mListView = mBinding.postList;
                mListView.setAdapter(mUserAdapter);
            }
        });
        mBinding.setUserViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(this);
    }

    //フォロー申請ボタンが押されたときに動くメソッド
    public void followRequest() {

        mFollowViewModel.insertApprovalPendingFollow(displayUserId, currentId);
        mFollowViewModel.insertApplicatedFollow(displayUserId, currentId);

        Toast.makeText(this, "フォローリクエストしました！", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        if (i == R.id.profileUpdateButton) {
            if (currentId != null) {
                startActivity(new Intent(getApplicationContext(), UserUpdateActivity.class)
                        .putExtra("currentId", currentId)
                );
            }
        } else if (i == R.id.timelineImageButton) {
            startActivity(new Intent(UserActivity.this, TimelineActivity.class));
        } else if (i == R.id.mapImageButton) {
            startActivity(new Intent(UserActivity.this, MapActivity.class));
        } else if (i == R.id.userImageButton) {
            startActivity(new Intent(UserActivity.this, UserActivity.class));
        } else if (i == R.id.followerButton) {
            startActivity(new Intent(UserActivity.this, FollowingListActivity.class));
        } else if (i == R.id.followingButton) {
            startActivity(new Intent(UserActivity.this, FollowerListActivity.class));
        } else if (i == R.id.profileUpdateButton) {
            startActivity(new Intent(UserActivity.this, UserUpdateActivity.class));
        } else if (i == R.id.followRequestButton) {
            TextView textView = findViewById(i);
            if (textView.getText().toString() == getString(R.string.approvalbutten)) {
                startActivity(new Intent(UserActivity.this, ApprovalPendingFollowListActivity.class));
            } else {
                followRequest();
            }
        }
    }
}
