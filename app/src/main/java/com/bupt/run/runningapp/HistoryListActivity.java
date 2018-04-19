package com.bupt.run.runningapp;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amap.api.maps.model.LatLng;
import com.bupt.run.R;
import com.google.gson.Gson;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bupt.run.runningapp.alert.AlertMessage;
import com.bupt.run.runningapp.alert.AlertableAppCompatActivity;
import com.bupt.run.runningapp.appserver.asyn.AppServerAsynHandler;
import com.bupt.run.runningapp.appserver.asyn.RunnerAsyn;
import com.bupt.run.runningapp.appserver.model.Point;
import com.bupt.run.runningapp.appserver.model.ReadRoute;
import com.bupt.run.runningapp.struct.HistoryItem;
import com.bupt.run.runningapp.struct.RunningData;
import com.bupt.run.runningapp.uicomponent.HistoryItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryListActivity extends AlertableAppCompatActivity {
    private HistoryListActivity self = this;
    private ListView listview;
    public ArrayList<HistoryItem> items = new ArrayList<HistoryItem>();

    private OnTabSelectListener onTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelected(@IdRes int tabId) {
            if (tabId == R.id.tab_run) {
                startActivity(new Intent(self, StatisticActivity.class));
                finish();
                overridePendingTransition(0, 0);
            } else if (tabId == R.id.tab_person) {
                startActivity(new Intent(self, UserInfoActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

        ((BottomBar) findViewById(R.id.bottomBar)).setDefaultTab(R.id.tab_history);
        ((BottomBar) findViewById(R.id.bottomBar)).setOnTabSelectListener(onTabSelectListener);

        listview = (ListView) findViewById(R.id.history_list_view);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HistoryItem item = items.get(position);
                ArrayList<LatLng> points = new ArrayList<LatLng>();
                for (Point p : item.getReadRoute().getRoute()) {
                    points.add(new LatLng(p.getLatitude(), p.getLongitude()));
                }
                RunningData runningData = new RunningData();
                runningData.ployPoints = points;
                runningData.startTime = item.getReadRoute().getStart().longValue() * 1000;
                runningData.length = item.getReadRoute().getLength();
                runningData.endTime = item.getReadRoute().getEnd().longValue() * 1000;
                runningData.centerPoint = new LatLng(
                        (points.get(0).latitude + points.get(points.size() / 2).latitude) / 2,
                        (points.get(0).longitude + points.get(points.size() / 2).longitude) / 2
                );
                Intent intent = new Intent();
                intent.setClass(self, HistoryDetailActivity.class);
                intent.putExtra("runningdata", new Gson().toJson(runningData));
                startActivity(intent);
            }
        });

        updateHistoryData();
    }

    private void updateHistoryData() {

        Call<List<ReadRoute>> getRouteHistoryCall = RunnerAsyn.RunnerService.getRouteHistory();
        RunnerAsyn.AppServerAsyn(getRouteHistoryCall, self, new AppServerAsynHandler<List<ReadRoute>>() {
                    @Override
                    public void handler(Response<List<ReadRoute>> response) throws Exception {
                        List<ReadRoute> routeList = response.body();
                        for (ReadRoute readRoute : routeList) {
                            Date date = new Date(readRoute.getStart().longValue() * 1000);
                            if (readRoute.getLength() == null)
                                readRoute.setLength(0.0);
                            items.add(new HistoryItem(date, new java.text.DecimalFormat("######0.0").format(readRoute.getLength()) + "KM", readRoute));
                        }
                        HistoryItemAdapter adapter = new HistoryItemAdapter(HistoryListActivity.this, R.layout.layout_history_item_adapter, items);
                        listview.setAdapter(adapter);
                    }
                },
                new AlertMessage("系统错误", "请重启app"),
                new AlertMessage("网络错误", "请检查网络连接")
        );
    }
}
