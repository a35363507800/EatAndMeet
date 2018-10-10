package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by lc on 2017/7/13 16.
 */

public class LookAnchorBean
{
//                "age":"年龄",
//                "anchorLevel":"主播等级",
//                "fansNum":"粉丝数",
//                "focus":"是否关注 空：查看自己，0：未关注对方，1：已关注对方",
//                "focusNum":"关注数",
//                "id":"Id",  房间ID
//                "inWish":"是否在心愿单中，0：否，1：是",
//                "level":"用户等级",
//                "meal":"饭票数",
//                "nicName":"昵称",
//                "rmFlg":"是否身份认证，0：否，1：是",
//                "sex":"性别",
//                "uId":"uId",
//                "uphUrl":"头像"
//                 "isSayHello":"打招呼还是聊天（空：查看自己，0：聊天1：打招呼）",
    //             "inBlack":"1"是  "0"否；

    private String age;
    private String anchorLevel;
    private String fansNum;
    private String focus;
    private String id;
    private String focusNum;
    private String inWish;
    private String level;
    private String meal;
    private String nicName;
    private String rmFlg;
    private String uId;
    private String sex;
    private String uphUrl;
    private String isSayHello;
    private String inBlack;
    private String remark;
    private String imuId;
    private String isVuser;

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public String getImuId()
    {
        return imuId;
    }

    public void setImuId(String imuId)
    {
        this.imuId = imuId;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getInBlack()
    {
        return inBlack;
    }

    public void setInBlack(String inBlack)
    {
        this.inBlack = inBlack;
    }

    public String getIsSayHello()
    {
        return isSayHello;
    }

    public void setIsSayHello(String isSayHello)
    {
        this.isSayHello = isSayHello;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getAnchorLevel()
    {
        return anchorLevel;
    }

    public void setAnchorLevel(String anchorLevel)
    {
        this.anchorLevel = anchorLevel;
    }

    public String getFansNum()
    {
        return fansNum;
    }

    public void setFansNum(String fansNum)
    {
        this.fansNum = fansNum;
    }

    public String getFocus()
    {
        return focus;
    }

    public void setFocus(String focus)
    {
        this.focus = focus;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getFocusNum()
    {
        return focusNum;
    }

    public void setFocusNum(String focusNum)
    {
        this.focusNum = focusNum;
    }

    public String getInWish()
    {
        return inWish;
    }

    public void setInWish(String inWish)
    {
        this.inWish = inWish;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getMeal()
    {
        return meal;
    }

    public void setMeal(String meal)
    {
        this.meal = meal;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getRmFlg()
    {
        return rmFlg;
    }

    public void setRmFlg(String rmFlg)
    {
        this.rmFlg = rmFlg;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getUphUrl()
    {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl)
    {
        this.uphUrl = uphUrl;
    }

    @Override
    public String toString()
    {
        return "LookAnchorBean{" +
                "age='" + age + '\'' +
                ", anchorLevel='" + anchorLevel + '\'' +
                ", fansNum='" + fansNum + '\'' +
                ", focus='" + focus + '\'' +
                ", id='" + id + '\'' +
                ", focusNum='" + focusNum + '\'' +
                ", inWish='" + inWish + '\'' +
                ", level='" + level + '\'' +
                ", meal='" + meal + '\'' +
                ", nicName='" + nicName + '\'' +
                ", rmFlg='" + rmFlg + '\'' +
                ", uId='" + uId + '\'' +
                ", sex='" + sex + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", isSayHello='" + isSayHello + '\'' +
                ", inBlack='" + inBlack + '\'' +
                ", remark='" + remark + '\'' +
                ", imuId='" + imuId + '\'' +
                ", isVuser='" + isVuser + '\'' +
                '}';
    }
}
