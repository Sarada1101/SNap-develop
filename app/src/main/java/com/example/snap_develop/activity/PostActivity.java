package com.example.snap_develop.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.viewModel.PostViewModel;

public class PostActivity extends AppCompatActivity {

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPostBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_post);
        mBinding.postFloatingActionButton.setOnClickListener(this);
        PostBean postBean = new PostBean();
        postBean.setMessage("test");
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
