package com.bupt.run.runningapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bupt.run.R;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.bupt.run.runningapp.appserver.model.Point;
import com.bupt.run.runningapp.appserver.model.WriteRoute;
import com.bupt.run.runningapp.struct.RunningData;

public class HistoryDetailActivity extends AppCompatActivity {
    HistoryDetailActivity self = this;
    private RunningData runningData;
    private AMap map;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back_button:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        setTitle("跑步信息");
        this.runningData = new Gson().fromJson(getIntent().getStringExtra("runningdata"), RunningData.class);

        ((MapView) findViewById(R.id.detail_map)).onCreate(savedInstanceState);
        this.map = ((MapView) findViewById(R.id.detail_map)).getMap();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.setPoints(runningData.ployPoints);
        this.map.addPolyline(polylineOptions);
        MarkerOptions startMarker = new MarkerOptions().position(runningData.ployPoints.get(0));
        MarkerOptions endMarker = new MarkerOptions().position(runningData.ployPoints.get(runningData.ployPoints.size() - 1));
        this.map.addMarker(startMarker);
        this.map.addMarker(endMarker);
        this.map.moveCamera(CameraUpdateFactory.changeLatLng(runningData.centerPoint));
        this.map.moveCamera(CameraUpdateFactory.zoomTo(13));
        this.map.getUiSettings().setAllGesturesEnabled(false);
        this.map.getUiSettings().setZoomControlsEnabled(false);

        ((TextView) findViewById(R.id.length_text_view)).setText((new DecimalFormat("######0.00")).format(runningData.length * 1000) + "米");
        ((TextView) findViewById(R.id.time_text_view)).setText(timeFormat(runningData.endTime - runningData.startTime));
        ((TextView) findViewById(R.id.kalorie_txt_view)).setText((new DecimalFormat("######0.00")).format(runningData.length * 65) + "千卡");

        ((Button) findViewById(R.id.back_button)).setOnClickListener(onClickListener);
    }

    private String timeFormat(long mills) {
        int h, m, s;
        h = (int) (mills / 1000 / 3600 % 24);
        m = (int) (mills / 1000 / 60 % 60);
        s = (int) (mills / 1000 % 60);
        return "" + h + "时" + m + "分" + s + "秒";
    }
}
