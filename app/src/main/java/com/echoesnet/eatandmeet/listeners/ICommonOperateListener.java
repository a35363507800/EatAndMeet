package com.echoesnet.eatandmeet.listeners;

/**
 * Created by ben on 2017/4/26.
 */

public interface ICommonOperateListener
{
    void onSuccess(String response);
    void onError(String code,String msg);
}
