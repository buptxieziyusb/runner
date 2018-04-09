package cn.bupt.runningapp.uicomponent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.autonavi.ae.route.route.Route;

import cn.bupt.runningapp.R;
import cn.bupt.runningapp.struct.RouteData;


/**
 * Created by yisic on 2017/7/9.
 */

public class RouteSchematicDiagramLayout extends LinearLayout {
    private RouteSchematicDiagramLayout self = this;
    private MapView mapView;
    private TextView textView;
    private RouteData routeData;

    public RouteSchematicDiagramLayout(Context context, Bundle savedInstanceState) {
        super(context);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        inflater.inflate(R.layout.layout_route_schematic_diagram, this);
        mapView = (MapView) findViewById(R.id.route_schematic_map);
        textView = (TextView) findViewById(R.id.route_schematic_length_text_view);
        mapView.onCreate(savedInstanceState);
    }

    public MapView getMapView() {
        return mapView;
    }

    public TextView getTextView() {
        return textView;
    }

    public RouteData getRouteData() {
        return routeData;
    }

    public void setRouteData(RouteData routeData) {
        this.routeData = routeData;
    }

    public void chooseView(boolean choose) {
        if (choose) {
            this.setBackgroundColor(0xFF00FF00);
        } else {
            this.setBackgroundColor(0xFFFFFFFF);
        }
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        super.setOnClickListener(l);
        mapView.getMap().setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                self.callOnClick();
            }
        });
        textView.setOnClickListener(l);
    }
}
