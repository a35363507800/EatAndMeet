package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/26 0026
 * @description
 */
public interface ITrendsPlayVideoView
{
    void focusCallBack();
    void getIsFocusCallback(String focus);
    void sendRedCallback(String red);
    void getRedCallback(String response);
    void shareRedCallback(String amount,String income);
    void getMyRedInComeCallback(String response);
    void onError(String response);
}
