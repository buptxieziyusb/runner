package cn.bupt.runningapp.uicomponent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.bupt.runningapp.R;
import cn.bupt.runningapp.appserver.model.GiftTicket;
import cn.bupt.runningapp.appserver.service.ServiceGenerator;

/**
 * Created by yisic on 2017/7/10.
 */

public class GiftTicketItemAdapter extends ArrayAdapter<GiftTicket> {
    private int resourceId;

    public GiftTicketItemAdapter(Context context, int textViewResourceId, List<GiftTicket> objects) {
        super(context, textViewResourceId, objects);
        //拿取到子项布局ID
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GiftTicket giftTicket = getItem(position);
        //为子项动态加载布局
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ((TextView) view.findViewById(R.id.gift_name)).setText(giftTicket.getGift().getMerchantName() + "：" + giftTicket.getGift().getName());
        ((TextView) view.findViewById(R.id.gift_description)).setText(giftTicket.getGift().getDescription());
        ((TextView) view.findViewById(R.id.gift_code)).setText("代码：" + giftTicket.getCode());
        String a = ServiceGenerator.API_BASE_URL.substring(0, ServiceGenerator.API_BASE_URL.length() - 5) + (String) giftTicket.getGift().getPic();
        ((SimpleDraweeView) view.findViewById(R.id.gift_pic)).setImageURI(ServiceGenerator.API_BASE_URL.substring(0, ServiceGenerator.API_BASE_URL.length() - 5) + (String) giftTicket.getGift().getPic());
        if (giftTicket.getUsed()) {
            (view.findViewById(R.id.pic_gift_used)).setVisibility(View.VISIBLE);
        }
        return view;
    }

}
