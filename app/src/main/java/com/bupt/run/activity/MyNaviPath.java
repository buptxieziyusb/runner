package com.bupt.run.activity;

import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.AMapRestrictionInfo;
import com.amap.api.navi.model.AMapTrafficStatus;
import com.amap.api.navi.model.NaviLatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanjie on 2018/3/20.
 */

public class MyNaviPath extends AMapNaviPath {
    public int[] wayPointIndex = null;
    private int allLength;
    private int strategy;
    private int allTime;
    private int stepsCount;
    private String labels;
    private List<AMapTrafficStatus> trafficStatuses = new ArrayList();
    private List<AMapNaviStep> mSteps;
    private List<NaviLatLng> list;
    private NaviLatLng startPoi;
    private NaviLatLng endPoi;
    private List<NaviLatLng> wayPoi;
    private int tollCost = 0;
    private List<AMapNaviCameraInfo> cameras;
    private AMapRestrictionInfo restrictionInfo;
    private int[] cityAdcodeList;
    private NaviLatLng carToFootPoint;
    private int dataVersion;
    private List<NaviLatLng> lightList;
    private NaviLatLng center;
    private LatLngBounds bounds;

    public List<NaviLatLng> getList() {
        return list;
    }

    void setList(List<NaviLatLng> list) {
        this.list = list;
    }

    public static MyNaviPath fromAMapNaviPath(AMapNaviPath path) {
        MyNaviPath myPath = new MyNaviPath();
        myPath.wayPointIndex = path.getWayPointIndex();
        myPath.wayPoi = path.getWayPoint();
        myPath.allLength = path.getAllLength();
        myPath.strategy = path.getStrategy();
        myPath.allTime = path.getAllTime();
        myPath.stepsCount = path.getStepsCount();
        myPath.labels = path.getLabels();
        myPath.trafficStatuses = path.getTrafficStatuses();
        myPath.mSteps = path.getSteps();
        myPath.startPoi = path.getStartPoint();
        myPath.endPoi = path.getEndPoint();
        myPath.tollCost = path.getTollCost();
        myPath.cameras = path.getAllCameras();
        myPath.restrictionInfo = path.getRestrictionInfo();
        myPath.cityAdcodeList = path.getCityAdcodeList();
        myPath.carToFootPoint = path.getCarToFootPoint();
        myPath.dataVersion = path.getDataVersion();
        myPath.lightList = path.getLightList();
        myPath.center = path.getCenterForPath();
        myPath.bounds = path.getBoundsForPath();
        return myPath;
    }
}
