package com.echoesnet.eatandmeet.presenters;

import android.view.View;

import com.echoesnet.eatandmeet.activities.NotificationCenterAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.INotifyCenterPre;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by lc on 2017/7/10 16.
 */

public class ImpINotifyCenterPre extends BasePresenter<NotificationCenterAct> implements INotifyCenterPre
{
    private final String TAG = ImpINotifyCenterPre.class.getSimpleName();

    @Override
    public void getAllNotification(String startIdx, String num, final String type)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.startIdx, startIdx);
        reqParamMap.put(ConstCodeTable.num, num);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                Logger.t(TAG).d("返回参数》》" + response.toString());
                super.onNext(response);
                if (getView() != null)
                {
                    getView().requestAllNotifyCallback(response, type);
                }

            }
        }, NetInterfaceConstant.MessageC_queryMessage, reqParamMap);
    }


    @Override
    public void ignoreUnread()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                Logger.t(TAG).d("返回参数》》" + response.toString());
                super.onNext(response);
                if (getView() != null)
                    getView().ignoreUnreadCallBack(response);
            }
        }, NetInterfaceConstant.MessageC_ignoreUnread, reqParamMap);
    }

    @Override
    public void focusPerson(String luId, final String operFlag, final int position, final View view)
    {

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.operFlag, operFlag);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回参数》》" + response.toString());
                if (getView() != null)
                    getView().focusCallBack(response, position, operFlag, view);
            }
        }, NetInterfaceConstant.LiveC_focus, reqParamMap);
    }

    @Override
    public void deleteMessage(String messageId, final int position)
    {

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.messageId, messageId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回参数》》" + response.toString());
                if (getView() != null)
                    getView().deleteMessageCallBack(response, position);
            }
        }, NetInterfaceConstant.MessageC_deleteMessage, reqParamMap);
    }

}
