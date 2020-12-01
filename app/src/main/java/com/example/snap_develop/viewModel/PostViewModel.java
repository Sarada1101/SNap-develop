package com.example.snap_develop.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.model.PostModel;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.List;

public class PostViewModel extends ViewModel {
    MutableLiveData<PostBean> post;
    MutableLiveData<List<PostBean>> postList;
    PostModel postModel = new PostModel();

    public void insertPost(PostBean postBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.insertPost(postBean);
    }


    public MutableLiveData<List<PostBean>> getPostList() {
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        return postList;
    }

    public void fetchTimeLine(List<String> uidList) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postList = new MutableLiveData<>();
        postModel.fetchTimeLine(uidList, postList);
    }


    public void insertComment(PostBean postBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.insertComment(postBean);
    }

    public void fetchMapPostList(VisibleRegion visibleRegion) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.fetchMapPostList(visibleRegion, postList);
    }

    public void addGood(String postPath) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.addGood(postPath);
    }
}
