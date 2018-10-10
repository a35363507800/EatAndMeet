package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/5/24.
 */
public class ActivityWindowBean
{
    //  "type":"活动类型 0弹窗 1悬浮",
    //    "pic":"图片(约定悬浮的类型没有图片用美术给的资源)",
    //            "url":"跳转"

    private String type;
    private String pic;
    private String url;
    private String activity = "";

    public String getActivity()
    {
        return activity;
    }

    public void setActivity(String activity)
    {
        this.activity = activity;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPic()
    {
        return pic;
    }

    public void setPic(String pic)
    {
        this.pic = pic;
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
    public String toString()
    {
        return "ActivityWindowBean{" +
                "type='" + type + '\'' +
                ", pic='" + pic + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
