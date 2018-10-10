package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.activities.TrendsMsgAct;
import com.echoesnet.eatandmeet.activities.TrendsPraiseListAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.TrendsMsgBean;
import com.echoesnet.eatandmeet.models.bean.TrendsPraiseBean;
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
public class ImpITrendsPraiseListView extends BasePresenter<TrendsPraiseListAct>
{
    private final String TAG = ImpITrendsPraiseListView.class.getSimpleName();
    private Gson gson;

    public ImpITrendsPraiseListView()
    {
        gson = new Gson();
    }

    public void getTrendsPraiseList(final String type, String start, String num,String tId)
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.startIdx, start);
        params.put(ConstCodeTable.num, num);
        params.put(ConstCodeTable.tId, tId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("动态消息列表" + response.getBody().toString());
                getView().getMyTrendsListCallBack(type,(List<TrendsPraiseBean>) gson.fromJson(response.getBody(),new TypeToken<List<TrendsPraiseBean>>(){}.getType()));
            }
        }, NetInterfaceConstant.TrendC_likeMyTrendList, null, params);
    }
}
