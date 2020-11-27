package com.example.snap_develop.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.BuildConfig;
import com.example.snap_develop.R;
import com.example.snap_develop.databinding.ActivityUserBinding;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ListView lv;
    SimpleAdapter sAdapter;
    ArrayList<HashMap<String, String>> listData;
    FollowViewModel mFollowViewModel;
    UserViewModel mUserViewModel;
    ActivityUserBinding mBinding;
    private final static String TAG = LogUtil.getClassName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.tag(TAG).i(LogUtil.getLogMessage());

        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user);

        mBinding.followButton.setOnClickListener(this);
        mBinding.followerButton.setOnClickListener(this);

        String uid = null;
        try {
            // 他人のユーザー情報を表示する時（uidがIntentに設定されている時）
            uid = getIntent().getStringExtra("uid");
            Log.w(LogUtil.getClassName(), uid);
        } catch (NullPointerException e) {
            // 自分のユーザー情報を表示する時（uidがIntentに設定されていない時）
            uid = mUserViewModel.getCurrentUser().getUid();
            Log.w(LogUtil.getClassName(), uid);
        }
        mBinding.setUserViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(this);

        mUserViewModel.fetchUserInfo(uid);


//        listData = new ArrayList<HashMap<String, String>>();
//
//        HashMap<String, String> data1 = new HashMap<String, String>();
//        data1.put("", "");
//        data1.put("", "");
//        data1.put("", "");
//        listData.add(data1);
//
//        HashMap<String, String> data2 = new HashMap<String, String>();
//        data2.put("", "");
//        data2.put("", "");
//        data2.put("", "");
//        listData.add(data2);
//
//        sAdapter = new SimpleAdapter(this, listData,
//                R.layout.list_user,
//                new String[]{"", "", ""},
//                new int[]{R.id.userpicture, R.id.username, R.id.userid, R.id.date,
//                        R.id.postcontents, R.id.postpicture,
//                        R.id.comment, R.id.favorite, R.id.favoritequantity, R.id.location,
//                        R.id.locationinfomation});
//
//        lv = (ListView) findViewById(R.id.postList);
//        lv.setAdapter(sAdapter);
    }

    //フォロー申請ボタンが押されたときに動くonClickメソッド
    public void followApplicated() {

        //テストデータ
        String myUid = "UtJFmruiiBS28WH333AE6YHEjf72";
        String applicatedUid = "nGBoEuFPNBf9LmpLuFA6aGKshBr1";

    }

    @Override
    public void onClick(View v) {

    }
}
