package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by liuchao on 2017/7/12 15.
 */

public class SysNewsMessageListBean
{
    private String messageId;
    private String desc;
    private String createTime;
    private UserBean user;
    private String tip;
    private String unread;
    private String orderId;

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public UserBean getUser()
    {
        return user;
    }

    public void setUser(UserBean user)
    {
        this.user = user;
    }

    public String getTip()
    {
        return tip;
    }

    public void setTip(String tip)
    {
        this.tip = tip;
    }

    public String getUnread()
    {
        return unread;
    }

    public void setUnread(String unread)
    {
        this.unread = unread;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public String toString()
    {
        return "SysNewsMessageListBean{" +
                "messageId='" + messageId + '\'' +
                ", desc='" + desc + '\'' +
                ", createTime='" + createTime + '\'' +
                ", user=" + user +
                ", tip='" + tip + '\'' +
                ", unread='" + unread + '\'' +
                '}';
    }

    public static class UserBean
      {
          String nicName;
          String uId;
          String id;
          String sex;
          String age;
          String phUrl;
          String level;
          String focus;
          String distance;
          String remark;
          String isVuser;

          public String getIsVuser()
          {
              return isVuser;
          }

          public void setIsVuser(String isVuser)
          {
              this.isVuser = isVuser;
          }

          public String getNicName()
          {
              return nicName;
          }

          public void setNicName(String nicName)
          {
              this.nicName = nicName;
          }

          public String getRemark()
          {
              return remark;
          }

          public void setRemark(String remark)
          {
              this.remark = remark;
          }

          public String getDistance()
          {
              return distance;
          }

          public void setDistance(String distance)
          {
              this.distance = distance;
          }

          public String getNickName()
          {
              return nicName;
          }

          public void setNickName(String nickName)
          {
              this.nicName = nickName;
          }

          public String getuId()
          {
              return uId;
          }

          public void setuId(String uId)
          {
              this.uId = uId;
          }

          public String getId()
          {
              return id;
          }

          public void setId(String id)
          {
              this.id = id;
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

          public String getPhUrl()
          {
              return phUrl;
          }

          public void setPhUrl(String phUrl)
          {
              this.phUrl = phUrl;
          }

          public String getLevel()
          {
              return level;
          }

          public void setLevel(String level)
          {
              this.level = level;
          }

          public String getFocus()
          {
              return focus;
          }

          public void setFocus(String focus)
          {
              this.focus = focus;
          }

          @Override
          public String toString()
          {
              return "UserBean{" +
                      "nicName='" + nicName + '\'' +
                      ", uId='" + uId + '\'' +
                      ", id='" + id + '\'' +
                      ", sex='" + sex + '\'' +
                      ", age='" + age + '\'' +
                      ", phUrl='" + phUrl + '\'' +
                      ", level='" + level + '\'' +
                      ", focus='" + focus + '\'' +
                      ", distance='" + distance + '\'' +
                      ", remark='" + remark + '\'' +
                      ", isVuser='" + isVuser + '\'' +
                      '}';
          }
      }
}

