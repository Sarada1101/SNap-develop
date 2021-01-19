package com.example.snap_develop.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.UserModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import timber.log.Timber;

public class UserViewModel extends ViewModel {

    private final UserModel userModel = new UserModel();
    private MutableLiveData<String> authResult;
    private MutableLiveData<String> updateResult;
    private MutableLiveData<UserBean> user;
    private MutableLiveData<List<UserBean>> userList;

    public MutableLiveData<String> getAuthResult() {
        Timber.i(MyDebugTree.START_LOG);
        if (authResult == null) {
            authResult = new MutableLiveData<>();
        }
        return authResult;
    }


    public MutableLiveData<String> getUpdateResult() {
        Timber.i(MyDebugTree.START_LOG);
        if (updateResult == null) {
            updateResult = new MutableLiveData<>();
        }
        return updateResult;
    }


    public MutableLiveData<UserBean> getUser() {
        Timber.i(MyDebugTree.START_LOG);
        if (user == null) {
            user = new MutableLiveData<>();
        }
        return user;
    }


    public MutableLiveData<List<UserBean>> getUserList() {
        Timber.i(MyDebugTree.START_LOG);
        if (userList == null) {
            userList = new MutableLiveData<>();
        }
        return userList;
    }


    public FirebaseUser getCurrentUser() {
        Timber.i(MyDebugTree.START_LOG);
        return userModel.getCurrentUser();
    }


    public void createAccount(String email, String password, UserBean userBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "email", email));
        if (authResult == null) {
            authResult = new MutableLiveData<>();
        }
        userModel.createAccount(email, password, userBean, authResult);
    }


    public void signIn(String email, String password) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "email", email));
        if (authResult == null) {
            authResult = new MutableLiveData<>();
        }
        userModel.signIn(email, password, authResult);
    }


    public void sendEmailVerification() {
        Timber.i(MyDebugTree.START_LOG);
        userModel.sendEmailVerification();
    }

    public void sendResetPasswordEmail(String email) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "email", email));
        userModel.sendResetPasswordEmail(email);
    }


    public void signOut() {
        Timber.i(MyDebugTree.START_LOG);
        userModel.signOut();
    }


    public void updateEmail(String email, String password) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "email", email, "password", password));
        if (updateResult == null) {
            updateResult = new MutableLiveData<>();
        }
        userModel.updateEmail(email, password, updateResult);
    }


    public void updatePassword(String password) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "password", password));
        if (updateResult == null) {
            updateResult = new MutableLiveData<>();
        }
        userModel.updatePassword(password, updateResult);
    }


    public void updateUser(UserBean userBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean));
        if (updateResult == null) {
            updateResult = new MutableLiveData<>();
        }
        userModel.updateUser(userBean, updateResult);
    }


    public void updateSetting(UserBean userBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean));
        userModel.updateSetting(userBean);
    }


    public void fetchUserInfo(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));
        if (user == null) {
            user = new MutableLiveData<>();
        }
        userModel.fetchUserInfo(uid, user);
    }


    public void fetchUserInfoList(List<String> uidList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uidList", uidList));
        if (userList == null) {
            userList = new MutableLiveData<>();
        }
        userModel.fetchUserInfoList(uidList, userList);
    }


    public void fcmTokenInsert(String uid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "uid", uid));

        userModel.fcmTokenInsert(uid);
    }
}
