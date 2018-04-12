package com.bupt.run.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.bupt.run.R;

import java.text.DateFormat;
import java.util.Date;

public class EndRunActivity extends AppCompatActivity {

    private TextView duringTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_run);

        Intent intent = getIntent();
        Date date = new Date(intent.getLongExtra("run_during", 0));
        duringTime = (TextView) findViewById(R.id.during_time);

        duringTime.setText(date.toString());
    }
}
