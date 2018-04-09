package cn.bupt.runningapp.uicomponent;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by sunli on 2017/7/5.
 */

public abstract class BaseDialog {
    protected Context context;
    private Display display;
    private Dialog dialog;


    protected abstract View getView();

    //构造方法 来实现 最基本的对话框
    public BaseDialog(Context context, int width, int height) {
        this.context = context;
        dialog = new Dialog(context);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();


        // 调整dialog背景大小
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(getView());

        //隐藏系统输入盘
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    //像这类设置对话框属性的方法，就返回值写自己，这样就可以一条链式设置了
    public BaseDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public BaseDialog setdismissListeren(DialogInterface.OnDismissListener dismissListener) {
        dialog.setOnDismissListener(dismissListener);
        return this;
    }
}