package com.echoesnet.eatandmeet.presenters.viewinterface;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/27.
 */

public interface IDOrderCommentView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void postResCommentTextCallback(String response);

    void postResCommentPicCallback(JSONObject response);
}
