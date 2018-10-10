package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.activities.MyInfoAccountAct2;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIMyInfoAccountView extends BasePresenter<MyInfoAccountAct2>
{
    private final String TAG = ImpIMyInfoAccountView.class.getSimpleName();

    /**
     * 获取账户余额接口信息
     */
    public void getAccountData()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().getAccountDataCallback(response);
            }

            @Override
            public void onHandledError(ApiException apiE) {
                if(!ErrorCodeTable.SIGN_ANCHOR_NOT_WITHDRAW.equals(apiE.getErrorCode()))
                super.onHandledError(apiE);

            }
        }, NetInterfaceConstant.UserC_balance, reqParamMap);
    }

    public void getMyBalance()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getMyBalanceCallBack(response);
            }

            @Override
            public void onHandledError(ApiException apiE) {
                if(!ErrorCodeTable.SIGN_ANCHOR_NOT_WITHDRAW.equals(apiE.getErrorCode()))
                    super.onHandledError(apiE);

            }

        }, NetInterfaceConstant.WithdrawC_myBalance, reqParamMap);
    }

    /**
     * @author lc
     * 获得新版查询充值列表接口
     */
    public void getNewAccountData()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d(">>>>>>>返回结果：" + response);
                if (getView() != null)
                    getView().getNewAccountDataCallback(response);
            }

            @Override
            public void onHandledError(ApiException apiE) {
                if(!ErrorCodeTable.SIGN_ANCHOR_NOT_WITHDRAW.equals(apiE.getErrorCode()))
                    super.onHandledError(apiE);

            }

        }, NetInterfaceConstant.DealDetailC_recharge, reqParamMap);
    }

    /**
     * 获取我的等级接口信息
     */
    public void getMyLevelData()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getMyLevelDataCallback(response);
            }


            @Override
            public void onHandledError(ApiException apiE) {
                if(!ErrorCodeTable.SIGN_ANCHOR_NOT_WITHDRAW.equals(apiE.getErrorCode()))
                    super.onHandledError(apiE);

            }

        }, NetInterfaceConstant.UserC_myLevel, reqParamMap);
    }

    /**
     * 获取绑定状态
     */
    public void getBindStats()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.LiveC_validate, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().getBindStatsCallBack(response);
            }
        }, NetInterfaceConstant.LiveC_validate, reqParamMap);

    }


}
