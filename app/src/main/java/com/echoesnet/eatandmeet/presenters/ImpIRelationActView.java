package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.presenters.viewinterface.IRelationActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * @author Administrator
 * @Date 2017/8/1
 * @Version 1.0
 */

public class ImpIRelationActView extends BasePresenter<IRelationActView>
{
    private static final String TAG = ImpIRelationActView.class.getSimpleName();
    private Activity mAct;
    private IRelationActView iRelationActView;

    public ImpIRelationActView(Activity mAct, IRelationActView iRelationActView)
    {
        this.mAct = mAct;
        this.iRelationActView = iRelationActView;
    }

    public void queryUsersRelationShip(final EaseUser user)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, user.getuId());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (iRelationActView != null)
                    iRelationActView.queryUsersRelationShipCallBack(response.getBody(),user);
            }
        }, NetInterfaceConstant.UserC_usersRelationship, null, reqParamMap);
    }

    /**
     *
     * 国庆活动赠送卡片
     * @param luid
     * @param cardId
     * @param source "0：系统消息，1：活动页面"
     */
    public void giveCard(String luid,String cardId,String source, final String param, final EaseUser user)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, luid);
        reqParamMap.put(ConstCodeTable.source, source);
        reqParamMap.put(ConstCodeTable.cardId, cardId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (iRelationActView != null)
                    iRelationActView.giveCardCallback(param,user);
            }
        }, NetInterfaceConstant.ChristmasC_giveCard, null, reqParamMap);
    }
    /**
     *
     * 国庆活动索要卡片
     * @param luid
     * @param cardId
     */
    public void askCard(String luid, String cardId, final String param, final EaseUser user)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, luid);
        reqParamMap.put(ConstCodeTable.cardId, cardId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (iRelationActView != null)
                    iRelationActView.askCardCallback(param,user);
            }
        }, NetInterfaceConstant.ChristmasC_askCard, null, reqParamMap);
    }

    public void sendFirstTalk(String luId, final EMMessage message, final EaseUser user)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("首次聊天返回参数》》" + response.toString());
                if (iRelationActView != null)
                    iRelationActView.firstTalkCallback(response.getBody(),message,user);
            }
        }, NetInterfaceConstant.FriendC_firstTalk, null, reqParamMap);
    }

}
