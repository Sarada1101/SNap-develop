package com.example.snap_develop.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityAuthBinding;
import com.example.snap_develop.viewModel.UserViewModel;

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

        //TODO 開発が終わったら削除する
        mUserViewModel.signOut();

        //userViewModelのgetAuthResultメソッドで取得できる値を監視する
        mUserViewModel.getAuthResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String authResult) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "authResult", authResult));

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
        Timber.i(MyDebugTree.START_LOG);
        String email = mBinding.emailTextInputEditText.getText().toString();
        String password = mBinding.passwordTextInputEditText.getText().toString();

        UserBean userBean = new UserBean();
        userBean.setMessage("よろしくお願いします。");
        userBean.setIconName("no_img");
        userBean.setIcon(getBitmapFromVectorDrawable(this, R.drawable.ic_baseline_account_circle_24));
        userBean.setFollowingCount((long) 0);
        userBean.setFollowerCount((long) 0);
        userBean.setFollowNotice(true);
        userBean.setGoodNotice(true);
        userBean.setCommentNotice(true);
        userBean.setPublicationArea("public");

        if (!validateForm(email, password)) {
            return;
        }
        mUserViewModel.createAccount(email, password, userBean);
    }


    // VectorDrawableをBitmapに変換
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    private void signIn() {
        Timber.i(MyDebugTree.START_LOG);
        String email = mBinding.emailTextInputEditText.getText().toString();
        String password = mBinding.passwordTextInputEditText.getText().toString();

        if (!validateForm(email, password)) {
            return;
        }
        mUserViewModel.signIn(email, password);
    }


    private boolean validateForm(String email, String password) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "email", email));
        boolean isValidSuccess = false;

        if (TextUtils.isEmpty(email)) {
            mBinding.emailTextInputLayout.setError("メールアドレスを入力してください");
        } else {
            mBinding.emailTextInputLayout.setError(null);
            isValidSuccess = true;
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.passwordTextInputLayout.setError("パスワードを入力してください");
        } else {
            mBinding.passwordTextInputLayout.setError(null);
            isValidSuccess = true;
        }

        Timber.i(String.format("%s %s=%s", MyDebugTree.RETURN_LOG, "isValidSuccess", isValidSuccess));
        return isValidSuccess;
    }


    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        if (i == R.id.createAccountButton) {
            createAccount();
        } else if (i == R.id.loginButton) {
            signIn();
        }
    }
}
