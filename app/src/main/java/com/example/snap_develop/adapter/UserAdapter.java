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

public class UserAdapter extends BaseAdapter {

    private ArrayList<PostBean> mPostList;
    private Context mContext;
    private UserBean mUserBean;
    private LayoutInflater mInflater;
    private int mLayoutID;

    static class ViewHolder {
        ImageView icon;
        TextView username;
        TextView uid;
        TextView post;
        ImageView photo;
        TextView goodCount;
        TextView latLng;
        TextView datetime;
    }


    public UserAdapter(Context context, ArrayList<PostBean> postList, UserBean userBean, int rowLayout) {
        Timber.i(MyDebugTree.START_LOG);
        this.mContext = context;
        this.mPostList = postList;
        this.mUserBean = userBean;
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
            holder.post = convertView.findViewById(R.id.listCommentTextView);
            holder.photo = convertView.findViewById(R.id.photoImageView);
            holder.goodCount = convertView.findViewById(R.id.goodCountTextView);
            holder.latLng = convertView.findViewById(R.id.latLngTextView);
            holder.datetime = convertView.findViewById(R.id.dateTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageBitmap(mUserBean.getIcon());
        holder.username.setText(mUserBean.getName());
        holder.uid.setText(mUserBean.getUid());
        holder.post.setText(mPostList.get(position).getMessage());
        if (mPostList.get(position).getPhoto() != null && !mPostList.get(position).getPhoto().equals("")) {
            holder.photo.setImageBitmap(mPostList.get(position).getPhoto());
        }
        holder.goodCount.setText(Integer.toString(mPostList.get(position).getGoodCount()));
        holder.latLng.setText(String.format("%d, %d", (int) mPostList.get(position).getLatLng().latitude,
                (int) mPostList.get(position).getLatLng().longitude));
        holder.datetime.setText(mPostList.get(position).getStrDatetime());

        return convertView;
    }
}
