package com.example.snap_develop.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

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
                                String.format("add posts document ID: %s",
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
                                                String.format("add users/posts document ID: %s",
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


    public void fetchTimeLine(List<UserBean> userList,
            final MutableLiveData<List<PostBean>> postList) {
        this.firestoreConnect();
        this.storageConnect();

        final List<PostBean> setList = new ArrayList<>();

        //フォローしている人のそれぞれの投稿を取得
        for (UserBean bean : userList) {
            firestore.collection("posts")
                    .whereEqualTo("uid", bean.getUid())
                    .whereEqualTo("type", "post")
                    .orderBy("datetime", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    System.out.println(
                                            "------------------success--------------------");
                                    System.out.println(document.getData());
                                    final PostBean addPost = new PostBean();

                                    if (!document.getString("picture").isEmpty()) {
                                        addPost.setPhotoName(document.getString("picture"));
                                    } else {
                                        System.out.println("no");
                                    }

                                    addPost.setAnonymous(document.getBoolean("anonymous"));
                                    addPost.setDanger(document.getBoolean("danger"));
                                    addPost.setDatetime(document.getDate("datetime"));
                                    LatLng geopoint = new LatLng(
                                            document.getGeoPoint("geopoint").getLatitude(),
                                            document.getGeoPoint("geopoint").getLongitude());
                                    addPost.setLatLng(geopoint);
                                    addPost.setMessage(document.getString("message"));
                                    addPost.setType(document.getString("type"));
                                    addPost.setUid(document.getString("uid"));
                                    addPost.setPostId(document.getId());
                                    addPost.setGoodCount(Integer.valueOf(String.valueOf(document.get("good_count"))));
                                    setList.add(addPost);
                                    System.out.println("------------------fetchTimeLine:Success!!------------------");
                                }
                                postList.setValue(setList);
                            } else {
                                System.out.println("------------------else" + task.getException()
                                        + "--------------------");
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


    public void fetchMapPostList(final VisibleRegion visibleRegion,
            final MutableLiveData<List<PostBean>> postList) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());

        //表示されている地図の北東と南西の緯度経度
        final double topLatitude = visibleRegion.latLngBounds.northeast.latitude;
        final double rightLongitude = visibleRegion.latLngBounds.northeast.longitude;
        final double bottomLatitude = visibleRegion.latLngBounds.southwest.latitude;
        final double leftLongitude = visibleRegion.latLngBounds.southwest.longitude;

        this.firestoreConnect();

        final List<PostBean> postBeanList = new ArrayList<>();

        //datetimeが1時間前より大きいかつ、typeがpostの投稿を取得する
        firestore.collection("posts")
                .whereGreaterThan("datetime", new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .whereEqualTo("type", "post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint geoPoint = document.getGeoPoint("geopoint");
                                double lat = geoPoint.getLatitude();
                                double lon = geoPoint.getLongitude();
                                //投稿位置が画面内に入るなら
                                if ((lat <= topLatitude && lon <= rightLongitude) &&
                                        (lat >= bottomLatitude && lon >= leftLongitude)) {
                                    PostBean postBean = new PostBean();
                                    postBean.setMessage(document.getString("message"));
                                    postBean.setLatLng(new LatLng(lat, lon));
                                    postBean.setPostPath(document.getReference().getPath());
                                    Log.d(LogUtil.getClassName(),
                                            String.format("get post document ID: %s",
                                                    document.getId()));
                                    postBeanList.add(postBean);
                                } else {
                                    Log.d(LogUtil.getClassName(),
                                            String.format("out of range post document ID: %s",
                                                    document.getId()));
                                }
                            }
                            postList.setValue(postBeanList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LogUtil.getClassName(), e);
                    }
                });
    }

    public void fetchPostPictures(Map<String, String> pathList, final MutableLiveData<Map<String, Bitmap>> timeLinePictureList) {
        this.storageConnect();
        final Map<String, Bitmap> addData = new HashMap<>();

        for (final Map.Entry<String, String> entry : pathList.entrySet()) {
            final long FIVE_MEGABYTE = 1024 * 1024 * 5;
            storage.getReference()
                    .child("postPhoto")
                    .child(entry.getKey())
                    .child(entry.getValue())
                    .getBytes(FIVE_MEGABYTE)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] aByte) {
                            Timber.i(MyDebugTree.SUCCESS_LOG);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0, aByte.length);
                            addData.put(entry.getKey(), bitmap);
                            timeLinePictureList.setValue(addData);
                            System.out.println("---------------postPicture:Success-----------------");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.i(MyDebugTree.FAILURE_LOG);
                            Timber.e(e.toString());
                            System.out.println("--------------postPicture:false-----------------");
                        }
                    });
        }
    }
}
