package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.DishDetailBean;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface IFoodDetailView {
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void getNewDishDetailCallback(DishDetailBean response);
}
