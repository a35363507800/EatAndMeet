package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/15.
 */

public interface IResLstInfoAdapterView {
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetError(Call call, Exception e, String exceptSource);
    void setPraiseInfoCallback(String response, final int position);
    void setUnPraiseInfoCallback(String response, final int position);
}
