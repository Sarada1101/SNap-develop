package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.adapter.UserAdapter;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityUserBinding;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ListView mListView;
    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private ActivityUserBinding mActivityUserBinding;
    private UserAdapter mUserAdapter;
    private String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mActivityUserBinding = DataBindingUtil.setContentView(this, R.layout.activity_user);

        mActivityUserBinding.followButton.setOnClickListener(this);
        mActivityUserBinding.followerButton.setOnClickListener(this);

        String uid;
        // 他人のユーザー情報を表示する時（uidがIntentに設定されている時）
        uid = getIntent().getStringExtra("uid");
        if (uid == null) {
            // 自分のユーザー情報を表示する時（uidがIntentに設定されていない時）
            uid = mUserViewModel.getCurrentUser().getUid();
            currentId = uid;
        }
        Timber.i(String.format("%s=%s", "uid", uid));

        // ユーザー情報を取得したら投稿リストを取得する
        final String finalUid = uid;
        mUserViewModel.getUser().observe(this, new Observer<UserBean>() {
            @Override
            public void onChanged(UserBean userBean) {
                mPostViewModel.fetchPostList(finalUid);
            }
        });
        mUserViewModel.fetchUserInfo(uid);

        // 投稿リストを取得したらリストを表示する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postList) {
                ArrayList<PostBean> dataList = (ArrayList<PostBean>) postList;
                mUserAdapter = new UserAdapter(UserActivity.this, dataList, mUserViewModel.getUser().getValue(),
                        R.layout.list_user);
                mListView = mActivityUserBinding.postList;
                mListView.setAdapter(mUserAdapter);
            }
        });
        mActivityUserBinding.setUserViewModel(mUserViewModel);
        mActivityUserBinding.setLifecycleOwner(this);
    }

    //フォロー申請ボタンが押されたときに動くonClickメソッド
    public void followApplicated() {

        //テストデータ
        String myUid = "UtJFmruiiBS28WH333AE6YHEjf72";
        String applicatedUid = "nGBoEuFPNBf9LmpLuFA6aGKshBr1";

    }

    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        if (i == R.id.profileUpdateButton) {
            if (currentId != null) {
                startActivity(new Intent(getApplicationContext(), UserUpdateActivity.class)
                        .putExtra("currentId", (Serializable) currentId)
                );
            }
        }
    }
}
