package com.bupt.run.runningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bupt.run.R;
import com.bupt.run.activity.NaviActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bupt.run.runningapp.alert.AlertMessage;
import com.bupt.run.runningapp.alert.AlertableAppCompatActivity;
import com.bupt.run.runningapp.appserver.model.Merchant;
import com.bupt.run.runningapp.runnerlib.RouteGenerate;
import com.bupt.run.runningapp.struct.RouteData;
import com.bupt.run.runningapp.uicomponent.MerchantInfoDialog;
import com.bupt.run.runningapp.uicomponent.RouteSchematicDiagramLayout;
import com.bupt.run.runningapp.tools.MapScale;

public class RouteSelectActivity extends AlertableAppCompatActivity {
    private RouteSelectActivity self = this;
    private Bundle savedInstanceState;
    private MapView mainMapView;
    private LinearLayout routeSchemaDiagramContainer;
    private ArrayList<RouteSchematicDiagramLayout> routeSchematicDiagramList = new ArrayList<RouteSchematicDiagramLayout>();
    private RouteSchematicDiagramLayout selectedDiagram = null;

    //data for search_view_bg route
    private int length;
    private double longitude;
    private double latitude;
    private double passingPointLat;
    private double passingPointLng;
    private List<LatLng> passPoints = null;

    //marker-merchant map
    private HashMap<Marker, Merchant> markerMerchantHashMap = new HashMap<Marker, Merchant>();

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_navi_button:
                    if (selectedDiagram == null) {
                        alert(new AlertMessage("错误", "请先选择路线再点击开始"));
                    } else {
                        if (passPoints.size() < 2) {
                            alert(new AlertMessage("错误", "路线规划失败"));
                        }
                        Intent intent = new Intent();
                       // intent.putExtra("route_data", new Gson().toJson(selectedDiagram.getRouteData()));
                        for (int j = 0; j < passPoints.size(); j++) {
                            intent.putExtra("point" + j, passPoints.get(j));
                        }
                        intent.setClass(self, NaviActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                default:
                    for (RouteSchematicDiagramLayout diagram : routeSchematicDiagramList) {
                        if (v == diagram || v == diagram || v == diagram.getTextView()) {
                            for (RouteSchematicDiagramLayout d : routeSchematicDiagramList)
                                d.chooseView(false);
                            diagram.chooseView(true);
                            RouteData routeDataToDraw = diagram.getRouteData();
                            mainMapView.getMap().clear();
                            //start/stop marker
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(routeDataToDraw.keyPoints.get(0));
                            markerOptions.draggable(false);
                            markerOptions.title("开始/终止点");
                            mainMapView.getMap().addMarker(markerOptions);

                            //merchant marker
                            for (int i = 0; i < routeDataToDraw.isMerchant.size(); i++) {
                                if (routeDataToDraw.isMerchant.get(i)) {
                                    markerOptions = new MarkerOptions();
                                    markerOptions.position(routeDataToDraw.keyPoints.get(i));
                                    markerOptions.draggable(false);
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shop_logo));
                                    Marker marker = mainMapView.getMap().addMarker(markerOptions);
                                    markerMerchantHashMap.put(marker, routeDataToDraw.merchantInfo.get(i));
                                }
                            }
                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.setPoints(routeDataToDraw.ployPoints);
                            passPoints = routeDataToDraw.keyPoints;
                            mainMapView.getMap().addPolyline(polylineOptions);
                            mainMapView.getMap().moveCamera(CameraUpdateFactory.changeLatLng(routeDataToDraw.centerPoint));
                            mainMapView.getMap().moveCamera(CameraUpdateFactory.zoomTo(MapScale.getScale((int)diagram.getRouteData().length)));
                            selectedDiagram = diagram;
                            break;
                        }
                    }
                    break;
            }
        }
    };

    private AMap.OnMarkerClickListener onMarkerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Merchant merchant = markerMerchantHashMap.get(marker);
            if (merchant != null) {
                new MerchantInfoDialog(self, merchant).show();
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_select);
        this.savedInstanceState = savedInstanceState;

        //get data for search_view_bg route
        length = Integer.parseInt(getIntent().getStringExtra("length"));
        longitude = getIntent().getDoubleExtra("lng", 0);
        latitude = getIntent().getDoubleExtra("lat", 0);
        passingPointLat = getIntent().getDoubleExtra("passingPointLat", -1);
        passingPointLng = getIntent().getDoubleExtra("passingPointLng", -1);

        this.setTitle("选择您喜欢的路线");

        //init ui
        ((Button) findViewById(R.id.start_navi_button)).setOnClickListener(onClickListener);
        this.mainMapView = (MapView) findViewById(R.id.main_map);
        this.routeSchemaDiagramContainer = (LinearLayout) findViewById(R.id.route_schema_diagram_container);
        this.mainMapView.onCreate(savedInstanceState);
        mainMapView.getMap().setOnMarkerClickListener(onMarkerClickListener);
        if(passingPointLng != -1 && passingPointLat != -1){
            searchRoute(passingPointLat, passingPointLng);
        }else{
            searchRoute();
        }
    }

    public void addSchematicDiagram(RouteSchematicDiagramLayout routeSchematicDiagramLayout) {
        this.routeSchematicDiagramList.add(routeSchematicDiagramLayout);
        this.routeSchemaDiagramContainer.addView(routeSchematicDiagramLayout);
        routeSchematicDiagramLayout.setOnClickListener(onClickListener);
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    private void searchRoute() {
        RouteGenerate.generateRoute(
                new LatLng(latitude, longitude),
                length,
                RouteGenerate.NORTH,
                self
        );
        RouteGenerate.generateRoute(
                new LatLng(latitude, longitude),
                length,
                RouteGenerate.SOUTH,
                self
        );
        RouteGenerate.generateRoute(
                new LatLng(latitude, longitude),
                length,
                RouteGenerate.WEST,
                self
        );
        RouteGenerate.generateRoute(
                new LatLng(latitude, longitude),
                length,
                RouteGenerate.EAST,
                self
        );
        RouteGenerate.generateRouteWithMerchant(
                new LatLng(latitude, longitude),
                length,
                RouteGenerate.NORTH,
                self
        );
        RouteGenerate.generateRouteWithMerchant(
                new LatLng(latitude, longitude),
                length,
                RouteGenerate.SOUTH,
                self
        );
        RouteGenerate.generateRouteWithMerchant(
                new LatLng(latitude, longitude),
                length,
                RouteGenerate.WEST,
                self
        );
        RouteGenerate.generateRouteWithMerchant(
                new LatLng(latitude, longitude),
                length,
                RouteGenerate.EAST,
                self
        );
    }

    private void searchRoute(double lat, double lng){
        RouteGenerate.generateRouteWithPassingPoint(
                new LatLng(latitude, longitude),
                new LatLng(lat, lng),
                length,
                RouteGenerate.NORTH,
                self
        );
        RouteGenerate.generateRouteWithPassingPoint(
                new LatLng(latitude, longitude),
                new LatLng(lat, lng),
                length,
                RouteGenerate.SOUTH,
                self
        );
        RouteGenerate.passingPointRouteWithMerchant(
                new LatLng(latitude, longitude),
                new LatLng(lat, lng),
                length,
                RouteGenerate.NORTH,
                self
        );
        RouteGenerate.passingPointRouteWithMerchant(
                new LatLng(latitude, longitude),
                new LatLng(lat, lng),
                length,
                RouteGenerate.NORTH,
                self
        );
    }
}
