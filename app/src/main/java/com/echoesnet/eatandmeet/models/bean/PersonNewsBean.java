package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by lc on 2017/7/11 13.
 */

public class PersonNewsBean
{
//            "nickName":"相关用户昵称",
//            "uId":"相关用户uid",
//            "id":"相关用户id",
//            "sex":"相关用户性别",
//            "age":"相关用户年龄",
//            "phUrl":"相关用户头像",
//            "level":"相关用户等级",
//            "focus":"是否关注该用户 1是0否"

   private String nickName;
   private String uId;
   private String id;
   private String sex;
   private String age;
   private String phUrl;
   private String level;
   private String focus;

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
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
        return "PersonNewsBean{" +
                "nickName='" + nickName + '\'' +
                ", uId='" + uId + '\'' +
                ", id='" + id + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", phUrl='" + phUrl + '\'' +
                ", level='" + level + '\'' +
                ", focus='" + focus + '\'' +
                '}';
    }
}
