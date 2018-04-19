package com.bupt.run.runningapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupt.run.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import com.bupt.run.runningapp.alert.AlertMessage;
import com.bupt.run.runningapp.alert.AlertableAppCompatActivity;
import com.bupt.run.runningapp.appserver.asyn.AppServerAsynHandler;
import com.bupt.run.runningapp.appserver.asyn.RunnerAsyn;
import com.bupt.run.runningapp.appserver.model.PhysicalStatus;
import com.bupt.run.runningapp.appserver.model.UserDetail;
import com.bupt.run.runningapp.uicomponent.ChartMarkerView;
import com.bupt.run.runningapp.uicomponent.WeightInputDialog;
import retrofit2.Call;
import retrofit2.Response;

public class BodyDataActivity extends AlertableAppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {
    private BodyDataActivity self = this;
    //line chart data
    private LineChart mChart;
    private ImageView record_data_in_chart;
    private XAxis xAxis;
    private YAxis yAxis;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_savedata:
                    UserDetail userDetail = new UserDetail();
                    userDetail.setHeight(Long.parseLong(((TextView) findViewById(R.id.input_height)).getText().toString()));
                    Call<UserDetail> editMyInfoCall = RunnerAsyn.RunnerService.editMyInfo(userDetail);
                    RunnerAsyn.AppServerAsyn(editMyInfoCall, self, new AppServerAsynHandler<UserDetail>() {
                                @Override
                                public void handler(Response<UserDetail> response) throws Exception {
                                    response.body().setWeight(0L);
                                    finish();
                                }
                            },
                            new AlertMessage("系统错误", "请重启app"),
                            new AlertMessage("网络错误", "请检查网络连接")
                    );
                    finish();
                    break;
                case R.id.record_data_in_chart:
                    new WeightInputDialog(BodyDataActivity.this, self).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_data);

        ((Button) findViewById(R.id.btn_savedata)).setOnClickListener(onClickListener);

        mChart = (LineChart) findViewById(R.id.weight_chart);
        record_data_in_chart = (ImageView) findViewById(R.id.record_data_in_chart);
        record_data_in_chart.setOnClickListener(onClickListener);

        //line chart init
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(true);
        mChart.getAxisRight().setEnabled(false);


        xAxis = mChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setEnabled(false);
        yAxis = mChart.getAxisLeft();
        yAxis.setEnabled(true);
        yAxis.setDrawGridLines(false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        ChartMarkerView mv = new ChartMarkerView(this, R.layout.layout_body_data_mark_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);

        updateLineChart();
    }

    public void updateLineChart() {
        Call<List<PhysicalStatus>> getPhysicalStatusCall = RunnerAsyn.RunnerService.getPhysicalStatus();
        RunnerAsyn.AppServerAsyn(getPhysicalStatusCall, self, new AppServerAsynHandler<List<PhysicalStatus>>() {
                    @Override
                    public void handler(Response<List<PhysicalStatus>> response) throws Exception {
                        setLineChartData(response.body());
                    }
                },
                new AlertMessage("系统错误", "请重启app"),
                new AlertMessage("网络错误", "请检查网络连接")
        );
        Call<UserDetail> myInfoCall = RunnerAsyn.RunnerService.myInfo();
        RunnerAsyn.AppServerAsyn(myInfoCall, self, new AppServerAsynHandler<UserDetail>() {
                    @Override
                    public void handler(Response<UserDetail> response) throws Exception {
                        if (response.body().getHeight() == null)
                            ((TextView) findViewById(R.id.input_height)).setText("0");
                        else
                            ((TextView) findViewById(R.id.input_height)).setText("" + response.body().getHeight());
                    }
                },
                new AlertMessage("系统错误", "请重启app"),
                new AlertMessage("网络错误", "请检查网络连接")
        );
    }

    //Line Chart Listener
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    private void setLineChartData(final List<PhysicalStatus> physicalStatuses) {
        if (physicalStatuses.size() == 0)
            return;
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        final String[] quarters = new String[physicalStatuses.size() + 1];

        for (int i = 0; i < physicalStatuses.size(); i++) {
//            int j = physicalStatuses.size() - i - 1;
            PhysicalStatus physicalStatusNode = physicalStatuses.get(i);
            quarters[i] = physicalStatusNode.getDate().substring(5);
            yVals.add(new Entry(i, physicalStatusNode.getWeight()));
        }
        LineDataSet set1 = new LineDataSet(yVals, "");

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            public int getDecimalDigits() {
                return 0;
            }
        };
        xAxis.setValueFormatter(formatter);
        LineData data = new LineData(set1);
        mChart.setData(data);
        mChart.invalidate();
    }
}
