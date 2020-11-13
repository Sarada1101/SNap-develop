package com.example.snap_develop.model;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreBase {

    protected FirebaseFirestore firestore = null;

    public void connect() {
        firestore = FirebaseFirestore.getInstance();
    }
}
