package com.echoesnet.eatandmeet.models.bean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/9/15 0015
 * @description
 */
public class UnFocusVuserItemBean
{

    /**
     * age : 年龄
     * level : 等级
     * nicName : 昵称
     * phurl : 头像
     * sex : 性别
     * signature : 签名
     * uId : 用户uId
     */

    private String age;
    private String level;
    private String nicName;
    private String phurl;
    private String sex;
    private String signature;
    private String uId;
    private String isVUser = "";//大V

    public String getIsVUser()
    {
        return isVUser;
    }

    public void setIsVUser(String isVUser)
    {
        this.isVUser = isVUser;
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

    public String getPhurl()
    {
        return phurl;
    }

    public void setPhurl(String phurl)
    {
        this.phurl = phurl;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getUId()
    {
        return uId;
    }

    public void setUId(String uId)
    {
        this.uId = uId;
    }
}
