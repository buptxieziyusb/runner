package com.bupt.run.runningapp.alert;

/**
 * Created by yisic on 2017/7/8.
 */

public class AlertMessage {
    private String title;
    private String detail;

    public AlertMessage(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }
}
