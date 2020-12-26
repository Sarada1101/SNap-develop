package com.example.snap_develop.view.viewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snap_develop.R;

public class ApprovalPendingFollowListViewHolder extends RecyclerView.ViewHolder {

    public ImageView mIconImageView;
    public TextView mUserNameTextView;
    public TextView mUserIdTextView;
    public Button mRejectButton;
    public ConstraintLayout mConstraintLayout;

    public ApprovalPendingFollowListViewHolder(@NonNull View itemView) {
        super(itemView);
        mIconImageView = itemView.findViewById(R.id.iconImageView);
        mUserNameTextView = itemView.findViewById(R.id.userNameTextView);
        mUserIdTextView = itemView.findViewById(R.id.userIdTextView);
        mRejectButton = itemView.findViewById(R.id.rejectButton);
        mConstraintLayout = itemView.findViewById(R.id.ConstraintLayout);
    }
}
