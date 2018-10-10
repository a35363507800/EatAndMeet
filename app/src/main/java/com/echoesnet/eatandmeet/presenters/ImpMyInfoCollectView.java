package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.CollectBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoCollectView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/15.
 */

public class ImpMyInfoCollectView
{

    private final String TAG = ImpMyInfoCollectView.class.getSimpleName();
    private Activity mAct;
    private IMyInfoCollectView iMyInfoCollectView;

    public ImpMyInfoCollectView(Activity mAct, IMyInfoCollectView iMyInfoCollectView)
    {
        this.mAct = mAct;
        this.iMyInfoCollectView = iMyInfoCollectView;
    }

    public void getCollectData()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (iMyInfoCollectView != null)
                    iMyInfoCollectView.requestNetErrorCallback(NetInterfaceConstant.USERC_MYCOLLECT_V422,throwable);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iMyInfoCollectView != null)
                    iMyInfoCollectView.callServerErrorCallback(NetInterfaceConstant.USERC_MYCOLLECT_V422,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<CollectBean> resLst = new Gson().fromJson(response, new TypeToken<List<CollectBean>>()
                {
                }.getType());
                if (iMyInfoCollectView != null)
                    iMyInfoCollectView.getCollectDataCallback(resLst);
            }
        },NetInterfaceConstant.USERC_MYCOLLECT_V422,reqParamMap);
    }

    public void deleteCollectData(String rIds)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.rIds, rIds);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iMyInfoCollectView != null)
                    iMyInfoCollectView.deleteCollectDataCallback(response);
            }
        },NetInterfaceConstant.UserC_delCollect,reqParamMap);
    }
}
