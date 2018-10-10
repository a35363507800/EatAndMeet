package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/22.
 */

public interface IDBigVcomDetailView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void getBigVSupportCountCallBack(String response);

    void supportBigVCallBack(String response);

}
