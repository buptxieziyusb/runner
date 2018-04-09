package cn.bupt.runningapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

import cn.bupt.runningapp.adapter.SearchAdapter;
import cn.bupt.runningapp.alert.AlertMessage;
import cn.bupt.runningapp.entity.PositionEntity;


/**
 * Created by apple on 2018/3/22.
 */

public class SearchPassingPointActivity extends AppCompatActivity implements Inputtips.InputtipsListener, TextWatcher {

    private String city;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_point);

        EditText editAddress = (EditText) findViewById(R.id.search_passing_point);
        editAddress.addTextChangedListener(this);


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
            SearchAdapter searchAdapter = new SearchAdapter(SearchPassingPointActivity.this,
                    R.layout.address_item, positionEntityArrayList);
            ListView listView = (ListView) findViewById(R.id.search_list);
            listView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    PositionEntity positionEntity = positionEntityArrayList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("latitue", positionEntity.latitue);
                    intent.putExtra("longitude", positionEntity.longitude);
                    intent.putExtra("address", positionEntity.address);
                    intent.putExtra("city", positionEntity.city);
                    setResult(RESULT_OK, intent);
                    finish();
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
}
