package com.example.snap_develop.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.FollowModel;

import java.util.List;

import timber.log.Timber;

public class FollowViewModel extends ViewModel {

    private FollowModel mFollowModel = new FollowModel();
    private MutableLiveData<List<UserBean>> followList;
    private MutableLiveData<Boolean> following;
    private MutableLiveData<Boolean> follower;
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


    public MutableLiveData<Boolean> getFollower() {
        if (follower == null) {
            follower = new MutableLiveData<>();
        }
        return follower;
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
        mFollowModel.insertFollowing(toUid, insertUid);
    }


    public void insertFollower(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));
        mFollowModel.insertFollower(toUid, insertUid);
    }


    public void insertApprovalPendingFollow(String toUid, String inseertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", inseertUid));
        mFollowModel.insertApprovalPendingFollow(toUid, inseertUid);
    }


    public void insertApplicatedFollow(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));
        mFollowModel.insertApplicatedFollow(toUid, insertUid);
    }


    public void deleteApprovalPendingFollow(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));
        mFollowModel.deleteApprovalPendingFollow(fromUid, deleteUid);
    }


    public void deleteApplicatedFollow(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", fromUid, "insertUid", deleteUid));
        mFollowModel.deleteApplicatedFollow(fromUid, deleteUid);
    }


    public void fetchFollowingList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        mFollowModel.fetchFollowingList(uid, followList);
    }


    public void fetchFollowerList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        mFollowModel.fetchFollowerList(uid, followList);
    }


    public void fetchApprovalPendingList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        mFollowModel.fetchApprovalPendingList(uid, followList);
    }


    public void fetchApplicatedList(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (followList == null) {
            followList = new MutableLiveData<>();
        }
        mFollowModel.fetchApplicatedList(uid, followList);
    }


    public void checkFollowing(String fromUid, String checkUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "checkUid", checkUid));
        if (following == null) {
            following = new MutableLiveData<>();
        }
        mFollowModel.checkFollowing(fromUid, checkUid, following);
    }


    public void checkFollower(String fromUid, String checkUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "checkUid", checkUid));
        if (follower == null) {
            follower = new MutableLiveData<>();
        }
        mFollowModel.checkFollower(fromUid, checkUid, follower);
    }


    public void checkApprovalPendingFollow(String fromUid, String checkUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "checkUid", checkUid));
        if (approvalPendingFollow == null) {
            approvalPendingFollow = new MutableLiveData<>();
        }
        mFollowModel.checkApprovalPendingFollow(fromUid, checkUid, approvalPendingFollow);
    }
}
