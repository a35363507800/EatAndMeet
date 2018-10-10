package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by liuchao on 2017/4/20 16.
 */

public class NewRechargeBean
{
    private String rechargeAmount; // 充值金额
    private String getAmount; // 可获得金额
    private String style; // 样式
    private boolean isSelect;


    public String getRechargeAmount()
    {
        return rechargeAmount;
    }

    public void setRechargeAmount(String rechargeAmount)
    {
        this.rechargeAmount = rechargeAmount;
    }

    public String getGetAmount()
    {
        return getAmount;
    }

    public void setGetAmount(String getAmount)
    {
        this.getAmount = getAmount;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public boolean isSelect()
    {
        return isSelect;
    }

    public void setSelect(boolean select)
    {
        isSelect = select;
    }

    @Override
    public String toString()
    {
        return "NewRechargeBean{" +
                "rechargeAmount='" + rechargeAmount + '\'' +
                ", getAmount='" + getAmount + '\'' +
                ", style='" + style + '\'' +
                '}';
    }
}
