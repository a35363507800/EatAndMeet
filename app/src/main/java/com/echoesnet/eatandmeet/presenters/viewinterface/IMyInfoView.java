package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/14.
 */

public interface IMyInfoView
{

    /**
     * 向后台发起的请求返回错误的回调
     *
     * @param interfaceName  接口名称
     * @param code   错误码
     * @param errBody  错误内容
     */
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    /**
     * 获得是否显示排行榜
     * @param receive
     */
    void getLivePlaySwapCallback(String receive);

    /**
     * 我的页面信息接口的回调
     * @param response 接口返回的内容
     */
    void getMyinfoCallBack(String response);
}
