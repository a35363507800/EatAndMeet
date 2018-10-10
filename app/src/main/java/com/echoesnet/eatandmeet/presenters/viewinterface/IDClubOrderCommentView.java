package com.echoesnet.eatandmeet.presenters.viewinterface;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/26
 * @description
 */
public interface IDClubOrderCommentView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void postResCommentTextCallback(String response);

    void postResCommentPicCallback(JSONObject response);
}
