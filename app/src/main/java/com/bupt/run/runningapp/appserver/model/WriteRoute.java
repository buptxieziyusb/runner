package com.bupt.run.runningapp.appserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yisic on 2017/5/7.
 */

public class WriteRoute {
    @SerializedName("start")
    @Expose
    private Double start;
    @SerializedName("end")
    @Expose
    private Double end;
    @SerializedName("route")
    @Expose
    private List<Point> point = null;
    @SerializedName("note")
    @Expose
    private String note;


    public Double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start;
    }

    public Double getEnd() {
        return end;
    }

    public void setEnd(Double end) {
        this.end = end;
    }

    public List<Point> getRoute() {
        return point;
    }

    public void setRoute(List<Point> point) {
        this.point = point;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
