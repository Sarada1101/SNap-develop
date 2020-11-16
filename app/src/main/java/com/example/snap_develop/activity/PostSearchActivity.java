package com.example.snap_develop.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snap_develop.R;
import com.example.snap_develop.util.LogUtil;

public class PostSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtil.getClassName(), LogUtil.getLogMessage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_search);
    }
}
