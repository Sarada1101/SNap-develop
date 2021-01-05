package com.example.snap_develop.view.view_holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.R;

public class FollowListViewHolder extends RecyclerView.ViewHolder {

    public final ImageView mIconImageView;
    public final TextView mUserNameTextView;
    public final TextView mUserIdTextView;
    public final ConstraintLayout mConstraintLayout;

    public FollowListViewHolder(@NonNull View itemView) {
        super(itemView);
        mIconImageView = itemView.findViewById(R.id.iconImageView);
        mUserNameTextView = itemView.findViewById(R.id.userNameTextView);
        mUserIdTextView = itemView.findViewById(R.id.userIdTextView);
        mConstraintLayout = itemView.findViewById(R.id.ConstraintLayout);
    }
}
