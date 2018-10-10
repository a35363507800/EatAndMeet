package com.echoesnet.eatandmeet.activities.liveplay.Presenter;


import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.Interfaces.ILiveFlowPre;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.Interfaces.ILiveRoomInteractPre;
import com.echoesnet.eatandmeet.activities.liveplay.View.LiveBaseAct;
import com.echoesnet.eatandmeet.activities.liveplay.managers.ViewShareHelper;
import com.echoesnet.eatandmeet.models.bean.AudienceBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.GiftBean;
import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;
import com.echoesnet.eatandmeet.models.datamodel.ExitRoomType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/4/19
 * @description 房间交互接口的骨架实现类, 实现所有公共的部分
 */
public abstract class LivePresenter<A extends LiveBaseAct, R extends LiveRecord> extends LiveBasePresenter<A, R>
        implements ILiveFlowPre, ILiveRoomInteractPre
{
    private final static String TAG = LivePresenter.class.getSimpleName();
    public final static int AUDIENCE_MAX_COUNT = 200;//观众列表最大人数
    public boolean isFirstSendHeart = true;//是否第一次发送心跳
    public List<AudienceBean> arrAudiencesObj = Collections.synchronizedList(new ArrayList<AudienceBean>());//观众列表,应该放在record里面，以后更改

    @Override
    public void onCreate()
    {
        super.onCreate();
        //基类实现后，初始化 LiveRecord; 直播相关数据都在这里；
        LiveEnterRoomBean eh = (LiveEnterRoomBean) mActivity.getIntent().getSerializableExtra(LiveRecord.KEY_ENTERROOM_EH);
        if (null == eh)
        {
            mActivity.finish(); // 基本数据拿不到，关闭直播间
        }
        else
        {
            mRecord.setEnterRoom4EH(eh);
            mRecord.setRoomId(mActivity.getIntent().getStringExtra("roomid"));
            mRecord.setHxChatRoomId(eh.getHxRoomId());
            mRecord.setVedioName(mActivity.getIntent().getStringExtra("vedioName"));
            mRecord.setModeOfRoom(mActivity.getIntent().getIntExtra("roomMode", LiveRecord.ROOM_MODE_MEMBER));
            if (TextUtils.isEmpty(mRecord.getRoomId()))
            {
                ToastUtils.showShort("缺少房间号");
                mActivity.finish(); // 基本数据拿不到，关闭直播间
            }
            mRecord.initRoomLayerData();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void sendGift(String chosenGiftId, String chosenGiftNum, GiftBean giftBean)
    {
        if (TextUtils.isEmpty(chosenGiftId))
        {
            ToastUtils.showShort("赠送失败，请重试");
            return;
        }
        Map<String, Object> transElement = new HashMap<>();
        transElement.put("chosenGiftBean", giftBean);
        transElement.put(ConstCodeTable.roomId, mRecord.getRoomId());
        transElement.put(ConstCodeTable.gNum, chosenGiftNum);
      //  int countTotal = ViewShareHelper.getInstance(mActivity, this).getGiftCount(SharePreUtils.getId(mActivity) + chosenGiftId);
        int countTotal = 0;
        transElement.put("giftTotal", countTotal);

        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.gId, chosenGiftId);
        reqMap.put(ConstCodeTable.roomId, mRecord.getRoomId());
        reqMap.put(ConstCodeTable.gNum, chosenGiftNum);
        callServerSilence(NetInterfaceConstant.LiveC_sendGift, transElement, null, reqMap);

    }

    @Override
    public void lookHostInfo(String luId)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.lUId, luId);
        callServerSilence(NetInterfaceConstant.LiveC_anchorBaseInfo, null, null, reqMap);
    }

    @Override
    public void addWish(String luId)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.lUId, luId);


        callServerSilence(NetInterfaceConstant.AppointmentC_addWish, null, null, reqMap);
    }

    @Override
    public void pullAudiences(int index, Map<String, Object> tranParam)
    {
        Logger.t(TAG).d("");
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(mActivity);
        reqParam.put(ConstCodeTable.roomId, mRecord.getRoomId());
        reqParam.put(ConstCodeTable.startIdx, index + "");
        reqParam.put(ConstCodeTable.num, "20");
        Logger.t(TAG).d(NetInterfaceConstant.LiveC_roomMember_v307 + CommonUtils.SEPARATOR + new Gson().toJson(reqParam));

        //防止roomid为null无效请求
        if (TextUtils.isEmpty(reqParam.get(ConstCodeTable.roomId)))
            return;

        callServerSilence(NetInterfaceConstant.LiveC_roomMember_v307, tranParam, "1", reqParam);
    }

    @Override
    public synchronized boolean audienceIntoRoom(String uid, @NonNull String id, String nickName, String headImg, boolean isFakeMember, String level, String imId,String isVuser)
    {
        // 这个id有时会带u "u102533" ，没有追到产生位置
        Logger.t(TAG).d("观众列表1》 传入id》" + id + " 主播id》 " + mRecord.getEnterRoom4EH().getAnchorId() + " 观众列表》" + arrAudiencesObj.toString());

        id = id.replace("u", "");
        boolean isAddAudience = true;
        if (TextUtils.isEmpty(id) || id.equals(mRecord.getEnterRoom4EH().getAnchorId()))//主播开直播，观众列表不显示主播头像
            return false;
        AudienceBean userBean = new AudienceBean();
        userBean.setIdentifier(id);
        userBean.setFaceUrl(headImg);
        userBean.setNicName(nickName);
        userBean.setLevel(level);
        userBean.setImId(imId);
        userBean.setUid(uid);
        userBean.setIsVuser(isVuser);

        if (isFakeMember)
            userBean.setIsGhost("1");
        else
            userBean.setIsGhost("0");

        int index = -1;
        for (int i = 0; i < arrAudiencesObj.size(); i++)
        {
            AudienceBean bean = arrAudiencesObj.get(i);
            if (bean.getIdentifier().equals(id))
            {
                index = i;
                break;
            }
        }
        //存在重复数据,则只是替换它
        if (index != -1)
        {
            arrAudiencesObj.set(index, userBean);
            isAddAudience = false;
        }
        else
        {
            int insertIndex = Collections.binarySearch(arrAudiencesObj, userBean);
            if (insertIndex >= 0)
            {
                arrAudiencesObj.add(insertIndex, userBean);
            }
            else
            {
                arrAudiencesObj.add(-insertIndex - 1, userBean);
            }
            trimAudienceList();
        }
        Logger.t(TAG).d("是否增加结果》 "+isAddAudience);
        return isAddAudience;
    }

    @Override
    public void switchBarrage(boolean isOpen)
    {
        String type = isOpen ? "1" : "0";
        Map<String, Object> transElement = new HashMap<>();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.roomId, getmRecord().getRoomId());
        reqParamMap.put(ConstCodeTable.type, type);
        callServerSilence(NetInterfaceConstant.LiveC_barrage, transElement, "1", reqParamMap);
    }

    @Override
    public void sendBarrage(String msg)
    {
        Map<String, Object> transElement = new HashMap<>();
        transElement.put("msg", msg);
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.roomId, getmRecord().getRoomId());
        reqParamMap.put(ConstCodeTable.context, msg);
        callServerSilence(NetInterfaceConstant.LiveC_sendBarrage, transElement, "1", reqParamMap);
    }

    @Override
    public void followHost()
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.lUId, mRecord.getEnterRoom4EH().getuId());
        reqMap.put(ConstCodeTable.operFlag, "1");
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_focus, new Gson().toJson(reqMap));
        Logger.t(TAG).d("请求关注参数--> " + paramJson);
        callServerSilence(NetInterfaceConstant.LiveC_focus, new HashMap<String, Object>(), "1", reqMap);
    }

    @Override
    public void getGroundRed(String streamId, String roomId, String Id)
    {
        Map<String, Object> transElement = new HashMap<>();
        transElement.put("Id", Id);
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(mActivity);
        reqParam.put(ConstCodeTable.streamId, streamId);
        reqParam.put(ConstCodeTable.roomId, roomId);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_getGroupRed, new Gson().toJson(reqParam));
        Logger.t(TAG).d("收群红包参数--> " + paramJson);
        callServerSilence(NetInterfaceConstant.LiveC_getGroupRed, transElement, "1", reqParam);
    }

    @Override
    public void enteringRoomSetAdminDialog(String adminUid, String roomId)
    {
        Map<String, Object> transElement = new HashMap<>();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        reqParamMap.put(ConstCodeTable.lUId, adminUid);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_roomAdmin, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("NetInterfaceConstant.LiveC_roomAdmin 参数--> " + paramJson);
        callServerSilence(NetInterfaceConstant.LiveC_roomAdmin, transElement, "1", reqParamMap);
    }

    @Override
    public void enteringRoomCancelAdminDialog(String adminUid, String roomId)
    {
        Map<String, Object> transElement = new HashMap<>();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        reqParamMap.put(ConstCodeTable.lUId, adminUid);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_cancelRoomAdmin, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("NetInterfaceConstant.LiveC_cancelRoomAdmin 参数--> " + paramJson);
        callServerSilence(NetInterfaceConstant.LiveC_cancelRoomAdmin, transElement, "1", reqParamMap);
    }

    @Override
    public void notifyServerCloseRoom(String videoName)
    {
        Logger.t(TAG).d("通知后台关闭房间");
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.roomId, mRecord.getRoomId());
        reqMap.put(ConstCodeTable.vedio, videoName);
        reqMap.put(ConstCodeTable.max, mRecord.getWatchHighCount() + "");
        reqMap.put(ConstCodeTable.sec, "0");
        callServerSilence(NetInterfaceConstant.LiveC_closeRoom, null, "1", reqMap);
    }

    @Override
    public void enterRoomResetAdmin(String roomId, String imId)
    {
        Map<String, Object> transElement = new HashMap<>();
        transElement.put("imId", imId);
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        callServerSilence(NetInterfaceConstant.LiveC_roomAdminList, transElement, "1", reqParamMap);
    }

    @Override
    public void sendHeartBeat()
    {
        Logger.t(TAG).i("心跳》 ❤❤❤❤❤❤❤❤❤❤");
        Map<String, String> reqParam = new HashMap<>();
        reqParam.put(ConstCodeTable.uId, SharePreUtils.getUId(mActivity));
        reqParam.put(ConstCodeTable.num, isFirstSendHeart ? "0" : "");
        callServerSilence4Server(NetHelper.SERVER_SITE_HEART,NetInterfaceConstant.HeartC_beat,null,reqParam);
    }


    //region 私有函数
    private void trimAudienceList()
    {
        //确保观众列表的头像不超过AUDIENCE_COUNT个
        if (arrAudiencesObj.size() > AUDIENCE_MAX_COUNT)
        {
            arrAudiencesObj.remove(AUDIENCE_MAX_COUNT);
        }
    }

    @Override
    public void exitRoomToCallServer(String roomId, ExitRoomType type)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.roomId, roomId);
        Logger.t(TAG).d("退出直播间--> " + new Gson().toJson(reqMap));
        Map<String, Object> transElement = new ArrayMap<>();
        transElement.put("type", type);
        callServerSilence(NetInterfaceConstant.LiveC_exitRoom, transElement, "1", reqMap);
    }

    @Override
    public void getCanInviteList(String gameId, String roomId, String start, String num,String type)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.roomId, roomId);
        reqMap.put(ConstCodeTable.gameId, gameId);
        reqMap.put(ConstCodeTable.startIdx, start);
        reqMap.put(ConstCodeTable.num, num);
        Map<String,Object> transElement = new ArrayMap<>();
        transElement.put("type",type);
        callServerSilence(NetInterfaceConstant.SunMoonStarC_canInviteList, transElement, "1", reqMap);
    }

    @Override
    public void getGameInviters(String gameId, String roomId, String start, String num,String type)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.roomId, roomId);
        reqMap.put(ConstCodeTable.gameId, gameId);
        reqMap.put(ConstCodeTable.startIdx, start);
        reqMap.put(ConstCodeTable.num, num);
        Map<String,Object> transElement = new ArrayMap<>();
        transElement.put("type",type);
        callServerSilence(NetInterfaceConstant.SunMoonStarC_inviters, transElement, "1", reqMap);
    }

    @Override
    public void sendInvitation(String gameId, String roomId, String inviteArray,List<String> selectTxIdList,List<Integer> selectPosition)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.roomId, roomId);
        reqMap.put(ConstCodeTable.gameId, gameId);
        reqMap.put(ConstCodeTable.inviteArray, inviteArray);
        Map<String, Object> transElement = new ArrayMap<>();
        transElement.put("txId", selectTxIdList);
        transElement.put("selectPosition", selectPosition);
        callServerSilence(NetInterfaceConstant.SunMoonStarC_sendInvitation, transElement, "1", reqMap);
    }

    @Override
    public void answerInvitation(String gameId, String roomId, String lUId,String id, String flg,int position)
    {
        Logger.t(TAG).d("回应邀请"  + lUId);
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.roomId, roomId);
        reqMap.put(ConstCodeTable.gameId, gameId);
        reqMap.put(ConstCodeTable.lUId, lUId);
        reqMap.put(ConstCodeTable.flg, flg);
        Map<String, Object> transElement = new ArrayMap<>();
        transElement.put("flg", flg);
        transElement.put("position", position);
        transElement.put("txId", "u"+id);
        callServerSilence(NetInterfaceConstant.SunMoonStarC_answerInvitation, transElement, "1", reqMap);
    }

    @Override
    public void getMatchResult(String gameId)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.gameId, gameId);
        callServerSilence(NetInterfaceConstant.SunMoonStarC_matchResult, null, "1", reqMap);
    }

    @Override
    public void joinGame(String gameId)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.gameId, gameId);
        callServerSilence(NetInterfaceConstant.SunMoonStarC_joinGame, null, "1", reqMap);
    }

    @Override
    public void shareGame(String gameId, String matchingId, final String type, String score)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.gameId,gameId);
        reqMap.put(ConstCodeTable.matchingId,matchingId);
        reqMap.put(ConstCodeTable.share,type);
        reqMap.put(ConstCodeTable.score,score);
        callServerSilence(NetInterfaceConstant.SunMoonStarC_share, null, "1", reqMap);
    }

    @Override
    public void shareH5(String gameId,String matchingId, String score, String isMyDynamics)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.gameId, gameId);
        Map<String ,Object> transElement = new HashMap<>();
        transElement.put("matchingId",matchingId);
        transElement.put("score",score);
        transElement.put("gameId",gameId);
        transElement.put("isMyDynamics",isMyDynamics);
        callServerSilence(NetInterfaceConstant.GameC_shareH5, transElement, "1", reqMap);
    }

    @Override
    public void exitGame(String gameId)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.gameId, gameId);
        Map<String ,Object> transElement = new HashMap<>();
        transElement.put("gameId",gameId);
        callServerSilence(NetInterfaceConstant.SunMoonStarC_exitGame, transElement, "1", reqMap);
    }

    @Override
    public void checkPopups(String gameId)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.gameId, gameId);
        Map<String ,Object> transElement = new HashMap<>();
        transElement.put("gameId",gameId);
        callServerSilence(NetInterfaceConstant.SunMoonStarC_checkPopups, transElement, "1", reqMap);
    }
}
