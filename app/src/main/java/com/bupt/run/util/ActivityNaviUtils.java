package com.bupt.run.util;

import android.content.Context;
import android.content.Intent;

import com.alibaba.idst.nls.internal.protocol.Content;
import com.bupt.run.activity.EndRunActivity;
import com.bupt.run.activity.MainActivity;
import com.bupt.run.activity.NaviActivity;

/**
 * Created by tanjie on 2018/4/4.
 */

public class ActivityNaviUtils {
    public static void gotoMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void gotoNavi(Context context) {
        Intent intent = new Intent(context, NaviActivity.class);
        context.startActivity(intent);
    }

    public static void gotoEndRun(Context context) {
        Intent intent = new Intent(context, EndRunActivity.class);
        context.startActivity(intent);
    }
}
