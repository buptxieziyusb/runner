package com.bupt.run.runningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;

import com.bupt.run.R;
import com.bupt.run.runningapp.alert.AlertMessage;
import com.bupt.run.runningapp.alert.AlertableAppCompatActivity;
import com.bupt.run.runningapp.entity.PositionEntity;
import com.bupt.run.runningapp.runnerlib.LatLngCalculate;

public class RunningPreparationActivity extends AlertableAppCompatActivity {


    private RunningPreparationActivity self = this;
    private double longitude;
    private double latitude;
    private String address;
    private PositionEntity passingPoint;

    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            ((Button) findViewById(R.id.route_choose_Button)).setEnabled(true);
            ((TextView) findViewById(R.id.locate_text_view)).setText(aMapLocation.getAddress());
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
                AMap map = ((MapView) findViewById(R.id.locate_map)).getMap();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longitude));
                markerOptions.draggable(false);
                markerOptions.title("您的位置");
                Marker marker = map.addMarker(markerOptions);
                marker.showInfoWindow();
                marker.setClickable(false);
                map.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latitude, longitude)));
                map.moveCamera(CameraUpdateFactory.zoomTo(16));
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(false);
            }else{
                alert(new AlertMessage("错误", "GPS定位失败，请重试！"));
            }
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.route_choose_Button:
                    try {
                        int a = Integer.parseInt(((EditText) findViewById(R.id.length_edit_text))
                                .getText().toString());
                        if(a < 1000 || a > 13000){
                            alert(new AlertMessage("跑步距离不应该小于1km，或者超过13km",""));
                            break;
                        }
                    } catch (Exception e) {
                        alert(new AlertMessage("请输入正确的跑步路程长度", ""));
                        break;
                    }
                    ((Button) findViewById(R.id.route_choose_Button)).setEnabled(false);
                    ((Button) findViewById(R.id.route_choose_Button)).setText("路线生成中");
                    Intent intent = new Intent();
                    intent.putExtra("length", ((EditText) findViewById(
                            R.id.length_edit_text)).getText().toString());
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng", longitude);
                    intent.putExtra("address", address);
                    if(passingPoint != null){
                        double ppLat = passingPoint.latitue;
                        double ppLng = passingPoint.longitude;
                        intent.putExtra("passingPointLat", ppLat);
                        intent.putExtra("passingPointLng", ppLng);
                        double distance = LatLngCalculate.getDistance(latitude, longitude,
                                ppLat, ppLng);
                        if(distance * 2 > Integer.parseInt(((EditText) findViewById(
                                R.id.length_edit_text)).getText().toString())){
                            alert(new AlertMessage("抱歉，该途经点的往返路程超过您的跑步路程长度" +
                                    "请修改跑步长度或者途经点", ""));
                            ((Button) findViewById(R.id.route_choose_Button)).setEnabled(true);
                            ((Button) findViewById(R.id.route_choose_Button)).setText("开始路线规划");
                            break;
                        }
                    }
                    intent.setClass(self, RouteSelectActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.passing_point:
                    Intent intent1 = new Intent();
                    intent1.putExtra("length", ((EditText) findViewById(
                            R.id.length_edit_text)).getText().toString());
                    intent1.setClass(self, SearchPassingPointActivity.class);
                    startActivityForResult(intent1, 1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    String passingAddress = intent.getStringExtra("address");
                    double passingLatitue = intent.getDoubleExtra("latitue", 0.0);
                    double passingLongtude = intent.getDoubleExtra("longitude", 0.0);
                    String distinct = intent.getStringExtra("city");
                    TextView position = (TextView) findViewById(R.id.passing_point);
                    position.setText(passingAddress);
                    passingPoint = new PositionEntity(passingLatitue, passingLongtude, passingAddress, distinct);
                }
                break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_preparation);

        ((TextView) findViewById(R.id.passing_point)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.route_choose_Button)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.route_choose_Button)).setEnabled(false);

        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        aMapLocationClientOption.setLocationMode(
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        aMapLocationClientOption.setOnceLocation(true);
//        aMapLocationClientOption.setOnceLocationLatest(true);

        AMapLocationClient aMapLocationClient = new AMapLocationClient(this);
        aMapLocationClient.setLocationListener(aMapLocationListener);
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        aMapLocationClient.startLocation();

        ((MapView) findViewById(R.id.locate_map)).onCreate(savedInstanceState);

        ((EditText) findViewById(R.id.length_edit_text)).setInputType(EditorInfo.TYPE_CLASS_PHONE);

    }
}
