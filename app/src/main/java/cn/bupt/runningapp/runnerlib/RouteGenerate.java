package cn.bupt.runningapp.runnerlib;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngCreator;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.squareup.okhttp.Route;

import java.util.ArrayList;
import java.util.List;

import cn.bupt.runningapp.R;
import cn.bupt.runningapp.RouteSelectActivity;
import cn.bupt.runningapp.alert.AlertMessage;
import cn.bupt.runningapp.alert.AlertableAppCompatActivity;
import cn.bupt.runningapp.appserver.asyn.AppServerAsynHandler;
import cn.bupt.runningapp.appserver.asyn.RunnerAsyn;
import cn.bupt.runningapp.appserver.model.Merchant;
import cn.bupt.runningapp.struct.RouteData;
import cn.bupt.runningapp.uicomponent.RouteSchematicDiagramLayout;
import cn.bupt.runningapp.tools.MapScale;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by yisic on 2017/7/9.
 */

public class RouteGenerate {
    public static final int NORTH = 0;
    public static final int NORTHEAST = 1;
    public static final int EAST = 2;
    public static final int SOUTHEAST = 3;
    public static final int SOUTH = 4;
    public static final int SOUTHWEST = 5;
    public static final int WEST = 6;
    public static final int NORTHWEST = 7;

    private static RouteData getCircleKeyPoints(LatLng startPoint, double distance, int orient) {
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        //平衡路线规划增加的路径长度
        if (distance < 5000) {
            distance *= 0.6;
        } else if (distance < 7000) {
            distance *= 0.7;
        } else {
            distance *= 0.8;
        }

        //n,w,s,e
        LatLng n, s, w, e, nw, ne, sw, se, c;
        double tempLat, tempLng, tempDis;
        switch (orient) {
            case NORTH:
                s = new LatLng(startPoint.latitude, startPoint.longitude);
                tempLat = s.latitude;
                tempLng = s.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(s.latitude, s.longitude, tempLat, tempLng);
                    if (tempDis > distance / Math.PI)
                        break;
                    else
                        tempLat += 0.000001;
                }
                n = new LatLng(tempLat, tempLng);
                c = new LatLng((s.latitude + n.latitude) / 2, (s.longitude + n.longitude) / 2);
                tempLat = c.latitude;
                tempLng = c.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
                    if (tempDis > distance / (Math.PI * 2))
                        break;
                    else
                        tempLng -= 0.000001;
                }
                w = new LatLng(tempLat, tempLng);
                tempLat = c.latitude;
                tempLng = c.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
                    if (tempDis > distance / (Math.PI * 2))
                        break;
                    else
                        tempLng += 0.000001;
                }
                e = new LatLng(tempLat, tempLng);
                break;
            case SOUTH:
                n = new LatLng(startPoint.latitude, startPoint.longitude);
                tempLat = n.latitude;
                tempLng = n.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(n.latitude, n.longitude, tempLat, tempLng);
                    if (tempDis > distance / Math.PI)
                        break;
                    else
                        tempLat -= 0.000001;
                }
                s = new LatLng(tempLat, tempLng);
                c = new LatLng((s.latitude + n.latitude) / 2, (s.longitude + n.longitude) / 2);
                tempLat = c.latitude;
                tempLng = c.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
                    if (tempDis > distance / (Math.PI * 2))
                        break;
                    else
                        tempLng -= 0.000001;
                }
                w = new LatLng(tempLat, tempLng);
                tempLat = c.latitude;
                tempLng = c.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
                    if (tempDis > distance / (Math.PI * 2))
                        break;
                    else
                        tempLng += 0.000001;
                }
                e = new LatLng(tempLat, tempLng);
                break;
            case WEST:
                e = new LatLng(startPoint.latitude, startPoint.longitude);
                tempLat = e.latitude;
                tempLng = e.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(e.latitude, e.longitude, tempLat, tempLng);
                    if (tempDis > distance / Math.PI)
                        break;
                    else
                        tempLng -= 0.000001;
                }
                w = new LatLng(tempLat, tempLng);
                c = new LatLng((e.latitude + w.latitude) / 2, (e.longitude + w.longitude) / 2);
                tempLat = c.latitude;
                tempLng = c.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
                    if (tempDis > distance / (Math.PI * 2))
                        break;
                    else
                        tempLat += 0.000001;
                }
                n = new LatLng(tempLat, tempLng);
                tempLat = c.latitude;
                tempLng = c.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
                    if (tempDis > distance / (Math.PI * 2))
                        break;
                    else
                        tempLat -= 0.000001;
                }
                s = new LatLng(tempLat, tempLng);
                break;
            case EAST:
                w = new LatLng(startPoint.latitude, startPoint.longitude);
                tempLat = w.latitude;
                tempLng = w.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(w.latitude, w.longitude, tempLat, tempLng);
                    if (tempDis > distance / Math.PI)
                        break;
                    else
                        tempLng += 0.000001;
                }
                e = new LatLng(tempLat, tempLng);
                c = new LatLng((e.latitude + w.latitude) / 2, (e.longitude + w.longitude) / 2);
                tempLat = c.latitude;
                tempLng = c.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
                    if (tempDis > distance / (Math.PI * 2))
                        break;
                    else
                        tempLat += 0.000001;
                }
                n = new LatLng(tempLat, tempLng);
                tempLat = c.latitude;
                tempLng = c.longitude;
                while (true) {
                    tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
                    if (tempDis > distance / (Math.PI * 2))
                        break;
                    else
                        tempLat -= 0.000001;
                }
                s = new LatLng(tempLat, tempLng);
                break;
            default:
                return null;
        }

        //nw,ne,sw,se
        tempLat = c.latitude;
        tempLng = c.longitude;
        while (true) {
            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
            if (tempDis > distance / (Math.PI * 2))
                break;
            else {
                tempLat += 0.000001;
                tempLng -= 0.000001;
            }
        }
        nw = new LatLng(tempLat, tempLng);
        tempLat = c.latitude;
        tempLng = c.longitude;
        while (true) {
            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
            if (tempDis > distance / (Math.PI * 2))
                break;
            else {
                tempLat += 0.000001;
                tempLng += 0.000001;
            }
        }
        ne = new LatLng(tempLat, tempLng);
        tempLat = c.latitude;
        tempLng = c.longitude;
        while (true) {
            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
            if (tempDis > distance / (Math.PI * 2))
                break;
            else {
                tempLat -= 0.000001;
                tempLng -= 0.000001;
            }
        }
        sw = new LatLng(tempLat, tempLng);
        tempLat = c.latitude;
        tempLng = c.longitude;
        while (true) {
            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude, tempLat, tempLng);
            if (tempDis > distance / (Math.PI * 2))
                break;
            else {
                tempLat -= 0.000001;
                tempLng += 0.000001;
            }
        }
        se = new LatLng(tempLat, tempLng);
        switch (orient) {
            case NORTH:
                points.add(s);
                points.add(sw);
                points.add(w);
                points.add(nw);
                points.add(n);
                points.add(ne);
                points.add(e);
                points.add(se);
                points.add(s);
                break;
            case SOUTH:
                points.add(n);
                points.add(ne);
                points.add(e);
                points.add(se);
                points.add(s);
                points.add(sw);
                points.add(w);
                points.add(nw);
                points.add(n);
                break;
            case WEST:
                points.add(e);
                points.add(se);
                points.add(s);
                points.add(sw);
                points.add(w);
                points.add(nw);
                points.add(n);
                points.add(ne);
                points.add(e);
                break;
            case EAST:
                points.add(w);
                points.add(nw);
                points.add(n);
                points.add(ne);
                points.add(e);
                points.add(se);
                points.add(s);
                points.add(sw);
                points.add(w);
                break;
            default:
                return null;
        }

        RouteData routeData = new RouteData();
        routeData.centerPoint = c;
        routeData.keyPoints = points;
        return routeData;
    }

    private static RouteData getCircleKeyPointsWithPassingPoint(LatLng startPoint,
                                                                LatLng passingPoint,
                                                                double distance,
                                                                int orient){
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        //平衡路线规划增加的路径长度
        if (distance < 5000) {
            distance *= 0.6;
        } else if (distance < 7000) {
            distance *= 0.7;
        } else {
            distance *= 0.8;
        }

        //n,w,s,e
        LatLng n, s, w, e, nw, ne, sw, se, c;
        double tempLat, tempLng, tempDis, ac, bc;
//        double seta, halfChord, chordDistance;
//        double k = 0.0;
//        double centerX1, centerX2, centerY1, centerY2;
        double comLat, comLng, latLength, lngLength;
        boolean horizontal = false;
        comLng = passingPoint.longitude;
        comLat = startPoint.latitude;
        lngLength = LatLngCalculate.getDistance(comLat, comLng, passingPoint.latitude,
                passingPoint.longitude);
        latLength = LatLngCalculate.getDistance(comLat, comLng, startPoint.latitude,
                startPoint.longitude);

        if(latLength > lngLength){
            horizontal = true;
        }
        tempLat = startPoint.latitude;
        tempLng = startPoint.longitude;
        if(horizontal){
            while(true){
                ac = LatLngCalculate.getDistance(tempLat, tempLng, startPoint.latitude,
                        startPoint.longitude);
                bc = LatLngCalculate.getDistance(tempLat, tempLng, passingPoint.latitude,
                        passingPoint.longitude);
                if(Math.abs(ac - bc) <= 0){
                    break;
                }else{
                    if(startPoint.longitude < passingPoint.longitude)
                        tempLng += 0.000001;
                    else
                        tempLng -= 0.000001;
                }

            }
            switch (orient){
                case NORTH:
                    while (true){
                        tempDis = LatLngCalculate.getDistance(tempLat, tempLng, startPoint.latitude,
                                 startPoint.longitude);
                        if(tempDis > distance / (Math.PI * 2))
                            break;
                        else
                            tempLat += 0.000001;
                    }
                    break;
                case SOUTH:
                    while (true){
                        tempDis = LatLngCalculate.getDistance(tempLat, tempLng, startPoint.latitude,
                                startPoint.longitude);
                        if(tempDis > distance / (Math.PI * 2))
                            break;
                        else
                            tempLat -= 0.000001;
                    }
                    break;
                default:
                    return null;
            }
        }else{
            while(true){
                ac = LatLngCalculate.getDistance(tempLat, tempLng, startPoint.latitude,
                        startPoint.longitude);
                bc = LatLngCalculate.getDistance(tempLat, tempLng, passingPoint.latitude,
                        passingPoint.longitude);
                if(Math.abs(ac - bc) <= 50){
                    break;
                }else{
                    if(startPoint.latitude < passingPoint.longitude)
                        tempLat += 0.000001;
                    else
                        tempLat -= 0.000001;
                }
            }
            switch (orient){
                case NORTH:
                    while (true){
                        tempDis = LatLngCalculate.getDistance(tempLat, tempLng, startPoint.latitude,
                                startPoint.longitude);
                        if(tempDis > distance / (Math.PI * 2))
                            break;
                        else
                            tempLng += 0.000001;
                    }
                    break;
                case SOUTH:
                    while (true){
                        tempDis = LatLngCalculate.getDistance(tempLat, tempLng, startPoint.latitude,
                                startPoint.longitude);
                        if(tempDis > distance / (Math.PI * 2))
                            break;
                        else
                            tempLng -= 0.000001;
                    }
                    break;
                default:
                    return null;
            }
        }
        c = new LatLng(tempLat, tempLng);
        double d1 = LatLngCalculate.getDistance(c.latitude, c.longitude, startPoint.latitude, startPoint.longitude);
        double d2 = LatLngCalculate.getDistance(c.latitude, c.longitude, passingPoint.latitude, passingPoint.longitude);
        boolean b = (d1 == d2);



//        r = distance / (Math.PI * 2);
        //longitude-->X
        //latitude-->Y
//        k = (passingPoint.latitude - startPoint.latitude) / (passingPoint.longitude -
//                startPoint.longitude);
//        if(k == 0){
//            centerX1 = tempLng;
//            centerX2 = tempLng;
//            centerY1 = tempLat + Math.sqrt(r * r - (passingPoint.longitude - startPoint.longitude)
//            * (passingPoint.longitude - startPoint.longitude) / 4);
//            centerY2 = tempLat - Math.sqrt(r * r - (passingPoint.longitude - startPoint.longitude)
//            * (passingPoint.longitude - startPoint.longitude) / 4);
//        }
//        else{
////            设：C1 = (x2^2 - x1^2 + y2^2 - y1^2)/2/(x2-x1)
////            设：C2 = (y2-y1)/(x2-x1)
////            centerX = c1 - c2 * centerY
////            二次项系数：A = (C2^2+1)
////            一次项系数：B = (2*x1*C2-2*C1*C2-2*y1)
////            常数项：    C = x1^2-2*x1*C1+C1^2+y1^2-R^2
//            double x1 = startPoint.longitude, y1 = startPoint.latitude;
//            double x2 = passingPoint.longitude, y2 = passingPoint.latitude;
//            double radius = r * Math.sqrt((x2 - (x1 + x2) / 2) * (x2 - (x1 + x2) / 2) +
//                    (y2 - (y1 + y2) / 2) * (y2 - (y1 + y2) / 2)) /
//            LatLngCalculate.getDistance(y1, x1, (y1 + y2) / 2, (x1 + x2) / 2);
//            double c1 = (x2 * x2 - x1 * x1 + y2 * y2 - y1 * y1) / (2 * (x2 - x1));
//            double c2 = (y2 - y1) / (x2 - x1);
//            double A = (1.0 + c2 * c2);
//            double B = (2 * x1 * c2 - 2 * c1 * c2 - 2 * y1);
//            double C = x1 * x1 - 2 * x1 * c1 + c1 * c1 + y1 * y1 - radius * radius;
////          centerY1 >= centerY2
//            centerY1 = (-1.0 * B + Math.sqrt(B * B - 4 * A * C)) / (2 * A);
//            centerY2 = (-1.0 * B + Math.sqrt(B * B - 4 * A * C)) / (2 * A);
//            centerX1 = c1 - c2 * centerY1;
//            centerX2 = c1 - c2 * centerY2;
//
//        }


//        //c
//        switch (orient){
//            case NORTH:
//                //求出半弦长
//                halfChord = LatLngCalculate.getDistance(startPoint.latitude,
//                        startPoint.longitude, passingPoint.latitude, passingPoint.longitude) / 2;
//                //求出弦长对应圆心角的一半
//                seta = Math.asin(halfChord / r);
//                //求出弦心距
//                chordDistance = r * Math.cos(seta);
//
//                sLat = tempLat;
//                sLng = tempLng;
//
//                if((startPoint.latitude < passingPoint.latitude & startPoint.longitude <
//                        passingPoint.longitude) || (startPoint.latitude > passingPoint.latitude &
//                        startPoint.longitude > passingPoint.longitude)) {
//                    while (true) {
//                        tempDis = LatLngCalculate.getDistance(sLat, sLng, tempLat, tempLng);
//                        if (tempDis > chordDistance)
//                            break;
//                        else {
//                            tempLat += 0.000001 * Math.sin(seta);
//                            tempLng -= 0.000001 * Math.cos(seta);
//                        }
//                    }
//                }
//                else{
//                    while (true) {
//                        tempDis = LatLngCalculate.getDistance(sLat, sLng, tempLat, tempLng);
//                        if (tempDis > chordDistance)
//                            break;
//                        else {
//                            tempLat += 0.000001 * Math.sin(seta);
//                            tempLng -= 0.000001 * Math.cos(seta);
//                        }
//                    }
//                }
//                break;
//            case SOUTH:
//                //求出半弦长
//                halfChord = LatLngCalculate.getDistance(startPoint.latitude,
//                        startPoint.longitude, passingPoint.latitude, passingPoint.longitude) / 2;
//                //求出弦长对应圆心角的一半
//                seta = Math.asin(halfChord / r);
//                //求出弦心距
//                chordDistance = r * Math.cos(seta);
//
//                sLat = tempLat;
//                sLng = tempLng;
//                while(true){
//                    tempDis = LatLngCalculate.getDistance(sLat, sLng, tempLat, tempLng);
//                    if(tempDis > chordDistance)
//                        break;
//                    else{
//                        tempLat -= 0.000001 * Math.sin(seta);
//                        tempLng += 0.000001 * Math.cos(seta);
//                    }
//                }
//                break;
//            default:
//                return null;
//        }
//        //c
//        c = new LatLng(tempLat, tempLng);
//        //n,s,w,e,nw,ne,sw,se
//        double test = LatLngCalculate.getDistance(c.latitude, c.longitude,
//                startPoint.latitude, startPoint.longitude);
//        while (true){
//            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude,
//                    tempLat, tempLng);
//            if(tempDis > r)
//                break;
//            else
//                tempLat += 0.000001;
//        }
//        n = new LatLng(tempLat, tempLng);
//        tempLat = c.latitude;
//        tempLng = c.longitude;
//        while (true){
//            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude,
//                    tempLat, tempLng);
//            if(tempDis > r)
//                break;
//            else
//                tempLat -= 0.000001;
//        }
//        s = new LatLng(tempLat, tempLng);
//        tempLat = c.latitude;
//        tempLng = c.longitude;
//        while(true){
//            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude,
//                    tempLat, tempLng);
//            if(tempDis > r)
//                break;
//            else
//                tempLng += 0.000001;
//        }
//        e = new LatLng(tempLat, tempLng);
//        tempLat = c.latitude;
//        tempLng = c.longitude;
//        while(true){
//            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude,
//                    tempLat, tempLng);
//            if(tempDis > r)
//                break;
//            else
//                tempLng -= 0.000001;
//        }
//        w = new LatLng(tempLat, tempLng);
//        tempLat = c.latitude;
//        tempLng = c.longitude;
//        while(true){
//            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude,
//                    tempLat, tempLng);
//            if(tempDis > r)
//                break;
//            else {
//                tempLng += 0.000001;
//                tempLat += 0.000001;
//            }
//        }
//        ne = new LatLng(tempLat, tempLng);
//        tempLat = c.latitude;
//        tempLng = c.longitude;
//        while(true){
//            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude,
//                    tempLat, tempLng);
//            if(tempDis > r)
//                break;
//            else {
//                tempLng -= 0.000001;
//                tempLat += 0.000001;
//            }
//        }
//        nw = new LatLng(tempLat, tempLng);
//        tempLat = c.latitude;
//        tempLng = c.longitude;
//        while(true){
//            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude,
//                    tempLat, tempLng);
//            if(tempDis > r)
//                break;
//            else {
//                tempLng -= 0.000001;
//                tempLat -= 0.000001;
//            }
//        }
//        sw = new LatLng(tempLat, tempLng);
//        tempLat = c.latitude;
//        tempLng = c.longitude;
//        while(true){
//            tempDis = LatLngCalculate.getDistance(c.latitude, c.longitude,
//                    tempLat, tempLng);
//            if(tempDis > r)
//                break;
//            else {
//                tempLng += 0.000001;
//                tempLat -= 0.000001;
//            }
//        }
//        se = new LatLng(tempLat, tempLng);
//
//        if(startPoint.longitude >= n.longitude && passingPoint.longitude >= n.longitude){
//
//        }



        RouteData routeData = new RouteData();
        routeData.centerPoint = null;
        routeData.keyPoints = points;
        routeData.passingPoint = passingPoint;
        return routeData;
    }

    public static void generateRoute(final LatLng startPoint, double distance, int orient,
                                     final RouteSelectActivity activity) {
        final RouteData routeData = getCircleKeyPoints(startPoint, distance, orient);

        //amap search_view_bg
        final RouteSearch routeSearch = new RouteSearch(activity);
        final ArrayList<RouteSearch.FromAndTo> fromAndToList = new ArrayList<RouteSearch.FromAndTo>();
        ArrayList<RouteSearch.WalkRouteQuery> queryList = new ArrayList<RouteSearch.WalkRouteQuery>();

        for (int i = 0; i < routeData.keyPoints.size() - 1; i++) {
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                    new LatLonPoint(routeData.keyPoints.get(i).latitude, routeData.keyPoints.get(i).longitude),
                    new LatLonPoint(routeData.keyPoints.get(i + 1).latitude, routeData.keyPoints.get(i + 1).longitude)
            );
            fromAndToList.add(fromAndTo);
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(
                    new RouteSearch.FromAndTo(
                            new LatLonPoint(routeData.keyPoints.get(i).latitude, routeData.keyPoints.get(i).longitude),
                            new LatLonPoint(routeData.keyPoints.get(i + 1).latitude, routeData.keyPoints.get(i + 1).longitude)
                    )
            );
            queryList.add(query);
        }

        //query listener
        routeSearch.setRouteSearchListener(new RunnerRouteSearchListener(queryList.size()) {
            @Override
            public void successHandler(WalkRouteResult walkRouteResult) {
                int index;
                for (index = 0; index < fromAndToList.size(); index++) {
                    RouteSearch.FromAndTo fromAndTo1 = walkRouteResult.getWalkQuery().getFromAndTo();
                    RouteSearch.FromAndTo fromAndTo2 = fromAndToList.get(index);
                    if (fromAndTo1.getFrom().getLatitude() == fromAndTo2.getFrom().getLatitude()
                            && fromAndTo1.getFrom().getLongitude() == fromAndTo2.getFrom().getLongitude()) {
                        break;
                    }
                }
                resultList[index] = walkRouteResult;
                responseCounter++;
                if (responseCounter >= resultList.length) {
                    polyProcess();
                }
            }

            @Override
            public void errorHandler() {
                activity.alert(new AlertMessage("路线规划出错", ""));
            }

            private void polyProcess() {
                //从所有结果中提取多边形顶点
                ArrayList<LatLng> polyResourcePoints = new ArrayList<LatLng>();
                for (WalkRouteResult result : this.resultList) {
                    if (result == null)
                        continue;
                    WalkPath path = result.getPaths().get(result.getPaths().size() - 1);
                    for (WalkStep step : path.getSteps()) {
                        for (LatLonPoint rawPoint : step.getPolyline()) {
                            polyResourcePoints.add(new LatLng(rawPoint.getLatitude(), rawPoint.getLongitude()));
                        }
                    }
                }
                //删除重复路径
                Boolean[] deleteFlag = new Boolean[polyResourcePoints.size()];
                for (int i = 0; i < deleteFlag.length; i++)
                    deleteFlag[i] = false;
                //路线剪枝
                ArrayList<LatLng> optimizedPoints = new ArrayList<LatLng>();
                for (int i = 0, j; i < polyResourcePoints.size() - 1; i++) {
                    for (j = i + 1; j < polyResourcePoints.size(); j++) {
                        if (LatLngCalculate.isSamePoint(polyResourcePoints.get(i), polyResourcePoints.get(j))
                                && (j - i) <= polyResourcePoints.size() / 3) {
                            //剪枝
                            for (int deleteIndex = i + 1; deleteIndex <= j; deleteIndex++) {
                                deleteFlag[deleteIndex] = true;
                            }
                        }
                    }
                }
                for (int i = 0; i < polyResourcePoints.size(); i++) {
                    if (!deleteFlag[i])
                        optimizedPoints.add(polyResourcePoints.get(i));
                }
                polyResourcePoints = optimizedPoints;
                if (!LatLngCalculate.isSamePoint(polyResourcePoints.get(0), polyResourcePoints.get(polyResourcePoints.size() - 1)))
                    polyResourcePoints.add(new LatLng(polyResourcePoints.get(0).latitude, polyResourcePoints.get(0).longitude));


                //add polyline to schematic diagram map view
                routeData.length = LatLngCalculate.getPathLength(polyResourcePoints);
                routeData.ployPoints = polyResourcePoints;
                RouteSchematicDiagramLayout diagram = new RouteSchematicDiagramLayout(activity, activity.getSavedInstanceState());
                diagram.setRouteData(routeData);
                AMap map = diagram.getMapView().getMap();
                diagram.getTextView().setText("" + routeData.length + "M");
                diagram.getMapView().onCreate(activity.getSavedInstanceState());
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.setPoints(polyResourcePoints);
                polylineOptions.width(8);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
                map.addPolyline(polylineOptions);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(startPoint);
                markerOptions.draggable(false);
                map.addMarker(markerOptions).setClickable(false);
                map.moveCamera(CameraUpdateFactory.changeLatLng(routeData.centerPoint));
                map.moveCamera(CameraUpdateFactory.zoomTo(MapScale.getScale((int)routeData.length)));
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(false);
                activity.addSchematicDiagram(diagram);
            }
        });

        //asyn query
        for (RouteSearch.WalkRouteQuery query : queryList)
            routeSearch.calculateWalkRouteAsyn(query);

    }

    public static void generateRouteWithPassingPoint(final LatLng startPoint, LatLng passingPoint,
                                                     double distance, int orient,
                                                     final RouteSelectActivity activity){
        final RouteData routeData = getCircleKeyPointsWithPassingPoint(startPoint, passingPoint,
                distance, orient);


    }


    public static double Y_Coordinates(double x, double y, double k, double x0)
    {
        return k * x0 - k * x + y;
    }


    public static void generateRouteWithMerchant(final LatLng startPoint, double distance, int orient, final RouteSelectActivity activity) {
        final RouteData routeData = getCircleKeyPoints(startPoint, distance, orient);
        Call<List<Merchant>> searchMerchantsCall = RunnerAsyn.RunnerService.searchMerchants(routeData.centerPoint.longitude, routeData.centerPoint.latitude, (int) (distance / Math.PI / 2) + 100, (int) (distance / Math.PI / 4));
        RunnerAsyn.AppServerAsyn(searchMerchantsCall, activity, new AppServerAsynHandler<List<Merchant>>() {
                    @Override
                    public void handler(Response<List<Merchant>> response) throws Exception {
                        final List<Merchant> merchants = response.body();
                        if (merchants.size() == 0)
                            return;
                        for (int i = 0; i < 9; i++) {
                            routeData.isMerchant.add(false);
                            routeData.merchantInfo.add(null);
                        }
                        //使用商家点替换最近的关键点，若最近位起始/终止点，则插入商家点
                        for (Merchant merchant : merchants) {
                            LatLng mp = new LatLng(merchant.getPosition().getLatitude(), merchant.getPosition().getLongitude());
                            ArrayList<LatLng> points = routeData.keyPoints;
                            int nearPointIndex = -1;
                            double dis = 100000000;
                            for (int i = 0; i < points.size() - 1; i++) {
                                if (LatLngCalculate.getDistance(mp.latitude, mp.longitude, points.get(i).latitude, points.get(i).longitude) < dis) {
                                    dis = LatLngCalculate.getDistance(mp.latitude, mp.longitude, points.get(i).latitude, points.get(i).longitude);
                                    nearPointIndex = i;
                                }
                            }
                            if (nearPointIndex != 0) {
                                points.set(nearPointIndex, mp);
                                routeData.isMerchant.set(nearPointIndex, true);
                                routeData.merchantInfo.set(nearPointIndex, merchant);
                            } else {
                                if (LatLngCalculate.getDistance(mp.latitude, mp.longitude, points.get(1).latitude, points.get(1).longitude)
                                        <= LatLngCalculate.getDistance(mp.latitude, mp.longitude, points.get(points.size() - 2).latitude, points.get(points.size() - 2).longitude)) {
                                    points.add(1, mp);
                                    routeData.isMerchant.add(1, true);
                                    routeData.merchantInfo.add(1, merchant);
                                } else {
                                    points.add(points.size() - 1, mp);
                                    routeData.isMerchant.add(routeData.isMerchant.size() - 1, true);
                                    routeData.merchantInfo.add(routeData.merchantInfo.size() - 1, merchant);
                                }
                            }
                        }
                        //amap search_view_bg
                        final RouteSearch routeSearch = new RouteSearch(activity);
                        final ArrayList<RouteSearch.FromAndTo> fromAndToList = new ArrayList<RouteSearch.FromAndTo>();
                        ArrayList<RouteSearch.WalkRouteQuery> queryList = new ArrayList<RouteSearch.WalkRouteQuery>();

                        for (int i = 0; i < routeData.keyPoints.size() - 1; i++) {
                            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                                    new LatLonPoint(routeData.keyPoints.get(i).latitude, routeData.keyPoints.get(i).longitude),
                                    new LatLonPoint(routeData.keyPoints.get(i + 1).latitude, routeData.keyPoints.get(i + 1).longitude)
                            );
                            fromAndToList.add(fromAndTo);
                            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(
                                    new RouteSearch.FromAndTo(
                                            new LatLonPoint(routeData.keyPoints.get(i).latitude, routeData.keyPoints.get(i).longitude),
                                            new LatLonPoint(routeData.keyPoints.get(i + 1).latitude, routeData.keyPoints.get(i + 1).longitude)
                                    )
                            );
                            queryList.add(query);
                        }

                        //query listener
                        routeSearch.setRouteSearchListener(new RunnerRouteSearchListener(queryList.size()) {
                            @Override
                            public void successHandler(WalkRouteResult walkRouteResult) {
                                int index;
                                for (index = 0; index < fromAndToList.size(); index++) {
                                    RouteSearch.FromAndTo fromAndTo1 = walkRouteResult.getWalkQuery().getFromAndTo();
                                    RouteSearch.FromAndTo fromAndTo2 = fromAndToList.get(index);
                                    if (fromAndTo1.getFrom().getLatitude() == fromAndTo2.getFrom().getLatitude()
                                            && fromAndTo1.getFrom().getLongitude() == fromAndTo2.getFrom().getLongitude()) {
                                        break;
                                    }
                                }
                                resultList[index] = walkRouteResult;
                                responseCounter++;
                                if (responseCounter >= resultList.length) {
                                    polyProcess();
                                }
                            }

                            @Override
                            public void errorHandler() {
                                activity.alert(new AlertMessage("路线规划出错", ""));
                            }

                            private void polyProcess() {
                                //从所有结果中提取多边形顶点
                                ArrayList<LatLng> polyResourcePoints = new ArrayList<LatLng>();
                                for (WalkRouteResult result : this.resultList) {
                                    if (result == null)
                                        continue;
                                    WalkPath path = result.getPaths().get(result.getPaths().size() - 1);
                                    for (WalkStep step : path.getSteps()) {
                                        for (LatLonPoint rawPoint : step.getPolyline()) {
                                            polyResourcePoints.add(new LatLng(rawPoint.getLatitude(), rawPoint.getLongitude()));
                                        }
                                    }
                                }
                                //删除重复路径
                                Boolean[] deleteFlag = new Boolean[polyResourcePoints.size()];
                                for (int i = 0; i < deleteFlag.length; i++)
                                    deleteFlag[i] = false;
                                //路线剪枝
                                ArrayList<LatLng> optimizedPoints = new ArrayList<LatLng>();
                                for (int i = 0, j; i < polyResourcePoints.size() - 1; i++) {
                                    for (j = i + 1; j < polyResourcePoints.size(); j++) {
                                        if (LatLngCalculate.isSamePoint(polyResourcePoints.get(i), polyResourcePoints.get(j))
                                                && (j - i) <= polyResourcePoints.size() / 3) {
                                            //剪枝过程中被剪子段中如果含有商家，则放弃剪枝
                                            boolean haveMerchant = false;
                                            for (int testIndex = i + 1; testIndex <= j; testIndex++) {
                                                for (Merchant merchant : merchants) {
                                                    if (LatLngCalculate.isSamePoint(polyResourcePoints.get(testIndex), new LatLng(merchant.getPosition().getLatitude(), merchant.getPosition().getLatitude()))) {
                                                        haveMerchant = true;
                                                    }
                                                }
                                            }
                                            if (haveMerchant)
                                                continue;
                                            //剪枝
                                            for (int deleteIndex = i + 1; deleteIndex <= j; deleteIndex++) {
                                                deleteFlag[deleteIndex] = true;
                                            }
                                        }
                                    }
                                }
                                for (int i = 0; i < polyResourcePoints.size(); i++) {
                                    if (!deleteFlag[i])
                                        optimizedPoints.add(polyResourcePoints.get(i));
                                }
                                polyResourcePoints = optimizedPoints;
                                if (!LatLngCalculate.isSamePoint(polyResourcePoints.get(0), polyResourcePoints.get(polyResourcePoints.size() - 1)))
                                    polyResourcePoints.add(new LatLng(polyResourcePoints.get(0).latitude, polyResourcePoints.get(0).longitude));


                                //add polyline to schematic diagram map view
                                routeData.length = LatLngCalculate.getPathLength(polyResourcePoints);
                                routeData.ployPoints = polyResourcePoints;
                                RouteSchematicDiagramLayout diagram = new RouteSchematicDiagramLayout(activity, activity.getSavedInstanceState());
                                diagram.setRouteData(routeData);
                                AMap map = diagram.getMapView().getMap();
                                diagram.getTextView().setText("" + routeData.length + "M");
                                diagram.getMapView().onCreate(activity.getSavedInstanceState());
                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.setPoints(polyResourcePoints);
                                map.addPolyline(polylineOptions);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(startPoint);
                                markerOptions.draggable(false);
                                map.addMarker(markerOptions).setClickable(false);
                                //merchant marker
                                for (Merchant merchant : merchants) {
                                    LatLng locationLatLng = new LatLng(merchant.getPosition().getLatitude(), merchant.getPosition().getLongitude());
                                    markerOptions = new MarkerOptions();
                                    markerOptions.position(locationLatLng);
                                    markerOptions.draggable(false);
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shop_logo));
                                    map.addMarker(markerOptions).setClickable(false);
                                }

                                map.moveCamera(CameraUpdateFactory.changeLatLng(routeData.centerPoint));
                                map.moveCamera(CameraUpdateFactory.zoomTo(12));
                                map.getUiSettings().setZoomControlsEnabled(false);
                                map.getUiSettings().setAllGesturesEnabled(false);
                                activity.addSchematicDiagram(diagram);
                            }
                        });

                        //asyn query
                        for (RouteSearch.WalkRouteQuery query : queryList)
                            routeSearch.calculateWalkRouteAsyn(query);
                    }
                },
                new AlertMessage("系统出错", ""),
                new AlertMessage("网络错误", "请检查网络连接"));
    }


}


