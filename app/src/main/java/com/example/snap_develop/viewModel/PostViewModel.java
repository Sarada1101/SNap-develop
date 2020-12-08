package com.example.snap_develop.viewModel;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.PostModel;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PostViewModel extends ViewModel {

    MutableLiveData<PostBean> post;
    MutableLiveData<List<PostBean>> postList;
    MutableLiveData<Map<String, Bitmap>> timeLinePictureList;
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

    public MutableLiveData<Map<String, Bitmap>> getTimeLinePictureList() {
        if (timeLinePictureList == null) {
            timeLinePictureList = new MutableLiveData<>();
        }
        return timeLinePictureList;
    }

    public MutableLiveData<PostBean> getPost() {
        Timber.i(MyDebugTree.START_LOG);
        if (post == null) {
            post = new MutableLiveData<>();
        }
        return post;
    }

    public void fetchTimeLine(List<UserBean> userList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userList", userList));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        postModel.fetchTimeLine(userList, postList);
    }

    public void fetchPost(String postPath) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postPath", postPath));
        if (post == null) {
            post = new MutableLiveData<>();
        }
        postModel.fetchPost(postPath, post);

    }

    public void fetchPostCommentList(String postPath) {
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postPath", postPath));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        postModel.fetchPostCommentList(postPath, postList);
    }


    public void fetchMapPostList(VisibleRegion visibleRegion) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "visibleRegion", visibleRegion));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        postModel.fetchMapPostList(visibleRegion, postList);
    }

    public void fetchPostPictures(Map<String, String> pathList) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postModel.fetchPostPictures(pathList, timeLinePictureList);
    }

    public void fetchSearchPost(String searchWord) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        postList = new MutableLiveData<>();
        postModel.fetchSearchPost(searchWord, postList);
    }


    public void addGood(String userPath, String postPath) {
        Timber.i(MyDebugTree.START_LOG);
        postModel.addGood(userPath, postPath);
    }
}
