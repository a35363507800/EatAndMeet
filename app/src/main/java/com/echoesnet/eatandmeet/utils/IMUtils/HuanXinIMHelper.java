package com.echoesnet.eatandmeet.utils.IMUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.BuildConfig;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CChatActivity;
import com.echoesnet.eatandmeet.activities.GameAct;
import com.echoesnet.eatandmeet.activities.LoginModeAct;
import com.echoesnet.eatandmeet.activities.RedPacketShowAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.EaseUI;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.daos.EmojiconBigExpressionData;
import com.echoesnet.eatandmeet.daos.db.InviteMessage;
import com.echoesnet.eatandmeet.daos.db.InviteMessgeDao;
import com.echoesnet.eatandmeet.daos.db.UserDao;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.bean.UsersBean;
import com.echoesnet.eatandmeet.models.datamodel.AbstractEMMessageListener;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.ChatCommonUtils;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketConstant;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessage.Status;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.jakewharton.rxbinding2.view.RxView;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class HuanXinIMHelper
{
    protected static final String TAG = HuanXinIMHelper.class.getSimpleName();

    private EaseUI easeUI;
    protected EMMessageListener messageListener = null;
    private Map<String, EaseUser> contactList = new ArrayMap<>();
    private static HuanXinIMHelper instance = null;
    private HuanXinIMModel imModel = null;
    public boolean isVoiceCalling;
    public boolean isVideoCalling;
    private String username;
    private Context appContext;
    private EMConnectionListener connectionListener;

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    private LocalBroadcastManager broadcastManager;
    private boolean isGroupAndContactListenerRegisted = false;
    private List<String> messageIds;

    private List<String> bookOrderUsers = new ArrayList<>();
    private List<String> redList = new ArrayList<>();
    //  start  点击推送时 进入聊天页面会重新进入(走onCreate)， so 在此记录需要的信息
    public long inBackRecMsgTime;
    public EMMessage inBackRecMsg;
    //  end

    public synchronized static HuanXinIMHelper getInstance()
    {
        if (instance == null)
        {
            instance = new HuanXinIMHelper();
        }
        return instance;
    }

    /**
     * init helper
     * 初始化帮助类
     *
     * @param context application context
     */
    public void init(Context context)
    {
        imModel = new HuanXinIMModel(context);
        EMOptions options = initChatOptions();
        //use default options if options is null
        if (EaseUI.getInstance().init(context, options))
        {
            appContext = context;
            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(false);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
            //initialize preference manager
            PreferenceManager.init(context);
            //设置全局监听
            setGlobalListeners(context);
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
            initDbDao();
        }
    }

    private EMOptions initChatOptions()
    {
        Logger.t(TAG).d("初始化环信options");
        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);
        //you need apply & set your own id if you want to use google cloud messaging.
        //options.setGCMNumber("324169311137");
        //you need apply & set your own id if you want to use Mi push notification
        //options.setMipushConfig("2882303761517426801", "5381742660801");
        //you need apply & set your own id if you want to use Huawei push notification
        //options.setHuaweiPushAppId("10492024");

        options.allowChatroomOwnerLeave(true);
        options.setDeleteMessagesAsExitGroup(true);
        options.setAutoAcceptGroupInvitation(true);
        // 设置小米推送 appID 和 appKey
        options.setMipushConfig(BuildConfig.miPushAppID, BuildConfig.miPushAppKey);

        return options;
    }

    /**
     * 设置环信提供者
     */
    protected void setEaseUIProviders()
    {
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider()
        {
            @Override
            public boolean isSpeakerOpened()
            {
                Logger.t(TAG).d("isSpeak" + imModel.getSettingMsgSpeaker());
                return imModel.getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message)
            {
                return !CommonUtils.isInLiveRoom && SharePreUtils.getIsVibrate(appContext);
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message)
            {
                return SharePreUtils.getIsSound(appContext);
//                return false;
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message)
            {
                if (message == null)
                {
                    Logger.t(TAG).d("isMsgNotify1" + imModel.getSettingMsgNotification());
                    return imModel.getSettingMsgNotification();
                }
                if (!imModel.getSettingMsgNotification())
                {
                    Logger.t(TAG).d("isMsgNotify2" + imModel.getSettingMsgNotification());
                    return false;
                }
                else
                {
                    Logger.t(TAG).d("isMsgNotify3" + imModel.getSettingMsgNotification());
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // get user or group id which was blocked to show message notifications
                    if (message.getChatType() == ChatType.Chat)
                    {
                        chatUsename = message.getFrom();
                        notNotifyIds = imModel.getDisabledIds();
                        Logger.t(TAG).d("notNotifyIds" + notNotifyIds);
                    }
                    else
                    {
                        chatUsename = message.getTo();
                        notNotifyIds = imModel.getDisabledGroups();
                    }

                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename))
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        });
        //set emoji icon provider
        easeUI.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider()
        {
            @Override
            public EmojiIcon getEmojiconInfo(String emojiconIdentityCode)
            {
                List<EmojiIcon> EmojDataLst = EmojiconBigExpressionData.allEmojData(appContext);
                for (EmojiIcon emojicon : EmojDataLst)
                {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode))
                    {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping()
            {
                return null;
            }
        });

        //set notification options, will use default if you don't set it
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider()
        {
            @Override
            public String getTitle(EMMessage message)
            {
                //you can update title here
                return "看脸吃饭";
            }

            @Override
            public int getSmallIcon(EMMessage message)
            {
                //you can update icon here
                return R.mipmap.ic_launcher;
            }

            @Override
            public String getDisplayedText(EMMessage message)
            {
                EaseUser user = EaseUserUtils.getUserInfo(message);
                // be used on notification bar, different text according the message type.
                String ticker = ChatCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == Type.TXT)
                {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                Logger.t(TAG).d("message====" + message.toString());
                if (user != null)
                {
                    Logger.t(TAG).d("user====" + user.toString());
                    if (EaseAtMessageHelper.get().isAtMeMsg(message))
                    {
                        return String.format(appContext.getString(R.string.at_your_in_group), user.getNickName());
                    }
                    //如果昵称是环信ID,则从message属性中取nickname
                    if (message.getFrom().equals(user.getNickName()))
                    {
                        String nickName = message.getStringAttribute("nickName", "无名");
                        return nickName + ": " + ticker;
                    }
                    return user.getNickName() + ": " + ticker;
                }
                else
                {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message))
                    {
                        return String.format(appContext.getString(R.string.at_your_in_group), message.getFrom());
                    }
                    String nickName = null;
                    try
                    {
                        nickName = message.getStringAttribute("nickName");
                    } catch (HyphenateException e)
                    {
                        e.printStackTrace();
                    }
                    Logger.t(TAG).d("nickName=====" + nickName);
                    return nickName + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum)
            {
                // here you can customize the text.
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                return null;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message)
            {
/*                if (TextUtils.isEmpty(EaseUserUtils.getUserInfo(message.getFrom()).getuId()))
                {
                    return new Intent();
                }*/

                // you can set what activity you want display when user click the notification
                Intent intent = new Intent(appContext, CChatActivity.class);
                Logger.t(TAG).d("点击跳转");
                // open calling activity if there is call
                if (isVideoCalling)
                {
                    //intent = new Intent(appContext, VideoCallActivity.class);
                }
                else if (isVoiceCalling)
                {
                    //intent = new Intent(appContext, VoiceCallActivity.class);
                }
                else
                {
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat)
                    { // single chat message
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                    }
                    else
                    {
                        // group chat message
                        // message.getTo() is the group id
                        intent.putExtra("userId", message.getTo());
                        if (chatType == ChatType.GroupChat)
                        {
                            intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                        }
                        else
                        {
                            intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                        }
                    }
                }
                return intent;
            }
        });
    }

    public void addBookOrderUser(String username)
    {
        bookOrderUsers.add(username);
    }

    public boolean checkIsContainsUser(String username)
    {
        return bookOrderUsers.contains(username);
    }

    public String bookList2String()
    {
        return bookOrderUsers.toString();
    }

    /**
     * 设置全局监听
     */
    protected void setGlobalListeners(final Context context)
    {
        addHxConnectionListener(context);
        //register group and contact event listener
        registerGroupAndContactListener();
        //register message event listener
        registerMessageListener(context);
    }

    public void addHxConnectionListener(final Context context)
    {
        // 创建全局连接监听
        connectionListener = new EMConnectionListener()
        {
            @Override
            public void onDisconnected(int error)
            {
                Logger.t(TAG).d("环信失去连接HuanXinIMHelper，错误码为》" + error);
                EamLogger.t(TAG).writeToDefaultFile("环信失去连接HuanXinIMHelper，错误码为》" + error);

                if (error == EMError.USER_REMOVED)
                {
                    Logger.t(TAG).d("进入 EMError.USER_REMOVED");
                    onCurrentAccountRemoved();
                }
                else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE)
                {
                    EamLogger.t(TAG).t("环信账号在另一台设备上登录》");
                    onConnectionConflict();
                }
            }

            @Override
            public void onConnected()
            {
                Logger.t(TAG).d("环信建立连接》");
                EamLogger.t(TAG).t("环信建立连接》");
                //asyncFetchContactsFromServer(context, null);
            }
        };
        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);
    }

    private void initDbDao()
    {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userDao = new UserDao(appContext);
    }

    /**
     * register group and contact listener, you need register when login
     */
    public void registerGroupAndContactListener()
    {
        if (!isGroupAndContactListenerRegisted)
        {
            EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
            //是一个list，应该叫addContactListener,都会触发
            EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
            isGroupAndContactListenerRegisted = true;
        }
    }

    /**
     * 群相关的改变监听
     */
    class MyGroupChangeListener implements EMGroupChangeListener
    {
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason)
        {
            new InviteMessgeDao(appContext).deleteMessage(groupId);

            // user invite you to join group
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setGroupInviter(inviter);
            Logger.t(TAG).d("receive invitation to join the group：" + groupName);
            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason)
        {
            new InviteMessgeDao(appContext).deleteMessage(groupId);

            //user accept your invitation
            boolean hasGroup = false;
            EMGroup _group = null;
            for (EMGroup group : EMClient.getInstance().groupManager().getAllGroups())
            {
                if (group.getGroupId().equals(groupId))
                {
                    hasGroup = true;
                    _group = group;
                    break;
                }
            }
            if (!hasGroup)
                return;

            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(_group == null ? groupId : _group.getGroupName());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            Logger.t(TAG).d(invitee + "Accept to join the group：" + _group == null ? groupId : _group.getGroupName());
            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_ACCEPTED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason)
        {
            new InviteMessgeDao(appContext).deleteMessage(groupId);
            //user declined your invitation
            boolean hasGroup = false;
            EMGroup group = null;
            for (EMGroup _group : EMClient.getInstance().groupManager().getAllGroups())
            {
                if (_group.getGroupId().equals(groupId))
                {
                    group = _group;
                    hasGroup = true;
                    break;
                }
            }
            if (!hasGroup)
                return;

            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(group == null ? groupId : group.getGroupName());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            Logger.t(TAG).d(invitee + "Declined to join the group：" + group == null ? groupId : group.getGroupName());
            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_DECLINED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onUserRemoved(String groupId, String groupName)
        {
            //user is removed from group
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName)
        {
            // group is dismissed,
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason)
        {

            // user apply to join group
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d(TAG, applyer + " Apply to join group：" + groupName);
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter)
        {
            String st4 = appContext.getString(R.string.Agreed_to_your_group_chat_application);
            // your application was accepted
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(accepter + " " + st4));
            msg.setStatus(Status.SUCCESS);
            // save accept message
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify the accept message
            getNotifier().vibrateAndPlayTone(msg);

            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason)
        {
            // your application was declined, we do nothing here in demo
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage)
        {
            // got an invitation
            String st3 = appContext.getString(R.string.Invite_you_to_join_a_group_chat);
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(inviter + " " + st3));
            msg.setStatus(EMMessage.Status.SUCCESS);
            // save invitation as messages
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify invitation message
            getNotifier().vibrateAndPlayTone(msg);
            EMLog.d(TAG, "onAutoAcceptInvitationFromGroup groupId:" + groupId);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire)
        {

        }

        @Override
        public void onMuteListRemoved(String groupId, List<String> mutes)
        {

        }

        @Override
        public void onAdminAdded(String groupId, String administrator)
        {

        }

        @Override
        public void onAdminRemoved(String groupId, String administrator)
        {

        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner)
        {

        }

        @Override
        public void onMemberJoined(String groupId, String member)
        {

        }

        @Override
        public void onMemberExited(String groupId, String member)
        {

        }

        @Override
        public void onAnnouncementChanged(String groupId, String announcement)
        {

        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile)
        {

        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId)
        {

        }
    }

    /***
     * 好友变化监听
     */
    public class MyContactListener implements EMContactListener
    {
        //双方成为好友的时候触发，双向的
        @Override
        public void onContactAdded(String username)
        {
            getUserinfoByHxId(appContext, username, null);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        //删除好友时候触发，双向的
        @Override
        public void onContactDeleted(String username)
        {
            Logger.t(TAG).d("删除了好友,环信id为》 " + username);

            try
            {
                Map<String, EaseUser> localUsers = HuanXinIMHelper.getInstance().getContactList();
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessgeDao.deleteMessage(username);
                broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //被申请人收到通知
        @Override
        public void onContactInvited(String username, String reason)
        {
            Logger.t(TAG).d(String.format("%s申请为好友，原因为：%s", username, reason));

            try
            {
                List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                for (InviteMessage inviteMessage : msgs)
                {
                    if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username))
                    {
                        inviteMessgeDao.deleteMessage(username);
                    }
                }
                // save invitation as message
                InviteMessage msg = new InviteMessage();
                msg.setFrom(username);
                msg.setTime(System.currentTimeMillis());
                msg.setReason(reason);
                msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
                notifyNewInviteMessage(msg);
                broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //申请人会收到通知
        @Override
        public void onFriendRequestAccepted(String username)
        {
            Logger.t(TAG).d(username + "接受了邀请");
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs)
            {
                if (inviteMessage.getFrom().equals(username))
                {
                    return;
                }
            }
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Logger.t(TAG).d(username + "accept your request");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onFriendRequestDeclined(String username)
        {
            Logger.t(TAG).d(username + "拒绝了邀请");
        }
    }

    /**
     * save and notify invitation message
     *
     * @param msg
     */
    private void notifyNewInviteMessage(InviteMessage msg)
    {
        if (inviteMessgeDao == null)
        {
            inviteMessgeDao = new InviteMessgeDao(appContext);
        }
        inviteMessgeDao.saveMessage(msg);
        //increase the unread message count
        inviteMessgeDao.saveUnreadMessageCount(1);
        /***********************由于使用我们自己的推送系统了暂时不要发出声音****************************王犇*/
        // notify there is new message
        //getNotifier().vibrateAndPlayTone(null);
    }

    /**
     * //账号被踢了
     */
    protected void onConnectionConflict()
    {
        Logger.t(TAG).d("环信检测到账号冲突》");
        confirmConflict(appContext);
    }

    /**
     * account is removed
     */
    protected void onCurrentAccountRemoved()
    {
        Intent intent = new Intent(appContext, LoginModeAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }

    /**
     * 获得用户信息非常重要--wb
     * 目前还需要这个方法支撑会话列表的功能
     *
     * @param username 环信id
     * @return
     */
    public EaseUser getUserInfo(String username)
    {
        EaseUser user = null;
        if (username.equals(EMClient.getInstance().getCurrentUser()))
            return getCurrentUser();
        user = getContactList().get(username);
        Logger.t(TAG).d("用户信息》 " + (user == null ? "本地没有保存此用户信息" : user.toString()));
        if (user == null)
            user = new EaseUser(username);
        return user;
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public EaseUser getCurrentUser()
    {
        String username = EMClient.getInstance().getCurrentUser();
        EaseUser currentUser = new EaseUser(username);
        currentUser.setNickName(SharePreUtils.getNicName(appContext));
        currentUser.setAvatar(SharePreUtils.getHeadImg(appContext));
        currentUser.setuId(SharePreUtils.getUId(appContext));
        currentUser.setLevel(SharePreUtils.getLevel(appContext) + "");
        currentUser.setAge(SharePreUtils.getAge(appContext));
        currentUser.setSex(SharePreUtils.getSex(appContext));
        Logger.t(TAG).d("获取用户自己：" + currentUser.getAvatar() + "  " +
                currentUser.getNickName() + "uId " + currentUser.getuId() + "nickname " + currentUser.getNickName());
        return currentUser;
    }

    public void getUserinfoByHxId(final Context context, final String imuId, final ISyncUserinfoListener listener)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(context);
        reqParamMap.put(ConstCodeTable.imuId, imuId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String bodyStr)
            {
                super.onNext(bodyStr);
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        EaseUser user = new EaseUser(imuId);
                        try
                        {
                            JSONObject body = new JSONObject(bodyStr);
                            user.setAvatar(body.getString("uphUrl"));
                            user.setNickName(body.getString("nicName"));
                            user.setuId(body.getString("uId"));
                            HuanXinIMHelper.getInstance().saveContact(user);//同步到本地代码
                            if (listener != null)
                                listener.getUser(user);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }, NetInterfaceConstant.UserC_imuToUser, reqParamMap);
    }

    /**
     * 把单个用户信息更新保存本地数据库
     *
     * @param usersBean
     */
    public void updateContact(UsersBean usersBean, String reMark)
    {
        Map<String, EaseUser> userMap = new HashMap<String, EaseUser>();
        EaseUser easeUser = new EaseUser(usersBean.getImuId());
        easeUser.setuId(usersBean.getuId());
        easeUser.setAvatar(usersBean.getUphUrl());
        easeUser.setLevel(usersBean.getLevel());
        easeUser.setAge(usersBean.getAge());
        if (TextUtils.isEmpty(reMark))
        {
            easeUser.setNickName(usersBean.getNicName());
        }
        else
        {
            easeUser.setNickName(reMark);
        }
        userMap.put(usersBean.getImuId(), easeUser);
        getContactList().putAll(userMap);
        UserDao dao = new UserDao(appContext);
        dao.saveContact(easeUser);
    }

    public EMMessage newGameInviteMessage;

    /**
     * 全局消息监听
     *
     * @param context
     */
    protected void registerMessageListener(final Context context)
    {

        messageListener = new AbstractEMMessageListener()
        {
            @Override
            public void onMessageReceived(List<EMMessage> messages)
            {
                if (CommonUtils.isAppKilled)//app已经双击返回退出，但是进程还在，所以双击退出时不接收消息
                    return;
                try
                {
                    if (messageIds == null)
                        messageIds = new ArrayList<>();

                    for (final EMMessage message : messages)
                    {
                        Logger.t(TAG).d("chat------>检查程序是否在前台：" + CommonUtils.isAppOnForeground(context));
                        Logger.t(TAG).d("onMessageReceived id : " + message.getMsgId() +
                                message.getStringAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, "12345")
                                + " " + message.getFrom() + " " + message.getBody() + " " + message.getTo() + " | " + message.getChatType());
                        // in background, do not refresh UI, notify it in notification bar
                        if (message.getChatType() == ChatType.Chat)
                        {
                            checkMessageIsNoAttribute(message);

                            Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_RED_HOME);
                            EamApplication.getInstance().sendBroadcast(intent);
                            if (!CommonUtils.isInChatRoom && !CommonUtils.isInLiveRoom)//是否在聊天界面
                            {
                                if (message.getType() == Type.TXT)
                                {
                                    //在切后台的状态下接收到游戏邀请时 记录最新一条消息去展示弹窗
                                    if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_GAME_MESSAGE, false) && CommonUtils.isSwitched2Back)
                                    {
                                        newGameInviteMessage = message;
//                                        if (gameMessageReceiveListener != null)
//                                            gameMessageReceiveListener.onGameMsgReceive(true, "", message);
//                                        showGoFightView(context, true, "", message);
//                                        Message hMessage = new Message();
//                                        Map<String, Object> map = new ArrayMap<>();
//                                        map.put("context", context);
//                                        map.put("isBeInvited", true);
//                                        map.put("matchId", "");
//                                        map.put("message", message);
//                                        hMessage.obj = map;
//                                        hMessage.what = GAME_MESSAGE_NOTIFICATION;
//                                        handler.sendMessage(hMessage);
                                    }
                                }
                            }


//                            if (!easeUI.hasForegroundActivies() && !messageIds.contains(message.getMsgId()))
                            if (!CommonUtils.isAppOnForeground(context) && !messageIds.contains(message.getMsgId()))
                            {
                                if ("1".equals(SharePreUtils.getIsShowNotify(context)))
                                {
                                    String uid = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, "");
                                    if (TextUtils.isEmpty(uid))
                                        return;
                                    messageIds.add(message.getMsgId());
                                    Observable.just(EMClient.getInstance().chatManager().getUnreadMessageCount())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<Integer>()
                                            {
                                                @Override
                                                public void accept(Integer allMsgUnReadCount) throws Exception
                                                {
                                                    getNotifier().onNewMsg(message);
                                                }
                                            });
                                }
                            }
                            Observable.just(EMClient.getInstance().chatManager().getUnreadMessageCount())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Integer>()
                                    {
                                        @Override
                                        public void accept(Integer allMsgUnReadCount) throws Exception
                                        {
                                            EamApplication.getInstance().msgCount = allMsgUnReadCount + "";
                                            EamApplication.getInstance().notifiBadgerCount();
                                        }
                                    });
                        }
                        if (messageIds.size() > 10)
                        {
                            messageIds.clear();
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("hx 消息解析错误" + e.getMessage());
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages)
            {
                if (CommonUtils.isAppKilled)//app已经双击返回退出，但是进程还在，所以双击退出时不接收消息
                    return;
                for (EMMessage message : messages)
                {
                    String action = "";
                    EMMessageBody cmdMsgBody = message.getBody();
                    Logger.t(TAG).d("接到了后台推送的红点信息" + messages.size() + "///" + messages.toString() + "///" + cmdMsgBody);
                    if (cmdMsgBody instanceof EMCmdMessageBody)
                    {
                        action = ((EMCmdMessageBody) cmdMsgBody).action();
                    }

                    switch (action)
                    {
                        case EamConstant.EAM_HX_CMD_START_UPLOAD_LOCATION:
                            try
                            {
                                Intent intent1 = new Intent(EamConstant.EAM_HX_CMD_START_UPLOAD_LOCATION);
                                intent1.putExtra("extInfo", message.getStringAttribute("ext"));
                                EamApplication.getInstance().sendBroadcast(intent1);
                            } catch (HyphenateException e)
                            {
                                e.printStackTrace();
                                Logger.t(TAG).d(e.getMessage());
                            }
                            break;
                        case EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND:

                            Map<String, String> mapParam = NetHelper.getCommonPartOfParam(EamApplication.getInstance());
                            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
                            {
                                @Override
                                public void onNext(String response)
                                {
                                    super.onNext(response);
                                    try
                                    {
                                        JSONObject body = new JSONObject(response);
                                        String invitedRemind = body.getString("invitedRemind");
                                        if (invitedRemind.equals("1"))
                                        {
                                            Intent intent2 = new Intent(EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND);
                                            EamApplication.getInstance().sendBroadcast(intent2);
                                        }
                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }, NetInterfaceConstant.AppointmentC_queryRedStatus_v420, mapParam);

                            break;
                        case EamConstant.EAM_HX_CMD_SUCC_RED_REMIND:
                            Intent Intent3 = new Intent(EamConstant.EAM_HX_CMD_SUCC_RED_REMIND);
                            EamApplication.getInstance().sendBroadcast(Intent3);
                            break;
                        case EamConstant.EAM_HX_CMD_TASK_RED_REMIND:
                            Intent taskIntent2 = new Intent(EamConstant.EAM_HX_CMD_TASK_RED_REMIND);
                            EamApplication.getInstance().sendBroadcast(taskIntent2);
                            break;
                        case EamConstant.EAM_SINGLES_DAY:
                            if (EamApplication.getInstance().isCheckRed)
                                getMyRedIncome(context);
                            break;
                        case EamConstant.EAM_CHAT_RECALL_MSG_NOTIFY:
                            String messageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_RECALL_MSG_ID, "");
                            EMMessage emMessage = EMClient.getInstance().chatManager().getMessage(messageId);
                            EMMessage recallMsg = ChatCommonUtils.markAsRecallMessage(emMessage, false);
                            EMConversation c = EMClient.getInstance().chatManager().getConversation(message.getFrom(),
                                    ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                            c.updateMessage(recallMsg);
                            break;
                        case EamConstant.EAM_CHAT_ACCEPT_GAME_NOTIFY://接受游戏邀请透传
                        {
                            String gameMessageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
                            String gameMessageState = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, "");
                            String matchId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MATCH_ID, "");
                            Logger.t(TAG).d("chat------>收到接受透传 消息id：" + gameMessageId + " | gameMessageState:" + gameMessageState + " | matchId:" + matchId);
                            EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
//                            if (gameMsg != null)
//                            {
//                                gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, gameMessageState);
//                                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
//                                conversation.updateMessage(gameMsg);
//                            }
                            if (!CommonUtils.isInChatRoom && !CommonUtils.isInLiveRoom)//  && CommonUtils.isAppOnForeground(context)   去对战窗口  只在前台时显示
                            {
                                Message hMessage = new Message();
                                Map<String, Object> map = new ArrayMap<>();
//                                map.put("context", context);
                                map.put("isBeInvited", false);
                                map.put("matchId", matchId);
                                map.put("message", message);
                                map.put("gameMessage", gameMsg);
                                hMessage.obj = map;
                                hMessage.what = GAME_MESSAGE_NOTIFICATION;
                                handler.sendMessage(hMessage);
                                if (!CommonUtils.isAppOnForeground(context))
                                {
                                    showNotification(context, matchId, false, message);
                                    isClose2Fight = false;
                                }
                            }
                            if (!CommonUtils.isAppOnForeground(context))
                            {
                                inBackRecMsgTime = System.currentTimeMillis();//通过点击推送进入APP 记录时间点
                                inBackRecMsg = message;
                            }
                            break;
                        }
                        case EamConstant.EAM_CHAT_REFUSE_GAME_NOTIFY://拒绝游戏邀请透传
                        {
                            String gameMessageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
                            String gameMessageState = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, "");
                            EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                            Logger.t(TAG).d("chat------>收到拒绝透传 消息id：" + gameMessageId + " | gameMsg:" + gameMsg + " | gameMessageState:" + gameMessageState);
                            gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, gameMessageState);
                            conversation.updateMessage(gameMsg);
                            break;
                        }

                        default:
                            break;
                    }
                    Logger.t(TAG).d(String.format("Command：action:%s,message:%s", action, message.toString()));
                }
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    private void showNotification(Context context, String matchId, boolean isBeInvited, EMMessage message)
    {
        Logger.t(TAG).d("chat------>接收到游戏邀请接受cmd，在后台，创建推送");

        Intent broadcastIntent = new Intent(context, CChatActivity.class);
//        broadcastIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        broadcastIntent.putExtra("message", "{\"code\":\"GAME_ACCEPT\"}");
        Map<String, Object> map = new HashMap<>();
        map.put("matchId", matchId);
        map.put("isBeInvited", isBeInvited);


        String gameMessageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
        String nicName = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, "");
        String remark = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, "");
        String gameId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, "");
        String gameUrl = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, "");
        String uid = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, "");

        map.put("nicName", nicName);
        map.put("remark", remark);
        map.put("gameId", gameId);
        map.put("gameUrl", gameUrl);
        map.put("uid", uid);
        map.put("gameMessageId", gameMessageId);

        broadcastIntent.putExtra("moreValues", (Serializable) map);

        EaseUser eUser = new EaseUser(message.getFrom());
        eUser.setNickName(nicName);
        eUser.setuId(uid);
        eUser.setRemark(remark);
        broadcastIntent.putExtra(Constant.EXTRA_TO_EASEUSER, eUser);

//        Intent mainIntent = new Intent(context, HomeAct.class);
//        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Intent[] intents = {mainIntent, broadcastIntent};

        PendingIntent intent = PendingIntent.getActivity(context, EamConstant.EAM_NOTIFY_GAME_ACCEPT, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String content = "您的好友同意了你的游戏邀请，请火速前往对战";
        //设置通知信息
        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle("看脸吃饭")
                .setContentText(content)
                .setContentIntent(intent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("《看脸吃饭》消息来了")
                .setWhen(System.currentTimeMillis())
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        //通知
        manager.notify(EamConstant.EAM_NOTIFY_GAME_ACCEPT, notification);
    }

    /**
     * 在聊天室切后台时 收到游戏接受邀请的 通知
     */
    public void showInChatNotification(Activity mAct, EaseUser eUser)
    {
        Logger.t(TAG).d("chat------>接收到游戏邀请接受cmd，在聊天室切后台时，创建推送");
        Intent intent = new Intent(mAct, CChatActivity.class);

        intent.putExtra(Constant.EXTRA_TO_EASEUSER, eUser);

        PendingIntent pIntent = PendingIntent.getActivity(mAct, EamConstant.EAM_NOTIFY_GAME_ACCEPT, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String content = "您的好友同意了你的游戏邀请，请火速前往对战";
        //设置通知信息
        Notification notification = new Notification.Builder(mAct)
                .setAutoCancel(true)
                .setContentTitle("看脸吃饭")
                .setContentText(content)
                .setContentIntent(pIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("《看脸吃饭》消息来了")
                .setWhen(System.currentTimeMillis())
                .build();
        NotificationManager manager = (NotificationManager) mAct.getSystemService(mAct.NOTIFICATION_SERVICE);
        //通知
        manager.notify(EamConstant.EAM_NOTIFY_GAME_ACCEPT, notification);

    }

    private OnShowGoFightListener showGoFightListener;

    public interface OnShowGoFightListener
    {
        void onShowGoFightDialog(boolean isBeInvited, String matchId, EMMessage message, EMMessage gameMessage);
    }

    public void setOnShowGoFightListener(OnShowGoFightListener listener)
    {
        showGoFightListener = listener;
    }

    private long gameInviteDialogShowTime;
    private Dialog dialog;
    public boolean isClose2Fight = false;

    /**
     * @param mAct
     * @param isBeInvited
     * @param matchId
     * @param message
     */
    public void showGameInviteDialog(Activity mAct, boolean isBeInvited, String matchId, EMMessage message, EMMessage gameMessage)
    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(EamApplication.getInstance().getApplicationContext());
//        dialog = builder.create();
        Logger.t(TAG).d("chat------>聊天中显示弹窗mAct.isFinishing():" + mAct.isFinishing());
        Logger.t(TAG).d("chat------>弹窗显示数据:" + isBeInvited + " | " + matchId + " | " + message);
        if (mAct.isFinishing())
            return;
        if (dialog == null)
            dialog = new Dialog(mAct, R.style.dialog2);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.setCanceledOnTouchOutside(false);//点击屏幕不消失  返回键 还能消失
        dialog.setCancelable(false); //返回键 也 不消失
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);

        View view = LayoutInflater.from(mAct).inflate(R.layout.dialog_chat_game, null);
        TextView tvGameTitle = view.findViewById(R.id.game_dialog_title);
        TextView tvGameNicName = view.findViewById(R.id.game_dialog_nicname);
        TextView tvGameContent = view.findViewById(R.id.game_dialog_content);
        TextView tvGameTimeout = view.findViewById(R.id.game_dialog_invite_timeout);
        LinearLayout gameAcceptGroup = view.findViewById(R.id.game_dialog_button_group);
        Button gameAccept = view.findViewById(R.id.game_dialog_btn_accept);
        Button gameRefuse = view.findViewById(R.id.game_dialog_btn_refuse);
        Button gameGoFight = view.findViewById(R.id.game_dialog_go_fight);
        IconTextView itvGameCancel = view.findViewById(R.id.game_cancel);
        tvGameTitle.setText(isBeInvited ? "游戏邀请" : "邀请结果");
        String nicName = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, "");
        String remark = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, "");
        tvGameNicName.setText(TextUtils.isEmpty(remark) ? nicName : remark);
        String content = isBeInvited ? mAct.getString(R.string.game_dialog_beinvited_msg) : mAct.getString(R.string.game_dialog_gofight_msg);
        tvGameContent.setText(content);
        tvGameTimeout.setVisibility(isBeInvited ? View.VISIBLE : View.GONE);
        gameAcceptGroup.setVisibility(isBeInvited ? View.VISIBLE : View.GONE);
        gameGoFight.setVisibility(isBeInvited ? View.GONE : View.VISIBLE);
        String luid = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, "");
        RxView.clicks(gameAccept)
                .throttleFirst(600, TimeUnit.MILLISECONDS)
                .subscribe((o) -> acceptGameInvite(mAct, luid, message));
        RxView.clicks(gameRefuse)
                .throttleFirst(600, TimeUnit.MILLISECONDS)
                .subscribe((o) -> refuseGameInvite(mAct, luid, message));

        RxView.clicks(gameGoFight)
                .throttleFirst(600, TimeUnit.MILLISECONDS)
                .subscribe((o) ->
                {
                    if (System.currentTimeMillis() - gameInviteDialogShowTime < 1000 * 15)
                    {
                        queryAnotherInvite(mAct, matchId, message);
                    }
                    else
                    {
                        ToastUtils.showShort("链接已失效！");
                        String gameMessageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
                        EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
                        if (gameMsg != null)
                        {
                            gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                            conversation.updateMessage(gameMsg);
                        }
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }
                });
        RxView.clicks(itvGameCancel)
                .throttleFirst(600, TimeUnit.MILLISECONDS)
                .subscribe((o) ->
                {
                    if (!isBeInvited)
                    {
                        if (System.currentTimeMillis() - gameInviteDialogShowTime < 1000 * 15)
                        {
                            closeInvite(mAct, matchId, message);
                        }
                        else
                        {
                            String msgId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
                            // 2017/11/29 更改自己消息状态 发送过期消息  发送 宝宝有点忙文本消息
                            EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(msgId);
                            if (gameMsg != null)
                            {
                                gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
                                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                                if (conversation != null)
                                    conversation.updateMessage(gameMsg);
                            }
                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();
                            //发送宝宝忙消息
//                    queryUsersRelationShip(mAct, message);
                        }
                    }
                    else
                    {
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }
                });
        dialog.setContentView(view);
        if (!dialog.isShowing())
        {
            dialog.show();
            gameInviteDialogShowTime = System.currentTimeMillis();
        }

    }

    /**
     * 关闭邀请弹窗
     */
    public void dismissGameInviteDialog()
    {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        else
        {
            Logger.t(TAG).d("chat------>dismissGameInviteDialog():dialog为空使用全局关闭:" + dialog);
            if (dialog != null && dialog.isShowing())
            {
                dialog.dismiss();
            }
            else
            {
                Logger.t(TAG).d("chat------>queryUsersRelationShip():全局dialog为空");
            }
        }
    }

    /**
     * 点击弹窗 关闭按钮
     *
     * @param mAct
     * @param matchId
     * @param message
     */
    private void closeInvite(Activity mAct, String matchId, EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.matchingId, matchId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                isClose2Fight = true;
                String msgId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
                // 2017/11/29 更改自己消息状态 发送过期消息  发送 宝宝有点忙文本消息
                EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(msgId);
                Logger.t(TAG).d("chat------>关闭去对战按钮点击 获取消息更改过期：" + msgId + " | message:" + gameMsg);
                if (gameMsg != null)
                {
                    gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_OVERDUE);
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                    if (conversation != null)
                        conversation.updateMessage(gameMsg);
                }

                //发送宝宝忙消息
                queryUsersRelationShip(mAct, message);

//                    map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, message.getMsgId());

                    /*Map<String, String> map = new ArrayMap<>();
                    map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_OVERDUE);
                    map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, msgId);
                    sendCMDMsg(EamConstant.EAM_CHAT_REFUSE_GAME_NOTIFY, message.getFrom(), map, new EMCallBack()
                    {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError(int i, String s)
                        {
                            Logger.t(TAG).d("发送失败：" + i + " | " + s);
                        }

                        @Override
                        public void onProgress(int i, String s)
                        {

                        }
                    });*/
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        }, NetInterfaceConstant.Merge10C_closeInvite, reqParamMap);
    }

    /**
     * 查询聊天对象关系
     *
     * @param message
     */
    public void queryUsersRelationShip(Activity mAct, EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        String luid = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, "");
        reqParamMap.put(ConstCodeTable.lUId, luid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                EMMessage txtMessage = EMMessage.createTxtSendMessage("宝宝现在有点忙，下次再战~", message.getFrom());
                makeMessageAttribute(mAct, luid, response, txtMessage);
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                else
                {
                    Logger.t(TAG).d("chat------>queryUsersRelationShip():dialog为空使用全局关闭" + dialog);
                    if (dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    else
                    {
                        Logger.t(TAG).d("chat------>queryUsersRelationShip():全局dialog为空" + dialog);
                    }

                }

            }
        }, NetInterfaceConstant.UserC_usersRelationship, reqParamMap);
    }

    /**
     * 构造消息体
     *
     * @param mAct
     * @param luid
     * @param response
     * @param txtMessage
     */
    private void makeMessageAttribute(Activity mAct, String luid, String response, EMMessage txtMessage)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            String inBlack = jsonObject.getString("inBlack");
            String isSayHello = jsonObject.getString("isSayHello");
            String remark = jsonObject.getString("remark");

            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_ID, SharePreUtils.getId(mAct));
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_UID, SharePreUtils.getUId(mAct));
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_DEVICE_TYPE, EamConstant.DEVICE_TYPE);
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, SharePreUtils.getNicName(mAct));
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE, SharePreUtils.getHeadImg(mAct));
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_LEVEL, SharePreUtils.getLevel(mAct));
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_GENDER, SharePreUtils.getSex(mAct));
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_AGE, SharePreUtils.getAge(mAct));
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_VUSER, SharePreUtils.getIsVUser(mAct));
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
            Logger.t(TAG).d("是否为打招呼：" + "1".equals(isSayHello));
            txtMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, "1".equals(isSayHello));

            if ("1".equals(inBlack))
            {
                sendMessage(txtMessage, true);
            }
            else
            {
                if ("1".equals(isSayHello))
                {
                    sendFirstTalk(mAct, luid, txtMessage);
                }
                else
                {
                    sendMessage(txtMessage, false);
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 调用首次聊天接口
     *
     * @param mAct
     * @param luId
     * @param message
     */
    public void sendFirstTalk(Activity mAct, String luId, EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("首次聊天返回参数》》" + response.toString());

                sendMessage(message, false);

            }
        }, NetInterfaceConstant.FriendC_firstTalk, null, reqParamMap);
    }

    /**
     * 发送消息
     *
     * @param message
     * @param isAppend
     */
    private void sendMessage(EMMessage message, boolean isAppend)
    {
        if (isAppend)
        {
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, true);
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getTo(), EMConversation.EMConversationType.Chat, true);
            if (conversation != null)
                conversation.appendMessage(message);
        }
        else
        {
            EMClient.getInstance().chatManager().sendMessage(message);
        }
//        if (dialog != null && dialog.isShowing())
//            dialog.dismiss();
    }

//    public void dissmissGameInviteDialog()
//    {
//        if (dialog != null && dialog.isShowing())
//            dialog.dismiss();
//    }

    /**
     * 查询未接受我邀请的其他人
     */
    public void queryAnotherInvite(Context mContext, String matchId, EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    Logger.t(TAG).d("chat------>查询未接受我邀请的其他人返回结果：" + response.getBody());
                    JSONObject jsonObject = new JSONObject(response.getBody());
                    JSONArray array = jsonObject.getJSONArray("refuseList");
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject object = new JSONObject(array.getString(i));
                        String hxId = object.getString("hxId");
                        String messageId = object.getString("messageId");
                        Map<String, String> map = new ArrayMap<>();
                        map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ONGOING);
                        map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, messageId);
                        sendCMDMsg(EamConstant.EAM_CHAT_REFUSE_GAME_NOTIFY, hxId, map);
                        //拿到消息id后 要更新 自己 与这个人 的 会话中的消息 状态 为 过期
                        EMMessage localMessage = EMClient.getInstance().chatManager().getMessage(messageId);
                        if (localMessage != null)
                        {
                            localMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_OVERDUE);
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(hxId, ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                            conversation.updateMessage(localMessage);
                        }
                    }
                    String gameMessageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
                    EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
                    if (gameMsg != null)
                    {
                        gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                        conversation.updateMessage(gameMsg);
                    }

                    String gameId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, "");
                    String gameUrl = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, "");
                    Intent intent = new Intent(mContext, GameAct.class);
                    intent.putExtra("gameId", gameId);
                    intent.putExtra("gameUrl", gameUrl);
                    intent.putExtra("gameName", mContext.getString(R.string.chat_game_name));
                    intent.putExtra("matchId", matchId);
                    Logger.t(TAG).d("chat------>点击去对战跳转数据：gameId:" + gameId + " | gameUrl:" + gameUrl + " | matchId:" + matchId);
                    mContext.startActivity(intent);
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }


            }
        }, NetInterfaceConstant.Merge10C_refuseList, null, reqParamMap);
    }

    /**
     * 接受游戏邀请
     *
     * @param mAct
     * @param lUid
     * @param message
     */
    private void acceptGameInvite(Activity mAct, String lUid, EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        reqParamMap.put(ConstCodeTable.messageId, message.getMsgId());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    Logger.t(TAG).d("chat------>弹窗接受游戏邀请返回>>" + response);
                    message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat, true);
                    //更新本地消息状态
                    conversation.updateMessage(message);

                    JSONObject object = new JSONObject(response);
                    String matchId = object.getString("matchId");
                    JSONArray array = object.getJSONArray("refuseList");
                    String remark = object.getString("remark");
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject object1 = new JSONObject(array.getString(i));
                        String hxId = object1.getString("hxId");
                        String messageId = object1.getString("messageId");
                        Map<String, String> map = new ArrayMap<>();
                        map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_REFUSE);
                        map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, messageId);
                        sendCMDMsg(EamConstant.EAM_CHAT_REFUSE_GAME_NOTIFY, hxId, map);
                    }
                    // 多个人邀请我 游戏  接受 某个人 返回拒绝其他人的邀请 refuseIds  我需要拒绝这些人 的邀请

                    Map<String, String> map = new ArrayMap<>();
                    map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
                    map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, message.getMsgId());
                    map.put(EamConstant.EAM_CHAT_ATTR_GAME_ID, message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, ""));
                    map.put(EamConstant.EAM_CHAT_ATTR_GAME_URL, message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, ""));
                    map.put(EamConstant.EAM_CHAT_ATTR_NICKNAME, SharePreUtils.getNicName(mAct));
                    map.put(EamConstant.EAM_CHAT_ATTR_UID, SharePreUtils.getUId(mAct));
                    map.put(EamConstant.EAM_CHAT_ATTR_GAME_MATCH_ID, matchId);
                    map.put(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
                    sendCMDMsg(EamConstant.EAM_CHAT_ACCEPT_GAME_NOTIFY, message.getFrom(), map, new EMCallBack()
                    {
                        @Override
                        public void onSuccess()
                        {
                            Logger.t(TAG).d("chat------>同意邀请发送cmd 成功");
                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();
                            mAct.runOnUiThread(() ->
                            {
                                if (dialog != null && dialog.isShowing())
                                    dialog.dismiss();
                            });
                            Intent intent = new Intent(mAct, GameAct.class);
                            String gameId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, "");
                            String gameUrl = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, "");
                            intent.putExtra("gameId", gameId);
                            intent.putExtra("gameUrl", gameUrl);
                            intent.putExtra("gameName", mAct.getString(R.string.chat_game_name));
                            intent.putExtra("matchId", matchId);
                            mAct.startActivity(intent);
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
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                if ("ALREADY_REFUSED".equals(apiE.getErrorCode()))
                {
                    message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_OVERDUE);
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                    conversation.updateMessage(message);
                    Logger.t(TAG).d("chat------>点击游戏邀请弹窗后过期后台返回错误跳转");
                    mAct.runOnUiThread(() ->
                    {
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    });
                    Intent gameIntent = new Intent(mAct, GameAct.class);
                    gameIntent.putExtra("gameUrl", message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, ""));
                    gameIntent.putExtra("gameId", message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, ""));
                    mAct.startActivity(gameIntent);
                }
            }
        }, NetInterfaceConstant.Merge10C_agreeInvite, reqParamMap);
    }

    /**
     * 拒绝游戏邀请
     *
     * @param lUid
     */
    public void refuseGameInvite(Activity mAct, String lUid, EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        reqParamMap.put(ConstCodeTable.messageId, message.getMsgId());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                String msgId = message.getMsgId();
                Logger.t(TAG).d("点击接受或拒绝： | " + msgId);
                Logger.t(TAG).d("chat------>拒绝游戏邀请返回结果：" + response);
                Map<String, String> map = new ArrayMap<>();
                map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_REFUSE);
                map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, msgId);
                sendCMDMsg(EamConstant.EAM_CHAT_REFUSE_GAME_NOTIFY, message.getFrom(), map, new EMCallBack()
                {
                    @Override
                    public void onSuccess()
                    {
                        message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_REFUSE);
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat, true);
                        conversation.updateMessage(message);
                        mAct.runOnUiThread(() ->
                        {
                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();
                        });
                    }

                    @Override
                    public void onError(int i, String s)
                    {
                        Logger.t(TAG).d("发送失败：" + i + " | " + s);
                    }

                    @Override
                    public void onProgress(int i, String s)
                    {

                    }
                });
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                if ("ALREADY_REFUSED".equals(apiE.getErrorCode()))
                {
                    message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_OVERDUE);
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat, true);
                    conversation.updateMessage(message);
                    mAct.runOnUiThread(() ->
                    {
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    });
                }
            }
        }, NetInterfaceConstant.Merge10C_refuseInvite, reqParamMap);
    }

    private void sendCMDMsg(String action, String toChatUserName, Map<String, String> attribute)
    {
        sendCMDMsg(action, toChatUserName, attribute, null);
    }

    private void sendCMDMsg(String action, String toChatUserName, Map<String, String> attribute, EMCallBack callBack)
    {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setChatType(EMMessage.ChatType.Chat);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(action);
        message.setTo(toChatUserName);
        message.addBody(cmdMessageBody);
        if (attribute != null)
        {
            for (Map.Entry<String, String> stringStringEntry : attribute.entrySet())
            {
                message.setAttribute(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }
        if (callBack == null)
        {
            callBack = new EMCallBack()
            {
                @Override
                public void onSuccess()
                {
                    Logger.t(TAG).d("发送cmd成功");
                }

                @Override
                public void onError(int i, String s)
                {

                }

                @Override
                public void onProgress(int i, String s)
                {

                }
            };
        }
        message.setMessageStatusCallback(callBack);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 接到环信时调的 获取看过你视频的收益然后弹窗
     */
    private void getMyRedIncome(Context context)
    {
        Map<String, String> param = NetHelper.getCommonPartOfParam(context);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("hx查询红包返回>>" + response.getBody());
                try
                {
                    JSONObject jsonObject = new JSONObject(response.getBody());
                    if ("1".equals(jsonObject.getString("red")))
                    {
                        if (CommonUtils.isAppOnForeground(context))
                        {
                            Intent intent = new Intent(EamApplication.getInstance(), RedPacketShowAct.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            intent.putExtra("content", jsonObject.optString("content", ""));
                            intent.putExtra("income", jsonObject.optString("income", ""));
                            EamApplication.getInstance().startActivity(intent);
                        }
                        else if (redList != null)
                        {
                            redList.add(response.getBody());
                        }
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

        }, NetInterfaceConstant.SinglesDayC_myIncome, "", param);
    }

    /**
     * 显示app后台时 双十一红包
     */
    public void showSaveRedPacket()
    {
        if (redList != null)
        {
            for (String s : redList)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    Intent intent = new Intent(EamApplication.getInstance(), RedPacketShowAct.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intent.putExtra("content", jsonObject.optString("content", ""));
                    intent.putExtra("income", jsonObject.optString("income", ""));
                    EamApplication.getInstance().startActivity(intent);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            redList.clear();
        }
    }

    int firstMsgTime = 0;
    int secondMsgTime = 0;
    boolean isFirstIn = false;
    private final int MESSAGE_NOTIFICATION = 1001;
    private final int GAME_MESSAGE_NOTIFICATION = 5566;

    private void filterNotification(EMMessage message)
    {
        if (handler.hasMessages(MESSAGE_NOTIFICATION))
            handler.removeMessages(MESSAGE_NOTIFICATION);
        Message msg = new Message();
        msg.what = MESSAGE_NOTIFICATION;
        msg.obj = message;
        handler.sendMessageDelayed(msg, 1000);
    }

    private Handler handler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_NOTIFICATION:
                    Logger.t(TAG).d("-----------------收到消息");
                    getNotifier().onNewMsg((EMMessage) msg.obj);
                    break;
                case GAME_MESSAGE_NOTIFICATION:
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    boolean isBeInvite = (boolean) map.get("isBeInvited");
                    String matchId = map.get("matchId").toString();
                    EMMessage message = (EMMessage) map.get("message");
                    EMMessage gameMessage = (EMMessage) map.get("gameMessage");
                    Logger.t(TAG).d("chat------>Handler收到弹窗消息：showGoFightListener:" + showGoFightListener);
                    if (showGoFightListener != null)
                        showGoFightListener.onShowGoFightDialog(isBeInvite, matchId, message, gameMessage);
                    break;
            }
        }
    };

    /**
     * 兼容 ios 线上老版本 ， 获取 用户信息
     *
     * @param message
     */
    private void checkMessageIsNoAttribute(EMMessage message)
    {
        if (message == null)
            return;
        String uid = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, "");
        Logger.t(TAG).d("---------->uid:" + uid + "user" + getUserInfo(message.getFrom()));
        if (TextUtils.isEmpty(uid))
        {
            String imUid = message.getFrom();
            EaseUser user = getUserInfo(imUid);
            if (TextUtils.isEmpty(user.getuId()))
            {
                getUserInfoByImuid(imUid);
            }
        }
    }

    private void getUserInfoByImuid(final String imUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(EamApplication.getInstance());
        reqParamMap.put(ConstCodeTable.imuId, imUid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    Logger.t(TAG).d("UserC_imuToUser:" + response.toString());
                    JSONObject object = new JSONObject(response.getBody());
                    final EaseUser toEaseUser = new EaseUser(imUid);
                    toEaseUser.setuId(object.getString("uId"));
                    toEaseUser.setId(object.getString("id"));
                    toEaseUser.setNickName(object.getString("nicName"));
                    toEaseUser.setAvatar(object.getString("uphUrl"));
                    toEaseUser.setLevel(object.getString("level"));
                    toEaseUser.setSex(object.getString("sex"));
                    toEaseUser.setAge(object.getString("age"));
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            saveContact(toEaseUser);
                        }
                    }).start();

                    if (saveContactSuccessListener != null)
                        saveContactSuccessListener.onRefreshConversationData();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.UserC_imuToUser, null, reqParamMap);
    }

    private ISaveContactSuccessListener saveContactSuccessListener;

    public interface ISaveContactSuccessListener
    {
        void onRefreshConversationData();
    }

    public void setOnSaveContactSuccessListener(ISaveContactSuccessListener listener)
    {
        saveContactSuccessListener = listener;
    }

    /**
     * 环信是否是登录状态
     *
     * @return
     */
    public boolean isLoggedIn()
    {
        return EMClient.getInstance().isLoggedInBefore();
    }


    /**
     * 正常退出
     *
     * @param mContext
     * @param callback
     */
    public void quitNormal(final Context mContext, final ICommonOperateListener callback)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                String code = apiE.getErrorCode();
                Logger.t(TAG).d("错误码为：%s", code);
                if (callback != null)
                    callback.onError(code, ErrorCodeTable.parseErrorCode(code));
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    //region 退出腾讯账号
                    ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack()
                    {
                        @Override
                        public void onSuccess(Object data)
                        {
                            //清除本地缓存
                            Logger.t(TAG).d("腾讯退出成功");
                            EamLogger.t("TXIM").writeToDefaultFile("腾讯退出成功");
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg)
                        {
                            //ToastUtils.showShort(mContext, "T退出失败 " + errMsg);
                            Logger.t(TAG).d("腾讯退出错误码》" + errCode + "描述》" + errMsg);
                            EamLogger.t("TXIM").writeToDefaultFile("腾讯退出错误码》" + errCode + "描述》" + errMsg);
                        }
                    });
                    //endregion

                    //region 退出环信账号
                    IMHelper.getInstance().setOnILogoutFinishListener(new IMHelper.IHxLogoutFinishedListener()
                    {
                        @Override
                        public void onSuccess()
                        {
                            Logger.t(TAG).d("环信退出成功");
                            Intent intent = new Intent(appContext, LoginModeAct.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            if (callback != null)
                                callback.onSuccess("success");
                        }

                        @Override
                        public void onFailed(int i, String s)
                        {
                            ToastUtils.showShort("H退出失败 " + s);
                        }
                    });
                    IMHelper.getInstance().huanXinLogout(mContext);
                    //endregion
                    quiteCommon(mContext);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onError("100", e.getMessage());
                }
            }
        }, NetInterfaceConstant.UserC_loginOut, reqParamMap);
    }

    /**
     * 用户在线上被顶了，用户还有可能在直播当中被顶了（这你妈有多大几率是这样的啊，技术苦逼也得处理）
     *
     * @param mContext
     */
    public void quitOnlineConflict(final Context mContext, final ICommonOperateListener callback)
    {
        if (quitAccountListener != null)
            quitAccountListener.QuitAccountSuccess(EamConstant.EAM_STATUS_CONFLICT);
        /**********腾讯账号与环信账号被顶了，此处应该不用退出了，待测试*********************/
        //region 退出环信账号
        IMHelper.getInstance().setOnILogoutFinishListener(new IMHelper.IHxLogoutFinishedListener()
        {
            @Override
            public void onSuccess()
            {
                Logger.t(TAG).d("环信退出成功");
                if (callback != null)
                    callback.onSuccess("success");
            }

            @Override
            public void onFailed(int i, String s)
            {
                ToastUtils.showShort("H退出失败 " + s);
                if (callback != null)
                    callback.onError(i + "", s);
            }
        });
        IMHelper.getInstance().huanXinLogout(mContext);
        //endregion
        quiteCommon(mContext);
        if (callback != null)
            callback.onSuccess("success");
    }

    /**
     * 被顶用户不在线，下次登录时发现被顶需要清除相关资源
     *
     * @param mContext
     */
    public void quitOfflineConflict(final Context mContext, final ICommonOperateListener callback)
    {
        /**********腾讯账号与环信账号被顶了，此处应该不用退出了，待测试*********************/
        quiteCommon(mContext);
        //region 退出环信账号
        IMHelper.getInstance().setOnILogoutFinishListener(new IMHelper.IHxLogoutFinishedListener()
        {
            @Override
            public void onSuccess()
            {
                Logger.t(TAG).d("环信退出成功");
                if (callback != null)
                    callback.onSuccess("success");
            }

            @Override
            public void onFailed(int i, String s)
            {
                ToastUtils.showShort("H退出失败 " + s);
                if (callback != null)
                    callback.onError(i + "", s);
            }
        });
        IMHelper.getInstance().huanXinLogout(mContext);
        //endregion
    }

    private void quiteCommon(final Context mContext)
    {
//        //退出百度推送
//        PushManager.stopWork(mContext);
        //退出小米推送
        MiPushClient.unregisterPush(mContext);
        //不需要删除的share字段添加到aLst中
        ArrayList<String> aLst = new ArrayList<String>();
        aLst.add("giftVersion");
        aLst.add("versionCode");
        SharePreUtils.removeValueExcludeSome(mContext, aLst);
        PreferenceManager.getInstance().removeCurrentUserInfo();
    }

    public void openLoginPageAfterQuite(Context mContext)
    {
        Intent intent = new Intent(mContext, LoginModeAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * get instance of EaseNotifier
     *
     * @return
     */
    public EaseNotifier getNotifier()
    {
        return easeUI.getNotifier();
    }


    /**
     * save single contact
     */
    public void saveContact(final EaseUser user)
    {
        if (TextUtils.isEmpty(user.getUsername()))
        {
            Logger.t(TAG).d("此用户没有hxId，不保存该用户信息");
            return;
        }
        if (contactList != null)
            contactList.put(user.getUsername(), user);
        imModel.saveContact(user);
    }

    public void deleteContact(String hxUsername)
    {
        Logger.t(TAG).d("删除用户信息hxUserName:" + hxUsername);
        contactList.remove(hxUsername);
        imModel.deleteContact(hxUsername);
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList()
    {
        Logger.t(TAG).d(String.format("是否登录》 %s    联系人列表》 %s ", isLoggedIn(), (contactList == null ? "null" : contactList.toString())));
        if (contactList.isEmpty())
        {
            contactList = imModel.getContactList();
        }
        if (contactList == null)
        {
            return new Hashtable<String, EaseUser>();
        }
/*        for (Map.Entry<String, EaseUser> eu : contactList.entrySet())
        {
            Logger.t(TAG).d("hx获取联系人列表=======" + eu.getKey() + "----" + eu.getValue().toString());
        }*/
        return contactList;
    }


    /**
     * get current user's id
     */
    public String getCurrentUsernName()
    {
        if (username == null)
        {
            username = imModel.getCurrentUsernName();
        }
        return username;
    }

    /**
     * 此处当通过环信账号冲突机制获知被顶号后，调用我们自己的token 登录函数来完成操作
     */
    private void confirmConflict(final Context context)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(context);
        reqParamMap.remove(ConstCodeTable.token);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                NetHelper.handleNetError(context, "网络原因，token登录失败--> ", TAG, throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    //首次签到
                    SharePreUtils.setFirst(context, new JSONObject(response).getString("first"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                IMHelper.getInstance().huanXinLogin(appContext);
            }
        }, NetInterfaceConstant.UserC_checkDevice, reqParamMap);
    }

    public static final String HELLO_CHAT_TYPE = "helloChat";
    public static final String NORMAL_CHAT_TYPE = "normalChat";
    public static final String ALL_CHAT_TYPE = "allChat";

    /**
     * 获取未读消息数
     *
     * @param chatType all / normalChat / helloChat
     * @return
     */
    public void getUnreadChatMsgNum(String chatType, ICommonOperateListener listener)
    {
        List<EMConversation> chatCon = EMClient.getInstance().chatManager().getConversationsByType(EMConversation.EMConversationType.Chat);
        if (chatCon == null)
            return;
        Observable.fromIterable(chatCon)
                .map(new Function<EMConversation, Map<String, Integer>>()
                {
                    @Override
                    public Map<String, Integer> apply(EMConversation emConversation) throws Exception
                    {
                        Map<String, Integer> map = new HashMap<>();
                        map.put("hello",0);
                        map.put("chat",0);
                        map.put("all",0);
                        EMMessage lastMsgFromOther = emConversation.getLatestMessageFromOthers();
                        if (HELLO_CHAT_TYPE.equals(chatType))//过滤打招呼的消息
                        {
                            boolean result;
                            if (lastMsgFromOther != null)
                                result = lastMsgFromOther.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false) == true;
                            else
                                result = false;
                            if (result)
                            {
                                map.put("hello", emConversation.getUnreadMsgCount());
                            }
                        }
                        else if (NORMAL_CHAT_TYPE.equals(chatType))
                        {
                            boolean result;
                            if (lastMsgFromOther == null)//说明此会话里面只包含自己发送的消息，对方没有回应，处于聊天列表中
                                result = true;
                            else
                                result = lastMsgFromOther.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false) == false;
                            if (result)
                            {
                                map.put("chat", emConversation.getUnreadMsgCount());
                            }
                        }
                        else
                            map.put("all", emConversation.getUnreadMsgCount());
                        return map;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Map<String, Integer>>>()
                {
                    @Override
                    public void accept(List<Map<String, Integer>> maps) throws Exception
                    {
                        Map<String, Integer> resultMap = new HashMap<>();
                        resultMap.put("hello",0);
                        resultMap.put("chat",0);
                        resultMap.put("all",0);
                        int helloUnread = 0;
                        int chatUnread = 0;
                        int allUnread = 0;
                        for (Map<String, Integer> map : maps)
                        {
                            if (HELLO_CHAT_TYPE.equals(chatType))
                            {
                                helloUnread += map.get("hello");
                            }
                            else if (NORMAL_CHAT_TYPE.equals(chatType))
                            {
                                chatUnread += map.get("chat");
                            }
                            else
                                allUnread += map.get("all");

                        }
                        resultMap.put("hello", helloUnread);
                        resultMap.put("chat", chatUnread);
                        resultMap.put("all", allUnread);
                        if (listener != null)
                            listener.onSuccess(new Gson().toJson(resultMap));
                    }
                });
    }


    public void pushActivity(Activity activity)
    {
        if (easeUI != null)
            easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity)
    {
        easeUI.popActivity(activity);
    }


    private IQuitAccountFinishListener quitAccountListener;

    public interface IQuitAccountFinishListener
    {
        void QuitAccountSuccess(String quitType);
    }

    public void setQuitAccountListener(IQuitAccountFinishListener listener)
    {
        quitAccountListener = listener;
    }

    public void removeQuitAccountListener()
    {
        quitAccountListener = null;
    }

    public interface ISyncUserinfoListener
    {
        void getUser(EaseUser eUser);
    }
}