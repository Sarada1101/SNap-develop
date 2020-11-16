package com.example.snap_develop.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.model.PostModel;

public class PostViewModel extends ViewModel {
    MutableLiveData<PostBean> post;
    PostModel postModel = new PostModel();

    public void insertPost(PostBean postBean) {
        postModel.insertComment(postBean);
    }
}
