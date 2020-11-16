package com.example.snap_develop.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class PostModel extends FirestoreBase {

    public void insertPost(final PostBean postBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        Map<String, Object> post = new HashMap<>();
        post.put("message", postBean.getMessage());
        post.put("picture", postBean.getPicture());
        //TODO 現在地
        post.put("datetime", postBean.getDatetime());
        post.put("anonymous", postBean.isAnonymous());
        post.put("danger", postBean.isDanger());
        post.put("uid", postBean.getUid());
        post.put("type", postBean.getType());

        this.connect();

        //投稿を追加
        firestore.collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LogUtil.getClassName(),
                                String.format("posts add document ID: %s",
                                        documentReference.getId()));

                        //パスをusersコレクションに追加
                        Map<String, Object> usersPost = new HashMap<>();
                        usersPost.put("path", documentReference);
                        firestore.collection("users")
                                .document(postBean.getUid())
                                .collection("posts")
                                .add(usersPost)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(LogUtil.getClassName(),
                                                String.format("users/post add document ID: %s",
                                                        documentReference.getId()));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(LogUtil.getClassName(), e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LogUtil.getClassName(), e);
                    }
                });
    }
}
