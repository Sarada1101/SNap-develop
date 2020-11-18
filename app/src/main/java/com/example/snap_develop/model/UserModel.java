package com.example.snap_develop.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class UserModel extends FirestoreBase {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public FirebaseUser getCurrentUser() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        return firebaseAuth.getCurrentUser();
    }

    public void createAccount(String email, String password,
            final MutableLiveData<String> authResult) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LogUtil.getClassName(), "createUserWithEmail:success");
                            authResult.setValue("success");
                            UserBean userBean = new UserBean();
                            UserModel userModel = new UserModel();
                            userBean.setUid(userModel.getCurrentUser().getUid());
                            userBean.setName(userModel.getCurrentUser().getUid());
                            userBean.setMessage("よろしくお願いします。");
                            userBean.setIcon("no_image.png");
                            userBean.setFollowNotice(true);
                            userBean.setGoodNotice(true);
                            userBean.setCommentNotice(true);
                            userBean.setPublicationArea("public");
                            insertUser(userBean);
                        } else {
                            Log.w(LogUtil.getClassName(),
                                    "createUserWithEmail:failure:" + task.getException());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }

    public void signIn(String email, String password,
            final MutableLiveData<String> authResult) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LogUtil.getClassName(), "signInUserWithEmail:success");
                            authResult.setValue("success");
                        } else {
                            Log.w(LogUtil.getClassName(), "signInUserWithEmail:failure",
                                    task.getException());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }

    public void insertUser(UserBean userBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        Map<String, Object> user = new HashMap<>();
        user.put("uid", userBean.getUid());
        user.put("name", userBean.getName());
        user.put("message", userBean.getMessage());
        user.put("icon", userBean.getIcon());
        user.put("followNotice", userBean.isFollowNotice());
        user.put("goodNotice", userBean.isGoodNotice());
        user.put("commentNotice", userBean.isCommentNotice());

        this.connect();

        firestore.collection("users").document(userBean.getUid()).set(user);
    }

    public void signOut() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        firebaseAuth.signOut();
    }
}
