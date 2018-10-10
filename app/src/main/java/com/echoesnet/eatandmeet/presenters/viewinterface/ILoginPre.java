package com.echoesnet.eatandmeet.presenters.viewinterface;

import cn.sharesdk.framework.Platform;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/6 10:20
 * @description
 */

public interface ILoginPre
{
    void getTokenId( int type);
    void login(String mobile, String passWord);
    void weChatLogin(Platform platform);
}
