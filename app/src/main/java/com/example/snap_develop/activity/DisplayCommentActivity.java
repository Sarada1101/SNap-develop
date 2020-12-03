package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityDisplayCommentBinding;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayCommentActivity extends AppCompatActivity {

    PostViewModel postViewModel;
    UserViewModel userViewModel;
    ActivityDisplayCommentBinding mBinding;


    ListView lv;
    SimpleAdapter sAdapter;
    ArrayList<HashMap<String, String>> listData;
    String postPath;
    UserViewModel userViewModel;
    PostViewModel postViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comment);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_display_comment);


        String postPath = "5tz1lsaRGKHt59ntjiRj";

        postViewModel.fetchPost(postPath);


        postViewModel.getPost().observe(this, new Observer<PostBean>() {
            @Override
            public void onChanged(PostBean postBean) {
                String userId = postBean.getUid();
                userViewModel.fetchUserInfo(userId);

                LatLng latlng = postBean.getLatLng();
            }
        });

        //コメントのPostpathの取得
        final List<String> commentList = new ArrayList<>();
        commentList.add("2ZJzFlMVOcWzcYmkbKU0");
        commentList.add("IrvcvPxohsko3gXT6xHM");
        commentList.add("wdDtw2jv5n4KAuS0sS2U");

        postViewModel.fetchPostCommentList(commentList);

        postViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> postBeans) {
                listData = new ArrayList<HashMap<String, String>>();

                for (final PostBean bean : postBeans) {
                    String userId = bean.getUid();
                    userViewModel.fetchUserInfo(userId);
                    String UName;

                    userViewModel.getUser().observe(DisplayCommentActivity.this, new Observer<UserBean>() {
                        @Override
                        public void onChanged(UserBean userBean) {
                            String UName = userBean.getName();

                            HashMap<String, String> date = new HashMap<String, String>();
                            date.put("usrName", UName);
                            date.put("usrId", bean.getUid());
                            date.put("dateTime", bean.getDatetime().toString());
                            date.put("post", bean.getMessage());
                            listData.add(date);
                        }
                    });


                }

                sAdapter = new SimpleAdapter(DisplayCommentActivity.this, listData,
                        R.layout.activity_display_comment_list,
                        new String[]{"usrIcon", "usrName", "usrId", "dateTime", "post"},
                        new int[]{R.id.userIconView, R.id.userNameText, R.id.userIdText, R.id.timeText,
                                R.id.postText});

                lv = (ListView) findViewById(R.id.commentListView);
                lv.setAdapter(sAdapter);


            }
        });

        //地図画面からタップされたマーカーの投稿のパスを取得
        Intent intent = getIntent();
        postPath = intent.getStringExtra("postPath");
        Log.d(LogUtil.getClassName(), String.format("postPath: %s", postPath));

        mBinding.setPostViewModel(postViewModel);
        mBinding.setUserViewModel(userViewModel);
        mBinding.setLifecycleOwner(this);
    }
}
