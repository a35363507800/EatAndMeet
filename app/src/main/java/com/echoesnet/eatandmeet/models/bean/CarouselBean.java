package com.echoesnet.eatandmeet.models.bean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/9/25 0025
 * @description 轮播数据bean
 */
public class CarouselBean
{
    String type ;// 默认0 图片 1 视频
    String videoUrl;//视频播放地址
    String picUrl;//type 0 图片url type 1 缩略图

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    public String getPicUrl()
    {
        return picUrl;
    }

    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }
}
