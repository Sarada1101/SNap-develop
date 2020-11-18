package com.example.snap_develop.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snap_develop.R;
import com.example.snap_develop.viewModel.FollowViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplicatedFollowListActivity extends AppCompatActivity {

    ListView lv;
    SimpleAdapter sAdapter;
    ArrayList<HashMap<String, String>> listData;
    FollowViewModel followViewModel = new FollowViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicated_follow_list);

        listData = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> data1 = new HashMap<String, String>();
        data1.put("username", "西山大成");
        data1.put("userid", "t1233");
        listData.add(data1);

        HashMap<String, String> data2 = new HashMap<String, String>();
        data2.put("username", "井上r");
        data2.put("userid", "ijdsbf");
        listData.add(data2);


        sAdapter = new SimpleAdapter(this, listData,
                R.layout.activity_applicated_follow_list_row,
                new String[]{"usericon", "username", "userid"},
                new int[]{R.id.userImageView, R.id.userNameTextView, R.id.userIdTextView});
        lv = (ListView) findViewById(R.id.applicatedFollowList);
        lv.setAdapter(sAdapter);
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