package com.example.snap_develop.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.viewModel.PostViewModel;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        PostBean postBean = new PostBean();
        postBean.setMessage("test");
        postViewModel.insertPost(postBean);
    }
}
