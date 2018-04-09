package cn.bupt.runningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cn.bupt.runningapp.alert.AlertMessage;
import cn.bupt.runningapp.alert.AlertableAppCompatActivity;
import cn.bupt.runningapp.appserver.asyn.AppServerAsynHandler;
import cn.bupt.runningapp.appserver.asyn.RunnerAsyn;
import cn.bupt.runningapp.appserver.model.GiftTicket;
import cn.bupt.runningapp.appserver.model.Merchant;
import cn.bupt.runningapp.runnerlib.LatLngCalculate;
import cn.bupt.runningapp.struct.RouteData;
import cn.bupt.runningapp.struct.RunningData;
import cn.bupt.runningapp.uicomponent.GiftAcceptDialog;
import cn.bupt.runningapp.uicomponent.MerchantInfoDialog;
import retrofit2.Call;
import retrofit2.Response;

public class RunningNaviActivity extends AlertableAppCompatActivity {
    private RunningNaviActivity self = this;
    private AMapLocationClientOption aMapLocationClientOption;
    private AMapLocationClient aMapLocationClient;
    private AMap naviMap;
    private AMapLocationListener aMapLocationListener;
    private RouteData routeData;
    private ArrayList<LatLng> pathPoints = new ArrayList<LatLng>();
    private long startTime;
    private long endTime;
    private double tempLength = 0.0;
    private static final double CLOSE_LIMINAL = 100.0;
    private int testCounter = 0;
    private int debugTriggerCounter = 0;
    private boolean isDebugMode = false;


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.debug_button:
                    if (testCounter < routeData.ployPoints.size()) {
                        AMapLocation testLocation = new AMapLocation("北京邮电大学");
                        testLocation.setLatitude(routeData.ployPoints.get(testCounter).latitude - 0.00001);
                        testLocation.setLongitude(routeData.ployPoints.get(testCounter).longitude - 0.00001);
                        aMapLocationListener.onLocationChanged(testLocation);
                        testCounter++;
                    }
                    break;
                case R.id.navi_finish_button:
                    //计算结果信息
                    RunningData runningData = new RunningData();
                    runningData.keyPoints = routeData.keyPoints;
                    runningData.ployPoints = pathPoints;
                    runningData.length = tempLength;
                    runningData.startTime = startTime;
                    runningData.endTime = System.currentTimeMillis();
                    runningData.centerPoint = routeData.centerPoint;
                    Intent intent = new Intent();
                    intent.setClass(self, RunningFinishActivity.class);
                    intent.putExtra("running_data", new Gson().toJson(runningData));
                    startActivity(intent);
                    finish();
                    break;
                case R.id.navi_length_text_view:
                    debugTriggerCounter++;
                    if (debugTriggerCounter > 10) {
                        alert(new AlertMessage("debug mode", ""));
                        aMapLocationClient.stopLocation();
                        isDebugMode = true;
                        ((Button) findViewById(R.id.debug_button)).setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_navi);
        this.setTitle("跑步中");
        routeData = new Gson().fromJson(getIntent().getStringExtra("route_data"), RouteData.class);
        ((MapView) findViewById(R.id.navi_map)).onCreate(savedInstanceState);
        naviMap = ((MapView) findViewById(R.id.navi_map)).getMap();
        this.startTime = System.currentTimeMillis();

        //绘制路线
        final PolylineOptions routePolylineOptions = new PolylineOptions();
        routePolylineOptions.transparency(0.6f);
        routePolylineOptions.setPoints(this.routeData.ployPoints);
        this.naviMap.addPolyline(routePolylineOptions);
        this.naviMap.moveCamera(CameraUpdateFactory.changeLatLng(routeData.centerPoint));
        this.naviMap.moveCamera(CameraUpdateFactory.zoomTo(13));

        //定位
        aMapLocationClientOption = new AMapLocationClientOption();
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        aMapLocationClientOption.setOnceLocation(true);//test
        aMapLocationClientOption.setOnceLocation(false);
        aMapLocationClientOption.setMockEnable(true);

        aMapLocationListener = new AMapLocationListener() {
            PolylineOptions polylineOptions = new PolylineOptions();
            boolean initFlag = false;

            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                for (int i = 0; i < routeData.merchantInfo.size(); i++) {
                    if (routeData.isMerchant.get(i)
                            && routeData.merchantInfo.get(i) != null
                            && LatLngCalculate.getDistance(aMapLocation.getLatitude(), aMapLocation.getLongitude(), routeData.merchantInfo.get(i).getPosition().getLatitude(), routeData.merchantInfo.get(i).getPosition().getLongitude()) < CLOSE_LIMINAL) {
                        routeData.isMerchant.set(i, false);
                        getGiftTicket(routeData.merchantInfo.get(i));
                    }
                }

                if (!initFlag) {
                    initFlag = true;
                    naviMap.addPolyline(polylineOptions);
                    polylineOptions.color(0xFFFF0000);
                }
                if (self.pathPoints.size() > 0) {
                    LatLng lastPoint = self.pathPoints.get(self.pathPoints.size() - 1);
                    if (!isDebugMode && LatLngCalculate.getDistance(lastPoint.latitude, lastPoint.longitude, aMapLocation.getLatitude(), aMapLocation.getLongitude()) < 50) {
                        return;
                    }
                }
                self.pathPoints.add(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                polylineOptions.add(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                naviMap.clear();
                naviMap.addPolyline(polylineOptions);
                naviMap.addPolyline(routePolylineOptions);
                naviMap.addMarker(new MarkerOptions().position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                naviMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                naviMap.moveCamera(CameraUpdateFactory.zoomTo(159));
                tempLength = LatLngCalculate.getPathLength(pathPoints);
                ((TextView) findViewById(R.id.navi_length_text_view)).setText("里程：" + (int) tempLength + "米");
            }
        };
        aMapLocationClient = new AMapLocationClient(this);
        aMapLocationClient.setLocationListener(aMapLocationListener);
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        aMapLocationClient.startLocation();

        ((Button) findViewById(R.id.debug_button)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.navi_finish_button)).setOnClickListener(onClickListener);
        ((TextView) findViewById(R.id.navi_length_text_view)).setOnClickListener(onClickListener);


        ((Chronometer) findViewById(R.id.chronometer)).setFormat("时间：%s");
        ((Chronometer) findViewById(R.id.chronometer)).start();
    }

    private void getGiftTicket(Merchant merchant) {
        RunnerAsyn.AppServerAsyn(RunnerAsyn.RunnerService.giftTicketRequire(merchant), self,
                new AppServerAsynHandler<GiftTicket>() {
                    @Override
                    public void handler(Response<GiftTicket> response) throws Exception {
                        try {
                            new GiftAcceptDialog(self, response.body()).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw e;
                        }
                    }
                },
                new AlertMessage("系统错误", ""),
                new AlertMessage("网络错误", "请检查网络连接")
        );
    }
}
