package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.SearchRestaurantBean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/1/19
 * @description
 */
public interface IRoomSearchView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName, Throwable e);

    void getResListCallback(List<SearchRestaurantBean> response, String operateType);
}
