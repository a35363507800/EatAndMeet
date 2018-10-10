package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;

import java.util.List;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/26 11.
 * @description app个人详情页的动态tab页面 功能接口
 */

public interface IDynamicStateView
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
     * 向后台发起的加载动态列表的回调
     *
     * @param type       刷新类型
     * @param trendsList 动态列表集合
     */
    void getUserTrendsCallback(String type, List<FTrendsItemBean> trendsList);

    /**
     * 向后台发起的 点赞取消赞动态 的回调
     *
     * @param position 索引位置
     * @param flg      0：点赞，1：取赞
     * @param likeNum  点赞数量
     */
    void getLikeTrendsSuccessCallback(int position, String flg, int likeNum);
}
