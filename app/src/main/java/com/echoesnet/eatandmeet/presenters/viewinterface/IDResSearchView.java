package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.SearchRestaurantBean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface IDResSearchView {
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName, Throwable e);
    void getResListCallback(List<SearchRestaurantBean> response,String operateType);

}
