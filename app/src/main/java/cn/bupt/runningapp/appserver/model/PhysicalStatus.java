package cn.bupt.runningapp.appserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yisic on 2017/7/6.
 */

public class PhysicalStatus {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("weight")
    @Expose
    private Float weight;

    @SerializedName("date")
    @Expose
    private String date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getBelong_to() {
        return belong_to;
    }

    public void setBelong_to(Integer belong_to) {
        this.belong_to = belong_to;
    }

    @SerializedName("belong_to")
    @Expose
    private Integer belong_to;

}
