package com.bupt.run.runningapp.uicomponent;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.bupt.run.R;
import com.bupt.run.runningapp.appserver.model.Merchant;
import com.bupt.run.runningapp.appserver.service.ServiceGenerator;

/**
 * Created by sunli on 2017/7/7.
 */

public class MerchantInfoDialog {
    //base dialog
    protected Context context;
    private Display display;
    private Dialog dialog;

    //merchant info dialog
    private ImageView close_button;
    private Merchant merchant;

    public MerchantInfoDialog(Context context, Merchant merchant) {
        this.context = context;
        dialog = new Dialog(context);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        this.merchant = merchant;

        // 调整dialog背景大小
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(getView());

        //隐藏系统输入盘
        dialog.getWindow().setLayout(800, 900);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }

    protected View getView() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_merchant_detail, null);

        ((TextView) view.findViewById(R.id.merchant_name)).setText(merchant.getName());
        ((TextView) view.findViewById(R.id.merchant_description)).setText(merchant.getDescription());
        ((SimpleDraweeView) view.findViewById(R.id.merchant_pic)).setImageURI(ServiceGenerator.API_BASE_URL.substring(0, ServiceGenerator.API_BASE_URL.length() - 5) + (String) merchant.getPic());

        close_button = (ImageView) view.findViewById(R.id.img_close_btn);

        initViewEvent();
        return view;
    }


    private void initViewEvent() {
        close_button.setOnClickListener(new View.OnClickListener() {
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
