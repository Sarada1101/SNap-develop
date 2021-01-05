package com.example.snap_develop.view.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityApprovalPendingFollowListBinding;
import com.example.snap_develop.view.adapter.ApprovalPendingFollowListAdapter;
import com.example.snap_develop.view_model.FollowViewModel;
import com.example.snap_develop.view_model.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class ApprovalPendingFollowListActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    @SuppressWarnings("FieldCanBeLocal")
    private UserViewModel mUserViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityApprovalPendingFollowListBinding mBinding;
    private ApprovalPendingFollowListAdapter mApprovalPendingFollowListAdapter;
    private RecyclerView mRecyclerView;
    private List<UserBean> mFollowList;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_pending_follow_list);
        setTitle("フォロー承認待ち");

        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_approval_pending_follow_list);

        Objects.requireNonNull(mBinding.listTabLayout.getTabAt(0)).select();

        TabLayout.Tab tabAt = Objects.requireNonNull(mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS));
        tabAt.select();
        Objects.requireNonNull(tabAt.getIcon()).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        mBinding.listTabLayout.addOnTabSelectedListener(this);
        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        // フォロー承認待ちリストを取得したら
        mFollowViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "followList", followList));

                mApprovalPendingFollowListAdapter = new ApprovalPendingFollowListAdapter(
                        ApprovalPendingFollowListActivity.this, followList);
                mRecyclerView = mBinding.approvalPendingFollowRecyclerView;
                LinearLayoutManager llm = new LinearLayoutManager(ApprovalPendingFollowListActivity.this);
                mRecyclerView.setLayoutManager(llm);
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                        ApprovalPendingFollowListActivity.this, DividerItemDecoration.VERTICAL);
                mRecyclerView.addItemDecoration(itemDecoration);
                mRecyclerView.setAdapter(mApprovalPendingFollowListAdapter);

                mFollowList = followList;

                ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int swipedPosition = viewHolder.getAdapterPosition();
                        cancelFollow(mFollowList.get(swipedPosition).getUid(), mUid);
                        listRemove(swipedPosition);
                        Toast.makeText(getApplication(), "フォロー申請をキャンセルしました", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                            boolean isCurrentlyActive) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        View itemView = viewHolder.itemView;

                        // キャンセルされた時
                        if (dX == 0f && !isCurrentlyActive) {
                            clearCanvas(c, itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(),
                                    itemView.getBottom());
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false);
                            return;
                        }

                        Drawable deleteIcon = Objects.requireNonNull(
                                ContextCompat.getDrawable(getApplication(), R.drawable.ic_close));
                        ColorDrawable background = new ColorDrawable();
                        background.setColor(ContextCompat.getColor(getApplication(), R.color.colorDanger));
                        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(),
                                itemView.getBottom());
                        background.draw(c);
                        int deleteIconTop =
                                itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                        int deleteIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                        int deleteIconLeft =
                                itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
                        int deleteIconRight = itemView.getRight() - deleteIconMargin;
                        int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();

                        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                        deleteIcon.draw(c);
                    }
                };
                new ItemTouchHelper(callback).attachToRecyclerView(mRecyclerView);
            }
        });

        mUid = mUserViewModel.getCurrentUser().getUid();
        mFollowViewModel.fetchApprovalPendingList(mUid);
    }


    private void clearCanvas(Canvas c, int left, int top, int right, int bottom) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        c.drawRect(left, top, right, bottom, paint);
    }


    private void cancelFollow(String uid, String myUid) {
        mFollowViewModel.deleteApplicatedFollow(/*from*/uid, /*delete*/myUid);
        mFollowViewModel.deleteApprovalPendingFollow(myUid, uid);
    }


    private void listRemove(int position) {
        mFollowList.remove(position);
        Objects.requireNonNull(mBinding.approvalPendingFollowRecyclerView.getAdapter()).notifyItemRemoved(position);
        mBinding.approvalPendingFollowRecyclerView.getAdapter().notifyItemRangeRemoved(position, mFollowList.size());
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Timber.i(MyDebugTree.START_LOG);
        int i = Objects.requireNonNull(tab.parent).getId();
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
        int i = Objects.requireNonNull(tab.parent).getId();

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
