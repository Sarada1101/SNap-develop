package com.example.snap_develop.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.MainApplication;
import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.view.ui.DisplayCommentActivity;
import com.example.snap_develop.view.ui.UserActivity;
import com.example.snap_develop.view.view_holder.TimelineViewHolder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineViewHolder> {

    private final Context mContext;
    private final List<Map<String, Object>> mTimelineDataMapList;

    public TimelineAdapter(Context context, List<Map<String, Object>> timelineDataMapList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "context", context, "timelineDataMapList",
                timelineDataMapList));

        this.mContext = context;
        this.mTimelineDataMapList = timelineDataMapList;
    }


    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "parent", parent, "viewType", viewType));

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_timeline_item, parent,
                false);
        return new TimelineViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "holder", holder, "position", position));

        final UserBean userBean = Objects.requireNonNull((UserBean) mTimelineDataMapList.get(position).get("userBean"));
        final PostBean postBean = Objects.requireNonNull((PostBean) mTimelineDataMapList.get(position).get("postBean"));
        holder.mIconImageView.setImageBitmap(userBean.getIcon());
        holder.mUserNameTextView.setText(userBean.getName());
        holder.mUserIdTextView.setText(userBean.getUid());
        holder.mMessageTextView.setText(postBean.getMessage());
        holder.mDatetimeTextView.setText(postBean.getStrDatetime());

        if (postBean.getPhoto() != null) holder.mPhotoImageView.setImageBitmap(postBean.getPhoto());

        if (TextUtils.equals(postBean.getType(), "post")) {
            holder.mGoodCountTextView.setText(postBean.getGoodCount());
            holder.mLatLngTextView.setText(
                    String.format("%s, %s", (int) postBean.getLatLng().latitude, (int) postBean.getLatLng().longitude));
        }

        holder.mUserInfoConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UserActivity.class).putExtra("uid", userBean.getUid()));
            }
        });

        if (postBean.isAnonymous() || TextUtils.equals(userBean.getPublicationArea(), "anonymous")) {
            setAnonymousUser(holder);
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


    private void setAnonymousUser(TimelineViewHolder holder) {
        holder.mIconImageView.setImageBitmap(
                MainApplication.getBitmapFromVectorDrawable(mContext, R.drawable.ic_baseline_account_circle_24));
        holder.mUserNameTextView.setText("匿名");
        holder.mUserIdTextView.setText("匿名");
        holder.mConstraintLayout.setOnClickListener(null);
    }


    @Override
    public int getItemCount() {
        Timber.i(MyDebugTree.START_LOG);
        return mTimelineDataMapList.size();
    }
}
