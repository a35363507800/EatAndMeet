package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IChatActivityView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @Date 2017/7/27
 * @Version 1.0
 */

public class ImpIChatActivityView extends BasePresenter<IChatActivityView>
{
    private Activity mAct;
    private IChatActivityView iChatActivityView;

    public ImpIChatActivityView(Activity mActivity, IChatActivityView iChatActivityView)
    {
        this.mAct = mActivity;
        this.iChatActivityView = iChatActivityView;
    }

    /**
     * 查询与聊天用户关系
     *
     * @param lUid 对方Uid
     */
    public void queryUsersRelationShip(String lUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (iChatActivityView != null)
                    iChatActivityView.queryUsersRelationShipCallBack(response.getBody());
            }
        }, NetInterfaceConstant.UserC_usersRelationship, null, reqParamMap);
    }

    /**
     * 拉黑用户
     *
     * @param lUid 拉黑用户id
     */
    public void pull2Black(String lUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (iChatActivityView != null)
                    iChatActivityView.pull2BlackCallBack(response.getBody());
            }
        }, NetInterfaceConstant.FriendC_pullTheBlack, null, reqParamMap);
    }

    /**
     * 检查红包领取状态
     *
     * @param redPacketIds 多个红包id
     */
    public void checkRedPacketsStates(List<String> redPacketIds,boolean isPull2Black)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.streamId, new Gson().toJson(redPacketIds));
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (iChatActivityView != null)
                    iChatActivityView.checkRedPacketStatsCallback(response.getBody(),isPull2Black);
            }
        }, NetInterfaceConstant.UserC_checkRedList, null, reqParamMap);
    }
}
