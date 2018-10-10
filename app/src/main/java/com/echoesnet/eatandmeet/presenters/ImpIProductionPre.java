package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.VProductionFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IProductionPre;
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
 * @author ben
 * @version 1.0
 * @createDate 2017/7/14 16:33
 * @description
 */

public class ImpIProductionPre extends BasePresenter<VProductionFrg> implements IProductionPre
{
    private static final String TAG = ImpIProductionPre.class.getSimpleName();

    @Override
    public void getVTrends(final String type, String startIdx, String num)
    {
        Logger.t(TAG).d("getVTrends:"+type + "startIdx>"+ startIdx  + "num>"+num);
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(null);
        reqParam.put(ConstCodeTable.num, num);
        reqParam.put(ConstCodeTable.startIdx, startIdx);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                Logger.t(TAG).d("大V列表返回结果" + "=============" + response.getBody());
                super.onNext(response);
                if (getView() != null)
                    getView().getTrendsCallback(type, (List<FTrendsItemBean>) EamApplication.getInstance().getGsonInstance()
                            .fromJson(response.getBody(), new TypeToken<List<FTrendsItemBean>>()
                            {
                            }.getType()));

            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_columns, apiE.getErrorCode(), apiE.getErrBody());
            }

        }, NetInterfaceConstant.TrendC_columns, null, reqParam);
    }

    /**
     * 关注
     *
     * @param luId
     */
    public void focusFriendCallServer(final String luId, final FTrendsItemBean itemBean)
    {
        if (!isViewAttached())
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.operFlag, "1");
        Gson gson = new Gson();
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_focus, gson.toJson(reqParamMap));
        Logger.t(TAG).d("请求关注接口参数--> " + paramJson);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (isViewAttached())
                    getView().focusCallback(itemBean);

            }
        }, NetInterfaceConstant.LiveC_focus, null, reqParamMap);
    }
}
