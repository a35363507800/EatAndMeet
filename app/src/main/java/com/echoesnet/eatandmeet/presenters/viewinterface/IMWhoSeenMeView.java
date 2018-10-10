package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.WhoSeenMeBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface IMWhoSeenMeView {
    void requestNetError(Call call, Exception e, String exceptSource);
    void getSeenMeDataCallback(List<WhoSeenMeBean> response, String operateType);
}
