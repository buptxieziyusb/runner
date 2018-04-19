package com.bupt.run.runningapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bupt.run.R;
import com.bupt.run.runningapp.entity.PositionEntity;


/**
 * Created by apple on 2018/3/21.
 */

public class SearchAdapter extends ArrayAdapter<PositionEntity> {

    private LayoutInflater mInflater;
    private int resourceId;
    private List<PositionEntity> mDatas;

    public SearchAdapter(Context context, int textViewResourceId, List<PositionEntity> mDatas){
        super(context, textViewResourceId, mDatas);
        mInflater = LayoutInflater.from(context);
        resourceId = textViewResourceId;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public PositionEntity getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView == null){
            convertView = mInflater.inflate(resourceId, parent, false);
            vh = new ViewHolder();
            vh.ico = (ImageView) convertView.findViewById(R.id.search_point_ico);
            vh.address = (TextView) convertView.findViewById(R.id.search_point_address);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.ico.setImageResource(R.drawable.ico_address);
        vh.address.setText(mDatas.get(position).address);
        return convertView;
    }

    class ViewHolder{
        ImageView ico;
        TextView address;
    }
}

