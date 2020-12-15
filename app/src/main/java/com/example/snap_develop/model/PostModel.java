package com.example.snap_develop.model;

import static com.example.snap_develop.MyDebugTree.FAILURE_LOG;
import static com.example.snap_develop.MyDebugTree.INPUT_LOG;
import static com.example.snap_develop.MyDebugTree.START_LOG;
import static com.example.snap_develop.MyDebugTree.SUCCESS_LOG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.service.ConnectApiService;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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
        post.put("parent_post", firestore.document(postBean.getPostPath()));

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
                        // posts/{parentPostPath}/comments
                        firestore.document(postBean.getPostPath())
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


    public void fetchPost(String postPath, final MutableLiveData<PostBean> post) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", INPUT_LOG, "postPath", postPath, "post", post));

        this.firestoreConnect();
        this.storageConnect();

        firestore.document(postPath)
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
                                new SimpleDateFormat("yyyy/MM/dd HH:mm").format(document.getDate("datetime")));
                        LatLng geopoint = new LatLng(
                                document.getGeoPoint("geopoint").getLatitude(),
                                document.getGeoPoint("geopoint").getLongitude());
                        postBean.setLatLng(geopoint);
                        postBean.setMessage(document.getString("message"));
                        postBean.setPhotoName(document.getString("picture"));
                        if (postBean.getPhotoName().equals("")) postBean.setPhotoName(" ");
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
                                        post.setValue(postBean);
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
                                    new SimpleDateFormat("yyyy/MM/dd HH:mm").format(document.getDate("datetime")));
                            postBean.setMessage(document.getString("message"));
                            postBean.setType(document.getString("type"));
                            postBean.setUid(document.getString("uid"));
                            postBean.setPostPath(document.getReference().getPath());

                            if (postBean.getType().equals("post")) {
                                postBean.setPhotoName(document.getString("picture"));
                                postBean.setDanger(document.getBoolean("danger"));
                                postBean.setGoodCount(Integer.parseInt(document.getLong("good_count").toString()));
                                LatLng geopoint = new LatLng(
                                        document.getGeoPoint("geopoint").getLatitude(),
                                        document.getGeoPoint("geopoint").getLongitude());
                                postBean.setLatLng(geopoint);
                            } else if (postBean.getType().equals("comment")) {
                                postBean.setParentPost(document.getDocumentReference("parent_post").getPath());
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


    public void fetchPostCommentList(String postPath, final MutableLiveData<List<PostBean>> postList) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", INPUT_LOG, "postPath", postPath, "postList", postList));

        this.firestoreConnect();

        final List<DocumentReference> commentPathList = new ArrayList<>();
        final List<PostBean> postBeanList = new ArrayList<>();
        final List<String> documentIdList = new ArrayList<>();

        // コメントへのパスを取得する
        firestore.document(postPath)
                .collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.i(START_LOG);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            commentPathList.add(document.getDocumentReference("path"));
                            Timber.i(String.format("%s=%s", "commentPath", document.getDocumentReference("path")));
                        }

                        // コメントの詳細を取得する
                        for (int i = 0; i < commentPathList.size(); i++) {
                            final int finalI = i;
                            DocumentReference ref = commentPathList.get(i);
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Timber.i(START_LOG);
                                    Timber.i(String.format("%s %s=%s", INPUT_LOG, "task", task));

                                    DocumentSnapshot document = task.getResult();
                                    PostBean postBean = new PostBean();
                                    postBean.setDocumentId(document.getId());
                                    postBean.setAnonymous(document.getBoolean("anonymous"));
                                    postBean.setDatetime(document.getDate("datetime"));
                                    postBean.setStrDatetime(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(
                                            document.getDate("datetime")));
                                    postBean.setMessage(document.getString("message"));
                                    postBean.setType(document.getString("type"));
                                    postBean.setUid(document.getString("uid"));
                                    postBeanList.add(postBean);
                                    if (finalI == commentPathList.size() - 1) {
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


    public void fetchTimeLine(final List<UserBean> userBeanList, final MutableLiveData<List<PostBean>> postList) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", INPUT_LOG, "userList", userBeanList, "postList", postList));

        this.firestoreConnect();
        this.storageConnect();

        final List<String> documentIdList = new ArrayList<>();
        final List<PostBean> postBeanList = new ArrayList<>();

        //フォローしている人のそれぞれの投稿を取得
        for (int i = 0; i < userBeanList.size(); i++) {
            final int finalI = i;
            UserBean userBean = userBeanList.get(i);
            firestore.collection("posts")
                    .whereEqualTo("uid", userBean.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Timber.i(SUCCESS_LOG);
                            Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timber.i(String.format("get post document ID: %s", document.getId()));
                                documentIdList.add(document.getId());
                                PostBean postBean = new PostBean();
                                postBean.setDocumentId(document.getId());
                                postBean.setAnonymous(document.getBoolean("anonymous"));
                                postBean.setDatetime(document.getDate("datetime"));
                                postBean.setStrDatetime(
                                        new SimpleDateFormat("yyyy/MM/dd HH:mm").format(document.getDate("datetime")));
                                postBean.setMessage(document.getString("message"));
                                postBean.setType(document.getString("type"));
                                postBean.setUid(document.getString("uid"));
                                postBean.setPostPath(document.getReference().getPath());

                                if (postBean.getType().equals("post")) {
                                    postBean.setPhotoName(document.getString("picture"));
                                    postBean.setDanger(document.getBoolean("danger"));
                                    postBean.setGoodCount(Integer.parseInt(document.getLong("good_count").toString()));
                                    LatLng geopoint = new LatLng(
                                            document.getGeoPoint("geopoint").getLatitude(),
                                            document.getGeoPoint("geopoint").getLongitude());
                                    postBean.setLatLng(geopoint);
                                } else if (postBean.getType().equals("comment")) {
                                    postBean.setParentPost(document.getDocumentReference("parent_post").getPath());
                                }
                                postBeanList.add(postBean);
                            }

                            // 投稿リストを全て取得したら
                            if (finalI == userBeanList.size() - 1) {
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
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0,
                                                            aByte.length);
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


    public void fetchSearchPost(String searchWord, final MutableLiveData<List<PostBean>> postList) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", INPUT_LOG, "searchWord", searchWord, "postList", postList));

        firestoreConnect();
        storageConnect();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConnectApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ConnectApiService connectApiService = retrofit.create(ConnectApiService.class);

        final List<PostBean> postBeanList = new ArrayList<>();
        final List<String> documentIdList = new ArrayList<>();

        // 検索条件に合う投稿をmysqlから取得
        connectApiService.searchPost(searchWord).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Timber.i(START_LOG);
                Timber.i(String.valueOf(response));
                List<Map<String, String>> dataList = (List<Map<String, String>>) response.body().get("data");

                // 投稿ごとの詳細を取得
                for (int i = 0; i < dataList.size(); i++) {
                    final int finalI = i;
                    firestore.collection("posts")
                            .document(dataList.get(i).get("id"))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    Timber.i(String.format("get post document ID: %s", document.getId()));
                                    documentIdList.add(document.getId());
                                    final PostBean postBean = new PostBean();
                                    postBean.setDocumentId(document.getId());
                                    postBean.setAnonymous(document.getBoolean("anonymous"));
                                    postBean.setDatetime(document.getDate("datetime"));
                                    postBean.setStrDatetime(
                                            new SimpleDateFormat("yyyy/MM/dd HH:mm").format(
                                                    document.getDate("datetime")));
                                    postBean.setMessage(document.getString("message"));
                                    postBean.setType(document.getString("type"));
                                    postBean.setUid(document.getString("uid"));
                                    postBean.setPostPath(document.getReference().getPath());

                                    if (postBean.getType().equals("post")) {
                                        postBean.setPhotoName(document.getString("picture"));
                                        postBean.setDanger(document.getBoolean("danger"));
                                        postBean.setGoodCount(
                                                Integer.parseInt(document.getLong("good_count").toString()));
                                        LatLng geopoint = new LatLng(
                                                document.getGeoPoint("geopoint").getLatitude(),
                                                document.getGeoPoint("geopoint").getLongitude());
                                        postBean.setLatLng(geopoint);
                                    } else if (postBean.getType().equals("comment")) {
                                        postBean.setParentPost(document.getDocumentReference("parent_post").getPath());
                                    }
                                    postBeanList.add(postBean);

                                    // 投稿リストを全て取得したら
                                    if (finalI == postBeanList.size() - 1) {
                                        final int[] count = {0};
                                        final long ONE_MEGABYTE = 1024 * 1024 * 5;
                                        for (int i = 0; i < postBeanList.size(); i++) {
                                            final PostBean postDataBean = postBeanList.get(i);

                                            if (postDataBean.getPhotoName() == null
                                                    || postDataBean.getPhotoName().equals("")) {
                                                postDataBean.setPhotoName(" ");
                                            }

                                            // posts/{uid}/{photoName}
                                            final int finalI = i;
                                            storage.getReference()
                                                    .child("postPhoto")
                                                    .child(documentIdList.get(i))
                                                    .child(postDataBean.getPhotoName())
                                                    .getBytes(ONE_MEGABYTE)
                                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                        @Override
                                                        public void onSuccess(byte[] aByte) {
                                                            Timber.i(SUCCESS_LOG);
                                                            Timber.i(String.format("path=/%s/%s/%s", "postPhoto",
                                                                    documentIdList.get(finalI),
                                                                    postBeanList.get(finalI).getPhotoName()));
                                                            Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0,
                                                                    aByte.length);
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
                                                                    documentIdList.get(finalI),
                                                                    postDataBean.getPhotoName()));
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

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Timber.e(FAILURE_LOG);
                Timber.e(String.valueOf(call));
                Timber.e(String.valueOf(t));
            }
        });
    }


    public void addGood(final String uid, final String postPath) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "postPath", postPath));

        this.firestoreConnect();

        firestore.document(postPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Timber.i(MyDebugTree.SUCCESS_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                        int goodCount = Integer.parseInt(String.valueOf(task.getResult().getLong("good_count")));

                        firestore.document(postPath)
                                .update("good_count", goodCount + 1)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Timber.i(MyDebugTree.SUCCESS_LOG);
                                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                                        callGoodNotification(uid, postPath);
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(MyDebugTree.FAILURE_LOG);
                        Timber.e(e.toString());
                    }
                });
    }


    public void callGoodNotification(String uid, String postPath) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("postPath", postPath);

        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
        mFunctions.getHttpsCallable("goodNotification")
                .call(data)
                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            System.out.println(e);
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            // ...
                        } else {
                            Timber.i(MyDebugTree.SUCCESS_LOG);
                        }
                        //Timber.d(String.format("%s : %s", "result", (String) task.getResult().getData()));
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
