package com.example.snap_develop.model;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreBase {
    FirebaseFirestore firestore;

    public void connect() {
        firestore = FirebaseFirestore.getInstance();
    }
}
