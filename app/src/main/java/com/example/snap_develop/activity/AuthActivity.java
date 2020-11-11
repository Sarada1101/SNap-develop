package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.databinding.ActivityAuthBinding;
import com.example.snap_develop.viewModel.UserViewModel;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private UserViewModel userViewModel;
    private ActivityAuthBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        mBinding.createAccountButton.setOnClickListener(this);
        mBinding.loginButton.setOnClickListener(this);

        userViewModel.signOut();

        //userViewModelのgetAuthResultメソッドで取得できる値を監視する
        userViewModel.getAuthResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String authResult) {
                //上記の値が変更されたときにonChangedメソッドが発生し、中に記述されている処理が実行される
                if (TextUtils.equals(authResult, "success")) {
                    Toast.makeText(AuthActivity.this, "成功しました", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AuthActivity.this, MapActivity.class));

                } else if (authResult.contains("The email address is badly formatted")) {
                    mBinding.emailTextInputLayout.setError("メールアドレスを入力してください");

                } else if (authResult.contains(
                        "The email address is already in use by another account.")) {
                    mBinding.emailTextInputLayout.setError("メールアドレスが既に登録済みです");

                } else if (authResult.contains(
                        "There is no user record corresponding to this identifier. The user may "
                                + "have been deleted.")) {
                    mBinding.emailTextInputLayout.setError("登録されていないメールアドレスです");

                } else if (authResult.contains(
                        "The password is invalid or the user does not have a password.")) {
                    mBinding.passwordTextInputLayout.setError("パスワードが異なります");

                } else if (authResult.contains("The given password is invalid.")) {
                    mBinding.passwordTextInputLayout.setError("パスワード6文字以上入力してください");
                }
            }
        });
    }


    private void createAccount() {
        String email = mBinding.emailTextInputEditText.getText().toString();
        String password = mBinding.passwordTextInputEditText.getText().toString();

        if (!validateForm(email, password)) {
            return;
        }

        userViewModel.createAccount(email, password);
    }


    private void signIn() {
        String email = mBinding.emailTextInputEditText.getText().toString();
        String password = mBinding.passwordTextInputEditText.getText().toString();

        if (!validateForm(email, password)) {
            return;
        }
        userViewModel.signIn(email, password);
    }


    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mBinding.emailTextInputLayout.setError("メールアドレスを入力してください");
            valid = false;
        } else {
            mBinding.emailTextInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.passwordTextInputLayout.setError("パスワードを入力してください");
            valid = false;
        } else {
            mBinding.passwordTextInputLayout.setError(null);
        }
        return valid;
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.createAccountButton) {
            createAccount();
        } else if (i == R.id.loginButton) {
            signIn();
        }
    }
}
