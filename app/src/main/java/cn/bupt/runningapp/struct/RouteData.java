package cn.bupt.runningapp.struct;

import com.amap.api.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

import cn.bupt.runningapp.appserver.model.Merchant;

/**
 * Created by yisic on 2017/5/3.
 */

public class RouteData implements Serializable {
    public ArrayList<LatLng> keyPoints = new ArrayList<LatLng>();
    public ArrayList<Boolean> isMerchant = new ArrayList<Boolean>();
    public ArrayList<Merchant> merchantInfo = new ArrayList<Merchant>();
    public ArrayList<LatLng> ployPoints = new ArrayList<LatLng>();
    public LatLng centerPoint;
    public LatLng passingPoint;
    public double length;
//    public WalkRouteResult result;
}
