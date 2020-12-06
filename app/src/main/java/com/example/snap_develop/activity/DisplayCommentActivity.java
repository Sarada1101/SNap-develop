package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.adapter.DisplayCommentAdapter;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityDisplayCommentBinding;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class DisplayCommentActivity extends AppCompatActivity implements View.OnClickListener {

    PostViewModel mPostViewModel;
    UserViewModel mUserViewModel;
    ActivityDisplayCommentBinding mBinding;
    ListView lv;
    DisplayCommentAdapter mDisplayCommentAdapter;
    List<PostBean> postDataList;
    String finalPostPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comment);

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_display_comment);

        mBinding.commentButton.setOnClickListener(this);

        String postPath;
        // 投稿情報のパスを取得
        postPath = getIntent().getStringExtra("postPath");
        Timber.i(String.format("%s=%s", "postPath", postPath));

        // 投稿情報を取得したら投稿のユーザー情報を取得する
        finalPostPath = postPath;
        mPostViewModel.getPost().observe(this, new Observer<PostBean>() {
            @Override
            public void onChanged(PostBean postBean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s=%s", "postBean", postBean));
                mUserViewModel.fetchUserInfo(postBean.getUid());
            }
        });
        mPostViewModel.fetchPost(postPath);

        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                mPostViewModel.fetchPostCommentList(finalPostPath);
            }
        });

        // コメントリストを取得したらコメントごとのユーザー情報を取得する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postList) {
                List<String> uidList = new ArrayList<>();
                postDataList = postList;
                for (final PostBean postBean : postList) {
                    uidList.add(postBean.getUid());
                }
                mUserViewModel.fetchUserInfoList(uidList);
            }
        });

        mUserViewModel.getUserList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> userList) {
                mDisplayCommentAdapter = new DisplayCommentAdapter(DisplayCommentActivity.this, (ArrayList<UserBean>) userList
                        , (ArrayList<PostBean>) postDataList, R.layout.activity_display_comment_list);
                lv = findViewById(R.id.postList);
                lv.setAdapter(mDisplayCommentAdapter);
            }
        });

        mBinding.setPostViewModel(mPostViewModel);
        mBinding.setUserViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(this);
    }

    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        if (i == R.id.commentButton) {
            startActivity(new Intent(getApplication(), CommentActivity.class)
                    .putExtra("parentPost", finalPostPath));
        }
    }
}
