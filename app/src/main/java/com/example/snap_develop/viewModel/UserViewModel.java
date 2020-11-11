package com.example.snap_develop.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.model.UserModel;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {
    private MutableLiveData<String> authResult;
    UserModel userModel = new UserModel();

    public FirebaseUser getCurrentUser() {
        return userModel.getCurrentUser();
    }

    public void createAccount(String email, String password) {
        userModel.createAccount(email, password, authResult);
    }

    public void signIn(String email, String password) {
        userModel.signIn(email, password, authResult);
    }
    
    public void signOut() {
        userModel.signOut();
    }

    public MutableLiveData<String> getAuthResult() {
        if (authResult == null) {
            authResult = new MutableLiveData<>();
        }
        return authResult;
    }
}

