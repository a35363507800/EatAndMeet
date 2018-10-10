package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMMyInfoCenterMessageView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIMMyInfoCenterMessageView
{

    private final String TAG = ImpIMMyInfoCenterMessageView.class.getSimpleName();
    private Context mContext;
    private IMMyInfoCenterMessageView imMyInfoCenterMessageView;

    public ImpIMMyInfoCenterMessageView(Context context, IMMyInfoCenterMessageView imMyInfoCenterMessageView)
    {
        this.mContext = context;
        this.imMyInfoCenterMessageView = imMyInfoCenterMessageView;
    }

    public void getMsg()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (imMyInfoCenterMessageView != null)
                    imMyInfoCenterMessageView.getMessageCallback(response);
            }
        }, NetInterfaceConstant.MsgC_queryStat, reqParamMap);
    }
}
