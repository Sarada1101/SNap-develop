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
import com.example.snap_develop.adapter.ApplicatedFollowListAdapter;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityApplicatedFollowListBinding;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.List;

import timber.log.Timber;

public class ApplicatedFollowListActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private FollowViewModel mFollowViewModel;
    private UserViewModel mUserViewModel;
    private String mUid;
    private ListView mListView;
    private ApplicatedFollowListAdapter mApplicatedFollowListAdapter;
    private List<UserBean> mFollowList;
    private ActivityApplicatedFollowListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicated_follow_list);
        setTitle("フォロー申請");

        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_applicated_follow_list);

        mBinding.applicatedFollowButton.setOnClickListener(this);
        mBinding.approvalPendingFolliwButton.setOnClickListener(this);
        mBinding.timelineImageButton.setOnClickListener(this);
        mBinding.mapImageButton.setOnClickListener(this);
        mBinding.userImageButton.setOnClickListener(this);

        mUid = mUserViewModel.getCurrentUser().getUid();

        // フォロー申請リストを取得したら
        mFollowViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "followList", followList));

                mFollowList = followList;
                mApplicatedFollowListAdapter = new ApplicatedFollowListAdapter(ApplicatedFollowListActivity.this,
                        followList, R.layout.activity_applicated_follow_list_row);
                mListView = mBinding.applicatedFollowListView;
                mListView.setAdapter(mApplicatedFollowListAdapter);
                mListView.setOnItemClickListener(ApplicatedFollowListActivity.this);
            }
        });
        mFollowViewModel.fetchApplicatedList(mUid);
    }


    private void approvalFollow(String uid, String myUid) {
        mFollowViewModel.insertFollower(/*to*/myUid, /*insert*/uid);
        mFollowViewModel.deleteApplicatedFollow(/*from*/myUid, /*delete*/uid);
        mFollowViewModel.insertFollowing(uid, myUid);
        mFollowViewModel.deleteApprovalPendingFollow(uid, myUid);
    }

    private void rejectFollow(String uid, String myUid) {
        mFollowViewModel.deleteApplicatedFollow(/*from*/myUid, /*delete*/uid);
        mFollowViewModel.deleteApprovalPendingFollow(uid, myUid);
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
        } else if (i == R.id.applicatedFollowButton) {
            startActivity(new Intent(getApplication(), ApplicatedFollowListActivity.class));
        } else if (i == R.id.approvalPendingFolliwButton) {
            startActivity(new Intent(getApplication(), ApprovalPendingFollowListActivity.class));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view.getId() == R.id.approvalButton) {
            approvalFollow(mFollowList.get(position).getUid(), mUid);
        } else if (view.getId() == R.id.rejectButton) {
            rejectFollow(mFollowList.get(position).getUid(), mUid);
        } else {
            UserBean userBean = mFollowList.get(position);
            startActivity(new Intent(getApplication(), UserActivity.class).putExtra("uid", userBean.getUid()));
        }
    }
}
