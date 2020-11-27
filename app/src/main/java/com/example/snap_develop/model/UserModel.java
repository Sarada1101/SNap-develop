package com.example.snap_develop.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.BuildConfig;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class UserModel extends Firebase {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private final static String SUCCESS = "success";
    private final static String TAG = LogUtil.getClassName();

    public UserModel() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public FirebaseUser getCurrentUser() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        return firebaseAuth.getCurrentUser();
    }

    public void createAccount(String email, String password,
            final MutableLiveData<String> authResult) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LogUtil.getClassName(), "createUserWithEmail:success");
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
                            Log.w(LogUtil.getClassName(),
                                    "createUserWithEmail:failure:" + task.getException());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }

    public void signIn(String email, String password,
            final MutableLiveData<String> authResult) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LogUtil.getClassName(), "signInUserWithEmail:success");
                            authResult.setValue("success");
                        } else {
                            Log.w(LogUtil.getClassName(), "signInUserWithEmail:failure",
                                    task.getException());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }

    public void insertUser(UserBean userBean) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        Map<String, Object> user = new HashMap<>();
        user.put("uid", userBean.getUid());
        user.put("name", userBean.getName());
        user.put("message", userBean.getMessage());
        user.put("icon", userBean.getIcon());
        user.put("followNotice", userBean.isFollowNotice());
        user.put("goodNotice", userBean.isGoodNotice());
        user.put("commentNotice", userBean.isCommentNotice());

        this.firestoreConnect();

        firestore.collection("users").document(userBean.getUid()).set(user);
    }

    public void signOut() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        firebaseAuth.signOut();
    }

    public void updateUser(UserBean userBean, byte[] data,
            final MutableLiveData<String> updateResult) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());

        //画像をstorageに保存
        imgUpload(data, userBean.getIconName());

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
        Timber.tag(TAG).i(LogUtil.getLogMessage());
        this.firestoreConnect();
        this.storageConnect();

        final UserBean userBean = new UserBean();

        //datetimeが1時間前より大きいかつ、typeがpostの投稿を取得する
        firestore.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Timber.tag(TAG).i(LogUtil.getLogMessage());
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            userBean.setName(document.getString("name"));
                            userBean.setUid(document.getId());
                            userBean.setMessage(document.getString("message"));
                            userBean.setIconName(document.getString("icon"));
                            userBean.setFollowingCount(document.getLong("following_count"));
                            userBean.setFollowerCount(document.getLong("follower_count"));
                        }

                        final long ONE_MEGABYTE = 1024 * 1024;
                        storage.getReference()
                                // icon/{uid}/{iconName}
                                .child("icon")
                                .child(uid)
                                .child(userBean.getIconName())
                                .getBytes(ONE_MEGABYTE)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] aByte) {
                                        Timber.tag(TAG).i(LogUtil.getLogMessage());
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0, aByte.length);
                                        userBean.setIcon(bitmap);
                                        user.setValue(userBean);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Timber.tag(TAG).e(LogUtil.getLogMessage(), e);
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

    //storageに画像をアップロードするメソッド
    public void imgUpload(byte[] data, String path) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference
        StorageReference userImagesRef = storageRef.child(path);

        UploadTask uploadTask = userImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                System.out.println("--------No upload------");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type,
                // etc.
                // ...
                System.out.println("--------Yes upload-------");
            }
        });
    }
}
