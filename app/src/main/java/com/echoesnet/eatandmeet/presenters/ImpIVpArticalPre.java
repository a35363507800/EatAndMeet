package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.VpArticalFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.bean.VpArticalBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IVpArticalPre;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.Constants;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/9/12
 * @description
 */
public class ImpIVpArticalPre extends BasePresenter<VpArticalFrg> implements IVpArticalPre
{
    private static final String TAG = ImpIVpArticalPre.class.getSimpleName();


    @Override
    public void likeArtical(final int position, final String flg, final String vArticalId, final String likeNum)
    {
        //vArticalId  专栏文章Id
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
        params.put(ConstCodeTable.flg, flg);
        params.put(ConstCodeTable.tId, vArticalId);

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
                    } else if (likeNumInt > 0)
                    {
                        likeNumInt--;
                    }
                    if (getView() != null)
                        getView().praiseClickCallBack(position, "1".equals(flg) ? "0" : "1", likeNumInt);
                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }, NetInterfaceConstant.TrendC_likeTrend, params);


    }

    @Override
    public void getArticalList(String uId, String startIdex, String Nums, final String type)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(getView().getActivity());
        reqParam.put(ConstCodeTable.num, Nums);
        reqParam.put(ConstCodeTable.startIdx, startIdex);
        reqParam.put(ConstCodeTable.lUId, uId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                Logger.t(TAG).d(response.toString());
                super.onNext(response);
                if (getView() != null)
                    getView().getArticalListCallBack(type, (List<FTrendsItemBean>) EamApplication.getInstance().getGsonInstance()
                            .fromJson(response, new TypeToken<List<FTrendsItemBean>>()
                            {
                            }.getType()));
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_articleList, apiE.getErrorCode(), apiE.getErrBody());
            }
        }, NetInterfaceConstant.TrendC_articleList, reqParam);
    }
}
