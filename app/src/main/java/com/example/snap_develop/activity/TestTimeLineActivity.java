package com.example.snap_develop.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.viewModel.PostViewModel;

import java.util.ArrayList;
import java.util.List;

public class TestTimeLineActivity extends AppCompatActivity {

    PostViewModel mPostViewModel = new PostViewModel();
    List<List<PostBean>> timeLine = new ArrayList<>();
    Integer userCount = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_time_line);

        //フォローリスト
        List<String> userList = new ArrayList<>();
        userList.add("p1Fx9i59HhpjLFSvteck");
        userList.add("nGBoEuFPNBf9LmpLuFA6aGKshBr1");

        //フォロー数の取得
        userCount = userList.size();

        mPostViewModel.fetchTimeLine(userList);

        mPostViewModel.getTimeLine().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> timeLine) {
                System.out.println("---------------OnChanged:timeLine-----------------");

                count++;
                if (count >= userCount) {
                    for (PostBean bean : timeLine) {
                        System.out.println(bean.getMessage());
                    }
                }
            }
        });

    }
}