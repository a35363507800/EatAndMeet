package com.echoesnet.eatandmeet.models.bean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/9/13 0013
 * @description
 */
public class ResListBannerBean
{

    /**
     * url : 图片url或视频缩略图url
     * type : 0图片1视频
     * title : 展示标题（如果有）
     * jump : 跳转的url或播放的视频
     */

    private String url;
    private String type;
    private String title;
    private String jump;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getJump()
    {
        return jump;
    }

    public void setJump(String jump)
    {
        this.jump = jump;
    }

    @Override
    public String toString()
    {
        return "ResListBannerBean{" +
                "url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", jump='" + jump + '\'' +
                '}';
    }
}
