package com.echoesnet.eatandmeet.models.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 直播礼物bean
 * Created by an on 2016/10/24 0024.
 */

public class GiftBean
{
    private String gId;
    private String gName;
    private String gPrice;
    private String gUrl;
    private String gType;/*是否大礼物，0：否，1是*/

    @SerializedName("left")
    private String gcount = "0"; //用户拥有数量

    private String isPrivilege;
    private String authority;
    private String sort;
    private String isActivity;//是否活动礼物 0否，1是

    public String getIsActivity()
    {
        return isActivity;
    }

    public void setIsActivity(String isActivity)
    {
        this.isActivity = isActivity;
    }

    public int getCountTotal()
    {
        return countTotal;
    }

    public void setCountTotal(int countTotal)
    {
        this.countTotal = countTotal;
    }

    private int countTotal;

    public String getIsPrivilege()
    {
        return isPrivilege;
    }

    public void setIsPrivilege(String isPrivilege)
    {
        this.isPrivilege = isPrivilege;
    }

    public String getGcount()
    {
        return gcount;
    }

    public void setGcount(String gcount)
    {
        this.gcount = gcount;
    }

    public String getgType()
    {
        return gType;
    }

    public void setgType(String gType)
    {
        this.gType = gType;
    }

    public String getgId()
    {
        return gId;
    }

    public void setgId(String gId)
    {
        this.gId = gId;
    }

    public String getgName()
    {
        return gName;
    }

    public void setgName(String gName)
    {
        this.gName = gName;
    }

    public String getgPrice()
    {
        return gPrice;
    }

    public void setgPrice(String gPrice)
    {
        this.gPrice = gPrice;
    }

    public String getgUrl()
    {
        return gUrl;
    }

    public void setgUrl(String gUrl)
    {
        this.gUrl = gUrl;
    }

    public String getAuthority()
    {
        return authority;
    }

    public void setAuthority(String authority)
    {
        this.authority = authority;
    }

    public String getSort()
    {
        return sort;
    }

    public void setSort(String sort)
    {
        this.sort = sort;
    }

    @Override
    public String toString()
    {
        return "LGiftbean{" +
                "gId='" + gId + '\'' +
                ", gName='" + gName + '\'' +
                ", gPrice='" + gPrice + '\'' +
                ", gUrl='" + gUrl + '\'' +
                ", gType='" + gType + '\'' +
                ", gcount='" + gcount + '\'' +
                ", authority='" + authority + '\'' +
                ", sort='" + sort + '\'' +

                '}';
    }
}
