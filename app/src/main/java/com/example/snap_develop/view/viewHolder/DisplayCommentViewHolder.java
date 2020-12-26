package com.example.snap_develop.view.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.R;

public class DisplayCommentViewHolder extends RecyclerView.ViewHolder {

    public ImageView mIconImageView;
    public TextView mUserNameTextView;
    public TextView mUserIdTextView;
    public TextView mMessageTextView;
    public TextView mDatetimeTextView;
    public ConstraintLayout mConstraintLayout;

    public DisplayCommentViewHolder(@NonNull View itemView) {
        super(itemView);
        mIconImageView = itemView.findViewById(R.id.iconImageView);
        mUserNameTextView = itemView.findViewById(R.id.userNameTextView);
        mUserIdTextView = itemView.findViewById(R.id.userIdTextView);
        mMessageTextView = itemView.findViewById(R.id.messageTextView);
        mDatetimeTextView = itemView.findViewById(R.id.datetimeTextView);
        mConstraintLayout = itemView.findViewById(R.id.ConstraintLayout);
    }
}
