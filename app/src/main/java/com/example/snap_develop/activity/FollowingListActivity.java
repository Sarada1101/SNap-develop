package com.example.snap_develop.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snap_develop.R;
import com.example.snap_develop.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowingListActivity extends AppCompatActivity {
    ListView lv;
    SimpleAdapter sAdapter;
    ArrayList<HashMap<String, String>> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);

        listData = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> data1 = new HashMap<String, String>();
        data1.put("username", "西山大成");
        data1.put("userid", "t1233");
        listData.add(data1);

        HashMap<String, String> data2 = new HashMap<String, String>();
        data2.put("username", "井上r");
        data2.put("userid", "ijdsbf");
        listData.add(data2);

        sAdapter = new SimpleAdapter(this, listData, R.layout.activity_following_list_row,
                new String[]{"usericon", "username", "userid"},
                new int[]{R.id.iconImageView, R.id.nameTextView, R.id.idTextView});
        lv = (ListView) findViewById(R.id.followView);
        lv.setAdapter(sAdapter);
    }

}