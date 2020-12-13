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
import com.example.snap_develop.adapter.ApprovalPendingFollowListAdapter;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityApprovalPendingFollowListBinding;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.List;

import timber.log.Timber;

public class ApprovalPendingFollowListActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private UserViewModel mUserViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityApprovalPendingFollowListBinding mBinding;
    private ApprovalPendingFollowListAdapter mApprovalPendingFollowListAdapter;
    private ListView mListView;
    private List<UserBean> mFollowList;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicated_follow_list);
        setTitle("フォロー承認待ち");

        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_approval_pending_follow_list);

        mBinding.applicatedFollowButton.setOnClickListener(this);
        mBinding.approvalPendingFolliwButton.setOnClickListener(this);
        mBinding.timelineImageButton.setOnClickListener(this);
        mBinding.mapImageButton.setOnClickListener(this);
        mBinding.userImageButton.setOnClickListener(this);

        // フォロー承認待ちリストを取得したら
        mFollowViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "followList", followList));

                mFollowList = followList;
                mApprovalPendingFollowListAdapter = new ApprovalPendingFollowListAdapter(
                        ApprovalPendingFollowListActivity.this,
                        followList, R.layout.activity_approval_pending_follow_list_row);
                mListView = mBinding.approvalPendingFollowListView;
                mListView.setAdapter(mApprovalPendingFollowListAdapter);
                mListView.setOnItemClickListener(ApprovalPendingFollowListActivity.this);
            }
        });

        mUid = mUserViewModel.getCurrentUser().getUid();
        mFollowViewModel.fetchApprovalPendingList(mUid);
    }


    private void cancelFollow(String uid, String myUid) {
        mFollowViewModel.deleteApplicatedFollow(/*from*/uid, /*delete*/myUid);
        mFollowViewModel.deleteApprovalPendingFollow(myUid, uid);
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
        if (view.getId() == R.id.rejectButton) {
            cancelFollow(mFollowList.get(position).getUid(), mUid);
        } else {
            UserBean userBean = mFollowList.get(position);
            startActivity(new Intent(getApplication(), UserActivity.class).putExtra("uid", userBean.getUid()));
        }
    }
}
