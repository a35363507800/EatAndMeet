package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.activities.GameAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGame;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/8/16 0016
 * @description
 */
public class ImpIGamePre extends BasePresenter<GameAct> implements IGame
{
    @Override
    public void shareH5(String gameId)
    {
        if (getView() == null)
            return;
        Map<String,String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.gameId,gameId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    JSONObject jsonObject = new JSONObject(response.getBody());
                    getView().shareH5Callback(jsonObject.getString("url"),jsonObject.getString("shareIcon"),
                            jsonObject.getString("shareContent"),jsonObject.getString("shareTitle"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.GameC_shareH5,null,params);
    }

    @Override
    public void shareGame(String gameId, String matchingId, final String type, String score)
    {
        if (getView() == null)
            return;
        Map<String,String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.gameId,gameId);
        params.put(ConstCodeTable.matchingId,matchingId);
        params.put(ConstCodeTable.share,type);
        params.put(ConstCodeTable.score,score);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                if (getView() == null)
                    return;
                super.onNext(response);
                getView().shareGameCallback(type);
            }
        }, NetInterfaceConstant.GameC_share,null,params);
    }

    @Override
    public void enterGame(String gameId)
    {
        Map<String,String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.gameId,gameId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                if (getView() == null)
                    return;
                super.onNext(response);
                getView().enterGameCallback(response.getBody());
            }
        }, NetInterfaceConstant.GameC_enterGame,null,params);
    }

    @Override
    public void exitGame(String gameId)
    {
        Map<String,String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.gameId,gameId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                if (getView() == null)
                    return;
                super.onHandledError(apiE);
                getView().exitGameFail();
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                if (getView() == null)
                    return;
                super.onHandledNetError(throwable);
                getView().exitGameFail();
            }

            @Override
            public void onNext(ResponseResult response)
            {
                if (getView() == null)
                    return;
                super.onNext(response);
                getView().exitGameCallback();
            }
        }, NetInterfaceConstant.GameC_exitGame,null,params);
    }
}
