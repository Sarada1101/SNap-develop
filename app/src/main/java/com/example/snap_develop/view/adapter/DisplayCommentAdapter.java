package com.example.snap_develop.view.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.snap_develop.view.ui.UserActivity;
import com.example.snap_develop.view.viewHolder.DisplayCommentViewHolder;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DisplayCommentAdapter extends RecyclerView.Adapter<DisplayCommentViewHolder> {

    private Context mContext;
    private List<Map<String, Object>> mCommentDataMapList;

    public DisplayCommentAdapter(Context context, List<Map<String, Object>> commentDataMapList) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "context", context, "commentDataMapList",
                commentDataMapList));

        this.mContext = context;
        this.mCommentDataMapList = commentDataMapList;
    }


    @NonNull
    @Override
    public DisplayCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "parent", parent, "viewType", viewType));

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_display_comment_item, parent,
                false);
        return new DisplayCommentViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(@NonNull DisplayCommentViewHolder holder, int position) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "holder", holder, "position", position));

        final UserBean userBean = (UserBean) mCommentDataMapList.get(position).get("userBean");
        PostBean postBean = (PostBean) mCommentDataMapList.get(position).get("postBean");
        holder.mIconImageView.setImageBitmap(userBean.getIcon());
        holder.mUserNameTextView.setText(userBean.getName());
        holder.mUserIdTextView.setText(userBean.getUid());
        holder.mMessageTextView.setText(postBean.getMessage());
        holder.mDatetimeTextView.setText(postBean.getStrDatetime());

        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
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
            holder.mConstraintLayout.setOnClickListener(null);
        }

        if (userBean.getPublicationArea().equals("anonymous")) {
            holder.mIconImageView.setImageBitmap(
                    MainApplication.getBitmapFromVectorDrawable(mContext, R.drawable.ic_baseline_account_circle_24));
            holder.mUserNameTextView.setText("匿名");
            holder.mUserIdTextView.setText("匿名");
            holder.mConstraintLayout.setOnClickListener(null);
        }
    }


    @Override
    public int getItemCount() {
        Timber.i(MyDebugTree.START_LOG);
        return mCommentDataMapList.size();
    }
}
