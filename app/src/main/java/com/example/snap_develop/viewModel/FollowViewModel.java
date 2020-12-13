package com.example.snap_develop.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.FollowModel;

import java.util.List;

import timber.log.Timber;

public class FollowViewModel extends ViewModel {

    FollowModel followModel = new FollowModel();
    private MutableLiveData<List<UserBean>> followList;
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

    public void deleteApplicatedFollow(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", fromUid, "insertUid", deleteUid));
        followModel.deleteApplicatedFollow(fromUid, deleteUid);
    }


    public void deleteApprovalPendingFollow(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));
        followModel.deleteApprovalPendingFollow(fromUid, deleteUid);
    }


    public void insertFollower(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));
        followModel.insertFollower(toUid, insertUid);
    }


    public void insertFollowing(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));
        followModel.insertFollowing(toUid, insertUid);
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


    public void fetchFollowingList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        followModel.fetchFollowingList(uid, followList);
    }


    public void fetchFollowerList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        followModel.fetchFollowerList(uid, followList);
    }


    public void fetchApprovalPendingList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        followModel.fetchApprovalPendingList(uid, followList);
    }


    public void fetchApplicatedList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        followModel.fetchApplicatedList(uid, followList);
    }

    public void fetchCount(String userPath, String countPath) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "userPath", userPath, "countPath", countPath));

        userCount = new MutableLiveData<>();
        followModel.fetchCount(userPath, countPath, userCount);
    }
}
