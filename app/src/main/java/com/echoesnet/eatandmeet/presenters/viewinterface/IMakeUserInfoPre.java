package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.UsersBean;

/**
 * Created by Administrator on 2016/11/18.
 */

public interface IMakeUserInfoPre
{
     void getRegisterPresent();
     void inputUserInfo(final UsersBean usersBean, final String userName, final String psw);
}
