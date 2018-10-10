package com.echoesnet.eatandmeet.activities.liveplay.Presenter;

import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.View.LiveRoomAct1;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.AudienceBean;
import com.echoesnet.eatandmeet.models.bean.ChosenAdminBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.GiftBean;
import com.echoesnet.eatandmeet.models.bean.RefreshLiveMsgBean;
import com.echoesnet.eatandmeet.models.datamodel.ExitRoomType;
import com.echoesnet.eatandmeet.models.datamodel.LiveMsgType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.TXConstants;
import com.echoesnet.eatandmeet.utils.IMUtils.TXMessageEvent;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.tencent.TIMCallBack;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivefilter.TILFilter;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.livesdk.ILVChangeRoleRes;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVText;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/5/10
 * @description 完成房间交互逻辑。
 */
public abstract class LiveRoomPre1<A extends LiveRoomAct1, R extends LiveRecord> extends
        LivePresenter<A, R>
        implements java.util.Observer
{
    private static final String TAG = LiveRoomPre1.class.getSimpleName();

    private boolean bCameraOn = false;//旋转摄像头
    private boolean bMicOn = false;   //enableMic(bMicOn); it use like this
    //切换群简介
    private int changeHostStatusCount;
    private String tempStatus;
    private String tempRoomId;
    private TILFilter mUDFilter = null;
    //现在要求真用户在前，假用户在后，而且还按等级排序，简直就是sb要求，那如果其中一个假用户的等级比真用户高，
    //但是他还是排在等级低的后边，与排序规则冲突
    public List<AudienceBean> arrAudiencesObj = Collections.synchronizedList(new
            ArrayList<AudienceBean>());//观众列表

    // FIXME: 2017/3/24 need optimization
    public void whenSwitchRoomToUpdate()
    {
        if (mActivity == null)
            return;
        mRecord.setModeOfRoom(mActivity.getIntent().getIntExtra("roomMode", LiveRecord
                .ROOM_MODE_MEMBER));
        mRecord.setRoomId(mActivity.getIntent().getStringExtra("roomid"));

    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        //在最基类的 act 的 oncreate 中执行；
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        callServerSilence(NetInterfaceConstant.LiveC_swap, null, "1", reqMap);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        TXMessageEvent.getInstance().addObserver(this);//可以重复加
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        TXMessageEvent.getInstance().deleteObserver(this);
    }

    //------------------------底部功能监听回调部分---------------------------------------------

    /***--------聊天室 begin-------***/
    //TXMessageEvent
    @Override
    public void update(java.util.Observable observable, final Object msg)
    {
        List<TIMMessage> list = (List<TIMMessage>) msg;
        parseIMMessage(list);
    }

    /**
     * 解析消息回调
     *
     * @param tlist 消息列表
     */
    private void parseIMMessage(List<TIMMessage> tlist)
    {
        for (int i = tlist.size() - 1; i >= 0; i--)
        {
            TIMMessage currMsg = tlist.get(i);
            for (int j = 0; j < currMsg.getElementCount(); j++)
            {
                if (currMsg.getElement(j) == null)
                    continue;
                TIMElem elem = currMsg.getElement(j);
                TIMElemType type = elem.getType();
                String sendId = currMsg.getSender();

                Logger.t(TAG).d(" Peer:" + currMsg.getConversation().getPeer() + ", Identifer:" +
                        currMsg.getConversation().getIdentifer() + ", 房间号:" + mRecord.getRoomId()
                        + ", 消息发送者:" + currMsg.getSender()
                        + ", 消息类型:" + type);
                //Peer>AdminOnline   Identifer>u100015   房间号》100056  消息发送者》AdminOnline  消息类型》Custom
                // 其他群消息过滤
                if (currMsg.getConversation() != null && currMsg.getConversation().getPeer() !=
                        null && type != TIMElemType.GroupSystem)
                    if (!mRecord.getRoomId().equals(currMsg.getConversation().getPeer()))
                    {
                        if (!currMsg.getConversation().getPeer().contains("u"))
                            continue;
                        else if (!("u" + mRecord.getRoomId()).equals(currMsg.getConversation()
                                .getPeer())
                                && mRecord.getModeOfRoom() == LiveRecord.ROOM_MODE_MEMBER)
                            continue;
                    }

                //region --系统消息-----------
                if (type == TIMElemType.GroupSystem)
                {
                    TIMGroupSystemElem sysElem = (TIMGroupSystemElem) elem;
                    EamLogger.t("TXIM").writeToDefaultFile("消息类型》" + sysElem.getSubtype() + " " +
                            "系统群Id> " + sysElem.getGroupId() +
                            "操作原因》" + sysElem.getOpReason() + " senderId>" + sendId);

                    if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == sysElem
                            .getSubtype()
                            && sysElem.getGroupId().contains(getmRecord().getRoomId()))
                    {
                        EamLogger.t("TXIM").writeToDefaultFile("消息类型》" + sysElem.getSubtype() + "" +
                                " 群Id> " + sysElem.getGroupId() +
                                "操作原因》" + sysElem.getOpReason() + " senderId>" + sendId);
                        if (mActivity != null)
                        {
                            mActivity.roomEventsHostLeave("passive1", sysElem.getOpReason());
                        }
                        else
                        {
                            EamLogger.t("TXIM").writeToDefaultFile
                                    ("消息类型》处理腾讯删除群的消息时直播Activity为null了" + " senderId>" + sendId);
                        }
                    }//群解散，带解散原因
                    else if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_CUSTOM_INFO == sysElem
                            .getSubtype()
                            && ((TIMGroupSystemElem) elem).getGroupId().contains(getmRecord()
                            .getRoomId()))
                    {
                        String data = new String(sysElem.getUserData());
                        EamLogger.t("TXIM").writeToDefaultFile("消息类型》" + sysElem.getSubtype() + "" +
                                " 群Id》 " + sysElem.getGroupId() + " senderId》"
                                + sendId + "后台发送原因》" + data);
                        String[] dataStr = data.split(CommonUtils.SEPARATOR);
                        if (data.contains("close"))
                        {
                            if (mActivity != null)
                                mActivity.roomEventsHostLeave("passive2", dataStr[1]);
                            else
                            {
                                EamLogger.t("TXIM").writeToDefaultFile
                                        ("消息类型》处理（看脸）删除群的消息时直播Activity为null了" + " senderId>" +
                                                sendId);
                            }
                        }
                    }
                    else if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_GRANT_ADMIN_TYPE == sysElem
                            .getSubtype())
                    {
                        //设置管理员，（被设置者接受） 在log上看到的 ，看看有用没，没用的话 可以删除-----------yqh
                    }
                    continue;
                }
                //endregion----------

                //region --定制消息----------
                if (type == TIMElemType.Custom)
                {
                    String identifier, nickname, faceUrl;
                    TIMUserProfile tProfile = currMsg.getSenderProfile();
                    if (tProfile != null)
                    {
                        Logger.t(TAG).d(tProfile.toString());
                        identifier = tProfile.getIdentifier();
                        nickname = tProfile.getNickName();
                        faceUrl = tProfile.getFaceUrl();
                    }
                    else
                    {
                        identifier = sendId;
                        nickname = "隐身人士";
                        faceUrl = sendId;
                    }
                    Logger.t(TAG).d("消息信息：identifier:" + identifier + ",nickname:" + nickname +
                            ",faceUrl：" + faceUrl);
                    handleCustomMsg(currMsg, (TIMCustomElem) elem, identifier, nickname, faceUrl);
                    continue;
                }
                //endregion

                //region-----------------最后处理文本消息------------------------------
                if (type == TIMElemType.Text)
                {
                    TIMTextElem textElem = (TIMTextElem) elem;
                    //请勿删除此代码  -------yqh
                    Map<String, byte[]> customInfoMap = currMsg.getSenderProfile().getCustomInfo();
                    Logger.t(TAG).d("设置customInfoMap:" + customInfoMap.toString());
                    String level = null;
                    try
                    {
                        String customInfoJson = new String(customInfoMap.get(TXConstants
                                .TX_CUSTOM_INFO_1), Charset.forName("utf-8"));
                        //  ToastUtils.showShort(mActivity, "json:" + customInfoJson);
                        Map<String, String> map = EamApplication.getInstance().getGsonInstance()
                                .fromJson(customInfoJson, new TypeToken<HashMap<String, String>>()
                                {
                                }.getType());
                        level = map.get("level");
                        Logger.t(TAG).d("设置获取level：" + level);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d("设置level err>>" + e.getMessage());
                    }
                    if (currMsg.isSelf())
                    {
                        String htmlStr = String.format("<font color=%s>%s</font>", TXConstants
                                .ENTER_ROOM_SEND_MSG_COLOR, textElem.getText());
                        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
                        refreshLiveMsgBean.setText(textElem.getText());
                        refreshLiveMsgBean.setId(SharePreUtils.getId(mActivity));
                        refreshLiveMsgBean.setName(SharePreUtils.getNicName(mActivity));
                        refreshLiveMsgBean.setType(LiveMsgType.NormalText);
                        refreshLiveMsgBean.setLevel(level);
                        refreshLiveMsgBean.setLiveLevelState("1");
                        mActivity.refreshText(refreshLiveMsgBean);
                    }
                    else
                    {
                        Logger.t(TAG).d("isSelf：", "false");
                        String nickname;
                        if (currMsg.getSenderProfile() != null && (!currMsg.getSenderProfile()
                                .getNickName().equals("")))
                        {
                            nickname = currMsg.getSenderProfile().getNickName();
                        }
                        else
                        {
                            nickname = sendId;
                        }
                        String htmlStr = String.format("<font color=%s>%s</font>", TXConstants
                                .ENTER_ROOM_SEND_MSG_COLOR, textElem.getText());
                        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
                        refreshLiveMsgBean.setText(textElem.getText());
                        refreshLiveMsgBean.setId(sendId);
                        refreshLiveMsgBean.setName(nickname);
                        refreshLiveMsgBean.setType(LiveMsgType.NormalText);
                        refreshLiveMsgBean.setLevel(level);
                        refreshLiveMsgBean.setLiveLevelState("1");
                        mActivity.refreshText(refreshLiveMsgBean);
                    }
                }
                //endregion
            }
        }
    }


    /**
     * 处理自定义消息
     *
     * @param currMsg
     * @param elem
     * @param identifier
     * @param nickname
     * @param faceUrl
     */
    private void handleCustomMsg(TIMMessage currMsg, TIMCustomElem elem, final String identifier,
                                 final String nickname, final String faceUrl)
    {
        try
        {
            String customText = new String(elem.getData(), "UTF-8");
            Map<String, byte[]> customInfoMap = currMsg.getSenderProfile().getCustomInfo();
            byte[] data = customInfoMap.get(TXConstants.TX_CUSTOM_INFO_1);
            String tempLevel = "0";
            String isSign = "0";
            String isVuser = "0";
            if (data != null)//防止没有设置自定义字段崩溃的问题
            {
                String customInfoJson = new String(data, Charset.forName("utf-8"));
                Map<String, String> map = EamApplication.getInstance().getGsonInstance()
                        .fromJson(customInfoJson, new TypeToken<HashMap<String, String>>()
                        {
                        }.getType());
                tempLevel = map.get(TXConstants.TX_CUSTOM_INFO_1_KEY_LEVEL);
                isSign = map.get(TXConstants.TX_CUSTOM_INFO_1_KEY_SIGN);
                isVuser = map.get(TXConstants.TX_CUSTOM_INFO_1_KEY_VUSER);
            }
            final String userLevel = tempLevel;
            final String userSign = isSign;
            final String vUser = isVuser;
            Logger.t(TAG).d("获取等级为--> " + userLevel);
            JSONTokener jsonParser = new JSONTokener(customText);
            final JSONObject json = (JSONObject) jsonParser.nextValue();

            //此处customText格式为：{"userAction":1,"actionParam":""}，以后当强更的时候将礼物的模式改掉--wb
            Logger.t(TAG).d("自定义消息>>>>>>" + customText);


            int action = -2;
            String param = "";


            //判断这种情况为IOS礼物格式
            if (customText.contains("gift") && !customText.contains(TXConstants.CMD_KEY))
            {
                param = json.toString();
                action = TXConstants.AVIMCMD_SEND_GIFT;
            }
            else
            {
                //按照常规模式走
                param = json.getString(TXConstants.CMD_PARAM);
                action = json.getInt(TXConstants.CMD_KEY);
            }

            Logger.t("==============").d("接到的内容为:" + action + ":" + param);
            switch (action)
            {
                case TXConstants.AVIMCMD_SEND_GIFT:
                    //    mActivity.refreshGift(json.getString(TXConstants.CMD_PARAM), nickname,
                    // identifier, faceUrl, userLevel,isVuser);
                    mActivity.refreshGift(param, nickname, identifier, faceUrl, userLevel, isVuser);
                    break;
                case TXConstants.AVIMCMD_MUlTI_HOST_INVITE:
                    // 多人主播发送邀请消息, C2C消息
                    mActivity.showInviteDialog();
                    break;
                case TXConstants.AVIMCMD_MUlTI_HOST_CLOSE_INVITE:
                    // 主播发送取消邀请消息, C2C消息
                    mActivity.hideInviteDialog(false);
                    break;
                case TXConstants.AVIMCMD_MUlTI_HOST_CLOSE_INVITE_TIMEOUT:
                    // 主播超时发送关闭邀请消息, C2C消息
                    mActivity.hideInviteDialog(true);
                    break;
                case TXConstants.AVIMCMD_MUlTI_JOIN:// 主播收到连麦观众同意上麦的回应（C2C消息）
                    if (LiveRecord.ROOM_MODE_HOST == getmRecord().getModeOfRoom())
                    {
                        if (mActivity.mWillUserList.contains(identifier))
                            mActivity.handleInviteRequest(identifier, nickname, "accept");
                    }
                    break;
                case TXConstants.AVIMCMD_MUlTI_REFUSE:// 主播收到连麦观众拒绝上麦的回应（C2C消息）
                    if (LiveRecord.ROOM_MODE_HOST == getmRecord().getModeOfRoom())
                    {
                        if (mActivity.mWillUserList.contains(identifier))
                            mActivity.handleInviteRequest(identifier, nickname, "refuse");
                    }
                    break;
                case TXConstants.AVIMCMD_MUlTI_MEMBER_UPROLE_FAIL_NOTIFY:// 观众上麦失败，通知主播 （C2C消息 ）
                    if (LiveRecord.ROOM_MODE_HOST == getmRecord().getModeOfRoom())
                    {
                        if (mActivity.mWillUserList.contains(identifier))
                            mActivity.handleInviteRequest(identifier, nickname, "acceptErr");
                    }
                    break;
                case TXConstants.AVIMCMD_Focus:
                    Logger.t(TAG).d("关注 identifier--> " + identifier + " , nickname-->" +
                            nickname + " , userLevel--> " + userLevel);
                    mActivity.focusHost(identifier, nickname, userLevel);
                    break;
                case TXConstants.AVIMCMD_EnterLive:
                    Observable.create(new ObservableOnSubscribe<Map<String, String>>()
                    {
                        @Override
                        public void subscribe(ObservableEmitter<Map<String, String>> e) throws
                                Exception
                        {
                            String id = identifier.replace("u", "");
                            String imId = "u" + id;
                            boolean isNeedAdd = audienceIntoRoom(null, id, nickname, faceUrl,
                                    false, userLevel, imId, vUser);
                            if (isNeedAdd)
                            {
                                // 调用房管列表接口 遍历其是否包含进入直播间的id
                                if (getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                                {
                                    enterRoomResetAdmin(getmRecord().getRoomId(), imId);
                                }
                                Map<String, String> map = new ArrayMap<>();
                                map.put("identifier", id);
                                map.put("nicName", nickname);
                                map.put("faceUrl", faceUrl);
                                map.put("level", userLevel);
                                map.put("isSign", userSign);
                                e.onNext(map);
                            }
                            else
                                e.onError(new Throwable("重复数据：id> " + id));
                        }
                    }).subscribeOn(Schedulers.computation())
                            .unsubscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Map<String, String>>()
                            {
                                @Override
                                public void accept(Map<String, String> stringMap) throws Exception
                                {
                                    mActivity.memberJoin(stringMap.get("identifier"), stringMap
                                                    .get("nicName"),
                                            stringMap.get("faceUrl"), stringMap.get("level"),
                                            stringMap.get("isSign"));
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(Throwable e) throws Exception
                                {
                                    EamLogger.t(TAG).writeToDefaultFile("真用户加入错误》" + e.getMessage
                                            ());
                                    Logger.t(TAG).d("真用户加入错误》" + e.getMessage());
                                }
                            });
                    break;
                case TXConstants.AVIMCMD_FakeMember_Enter://机器人进入
                    Observable.create(new ObservableOnSubscribe<Map<String, String>>()
                    {
                        @Override
                        public void subscribe(ObservableEmitter<Map<String, String>> e) throws
                                Exception
                        {
                            final Map<String, String> userInfoMap = EamApplication.getInstance()
                                    .getGsonInstance()
                                    .fromJson(json.getString(TXConstants.CMD_PARAM), new
                                            TypeToken<HashMap<String, String>>()
                                            {
                                            }.getType());
                            final String fUserId = userInfoMap.get("id");
                            final String uId = userInfoMap.get("uId");
                            final String fNickName = userInfoMap.get("nickName");
                            final String fHeadImg = userInfoMap.get("headImg");
                            final String level = userInfoMap.get("level");
                            boolean isNeedAdd = audienceIntoRoom(uId, fUserId, fNickName,
                                    fHeadImg, true, level, "u" + fUserId, vUser);
                            if (isNeedAdd)
                            {
                                e.onNext(userInfoMap);
                            }
                            else
                                e.onError(new Throwable("重复数据：id> " + identifier));
                        }
                    }).subscribeOn(Schedulers.computation())
                            .unsubscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(mActivity.<Map<String, String>>bindUntilEvent(ActivityEvent.DESTROY))
                            .subscribe(new Consumer<Map<String, String>>()
                            {
                                @Override
                                public void accept(Map<String, String> stringMap) throws Exception
                                {
                                    mActivity.fakeMemberJoin(stringMap.get("id"), stringMap.get
                                            ("nickName"), stringMap.get("headImg"), stringMap.get
                                            ("level"));
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(Throwable e) throws Exception
                                {
                                    EamLogger.t(TAG).writeToDefaultFile("假用户消息解析失败》" + e
                                            .getMessage());
                                }
                            });
                    break;
                case TXConstants.AVIMCMD_ExitLive:
                    mActivity.memberQuit(identifier, nickname);
                    break;
                case TXConstants.AVIMCMD_FakeMember_Leave:
                    //机器人离开
                    Map<String, String> userInfoMap2 = EamApplication.getInstance()
                            .getGsonInstance()
                            .fromJson(json.getString(TXConstants.CMD_PARAM), new
                                    TypeToken<HashMap<String, String>>()
                                    {
                                    }.getType());
                    mActivity.fakeMemberQuit(userInfoMap2.get("id"), userInfoMap2.get("nickName")
                            , userInfoMap2.get("headImg"));
                    break;
                case TXConstants.AVIMCMD_Member_Enter:
                    break;
                case TXConstants.AVIMCMD_MULTI_CANCEL_INTERACT: //主播关闭摄像头命令 如果是自己关闭Camera和Mic
                    Map<String, String> map = EamApplication.getInstance().getGsonInstance()
                            .fromJson(json.getString(TXConstants.CMD_PARAM), new
                                    TypeToken<HashMap<String, String>>()
                                    {
                                    }.getType());
                    EamLogger.t(TAG).writeToDefaultFile("关闭连麦发送》 senderid>" + identifier + " " +
                            "自定义消息》" + json.getString(TXConstants.CMD_PARAM));
                    if (!currMsg.getSender().equals(SharePreUtils.getTlsName(mActivity)))
                    {
                        //如果是自己
                        if (map.get("closeId").equals(SharePreUtils.getTlsName(mActivity)))
                        {
                            if (TextUtils.isEmpty(map.get("reason")))
                            {
                                ToastUtils.showShort("主播已取消连麦");
                            }
                            else
                            {
                                if ("0".equals(map.get("reason")))
                                    ToastUtils.showShort("主播已取消连麦");
                                else if ("2".equals(map.get("reason")))
                                    ToastUtils.showShort("网络连接超时，连麦已取消");
                            }
                            if (mActivity.backGroundId.equals(map.get("closeId")))
                            {
                                mActivity.avRootView.swapVideoView(0, 1);
                                mActivity.backGroundId = mActivity.avRootView.getViewByIndex(0)
                                        .getIdentifier();
                                Logger.t(TAG).d("backGroundId:" + mActivity.backGroundId);
                            }
                            down2MemberVideo();
                        }
                        if (mRecord.getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                        {
                            ToastUtils.showShort(nickname + "已取消连麦");
                        }
                        if (mActivity.backGroundId.equals(map.get("closeId")))
                        {
                            mActivity.avRootView.swapVideoView(0, 1);
                            mActivity.backGroundId = mActivity.avRootView.getViewByIndex(0)
                                    .getIdentifier();
                            Logger.t(TAG).d("backGroundId:" + mActivity.backGroundId);
                        }

                        if (mRecord.getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                        {
                            //重置 绑定关系 全部解除绑定
                            if (mActivity.backGroundId.equals(SharePreUtils.getTlsName(mActivity)))
                                mActivity.avRootView.bindIdAndView(1, AVView
                                        .VIDEO_SRC_TYPE_CAMERA, null, true);
                            else
                                mActivity.avRootView.bindIdAndView(0, AVView
                                        .VIDEO_SRC_TYPE_CAMERA, null, true);
                        }
                        else
                        {
                            if (mActivity.backGroundId.equals(SharePreUtils.getTlsName(mActivity)))
                                mActivity.avRootView.bindIdAndView(0, AVView
                                        .VIDEO_SRC_TYPE_CAMERA, null, true);
                            else
                                mActivity.avRootView.bindIdAndView(1, AVView
                                        .VIDEO_SRC_TYPE_CAMERA, null, true);
                        }
                        //其他人关闭小窗口
                        mActivity.avRootView.closeUserView(map.get("closeId"), AVView
                                .VIDEO_SRC_TYPE_CAMERA, true);
                        mActivity.hideCloseInviteBtn();
                    }
                    else
                    {
                        if (mActivity.backGroundId.equals(map.get("closeId")))
                        {
                            mActivity.avRootView.swapVideoView(0, 1);
                            mActivity.backGroundId = mActivity.avRootView.getViewByIndex(0)
                                    .getIdentifier();
                            Logger.t(TAG).d("backGroundId:" + mActivity.backGroundId);
                        }
                    }
                    //重置 绑定关系 这里是下麦， 然后除了主播的0 全部解除绑定
//                    for (int i = 1; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++)
//                    {
//                        mActivity.avRootView.bindIdAndView(i, AVView.VIDEO_SRC_TYPE_CAMERA,
// null,true);
//                    }
                    mActivity.isShowInviteLive = false;
                    break;
                case TXConstants.AVIMCMD_Host_Leave:
                    if (identifier.equals("u" + mRecord.getRoomId()))
                        mActivity.hostLeave(identifier, nickname);
                    break;
                case TXConstants.AVIMCMD_Host_Back:
                    if (identifier.equals("u" + mRecord.getRoomId()))
                        mActivity.hostBack(identifier, nickname);
                    break;
                case TXConstants.AVIMCMD_Host_ShutUp_On:
                    Logger.t(TAG).d("主播禁言用户--> " + json.getString(TXConstants.CMD_PARAM));
                    JSONObject shutUpJson = new JSONObject(json.getString(TXConstants.CMD_PARAM));
                    mActivity.hostShutUpOff(shutUpJson.getString("roomId"), shutUpJson.getString
                                    ("nickName")
                            , shutUpJson.getString("txId"), userLevel);
                    break;
                case TXConstants.AVIMCMD_ADMIN_SHUTUP_ON:
                    Logger.t(TAG).d("管理员禁言用户--> " + json.getString(TXConstants.CMD_PARAM));
                    JSONObject shutUpByAdminJson = new JSONObject(json.getString(TXConstants
                            .CMD_PARAM));
                    mActivity.adminShutUpOff(shutUpByAdminJson.getString("roomId"),
                            shutUpByAdminJson.getString("nickName")
                            , shutUpByAdminJson.getString("txId"), userLevel);
                    break;
                case TXConstants.AVIMCMD_ADMIN_SHUTUP_OFF:
                    Logger.t(TAG).d("管理员解除禁言用户--> " + json.getString(TXConstants.CMD_PARAM));
                    JSONObject shutUpOffByAdminJson = new JSONObject(json.getString(TXConstants
                            .CMD_PARAM));
                    mActivity.adminShutUpOn(shutUpOffByAdminJson.getString("roomId"),
                            shutUpOffByAdminJson.getString("nickName")
                            , shutUpOffByAdminJson.getString("txId"), userLevel);
                    break;
                case TXConstants.AVIMCMD_Host_ShutUp_Off:
                    Logger.t(TAG).d("主播解除禁言用户--> " + json.getString(TXConstants.CMD_PARAM));
                    JSONObject shutUpOffJson = new JSONObject(json.getString(TXConstants
                            .CMD_PARAM));
                    mActivity.hostShutUpOn(shutUpOffJson.getString("roomId"), shutUpOffJson
                                    .getString("nickName")
                            , shutUpOffJson.getString("txId"), userLevel);
                    break;
                case TXConstants.AVIMCMD_Praise:
                    mActivity.flowHeart();
                    break;
                case TXConstants.AVIMCMD_Praise_Msg:
                    String htmlStr = String.format("<font color=%s>%s</font>", TXConstants
                            .ENTER_ROOM_MSG_CONTENT_COLOR, "点亮了爱心");
                    RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
                    refreshLiveMsgBean.setText("点亮了爱心");
                    refreshLiveMsgBean.setId(identifier);
                    String htmlName = String.format("<font color=%s>%s</font>", TXConstants
                            .ENTER_ROOM_NAME_COLOR, nickname);
                    refreshLiveMsgBean.setName(nickname);
                    refreshLiveMsgBean.setType(LiveMsgType.ParseHost);
                    refreshLiveMsgBean.setLevel(userLevel);
                    refreshLiveMsgBean.setLiveLevelState("1");
                    mActivity.refreshText(refreshLiveMsgBean);
                    break;
                case TXConstants.AVIMCMD_Booty_Call:
                    //mActivity.startBootyCallMsgAni();
                    break;
                case TXConstants.AVIMCMD_Send_Barrage:
                    mActivity.shoot(faceUrl, userLevel, nickname
                            , json.getString(TXConstants.CMD_PARAM), isVuser);
                    break;
                case TXConstants.AVIMCMD_ROOM_ADMIN:
                    Logger.t(TAG).d("你已经被主播设置房管!++" + nickname + " , " + TXConstants.CMD_PARAM);
                    mActivity.setAdminNotify();
                    break;
                case TXConstants.AVIMCMD_ROOM_ADMIN_CANCEL:
                    Logger.t(TAG).d("你已经被主播取消了房管!");
                    mActivity.cancelAdminNotify();
                    break;
                case TXConstants.AVIMCMD_SEND_RED_PACKET:
                    Logger.t(TAG).d("测试群发红包 streamId>>" + json.getString(TXConstants.CMD_PARAM) +
                            " , userLevel--> " + userLevel);
                    RefreshLiveMsgBean sendRedPacketMsgBean = new RefreshLiveMsgBean();
                    //    String htmlSStr = String.format("<font color=%s>%s</font>", TXConstants
                    // .ENTER_ROOM_SEND_RED_COLOR, "发了一个红包~ ");
                    sendRedPacketMsgBean.setText("发了一个红包~ ");
                    sendRedPacketMsgBean.setId(identifier);
                    sendRedPacketMsgBean.setName(nickname);
                    sendRedPacketMsgBean.setType(LiveMsgType.SendRedPacket);
                    sendRedPacketMsgBean.setLevel(userLevel);
                    sendRedPacketMsgBean.setLiveLevelState("1");
                    sendRedPacketMsgBean.setStreamId(json.getString(TXConstants.CMD_PARAM));
                    mActivity.refreshText(sendRedPacketMsgBean);
                    break;
                case TXConstants.AVIMCMD_NOTIFY_ROOM_ADMIN:
                    Logger.t(TAG).d("设置房管群消息--> " + json.getString(TXConstants.CMD_PARAM));
                    JSONObject jsonObject = new JSONObject(json.getString(TXConstants.CMD_PARAM));
                    String luId = jsonObject.getString("luId");
                    String nickName = jsonObject.getString("nickName");
                    String toTxId = jsonObject.getString("txId");
                    RefreshLiveMsgBean setAdminLiveMsgBean = new RefreshLiveMsgBean();
                    String htmlFontStr;
                    if (mRecord.getEnterRoom4EH().getUser().equals(luId))
                    {
                        // htmlFontStr = String.format("<font color=%s>%s</font>", TXConstants
                        // .ENTER_ROOM_SYSTEM_COLOR, "主播设置了你为房管!");
                        htmlFontStr = "主播设置了你为房管!";
                        setAdminLiveMsgBean.setName("");
                    }
                    else
                    {
//                        htmlFontStr = String.format("<font color=%s>%s</font>",
//                                TXConstants.ENTER_ROOM_SYSTEM_COLOR, "被主播设为房管");
                        htmlFontStr = "被主播设为房管";
                        setAdminLiveMsgBean.setName(nickName);
                    }
                    setAdminLiveMsgBean.setText(htmlFontStr);
                    setAdminLiveMsgBean.setId(toTxId);
                    setAdminLiveMsgBean.setType(LiveMsgType.Admin);
                    setAdminLiveMsgBean.setLiveLevelState("0");
                    mActivity.refreshText(setAdminLiveMsgBean);
                    break;
                case TXConstants.AVIMCMD_NOTIFY_ROOM_ADMIN_CANCEL:
                    Logger.t(TAG).d("取消房管群消息--> " + json.getString(TXConstants.CMD_PARAM));
                    JSONObject jObject = new JSONObject(json.getString(TXConstants.CMD_PARAM));
                    String cancelLuId = jObject.getString("luId");
                    String cancelNickName = jObject.getString("nickName");
                    String txId = jObject.getString("txId");
                    RefreshLiveMsgBean cancelAdminMsgBean = new RefreshLiveMsgBean();
                    String cancelAdminFontStr;
                    if (mRecord.getEnterRoom4EH().getUser().equals(cancelLuId))
                    {
                        // cancelAdminFontStr = String.format("<font color=%s>%s</font>",
                        // TXConstants.ENTER_ROOM_SYSTEM_COLOR, "主播取消了你的房管!");
                        cancelAdminFontStr = "主播取消了你的房管!";
                        cancelAdminMsgBean.setName("");
                    }
                    else
                    {
//                        cancelAdminFontStr = String.format("<font color=%s>%s</font>",
//                                TXConstants.ENTER_ROOM_SYSTEM_COLOR, "被主播取消了房管");
                        cancelAdminFontStr = "被主播取消了房管";
                        cancelAdminMsgBean.setName(cancelNickName);
                    }
                    cancelAdminMsgBean.setText(cancelAdminFontStr);
                    cancelAdminMsgBean.setType(LiveMsgType.NotAdmin);
                    cancelAdminMsgBean.setLiveLevelState("0");
                    cancelAdminMsgBean.setId(txId);
                    mActivity.refreshText(cancelAdminMsgBean);
                    break;
                case TXConstants.AVIMCMD_NOTIFY_RED_HINT:

                    Logger.t(TAG).d("测试领取后红包消息" + identifier);
                    String getRedFontStr = String.format("<font color=%s>%s</font>",
                            TXConstants.ENTER_ROOM_SYSTEM_COLOR, "领取了你的红包");
                    RefreshLiveMsgBean getRedMsgBean = new RefreshLiveMsgBean();
                    getRedMsgBean.setText("领取了你的红包");
                    getRedMsgBean.setId(identifier.split("u")[1]);
                    getRedMsgBean.setName(nickname);
                    getRedMsgBean.setType(LiveMsgType.ReceiveRedToSendPacket);
                    getRedMsgBean.setLiveLevelState("2");
                    mActivity.refreshText(getRedMsgBean);
                    break;
                case TXConstants.AVIMCMD_NOTIFY_RED_HINT_GROUP:

                    jObject = new JSONObject(json.getString(TXConstants.CMD_PARAM));

                    if (jObject.getString("toId").equals("u" + SharePreUtils.getId(getView())))
                    {
                        Logger.t(TAG).d("测试领取后红包消息" + identifier);
//                        getRedFontStr = String.format("<font color=%s>%s</font>",
//                                TXConstants.ENTER_ROOM_SYSTEM_COLOR, "领取了你的红包");
                        getRedMsgBean = new RefreshLiveMsgBean();
                        getRedMsgBean.setText("领取了你的红包");
                        getRedMsgBean.setId(identifier.split("u")[1]);
//                        String nameHtml = String.format("<font color=%s>%s</font>",
//                                TXConstants.ENTER_ROOM_NAME_COLOR, nickname);
                        getRedMsgBean.setName(nickname);
                        getRedMsgBean.setType(LiveMsgType.ReceiveRedToSendPacket);
                        getRedMsgBean.setLiveLevelState("2");
                        mActivity.refreshText(getRedMsgBean);
                    }
                    break;
                case TXConstants.AVIMCMD_GAME_INVITATION://收到游戏邀请
                    JSONObject invitationJson = new JSONObject(param);
                    String toInvitationTxId = invitationJson.getString("toTxId");
                    List<String> txIds = EamApplication.getInstance().getGsonInstance().fromJson
                            (toInvitationTxId, new TypeToken<List<String>>()
                            {
                            }.getType());
                    Logger.t(TAG).d("收到游戏邀请" + txIds);
                    if (txIds.contains("u" + SharePreUtils.getId(mActivity)))
                    {
                        Logger.t(TAG).d("收到游戏邀请" + toInvitationTxId);
                        mActivity.startGameIconAnim();
                    }
                    break;
                case TXConstants.AVIMCMD_GAME_INVITE_ACCEPT://对方同意游戏邀请
                    JSONObject acceptJson = new JSONObject(param);
                    String acceptTxId = acceptJson.getString("toTxId");
                    if (TextUtils.equals("u" + SharePreUtils.getId(mActivity), acceptTxId))
                    {
                        Logger.t(TAG).d("对方同意游戏邀请" + acceptTxId);
                        checkPopups("0002");
                    }
                    break;
                case TXConstants.AVIMCMD_GAME_INVITE_REJECT://对方拒绝游戏邀请
                    Logger.t(TAG).d("拒绝邀请" + param + "id >" + identifier);
                    JSONObject rejectJson = new JSONObject(param);
                    String rejectTxId = rejectJson.getString("toTxId");
                    if (TextUtils.equals("u" + SharePreUtils.getId(mActivity), rejectTxId))
                    {
                        new CustomAlertDialog(mActivity)
                                .builder()
                                .setTitle("提示")
                                .setMsg(nickname + "拒绝了你的游戏邀请")
                                .setCancelable(true)
                                .show();
                        String id = identifier.substring(1, identifier.length());
                        mActivity.refreshGameInviteList(id);
                    }
                    break;
                case TXConstants.AVIMCMD_DIFFER_STAR_COUNT:
                    Logger.t(TAG).d("主播与上一名的星光值差距" + param);
                    JSONObject differStarJson = new JSONObject(param);
                    String differMsg = differStarJson.getString("msg");//消息内容
                    String star = differStarJson.getString("star");//星光值
                    String ranking = differStarJson.getString("ranking");//当前排名
                    if (mActivity != null)
                    {
                        mActivity.setDifferMsg(differMsg, star, ranking);
                    }
                    break;
                case TXConstants.AVIMCMD_STAR_FREE_GIFT:
                    JSONObject freeGiftJson = new JSONObject(param);
                    String msg = freeGiftJson.getString("msg");//弹窗提示内容
                    String url = freeGiftJson.getString("url");//礼物图片
                    String luIds = freeGiftJson.getString("luId");//用户uId
                    String roomId = freeGiftJson.getString("roomId");//直播间id
                    if (TextUtils.equals(luIds, SharePreUtils.getUId(null)) && TextUtils.equals
                            (roomId, mRecord.getRoomId()) && !mActivity.isFinishing())//不是主播,又在直播间
                    {
                        Logger.t(TAG).d("用户领取免费礼物 Group消息" + param);
                        mActivity.showFreeStarNotify(msg, url);
                    }
                    break;
                default:
                    break;
            }
        } catch (UnsupportedEncodingException | JSONException e)
        {
            e.printStackTrace();
            EamLogger.t(TAG).writeToDefaultFile("自定义消息解析失败1》" + e.getMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
            EamLogger.t(TAG).writeToDefaultFile("自定义消息解析失败2》" + e.getMessage());
        }
    }


    public void toggleCamera()
    {
        bCameraOn = !bCameraOn;
        Logger.t(TAG).d("toggleCamera->change camera:" + bCameraOn);

        ILiveRoomManager.getInstance().enableCamera(ILiveRoomManager.getInstance().getCurCameraId
                (), bCameraOn);
    }

    public void toggleMic()
    {
        bMicOn = !bMicOn;
        Logger.t(TAG).d("toggleMic->change mic:" + bMicOn);
        ILiveRoomManager.getInstance().enableMic(bMicOn);
    }

    /**
     * 发送自定义消息
     *
     * @param transElement 消息带的参数 cmd  params 等
     * @param evt          根据evt  在onservercallback 中接收返回值
     */
    public void sendTXIMMessage(Map<String, Object> transElement, String evt)
    {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("GroupId", mRecord.getRoomId());
        reqMap.put("From_Account", SharePreUtils.getTlsName(mActivity));
        reqMap.put("Random", new Random().nextInt(9999999));
        reqMap.put("MsgBody", makeMsgBody(transElement));
        callTXServer(evt, transElement, reqMap);
    }

    /**
     * 发送自定义消息
     *
     * @param transElement 消息带的参数 cmd  params 等
     * @param roomId       向指定群组发送消息
     * @param evt          根据evt  在onservercallback 中接收返回值
     */
    public void sendTXIMMessage(Map<String, Object> transElement, String roomId, String evt)
    {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("GroupId", roomId);
        reqMap.put("From_Account", SharePreUtils.getTlsName(mActivity));
        reqMap.put("Random", new Random().nextInt(9999999));
        reqMap.put("MsgBody", makeMsgBody(transElement));
        callTXServer(evt, transElement, reqMap);
    }

    private List<Object> makeMsgBody(Map<String, Object> transElement)
    {
        //自定义消息体
        List<Object> MsgBody = new ArrayList<>();
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("MsgType", "TIMCustomElem");
        Map<String, String> msgContent = new HashMap<>();
        JSONObject inviteCmd = new JSONObject();
        try
        {
//            inviteCmd.put(TXConstants.CMD_KEY, transElement.get(TXConstants.CMD_KEY));
//            inviteCmd.put(TXConstants.CMD_PARAM, transElement.get(TXConstants.CMD_PARAM));

            //TODO  为了适配IOS 3.0暂时这么写
            Iterator<String> it = transElement.keySet().iterator();
            while (it.hasNext())
            {
                String key = it.next();
                inviteCmd.put(key, transElement.get(key));

            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        String json = inviteCmd.toString();
        if (json.contains("gift") && !json.contains(TXConstants.CMD_KEY))
        {
            try
            {
                String gift = (String) inviteCmd.get("gift");
                json = inviteCmd.put("gift", -1).toString();
                json = json.toString().replace("\\", "");
                json = json.replace("-1", gift);

            } catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
        msgContent.put("Data", json);
        msgContent.put("Desc", "");
        msgContent.put("Ext", "");
        msgMap.put("MsgContent", msgContent);
        MsgBody.add(msgMap);
        return MsgBody;
    }

    /**
     * 发送聊天消息
     *
     * @param msg
     */
    public void sendTXIMTextMessage(final String msg)
    {
        if (mActivity == null)
            return;
        if (TextUtils.isEmpty(msg))
        {
            mActivity.sendMessageCallback(1, msg);
            return;
        }
        //发送消息
        ILVText iliveText = new ILVText(ILVText.ILVTextType.eGroupMsg, "", msg);
        int res = ILVLiveManager.getInstance().sendText(iliveText, new ILiveCallBack()
        {
            @Override
            public void onSuccess(Object data)
            {
                mActivity.sendMessageCallback(0, msg);
            }

            @Override
            public void onError(String module, int errCode, String errMsg)
            {
                if (errCode == 10017)
                {
                    mActivity.sendMessageCallback(3, msg);
                }
            }
        });

    }

    /**
     * 发送聊天消息
     *
     * @param msg
     */
    public void sendTextMessage(final String msg)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.remark, msg);
        reqMap.put(ConstCodeTable.roomId, getmRecord().getRoomId());
        callServerSilence(NetInterfaceConstant.LiveC_sendMsg, null, null, reqMap);
    }

    /**
     * 下麦
     */
    public void down2MemberVideo()
    {
        ILVLiveManager.getInstance().downToNorMember(TXConstants.NORMAL_MEMBER_ROLE, new
                ILiveCallBack<ILVChangeRoleRes>()
                {
                    @Override
                    public void onSuccess(ILVChangeRoleRes data)
                    {
                        String id = "";
                        for (int i = 0; i < mActivity.mRenderUserList.size(); i++)
                        {
                            id += mActivity.mRenderUserList.get(i);
                        }
                        EamLogger.t(TAG).writeToDefaultFile("下麦成功: id: " + id);
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg)
                    {
                        Logger.t(TAG).d("下麦失败：module：" + module + "| errorCode:" + errCode + "| errMgs:"
                                + errMsg);
                        EamLogger.t(TAG).writeToDefaultFile("下麦失败：module：" + module + "| errorCode:" +
                                errCode + "| errMgs:" + errMsg);
                    }
                });
    }

    /**
     * 上麦
     *
     * @param callBack
     */
    public void up2MemberVideo(ILiveCallBack<ILVChangeRoleRes> callBack)
    {
        ILVLiveManager.getInstance().upToVideoMember(TXConstants.VIDEO_MEMBER_ROLE, true, true,
                callBack != null ? callBack : new ILiveCallBack<ILVChangeRoleRes>()
                {
                    @Override
                    public void onSuccess(ILVChangeRoleRes data)
                    {
                        Logger.t(TAG).d("上麦成功");
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg)
                    {
                        Logger.t(TAG).d("上麦失败");
                    }
                });
    }

    /**
     * @param id
     * @param reason 0主播关闭，1用户关闭，2超时主播关闭
     */
    public void sendCloseInvite(String id, String reason)
    {
        Map<String, String> map = new HashMap<>();
        map.put("closeId", id);
        map.put("reason", reason);
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_MULTI_CANCEL_INTERACT);
        msgMap.put(TXConstants.CMD_PARAM, EamApplication.getInstance().getGsonInstance().toJson
                (map));
        sendTXIMMessage(msgMap, "sendCloseInvite");
    }


    /**
     * 发送弹幕消息
     *
     * @param msg
     */
    public void sendBarrageMessage(final String orV, final String msg, final String headUrl,
                                   final String level, final String name)
    {
//        Map<String, Object> transElement = new HashMap<>();
//        transElement.put("cmd", TXConstants.AVIMCMD_Send_Barrage);
//        transElement.put("param", "{\"msg\":\"" + msg + "\"}");

        //为了防止突破禁言限制，更替方法
        // sendTXIMMessage(transElement, TXConstants.MESSAGE_CMD, "");
        //sendGroupCmd(TXConstants.AVIMCMD_Send_Barrage, "{\"msg\":\"" + msg + "\"}");

        sendGroupCmd(TXConstants.AVIMCMD_Send_Barrage, "{\"msg\":\"" + msg + "\"}", new
                ILiveCallBack()
                {
                    @Override
                    public void onSuccess(Object data)
                    {
                        Logger.t(TAG).d("sendCmd->success:" + TXConstants.AVIMCMD_Send_Barrage + "|" + msg);
                        mActivity.shoot(headUrl, level, name, "{\"msg\":\"" + msg + "\"}", orV);
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg)
                    {
                    }
                });

    }

    /**
     * 发送信令
     */

    public int sendGroupCmd(int cmd, String param, ILiveCallBack callBack)
    {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        return sendCmd(customCmd, callBack);
    }

    /**
     * 发送C2C命令
     *
     * @param cmd    命令
     * @param param  参数
     * @param destId 目标id
     * @return
     */
    public int sendC2CCmd(final int cmd, Object param, String destId)
    {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setDestId(destId);
        customCmd.setCmd(cmd);
        // ios领取红包的时候 不知为什么取不到最新的名字 添加此字段
        if (param != null)
        {
            String iosParam = new Gson().toJson(param);
            customCmd.setParam(iosParam);
        }
        customCmd.setType(ILVText.ILVTextType.eC2CMsg);
        return sendCmd(customCmd, null);
    }

    public int sendC2CCmd(final int cmd, String param, String destId, ILiveCallBack callBack)
    {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setDestId(destId);
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eC2CMsg);
        return sendCmd(customCmd, callBack);
    }

    private int sendCmd(final ILVCustomCmd cmd, ILiveCallBack callBack)
    {
        ILiveCallBack iLiveCallBack = null;
        if (null == callBack)
        {
            iLiveCallBack = new ILiveCallBack()
            {

                @Override
                public void onSuccess(Object data)
                {
                    Logger.t(TAG).d("sendCmd->success:" + cmd.getCmd() + "|" + cmd.getParam());
                }

                @Override
                public void onError(String module, int errCode, String errMsg)
                {
                }
            };
        }
        else
            iLiveCallBack = callBack;
        return ILVLiveManager.getInstance().sendCustomCmd(cmd, iLiveCallBack);
    }


    /**
     * 发送在线信令
     */
    public int sendOnlineGroupCmd(int cmd, String param, ILiveCallBack callBack)
    {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        return sendC2COnlineCmd(customCmd, callBack);
    }

    /**
     * 发送腾讯在线C2C 消息
     *
     * @param cmd      cmd指令
     * @param param    附加信息
     * @param destId   接收用户ID
     * @param callBack callback
     * @return 发送结果
     */
    public int sendOnlineC2CCmd(final int cmd, String param, String destId, ILiveCallBack callBack)
    {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setDestId(destId);
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eC2CMsg);
        return sendC2COnlineCmd(customCmd, callBack);
    }

    private int sendC2COnlineCmd(final ILVCustomCmd cmd, ILiveCallBack callBack)
    {
        if (null == callBack)
        {
            callBack = new ILiveCallBack()
            {
                @Override
                public void onSuccess(Object data)
                {
                    Logger.t(TAG).d("sendC2COnlineCmd->success:" + cmd.getCmd() + "|" + cmd
                            .getParam());
                }

                @Override
                public void onError(String module, int errCode, String errMsg)
                {
                    Logger.t(TAG).d("sendC2COnlineCmd->failed:" + cmd.getCmd() + "|" + cmd
                            .getParam() + " | module:" + module + " | errCode：" + errCode + " | " +
                            "errMsg:" + errMsg);
                }
            };
        }
        return ILVLiveManager.getInstance().sendOnlineCustomCmd(cmd, callBack);
    }

    public void pullAudiences(int index, Map<String, Object> tranParam)
    {
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(mActivity);
        reqParam.put(ConstCodeTable.roomId, mRecord.getRoomId());
        reqParam.put(ConstCodeTable.startIdx, index + "");
        reqParam.put(ConstCodeTable.num, "20");

        //防止roomid为null无效请求
        if (TextUtils.isEmpty(reqParam.get(ConstCodeTable.roomId)))
            return;

        callServerSilence(NetInterfaceConstant.LiveC_roomMember_v307, tranParam, "1", reqParam);
    }

/*    public void getGroundRed(String streamId, String roomId, String txId)
    {
        Map<String, Object> transElement = new HashMap<>();
        transElement.put("txId", txId);
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(mActivity);
        reqParam.put(ConstCodeTable.streamId, streamId);
        reqParam.put(ConstCodeTable.roomId, roomId);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_getGroupRed,
        new Gson().toJson(reqParam));
        Logger.t(TAG).d("收群红包参数--> " + paramJson);
        callServerSilence(NetInterfaceConstant.LiveC_getGroupRed, transElement, "1", reqParam);
    }*/

    private void ownerSetRoomAdmin(final String txId, String roomId)
    {
        Map<String, Object> transElement = new HashMap<>();
        transElement.put("txId", txId);
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_roomAdminList,
                new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("请求房管列表参数--> " + paramJson);
        callServerSilence(NetInterfaceConstant.LiveC_roomAdminList, transElement, "1", reqParamMap);
    }


    /**
     * 以群简介来判断主播是否离开
     *
     * @param status         1代表离开 , 0代表回来
     * @param resultCallback
     */
    public void switchHostStatus(String roomId, final String status, TIMCallBack resultCallback)
    {
        tempStatus = status;
        tempRoomId = roomId;
        if (resultCallback == null)
        {
            resultCallback = new TIMCallBack()
            {
                @Override
                public void onError(int i, String s)
                {
                    changeHostStatusCount++;
                    if (changeHostStatusCount <= 3)
                    {
                        switchHostStatus(tempRoomId, tempStatus, null);
                    }
                    Logger.t(TAG).d("code>" + i + "des>" + s);
                    //code:6014   des: ERR_SDK_NOT_LOGGED_IN
                    EamLogger.t(TAG).writeToDefaultFile("修改群简介失败： i :" + i + ",s:" + s);
                }

                @Override
                public void onSuccess()
                {
                    changeHostStatusCount = 0;
                    Logger.t(TAG).d("设置成功> 状态为》" + status);
                }
            };
        }
        TIMGroupManager.getInstance().modifyGroupIntroduction(roomId, status, resultCallback);
    }

    public void sendGift(String chosenGiftId, String chosenGiftNum, GiftBean giftBean, int gifcount)
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
        transElement.put("giftCount", gifcount);

        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        reqMap.put(ConstCodeTable.gId, chosenGiftId);
        reqMap.put(ConstCodeTable.roomId, mRecord.getRoomId());
        reqMap.put(ConstCodeTable.gNum, chosenGiftNum);
        callServerSilence(NetInterfaceConstant.LiveC_sendGift, transElement, null, reqMap);
    }

//    public void downMemberVideo(){
//        ILVLiveManager.getInstance().downToNorMember(TXConstants.NORMAL_MEMBER_ROLE, new
// ILiveCallBack() {
//            @Override
//            public void onSuccess(Object data) {
//                bMicOn = false;
//                bCameraOn = false;
//                SxbLog.e(TAG, "downMemberVideo->onSuccess");
//            }
//
//            @Override
//            public void onError(String module, int errCode, String errMsg) {
//                SxbLog.e(TAG, "downMemberVideo->failed:"+module+"|"+errCode+"|"+errMsg);
//            }
//        });
//    }

    /**
     * 确保观众列表长度不超过一定长度，且只移除假用户
     */
    private void trimAudienceList()
    {
        //确保观众列表的头像不超过AUDIENCE_COUNT个
        if (arrAudiencesObj.size() > AUDIENCE_MAX_COUNT)
        {
            arrAudiencesObj.remove(AUDIENCE_MAX_COUNT);
            //降低复杂性
/*            int index = 0;
            for (int i = 0; i < arrAudiencesObj.size(); i++)
            {
                if ("1".equals(arrAudiencesObj.get(i).getIsGhost()))
                {
                    index = i;
                    break;
                }
            }
            arrAudiencesObj.remove(index == 0 ? AUDIENCE_MAX_COUNT : index);*/
        }
    }


    public void requestViewList(List<String> ids ,int type)
    {
        if (ids.size() == 0)
            return;
        AVView mRequestViewList[] = new AVView[ids.size()];
        String mRequestIdentifierList[] = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++)
        {
            AVView view = new AVView();
            view.videoSrcType = type;
            view.viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;
            mRequestViewList[i] = view;
            mRequestIdentifierList[i] = ids.get(i);
            if (mActivity.backGroundId.equals(ids.get(i)))
                mActivity.avRootView.bindIdAndView(0, view.videoSrcType, ids.get(i), true);
            else
                mActivity.avRootView.bindIdAndView(1, view.videoSrcType, ids.get(i), true);

//            if (ids.get(i).equals("u" + getmRecord().getRoomId()))
//            {
//                    mActivity.avRootView.bindIdAndView(0, view.videoSrcType, ids.get(i), true);
//                EamLogger.t(TAG).writeToDefaultFile("bindIdAndView id : " + ids.get(i) + " | "
// + "index:" + 0);
//            }
//            else
//            {
//                mActivity.avRootView.bindIdAndView(1, view.videoSrcType, ids.get(i), true);
//                EamLogger.t(TAG).writeToDefaultFile("bindIdAndView id : " + ids.get(i) + " | "
// + "index:" + 1);
//            }
        }
        Logger.t(TAG).d("requestViewList执行了");
        ILiveRoomManager.getInstance().getAvRoom().requestViewList(mRequestIdentifierList,
                mRequestViewList, ids.size(), new AVRoomMulti.RequestViewListCompleteCallback()
                {
                    @Override
                    public void OnComplete(String[] identifierList, AVView[] avViews, int i, int i1,
                                           String s)
                    {
                        String ids = "";
                        for (String s1 : identifierList)
                        {
                            ids += s1 + " | ";
                        }
                        EamLogger.t(TAG).writeToDefaultFile("渲染视频ids：" + ids);
                        Logger.t(TAG).d("requestViewListCallBack:" + ids + "i:" + i + " | i1:" + i1 + " |" +
                                " s:" + s);
                        for (int j = 0; j < identifierList.length; j++)
                        {
                            boolean isRenderSuc = mActivity.avRootView.renderVideoView(true,
                                    identifierList[j], avViews[j].videoSrcType, false);
                            EamLogger.t(TAG).writeToDefaultFile("渲染视频成功：" + isRenderSuc + " | id:" +
                                    identifierList[j] + "\r\n");
                        }
                    }
                });
    }


    /**
     * 检查用户是否在群组中
     *
     * @param userId   用户ID
     * @param callBack
     */
    public void checkUserIsInGroup(String userId, TIMValueCallBack<List<TIMGroupMemberInfo>>
            callBack)
    {
        List<String> ids = new ArrayList<>();
        ids.add(userId);
        TIMGroupManager.getInstance().getGroupMembersInfo(mRecord.getRoomId(), ids, callBack !=
                null ? callBack : new TIMValueCallBack<List<TIMGroupMemberInfo>>()
        {
            @Override
            public void onError(int i, String s)
            {
                Logger.t(TAG).d("查询资料失败 i :" + i + ",s:" + s);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos)
            {
                Logger.t(TAG).d("查询资料失败");
            }
        });
    }

    /**
     * 礼物mgbean
     * Created by an on 2016/10/25 0025.
     */
    private class GiftMsgBean
    {
        String isGift;
        GiftBean gift;
        String number;
        String mealTotal;

        public String getMealTotal()
        {
            return mealTotal;
        }

        public void setMealTotal(String mealTotal)
        {
            this.mealTotal = mealTotal;
        }

        public String getIsGift()
        {
            return isGift;
        }

        public void setIsGift(String isGift)
        {
            this.isGift = isGift;
        }

        public GiftBean getGift()
        {
            return gift;
        }

        public void setGift(GiftBean gift)
        {
            this.gift = gift;
        }

        public String getNumber()
        {
            return number;
        }

        public void setNumber(String number)
        {
            this.number = number;
        }
    }

    /**
     * 房管设置
     *
     * @param flag 目前传0或1都可以 后台已做判断
     */
    public void setRoomAdmin(String flag, String roomAdminUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.roomId, mRecord.getRoomId());
        reqParamMap.put(ConstCodeTable.lUId, roomAdminUid);
        reqParamMap.put(ConstCodeTable.flg, flag);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_roomAdmin, new
                Gson().toJson(reqParamMap));
        Logger.t(TAG).d("再次进入房间设置房管参数--> " + paramJson);
        callServerSilence(NetInterfaceConstant.LiveC_roomAdmin, null, "1", reqParamMap);
    }

    /**
     * 取消房管
     *
     * @param flag         目前传0或1都可以 后台已做判断
     * @param roomAdminUid
     */
    public void cancelRoomAdmin(String flag, String roomAdminUid)
    {
        Map<String, Object> transElement = new HashMap<>();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.roomId, mRecord.getRoomId());
        reqParamMap.put(ConstCodeTable.lUId, roomAdminUid);
        reqParamMap.put(ConstCodeTable.flg, flag);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant
                .LiveC_cancelRoomAdmin, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("再次进入房间取消房管参数--> " + paramJson);
        callServerSilence(NetInterfaceConstant.LiveC_cancelRoomAdmin, transElement, "1",
                reqParamMap);
    }

    public void setAdminTx(String roomId, String targetId, TIMCallBack callBack)
    {
        TIMGroupManager.getInstance().modifyGroupMemberInfoSetRole(roomId, targetId,
                TIMGroupMemberRoleType.Admin, callBack);

    }

    /**
     * 指定某些角色（现在是只有主播）可以将特定的人取消房管资格
     *
     * @param targetId 要被取消房管的那个人的 txId
     */
    public void txCancelAdmin(String roomId, String targetId, TIMCallBack callBack)
    {
        TIMGroupManager.getInstance().modifyGroupMemberInfoSetRole(roomId, targetId,
                TIMGroupMemberRoleType.Normal, callBack);
    }

    /**
     * 用户是否被禁言
     *
     * @param avRoomId
     * @param tUserName
     */
    public void getUserShutUpStatus(String avRoomId, final String tUserName, final
    IsShutUpCallBack shutUpCallBack)
    {
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put("GroupId", avRoomId);
        String paramJson = new Gson().toJson(reqParamMap);
        Logger.t(TAG).d("提交的请求：" + NetHelper.getRequestStrToTx(NetInterfaceConstant
                .TX_GetShutUpStatus, null)
                + paramJson);
        OkHttpUtils
                .postString()
                .url(NetHelper.getRequestStrToTx(NetInterfaceConstant.TX_GetShutUpStatus, null))
                .content(paramJson)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        Logger.t(TAG).d("该用户为假用户");
                        if (shutUpCallBack != null)
                            shutUpCallBack.requestNetError(call, e, TAG + NetInterfaceConstant
                                    .TX_GetShutUpStatus);
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("返回的结果》" + response);
                        try
                        {
                            JSONObject result = new JSONObject(response);
                            String status = result.getString("ActionStatus");
                            if (status.equals("OK"))
                            {
                                JSONArray jsonArray = result.getJSONArray("ShuttedUinList");
                                boolean isShutUp = false;
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jObj = jsonArray.getJSONObject(i);
                                    if (tUserName.equals(jObj.getString("Member_Account")))
                                    {
                                        isShutUp = true;
                                        break;
                                    }
                                }
                                if (shutUpCallBack != null)
                                    shutUpCallBack.isShutUpCallBack(isShutUp);
                            }
                            else
                            {
                                int errorCode = result.getInt("ErrorCode");
                                Logger.t(TAG).d("错误码》" + errorCode);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Logger.t(TAG).d("错误码》" + e.getMessage());
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            Logger.t(TAG).d("错误码》" + e.getMessage());
                        }
                    }
                });
    }

    public interface IsShutUpCallBack
    {
        void isShutUpCallBack(boolean isShutUp);

        void requestNetError(Call call, Exception e, String resourse);
    }

    public void setBeautyData(int beauty, int white)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            Logger.t(TAG).d("Android API level " + Build.VERSION.SDK_INT + " ilivefilter " +
                    "support!!");
            mUDFilter = new TILFilter(mActivity);
            // 小于或等于0：原始滤镜 1：美颜 2：浪漫 3：清新 4：唯美 5：粉嫩 6: 怀旧 7:蓝调  8: 清凉 9: 日系
            mUDFilter.setFilter(1);
            // 设置美颜级别 （级别为 0~7）
            mUDFilter.setBeauty(beauty);
            // 设置美白级别（级别为 0~9）
            mUDFilter.setWhite(white);
        }
        else
        {
            Logger.t(TAG).d("Android API level " + Build.VERSION.SDK_INT + " ilivefilter don't " +
                    "support!!");
        }
    }


    public void setBeautyCameraCallback()
    {
        ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(new AVVideoCtrl
                .LocalVideoPreProcessCallback()
        {
            @Override
            public void onFrameReceive(AVVideoCtrl.VideoFrame var1)
            {
                if (mUDFilter != null)
                {
                    // 回调的数据，传递给 ilivefilter processData接口处理
                    mUDFilter.processData(var1.data, var1.dataLen, var1.width, var1.height,
                            var1.srcType);
                }
            }
        });
    }

    public void destroyBeautyData()
    {
        ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        if (mUDFilter != null)
        {
            // 退出房间后，一定要销毁filter 资源；否则下次进入房间，setFilter将不生效或其他异常
            mUDFilter.setFilter(-1);
            mUDFilter.destroyFilter();
        }
    }

    @Override
    public void createRoom()
    {

    }

    @Override
    public void joinRoom(String roomId)
    {
    }

    @Override
    public void closeRoom(ExitRoomType closeType)
    {

    }

    @Override
    public void quitRoom(ExitRoomType quitType)
    {

    }

    @Override
    public void switchCamera()
    {

    }

    @Override
    public void notifySetAdminByHost(final Map<String, String> paramMap)
    {
        if (paramMap.get("inRoom").equals("0"))
        {
            setAdminTx(getmRecord().getRoomId(), "u" + paramMap.get("txId"), new TIMCallBack()
            {
                @Override
                public void onError(int i, String s)
                {
                    ToastUtils.showShort("设置房管失败");
                }

                @Override
                public void onSuccess()
                {
                    sendC2CCmd(TXConstants.AVIMCMD_ROOM_ADMIN, "", "u" + paramMap.get("txId"));
                    // 发C2C进行弹窗
                }
            });
        }
        Map<String, Object> transElement = new HashMap<>();
        transElement.put(TXConstants.CMD_PARAM, paramMap.get(TXConstants.CMD_PARAM));
        transElement.put(TXConstants.CMD_KEY, paramMap.get(TXConstants.CMD_KEY));
        sendTXIMMessage(transElement, getmRecord().getRoomId(), "");
    }


    @Override
    public void notifyCancelAdminByHost(final Map<String, String> paramMap)
    {
        if (paramMap.get("inRoom").equals("0"))
        {
            txCancelAdmin(getmRecord().getRoomId(), "u" + paramMap.get("txId"), new TIMCallBack()
            {
                @Override
                public void onError(int i, String s)
                {
                    ToastUtils.showShort("取消房管失败");
                }

                @Override
                public void onSuccess()
                {
                    sendC2CCmd(TXConstants.AVIMCMD_ROOM_ADMIN_CANCEL, "", "u" + paramMap.get
                            ("txId")); // 发C2C进行弹窗
                }
            });

        }

        Map<String, Object> transElement = new HashMap<>();
        transElement.put(TXConstants.CMD_PARAM, paramMap.get(TXConstants.CMD_PARAM));
        transElement.put(TXConstants.CMD_KEY, paramMap.get(TXConstants.CMD_KEY));
        sendTXIMMessage(transElement, getmRecord().getRoomId(), "");
    }

    @Override
    public void notifyShuntUpByAdmin(Map<String, String> paramMap)
    {
        Map<String, Object> transElement = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("roomId", paramMap.get("currentUserAvRoomId"));
        map.put("nickName", paramMap.get("toCheckUserNicName"));
        map.put("txId", paramMap.get("toCheckUserIdT"));
        transElement.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_ADMIN_SHUTUP_ON);
        transElement.put(TXConstants.CMD_PARAM, new Gson().toJson(map));
        sendTXIMMessage(transElement, "TXConstants.AVIMCMD_ADMIN_SHUTUP_ON");
    }

    @Override
    public void notifyShuntUpByHost(Map<String, String> paramMap)
    {
        Map<String, Object> transElement = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("roomId", paramMap.get("currentUserAvRoomId"));
        map.put("nickName", paramMap.get("toCheckUserNicName"));
        map.put("txId", paramMap.get("toCheckUserIdT"));
        transElement.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_Host_ShutUp_On);
        transElement.put(TXConstants.CMD_PARAM, new Gson().toJson(map));
        sendTXIMMessage(transElement, "TXConstants.AVIMCMD_Host_ShutUp_On");
    }

    @Override
    public void notifyShuntUpOffByHost(Map<String, String> paramMap)
    {
        Map<String, Object> transElement = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("roomId", paramMap.get("currentUserAvRoomId"));
        map.put("nickName", paramMap.get("toCheckUserNicName"));
        map.put("txId", paramMap.get("toCheckUserIdT"));
        transElement.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_Host_ShutUp_Off);
        transElement.put(TXConstants.CMD_PARAM, new Gson().toJson(map));
        sendTXIMMessage(transElement, "TXConstants.AVIMCMD_Host_ShutUp_Off");
    }

    @Override
    public void notifyShuntUpOffByAdmin(Map<String, String> paramMap)
    {
        Map<String, Object> transElement = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("roomId", paramMap.get("currentUserAvRoomId"));
        map.put("nickName", paramMap.get("toCheckUserNicName"));
        map.put("txId", paramMap.get("toCheckUserIdT"));
        transElement.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_ADMIN_SHUTUP_OFF);
        transElement.put(TXConstants.CMD_PARAM, new Gson().toJson(map));
        sendTXIMMessage(transElement, "TXConstants.AVIMCMD_ADMIN_SHUTUP_OFF");
    }

    /**
     * 遍历房管 设置房管
     *
     * @param adminLst 房管列表
     * @param txId     腾讯ID
     */
    public void ergodicRoomAdmin(List<ChosenAdminBean> adminLst, final String txId, String roomId)
    {
        for (ChosenAdminBean bean : adminLst)
        {
            // 房管列表中包含此观众 设置此观众为房管
            if (("u" + bean.getId()).equals(txId))
            {
                TIMGroupManager.getInstance().modifyGroupMemberInfoSetRole(roomId,
                        txId, TIMGroupMemberRoleType.Admin, new TIMCallBack()
                        {
                            @Override
                            public void onError(int i, String s)
                            {
                                Logger.t(TAG).d("房管设置onError" + "modifyGroupMemberInfoSetRole " +
                                        "failed: " + i + " desc" + s);
                            }

                            @Override
                            public void onSuccess()
                            {
                                Logger.t(TAG).d("进入房间设置房管onSuccess--> " + txId + " , " + txId
                                        .substring(1, txId.length()));
                            }
                        });
                break;
            }
        }
    }


}
