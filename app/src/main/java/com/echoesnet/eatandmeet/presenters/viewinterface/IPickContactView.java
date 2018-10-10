package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by an on 2016/12/6 0006.
 */

public interface IPickContactView {
    void requestNetError(Call call, Exception e, String exceptSource);
    void getContactList(String btnOnOff);
}
