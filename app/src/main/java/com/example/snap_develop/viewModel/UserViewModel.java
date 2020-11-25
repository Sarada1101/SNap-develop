package com.example.snap_develop.viewModel;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.UserModel;
import com.example.snap_develop.util.LogUtil;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;

public class UserViewModel extends ViewModel {
    private MutableLiveData<String> authResult;
    private MutableLiveData<String> updateResult;
    private MutableLiveData<Map<String, Bitmap>> iconList;
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

    public void fetchIconBmp(List<UserBean> userList) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        
        userModel.fetchIconBmp(userList, iconList);
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

    public MutableLiveData<Map<String, Bitmap>> getIconList() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        if (iconList == null) {
            iconList = new MutableLiveData<>();
        }
        return iconList;
    }
}

