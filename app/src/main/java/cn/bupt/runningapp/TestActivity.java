package cn.bupt.runningapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

import cn.bupt.runningapp.alert.AlertMessage;
import cn.bupt.runningapp.alert.AlertableAppCompatActivity;
import cn.bupt.runningapp.appserver.asyn.AppServerAsynHandler;
import cn.bupt.runningapp.appserver.asyn.RunnerAsyn;
import cn.bupt.runningapp.appserver.model.GiftTicket;
import cn.bupt.runningapp.appserver.model.Merchant;
import retrofit2.Call;
import retrofit2.Response;

public class TestActivity extends AlertableAppCompatActivity {
    private TestActivity self = this;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.test_get_gift_button:
                    testGetGift();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ((Button) findViewById(R.id.test_get_gift_button)).setOnClickListener(onClickListener);
    }

    private void testGetGift() {
        Merchant merchant = new Merchant();
        merchant.setId(8);
        Call<GiftTicket> giftTicketRequireCall = RunnerAsyn.RunnerService.giftTicketRequire(merchant);
        RunnerAsyn.AppServerAsyn(giftTicketRequireCall, self, new AppServerAsynHandler<GiftTicket>() {
                    @Override
                    public void handler(Response<GiftTicket> response) throws Exception {
                        GiftTicket giftTicket = response.body();
                    }
                },
                new AlertMessage("系统错误", "请重启app"),
                new AlertMessage("网络错误", "请检查网络连接")
        );
    }
}
