package com.example.snap_develop.model;

import com.example.snap_develop.MyDebugTree;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import timber.log.Timber;

public class Firebase {
    protected FirebaseFirestore firestore;
    protected FirebaseStorage storage;

    public void firestoreConnect() {
        Timber.i(MyDebugTree.START_LOG);
        firestore = FirebaseFirestore.getInstance();
    }

    public void storageConnect() {
        Timber.i(MyDebugTree.START_LOG);
        storage = FirebaseStorage.getInstance();
    }
}
