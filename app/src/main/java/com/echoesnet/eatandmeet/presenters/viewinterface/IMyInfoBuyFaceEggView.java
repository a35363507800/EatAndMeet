package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import com.jungly.gridpasswordview.GridPasswordView;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/29.
 */

public interface IMyInfoBuyFaceEggView
{
    /**
     * 失败
     *
     * @param interfaceName    接口名称
     * @param code             错误码
     * @param errBody          错误内容
     * @param gridPasswordView
     */
    void callServerErrorCallback(String interfaceName, String code, String errBody,
                                 GridPasswordView gridPasswordView);

    /**
     * 网络错误
     *
     * @param interfaceName 接口名称
     * @param e             异常
     */
    void requestNetErrorCallback(String interfaceName, Throwable e);


    /**
     * 脸蛋购买列表接口回调
     *
     * @param response 接口返回内容
     */
    void getFaceRechargeListCallBack(String response);

    /**
     * 脸蛋充值
     *
     * @param str              返回结果
     * @param gridPasswordView
     */
    void postFaceEggRecharge2CallBack(String str, GridPasswordView gridPasswordView);
}
