package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.databinding.ActivityEmailCheckBinding;
import com.example.snap_develop.viewModel.UserViewModel;

import timber.log.Timber;

public class EmailCheckActivity extends AppCompatActivity implements View.OnClickListener {

    private UserViewModel mUserViewModel;
    private ActivityEmailCheckBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_check);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_email_check);

        mBinding.sendEmailVerifiedButton.setOnClickListener(this);
        mBinding.backLoginButton.setOnClickListener(this);

        String activity = getIntent().getStringExtra("activity");
        String text = null;

        if (TextUtils.equals(activity, "createAccount")) {
            text = mUserViewModel.getCurrentUser().getEmail() + "に認証メールを送信しました。\n認証メール内のリンクを開いてメールアドレスを認証してください。";
        } else if (TextUtils.equals(activity, "signIn")) {
            text = "メールアドレスが認証されていません。\n認証メール内のリンクを開いてメールアドレスを認証してください。";
        }

        mBinding.emailCheckTextView.setText(text);
    }

    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        if (i == R.id.sendEmailVerifiedButton) {
            Toast.makeText(getApplication(), "認証メールを送信しました。", Toast.LENGTH_SHORT).show();
            mUserViewModel.sendEmailVerification();
        } else if (i == R.id.backLoginButton) {
            startActivity(new Intent(getApplication(), AuthActivity.class));
        }
    }
}
