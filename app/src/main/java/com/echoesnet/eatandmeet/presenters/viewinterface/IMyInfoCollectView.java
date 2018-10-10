package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.CollectBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/15.
 */

public interface IMyInfoCollectView
{
    /**
     * 错误回调
     *
     * @param interfaceName
     * @param code
     * @param errBody
     */
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    /**
     * 网络错误回调
     *
     * @param interfaceName
     * @param e
     */
    void requestNetErrorCallback(String interfaceName, Throwable e);

    /**
     * 获取收藏列表信息回调
     * @param response
     */
    void getCollectDataCallback(List<CollectBean> response);

    /**
     * 删除收藏接口回调
     * @param response
     */
    void deleteCollectDataCallback(String response);
}
