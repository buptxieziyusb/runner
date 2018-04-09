package cn.bupt.runningapp.appserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yisic on 2017/5/7.
 */

public class ReadRoute {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("start")
    @Expose
    private Double start;
    @SerializedName("end")
    @Expose
    private Double end;
    @SerializedName("route")
    @Expose
    private List<Point> point = null;
    @SerializedName("length")
    @Expose
    private Double length;
    @SerializedName("energy_consume")
    @Expose
    private Double energyConsume;
    @SerializedName("speed")
    @Expose
    private Double speed;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("belong_to")
    @Expose
    private Long belongTo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getEnergyConsume() {
        return energyConsume;
    }

    public void setEnergyConsume(Double energyConsume) {
        this.energyConsume = energyConsume;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(Long belongTo) {
        this.belongTo = belongTo;
    }

}
