package com.example.snap_develop.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.UserModel;
import com.example.snap_develop.util.LogUtil;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {
    private MutableLiveData<String> authResult;
    private MutableLiveData<String> updateResult;
    private MutableLiveData<UserBean> user;
    UserModel userModel = new UserModel();

    public FirebaseUser getCurrentUser() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        return userModel.getCurrentUser();
    }

    public void createAccount(String email, String password) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        userModel.createAccount(email, password, authResult);
    }

    public void signIn(String email, String password) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        userModel.signIn(email, password, authResult);
    }

    public void insertUser(UserBean userBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        userModel.insertUser(userBean);
    }

    public void signOut() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        userModel.signOut();
    }

    public void updateUser(UserBean userBean, byte[] data) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        userModel.updateUser(userBean, data, updateResult);
    }

    //ここから
    public void fetchUserInfo(String UserId) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        user = new MutableLiveData<>();
        userModel.fetchUserInfo(UserId, user);

    }


    public MutableLiveData<String> getAuthResult() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        if (authResult == null) {
            authResult = new MutableLiveData<>();
        }
        return authResult;
    }

    public MutableLiveData<String> getUpdateResult() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        if (updateResult == null) {
            updateResult = new MutableLiveData<>();
        }
        return updateResult;
    }

    public MutableLiveData<UserBean> getUser() {
        if (user == null) {
            user = new MutableLiveData<>();
        }
        return user;
    }
}

