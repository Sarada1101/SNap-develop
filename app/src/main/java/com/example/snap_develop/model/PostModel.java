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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.i(START_LOG);
                        Timber.i(String.format("%s %s=%s", INPUT_LOG, "task", task));
                        if (task.isSuccessful()) {
                            Timber.i(SUCCESS_LOG);
                            final List<String> documentIdList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timber.i(String.format("get post document ID: %s", document.getId()));
                                documentIdList.add(document.getId());
                                PostBean postBean = new PostBean();
                                postBean.setAnonymous(document.getBoolean("anonymous"));
                                postBean.setDanger(document.getBoolean("danger"));
                                postBean.setGoodCount(document.getLong("good_count"));
                                postBean.setDatetime(document.getDate("datetime"));
                                LatLng geopoint = new LatLng(
                                        document.getGeoPoint("geopoint").getLatitude(),
                                        document.getGeoPoint("geopoint").getLongitude());
                                postBean.setLatLng(geopoint);
                                postBean.setMessage(document.getString("message"));
                                postBean.setPhotoName(document.getString("picture"));
                                postBean.setType(document.getString("type"));
                                postBean.setUid(document.getString("uid"));
                                postBeanList.add(postBean);
                            }

                            final long ONE_MEGABYTE = 1024 * 1024 * 5;
                            for (int i = 0; i < postBeanList.size(); i++) {
                                final PostBean postBean = postBeanList.get(i);
                                final int finalI = i;
                                // 投稿に画像が含まれていなかったら
                                if (postBean.getPhotoName() == null || postBean.getPhotoName().equals("")) {
                                    // もし画像を全て取得したら
                                    if (finalI == postBeanList.size() - 1) {
                                        postList.setValue(postBeanList);
                                    }
                                    continue;
                                }
                                // posts/{uid}/{photoName}
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
                                                        documentIdList.get(finalI), postBean.getPhotoName()));
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0, aByte.length);
                                                postBeanList.get(finalI).setPhoto(bitmap);

                                                // もし画像を全て取得したら
                                                if (finalI == postBeanList.size() - 1) {
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
                                            }
                                        });
                            }
                        } else {
                            Timber.i(FAILURE_LOG);
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


    public void fetchTimeLine(List<String> uidList, final MutableLiveData<List<PostBean>> postList) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", INPUT_LOG, "uidList", uidList, "postList", postList));

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
                            Timber.i(MyDebugTree.START_LOG);
                            Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                            if (task.isSuccessful()) {
                                Timber.i(MyDebugTree.SUCCESS_LOG);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Timber.i(String.format("get post document ID: %s", document.getId()));

                                    PostBean postBean = new PostBean();
                                    postBean.setAnonymous(document.getBoolean("anonymous"));
                                    postBean.setDanger(document.getBoolean("danger"));
                                    postBean.setDatetime(document.getDate("datetime"));
                                    String str = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(document.getDate("datetime"));
                                    postBean.setStrDatetime(str);
                                    LatLng geopoint = new LatLng(
                                            document.getGeoPoint("geopoint").getLatitude(),
                                            document.getGeoPoint("geopoint").getLongitude());
                                    postBean.setLatLng(geopoint);
                                    postBean.setMessage(document.getString("message"));
                                    postBean.setPhotoName(document.getString("picture"));
                                    postBean.setType(document.getString("type"));
                                    postBean.setUid(document.getString("uid"));

                                    setList.add(postBean);
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
                        postBean.setGoodCount(document.getLong("good_count"));
                        postBean.setDatetime(document.getDate("datetime"));
                        String str = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(document.getDate("datetime"));
                        postBean.setStrDatetime(str);
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
                .collection("comment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.i(START_LOG);
                        // コメントへのパスを取得する
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            commentPathList.add(document.getDocumentReference("parent_post"));
                        }

                        // コメントの詳細を取得する
                        for (DocumentReference ref : commentPathList) {
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    final DocumentSnapshot document = task.getResult();
                                    final PostBean postBean = new PostBean();
                                    documentIdList.add(document.getId());
                                    System.out.println(document.getId());
                                    System.out.println(document.getBoolean("anonymous"));
                                    postBean.setAnonymous(document.getBoolean("anonymous"));
                                    //postBean.setDanger(document.getBoolean("danger"));
                                    //postBean.setGoodCount(document.getLong("good_count"));
                                    postBean.setDatetime(document.getDate("datetime"));
                                    String str = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(document.getDate("datetime"));
                                    postBean.setStrDatetime(str);
                                    //LatLng geopoint = new LatLng(
                                    //document.getGeoPoint("geopoint").getLatitude(),
                                    //document.getGeoPoint("geopoint").getLongitude());
                                    //postBean.setLatLng(geopoint);
                                    postBean.setMessage(document.getString("message"));
                                    //postBean.setPhotoName(document.getString("picture"));
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

}
