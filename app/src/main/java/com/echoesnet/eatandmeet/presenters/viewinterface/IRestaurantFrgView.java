package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.CommonUserCommentBean;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;

import java.util.List;

/**
 * Created by an on 2016/12/12.
 */

public interface IRestaurantFrgView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName, Throwable e);

    void refreshLoserComCallback(List<CommonUserCommentBean> response);

    void getResDetailInfoCallback(RestaurantBean rBean);
}
