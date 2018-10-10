package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Created by Administrator on 2016/12/29.
 */

public interface IComplaintUserView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Exception e);

    void commitMessageCallback(String response);
}
