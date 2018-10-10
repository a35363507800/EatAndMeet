package com.echoesnet.eatandmeet.models.datamodel;

/**
 * Created by Administrator on 2016/5/26.
 */
public class TimePeriodModel
{
    private String timeStr;
    private String status;//"0不可选；1可选；2选中"

    public TimePeriodModel()
    {
    }

    public TimePeriodModel(String status, String timeStr)
    {
        this.status = status;
        this.timeStr = timeStr;
    }

    @Override
    public String toString()
    {
        return "TimePeriodModel{" +
                "status='" + status + '\'' +
                ", timeStr='" + timeStr + '\'' +
                '}';
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getTimeStr()
    {
        return timeStr;
    }

    public void setTimeStr(String timeStr)
    {
        this.timeStr = timeStr;
    }
}
