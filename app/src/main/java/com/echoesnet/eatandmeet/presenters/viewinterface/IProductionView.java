package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/14 16:32
 * @description
 */

public interface IProductionView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void getTrendsCallback(String type, List<FTrendsItemBean> trendsList);
    void focusCallback(FTrendsItemBean itemBean);
}
