package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/13 0013
 * @description
 */
public class TrendsDetailBean
{

    /**
     * age : 发布动态用户年龄
     * commentNum : 评论条数
     * comments : [{"age":"评论用户年龄","comment":"评论内容","commentId":"评论id","date":"评论时间","distance":"距离","level":"评论等级","nicName":"评论用户昵称","replyCommentId":"被回复评论Id","replyName":"被回复评论用户昵称","replyer":"被回复评论用户uId","sex":"评论用户性别","uId":"评论用户uId","up":"动态发布人"}]
     * content : 动态内容
     * distance : 距离
     * ext : {"anchor":"主播uId","hxRoomId":"环信聊天室id","liveSource":"直播源","rId":"餐厅id","rName":"餐厅名称","roomId":"房间id"}
     * focus : 1：已关注，0：未关注，空：本人
     * level : 发布动态用户等级
     * likedList : [{"phurl":"头像","uId":"uId"}]
     * likedNum : 点赞数量
     * nicName : 发布动态用户昵称
     * phurl : 发布动态用户头像
     * posx : 坐标纬度
     * posy : 坐标经度
     * sex : 发布动态用户性别
     * tId : 动态id
     * thumbnails : 视频缩略图
     * time : 动态发布时间
     * timeToNow : 动态发布时间距离当前时间
     * type : 0：普通动态，1：订单完成动态，2：直播动态
     * up : 发布动态用户的uid
     * url : thumbnails为空字符串：动态图片，thumbnails不为空：视频链接
     */

    private String age;
    private String commentNum;
    private String content;
    private String distance;
    private String focus;
    private String level;
    private String likedNum;
    private String nicName;
    private String phurl;
    private String posx;
    private String posy;
    private String location;
    private String sex;
    private String tId;
    private String thumbnails;
    private String time;
    private String timeToNow;
    private String type;
    private String up;
    private String url;
    private String isLike;
    private String showType;
    private List<LikedListBean> likedList;

    public String getShowType()
    {
        return showType;
    }

    public void setShowType(String showType)
    {
        this.showType = showType;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getIsLike()
    {
        return isLike;
    }

    public void setIsLike(String isLike)
    {
        this.isLike = isLike;
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

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getFocus()
    {
        return focus;
    }

    public void setFocus(String focus)
    {
        this.focus = focus;
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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getUp()
    {
        return up;
    }

    public void setUp(String up)
    {
        this.up = up;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<LikedListBean> getLikedList()
    {
        return likedList;
    }

    public void setLikedList(List<LikedListBean> likedList)
    {
        this.likedList = likedList;
    }


    public  class LikedListBean
    {
        /**
         * phurl : 头像
         * uId : uId
         */

        private String phurl;
        private String uId;

        public String getPhurl()
        {
            return phurl;
        }

        public void setPhurl(String phurl)
        {
            this.phurl = phurl;
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
}
