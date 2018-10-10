package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.models.bean.ConversationBean;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseCommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseUserUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketConstant;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/7/28 10:24
 * @description
 */

public class ManagerConversion
{
    private static final String TAG = ManagerConversion.class.getSimpleName();

    private ManagerConversion()
    {
    }

    public static ConversationBean emcon2Conbean(EMConversation conversation)
    {
        Logger.t(TAG).d("conversation1 id:" + conversation.conversationId() + "unreadcount:" + conversation.getUnreadMsgCount());
        ConversationBean bean = new ConversationBean();
        String username = conversation.conversationId();
        if (conversation.getAllMsgCount() != 0)
        {
            EMMessage lastMsgOther = conversation.getLatestMessageFromOthers();
            EMMessage lastMsg = conversation.getLastMessage();
            EaseUser user = null;
            if (lastMsgOther != null)
            {
                user = EaseUserUtils.getUserInfo(lastMsgOther);
                //获取本地用户信息
                EaseUser localUserInfo = HuanXinIMHelper.getInstance().getUserInfo(username);
                if (!TextUtils.isEmpty(localUserInfo.getuId()) || !TextUtils.isEmpty(localUserInfo.getNickName()) ||
                        !TextUtils.isEmpty(localUserInfo.getId()))//表示本地保存了此人信息
                {
                    //拿消息获取的信息和本地的信息对比，以本地信息为基准   ---yqh
//                    if (!user.getRemark().equals(localUserInfo.getRemark()))
//                    {
                    user = localUserInfo;
//                    }
                }

                // TODO: 2017/8/21 兼容线上ios
                //------------------------------------兼容ios线上问题 start----------------------------------------
                if (TextUtils.isEmpty(user.getId()))
                {
                    user = HuanXinIMHelper.getInstance().getUserInfo(username);
                }
                //------------------------------------兼容ios线上问题 end----------------------------------------
                bean.setHelloMsg(lastMsgOther.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false));
            }
            else
            {
                user = HuanXinIMHelper.getInstance().getUserInfo(username);
                Logger.t(TAG).d("保存的user》" + user.toString());
                bean.setHelloMsg(false);
            }
            bean.setHxId(user.getUsername());
            bean.setuId(user.getuId());
            bean.setId(user.getId());
            bean.setAge(user.getAge());
            bean.setGender(user.getSex());
            bean.setNickName(user.getNickName());
            bean.setLevel(user.getLevel());
            bean.setHeadImage(user.getAvatar());
            bean.setRemark(user.getRemark());
            bean.setIsVUser(user.getIsVuser());
            bean.setConversationId(username);
            bean.setTime(lastMsg.getMsgTime());
            int unReadCount = 0;
            for (EMMessage message : conversation.getAllMessages())
            {
                if (message.isUnread())
                {
                    unReadCount++;
                }
            }
//            if (unReadCount < conversation.getUnreadMsgCount())
//                unReadCount = conversation.getUnreadMsgCount();
            bean.setUnreadMsgNumber(unReadCount);
            bean.setType(conversation.getType());
            bean.setMsgState(lastMsg.direct() == EMMessage.Direct.SEND
                    && lastMsg.status() == EMMessage.Status.FAIL);
            String content = EaseCommonUtils.getMessageDigest(lastMsg, null);
            if (lastMsg.direct() == EMMessage.Direct.SEND)
            {
                String sensitiveContent = lastMsg.getStringAttribute(Constant.MESSAGE_ATTR_SENSITIVE_CONTENT, "");
                if (!TextUtils.isEmpty(sensitiveContent))//发送方 带有敏感词原文
                {
                    content = sensitiveContent;
                }
            }
            if (!TextUtils.isEmpty(content))
            {
                String fromName = EaseUserUtils.getUserInfo(lastMsg).getNickName();
                Logger.t(TAG).d("content:" + content + " | fromName:" + fromName);
                // 设置内容
                if (content.equals(fromName + "通过了你的好友请求，现在可以发起聊天了") && lastMsg.direct() == EMMessage.Direct.SEND)
                {
                    String nickName = EaseUserUtils.getUserInfo(lastMsg).getNickName();
                    if (!TextUtils.isEmpty(nickName))
                        content = "我通过了" + nickName + "的好友请求，现在可以发起聊天了";
                }
                //红包情况
                if (content.contains(fromName + "领取了你的红包") && lastMsg.direct() == EMMessage.Direct.SEND)
                {
                    EaseUser eUser = EaseUserUtils.getUserInfo(lastMsgOther);
                    String nickName;
                    if (TextUtils.isEmpty(eUser.getRemark()))
                        nickName = eUser.getNickName();
                    else
                        nickName = eUser.getRemark();
                    if (!TextUtils.isEmpty(nickName))
                        content = "你领取了[" + nickName + "]的红包";
                }
                if (content.contains(fromName + "领取了你的红包") && lastMsg.direct() == EMMessage.Direct.RECEIVE)
                {
                    String nickName = EaseUserUtils.getUserInfo(lastMsgOther).getNickName();
                    if (!TextUtils.isEmpty(user.getRemark()))
                        fromName = user.getRemark();
                    if (!TextUtils.isEmpty(nickName))
                        content = "["+fromName + "]领取了你的红包";
                }

                if(lastMsg.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE,false))
                {
                    if (lastMsg.direct() == EMMessage.Direct.SEND)
                    {
                        if (!TextUtils.isEmpty(user.getRemark()))
                            fromName = user.getRemark();
                        else fromName = user.getNickName();
                        content = "你给[" + fromName + "]发了一个红包";
                    }
                    else
                    {
                        if (!TextUtils.isEmpty(user.getRemark()))
                            fromName = user.getRemark();
                        content = "[" + fromName + "] 给你发来一个红包";
                    }
                }


                if (content.contains("[红包]") && lastMsg.direct() == EMMessage.Direct.SEND)
                {
                    if (!TextUtils.isEmpty(user.getRemark()))
                        fromName = user.getRemark();
                    else fromName = user.getNickName();
                    content = "你给[" + fromName + "]发了一个红包";
                }
                if (content.contains("[红包]") && lastMsg.direct() == EMMessage.Direct.RECEIVE)
                {
                    if (!TextUtils.isEmpty(user.getRemark()))
                        fromName = user.getRemark();
                    content = "[" + fromName + "] 给你发来一个红包";
                }
            }

            if (lastMsg.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_RECALL_MESSAGE, false))
            {
                if (lastMsg.direct() == EMMessage.Direct.SEND)
                    content = "你撤回了一条消息";
                else
                    content = "对方撤回了一条消息";
            }
            bean.setLastMsg(content);
        }
        return bean;
    }
}
