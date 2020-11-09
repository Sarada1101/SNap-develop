package com.example.snap_develop.model;

import android.util.Log;

import com.example.snap_develop.util.LogUtil;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreBase {
    protected FirebaseFirestore firestore = null;

    protected FirebaseFirestore firestore = null;

    public void connect() {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        firestore = FirebaseFirestore.getInstance();
    }
}
