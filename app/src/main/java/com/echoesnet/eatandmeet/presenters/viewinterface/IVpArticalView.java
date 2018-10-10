package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.bean.VpArticalBean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2017/9/12
 * @description app 个人详情页的大V Tab功能接口
 */
public interface IVpArticalView
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
     * 点赞文章的回调
     *
     * @param position   索引位置
     * @param flg        是否点赞 0：点赞，1：取赞
     * @param likeNumInt 点赞数量
     */
    void praiseClickCallBack(int position, String flg, int likeNumInt);

    /**
     * 查看大V文章列表的回调
     *
     * @param type        类型
     * @param aticalsList 大V文章集合
     */
    void getArticalListCallBack(String type, List<FTrendsItemBean> aticalsList);
}
