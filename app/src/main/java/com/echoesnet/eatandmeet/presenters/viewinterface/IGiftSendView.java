package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.LGiftListBean;

import okhttp3.Call;

/**
 * Created by an on 2016/12/12.
 */

public interface IGiftSendView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void getLGiftDataCallback(LGiftListBean response);
    void sendGifts2ServerCallback(String response);
}
