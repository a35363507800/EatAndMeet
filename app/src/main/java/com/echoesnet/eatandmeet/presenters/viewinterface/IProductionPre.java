package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/14 16:33
 * @description
 */

public interface IProductionPre
{
    /**
     * 获取关注人的动态列表
     *
     * @param type     操作类型，加载还是刷新
     * @param startIdx 分页起始id
     * @param num      每页的数目
     */
    void getVTrends(final String type, String startIdx, String num);

    void focusFriendCallServer(final String luId, final FTrendsItemBean itemBean);

}
