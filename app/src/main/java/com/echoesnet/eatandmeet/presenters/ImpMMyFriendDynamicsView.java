package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MyFriendDynamicsBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMMyFriendDynamicsView;
import com.echoesnet.eatandmeet.utils.MD5Util;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/28.
 */

public class ImpMMyFriendDynamicsView {
    private final String TAG = ImpMMyFriendDynamicsView.class.getSimpleName();
    private Context mContext;
    private IMMyFriendDynamicsView imMyFriendDynamicsView;

    public ImpMMyFriendDynamicsView(Context mContext, IMMyFriendDynamicsView imMyFriendDynamicsView) {
        this.mContext = mContext;
        this.imMyFriendDynamicsView = imMyFriendDynamicsView;
    }

    public void getFriUpdates(final String getItemStartIndex, String getItemNum) {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (imMyFriendDynamicsView != null)
                    imMyFriendDynamicsView.requestNetErrorCallback(NetInterfaceConstant.MsgC_friUpdates,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (imMyFriendDynamicsView != null)
                    imMyFriendDynamicsView.getFriUpdatesCallback(new Gson().fromJson(response, new TypeToken< List<MyFriendDynamicsBean>>(){}.getType()), getItemStartIndex);
            }
        },NetInterfaceConstant.MsgC_friUpdates,reqParamMap);
    }

}
