package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.LGiftListBean;

import okhttp3.Call;

/**
 * Created by an on 2016/11/17 0017.
 */

public interface IRefreshFindView {
    void requestNetError(Call call, Exception e, String exceptSource);

    void getGiftListDataSucess(LGiftListBean str);

    void getHostAnchorsSucess(String response, String operType);

    void getRecommendSucess(String response, String operType);

    void getBiggieSucess(String response, String operType);

    void getCarouselSucess(String response);
}
