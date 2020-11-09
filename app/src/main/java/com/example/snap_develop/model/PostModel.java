package com.example.snap_develop.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snap_develop.bean.PostBean;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostModel extends FirestoreBase {

    private final String TAG = "Firestore";

    public void insertPost(PostBean postBean) {
        this.connect();
        Map<String, Object> post = new HashMap<>();
        post.put("message", postBean.getMessage());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println(
                                "DocumentSnapshot written with ID: " + documentReference.getId());
                        Log.d(TAG,
                                "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error adding document" + e);
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
