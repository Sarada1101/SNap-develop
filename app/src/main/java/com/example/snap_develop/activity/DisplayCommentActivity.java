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

public class DisplayCommentActivity extends AppCompatActivity {
    ListView lv;
    SimpleAdapter sAdapter;
    ArrayList<HashMap<String, String>> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comment);

        listData = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> date1 = new HashMap<String, String>();
        date1.put("usrName", "西山大成");
        date1.put("usrId", "12345");
        date1.put("dateTime", "2011/4/3 12:12");
        date1.put("post", "いいですね");
        listData.add(date1);

        HashMap<String, String> date2 = new HashMap<String, String>();
        date2.put("usrName", "井上大靖");
        date2.put("usrId", "12344");
        date2.put("dateTime", "2011/4/3 12:15");
        date2.put("post", "aaaaa");
        listData.add(date2);

        sAdapter = new SimpleAdapter(this, listData, R.layout.activity_display_comment_list,
                new String[]{"usrIcon", "usrName", "usrId", "dateTime", "post"},
                new int[]{R.id.userIconView, R.id.userNameText, R.id.userIdText, R.id.timeText,
                        R.id.postText});

        lv = (ListView) findViewById(R.id.commentListView);
        lv.setAdapter(sAdapter);
    }
}
