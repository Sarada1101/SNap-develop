package com.example.snap_develop.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.viewModel.UserViewModel;

import java.io.ByteArrayOutputStream;

public class UserUpdateActivity extends AppCompatActivity {

    UserViewModel mUserViewModel = new UserViewModel();
    private static final int RESULT_PICK_IMAGEFILE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);

        mUserViewModel.getUpdateResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String updateResult) {
                System.out.println("----------------OnChanged:updateResult----------------");

                //変更内容がデータベースに登録が完了するとユーザー情報画面に遷移
                //Intent intent = new Intent(UserUpdateActivity.this,UserActivity.class);
                //startActivity(intent);
            }
        });
    }

    public void onClick(View view) {
        /*
        TextView name = (TextView)findViewById(R.id.);
        TextView message = (TextView)findViewById(R.id.);*/

        //ImageViewに表示されている画像をstorageに保存するためにbyte[]に変換する処理
        ImageView icon = (ImageView) findViewById(R.id.imageView);
        Bitmap bmp = ((BitmapDrawable) icon.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        //テストデータ
        String iconName = "user.png";
        UserBean updateBean = new UserBean();
        updateBean.setUid("UtJFmruiiBS28WH333AE6YHEjf72");
        updateBean.setName("update_name_test");
        updateBean.setMessage("update_message_test");
        updateBean.setIcon(updateBean.getUid() + "/" + iconName);


        mUserViewModel.updateUser(updateBean, data);
    }


    /*端末内の画像を一つ選択する処理
    //ボタンが押されたときに動く処理
    public void getImage(View view) {
        //端末内の画像を選ぶ画面に遷移するIntent
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_PICK_IMAGEFILE);  //intent開始
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
                    imageView.setImageBitmap(bmp);
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
    */
}
