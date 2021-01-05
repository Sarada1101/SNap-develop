package com.example.snap_develop.view.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.databinding.ActivityCommentBinding;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;

import timber.log.Timber;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener,
        TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private ActivityCommentBinding mBinding;
    private String mParentPostPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        setTitle("コメント投稿");

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comment);

        mBinding.buttonTabLayout.getTabAt(MainApplication.MAP_POS).select();
        mBinding.buttonTabLayout.getTabAt(MainApplication.MAP_POS).getIcon().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        mBinding.postCommentButton.setOnClickListener(this);
        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        mParentPostPath = getIntent().getStringExtra("postPath");
    }


    private void insertComment() {
        Timber.i(MyDebugTree.START_LOG);
        String comment = mBinding.commentTextInputEditText.getText().toString();

        if (!validateForm(comment)) {
            return;
        }

        PostBean commentBean = new PostBean();
        commentBean.setAnonymous(mBinding.commentAnonymousSwitch.isChecked());
        commentBean.setMessage(comment);
        commentBean.setDatetime(new Date());
        commentBean.setUid(mUserViewModel.getCurrentUser().getUid());
        commentBean.setType("comment");
        commentBean.setPostPath(mParentPostPath);

        mPostViewModel.insertComment(commentBean);
        startActivity(new Intent(CommentActivity.this, DisplayCommentActivity.class).putExtra("postPath",
                mParentPostPath));
    }


    private boolean validateForm(String comment) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "comment", comment));
        boolean isValidSuccess = false;

        int MAX_LENGTH = 200;
        if (TextUtils.isEmpty(comment)) {
            mBinding.commentTextInputLayout.setError("コメントを入力してください");
        } else if (comment.length() > MAX_LENGTH) {
            mBinding.commentTextInputLayout.setError("コメントは200文字以内にしてください");
        } else {
            mBinding.commentTextInputLayout.setError(null);
            isValidSuccess = true;
        }

        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidSuccess", isValidSuccess));
        return isValidSuccess;
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.postCommentButton) {
            if (mUserViewModel.getCurrentUser() == null) {
                startActivity(new Intent(getApplication(), AuthActivity.class));
            } else {
                insertComment();
            }
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Timber.i(MyDebugTree.START_LOG);
        switch (tab.getPosition()) {
            case MainApplication.TIMELINE_POS:
                if (mUserViewModel.getCurrentUser() == null) {
                    startActivity(new Intent(getApplication(), AuthActivity.class));
                } else {
                    startActivity(new Intent(getApplication(), TimelineActivity.class));
                }
                break;
            case MainApplication.MAP_POS:
                if (mUserViewModel.getCurrentUser() == null) {
                    startActivity(new Intent(getApplication(), AuthActivity.class));
                } else {
                    startActivity(new Intent(getApplication(), MapActivity.class));
                }
                break;
            case MainApplication.USER_POS:
                if (mUserViewModel.getCurrentUser() == null) {
                    startActivity(new Intent(getApplication(), AuthActivity.class));
                } else {
                    startActivity(new Intent(getApplication(), UserActivity.class));
                }
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
