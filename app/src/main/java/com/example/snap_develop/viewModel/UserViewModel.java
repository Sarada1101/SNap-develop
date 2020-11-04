package com.example.snap_develop.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.snap_develop.model.UserModel;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {
    private MutableLiveData<String> createAccountResult;
    UserModel userModel = new UserModel();

    public void createAccount(String email, String password) {
        userModel.createAccount(email, password, createAccountResult);
    }

    public MutableLiveData<String> getCreateAccountResult() {
        if (createAccountResult == null) {
            createAccountResult = new MutableLiveData<>();
        }
        return createAccountResult;
    }

    public FirebaseUser getCurrentUser() {
        return userModel.getCurrentUser();
    }

    public void signOut() {
        userModel.signOut();
    }
}
