package com.example.snap_develop.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snap_develop.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TimelineActivity extends AppCompatActivity {
    ListView lv;
    SimpleAdapter sAdapter;
    ArrayList<HashMap<String, String>> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        listData = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> data1 = new HashMap<String, String>();
        data1.put("username", "西山大成");
        data1.put("userid", "t1233");
        data1.put("date", "2020/1/23");
        data1.put("post", "亀がいた");
        data1.put("good", "12");
        data1.put("location", "福岡市博多区");
        listData.add(data1);

        HashMap<String, String> data2 = new HashMap<String, String>();
        data2.put("username", "西山大成");
        data2.put("userid", "ijdsbf");
        data2.put("date", "2020/1/25");
        data2.put("post", "馬さん、、");
        data2.put("good", "5");
        data2.put("location", "山口県");
        listData.add(data2);

        sAdapter = new SimpleAdapter(this, listData,
                R.layout.activity_timeline_list,
                new String[]{"usericon", "username", "userid", "date", "post", "postimage", "good",
                        "location"},
                new int[]{R.id.imageView, R.id.textView, R.id.textView2, R.id.textView3,
                        R.id.editTextTextPersonName2, R.id.imageView2, R.id.textView4,
                        R.id.textView5});
        lv = (ListView) findViewById(R.id.timeLineListView);
        lv.setAdapter(sAdapter);
    }
}