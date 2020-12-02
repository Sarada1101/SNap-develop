package com.example.snap_develop.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
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


    public void fetchTimeLine(List<String> uidList,
                              final MutableLiveData<List<PostBean>> postList) {
        this.firestoreConnect();

        final List<PostBean> setList = new ArrayList<>();

        //フォローしている人のそれぞれの投稿を取得
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
                                    LatLng geopoint = new LatLng(
                                            document.getGeoPoint("geopoint").getLatitude(),
                                            document.getGeoPoint("geopoint").getLongitude());
                                    addPost.setLatLng(geopoint);
                                    addPost.setMessage(document.getString("message"));
                                    addPost.setPhotoName(document.getString("picture"));
                                    addPost.setType(document.getString("type"));
                                    addPost.setUid(document.getString("uid"));
                                    setList.add(addPost);
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
                                    postBean.setUid(document.getString("uid"));
                                    postBean.setMessage(document.getString("message"));
                                    postBean.setLatLng(new LatLng(lat, lon));
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

    static class MySqlConnect {
        static Connection getConnection() throws Exception {
            Class.forName("com.mysql.jdbc.Driver");
            //各設定
            String url = "jdbc:mysql://localhost:3306/sNap";
            String user = "root";
            String pass = "root";
            //データベースに接続
            Connection con = DriverManager.getConnection(url, user, pass);
            return con;
        }
    }

    public void fetchSearchPost(String searchWord,
                                final MutableLiveData<List<PostBean>> postBeanList) {

        final List<String> idList = new ArrayList<>();
        final List<PostBean> setList = new ArrayList<>();

        try {
            //データベースに接続
            Connection con = MySqlConnect.getConnection();
            //ステートメントオブジェクトを作成
            PreparedStatement stmt = null;

            //SQL
            stmt = con.prepareStatement("select * from posts where message like ?;");
            stmt.setString(1, "%" + searchWord + "%");
            ResultSet rs = stmt.executeQuery();

            this.firestoreConnect();

            while (rs.next()) {
                idList.add(rs.getString("documentID"));
            }

            //オブジェクトを解放
            rs.close();
            stmt.close();
            con.close();

        } catch (Exception e) {
        }
        for (String docId : idList) {
            firestore.collection("posts")
                    .document(docId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                QueryDocumentSnapshot document = task.getResult())
                                    System.out.println(
                                            "------------------success--------------------");
                                    System.out.println(document.getData());

                                    PostBean addPost = new PostBean();
                                    addPost.setAnonymous(document.getBoolean("anonymous"));
                                    addPost.setDanger(document.getBoolean("danger"));
                                    addPost.setDatetime(document.getDate("datetime"));
                                    LatLng geopoint = new LatLng(
                                            document.getGeoPoint("geopoint").getLatitude(),
                                            document.getGeoPoint("geopoint").getLongitude());
                                    addPost.setLatLng(geopoint);
                                    addPost.setMessage(document.getString("message"));
                                    addPost.setPhotoName(document.getString("picture"));
                                    addPost.setType(document.getString("type"));
                                    addPost.setUid(document.getString("uid"));
                                    setList.add(addPost);

                                postBeanList.setValue(setList);
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
}
