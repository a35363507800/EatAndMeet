package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.LAnchorsListBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by an on 2016/12/12.
 */

public interface ILivePlayFrgView {
    void requestNetError(Call call, Exception e, String exceptSource);

    void getAnchorDataCallback(List<LAnchorsListBean> response, boolean isRefresh,String styleFlag);
}
