package com.example.snap_develop.activity;

import static android.text.TextUtils.isEmpty;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.model.FollowListAdapter;
import com.example.snap_develop.viewModel.FollowViewModel;
import com.example.snap_develop.viewModel.PostViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimelineActivity extends AppCompatActivity {

    PostViewModel postViewModel;
    FollowViewModel followViewModel;
    Integer followCount = 0;
    int count[] = {0, 0, 0};
    ListView lv;
    FirebaseUser currentUser;
    String currentUid;
    List<PostBean> resultPostList;
    HashMap<String, Map<String, Object>> resultUserData;
    Map<String, String> pathList;
    ArrayList<HashMap<String, HashMap<String, Object>>> dataList;
    HashMap<String, List<String>> keyData;
    Map<String, HashMap<String, Integer>> viewData;
    FollowListAdapter followListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        followViewModel = new ViewModelProvider(this).get(FollowViewModel.class);

        //現在ログイン中のユーザーのUidを取得する処理
        //currentUser = userViewModel.getCurrentUser();
        //currentUid = currentUser.getUid();

        //テストデータ
        currentUid = "UtJFmruiiBS28WH333AE6YHEjf72";

        //フォローしている人数を取得する処理
        followViewModel.fetchCount(currentUid, "following_count");

        followViewModel.getUserCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer userCount) {
                followCount = userCount;
                followViewModel.fetchFollowingList(currentUid);
            }
        });

        followViewModel.getFollowList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> followList) {
                System.out.println("--------------------onChanged:count----------------------");
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

                    postViewModel.fetchTimeLine(followList);
                }
            }
        });

        postViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @Override
            public void onChanged(List<PostBean> timeLine) {
                System.out.println("---------------OnChanged:timeLine-----------------");
                count[1]++;
                if (count[1] >= followCount) {
                    pathList = new HashMap<>();
                    resultPostList = timeLine;
                    for (PostBean bean : resultPostList) {
                        if (!isEmpty(bean.getPhotoName())) {
                            pathList.put(bean.getPostId(), bean.getPhotoName());
                        } else {
                            System.out.println("no");
                        }
                    }
                    System.out.println(pathList);
                    postViewModel.fetchPostPictures(pathList);
                }
            }
        });

        postViewModel.getTimeLinePictureList().observe(this, new Observer<Map<String, Bitmap>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(Map<String, Bitmap> timeLinePictureList) {
                System.out.println("---------------OnChanged:getPicture-----------------");
                count[2]++;
                if (count[2] >= pathList.size()) {
                    System.out.println(timeLinePictureList.size());


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

                    /////////////////////////////////////////////////////////////////////////////////////
                    //アダプターに渡すデータ作成///////////////////////////////////////////////////////////
                    dataList = new ArrayList<>();
                    for (PostBean postBean : resultPostList) {
                        HashMap<String, Object> textData = new HashMap<>();
                        textData.put("userId", postBean.getUid());
                        textData.put("userName", resultUserData.get(postBean.getUid()).get("userName"));
                        String str = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(postBean.getDatetime());
                        textData.put("date", str);
                        textData.put("message", postBean.getMessage());
                        textData.put("good", String.valueOf(postBean.getGoodCount()));
                        textData.put("location", "住所");
                        textData.put("anoymous", postBean.isAnonymous());
                        textData.put("danger", postBean.isDanger());


                        HashMap<String, Object> imageData = new HashMap<>();
                        imageData.put("userIcon", resultUserData.get(postBean.getUid()).get("userIcon"));
                        imageData.put("postPicture", timeLinePictureList.get(postBean.getPostId()));


                        HashMap<String, HashMap<String, Object>> addData = new HashMap<>();
                        addData.put(FollowListAdapter.TEXT, textData);
                        addData.put(FollowListAdapter.IMAGE, imageData);

                        dataList.add(addData);
                        /////////////////////////////////////////////////////////////////////////////////////
                    }

                    /////////////////////////////////////////////////////////////////////////////////////
                    //アダプターに渡すキーデータの作成
                    List<String> textKeyList = new ArrayList<>();
                    textKeyList.add("userId");
                    textKeyList.add("userName");
                    textKeyList.add("date");
                    textKeyList.add("message");
                    textKeyList.add("good");
                    textKeyList.add("location");
                    textKeyList.add("anoymous");
                    textKeyList.add("danger");

                    List<String> imageKeyList = new ArrayList<>();
                    imageKeyList.add("userIcon");
                    imageKeyList.add("postPicture");

                    keyData = new HashMap<>();
                    keyData.put(FollowListAdapter.TEXT, textKeyList);
                    keyData.put(FollowListAdapter.IMAGE, imageKeyList);
                    /////////////////////////////////////////////////////////////////////////////////////

                    /////////////////////////////////////////////////////////////////////////////////////
                    //アダプターに渡すviewのデータの作成
                    HashMap<String, Integer> textViewData = new HashMap<>();
                    textViewData.put("userId", R.id.textView2);
                    textViewData.put("userName", R.id.textView);
                    textViewData.put("date", R.id.textView3);
                    textViewData.put("message", R.id.timeLineMessage);
                    textViewData.put("good", R.id.textView4);
                    textViewData.put("location", R.id.textView5);
                    textViewData.put("anoymous", R.id.anonymous);
                    textViewData.put("danger", R.id.timeLinePost);

                    HashMap<String, Integer> imageViewData = new HashMap<>();
                    imageViewData.put("userIcon", R.id.imageView);
                    imageViewData.put("postPicture", R.id.imageView2);

                    viewData = new HashMap<>();
                    viewData.put(FollowListAdapter.TEXT, textViewData);
                    viewData.put(FollowListAdapter.IMAGE, imageViewData);
                    /////////////////////////////////////////////////////////////////////////////////////

                    //アダプターを作成してリストビューにセット
                    followListAdapter = new FollowListAdapter(TimelineActivity.this, dataList,
                            R.layout.activity_timeline_list, viewData, keyData);
                    lv = (ListView) findViewById(R.id.timeLineListView);
                    lv.setAdapter(followListAdapter);
                }
            }
        });
    }
}
