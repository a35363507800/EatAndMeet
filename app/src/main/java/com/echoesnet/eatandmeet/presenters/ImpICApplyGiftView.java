package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.CApplyGiftAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICApplyGiftView;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpICApplyGiftView extends BasePresenter<ICApplyGiftView>
{
    private static final String TAG = ImpICApplyGiftView.class.getSimpleName();

    /**
     * 见面礼申请(升级版)
     *
     * @param toAddUserUid
     */
    public void applyFriendByMoney2(String toAddUserUid, final String reason, final String payType,
                                    String amount, String streamId, final String passWord,
                                    final GridPasswordView gridPasswordView)
    {
        final ICApplyGiftView mICApplyGiftView = getView();
        if (mICApplyGiftView == null)
            return;
        Context mContext = (CApplyGiftAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.lUId, toAddUserUid);
        reqParamMap.put(ConstCodeTable.gift, reason);
        reqParamMap.put(ConstCodeTable.payType, payType);
        reqParamMap.put(ConstCodeTable.amount, amount);
        reqParamMap.put(ConstCodeTable.streamId, streamId);
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
                if (mICApplyGiftView!=null)
                    mICApplyGiftView.callServerErrorCallback(NetInterfaceConstant.NeighborC_giftFriend,apiE.getErrorCode(),apiE.getErrBody(),gridPasswordView);
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (mICApplyGiftView!=null)
                    mICApplyGiftView.requestNetErrorCallback(NetInterfaceConstant.NeighborC_giftFriend,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (mICApplyGiftView != null)
                    mICApplyGiftView.applyFriendByMoneyCallback(response, gridPasswordView, reason);
            }
        },NetInterfaceConstant.NeighborC_giftFriend,reqParamMap);
    }
}
