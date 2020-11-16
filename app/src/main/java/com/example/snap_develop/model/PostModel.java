package com.example.snap_develop.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.bean.PostBean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostModel extends FirestoreBase {

    private static final String TAG = "Firestore";

    public void insertPost(PostBean postBean) {
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

        firestore.collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println(
                                "Document written with ID: " + documentReference.getId());
                        Log.d(TAG,
                                "Document written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        //TODO usersコレクションにパスを挿入
    }

    public void fetchTimeLine(List<String> uidList,
            final MutableLiveData<List<PostBean>> timeLine) {
        this.connect();

        final List<PostBean> postList = new ArrayList<>();

        System.out.println("---------------fetchTimeLine:model-----------------");

        for (String uid : uidList) {
            firestore.collection("posts")
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    System.out.println(
                                            "------------------success--------------------");
                                    System.out.println(document.getData());

                                    PostBean addPost = new PostBean();
                                    addPost.setAnonymous(document.getBoolean("anonymous"));
                                    addPost.setDanger(document.getBoolean("danger"));
                                    addPost.setDatetime(document.getDate("datetime"));
                                    GeoPoint geopoint = new GeoPoint(
                                            document.getGeoPoint("geopoint").getLatitude(),
                                            document.getGeoPoint("geopoint").getLongitude());
                                    addPost.setLat(geopoint.getLatitude());
                                    addPost.setLon(geopoint.getLongitude());
                                    addPost.setMessage(document.getString("message"));
                                    addPost.setPicture(document.getString("picture"));
                                    addPost.setType(document.getString("type"));
                                    addPost.setUid(document.getString("uid"));
                                    postList.add(addPost);
                                }
                                timeLine.setValue(postList);
                            } else {
                                System.out.println("------------------else--------------------");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("--------err:" + e + "----------");
                        }
                    });
        }
    }
}
