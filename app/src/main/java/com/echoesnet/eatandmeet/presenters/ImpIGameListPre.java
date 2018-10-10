package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.GameListFrg;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.GameItemBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGameListPre;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.serverdatacache.Elixir;
import com.google.gson.Gson;
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
 * @createDate 2017/10/27
 * @description
 */
public class ImpIGameListPre extends BasePresenter<GameListFrg> implements IGameListPre
{
    private final String TAG = ImpIGameListPre.class.getSimpleName();
    @Override
    public void getGameList()
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(final String response)
            {
                super.onNext(response);
                 Logger.t(TAG).d("response>>"+response);
                Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.GameC_gameList, response);
                try
                {
                    if (getView()!=null)
                    getView().getGameListCallback((List<GameItemBean>) new Gson().fromJson(response, new TypeToken<List<GameItemBean>>()
                    {
                    }.getType()));
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.GameC_gameList, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        if (getView() != null)
                            getView().getGameListCallback((List<GameItemBean>) new Gson().fromJson(response, new TypeToken<List<GameItemBean>>()
                            {
                            }.getType()));
                    }

                    @Override
                    public void onError(String code, String msg)
                    {

                    }
                });
            }
        }, NetInterfaceConstant.GameC_gameList, params);
    }
}
