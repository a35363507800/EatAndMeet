package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by lc on 2017/7/17 15.
 */

public class SysMesBean
{
//     "messageId":"消息id",
//             "desc":"内容",
//             "createTime":"发布时间",
//             "unread":"是否未读 1未读0已读"
//  "tip":"就是原来那个tip 好像是你们【】里用的"
    private String messageId;
    private String desc="";
    private String createTime;
    private String unread;
    private String tip;
    private String cardName;
    private UserBean user;


    public String getCardName()
    {
        return cardName;
    }

    public void setCardName(String cardName)
    {
        this.cardName = cardName;
    }

    public String getTip()
    {
        return tip;
    }

    public void setTip(String tip)
    {
        this.tip = tip;
    }

    public UserBean getUser()
    {
        return user;
    }

    public void setUser(UserBean user)
    {
        this.user = user;
    }

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

    public String getUnread()
    {
        return unread;
    }

    public void setUnread(String unread)
    {
        this.unread = unread;
    }

    @Override
    public String toString()
    {
        return "SysMesBean{" +
                "messageId='" + messageId + '\'' +
                ", desc='" + desc + '\'' +
                ", createTime='" + createTime + '\'' +
                ", unread='" + unread + '\'' +
                ", tip='" + tip + '\'' +
                ", user=" + user +
                '}';
    }

    public static class UserBean
    {
        private String nicName;
        private String uId;
        private String id;
        private String sex;
        private String age;
        private String phUrl;
        private String level;
        private String focus;
        private String distance;
        private String isVuser;
        private String remark;

        public String getRemark()
        {
            return remark;
        }

        public void setRemark(String remark)
        {
            this.remark = remark;
        }

        public String getNicName()
        {
            return nicName;
        }

        public void setNicName(String nicName)
        {
            this.nicName = nicName;
        }

        public String getIsVuser()
        {
            return isVuser;
        }

        public void setIsVuser(String isVuser)
        {
            this.isVuser = isVuser;
        }

        public String getDistance()
        {
            return distance;
        }

        public void setDistance(String distance)
        {
            this.distance = distance;
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
                    ", isVuser='" + isVuser + '\'' +
                    ", remark='" + remark + '\'' +
                    '}';
        }
    }
}
