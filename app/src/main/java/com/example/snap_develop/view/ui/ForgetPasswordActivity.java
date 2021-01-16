package com.example.snap_develop.view.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.databinding.ActivityForgetPasswordBinding;
import com.example.snap_develop.view_model.UserViewModel;

import timber.log.Timber;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private UserViewModel mUserViewModel;
    private ActivityForgetPasswordBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password);

        mBinding.sendMailButton.setOnClickListener(this);
    }


    private void resetPassword() {
        Timber.i(MyDebugTree.START_LOG);
        String email = mBinding.emailTextInputEditText.getText().toString();

        if (!validateForm(email)) {
            return;
        }

        mUserViewModel.sendResetPasswordEmail(email);
        Toast.makeText(this, "パスワード再設定のメールを送信しました", Toast.LENGTH_LONG).show();
    }


    private boolean validateForm(String email) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "email", email));
        boolean isValidSuccess = false;

        if (TextUtils.isEmpty(email)) {
            mBinding.emailTextInputLayout.setError("メールアドレスを入力してください");
        } else {
            mBinding.emailTextInputLayout.setError(null);
            isValidSuccess = true;
        }

        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidSuccess", isValidSuccess));
        return isValidSuccess;
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        if (i == R.id.sendMailButton) {
            resetPassword();
        }
    }
}
