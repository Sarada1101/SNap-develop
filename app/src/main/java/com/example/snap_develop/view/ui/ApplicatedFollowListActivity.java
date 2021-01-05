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
import com.example.snap_develop.databinding.ActivityApplicatedFollowListBinding;
import com.example.snap_develop.view.adapter.ApplicatedFollowListAdapter;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import timber.log.Timber;

public class ApplicatedFollowListActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

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

                ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
                        (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int swipedPosition = viewHolder.getAdapterPosition();
                        if (direction == ItemTouchHelper.LEFT) {
                            rejectFollow(mFollowList.get(swipedPosition).getUid(), mUid);
                            listRemove(swipedPosition);
                            Toast.makeText(getApplication(), "申請を拒否しました", Toast.LENGTH_SHORT).show();
                        } else if (direction == ItemTouchHelper.RIGHT) {
                            approvalFollow(mFollowList.get(swipedPosition).getUid(), mUid);
                            listRemove(swipedPosition);
                            Toast.makeText(getApplication(), "申請を承認しました", Toast.LENGTH_SHORT).show();
                        }
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

                        if (dX < 0) {
                            // 左スワイプのとき
                            Drawable deleteIcon = ContextCompat.getDrawable(ApplicatedFollowListActivity.this,
                                    R.drawable.ic_close);
                            ColorDrawable background = new ColorDrawable();
                            background.setColor(getResources().getColor(R.color.colorDanger));
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
                        } else {
                            // 右スワイプのとき
                            Drawable deleteIcon = ContextCompat.getDrawable(ApplicatedFollowListActivity.this,
                                    R.drawable.ic_check);
                            ColorDrawable background = new ColorDrawable();
                            background.setColor(getResources().getColor(R.color.colorAccent));
                            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX,
                                    itemView.getBottom());
                            background.draw(c);

                            int deleteIconTop =
                                    itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                            int deleteIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                            int deleteIconLeft = itemView.getLeft() + deleteIconMargin;
                            int deleteIconRight =
                                    itemView.getLeft() + deleteIconMargin + deleteIcon.getIntrinsicWidth();
                            int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();

                            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                            deleteIcon.draw(c);
                        }
                    }
                };
                new ItemTouchHelper(callback).attachToRecyclerView(mRecyclerView);
            }
        });

        mUid = mUserViewModel.getCurrentUser().getUid();
        mFollowViewModel.fetchApplicatedList(mUid);
    }


    private void clearCanvas(Canvas c, int left, int top, int right, int bottom) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        c.drawRect(left, top, right, bottom, paint);
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
