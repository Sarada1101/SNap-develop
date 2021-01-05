package com.example.snap_develop.model;

import static java.util.Objects.requireNonNull;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.bean.UserBean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class FollowModel extends Firebase {

    // toUid へ insetUid を追加する
    public void insertFollowing(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));

        this.firestoreConnect();

        Map<String, Object> followingPath = new HashMap<>();
        followingPath.put("path", firestore.collection("users").document(insertUid));

        insertFollowData(toUid, insertUid, "following", followingPath);
        updateFollowCount(toUid, "following_count", "+");
    }


    // toUid へ insetUid を追加する
    public void insertFollower(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));

        this.firestoreConnect();

        Map<String, Object> followerPath = new HashMap<>();
        followerPath.put("path", firestore.collection("users").document(insertUid));

        insertFollowData(toUid, insertUid, "follower", followerPath);
        updateFollowCount(toUid, "follower_count", "+");
    }


    public void insertApprovalPendingFollow(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));

        this.firestoreConnect();

        Map<String, Object> path = new HashMap<>();
        path.put("path", firestore.collection("users").document(insertUid));

        insertFollowData(toUid, insertUid, "approval_pending_follows", path);
    }


    // toUid へ insetUid を追加する
    public void insertApplicatedFollow(String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));

        this.firestoreConnect();

        Map<String, Object> path = new HashMap<>();
        path.put("path", firestore.collection("users").document(insertUid));

        insertFollowData(toUid, insertUid, "applicated_follows", path);
    }


    private void insertFollowData(String toUid, String insertUid, String collection, Map<String, Object> data) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid",
                insertUid, "collection", collection, "data", data));

        firestore.collection("users")
                .document(toUid)
                .collection(collection)
                .document(insertUid)
                .set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Timber.i(MyDebugTree.INPUT_LOG);
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


    public void deleteFollowing(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));

        this.firestoreConnect();

        deleteFollowData(fromUid, deleteUid, "following");
        updateFollowCount(fromUid, "following_count", "-");
    }


    public void deleteFollower(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));

        this.firestoreConnect();

        deleteFollowData(fromUid, deleteUid, "follower");
        updateFollowCount(fromUid, "follower_count", "-");
    }


    // toUid から deleteUid を削除する
    public void deleteApprovalPendingFollow(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));

        this.firestoreConnect();

        deleteFollowData(fromUid, deleteUid, "approval_pending_follows");
    }


    // toUid から deleteUid を削除する
    public void deleteApplicatedFollow(String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));

        this.firestoreConnect();

        deleteFollowData(fromUid, deleteUid, "applicated_follows");
    }


    private void deleteFollowData(String fromUid, String deleteUid, String collection) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid",
                deleteUid, "collection", collection));

        firestore.collection("users")
                .document(fromUid)
                .collection(collection)
                .document(deleteUid)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Timber.i(MyDebugTree.INPUT_LOG);
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


    private void updateFollowCount(final String toUid, final String fieldName, final String calcType) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "fieldName", fieldName,
                "calcType", calcType));

        firestore.collection("users")
                .document(toUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Integer updateCount = Integer.valueOf(String.valueOf(task.getResult().get(fieldName)));
                        if (TextUtils.equals(calcType, "+")) {
                            updateCount++;
                        } else if (TextUtils.equals(calcType, "-")) {
                            updateCount--;
                        }

                        firestore.collection("users")
                                .document(toUid)
                                .update(fieldName, updateCount)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Timber.i(MyDebugTree.INPUT_LOG);
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(MyDebugTree.FAILURE_LOG);
                        Timber.e(e.toString());
                    }
                });
    }


    public void fetchFollowingList(String uid, MutableLiveData<List<UserBean>> followList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "followList", followList));

        this.firestoreConnect();
        this.storageConnect();

        fetchFollowDataList(uid, "following", followList);
    }


    public void fetchFollowerList(String uid, MutableLiveData<List<UserBean>> followList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "followList", followList));

        this.firestoreConnect();
        this.storageConnect();

        fetchFollowDataList(uid, "follower", followList);
    }


    public void fetchApprovalPendingList(String uid, MutableLiveData<List<UserBean>> followList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "followList", followList));

        this.firestoreConnect();
        this.storageConnect();

        fetchFollowDataList(uid, "approval_pending_follows", followList);
    }


    public void fetchApplicatedList(String uid, MutableLiveData<List<UserBean>> followList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "followList", followList));

        this.firestoreConnect();
        this.storageConnect();

        fetchFollowDataList(uid, "applicated_follows", followList);
    }


    private void fetchFollowDataList(String uid, String collection, final MutableLiveData<List<UserBean>> followList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "collection", collection,
                "followList", followList));

        final List<DocumentReference> refList = new ArrayList<>();
        final List<UserBean> userList = new ArrayList<>();

        // フォローリストを取得
        firestore.collection("users")
                .document(uid)
                .collection(collection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            refList.add(document.getDocumentReference("path"));
                        }

                        // フォローしているユーザー情報を取得
                        for (DocumentReference ref : refList) {
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Timber.i(MyDebugTree.START_LOG);
                                    Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                                    DocumentSnapshot document = task.getResult();
                                    final UserBean userBean = new UserBean();
                                    userBean.setName(document.getString("name"));
                                    userBean.setUid(document.getId());
                                    userBean.setMessage(document.getString("message"));
                                    userBean.setIconName(document.getString("icon"));
                                    userBean.setFollowingCount(Math.toIntExact(
                                            requireNonNull(document.getLong("following_count"))));
                                    userBean.setFollowerCount(Math.toIntExact(
                                            requireNonNull(document.getLong("follower_count"))));
                                    userBean.setFollowNotice(
                                            requireNonNull(document.getBoolean("follow_notice")));
                                    userBean.setGoodNotice(
                                            requireNonNull(document.getBoolean("good_notice")));
                                    userBean.setCommentNotice(
                                            requireNonNull(document.getBoolean("comment_notice")));
                                    userBean.setPublicationArea(document.getString("publication_area"));

                                    // アイコン画像を取得
                                    long imageSize = 1024 * 1024 * 100;
                                    storage.getReference()
                                            .child("icon")
                                            .child(userBean.getUid())
                                            .child(userBean.getIconName())
                                            .getBytes(imageSize)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] aByte) {
                                                    Timber.i(MyDebugTree.SUCCESS_LOG);
                                                    Timber.i(String.format("path=/%s/%s/%s", "icon", userBean.getUid(),
                                                            userBean.getIconName()));
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0,
                                                            aByte.length);
                                                    userBean.setIcon(bitmap);
                                                    userList.add(userBean);

                                                    // もし画像を全て取得したら
                                                    if (userList.size() >= refList.size()) {
                                                        Timber.i(String.valueOf(userList));
                                                        followList.setValue(userList);
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(MyDebugTree.FAILURE_LOG);
                        Timber.e(e.toString());
                    }
                });
    }


    public void checkFollowing(String fromUid, String checkUid, MutableLiveData<Boolean> following) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(
                String.format("%s %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "checkUid", checkUid,
                        "following", following));

        this.firestoreConnect();

        checkFollowData(fromUid, checkUid, "following", following);
    }


    public void checkApprovalPendingFollow(String fromUid, String checkUid,
            MutableLiveData<Boolean> approvalPendingFollow) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid,
                "checkUid", checkUid, "approvalPendingFollow", approvalPendingFollow));

        this.firestoreConnect();

        checkFollowData(fromUid, checkUid, "approval_pending_follows", approvalPendingFollow);
    }


    private void checkFollowData(String fromUid, String checkUid, String collection,
            final MutableLiveData<Boolean> isRegistered) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid,
                "checkUid", checkUid, "collection", collection, "collection", collection));

        firestore.collection("users")
                .document(fromUid)
                .collection(collection)
                .document(checkUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                        try {
                            String path = requireNonNull(task.getResult().getDocumentReference("path")).getPath();
                            Timber.i("get document reference:" + path);
                            isRegistered.setValue(true);
                        } catch (NullPointerException e) {
                            isRegistered.setValue(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.i(MyDebugTree.FAILURE_LOG);
                        Timber.e(e.toString());
                        isRegistered.setValue(false);
                    }
                });
    }
}
