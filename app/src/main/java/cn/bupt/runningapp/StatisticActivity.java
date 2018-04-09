package cn.bupt.runningapp;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import cn.bupt.runningapp.alert.AlertMessage;
import cn.bupt.runningapp.alert.AlertableAppCompatActivity;
import cn.bupt.runningapp.appserver.asyn.AppServerAsynHandler;
import cn.bupt.runningapp.appserver.asyn.RunnerAsyn;
import cn.bupt.runningapp.appserver.model.User;
import cn.bupt.runningapp.appserver.model.UserDetail;
import cn.bupt.runningapp.struct.ServerSessionInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticActivity extends AlertableAppCompatActivity {
    private StatisticActivity self = this;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_button_circular:
                case R.id.button_start_run_text:
                    startActivity(new Intent(self, RunningPreparationActivity.class));
                    break;
                default:
                    break;
            }
        }
    };

    private OnTabSelectListener onTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelected(@IdRes int tabId) {
            if (tabId == R.id.tab_person) {
                startActivity(new Intent(self, UserInfoActivity.class));
                finish();
                overridePendingTransition(0, 0);
            } else if (tabId == R.id.tab_history) {
                startActivity(new Intent(self, HistoryListActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        ((BottomBar) findViewById(R.id.bottomBar)).setDefaultTab(R.id.tab_run);
        ((BottomBar) findViewById(R.id.bottomBar)).setOnTabSelectListener(onTabSelectListener);
        ((ImageView) findViewById(R.id.start_button_circular)).setOnClickListener(onClickListener);
        ((TextView) findViewById(R.id.button_start_run_text)).setOnClickListener(onClickListener);


        updateStatisticData();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatisticData();
    }

    private void updateStatisticData() {
        Call<UserDetail> myInfoCall = RunnerAsyn.RunnerService.myInfo();
        RunnerAsyn.AppServerAsyn(myInfoCall, self, new AppServerAsynHandler<UserDetail>() {
                    @Override
                    public void handler(Response<UserDetail> response) throws Exception {
                        UserDetail userDetail = response.body();
                        ((TextView) findViewById(R.id.total_length_text_view)).setText("" + new java.text.DecimalFormat("######0.0").format(userDetail.getTotalLength()));
                        ((TextView) findViewById(R.id.total_day_text_view)).setText("" + userDetail.getTotalDay());
                        ((TextView) findViewById(R.id.total_time_text_view)).setText("" + userDetail.getTotalTime() / 60);
                        ((TextView) findViewById(R.id.total_calories_txt)).setText("" + new java.text.DecimalFormat("######0.0").format(userDetail.getTotal_energy_consume()));
                    }
                },
                new AlertMessage("系统错误", "请重启app"),
                new AlertMessage("网络错误", "请检查网络连接")
        );
    }
}
