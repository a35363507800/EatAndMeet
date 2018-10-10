package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.activities.LiveHouseManageAct;
import com.echoesnet.eatandmeet.controllers.okhttpCallback.BaseCallback;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ChosenAdminBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/6/15
 * @description 取消房管接口请求
 */
public class ImpILiveHouseView extends BasePresenter<LiveHouseManageAct>
{
    private static final String TAG = ImpILiveHouseView.class.getSimpleName();

    public void getLieHouseList(String roomId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<ChosenAdminBean> manageList = new Gson().fromJson(response, new TypeToken<List<ChosenAdminBean>>()
                {
                }.getType());
                if (getView() != null)
                    getView().getHouseManageListCallback(manageList);
            }
        }, NetInterfaceConstant.LiveC_roomAdminList, reqParamMap);
    }

    /**
     * 取消房管
     *
     * @param roomId 房间号
     * @param luId   房管uId
     */
    public void unChosenManage(String roomId, final String luId, final int position, final ChosenAdminBean bean)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_cancelRoomAdmin, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("取消房管参数--> " + paramJson);


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                {
                    getView().unAdminCallback(response, position, bean);
                }
            }
        }, NetInterfaceConstant.LiveC_cancelRoomAdmin, reqParamMap);

    }
}
