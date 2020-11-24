package com.example.snap_develop.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    ArrayList<HashMap<String, Object>> dataList;
    int[] id;

    static class ViewHolder {
        TextView name;
        TextView uid;
        ImageView icon;
    }

    public FollowListAdapter(Context context, ArrayList<HashMap<String, Object>> dataList, int rowLayout, int[] idList) {
        this.inflater = LayoutInflater.from(context);
        this.layoutID = rowLayout;
        this.dataList = dataList;
        this.id = idList;
    }

    @Override
    public int getCount() {
        return this.dataList.size();
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutID, null);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(id[0]);
            holder.uid = convertView.findViewById(id[1]);
            holder.icon = convertView.findViewById(id[2]);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        System.out.println(holder.name);
        System.out.println((String) dataList.get(position).get("username"));
        holder.name.setText((String) dataList.get(position).get("username"));
        holder.uid.setText((String) dataList.get(position).get("userid"));
        holder.icon.setImageBitmap((Bitmap) dataList.get(position).get("usericon"));

        return convertView;
    }
}
