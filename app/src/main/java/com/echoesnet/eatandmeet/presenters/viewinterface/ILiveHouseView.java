package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ChosenAdminBean;

import java.util.List;

import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/6/15
 * @description 定义房管列表及取消房管接口
 */

public interface ILiveHouseView
{

    void requestNetError(Call call, Exception e, String exceptSource);

    void getHouseManageListCallback(List<ChosenAdminBean> response);

    void unAdminCallback(String response, int position, ChosenAdminBean bean);
}
