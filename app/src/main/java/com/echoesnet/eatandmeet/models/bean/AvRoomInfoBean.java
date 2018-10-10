package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/28.
 */

public class AvRoomInfoBean
{
    //主播腾讯用户名
    private String name;
    //主播Uid
    private String uId;
    //直播间名称
    private String hn;
    //饭票数目
    private String meal;
    /*主播头像*/
    private String phUrl;
    /*房间封面图片*/
    private String anph;
    /*是否关注1是0否*/
    private String flag;
    private String nicName;
    private String status;//直播状态
    private String level;//主播等级
    //直播间是否是签约直播0不是、1是(签约主播)
    private String roomSigned;
    //进入房间的用户是否签约主播0不是、1是(签约主播)
    private String isSignedAnchor;
    private List<LivePlayUserBean> ghost;

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getAnph()
    {
        return anph;
    }

    public void setAnph(String anph)
    {
        this.anph = anph;
    }

    public String getFlag()
    {
        return flag;
    }

    public void setFlag(String flag)
    {
        this.flag = flag;
    }

    public List<LivePlayUserBean> getGhost()
    {
        return ghost;
    }

    public void setGhost(List<LivePlayUserBean> ghost)
    {
        this.ghost = ghost;
    }

    public String getHn()
    {
        return hn;
    }

    public void setHn(String hn)
    {
        this.hn = hn;
    }

    public String getMeal()
    {
        return meal;
    }

    public void setMeal(String meal)
    {
        this.meal = meal;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhUrl()
    {
        return phUrl;
    }

    public void setPhUrl(String phUrl)
    {
        this.phUrl = phUrl;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getIsSignedAnchor()
    {
        return isSignedAnchor;
    }

    public void setIsSignedAnchor(String isSignedAnchor)
    {
        this.isSignedAnchor = isSignedAnchor;
    }

    public String getRoomSigned()
    {
        return roomSigned;
    }

    public void setRoomSigned(String roomSigned)
    {
        this.roomSigned = roomSigned;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    @Override
    public String toString()
    {
        return "AvRoomInfoBean{" +
                "name='" + name + '\'' +
                ", uId='" + uId + '\'' +
                ", hn='" + hn + '\'' +
                ", meal='" + meal + '\'' +
                ", phUrl='" + phUrl + '\'' +
                ", anph='" + anph + '\'' +
                ", flag='" + flag + '\'' +
                ", nicName='" + nicName + '\'' +
                ", status='" + status + '\'' +
                ", level='" + level + '\'' +
                ", roomSigned='" + roomSigned + '\'' +
                ", isSignedAnchor='" + isSignedAnchor + '\'' +
                ", ghost=" + ghost +
                '}';
    }
}
