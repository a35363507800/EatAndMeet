package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Created by an on 2016/12/20.
 */

public interface ILiveShowBootyCallActView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void sendReceiveCallback(String response);
}
