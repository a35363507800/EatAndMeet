package com.echoesnet.eatandmeet.models.bean;


import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by Administrator on 2017/5/9.
 */

public class ShareBean
{
    private int shareType;
    private String shareTitle;
    private String shareWeChatMomentsTitle;
    private String shareUrl;
    private String shareImgUrl;
    private String shareContent;
    private String shareWeChatMomentsContent;
    private String shareSinaContent;
    private PlatformActionListener shareListener;
    //添加qq qqzone新属性
    private String shareSiteUrl;
    private String shareTitleUrl;
    private String shareAppImageUrl;
    private String shareSite;
   // private Map<String,String> map;



//    public Map<String,String> getMap()
//    {
//        if (map==null)
//        {
//            map = new HashMap<>();
//        }
//        return map;
//    }
//    public void setMap(Map<String,String> map)
//    {
//      this.map = map;
//    }

    public String getShareSite()
    {
        return shareSite;
    }

    public void setShareSite(String shareSite)
    {
        this.shareSite = shareSite;
    }

    public String getShareSiteUrl()
    {
        return shareSiteUrl;
    }

    public void setShareSiteUrl(String shareSiteUrl)
    {
        this.shareSiteUrl = shareSiteUrl;
    }

    public String getShareTitleUrl()
    {
        return shareTitleUrl;
    }

    public void setShareTitleUrl(String shareTitleUrl)
    {
        this.shareTitleUrl = shareTitleUrl;
    }

    public String getShareAppImageUrl()
    {
        return shareAppImageUrl;
    }

    public void setShareAppImageUrl(String shareAppImageUrl)
    {
        this.shareAppImageUrl = shareAppImageUrl;
    }

    public int getShareType()
    {
        return shareType;
    }

    public void setShareType(int shareType)
    {
        this.shareType = shareType;
    }

    public String getShareTitle()
    {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle)
    {
        this.shareTitle = shareTitle;
    }

    public String getShareWeChatMomentsTitle()
    {
        return shareWeChatMomentsTitle;
    }

    public void setShareWeChatMomentsTitle(String shareWeChatMomentsTitle)
    {
        this.shareWeChatMomentsTitle = shareWeChatMomentsTitle;
    }

    public String getShareUrl()
    {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl)
    {
        this.shareUrl = shareUrl;
    }

    public String getShareImgUrl()
    {
        return shareImgUrl;
    }

    public void setShareImgUrl(String shareImgUrl)
    {
        this.shareImgUrl = shareImgUrl;
    }

    public String getShareContent()
    {
        return shareContent;
    }

    public void setShareContent(String shareContent)
    {
        this.shareContent = shareContent;
    }

    public String getShareWeChatMomentsContent()
    {
        return shareWeChatMomentsContent;
    }

    public void setShareWeChatMomentsContent(String shareWeChatMomentsContent)
    {
        this.shareWeChatMomentsContent = shareWeChatMomentsContent;
    }

    public String getShareSinaContent()
    {
        return shareSinaContent;
    }

    public void setShareSinaContent(String shareSinaContent)
    {
        this.shareSinaContent = shareSinaContent;
    }

    public PlatformActionListener getShareListener()
    {
        return shareListener;
    }

    public void setShareListener(PlatformActionListener shareListener)
    {
        this.shareListener = shareListener;
    }

    @Override
    public String toString()
    {
        return "ShareBean{" +
                "shareType=" + shareType +
                ", shareTitle='" + shareTitle + '\'' +
                ", shareWeChatMomentsTitle='" + shareWeChatMomentsTitle + '\'' +
                ", shareUrl='" + shareUrl + '\'' +
                ", shareImgUrl='" + shareImgUrl + '\'' +
                ", shareContent='" + shareContent + '\'' +
                ", shareWeChatMomentsContent='" + shareWeChatMomentsContent + '\'' +
                ", shareSinaContent='" + shareSinaContent + '\'' +
                ", shareListener=" + shareListener +
                ", shareSiteUrl='" + shareSiteUrl + '\'' +
                ", shareTitleUrl='" + shareTitleUrl + '\'' +
                ", shareAppImageUrl='" + shareAppImageUrl + '\'' +
                ", shareSite='" + shareSite + '\'' +
                '}';
    }
}
