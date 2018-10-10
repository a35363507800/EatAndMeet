package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;

/**
 * Created by ben on 2016/11/29.
 * app主页面功能回调接口
 */
public interface IHomeActView
{
    /**
     * 向后台发起的请求返回错误的回调
     *
     * @param interfaceName 接口名称
     * @param code          错误码
     * @param errBody       错误信息
     */
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    /**
     * 向后台发起的请求由于网络原因失败的回调
     *
     * @param interfaceName 接口名称
     * @param e             异常
     */
    void requestNetErrorCallback(String interfaceName, Throwable e);

    /**
     * 获取最新版本号回调.
     *
     * @param str 后台返回内容
     */
    void getVersionCodeCallback(String str);

    /**
     * 向后台上传用户当前位置成功的回调.
     *
     * @param response 后台返回内容
     */
    void postDateUserLocationCallback(ResponseResult response);

    /**
     * 向后台发送用户位置开关状态成功的回调.
     *
     * @param body
     */
    void sendLocationSwitchCallback(String body);

    /**
     * 更新消息红点状态成功的回调.
     *
     * @param task
     * @param successes
     * @param receive
     */
    void updateTaskOkCallBack(String task, String successes, String receive);
}
