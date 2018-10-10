package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.view.View;

import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/14 16:32
 * @description
 */

public interface IMomentsView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void getTrendsCallback(String type, String msgNum, List<FTrendsItemBean> trendsList);
    void getUserTrendsCallback(String type, List<FTrendsItemBean> trendsList);
    void getLikeTrendsSuccessCallback(View view, int position, String flg, int likeNum);
    void deleteCommentSuc(int position);
    void PublishSuccess(String tid, String stamp);
}
