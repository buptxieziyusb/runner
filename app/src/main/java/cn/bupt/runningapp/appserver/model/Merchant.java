package cn.bupt.runningapp.appserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yisic on 2017/7/8.
 */

public class Merchant {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("gifts")
    @Expose
    private List<Gift> gifts;

    @SerializedName("position")
    @Expose
    private LatlngPosition position;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("pic")
    @Expose
    private Object pic;

    @SerializedName("desc")
    @Expose
    private String description;

    @SerializedName("checked")
    @Expose
    private boolean checked;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Gift> getGifts() {
        return gifts;
    }

    public void setGifts(List<Gift> gifts) {
        this.gifts = gifts;
    }

    public LatlngPosition getPosition() {
        return position;
    }

    public void setPosition(LatlngPosition position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getPic() {
        return pic;
    }

    public void setPic(Object pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
