package com.echoesnet.eatandmeet.models.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

/**
 * Created by ben on 2016/10/25.
 */

public class AudienceBean implements Comparable<AudienceBean>
{
    private final static String TAG = AudienceBean.class.getSimpleName();

    private String identifier;//注意这个id赋值为我们系统的id，它是个6位数字！！！--wb
    private String faceUrl;//头像
    private String nicName;
    private String isGhost = "0"; //真用户：0  假用户: 1
    private String level = "0"; // 等级
    private String imId;//IM ID :如果是腾讯就是腾讯id，如果是环信就是环信id
    private String uid;

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    private String isVuser; //大V用户

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getImId()
    {
        return imId;
    }

    public void setImId(String imId)
    {
        this.imId = imId;
    }

    public String getFaceUrl()
    {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl)
    {
        this.faceUrl = faceUrl;
    }

    public String getIdentifier()
    {
        return identifier.replace("u","");//防止一些二货传入腾讯的id，此处需要的是我们自己的id，是6位数字
    }

    public void setIdentifier(String identifier)
    {

        this.identifier = identifier.replace("u","");
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getIsGhost()
    {
        return isGhost;
    }

    public void setIsGhost(String isGhost)
    {
        this.isGhost = isGhost;
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
        return "AudienceBean{" +
                "identifier='" + identifier + '\'' +
                ", faceUrl='" + faceUrl + '\'' +
                ", nicName='" + nicName + '\'' +
                ", isGhost='" + isGhost + '\'' +
                ", level='" + level + '\'' +
                ", imId='" + imId + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    //不要随便改动这个方法--wb
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AudienceBean that = (AudienceBean) o;
        if (!TextUtils.isEmpty(identifier) && !TextUtils.isEmpty(that.identifier))
            return identifier.equals(that.identifier);
        return false;
    }

    @Override
    public int hashCode()
    {
        return identifier != null ? identifier.hashCode() : 0;
    }


    @Override
    public int compareTo(@NonNull AudienceBean bean)
    {
        int thisLevel = 0;
        int thatLevel = 0;
        try
        {
            thisLevel = Integer.parseInt(this.level);
            thatLevel = Integer.parseInt(bean.getLevel());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            Logger.t(TAG).e("等级格式不正确》" + bean.getLevel());
        }
        int thisStatus = "1".equals(this.isGhost) ? 0 : 1;//如果是假用户就设置状态为0，真为1
        int thatStatus = "1".equals(bean.getIsGhost()) ? 0 : 1;
        int result1 = thatStatus - thisStatus;
        int result2 = thatLevel - thisLevel;
        if (result1 > 0)
            return 1;
        else if (result1 < 0)
            return -1;
        else
        {
            return result2;
        }
    }
}
