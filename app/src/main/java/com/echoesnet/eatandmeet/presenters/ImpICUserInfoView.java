package com.echoesnet.eatandmeet.presenters;

import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.CUserInfoAct;
import com.echoesnet.eatandmeet.controllers.okhttpCallback.BaseCallback;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.CGiftBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.UsersBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICUserInfoPre;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2016/11/21.
 * @description 用户详情界面相关接口请求
 */

public class ImpICUserInfoView extends BasePresenter<CUserInfoAct> implements ICUserInfoPre
{
    private static final String TAG = ImpICUserInfoView.class.getSimpleName();

    /**
     * 修改用户备注
     *
     * @param reMark   备注
     * @param toAddUid 要添加的人uId
     */
    @Override
    public void editReMark(final String reMark, final String toAddUid)
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
                Logger.t(TAG).d("返回结果" + response);
                if (getView() != null)
                    getView().editReMarkCallback(reMark, response);
            }
        }, NetInterfaceConstant.NeighborC_editRemark, reqParamMap);
    }

    /**
     * 获得用户详细信息
     *
     * @param targetUserId 要查询的用户id或者uId，shit！
     */
    @Override
    public void getUserInfoDetail(String targetUserId, String type)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        String interFace = "";
        String andInterFace = "";
        if (type.equals("Id"))
        {
            reqParamMap.put(ConstCodeTable.userId, targetUserId);
            interFace = NetInterfaceConstant.UserC_qOthersInfoById;
            andInterFace = NetInterfaceConstant.UserC_qOthersInfoById;
        } else
        {
            reqParamMap.put(ConstCodeTable.lUId, targetUserId);
            interFace = NetInterfaceConstant.UserC_qOthersInfo;
            andInterFace = NetInterfaceConstant.UserC_qOthersInfo;
        }
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject bodyObj = new JSONObject(response);
                    //用户信息
                    UsersBean userInfo = new Gson().fromJson(bodyObj.getString("userBean"), UsersBean.class);
                    //用户的订单信息
                    ArrayMap<String, String> orderInfoMap = new Gson().fromJson(bodyObj.getString("orderBean"), new TypeToken<ArrayMap<String, String>>()
                    {
                    }.getType());
                    //用户间关系信息
                    CGiftBean giftInfo = new Gson().fromJson(bodyObj.getString("welgiftBean"), CGiftBean.class);
                    Map<String, Object> resultMap = new ArrayMap<>();
                    resultMap.put("userBean", userInfo);
                    resultMap.put("orderBean", orderInfoMap);
                    resultMap.put("stat", bodyObj.getString("stat"));
                    resultMap.put("welgiftBean", giftInfo);
                    if (getView() != null)
                        getView().getUserInfoCallback(resultMap);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, andInterFace, reqParamMap);
    }

    /**
     * 将好友关系保存到后台
     *
     * @param toAddUserUid 要添加的用户uid
     * @param amount       见面礼金额
     * @param streamId     见面礼流水号
     * @param payType      见面礼付款方式
     */
    @Override
    public void saveContactStatusToServer(final String toAddUserUid, String amount, String streamId, final String payType)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, toAddUserUid);
        reqParamMap.put(ConstCodeTable.amount, amount);
        reqParamMap.put(ConstCodeTable.streamId, streamId);
        reqParamMap.put(ConstCodeTable.payType, payType);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.NeighborC_friend, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().saveContactToServerCallback(response);
            }
        }, NetInterfaceConstant.NeighborC_friend, reqParamMap);
    }

    /**
     * 普通申请
     *
     * @param toAddUserUid 要申请人的uId
     */
    @Override
    public void applyFriendByHello(String toAddUserUid)
    {

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, toAddUserUid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().applyFriendByHelloCallback(response);
            }
        }, NetInterfaceConstant.NeighborC_preFriend, reqParamMap);
    }

    /**
     * 查询用户的禁言状态
     *
     * @param avRoomId
     * @param userUid
     */
    @Override
    public void checkUserShutUpState(String avRoomId, String userUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, userUid);
        reqParamMap.put(ConstCodeTable.roomId, avRoomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.LiveC_muteStatus, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获取用户的禁言状态返回值--> " + response.toString());
                if (getView() != null)
                    getView().getUserShutUpStateCallback(response);
            }
        }, NetInterfaceConstant.LiveC_muteStatus, reqParamMap);
    }

    /**
     * 设置用户禁言
     *
     * @param avRoomId
     * @param userUid
     */
    @Override
    public void setUserShutUpYes(String avRoomId, String userUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
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
                Logger.t(TAG).d("设置用户的禁言 返回值--> " + response.toString());
                if (getView() != null)
                    getView().setUserShutUpYesCallback(response);
            }
        }, NetInterfaceConstant.LiveC_addMute, reqParamMap);
    }


    /**
     * 解除用户的禁言
     *
     * @param avRoomId
     * @param userUid
     */
    @Override
    public void setUserShutUpNo(String avRoomId, String userUid)
    {
        final CUserInfoAct mUserInfoAct = getView();
        if (mUserInfoAct == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mUserInfoAct);
        reqParamMap.put(ConstCodeTable.lUId, userUid);
        reqParamMap.put(ConstCodeTable.roomId, avRoomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
/*          注意，这是一个示范：如果你不需要对返回的错误码特殊处理，只需要重写下面的onNext()方法即可，
            如果你需要处理某些特殊的错误码，则也需要按照下面的方法重写onHandledError（）--wb
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (mUserInfoAct != null)
                    mUserInfoAct.callServerErrorCallback(NetInterfaceConstant.And_LiveC_delMute, apiE.getErrorCode(), apiE.getErrBody());
            }*/

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (mUserInfoAct != null)
                    mUserInfoAct.setUserShutUpNoCallback(response);
            }
        }, NetInterfaceConstant.LiveC_delMute, reqParamMap);
    }


    /**
     * 获取某一用户在给定直播间的身份，以及自己的身份
     *
     * @param avRoomId
     * @param myUid    查看用户Uid
     * @param checkUid 被查看用户的Uid
     */
    @Override
    public void checkUserRole(final String avRoomId, final String myUid, final String checkUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, checkUid);
        reqParamMap.put(ConstCodeTable.roomId, avRoomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView() .callServerErrorCallback(NetInterfaceConstant.LiveC_userRole, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response1)
            {
                super.onNext(response1);
                Logger.t(TAG).d("获得的结果：" + response1);
                //查询自己的身份
                Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
                reqParamMap.put(ConstCodeTable.lUId, myUid);
                reqParamMap.put(ConstCodeTable.roomId, avRoomId);
                HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
                {
                    @Override
                    public void onHandledError(ApiException apiE)
                    {
                        super.onHandledError(apiE);
                        if (getView()  != null)
                            getView() .callServerErrorCallback(NetInterfaceConstant.LiveC_userRole, apiE.getErrorCode(), apiE.getErrBody());
                    }

                    @Override
                    public void onNext(String response2)
                    {
                        super.onNext(response2);
                        Logger.t(TAG).d("获得的结果：" + response2);
                        try
                        {
                            String checkedRole = (new JSONObject(response1)).getString("userRole");
                            String myRole = (new JSONObject(response2)).getString("userRole");
                            if (getView()  != null)
                                getView() .checkUserRoleCallback(myUid, myRole, checkUid, checkedRole);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, NetInterfaceConstant.LiveC_userRole, reqParamMap);
            }
        }, NetInterfaceConstant.LiveC_userRole, reqParamMap);
    }
}
