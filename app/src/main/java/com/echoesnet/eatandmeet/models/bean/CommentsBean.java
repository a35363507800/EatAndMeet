package com.echoesnet.eatandmeet.models.bean;

import android.text.TextUtils;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/14 0014
 * @description
 */
public  class CommentsBean
{
    /**
     * age : 评论用户年龄
     * comment : 评论内容
     * commentId : 评论id
     * date : 评论时间
     * distance : 距离
     * level : 评论等级
     * nicName : 评论用户昵称
     * replyCommentId : 被回复评论Id
     * replyName : 被回复评论用户昵称
     * replyer : 被回复评论用户uId
     * sex : 评论用户性别
     * uId : 评论用户uId
     * up : 动态发布人
     */

    private String age;
    private String comment;
    private String commentId;
    private String date;
    private String distance;
    private String level;
    private String nicName;
    private String replyCommentId;
    private String replyName;
    private String replyer;
    private String sex;
    private String uId;
    private String up;
    private String phurl;
    private String remark;
    private String replyRemark;
    private String isVuser;

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getReplyRemark()
    {
        return replyRemark;
    }

    public void setReplyRemark(String replyRemark)
    {
        this.replyRemark = replyRemark;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getPhurl()
    {
        return phurl;
    }

    public void setPhurl(String phurl)
    {
        this.phurl = phurl;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getCommentId()
    {
        return commentId;
    }

    public void setCommentId(String commentId)
    {
        this.commentId = commentId;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
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

    public String getReplyCommentId()
    {
        return replyCommentId;
    }

    public void setReplyCommentId(String replyCommentId)
    {
        this.replyCommentId = replyCommentId;
    }

    public String getReplyName()
    {
        return replyName;
    }

    public void setReplyName(String replyName)
    {
        this.replyName = replyName;
    }

    public String getReplyer()
    {
        return replyer;
    }

    public void setReplyer(String replyer)
    {
        this.replyer = replyer;
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

    public String getUp()
    {
        return up;
    }

    public void setUp(String up)
    {
        this.up = up;
    }


    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof CommentsBean))
            return false;
        CommentsBean bean = (CommentsBean) o;
        if (!TextUtils.isEmpty(this.getCommentId()) && !TextUtils.isEmpty(bean.getCommentId()))
        {
            return this.getCommentId().equals(bean.getCommentId());
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return "CommentsBean{" +
                "age='" + age + '\'' +
                ", comment='" + comment + '\'' +
                ", commentId='" + commentId + '\'' +
                ", date='" + date + '\'' +
                ", distance='" + distance + '\'' +
                ", level='" + level + '\'' +
                ", nicName='" + nicName + '\'' +
                ", replyCommentId='" + replyCommentId + '\'' +
                ", replyName='" + replyName + '\'' +
                ", replyer='" + replyer + '\'' +
                ", sex='" + sex + '\'' +
                ", uId='" + uId + '\'' +
                ", up='" + up + '\'' +
                ", phurl='" + phurl + '\'' +
                ", remark='" + remark + '\'' +
                ", replyRemark='" + replyRemark + '\'' +
                '}';
    }
}
