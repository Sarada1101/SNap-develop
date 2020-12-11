package com.example.snap_develop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snap_develop.MyDebugTree;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class FollowListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private int mLayoutID;
    private int[] idList;
    private ArrayList<HashMap<String, Object>> dataList;

    static class ViewHolder {
        ImageView userIcon;
        TextView userName;
        TextView userId;
    }

    public FollowListAdapter(Context context, ArrayList<HashMap<String, Object>> dataList, int rowLayout, int[] idList) {
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutID = rowLayout;
        this.dataList = dataList;
        this.idList = idList;
    }

    @Override
    public int getCount() {
        return this.dataList.size();
    }

    @Override
    public HashMap<String, Object> getItem(int position) {
        return this.dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Timber.i(MyDebugTree.START_LOG);
        ViewHolder holder;

        HashMap<String, Object> nextData = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutID, null);
            holder = new ViewHolder();
            holder.userName = convertView.findViewById(idList[0]);
            holder.userId = convertView.findViewById(idList[1]);
            holder.userIcon = convertView.findViewById(idList[2]);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userIcon.setImageBitmap((Bitmap) nextData.get("userIcon"));
        holder.userName.setText((String) nextData.get("userName"));
        holder.userId.setText((String) nextData.get("userId"));

        return convertView;
    }
}