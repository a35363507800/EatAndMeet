package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.ClubCommentBean;
import com.echoesnet.eatandmeet.models.bean.ClubDetailBean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/6
 * @description
 */
public interface IClubDetailView
{
    /**
     * 向后台发起的请求返回错误的回调
     *
     * @param interfaceName 接口名称
     * @param errorCode     错误码
     * @param errorBody     错误信息
     */
    void callServerErrorCallback(String interfaceName, String errorCode, String errorBody);

    /**
     * 向后台发起的请求由于网络原因失败的回调
     *
     * @param interfaceName 接口名称
     * @param e             异常
     */
    void requestNetErrorCallback(String interfaceName, Throwable e);

    void lookClubDetailCallBack(String response);


    void collectedClubCallback(String response);

    void  removeClubCallback(String response);

    void getClubCommentCallback(List<ClubDetailBean.CommentsBean> response, String type);
}
