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

    private static final String TAG = "Firestore";

    public void insertPost(PostBean postBean) {
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
                        Log.d(TAG,
                                "posts written with ID: " + documentReference.getId());

                        //パスをusersコレクションに追加
                        Map<String, Object> usersPost = new HashMap<>();
                        usersPost.put("path", documentReference);
                        firestore.collection("users")
                                .document(postBean.getUid())
                                .collection("post")
                                .add(usersPost)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG,
                                                "users/post written with ID: "
                                                        + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding users/post", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding posts", e);
                    }
                });
    }
}
