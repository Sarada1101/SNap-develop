package com.example.snap_develop.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.activity.DisplayCommentActivity;
import com.example.snap_develop.activity.UserActivity;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.view.viewHolder.PostSearchViewHolder;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PostSearchAdapter extends RecyclerView.Adapter<PostSearchViewHolder> {

    private Context mContext;
    private List<Map<String, Object>> mPostDataMapList;

    public PostSearchAdapter(Context context, List<Map<String, Object>> postDataMapList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "context", context, "postDataMapList",
                postDataMapList));

        this.mContext = context;
        this.mPostDataMapList = postDataMapList;
    }


    @NonNull
    @Override
    public PostSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "parent", parent, "viewType", viewType));

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_search_item, parent,
                false);
        return new PostSearchViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(@NonNull PostSearchViewHolder holder, int position) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "holder", holder, "position", position));

        final UserBean userBean = (UserBean) mPostDataMapList.get(position).get("userBean");
        final PostBean postBean = (PostBean) mPostDataMapList.get(position).get("postBean");
        holder.mIconImageView.setImageBitmap(userBean.getIcon());
        holder.mUserNameTextView.setText(userBean.getName());
        holder.mUserIdTextView.setText(userBean.getUid());
        holder.mMessageTextView.setText(postBean.getMessage());
        holder.mDatetimeTextView.setText(postBean.getStrDatetime());

        if (postBean.getPhoto() != null) holder.mPhotoImageView.setImageBitmap(postBean.getPhoto());

        if (postBean.getType().equals("post")) {
            holder.mGoodCountTextView.setText(Integer.toString(postBean.getGoodCount()));
            holder.mLatLngTextView.setText(
                    String.format("%d, %d", (int) postBean.getLatLng().latitude, (int) postBean.getLatLng().longitude));
        }

        holder.mUserInfoConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UserActivity.class).putExtra("uid", userBean.getUid()));
            }
        });

        if (postBean.isAnonymous()) {
            holder.mIconImageView.setImageBitmap(
                    MainApplication.getBitmapFromVectorDrawable(mContext, R.drawable.ic_baseline_account_circle_24));
            holder.mUserNameTextView.setText("匿名");
            holder.mUserIdTextView.setText("匿名");
            holder.mUserInfoConstraintLayout.setOnClickListener(null);
        }

        if (userBean.getPublicationArea().equals("anonymous")) {
            holder.mIconImageView.setImageBitmap(
                    MainApplication.getBitmapFromVectorDrawable(mContext, R.drawable.ic_baseline_account_circle_24));
            holder.mUserNameTextView.setText("匿名");
            holder.mUserIdTextView.setText("匿名");
            holder.mUserInfoConstraintLayout.setOnClickListener(null);
        }

        if (postBean.isDanger()) {
            holder.mConstraintLayout.setBackgroundColor(Color.rgb(240, 96, 96));
        }

        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postBean.getType().equals("post")) {
                    mContext.startActivity(new Intent(mContext, DisplayCommentActivity.class).putExtra("postPath",
                            postBean.getPostPath()));
                } else if (postBean.getType().equals("comment")) {
                    mContext.startActivity(new Intent(mContext, DisplayCommentActivity.class).putExtra("postPath",
                            postBean.getParentPost()));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        Timber.i(MyDebugTree.START_LOG);
        return mPostDataMapList.size();
    }
}
