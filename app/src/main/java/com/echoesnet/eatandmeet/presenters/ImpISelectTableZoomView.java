package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ISelectTableZoomView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/13.
 */

public class ImpISelectTableZoomView
{
    private static final String TAG = ImpISelectTableZoomView.class.getSimpleName();
    private Context mContext;
    private ISelectTableZoomView mISelectTableZoomView;

    public ImpISelectTableZoomView(Context mContext, ISelectTableZoomView mISelectTableZoomView)
    {
        this.mContext = mContext;
        this.mISelectTableZoomView = mISelectTableZoomView;
    }

    //获取餐桌状态
    public void getTableStatusFromSever(String resId, String floorNum, String orderTime, final List<HashMap<String, String>> selectedTables)
    {

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.rId, resId);
        reqParamMap.put(ConstCodeTable.layoutId, floorNum);
        reqParamMap.put(ConstCodeTable.orderTime, orderTime);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ArrayMap<String, Object> map = new ArrayMap<>();
                map.put("response", response);
                map.put("selectedTables", selectedTables);
                if (mISelectTableZoomView != null)
                    mISelectTableZoomView.getTableStatusFromSeverCallback(map);
            }
        }, NetInterfaceConstant.RestaurantC_tabInfo, reqParamMap);
    }

}
