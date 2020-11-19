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

public class FollowModel extends Firebase {

    //userPath : 申請された人のuid
    //myUid : 申請した人のuid
    //フォロー申請された人のapplicated_followsに申請された人のuidのパスを追加
    public void insertApplicatedFollow(String userPath, String myUid) {
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
    }

    //userPath : 申請された人のuid
    //myUid : 申請した人のuid
    //フォロー申請した人のapproval_pending_followsに申請した人のuidのパスを追加
    public void insertApprovalPendingFollow(String userPath, String myUid) {
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
}
