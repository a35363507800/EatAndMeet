package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/17 0017
 * @description
 */
public interface ITrendsPublishView
{
    void requestNetError(String err);
    void publishTrendsCallback();
    void shareAct2TrendsCallback();
}
