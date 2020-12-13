package com.example.snap_develop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.snap_develop.MyDebugTree;
import com.example.snap_develop.R;
import com.example.snap_develop.activity.UserActivity;
import com.example.snap_develop.bean.PostBean;
import com.example.snap_develop.bean.UserBean;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class TimelineAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private int mLayoutID;
    List<Map<String, Object>> mTimelineDataMapList;

    static class ViewHolder {
        ImageView icon;
        TextView username;
        TextView uid;
        TextView message;
        TextView datetime;
        TextView goodCount;
        TextView latLng;
        ImageView photo;
        ConstraintLayout userInfo;
    }

    public TimelineAdapter(Context context, List<Map<String, Object>> timelineDataMapList, int rowLayout) {
        Timber.i(MyDebugTree.START_LOG);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutID = rowLayout;
        this.mTimelineDataMapList = timelineDataMapList;
    }

    @Override
    public int getCount() {
        return this.mTimelineDataMapList.size();
    }

    @Override
    public Map<String, Object> getItem(int position) {
        return this.mTimelineDataMapList.get(position);
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
            holder.icon = convertView.findViewById(R.id.timelineIconImageView);
            holder.datetime = convertView.findViewById(R.id.timelineDatetimeTextView);
            holder.goodCount = convertView.findViewById(R.id.timelineGoodCountTextView);
            holder.latLng = convertView.findViewById(R.id.timelineLatLngTextView);
            holder.message = convertView.findViewById(R.id.timeLineMessage);
            holder.photo = convertView.findViewById(R.id.timelinePhotoImageView);
            holder.uid = convertView.findViewById(R.id.timelineUidTextView);
            holder.username = convertView.findViewById(R.id.timelineNameTextView);
            holder.userInfo = convertView.findViewById(R.id.userInfoConstraintLayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UserBean userBean = (UserBean) mTimelineDataMapList.get(position).get("userBean");
        PostBean postBean = (PostBean) mTimelineDataMapList.get(position).get("postBean");

        holder.icon.setImageBitmap(userBean.getIcon());
        holder.username.setText(userBean.getName());
        holder.uid.setText(userBean.getUid());
        holder.message.setText(postBean.getMessage());
        holder.datetime.setText(postBean.getStrDatetime());

        if (postBean.getPhoto() != null) holder.photo.setImageBitmap(postBean.getPhoto());

        if (postBean.getType().equals("post")) {
            holder.goodCount.setText(Integer.toString(postBean.getGoodCount()));
            holder.latLng.setText(String.format("%d, %d", (int) postBean.getLatLng().latitude,
                    (int) postBean.getLatLng().longitude));
        }

        holder.userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UserActivity.class).putExtra("uid", userBean.getUid()));
            }
        });
        return convertView;
    }
}
