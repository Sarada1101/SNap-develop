package com.example.snap_develop.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.activity.DisplayCommentActivity;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;
import com.example.snap_develop.view.viewHolder.UserViewHolder;

import java.util.List;

import timber.log.Timber;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private Context mContext;
    private List<PostBean> mPostBeanList;
    private UserBean mUserBean;

    public UserAdapter(Context context, List<PostBean> postList, UserBean userBean) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "context", context, "postList",
                postList, "userBean", userBean));

        this.mContext = context;
        this.mPostBeanList = postList;
        this.mUserBean = userBean;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "parent", parent, "viewType", viewType));

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_item, parent,
                false);
        return new UserViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Timber.i(MyDebugTree.START_LOG);
        Timber.i(String.format("%s %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "holder", holder, "position", position));

        final PostBean postBean = mPostBeanList.get(position);
        holder.mIconImageView.setImageBitmap(mUserBean.getIcon());
        holder.mUserNameTextView.setText(mUserBean.getName());
        holder.mUserIdTextView.setText(mUserBean.getUid());
        holder.mMessageTextView.setText(postBean.getMessage());
        holder.mDatetimeTextView.setText(postBean.getStrDatetime());

        if (postBean.getPhoto() != null) holder.mPhotoImageView.setImageBitmap(postBean.getPhoto());

        if (postBean.getType().equals("post")) {
            holder.mGoodCountTextView.setText(Integer.toString(postBean.getGoodCount()));
            holder.mLatLngTextView.setText(
                    String.format("%d, %d", (int) postBean.getLatLng().latitude, (int) postBean.getLatLng().longitude));
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
        return mPostBeanList.size();
    }
}
