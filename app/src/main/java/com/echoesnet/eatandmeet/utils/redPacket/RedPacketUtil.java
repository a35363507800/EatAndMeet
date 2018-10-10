package com.echoesnet.eatandmeet.utils.redPacket;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CRPsendRedPacketAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseUserUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.chat.ChatMessageList;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

/**
 * 红包帮助类
 */
public class RedPacketUtil
{
    private final static String TAG = RedPacketUtil.class.getSimpleName();

    /**
     * 进入发红包页面
     *
     * @param fragment
     * @param chatType
     * @param toEaseUser
     * @param requestCode
     */
    public static void startRedPacketActivityForResult(Fragment fragment, int chatType, EaseUser toEaseUser, int requestCode)
    {
        //发送者头像url
        String fromAvatarUrl = "";
        //发送者昵称 设置了昵称就传昵称 否则传id
        String fromNickname = "";
        EaseUser easeUser = EaseUserUtils.getCurrentUserInfo(null);
        if (easeUser != null)
        {
            fromAvatarUrl = TextUtils.isEmpty(easeUser.getAvatar()) ? "none" : easeUser.getAvatar();
            fromNickname = TextUtils.isEmpty(easeUser.getNickName()) ? easeUser.getUsername() : easeUser.getNickName();
        }
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.fromAvatarUrl = fromAvatarUrl;
        redPacketInfo.fromNickName = fromNickname;
        redPacketInfo.toUserId = toEaseUser.getUsername();
        redPacketInfo.toUserUid = toEaseUser.getuId();
        redPacketInfo.toNickName = toEaseUser.getNickName();
        redPacketInfo.toRemark = toEaseUser.getRemark();
        //接收者Id或者接收的群Id
        if (chatType == Constant.CHATTYPE_SINGLE)
        {
            redPacketInfo.toUserId = toEaseUser.getUsername();
            redPacketInfo.chatType = 1;
        }
        else if (chatType == Constant.CHATTYPE_GROUP)
        {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toEaseUser.getUsername());
            redPacketInfo.toGroupId = group.getGroupId();
            redPacketInfo.groupMemberCount = group.getMemberCount();
            redPacketInfo.chatType = 2;
        }
        Intent intent = new Intent(fragment.getActivity(), CRPsendRedPacketAct.class);
        intent.putExtra(RedPacketConstant.EXTRA_MONEY_INFO, redPacketInfo);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 创建一条红包消息
     *
     * @param context        上下文
     * @param data           intent
     * @param toChatUsername 消息接收者id
     * @return
     */
    public static EMMessage createRPMessage(Context context, Intent data, String toChatUsername)
    {
        //发送金额
        String moneyAmount = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_MONEY_AMOUNT);
        //祝福语
        String greetings = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_GREETING);
        //红包Id
        String moneyId = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_ID);
        //发送人Uid
        String fromUserUid = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_SENDER_UID);
        String fromUserNicName = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_FROM_NICNAME);
//        String fromUserRemark = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_FROM_REMARK);
        String fromUserUrl = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_FROM_URL);

        EMMessage message = EMMessage.createTxtSendMessage("[" + "红包" + "]" + (greetings == null ? "恭喜发财" : greetings), toChatUsername);
//        EMMessage message = EMMessage.createTxtSendMessage("[" + fromUserRemark + "]" + (greetings == null ? "给你发来一个红包" : greetings), toChatUsername);
        message.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, true);
        message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_GREETING, greetings);
        message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, moneyId);
        message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_MONEY_AMOUNT, moneyAmount);
        message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_FROM_NICNAME, fromUserNicName);
//        message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_FROM_REMARK, fromUserRemark);
        message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_UID, fromUserUid);
        message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_FROM_URL, fromUserUrl);
        //标示红包是否被领取
        message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_IS_OPENED, false);
        return message;
    }

    /**
     * 拆红包的方法
     *
     * @param activity    FragmentActivity
     * @param chatType    聊天类型
     * @param message     EMMessage
     * @param toEaseUser  消息接收者
     * @param messageList
     * @param isSayHello  是否为打招呼消息
     * @return
     */
    public static void openRedPacket(final FragmentActivity activity, final int chatType, final EMMessage message,
                                     final EaseUser toEaseUser, final ChatMessageList messageList, boolean isSayHello,
                                     final String remark)
    {
        String moneyId = "";
        String amount = "0";//金额
        String messageDirect;//消息方向
        String toAvatarUrl = "none";//接收者头像url 默认值为none
        String toNickname = EMClient.getInstance().getCurrentUser();//接收者昵称 默认值为当前用户ID
        String fromUserUid = null;
        String fromUserNicName = "";
        String fromAvatarUrl = "";
        try
        {
            moneyId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID);
            amount = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_MONEY_AMOUNT);
            fromUserUid = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_UID);
            fromUserNicName = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_FROM_NICNAME);
            fromAvatarUrl = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE);
        } catch (HyphenateException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
        if (message.direct() == EMMessage.Direct.SEND)
        {
            messageDirect = RedPacketConstant.MESSAGE_DIRECT_SEND;
        }
        else
        {
            messageDirect = RedPacketConstant.MESSAGE_DIRECT_RECEIVE;
        }
        //自己
        EaseUser easeUser = EaseUserUtils.getCurrentUserInfo(null);
        if (easeUser != null)
        {
            toAvatarUrl = TextUtils.isEmpty(easeUser.getAvatar()) ? "none" : easeUser.getAvatar();
            toNickname = TextUtils.isEmpty(easeUser.getNickName()) ? easeUser.getUsername() : easeUser.getNickName();
        }
        /*//自己的红包不能领
        if (fromUserUid.equals(easeUser.getuId()))
        {
            ToastUtils.showShort(activity,"自己不能领取自己的红包");
            return;
        }*/
        if (message.direct() == EMMessage.Direct.SEND)
        {
            ToastUtils.showShort("不能领取自己的红包");
            return;
        }

        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.moneyID = moneyId;
        redPacketInfo.toAvatarUrl = toAvatarUrl;
        redPacketInfo.toNickName = toNickname;
        redPacketInfo.toRemark = toEaseUser.getRemark();
        redPacketInfo.toUserId = toEaseUser.getUsername();
        redPacketInfo.fromUserUid = fromUserUid;
        redPacketInfo.fromNickName = fromUserNicName;
        redPacketInfo.fromAvatarUrl = fromAvatarUrl;
        redPacketInfo.moneyMsgDirect = messageDirect;
        redPacketInfo.chatType = chatType;
        redPacketInfo.moneyAmount = amount;

        Logger.t("=======================").d(redPacketInfo.toString());
        checkRedPacketState(redPacketInfo, activity, message, messageList, isSayHello, remark);
    }

    /**
     * 打开红包，修改余额
     *
     * @param mContext
     */
    private static void openRedPacket2(final RedPacketInfo redPacketInfo, final Activity mContext,
                                       final ChatMessageList messageList, final EMMessage message,
                                       final Dialog redDialog, final boolean isSayHello, final String remark)
    {
        final Dialog pDialog = DialogUtil.getCommonDialog(mContext, "正在打开红包...");
        pDialog.setCancelable(false);
        mContext.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (!pDialog.isShowing())
                    pDialog.show();
            }
        });
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.streamId, redPacketInfo.moneyID);
        reqParamMap.put(ConstCodeTable.lUId, redPacketInfo.fromUserUid);
        try
        {
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    Logger.t(TAG).d("获得的结果：" + response);
                    CustomAlertDialog dialog = new CustomAlertDialog(mContext);
                    dialog.builder()
                            .setMsg("已成功领取红包")
                            .setNegativeButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (redDialog != null && redDialog.isShowing())
                                        redDialog.dismiss();
                                }
                            })
                            .show();
                    dialog.setOnDismissListener((dialogInterface) ->
                    {
                        if (redDialog != null && redDialog.isShowing())
                            redDialog.dismiss();
                    });
                    //将红包设为领取
                    message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_IS_OPENED, true);
                    //领取红包成功 发送消息到聊天窗口
                    String receiverId = EMClient.getInstance().getCurrentUser();
                    //设置默认值为id
                    String receiverNickname = receiverId;
                    EaseUser receiverUser = EaseUserUtils.getCurrentUserInfo(null);
                    if (receiverUser != null)
                    {
                        receiverNickname = TextUtils.isEmpty(receiverUser.getNickName()) ? receiverUser.getUsername() : receiverUser.getNickName();
                    }
                    if (redPacketInfo.chatType == Constant.CHATTYPE_SINGLE)
                    {
                        //刷新一下页面
                        getRelationShip(mContext, messageList, redPacketInfo, receiverNickname);
                    }
                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                }
            }, NetInterfaceConstant.UserC_getRed, reqParamMap);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void getRelationShip(Context mContext, ChatMessageList messageList, RedPacketInfo redPacketInfo, String receiverNickname)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.lUId, redPacketInfo.fromUserUid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    String isFocus = jsonObject.getString("focus");
                    String inBlack = jsonObject.getString("inBlack");
                    String isSayHello = jsonObject.getString("isSayHello");
                    String remark = jsonObject.getString("remark");
                    boolean isAppendMsg = false;
                    if ("1".equals(inBlack) || "3".equals(inBlack)) //对方把我拉黑  或者 双方互相拉黑
                    {
                        isAppendMsg = true;
                    }
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(redPacketInfo.toUserId, EMConversation.EMConversationType.Chat, true);
                    EMMessage message = sendMoneyAckMessage(mContext, redPacketInfo, receiverNickname, false, remark, isAppendMsg);
                    if (isAppendMsg)
                    {
                        conversation.appendMessage(message);
                    }
                    else
                        EMClient.getInstance().chatManager().sendMessage(message);
                    if (!"1".equals(isSayHello))
                    {
                        EMMessage lastMsgOther = conversation.getLatestMessageFromOthers();
                        if (lastMsgOther != null)
                        {
                            lastMsgOther.setAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false);
                            conversation.updateMessage(lastMsgOther);
                        }
                    }
                    messageList.refreshLast(message);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.UserC_usersRelationship, reqParamMap);
    }

    private static EMMessage sendMoneyAckMessage(Context mContext, RedPacketInfo redPacketInfo, String receiverNickname, boolean isSayHello, String remark, boolean isAppendMsg)
    {
        EMMessage msg = EMMessage.createTxtSendMessage(String.format("%1$s领取了你的红包", receiverNickname), redPacketInfo.toUserId);
        msg.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, true);
        msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, receiverNickname);
        msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, redPacketInfo.fromNickName);
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_UID, SharePreUtils.getUId(mContext));
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_ID, SharePreUtils.getId(mContext));
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_DEVICE_TYPE, EamConstant.DEVICE_TYPE);
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, SharePreUtils.getNicName(mContext));
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE, SharePreUtils.getHeadImg(mContext));
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_LEVEL, SharePreUtils.getLevel(mContext));
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_GENDER, SharePreUtils.getSex(mContext));
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_AGE, SharePreUtils.getAge(mContext));
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_VUSER, SharePreUtils.getIsVUser(mContext));
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
        msg.setAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, isSayHello);
        return msg;
    }


    /**
     * 使用cmd消息发送领到红包之后的回执消息
     */
    private static void sendRedPacketAckMessage(final EMMessage message, final String senderId, final String senderNickname,
                                                String receiverId, final String receiverNickname, final EMCallBack callBack)
    {
        //创建透传消息
        final EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMsg.setChatType(EMMessage.ChatType.GroupChat);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setTo(message.getTo());
        //设置扩展属性
        cmdMsg.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, true);
        cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, senderNickname);
        cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, receiverNickname);
        cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID, senderId);
        cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID, receiverId);
        cmdMsg.setMessageStatusCallback(new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                //保存消息到本地
                EMMessage sendMessage = EMMessage.createTxtSendMessage("content", message.getTo());
                sendMessage.setChatType(EMMessage.ChatType.GroupChat);
                sendMessage.setFrom(message.getFrom());
                sendMessage.setTo(message.getTo());
                sendMessage.setMsgId(UUID.randomUUID().toString());
                sendMessage.setMsgTime(cmdMsg.getMsgTime());
                sendMessage.setUnread(false);//去掉未读的显示
                sendMessage.setDirection(EMMessage.Direct.SEND);
                sendMessage.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, true);
                sendMessage.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, senderNickname);
                sendMessage.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, receiverNickname);
                sendMessage.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID, senderId);
                EMClient.getInstance().chatManager().saveMessage(sendMessage);
                callBack.onSuccess();
            }

            @Override
            public void onError(int i, String s)
            {

            }

            @Override
            public void onProgress(int i, String s)
            {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }

    /**
     * 使用cmd消息收取领到红包之后的回执消息
     */
    public static void receiveRedPacketAckMessage(EMMessage message)
    {
        String senderNickname = "";
        String receiverNickname = "";
        String senderId = "";
        String receiverId = "";
        try
        {
            senderNickname = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME);
            receiverNickname = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME);
            senderId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID);
            receiverId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID);
        } catch (HyphenateException e)
        {
            e.printStackTrace();
        }
        String currentUser = EMClient.getInstance().getCurrentUser();
        //更新UI为 xx领取了你的红包
        if (currentUser.equals(senderId) && !receiverId.equals(senderId))
        {
            //如果不是自己领取的红包更新此类消息UI
            EMMessage msg = EMMessage.createTxtSendMessage("content", message.getTo());
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(message.getFrom());
            msg.setTo(message.getTo());
            msg.setMsgId(UUID.randomUUID().toString());
            msg.setMsgTime(message.getMsgTime());
            msg.setDirection(EMMessage.Direct.RECEIVE);
            msg.setUnread(false);//去掉未读的显示
            msg.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, true);
            msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, senderNickname);
            msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, receiverNickname);
            msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID, senderId);
            //保存消息
            EMClient.getInstance().chatManager().saveMessage(msg);
        }
    }

    /**
     * 显示红包领取弹出框
     *
     * @param redPacketInfo
     * @param mContext
     * @param message
     * @param messageList
     */
    private static void showRedPacketDialog(final RedPacketInfo redPacketInfo, final Activity mContext,
                                            final EMMessage message, final ChatMessageList messageList,
                                            final boolean isSayHello, final String remark)
    {
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_red_packet, null);
        dialog.setContentView(contentView);
        TextView tvFromUser = (TextView) contentView.findViewById(R.id.tv_r_from_user);
        TextView tvPacketAmount = (TextView) contentView.findViewById(R.id.tv_r_amount);
        Button btnOpenPacket = (Button) contentView.findViewById(R.id.btn_open_packet);
        LevelHeaderView ivheadimage = (LevelHeaderView) contentView.findViewById(R.id.pack_headimage);

        ivheadimage.setHeadImageByUrl(redPacketInfo.fromAvatarUrl);
        ivheadimage.showRightIcon(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_VUSER, "0"));
        tvFromUser.setText(TextUtils.isEmpty(redPacketInfo.toRemark) ? redPacketInfo.fromNickName : redPacketInfo.toRemark);
        tvPacketAmount.setText(String.format("￥ %s", CommonUtils.keep2Decimal(Double.parseDouble(redPacketInfo.moneyAmount))));
        btnOpenPacket.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //修改用户余额
//                openRedPacket(redPacketInfo,mContext,messageList,message,dialog);
                openRedPacket2(redPacketInfo, mContext, messageList, message, dialog, isSayHello, remark);
            }
        });
        dialog.show();
    }

    /**
     * 查看红包状态
     *
     * @param redPacketInfo
     * @param mContext
     * @param message
     * @param messageList
     */
    private static void checkRedPacketState(final RedPacketInfo redPacketInfo, final Activity mContext,
                                            final EMMessage message, final ChatMessageList messageList,
                                            final boolean isSayHello, final String remark)
    {
//        final Dialog pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
//        pDialog.setCancelable(false);
//        mContext.runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                if (!pDialog.isShowing())
//                    pDialog.show();
//            }
//        });
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.streamId, redPacketInfo.moneyID);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject body = new JSONObject(response);
                    String rpState = body.getString("status");
                    switch (rpState)
                    {
                        //待接收
                        case "0":
                            showRedPacketDialog(redPacketInfo, mContext, message, messageList, isSayHello, remark);
                            break;
                        //已经领取
                        case "1":
                            ToastUtils.showShort("您已领取过此红包，不能重复领取");
                            break;
                        //过期
                        case "2":
                            ToastUtils.showShort("此红包已经过期退回，不能领取");
                            break;
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.UserC_checkRed, reqParamMap);
    }
}
