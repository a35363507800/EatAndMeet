package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.DynamicStateFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDynamicStatePre;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by lc on 2017/7/26 11.
 */

public class ImpIDynamicStatePre extends BasePresenter<DynamicStateFrg> implements IDynamicStatePre
{
    private static final String TAG = ImpIDynamicStatePre.class.getSimpleName();
    public ImpIDynamicStatePre()
    {
    }

    @Override
    public void getUserTrends(String luid, String startIdx, String num,final String type)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(getView().getActivity());
        reqParam.put(ConstCodeTable.num, num);
        reqParam.put(ConstCodeTable.startIdx, startIdx);
        reqParam.put(ConstCodeTable.lUId, luid);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("response>>"+response);
                if (getView() != null)
                getView().getUserTrendsCallback(type,(List<FTrendsItemBean>) EamApplication.getInstance().getGsonInstance()
                        .fromJson(response, new TypeToken<List<FTrendsItemBean>>()
                        {
                        }.getType()));
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_userTrends,apiE.getErrorCode(),apiE.getErrBody());
            }
        }, NetInterfaceConstant.TrendC_userTrends, reqParam);


    }

    /**
     * 点赞，取赞动态
     *
     * @param tId 动态id
     * @param flg 0：点赞，1：取赞
     */


    @Override

    public void likeTrends(final int position, String tId, final String flg, final String likeNum)
    {
        Map<String, String> params = NetHelper.getCommonPartOfParam(null);
        params.put(ConstCodeTable.flg, flg);
        params.put(ConstCodeTable.tId, tId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    int likeNumInt = Integer.parseInt(likeNum);
                    if ("0".equals(flg))
                    {
                        likeNumInt++;
                    }
                    else if (likeNumInt > 0)
                    {
                        likeNumInt--;
                    }
                    if (getView() != null)
                        getView().getLikeTrendsSuccessCallback(position, "1".equals(flg) ? "0" : "1", likeNumInt);
                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }, NetInterfaceConstant.TrendC_likeTrend, params);
    }
}
