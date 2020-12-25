package com.example.snap_develop.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.bean.UserBean;

import java.util.List;

import timber.log.Timber;

public class FollowListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private int mLayoutID;
    private List<UserBean> mFollowList;

    static class ViewHolder {
        ImageView icon;
        TextView username;
        TextView uid;
    }

    public FollowListAdapter(Context context, List<UserBean> followList, int rowLayout) {
        Timber.i(MyDebugTree.START_LOG);
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutID = rowLayout;
        this.mFollowList = followList;
    }

    @Override
    public int getCount() {
        return this.mFollowList.size();
    }

    @Override
    public UserBean getItem(int position) {
        return this.mFollowList.get(position);
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
            holder.username = convertView.findViewById(R.id.nameTextView);
            holder.uid = convertView.findViewById(R.id.idTextView);
            holder.icon = convertView.findViewById(R.id.iconImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserBean userBean = mFollowList.get(position);
        holder.username.setText(userBean.getName());
        holder.uid.setText(userBean.getUid());
        holder.icon.setImageBitmap(userBean.getIcon());

        return convertView;
    }
}
