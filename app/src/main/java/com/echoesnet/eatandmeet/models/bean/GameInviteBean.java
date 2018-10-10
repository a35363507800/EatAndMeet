package com.echoesnet.eatandmeet.models.bean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/11/13 0013
 * @description
 */
public class GameInviteBean
{
    /**
     * age : 年龄
     * level : 等级
     * nicName : 昵称
     * sex : 性别
     * uId : uId
     * uphUrl : 头像
     * remark : 备注名
     * id : 用户id
     * "isVuser":"V用户标识 0：否1：是"
     */

    private String age;
    private String level;
    private String nicName;
    private String sex;
    private String uId;
    private String uphUrl;
    private String remark;
    private String id;
    private String isVuser;
    private String status;
    private String agree;
    private int position;
    private boolean isSelect = false;

    public String getAgree()
    {
        return agree;
    }

    public void setAgree(String agree)
    {
        this.agree = agree;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public boolean isSelect()
    {
        return isSelect;
    }

    public void setSelect(boolean select)
    {
        isSelect = select;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
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

    public String getUId()
    {
        return uId;
    }

    public void setUId(String uId)
    {
        this.uId = uId;
    }

    public String getUphUrl()
    {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl)
    {
        this.uphUrl = uphUrl;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof GameInviteBean)
            return((GameInviteBean)obj).id.equals(this.id);
        else
            return false;

    }



    @Override
    public String toString()
    {
        return "GameInviteBean{" +
                "age='" + age + '\'' +
                ", level='" + level + '\'' +
                ", nicName='" + nicName + '\'' +
                ", sex='" + sex + '\'' +
                ", uId='" + uId + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", remark='" + remark + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
