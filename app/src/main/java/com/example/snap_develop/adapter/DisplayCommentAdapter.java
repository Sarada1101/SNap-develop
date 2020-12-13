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

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DisplayCommentAdapter extends BaseAdapter {

    private List<Map<String, Object>> mCommentDataMapList;
    private LayoutInflater mInflater;
    private int mLayoutID;

    static class ViewHolder {
        ImageView icon;
        TextView username;
        TextView uid;
        TextView comment;
        TextView datetime;
    }


    public DisplayCommentAdapter(Context context, List<Map<String, Object>> commentDataMapList, int rowLayout) {
        Timber.i(MyDebugTree.START_LOG);
        this.mCommentDataMapList = commentDataMapList;
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutID = rowLayout;
    }


    @Override
    public int getCount() {
        return this.mCommentDataMapList.size();
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
        Timber.i(String.format("%s %s=%s, %s=%s, %s=%s", MyDebugTree.INPUT_LOG, "position", position, "convertView",
                convertView, "parent", parent));
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

        UserBean userBean = (UserBean) mCommentDataMapList.get(position).get("userBean");
        PostBean postBean = (PostBean) mCommentDataMapList.get(position).get("postBean");

        holder.icon.setImageBitmap(userBean.getIcon());
        holder.username.setText(userBean.getName());
        holder.uid.setText(userBean.getUid());
        holder.comment.setText(postBean.getMessage());
        holder.datetime.setText(postBean.getStrDatetime());

        return convertView;
    }
}
