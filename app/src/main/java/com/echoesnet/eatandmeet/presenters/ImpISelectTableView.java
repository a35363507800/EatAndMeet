package com.echoesnet.eatandmeet.presenters;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.SelectTableAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.datamodel.OperateType;
import com.echoesnet.eatandmeet.presenters.viewinterface.ISelectTableView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by ben on 2016/12/19.
 */

public class ImpISelectTableView extends BasePresenter<SelectTableAct>
{
    private static final String TAG = ImpISelectTableView.class.getSimpleName();
    /**
     * 入口
     */
    public void getRestoreDays(String resId)
    {
        final SelectTableAct mISelectTableView = getView();
        if (mISelectTableView == null)
        {
            return;
        }
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mISelectTableView);
        reqParamMap.put(ConstCodeTable.rId, resId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (mISelectTableView != null)
                    mISelectTableView.getRestoreDaysCallback(response);
            }
        },NetInterfaceConstant.RestaurantC_ordDays,reqParamMap);
    }

    /**
     * 约主播吃饭可订餐日期
     */
    public void getRestoreDaysForAppo(String resId)
    {
        final ISelectTableView mISelectTableView = getView();
        if (mISelectTableView == null)
        {
            return;
        }
        Context mContext = (SelectTableAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.rId, resId);
        reqParamMap.put(ConstCodeTable.streamId, EamApplication.getInstance().dateStreamId);
        Logger.t(TAG).d("dateStreamId>>>>>>>" + EamApplication.getInstance().dateStreamId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("约主播吃饭可订餐日期获得的结果：" + response);
                if (mISelectTableView != null)
                    mISelectTableView.getRestoreDaysCallback(response);
            }
        },NetInterfaceConstant.RestaurantC_ordDaysForAppo,reqParamMap);
    }

    /**
     * 获得营业时间，时间间隔
     *
     * @param orderDate 日期
     * @param type      是初始化还是正常获得时间
     */
    public void getOpenTimePeriodData(String orderDate, String resId, final OperateType type)
    {
        final SelectTableAct mISelectTableView = getView();
        if (mISelectTableView == null)
        {
            return;
        }
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mISelectTableView);
        reqParamMap.put(ConstCodeTable.rId, resId);
        reqParamMap.put(ConstCodeTable.date, orderDate);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (mISelectTableView != null)
                    mISelectTableView.getOpenTimePeriodDataCallback(response, type);
            }
        },NetInterfaceConstant.RestaurantC_ordTime,reqParamMap);
    }

    //获取餐桌状态selectedTable 中key表示某楼层的桌子id，value为状态
    public void getTableStatusFromSever(final String resId, final String floorNum, String orderTime)
    {
        final SelectTableAct mISelectTableView = getView();
        if (mISelectTableView == null)
        {
            return;
        }
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mISelectTableView);
        reqParamMap.put(ConstCodeTable.rId, resId);
        reqParamMap.put(ConstCodeTable.layoutId, floorNum);
        reqParamMap.put(ConstCodeTable.orderTime, orderTime);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (mISelectTableView != null)
                    mISelectTableView.getTableStatusFromSeverCallback(response);
            }
        },NetInterfaceConstant.RestaurantC_tabInfo,reqParamMap);
    }

    //验证验证码
    public void validSecurityCode(final String mobile, String code, String type, final Dialog dialog)
    {
        final ISelectTableView mISelectTableView = getView();
        if (mISelectTableView == null)
        {
            return;
        }
        Context mContext = (SelectTableAct) getView();
        ArrayMap<String, String> reqParamMap = new ArrayMap<>();
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        reqParamMap.put(ConstCodeTable.code, code);
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(mContext));
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(mContext));
        reqParamMap.put(ConstCodeTable.type, type);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (mISelectTableView != null)
                    mISelectTableView.validSecurityCodeCallback(response, mobile, dialog);
            }
        },NetInterfaceConstant.UserC_validCodes,reqParamMap);
    }
}
