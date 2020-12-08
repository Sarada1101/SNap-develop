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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class FollowModel extends Firebase {

    //userPath : 申請された人のuid
    //myUid : 申請した人のuid
    //フォロー申請された人のapplicated_followsに申請された人のuidのパスを追加
    public void insertApplicatedFollow(final String userPath, String myUid) {
        System.out.println("-----------------insertApplicated----------------");

        this.firestoreConnect();

        DocumentReference applicatedPath = firestore.collection("users").document(myUid);

        Map<String, Object> addData = new HashMap<>();
        addData.put("path", applicatedPath);

        firestore.collection("users")
                .document(userPath)
                .collection("applicated_follows")
                .document(myUid)//ドキュメントIDを申請された人のuidに指定
                .set(addData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LogUtil.getClassName(), "insertApplicatedFoolow:success");
                        System.out.println(
                                "-----------------insertApplicated:comp----------------");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "insertApplicatedFoolow:failure:" + e);
            }
        });

        firestore.collection("users")
                .document(userPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                        Integer updateCount = Integer.valueOf(String.valueOf(doc.get("applicated_count")));
                        updateCount++;

                        firestore.collection("users")
                                .document(userPath)
                                .update("applicated_count", updateCount)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(LogUtil.getClassName(), "update(applicated_count):success");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(LogUtil.getClassName(), "update(applicated_count):failure", e);
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "update(applicated_count):failure", e);
            }
        });
    }

    //userPath : 申請された人のuid
    //myUid : 申請した人のuid
    //フォロー申請した人のapproval_pending_followsに申請した人のuidのパスを追加
    public void insertApprovalPendingFollow(String userPath, final String myUid) {
        this.firestoreConnect();

        DocumentReference approvalPath = firestore.collection("users").document(userPath);

        Map<String, Object> addData = new HashMap<>();
        addData.put("path", approvalPath);

        firestore.collection("users")
                .document(myUid)
                .collection("approval_pending_follows")
                .document(userPath)//ドキュメントIDを申請した人のuidに指定
                .set(addData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LogUtil.getClassName(), "insertApprovvalPendingFollow:success");
                        System.out.println("-----------------insertApproval:comp----------------");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "insertApprovalPendingFollow:failure:" + e);
            }
        });
    }

    //userPath : 申請して許可を待っている人のuid
    //myUid : 申請を拒否しようとしている人のuid
    //申請を拒否しようとしている人のapplicated_followsのドキュメントIDが申請して許可を待っている人のuidのとこを削除
    public void deleteApplicatedFollow(String userPath, final String myUid) {
        this.firestoreConnect();

        firestore.collection("users")
                .document(myUid)
                .collection("applicated_follows")
                .document(userPath)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LogUtil.getClassName(), "deleteApplicatedFollow:success");
                        System.out.println(
                                "-----------------deleteApplicated:comp----------------");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "deleteApplicatedFollow:failure:" + e);
            }
        });

        firestore.collection("users")
                .document(myUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                        Integer updateCount = Integer.valueOf(String.valueOf(doc.get("applicated_count")));
                        updateCount--;

                        firestore.collection("users")
                                .document(myUid)
                                .update("applicated_count", updateCount)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(LogUtil.getClassName(), "update(applicated_count):success");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(LogUtil.getClassName(), "update(applicated_count):failure", e);
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "update(applicated_count):failure", e);
            }
        });
    }

    //userPath : 申請して許可を待っている人のuid
    //myUid : 申請を拒否しようとしている人のuid
    //申請して許可を待っている人のapproval_pending_followsのドキュメントIDが申請を拒否しようとしている人のuidのとこを削除
    public void deleteApprovalPendingFollow(final String userPath, String myUid) {
        this.firestoreConnect();

        firestore.collection("users")
                .document(userPath)
                .collection("approval_pending_follows")
                .document(myUid)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LogUtil.getClassName(), "deleteApprovvalPendingFollow:success");
                        System.out.println("-----------------deleteApproval:comp----------------");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "deleteApprovalPendingFollow:failure:" + e);
            }
        });
    }

    //userPath : 申請して許可を待っている人のuid
    //myUid : 申請を許可した人のuid
    //申請を許可した人のfollowerのコレクションの中に申請して許可を待っている人のuidのパスを追加
    public void insertFollower(String userPath, String myUid) {
        this.firestoreConnect();

        DocumentReference followerPath = firestore.collection("users").document(userPath);

        Map<String, Object> addData = new HashMap<>();
        addData.put("path", followerPath);

        firestore.collection("users")
                .document(myUid)
                .collection("follower")
                .document(userPath)//ドキュメントIDを申請された人のuidに指定
                .set(addData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LogUtil.getClassName(), "insertFollower:success");
                        System.out.println(
                                "-----------------insertFollower:comp----------------");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "insertFollower:failure:" + e);
            }
        });
    }

    //userPath : 申請して許可を待っている人のuid
    //myUid : 申請を許可した人のuid
    //申請して許可を待っている人のfollowingのコレクションの中に申請を許可した人のuidのパスを追加
    public void insertFollowing(String userPath, String myUid) {
        this.firestoreConnect();

        DocumentReference followingPath = firestore.collection("users").document(myUid);

        Map<String, Object> addData = new HashMap<>();
        addData.put("path", followingPath);

        firestore.collection("users")
                .document(userPath)
                .collection("following")
                .document(myUid)//ドキュメントIDを申請された人のuidに指定
                .set(addData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LogUtil.getClassName(), "insertFollowing:success");
                        System.out.println(
                                "-----------------insertFollowing:comp----------------");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "insertFollowing:failure:" + e);
            }
        });
    }

    public void fetchApplicatedList(String userPath, final MutableLiveData<List<UserBean>> followList) {
        this.firestoreConnect();

        final List<DocumentReference> refList = new ArrayList<>();
        final List<UserBean> followingUserList = new ArrayList<>();

        firestore.collection("users")
                .document(userPath)
                .collection("applicated_follows")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(LogUtil.getClassName(), "getRefList:success");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                refList.add(document.getDocumentReference("path"));
                            }
                            for (DocumentReference ref : refList) {
                                ref.get()
                                        .addOnCompleteListener(
                                                new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(
                                                            @NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot doc = task.getResult();
                                                            UserBean addBean =
                                                                    new UserBean();
                                                            addBean.setIcon(doc.getString("icon"));
                                                            addBean.setMessage(doc.getString("message"));
                                                            addBean.setName(doc.getString("name"));
                                                            addBean.setUid(doc.getId());
                                                            addBean.setCommentNotice(doc.getBoolean("comment_notice"));
                                                            addBean.setFollowNotice(doc.getBoolean("follow_notice"));
                                                            addBean.setGoodNotice(doc.getBoolean("good_notice"));
                                                            addBean.setPublicationArea(doc.getString("publication_area"));
                                                            followingUserList.add(addBean);
                                                            followList.setValue(followingUserList);
                                                            Log.d(LogUtil.getClassName(),
                                                                    "fetchApplicatedList:success");
                                                        } else {
                                                            Log.w(LogUtil.getClassName(), "fetchApplicatedList:failure",
                                                                    task.getException());
                                                        }

                                                    }
                                                });
                            }
                        } else {
                            Log.w(LogUtil.getClassName(), "getRefList:failure",
                                    task.getException());
                        }
                    }
                });
    }

    public void fetchCount(String userPath, final String countPath, final MutableLiveData<Integer> userCount) {
        this.firestoreConnect();

        firestore.collection("users")
                .document(userPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d(LogUtil.getClassName(), "fetchCount(" + countPath + "):success");
                        DocumentSnapshot doc = task.getResult();

                        userCount.setValue(Integer.valueOf(String.valueOf(doc.get(countPath))));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "fetchCount(" + countPath + "):failure", e);
            }
        });
    }
}
