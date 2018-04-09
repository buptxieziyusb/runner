package cn.bupt.runningapp.appserver.model;

/**
 * Created by yisic on 2017/7/8.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GiftTicket {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("gift")
    @Expose
    private Gift gift;

    @SerializedName("datetime")
    @Expose
    private String datetime;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("used")
    @Expose
    private Boolean used;

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
