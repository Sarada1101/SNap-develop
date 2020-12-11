package com.example.snap_develop.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.FollowModel;
import com.example.snap_develop.util.LogUtil;

import java.util.List;

import timber.log.Timber;

public class FollowViewModel extends ViewModel {

    FollowModel followModel = new FollowModel();
    MutableLiveData<List<UserBean>> followList;
    private MutableLiveData<Integer> userCount;

    public MutableLiveData<List<UserBean>> getFollowList() {
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        return followList;
    }

    public MutableLiveData<Integer> getUserCount() {
        if (userCount == null) {
            userCount = new MutableLiveData<>();
        }
        return userCount;
    }

    public void deleteApplicatedFollow(String userPath, String myUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "userPath", userPath, "myUid", myUid));
        followModel.deleteApplicatedFollow(userPath, myUid);
    }


    public void deleteApprovalPendingFollow(String userPath, String myUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "userPath", userPath, "myUid", myUid));
        followModel.deleteApprovalPendingFollow(userPath, myUid);
    }


    public void insertFollower(String userPath, String myUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "userPath", userPath, "myUid", myUid));
        followModel.insertFollower(userPath, myUid);
    }


    public void insertFollowing(String userPath, String myUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "userPath", userPath, "myUid", myUid));
        followModel.insertFollowing(userPath, myUid);
    }


    public void insertApplicatedFollow(String userPath, String myUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "userPath", userPath, "myUid", myUid));
        followModel.insertApplicatedFollow(userPath, myUid);
    }


    public void insertApprovalPendingFollow(String userPath, String myUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "userPath", userPath, "myUid", myUid));
        followModel.insertApprovalPendingFollow(userPath, myUid);
    }


    public void fetchApprovalPendingList(String userPath) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userPath", userPath));
        followModel.fetchApprovalPendingList(userPath, followList);
    }

    public void fetchApplicatedList(String userPath) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        followModel.fetchApplicatedList(userPath, followList);

    }

    public void fetchCount(String userPath, String countPath) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "userPath", userPath, "countPath", countPath));

        userCount = new MutableLiveData<>();
        followModel.fetchCount(userPath, countPath, userCount);
    }
}
