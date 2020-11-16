package com.example.snap_develop.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.databinding.ActivityPostBinding;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private PostViewModel postViewModel;
    private ActivityPostBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        mBinding.postFloatingActionButton.setOnClickListener(this);

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getAuthResult();
        userViewModel.signIn("aaa@aaa.com", "aaaaaa");
    }

    private void insertPost() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        FirebaseUser firebaseUser = new ViewModelProvider(this).get(
                UserViewModel.class).getCurrentUser();
        PostBean postBean = new PostBean();

        postBean.setMessage(String.valueOf(mBinding.postTextMultiLine.getText()));
        postBean.setPicture("no_img.png");
        //TODO 現在地
        postBean.setDatetime(new Date());
        postBean.setAnonymous(mBinding.anonymousSwitch.isChecked());
        postBean.setDanger(mBinding.dangerSwitch.isChecked());
        postBean.setUid(firebaseUser.getUid());
        postBean.setType("post");

        postViewModel.insertPost(postBean);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.postFloatingActionButton) {
            Log.d("insertPost", "success");
            insertPost();
        }
    }
}
