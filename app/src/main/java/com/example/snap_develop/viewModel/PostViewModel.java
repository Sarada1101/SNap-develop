package com.example.snap_develop.viewModel;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.model.PostModel;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.List;

import timber.log.Timber;

public class PostViewModel extends ViewModel {

    MutableLiveData<PostBean> post;
    MutableLiveData<List<PostBean>> postList;
    PostModel postModel = new PostModel();

    public MutableLiveData<List<PostBean>> getPostList() {
        Timber.i(MyDebugTree.START_LOG);
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        return postList;
    }


    public void insertComment(PostBean postBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postBean", postBean));
        postModel.insertComment(postBean);
    }


    public void insertPost(PostBean postBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postBean", postBean));
        postModel.insertPost(postBean);
    }


    public void fetchPostList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        postModel.fetchPostList(uid, postList);
    }


    public MutableLiveData<PostBean> getPost() {
        if (post == null) {
            post = new MutableLiveData<>();
        }
        return post;
    }


    public void fetchTimeLine(List<String> uidList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uidList", uidList));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        postModel.fetchTimeLine(uidList, postList);
    }

    public void fetchPost(String postPath) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        post = new MutableLiveData<>();
        postModel.fetchPost(postPath, post);

    }

    public void fetchPostCommentList(List<String> commentList) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postList = new MutableLiveData<>();
        postModel.fetchPostCommentList(commentList, postList);
    }


    public void fetchMapPostList(VisibleRegion visibleRegion) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "visibleRegion", visibleRegion));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        postModel.fetchMapPostList(visibleRegion, postList);
    }


    public void addGood(String userPath, String postPath) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.addGood(userPath, postPath);
    }
}
