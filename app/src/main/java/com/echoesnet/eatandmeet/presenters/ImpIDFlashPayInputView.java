package com.echoesnet.eatandmeet.presenters;

import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.DFlashPayInputAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author       ben
 * @modifier
 * @createDate   2016/12/13
 * @version      1.0
 * @description  闪付业务逻辑层
 */
public class ImpIDFlashPayInputView extends BasePresenter<DFlashPayInputAct>
{
    private static final String TAG = ImpIDFlashPayInputView.class.getSimpleName();

    /**
     * 获取账户余额
     */
    public void getAccountBalance()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                if (getView()!=null)
                    getView().callServerFailCallback(NetInterfaceConstant.UserC_balance,apiE.getErrorCode(),apiE.getErrBody(),null);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject body = new JSONObject(response);
                    String balance = (body.getString("balance"));
                    if (getView() != null)
                        getView().getAccountBalanceCallback(balance);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("异常》》"+e.getMessage());
                }
            }
        },NetInterfaceConstant.UserC_balance,reqParamMap);
    }

    /**
     * 闪付(升级版)
     *
     * @param resId
     * @param amount
     * @param discount 折扣
     * @param passWord
     */
    public void quickPay2(String resId, String amount, String discount, String passWord, String consultant, final GridPasswordView gridPasswordView)
    {
        final DFlashPayInputAct mAct = getView();
        if (mAct == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.rId, resId);
        reqParamMap.put(ConstCodeTable.amount, amount);
        reqParamMap.put(ConstCodeTable.consultant, consultant);
        reqParamMap.put(ConstCodeTable.scale, discount == null ? "1" : discount);
        try
        {
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(passWord));
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()

        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                Map<String,Object> tranMap = new ArrayMap<>();
                tranMap.put("gPasswordView",gridPasswordView);
                if (getView()!=null)
                    getView().callServerFailCallback(NetInterfaceConstant.OrderC_quickPay,apiE.getErrorCode(),apiE.getErrBody(),tranMap);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (mAct != null)
                    mAct.quickPayCallback(response, gridPasswordView);
            }
        },NetInterfaceConstant.OrderC_quickPay,reqParamMap);
    }

    /**
     * 获取就餐顾问
     */
    public void getMyConsultant()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                if (getView()!=null)
                    getView().callServerFailCallback(NetInterfaceConstant.ConsultantC_myConsultant,apiE.getErrorCode(),apiE.getErrBody(),
                            null);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().getMyConsultantCallback(response);
            }
        },NetInterfaceConstant.ConsultantC_myConsultant,reqParamMap);
    }

    /**
     * 查询就餐顾问
     */
    public void queryConsultant(String consultantId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.id, consultantId);
        reqParamMap.put(ConstCodeTable.consultant, "");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerFailCallback(NetInterfaceConstant.ConsultantC_queryConsultant,apiE.getErrorCode(),apiE.getErrBody(),null);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().queryConsultantCallback(response);
            }
        },NetInterfaceConstant.ConsultantC_queryConsultant,reqParamMap);
    }
}
