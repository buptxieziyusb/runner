package com.bupt.run.runningapp.appserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yisic on 2017/7/8.
 */

public class LatlngPosition {
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    @SerializedName("latitude")
    @Expose
    private double latitude;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
