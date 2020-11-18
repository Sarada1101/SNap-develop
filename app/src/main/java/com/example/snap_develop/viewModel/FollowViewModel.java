package com.example.snap_develop.viewModel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.snap_develop.model.FollowModel;
import com.example.snap_develop.util.LogUtil;

public class FollowViewModel extends ViewModel {

    FollowModel followModel = new FollowModel();

    public void insertApplicatedFollow(String userPath, String myUid) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        followModel.insertApplicatedFollow(userPath, myUid);
    }

    public void insertApprovalPendingFollow(String userPath, String myUid) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        followModel.insertApprovalPendingFollow(userPath, myUid);
    }
}
