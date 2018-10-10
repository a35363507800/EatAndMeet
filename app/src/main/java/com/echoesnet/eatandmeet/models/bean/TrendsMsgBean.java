package com.echoesnet.eatandmeet.models.bean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/25 0025
 * @description
 */
public class TrendsMsgBean
{

    /**
     * type : 0：点赞，1：评论
     * age : 年龄
     * comment : 评论内容
     * commentId : 评论id
     * date : 15:19
     * detail : {"content":"动态详情","tId":"动态id","thumbnails":"视频缩略图","url":"thumbnails为空字符串：动态图片，thumbnails不为空：视频链接"}
     * tId : 动态id
     * level : 等级
     * nicName : 昵称
     * phurl : 头像
     * sex : 性别
     * uId : uid
     * distance : 距离
     */

    private String type;
    private String age;
    private String comment;
    private String commentId;
    private String date;
    private DetailBean detail;
    private String tId;
    private String level;
    private String nicName;
    private String phurl;
    private String sex;
    private String uId;
    private String distance;
    private String remark;
    private String isVuser;

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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

    public DetailBean getDetail()
    {
        return detail;
    }

    public void setDetail(DetailBean detail)
    {
        this.detail = detail;
    }

    public String getTId()
    {
        return tId;
    }

    public void setTId(String tId)
    {
        this.tId = tId;
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

    public String getUId()
    {
        return uId;
    }

    public void setUId(String uId)
    {
        this.uId = uId;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public static class DetailBean
    {
        /**
         * content : 动态详情
         * tId : 动态id
         * thumbnails : 视频缩略图
         * url : thumbnails为空字符串：动态图片，thumbnails不为空：视频链接
         */

        private String content;
        private String tId;
        private String thumbnails;
        private String url;

        public String getContent()
        {
            return content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        public String getTId()
        {
            return tId;
        }

        public void setTId(String tId)
        {
            this.tId = tId;
        }

        public String getThumbnails()
        {
            return thumbnails;
        }

        public void setThumbnails(String thumbnails)
        {
            this.thumbnails = thumbnails;
        }

        public String getUrl()
        {
            return url;
        }

        public void setUrl(String url)
        {
            this.url = url;
        }

        public String gettId()
        {
            return tId;
        }

        public void settId(String tId)
        {
            this.tId = tId;
        }

        @Override
        public String toString()
        {
            return "DetailBean{" +
                    "content='" + content + '\'' +
                    ", tId='" + tId + '\'' +
                    ", thumbnails='" + thumbnails + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public String gettId()
    {
        return tId;
    }

    public void settId(String tId)
    {
        this.tId = tId;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    @Override
    public String toString()
    {
        return "TrendsMsgBean{" +
                "type='" + type + '\'' +
                ", age='" + age + '\'' +
                ", comment='" + comment + '\'' +
                ", commentId='" + commentId + '\'' +
                ", date='" + date + '\'' +
                ", detail=" + detail +
                ", tId='" + tId + '\'' +
                ", level='" + level + '\'' +
                ", nicName='" + nicName + '\'' +
                ", phurl='" + phurl + '\'' +
                ", sex='" + sex + '\'' +
                ", uId='" + uId + '\'' +
                ", distance='" + distance + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
