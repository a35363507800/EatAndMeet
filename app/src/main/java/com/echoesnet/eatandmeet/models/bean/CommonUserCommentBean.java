package com.echoesnet.eatandmeet.models.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangben on 2016/5/5.
 */
public class CommonUserCommentBean
{
    private String rId;
    private String uId;
    private String isVuser;
    private String evaNicName;
    private String remark;
    private String rStar;
    private String evalContent;
    //头像
    private String evaImg;
    private String epUrls;
    //评论id
    private String eId;
    private String evalTime;
    //评论的订单号
    private String oId;
    //商家是否回复
    private String replyOrNot;
    //商家回复时间
    private String replyTime;
    //商家的回复内容
    private String resReply;
    private String level;

    private String sex;
    private String age;

    //private List<String> commentImgUrls;

    public CommonUserCommentBean()
    {}
    public void setlevel(String Level)
    {
        this.level = Level;
    }

    public String getlevel()
    {
        return level;
    }

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public List<String> getCommentImgUrls()
    {
        List<String> stuff = new ArrayList<String>();
        if (TextUtils.isEmpty(getEpUrls()))
        {
            return stuff;
        }

        stuff.addAll(Arrays.asList(getEpUrls().split("!=end=!")));
        return stuff;
    }
    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
    public String getEvaNicName()
    {
        return evaNicName;
    }

    public void setEvaNicName(String evaNicName)
    {
        this.evaNicName = evaNicName;
    }

    public String getrId()
    {
        return rId;
    }

    public void setrId(String rId)
    {
        this.rId = rId;
    }

    public String getrStar()
    {
        return rStar;
    }

    public void setrStar(String rStar)
    {
        this.rStar = rStar;
    }

    public String getEvaImg()
    {
        return evaImg;
    }

    public void setEvaImg(String evaImg)
    {
        this.evaImg = evaImg;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getEpUrls()
    {
        return epUrls;
    }

    public void setEpUrls(String epUrls)
    {
        this.epUrls = epUrls;
    }

    public String getEvalContent()
    {
        return evalContent;
    }

    public void setEvalContent(String evalContent)
    {
        this.evalContent = evalContent;
    }

    public String geteId()
    {
        return eId;
    }

    public void seteId(String eId)
    {
        this.eId = eId;
    }

    public String getEvalTime()
    {
        return evalTime;
    }

    public void setEvalTime(String evalTime)
    {
        this.evalTime = evalTime;
    }

    public String getoId()
    {
        return oId;
    }

    public void setoId(String oId)
    {
        this.oId = oId;
    }

    public String getReplyOrNot()
    {
        return replyOrNot;
    }

    public void setReplyOrNot(String replyOrNot)
    {
        this.replyOrNot = replyOrNot;
    }

    public String getReplyTime()
    {
        return replyTime;
    }

    public void setReplyTime(String replyTime)
    {
        this.replyTime = replyTime;
    }

    public String getResReply()
    {
        return resReply;
    }

    public void setResReply(String resReply)
    {
        this.resReply = resReply;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
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

    @Override
    public String toString()
    {
        return "CommonUserCommentBean{" +
                "eId='" + eId + '\'' +
                ", rId='" + rId + '\'' +
                ", userId='" + uId + '\'' +
                ", evaNicName='" + evaNicName + '\'' +
                ", remark='" + remark + '\'' +
                ", rStar='" + rStar + '\'' +
                ", evalContent='" + evalContent + '\'' +
                ", evaImg='" + evaImg + '\'' +
                ", epUrls='" + epUrls + '\'' +
                ", evalTime='" + evalTime + '\'' +
                ", oId='" + oId + '\'' +
                ", replyOrNot='" + replyOrNot + '\'' +
                ", replyTime='" + replyTime + '\'' +
                ", resReply='" + resReply + '\'' +
                '}';
    }
}
