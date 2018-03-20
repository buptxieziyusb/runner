package com.bupt.run.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.bupt.run.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RouteSearch.OnRouteSearchListener, GeocodeSearch.OnGeocodeSearchListener {
    private MapView mapView = null;
    private AMap aMap;
    private UiSettings mUiSettings;
    private MyLocationStyle myLocationStyle;
    private RouteSearch routeSearch;
    private GeocodeSearch geocodeSearch;
    private WalkRouteResult walkRouteResult;


    private Button startNaviButton;

    private List<LatLonPoint> passPoints = new ArrayList<LatLonPoint>();
    private List<String> testNames = new ArrayList<String>();
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        initView();
        initMap(savedInstanceState);
    }

    //初始化视图控件
    private void initView() {
        startNaviButton = (Button)findViewById(R.id.start_navi_button);
/*
        startNaviButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NaviActivity.class);
                startActivity(intent);
            }
        });
        */
        startNaviButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testNames.clear();
                passPoints.clear();
                index = 0;
                testNames.add("北京市海淀区西土城路10号");
                testNames.add("北京市海淀区西土城路地铁站");
                testNames.add("北京市牡丹园地铁站");
                testNames.add("北京市海淀区北京师范大学");

                getLatlon(testNames.get(0));
            }
        });
    }

    //初始化地图
    private  void initMap(Bundle savedInstanceState) {
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        mUiSettings = aMap.getUiSettings();

        //定位蓝点
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        // myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style

        mUiSettings.setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);

        CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(17);

        //开启室内地图
        aMap.showIndoorMap(true);

        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);


    }

   /*
    private void generateRoute(List<String> pointNames) {
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);

        LatLonPoint startPoint = new LatLonPoint();
    }*/

    private void generateRoute(final RouteSearch.FromAndTo fromAndTo) {
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);

        WalkRouteQuery query = new WalkRouteQuery(fromAndTo);
        routeSearch.calculateWalkRouteAsyn(query);
        //LatLonPoint startPoint = new LatLonPoint(39.959628, 116.358077);
    }

    private void getLatlon(final String name) {
        GeocodeQuery query = new GeocodeQuery(name, "北京");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocodeSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int i) {
        /*if (i == 1000) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                walkRouteResult = result;
                WalkPath walkPath = walkRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
            }
        }
        else showToast("路线规划失败");*/
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (i == 1000) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null && geocodeResult.getGeocodeAddressList().size() > 0) {
                LatLonPoint point = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
                showToast(point + "");
                passPoints.add(point);
                try {
                    getLatlon(testNames.get(++index));
                } catch (IndexOutOfBoundsException e) {
                    showToast("查询完毕");

                    Intent intent = new Intent(MainActivity.this, NaviActivity.class);
                    for (int j = 0; j < passPoints.size(); j++) {
                        intent.putExtra("point" + j, passPoints.get(j));
                    }
                    startActivity(intent);
                  //  generateRoute();
                }
            }
        }
        else showToast("地址错误");
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

}
