package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by wangben on 2016/5/25.
 */
public class MeetPersonBean
{
    // ======== 设置新属性 ========
    private String luId; // 邂逅人uId
    private String upUrl; // 邂逅人头像url
    private String nicName; // 邂逅人昵称
    private String city; // 邂逅人城市
    private String flag; //是否隐藏信息
    private String age; // 邂逅人年龄
    private String rAddr; // 邂逅人预约地
    private String rName; // 邂逅人预约地
    private String oTime; // 邂逅人预约时间
//    private String rPreAddr; // 邂逅人曾经预订过的餐厅， 多个
    private String rPreName; // 邂逅人曾经预订过的餐厅， 多个
    private String status; // 直播状态 0:未直播，1：直播中



    private String isLove = "1";
    private String sex = "男";

    public MeetPersonBean()
    {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFlag()
    {
        return flag;
    }

    public void setFlag(String flag)
    {
        this.flag = flag;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getIsLove()
    {
        return isLove;
    }

    public void setIsLove(String isLove)
    {
        this.isLove = isLove;
    }


    public String getLuId()
    {
        return luId;
    }

    public void setLuId(String luId)
    {
        this.luId = luId;
    }

    public String getUpUrl()
    {
        return upUrl;
    }

    public void setUpUrl(String upUrl)
    {
        this.upUrl = upUrl;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getrAddr()
    {
        return rAddr;
    }

    public void setrAddr(String rAddr)
    {
        this.rAddr = rAddr;
    }

    public String getoTime()
    {
        return oTime;
    }

    public void setoTime(String oTime)
    {
        this.oTime = oTime;
    }

   /* public String getrPreAddr()
    {
        return rPreAddr;
    }

    public void setrPreAddr(String rPreAddr)
    {
        this.rPreAddr = rPreAddr;
    }*/

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getrName()
    {
        return rName;
    }

    public void setrName(String rName)
    {
        this.rName = rName;
    }

    public String getrPreName()
    {
        return rPreName;
    }

    public void setrPreName(String rPreName)
    {
        this.rPreName = rPreName;
    }

    @Override
    public String toString() {
        return "MeetPersonBean{" +
                "luId='" + luId + '\'' +
                ", upUrl='" + upUrl + '\'' +
                ", nicName='" + nicName + '\'' +
                ", city='" + city + '\'' +
                ", flag='" + flag + '\'' +
                ", age='" + age + '\'' +
                ", rAddr='" + rAddr + '\'' +
                ", rName='" + rName + '\'' +
                ", oTime='" + oTime + '\'' +
                ", rPreName='" + rPreName + '\'' +
                ", status='" + status + '\'' +
                ", isLove='" + isLove + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
