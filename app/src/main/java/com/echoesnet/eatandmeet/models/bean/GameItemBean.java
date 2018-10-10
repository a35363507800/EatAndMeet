package com.echoesnet.eatandmeet.models.bean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27 0027
 * @description
 */
public class GameItemBean
{

    /**
     * gameId : 游戏id
     * gameName : 游戏名称
     * gamePic : 游戏图片
     * redirectUrl : 跳转链接
     */

    private String gameId;
    private String gameName;
    private String gamePic;
    private String redirectUrl;
    private String gameTrendPic;
    private String posterUrl;
    private String shareContent;
    private String shareIcon;
    private String shareUrl;
    private String shareTitle;
    private String status;
    private String statusDesc;

    public String getStatusDesc()
    {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc)
    {
        this.statusDesc = statusDesc;
    }

    public String getGameTrendPic()
    {
        return gameTrendPic;
    }

    public void setGameTrendPic(String gameTrendPic)
    {
        this.gameTrendPic = gameTrendPic;
    }

    public String getPosterUrl()
    {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl)
    {
        this.posterUrl = posterUrl;
    }

    public String getShareContent()
    {
        return shareContent;
    }

    public void setShareContent(String shareContent)
    {
        this.shareContent = shareContent;
    }

    public String getShareIcon()
    {
        return shareIcon;
    }

    public void setShareIcon(String shareIcon)
    {
        this.shareIcon = shareIcon;
    }

    public String getShareUrl()
    {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl)
    {
        this.shareUrl = shareUrl;
    }

    public String getShareTitle()
    {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle)
    {
        this.shareTitle = shareTitle;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getGameId()
    {
        return gameId;
    }

    public void setGameId(String gameId)
    {
        this.gameId = gameId;
    }

    public String getGameName()
    {
        return gameName;
    }

    public void setGameName(String gameName)
    {
        this.gameName = gameName;
    }

    public String getGamePic()
    {
        return gamePic;
    }

    public void setGamePic(String gamePic)
    {
        this.gamePic = gamePic;
    }

    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String toString()
    {
        return "GameItemBean{" +
                "gameId='" + gameId + '\'' +
                ", gameName='" + gameName + '\'' +
                ", gamePic='" + gamePic + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", gameTrendPic='" + gameTrendPic + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", shareContent='" + shareContent + '\'' +
                ", shareIcon='" + shareIcon + '\'' +
                ", shareUrl='" + shareUrl + '\'' +
                ", shareTitle='" + shareTitle + '\'' +
                ", status='" + status + '\'' +
                ", statusDesc='" + statusDesc + '\'' +
                '}';
    }
}
