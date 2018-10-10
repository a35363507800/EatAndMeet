package com.echoesnet.eatandmeet.models.bean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2018/3/19
 * @description
 */
public class DateBaseInfoBean
{
    String date;
    String status;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "DateBaseInfoBean{" +
                "date='" + date + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
