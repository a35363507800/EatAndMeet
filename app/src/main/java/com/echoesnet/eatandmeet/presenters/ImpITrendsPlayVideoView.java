package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;
import android.util.Log;

import com.echoesnet.eatandmeet.activities.TrendsPlayVideoAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

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
 * @createDate 2017/7/26 0026
 * @description
 */
public class ImpITrendsPlayVideoView extends BasePresenter<TrendsPlayVideoAct>
{
    private final String TAG = ImpITrendsPlayVideoView.class.getSimpleName();

    /**
     * 关注
     *
     * @param lUId
     * @param operFlag
     */
    public void focusUser(String lUId,String operFlag)
    {
        if (getView() == null|| TextUtils.isEmpty(lUId))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.lUId, lUId);
        params.put(ConstCodeTable.operFlag, operFlag);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("关注" + response.getBody().toString());
                if ("0".equals(response.getStatus()))
                {
                    getView().focusCallBack();
                }
            }
        },NetInterfaceConstant.LiveC_focus,null,params);
    }



    /**
     * 获取是否关注
     *
     * @param lUId
     */
    public void getUsersRelationship(String lUId)
    {
        if (getView() == null|| TextUtils.isEmpty(lUId))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.lUId, lUId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("是否关注" + response.getBody().toString());
                if ("0".equals(response.getStatus()))
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(response.getBody());
                        if (getView() != null)
                            getView().getIsFocusCallback(jsonObject.getString("focus"));
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d("解析异常" + e.getMessage());
                    }
                }
            }
        },NetInterfaceConstant.UserC_usersRelationship,null,params);
    }


    /**
     * 双十一 发送红包
     *
     * @param tid
     */
    public void sendRed(String tid)
    {
        if (getView() == null || TextUtils.isEmpty(tid))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.tId,tid );
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if ("0".equals(response.getStatus()))
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(response.getBody());
                        if(getView()!=null)
                        {
                            getView().sendRedCallback(jsonObject.getString("red"));
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d("解析异常" + e.getMessage());
                    }
                }
            }
        },NetInterfaceConstant.SinglesDayC_sendRed,null,params);
    }

    /**
     * 双十一 领红包
     *
     * @param tid
     */
    public void getRed(String tid)
    {
        if (getView() == null|| TextUtils.isEmpty(tid))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.tId,tid );
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getRedCallback(response.getBody());
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().onError(NetInterfaceConstant.SinglesDayC_getRed);
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView() != null)
                    getView().onError(NetInterfaceConstant.SinglesDayC_getRed);
            }
        },NetInterfaceConstant.SinglesDayC_getRed,null,params);
    }
    /**
     * 双十一 分享
     *
     * @param tid
     */
    public void shareRed(String tid)
    {
        if (getView() == null|| TextUtils.isEmpty(tid))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.tId,tid );
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    JSONObject jsonObject = new JSONObject(response.getBody());
                    if (getView() != null)
                        getView().shareRedCallback(jsonObject.getString("amount"), jsonObject.getString("income"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        },NetInterfaceConstant.SinglesDayC_share,null,params);
    }

    /**
     * 获取看过你视频的收益然后弹窗
     */
    public  void getMyRedIncome()
    {
        Map<String, String> param = NetHelper.getCommonPartOfParam(getView());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                if (getView() == null)
                    return;
                super.onNext(response);
                Logger.t(TAG).d("查询红包返回>>" + response.getBody());
                if (getView() != null)
                    getView().getMyRedInComeCallback(response.getBody());
            }

        }, NetInterfaceConstant.SinglesDayC_myIncome, "", param);
    }
}
