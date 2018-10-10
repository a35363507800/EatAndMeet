package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MyInfoSystemRemindBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMMySystemInfoRemindView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/28.
 */

public class ImpMySystemInfoRemindView
{
    private final String TAG = ImpMySystemInfoRemindView.class.getSimpleName();
    private Context mContext;
    private IMMySystemInfoRemindView imMySystemInfoRemindView;

    public ImpMySystemInfoRemindView(Context mContext, IMMySystemInfoRemindView imMySystemInfoRemindView)
    {
        this.mContext = mContext;
        this.imMySystemInfoRemindView = imMySystemInfoRemindView;
    }

    public void getSystemData(final String getItemStartIndex, String getItemNum, final boolean isPullDown)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<MyInfoSystemRemindBean> myInfoOrderRemindList = new Gson().fromJson(response, new TypeToken< List<MyInfoSystemRemindBean>>(){}.getType());
                if (imMySystemInfoRemindView != null)
                    imMySystemInfoRemindView.getSystemMsgCallback(myInfoOrderRemindList, getItemStartIndex, isPullDown);
            }
        },NetInterfaceConstant.MsgC_systemMsg,reqParamMap);
    }

    public void ignoreBind(MyInfoSystemRemindBean bean)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.type, bean.getTip());
        reqParamMap.put(ConstCodeTable.consultant, bean.getUId());
        reqParamMap.put(ConstCodeTable.context, bean.getMsg());
        reqParamMap.put(ConstCodeTable.date, bean.getDate());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (imMySystemInfoRemindView != null)
                    imMySystemInfoRemindView.ignoreBindCallback(response);
            }
        },NetInterfaceConstant.ConsultantC_ignoreBind,reqParamMap);
    }

    public void bindDiningConsultant(String consultantId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.consultant, consultantId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if ( imMySystemInfoRemindView!= null)
                imMySystemInfoRemindView.requestNetErrorCallback(NetInterfaceConstant.ConsultantC_BindConsultant,apiE);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (imMySystemInfoRemindView != null)
                    imMySystemInfoRemindView.bindConsultantCallback(response);
            }
        },NetInterfaceConstant.ConsultantC_BindConsultant,reqParamMap);

    }

}
