package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityTimelineBinding;
import com.example.snap_develop.view.adapter.TimelineAdapter;
import com.example.snap_develop.viewModel.FollowViewModel;
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

public class TimelineActivity extends AppCompatActivity implements View.OnClickListener {

    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityTimelineBinding mBinding;
    private TimelineAdapter mTimelineAdapter;
    private RecyclerView mRecyclerView;
    private List<Map<String, Object>> mTimelineDataMapList;
    private List<UserBean> mUserBeanList;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        setTitle("タイムライン");

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        mBinding.timelineImageButton.setOnClickListener(this);
        mBinding.mapImageButton.setOnClickListener(this);
        mBinding.userImageButton.setOnClickListener(this);

        // フォローリストを取得したらタイムラインを取得
        mFollowViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "followList", followList));
                mUserBeanList = followList;
                mPostViewModel.fetchTimeLine(followList);
            }
        });

        // タイムラインを取得したらソートして表示する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(List<PostBean> timeLine) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "timeLine", timeLine));

                //投稿を日時順にソート
                class TimelineSortCompare implements Comparator<PostBean> {
                    @Override
                    public int compare(PostBean o1, PostBean o2) {
                        Date sortKey1 = o1.getDatetime();
                        Date sortKey2 = o2.getDatetime();
                        return sortKey1.compareTo(sortKey2);
                    }
                }
                Collections.sort(timeLine, new TimelineSortCompare().reversed());

                // コメントリストとユーザー情報を紐付ける
                mTimelineDataMapList = new ArrayList<>();
                for (PostBean postBean : timeLine) {
                    Map<String, Object> timelineDataMap = new HashMap<>();
                    timelineDataMap.put("postBean", postBean);
                    for (UserBean userBean : mUserBeanList) {
                        if (userBean.getUid().equals(postBean.getUid())) {
                            timelineDataMap.put("userBean", userBean);
                            break;
                        }
                    }
                    mTimelineDataMapList.add(timelineDataMap);
                }

                mTimelineAdapter = new TimelineAdapter(TimelineActivity.this, mTimelineDataMapList);
                mRecyclerView = mBinding.timelineRecyclerView;
                LinearLayoutManager llm = new LinearLayoutManager(TimelineActivity.this);
                mRecyclerView.setLayoutManager(llm);
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(TimelineActivity.this,
                        DividerItemDecoration.VERTICAL);
                mRecyclerView.addItemDecoration(itemDecoration);
                mRecyclerView.setAdapter(mTimelineAdapter);
            }
        });

        //現在ログイン中のユーザーのUidを取得する処理
        mUid = mUserViewModel.getCurrentUser().getUid();
        mFollowViewModel.fetchFollowingList(mUid);
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
