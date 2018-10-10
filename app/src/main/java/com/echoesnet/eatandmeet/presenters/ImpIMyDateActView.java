package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyDateActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/2/28.
 */

public class ImpIMyDateActView
{
    private final String TAG = ImpIMyDateActView.class.getSimpleName();
    private Context mContext;
    private IMyDateActView dateActView;

    public ImpIMyDateActView(Context mContext, IMyDateActView dateActView)
    {
        this.mContext = mContext;
        this.dateActView = dateActView;
    }

    public void queryRedStatus()
    {
        Map<String, String> map = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (dateActView != null)
                    dateActView.queryRedStatusCallBack(response);
            }
        },NetInterfaceConstant.AppointmentC_queryRedStatus_v420, map);
    }

}
