package cn.bupt.runningapp.appserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yisic on 2017/5/7.
 */


public class UserDetail {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("total_length")
    @Expose
    private Double totalLength;

    @SerializedName("total_time")
    @Expose
    private Long totalTime;

    @SerializedName("total_day")
    @Expose
    private Integer totalDay;

    @SerializedName("avg_speed")
    @Expose
    private Double avgSpeed;

    @SerializedName("age")
    @Expose
    private Long age;

    @SerializedName("avatar")
    @Expose
    private Object avatar;

    @SerializedName("height")
    @Expose
    private Long height;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(Double totalLength) {
        this.totalLength = totalLength;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getTotalDay() {
        return totalDay;
    }

    public void setTotalDay(Integer totalDay) {
        this.totalDay = totalDay;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Object getAvatar() {
        return avatar;
    }

    public void setAvatar(Object avatar) {
        this.avatar = avatar;
    }

    public Long getHeight() {
        if (height == null)
            return (long) 0;
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public double getTotal_energy_consume() {
        return total_energy_consume;
    }

    public void setTotal_energy_consume(double total_energy_consume) {
        this.total_energy_consume = total_energy_consume;
    }

    @SerializedName("weight")
    @Expose

    private Long weight;

    @SerializedName("gender")
    @Expose
    private Integer gender;

    @SerializedName("total_energy_consume")
    @Expose
    private double total_energy_consume;


}
