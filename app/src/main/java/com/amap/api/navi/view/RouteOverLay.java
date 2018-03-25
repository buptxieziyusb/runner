//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.amap.api.navi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.amap.api.col.sln3.lu;
import com.amap.api.col.sln3.lw;
import com.amap.api.col.sln3.pk;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NavigateArrow;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AMapNaviException;
import com.amap.api.navi.model.AMapNaviLink;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.AMapNotAvoidInfo;
import com.amap.api.navi.model.AMapTrafficStatus;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.RouteOverlayOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class RouteOverLay {
    private Bitmap startBitmap;
    private Bitmap endBitmap;
    private Bitmap wayBitmap;
    private BitmapDescriptor startBitmapDescriptor;
    private BitmapDescriptor endBitmapDescriptor;
    private BitmapDescriptor wayPointBitmapDescriptor;
    private Marker startMarker;
    private List<Marker> wayMarkers;
    private Marker endMarker;
    private BitmapDescriptor arrowOnRoute = null;
    private BitmapDescriptor normalRoute = null;
    private BitmapDescriptor unknownTraffic = null;
    private BitmapDescriptor smoothTraffic = null;
    private BitmapDescriptor slowTraffic = null;
    private BitmapDescriptor jamTraffic = null;
    private BitmapDescriptor veryJamTraffic = null;
    private BitmapDescriptor fairWayRes = null;
    private List<Polyline> mTrafficColorfulPolylines = new ArrayList();
    private RouteOverlayOptions mRouteOverlayOptions = null;
    private float mWidth = 40.0F;
    private AMapNaviPath mAMapNaviPath = null;
    private Polyline mDefaultPolyline;
    private AMap aMap;
    private Context mContext;
    private List<LatLng> mLatLngsOfPath;
    private Polyline guideLink = null;
    private List<Circle> gpsCircles = null;
    private boolean emulateGPSLocationVisibility = true;
    private NavigateArrow naviArrow = null;
    private boolean isTrafficLine = true;
    private List<Polyline> mCustomPolylines = new ArrayList();
    private int arrowColor;
    protected List<Marker> ferryMarkers;
    protected HashMap<Integer, BitmapDescriptor> custtextureHash = new HashMap();
    protected int tempLinkType = 0;
    protected int tempTrafficIndex = -1;
    protected NaviLimitOverlay naviLimitOverlay;

    public RouteOverLay(AMap var1, AMapNaviPath var2, Context var3) {
        try {
            this.arrowColor = Color.parseColor("#4DF6CC");
            this.mContext = var3;
            this.mWidth = (float)lu.a(var3, 22);
            this.init(var1, var2);
        } catch (Throwable var4) {
            var4.printStackTrace();
        }
    }

    public float getWidth() {
        return this.mWidth;
    }

    /** @deprecated */
    public void setWidth(float var1) throws AMapNaviException {
        if(var1 > 0.0F) {
            this.mWidth = var1;
        }
    }

    public RouteOverlayOptions getRouteOverlayOptions() {
        return this.mRouteOverlayOptions;
    }

    public void setRouteOverlayOptions(RouteOverlayOptions var1) {
        try {
            this.mRouteOverlayOptions = var1;
            if(var1 != null && var1.getNormalRoute() != null) {
                this.normalRoute = BitmapDescriptorFactory.fromBitmap(var1.getNormalRoute());
            }

            if(var1 != null && var1.getArrowOnTrafficRoute() != null) {
                this.arrowOnRoute = BitmapDescriptorFactory.fromBitmap(var1.getArrowOnTrafficRoute());
            }

            if(var1 != null && var1.getUnknownTraffic() != null) {
                this.unknownTraffic = BitmapDescriptorFactory.fromBitmap(var1.getUnknownTraffic());
            }

            if(var1 != null && var1.getSmoothTraffic() != null) {
                this.smoothTraffic = BitmapDescriptorFactory.fromBitmap(var1.getSmoothTraffic());
            }

            if(var1 != null && var1.getSlowTraffic() != null) {
                this.slowTraffic = BitmapDescriptorFactory.fromBitmap(var1.getSlowTraffic());
            }

            if(var1 != null && var1.getJamTraffic() != null) {
                this.jamTraffic = BitmapDescriptorFactory.fromBitmap(var1.getJamTraffic());
            }

            if(var1 != null && var1.getVeryJamTraffic() != null) {
                this.veryJamTraffic = BitmapDescriptorFactory.fromBitmap(var1.getVeryJamTraffic());
            }

            if(var1 != null && var1.getLineWidth() > 0.0F) {
                this.mWidth = var1.getLineWidth();
            }

            if(var1 != null && var1.getArrowColor() != this.arrowColor) {
                this.arrowColor = var1.getArrowColor();
            }

            this.custtextureHash.put(Integer.valueOf(0), this.unknownTraffic);
            this.custtextureHash.put(Integer.valueOf(1), this.smoothTraffic);
            this.custtextureHash.put(Integer.valueOf(2), this.slowTraffic);
            this.custtextureHash.put(Integer.valueOf(3), this.jamTraffic);
            this.custtextureHash.put(Integer.valueOf(4), this.veryJamTraffic);
        } catch (Throwable var2) {
            var2.printStackTrace();
        }
    }

    public AMapNaviPath getAMapNaviPath() {
        return this.mAMapNaviPath;
    }

    public void setAMapNaviPath(AMapNaviPath var1) {
        this.mAMapNaviPath = var1;
    }

    /** @deprecated */
    @Deprecated
    public void setRouteInfo(AMapNaviPath var1) {
        this.mAMapNaviPath = var1;
    }

    private void init(AMap var1, AMapNaviPath var2) {
        try {
            try {
                this.aMap = var1;
                this.mAMapNaviPath = var2;
                this.normalRoute = BitmapDescriptorFactory.fromAsset("custtexture.png");
                this.naviLimitOverlay = new NaviLimitOverlay(this.mContext, var1);
            } catch (Throwable var3) {
                lu.a(var3);
                pk.b(var3, "RouteOverLay", "init(AMap amap, AMapNaviPath aMapNaviPath)");
            }

            this.arrowOnRoute = BitmapDescriptorFactory.fromAsset("custtexture_aolr.png");
            this.smoothTraffic = BitmapDescriptorFactory.fromAsset("custtexture_green.png");
            this.unknownTraffic = BitmapDescriptorFactory.fromAsset("custtexture_no.png");
            this.slowTraffic = BitmapDescriptorFactory.fromAsset("custtexture_slow.png");
            this.jamTraffic = BitmapDescriptorFactory.fromAsset("custtexture_bad.png");
            this.veryJamTraffic = BitmapDescriptorFactory.fromAsset("custtexture_grayred.png");
            this.fairWayRes = BitmapDescriptorFactory.fromAsset("lbs_custtexture_dott_gray.png");
            this.wayPointBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(lw.a(), 2130837870));
            this.custtextureHash.put(Integer.valueOf(0), this.unknownTraffic);
            this.custtextureHash.put(Integer.valueOf(1), this.smoothTraffic);
            this.custtextureHash.put(Integer.valueOf(2), this.slowTraffic);
            this.custtextureHash.put(Integer.valueOf(3), this.jamTraffic);
            this.custtextureHash.put(Integer.valueOf(4), this.veryJamTraffic);
            this.custtextureHash.put(Integer.valueOf(5), this.fairWayRes);
        } catch (Throwable var4) {
            var4.printStackTrace();
        }
    }

    public void addToMap() {
        try {
            if(this.aMap != null) {
                if(this.mDefaultPolyline != null) {
                    this.mDefaultPolyline.remove();
                    this.mDefaultPolyline = null;
                }

                if(this.mWidth != 0.0F && this.mAMapNaviPath != null) {
                    this.handleLimitAndForbiddenInfos();
                    if(this.naviArrow != null) {
                        this.naviArrow.setVisible(false);
                    }

                    List var1;
                    if((var1 = this.mAMapNaviPath.getCoordList()) != null) {
                        int var2 = var1.size();
                        this.mLatLngsOfPath = new ArrayList(var2);
                        Iterator var7 = var1.iterator();

                        while(var7.hasNext()) {
                            NaviLatLng var9 = (NaviLatLng)var7.next();
                            LatLng var3 = new LatLng(var9.getLatitude(), var9.getLongitude(), false);
                            this.mLatLngsOfPath.add(var3);
                        }

                        if(this.mLatLngsOfPath.size() != 0) {
                            this.clearTrafficLineAndInvisibleOriginalLine();
                            this.mDefaultPolyline = this.aMap.addPolyline((new PolylineOptions()).addAll(this.mLatLngsOfPath).setCustomTexture(this.normalRoute).width(this.mWidth - 5.0F));
                            this.mDefaultPolyline.setVisible(true);
                            LatLng var8 = null;
                            LatLng var10 = null;
                            List var12 = null;
                            if(this.mAMapNaviPath.getStartPoint() != null && this.mAMapNaviPath.getEndPoint() != null) {
                                var8 = new LatLng(this.mAMapNaviPath.getStartPoint().getLatitude(), this.mAMapNaviPath.getStartPoint().getLongitude());
                                var10 = new LatLng(this.mAMapNaviPath.getEndPoint().getLatitude(), this.mAMapNaviPath.getEndPoint().getLongitude());
                                var12 = this.mAMapNaviPath.getWayPoint();
                            }

                            if(this.startMarker != null) {
                                this.startMarker.remove();
                                this.startMarker = null;
                            }

                            if(this.endMarker != null) {
                                this.endMarker.remove();
                                this.endMarker = null;
                            }

                            int var4;
                            if(this.wayMarkers != null && this.wayMarkers.size() > 0) {
                                for(var4 = 0; var4 < this.wayMarkers.size(); ++var4) {
                                    Marker var5;
                                    if((var5 = (Marker)this.wayMarkers.get(var4)) != null) {
                                        var5.remove();
                                    }
                                }
                            }

                            if(this.startBitmap == null) {
                                this.startMarker = this.aMap.addMarker((new MarkerOptions()).position(var8).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(lw.a(), 2130837857))));
                            } else if(this.startBitmapDescriptor != null) {
                                this.startMarker = this.aMap.addMarker((new MarkerOptions()).position(var8).icon(this.startBitmapDescriptor));
                            }

                            if(var12 != null && var12.size() > 0) {
                                var4 = var12.size();
                                if(this.wayMarkers == null) {
                                    this.wayMarkers = new ArrayList(var4);
                                }

                                Marker var13;
                                for(Iterator var14 = var12.iterator(); var14.hasNext(); this.wayMarkers.add(var13)) {
                                    NaviLatLng var11 = (NaviLatLng)var14.next();
                                    var8 = new LatLng(var11.getLatitude(), var11.getLongitude());
                                    var13 = null;
                                    if(this.wayBitmap == null) {
                                        var13 = this.aMap.addMarker((new MarkerOptions()).position(var8).icon(this.wayPointBitmapDescriptor));
                                    } else if(this.wayPointBitmapDescriptor != null) {
                                        var13 = this.aMap.addMarker((new MarkerOptions()).position(var8).icon(this.wayPointBitmapDescriptor));
                                    }
                                }
                            }

                            if(this.endBitmap == null) {
                                this.endMarker = this.aMap.addMarker((new MarkerOptions()).position(var10).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(lw.a(), 2130837604))));
                            } else if(this.endBitmapDescriptor != null) {
                                this.endMarker = this.aMap.addMarker((new MarkerOptions()).position(var10).icon(this.endBitmapDescriptor));
                            }

                            if(this.isTrafficLine) {
                                this.setTrafficLine(Boolean.valueOf(this.isTrafficLine));
                            }

                        }
                    }
                }
            }
        } catch (Throwable var6) {
            lu.a(var6);
            pk.b(var6, "RouteOverLay", "addToMap()");
        }
    }

    public void drawGuideLink(LatLng var1, LatLng var2, boolean var3) {
        try {
            if(var3) {
                ArrayList var5;
                (var5 = new ArrayList(2)).add(var1);
                var5.add(var2);
                if(this.guideLink == null) {
                    this.guideLink = this.aMap.addPolyline((new PolylineOptions()).addAll(var5).width(this.mWidth / 3.0F).setDottedLine(true));
                } else {
                    this.guideLink.setPoints(var5);
                }

                this.guideLink.setVisible(true);
            } else {
                if(this.guideLink != null) {
                    this.guideLink.setVisible(false);
                }

            }
        } catch (Throwable var4) {
            var4.printStackTrace();
        }
    }

    public void drawEmulateGPSLocation(Vector<String> var1) {
        try {
            Iterator var2;
            if(this.gpsCircles == null) {
                this.gpsCircles = new ArrayList(var1.size());
            } else {
                var2 = this.gpsCircles.iterator();

                while(var2.hasNext()) {
                    ((Circle)var2.next()).remove();
                }

                this.gpsCircles.clear();
            }

            var2 = var1.iterator();

            while(var2.hasNext()) {
                String[] var4;
                if((var4 = ((String)var2.next()).split(",")) != null && var4.length >= 11) {
                    LatLng var5 = new LatLng(Double.parseDouble(var4[0]), Double.parseDouble(var4[1]));
                    Circle var6 = this.aMap.addCircle((new CircleOptions()).center(var5).radius(1.5D).strokeWidth(0.0F).fillColor(-65536));
                    this.gpsCircles.add(var6);
                }
            }

        } catch (Throwable var3) {
            var3.printStackTrace();
            pk.b(var3, "RouteOverLay", "drawEmulateGPSLocation(Vector<String> gpsData)");
        }
    }

    public void setEmulateGPSLocationVisible() {
        try {
            if(this.gpsCircles != null) {
                this.emulateGPSLocationVisibility = !this.emulateGPSLocationVisibility;
                Iterator var1 = this.gpsCircles.iterator();

                while(var1.hasNext()) {
                    ((Circle)var1.next()).setVisible(this.emulateGPSLocationVisibility);
                }
            }

        } catch (Throwable var2) {
            var2.printStackTrace();
        }
    }

    public void setStartPointBitmap(Bitmap var1) {
        try {
            this.startBitmap = var1;
            if(this.startBitmap != null) {
                this.startBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(this.startBitmap);
            }

        } catch (Throwable var2) {
            var2.printStackTrace();
        }
    }

    public void setWayPointBitmap(Bitmap var1) {
        try {
            this.wayBitmap = var1;
            if(this.wayBitmap != null) {
                this.wayPointBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(this.wayBitmap);
            }

        } catch (Throwable var2) {
            var2.printStackTrace();
        }
    }

    public void setEndPointBitmap(Bitmap var1) {
        try {
            this.endBitmap = var1;
            if(this.endBitmap != null) {
                this.endBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(this.endBitmap);
            }

        } catch (Throwable var2) {
            var2.printStackTrace();
        }
    }

    public void removeFromMap() {
        try {
            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.setVisible(false);
            }

            if(this.startMarker != null) {
                this.startMarker.setVisible(false);
            }

            Iterator var1;
            if(this.wayMarkers != null) {
                var1 = this.wayMarkers.iterator();

                while(var1.hasNext()) {
                    ((Marker)var1.next()).setVisible(false);
                }
            }

            if(this.endMarker != null) {
                this.endMarker.setVisible(false);
            }

            if(this.naviArrow != null) {
                this.naviArrow.remove();
            }

            if(this.guideLink != null) {
                this.guideLink.setVisible(false);
            }

            if(this.gpsCircles != null) {
                var1 = this.gpsCircles.iterator();

                while(var1.hasNext()) {
                    ((Circle)var1.next()).setVisible(false);
                }
            }

            this.clearTrafficLineAndInvisibleOriginalLine();
        } catch (Throwable var2) {
            lu.a(var2);
            pk.b(var2, "RouteOverLay", "removeFromMap()");
        }
    }

    private void clearTrafficLineAndInvisibleOriginalLine() {
        int var1;
        if(this.mTrafficColorfulPolylines.size() > 0) {
            for(var1 = 0; var1 < this.mTrafficColorfulPolylines.size(); ++var1) {
                if(this.mTrafficColorfulPolylines.get(var1) != null) {
                    ((Polyline)this.mTrafficColorfulPolylines.get(var1)).remove();
                }
            }
        }

        this.mTrafficColorfulPolylines.clear();
        if(this.mDefaultPolyline != null) {
            this.mDefaultPolyline.setVisible(false);
        }

        if(this.mCustomPolylines.size() > 0) {
            for(var1 = 0; var1 < this.mCustomPolylines.size(); ++var1) {
                if(this.mCustomPolylines.get(var1) != null) {
                    ((Polyline)this.mCustomPolylines.get(var1)).setVisible(false);
                }
            }
        }

    }

    protected void colorWayUpdate(List<AMapTrafficStatus> var1) {
        try {
            if(var1 != null && var1.size() > 0) {
                this.tempTrafficIndex = -1;
                NaviLatLng var13 = this.mAMapNaviPath.getCarToFootPoint();
                Object var2 = new ArrayList();
                List var3 = this.mAMapNaviPath.getSteps();
                boolean var4 = false;

                for(int var5 = 0; var5 < var3.size(); ++var5) {
                    AMapNaviStep var6;
                    List var7 = (var6 = (AMapNaviStep)var3.get(var5)).getLinks();
                    AMapNaviLink var8;
                    if((var8 = (AMapNaviLink)var6.getLinks().get(0)).getLinkType() == 1) {
                        if(((List)var2).size() > 1) {
                            this.drawTrafficPolyline((List)var2, this.tempTrafficIndex, (LatLng)null);
                        }

                        this.drawFairWayPositionIcon(var8, var6);
                        this.drawFairWayLine(var7);
                        ((List)var2).clear();
                        this.tempTrafficIndex = -1;
                    } else {
                        this.tempLinkType = var8.getLinkType();
                        int var15 = -1;
                        if(var13 != null) {
                            var15 = lu.a(this.mAMapNaviPath.getStartPoint(), var13);
                        }

                        for(int var16 = 0; var16 < var7.size(); ++var16) {
                            AMapNaviLink var9;
                            (var9 = (AMapNaviLink)var7.get(var16)).getRoadClass();
                            var9.getRoadName();
                            LatLng var10 = null;

                            int var11;
                            for(var11 = 0; !var4 && var11 < var9.getCoords().size(); ++var11) {
                                var10 = new LatLng(((NaviLatLng)var9.getCoords().get(var11)).getLatitude(), ((NaviLatLng)var9.getCoords().get(var11)).getLongitude(), false);
                                if(var13 != null && var15 != -1 && var15 > 1000 && Math.abs(var10.latitude - var13.getLatitude()) < 5.0E-6D && Math.abs(var10.longitude - var13.getLongitude()) < 5.0E-6D) {
                                    ((List)var2).add(var10);
                                    var4 = true;
                                    break;
                                }

                                if((((List)var2).size() == 0 || !((LatLng)((List)var2).get(((List)var2).size() - 1)).equals(var10)) && (var5 != var3.size() - 1 || var16 != var7.size() - 1)) {
                                    ((List)var2).add(var10);
                                }
                            }

                            var11 = var9.getTrafficStatus();
                            if(((List)var2).size() > 0 && this.tempTrafficIndex != -1) {
                                if(var5 >= var3.size() - 1 && var16 >= var7.size() - 1) {
                                    if(this.tempTrafficIndex != var11) {
                                        var10 = (LatLng)((List)var2).get(((List)var2).size() - 1);
                                        this.drawTrafficPolyline((List)var2, this.tempTrafficIndex, var10);
                                    }

                                    var2 = this.getLinkLatlngs((List)var2, var9);
                                    this.drawTrafficPolyline((List)var2, var11, (LatLng)null);
                                } else if(this.tempTrafficIndex != var11) {
                                    this.drawTrafficPolyline((List)var2, this.tempTrafficIndex, var10);
                                }
                            }

                            this.tempTrafficIndex = var9.getTrafficStatus();
                        }
                    }
                }

                Polyline var14 = this.aMap.addPolyline((new PolylineOptions()).addAll(this.mLatLngsOfPath).width(this.mWidth).setCustomTexture(this.arrowOnRoute));
                this.mTrafficColorfulPolylines.add(var14);
            }
        } catch (Throwable var12) {
            var12.printStackTrace();
        }
    }

    private List<LatLng> getLinkLatlngs(List<LatLng> var1, AMapNaviLink var2) {
        for(int var3 = 0; var3 < var2.getCoords().size(); ++var3) {
            LatLng var4 = new LatLng(((NaviLatLng)var2.getCoords().get(var3)).getLatitude(), ((NaviLatLng)var2.getCoords().get(var3)).getLongitude(), false);
            if(var1.size() == 0 || !((LatLng)var1.get(var1.size() - 1)).equals(var4)) {
                var1.add(var4);
            }
        }

        return var1;
    }

    protected void drawTrafficPolyline(List<LatLng> var1, int var2, LatLng var3) {
        BitmapDescriptor var4;
        Polyline var5;
        if((var4 = (BitmapDescriptor)this.custtextureHash.get(Integer.valueOf(var2))) != null) {
            var5 = this.addTrafficPolyline(var1, var4);
        } else {
            var5 = this.addTrafficPolyline(var1, this.unknownTraffic);
        }

        this.mTrafficColorfulPolylines.add(var5);
        var1.clear();
        if(var3 != null) {
            var1.add(var3);
        }

    }

    protected void drawFairWayPositionIcon(AMapNaviLink var1, AMapNaviStep var2) {
        if(var1.getLinkType() != this.tempLinkType) {
            if(this.ferryMarkers == null) {
                this.ferryMarkers = new ArrayList();
            }

            NaviLatLng var3 = (NaviLatLng)var2.getCoords().get(0);
            LatLng var4 = new LatLng(var3.getLatitude(), var3.getLongitude());
            Marker var5 = this.aMap.addMarker((new MarkerOptions()).position(var4).anchor(0.5F, 0.5F).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(lw.a(), 2130837563))));
            this.ferryMarkers.add(var5);
        }

    }

    protected void drawFairWayLine(List<AMapNaviLink> var1) {
        ArrayList var2 = new ArrayList();

        for(int var3 = 0; var3 < var1.size(); ++var3) {
            AMapNaviLink var4 = (AMapNaviLink)var1.get(var3);

            for(int var5 = 0; var5 < var4.getCoords().size(); ++var5) {
                LatLng var6 = new LatLng(((NaviLatLng)var4.getCoords().get(var5)).getLatitude(), ((NaviLatLng)var4.getCoords().get(var5)).getLongitude(), false);
                if(var2.size() == 0 || !((LatLng)var2.get(var2.size() - 1)).equals(var6)) {
                    var2.add(var6);
                }
            }
        }

        Polyline var7 = this.addTrafficPolyline(var2, this.fairWayRes);
        this.mTrafficColorfulPolylines.add(var7);
        var2.clear();
    }

    protected Polyline addTrafficPolyline(List<LatLng> var1, BitmapDescriptor var2) {
        return this.aMap.addPolyline((new PolylineOptions()).addAll(var1).width(this.mWidth).setCustomTexture(var2));
    }

    public void zoomToSpan() {
        try {
            this.zoomToSpan(100);
        } catch (Throwable var1) {
            var1.printStackTrace();
        }
    }

    public void zoomToSpan(int var1) {
        try {
            if(this.mAMapNaviPath != null) {
                CameraUpdate var3 = CameraUpdateFactory.newLatLngBounds(this.mAMapNaviPath.getBoundsForPath(), var1);
                this.aMap.animateCamera(var3, 1000L, (CancelableCallback)null);
            }
        } catch (Throwable var2) {
            lu.a(var2);
            pk.b(var2, "RouteOverLay", "zoomToSpan()");
        }
    }

    public void zoomToSpan(int var1, AMapNaviPath var2) {
        if(var2 != null) {
            try {
                CameraUpdate var4 = CameraUpdateFactory.newLatLngBounds(var2.getBoundsForPath(), var1);
                this.aMap.animateCamera(var4, 1000L, (CancelableCallback)null);
            } catch (Throwable var3) {
                lu.a(var3);
                pk.b(var3, "RouteOverLay", "zoomToSpan()");
            }
        }
    }

    public void destroy() {
        try {
            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.remove();
            }

            this.mAMapNaviPath = null;
            if(this.arrowOnRoute != null) {
                this.arrowOnRoute.recycle();
            }

            if(this.smoothTraffic != null) {
                this.smoothTraffic.recycle();
            }

            if(this.unknownTraffic != null) {
                this.unknownTraffic.recycle();
            }

            if(this.slowTraffic != null) {
                this.slowTraffic.recycle();
            }

            if(this.jamTraffic != null) {
                this.jamTraffic.recycle();
            }

            if(this.veryJamTraffic != null) {
                this.veryJamTraffic.recycle();
            }

            if(this.startBitmap != null) {
                this.startBitmap.recycle();
            }

            if(this.endBitmap != null) {
                this.endBitmap.recycle();
            }

            if(this.wayBitmap != null) {
                this.wayBitmap.recycle();
            }

        } catch (Throwable var2) {
            lu.a(var2);
            pk.b(var2, "RouteOverLay", "destroy()");
        }
    }

    public void drawArrow(List<NaviLatLng> var1) {
        try {
            if(var1 == null) {
                this.naviArrow.setVisible(false);
            } else {
                int var2 = var1.size();
                ArrayList var6 = new ArrayList(var2);
                Iterator var5 = var1.iterator();

                while(var5.hasNext()) {
                    NaviLatLng var3 = (NaviLatLng)var5.next();
                    LatLng var7 = new LatLng(var3.getLatitude(), var3.getLongitude(), false);
                    var6.add(var7);
                }

                if(this.naviArrow == null) {
                    this.naviArrow = this.aMap.addNavigateArrow((new NavigateArrowOptions()).addAll(var6).topColor(this.arrowColor).width(this.mWidth * 0.4F));
                } else {
                    this.naviArrow.setPoints(var6);
                }

                this.naviArrow.setZIndex(1.0F);
                this.naviArrow.setVisible(true);
            }
        } catch (Throwable var4) {
            var4.printStackTrace();
            pk.b(var4, "RouteOverLay", "drawArrow(List<NaviLatLng> list) ");
        }
    }

    public List<NaviLatLng> getArrowPoints(int var1) {
        if(this.mAMapNaviPath == null) {
            return null;
        } else {
            try {
                if(var1 >= this.mAMapNaviPath.getStepsCount()) {
                    return null;
                }

                List var2;
                int var3 = (var2 = this.mAMapNaviPath.getCoordList()).size();
                var1 = ((AMapNaviStep)this.mAMapNaviPath.getSteps().get(var1)).getEndIndex();
                NaviLatLng var4 = (NaviLatLng)var2.get(var1);
                Vector var5 = new Vector();
                NaviLatLng var6 = var4;
                int var7 = 0;

                int var8;
                NaviLatLng var9;
                int var10;
                for(var8 = var1 - 1; var8 >= 0; --var8) {
                    var9 = (NaviLatLng)var2.get(var8);
                    var10 = lu.a(var6, var9);
                    if((var7 += var10) >= 50) {
                        var6 = lu.a(var6, var9, (double)(50 + var10 - var7));
                        var5.add(var6);
                        break;
                    }

                    var6 = var9;
                    var5.add(var9);
                }

                Collections.reverse(var5);
                var5.add(var4);
                var7 = 0;
                var6 = var4;

                for(var8 = var1 + 1; var8 < var3; ++var8) {
                    var9 = (NaviLatLng)var2.get(var8);
                    var10 = lu.a(var6, var9);
                    if((var7 += var10) >= 50) {
                        var6 = lu.a(var6, var9, (double)(50 + var10 - var7));
                        var5.add(var6);
                        break;
                    }

                    var6 = var9;
                    var5.add(var9);
                }

                if(var5.size() > 2) {
                    return var5;
                }
            } catch (Throwable var11) {
                var11.printStackTrace();
                pk.b(var11, "RouteOverLay", "getArrowPoints(int roadIndex)");
            }

            return null;
        }
    }

    public boolean isTrafficLine() {
        return this.isTrafficLine;
    }

    public void setTrafficLine(Boolean var1) {
        try {
            if(this.mContext != null) {
                this.isTrafficLine = var1.booleanValue();
                List var3 = null;
                this.clearTrafficLineAndInvisibleOriginalLine();
                if(this.isTrafficLine) {
                    if(this.mAMapNaviPath != null) {
                        var3 = this.mAMapNaviPath.getTrafficStatuses();
                    }

                    if(var3 != null && var3.size() != 0) {
                        this.colorWayUpdate(var3);
                    } else {
                        this.NoTrafficStatusDisplay();
                    }
                } else {
                    this.NoTrafficStatusDisplay();
                }
            }
        } catch (Throwable var2) {
            var2.printStackTrace();
            pk.b(var2, "RouteOverLay", "setTrafficLine(Boolean enabled)");
        }
    }

    private void NoTrafficStatusDisplay() {
        try {
            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.setVisible(true);
            }

            if(this.mCustomPolylines.size() > 0) {
                for(int var1 = 0; var1 < this.mCustomPolylines.size(); ++var1) {
                    if(this.mCustomPolylines.get(var1) != null) {
                        ((Polyline)this.mCustomPolylines.get(var1)).setVisible(true);
                    }
                }
            }

        } catch (Throwable var2) {
            var2.printStackTrace();
        }
    }

    public void addToMap(int[] var1, int[] var2, BitmapDescriptor[] var3) {
        try {
            if(this.aMap != null) {
                if(this.mDefaultPolyline != null) {
                    this.mDefaultPolyline.remove();
                    this.mDefaultPolyline = null;
                }

                if(this.mWidth != 0.0F && this.mAMapNaviPath != null && this.normalRoute != null) {
                    if(this.naviArrow != null) {
                        this.naviArrow.setVisible(false);
                    }

                    List var4;
                    if((var4 = this.mAMapNaviPath.getCoordList()) != null) {
                        this.clearTrafficLineAndInvisibleOriginalLine();
                        int var5 = var4.size();
                        this.mLatLngsOfPath = new ArrayList(var5);
                        ArrayList var18 = new ArrayList();
                        int var6 = 0;
                        int var8;
                        if(var1 == null) {
                            var8 = var3.length;
                        } else {
                            var8 = var1.length;
                        }

                        Polyline var19;
                        for(int var9 = 0; var9 < var8; ++var9) {
                            if(var2 == null || var9 >= var2.length || var2[var9] > 0) {
                                var18.clear();

                                while(var6 < var4.size()) {
                                    NaviLatLng var7 = (NaviLatLng)var4.get(var6);
                                    LatLng var10 = new LatLng(var7.getLatitude(), var7.getLongitude(), false);
                                    this.mLatLngsOfPath.add(var10);
                                    var18.add(var10);
                                    if(var2 != null && var9 < var2.length && var6 == var2[var9]) {
                                        break;
                                    }

                                    ++var6;
                                }

                                if(var3 != null && var3.length != 0) {
                                    var19 = this.aMap.addPolyline((new PolylineOptions()).addAll(var18).setCustomTexture(var3[var9]).width(this.mWidth));
                                } else {
                                    var19 = this.aMap.addPolyline((new PolylineOptions()).addAll(var18).color(var1[var9]).width(this.mWidth));
                                }

                                var19.setVisible(true);
                                this.mCustomPolylines.add(var19);
                            }
                        }

                        var19 = this.aMap.addPolyline((new PolylineOptions()).addAll(this.mLatLngsOfPath).width(this.mWidth).setCustomTexture(this.arrowOnRoute));
                        this.mCustomPolylines.add(var19);
                        LatLng var21 = null;
                        LatLng var20 = null;
                        List var22 = null;
                        if(this.mAMapNaviPath.getStartPoint() != null && this.mAMapNaviPath.getEndPoint() != null) {
                            var21 = new LatLng(this.mAMapNaviPath.getStartPoint().getLatitude(), this.mAMapNaviPath.getStartPoint().getLongitude());
                            var20 = new LatLng(this.mAMapNaviPath.getEndPoint().getLatitude(), this.mAMapNaviPath.getEndPoint().getLongitude());
                            var22 = this.mAMapNaviPath.getWayPoint();
                        }

                        if(this.startMarker != null) {
                            this.startMarker.remove();
                            this.startMarker = null;
                        }

                        if(this.endMarker != null) {
                            this.endMarker.remove();
                            this.endMarker = null;
                        }

                        int var12;
                        if(this.wayMarkers != null && this.wayMarkers.size() > 0) {
                            for(var12 = 0; var12 < this.wayMarkers.size(); ++var12) {
                                Marker var13;
                                if((var13 = (Marker)this.wayMarkers.get(var12)) != null) {
                                    var13.remove();
                                }
                            }
                        }

                        if(this.startBitmap == null) {
                            this.startMarker = this.aMap.addMarker((new MarkerOptions()).position(var21).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(lw.a(), 2130837857))));
                        } else if(this.startBitmapDescriptor != null) {
                            this.startMarker = this.aMap.addMarker((new MarkerOptions()).position(var21).icon(this.startBitmapDescriptor));
                        }

                        if(var22 != null && var22.size() > 0) {
                            var12 = var22.size();
                            if(this.wayMarkers == null) {
                                this.wayMarkers = new ArrayList(var12);
                            }

                            Marker var17;
                            for(Iterator var15 = var22.iterator(); var15.hasNext(); this.wayMarkers.add(var17)) {
                                NaviLatLng var14 = (NaviLatLng)var15.next();
                                LatLng var16 = new LatLng(var14.getLatitude(), var14.getLongitude());
                                var17 = null;
                                if(this.wayBitmap == null) {
                                    var17 = this.aMap.addMarker((new MarkerOptions()).position(var16).icon(this.wayPointBitmapDescriptor));
                                } else if(this.wayPointBitmapDescriptor != null) {
                                    var17 = this.aMap.addMarker((new MarkerOptions()).position(var16).icon(this.wayPointBitmapDescriptor));
                                }
                            }
                        }

                        if(this.endBitmap == null) {
                            this.endMarker = this.aMap.addMarker((new MarkerOptions()).position(var20).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(lw.a(), 2130837604))));
                        } else if(this.endBitmapDescriptor != null) {
                            this.endMarker = this.aMap.addMarker((new MarkerOptions()).position(var20).icon(this.endBitmapDescriptor));
                        }

                        if(this.isTrafficLine) {
                            this.setTrafficLine(Boolean.valueOf(this.isTrafficLine));
                        }

                    }
                }
            }
        } catch (Throwable var11) {
            lu.a(var11);
            pk.b(var11, "RouteOverLay", "addToMap(int[] color, int[] index, BitmapDescriptor[] resourceArray)");
        }
    }

    public void addToMap(int[] var1, int[] var2) {
        try {
            if(var1 != null && var1.length != 0) {
                this.addToMap(var1, var2, (BitmapDescriptor[])null);
            }
        } catch (Throwable var3) {
            var3.printStackTrace();
        }
    }

    public void addToMap(BitmapDescriptor[] var1, int[] var2) {
        try {
            if(var1 != null && var1.length != 0) {
                this.addToMap((int[])null, var2, var1);
            }
        } catch (Throwable var3) {
            var3.printStackTrace();
        }
    }

    public void setTransparency(float var1) {
        try {
            if(var1 < 0.0F) {
                var1 = 0.0F;
            } else if(var1 > 1.0F) {
                var1 = 1.0F;
            }

            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.setTransparency(var1);
            }

            Iterator var2 = this.mTrafficColorfulPolylines.iterator();

            while(var2.hasNext()) {
                ((Polyline)var2.next()).setTransparency(var1);
            }

        } catch (Throwable var3) {
            var3.printStackTrace();
        }
    }

    public void setZindex(int var1) {
        try {
            if(this.mTrafficColorfulPolylines != null) {
                for(int var2 = 0; var2 < this.mTrafficColorfulPolylines.size(); ++var2) {
                    ((Polyline)this.mTrafficColorfulPolylines.get(var2)).setZIndex((float)var1);
                }
            }

            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.setZIndex((float)var1);
            }

        } catch (Throwable var3) {
            var3.printStackTrace();
        }
    }

    public void handleLimitAndForbiddenInfos() {
        try {
            if(this.naviLimitOverlay != null) {
                this.naviLimitOverlay.removeAllMarker();
                if(this.mAMapNaviPath.getLimitInfos() != null) {
                    this.naviLimitOverlay.drawLimitInfo(this.mAMapNaviPath.getLimitInfos());
                }

                if(this.mAMapNaviPath.getForbiddenInfos() != null) {
                    this.naviLimitOverlay.drawForbiddenInfo(this.mAMapNaviPath.getForbiddenInfos());
                }
            }

        } catch (Throwable var1) {
            var1.printStackTrace();
        }
    }

    public void handlePassLimitAndForbidden(AMapNotAvoidInfo var1) {
        if(this.naviLimitOverlay != null && var1 != null) {
            this.naviLimitOverlay.handlePassLimitAndForbidden(var1);
        }

    }
}
