package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.TrendsMsgBean;
import com.echoesnet.eatandmeet.models.bean.TrendsPraiseBean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/25 0025
 * @description
 */
public interface ITrendsPraiseListView
{
    void requestError(String err);
    void getMyTrendsListCallBack(String type, List<TrendsPraiseBean> trendsPraiseList);
}
