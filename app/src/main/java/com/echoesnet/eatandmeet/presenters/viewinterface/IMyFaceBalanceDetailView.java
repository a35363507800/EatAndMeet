package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.FaceBalanceDetailBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/15.
 */

public interface IMyFaceBalanceDetailView {
    void requestNetError(Call call, Exception e, String exceptSource);
    void getBalanceDetailDataCallback(List<FaceBalanceDetailBean> resLst , String operateType);
}
