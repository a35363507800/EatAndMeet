package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.live.LHotAnchorVideoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILHotAnchorVideoActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by an on 2016/12/29.
 */

public class ImpLHotAnchorVideoActView extends BasePresenter<ILHotAnchorVideoActView>
{
    private final String TAG = ImpLHotAnchorVideoActView.class.getSimpleName();

    /**
     * 是否显示排行榜 我的约会
     */
    public void showRankingAndBootyCall()
    {
        final ILHotAnchorVideoActView ilHotAnchorVideoActView = getView();
        if (ilHotAnchorVideoActView == null)
            return;
        final Context mContext = (LHotAnchorVideoAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_swap, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("提交的请求>" + NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_swap, new Gson().toJson(reqParamMap)));


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(ilHotAnchorVideoActView==null)
                    return;

                try
                {
                JSONObject bodyJObj = new JSONObject(response);
                String receive = bodyJObj.getString("receive");
                ilHotAnchorVideoActView.showRankingCallBack("0".equals(bodyJObj.getString("ranking")), "0".equals(bodyJObj.getString("thisTime")), receive);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.showRankingCallBack(false, false, "");
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.showRankingCallBack(false, false, "");
                }
            }
        },NetInterfaceConstant.LiveC_swap,reqParamMap);


    }


    /**
     * 获取房间信息
     */
    public void getRoomInfor(String roomId)
    {
        final ILHotAnchorVideoActView ilHotAnchorVideoActView = getView();
        if (ilHotAnchorVideoActView == null)
            return;
        final Context mContext = (LHotAnchorVideoAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.roomId,roomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(ilHotAnchorVideoActView==null)
                    return;
                ilHotAnchorVideoActView.getRoomInformationCallBack(EamApplication.getInstance().getGsonInstance().fromJson(response,new TypeToken<LiveEnterRoomBean>(){}.getType()));
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.requestNetError(null,apiE,NetInterfaceConstant.LiveC_enterRoom);
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.requestNetError(null,null,NetInterfaceConstant.LiveC_enterRoom);
                }
            }
        },NetInterfaceConstant.LiveC_enterRoom,reqParamMap);
    }

    /**
     * 获取主播信息
     */
    public void getAnchorInformation(String luId)
    {
        final ILHotAnchorVideoActView ilHotAnchorVideoActView = getView();
        if (ilHotAnchorVideoActView == null)
            return;
        final Context mContext = (LHotAnchorVideoAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(ilHotAnchorVideoActView==null)
                    return;
                ilHotAnchorVideoActView.getAnchorInformationCallBack(response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.requestNetError(null,apiE,NetInterfaceConstant.LiveC_anchorBaseInfo);
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.requestNetError(null,null,NetInterfaceConstant.LiveC_anchorBaseInfo);
                }
            }
        },NetInterfaceConstant.LiveC_anchorBaseInfo,reqParamMap);
    }

    /**
     * 关注
     */
    public void focus(String luId)
    {
        final ILHotAnchorVideoActView ilHotAnchorVideoActView = getView();
        if (ilHotAnchorVideoActView == null)
            return;
        final Context mContext = (LHotAnchorVideoAct) getView();
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mContext);
        reqMap.put(ConstCodeTable.lUId, luId);
        reqMap.put(ConstCodeTable.operFlag, "1");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(ilHotAnchorVideoActView==null)
                    return;
                ilHotAnchorVideoActView.focusSuccess();
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.requestNetError(null,apiE,NetInterfaceConstant.LiveC_anchorBaseInfo);
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.requestNetError(null,null,NetInterfaceConstant.LiveC_anchorBaseInfo);
                }
            }
        },NetInterfaceConstant.LiveC_focus,reqMap);
    }

    /**
     * 加入约会
     */
    public void addWish(String luId)
    {
        final ILHotAnchorVideoActView ilHotAnchorVideoActView = getView();
        if (ilHotAnchorVideoActView == null)
            return;
        final Context mContext = (LHotAnchorVideoAct) getView();
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mContext);
        reqMap.put(ConstCodeTable.lUId, luId);
        reqMap.put(ConstCodeTable.operFlag, "1");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(ilHotAnchorVideoActView==null)
                    return;
                ilHotAnchorVideoActView.addWishSuccess();
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.requestNetError(null,apiE,NetInterfaceConstant.AppointmentC_addWish);
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (ilHotAnchorVideoActView != null)
                {
                    ilHotAnchorVideoActView.requestNetError(null,null,NetInterfaceConstant.AppointmentC_addWish);
                }
            }
        },NetInterfaceConstant.AppointmentC_addWish,reqMap);
    }
}
