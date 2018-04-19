package com.bupt.run.runningapp.uicomponent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.bupt.run.R;
import com.bupt.run.runningapp.struct.HistoryItem;

/**
 * Created by yisic on 2017/7/9.
 */

public class HistoryItemAdapter extends ArrayAdapter<HistoryItem> {
    private int resourceId;

    /**
     * context:当前活动上下文
     * textViewResourceId:ListView子项布局的ID
     * objects：要适配的数据
     */
    public HistoryItemAdapter(Context context, int textViewResourceId, List<HistoryItem> objects) {
        super(context, textViewResourceId, objects);
        //拿取到子项布局ID
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryItem item = getItem(position);
        //为子项动态加载布局
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView startDateText = (TextView) view.findViewById(R.id.StartDateText);
        TextView startTimeText = (TextView) view.findViewById(R.id.StartTimeText);
        TextView routeLengthText = (TextView) view.findViewById(R.id.RouteLengthText);
//        StartTimeText.setText(item.getStartTimeText());
        startDateText.setText(new SimpleDateFormat("yyyy年MM月dd日").format(item.getStartTime()));
        startTimeText.setText(new SimpleDateFormat("HH : mm").format(item.getStartTime()));
        routeLengthText.setText(item.getRouteLengthText());
        return view;
    }
}
