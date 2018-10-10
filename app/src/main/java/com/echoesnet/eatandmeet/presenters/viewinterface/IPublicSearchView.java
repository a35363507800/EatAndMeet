package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.SearchUserBean;

import java.util.List;

import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27
 * @description
 */
public interface IPublicSearchView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void getSearchInfoCallback(List<SearchUserBean> list, String operateType);

    void focusCallBack(final int position);

}
