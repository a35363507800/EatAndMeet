package com.echoesnet.eatandmeet.models.bean;

/**
 * @author Administrator
 * @Date 2017/9/16
 * @Version 1.0
 */

public class BigGiftItemBean
{
    private String giftName;
    private String giftUrl;
    private String giftCode;

    public String getGiftName()
    {
        return giftName;
    }

    public void setGiftName(String giftName)
    {
        this.giftName = giftName;
    }

    public String getGiftUrl()
    {
        return giftUrl;
    }

    public void setGiftUrl(String giftUrl)
    {
        this.giftUrl = giftUrl;
    }

    public String getGiftCode()
    {
        return giftCode;
    }

    public void setGiftCode(String giftCode)
    {
        this.giftCode = giftCode;
    }

    @Override
    public String toString()
    {
        return "BigGiftItemBean{" +
                "giftName='" + giftName + '\'' +
                ", giftUrl='" + giftUrl + '\'' +
                ", giftCode='" + giftCode + '\'' +
                '}';
    }
}
