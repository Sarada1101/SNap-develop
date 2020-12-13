package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DisplayCommentActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private PostViewModel mPostViewModel;
    private UserViewModel mUserViewModel;
    private ActivityDisplayCommentBinding mBinding;
    private ListView mListView;
    private DisplayCommentAdapter mDisplayCommentAdapter;
    private List<PostBean> mPostBeanList;
    private String mParentPostPath;
    private String mUid;
    List<Map<String, Object>> mCommentDataMapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comment);
        setTitle("詳細");

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_display_comment);

        mBinding.commentButton.setOnClickListener(this);
        mBinding.timelineImageButton.setOnClickListener(this);
        mBinding.mapImageButton.setOnClickListener(this);
        mBinding.userImageButton.setOnClickListener(this);

        // 投稿情報のパスを取得
        mParentPostPath = getIntent().getStringExtra("postPath");
        Timber.i(String.format("%s=%s", "postPath", mParentPostPath));

        // 投稿情報を取得したら投稿のユーザー情報を取得する
        mPostViewModel.getPost().observe(this, new Observer<PostBean>() {
            @Override
            public void onChanged(PostBean postBean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s=%s", "postBean", postBean));
                if (postBean.getPhotoName() != null) mBinding.photoImageView.setMaxHeight(300);
                mUserViewModel.fetchUserInfo(postBean.getUid());
            }
        });
        mPostViewModel.fetchPost(mParentPostPath);

        // ユーザー情報を取得したらアイコンにクリックリスナーを設定する
        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                mUid = userBean.getUid();
                mBinding.iconImageView.setOnClickListener(DisplayCommentActivity.this);
            }
        });

        // コメントリストを取得したらコメントごとのユーザー情報を取得する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(List<PostBean> postList) {
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
        mPostViewModel.fetchPostCommentList(mParentPostPath);

        //コメントごとのユーザー情報を取得したらリスト表示する
        mUserViewModel.getUserList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> userList) {
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
                mDisplayCommentAdapter = new DisplayCommentAdapter(DisplayCommentActivity.this,
                        mCommentDataMapList, R.layout.activity_display_comment_list);
                mListView = mBinding.commentListView;
                mListView.setAdapter(mDisplayCommentAdapter);
                mListView.setOnItemClickListener(DisplayCommentActivity.this);
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
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.commentButton) {
            startActivity(new Intent(getApplication(), CommentActivity.class)
                    .putExtra("postPath", mParentPostPath));
        } else if (i == R.id.timelineImageButton) {
            startActivity(new Intent(getApplication(), TimelineActivity.class));
        } else if (i == R.id.mapImageButton) {
            startActivity(new Intent(getApplication(), MapActivity.class));
        } else if (i == R.id.userImageButton) {
            startActivity(new Intent(getApplication(), UserActivity.class));
        } else if (i == R.id.iconImageView) {
            startActivity(new Intent(getApplication(), UserActivity.class).putExtra("uid", mUid));
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserBean userBean = (UserBean) mCommentDataMapList.get(position).get("userBean");
        startActivity(new Intent(getApplication(), UserActivity.class).putExtra("uid", userBean.getUid()));
    }
}
