package com.bupt.run.runningapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.bupt.run.R;
import com.bupt.run.runningapp.alert.AlertMessage;
import com.bupt.run.runningapp.alert.AlertableAppCompatActivity;
import com.bupt.run.runningapp.appserver.asyn.AppServerAsynHandler;
import com.bupt.run.runningapp.appserver.asyn.RunnerAsyn;
import com.bupt.run.runningapp.appserver.model.Gift;
import com.bupt.run.runningapp.appserver.model.GiftTicket;
import com.bupt.run.runningapp.uicomponent.GiftTicketItemAdapter;
import retrofit2.Call;
import retrofit2.Response;

public class GiftListActivity extends AlertableAppCompatActivity {
    GiftListActivity self = this;
    ListView giftListView;
    public int ListItemNumbers;
    public List<GiftTicket> items = new ArrayList<GiftTicket>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_list);

        giftListView = (ListView) findViewById(R.id.gift_list_view);

        getGiftTicketFromAppServer();
    }

    private void getGiftTicketFromAppServer() {
        Call<List<GiftTicket>> obtainedGiftsCall = RunnerAsyn.RunnerService.obtainedGifts();
        RunnerAsyn.AppServerAsyn(obtainedGiftsCall, self, new AppServerAsynHandler<List<GiftTicket>>() {
                    @Override
                    public void handler(Response<List<GiftTicket>> response) throws Exception {
                        List<GiftTicket> giftTickets = response.body();
                        for (int i = giftTickets.size() - 1; i >= 0; i--) {
                            if (!giftTickets.get(i).getUsed())
                                items.add(giftTickets.get(i));
                        }
                        for (int i = giftTickets.size() - 1; i >= 0; i--) {
                            if (giftTickets.get(i).getUsed())
                                items.add(giftTickets.get(i));
                        }
                        GiftTicketItemAdapter adapter = new GiftTicketItemAdapter(self, R.layout.layout_gift_ticket_item_adapter, items);
                        giftListView.setAdapter(adapter);

                        giftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                GiftTicket giftTicket = items.get(position);
                                // 加入跳转代码，position 指的是第几行的数据
                            }
                        });
                    }
                },
                new AlertMessage("系统错误", "请重启app"),
                new AlertMessage("网络错误", "请检查网络连接")
        );

    }
}
