package com.example.snap_develop.view.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
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
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityDisplayCommentBinding;
import com.example.snap_develop.view.adapter.DisplayCommentAdapter;
import com.example.snap_develop.view_model.FollowViewModel;
import com.example.snap_develop.view_model.PostViewModel;
import com.example.snap_develop.view_model.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class DisplayCommentActivity extends AppCompatActivity implements View.OnClickListener,
        TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityDisplayCommentBinding mBinding;
    private DisplayCommentAdapter mDisplayCommentAdapter;
    private RecyclerView mRecyclerView;
    private List<PostBean> mPostBeanList;
    private List<Map<String, Object>> mCommentDataMapList;
    private String mParentPostPath;
    private String mUid;
    private UserBean mUserBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comment);
        setTitle("詳細");

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_display_comment);

        TabLayout.Tab tabAt = Objects.requireNonNull(mBinding.buttonTabLayout.getTabAt(MainApplication.MAP_POS));
        tabAt.select();
        Objects.requireNonNull(tabAt.getIcon()).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        mBinding.goodIcon.setOnClickListener(this);
        mBinding.commentButton.setOnClickListener(this);
        mBinding.userInfoConstraintLayout.setOnClickListener(this);
        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        // 投稿情報を取得したら投稿のユーザー情報を取得する
        mPostViewModel.getPost().observe(this, new Observer<PostBean>() {
            @Override
            public void onChanged(PostBean postBean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s=%s", "postBean", postBean));
                if (postBean.getPhotoName() != null) mBinding.photoImageView.setMaxHeight(300);

                if (postBean.isAnonymous()) {
                    mBinding.iconImageView.setImageBitmap(
                            MainApplication.getBitmapFromVectorDrawable(DisplayCommentActivity.this,
                                    R.drawable.ic_baseline_account_circle_24));
                    mBinding.userNameTextView.setText("匿名");
                    mBinding.userIdTextView.setText("匿名");
                    mBinding.userInfoConstraintLayout.setOnClickListener(null);
                } else {
                    mUserViewModel.fetchUserInfo(postBean.getUid());
                }

                if (postBean.isDanger()) {
                    mBinding.parentPostConstraintLayout.setBackgroundColor(Color.rgb(240, 96, 96));
                }
            }
        });

        // ユーザー情報を取得したらアイコンにクリックリスナーを設定する
        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean));

                mUid = userBean.getUid();

                switch (userBean.getPublicationArea()) {
                    case "anonymous":
                        mBinding.iconImageView.setImageBitmap(
                                MainApplication.getBitmapFromVectorDrawable(DisplayCommentActivity.this,
                                        R.drawable.ic_baseline_account_circle_24));
                        mBinding.userNameTextView.setText("匿名");
                        mBinding.userIdTextView.setText("匿名");
                        mBinding.userInfoConstraintLayout.setOnClickListener(null);
                        break;
                    case "public":
                        mBinding.iconImageView.setImageBitmap(userBean.getIcon());
                        mBinding.userNameTextView.setText(userBean.getName());
                        mBinding.userIdTextView.setText(userBean.getUid());
                        break;
                    case "followPublic":
                        mUserBean = userBean;
                        mFollowViewModel.checkFollowing(mUid, mUserViewModel.getCurrentUser().getUid());
                        break;
                }
            }
        });

        // コメントリストを取得したらコメントごとのユーザー情報を取得する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(List<PostBean> postList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postList", postList));

                List<String> uidList = new ArrayList<>();
                for (final PostBean postBean : postList) {
                    uidList.add(postBean.getUid());
                }
                mUserViewModel.fetchUserInfoList(uidList);

                //投稿を日時順にソート
                class PostSortCompare implements Comparator<PostBean> {
                    @Override
                    public int compare(PostBean o1, PostBean o2) {
                        Date sortKey1 = o1.getDatetime();
                        Date sortKey2 = o2.getDatetime();
                        return sortKey1.compareTo(sortKey2);
                    }
                }
                Collections.sort(postList, new PostSortCompare().reversed());
                mPostBeanList = postList;
            }
        });

        //コメントごとのユーザー情報を取得したらリスト表示する
        mUserViewModel.getUserList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> userList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userList", userList));

                // コメントリストとユーザー情報を紐付ける
                mCommentDataMapList = new ArrayList<>();
                for (PostBean postBean : mPostBeanList) {
                    Map<String, Object> commentDataMap = new HashMap<>();
                    commentDataMap.put("postBean", postBean);
                    for (UserBean userBean : userList) {
                        if (userBean.getUid().equals(postBean.getUid())) {
                            commentDataMap.put("userBean", userBean);
                            break;
                        }
                    }
                    mCommentDataMapList.add(commentDataMap);
                }
                mDisplayCommentAdapter = new DisplayCommentAdapter(DisplayCommentActivity.this, mCommentDataMapList);
                mRecyclerView = mBinding.commentListRecyclerView;
                // setLayoutManager()に渡すLayoutManagerによって，RecyclerViewに1列表示なのか，Grid表示なのかなどを教える
                LinearLayoutManager llm = new LinearLayoutManager(DisplayCommentActivity.this);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setHasFixedSize(true); // リストのコンテンツの大きさがデータによって変わらないならtrue
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(DisplayCommentActivity.this,
                        DividerItemDecoration.VERTICAL);
                mRecyclerView.addItemDecoration(itemDecoration);
                mRecyclerView.setAdapter(mDisplayCommentAdapter);
            }
        });

        mFollowViewModel.getFollowing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "aBoolean", aBoolean));
                if (aBoolean) {
                    mBinding.iconImageView.setImageBitmap(mUserBean.getIcon());
                    mBinding.userNameTextView.setText(mUserBean.getName());
                    mBinding.userIdTextView.setText(mUserBean.getUid());
                } else {
                    mBinding.iconImageView.setImageBitmap(
                            MainApplication.getBitmapFromVectorDrawable(DisplayCommentActivity.this,
                                    R.drawable.ic_baseline_account_circle_24));
                    mBinding.userNameTextView.setText("匿名");
                    mBinding.userIdTextView.setText("匿名");
                    mBinding.userInfoConstraintLayout.setOnClickListener(null);
                }
            }
        });

        // 投稿情報のパスを取得
        mParentPostPath = getIntent().getStringExtra("postPath");
        Timber.i(String.format("%s=%s", "postPath", mParentPostPath));

        mPostViewModel.fetchPost(mParentPostPath);
        mPostViewModel.fetchPostCommentList(mParentPostPath);

        mBinding.setPostViewModel(mPostViewModel);
        mBinding.setUserViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(this);
    }


    @SuppressLint("SetTextI18n")
    private void addGood() {
        int goodCount = Integer.parseInt(mBinding.goodCountTextView.getText().toString());
        mBinding.goodCountTextView.setText(Integer.toString(goodCount + 1));
        mPostViewModel.addGood(mUid, mParentPostPath);
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.commentButton) {
            if (mUserViewModel.getCurrentUser() == null) {
                startActivity(new Intent(getApplication(), AuthActivity.class));
            } else {
                startActivity(
                        new Intent(getApplication(), CommentActivity.class).putExtra("postPath", mParentPostPath));
            }
        } else if (i == R.id.goodIcon) {
            addGood();
        } else if (i == R.id.userInfoConstraintLayout) {
            startActivity(new Intent(getApplication(), UserActivity.class).putExtra("uid", mUid));
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
