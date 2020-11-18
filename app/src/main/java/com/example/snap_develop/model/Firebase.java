package com.example.snap_develop.model;

import android.util.Log;

import com.example.snap_develop.util.LogUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class Firebase {
    protected FirebaseFirestore firestore;
    protected FirebaseStorage storage;

    public void firestoreConnect() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        firestore = FirebaseFirestore.getInstance();
    }

    public void storageConnect() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        storage = FirebaseStorage.getInstance();
    }
}
