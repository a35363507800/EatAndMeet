package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.CAddEmojBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface ICAddEmojsView
{
    /**
     *
     * @param call
     * @param e
     * @param exceptSource
     */
    void requestNetError(Call call, Exception e, String exceptSource);
    void getEmojDataLstCallback(List<CAddEmojBean> emojLst);
}
