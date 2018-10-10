package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/6.
 */

public interface IMyCommentView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void getCommentTextCallback(String response);
    void postResCommentTextCallback(String response);
    void postResCommentPicCallback(String response);
}
