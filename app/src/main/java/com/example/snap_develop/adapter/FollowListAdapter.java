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
import com.example.snap_develop.R;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class FollowListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private int mLayoutID;
    ArrayList<HashMap<String, Object>> dataList;

    static class ViewHolder {
        ImageView userIcon;
        TextView userName;
        TextView uid;
    }

    public FollowListAdapter(Context context, ArrayList<HashMap<String, Object>> dataList, int rowLayout) {
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutID = rowLayout;
        this.dataList = dataList;
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
            holder.userIcon = convertView.findViewById(R.id.listIconImageView);
            holder.userName = convertView.findViewById(R.id.listUserNameTextView);
            holder.uid = convertView.findViewById(R.id.listUserIdTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userIcon.setImageBitmap((Bitmap) nextData.get("userIcon"));
        holder.userName.setText((String) nextData.get("userName"));
        holder.uid.setText((String) nextData.get("uid"));

        return convertView;
    }
}