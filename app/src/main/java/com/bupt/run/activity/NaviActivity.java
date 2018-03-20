package com.bupt.run.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.navi.AMapNaviException;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.bupt.run.R;
import com.bupt.run.util.TTSController;

import java.util.ArrayList;
import java.util.List;

import static com.amap.api.navi.enums.PathPlanningStrategy.DRIVING_SINGLE_ROUTE_AVOID_HIGHSPEED_COST_CONGESTION;

public class NaviActivity extends AppCompatActivity implements AMapNaviViewListener , AMapNaviListener, INaviInfoCallback {
    private AMapNaviView mAMapNaviView = null;
    private AMapNavi mAMapNavi;
    private List<NaviLatLng> passPoints = new ArrayList<NaviLatLng>();
    //private List<N>
    private List<NaviLatLng> pathPoints = new ArrayList<NaviLatLng>();
    private NaviLatLng startPoint;
    private NaviLatLng endPoint;
    private TTSController ttsManager;
    private int calculatedPathIndex = 1;
    private boolean startNavigation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);

        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(this);

        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setLayoutVisible(true);
        options.setAutoDrawRoute(false);
        mAMapNaviView.setViewOptions(options);

        Intent intent = getIntent();
        for (int i = 0;; i++) {
            LatLonPoint latLonPoint = (LatLonPoint) intent.getParcelableExtra("point" + i);
            if (latLonPoint == null) {
                break;
            }
            NaviLatLng naviLatLng = latLonPointToNaviLatLng(latLonPoint);
            passPoints.add(naviLatLng);
        }
        ttsManager = TTSController.getInstance(this);

      //  options.setAutoDrawRoute(false);
        //mAMapNaviView.setViewOptions(options);
    }

    public NaviLatLng latLonPointToNaviLatLng(LatLonPoint point) {
        return new NaviLatLng(point.getLatitude(), point.getLongitude());
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {

    }

    @Override
    public boolean onNaviBackClick() {
        return true;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();

        ttsManager.pauseSpeaking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        ttsManager.destroy();
        mAMapNavi.destroy();
    }

    public void onBackPressed() {
        ttsManager.noMoreTalk();
        ttsManager.destroy();
        finish();
    }


    //以下是AMapNaviListener接口应实现的方法
    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        //mAMapNavi.calculateWalkRoute(passPoints.get(0), passPoints.get(passPoints.size() - 1));
        int lastIndex = passPoints.size();
        //int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
        //mAMapNavi.calculateDriveRoute(passPoints.subList(0, 1), passPoints.subList(lastIndex - 1, lastIndex), passPoints.subList(1, lastIndex - 1), DRIVING_SINGLE_ROUTE_AVOID_HIGHSPEED_COST_CONGESTION);

        mAMapNavi.calculateWalkRoute(passPoints.get(0), passPoints.get(1));
    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {
        Log.e("NAVIGATION", s);
        ttsManager.say(s);
    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {
        if (ttsManager != null) {
            ttsManager.noMoreTalk();
        }
    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        //mAMapNavi.startNavi(NaviType.GPS);
        pathPoints.addAll(mAMapNavi.getNaviPath().getCoordList());
        if (calculatedPathIndex != passPoints.size() - 1 && !startNavigation) {
            mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex), passPoints.get(++calculatedPathIndex));
            return;
        } else if (calculatedPathIndex == passPoints.size() - 1) {
            startNavigation = true;
            calculatedPathIndex = 0;
            MyNaviPath myNaviPath = MyNaviPath.fromAMapNaviPath(mAMapNavi.getNaviPath());
            myNaviPath.setList(pathPoints);
            RouteOverLay routeOverLay = new RouteOverLay(mAMapNaviView.getMap(), myNaviPath, this);
            BitmapDescriptor[] descriptors = {
                    BitmapDescriptorFactory.fromAsset("green_road.png")
            };
            try {
                routeOverLay.setWidth(50);
            } catch (AMapNaviException e) {
                e.printStackTrace();
            }
            int color[] = new int[10];
            color[0] = Color.BLACK;
            color[1] = Color.RED;
            color[2] = Color.BLUE;
            color[3] = Color.YELLOW;
            color[4] = Color.GRAY;
            routeOverLay.addToMap(color, myNaviPath.getWayPointIndex());
            //mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex), passPoints.get(++calculatedPathIndex));
        } else {
            mAMapNavi.startNavi(NaviType.EMULATOR);
        }

        checkPointsInPath(mAMapNavi.getNaviPath());

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    public List<NaviLatLng> checkPointsInPath(AMapNaviPath path, NaviLatLng... passPoints) {
        List<NaviLatLng> resultSet = new ArrayList<NaviLatLng>(passPoints.length);
        resultSet = path.getCoordList();
        for (NaviLatLng naviLatLng : passPoints) {

            //resultSet
        }
        return resultSet;
    }

    public boolean neighborPoint(NaviLatLng point1, NaviLatLng point2) {
        if (getDistance(point1.getLongitude(), point1.getLatitude(),
                point2.getLongitude(), point2.getLatitude()) <= 0.001) {
            return true;
        }
        return false;
    }

    public double getDistance(double point1x, double point1y, double point2x, double point2y) {
        return Math.sqrt(Math.pow(point1x - point2x, 2) + Math.pow(point1y - point2y, 2));
    }
}
