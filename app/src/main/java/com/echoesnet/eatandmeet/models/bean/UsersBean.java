package com.echoesnet.eatandmeet.models.bean;

import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangben on 2016/5/24.
 */
public class UsersBean implements Serializable
{
    //用户uId
    private String uId;
    /* 用户手机号 */
    private String mobile;
    /* 用户城市*/
    private String city;
    /* 用户昵称 */
    private String nicName;
    //性别
    private String sex;
    /* 用户生日*/
    private String birth;
    /* 用户身高 */
    private String height;
    /* 用户星座 */
    private String constellation;
    /* 用户情感状况 */
    private String emState;
    /* 用户个性签名 */
    private String signature;
    /* 用户职业 */
    private String occupation;
    /* 用户收入 */
    private String income;
    /* 用户学历 */
    private String education;
    //头像url
    private String uphUrl;
    /* 用户相册urls(array) */
    private String upUrls;
    /* 用户标签 */
    private String uLab;
    /* 环信账号 */
    private String imuId;
    //环信密码
    private String imPass;
    //邀请码
    private String inCode;
    //头衔
    private String title;
    //年龄
    private String age="18";
    //备注名称
    private String remark;

    //是否在直播
    private String living="0";
    //是否是幽灵用户
    private String ghost;

    private String whitherId;//最近要去餐厅ID
    private String whitherName;//最近要去餐厅名称
    private String whitherTime;//最近要去餐厅时间

    private String rPreId;//上次出现过餐厅ID
    private String rPreName;//上次出现过餐厅名称

    private String level;

    //纬度
    private String posx ="39";
    //经度
    private String posy ="117";
    //用户的状态 0：没有直播；1：直播
    private String status="0";
    //看脸吃饭用户的id，也是直播的roomId
    private String id;
    //饭票
    private String meal;
    //累计饭票
    private String mealTotal;

    private String phurl;

    public String getPhurl()
    {
        return phurl;
    }

    public void setPhurl(String phurl)
    {
        this.phurl = phurl;
    }

    public UsersBean()
    {
    }

    public List<String> getImgUrls()
    {
        return CommonUtils.strWithSeparatorToList(getUpUrls(),CommonUtils.SEPARATOR);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getMealTotal()
    {
        return mealTotal;
    }

    public void setMealTotal(String mealTotal)
    {
        this.mealTotal = mealTotal;
    }

    public String getBirth()
    {
        return birth;
    }

    public void setBirth(String birth)
    {
        this.birth = birth;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getInCode()
    {
        return inCode;
    }

    public void setInCode(String inCode)
    {
        this.inCode = inCode;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
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

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getConstellation()
    {
        return constellation;
    }

    public void setConstellation(String constellation)
    {
        this.constellation = constellation;
    }

    public String getEducation()
    {
        return education;
    }

    public void setEducation(String education)
    {
        this.education = education;
    }

    public String getEmState()
    {
        return emState;
    }

    public void setEmState(String emState)
    {
        this.emState = emState;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getImuId()
    {
        return imuId;
    }

    public void setImuId(String imuId)
    {
        this.imuId = imuId;
    }

    public String getImPass()
    {
        return imPass;
    }

    public void setImPass(String imPass)
    {
        this.imPass = imPass;
    }

    public String getIncome()
    {
        return income;
    }

    public void setIncome(String income)
    {
        this.income = income;
    }

    public String getOccupation()
    {
        return occupation;
    }

    public void setOccupation(String occupation)
    {
        this.occupation = occupation;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getuLab()
    {
        return uLab;
    }

    public void setuLab(String uLab)
    {
        this.uLab = uLab;
    }

    public String getUpUrls()
    {
        return upUrls;
    }

    public void setUpUrls(String upUrls)
    {
        this.upUrls = upUrls;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getLiving()
    {
        return living;
    }

    public void setLiving(String living)
    {
        this.living = living;
    }

    public String getGhost()
    {
        return ghost;
    }

    public void setGhost(String ghost)
    {
        this.ghost = ghost;
    }
    public String getPosx()
    {
        return posx;
    }

    public void setPosx(String posx)
    {
        this.posx = posx;
    }

    public String getPosy()
    {
        return posy;
    }

    public void setPosy(String posy)
    {
        this.posy = posy;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getMeal()
    {
        return meal;
    }

    public void setMeal(String meal)
    {
        this.meal = meal;
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

    public String getrPreId()
    {
        return rPreId;
    }

    public void setrPreId(String rPreId)
    {
        this.rPreId = rPreId;
    }

    public String getrPreName()
    {
        return rPreName;
    }

    public void setrPreName(String rPreName)
    {
        this.rPreName = rPreName;
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
        return "UsersBean{" +
                "uId='" + uId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", city='" + city + '\'' +
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
                ", imuId='" + imuId + '\'' +
                ", inCode='" + inCode + '\'' +
                ", title='" + title + '\'' +
                ", age='" + age + '\'' +
                ", remark='" + remark + '\'' +
                ", living='" + living + '\'' +
                ", ghost='" + ghost + '\'' +
                ", whitherId='" + whitherId + '\'' +
                ", whitherName='" + whitherName + '\'' +
                ", whitherTime='" + whitherTime + '\'' +
                ", rPreId='" + rPreId + '\'' +
                ", rPreName='" + rPreName + '\'' +
                ", level='" + level + '\'' +
                ", posx='" + posx + '\'' +
                ", posy='" + posy + '\'' +
                ", status='" + status + '\'' +
                ", id='" + id + '\'' +
                ", meal='" + meal + '\'' +
                ", mealTotal='" + mealTotal + '\'' +
                '}';
    }
}
