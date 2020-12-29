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
import com.example.snap_develop.bean.UserBean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class UserModel extends Firebase {

    private final long IMAGE_SIZE = 1024 * 1024 * 100;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public FirebaseUser getCurrentUser() {
        Timber.i(MyDebugTree.START_LOG);
        return firebaseAuth.getCurrentUser();
    }


    public void createAccount(String email, String password, final UserBean userBean,
            final MutableLiveData<String> authResult) {
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
                            authResult.setValue("createAccountSuccess");
                            String uid = getCurrentUser().getUid();
                            userBean.setUid(uid);
                            userBean.setName(uid);
                            insertUser(userBean);
                        } else {
                            Timber.i(MyDebugTree.FAILURE_LOG);
                            Timber.e(task.getException().toString());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }


    public void insertUser(final UserBean userBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean));

        Map<String, Object> user = new HashMap<>();
        user.put("uid", userBean.getUid());
        user.put("name", userBean.getName());
        user.put("message", userBean.getMessage());
        user.put("icon", userBean.getIconName());
        user.put("following_count", userBean.getFollowingCount());
        user.put("follower_count", userBean.getFollowerCount());
        user.put("follow_notice", userBean.isFollowNotice());
        user.put("good_notice", userBean.isGoodNotice());
        user.put("comment_notice", userBean.isCommentNotice());
        user.put("publication_area", userBean.getPublicationArea());

        this.firestoreConnect();
        this.storageConnect();

        // users/{uid}
        firestore.collection("users")
                .document(userBean.getUid())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Timber.i(MyDebugTree.SUCCESS_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                        saveIcon(userBean);
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
                            authResult.setValue("signInSuccess");
                        } else {
                            Timber.i(MyDebugTree.FAILURE_LOG);
                            Timber.e(task.getException().toString());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }


    public void sendEmailVerification() {
        Timber.i(MyDebugTree.START_LOG);
        getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Timber.i("Email sent.");
                }
            }
        });
    }


    public void signOut() {
        Timber.i(MyDebugTree.START_LOG);
        firebaseAuth.signOut();
    }


    public void updateUser(UserBean userBean, final MutableLiveData<String> updateResult) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "userBean", userBean, "updateResult",
                updateResult));

        this.firestoreConnect();

        firestore.collection("users")
                .document(userBean.getUid())
                .update(
                        "name", userBean.getName(),
                        "message", userBean.getMessage(),
                        "icon", userBean.getIconName()
                )
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Timber.i(SUCCESS_LOG);
                                Timber.i(String.format("%s %s=%s", INPUT_LOG, "task", task));
                                updateResult.setValue("success");

                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(FAILURE_LOG);
                        Timber.e(e.toString());
                        updateResult.setValue(e.toString());
                    }
                });

        saveIcon(userBean);
    }


    public void saveIcon(final UserBean userBean) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s", INPUT_LOG, "userBean", userBean));

        storageConnect();

        // アイコン画像を保存
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userBean.getIcon().compress(Bitmap.CompressFormat.JPEG, 100, baos);
        storage.getReference()
                .child("icon")
                .child(userBean.getUid())
                .child(userBean.getIconName())
                .putBytes(baos.toByteArray()).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Timber.i(SUCCESS_LOG);
                        Timber.i(
                                String.format("%s %s=%s", INPUT_LOG, "taskSnapshot",
                                        taskSnapshot));
                        Timber.i(String.format("save image path: %s",
                                String.format("icon/%s/%s", userBean.getUid(),
                                        userBean.getIconName())));
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


    public void updateSetting(UserBean userBean) {
        Timber.i(START_LOG);
        Timber.i(String.format("%s %s=%s", INPUT_LOG, "userBean", userBean));

        Map<String, Object> setting = new HashMap<>();
        setting.put("follow_notice", userBean.isFollowNotice());
        setting.put("good_notice", userBean.isGoodNotice());
        setting.put("comment_notice", userBean.isCommentNotice());
        setting.put("publication_area", userBean.getPublicationArea());

        this.firestoreConnect();

        firestore.collection("users")
                .document(userBean.getUid())
                .update(setting)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Timber.i(SUCCESS_LOG);
                        Timber.i(String.format("%s %s=%s", INPUT_LOG, "task", task));
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
                            userBean.setFollowingCount(
                                    Integer.parseInt(document.getLong("following_count").toString()));
                            userBean.setFollowerCount(Integer.parseInt(document.getLong("follower_count").toString()));
                            userBean.setFollowNotice(document.getBoolean("follow_notice"));
                            userBean.setGoodNotice(document.getBoolean("good_notice"));
                            userBean.setCommentNotice(document.getBoolean("comment_notice"));
                            userBean.setPublicationArea(document.getString("publication_area"));
                        }

                        // icon/{uid}/{iconName}
                        storage.getReference()
                                .child("icon")
                                .child(userBean.getUid())
                                .child(userBean.getIconName())
                                .getBytes(IMAGE_SIZE)
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
        this.storageConnect();

        final List<UserBean> userBeanList = new ArrayList<>();

        for (int i = 0; i < uidList.size(); i++) {
            String uid = uidList.get(i);
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
                            userBean.setMessage(document.getString("message"));
                            userBean.setIconName(document.getString("icon"));
                            userBean.setFollowingCount(
                                    Integer.parseInt(document.getLong("following_count").toString()));
                            userBean.setFollowerCount(Integer.parseInt(document.getLong("follower_count").toString()));
                            userBean.setFollowNotice(document.getBoolean("follow_notice"));
                            userBean.setGoodNotice(document.getBoolean("good_notice"));
                            userBean.setCommentNotice(document.getBoolean("comment_notice"));
                            userBean.setPublicationArea(document.getString("publication_area"));

                            // icon/{uid}/{iconName}
                            storage.getReference()
                                    .child("icon")
                                    .child(userBean.getUid())
                                    .child(userBean.getIconName())
                                    .getBytes(IMAGE_SIZE)
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


    //fcmトークンをfirestoreに登録するメソッド
    public void fcmTokenInsert(final String uid) {
        this.firestoreConnect();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        final String token = task.getResult();

                        firestore.collection("users")
                                .document(uid)
                                .update("fcm_token", token)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Timber.i(MyDebugTree.SUCCESS_LOG);
                                        Timber.i(String.format("uid = %s, token = %s", uid, token));
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
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.i(MyDebugTree.FAILURE_LOG);
                Timber.e(e.toString());
            }
        });
    }
}
