package com.example.snap_develop.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

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
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityApplicatedFollowListBinding;
import com.example.snap_develop.view.adapter.ApplicatedFollowListAdapter;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import timber.log.Timber;

public class ApplicatedFollowListActivity extends AppCompatActivity implements View.OnClickListener,
        TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityApplicatedFollowListBinding mBinding;
    private ApplicatedFollowListAdapter mApplicatedFollowListAdapter;
    private RecyclerView mRecyclerView;
    private List<UserBean> mFollowList;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicated_follow_list);
        setTitle("フォロー申請");

        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_applicated_follow_list);

        mBinding.listTabLayout.getTabAt(1).select();

        mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS).select();
        mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS).getIcon().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        mBinding.listTabLayout.addOnTabSelectedListener(this);
        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        // フォロー申請リストを取得したら
        mFollowViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(final List<UserBean> followList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "followList", followList));

                mApplicatedFollowListAdapter = new ApplicatedFollowListAdapter(ApplicatedFollowListActivity.this,
                        followList);
                mRecyclerView = mBinding.applicatedFollowRecyclerView;
                LinearLayoutManager llm = new LinearLayoutManager(ApplicatedFollowListActivity.this);
                mRecyclerView.setLayoutManager(llm);
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                        ApplicatedFollowListActivity.this, DividerItemDecoration.VERTICAL);
                mRecyclerView.addItemDecoration(itemDecoration);
                mRecyclerView.setAdapter(mApplicatedFollowListAdapter);

                mFollowList = followList;
                mApplicatedFollowListAdapter.setOnItemClickListener(ApplicatedFollowListActivity.this);
            }
        });

        mUid = mUserViewModel.getCurrentUser().getUid();
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


    private void listRemove(int position) {
        mFollowList.remove(position);
        mBinding.applicatedFollowRecyclerView.getAdapter().notifyItemRemoved(position);
        mBinding.applicatedFollowRecyclerView.getAdapter().notifyItemRangeRemoved(position, mFollowList.size());
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.approvalButton) {
            int position = mApplicatedFollowListAdapter.mPosition;
            approvalFollow(mFollowList.get(position).getUid(), mUid);
            listRemove(position);
        } else if (i == R.id.rejectButton) {
            int position = mApplicatedFollowListAdapter.mPosition;
            rejectFollow(mFollowList.get(position).getUid(), mUid);
            listRemove(position);
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Timber.i(MyDebugTree.START_LOG);
        int i = tab.parent.getId();
        Timber.i("parent = " + tab.parent + " pos = " + tab.getPosition());


        if (i == R.id.listTabLayout) {
            switch (tab.getPosition()) {
                case 0:
                    startActivity(new Intent(getApplication(), ApprovalPendingFollowListActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getApplication(), ApplicatedFollowListActivity.class));
                    break;
            }
        } else if (i == R.id.buttonTabLayout) {
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

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Timber.i(MyDebugTree.START_LOG);
        int i = tab.parent.getId();
        Timber.i("parent = " + tab.parent + " pos = " + tab.getPosition());


        if (i == R.id.listTabLayout) {
            switch (tab.getPosition()) {
                case 0:
                    startActivity(new Intent(getApplication(), ApprovalPendingFollowListActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getApplication(), ApplicatedFollowListActivity.class));
                    break;
            }
        } else if (i == R.id.buttonTabLayout) {
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
}
