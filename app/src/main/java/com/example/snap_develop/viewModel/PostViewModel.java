package com.example.snap_develop.viewModel;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.PostModel;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.List;
import java.util.Map;

public class PostViewModel extends ViewModel {
    MutableLiveData<PostBean> post;
    MutableLiveData<List<PostBean>> postList;
    MutableLiveData<Map<String, Bitmap>> timeLinePictureList;
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

    public MutableLiveData<Map<String, Bitmap>> getTimeLinePictureList() {
        if (timeLinePictureList == null) {
            timeLinePictureList = new MutableLiveData<>();
        }
        return timeLinePictureList;
    }

    public void fetchTimeLine(List<UserBean> userList) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        postModel.fetchTimeLine(userList, postList);
    }


    public void insertComment(PostBean postBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.insertComment(postBean);
    }

    public void fetchMapPostList(VisibleRegion visibleRegion) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.fetchMapPostList(visibleRegion, postList);
    }

    public void fetchPostPictures(Map<String, String> pathList) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.fetchPostPictures(pathList, timeLinePictureList);
    }
}
