package com.example.snap_develop.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserModel {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final String TAG = "EmailPassword";

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void createAccount(String email, String password,
            final MutableLiveData<String> authResult) {
        Log.d(TAG, "createAccount:" + email);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            authResult.setValue("success");
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure:" + task.getException());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }

    public void signIn(String email, String password,
            final MutableLiveData<String> authResult) {
        Log.d(TAG, "signIn:" + email);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            authResult.setValue("success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            authResult.setValue(String.valueOf(task.getException()));
                        }
                    }
                });
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
}
