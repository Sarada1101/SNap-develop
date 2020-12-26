package com.example.snap_develop.view.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.R;

public class TimelineViewHolder extends RecyclerView.ViewHolder {

    public ImageView mIconImageView;
    public ImageView mPhotoImageView;
    public TextView mUserNameTextView;
    public TextView mUserIdTextView;
    public TextView mMessageTextView;
    public TextView mGoodCountTextView;
    public TextView mLatLngTextView;
    public TextView mDatetimeTextView;
    public ConstraintLayout mConstraintLayout;
    public ConstraintLayout mUserInfoConstraintLayout;

    public TimelineViewHolder(@NonNull View itemView) {
        super(itemView);
        mIconImageView = itemView.findViewById(R.id.iconImageView);
        mPhotoImageView = itemView.findViewById(R.id.photoImageView);
        mUserNameTextView = itemView.findViewById(R.id.userNameTextView);
        mUserIdTextView = itemView.findViewById(R.id.userIdTextView);
        mMessageTextView = itemView.findViewById(R.id.messageTextView);
        mGoodCountTextView = itemView.findViewById(R.id.goodCountTextView);
        mLatLngTextView = itemView.findViewById(R.id.latLngTextView);
        mDatetimeTextView = itemView.findViewById(R.id.datetimeTextView);
        mConstraintLayout = itemView.findViewById(R.id.ConstraintLayout);
        mUserInfoConstraintLayout = itemView.findViewById(R.id.userInfoConstraintLayout);
    }
}
