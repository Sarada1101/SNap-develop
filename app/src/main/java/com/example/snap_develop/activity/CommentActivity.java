package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.databinding.ActivityCommentBinding;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.Date;

import timber.log.Timber;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private PostViewModel mPostViewModel;
    private UserViewModel mUserViewModel;
    private String mParentPostPath;
    private ActivityCommentBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mParentPostPath = getIntent().getStringExtra("postPath");

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comment);

        mBinding.postCommentButton.setOnClickListener(this);
        mBinding.timelineImageButton.setOnClickListener(this);
        mBinding.mapImageButton.setOnClickListener(this);
        mBinding.userImageButton.setOnClickListener(this);
    }

    private void insertComment() {
        Timber.i(MyDebugTree.START_LOG);
        String comment = mBinding.commentTextInputEeditText.getText().toString();

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
        if (i == R.id.postCommentButton) {
            insertComment();
        } else if (i == R.id.timelineImageButton) {
            startActivity(new Intent(CommentActivity.this, TimelineActivity.class));
        } else if (i == R.id.mapImageButton) {
            startActivity(new Intent(CommentActivity.this, MapActivity.class));
        } else if (i == R.id.userImageButton) {
            startActivity(new Intent(CommentActivity.this, UserActivity.class));
        }
    }
}
