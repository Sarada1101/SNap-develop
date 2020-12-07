package com.example.snap_develop.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class UserModel extends Firebase {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final static String SUCCESS = "success";

    public FirebaseUser getCurrentUser() {
        Timber.i(MyDebugTree.START_LOG);
        return firebaseAuth.getCurrentUser();
    }


    public void createAccount(String email, String password, final MutableLiveData<String> authResult) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "email", email, "authResult", authResult));
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                        if (task.isSuccessful()) {
                            Timber.i(MyDebugTree.SUCCESS_LOG);
                            authResult.setValue(SUCCESS);
                            UserBean userBean = new UserBean();
                            UserModel userModel = new UserModel();
                            userBean.setUid(userModel.getCurrentUser().getUid());
                            userBean.setName(userModel.getCurrentUser().getUid());
                            userBean.setMessage("よろしくお願いします。");
                            userBean.setIconName("no_image.png");
                            userBean.setFollowNotice(true);
                            userBean.setGoodNotice(true);
                            userBean.setCommentNotice(true);
                            userBean.setPublicationArea("public");
                            insertUser(userBean);
                        } else {
                            Timber.i(MyDebugTree.FAILURE_LOG);
                            Timber.e(task.getException().toString());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }


    public void signIn(String email, String password, final MutableLiveData<String> authResult) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "email", email, "authResult", authResult));
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                        if (task.isSuccessful()) {
                            Timber.i(MyDebugTree.SUCCESS_LOG);
                            authResult.setValue(SUCCESS);
                        } else {
                            Timber.i(MyDebugTree.FAILURE_LOG);
                            Timber.e(task.getException().toString());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }


    public void signOut() {
        Timber.i(MyDebugTree.START_LOG);
        firebaseAuth.signOut();
    }


    public void insertUser(UserBean userBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean));

        Map<String, Object> user = new HashMap<>();
        user.put("uid", userBean.getUid());
        user.put("name", userBean.getName());
        user.put("message", userBean.getMessage());
        user.put("icon", userBean.getIconName());
        user.put("followNotice", userBean.isFollowNotice());
        user.put("goodNotice", userBean.isGoodNotice());
        user.put("commentNotice", userBean.isCommentNotice());

        this.firestoreConnect();

        // users/{uid}
        firestore.collection("users")
                .document(userBean.getUid())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Timber.i(MyDebugTree.SUCCESS_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
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


    public void updateUser(UserBean userBean, byte[] data,
            final MutableLiveData<String> updateResult) {
        Timber.i(MyDebugTree.START_LOG);

        //画像をstorageに保存
//        imgUpload(data, userBean.getIconName());

        this.firestoreConnect();

        firestore.collection("users")
                .document(userBean.getUid())
                .update(
                        "name", userBean.getName(),
                        "message", userBean.getMessage(),
                        "icon", userBean.getIcon()
                )
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(LogUtil.getClassName(), "updateUser:success");
                                updateResult.setValue(SUCCESS);
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LogUtil.getClassName(), "updateUser:failure", e);
                        updateResult.setValue(String.valueOf(e));
                    }
                });
    }


    public void fetchUserInfo(final String uid, final MutableLiveData<UserBean> user) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "user", user));

        this.firestoreConnect();
        this.storageConnect();

        final UserBean userBean = new UserBean();

        //datetimeが1時間前より大きいかつ、typeがpostの投稿を取得する
        //users/{uid}
        firestore.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                        if (task.isSuccessful()) {
                            Timber.i(MyDebugTree.SUCCESS_LOG);
                            DocumentSnapshot document = task.getResult();
                            userBean.setName(document.getString("name"));
                            userBean.setUid(document.getId());
                            userBean.setMessage(document.getString("message"));
                            userBean.setIconName(document.getString("icon"));
                            userBean.setFollowingCount(document.getLong("following_count"));
                            userBean.setFollowerCount(document.getLong("follower_count"));
                        }

                        final long ONE_MEGABYTE = 1024 * 1024 * 5;
                        // icon/{uid}/{iconName}
                        storage.getReference()
                                .child("icon")
                                .child(userBean.getUid())
                                .child(userBean.getIconName())
                                .getBytes(ONE_MEGABYTE)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] aByte) {
                                        Timber.i(MyDebugTree.SUCCESS_LOG);
                                        Timber.i(String.format("path=/%s/%s/%s", "icon", userBean.getUid(),
                                                userBean.getIconName()));
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0, aByte.length);
                                        userBean.setIcon(bitmap);
                                        user.setValue(userBean);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Timber.i(MyDebugTree.FAILURE_LOG);
                                        Timber.i(String.format("path=/%s/%s/%s", "icon", userBean.getUid(),
                                                userBean.getIconName()));
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


    public void fetchUserInfoList(final List<String> uidList, final MutableLiveData<List<UserBean>> userList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uidList", uidList, "userList", userList));

        this.firestoreConnect();

        final List<UserBean> userBeanList = new ArrayList<>();

        for (int i = 0; i < uidList.size(); i++) {
            String uid = uidList.get(i);
            final int finalI = i;
            firestore.collection("users")
                    .document(uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Timber.i(MyDebugTree.SUCCESS_LOG);
                            Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                            DocumentSnapshot document = task.getResult();
                            final UserBean userBean = new UserBean();
                            userBean.setName(document.getString("name"));
                            userBean.setUid(document.getId());
                            userBean.setIconName(document.getString("icon"));

                            final long ONE_MEGABYTE = 1024 * 1024 * 5;
                            // icon/{uid}/{iconName}
                            storage.getReference()
                                    .child("icon")
                                    .child(userBean.getUid())
                                    .child(userBean.getIconName())
                                    .getBytes(ONE_MEGABYTE)
                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] aByte) {
                                            Timber.i(MyDebugTree.SUCCESS_LOG);
                                            Timber.i(String.format("path=/%s/%s/%s", "icon", userBean.getUid(),
                                                    userBean.getIconName()));
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0, aByte.length);
                                            userBean.setIcon(bitmap);
                                            userBeanList.add(userBean);

                                            // もし画像を全て取得したら
                                            if (userBeanList.size() >= uidList.size()) {
                                                userList.setValue(userBeanList);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Timber.i(MyDebugTree.FAILURE_LOG);
                                            Timber.i(String.format("path=/%s/%s/%s", "icon", userBean.getUid(),
                                                    userBean.getIconName()));
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
    }

//
//    //storageに画像をアップロードするメソッド
//    public void imgUpload(byte[] data, String path) {
//        Timber.i(MyDebugTree.START_LOG);
//        // Create a storage reference from our app
//        StorageReference storageRef = storage.getReference();
//
//        // Create a reference
//        StorageReference userImagesRef = storageRef.child(path);
//
//        UploadTask uploadTask = userImagesRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//                System.out.println("--------No upload------");
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type,
//                // etc.
//                // ...
//                System.out.println("--------Yes upload-------");
//            }
//        });
//    }
}
