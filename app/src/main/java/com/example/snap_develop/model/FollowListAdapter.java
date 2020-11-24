package com.example.snap_develop.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snap_develop.R;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    ArrayList<HashMap<String, Object>> dataList;

    static class ViewHolder {
        TextView name;
        TextView uid;
        ImageView icon;
    }

    public FollowListAdapter(Context context, ArrayList<HashMap<String, Object>> dataList, int rowLayout) {
        this.inflater = LayoutInflater.from(context);
        this.layoutID = rowLayout;
        this.dataList = dataList;
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
            holder.icon = convertView.findViewById(R.id.iconImageView);
            holder.name = convertView.findViewById(R.id.nameTextView);
            holder.uid = convertView.findViewById(R.id.idTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon.setImageBitmap((Bitmap) dataList.get(position).get("usericon"));
        holder.name.setText((String) dataList.get(position).get("username"));
        holder.uid.setText((String) dataList.get(position).get("userid"));

        return convertView;
    }
}
