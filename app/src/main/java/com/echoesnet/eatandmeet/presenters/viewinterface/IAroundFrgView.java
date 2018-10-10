package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.presenters.ImpIAroundPre;

import java.util.List;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/7/28
 * @description
 */

public interface IAroundFrgView
{
    void refreshPhoneContactCallback();

    void getPhoneContactLstCallback(List<ImpIAroundPre.ContactEntity> contactEntities);

    void loadSayHelloAndTrendsNumCallback(String response);

}
