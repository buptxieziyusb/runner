package com.bupt.run.runningapp.runnerlib;//package cn.bupt.runningapp.runnerlib;
//
///**
// * Created by apple on 2018/3/22.
// */
//import android.content.Context;import android.util.Log;
//import com.amap.api.services.core.AMapException;
//import com.amap.api.services.help.Inputtips;
//import com.amap.api.services.help.Inputtips.InputtipsListener;
//import com.amap.api.services.help.Tip;
//import java.util.ArrayList;import java.util.List;
//
//import cn.bupt.runningapp.adapter.SearchAdapter;
//import cn.bupt.runningapp.entity.PositionEntity;
//
///**
// * ClassName:InputTipTask <br/>
// * Function: 简单封装了Inputtips的搜索服务，将其余提示的adapter进行数据绑定
// * @author   yiyi.qi   * @version     * @since    JDK 1.6   * @see  */
//public class InputTipTask implements InputtipsListener {
//    private static InputTipTask mInputTipTask;
//    private Inputtips mInputTips;
//    private SearchAdapter mAdapter;
//    Context mContext;
//    public static InputTipTask getInstance(Context context, SearchAdapter adapter){
//        if(mInputTipTask==null){
//            mInputTipTask=new InputTipTask(context);
//        }        //单例情况，多次进入DestinationActivity传进来的RecomandAdapter对象会不是同一个
//        mInputTipTask.setRecommandAdapter(adapter);
//        return mInputTipTask;
//    }
//    public void setRecommandAdapter(SearchAdapter adapter){
//        mAdapter=adapter;
//    }
//    private InputTipTask(Context context ){
//        mInputTips=new Inputtips(context, this);
//    }
//    public void searchTips(String keyWord, String city){
//        try {
//            mInputTips.requestInputtips(keyWord, city);
//        } catch (AMapException e) {
//            e.printStackTrace();
//        }
//    }
//    @Override
//    public void onGetInputtips(List<Tip> tips, int resultCode) {
//        //v3.2.1及以上版本SDK 返回码1000是正常 千万注意
//        if(resultCode==1000&&tips!=null){
//            ArrayList<PositionEntity> positions=new ArrayList<PositionEntity>();
//            for(Tip tip:tips){
//                //经纬度 address city(adcode)
//                positions.add(new PositionEntity(0, 0, tip.getName(),tip.getAdcode()));
//            }
//            mAdapter.setPositionEntities(positions);
//            mAdapter.notifyDataSetChanged();
//            PoiSearchTask poiSearchTask=new PoiSearchTask(mContext.getApplicationContext(), mAdapter);
//            for(int i = 0;i<positions.size();i++){
//                PositionEntity entity = (PositionEntity) mAdapter.getItem(i);
////                poiSearchTask.search(entity.address,RouteTask.getInstance(mContext.getApplicationContext()).getStartPoint().city);
//            }
//        }else {
//            //可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理
//        }
//    }
//}