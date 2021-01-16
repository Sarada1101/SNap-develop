package com.example.snap_develop.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.FollowModel;

import java.util.List;

import timber.log.Timber;

public class FollowViewModel extends ViewModel {

    private final FollowModel followModel = new FollowModel();
    private MutableLiveData<List<UserBean>> followList;
    private MutableLiveData<Boolean> following;
    private MutableLiveData<Boolean> approvalPendingFollow;

    public MutableLiveData<List<UserBean>> getFollowList() {
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        return followList;
    }


    public MutableLiveData<Boolean> getFollowing() {
        if (following == null) {
            following = new MutableLiveData<>();
        }
        return following;
    }


    public MutableLiveData<Boolean> getApprovalPendingFollow() {
        if (approvalPendingFollow == null) {
            approvalPendingFollow = new MutableLiveData<>();
        }
        return approvalPendingFollow;
    }


    public void insertFollowing(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));
        followModel.insertFollowing(toUid, insertUid);
    }


    public void insertFollower(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));
        followModel.insertFollower(toUid, insertUid);
    }


    public void insertApprovalPendingFollow(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));
        followModel.insertApprovalPendingFollow(toUid, insertUid);
    }


    public void insertApplicatedFollow(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));
        followModel.insertApplicatedFollow(toUid, insertUid);
    }


    public void deleteFollowing(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));
        followModel.deleteFollowing(fromUid, deleteUid);
    }


    public void deleteFollower(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", fromUid, "insertUid", deleteUid));
        followModel.deleteFollower(fromUid, deleteUid);
    }


    public void deleteApprovalPendingFollow(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));
        followModel.deleteApprovalPendingFollow(fromUid, deleteUid);
    }


    public void deleteApplicatedFollow(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", fromUid, "insertUid", deleteUid));
        followModel.deleteApplicatedFollow(fromUid, deleteUid);
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


    public void checkFollowing(String fromUid, String checkUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "checkUid", checkUid));
        if (following == null) {
            following = new MutableLiveData<>();
        }
        followModel.checkFollowing(fromUid, checkUid, following);
    }


    public void checkApprovalPendingFollow(String fromUid, String checkUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "checkUid", checkUid));
        if (approvalPendingFollow == null) {
            approvalPendingFollow = new MutableLiveData<>();
        }
        followModel.checkApprovalPendingFollow(fromUid, checkUid, approvalPendingFollow);
    }
}
