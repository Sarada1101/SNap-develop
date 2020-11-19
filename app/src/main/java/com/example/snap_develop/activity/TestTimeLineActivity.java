package com.example.snap_develop.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.viewModel.PostViewModel;

import java.util.ArrayList;
import java.util.List;

public class TestTimeLineActivity extends AppCompatActivity {

    PostViewModel postViewModel;
    List<List<PostBean>> timeLine = new ArrayList<>();
    Integer followCount = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_time_line);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        //フォローリスト
        List<String> userList = new ArrayList<>();
        userList.add("p1Fx9i59HhpjLFSvteck");
        userList.add("nGBoEuFPNBf9LmpLuFA6aGKshBr1");

        //フォロー数の取得
        followCount = userList.size();

        postViewModel.fetchTimeLine(userList);

        postViewModel.getTimeLine().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> timeLine) {
                System.out.println("---------------OnChanged:timeLine-----------------");

                count++;
                if (count >= followCount) {
                    for (PostBean bean : timeLine) {
                        System.out.println(bean.getMessage());
                    }
                }
            }
        });

    }
}