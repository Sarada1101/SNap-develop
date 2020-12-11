package com.example.snap_develop.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.adapter.TimelineAdapter;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static android.text.TextUtils.isEmpty;

public class TimelineActivity extends AppCompatActivity implements View.OnClickListener {

    PostViewModel mPostViewModel;
    FollowViewModel mFollowViewModel;
    UserViewModel mUserViewModel;
    Integer followCount = 0;
    int count[] = {0, 0, 0};
    ListView lv;
    String currentUid;
    List<PostBean> resultPostList;
    HashMap<String, Map<String, Object>> resultUserData;
    Map<String, String> pathList;
    ArrayList<HashMap<String, Object>> dataList;
    TimelineAdapter mTimelineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mFollowViewModel = new ViewModelProvider(this).get(FollowViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        findViewById(R.id.timelineImageButton).setOnClickListener(this);
        findViewById(R.id.mapImageButton).setOnClickListener(this);
        findViewById(R.id.userImageButton).setOnClickListener(this);

        //現在ログイン中のユーザーのUidを取得する処理
        currentUid = mUserViewModel.getCurrentUser().getUid();

        //フォローしている人数を取得する処理
        mFollowViewModel.fetchCount(currentUid, "following_count");

        mFollowViewModel.getUserCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer userCount) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "userCount", userCount));
                followCount = userCount;
                //mFollowViewModel.fetchFollowingList(currentUid);
            }
        });

        mFollowViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "followList", followList));
                count[0]++;
                if (count[0] >= followCount) {

                    //currentUidの人がフォローしている人のデータ(ID、名前、アイコン画像)の作成
                    resultUserData = new HashMap<>();
                    for (UserBean bean : followList) {
                        Map<String, Object> addUserData = new HashMap<>();
                        addUserData.put("userName", bean.getName());
                        addUserData.put("userIcon", bean.getIcon());
                        resultUserData.put(bean.getUid(), addUserData);
                    }

                    mPostViewModel.fetchTimeLine(followList);
                }
            }
        });

        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> timeLine) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "timeLine", timeLine));
                count[1]++;
                if (count[1] >= followCount) {
                    pathList = new HashMap<>();
                    resultPostList = timeLine;
                    for (PostBean bean : resultPostList) {
                        if (!isEmpty(bean.getPhotoName())) {
                            pathList.put(bean.getDocumentId(), bean.getPhotoName());
                        }
                    }
                    System.out.println(pathList);
                    mPostViewModel.fetchPostPictures(pathList);
                }
            }
        });

        mPostViewModel.getTimeLinePictureList().observe(this, new Observer<Map<String, Bitmap>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(Map<String, Bitmap> timeLinePictureList) {
                Timber.i(MyDebugTree.START_LOG);
                Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "timeLinePictureList", timeLinePictureList));
                count[2]++;
                if (count[2] >= pathList.size()) {
                    /////////////////////////////////////////////////////////////////////////////////////
                    //投稿を日時順にソート
                    class PostSortCompare implements Comparator<PostBean> {
                        @Override
                        public int compare(PostBean o1, PostBean o2) {
                            Date sortKey1 = o1.getDatetime();
                            Date sortKey2 = o2.getDatetime();
                            return sortKey1.compareTo(sortKey2);
                        }
                    }
                    Collections.sort(resultPostList, new PostSortCompare().reversed());
                    /////////////////////////////////////////////////////////////////////////////////////

                    //アダプターに渡すデータ作成///////////////////////////////////////////////////////////
                    dataList = new ArrayList<>();
                    for (PostBean postBean : resultPostList) {
                        HashMap<String, Object> addData = new HashMap<>();
                        addData.put("userId", postBean.getUid());
                        addData.put("userName", resultUserData.get(postBean.getUid()).get("userName"));
                        String str = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(postBean.getDatetime());
                        addData.put("date", str);
                        addData.put("message", postBean.getMessage());
                        addData.put("goodCount", postBean.getGoodCount());
                        addData.put("location", "住所");
                        addData.put("anonymous", postBean.isAnonymous());
                        addData.put("danger", postBean.isDanger());
                        addData.put("userIcon", resultUserData.get(postBean.getUid()).get("userIcon"));
                        addData.put("postPicture", timeLinePictureList.get(postBean.getDocumentId()));

                        dataList.add(addData);
                    }
                    /////////////////////////////////////////////////////////////////////////////////////

                    //タイムライン表示
                    displayTimeline();
                }
            }
        });
    }

    protected void displayTimeline() {
        Timber.i(MyDebugTree.START_LOG);
        //アダプターを作成してリストビューにセット
        mTimelineAdapter = new TimelineAdapter(this, dataList, R.layout.activity_timeline_list);
        lv = (ListView) findViewById(R.id.timeLineListView);
        lv.setAdapter(mTimelineAdapter);
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
