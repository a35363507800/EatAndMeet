package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.activities.TrendsMsgAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.TrendsMsgBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsMsg;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/25 0025
 * @description
 */
public class ImpITrendsMsgPre extends BasePresenter<TrendsMsgAct> implements ITrendsMsg
{
    private final String TAG = ImpITrendsMsgPre.class.getSimpleName();
    private Gson gson;

    public ImpITrendsMsgPre()
    {
        gson = new Gson();
    }

    @Override
    public void getTrendsMsgList(final String type, String start, String num)
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.startIdx, start);
        params.put(ConstCodeTable.num, num);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("动态消息列表" + response.getBody().toString());
                if (getView()!=null)
                {
                getView().getTrendsMsgListCallBack(type, (List<TrendsMsgBean>) gson.fromJson(response.getBody(), new TypeToken<List<TrendsMsgBean>>()
                {
                }.getType()));
                }
            }
        }, NetInterfaceConstant.TrendC_trendMsgList, null, params);
    }

    @Override
    public void cleanTrendMsg()
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView() != null)
                    getView().requestError(NetInterfaceConstant.TrendC_cleanTrendMsg);
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("清除互动通知" + response.getBody().toString());
                if (getView() != null)
                    getView().cleanTrendMsgListCallBack();
            }
        }, NetInterfaceConstant.TrendC_cleanTrendMsg, null, params);
    }
}
