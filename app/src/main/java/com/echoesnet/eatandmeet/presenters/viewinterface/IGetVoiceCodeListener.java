package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Created by Administrator on 2017/3/8.
 */

public interface IGetVoiceCodeListener
{
    void onSuccess();
    void onFailed(String errorCode);
}
