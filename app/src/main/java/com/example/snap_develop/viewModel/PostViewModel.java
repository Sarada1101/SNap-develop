package com.example.snap_develop.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.model.PostModel;

public class PostViewModel extends ViewModel {
    PostModel postModel = new PostModel();

    public void insertComment(PostBean postBean) {
        postModel.insertComment(postBean);
    }

}
