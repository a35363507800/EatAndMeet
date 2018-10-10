package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.live.LiveWithDrawActivity;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IWithDrawView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import java.util.Map;

import cn.sharesdk.framework.Platform;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIWithDrawView extends BasePresenter<IWithDrawView>
{
    private final String TAG = ImpIWithDrawDetailView.class.getSimpleName();

    /**
     * 提现到微信
     */
    public void withDrawToWeChat(String withDrawMoney)
    {
        final IWithDrawView withDrawView = getView();
        if (withDrawView == null)
            return;
        Context mContext = (LiveWithDrawActivity) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.withdraw, withDrawMoney);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (withDrawView != null)
                    withDrawView.withDrawToWeChatCallBack(response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (withDrawView != null)
                    withDrawView.callServerErrorCallback(NetInterfaceConstant.WithdrawC_withdraw, apiE.getErrorCode(), apiE.getErrBody());
            }
        }, NetInterfaceConstant.WithdrawC_withdraw, reqParamMap);
    }

    /**
     * 微信授权成功后与后台绑定
     */
    public void loginWeChatSuccess(Platform platform)
    {
        final IWithDrawView withDrawView = getView();
        if (withDrawView == null)
            return;
        Context mContext = (LiveWithDrawActivity) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.access_token, platform.getDb().getToken());
        reqParamMap.put(ConstCodeTable.refresh_token, platform.getDb().get("refresh_token"));
        reqParamMap.put(ConstCodeTable.unionid, platform.getDb().get("unionid"));
        reqParamMap.put(ConstCodeTable.openId, platform.getDb().getUserId());

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (withDrawView != null)
                    withDrawView.loginWeChatSuccessCallBack(response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (withDrawView != null)
                    withDrawView.callServerErrorCallback(NetInterfaceConstant.LiveC_bindWeChat, apiE.getErrorCode(), apiE.getErrBody());
            }
        }, NetInterfaceConstant.LiveC_bindWeChat, reqParamMap);
    }


    /**
     * 获取账户余额接口信息
     */
    public void getAccountBalance()
    {
        final IWithDrawView withDrawView = getView();
        if (withDrawView == null)
            return;
        Context mContext = (LiveWithDrawActivity) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获取账户余额接口返回结果--> " + response);
                if (withDrawView != null)
                    withDrawView.getAccountBalanceCallBack(response);
            }
        }, NetInterfaceConstant.UserC_newBalance, reqParamMap);
    }

}
