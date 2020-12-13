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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class FollowModel extends Firebase {

    //uid : 申請された人のuid
    //myUid : 申請した人のuid
    //フォロー申請された人のapplicated_followsに申請された人のuidのパスを追加
    public void insertApplicatedFollow(final String uid, String myUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "myUid", myUid));

        this.firestoreConnect();

        DocumentReference applicatedPath = firestore.collection("users").document(myUid);

        Map<String, Object> addData = new HashMap<>();
        addData.put("path", applicatedPath);

        firestore.collection("users")
                .document(uid)
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
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                        Integer updateCount = Integer.valueOf(String.valueOf(doc.get("applicated_count")));
                        updateCount++;

                        firestore.collection("users")
                                .document(uid)
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

    //uid : 申請された人のuid
    //myUid : 申請した人のuid
    //フォロー申請した人のapproval_pending_followsに申請した人のuidのパスを追加
    public void insertApprovalPendingFollow(String uid, final String myUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "myUid", myUid));

        this.firestoreConnect();

        DocumentReference approvalPath = firestore.collection("users").document(uid);

        Map<String, Object> addData = new HashMap<>();
        addData.put("path", approvalPath);

        firestore.collection("users")
                .document(myUid)
                .collection("approval_pending_follows")
                .document(uid)//ドキュメントIDを申請した人のuidに指定
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

        firestore.collection("users")
                .document(myUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                        Integer updateCount = Integer.valueOf(String.valueOf(doc.get("approval_pending_count")));
                        updateCount++;

                        firestore.collection("users")
                                .document(myUid)
                                .update("approval_pending_count", updateCount)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(LogUtil.getClassName(), "update(approval_pending_count):success");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(LogUtil.getClassName(), "update(approval_pending_count):failure", e);
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LogUtil.getClassName(), "update(approval_pending_count):failure", e);
            }
        });
    }

    // toUid から deleteUid を削除する
    public void deleteApplicatedFollow(String fromUid, final String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));

        this.firestoreConnect();

        firestore.collection("users")
                .document(fromUid)
                .collection("applicated_follows")
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

    // toUid から deleteUid を削除する
    public void deleteApprovalPendingFollow(final String fromUid, String deleteUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "fromUid", fromUid, "deleteUid", deleteUid));

        this.firestoreConnect();

        firestore.collection("users")
                .document(fromUid)
                .collection("approval_pending_follows")
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

    // toUid へ insetUid を追加する
    public void insertFollower(final String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));

        this.firestoreConnect();

        Map<String, Object> followerPath = new HashMap<>();
        followerPath.put("path", firestore.collection("users").document(insertUid));

        firestore.collection("users")
                .document(toUid)
                .collection("follower")
                .document(insertUid)
                .set(followerPath)
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

        firestore.collection("users")
                .document(toUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Integer updateCount = Integer.valueOf(
                                String.valueOf(task.getResult().getLong("follower_count")));
                        updateCount++;

                        firestore.collection("users")
                                .document(toUid)
                                .update("follower_count", updateCount)
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

    // toUid へ insetUid を追加する
    public void insertFollowing(final String toUid, String insertUid) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "toUid", toUid, "insertUid", insertUid));

        this.firestoreConnect();

        Map<String, Object> followingPath = new HashMap<>();
        followingPath.put("path", firestore.collection("users").document(insertUid));

        firestore.collection("users")
                .document(toUid)
                .collection("following")
                .document(insertUid)
                .set(followingPath)
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

        firestore.collection("users")
                .document(toUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Integer updateCount = Integer.valueOf(String.valueOf(task.getResult().get("following_count")));
                        updateCount++;

                        firestore.collection("users")
                                .document(toUid)
                                .update("following_count", updateCount)
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


    public void fetchFollowingList(String uid, final MutableLiveData<List<UserBean>> followList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "followList", followList));

        this.firestoreConnect();
        this.storageConnect();

        final List<DocumentReference> followingRefList = new ArrayList<>();
        final List<UserBean> followingUserList = new ArrayList<>();

        // フォローリストを取得
        firestore.collection("users")
                .document(uid)
                .collection("following")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            followingRefList.add(document.getDocumentReference("path"));
                        }

                        // フォローしているユーザー情報を取得
                        for (DocumentReference ref : followingRefList) {
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Timber.i(MyDebugTree.START_LOG);
                                    Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                                    DocumentSnapshot document = task.getResult();
                                    final UserBean userBean = new UserBean();
                                    userBean.setIconName(document.getString("icon"));
                                    userBean.setName(document.getString("name"));
                                    userBean.setUid(document.getId());

                                    // アイコン画像を取得
                                    final long FIVE_MEGABYTE = 1024 * 1024 * 5;
                                    storage.getReference()
                                            .child("icon")
                                            .child(userBean.getUid())
                                            .child(userBean.getIconName())
                                            .getBytes(FIVE_MEGABYTE)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] aByte) {
                                                    Timber.i(MyDebugTree.SUCCESS_LOG);
                                                    Timber.i(String.format("path=/%s/%s/%s", "icon", userBean.getUid(),
                                                            userBean.getIconName()));
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0,
                                                            aByte.length);
                                                    userBean.setIcon(bitmap);
                                                    followingUserList.add(userBean);

                                                    // もし画像を全て取得したら
                                                    if (followingUserList.size() >= followingRefList.size()) {
                                                        Timber.i(String.valueOf(followingUserList));
                                                        followList.setValue(followingUserList);
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


    public void fetchFollowerList(String uid, final MutableLiveData<List<UserBean>> followList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "followList", followList));

        this.firestoreConnect();
        this.storageConnect();

        final List<DocumentReference> followingRefList = new ArrayList<>();
        final List<UserBean> followingUserList = new ArrayList<>();

        // フォロワーリストを取得
        firestore.collection("users")
                .document(uid)
                .collection("follower")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            followingRefList.add(document.getDocumentReference("path"));
                        }

                        // フォロワーしているユーザー情報を取得
                        for (DocumentReference ref : followingRefList) {
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Timber.i(MyDebugTree.START_LOG);
                                    Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                                    DocumentSnapshot document = task.getResult();
                                    final UserBean userBean = new UserBean();
                                    userBean.setIconName(document.getString("icon"));
                                    userBean.setName(document.getString("name"));
                                    userBean.setUid(document.getId());

                                    // アイコン画像を取得
                                    final long FIVE_MEGABYTE = 1024 * 1024 * 5;
                                    storage.getReference()
                                            .child("icon")
                                            .child(userBean.getUid())
                                            .child(userBean.getIconName())
                                            .getBytes(FIVE_MEGABYTE)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] aByte) {
                                                    Timber.i(MyDebugTree.SUCCESS_LOG);
                                                    Timber.i(String.format("path=/%s/%s/%s", "icon", userBean.getUid(),
                                                            userBean.getIconName()));
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0,
                                                            aByte.length);
                                                    userBean.setIcon(bitmap);
                                                    followingUserList.add(userBean);

                                                    // もし画像を全て取得したら
                                                    if (followingUserList.size() >= followingRefList.size()) {
                                                        Timber.i(String.valueOf(followingUserList));
                                                        followList.setValue(followingUserList);
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


    public void fetchApprovalPendingList(String uid, final MutableLiveData<List<UserBean>> followList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "followList", followList));

        this.firestoreConnect();
        this.storageConnect();

        final List<DocumentReference> followingRefList = new ArrayList<>();
        final List<UserBean> followingUserList = new ArrayList<>();

        // フォロー承認待ちリストを取得
        firestore.collection("users")
                .document(uid)
                .collection("approval_pending_follows")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            followingRefList.add(document.getDocumentReference("path"));
                        }

                        // フォロー承認待ちしているユーザー情報を取得
                        for (DocumentReference ref : followingRefList) {
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Timber.i(MyDebugTree.START_LOG);
                                    Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                                    DocumentSnapshot document = task.getResult();
                                    final UserBean userBean = new UserBean();
                                    userBean.setIconName(document.getString("icon"));
                                    userBean.setName(document.getString("name"));
                                    userBean.setUid(document.getId());

                                    // アイコン画像を取得
                                    final long FIVE_MEGABYTE = 1024 * 1024 * 5;
                                    storage.getReference()
                                            .child("icon")
                                            .child(userBean.getUid())
                                            .child(userBean.getIconName())
                                            .getBytes(FIVE_MEGABYTE)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] aByte) {
                                                    Timber.i(MyDebugTree.SUCCESS_LOG);
                                                    Timber.i(String.format("path=/%s/%s/%s", "icon", userBean.getUid(),
                                                            userBean.getIconName()));
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0,
                                                            aByte.length);
                                                    userBean.setIcon(bitmap);
                                                    followingUserList.add(userBean);

                                                    // もし画像を全て取得したら
                                                    if (followingUserList.size() >= followingRefList.size()) {
                                                        Timber.i(String.valueOf(followingUserList));
                                                        followList.setValue(followingUserList);
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


    public void fetchApplicatedList(String uid, final MutableLiveData<List<UserBean>> followList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "uid", uid, "followList", followList));

        this.firestoreConnect();
        this.storageConnect();

        final List<DocumentReference> followingRefList = new ArrayList<>();
        final List<UserBean> followingUserList = new ArrayList<>();

        // フォロー申請リストを取得
        firestore.collection("users")
                .document(uid)
                .collection("applicated_follows")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        Timber.i(MyDebugTree.START_LOG);
                        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            followingRefList.add(document.getDocumentReference("path"));
                        }

                        // フォロー申請しているユーザー情報を取得
                        for (DocumentReference ref : followingRefList) {
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Timber.i(MyDebugTree.START_LOG);
                                    Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "task", task));

                                    DocumentSnapshot document = task.getResult();
                                    final UserBean userBean = new UserBean();
                                    userBean.setIconName(document.getString("icon"));
                                    userBean.setName(document.getString("name"));
                                    userBean.setUid(document.getId());

                                    // アイコン画像を取得
                                    final long FIVE_MEGABYTE = 1024 * 1024 * 5;
                                    storage.getReference()
                                            .child("icon")
                                            .child(userBean.getUid())
                                            .child(userBean.getIconName())
                                            .getBytes(FIVE_MEGABYTE)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] aByte) {
                                                    Timber.i(MyDebugTree.SUCCESS_LOG);
                                                    Timber.i(String.format("path=/%s/%s/%s", "icon", userBean.getUid(),
                                                            userBean.getIconName()));
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(aByte, 0,
                                                            aByte.length);
                                                    userBean.setIcon(bitmap);
                                                    followingUserList.add(userBean);

                                                    // もし画像を全て取得したら
                                                    if (followingUserList.size() >= followingRefList.size()) {
                                                        Timber.i(String.valueOf(followingUserList));
                                                        followList.setValue(followingUserList);
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
