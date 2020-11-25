package com.example.snap_develop.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.FollowListAdapter;
import com.example.snap_develop.util.LogUtil;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicatedFollowListActivity extends AppCompatActivity {

    ListView lv;
    FollowListAdapter fAdapter;
    ArrayList<HashMap<String, Object>> listData;
    FollowViewModel followViewModel;
    UserViewModel userViewModel;
    Integer followCount;
    int count = 0;
    int count2 = 0;
    List<UserBean> dispFollowList;
    FirebaseUser currentUser;
    String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicated_follow_list);

        listData = new ArrayList<HashMap<String, Object>>();

        followViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //現在ログイン中のユーザーのUidを取得する処理
        //currentUser = userViewModel.getCurrentUser();
        //currentUid = currentUser.getUid();

        //テストデータ
        currentUid = "UtJFmruiiBS28WH333AE6YHEjf72";

        //フォローしている人数を取得する処理
        followViewModel.fetchCount(currentUid, "applicated_count");

        followViewModel.getUserCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer userCount) {
                followCount = userCount;
                followViewModel.fetchApplicatedList(currentUid);
            }
        });

        followViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                System.out.println("--------------------onChanged:count----------------------");
                count++;
                if (count >= followCount) {
                    dispFollowList = new ArrayList<>();
                    dispFollowList = followList;
                    userViewModel.fetchIconBmp(dispFollowList);
                }
            }
        });

        userViewModel.getIconList().observe(this, new Observer<Map<String, Bitmap>>() {
            @Override
            public void onChanged(Map<String, Bitmap> iconList) {
                System.out.println("--------------------onChanged----------------------");
                count2++;
                if (count2 >= dispFollowList.size()) {
                    for (UserBean bean : dispFollowList) {
                        HashMap<String, Object> addData = new HashMap<String, Object>();
                        addData.put("username", bean.getName());
                        addData.put("userid", bean.getUid());
                        addData.put("usericon", iconList.get(bean.getUid()));
                        System.out.println(iconList);
                        listData.add(addData);
                    }
                    fAdapter = new FollowListAdapter(ApplicatedFollowListActivity.this, listData, R.layout.activity_applicated_follow_list_row
                            , new int[]{R.id.userNameTextView, R.id.userIdTextView, R.id.userImageView});
                    lv = (ListView) findViewById(R.id.applicatedFollowList);
                    lv.setAdapter(fAdapter);
                }
            }
        });
    }

    //フォロー申請が許可されたときに動くonClickメソッド
    public void followApplicatedPermit() {

        //テストデータ
        String myUid = "UtJFmruiiBS28WH333AE6YHEjf72";
        String applicatedUid = "nGBoEuFPNBf9LmpLuFA6aGKshBr1";

        followViewModel.deleteApplicatedFollow(applicatedUid, myUid);
        followViewModel.deleteApprovalPendingFollow(applicatedUid, myUid);
        followViewModel.insertFollower(applicatedUid, myUid);
        followViewModel.insertFollowing(applicatedUid, myUid);
    }

    //フォロー申請が拒否されたときに動くonClickメソッド
    public void followApplicatedReject() {

        //テストデータ
        String myUid = "UtJFmruiiBS28WH333AE6YHEjf72";
        String applicatedUid = "nGBoEuFPNBf9LmpLuFA6aGKshBr1";

        followViewModel.deleteApplicatedFollow(applicatedUid, myUid);
        followViewModel.deleteApprovalPendingFollow(applicatedUid, myUid);
    }
}