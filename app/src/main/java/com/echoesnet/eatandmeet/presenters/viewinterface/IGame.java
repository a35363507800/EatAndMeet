package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/8/16 0016
 * @description
 */
public interface IGame
{
    /**
     * @param gameId 游戏Id
     */
    void shareH5(String gameId);

    /**
     * 通知后台分享类型
     * @param gameId 游戏id
     * @param matchingId 对战Id
     * @param type 分享类型 1微信好友 2qq好友 3微信朋友圈 4qq空间 5微博 6动态
     * @param score 得分
     */
    void shareGame(String gameId,String matchingId,String type,String score);

    /**
     * 进入游戏
     * @param gameId
     */
    void enterGame(String gameId);

    /**
     * 推出游戏
     * @param gameId
     */
    void exitGame(String gameId);
}
