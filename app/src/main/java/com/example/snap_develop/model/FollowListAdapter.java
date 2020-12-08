package com.example.snap_develop.model;

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

import com.example.snap_develop.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowListAdapter extends BaseAdapter {

    public static final String TEXT = "TEXT";
    public static final String IMAGE = "IMAGE";
    private LayoutInflater inflater;
    private int layoutID;
    ArrayList<HashMap<String, HashMap<String, Object>>> dataList;
    HashMap<String, Integer> textViewData;
    HashMap<String, Integer> imageViewData;
    List<String> textKeyList;
    List<String> imageKeyList;

    public FollowListAdapter(Context context, ArrayList<HashMap<String, HashMap<String, Object>>> dataList, int rowLayout,
            Map<String, HashMap<String, Integer>> viewData, HashMap<String, List<String>> keyData) {
        this.inflater = LayoutInflater.from(context);
        this.layoutID = rowLayout;
        this.dataList = dataList;
        this.textViewData = viewData.get(TEXT);
        this.imageViewData = viewData.get(IMAGE);
        this.textKeyList = keyData.get(TEXT);
        this.imageKeyList = keyData.get(IMAGE);
    }

    @Override
    public int getCount() {
        return this.dataList.size();
    }

    @Override
    public HashMap<String, HashMap<String, Object>> getItem(int position) {
        return this.dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layoutID, null);
        }

        HashMap<String, HashMap<String, Object>> nextData = getItem(position);
        HashMap<String, Object> nextTextData = nextData.get(TEXT);
        HashMap<String, Object> nextImageData = nextData.get(IMAGE);

        TextView setTextView = null;
        for (String key : textKeyList) {
            if (key != "danger") {
                setTextView = convertView.findViewById(textViewData.get(key));
            }

            if (key == "anoymous") {
                if (!(Boolean) nextTextData.get("anoymous")) {
                    setTextView.setVisibility(View.INVISIBLE);
                    userInfo.setVisibility(View.INVISIBLE);
                    ConstraintLayout userInfo = convertView.findViewById(R.id.userInfo);
                    userInfo.setVisibility(View.VISIBLE);
                } else {
                    setTextView.setVisibility(View.VISIBLE);
                    ConstraintLayout userInfo = convertView.findViewById(R.id.userInfo);
                    userInfo.setVisibility(View.INVISIBLE);
                }
            } else if (key == "danger") {
                if ((Boolean) nextTextData.get("danger")) {
                    ConstraintLayout timeLinePost = convertView.findViewById(textViewData.get("danger"));
                    timeLinePost.setBackgroundColor(Color.rgb(255, 100, 100));
                }
            } else {
                setTextView.setText((String) nextTextData.get(key));
            }
        }

        for (String key : imageKeyList) {
            ImageView setImageView = convertView.findViewById(imageViewData.get(key));
            setImageView.setImageBitmap((Bitmap) nextImageData.get(key));
            if (key == "postPicture") {
                if (nextImageData.get(key) == null) {
                    setImageView.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }
}