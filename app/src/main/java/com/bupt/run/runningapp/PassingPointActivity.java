package com.bupt.run.runningapp;//package cn.bupt.runningapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AutoCompleteTextView;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//import android.widget.TextView;
//
//import com.amap.api.services.core.AMapException;
//import com.amap.api.services.help.Inputtips;
//import com.amap.api.services.help.InputtipsQuery;
//import com.amap.api.services.help.Tip;
//import com.amap.api.services.poisearch.PoiSearch;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import cn.bupt.runningapp.adapter.SearchAdapter;
//import cn.bupt.runningapp.alert.AlertableAppCompatActivity;
//import cn.bupt.runningapp.runnerlib.PoiSearchTask;
//
///**
// * Created by apple on 2018/3/22.
// */
//
//public class PassingPointActivity extends AlertableAppCompatActivity implements View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {
//
//    //    private AutoCompleteTextView autotext;
////    private ArrayAdapter<String> arrayAdapter;
//    private AutoCompleteTextView passing_point_text;
//    private ListView search_list;
//    private String city;
//    private TextView search_for_place;
//    private SearchAdapter searchAdapter;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_point);
//
//
////        autotext =(AutoCompleteTextView) findViewById(R.id.passing_point);
////        String [] arr={"aa","aab","aac"};
////        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arr);
////        autotext.setAdapter(arrayAdapter);
//
//        passing_point_text = (AutoCompleteTextView) findViewById(R.id.passing_point);
//        search_list = (ListView) findViewById(R.id.search_list);
//        search_for_place= (TextView) findViewById(R.id.search_for_place);
//        passing_point_text.addTextChangedListener(this);
//        search_list.setOnItemClickListener(this);
//        search_for_place.setOnClickListener(this);
////        city_tv.setText(sendCityInfo());//设置tv显示的城市信息
//        city=sendCityInfo();//将前面的定位城市信息传入作为搜索参数
//
//
//
//    }
//
//    public String sendCityInfo(){//将前面定位数据中的city数据传过来
//        String info;//前面定位所在城市信息
//        Intent intent=this.getIntent();
//        info=intent.getStringExtra("city");
//        return info;
//    }
//
//    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        String content=s.toString().trim();//获取自动提示输入框的内容
//        InputtipsQuery inputtipsQuery=new InputtipsQuery(content,city);//初始化一个输入提示搜索对象，并传入参数
//        inputtipsQuery.setCityLimit(true);//将获取到的结果进行城市限制筛选
//        Inputtips inputtips=new Inputtips(this,inputtipsQuery);//定义一个输入提示对象，传入当前上下文和搜索对象
//        inputtips.setInputtipsListener((Inputtips.InputtipsListener) this);//设置输入提示查询的监听，实现输入提示的监听方法onGetInputtips()
//        inputtips.requestInputtipsAsyn();//输入查询提示的异步接口实现
//
//    }
//
//    public void afterTextChanged(Editable s) {
//
//    }
//
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//    }
//
//    @Override
//    public void onClick(View view) {
//
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////        PositionEntity entity = (PositionEntity) searchAdapter.getItem(position);
////        if (entity.latitue == 0 && entity.longitude == 0) {
////            PoiSearchTask poiSearchTask=new PoiSearchTask(getApplicationContext(), searchAdapter);
////            poiSearchTask.search(entity.address,RouteTask.getInstance(getApplicationContext()).getStartPoint().city);
////
////        } else {
//////            mRouteTask.setEndPoint(entity);
//////            mRouteTask.search();
////            Intent intent = new Intent(POISearchActivity.this, MainMenuActivity.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
////            startActivity(intent);
////            finish();
////        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.search_for_place:
//                PoiSearchTask poiSearchTask=new PoiSearchTask(getApplicationContext(), searchAdapter);
//                poiSearchTask.search(passing_point_text.getText().toString(),RouteTask.getInstance(getApplicationContext()).getStartPoint().city);
//                break;
//        }
//    }
//
//    @Override
//    /*
//     输入提示的回调方法
//     参数1：提示列表
//     参数2：返回码
//     */
//    public void onGetInputtips(List<Tip> list, int returnCode) {
//        if(returnCode== AMapException.CODE_AMAP_SUCCESS){//如果输入提示搜索成功
//            List<HashMap<String,String>> searchList=new ArrayList<HashMap<String, String>>() ;
//            for (int i=0;i<list.size();i++){
//                HashMap<String,String> hashMap=new HashMap<String, String>();
//                hashMap.put("name",list.get(i).getName());
//                hashMap.put("address",list.get(i).getDistrict());//将地址信息取出放入HashMap中
//                searchList.add(hashMap);//将HashMap放入表中
//
//            }
//            searchAdapter=new SearchAdapter(this);//新建一个适配器
//            search_list.setAdapter(searchAdapter);//为listview适配
//            SimpleAdapter aAdapter = new SimpleAdapter(getApplicationContext(), searchList, R.layout.address_item,
//                    new String[] {"name","address"}, new int[] {R.id.search_item_title, R.id.search_item_text});
//
//            search_list.setAdapter(aAdapter);
//            aAdapter.notifyDataSetChanged();//动态更新listview
//
//
//        }else{
//            ToastUtil.show(this,returnCode);
//
//        }
//
//    }
//}
