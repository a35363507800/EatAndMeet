package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.DishRightMenuGroupBean;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/15.
 */

public interface IDishFrgView {
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void getDishListCallback(ArrayList<DishRightMenuGroupBean> response);
}
