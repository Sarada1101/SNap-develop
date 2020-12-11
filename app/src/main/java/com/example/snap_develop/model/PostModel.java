package com.example.snap_develop.model;

import static com.example.snap_develop.MyDebugTree.FAILURE_LOG;
import static com.example.snap_develop.MyDebugTree.INPUT_LOG;
import static com.example.snap_develop.MyDebugTree.START_LOG;
import static com.example.snap_develop.MyDebugTree.SUCCESS_LOG;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import timber.log.Timber;

public class PostModel extends Firebase {

    public void insertPost(final PostBean postBean) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s", INPUT_LOG, "postBean", postBean));
        final Map<String, Object> post = new HashMap<>();
        post.put("message", postBean.getMessage());
        post.put("picture", postBean.getPhotoName());
        post.put("good_count", 0);
        post.put("geopoint", new GeoPoint(postBean.getLatLng().latitude, postBean.getLatLng().longitude));
        post.put("datetime", postBean.getDatetime());
        post.put("anonymous", postBean.isAnonymous());
        post.put("danger", postBean.isDanger());
        post.put("uid", postBean.getUid());
        post.put("type", postBean.getType());

        this.firestoreConnect();
        this.storageConnect();

        final String[] documentId = new String[1];

        //投稿を追加
        // posts/{documentId}
        firestore.collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Timber.i(SUCCESS_LOG);
                        Timber.i(String.format("%s %s=%s", INPUT_LOG, "documentReference",
                                documentReference));
                        Timber.i(String.format("add posts document ID: %s", documentReference.getId()));
                        documentId[0] = documentReference.getId();

                        //パスをusersコレクションに追加
                        Map<String, Object> path = new HashMap<>();
                        path.put("path", documentReference);
                        // users/{uid}/posts/
                        firestore.collection("users")
                                .document(postBean.getUid())
                                .collection("posts")
                                .add(path)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Timber.i(SUCCESS_LOG);
                                        Timber.i(String.format("%s %s=%s", INPUT_LOG, "documentReference",
                                                documentReference));
                                        Timber.i(String.format("add users/posts document ID: %s",
                                                documentReference.getId()));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Timber.i(FAILURE_LOG);
                                        Timber.e(e.toString());
                                    }
                                });

                        if (postBean.getPhoto() != null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            postBean.getPhoto().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            storage.getReference()
                                    .child("postPhoto")
                                    .child(documentId[0])
                                    .child(postBean.getPhotoName())
                                    .putBytes(baos.toByteArray()).addOnSuccessListener(
                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Timber.i(SUCCESS_LOG);
                                            Timber.i(
                                                    String.format("%s %s=%s", INPUT_LOG, "taskSnapshot", taskSnapshot));
                                            Timber.i(String.format("save image path: %s",
                                                    String.format("postPhoto/%s/%s", documentId[0],
                                                            postBean.getPhotoName())));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Timber.i(FAILURE_LOG);
                                            Timber.e(e.toString());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(FAILURE_LOG);
                        Timber.e(e.toString());
                    }
                });
    }


    public void insertComment(final PostBean postBean) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s", INPUT_LOG, "postBean", postBean));

        this.firestoreConnect();

        final Map<String, Object> post = new HashMap<>();
        post.put("message", postBean.getMessage());
        post.put("datetime", postBean.getDatetime());
        post.put("anonymous", postBean.isAnonymous());
        post.put("uid", postBean.getUid());
        post.put("type", postBean.getType());
        DocumentReference perent_post = firestore.collection("posts").document(postBean.getPostPath());
        post.put("perent_post", perent_post);

        //コメントを追加
        firestore.collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Timber.i(SUCCESS_LOG);
                        Timber.i(String.format("%s %s=%s", INPUT_LOG, "documentReference", documentReference));
                        Timber.i(String.format("add posts document ID: %s", documentReference.getId()));

                        //パスをusersコレクションに追加
                        Map<String, Object> path = new HashMap<>();
                        path.put("path", documentReference);
                        // users/{uid}/posts/
                        firestore.collection("users")
                                .document(postBean.getUid())
                                .collection("posts")
                                .add(path)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Timber.i(SUCCESS_LOG);
                                        Timber.i(String.format("%s %s=%s", INPUT_LOG, "documentReference",
                                                documentReference));
                                        Timber.i(String.format("add users/posts document ID: %s",
                                                documentReference.getId()));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Timber.i(FAILURE_LOG);
                                        Timber.e(e.toString());
                                    }
                                });

                        //パスをpostsコレクションに追加
                        path.put("path", documentReference);
                        // posts/{uid}/comments
                        firestore.collection("posts")
                                .document(postBean.getPostPath())
                                .collection("comments")
                                .add(path)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Timber.i(SUCCESS_LOG);
                                        Timber.i(String.format("%s %s=%s", INPUT_LOG, "documentReference",
                                                documentReference));
                                        Timber.i(String.format("add posts/comments document ID: %s",
                                                documentReference.getId()));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Timber.i(FAILURE_LOG);
                                        Timber.e(e.toString());
                                    }
                                });
                    }
                });
    }


    public void fetchPostList(String uid, final MutableLiveData<List<PostBean>> postList) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", INPUT_LOG, "uidList", uid, "postList", postList));

        this.firestoreConnect();
        this.storageConnect();

        final List<PostBean> postBeanList = new ArrayList<>();

        // posts/{documentId}
        firestore.collection("posts")
                .whereEqualTo("uid", uid)
                .orderBy("datetime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.i(SUCCESS_LOG);
                        Timber.i(String.format("%s %s=%s", INPUT_LOG, "task", task));
                        final List<String> documentIdList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timber.i(String.format("get post document ID: %s", document.getId()));
                            documentIdList.add(document.getId());
                            PostBean postBean = new PostBean();
                            postBean.setDocumentId(document.getId());
                            postBean.setAnonymous(document.getBoolean("anonymous"));
                            postBean.setDatetime(document.getDate("datetime"));
                            postBean.setStrDatetime(
                                    new SimpleDateFormat("yyyy/MM/dd hh:mm").format(document.getDate("datetime")));
                            postBean.setMessage(document.getString("message"));
                            postBean.setType(document.getString("type"));
                            postBean.setUid(document.getString("uid"));

                            if (postBean.getType().equals("post")) {
                                postBean.setPhotoName(document.getString("picture"));
                                postBean.setDanger(document.getBoolean("danger"));
                                postBean.setGoodCount(Integer.parseInt(document.getLong("good_count").toString()));
                                LatLng geopoint = new LatLng(
                                        document.getGeoPoint("geopoint").getLatitude(),
                                        document.getGeoPoint("geopoint").getLongitude());
                                postBean.setLatLng(geopoint);
                            }
                            postBeanList.add(postBean);
                        }

                        final int[] count = {0};
                        final long ONE_MEGABYTE = 1024 * 1024 * 5;
                        for (int i = 0; i < postBeanList.size(); i++) {
                            final PostBean postBean = postBeanList.get(i);

                            if (postBean.getPhotoName() == null || postBean.getPhotoName().equals("")) {
                                postBean.setPhotoName(" ");
                            }

                            // posts/{uid}/{photoName}
                            final int finalI = i;
                            storage.getReference()
                                    .child("postPhoto")
                                    .child(documentIdList.get(i))
                                    .child(postBean.getPhotoName())
                                    .getBytes(ONE_MEGABYTE)
                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] aByte) {
                                            Timber.i(SUCCESS_LOG);
                                            Timber.i(String.format("path=/%s/%s/%s", "postPhoto",
                                                    documentIdList.get(finalI),
                                                    postBeanList.get(finalI).getPhotoName()));
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0, aByte.length);
                                            postBeanList.get(finalI).setPhoto(bitmap);
                                            count[0]++;

                                            // もし画像を全て取得したら
                                            if (count[0] == postBeanList.size()) {
                                                postList.setValue(postBeanList);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Timber.i(FAILURE_LOG);
                                            Timber.i(String.format("path=/%s/%s/%s", "postPhoto",
                                                    documentIdList.get(finalI), postBean.getPhotoName()));
                                            Timber.e(e.toString());
                                            count[0]++;

                                            // もし画像を全て取得したら
                                            if (count[0] == postBeanList.size()) {
                                                postList.setValue(postBeanList);
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(MyDebugTree.FAILURE_LOG);
                        Timber.e(e.toString());
                    }
                });
    }


    public void fetchTimeLine(List<UserBean> userList, final MutableLiveData<List<PostBean>> postList) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", INPUT_LOG, "userList", userList, "postList", postList));

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
                            Timber.i(MyDebugTree.START_LOG);
                            Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    Timber.i(SUCCESS_LOG);
                                    System.out.println(document.getData());
                                    final PostBean addPost = new PostBean();

                                    if (!document.getString("picture").isEmpty()) {
                                        addPost.setPhotoName(document.getString("picture"));
                                    }

                                    PostBean postBean = new PostBean();
                                    postBean.setAnonymous(document.getBoolean("anonymous"));
                                    postBean.setDatetime(document.getDate("datetime"));
                                    postBean.setStrDatetime(new SimpleDateFormat("yyyy/MM/dd hh:mm").format(
                                            document.getDate("datetime")));
                                    postBean.setMessage(document.getString("message"));
                                    postBean.setType(document.getString("type"));
                                    postBean.setUid(document.getString("uid"));
                                    addPost.setDocumentId(document.getId());

                                    if (postBean.getType().equals("post")) {
                                        postBean.setPhotoName(document.getString("picture"));
                                        postBean.setDanger(document.getBoolean("danger"));
                                        postBean.setGoodCount(
                                                Integer.parseInt(document.getLong("good_count").toString()));
                                        LatLng geopoint = new LatLng(
                                                document.getGeoPoint("geopoint").getLatitude(),
                                                document.getGeoPoint("geopoint").getLongitude());
                                        postBean.setLatLng(geopoint);
                                    }
                                    setList.add(addPost);
                                }
                                postList.setValue(setList);
                            } else {
                                Timber.i(MyDebugTree.FAILURE_LOG);
                                Timber.e(task.getException().toString());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.i(MyDebugTree.FAILURE_LOG);
                            Timber.e(e.toString());
                        }
                    });
        }
    }

    //ここから
    public void fetchPost(String postPath, final MutableLiveData<PostBean> post) {
        Timber.i(MyDebugTree.START_LOG);

        this.firestoreConnect();
        this.storageConnect();

        firestore.collection("posts")
                .document(postPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Timber.i(MyDebugTree.SUCCESS_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                        final DocumentSnapshot document = task.getResult();
                        final PostBean postBean = new PostBean();
                        postBean.setAnonymous(document.getBoolean("anonymous"));
                        postBean.setDanger(document.getBoolean("danger"));
                        postBean.setGoodCount(Integer.parseInt(document.getLong("good_count").toString()));
                        postBean.setDatetime(document.getDate("datetime"));
                        postBean.setStrDatetime(
                                new SimpleDateFormat("yyyy/MM/dd hh:mm").format(document.getDate("datetime")));
                        LatLng geopoint = new LatLng(
                                document.getGeoPoint("geopoint").getLatitude(),
                                document.getGeoPoint("geopoint").getLongitude());
                        postBean.setLatLng(geopoint);
                        postBean.setMessage(document.getString("message"));
                        postBean.setPhotoName(document.getString("picture"));
                        postBean.setType(document.getString("type"));
                        postBean.setUid(document.getString("uid"));

                        final long ONE_MEGABYTE = 1024 * 1024 * 5;
                        // posts/{uid}/{photoName}
                        storage.getReference()
                                .child("postPhoto")
                                .child(document.getId())
                                .child(postBean.getPhotoName())
                                .getBytes(ONE_MEGABYTE)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] aByte) {
                                        Timber.i(SUCCESS_LOG);
                                        Timber.i(String.format("path=/%s/%s/%s", "postPhoto", document.getId(),
                                                postBean.getPhotoName()));
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0, aByte.length);
                                        postBean.setPhoto(bitmap);
                                        post.setValue(postBean);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Timber.i(FAILURE_LOG);
                                        Timber.i(String.format("path=/%s/%s/%s", "postPhoto", document.getId(),
                                                postBean.getPhotoName()));
                                        Timber.e(e.toString());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(MyDebugTree.FAILURE_LOG);
                        Timber.e(e.toString());
                    }
                });
    }


    public void fetchPostCommentList(String postPath, final MutableLiveData<List<PostBean>> postList) {
        this.firestoreConnect();

        final List<DocumentReference> commentPathList = new ArrayList<>();
        final List<PostBean> postBeanList = new ArrayList<>();
        final List<String> documentIdList = new ArrayList<>();

        firestore.collection("posts")
                .document(postPath)
                .collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.i(START_LOG);
                        // コメントへのパスを取得する
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            commentPathList.add(document.getDocumentReference("path"));
                        }

                        // コメントの詳細を取得する
                        for (DocumentReference ref : commentPathList) {
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    final DocumentSnapshot document = task.getResult();
                                    final PostBean postBean = new PostBean();
                                    documentIdList.add(document.getId());
                                    postBean.setAnonymous(document.getBoolean("anonymous"));
                                    postBean.setDatetime(document.getDate("datetime"));
                                    postBean.setStrDatetime(new SimpleDateFormat("yyyy/MM/dd hh:mm").format(
                                            document.getDate("datetime")));
                                    postBean.setMessage(document.getString("message"));
                                    postBean.setType(document.getString("type"));
                                    postBean.setUid(document.getString("uid"));
                                    postBeanList.add(postBean);
                                    if (postBeanList.size() >= commentPathList.size()) {
                                        postList.setValue(postBeanList);
                                    }
                                }
                            });
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(MyDebugTree.FAILURE_LOG);
                        Timber.e(e.toString());
                    }
                });
    }


    public void fetchMapPostList(final VisibleRegion visibleRegion, final MutableLiveData<List<PostBean>> postList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "visibleRegion", visibleRegion, "postList",
                postList));

//表示されている地図の北東と南西の緯度経度
        final double topLatitude = visibleRegion.latLngBounds.northeast.latitude;
        final double rightLongitude = visibleRegion.latLngBounds.northeast.longitude;
        final double bottomLatitude = visibleRegion.latLngBounds.southwest.latitude;
        final double leftLongitude = visibleRegion.latLngBounds.southwest.longitude;

        this.firestoreConnect();

        final List<PostBean> postBeanList = new ArrayList<>();

        //datetimeが1時間前より大きいかつ、typeがpostの投稿を取得する
        // posts/{documentId}
        firestore.collection("posts")
                .whereGreaterThan("datetime", new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .whereEqualTo("type", "post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                        if (task.isSuccessful()) {
                            Timber.i(MyDebugTree.SUCCESS_LOG);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint geoPoint = document.getGeoPoint("geopoint");
                                double lat = geoPoint.getLatitude();
                                double lon = geoPoint.getLongitude();

                                //投稿位置が画面内に入るなら
                                if ((lat <= topLatitude && lon <= rightLongitude) && (lat >= bottomLatitude
                                        && lon >= leftLongitude)) {
                                    PostBean postBean = new PostBean();
                                    postBean.setMessage(document.getString("message"));
                                    postBean.setLatLng(new LatLng(lat, lon));
                                    postBean.setPostPath(document.getReference().getPath());

                                    postBeanList.add(postBean);
                                    Timber.i(String.format("get post document ID: %s", document.getId()));
                                } else {
                                    Timber.i(String.format("out of range document ID: %s", document.getId()));
                                }
                            }
                            postList.setValue(postBeanList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(MyDebugTree.FAILURE_LOG);
                        Timber.e(e.toString());
                    }
                });
    }


    public Connection getConnection() throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int rsno = 0;
        DataSource ds = null;
        try {
            ///////////////////////////////////

            Class.forName("com.mysql.cj.jdbc.Driver");

            String databaseName = "posts";
            String instanceConnectionName = "intense-pointer-297407:us-central1:test-snap";
            String jdbcUrl = String.format(
                    "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                            + "socketFactory=com.google.cloud.sql.mysql.SocketFactory&serverTimezone=JST&useSSL=false",
                    databaseName,
                    instanceConnectionName);

            con = DriverManager.getConnection(jdbcUrl,
                    "root", "root");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;

    }


    public void fetchSearchPost(String searchWord,
            final MutableLiveData<List<PostBean>> postBeanList) {

        final List<String> idList = new ArrayList<>();
        final List<PostBean> setList = new ArrayList<>();

        try {
            //データベースに接続
            Connection con = getConnection();
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
                                DocumentSnapshot document = task.getResult();
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

    public void addGood(final String userPath, final String postPath) {
        this.firestoreConnect();

        final DocumentReference goodPost = firestore.collection("posts").document(postPath);

        firestore.collection("users")
                .document(userPath)
                .collection("good_posts")
                .whereEqualTo("path", goodPost)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            firestore.collection("posts")
                                    .document(postPath)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot doc = task.getResult();

                                            Integer updateGood = Integer.valueOf(String.valueOf(doc.get("good_count")));
                                            updateGood++;

                                            firestore.collection("posts")
                                                    .document(postPath)
                                                    .update("good_count", updateGood)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            Log.d(LogUtil.getClassName(), "update(updategood_count)
//                                                            :success");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
//                                                            Log.w(LogUtil.getClassName(), "update(good_count)
//                                                            :failure", e);
                                                        }
                                                    });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(LogUtil.getClassName(), "update(good_count):failure", e);
                                }
                            });

                            Map<String, Object> addData = new HashMap<>();
                            addData.put("path", goodPost);

                            firestore.collection("users")
                                    .document(userPath)
                                    .collection("good_posts")
                                    .document(postPath)
                                    .set(addData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Log.d(LogUtil.getClassName(), "insertGoodPath:success");
                                            System.out.println(
                                                    "-----------------insertGoodPath:comp----------------");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w(LogUtil.getClassName(), "insertGoodPath:failure:" + e);
                                        }
                                    });
                        } else {
//                            Log.w(LogUtil.getClassName(), "Already good");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LogUtil.getClassName(), "insertGoodPath:failure:" + e);
                    }
                });
    }

    public void fetchPostPictures(Map<String, String> pathList,
            final MutableLiveData<Map<String, Bitmap>> timeLinePictureList) {
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
