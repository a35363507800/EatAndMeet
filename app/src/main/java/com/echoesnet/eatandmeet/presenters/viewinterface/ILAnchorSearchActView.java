package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.AnchorSearchBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by an on 2016/12/28 0028.
 */

public interface ILAnchorSearchActView {
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);

    void getLiveSearchCallback(List<AnchorSearchBean> response);
}
