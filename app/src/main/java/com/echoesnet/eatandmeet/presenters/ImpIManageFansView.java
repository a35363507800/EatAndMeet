package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.LiveManageFansAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.okhttpCallback.BaseCallback;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ChosenFansBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier zdw
 * @createDate 2017/6/15
 * @description 设置房管请求相关接口
 */

public class ImpIManageFansView extends BasePresenter<LiveManageFansAct>
{
    private final String TAG = ImpIManageFansView.class.getSimpleName();

    /**
     * 粉丝列表
     *
     * @param getItemStartIndex
     * @param getItemNum
     * @param operateType
     */
    public void getAllFansPerson(String getItemStartIndex, String getItemNum, final String operateType)
    {
        final LiveManageFansAct myFansView = getView();
        if (myFansView == null)
            return;
        Context mContext = getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if(isViewAttached())
                    myFansView.requestNetErrorCallback(NetInterfaceConstant.LiveC_myFans_v305,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);

                Observable.create(new ObservableOnSubscribe<ArrayMap<String,Object>>()
                {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayMap<String,Object>> e) throws Exception
                    {
                        ArrayMap<String,Object> map=new ArrayMap<>();
                        JSONObject obj=new JSONObject(response);
                        String num = obj.getString("num");
                        String body = obj.getString("data");
                        ArrayList<ChosenFansBean> orderLst = new Gson().fromJson(body, new TypeToken<List<ChosenFansBean>>()
                        {
                        }.getType());
                        for (int i = orderLst.size() - 1; i >= 0; i--)
                        {
                            if(orderLst.get(i).getIsAdmin().equals("1"))
                            {
                                orderLst.remove(i);
                            }
                        }
                        map.put("num",num);
                        map.put("data",orderLst);
                        map.put("operateType", operateType);
                        e.onNext(map);
                    }
                }).subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread())
                        .compose(getView().<ArrayMap<String,Object>>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(new Consumer<ArrayMap<String,Object>>()
                        {
                            @Override
                            public void accept(ArrayMap<String,Object> s) throws Exception
                            {
                                if(isViewAttached())
                                    myFansView.getAllFansPersonCallBack(s);
                            }
                        });
            }
        },NetInterfaceConstant.LiveC_myFans_v305,reqParamMap);

    }

    /**
     * 把某位粉丝设为房管
     *
     * @param roomId 房间号
     * @param lUId   房管uId
     */
    public void setAdminByServer(String roomId, String lUId)
    {
        final LiveManageFansAct listener = getView();
        if (listener == null)
        {
            return;
        }
        Context mContext = getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        reqParamMap.put(ConstCodeTable.lUId, lUId);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_roomAdmin, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("设为房管参数--> " + paramJson);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (listener != null)
                {
                    listener.setAdminCallback(response);
                }
            }
        }, NetInterfaceConstant.LiveC_roomAdmin, reqParamMap);

    }

}
