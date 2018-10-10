package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.view.View;

import com.echoesnet.eatandmeet.models.bean.SearchUserBean;

import java.util.List;

/**
 * @author Administrator
 * @Date 2017/10/12
 * @Version 1.0
 */

public interface ICSearchConversationView
{
    void searchUserCallback(List<SearchUserBean> list);

    void focusCallBack(int position, View view);

}
