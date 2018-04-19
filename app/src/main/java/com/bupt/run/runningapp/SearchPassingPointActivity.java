package com.bupt.run.runningapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.model.AmapCarLocation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.bupt.run.R;
import com.bupt.run.runningapp.adapter.SearchAdapter;
import com.bupt.run.runningapp.alert.AlertMessage;
import com.bupt.run.runningapp.alert.AlertableAppCompatActivity;
import com.bupt.run.runningapp.entity.PositionEntity;
import com.bupt.run.runningapp.tools.LengthBalance;
import com.bupt.run.runningapp.tools.MapScale;


/**
 * Created by apple on 2018/3/22.
 */

public class SearchPassingPointActivity extends AlertableAppCompatActivity implements
        Inputtips.InputtipsListener, TextWatcher, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener{

    private String city;
    private double longitude;
    private double latitude;
    private String address;
    private double passLng = -1.0;
    private double passLat = -1.0;
    private String passAddress = "";
    private String passCity = "";
    private double length;
    private GeocodeSearch geocodeSearch;

    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            int errorType = aMapLocation.getErrorCode();
            if(errorType == 4){
                alert(new AlertMessage("错误", "请检查设备网络是否通畅!"));
            }else if(errorType == 12){
                alert(new AlertMessage("错误", "缺少定位权!"));
            }else if(errorType == 13){
                alert(new AlertMessage("错误", "建议开启WIFI，或者插入一张" +
                        "可以正常工作的SIM卡，或者检查GPS是否开启!"));
            }else if(errorType == 14){
                alert(new AlertMessage("错误", "GPS 定位失败，建议持设备到相对开阔的露天场" +
                        "所再次尝试!"));
            }else if(errorType == 18){
                alert(new AlertMessage("错误", "定位失败，请关闭飞行模式！"));
            }else if(errorType == 19){
                alert(new AlertMessage("错误", "定位失败，建议手机插上sim卡，打开WIFI开关"));
            }else if(errorType == 0) {
                address = aMapLocation.getAddress();
                latitude = aMapLocation.getLatitude();
                longitude = aMapLocation.getLongitude();
                AMap map = ((MapView) findViewById(R.id.passing_point_circle)).getMap();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longitude));
                markerOptions.draggable(false);
                Marker marker = map.addMarker(markerOptions);
                marker.showInfoWindow();
                marker.setClickable(true);
                map.addCircle(new CircleOptions().
                        center(new LatLng(latitude, longitude)).
                        radius(LengthBalance.lengthBalance(length) / Math.PI).
                        fillColor(Color.argb(20, 1, 1, 1)).
                        strokeColor(Color.argb(20, 1, 1, 1)).
                        strokeWidth(15));
                map.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latitude, longitude)));
                map.moveCamera(CameraUpdateFactory.zoomTo(MapScale.getScale((int)length)));
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setAllGesturesEnabled(true);
            }else{
                alert(new AlertMessage("错误", "GPS定位失败，请重试！"));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_point);
        if(!getIntent().getStringExtra("length").equals(""))
            length = Integer.parseInt(getIntent().getStringExtra("length"));
        else
            length = 0.0;

        ((MapView) findViewById(R.id.passing_point_circle)).onCreate(savedInstanceState);
        ((Button) findViewById(R.id.ack_passing_point)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passCity.equals("") || passAddress.equals("") || passLat == -1.0 || passLng == -1.0){
                    alert(new AlertMessage("错误", "请输入途经点！"));
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("latitue", passLat);
                    intent.putExtra("longitude", passLng);
                    intent.putExtra("address", passAddress);
                    intent.putExtra("city", passCity);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });
        EditText editAddress = (EditText) findViewById(R.id.search_passing_point);
        editAddress.addTextChangedListener(this);

        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        aMapLocationClientOption.setLocationMode(
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        aMapLocationClientOption.setOnceLocation(true);
//        aMapLocationClientOption.setOnceLocationLatest(true);

        AMapLocationClient aMapLocationClient = new AMapLocationClient(this);
        aMapLocationClient.setLocationListener(aMapLocationListener);
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        aMapLocationClient.startLocation();
        ((MapView) findViewById(R.id.passing_point_circle)).getMap().setOnMapClickListener(this);
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);

    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int resultCode) {
        if(tipList != null){
            final ArrayList<PositionEntity> positionEntityArrayList = new ArrayList<>();
            for(Tip tip: tipList){
                if(tip.getAddress() != "")
                    positionEntityArrayList.add(new PositionEntity(tip.getPoint().getLatitude(),
                        tip.getPoint().getLongitude(), tip.getAddress(), tip.getDistrict()));
            }
            final SearchAdapter searchAdapter = new SearchAdapter(SearchPassingPointActivity.this,
                    R.layout.address_item, positionEntityArrayList);
            final ListView listView = (ListView) findViewById(R.id.search_list);
            listView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    PositionEntity positionEntity = positionEntityArrayList.get(position);
                    passAddress = positionEntity.address;
                    passCity = positionEntity.city;
                    passLat = positionEntity.latitue;
                    passLng = positionEntity.longitude;
//                    intent.putExtra("latitue", positionEntity.latitue);
//                    intent.putExtra("longitude", positionEntity.longitude);
//                    intent.putExtra("address", positionEntity.address);
//                    intent.putExtra("city", positionEntity.city);
                    positionEntityArrayList.removeAll(positionEntityArrayList);
                    searchAdapter.notifyDataSetChanged();
                    listView.setAdapter(searchAdapter);
                    ((EditText) findViewById(R.id.search_passing_point)).setText("");
                    MarkerOptions passPoint = new MarkerOptions();
                    passPoint.position(new LatLng(positionEntity.latitue, positionEntity.longitude));
                    passPoint.draggable(false);
                    ((MapView) findViewById(R.id.passing_point_circle)).getMap().addMarker(passPoint).setClickable(true);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                    Toast.makeText(SearchPassingPointActivity.this, positionEntity.address,
//                            Toast.LENGTH_LONG).show();
                }
            });
        }else{
            new AlertMessage("title", "无法定位您的途经点,请重新输入！");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence passingPoint, int i, int i1, int i2) {
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        InputtipsQuery inputtipsQuery = new InputtipsQuery(passingPoint.toString(), "北京");
        inputtipsQuery.setCityLimit(true);

        Inputtips inputtips = new Inputtips(SearchPassingPointActivity.this, inputtipsQuery);
        inputtips.setInputtipsListener(this);
        inputtips.requestInputtipsAsyn();

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(passLat != -1.0 || passLng != -1.0)
            ((MapView) findViewById(R.id.passing_point_circle)).getMap().getMapScreenMarkers().get(1).remove();
        passLat = latLng.latitude;
        passLng = latLng.longitude;
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(passLat, passLng), 200, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(false);
        ((MapView) findViewById(R.id.passing_point_circle)).getMap().addMarker(markerOptions).setClickable(true);
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        //解析result获取地址描述信息
        RegeocodeAddress passingAddress = regeocodeResult.getRegeocodeAddress();
        passCity = passingAddress.getCity();
        passAddress = passingAddress.getFormatAddress();

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
