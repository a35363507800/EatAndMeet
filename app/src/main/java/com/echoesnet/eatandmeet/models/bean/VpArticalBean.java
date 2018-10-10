package com.echoesnet.eatandmeet.models.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/9/12
 * @description
 */
public class VpArticalBean implements Parcelable
{
    private String age;// 发布动态用户年龄
    private String commentNum;//评论条数
    private String content;//"动态内容",
    private String url;//"thumbnails为空字符串：动态图片，thumbnails不为空：视频链接",
    private String thumbnails;//视频缩略图
    private String type;// "4：专栏文章"
    private String level;//发布动态用户等级
    private String likedNum;//点赞数量
    private String nicName;//发布动态用户昵称
    private String phurl;//"发布动态用户头像"
    private String posx;//"坐标纬度",
    private String posy;//"坐标经度",
    private String sex;//"发布动态用户性别"
    private String tId;//"动态id",
    private String time;//"动态发布时间"
    private String timeToNow;//"动态发布时间距离当前时间"
    private String up;//发布动态用户的uid"
    private String distance;//"距离"
    private String isLike;//，0：否，1：是"
    private String location;//"位置描述"
    private String showType;//"显示类型"
    private String stamp;//"时间戳"
    private String remark;//"备注名"
    private Ext ext;

    public Ext getExt()
    {
        return ext;
    }

    public void setExt(Ext ext)
    {
        this.ext = ext;
    }

    class Ext implements Parcelable
    {
        private String shareUrl;// 专栏分享链接
        private String title;//专栏标题
        private String content;//"H5页面信息",
        private String columnName;//"专栏名",
        private String articleId;//"文章Id"

         public Ext()
         {

         }

        public String getColumnName()
        {
            return columnName;
        }

        public void setColumnName(String columnName)
        {
            this.columnName = columnName;
        }

        public String getArticleId()
        {
            return articleId;
        }

        public void setArticleId(String articleId)
        {
            this.articleId = articleId;
        }

        public String getShareUrl()
        {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl)
        {
            this.shareUrl = shareUrl;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getContent()
        {
            return content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        @Override
        public String toString()
        {
            return "Ext{" +
                    "shareUrl='" + shareUrl + '\'' +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", columnName='" + columnName + '\'' +
                    ", articleId='" + articleId + '\'' +
                    '}';
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {

        }
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getCommentNum()
    {
        return commentNum;
    }

    public void setCommentNum(String commentNum)
    {
        this.commentNum = commentNum;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getThumbnails()
    {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails)
    {
        this.thumbnails = thumbnails;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getLikedNum()
    {
        return likedNum;
    }

    public void setLikedNum(String likedNum)
    {
        this.likedNum = likedNum;
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

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String gettId()
    {
        return tId;
    }

    public void settId(String tId)
    {
        this.tId = tId;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getTimeToNow()
    {
        return timeToNow;
    }

    public void setTimeToNow(String timeToNow)
    {
        this.timeToNow = timeToNow;
    }

    public String getUp()
    {
        return up;
    }

    public void setUp(String up)
    {
        this.up = up;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getIsLike()
    {
        return isLike;
    }

    public void setIsLike(String isLike)
    {
        this.isLike = isLike;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getShowType()
    {
        return showType;
    }

    public void setShowType(String showType)
    {
        this.showType = showType;
    }

    public String getStamp()
    {
        return stamp;
    }

    public void setStamp(String stamp)
    {
        this.stamp = stamp;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    @Override
    public String toString()
    {
        return "VpArticalBean{" +
                "age='" + age + '\'' +
                ", commentNum='" + commentNum + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", thumbnails='" + thumbnails + '\'' +
                ", type='" + type + '\'' +
                ", level='" + level + '\'' +
                ", likedNum='" + likedNum + '\'' +
                ", nicName='" + nicName + '\'' +
                ", phurl='" + phurl + '\'' +
                ", posx='" + posx + '\'' +
                ", posy='" + posy + '\'' +
                ", sex='" + sex + '\'' +
                ", tId='" + tId + '\'' +
                ", time='" + time + '\'' +
                ", timeToNow='" + timeToNow + '\'' +
                ", up='" + up + '\'' +
                ", distance='" + distance + '\'' +
                ", isLike='" + isLike + '\'' +
                ", location='" + location + '\'' +
                ", showType='" + showType + '\'' +
                ", stamp='" + stamp + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.age);
        dest.writeString(this.commentNum);
        dest.writeString(this.content);
        dest.writeString(this.url);
        dest.writeString(this.thumbnails);
        dest.writeString(this.type);
        dest.writeString(this.level);
        dest.writeString(this.likedNum);
        dest.writeString(this.nicName);
        dest.writeString(this.phurl);
        dest.writeString(this.posx);
        dest.writeString(this.posy);
        dest.writeString(this.sex);
        dest.writeString(this.tId);
        dest.writeString(this.time);
        dest.writeString(this.timeToNow);
        dest.writeString(this.up);
        dest.writeString(this.distance);
        dest.writeString(this.isLike);
        dest.writeString(this.location);
        dest.writeString(this.showType);
        dest.writeString(this.stamp);
        dest.writeString(this.remark);
        dest.writeParcelable(this.ext, flags);
    }

    public VpArticalBean()
    {
    }

    protected VpArticalBean(Parcel in)
    {
        this.age = in.readString();
        this.commentNum = in.readString();
        this.content = in.readString();
        this.url = in.readString();
        this.thumbnails = in.readString();
        this.type = in.readString();
        this.level = in.readString();
        this.likedNum = in.readString();
        this.nicName = in.readString();
        this.phurl = in.readString();
        this.posx = in.readString();
        this.posy = in.readString();
        this.sex = in.readString();
        this.tId = in.readString();
        this.time = in.readString();
        this.timeToNow = in.readString();
        this.up = in.readString();
        this.distance = in.readString();
        this.isLike = in.readString();
        this.location = in.readString();
        this.showType = in.readString();
        this.stamp = in.readString();
        this.remark = in.readString();
        this.ext = in.readParcelable(Ext.class.getClassLoader());
    }

    public static final Parcelable.Creator<VpArticalBean> CREATOR = new Parcelable.Creator<VpArticalBean>()
    {
        @Override
        public VpArticalBean createFromParcel(Parcel source)
        {
            return new VpArticalBean(source);
        }

        @Override
        public VpArticalBean[] newArray(int size)
        {
            return new VpArticalBean[size];
        }
    };
}
