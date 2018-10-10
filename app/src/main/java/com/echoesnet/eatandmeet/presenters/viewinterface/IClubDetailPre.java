package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/5
 * @description
 */
public interface IClubDetailPre
{
    void getClubDetail(String clubId,boolean isCheckOffLine);

    /**
     * 收藏club
     *
     * @param clubId
     */
    void collectedClub(String clubId);
    /**
     * 移除收藏club
     *
     * @param clubId
     */
    void removeClub(String clubId);

    /**
     * 获取club评论
     *
     * @param clubId
     * @param num
     * @param startIndex
     */
    void getPartyComment(String num,String startIndex,String clubId,String type);

}
