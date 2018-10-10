package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/18.
 */

public interface IMySettingView
{
    /**
     * 网络原因错误回调
     * @param call
     * @param e
     * @param exceptSource
     */
    void requestNetError(Call call, Exception e, String exceptSource);

    /**
     * 获得app版本
     * @param str
     */
    void getVersionCodeCallback(String str);

    /**
     * 获取公司联系方式
     * @param str
     */
    void getContactCallback(String str);

    /**
     * 消息推送开关状态
     * @param str
     */
    void getStatCallBack(String str);

    /**
     * 改变订餐信息展示开关
     * @param map
     */
    void changeOrderStatCallback(ArrayMap<String, Object> map);

    /**
     * 改变消息推送开关
     * @param body
     * @param status
     */
    void changePushStatCallback(String body, String status);

    /**
     * 获取缓存大小
     * @param length 缓存大小(byte)
     */
    void getFileLengthCallback(long length);
}
