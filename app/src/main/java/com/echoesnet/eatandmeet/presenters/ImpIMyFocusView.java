package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.livefragments.LiveFocusFrg;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MyFocusPersonBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyFocusView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIMyFocusView extends BasePresenter<IMyFocusView>
{
    private final String TAG = ImpIMyFocusView.class.getSimpleName();

    public void getAllFollowPerson(String getItemStartIndex, String getItemNum, final String operateType)
    {
        final IMyFocusView focusView = getView();
        if (focusView == null)
            return;
        Context mContext = ((LiveFocusFrg) getView()).getActivity();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if(focusView!=null)
                focusView.requestNetErrorCallback(NetInterfaceConstant.LiveC_myFocus_v305,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                    if(focusView!=null)
                        focusView.getAllFocusPersonCallBack(response,operateType);
            }
        },NetInterfaceConstant.LiveC_myFocus_v305,reqParamMap);

    }

    /**
     * 主播关注
     * 请使用ArrayMap 构建请求参数--wb
     * isFocus 1：关注，0：取消关注
     */
    public void focusAnchor(String hostId, String isFocus)
    {
        final IMyFocusView focusView = getView();
        if (focusView == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, hostId);
        reqParamMap.put(ConstCodeTable.operFlag, isFocus);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (focusView!=null)
                    focusView.changeFollowCallBack();
            }
        },NetInterfaceConstant.LiveC_focus,reqParamMap);
    }
}
