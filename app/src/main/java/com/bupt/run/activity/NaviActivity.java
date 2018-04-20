package com.bupt.run.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
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
import com.bupt.run.runningapp.runnerlib.LatLngCalculate;
import com.bupt.run.util.TTSController;
import com.bupt.run.util.ToastUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class NaviActivity extends AppCompatActivity implements AMapNaviViewListener , AMapNaviListener, INaviInfoCallback {
    private final double NEIBOUR_DISTANCE = 0.0001;
    private final int INVALID_INDEX = -1;
    private AMapNaviView mAMapNaviView = null;
    private AMapNavi mAMapNavi;
    private AMap aMap;
    private Button stopNavi;
    private List<NaviLatLng> passPoints = new ArrayList<NaviLatLng>();
    private List<NaviLatLng> pathPoints = new ArrayList<NaviLatLng>();
    private List<RouteOverLay> routeOverLays = new ArrayList<>();
    private TTSController ttsManager;
    private int calculatedPathIndex = 0;
    private boolean initialized = false;
    private boolean navigationStarted = false;
    private NaviLatLng lastLocationInPath;
    private long runStartAt;
    private Bitmap transparentBitmap;
    private LinkedList<NaviLatLng> historyLocations = new LinkedList<>();

    BitmapDescriptor[] descriptors = {
            BitmapDescriptorFactory.fromResource(R.drawable.blue_road)
    };


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
        transparentBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.transparent);
        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(this);

        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setLayoutVisible(true);
        options.setAutoDrawRoute(false);
        //options.setCarBitmap(transparentBitmap);
        mAMapNaviView.setViewOptions(options);
        aMap = mAMapNaviView.getMap();
        stopNavi = (Button) findViewById(R.id.end_navi_button);
        stopNavi.setOnClickListener(view -> {
            long runStopAt = System.currentTimeMillis();
            Intent intent = new Intent(this, EndRunActivity.class);
            intent.putExtra("run_during", runStopAt - runStartAt);
            super.startActivity(intent);
            this.finish();
        });

        Intent intent = getIntent();
        for (int i = 0;; i++) {
            LatLng latLng = (LatLng) intent.getParcelableExtra("point" + i);
            if (latLng == null) {
                break;
            }
            NaviLatLng naviLatLng = latLngToNaviLatLng(latLng);
            passPoints.add(naviLatLng);
        }
        //size过小，规划失败
        if (passPoints.size() < 2 || !passPoints.get(0).equals(passPoints.get(passPoints.size() - 1))) {
            ToastUtil.show(this, getString(R.string.calc_path_fail));
            finish();
            return;
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
            AMapLocation location = mLocationClient.getLastKnownLocation();
            if (location != null) {
                NaviLatLng myPoint = new NaviLatLng(location.getLatitude(), location.getLongitude());
                if (!neighborPoint(myPoint, passPoints.get(0))) {
                    //TODO
                }
            } else {
                ToastUtil.show(this, "打开定位权限");
            }

        }
        //  options.setAutoDrawRoute(false);
        //mAMapNaviView.setViewOptions(options);
    }

    public NaviLatLng latLngToNaviLatLng(LatLng point) {
        return new NaviLatLng(point.latitude, point.longitude);
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
         mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex), passPoints.get(calculatedPathIndex + 1));
    }

    @Override
    public void onStartNavi(int i) {
        runStartAt = System.currentTimeMillis();
    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        if (aMapNaviLocation != null) {
            LatLng latLng = new LatLng(aMapNaviLocation.getCoord().getLatitude(), aMapNaviLocation.getCoord().getLongitude());
            // 显示定位小图标，初始化时已经创建过了，这里修改位置即可
            aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
            NaviLatLng naviLatLng = latLngToNaviLatLng(latLng);
            /*
            int nearestPointIndex = findNearstPointInList(naviLatLng, passPoints);
            //第一个点和最后一个点相等，因此为了避免误判，将起点和终点重叠之后，取第1个点和倒数第2个点为起点重点的邻点
            int pre = nearestPointIndex == 0 ? passPoints.size() - 2 : nearestPointIndex - 1;
            int next = nearestPointIndex == passPoints.size() - 1 ? 1 : nearestPointIndex + 1;
            int whereAmI;
            if (getDistance(passPoints.get(pre), naviLatLng) < getDistance(passPoints.get(next), naviLatLng)) {
                whereAmI = pre;
            } else {
                whereAmI = nearestPointIndex;
            }
            if (calculatedPathIndex != whereAmI) {
                calculatedPathIndex = whereAmI;
                mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex + 1));
            }*/
            for (int i = 0; i < passPoints.size(); i++) {
                NaviLatLng passPoint = passPoints.get(i);
                if (getDistance(naviLatLng, passPoint) < 0.01) {
                    if (calculatedPathIndex != i) {
                        mAMapNavi.calculateWalkRoute(passPoints.get(i + 1));
                        calculatedPathIndex = i;
                    }
                }
            }

        }


        /*
        int size = historyLocations.size();
        int neighborCount = 0;
        int highOccursPointFounded = -1;
        NaviLatLng location = aMapNaviLocation.getCoord();
        if (size > 5) {//暂时设为5
            historyLocations.pollFirst();
        }
        for (int j = 0; j < size; j++) {
            NaviLatLng curPoint = historyLocations.get(j);
            if (curPoint != null && neighborPoint(location, curPoint)) {
                neighborCount++;
                if (highOccursPointFounded == -1) {
                    highOccursPointFounded = j;
                }
            }
            if (size / 2 <= neighborCount) {
                //检查当前是第几段路线
                double nearest = 100;
                int nearstDisIndex = -1;
                for (int i = routeOverLays.size() - 1; i >= 0; i--) {
                    List<NaviLatLng> coordList = routeOverLays.get(i).getAMapNaviPath().getCoordList();
                    NaviLatLng start = coordList.get(0);
                    NaviLatLng end = coordList.get(coordList.size() - 1);
                    double sDis = getDistance(start, aMapNaviLocation.getCoord());
                    double eDis = getDistance(end, aMapNaviLocation.getCoord());
                    if (Math.min(sDis, eDis) < nearest) {
                        nearest = Math.min(sDis, eDis);
                        nearstDisIndex = i;
                    }
                }
                calculatedPathIndex = Math.max(nearstDisIndex, calculatedPathIndex);
                //清理已经过的路段（定位可能突然跳到下一个路段上，此时需要删除已经过的路段）
                for (int k = 0; k < calculatedPathIndex; k++) {
                    routeOverLays.get(k).removeFromMap();
                }
                Log.i("tan", " 最近的路段index::" + calculatedPathIndex);

                break;
            }
        }
        historyLocations.addLast(location);
        mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex + 1));
        */
        /*
        if (marker != null) {
            marker.remove();
        }
        LatLng latLng = new LatLng(aMapNaviLocation.getCoord().getLatitude(),aMapNaviLocation.getCoord().getLongitude());
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.location)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        marker = mAMapNaviView.getMap().addMarker(markerOption);
        */
        /*偏离路线重新算路
        if (isTooFarFromPath(aMapNaviLocation.getCoord(), routeOverLays.get(calculatedPathIndex).getAMapNaviPath())) {
            mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex));
        } else {

        }
        */
        /*

        */

        //位置改变？若偏离路线则重新算路，否则只更新routeOverLay：不进行任何操作
       // RouteOverLay routeOverLay = routeOverLays.get(calculatedPathIndex);
        //AMapNaviPath aMapNaviPath = routeOverLay.getAMapNaviPath();
        //if (!neighborPoint(lastLocationInPath, aMapNaviLocation.getCoord()) //位置偏移的两种情况，和上一个点的偏移，或者和路径起点的偏移
          //      || !neighborPoint(aMapNaviLocation.getCoord(), aMapNaviPath.getCoordList().get(0))) {
            /*
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
                    routeOverLay.addToMap(descriptors, aMapNaviPath.getWayPointIndex());
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
                mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex + 1));
            }*/
            //mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex + 1));
      //  }

    }

    @Override
    public void onArriveDestination(boolean isEmulaterNavi) {/*
        if (calculatedPathIndex != passPoints.size() - 2) {
            mAMapNavi.calculateWalkRoute(passPoints.get(++calculatedPathIndex + 1));
        } else if (calculatedPathIndex == passPoints.size() - 1) {
            ToastUtil.show(this, "导航结束");
        }*/
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
        //mAMapNavi.startNavi(NaviType.GPS);
        if (calculatedPathIndex != passPoints.size() && !initialized) {
            /*RouteOverLay routeOverLay = new RouteOverLay(mAMapNaviView.getMap(), mAMapNavi.getNaviPath(), this);
            if (calculatedPathIndex != 1) {
                routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.transparent));
                //routeOverLay.set
            }
            if (calculatedPathIndex != passPoints.size() - 1) {
                routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.transparent));
            }

            try {
                routeOverLay.setWidth(30);
            } catch (AMapNaviException e) {
                e.printStackTrace();
            }
            routeOverLay.addToMap(descriptors, mAMapNavi.getNaviPath().getWayPointIndex());
            */

            RouteOverLay routeOverLay = drawRoute(mAMapNavi.getNaviPath(), 40, 0, passPoints.size() - 2);
            calculatedPathIndex++;
            routeOverLays.add(routeOverLay);
            if (calculatedPathIndex == passPoints.size() - 1) {
                calculatedPathIndex = 0;
                initialized = true;
                mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex + 1));
            } else {
                mAMapNavi.calculateWalkRoute(passPoints.get(calculatedPathIndex), passPoints.get(calculatedPathIndex + 1));
            }

            return;
        } else if (initialized && !navigationStarted) {
            mAMapNavi.startNavi(NaviType.GPS);
            navigationStarted = true;
        }
        if (navigationStarted) {
            RouteOverLay routeOverLay = routeOverLays.get(calculatedPathIndex);
            routeOverLay.removeFromMap();
            routeOverLay.destroy();
            routeOverLay = drawRoute(mAMapNavi.getNaviPath(), 40, 0, passPoints.size() - 2);
           // routeOverLay.addToMap(descriptors, mAMapNavi.getNaviPath().getWayPointIndex());
            routeOverLays.set(calculatedPathIndex, routeOverLay);
        }
        //routeOverLays.get(0).removeFromMap();
       // checkPointsInPath(mAMapNavi.getNaviPath());

    }

    private RouteOverLay drawRoute(AMapNaviPath path, int width, int startLogo, int endLogo) {
        RouteOverLay routeOverLay = new RouteOverLay(mAMapNaviView.getMap(), path, this);
        if (calculatedPathIndex != startLogo) {
            routeOverLay.setStartPointBitmap(transparentBitmap);
            Log.i("tanjie", calculatedPathIndex + "无起点");
            //routeOverLay.set
        }
        if (calculatedPathIndex != endLogo) {
            routeOverLay.setEndPointBitmap(transparentBitmap);
            Log.i("tanjie", calculatedPathIndex + "无终点");
        }

        try {
            routeOverLay.setWidth(width);
        } catch (AMapNaviException e) {
            e.printStackTrace();
        }
        routeOverLay.addToMap(descriptors, path.getWayPointIndex());
        return routeOverLay;
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

    private List<NaviLatLng> checkPointsInPath(AMapNaviPath path, NaviLatLng... passPoints) {
        List<NaviLatLng> resultSet = new ArrayList<NaviLatLng>(passPoints.length);
        resultSet = path.getCoordList();
        for (NaviLatLng naviLatLng : passPoints) {

            //resultSet
        }
        return resultSet;
    }

    private boolean neighborPoint(NaviLatLng point1, NaviLatLng point2) {
        if (getDistance(point1.getLongitude(), point1.getLatitude(),
                point2.getLongitude(), point2.getLatitude()) <= NEIBOUR_DISTANCE) {
            return true;
        }
        return false;
    }

    private double getDistance(double point1x, double point1y, double point2x, double point2y) {
        return Math.sqrt(Math.pow(point1x - point2x, 2) + Math.pow(point1y - point2y, 2));
    }

    private double getDistance(NaviLatLng point1, NaviLatLng point2) {
        return getDistance(point1.getLongitude(), point1.getLatitude(), point2.getLongitude(), point2.getLatitude());
    }

    private int findNearestPointIndex(NaviLatLng point, AMapNaviPath path) {
        return findNearstPointInList(point, path.getCoordList());
    }

    private int findNearstPointInList(NaviLatLng point, List<NaviLatLng> pathPoints) {
        int i;
        double min = -1;
        //暂时设定为依次判断所有点，无跳过
        for (i = 0; i < pathPoints.size(); i++) {
            NaviLatLng naviLatLng = pathPoints.get(i);
            double curDis = getDistance(naviLatLng, point);
            min = Math.min(min, curDis);
        }
        if (min != -1) {
            return i;
        }
        return ReturnResults.COULD_NOT_FIND;
    }
}
