package com.echoesnet.eatandmeet.models.bean;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.TrendsPublishAct;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/10 0010
 * @description
 */
public class FTrendsItemBean implements Serializable
{

    /**
     * age : 34
     * commentNum : 0
     * content : 越帅越优惠，越靓越实惠，看脸吃饭 【glacier的直播间】
     * ext : {"anchor":"4229d7a5-acc2-460f-9e75-0d1a9e07a75d","hxRoomId":"21441605074945","liveSource":"2","roomId":"101534"}
     * level : 5
     * likedNum : 0
     * nicName : glacier
     * phurl : http://huisheng.ufile.ucloud.com.cn/image/20FAE52BB833B00310F1C1587F0FAA5B.jpg
     * posx :
     * posy :
     * sex : 男
     * tId : 149978880104
     * thumbnails :
     * time : 2017-07-12 16:05:08
     * timeToNow : 2分钟前
     * type : 2
     * up : 4229d7a5-acc2-460f-9e75-0d1a9e07a75d
     * url : http://huisheng.ufile.ucloud.com.cn/image/20FAE52BB833B00310F1C1587F0FAA5B.jpg
     */

    private String age;
    private String commentNum;
    private String content;
    private ExtBean ext;
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
    private String type;//"0：普通动态，1：订单完成动态，2：直播动态，3.游戏动态，4.专栏，5中秋节活动分享",6 轰趴餐馆分享
    private String up;
    private String url;
    private String distance;
    private String isLike;
    private String showType;
    private String focus;
    private String status;
    private String stamp;
    private String playComplete;
    private String remark;
    private String readNum ;
    private String isVuser = "";//大V
    private List<UsersBean> likedList;
    private TrendsPublishAct.TrendsPublish trendsPublish;

    public String getReadNum()
    {
        return readNum;
    }

    public void setReadNum(String readNum)
    {
        this.readNum = readNum;
    }

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

    public String getPlayComplete()
    {
        return playComplete;
    }

    public void setPlayComplete(String playComplete)
    {
        this.playComplete = playComplete;
    }

    public List<UsersBean> getLikedList()
    {
        return likedList;
    }

    public void setLikedList(List<UsersBean> likedList)
    {
        this.likedList = likedList;
    }

    public TrendsPublishAct.TrendsPublish getTrendsPublish()
    {
        return trendsPublish;
    }

    public void setTrendsPublish(TrendsPublishAct.TrendsPublish trendsPublish)
    {
        this.trendsPublish = trendsPublish;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }


    public String getStamp()
    {
        return stamp;
    }

    public void setStamp(String stamp)
    {
        this.stamp = stamp;
    }

    public String getFocus()
    {
        return focus;
    }

    public void setFocus(String focus)
    {
        this.focus = focus;
    }

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

    public String gettId()
    {
        return tId;
    }

    public void settId(String tId)
    {
        this.tId = tId;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
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

    public ExtBean getExt()
    {
        return ext;
    }

    public void setExt(ExtBean ext)
    {
        this.ext = ext;
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

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof FTrendsItemBean))
            return false;
        FTrendsItemBean bean = (FTrendsItemBean) o;
        if (!TextUtils.isEmpty(this.gettId()) && !TextUtils.isEmpty(bean.gettId()))
        {
            return this.gettId().equals(bean.gettId());
        } else if (!TextUtils.isEmpty(this.getStamp()) && !TextUtils.isEmpty(bean.getStamp()))
        {
            return this.getStamp().equals(bean.getStamp());
        }
        {
            return false;
        }
    }

    public static class ExtBean implements Serializable
    {
        /**
         * anchor : 4229d7a5-acc2-460f-9e75-0d1a9e07a75d
         * hxRoomId : 21441605074945
         * liveSource : 2
         * roomId : 101534
         */
        private String anchor;
        private String hxRoomId;
        private String liveSource;
        private String roomId;
        private String liveStatus;
        private String rId;
        private String gameUrl;
        private String vedio;
        private String gameName;
        private String gameId;
        private String shareUrl;
        private String title;
        private String content;
        private String columnName;
        private String articleId;
        private String fuckDate;
        private String gameType;
        private String hpId ;
        private String hpName;
        private String trendsDesc;
        private String activityId;
        private String pageTitle;
        private String imgUrl;

        public String getTrendsDesc()
        {
            return trendsDesc;
        }

        public void setTrendsDesc(String trendsDesc)
        {
            this.trendsDesc = trendsDesc;
        }

        public String getActivityId()
        {
            return activityId;
        }

        public void setActivityId(String activityId)
        {
            this.activityId = activityId;
        }

        public String getPageTitle()
        {
            return pageTitle;
        }

        public void setPageTitle(String pageTitle)
        {
            this.pageTitle = pageTitle;
        }

        public String getHpId()
        {
            return hpId;
        }

        public void setHpId(String hpId)
        {
            this.hpId = hpId;
        }

        public String getHpName()
        {
            return hpName;
        }

        public void setHpName(String hpName)
        {
            this.hpName = hpName;
        }

        public String getGameType()
        {
            return gameType;
        }

        public void setGameType(String gameType)
        {
            this.gameType = gameType;
        }

        public String getFuckDate()
        {
            return fuckDate;
        }

        public void setFuckDate(String fuckDate)
        {
            this.fuckDate = fuckDate;
        }

        public String getColumnName()
        {
            return columnName;
        }

        public void setColumnName(String columnName)
        {
            this.columnName = columnName;
        }

        public String getImgUrl()
        {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl)
        {
            this.imgUrl = imgUrl;
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


        public String getGameUrl()
        {
            return gameUrl;
        }

        public void setGameUrl(String gameUrl)
        {
            this.gameUrl = gameUrl;
        }

        public String getGameName()
        {
            return gameName;
        }

        public void setGameName(String gameName)
        {
            this.gameName = gameName;
        }

        public String getGameId()
        {
            return gameId;
        }

        public void setGameId(String gameId)
        {
            this.gameId = gameId;
        }

        public String getVedio()
        {
            return vedio;
        }

        public void setVedio(String vedio)
        {
            this.vedio = vedio;
        }

        public String getrId()
        {
            return rId;
        }

        public void setrId(String rId)
        {
            this.rId = rId;
        }

        public String getAnchor()
        {
            return anchor;
        }

        public void setAnchor(String anchor)
        {
            this.anchor = anchor;
        }

        public String getHxRoomId()
        {
            return hxRoomId;
        }

        public void setHxRoomId(String hxRoomId)
        {
            this.hxRoomId = hxRoomId;
        }

        public String getLiveSource()
        {
            return liveSource;
        }

        public void setLiveSource(String liveSource)
        {
            this.liveSource = liveSource;
        }

        public String getRoomId()
        {
            return roomId;
        }

        public void setRoomId(String roomId)
        {
            this.roomId = roomId;
        }

        public String getLiveStatus()
        {
            return liveStatus;
        }

        public void setLiveStatus(String liveStatus)
        {
            this.liveStatus = liveStatus;
        }

        @Override
        public String toString()
        {
            return "ExtBean{" +
                    "anchor='" + anchor + '\'' +
                    ", hxRoomId='" + hxRoomId + '\'' +
                    ", liveSource='" + liveSource + '\'' +
                    ", roomId='" + roomId + '\'' +
                    ", liveStatus='" + liveStatus + '\'' +
                    ", rId='" + rId + '\'' +
                    ", gameUrl='" + gameUrl + '\'' +
                    ", vedio='" + vedio + '\'' +
                    ", gameName='" + gameName + '\'' +
                    ", gameId='" + gameId + '\'' +
                    ", shareUrl='" + shareUrl + '\'' +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", columnName='" + columnName + '\'' +
                    ", articleId='" + articleId + '\'' +
                    ", fuckDate='" + fuckDate + '\'' +
                    ", imgUrl='" + imgUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString()
    {
        return "FTrendsItemBean{" +
                "age='" + age + '\'' +
                ", commentNum='" + commentNum + '\'' +
                ", content='" + content + '\'' +
                ", ext=" + ext +
                ", level='" + level + '\'' +
                ", likedNum='" + likedNum + '\'' +
                ", nicName='" + nicName + '\'' +
                ", phurl='" + phurl + '\'' +
                ", posx='" + posx + '\'' +
                ", posy='" + posy + '\'' +
                ", sex='" + sex + '\'' +
                ", tId='" + tId + '\'' +
                ", thumbnails='" + thumbnails + '\'' +
                ", time='" + time + '\'' +
                ", timeToNow='" + timeToNow + '\'' +
                ", type='" + type + '\'' +
                ", up='" + up + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
