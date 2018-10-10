package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2017/1/3.
 */

public class ExchangeRecordDetailBean
{
    private String balance;
    private String date;
    private String meal;

    public String getBalance()
    {
        return balance;
    }

    public void setBalance(String balance)
    {
        this.balance = balance;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getMeal()
    {
        return meal;
    }

    public void setMeal(String meal)
    {
        this.meal = meal;
    }

    @Override
    public String toString()
    {
        return "ExchangeRecordDetailBean{" +
                "balance='" + balance + '\'' +
                ", date='" + date + '\'' +
                ", meal='" + meal + '\'' +
                '}';
    }
}
