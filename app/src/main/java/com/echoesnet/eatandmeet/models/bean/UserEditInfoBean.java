package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 */
public class UserEditInfoBean implements Serializable
{
    private String id;
    private String age;     // 年龄
    private String mobile;  // 用户手机号
    private String city;    // 用户城市
    private String addr;   // 用户地址
    private String nicName;    // 用户昵称
    private String sex;  // 用户性别(0:男，1：女)
    private String birth;    // 用户生日
    private String height;   // 用户身高
    private String constellation;     // 用户星座
    private String emState;  //  用户情感状况 (0：未，1：已)
    private String signature;     //  用户个性签名
    private String occupation;   // 用户职业
    private String income;  // 用户收入
    private String education;  // 用户学历
    private String uphUrl;     // 用户头像url
    private String upUrls;    // 用户相册urls
    private String uLab;      // 用户标签
    private String sign;      // 用户标签
    private String mealTotal; //累计饭票
    private String level; //累计饭票
    private String rmAnFlg; //支付宝认证
    private String rmFlg; //身份证认证

    public String getRmAnFlg()
    {
        return rmAnFlg;
    }

    public void setRmAnFlg(String rmAnFlg)
    {
        this.rmAnFlg = rmAnFlg;
    }

    public String getRmFlg()
    {
        return rmFlg;
    }

    public void setRmFlg(String rmFlg)
    {
        this.rmFlg = rmFlg;
    }

    private String whitherId; //最近想去餐厅ID
    private String whitherName; //最近想去餐厅名称
    private String whitherTime; //最近想去餐厅时间


    private String cmEducation;  // 择偶条件  用户学历
    private String cmIncome;  // 用户收入
    private String cmOccupation;     // 用户职业
    private String cmHeight;    // 用户身高
    private String cmCity;      // 用户城市


//    level("001", "1", "等级标识"),
//    liveSeq("002", "1", "直播间头像优先显示"),
//    barrage("003", "4", "直播间弹幕功能"),
//    roomAdmin("004", "4", "房管功能"),
//    dailyReward("005", "6", "每日小奖励"),
//    gift("006", "7", "特权礼物"),
//    evenWheat("007", "7", "连麦功能"),
//    customerService("008", "9", "专属客服"),
//    enterRoom("009", "10", "进房特效"),
//    groupRed("010", "12", "发送群红包功能"),
//    dateAnchor("011", "15", "与主播单独用餐");
    private List<String> privilege;


    public String getMealTotal()
    {
        return mealTotal;
    }

    public void setMealTotal(String mealTotal)
    {
        this.mealTotal = mealTotal;
    }

    public String getSign()
    {
        return sign;
    }

    public void setSign(String sign)
    {
        this.sign = sign;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getAddr()
    {
        return addr;
    }

    public void setAddr(String addr)
    {
        this.addr = addr;
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

    public String getBirth()
    {
        return birth;
    }

    public void setBirth(String birth)
    {
        this.birth = birth;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getConstellation()
    {
        return constellation;
    }

    public void setConstellation(String constellation)
    {
        this.constellation = constellation;
    }

    public String getEmState()
    {
        return emState;
    }

    public void setEmState(String emState)
    {
        this.emState = emState;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getOccupation()
    {
        return occupation;
    }

    public void setOccupation(String occupation)
    {
        this.occupation = occupation;
    }

    public String getIncome()
    {
        return income;
    }

    public void setIncome(String income)
    {
        this.income = income;
    }

    public String getEducation()
    {
        return education;
    }

    public void setEducation(String education)
    {
        this.education = education;
    }

    public String getUphUrl()
    {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl)
    {
        this.uphUrl = uphUrl;
    }

    public String getUpUrls()
    {
        return upUrls;
    }

    public void setUpUrls(String upUrls)
    {
        this.upUrls = upUrls;
    }

    public String getuLab()
    {
        return uLab;
    }

    public void setuLab(String uLab)
    {
        this.uLab = uLab;
    }

    public String getCmEducation()
    {
        return cmEducation;
    }

    public void setCmEducation(String cmEducation)
    {
        this.cmEducation = cmEducation;
    }

    public String getCmIncome()
    {
        return cmIncome;
    }

    public void setCmIncome(String cmIncome)
    {
        this.cmIncome = cmIncome;
    }

    public String getCmOccupation()
    {
        return cmOccupation;
    }

    public void setCmOccupation(String cmOccupation)
    {
        this.cmOccupation = cmOccupation;
    }

    public String getCmHeight()
    {
        return cmHeight;
    }

    public void setCmHeight(String cmHeight)
    {
        this.cmHeight = cmHeight;
    }

    public String getCmCity()
    {
        return cmCity;
    }

    public void setCmCity(String cmCity)
    {
        this.cmCity = cmCity;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getWhitherId()
    {
        return whitherId;
    }

    public void setWhitherId(String whitherId)
    {
        this.whitherId = whitherId;
    }

    public String getWhitherName()
    {
        return whitherName;
    }

    public void setWhitherName(String whitherName)
    {
        this.whitherName = whitherName;
    }

    public String getWhitherTime()
    {
        return whitherTime;
    }

    public void setWhitherTime(String whitherTime)
    {
        this.whitherTime = whitherTime;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public List<String> getPrivilege()
    {
        return privilege;
    }

    public void setPrivilege(List<String> privilege)
    {
        this.privilege = privilege;
    }

    @Override
    public String toString()
    {
        return "UserEditInfoBean{" +
                "id='" + id + '\'' +
                ", age='" + age + '\'' +
                ", mobile='" + mobile + '\'' +
                ", city='" + city + '\'' +
                ", addr='" + addr + '\'' +
                ", nicName='" + nicName + '\'' +
                ", sex='" + sex + '\'' +
                ", birth='" + birth + '\'' +
                ", height='" + height + '\'' +
                ", constellation='" + constellation + '\'' +
                ", emState='" + emState + '\'' +
                ", signature='" + signature + '\'' +
                ", occupation='" + occupation + '\'' +
                ", income='" + income + '\'' +
                ", education='" + education + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", upUrls='" + upUrls + '\'' +
                ", uLab='" + uLab + '\'' +
                ", sign='" + sign + '\'' +
                ", mealTotal='" + mealTotal + '\'' +
                ", level='" + level + '\'' +
                ", whitherId='" + whitherId + '\'' +
                ", whitherName='" + whitherName + '\'' +
                ", whitherTime='" + whitherTime + '\'' +
                ", cmEducation='" + cmEducation + '\'' +
                ", cmIncome='" + cmIncome + '\'' +
                ", cmOccupation='" + cmOccupation + '\'' +
                ", cmHeight='" + cmHeight + '\'' +
                ", cmCity='" + cmCity + '\'' +
                '}';
    }
}
