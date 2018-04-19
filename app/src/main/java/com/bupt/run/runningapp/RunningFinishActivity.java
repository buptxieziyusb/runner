package com.bupt.run.runningapp;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.bupt.run.runningapp.alert.AlertMessage;
import com.bupt.run.runningapp.alert.AlertableAppCompatActivity;
import com.bupt.run.runningapp.appserver.asyn.AppServerAsynHandler;
import com.bupt.run.runningapp.appserver.asyn.RunnerAsyn;
import com.bupt.run.runningapp.appserver.model.Point;
import com.bupt.run.runningapp.appserver.model.ReadRoute;
import com.bupt.run.runningapp.appserver.model.WriteRoute;
import com.bupt.run.runningapp.struct.RunningData;
import com.bupt.run.runningapp.struct.ServerSessionInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RunningFinishActivity extends AlertableAppCompatActivity implements WbShareCallback {
    private RunningFinishActivity self = this;
    private RunningData runningData;
    private AMap map;
    private WbShareHandler wbShareHandler;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back_button:
                    finish();
                    break;
                case R.id.share_button:
                    map.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                        @Override
                        public void onMapScreenShot(Bitmap bitmap) {
                            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                            weiboMessage.imageObject = new ImageObject();
                            weiboMessage.imageObject.setImageObject(bitmap);
                            weiboMessage.textObject = new TextObject();
                            weiboMessage.textObject.text = "我刚刚在Runner完成了一次长为" + runningData.length + "米的跑步,快来看看吧";
                            wbShareHandler.shareMessage(weiboMessage, false);
                        }

                        @Override
                        public void onMapScreenShot(Bitmap bitmap, int i) {
                            onMapScreenShot(bitmap);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_finish);

        setTitle("跑步信息");
        this.runningData = new Gson().fromJson(getIntent().getStringExtra("running_data"), RunningData.class);

        ((MapView) findViewById(R.id.finish_map)).onCreate(savedInstanceState);
        this.map = ((MapView) findViewById(R.id.finish_map)).getMap();
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

        ((TextView) findViewById(R.id.length_text_view)).setText("" + runningData.length + "米");
        ((TextView) findViewById(R.id.time_text_view)).setText(timeFormat(runningData.endTime - runningData.startTime));
        ((TextView) findViewById(R.id.kalorie_txt_view)).setText((new DecimalFormat("######0.00")).format(runningData.length / 1000 * 65) + "千卡");

        ((Button) findViewById(R.id.back_button)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.share_button)).setOnClickListener(onClickListener);

        //upload data
        WriteRoute writeRoute = new WriteRoute();
        final ArrayList<Point> uploadPoints = new ArrayList<Point>();
        for (LatLng latLng : runningData.ployPoints) {
            Point point = new Point();
            point.setLatitude(latLng.latitude);
            point.setLongitude(latLng.longitude);
            uploadPoints.add(point);
        }
        writeRoute.setStart((double) runningData.startTime / 1000);
        writeRoute.setEnd((double) runningData.endTime / 1000);
        writeRoute.setRoute(uploadPoints);
        writeRoute.setNote("");
        uploadRouteData(writeRoute);

        //weibo share
        wbShareHandler = new WbShareHandler(this);
        wbShareHandler.registerApp();
    }

    private void uploadRouteData(WriteRoute writeRoute) {
        Call<ReadRoute> postFinishedRouteCall = RunnerAsyn.RunnerService.postFinishedRoute(writeRoute);
        RunnerAsyn.AppServerAsyn(postFinishedRouteCall, self, new AppServerAsynHandler<ReadRoute>() {
                    @Override
                    public void handler(Response<ReadRoute> response) throws Exception {
                        response.body().setNote("");
                    }
                },
                new AlertMessage("系统错误", "请重启app"),
                new AlertMessage("网络错误", "请检查网络连接")
        );
    }

    private String timeFormat(long mills) {
        int h, m, s;
        h = (int) (mills / 1000 / 3600 % 24);
        m = (int) (mills / 1000 / 60 % 60);
        s = (int) (mills / 1000 % 60);
        return "" + h + "时" + m + "分" + s + "秒";
    }

    //weibo share callback
    @Override
    public void onWbShareSuccess() {
        ((Button) findViewById(R.id.share_button)).setText("已分享");
        ((Button) findViewById(R.id.share_button)).setEnabled(false);
    }

    @Override
    public void onWbShareCancel() {
        //ignore
    }

    @Override
    public void onWbShareFail() {
        //ignore
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        wbShareHandler.doResultIntent(intent, this);
    }
}
