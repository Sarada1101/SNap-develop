package com.example.snap_develop.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.model.FollowModel;

import timber.log.Timber;

public class FollowViewModel extends ViewModel {

    FollowModel followModel = new FollowModel();

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
}
