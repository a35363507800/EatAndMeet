package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.ClubDetailAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ClubDetailBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubDetailPre;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2018/2/5
 * @description
 */
public class ImpIClubDetailPre extends BasePresenter<ClubDetailAct> implements IClubDetailPre
{
    private static final String TAG = ImpIClubDetailPre.class.getSimpleName();

    @Override
    public void getClubDetail(String clubId,boolean isCheckOffLine)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.id, clubId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                if (TextUtils.equals("HOMEPARTY_OFFLINE",apiE.getErrorCode()))
                {
                    if (getView()!=null)
                        getView().callServerErrorCallback(NetInterfaceConstant.HomepartyC_partyDetails,apiE.getErrorCode(),apiE.getErrBody());
                }
                else
                {
                    super.onHandledError(apiE);
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetErrorCallback(NetInterfaceConstant.HomepartyC_partyDetails,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                {
                    if (!isCheckOffLine)
                    getView().lookClubDetailCallBack(response);
                }

            }
        }, NetInterfaceConstant.HomepartyC_partyDetails, reqParamMap);
    }

    @Override
    public void collectedClub(String clubId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.id, clubId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView() != null)
                    getView().requestNetErrorCallback(NetInterfaceConstant.HomepartyC_collect,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().collectedClubCallback(response);
            }
        }, NetInterfaceConstant.HomepartyC_collect, reqParamMap);
    }

    @Override
    public void removeClub(String clubId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.id, clubId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView() != null)
                    getView().requestNetErrorCallback(NetInterfaceConstant.HOMEPARTYC_DEL_COLLECT,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().removeClubCallback(response);
            }
        }, NetInterfaceConstant.HOMEPARTYC_DEL_COLLECT, reqParamMap);
    }

    @Override
    public void getPartyComment(String num, String startIndex, String clubId,String type)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());

        reqParamMap.put(ConstCodeTable.num, num);
        reqParamMap.put(ConstCodeTable.startIdx, startIndex);
        reqParamMap.put(ConstCodeTable.id, clubId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的ktv评论s：" + response);
                try
                {
                    JSONObject object = new JSONObject(response);
                    String comments = object.getString("comments");

                    if (getView() != null)
                        getView().getClubCommentCallback((List<ClubDetailBean.CommentsBean>) new Gson().fromJson(comments, new TypeToken<List<ClubDetailBean.CommentsBean>>()
                        {
                        }.getType()),type);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, NetInterfaceConstant.HomepartyC_partyComment, reqParamMap);
    }


}
