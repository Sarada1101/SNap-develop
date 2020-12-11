package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.adapter.FollowListAdapter;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class FollowerListActivity extends AppCompatActivity implements View.OnClickListener {

    FollowViewModel followViewModel;
    UserViewModel userViewModel;
    Integer followCount;
    int count = 0;
    String currentUid;
    ListView lv;
    FollowListAdapter fAdapter;
    ArrayList<HashMap<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list);
        setTitle("フォロワー");

        followViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //現在ログイン中のユーザーのUidを取得する処理
        //currentUid = userViewModel.getCurrentUser().getUid();

        //テストデータ
        currentUid = "UtJFmruiiBS28WH333AE6YHEjf72";

        //フォローしている人数を取得する処理
        followViewModel.fetchCount(currentUid, "follower_count");

        followViewModel.getUserCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer userCount) {
                System.out.println(userCount);
                followCount = userCount;
                followViewModel.fetchFollowerList(currentUid);
            }
        });

        followViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                System.out.println("--------------------onChanged:count----------------------");
                count++;
                if (count >= followCount) {
                    //アダプターに渡すデータ作成
                    dataList = new ArrayList<>();
                    for (UserBean bean : followList) {
                        HashMap<String, Object> addData = new HashMap<>();
                        addData.put("userName", bean.getName());
                        addData.put("userId", bean.getUid());
                        addData.put("userIcon", bean.getIcon());

                        dataList.add(addData);
                    }
                    /////////////////////////////////////////////////////////////////////////////////////

                    fAdapter = new FollowListAdapter(FollowerListActivity.this, dataList, R.layout.activity_follower_list_row
                            , new int[]{R.id.followerNameView, R.id.followerIdView, R.id.followerIconView});
                    lv = (ListView) findViewById(R.id.followerList);
                    lv.setAdapter(fAdapter);
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        Timber.i(MyDebugTree.START_LOG);
        int i = view.getId();
        Timber.i(getResources().getResourceEntryName(i));
        if (i == R.id.timelineImageButton) {
            startActivity(new Intent(getApplication(), TimelineActivity.class));
        } else if (i == R.id.mapImageButton) {
            startActivity(new Intent(getApplication(), MapActivity.class));
        } else if (i == R.id.userImageButton) {
            startActivity(new Intent(getApplication(), UserActivity.class));
        }
    }
}
