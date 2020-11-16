package com.example.snap_develop.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.model.PostModel;

import java.util.List;

public class PostViewModel extends ViewModel {
    MutableLiveData<PostBean> post;
    MutableLiveData<List<PostBean>> timeLine;
    PostModel postModel = new PostModel();

    public void insertPost(PostBean postBean) {
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
    }
}
