package com.echoesnet.eatandmeet.models.bean;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangben on 2016/5/5.
 */
public class BigVcommentBean
{
    private String restaurantId;
    private String uId;
    private String nickName;
    private String title;
    private int rating;
    private String comment;
    private String userHeadImg;
    private String commentImgUrls;
    private String level;//等级
    private String sex; //性别
    private String age;



    public BigVcommentBean()
    {
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public List<String> getCommentImgUrlLst()
    {
//        return CommonUtils.strWithSeparatorToList(getCommentImgUrls(),CommonUtils.SEPARATOR);
        List<String> stuff = new ArrayList<String>();
        if (TextUtils.isEmpty(getCommentImgUrls()))
        {
            return stuff;
        }
        stuff.addAll(Arrays.asList(getCommentImgUrls().split("!=end=!")));
        return stuff;
    }
    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }
    public void setLevel(String Level)
    {
        this.level = Level;
    }

    public String getLevel()
    {
        return level;
    }

    public String getRestaurantId()
    {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId)
    {
        this.restaurantId = restaurantId;
    }

    public int getRating()
    {
        return rating;
    }

    public void setRating(int rating)
    {
        this.rating = rating;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUserHeadImg()
    {
        return userHeadImg;
    }

    public void setUserHeadImg(String userHeadImg)
    {
        this.userHeadImg = userHeadImg;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getCommentImgUrls()
    {
        return commentImgUrls;
    }

    public void setCommentImgUrls(String commentImgUrls)
    {
        this.commentImgUrls = commentImgUrls;
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
        return "BigVcommentBean{" +
                "restaurantId='" + restaurantId + '\'' +
                ", uId='" + uId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", userHeadImg='" + userHeadImg + '\'' +
                ", commentImgUrls='" + commentImgUrls + '\'' +
                ", level='" + level + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
