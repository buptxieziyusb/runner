//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.amap.api.navi.model;

import com.amap.api.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class AMapNaviPath {
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
    private List<AMapNaviLimitInfo> limitInfos;
    private List<AMapNaviForbiddenInfo> forbiddenInfos;
    private NaviLatLng center;
    private LatLngBounds bounds;

    public AMapNaviPath() {
    }

    public void setCameras(List<AMapNaviCameraInfo> var1) {
        this.cameras = var1;
    }

    public List<AMapNaviCameraInfo> getAllCameras() {
        return this.cameras;
    }

    public int[] getWayPointIndex() {
        return this.wayPointIndex;
    }

    public List<NaviLatLng> getWayPoint() {
        return this.wayPoi;
    }

    void setWayPoint(List<NaviLatLng> var1) {
        this.wayPoi = var1;
    }

    public NaviLatLng getStartPoint() {
        return this.startPoi;
    }

    void setStartPoint(NaviLatLng var1) {
        this.startPoi = var1;
    }

    public NaviLatLng getEndPoint() {
        return this.endPoi;
    }

    void setEndPoint(NaviLatLng var1) {
        this.endPoi = var1;
    }

    public NaviLatLng getCenterForPath() {
        return this.center;
    }

    void setCenter(NaviLatLng var1) {
        this.center = var1;
    }

    public LatLngBounds getBoundsForPath() {
        return this.bounds;
    }

    void setBounds(LatLngBounds var1) {
        this.bounds = var1;
    }

    public List<AMapNaviStep> getSteps() {
        return this.mSteps;
    }

    void setSteps(List<AMapNaviStep> var1) {
        this.mSteps = var1;
    }

    public List<NaviLatLng> getCoordList() {
        return this.list;
    }

    public void setList(List<NaviLatLng> var1) {
        this.list = var1;
    }

    AMapNaviStep getStep(int var1) {
        return null;
    }

    public int getAllLength() {
        return this.allLength;
    }

    void setAllLength(int var1) {
        this.allLength = var1;
    }

    /** @deprecated */
    public int getStrategy() {
        return this.strategy;
    }

    void setStrategy(int var1) {
        this.strategy = var1;
    }

    public int getAllTime() {
        return this.allTime;
    }

    void setAllTime(int var1) {
        this.allTime = var1;
    }

    public int getStepsCount() {
        return this.stepsCount;
    }

    void setStepsCount(int var1) {
        this.stepsCount = var1;
    }

    public int getTollCost() {
        return this.tollCost;
    }

    void setTollCost(int var1) {
        this.tollCost = var1;
    }

    public void setTrafficStatus(List<AMapTrafficStatus> var1) {
        this.trafficStatuses = var1;
    }

    public List<AMapTrafficStatus> getTrafficStatuses() {
        return this.trafficStatuses;
    }

    public void setLabels(String var1) {
        this.labels = var1;
    }

    public String getLabels() {
        return this.labels;
    }

    public void setRestrictionInfo(AMapRestrictionInfo var1) {
        this.restrictionInfo = var1;
    }

    public AMapRestrictionInfo getRestrictionInfo() {
        return this.restrictionInfo;
    }

    public void setCityAdcodeList(int[] var1) {
        this.cityAdcodeList = var1;
    }

    public int[] getCityAdcodeList() {
        return this.cityAdcodeList;
    }

    public NaviLatLng getCarToFootPoint() {
        return this.carToFootPoint;
    }

    public void setCarToFootPoint(NaviLatLng var1) {
        this.carToFootPoint = var1;
    }

    public void setDataVersion(int var1) {
        this.dataVersion = var1;
    }

    public int getDataVersion() {
        return this.dataVersion;
    }

    public void setLightList(List<NaviLatLng> var1) {
        this.lightList = var1;
    }

    public List<NaviLatLng> getLightList() {
        return this.lightList;
    }

    public List<AMapNaviLimitInfo> getLimitInfos() {
        return this.limitInfos;
    }

    public void setLimitInfos(List<AMapNaviLimitInfo> var1) {
        this.limitInfos = var1;
    }

    public List<AMapNaviForbiddenInfo> getForbiddenInfos() {
        return this.forbiddenInfos;
    }

    public void setForbiddenInfos(List<AMapNaviForbiddenInfo> var1) {
        this.forbiddenInfos = var1;
    }
}
