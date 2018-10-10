package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/28.
 */

public class LiveEnterRoomBean implements Serializable
{
    /**
     * flag : 0
     * phUrl : http://huisheng.ufile.ucloud.cn/a_15620970743a358badc.jpg
     * status : 1
     * nicName : 156****0743
     * name : u100020
     * anph : http://huisheng.ufile.ucloud.cn/a_15620970743a358badc.jpg
     * uId : f43f7c11-059b-4b39-8df4-8ece93560fc5
     * ghost : []
     * hn : 的直播间
     * meal : 138630
     */
    private String flag;
    private String phUrl;
    private String status;
    private String nicName;
    private String name;
    private String anph;
    private String uId;   //主播的uId
    private String user;  //进入用户uId
    private String imuId;//环信id
    private String anchorImuId;//主播环信id
    private String hn;
    private String meal;
    private String isSignedAnchor;  // 签约主播 0不是 1是
    private List<?> ghost;
    private List<?> onWheat;//连麦用户id
    private String isAdmin;
    private String notification;  // 房管是否需要提示 0：无1：设置2：取消
    private String level; // 用户等级
    private String anchorLevel;//主播等级
    private String anchorSex;
    private String anchorAge;
    private String barrage; //用户弹幕开关状态
    private String remark; //主播备注
    private String hxRoomId;
    private String liveSource;//1:tencent;
    private String id;    //进房间用户id
    private String anchorId;//该房间主播的
    private String isVuser;//该房间主播的

    private String star;//主播当前拥有的星光值
    private String ranking;//主播当前排名，0：未上榜

    public String getStar()
    {
        return star;
    }

    public void setStar(String star)
    {
        this.star = star;
    }

    public String getRanking()
    {
        return ranking;
    }

    public void setRanking(String ranking)
    {
        this.ranking = ranking;
    }

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public String getFlag()
    {
        return flag;
    }

    public void setFlag(String flag)
    {
        this.flag = flag;
    }

    public String getPhUrl()
    {
        return phUrl;
    }

    public void setPhUrl(String phUrl)
    {
        this.phUrl = phUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAnph()
    {
        return anph;
    }

    public void setAnph(String anph)
    {
        this.anph = anph;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getImuId()
    {
        return imuId;
    }

    public void setImuId(String imuId)
    {
        this.imuId = imuId;
    }

    public String getAnchorImuId()
    {
        return anchorImuId;
    }

    public void setAnchorImuId(String anchorImuId)
    {
        this.anchorImuId = anchorImuId;
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

    public String getIsSignedAnchor()
    {
        return isSignedAnchor;
    }

    public void setIsSignedAnchor(String isSignedAnchor)
    {
        this.isSignedAnchor = isSignedAnchor;
    }

    public List<?> getGhost()
    {
        return ghost;
    }

    public void setGhost(List<?> ghost)
    {
        this.ghost = ghost;
    }

    public List<?> getOnWheat()
    {
        return onWheat;
    }

    public void setOnWheat(List<?> onWheat)
    {
        this.onWheat = onWheat;
    }

    public String getIsAdmin()
    {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin)
    {
        this.isAdmin = isAdmin;
    }

    public String getNotification()
    {
        return notification;
    }

    public void setNotification(String notification)
    {
        this.notification = notification;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getAnchorLevel()
    {
        return anchorLevel;
    }

    public void setAnchorLevel(String anchorLevel)
    {
        this.anchorLevel = anchorLevel;
    }

    public String getAnchorSex()
    {
        return anchorSex;
    }

    public void setAnchorSex(String anchorSex)
    {
        this.anchorSex = anchorSex;
    }

    public String getAnchorAge()
    {
        return anchorAge;
    }

    public void setAnchorAge(String anchorAge)
    {
        this.anchorAge = anchorAge;
    }

    public String getBarrage()
    {
        return barrage;
    }

    public void setBarrage(String barrage)
    {
        this.barrage = barrage;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getHxRoomId()
    {
        return hxRoomId;
    }

    public void setHxRoomId(String hxRoomId)
    {
        this.hxRoomId = hxRoomId;
    }

    public String getLiveSource()
    {
        return liveSource;
    }

    public void setLiveSource(String liveSource)
    {
        this.liveSource = liveSource;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAnchorId()
    {
        return anchorId;
    }

    public void setAnchorId(String anchorId)
    {
        this.anchorId = anchorId;
    }

    @Override
    public String toString()
    {
        return "LiveEnterRoomBean{" +
                "flag='" + flag + '\'' +
                ", phUrl='" + phUrl + '\'' +
                ", status='" + status + '\'' +
                ", nicName='" + nicName + '\'' +
                ", name='" + name + '\'' +
                ", anph='" + anph + '\'' +
                ", uId='" + uId + '\'' +
                ", imuId='" + imuId + '\'' +
                ", anchorImuId='" + anchorImuId + '\'' +
                ", hn='" + hn + '\'' +
                ", meal='" + meal + '\'' +
                ", isSignedAnchor='" + isSignedAnchor + '\'' +
                ", ghost=" + ghost +
                ", onWheat=" + onWheat +
                ", isAdmin='" + isAdmin + '\'' +
                ", notification='" + notification + '\'' +
                ", level='" + level + '\'' +
                ", anchorLevel='" + anchorLevel + '\'' +
                ", anchorSex='" + anchorSex + '\'' +
                ", anchorAge='" + anchorAge + '\'' +
                ", barrage='" + barrage + '\'' +
                ", user='" + user + '\'' +
                ", hxRoomId='" + hxRoomId + '\'' +
                ", liveSource='" + liveSource + '\'' +
                ", id='" + id + '\'' +
                ", anchorId='" + anchorId + '\'' +
                ", star='" + star + '\'' +
                ", ranking='" + ranking + '\'' +
                '}';
    }
}
