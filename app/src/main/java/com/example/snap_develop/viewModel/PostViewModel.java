package com.example.snap_develop.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.model.PostModel;
import com.example.snap_develop.util.LogUtil;

import java.util.List;

public class PostViewModel extends ViewModel {
    MutableLiveData<PostBean> post;
    MutableLiveData<List<PostBean>> timeLine;
    PostModel postModel = new PostModel();

    public void insertPost(PostBean postBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.insertPost(postBean);
    }


    public MutableLiveData<List<PostBean>> getTimeLine() {
        if (timeLine == null) {
            timeLine = new MutableLiveData<>();
        }
        return timeLine;
    }

    public void fetchTimeLine(List<String> uidList) {
        System.out.println("--------------------fetchTimeLine----------------------");
        timeLine = new MutableLiveData<>();
        postModel.fetchTimeLine(uidList, timeLine);


    public void insertComment(PostBean postBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.insertComment(postBean);
    }
}
