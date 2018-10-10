package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.FRestaurannt4FindBean;
import com.echoesnet.eatandmeet.models.bean.Liveplay4FindBean;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by an on 2017/3/29 0029.
 */

public interface IFRestaurant4FindFrgView
{
    void requestNetErrorCallback(String interfaceName, Throwable e);

    void getIndexRecommendSuccess(String type, FRestaurannt4FindBean restaurannt4FindBean);

    void getCarouselResSuccess(ArrayList<FPromotionBean> pBeenLst);
}
