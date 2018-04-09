package cn.bupt.runningapp.struct;

import com.amap.api.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yisic on 2017/5/3.
 */

public class RunningData implements Serializable {
    public ArrayList<LatLng> keyPoints;
    public ArrayList<LatLng> ployPoints;
    public LatLng centerPoint;
    public double length;
    public long startTime;
    public long endTime;
}
