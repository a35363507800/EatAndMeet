package com.echoesnet.eatandmeet.models.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * @author an
 * @Description: 主播列表bean
 * @time 2016/10/13 0013 14:08
 * modified by ben at 2016/12/13 override equals and hashCode() function
 */

public class LAnchorsListBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String uId;
    private String nicName;
    private String sex;
    private String age;
    private String uphUrl;
    private String status;
    private String roomName;
    private String id;
    private String roomUrl;
    private String viewer;
    private String sign;
    private String focus;/*是否关注该主播 0：否，1：是*/
    private String meal;
    private String level;
    private String vedio;
    private String roomId;
    private String hxRoomId;
    private String city;
    private String anchorTypeUrl;//主播类型图标url
    private String anchorType;///*主播类型*/ 0全民 1 自由 2兼职 3全职
    private String isVuser;//大V标识


    public String getViewer()
    {
        return viewer;
    }

    public void setViewer(String viewer)
    {
        this.viewer = viewer;
    }

    public String getFocus()
    {
        return focus;
    }

    public void setFocus(String focus)
    {
        this.focus = focus;
    }

    public String getMeal()
    {
        return meal;
    }

    public void setMeal(String meal)
    {
        this.meal = meal;
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

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getUphUrl()
    {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl)
    {
        this.uphUrl = uphUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getRoomUrl()
    {
        return roomUrl;
    }

    public void setRoomUrl(String roomUrl)
    {
        this.roomUrl = roomUrl;
    }

    public String getSign()
    {
        return sign;
    }

    public void setSign(String sign)
    {
        this.sign = sign;
    }

    public String getVedio()
    {
        return vedio;
    }

    public void setVedio(String vedio)
    {
        this.vedio = vedio;
    }

    public String getAnchorType()
    {
        return anchorType;
    }

    public void setAnchorType(String anchorType)
    {
        this.anchorType = anchorType;
    }

    public String getAnchorTypeUrl()
    {
        return anchorTypeUrl;
    }

    public void setAnchorTypeUrl(String anchorTypeUrl)
    {
        this.anchorTypeUrl = anchorTypeUrl;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getHxRoomId()
    {
        return hxRoomId;
    }

    public void setHxRoomId(String hxRoomId)
    {
        this.hxRoomId = hxRoomId;
    }

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    @Override
    public String toString()
    {
        return "LAnchorsListBean{" +
                "uId='" + uId + '\'' +
                ", nicName='" + nicName + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", status='" + status + '\'' +
                ", roomName='" + roomName + '\'' +
                ", id='" + id + '\'' +
                ", roomUrl='" + roomUrl + '\'' +
                ", viewer='" + viewer + '\'' +
                ", sign='" + sign + '\'' +
                ", focus='" + focus + '\'' +
                ", meal='" + meal + '\'' +
                ", level='" + level + '\'' +
                ", vedio='" + vedio + '\'' +
                ", roomId='" + roomId + '\'' +
                ", hxRoomId='" + hxRoomId + '\'' +
                ", city='" + city + '\'' +
                ", anchorTypeUrl='" + anchorTypeUrl + '\'' +
                ", anchorType='" + anchorType + '\'' +
                ", isVuser='" + isVuser + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof LAnchorsListBean))
            return false;
        LAnchorsListBean bean = (LAnchorsListBean) o;
        if (!TextUtils.isEmpty(this.getuId()) && !TextUtils.isEmpty(bean.getuId()))
        {
            return this.getuId().equals(bean.getuId());
        }
        else
        {
            return false;
        }
    }

    public String getRoomId()
    {
        return roomId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 17;
        result = PRIME * result + uId.hashCode();
        return result;
    }

}
