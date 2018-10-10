package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/8/7 0007
 * @description
 */
public interface ITrendsMsg
{
    /**
     * 获取互动通知消息
     * @param type
     * @param start
     * @param num
     */
    void getTrendsMsgList(final String type, String start, String num);

    /**
     * 清除互动通知
     */
    void cleanTrendMsg();
}
