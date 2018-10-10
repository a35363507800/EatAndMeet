package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.ResListBannerBean;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/15.
 */

public interface IOrderMealView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Call call, Exception e, String exceptSource);

    void getRestaurantLstESCallback(List<RestaurantBean> response, String operateType);

    void getResListBannerCallback(List<ResListBannerBean> response);

}
