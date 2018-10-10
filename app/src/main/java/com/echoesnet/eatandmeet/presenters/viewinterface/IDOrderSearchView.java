package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.OrderedDishItemBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/15.
 */

public interface IDOrderSearchView {
    void getDishListCallback(List<OrderedDishItemBean> response);
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);

}
