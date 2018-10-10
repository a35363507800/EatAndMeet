package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/8/11.
 */
public class MyInfoOrderRemindBean
{
    private String oId;
    private String tip;
    private String msg;
    private String date;

    public String getoId()
    {
        return oId;
    }

    public void setoId(String oId)
    {
        this.oId = oId;
    }

    public String getTip()
    {
        return tip;
    }

    public void setTip(String tip)
    {
        this.tip = tip;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}
