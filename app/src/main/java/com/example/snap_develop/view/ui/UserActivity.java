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
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityUserBinding;
import com.example.snap_develop.view.adapter.UserAdapter;
import com.example.snap_develop.view_model.FollowViewModel;
import com.example.snap_develop.view_model.PostViewModel;
import com.example.snap_develop.view_model.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class UserActivity extends AppCompatActivity implements View.OnClickListener,
        TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private FollowViewModel mFollowViewModel;
    private ActivityUserBinding mBinding;
    private UserAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<PostBean> mPostList;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("ユーザー情報");

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user);

        TabLayout.Tab tabAt = Objects.requireNonNull(mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS));
        tabAt.select();
        Objects.requireNonNull(tabAt.getIcon()).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        mBinding.followingButton.setOnClickListener(this);
        mBinding.followerButton.setOnClickListener(this);
        mBinding.followRequestButton.setOnClickListener(this);
        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        // ユーザー情報を取得したら投稿リストを取得する
        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean));
                mPostViewModel.fetchPostList(mUid);
            }
        });

        // 投稿リストを取得したらリストを表示する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postList", postList));

                mAdapter = new UserAdapter(UserActivity.this, postList, mUserViewModel.getUser().getValue());
                mRecyclerView = mBinding.userRecyclerView;
                LinearLayoutManager llm = new LinearLayoutManager(UserActivity.this);
                mRecyclerView.setLayoutManager(llm);
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(UserActivity.this,
                        DividerItemDecoration.VERTICAL);
                mRecyclerView.addItemDecoration(itemDecoration);
                mRecyclerView.setAdapter(mAdapter);

                mPostList = postList;

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
                                .setMessage("投稿を削除しますか？")
                                .setPositiveButton(R.string.yesMessage, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deletePost(mPostList.get(swipedPosition));
                                        listRemove(swipedPosition);
                                        Toast.makeText(getApplication(), "投稿を削除しました", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(R.string.noMessage, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Objects.requireNonNull(
                                                mBinding.userRecyclerView.getAdapter()).notifyItemChanged(
                                                swipedPosition);
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

                        Drawable deleteIcon = Objects.requireNonNull(
                                ContextCompat.getDrawable(getApplication(), R.drawable.ic_delete));
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

        mFollowViewModel.getFollowing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "aBoolean", aBoolean));
                if (aBoolean) {
                    mBinding.followRequestButton.setText("フォロー済み");
                    mBinding.followRequestButton.setOnClickListener(null);
                }
            }
        });

        mFollowViewModel.getApprovalPendingFollow().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "aBoolean", aBoolean));
                if (aBoolean) {
                    mBinding.followRequestButton.setText("申請済み");
                    mBinding.followRequestButton.setOnClickListener(null);
                }
            }
        });

        // 他人のユーザー情報を表示する時（uidがIntentに設定されている時）
        mUid = getIntent().getStringExtra("uid");
        if (mUid == null || mUid.equals(mUserViewModel.getCurrentUser().getUid())) {
            // 自分のユーザー情報を表示する時（uidがIntentに設定されていない時）
            mUid = mUserViewModel.getCurrentUser().getUid();
            mBinding.followRequestButton.setText("フォロー申請/承認");
        }
        Timber.i(String.format("%s=%s", "mUid", mUid));

        mUserViewModel.fetchUserInfo(mUid);

        if (!mUserViewModel.getCurrentUser().getUid().equals(mUid)) {
            mFollowViewModel.checkFollowing(mUserViewModel.getCurrentUser().getUid(), mUid);
            mFollowViewModel.checkApprovalPendingFollow(mUserViewModel.getCurrentUser().getUid(), mUid);
        }

        mBinding.setUserViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(this);
    }


    private void clearCanvas(Canvas c, int left, int top, int right, int bottom) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        c.drawRect(left, top, right, bottom, paint);
    }


    private void deletePost(PostBean postBean) {
        mPostViewModel.deletePost(postBean);
    }


    private void listRemove(int position) {
        mPostList.remove(position);
        Objects.requireNonNull(mBinding.userRecyclerView.getAdapter()).notifyItemRemoved(position);
        mBinding.userRecyclerView.getAdapter().notifyItemRangeRemoved(position, mPostList.size());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "menu", menu));
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "item", item));
        int id = item.getItemId();
        if (id == R.id.updateAccount) {
            startActivity(new Intent(this, AccountUpdateActivity.class));
        } else if (id == R.id.updateUser) {
            startActivity(new Intent(this, UserUpdateActivity.class));
        } else if (id == R.id.setting) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (id == R.id.signOut) {
            mUserViewModel.signOut();
            startActivity(new Intent(this, AuthActivity.class));
        }
        return true;
    }


    public void followRequest(String uid, String myUid) {
        mFollowViewModel.insertApprovalPendingFollow(myUid, uid);
        mFollowViewModel.insertApplicatedFollow(uid, myUid);
        mBinding.followRequestButton.setText("申請済み");
        // クリックリスナー解除
        mBinding.followRequestButton.setOnClickListener(null);
        Toast.makeText(this, "フォロー申請しました！", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.followerButton) {
            startActivity(new Intent(UserActivity.this, FollowerListActivity.class).putExtra("uid", mUid));
        } else if (i == R.id.followingButton) {
            startActivity(new Intent(UserActivity.this, FollowingListActivity.class).putExtra("uid", mUid));
        } else if (i == R.id.followRequestButton) {
            // 表示されている情報がログインしているユーザーの情報なら
            if (mUserViewModel.getCurrentUser().getUid().equals(mUid)) {
                startActivity(new Intent(UserActivity.this, ApprovalPendingFollowListActivity.class));
            } else {
                followRequest(mUid, mUserViewModel.getCurrentUser().getUid());
            }
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
