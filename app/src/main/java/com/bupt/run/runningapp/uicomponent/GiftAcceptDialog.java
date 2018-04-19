package com.bupt.run.runningapp.uicomponent;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.bupt.run.R;
import com.bupt.run.runningapp.appserver.model.GiftTicket;
import com.bupt.run.runningapp.appserver.service.ServiceGenerator;

/**
 * Created by yisic on 2017/7/10.
 */

public class GiftAcceptDialog {
    //base dialog
    protected Context context;
    private Dialog dialog;
    private Display display;

    //merchant info dialog
    private TextView acceptTextView;
    private GiftTicket giftTicket;

    public GiftAcceptDialog(Context context, GiftTicket giftTicket) {
        this.context = context;
        dialog = new Dialog(context);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        this.giftTicket = giftTicket;

        // 调整dialog背景大小
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(getView());

        //隐藏系统输入盘
        dialog.getWindow().setLayout(800, 900);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }

    protected View getView() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_gift_ticket_accept, null);

        ((TextView) view.findViewById(R.id.accepted_gift_name_text_view)).setText(giftTicket.getGift().getName());
        ((TextView) view.findViewById(R.id.accepted_gift_description_text_view)).setText(giftTicket.getGift().getDescription());
//        ((TextView) view.findViewById(R.id.accepted_gift_code_text_view)).setText(giftTicket.getCode());
        ((SimpleDraweeView) view.findViewById(R.id.accepted_gift_pic)).setImageURI(ServiceGenerator.API_BASE_URL.substring(0, ServiceGenerator.API_BASE_URL.length() - 5) + (String) giftTicket.getGift().getPic());
        acceptTextView = (TextView) view.findViewById(R.id.accept_text_view);

        initViewEvent();
        return view;
    }

    private void initViewEvent() {
        acceptTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}
