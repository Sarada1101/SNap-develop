package com.example.snap_develop.view.ui;

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

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityAuthBinding;
import com.example.snap_develop.view_model.UserViewModel;

import java.util.Objects;

import timber.log.Timber;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private UserViewModel mUserViewModel;
    private ActivityAuthBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        mBinding.createAccountButton.setOnClickListener(this);
        mBinding.loginButton.setOnClickListener(this);
        mBinding.forgetPasswordTextView.setOnClickListener(this);
        mBinding.notRegisterTextView.setOnClickListener(this);

        if (mUserViewModel.getCurrentUser() != null) {
            boolean isVerified = mUserViewModel.getCurrentUser().isEmailVerified();
            if (!isVerified) {  // メール認証されてないなら
                mUserViewModel.signOut();
            } else {
                startActivity(new Intent(getApplication(), MapActivity.class));
            }
        }

        //userViewModelのgetAuthResultメソッドで取得できる値を監視する
        mUserViewModel.getAuthResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String authResult) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "authResult", authResult));
                Objects.requireNonNull(authResult);

                //上記の値が変更されたときにonChangedメソッドが発生し、中に記述されている処理が実行される
                if (TextUtils.equals(authResult, "createAccountSuccess")) {
                    //FCMトークンを登録する
                    mUserViewModel.fcmTokenInsert(mUserViewModel.getCurrentUser().getUid());
                    mUserViewModel.sendEmailVerification();
                    startActivity(new Intent(getApplication(), EmailCheckActivity.class).putExtra("activity",
                            "createAccount"));
                } else if (TextUtils.equals(authResult, "signInSuccess")) {
                    if (mUserViewModel.getCurrentUser().isEmailVerified()) { // メール認証済みなら
                        Toast.makeText(AuthActivity.this, "ログインに成功しました", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplication(), MapActivity.class));
                    } else {
                        startActivity(
                                new Intent(getApplication(), EmailCheckActivity.class).putExtra("activity", "signIn"));
                    }
                } else {
                    setAuthError(authResult);
                }
            }
        });
    }


    private void createAccount() {
        Timber.i(MyDebugTree.START_LOG);
        String email = Objects.requireNonNull(mBinding.emailTextInputEditText.getText()).toString();
        String password = Objects.requireNonNull(mBinding.passwordTextInputEditText.getText()).toString();

        UserBean userBean = new UserBean();
        userBean.setMessage("よろしくお願いします。");
        userBean.setIconName("no_img");
        userBean.setIcon(MainApplication.getBitmapFromVectorDrawable(this, R.drawable.ic_baseline_account_circle_24));
        userBean.setFollowingCount(0);
        userBean.setFollowerCount(0);
        userBean.setFollowNotice(true);
        userBean.setGoodNotice(true);
        userBean.setCommentNotice(true);
        userBean.setPublicationArea("public");

        if (!validateForm(email, password)) {
            return;
        }
        mUserViewModel.createAccount(email, password, userBean);
    }


    private void signIn() {
        Timber.i(MyDebugTree.START_LOG);
        String email = Objects.requireNonNull(mBinding.emailTextInputEditText.getText()).toString();
        String password = Objects.requireNonNull(mBinding.passwordTextInputEditText.getText()).toString();

        if (!validateForm(email, password)) {
            return;
        }
        mUserViewModel.signIn(email, password);
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean validateForm(String email, String password) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "email", email));
        boolean isValidEmail;
        boolean isValidPassword;

        if (TextUtils.isEmpty(email)) {
            mBinding.emailTextInputLayout.setError("メールアドレスを入力してください");
            isValidEmail = false;
        } else {
            mBinding.emailTextInputLayout.setError(null);
            isValidEmail = true;
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.passwordTextInputLayout.setError("パスワードを入力してください");
            isValidPassword = false;
        } else {
            mBinding.passwordTextInputLayout.setError(null);
            isValidPassword = true;
        }

        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidEmail", isValidEmail));
        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidPassword", isValidPassword));
        return isValidEmail && isValidPassword;
    }


    private void setAuthError(String authResult) {
        if (authResult.contains("The email address is badly formatted")) {
            mBinding.emailTextInputLayout.setError("メールアドレスを入力してください");
        } else if (authResult.contains("The email address is already in use by another account.")) {
            mBinding.emailTextInputLayout.setError("メールアドレスが既に登録済みです");
        } else if (authResult.contains(
                "There is no user record corresponding to this identifier. The user may have been deleted.")) {
            mBinding.emailTextInputLayout.setError("登録されていないメールアドレスです");
        } else if (authResult.contains("The password is invalid or the user does not have a password.")) {
            mBinding.passwordTextInputLayout.setError("パスワードが異なります");
        } else if (authResult.contains("The given password is invalid.")) {
            mBinding.passwordTextInputLayout.setError("パスワード6文字以上入力してください");
        }
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        if (i == R.id.createAccountButton) {
            createAccount();
        } else if (i == R.id.loginButton) {
            signIn();
        } else if (i == R.id.forgetPasswordTextView) {
            startActivity(new Intent(getApplication(), ForgetPasswordActivity.class));
        } else if (i == R.id.notRegisterTextView) {
            startActivity(new Intent(getApplication(), MapActivity.class));
        }
    }
}
