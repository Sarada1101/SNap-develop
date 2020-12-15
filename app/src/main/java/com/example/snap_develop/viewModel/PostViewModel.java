package com.example.snap_develop.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.PostModel;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.List;

import timber.log.Timber;

public class PostViewModel extends ViewModel {

    PostModel mPostModel = new PostModel();
    MutableLiveData<PostBean> post;
    MutableLiveData<List<PostBean>> postList;

    public MutableLiveData<PostBean> getPost() {
        Timber.i(MyDebugTree.START_LOG);
        if (post == null) {
            post = new MutableLiveData<>();
        }
        return post;
    }


    public MutableLiveData<List<PostBean>> getPostList() {
        Timber.i(MyDebugTree.START_LOG);
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        return postList;
    }


    public void insertPost(PostBean postBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postBean", postBean));
        mPostModel.insertPost(postBean);
    }


    public void insertComment(PostBean postBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postBean", postBean));
        mPostModel.insertComment(postBean);
    }


    public void fetchPost(String postPath) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postPath", postPath));
        if (post == null) {
            post = new MutableLiveData<>();
        }
        mPostModel.fetchPost(postPath, post);
    }


    public void fetchPostList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        mPostModel.fetchPostList(uid, postList);
    }


    public void fetchPostCommentList(String postPath) {
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "postPath", postPath));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        mPostModel.fetchPostCommentList(postPath, postList);
    }


    public void fetchMapPostList(VisibleRegion visibleRegion) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "visibleRegion", visibleRegion));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        mPostModel.fetchMapPostList(visibleRegion, postList);
    }


    public void fetchTimeLine(List<UserBean> userBeanList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userList", userBeanList));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        mPostModel.fetchTimeLine(userBeanList, postList);
    }


    public void fetchSearchPost(String searchWord) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "searchWord", searchWord));
        if (postList == null) {
            postList = new MutableLiveData<>();
        }
        mPostModel.fetchSearchPost(searchWord, postList);
    }


    public void addGood(String uid, String postPath) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "postPath", postPath));
        mPostModel.addGood(uid, postPath);
    }
}
