package com.example.snap_develop.view.viewHolder;

import android.view.View;
import android.widget.Button;
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
    public Button mApprovalButton;
    public Button mRejectButton;
    public ConstraintLayout mConstraintLayout;

    public ApplicatedFollowListViewHolder(@NonNull View itemView) {
        super(itemView);
        mIconImageView = itemView.findViewById(R.id.iconImageView);
        mUserNameTextView = itemView.findViewById(R.id.userNameTextView);
        mUserIdTextView = itemView.findViewById(R.id.userIdTextView);
        mApprovalButton = itemView.findViewById(R.id.approvalButton);
        mRejectButton = itemView.findViewById(R.id.rejectButton);
        mConstraintLayout = itemView.findViewById(R.id.ConstraintLayout);
    }
}
