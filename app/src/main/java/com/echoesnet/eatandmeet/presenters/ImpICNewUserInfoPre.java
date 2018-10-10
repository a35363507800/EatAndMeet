package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICNewUserInfoPre;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by lc on 2017/7/14 16.
 */

public class ImpICNewUserInfoPre extends BasePresenter<CNewUserInfoAct> implements ICNewUserInfoPre
{
    private static final String TAG = ImpICNewUserInfoPre.class.getSimpleName();

    @Override
    public void lookUserInfo(final String targetUserId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.lUId, targetUserId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().lookUserInfoCallBack(response, targetUserId);
            }
        }, NetInterfaceConstant.UserC_userInfo, reqParamMap);
    }


    @Override
    public void checkUserInfo(final String targetUserId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.lUId, targetUserId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().checkUserInfoCallBack(response, targetUserId);
            }
        }, NetInterfaceConstant.UserC_userInfo, reqParamMap);
    }


    @Override
    public void editReMark(final String reMark, String toAddUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, toAddUid);
        reqParamMap.put(ConstCodeTable.remark, reMark);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().editReMarkCallback(reMark, response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                getView().callServerErrorCallback(NetInterfaceConstant.FriendC_editRemark,apiE.getErrorCode(),apiE.getErrBody());
            }
        }, NetInterfaceConstant.FriendC_editRemark, reqParamMap);

    }


    @Override
    public void queryUsersRelationShip(String lUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().queryUsersRelationShipCallBack(response);
            }
        }, NetInterfaceConstant.UserC_usersRelationship, reqParamMap);
    }

    @Override
    public void checkUserShutUpState(String avRoomId, String userUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.lUId, userUid);
        reqParamMap.put(ConstCodeTable.roomId, avRoomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getUserShutUpStateCallback(response);
            }
        }, NetInterfaceConstant.LiveC_muteStatus, reqParamMap);
    }

    @Override
    public void setUserShutUpYes(String avRoomId, String userUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.lUId, userUid);
        reqParamMap.put(ConstCodeTable.roomId, avRoomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.LiveC_addMute, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().setUserShutUpYesCallback(response);
            }
        }, NetInterfaceConstant.LiveC_addMute, reqParamMap);
    }

    @Override
    public void setUserShutUpNo(String avRoomId, String userUid)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.lUId, userUid);
        reqParamMap.put(ConstCodeTable.roomId, avRoomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.LiveC_delMute, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().setUserShutUpNoCallback(response);

            }
        }, NetInterfaceConstant.LiveC_delMute, reqParamMap);
    }

    @Override
    public void deFriend(String luId)
    {
        final Map<String, String> requMap = NetHelper.getCommonPartOfParam(getView());
        requMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.FriendC_pullTheBlack, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().deFriendCallBack(response);
            }
        }, NetInterfaceConstant.FriendC_pullTheBlack, requMap);

    }

    @Override
    public void checkRedPacketsStates(List<String> redPacketIds)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.streamId, new Gson().toJson(redPacketIds));
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().checkRedPacketStatsCallback(response);
            }
        }, NetInterfaceConstant.UserC_checkRedList, reqParamMap);
    }

    @Override
    public void focusPerson(String luId, String operFlag)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.operFlag, operFlag);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回参数》》" + response.toString());
                if (getView() != null)
                    getView().focusCallBack(response);
            }
        }, NetInterfaceConstant.LiveC_focus, reqParamMap);
    }

    @Override
    public void addWish(String luId)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回参数》》" + response.toString());
                if (getView() != null)
                    getView().addWishCallBack(response);
            }
        }, NetInterfaceConstant.AppointmentC_addWish, reqParamMap);

    }

    @Override
    public void checkUserRole(final String avRoomId, final String myUid, final String checkUid)
    {
        if (getView() == null)
            return;
        final Map<String, String> reqParamMap1 = NetHelper.getCommonPartOfParam(getView());
        reqParamMap1.put(ConstCodeTable.lUId, checkUid);
        reqParamMap1.put(ConstCodeTable.roomId, avRoomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.LiveC_userRole, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(final String response1)
            {
                super.onNext(response1);
                Logger.t(TAG).d("checkUserRole  response1返回参数》》" + response1.toString());
                if (getView() != null)
                {
                    //查询自己的身份
                    Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
                    reqParamMap.put(ConstCodeTable.lUId, myUid);
                    reqParamMap.put(ConstCodeTable.roomId, avRoomId);
                    HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
                    {
                        @Override
                        public void onHandledError(ApiException apiE)
                        {
                            super.onHandledError(apiE);
                            if (getView() != null)
                                getView().callServerErrorCallback(NetInterfaceConstant.LiveC_userRole, apiE.getErrorCode(), apiE.getErrBody());
                        }

                        @Override
                        public void onNext(String response2)
                        {
                            super.onNext(response2);
                            Logger.t(TAG).d("checkUserRole  response2返回参数》》" + response1.toString());
                            try
                            {
                                String checkedRole = (new JSONObject(response1)).getString("userRole");
                                String myRole = (new JSONObject(response2)).getString("userRole");
                                if (getView() != null)
                                    getView().checkUserRoleCallback(myUid, myRole, checkUid, checkedRole);
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }, NetInterfaceConstant.LiveC_userRole, reqParamMap);
                }
            }
        }, NetInterfaceConstant.LiveC_userRole, reqParamMap1);
    }
}
