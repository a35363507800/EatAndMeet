package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.Liveplay4FindBean;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by an on 2017/3/29 0029.
 */

public interface ILivePlay4FindFrgView {
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void getIndexLiveSuccess(String type, Liveplay4FindBean liveplay4FindBean);
    void getCarouselLiveSuccess(ArrayList<FPromotionBean> pBeenLst);
}
