package com.example.snap_develop.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.bean.PostBean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostModel extends Firebase {

    public void insertPost(final PostBean postBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        final Map<String, Object> post = new HashMap<>();
        post.put("message", postBean.getMessage());
        post.put("picture", postBean.getPhotoName());
        post.put("geopoint", new GeoPoint(postBean.getLatLng().latitude,
                postBean.getLatLng().longitude));
        post.put("datetime", postBean.getDatetime());
        post.put("anonymous", postBean.isAnonymous());
        post.put("danger", postBean.isDanger());
        post.put("uid", postBean.getUid());
        post.put("type", postBean.getType());

        this.firestoreConnect();
        this.storageConnect();

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
                        Map<String, Object> path = new HashMap<>();
                        path.put("path", documentReference);
                        firestore.collection("users")
                                .document(postBean.getUid())
                                .collection("posts")
                                .add(path)
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

                        StorageReference imagesRef = storage.getReference().child(
                                String.format("postPhoto/%s/%s", postBean.getUid(),
                                        postBean.getPhotoName()));
                        imagesRef.putFile(postBean.getPhoto()).addOnSuccessListener(
                                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.d(LogUtil.getClassName(),
                                                String.format("postPhoto/%s/%s", postBean.getUid(),
                                                        postBean.getPhotoName()));
                                    }
                                }).addOnFailureListener(
                                new OnFailureListener() {
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


    public void insertComment(final PostBean postBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        final Map<String, Object> post = new HashMap<>();
        post.put("message", postBean.getMessage());
        post.put("datetime", postBean.getDatetime());
        post.put("anonymous", postBean.isAnonymous());
        post.put("uid", postBean.getUid());
        post.put("type", postBean.getType());

        this.firestoreConnect();

        //コメントを追加
        firestore.collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LogUtil.getClassName(),
                                String.format("posts add document ID: %s",
                                        documentReference.getId()));

                        //パスをusersコレクションに追加
                        Map<String, Object> path = new HashMap<>();
                        path.put("path", documentReference);
                        firestore.collection("users")
                                .document(postBean.getUid())
                                .collection("posts")
                                .add(path)
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
                        //パスをpostsコレクションに追加
                        path.put("path", documentReference);
                        firestore.collection("posts")
                                .document(postBean.getUid())
                                .collection("comments")
                                .add(path)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(LogUtil.getClassName(),
                                                String.format("posts/comments add document ID: %s",
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
                });
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
