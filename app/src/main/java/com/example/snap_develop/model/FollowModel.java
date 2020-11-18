package com.example.snap_develop.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snap_develop.util.LogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class FollowModel extends FirestoreBase {

    //userPath : 申請して許可を待っている人のuid
    //myUid : 申請を拒否しようとしている人のuid
    //申請して許可を待っている人のapplicated_followsのドキュメントIDが申請を拒否しようとしている人のuidのとこを削除
    public void deleteApplicatedFollow(String userPath, String myUid) {
        this.connect();

        firestore.collection("users")
                .document(userPath)
                .collection("applicated_follows")
                .document(myUid)
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
    }

    //userPath : 申請して許可を待っている人のuid
    //myUid : 申請を拒否しようとしている人のuid
    //申請を拒否しようとしている人のapproval_pending_followsのドキュメントIDが申請して許可を待っている人のuidのとこを削除
    public void deleteApprovalPendingFollow(String userPath, String myUid) {
        this.connect();

        firestore.collection("users")
                .document(myUid)
                .collection("approval_pending_follows")
                .document(userPath)
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
        this.connect();

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
        this.connect();

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
}
