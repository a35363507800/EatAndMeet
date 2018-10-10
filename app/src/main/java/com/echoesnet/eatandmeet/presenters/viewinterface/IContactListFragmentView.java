package com.echoesnet.eatandmeet.presenters.viewinterface;


import com.echoesnet.eatandmeet.models.bean.EaseUser;

import okhttp3.Call;

/**
 * Created by an on 2016/12/12.
 */

public interface IContactListFragmentView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void getContactListFromServerCallback(String response);
    void deleteContactFromServerCallback(String response, EaseUser toDeleteUser);
}
