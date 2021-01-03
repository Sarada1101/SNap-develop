package com.example.snap_develop.view.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.R;

public class ApplicatedFollowListViewHolder extends RecyclerView.ViewHolder {

    public ImageView mIconImageView;
    public TextView mUserNameTextView;
    public TextView mUserIdTextView;
    public ConstraintLayout mConstraintLayout;

    public ApplicatedFollowListViewHolder(@NonNull View itemView) {
        super(itemView);
        mIconImageView = itemView.findViewById(R.id.iconImageView);
        mUserNameTextView = itemView.findViewById(R.id.userNameTextView);
        mUserIdTextView = itemView.findViewById(R.id.userIdTextView);
        mConstraintLayout = itemView.findViewById(R.id.ConstraintLayout);
    }
}
