package com.bupt.run.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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
import com.bupt.run.enums.ReturnResults;
import com.bupt.run.util.TTSController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NaviActivity extends AppCompatActivity implements AMapNaviViewListener , AMapNaviListener, INaviInfoCallback {
    private AMapNaviView mAMapNaviView = null;
    private AMapNavi mAMapNavi;
    private List<NaviLatLng> passPoints = new ArrayList<NaviLatLng>();
    //private List<N>
    private List<NaviLatLng> pathPoints = new ArrayList<NaviLatLng>();
    private List<RouteOverLay> routeOverLays = new ArrayList<>();
    private NaviLatLng startPoint;
    private NaviLatLng endPoint;
    private TTSController ttsManager;
    private int calculatedPathIndex = 0;
    private boolean startNavigation = false;
    private NaviLatLng lastLocationInPath;


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    /*在位置偏离路线时需要重新算路。由于整个跑步路径是圈，因此不能简单地判断
    **点和线的最短距离。例如在起点时往反方向跑，事实上偏离了路线，但仍在整条
    * 路径上。设置一个值等分路径，根据@param calculatedPathIndex的值确定
    * 本次比较的路径是第几分段
     */
    private final int PATH_DEVIDER = 4;

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

        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        if(null != mLocationClient){
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }

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
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();

        ttsManager.pauseSpeaking();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        ttsManager.destroy();
        mAMapNavi.destroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
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
         mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex), passPoints.get(++calculatedPathIndex));
        //mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex++));
    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        /*偏离路线重新算路
        if (isTooFarFromPath(aMapNaviLocation.getCoord(), routeOverLays.get(calculatedPathIndex).getAMapNaviPath())) {
            mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex));
        } else {

        }
        */
        //检查当前是第几段路线
        RouteOverLay routeOverLay = routeOverLays.get(calculatedPathIndex);
        AMapNaviPath aMapNaviPath = routeOverLay.getAMapNaviPath();
        if (!neighborPoint(lastLocationInPath, aMapNaviLocation.getCoord())) {
            int removeIndex = findNearestPointIndex(aMapNaviLocation.getCoord(), aMapNaviPath);

            //如果在路线上则更新路线，重新绘制
            if (removeIndex != ReturnResults.COULD_NOT_FIND) {
                List<NaviLatLng> naviLatLngs = aMapNaviPath.getCoordList().subList(removeIndex, aMapNaviPath.getCoordList().size() - 1);
                routeOverLay.removeFromMap();
                Class aNaviPath = AMapNaviPath.class;
                try {
                    Method setList = aNaviPath.getMethod("setList", List.class);
                    setList.setAccessible(true);
                    setList.invoke(aMapNaviPath, naviLatLngs);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                lastLocationInPath = aMapNaviLocation.getCoord();
            } else {
                //TODO不在路线上重新算路，需要确定在超时多久后为已偏离路线，引导用户回正确路线或者重新算路
            }
        }

    }

    @Override
    public void onArriveDestination(boolean isEmulaterNavi) {
        if (calculatedPathIndex != passPoints.size() - 1) {
            mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex), passPoints.get(++calculatedPathIndex));
        }
    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {
        Log.i("NAVIGATION", s);
        Log.i("GPS:::::", mAMapNavi.isGpsReady() ? "true" : "false");
        mAMapNavi.startGPS();
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
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

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
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

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
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        //mAMapNavi.startNavi(NaviType.GPS);
        if (calculatedPathIndex != passPoints.size() && !startNavigation) {
            RouteOverLay routeOverLay = new RouteOverLay(mAMapNaviView.getMap(), mAMapNavi.getNaviPath(), this);
            if (calculatedPathIndex != 1) {
                routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.transparent));
                //routeOverLay.set
            }
            if (calculatedPathIndex != passPoints.size() - 1) {
                routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.transparent));
            }
            BitmapDescriptor[] descriptors = {
                    BitmapDescriptorFactory.fromResource(R.drawable.blue_road)
            };
            //mAMapNavi.getNaviPath().setList();
            try {
                routeOverLay.setWidth(30);
            } catch (AMapNaviException e) {
                e.printStackTrace();
            }
            routeOverLay.addToMap(descriptors, mAMapNavi.getNaviPath().getWayPointIndex());
            routeOverLays.add(routeOverLay);
            if (calculatedPathIndex == passPoints.size() - 1) {
                calculatedPathIndex = 0;
                startNavigation = true;
            }
            mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex), passPoints.get(++calculatedPathIndex));
            return;
        } else {
            mAMapNavi.startNavi(NaviType.GPS);
            /*
            if (mAMapNavi.isGpsReady()) {
                //mAMapNavi.
                mAMapNavi.startNavi(NaviType.GPS);
            } else {
                mAMapNavi.startNavi(NaviType.EMULATOR);
            }*/
        }
       // checkPointsInPath(mAMapNavi.getNaviPath());

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

    public int findNearestPointIndex(NaviLatLng point, AMapNaviPath path) {
        List<NaviLatLng> pathCoords = path.getCoordList();
        //暂时设定为依次判断所有点，无跳过
        for (int i = 0; i < pathCoords.size(); i++) {
            NaviLatLng naviLatLng = pathCoords.get(i);
            if (neighborPoint(naviLatLng, point)) {
                return i;
            }
        }
        return ReturnResults.COULD_NOT_FIND;
    }
}
