package cn.bupt.runningapp.alert;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by yisic on 2017/7/8.
 */

abstract public class AlertableAppCompatActivity extends AppCompatActivity {
    public void alert(AlertMessage alertMessage) {
        new android.app.AlertDialog.Builder(this).setTitle(alertMessage.getTitle())
                .setMessage(alertMessage.getDetail())
                .show();
    }
}
