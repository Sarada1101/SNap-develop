package com.example.snap_develop.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.model.PostModel;
import com.example.snap_develop.util.LogUtil;

public class PostViewModel extends ViewModel {
    MutableLiveData<PostBean> post;
    PostModel postModel = new PostModel();

    public void insertPost(PostBean postBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.insertPost(postBean);
    }
}
