package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.Date;

import timber.log.Timber;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private PostViewModel mPostViewModel;
    private UserViewModel mUserViewModel;
    String parentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        parentPost = getIntent().getStringExtra("parentPost");

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void insertComment() {
        EditText comment = findViewById(R.id.posteditText);
        Switch anonymousSwitch = findViewById(R.id.anonymousswitch1);

        PostBean commentBean = new PostBean();
        commentBean.setUid(mUserViewModel.getCurrentUser().getUid());
        commentBean.setType("comment");
        commentBean.setPostPath(parentPost);
        commentBean.setMessage(comment.getText().toString());
        commentBean.setDatetime(new Date());
        commentBean.setAnonymous(anonymousSwitch.getShowText());

        mPostViewModel.insertComment(commentBean);
        Toast.makeText(this, "成功しました！！", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplication(), DisplayCommentActivity.class));
    }

    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        if (i == R.id.replybutton) {
            insertComment();
        } else if (i == R.id.toDisplayCommentButton) {
            startActivity(new Intent(getApplication(), DisplayCommentActivity.class));
        }
    }
}
