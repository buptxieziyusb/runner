package com.bupt.run.runningapp.struct;

import java.util.Date;

import com.bupt.run.runningapp.appserver.model.ReadRoute;

/**
 * Created by yisic on 2017/7/9.
 */

public class HistoryItem {
    private Date StartTime;
    private String RouteLengthText;
    private ReadRoute readRoute;

    public ReadRoute getReadRoute() {
        return readRoute;
    }

    public void setReadRoute(ReadRoute readRoute) {
        this.readRoute = readRoute;
    }


    public HistoryItem(Date StartTime, String RouteLengthText, ReadRoute readRoute) {
        this.RouteLengthText = RouteLengthText;
        this.StartTime = StartTime;
        this.readRoute = readRoute;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTimeText(Date startTime) {
        StartTime = startTime;
    }

    public String getRouteLengthText() {
        return RouteLengthText;
    }

    public void setRouteLengthText(String routeLengthText) {
        RouteLengthText = routeLengthText;
    }
}
