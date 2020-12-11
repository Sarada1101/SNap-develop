package com.example.snap_develop.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.viewModel.UserViewModel;

import java.io.FileDescriptor;
import java.io.IOException;

import timber.log.Timber;

public class UserUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    UserViewModel mUserViewModel = new UserViewModel();
    private static final int RESULT_PICK_IMAGEFILE = 1000;
    private String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userupdate);

        //現在ログインしているユーザーのIDをインテントから取得
        currentId = getIntent().getStringExtra("currentId");

        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                //画面に値をセット
                TextView userName = findViewById(R.id.updateUserName);
                TextView userMessage = findViewById(R.id.updateUserMessage);
                ImageView userIcon = findViewById(R.id.updateUserIcon);

                userName.setText(userBean.getName());
                userMessage.setText(userBean.getMessage());
                userIcon.setImageBitmap(userBean.getIcon());
            }
        });
        mUserViewModel.fetchUserInfo(currentId);

        mUserViewModel.getUpdateResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String updateResult) {
                System.out.println("----------------OnChanged:updateResult----------------");
                //ユーザー情報の変化が完了した後の処理
                Toast.makeText(UserUpdateActivity.this, "保存しました！！", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        if (i == R.id.toUserBackButton) {
            //ユーザー情報表示画面に戻る処理
            startActivity(new Intent(getApplication(), UserActivity.class));
        } else if (i == R.id.updateSaveButton) {
            //ユーザー情報を更新する処理
            TextView updateName = view.findViewById(R.id.updateUserName);
            TextView updateMessage = view.findViewById(R.id.updateUserMessage);
            ImageView updateIcon = view.findViewById(R.id.updateUserIcon);

            UserBean updateBean = new UserBean();
            updateBean.setUid(currentId);
            updateBean.setName(updateName.getText().toString());
            updateBean.setMessage(updateMessage.getText().toString());
            updateBean.setIcon(((BitmapDrawable) updateIcon.getDrawable()).getBitmap());
            updateBean.setIconName("userIcon.png");

            mUserViewModel.updateUser(updateBean);
        } else if (i == R.id.updateUserIcon) {
            //画像を選択する画面に遷移する処理
            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT)
                            .addCategory(Intent.CATEGORY_OPENABLE)
                            .setType("image/*")
                    , RESULT_PICK_IMAGEFILE);
        }
    }

    //画像を選んだあとの処理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                //選ばれた画像のURI取得
                uri = resultData.getData();
                try {
                    //URIからBitmapに変換してimageViewにセット
                    Bitmap bmp = getBitmapFromUri(uri);
                    ImageView iconView = findViewById(R.id.updateUserIcon);
                    iconView.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //URIからBitmapに変換するメソッド
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
