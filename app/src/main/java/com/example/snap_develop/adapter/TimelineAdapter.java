package com.example.snap_develop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class TimelineAdapter extends BaseAdapter {

    public static final String TEXT = "TEXT";
    public static final String IMAGE = "IMAGE";
    private LayoutInflater mInflater;
    private int mLayoutID;
    ArrayList<HashMap<String, Object>> dataList;
    HashMap<String, Integer> textViewData;
    HashMap<String, Integer> imageViewData;
    List<String> textKeyList;
    List<String> imageKeyList;

    static class ViewHolder {
        ImageView userIcon;
        TextView userName;
        TextView uid;
        TextView message;
        TextView datetime;
        TextView goodCount;
        TextView location;
        TextView anonymous;
        ConstraintLayout userInfo;
        ConstraintLayout danger;
        ImageView postPicture;
    }

    public TimelineAdapter(Context context, ArrayList<HashMap<String, Object>> dataList, int rowLayout) {
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

        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutID, null);
            holder = new ViewHolder();
            holder.userIcon = convertView.findViewById(R.id.imageView);
            holder.anonymous = convertView.findViewById(R.id.anonymous);
            holder.danger = convertView.findViewById(R.id.timeLinePost);
            holder.datetime = convertView.findViewById(R.id.textView3);
            holder.goodCount = convertView.findViewById(R.id.textView4);
            holder.location = convertView.findViewById(R.id.textView5);
            holder.message = convertView.findViewById(R.id.timeLineMessage);
            holder.postPicture = convertView.findViewById(R.id.imageView2);
            holder.uid = convertView.findViewById(R.id.textView2);
            holder.userInfo = convertView.findViewById(R.id.userInfo);
            holder.userName = convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, Object> nextData = getItem(position);

        holder.userName.setText((String) nextData.get("userName"));
        holder.uid.setText((String) nextData.get("uid"));
        holder.message.setText((String) nextData.get("message"));
        holder.location.setText((String) nextData.get("location"));
        holder.goodCount.setText((String) nextData.get("goodCount"));
        holder.datetime.setText((String) nextData.get("date"));
        holder.userIcon.setImageBitmap((Bitmap) nextData.get("userIcon"));

        if ((Boolean) nextData.get("anonymous")) {
            holder.anonymous.setVisibility(View.VISIBLE);
            holder.userInfo.setVisibility(View.INVISIBLE);
        } else {
            holder.anonymous.setVisibility(View.INVISIBLE);
            holder.userInfo.setVisibility(View.VISIBLE);
        }

        if ((Boolean) nextData.get("danger")) {
            holder.danger.setBackgroundColor(Color.rgb(255, 100, 100));
        }


        if (nextData.get("postPicture") == null) {
            holder.postPicture.setVisibility(View.GONE);
        } else {
            holder.postPicture.setImageBitmap((Bitmap) nextData.get("postPicture"));
        }

        return convertView;
    }
}