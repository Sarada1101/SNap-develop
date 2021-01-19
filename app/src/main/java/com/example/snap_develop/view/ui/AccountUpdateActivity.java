package com.example.snap_develop.view.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.databinding.ActivityAccountUpdateBinding;
import com.example.snap_develop.view_model.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import timber.log.Timber;

public class AccountUpdateActivity extends AppCompatActivity implements View.OnClickListener,
        TabLayout.OnTabSelectedListener {

    private UserViewModel mUserViewModel;
    private ActivityAccountUpdateBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_update);
        setTitle("アカウント情報更新");

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_update);

        TabLayout.Tab tabAt = Objects.requireNonNull(mBinding.buttonTabLayout.getTabAt(MainApplication.USER_POS));
        tabAt.select();
        Objects.requireNonNull(tabAt.getIcon()).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        mBinding.updateEmailButton.setOnClickListener(this);
        mBinding.updatePasswordButton.setOnClickListener(this);
        mBinding.buttonTabLayout.addOnTabSelectedListener(this);

        //アカウント情報の変化が完了した後の処理
        mUserViewModel.getUpdateResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String updateResult) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "updateResult", updateResult));
                Objects.requireNonNull(updateResult);
                //上記の値が変更されたときにonChangedメソッドが発生し、中に記述されている処理が実行される
                if (TextUtils.equals(updateResult, "updateEmail")) {
                    Toast.makeText(getApplication(), "メールアドレスを更新しました", Toast.LENGTH_SHORT).show();
                    mUserViewModel.sendEmailVerification();
                } else if (TextUtils.equals(updateResult, "updatePassword")) {
                    Toast.makeText(getApplication(), "パスワードを更新しました", Toast.LENGTH_SHORT).show();
                } else {
                    setUpdateError(updateResult);
                }
            }
        });

        mBinding.updateEmailTextInputEditText.setText(mUserViewModel.getCurrentUser().getEmail());

        String uid = mUserViewModel.getCurrentUser().getUid();
        mUserViewModel.fetchUserInfo(uid);
        mBinding.setLifecycleOwner(this);
    }


    private void updateEmail() {
        Timber.i(MyDebugTree.START_LOG);
        String email = Objects.requireNonNull(mBinding.updateEmailTextInputEditText.getText()).toString();
        String password = Objects.requireNonNull(mBinding.emailPasswordTextInputEditText.getText()).toString();
        if (!validateEmail(email, password)) {
            return;
        }
        mUserViewModel.updateEmail(email, password);
    }


    private boolean validateEmail(String email, String password) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "email", email, "password", password));
        boolean isValidEmail;
        boolean isValidPassword;

        if (TextUtils.isEmpty(email)) {
            mBinding.updateEmailTextInputLayout.setError("メールアドレスを入力してください");
            isValidEmail = false;
        } else {
            mBinding.updateEmailTextInputLayout.setError(null);
            isValidEmail = true;
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.emailPasswordTextInputLayout.setError("パスワードを入力してください");
            isValidPassword = false;
        } else {
            mBinding.emailPasswordTextInputLayout.setError(null);
            isValidPassword = true;
        }

        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidEmail", isValidEmail));
        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidPassword", isValidPassword));
        return isValidEmail && isValidPassword;
    }


    private void updatePassword() {
        Timber.i(MyDebugTree.START_LOG);
        String currentPassword = Objects.requireNonNull(mBinding.currentPasswordTextInputEditText.getText()).toString();
        String password = Objects.requireNonNull(mBinding.updatePasswordTextInputEditText.getText()).toString();
        String checkPassword = Objects.requireNonNull(mBinding.checkPasswordTextInputEditText.getText()).toString();
        if (!validatePassword(currentPassword, password, checkPassword)) {
            return;
        }
        mUserViewModel.updatePassword(currentPassword, password);
    }


    private boolean validatePassword(String password, String checkPassword) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(
                String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "password", password, "checkPassword",
                        checkPassword));
        boolean isValidSuccess = false;

        if (TextUtils.isEmpty(password)) {
            mBinding.updatePasswordTextInputLayout.setError("パスワードを入力してください");
            mBinding.checkPasswordTextInputLayout.setError(null);
        } else if (TextUtils.isEmpty(checkPassword)) {
            mBinding.checkPasswordTextInputLayout.setError("もう一度パスワードを入力してください");
            mBinding.updatePasswordTextInputLayout.setError(null);
        } else if (!TextUtils.equals(password, checkPassword)) {
            mBinding.checkPasswordTextInputLayout.setError("パスワードが間違っています");
            mBinding.updatePasswordTextInputLayout.setError(null);
        } else {
            mBinding.updatePasswordTextInputLayout.setError(null);
            mBinding.checkPasswordTextInputLayout.setError(null);
            isValidSuccess = true;
        }

        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidSuccess", isValidSuccess));
        return isValidSuccess;
    }


    private void setUpdateError(String updateResult) {
        if (updateResult.contains("The email address is badly formatted")) {
            mBinding.updateEmailTextInputLayout.setError("メールアドレスを入力してください");
        } else if (updateResult.contains("The email address is already in use by another account.")) {
            mBinding.updateEmailTextInputLayout.setError("メールアドレスが既に登録済みです");
        } else if (updateResult.contains("The given password is invalid.")) {
            mBinding.updatePasswordTextInputLayout.setError("パスワード6文字以上入力してください");
        }
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.updateEmailButton) {
            updateEmail();
        } else if (i == R.id.updatePasswordButton) {
            updatePassword();
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Timber.i(MyDebugTree.START_LOG);
        switch (tab.getPosition()) {
            case MainApplication.TIMELINE_POS:
                startActivity(new Intent(getApplication(), TimelineActivity.class));
                break;
            case MainApplication.MAP_POS:
                startActivity(new Intent(getApplication(), MapActivity.class));
                break;
            case MainApplication.USER_POS:
                startActivity(new Intent(getApplication(), UserActivity.class));
                break;
        }
    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }


    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Timber.i(MyDebugTree.START_LOG);
        switch (tab.getPosition()) {
            case MainApplication.TIMELINE_POS:
                startActivity(new Intent(getApplication(), TimelineActivity.class));
                break;
            case MainApplication.MAP_POS:
                startActivity(new Intent(getApplication(), MapActivity.class));
                break;
            case MainApplication.USER_POS:
                startActivity(new Intent(getApplication(), UserActivity.class));
                break;
        }
    }
}
