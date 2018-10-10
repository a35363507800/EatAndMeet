package com.echoesnet.eatandmeet.presenters.viewinterface;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/24 12:08
 * @description
 */

public interface ICommunicatePre
{
    void checkRedPacketsStates( String userName,  boolean deleteMessage, List<String> redPacketIds);

}
