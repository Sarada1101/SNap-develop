package com.example.snap_develop.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.databinding.ActivityPostSearchBinding;
import com.example.snap_develop.view.adapter.PostSearchAdapter;
import com.example.snap_develop.viewModel.PostViewModel;
import com.example.snap_develop.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PostSearchActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        SearchView.OnQueryTextListener {

    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    private ActivityPostSearchBinding mBinding;
    private PostSearchAdapter mPostSearchAdapter;
    private ListView mListView;
    List<Map<String, Object>> mPostDataMapList;
    private List<PostBean> mPostBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i(MyDebugTree.START_LOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_search);
        setTitle("検索");

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_post_search);

        // 検索リストを取得したら投稿ごとのユーザー情報を取得する
        mPostViewModel.getPostList().observe(this, new Observer<List<PostBean>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(List<PostBean> postList) {
                List<String> uidList = new ArrayList<>();
                for (final PostBean postBean : postList) {
                    uidList.add(postBean.getUid());
                }
                mUserViewModel.fetchUserInfoList(uidList);

                //投稿を日時順にソート
                class PostSortCompare implements Comparator<PostBean> {
                    @Override
                    public int compare(PostBean o1, PostBean o2) {
                        Date sortKey1 = o1.getDatetime();
                        Date sortKey2 = o2.getDatetime();
                        return sortKey1.compareTo(sortKey2);
                    }
                }
                Collections.sort(postList, new PostSortCompare().reversed());
                mPostBeanList = postList;
            }
        });

        //コメントごとのユーザー情報を取得したらリスト表示する
        mUserViewModel.getUserList().observe(this, new Observer<List<UserBean>>() {
            @Override
            public void onChanged(List<UserBean> userList) {
                // コメントリストとユーザー情報を紐付ける
                mPostDataMapList = new ArrayList<>();
                for (PostBean postBean : mPostBeanList) {
                    Map<String, Object> postDataMap = new HashMap<>();
                    postDataMap.put("postBean", postBean);
                    for (UserBean userBean : userList) {
                        if (userBean.getUid().equals(postBean.getUid())) {
                            postDataMap.put("userBean", userBean);
                            break;
                        }
                    }
                    mPostDataMapList.add(postDataMap);
                }
                mPostSearchAdapter = new PostSearchAdapter(PostSearchActivity.this,
                        mPostDataMapList, R.layout.activity_post_search_item);
                mListView = mBinding.searchPostListView;
                mListView.setAdapter(mPostSearchAdapter);
                mListView.setOnItemClickListener(PostSearchActivity.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s", MyDebugTree.INPUT_LOG, "menu", menu));
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search_menu_search_view);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        mPostViewModel.fetchSearchPost(query);
        return false;
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PostBean postBean = (PostBean) mPostDataMapList.get(position).get("postBean");
        if (postBean.getType().equals("post")) {
            startActivity(new Intent(getApplication(), DisplayCommentActivity.class).putExtra("postPath",
                    postBean.getPostPath()));
        } else if (postBean.getType().equals("comment")) {
            startActivity(new Intent(getApplication(), DisplayCommentActivity.class).putExtra("postPath",
                    postBean.getParentPost()));
        }
    }
}
