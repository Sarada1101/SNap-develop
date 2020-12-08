package com.example.snap_develop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;

import java.util.ArrayList;

import timber.log.Timber;

public class DisplayCommentAdapter extends BaseAdapter {

    private ArrayList<UserBean> mUserList;
    private ArrayList<PostBean> mPostList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int mLayoutID;

    static class ViewHolder {
        ImageView icon;
        TextView username;
        TextView uid;
        TextView comment;
        TextView datetime;
    }


    public DisplayCommentAdapter(Context context, ArrayList<UserBean> userList, ArrayList<PostBean> postList,
            int rowLayout) {
        Timber.i(MyDebugTree.START_LOG);
        this.mContext = context;
        this.mUserList = userList;
        this.mPostList = postList;
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutID = rowLayout;
    }


    @Override
    public int getCount() {
        return this.mPostList.size();
    }


    @Override
    public Object getItem(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Timber.i(MyDebugTree.START_LOG);
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutID, null);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.listIconImageView);
            holder.username = convertView.findViewById(R.id.listUserNameTextView);
            holder.uid = convertView.findViewById(R.id.listUserIdTextView);
            holder.comment = convertView.findViewById(R.id.listCommentTextView);
            holder.datetime = convertView.findViewById(R.id.dateTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageBitmap(mUserList.get(position).getIcon());
        holder.username.setText(mUserList.get(position).getName());
        holder.uid.setText(mUserList.get(position).getUid());
        holder.comment.setText(mPostList.get(position).getMessage());
        holder.datetime.setText(mPostList.get(position).getStrDatetime());

        return convertView;
    }
}
