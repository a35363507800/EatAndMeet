package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.livefragments.LiveFansFrg;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MyFocusPersonBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyFansView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIMyFansView extends BasePresenter<IMyFansView>
{
    private final String TAG = ImpIMyFansView.class.getSimpleName();

    public void getAllFansPerson(String getItemStartIndex, String getItemNum, final String operateType)
    {
        final IMyFansView myFansView = getView();
        if (myFansView == null)
            return;
        Context mContext = ((LiveFansFrg) getView()).getActivity();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if(myFansView!=null)
                myFansView.requestNetErrorCallback(NetInterfaceConstant.LiveC_myFans_v305,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ArrayMap<String,Object> map=new ArrayMap<>();
                try
                {
                    JSONObject obj = new JSONObject(response);
                    ArrayList<MyFocusPersonBean> orderLst = new Gson().fromJson(obj.getString("data"), new TypeToken<List<MyFocusPersonBean>>()
                    {
                    }.getType());
                    map.put("num",obj.getString("num"));
                    map.put("data",orderLst);
                    map.put("operateType", operateType);

                    if(myFansView!=null)
                    myFansView.getAllFansPersonCallBack(map);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        },NetInterfaceConstant.LiveC_myFans_v305,reqParamMap);
    }
}
