package com.example.snap_develop.view.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.snap_develop.databinding.ActivityFollowingListBinding;
import com.example.snap_develop.view.adapter.FollowListAdapter;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import timber.log.Timber;

public class FollowingListActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityFollowingListBinding mBinding;
    private FollowListAdapter mFollowListAdapter;
    private RecyclerView mRecyclerView;
    private List<UserBean> mFollowList;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);
        setTitle("フォロー");

        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_following_list);

        mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS).select();
        mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS).getIcon().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        // フォローリストを取得したら
        mFollowViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "followList", followList));

                mFollowListAdapter = new FollowListAdapter(FollowingListActivity.this, followList);
                mRecyclerView = mBinding.followingRecyclerView;
                LinearLayoutManager llm = new LinearLayoutManager(FollowingListActivity.this);
                mRecyclerView.setLayoutManager(llm);
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(FollowingListActivity.this,
                        DividerItemDecoration.VERTICAL);
                mRecyclerView.addItemDecoration(itemDecoration);
                mRecyclerView.setAdapter(mFollowListAdapter);

                mFollowList = followList;

                if (!mUid.equals(mUserViewModel.getCurrentUser().getUid())) {
                    return;
                }

                ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                        final int swipedPosition = viewHolder.getAdapterPosition();

                        new AlertDialog.Builder(viewHolder.itemView.getContext())
                                .setMessage(R.string.dialogMessage)
                                .setPositiveButton(R.string.yesMessage, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cancelFollowing(mFollowList.get(swipedPosition).getUid(), mUid);
                                        listRemove(swipedPosition);
                                        Toast.makeText(getApplication(), "フォローを解除しました", Toast.LENGTH_SHORT);
                                    }
                                })
                                .setNegativeButton(R.string.noMessage, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mBinding.followingRecyclerView.getAdapter().notifyItemChanged(swipedPosition);
                                    }
                                })
                                .create()
                                .show();
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

                        Drawable deleteIcon = ContextCompat.getDrawable(getApplication(), R.drawable.ic_close);
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
                    }
                };
                new ItemTouchHelper(callback).attachToRecyclerView(mRecyclerView);
            }
        });

        // 他人のユーザー情報を表示する時（uidがIntentに設定されている時）
        mUid = getIntent().getStringExtra("uid");
        if (mUid == null || mUid.equals(mUserViewModel.getCurrentUser().getUid())) {
            // 自分のユーザー情報を表示する時（uidがIntentに設定されていない時）
            mUid = mUserViewModel.getCurrentUser().getUid();
        }
        Timber.i(String.format("%s=%s", "mUid", mUid));

        mFollowViewModel.fetchFollowingList(mUid);
    }


    private void clearCanvas(Canvas c, int left, int top, int right, int bottom) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        c.drawRect(left, top, right, bottom, paint);
    }


    private void cancelFollowing(String uid, String myUid) {
        mFollowViewModel.deleteFollower(/*from*/uid, /*delete*/myUid);
        mFollowViewModel.deleteFollowing(myUid, uid);
    }


    private void listRemove(int position) {
        mFollowList.remove(position);
        mBinding.followingRecyclerView.getAdapter().notifyItemRemoved(position);
        mBinding.followingRecyclerView.getAdapter().notifyItemRangeRemoved(position, mFollowList.size());
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
