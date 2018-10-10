package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.jungly.gridpasswordview.GridPasswordView;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier zdw
 * @createDate 2017/6/15
 * @description 获取红包额度及发送红包接口定义
 */

public interface ILiveSendPacketView {

    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName,Throwable e);

    void requestLiveRedPacket(String response);

    void sendRedPackageByServer(String response, GridPasswordView gridPasswordView);
}
