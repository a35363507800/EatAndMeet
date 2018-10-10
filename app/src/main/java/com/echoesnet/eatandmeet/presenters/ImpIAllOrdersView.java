package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IAllOrdersView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by an on 2016/12/6 0006.
 */

public class ImpIAllOrdersView
{
    private final String TAG = ImpIAllOrdersView.class.getSimpleName();
    private Activity mActivity;
    private IAllOrdersView iAllOrdersView;
    private Gson gson;

    public ImpIAllOrdersView(Activity mActivity, IAllOrdersView iAllOrdersView)
    {
        this.mActivity = mActivity;
        this.iAllOrdersView = iAllOrdersView;
        gson = new Gson();
    }

    /**
     * 求代付按钮开关
     */
    public void getBtnOnOff()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iAllOrdersView != null)
                    iAllOrdersView.callServerErrorCallback(NetInterfaceConstant.FriendPayC_btnOnOff,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回》》》" + response);
                if (iAllOrdersView != null)
                    iAllOrdersView.getBtnOnOffSuccess(response);
            }
        }, NetInterfaceConstant.FriendPayC_btnOnOff, reqParamMap);

    }

    /**
     * 获取所有订单
     *
     * @param getItemStartIndex
     * @param getItemNum
     * @param orderType
     * @param operateType
     */
    public void getAllOrders(String getItemStartIndex, String getItemNum, String orderType, final String operateType)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.stat, orderType);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("所有订单reponse=="+response);
                if (iAllOrdersView != null)
                    iAllOrdersView.getOrderSuccess(new Gson().fromJson(response, new TypeToken<List<OrderRecordBean>>(){}.getType()), operateType);

            }
        },NetInterfaceConstant.OrderC_order_v422,reqParamMap);

    }
}