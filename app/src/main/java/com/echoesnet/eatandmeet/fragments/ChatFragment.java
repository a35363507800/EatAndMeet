package com.echoesnet.eatandmeet.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.Target;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.BaiduMapActivity;
import com.echoesnet.eatandmeet.activities.CAddEmojsAct;
import com.echoesnet.eatandmeet.activities.CChatActivity;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.CopyOrderMealAct;
import com.echoesnet.eatandmeet.activities.GameAct;
import com.echoesnet.eatandmeet.activities.RelationAct;
import com.echoesnet.eatandmeet.activities.TrendsRecordVideoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.daos.EmojiconBigExpressionData;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.datamodel.AbstractEMMessageListener;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.models.datamodel.EmojiGroupEntity;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.presenters.ImpIChatFragmentView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IChatFragmentView;
import com.echoesnet.eatandmeet.utils.ChatCommonUtils;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseCommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketConstant;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketUtil;
import com.echoesnet.eatandmeet.views.widgets.ChatTipDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.chat.ChatContextMenu;
import com.echoesnet.eatandmeet.views.widgets.chat.ChatInputMenu;
import com.echoesnet.eatandmeet.views.widgets.chat.ChatMessageList;
import com.echoesnet.eatandmeet.views.widgets.chat.SendVoiceTipView;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2017/7/14
 * @Description
 */
@RuntimePermissions
public class ChatFragment extends MVPBaseFragment<IChatFragmentView, ImpIChatFragmentView> implements IChatFragmentView
{
    private static final String TAG = ChatFragment.class.getSimpleName();

    @BindView(R.id.chat_msg_list)
    ChatMessageList chatMsgList;
    @BindView(R.id.send_voice_tip)
    SendVoiceTipView sendVoiceTip;
    @BindView(R.id.chat_input_menu)
    ChatInputMenu chatInputMenu;
    @BindView(R.id.btn_focus)
    Button btnFocus;
    @BindView(R.id.itv_cancle)
    IconTextView itvCancle;
    @BindView(R.id.rl_tip)
    RelativeLayout rlTip;
    @BindView(R.id.root_view)
    RelativeLayout rootView;
    private Unbinder unbinder;

    private static final int REQUEST_CODE_MAP = 1;
    private static final int REQUEST_CODE_SEND_MONEY = 16;

    private Activity mAct;

    private List<String> popupMenuItemList = new ArrayList<>();

    private EMConversation conversation;
    protected boolean isloading;
    protected boolean haveMoreData = true;
    private boolean isMessageListInited;
    private int pagesize = 20;
    private String toChatUserName;
    private EaseUser toEaseUser;
    private int chatType;
    private RecyclerView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isPressedVoice = false;

    private boolean isWideVoiceType = false;
    private AudioManager audioManager;

    private int sendMsgCount, receiveMsgCount;
    /*** 0 为聊天  1 为 打招呼*/
    private String isSayHello = "";
    /**
     * 0 双方都没拉黑
     * 1 luid的黑名单中有uid
     * 2 uid的黑名单中有luid
     * 3 双方互相拉黑
     */
    private String inBlack = "";
    private String remark;
    private boolean isFirstSendIsSayHelloMsg = true;
    private boolean isClickNotRemoveBlack = false;
    /**
     * 长按message
     */
    private EMMessage contextMenuMessage;
    private ClipboardManager clipboard;
    private float mRawX;
    private float mRawY;
    /**
     * 是否是转发
     */
    private boolean isForward = false;

    public boolean isReceivedAcceptMsg = false;
    //start  在本页面切后台时收到游戏同意邀请cmd时保存 内容
    public String inBackMatchId = "";
    public EMMessage inBackMessage;
    public long inBackRecMsgTime;
    //end

    private boolean isNeed2RefreshData = true;

    private Map<String, Object> saveStatesMap = new ArrayMap<>();

    private String gameId = "";
    private String gameUrl = "";
    private String gameName = "";
    private String gameMsgTitle = "《合到10》游戏邀请";
    private String gameMsgDesc = "多人对战游戏《合到10》！拼智商，刷排行！";
    private String gameMsgId = "";
    private ChatContextMenu chatMenu;

    public ChatFragment()
    {

    }

    public static ChatFragment newInstance(EaseUser toEaseUser)
    {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.EXTRA_TO_EASEUSER, toEaseUser);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected ImpIChatFragmentView createPresenter()
    {
        return new ImpIChatFragmentView(mAct, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.my_chat_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mAct = getActivity();
        init();
        chatInputMenu.init(null);
        //添加emoj表情
        for (EmojiGroupEntity e : EmojiconBigExpressionData.emojEntities(getActivity()))
        {
            if (!chatInputMenu.getChatEmojiMenu().isContainEmojiconGroup(e))
                chatInputMenu.getChatEmojiMenu().addEmojiconGroup(e);
        }
        chatInputMenu.setChatInputMenuListener(chatInputMenuListener);
    }


    private void init()
    {
        if (getArguments() != null)
        {
            toEaseUser = getArguments().getParcelable(Constant.EXTRA_TO_EASEUSER);
            toChatUserName = toEaseUser.getUsername();
            chatType = getArguments().getInt(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE);
        }
        gameName = getString(R.string.chat_game_name);
        if (mPresenter != null)
            mPresenter.queryUsersRelationShip(toEaseUser.getuId());

        audioManager = (AudioManager) mAct.getSystemService(Context.AUDIO_SERVICE);

        chatMsgList.init(toChatUserName, chatType);
        isMessageListInited = true;
        listView = chatMsgList.getListView();
        swipeRefreshLayout = chatMsgList.getSwipeRefreshLayout();
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        onConversationInit();
        setRefreshLayoutListener();
        setListViewTouchListener();
        setChatListBubbleClickListener();
        if (System.currentTimeMillis() - HuanXinIMHelper.getInstance().inBackRecMsgTime < 1000 * 15
                && !HuanXinIMHelper.getInstance().isClose2Fight)
        {
            showGoFightDialog(getArguments());
            HuanXinIMHelper.getInstance().isClose2Fight = false;
        }
        else
            changeMessageStat();
        //测试布局用
//        showBookRestaurantTip();
    }

    public void updateEaseUser(EaseUser eUser)
    {
        toEaseUser = eUser;
    }

    public void setIsSayHello(String isSayHello)
    {
        this.isSayHello = isSayHello;
    }

    public void setInBlack(String inBlack)
    {
        this.inBlack = inBlack;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getInBlack()
    {
        return inBlack;
    }

    public void sendInBlackCMDMsg(String action)
    {
        sendCMDMsg(action, toChatUserName, null);
    }

    public void sendOutBlackCMDMsg(String action)
    {
        sendCMDMsg(action, toChatUserName, null);
    }

    public void sendClearMsgCountCMDMsg(String action)
    {
        sendCMDMsg(action, toChatUserName, null);
    }


    public ChatMessageList getChatMsgList()
    {
        return chatMsgList;
    }

    /**
     * 显示关注view
     */
    public void showFocusTip()
    {
        if (rlTip != null)
        {
            if (rlTip.getVisibility() == View.VISIBLE)
                return;
            Animation animation = AnimationUtils.loadAnimation(mAct, R.anim.invite_connect_in);
            rlTip.setAnimation(animation);
            rlTip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏关注view
     */
    public void hideFocusTip()
    {
        if (rlTip != null)
        {
            if (rlTip.getVisibility() == View.GONE)
                return;
            Animation animation = AnimationUtils.loadAnimation(mAct, R.anim.invite_connect_out);
            rlTip.setAnimation(animation);
            rlTip.setVisibility(View.GONE);
        }
    }

    /**
     * 聊天彩蛋，显示去订餐
     */
    public void showBookRestaurantTip()
    {
        Logger.t(TAG).d("时间：hour: 直播变量：" + CommonUtils.isInLiveRoom +
                " | 是否点过不：" + HuanXinIMHelper.getInstance().checkIsContainsUser(toChatUserName) +
                " | list:" + HuanXinIMHelper.getInstance().bookList2String());
        if (CommonUtils.isInLiveRoom)
            return;
        if (HuanXinIMHelper.getInstance().checkIsContainsUser(toChatUserName))
            return;
        mAct.runOnUiThread(() ->
        {
            View view = LayoutInflater.from(mAct).inflate(R.layout.chat_res_tip, null);
            new ChatTipDialog()
                    .buildDialog(mAct)
                    .setCancelable(false)
                    .setContent(view)
                    .setCommitBtnText("立刻预订")
                    .setCancelBtnText("下次再说")
                    .setCommitBtnClickListener((v) ->
                    {
                        Intent intent = new Intent(mAct, CopyOrderMealAct.class);
                        intent.putExtra("chat", "chat");
                        startActivity(intent);
                    })
                    .setCancelBtnClickListener((v) -> HuanXinIMHelper.getInstance().addBookOrderUser(toChatUserName))
                    .show();
        });
    }

    /**
     * 移除黑名单 提示
     */
    public void showRemoveBlackListTip()
    {
        if (CommonUtils.isInLiveRoom)
            return;
        View view = LayoutInflater.from(mAct).inflate(R.layout.chat_remove_black_tip, null);
        new ChatTipDialog()
                .buildDialog(mAct)
                .setCancelable(false)
                .setContent(view)
                .setCommitBtnText("移 出")
                .setCancelBtnText("不")
                .setCommitBtnClickListener((v) -> mPresenter.deleteBlack(toEaseUser.getuId()))
                .setCancelBtnClickListener((v) -> isClickNotRemoveBlack = true)
                .show();
    }

    /**
     * 设置聊天item点击监听
     */
    private void setChatListBubbleClickListener()
    {
        chatMsgList.setItemClickListener(new ChatMessageList.MessageListItemClickListener()
        {
            @Override
            public void onResendClick(final EMMessage message)
            {
                new CustomAlertDialog(mAct)
                        .builder()
                        .setTitle("提示")
                        .setMsg("重新发送")
                        .setPositiveButton("是", (view) ->
                        {
//                            conversation.removeMessage(message.getMsgId());
//                            chatMsgList.removeMessage(message.getMsgId(), false);
//                            sendMessage(message, message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, false));

                            message.setStatus(EMMessage.Status.CREATE);
                            chatMsgList.notifyMessage(message.getMsgId());
                            handleSendMessage(message);
                        })
                        .setNegativeButton("否", (view) ->
                        {

                        }).show();
            }

            @Override
            public boolean onBubbleClick(EMMessage message)
            {
                //如果是红包进入且事件标记为已经处理
                if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false))
                {
                    //如果 我的黑名单 有他 或者 互相拉黑(代表我的黑名单  有他)
                    if (("2".equals(inBlack) || "3".equals(inBlack)) && message.direct() == EMMessage.Direct.RECEIVE)
                    {
                        if (!isClickNotRemoveBlack && !CommonUtils.isInLiveRoom)
                            showRemoveBlackListTip();
                        else
                            RedPacketUtil.openRedPacket(getActivity(), chatType, message, toEaseUser, chatMsgList, "1".equals(isSayHello), remark);
                    }
                    else
                        RedPacketUtil.openRedPacket(getActivity(), chatType, message, toEaseUser, chatMsgList, "1".equals(isSayHello), remark);
                    return true;
                }
                else if (message.getType() == EMMessage.Type.VOICE || message.getType() == EMMessage.Type.VIDEO)
                {
                    EaseCommonUtils.makeMessageAsRead(TAG + ".onBubbleClick", conversation, message, true);
                }

                return false;
            }

            @Override
            public void onBubbleLongClick(View view, float x, float y, EMMessage message)
            {
                contextMenuMessage = message;
                if (CommonUtils.checkDeviceHasNavigationBar(mAct))
                {
                    Logger.t(TAG).d("chat------>BottomStatusHeight:" + CommonUtils.getBottomStatusHeight(mAct));
                    y = y - CommonUtils.getBottomStatusHeight(mAct) / 2 - 20;
                }
//                else
//                    y = y - 20;
                onChatBubbleLongClick(view, x, y - 20, message);
            }

            @Override
            public void onUserAvatarClick(String uId)
            {
                Intent intent = new Intent(mAct, CNewUserInfoAct.class);
                if (TextUtils.equals(uId, SharePreUtils.getUId(mAct)))
                {
                    intent.putExtra("toUId", SharePreUtils.getUId(mAct));
                    intent.putExtra("toId", SharePreUtils.getId(mAct));
                }
                else
                {
                    intent.putExtra("toUId", toEaseUser.getuId());
                    intent.putExtra("toId", toEaseUser.getId());
                }
                mAct.startActivityForResult(intent, EamCode4Result.reQ_CChatActivity);
                if (mAct instanceof CChatActivity)
                {
                    ((CChatActivity) mAct).isRefreshDataOnResume = true;
                }
            }

            @Override
            public void onUserAvatarLongClick(String username)
            {

            }

            @Override
            public void onGameAcceptOrRefuseClick(boolean isAccept, int position, EMMessage message)
            {
                if (isAccept)
                {
                    if (!CommonUtils.isInLiveRoom)
                    {
                        mPresenter.acceptGameInvite(toEaseUser.getuId(), position, message);
                    }
                    else
                    {
                        new CustomAlertDialog(mAct)
                                .builder()
                                .setTitle("提示 !")
                                .setMsg(getString(R.string.chat_game_live_tips))
                                .setCancelable(false)
                                .setPositiveButton("确认", (view) ->
                                {
                                    Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_CLOSE_LIVE);
                                    EamApplication.getInstance().sendBroadcast(intent);
                                    mPresenter.acceptGameInvite(toEaseUser.getuId(), position, message);
                                })
                                .setNegativeButton("取消", (view) ->
                                {
                                })
                                .show();
                    }
                }
                else
                    mPresenter.refuseGameInvite(toEaseUser.getuId(), position, message);
            }

            @Override
            public void onMessageSendSuccess(EMMessage message)
            {
                //游戏消息发送成功时调用保存消息ID的 接口
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_GAME_MESSAGE, false))
                {
                    mPresenter.saveMessageId(toEaseUser.getuId(), message.getMsgId());
                }
            }
        });
    }

    /**
     * 长按消息 弹出操作框
     *
     * @param view
     * @param mRawX
     * @param mRawY
     * @param message
     */
    private void onChatBubbleLongClick(View view, float mRawX, float mRawY, EMMessage message)
    {
        popupMenuItemList.clear();
        EMMessage.Type type = message.getType();
        boolean isNeedRecall = false;
        if (EMMessage.Type.TXT == type)
        {
            // 2017/11/13 还要具体细分自定义字段 红包或者 其他消息
            if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false))
            {
                //红包消息
                popupMenuItemList.add(getString(R.string.chat_delete));
                isNeedRecall = false;
            }
            else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false))//大表情
            {
                popupMenuItemList.add(getString(R.string.chat_share));
                popupMenuItemList.add(getString(R.string.chat_delete));
                isNeedRecall = true;
            }
            else if (!TextUtils.isEmpty(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_EXPRESSION_NAME, "")))//小海豚表情
            {
                popupMenuItemList.add(getString(R.string.chat_share));
                popupMenuItemList.add(getString(R.string.chat_delete));
                isNeedRecall = true;
            }
            else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_LIVE_SHARE, false))//直播分享
            {
                popupMenuItemList.add(getString(R.string.chat_share));
                popupMenuItemList.add(getString(R.string.chat_delete));
                isNeedRecall = false;
            }
            else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_GAME_MESSAGE, false))//合到十游戏
            {
//                popupMenuItemList.add(getString(R.string.chat_share));
                popupMenuItemList.add(getString(R.string.chat_delete));
                isNeedRecall = false;
            }
            else
            {
                popupMenuItemList.add(getString(R.string.chat_copy));
                popupMenuItemList.add(getString(R.string.chat_share));
                popupMenuItemList.add(getString(R.string.chat_delete));
                isNeedRecall = true;
            }
        }
        else if (EMMessage.Type.IMAGE == type || EMMessage.Type.VOICE == type || EMMessage.Type.VIDEO == type)
        {
            if (EMMessage.Type.VOICE == type)
            {
                isWideVoiceType = audioManager.isSpeakerphoneOn();
                if (isWideVoiceType)
                {
                    popupMenuItemList.add("听筒播放");
                }
                else
                {
                    popupMenuItemList.add("扬声器播放");
                }
            }
            popupMenuItemList.add(getString(R.string.chat_share));
            popupMenuItemList.add(getString(R.string.chat_delete));
            isNeedRecall = true;
        }
        else if (EMMessage.Type.LOCATION == type)
        {
            popupMenuItemList.add(getString(R.string.chat_share));
            popupMenuItemList.add(getString(R.string.chat_delete));
            isNeedRecall = false;
        }
        if (message.direct() == EMMessage.Direct.SEND && isNeedRecall)
        {
            long msgTime = contextMenuMessage.getMsgTime();
            long nowTime = System.currentTimeMillis();
            Logger.t(TAG).d("msgTime:" + msgTime + " | nowTime:" + nowTime);
            if (nowTime - msgTime < 1000 * 60 * 2)
                popupMenuItemList.add(getString(R.string.chat_recall));
        }
        chatMenu = new ChatContextMenu(mAct);
        chatMsgList.setListViewCanScroll(false);
        chatMenu.showPopupListWindow(view, 0, mRawX, mRawY, popupMenuItemList, new ChatContextMenu.PopupListListener()
        {
            @Override
            public boolean showPopupList(View adapterView, View contextView, int contextPosition)
            {
                return true;
            }

            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position)
            {
                switch (popupMenuItemList.get(position))
                {
                    case "复制":
                        if (clipboard != null)
                        {
                            String text = ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage();
                            if (contextMenuMessage.direct() == EMMessage.Direct.SEND)
                            {
                                String sensitiveContent = contextMenuMessage.getStringAttribute(Constant.MESSAGE_ATTR_SENSITIVE_CONTENT, "");
                                if (!TextUtils.isEmpty(sensitiveContent))
                                {
                                    text = sensitiveContent;
                                }
                            }
                            clipboard.setPrimaryClip(ClipData.newPlainText(null, text));
                        }
                        break;
                    case "转发":
                        isNeed2RefreshData = false;
                        Intent intent = new Intent(mAct, RelationAct.class);
                        intent.putExtra("openSource", "forward");
                        startActivityForResult(intent, EamCode4Result.reQ_Forward_Message);
                        break;
                    case "删除":
                        new CustomAlertDialog(mAct)
                                .builder()
                                .setMsg("是否删除这条消息")
                                .setTitle("提示")
                                .setCancelable(false)
                                .setPositiveButton("是", new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        if (conversation != null)
                                        {
                                            conversation.removeMessage(contextMenuMessage.getMsgId());
                                            chatMsgList.removeMessage(contextMenuMessage.getMsgId(), true);
                                        }
                                    }
                                }).setNegativeButton("否", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                            }
                        }).show();
                        break;
                    case "撤回":
                        long msgTime = contextMenuMessage.getMsgTime();
                        long nowTime = System.currentTimeMillis();
                        Logger.t(TAG).d("msgTime:" + msgTime + " | nowTime:" + nowTime);
                        if (nowTime - msgTime < 1000 * 60 * 2)
                        {
                            new CustomAlertDialog(mAct)
                                    .builder()
                                    .setBoldMsg("是否撤回这条消息？")
                                    .setPositiveButton("是", (view) ->
                                    {
                                        Map<String, String> map = new HashMap<>();
                                        map.put(EamConstant.EAM_CHAT_ATTR_RECALL_MSG_ID, contextMenuMessage.getMsgId());
                                        sendCMDMsg(EamConstant.EAM_CHAT_RECALL_MSG_NOTIFY, toChatUserName, map);
                                        EMMessage recallMsg = ChatCommonUtils.markAsRecallMessage(contextMenuMessage, true);
                                        conversation.updateMessage(recallMsg);
                                        chatMsgList.recallMessage(message.getMsgId(), recallMsg);
                                    })
                                    .setNegativeButton("否", (view) ->
                                    {
                                    })
                                    .setPositiveTextColor(ContextCompat.getColor(mAct, R.color.C0412))
                                    .setNegativeTextColor(ContextCompat.getColor(mAct, R.color.C0322))
                                    .show();
                        }
                        else
                        {
                            ToastUtils.showShort("已经超过2分钟，不可撤回消息");
                        }
                        break;
                    case "听筒播放":
                    case "扬声器播放":
                        if (isWideVoiceType)
                        {
                            audioManager.setSpeakerphoneOn(false);
                        }
                        else
                        {
                            audioManager.setSpeakerphoneOn(true);
                        }
                        isWideVoiceType = !isWideVoiceType;
                        break;
                }
            }

            @Override
            public void onPopupDismiss()
            {
                chatMsgList.setListViewCanScroll(true);
            }
        });

    }

    /**
     * 聊天列表touch
     */
    private void setListViewTouchListener()
    {
        listView.setOnTouchListener((v, event) ->
        {
            if (isPressedVoice)
                return false;
            mRawX = event.getRawX();
            mRawY = event.getRawY();
            Logger.t(TAG).d("rootView.setOnTouchListener:" + mRawX + " | " + mRawY);
            chatInputMenu.hideKeyBroad();
            chatInputMenu.hideSendVoiceViewOrEmojiView();
            return false;
        });
    }

    /**
     * Sets swipeRefreshLayout listener.
     */
    protected void setRefreshLayoutListener()
    {
        swipeRefreshLayout.setOnRefreshListener(() ->
        {
            new Handler().postDelayed(() ->
            {
                if (isloading)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                isloading = true;
                if (chatMsgList.getItem(0) != null)
                {
                    List<EMMessage> messages = conversation.loadMoreMsgFromDB(chatMsgList.getItem(0).getMsgId(), pagesize);
                    if (messages == null)
                        messages = new ArrayList<>();
                    if (messages.size() > 0)
                    {
                        chatMsgList.refreshSeekTo(messages.size() - 1);
                        if (messages.size() != pagesize)
                        {
                            haveMoreData = false;
                        }
                    }
                    else
                    {
                        haveMoreData = false;
                    }
//                    if (!haveMoreData)
//                        ToastUtils.showShort("没有更多的消息.");
                    isloading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
                else
                {
//                    ToastUtils.showShort("没有更多的消息.");
                    isloading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 600);
        });
    }

    /**
     * 返回键动作
     *
     * @return
     */
    public boolean onBackPressed()
    {
        return chatInputMenu.onBackPressed();
    }


    /**
     * 聊天功能按钮监听
     */
    ChatInputMenu.ChatInputMenuListener chatInputMenuListener = new ChatInputMenu.ChatInputMenuListener()
    {
        @Override
        public void onSendMessage(String content)
        {
            sendTextMessage(content, toChatUserName);
        }

        @Override
        public void onScrollBarSendMessage(String content)
        {
            if (CommonUtils.isInLiveRoom)
                chatInputMenu.hideSendVoiceViewOrEmojiView();
            sendTextMessage(content, toChatUserName);
        }

        @Override
        public void onSelectImageClicked()
        {
            isNeed2RefreshData = false;
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setPreviewEnabled(true)
                    .setShowCamera(!CommonUtils.isInLiveRoom)
                    .setShowGif(false)
                    .start(mAct, ChatFragment.this, EamConstant.EAM_OPEN_IMAGE_PICKER);
        }

        @Override
        public void onTakePhotoClicked()
        {
            isNeed2RefreshData = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ChatFragmentPermissionsDispatcher.onCameraAudioPermGrantedWithPermissionCheck(ChatFragment.this);
            else
                startActivityForResult(new Intent(mAct, TrendsRecordVideoAct.class), EamConstant.EAM_OPEN_RECORD_VIDEO);
        }

        @Override
        public void onSendRedPackageClicked()
        {
            isNeed2RefreshData = false;
            RedPacketUtil.startRedPacketActivityForResult(ChatFragment.this, chatType, toEaseUser, REQUEST_CODE_SEND_MONEY);
        }

        @Override
        public void onSendLocationClicked()
        {
            isNeed2RefreshData = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ChatFragmentPermissionsDispatcher.onLocationPermGrantedWithPermissionCheck(ChatFragment.this);
            else
                startActivityForResult(new Intent(getActivity(), BaiduMapActivity.class), REQUEST_CODE_MAP);
        }

        @Override
        public void onBigExpressionClicked(EmojiIcon emojicon)
        {
            if (CommonUtils.isInLiveRoom)
                chatInputMenu.hideSendVoiceViewOrEmojiView();
            if (emojicon.getType() == EmojiIcon.Type.NORMAL_AS_EXPRESSION)
            {
                sendEmojiNormalAsExpressMessage(emojicon, toChatUserName);
            }
            else
            {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode(), emojicon.getBitIconNetPath(), toChatUserName);
            }
        }

        @Override
        public boolean onPressToSpeakBtnTouch(View v, MotionEvent event)
        {

            if (sendVoiceTipListener != null)
                sendVoiceTipListener.onPressSendVoiceView(event);

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    isPressedVoice = true;
//                    sendVoiceTempMessage();
                    break;
                case MotionEvent.ACTION_UP:
                    isPressedVoice = false;
                    break;
                default:
                    break;
            }

            return sendVoiceTip.onPressSendVoice(event, new SendVoiceTipView.VoiceRecorderCallback()
            {
                @Override
                public void onVoiceRecordComplete(final String voiceFilePath, final int voiceTimeLength)
                {
                    Logger.t(TAG).d("voiceFilePath:" + voiceFilePath + " | voiceTimeLength:" + voiceTimeLength);
                    sendVoiceMessage(voiceFilePath, voiceTimeLength, toChatUserName);
                }
//                @Override
//                public void onVoiceRecordCancel()
//                {
//                    removeVoiceTempMessage();
//                }
            });
        }

        @Override
        public void onAddEmojiBtnClicked()
        {
            Intent intent = new Intent(getActivity(), CAddEmojsAct.class);
            startActivityForResult(intent, EamConstant.REQUEST_CODE_AND_EMOJI);
        }

        @Override
        public void onEditTextHasFocus(String type)
        {
            Logger.t(TAG).d("点击输入框");
            if ("editText_key_enter".equals(type))
            {
                new Handler().postDelayed(() -> chatMsgList.smoothScroll2Last(), 200);
            }
            else//9248
                new Handler().postDelayed(() -> chatMsgList.selectLast(), 200);
            if ("emoji".equals(type) && !CommonUtils.isInLiveRoom)
            {
                showNewBieGuide();
            }
        }

        @Override
        public void onEmojiMenuClick(boolean isShown)
        {
            if (emojiMenuOpenListener != null)
            {
                if (isShown)
                    emojiMenuOpenListener.onEmojiMenuOpen();
                else
                    emojiMenuOpenListener.onEmojiMenuClose();
            }
        }
    };

    /**
     * 新手引导
     */
    private void showNewBieGuide()
    {
        if (SharePreUtils.getIsNewBieDownloadExpression(mAct))
        {
            NetHelper.checkIsShowNewbie(mAct, "4", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        //获取root节点
                        final FrameLayout fRoot = (FrameLayout) mAct.getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mAct, R.layout.view_newbie_guide_talk, null);
                        ImageView imageView = (ImageView) vGuide.findViewById(R.id.img_downloadExpression);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                fRoot.removeView(vGuide);
                                SharePreUtils.setIsNewBieDownloadExpression(mAct, false);
                                NetHelper.saveShowNewbieStatus(mAct, "4");
                            }
                        });
                        vGuide.setClickable(true);
                        fRoot.addView(vGuide);
                    }
                    else
                    {
                        SharePreUtils.setIsNewBieDownloadExpression(mAct, false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });

        }
    }

    /**
     * 发送游戏邀请
     */
    public void sendGameInvite()
    {
        EMMessage message = createGameMessage(toChatUserName, gameId, gameUrl);
        mPresenter.sendGameInvite(toEaseUser.getuId(), message);
    }


    private ISendVoiceTipViewPressListener sendVoiceTipListener;
    private IEmojiMenuOpenListener emojiMenuOpenListener;

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    public void clearInputFocus()
    {
        chatInputMenu.clearInputFocus();
    }

    public interface ISendVoiceTipViewPressListener
    {
        boolean onPressSendVoiceView(MotionEvent event);
    }

    public interface IEmojiMenuOpenListener
    {
        /**
         * emoji 展开
         */
        void onEmojiMenuOpen();

        /**
         * emoji 关闭
         */
        void onEmojiMenuClose();
    }

    public void setOnEmojiMenuOpenListener(IEmojiMenuOpenListener listener)
    {
        emojiMenuOpenListener = listener;
    }

    public void setISendVoiceTipViewListener(ISendVoiceTipViewPressListener listener)
    {
        sendVoiceTipListener = listener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d(requestCode + "," + resultCode);
        switch (resultCode)
        {
            case RESULT_OK:
                switch (requestCode)
                {
                    case EamConstant.EAM_OPEN_IMAGE_PICKER:
                        ArrayList<String> mResults = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        Logger.t(TAG).d("图片地址：" + mResults.get(0));
                        sendImageMessage(mResults.get(0), toChatUserName);
                        break;
                    case REQUEST_CODE_SEND_MONEY:
                        if (data != null)
                        {
                            if (!mAct.isFinishing())
                            {
                                ToastUtils.setGravity(Gravity.CENTER, 0, 0);
                                View view = LayoutInflater.from(mAct).inflate(R.layout.toast_ok_bg, null);
                                TextView tvSendContent = view.findViewById(R.id.toast_content);
                                tvSendContent.setText("红包发送成功");
                                ToastUtils.showCustomShortSafe(view);
                                ToastUtils.cancel();
                            }
                            makeMessageAttribute(RedPacketUtil.createRPMessage(getActivity(), data, toChatUserName));
                        }
                        break;
                    case REQUEST_CODE_MAP:
                        double latitude = data.getDoubleExtra("latitude", 0);
                        double longitude = data.getDoubleExtra("longitude", 0);
                        String locationAddress = data.getStringExtra("address");
                        String locationAddressName = data.getStringExtra("address_name");
                        if (!TextUtils.isEmpty(locationAddress))
                            sendLocationMessage(latitude, longitude, locationAddress, locationAddressName, toChatUserName);
                        else
                            ToastUtils.showShort("无法获取到您的位置信息!");
                        break;
                    case EamConstant.EAM_OPEN_RECORD_VIDEO:
                        String type = data.getStringExtra("type");
                        String showType = data.getStringExtra("showType");
                        String picUrl = data.getStringExtra("picUrl");
                        String videoUrl = data.getStringExtra("videoUrl");
                        String thumbnail = data.getStringExtra("thumbnail");//首帧缩略图
                        long videoTime = data.getLongExtra("videoTime", 0);
                        int duration = (int) Math.ceil(((double) videoTime / 1000));
                        if ("pic".equals(type))
                        {
                            sendImageMessage(picUrl, toChatUserName);
                        }
                        else
                        {
                            sendVideoMessage(videoUrl, thumbnail, duration, showType, toChatUserName);
                        }
                        break;
                    //添加emoj表情
                    case EamConstant.REQUEST_CODE_AND_EMOJI:
//                        for (EmojiconGroupEntity e : EmojiconExampleGroupData.emojEntities(getActivity()))
//                        {
//                            if (!((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).isContainEmojiconGroup(e))
//                                ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(e);
//                        }

                        ArrayList<String> emojiData = data.getStringArrayListExtra("emojiNames");
                        Logger.t(TAG).d("onActivityResult:  code:" + EamConstant.REQUEST_CODE_AND_EMOJI + " 返回表情" + emojiData);
                        if (emojiData != null && emojiData.size() != 0)
                        {
                            for (String s : emojiData)
                            {
                                //添加emoj表情
                                for (EmojiGroupEntity e : EmojiconBigExpressionData.emojEntities(getActivity(), s))
                                {
                                    if (!chatInputMenu.getChatEmojiMenu().isContainEmojiconGroup(e))
                                        chatInputMenu.getChatEmojiMenu().addEmojiconGroup(e);
                                }
                            }
                        }
                        break;
                    case EamCode4Result.reQ_CChatActivity:
                        String action = data.getStringExtra("action");
                        switch (action)
                        {
                            case CNewUserInfoAct.ACTION_FOCUS:
                                hideFocusTip();
                                break;
                            case CNewUserInfoAct.ACTION_BLACK:
                                getActivity().finish();
                                break;
                            case CNewUserInfoAct.ACTION_REMARK:
                                Map<String, String> map = new ArrayMap<>();
                                map.put(EamConstant.EAM_CHAT_ATTR_REMARK, toEaseUser.getRemark());
                                sendCMDMsg(EamConstant.EAM_CHAT_CHANGE_REMARK_NOTIFY, toChatUserName, map);
                                chatMsgList.refreshSelectLast();
                                break;
                            case CNewUserInfoAct.ACTION_FOCUS_REMARK:
                                hideFocusTip();
                                chatMsgList.refreshSelectLast();
                                break;
                        }
                        break;
                    case EamCode4Result.reQ_Forward_Message:
                        isForward = true;
                        saveStatesMap.clear();
                        saveStatesMap.put("remark", remark);
                        saveStatesMap.put("isSayHello", isSayHello);
                        saveStatesMap.put("inBlack", inBlack);
                        saveStatesMap.put("isFirstSendIsSayHelloMsg", isFirstSendIsSayHelloMsg);
                        saveStatesMap.put("toEaseUser", toEaseUser);
                        EaseUser user = data.getParcelableExtra("easeUser");
                        if (user != null)
                        {
                            toEaseUser = user;
                            mPresenter.queryForwardUsersRelationShip(toEaseUser.getuId());
                        }
                        break;
                }
                break;
            case Activity.RESULT_CANCELED:
                break;
        }
    }

    private void sendRemark2Other()
    {

    }

    /**
     * 发送文本消息
     *
     * @param content        发送内容
     * @param toChatUserName 发送对象
     */
    private void sendTextMessage(String content, String toChatUserName)
    {
//        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUserName);
//        makeMessageAttribute(message);

        Map<String, Object> map = CommonUtils.checkSensitiveWord(content, true);
        EMMessage message;
        String newWords;
        boolean isContains = (boolean) map.get("isContains");
        if (isContains)
        {
            newWords = map.get("newWords").toString();
            message = EMMessage.createTxtSendMessage(newWords, toChatUserName);
        }
        else
            message = EMMessage.createTxtSendMessage(content, toChatUserName);
        if (isContains)
            message.setAttribute(Constant.MESSAGE_ATTR_SENSITIVE_CONTENT, content);
        makeMessageAttribute(message);
    }

    /**
     * 发送 直播、游戏分享等 转发消息
     *
     * @param message 要转发消息
     */
    private void sendForwardTextCustomMessage(EMMessage message)
    {
        makeMessageAttribute(message);
    }

    /**
     * 发送小海疼 作为大表情消息
     *
     * @param emojicon       emoji实体
     * @param toChatUserName 发送对象
     */
    private void sendEmojiNormalAsExpressMessage(EmojiIcon emojicon, String toChatUserName)
    {
        EMMessage message = EMMessage.createTxtSendMessage(emojicon.getEmojiText(), toChatUserName);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_EXPRESSION_NAME, emojicon.getIdentityCode());
        makeMessageAttribute(message);
    }

    /**
     * 发送大表情
     *
     * @param name           表情名
     * @param identityCode   表情code
     * @param url            大表情url
     * @param toChatUserName 发送对象
     */
    protected void sendBigExpressionMessage(String name, String identityCode, String url, String toChatUserName)
    {
        EMMessage message = ChatCommonUtils.createExpressionMessage(toChatUserName, name, identityCode, url);
        makeMessageAttribute(message);
    }

    /**
     * 发送语音消息
     *
     * @param filePath       语音路径
     * @param length         语音时长
     * @param toChatUserName 发送对象
     */
    protected void sendVoiceMessage(String filePath, int length, String toChatUserName)
    {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUserName);
        makeMessageAttribute(message);
    }

    /**
     * 发送图片消息
     *
     * @param imagePath      图片路径
     * @param toChatUserName 发送对象
     */
    protected void sendImageMessage(String imagePath, String toChatUserName)
    {
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUserName);
        makeMessageAttribute(message);
    }

    /**
     * 发送位置信息
     *
     * @param latitude            维度
     * @param longitude           经度
     * @param locationAddress     详细地址
     * @param locationAddressName 地址名
     * @param toChatUserName      发送对象
     */
    protected void sendLocationMessage(double latitude, double longitude, String locationAddress, String locationAddressName, String toChatUserName)
    {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUserName);
        message.setAttribute(EamConstant.MESSAGE_ATTR_ADDRESS_NAME, locationAddressName);
        message.setAttribute(EamConstant.MESSAGE_ATTR_DETAIL_ADDRESS, locationAddress);
        makeMessageAttribute(message);
    }

    /**
     * 发送视频消息
     *
     * @param videoUrl       视频本地路径
     * @param thumbPath      首帧图
     * @param videoLength    视频时长
     * @param showType       横竖屏标识
     * @param toChatUserName 发送对象
     */
    protected void sendVideoMessage(String videoUrl, String thumbPath, int videoLength, String showType, String toChatUserName)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(thumbPath, options);
        Logger.t(TAG).d("图片宽高:" + options.outHeight + " | " + options.outWidth);

        Logger.t(TAG).d("chat------>videoUrl:" + videoUrl + " | thumbPath:" + thumbPath + " | videoLength:" + videoLength + " | showType:" + showType);
        EMMessage message = EMMessage.createVideoSendMessage(videoUrl, thumbPath, videoLength, toChatUserName);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_VIDEO_TYPE, showType);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_VIDEO_THUMBNAIL_WIDTH, options.outWidth);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_VIDEO_THUMBNAIL_HEIGHT, options.outHeight);
        makeMessageAttribute(message);
    }

    /**
     * 创建合到十消息
     *
     * @param toChatUserName 发送对象
     * @param gameId         消息ID
     * @param gameUrl        消息Url
     * @return 合到十消息
     */
    private EMMessage createGameMessage(String toChatUserName, String gameId, String gameUrl)
    {
        return ChatCommonUtils.createGameMessage(toChatUserName, gameMsgTitle, gameMsgDesc, gameId, gameUrl);
    }

    /**
     * 发送游戏邀请消息
     *
     * @param message
     */
    private void sendGameMessage(EMMessage message)
    {
        makeMessageAttribute(message);
    }

    /**
     * 发送文件
     *
     * @param filePath       文件路径
     * @param toChatUserName 发送对象
     */
    protected void sendFileMessage(String filePath, String toChatUserName)
    {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUserName);
        makeMessageAttribute(message);
    }

    /**
     * 转发时 下载为看过的视频
     *
     * @param message
     */
    private void downloadVideo(EMMessage message)
    {
//        ToastUtils.showShort("转发成功");
        message.setMessageStatusCallback(new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                EMVideoMessageBody videoMessageBody = (EMVideoMessageBody) message.getBody();
                String videoUrl = videoMessageBody.getLocalUrl();
                String thumbPath = videoMessageBody.getLocalThumb();
                int videoLength = videoMessageBody.getDuration();
                String showType = contextMenuMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_VIDEO_TYPE, "");
                sendVideoMessage(videoUrl, thumbPath, videoLength, showType, toEaseUser.getUsername());
//                mAct.runOnUiThread(() -> ToastUtils.showShort("转发成功"));
                Logger.t(TAG).d("chat------>转发没有看过的视频下载成功");
            }

            @Override
            public void onProgress(final int progress, String status)
            {
            }

            @Override
            public void onError(int error, String msg)
            {
                mAct.runOnUiThread(() -> ToastUtils.showShort("转发失败，请重试!"));
            }
        });
        EMClient.getInstance().chatManager().downloadAttachment(message);
    }


    /**
     * 转发消息
     */
    private void sendForwardMessage()
    {
        EMMessage.Type type = contextMenuMessage.getType();
        if (EMMessage.Type.TXT == type)
        {
            if (contextMenuMessage.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false))
            {
                String name = ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage();
                String id = contextMenuMessage.getStringAttribute(Constant.MESSAGE_ATTR_EXPRESSION_ID, "");
                String url = contextMenuMessage.getStringAttribute(Constant.MESSAGE_ATTR_EXPRESSION_URL, "");
                sendBigExpressionMessage(name, id, url, toEaseUser.getUsername());
            }
            else if (!TextUtils.isEmpty(contextMenuMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_EXPRESSION_NAME, "")))
            {
                String emojiText = ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage();
                String emojiId = contextMenuMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_EXPRESSION_NAME, "");
                EmojiIcon emojicon = new EmojiIcon();
                emojicon.setEmojiText(emojiText);
                emojicon.setIdentityCode(emojiId);
                sendEmojiNormalAsExpressMessage(emojicon, toEaseUser.getUsername());
            }
            else if (contextMenuMessage.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_LIVE_SHARE, false))
            {
                EMTextMessageBody textBody = (EMTextMessageBody) contextMenuMessage.getBody();
                String shareType = contextMenuMessage.getStringAttribute("shareType", "");
                EMMessage message = EMMessage.createTxtSendMessage(textBody.getMessage(), toEaseUser.getUsername());
                message.setAttribute("shareType", shareType);
                message.setAttribute(Constant.MESSAGE_ATTR_IS_LIVE_SHARE, true);
                if ("liveShare".equals(shareType))//直播分享
                {
                    String roomId = contextMenuMessage.getStringAttribute("roomId", "");
                    String nicName = contextMenuMessage.getStringAttribute("nicName", "");
                    String roomName = contextMenuMessage.getStringAttribute("RoomName", "");
                    String roomUrl = contextMenuMessage.getStringAttribute("roomUrl", "");
                    message.setAttribute("roomId", roomId);
                    message.setAttribute("nicName", nicName);
                    message.setAttribute("roomName", roomName);
                    message.setAttribute("roomUrl", roomUrl);
                }
                message.setAttribute("shareTitle", contextMenuMessage.getStringAttribute("shareTitle", ""));
                message.setAttribute("shareContent", contextMenuMessage.getStringAttribute("shareContent", ""));
                message.setAttribute("shareImageUrl", contextMenuMessage.getStringAttribute("shareImageUrl", ""));
                message.setAttribute("shareUrl", contextMenuMessage.getStringAttribute("shareUrl", ""));
                message.setAttribute("gameId", contextMenuMessage.getStringAttribute("gameId", ""));
                message.setAttribute("columnId", contextMenuMessage.getStringAttribute("columnId", ""));
                message.setAttribute("activityId", contextMenuMessage.getStringAttribute("activityId", ""));
                message.setAttribute("phId", contextMenuMessage.getStringAttribute("phId", ""));
                sendForwardTextCustomMessage(message);
            }
            else if (contextMenuMessage.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_GAME_MESSAGE, false))
            {
                String gameMsgTitle = contextMenuMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_TITLE, "");
                String gameMsgDesc = contextMenuMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_DES, "");
                String gameId = contextMenuMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, "");
                String gameUrl = contextMenuMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, "");
                EMMessage gameMessage = ChatCommonUtils.createGameMessage(toEaseUser.getUsername(), gameMsgTitle, gameMsgDesc, gameId, gameUrl);
                sendGameMessage(gameMessage);
            }
            else
            {
                //发送文本
                String content = ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage();
                if (contextMenuMessage.direct() == EMMessage.Direct.SEND)
                {
                    String sensitiveContent = contextMenuMessage.getStringAttribute(Constant.MESSAGE_ATTR_SENSITIVE_CONTENT, "");
                    if (!TextUtils.isEmpty(sensitiveContent))
                    {
                        content = sensitiveContent;
                    }
                }
                sendTextMessage(content, toEaseUser.getUsername());
            }
        }
        else if (EMMessage.Type.IMAGE == type)
        {
            // send image
            EMImageMessageBody imageMessageBody = (EMImageMessageBody) contextMenuMessage.getBody();
            Observable.create(new ObservableOnSubscribe<String>()
            {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception
                {
                    File file = GlideApp.with(EamApplication.getInstance())
                            .load(TextUtils.isEmpty(imageMessageBody.getThumbnailUrl()) ? imageMessageBody.getRemoteUrl() : imageMessageBody.getThumbnailUrl())
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    String filePath = file.getPath();
                    String destFilePath = NetHelper.getRootDirPath(mAct) + NetHelper.DOWNLOAD_IMAGE_FOLDER + "eam_copy_glide_" + System.currentTimeMillis() + ".jpg";
                    boolean result = FileUtils.copyFile(filePath, destFilePath);
                    Logger.t(TAG).d("forwardMsg------>image:filePath:" + filePath + " | destFilePath:" + destFilePath);
                    if (result)
                        e.onNext(destFilePath);
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>()
                    {
                        @Override
                        public void accept(String filePath) throws Exception
                        {
                            sendImageMessage(filePath, toEaseUser.getUsername());
                        }
                    });
        }
        else if (EMMessage.Type.VOICE == type)
        {
            EMVoiceMessageBody voiceMessageBody = ((EMVoiceMessageBody) contextMenuMessage.getBody());
            String path = voiceMessageBody.getLocalUrl();
            int length = voiceMessageBody.getLength();
            Logger.t(TAG).d("forwardMsg------>voice:path:" + path + " | length:" + length);
            sendVoiceMessage(path, length, toEaseUser.getUsername());
        }
        else if (EMMessage.Type.VIDEO == type)
        {
            EMVideoMessageBody videoMessageBody = (EMVideoMessageBody) contextMenuMessage.getBody();
            String videoUrl = videoMessageBody.getLocalUrl();
            String thumbPath = videoMessageBody.getLocalThumb();
            int videoLength = videoMessageBody.getDuration();
            String showType = contextMenuMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_VIDEO_TYPE, "");
            Logger.t(TAG).d("forwardMsg------>video:videoUrl:" + videoUrl + " | thumbPath:" + thumbPath + " | videoLength:" + videoLength + " | showType:" + showType);

            File var4 = new File(videoUrl);
            if (var4.exists())
            {
                sendVideoMessage(videoUrl, thumbPath, videoLength, showType, toEaseUser.getUsername());
            }
            else
                downloadVideo(contextMenuMessage);
        }
        else if (EMMessage.Type.LOCATION == type)
        {
            EMLocationMessageBody body = (EMLocationMessageBody) contextMenuMessage.getBody();
            double latitude = body.getLatitude();
            double longitude = body.getLongitude();
            String locationAddress = contextMenuMessage.getStringAttribute(EamConstant.MESSAGE_ATTR_ADDRESS_NAME, "");
            String locationAddressName = contextMenuMessage.getStringAttribute(EamConstant.MESSAGE_ATTR_DETAIL_ADDRESS, "");
            sendLocationMessage(latitude, longitude, locationAddress, locationAddressName, toEaseUser.getUsername());
        }
    }

    /**
     * 构建消息体内容
     *
     * @param message 具体消息
     */
    private void makeMessageAttribute(EMMessage message)
    {

        if (message == null)
        {
            return;
        }
        if (chatType == Constant.CHATTYPE_GROUP)
        {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        else if (chatType == Constant.CHATTYPE_CHATROOM)
        {
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }
        //判断是否 接受过消息
        if (conversation.getLatestMessageFromOthers() != null && !isForward)
        {
            setIsSayHello("0");
        }
        if (chatMsgList != null)
        {
            if (!chatMsgList.isAnimatorOpen())
                chatMsgList.openDefaultAnimator();
        }

        Logger.t(TAG).d("chat------>发送时消息id:" + message.getMsgId());

        message.setAttribute(EamConstant.EAM_CHAT_ATTR_UID, SharePreUtils.getUId(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_ID, SharePreUtils.getId(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_DEVICE_TYPE, EamConstant.DEVICE_TYPE);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, SharePreUtils.getNicName(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE, SharePreUtils.getHeadImg(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_LEVEL, SharePreUtils.getLevel(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_GENDER, SharePreUtils.getSex(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_AGE, SharePreUtils.getAge(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_VUSER, SharePreUtils.getIsVUser(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
        Logger.t(TAG).d("是否为打招呼：" + "1".equals(isSayHello));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, "1".equals(isSayHello));

        if ("1".equals(inBlack) || "3".equals(inBlack)) //对方把我拉黑  或者 双方互相拉黑
        {
            if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false))
            {
                if ("1".equals(isSayHello) && isFirstSendIsSayHelloMsg)
                {
                    mPresenter.sendFirstTalk(toEaseUser.getuId(), message);
                    mPresenter.sendSayHello(toEaseUser.getuId());
                    isFirstSendIsSayHelloMsg = false;
                }
                else
                {
                    sendMessage(message, false);
                }
            }
            else
            {
                sendMessage(message, true);
            }

        }
        else
        {
            if ("1".equals(isSayHello) && isFirstSendIsSayHelloMsg)
            {
                mPresenter.sendFirstTalk(toEaseUser.getuId(), message);
                mPresenter.sendSayHello(toEaseUser.getuId());
                isFirstSendIsSayHelloMsg = false;
            }
            else
            {
                if (isFirstSendIsSayHelloMsg)
                {
                    mPresenter.sendFirstTalk(toEaseUser.getuId(), message);
                    isFirstSendIsSayHelloMsg = false;
                }
                else
                {
                    sendMessage(message, false);
                }
            }
        }

        if (!"1".equals(isSayHello) && !isForward)
        {
            EMMessage lastMsgOther = conversation.getLatestMessageFromOthers();
            if (lastMsgOther != null)
            {
                lastMsgOther.setAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false);
                conversation.updateMessage(lastMsgOther);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param message  已经构建后的消息
     * @param isAppend 是否为附加消息(根据拉黑情况)
     */
    private void sendMessage(EMMessage message, boolean isAppend)
    {
        if (getActivity() instanceof CChatActivity && !isForward)
            ((CChatActivity) getActivity()).isDeleteUser = false;
        if (isAppend)
        {
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, true);
            if (!isForward)
                conversation.appendMessage(message);
            else
            {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toEaseUser.getUsername(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                if (conversation != null)
                    conversation.appendMessage(message);
            }
        }
        else
        {
            message.setMessageStatusCallback(new EMCallBack()
            {
                @Override
                public void onSuccess()
                {
                    getActivity().runOnUiThread(() ->
                    {
                        Logger.t(TAG).d("chat------>发送成功时消息id:" + message.getMsgId());
                        if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_GAME_MESSAGE, false))
                        {
                            // 如果是游戏邀请消息 那就 去调用保存消息id的接口
                            mPresenter.saveMessageId(toEaseUser.getuId(), message.getMsgId());
                        }
                    });
                }

                @Override
                public void onError(int i, String s)
                {
                    Logger.t(TAG).d("chat------>发送失败时消息id:" + message.getMsgId());
                }

                @Override
                public void onProgress(int i, String s)
                {
                }
            });
            EMClient.getInstance().chatManager().sendMessage(message);
            Logger.t(TAG).d("chat------>发送调完时消息id:" + message.getMsgId());
            Logger.t(TAG).d("chat------>发送调完时消息状态:" + message.status());
        }
        if (!isForward)
        {
            if (chatMsgList != null)
                chatMsgList.refreshLast(message);
            new Handler().postDelayed(() ->
            {
                if (chatMsgList != null)
                    chatMsgList.selectLast();
            }, 1000);
            sendMsgCount++;
            showTalkTip();
        }
        else
        {
            //转发的人 如果是 我正在聊天的人  就去做 发送逻辑
            if (toChatUserName.equals(message.getTo()))
            {
                if (chatMsgList != null)
                    chatMsgList.refreshLast(message);
                sendMsgCount++;
                showTalkTip();
            }

            remark = saveStatesMap.get("remark").toString();
            isSayHello = saveStatesMap.get("isSayHello").toString();
            inBlack = saveStatesMap.get("inBlack").toString();
            isFirstSendIsSayHelloMsg = (boolean) saveStatesMap.get("isFirstSendIsSayHelloMsg");
            toEaseUser = (EaseUser) saveStatesMap.get("toEaseUser");
            isForward = false;
            ToastUtils.showShort("发送成功");
        }
    }

    /**
     * 检查conversation中是否新增消息了，然后进行刷新数据
     */
    public void checkMessageHasChanged()
    {
        if (!isNeed2RefreshData)
        {
            isNeed2RefreshData = true;
            return;
        }
        if (conversation == null || chatMsgList == null)
            return;
        EMMessage lastMessage = conversation.getLastMessage();
        EMMessage dataLastMessage = chatMsgList.getLastMessage();
        if (lastMessage != null && dataLastMessage != null)
        {
            Logger.t(TAG).d("chat------>消息ID是否相同：" + lastMessage.getMsgId() + " | " + dataLastMessage.getMsgId()
                    + " | equal:" + lastMessage.getMsgId().equals(dataLastMessage.getMsgId()));
            if (!lastMessage.getMsgId().equals(dataLastMessage.getMsgId()))
            {
                chatMsgList.refreshNewData();
            }
        }
    }

    /**
     * 消息 发送 失败 重发
     *
     * @param message 消息体
     */
    private void handleSendMessage(EMMessage message)
    {
        message.setMessageStatusCallback(new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                Logger.t(TAG).d("chat------>消息重发成功");
                mAct.runOnUiThread(() -> chatMsgList.notifyMessage(message.getMsgId()));

            }

            @Override
            public void onError(int code, String error)
            {
                Logger.t(TAG).d("chat------>消息重发失败");
                mAct.runOnUiThread(() -> chatMsgList.notifyMessage(message.getMsgId()));
            }

            @Override
            public void onProgress(int progress, String status)
            {
//                Logger.t(TAG).d("chat------>消息重发正在发送中");
            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    boolean isShowTip = true;

    /**
     * 判断是否达到显示聊天订餐提示要求
     */
    private void showTalkTip()
    {
        if (isShowTip)
        {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(System.currentTimeMillis());
            int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            int mMinuts = mCalendar.get(Calendar.MINUTE);

            Logger.t(TAG).d("时间：hour:" + mHour + " | 分钟：" + mMinuts +
                    "次数：sendMsgCount:" + sendMsgCount +
                    " | receiveMsgCount：" + receiveMsgCount);
            if (mHour >= 11 && mHour < 13 || mHour >= 17 && mHour < 20)
            {
                if (sendMsgCount > 9 && receiveMsgCount > 9)
                {
                    showBookRestaurantTip();
                    isShowTip = false;
                }
            }
            else
            {
                if (sendMsgCount > 29 && receiveMsgCount > 29)
                {
                    showBookRestaurantTip();
                    isShowTip = false;
                }
            }
        }
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
     * 添加临时voice message
     */
    private void sendVoiceTempMessage()
    {
        //region 注释代码
        /*EMMessage message = EMMessage.createTxtSendMessage("111", toChatUserName);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_UID, SharePreUtils.getUId(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_ID, SharePreUtils.getId(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_DEVICE_TYPE, EamConstant.DEVICE_TYPE);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, SharePreUtils.getNicName(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE, SharePreUtils.getHeadImg(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_LEVEL, SharePreUtils.getLevel(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_GENDER, SharePreUtils.getSex(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_AGE, SharePreUtils.getAge(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, true);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_VOICE_TEMP_MESSAGE, true);
        message.setMsgId(EamConstant.MESSAGE_ATTR_VOICE_TEMP_ID);
        conversation.appendMessage(message);
        chatMsgList.addVoiceTempMessage(toChatUserName,remark);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                chatMsgList.refreshLast();
            }
        }, 300);*/
        //endregion
        chatMsgList.addVoiceTempMessage(toChatUserName, remark);
    }

    /**
     * 移除临时message
     */
    public void removeVoiceTempMessage()
    {
//        conversation.removeMessage(EamConstant.MESSAGE_ATTR_VOICE_TEMP_ID);
        chatMsgList.removeLastItem();
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }


    /**
     * init conversation.
     */
    protected void onConversationInit()
    {
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUserName, ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
        if (conversation == null)
            return;
        List<EMMessage> messageList = conversation.getAllMessages();
        for (EMMessage message : messageList)
        {
            //语音和视频要 听 或看 之后才算已读
            if (message.getType() == EMMessage.Type.VOICE || message.getType() == EMMessage.Type.VIDEO)
                EaseCommonUtils.makeMessageAsRead(TAG + ".onConversationInit", conversation, message, false);
            else
                EaseCommonUtils.makeMessageAsRead(TAG + ".onConversationInit", conversation, message, true);
        }
        // conversation.getAllMessages 默认加载1条消息所以从会话默认加载20条
//        final List<EMMessage> msgs = conversation.getAllMessages();
//        int msgCount = msgs != null ? msgs.size() : 0;//3
//        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize)
//        {
//            String msgId = null;
//            if (msgs != null && msgs.size() > 0)
//            {
//                msgId = msgs.get(0).getMsgId();
//            }
//            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
//        }
        int allMsgUnReadCount = EMClient.getInstance().chatManager().getUnreadMessageCount();
        EamApplication.getInstance().msgCount = allMsgUnReadCount + "";
    }

    /**
     * Refresh the list data.
     */
    public void refreshData()
    {
        if (chatMsgList != null)
            chatMsgList.refreshSelectLast();
    }

    /**
     * the message received listener
     */
    EMMessageListener msgListener = new AbstractEMMessageListener()
    {
        @Override
        public void onMessageReceived(List<EMMessage> messages)
        {
            try
            {
                super.onMessageReceived(messages);
                if (chatMsgList != null)
                {
                    if (!chatMsgList.isAnimatorOpen())
                        chatMsgList.openDefaultAnimator();
                }
                for (EMMessage message : messages)
                {
                    String username = message.getFrom();//15111693755250011
                    // if the message is for current conversation
                    Logger.t(TAG).d("username:" + username + " | toChatUserName:" + toChatUserName + " | message:" + message);
                    Logger.t(TAG).d("chat------>接收时消息id：" + message.getMsgId());
                    if (username.equals(toChatUserName))
                    {
                        mAct.runOnUiThread(() ->
                        {
                            if (chatMenu != null)
                                chatMenu.hidePopupListWindow();
                        });
                        if (getActivity() instanceof CChatActivity)
                        {
                            if (((CChatActivity) getActivity()).isDeleteUser) //如果删除记录就会 删掉 conversation 所以重新 获取一下
                            {
                                conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat, true);
                            }
                        }
                        if (message.getType() != EMMessage.Type.VOICE && message.getType() != EMMessage.Type.VIDEO)
                        {
                            EaseCommonUtils.makeMessageAsRead(TAG + ".onMessageReceived", conversation, message, true);
                        }
                        else
                        {
                            EaseCommonUtils.makeMessageAsRead(TAG + ".onMessageReceived", conversation, message, false);
                        }
                        chatMsgList.refreshLast(message);
                        receiveMsgCount++;
                        if (getActivity() instanceof CChatActivity)
                            ((CChatActivity) getActivity()).isDeleteUser = false;
                        setIsSayHello("0");
                        showTalkTip();
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
            super.onCmdMessageReceived(messages);
            if (chatMsgList != null)
            {
                if (!chatMsgList.isAnimatorOpen())
                    chatMsgList.openDefaultAnimator();
            }
            for (EMMessage message : messages)
            {
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                Logger.t(TAG).d("chat------>cmd消息：" + cmdMsgBody.action());
                switch (cmdMsgBody.action())
                {
                    case EamConstant.EAM_CHAT_INBLACK_NOTIFY:
                        if ("0".equals(inBlack)) //双方 都没拉黑 状态下  改为 对方把我拉黑了
                            setInBlack("1");
                        else if ("2".equals(inBlack)) //我的黑名单里有他 的状态下 改为 双方互拉黑
                            setInBlack("3");
                        break;
                    case EamConstant.EAM_CHAT_OUTBLACK_NOTIFY:
                        if ("1".equals(inBlack))
                            setInBlack("0");
                        else if ("3".equals(inBlack))
                            setInBlack("2");
                        break;
                    case EamConstant.EAM_CHAT_CLEAR_MSG_COUNT_NOTIFY:
                        sendMsgCount = 0;
                        receiveMsgCount = 0;
                        break;
                    case EamConstant.EAM_CHAT_RECALL_MSG_NOTIFY:
                        mAct.runOnUiThread(() ->
                        {
                            if (chatMenu != null)
                                chatMenu.hidePopupListWindow();
                        });
                        String messageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_RECALL_MSG_ID, "");
                        EMMessage emMessage = EMClient.getInstance().chatManager().getMessage(messageId);
                        EMMessage recallMsg = ChatCommonUtils.markAsRecallMessage(emMessage, false);
                        conversation.updateMessage(recallMsg);
                        chatMsgList.recallMessage(messageId, recallMsg);
                        break;
                    case EamConstant.EAM_CHAT_CHANGE_REMARK_NOTIFY:
                        String remark = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, "");
                        if (!TextUtils.isEmpty(remark))
                        {
                            setRemark(remark);
                        }
                        break;
                    case EamConstant.EAM_CHAT_ACCEPT_GAME_NOTIFY://接收到游戏邀请透传
                    {
                        String gameMessageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
                        String gameMessageState = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, "");
                        String matchId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MATCH_ID, "");
                        Logger.t(TAG).d("chat------>接收到接受游戏邀请透传：gameMessageId：" + gameMessageId + " | gameMessageState:" + gameMessageState
                                + " | matchId:" + matchId + " | getFrom:" + message.getFrom() + " | toChatUserName:" + toChatUserName);
                        EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
                        if (gameMsg != null)
                        {
                            gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, gameMessageState);
                            conversation.updateMessage(gameMsg);
                            chatMsgList.refreshData(gameMsg);
                        }
                        // 需要调 Merge10C/refuseList 通知 我邀请的其他人 我已经对战了
                        if (message.getFrom().equals(toChatUserName))
                        {
                            if (!CommonUtils.isAppOnForeground(mAct))//在后台
                            {
                                //显示推送，不进游戏
//                                EMMessage message1 = EMMessage.createTxtSendMessage("接受游戏邀请", toChatUserName);
//                                message中附带的内容
//                                map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
//                                map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, message.getMsgId());
//                                map.put(EamConstant.EAM_CHAT_ATTR_GAME_ID, message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, ""));
//                                map.put(EamConstant.EAM_CHAT_ATTR_GAME_URL, message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, ""));
//                                map.put(EamConstant.EAM_CHAT_ATTR_NICKNAME, SharePreUtils.getNicName(mAct));
//                                map.put(EamConstant.EAM_CHAT_ATTR_UID, SharePreUtils.getUId(mAct));
//                                map.put(EamConstant.EAM_CHAT_ATTR_GAME_MATCH_ID, matchId);
//                                message1.setAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, ""));
//                                message1.setAttribute(EamConstant.EAM_CHAT_ATTR_UID, message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, ""));
//                                HuanXinIMHelper.getInstance().getNotifier().onNewMsg(message);
                                String nicName = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, "");
                                String uid = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, "");

                                EaseUser user = new EaseUser(message.getFrom());
                                user.setNickName(nicName);
                                user.setuId(uid);
                                if (!CommonUtils.isInLiveRoom)
                                    HuanXinIMHelper.getInstance().showInChatNotification(mAct, user);

                                isReceivedAcceptMsg = true;
                                inBackMatchId = matchId;
                                inBackMessage = message;
                                inBackRecMsgTime = System.currentTimeMillis();
                            }
                            else
                            {
                                if (CommonUtils.isInLiveRoom)
                                {
                                    long dialogShowTime = System.currentTimeMillis();
                                    mAct.runOnUiThread(() ->
                                            new CustomAlertDialog(mAct)
                                                    .builder()
                                                    .setTitle("提示 !")
                                                    .setMsg(getString(R.string.chat_game_live_tips))
                                                    .setCancelable(false)
                                                    .setPositiveButton("确认", (view) ->
                                                    {
                                                        if (System.currentTimeMillis() - dialogShowTime < 1000 * 15)
                                                        {
                                                            mPresenter.queryAnotherInvite(matchId, message);
                                                            Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_CLOSE_LIVE);
                                                            mAct.sendBroadcast(intent);
                                                        }
                                                        else
                                                            ToastUtils.showShort("游戏邀请已过期。");
                                                    })
                                                    .setNegativeButton("取消", (view) ->
                                                    {
                                                    })
                                                    .show());
                                }
                                else
                                    mPresenter.queryAnotherInvite(matchId, message);
                            }
                        }
                        else
                        {
                            if (!CommonUtils.isInLiveRoom)
                            {
                                //  在聊天当中收到别人的游戏同意消息  显示 去对战弹窗
                                mAct.runOnUiThread(() -> HuanXinIMHelper.getInstance().showGameInviteDialog(mAct,
                                        false, matchId, message, null));
                            }
                        }
                        break;
                    }
                    case EamConstant.EAM_CHAT_REFUSE_GAME_NOTIFY://拒绝游戏邀请透传
                        String gameMessageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
                        String gameMessageState = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, "");
                        EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
                        Logger.t(TAG).d("chat------>收到拒绝透传 消息id：" + gameMessageId
                                + " | gameMsg:" + gameMsg + " | gameMessageState:" + gameMessageState);
                        gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, gameMessageState);
                        conversation.updateMessage(gameMsg);
                        chatMsgList.refreshData(gameMsg);
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onMessageRead(List<EMMessage> messages)
        {
            super.onMessageRead(messages);

            if (isMessageListInited)
            {
                for (final EMMessage message : messages)
                {
                    Logger.t(TAG).d("++++++++++>>>>message:" + message);
                    mAct.runOnUiThread(() -> chatMsgList.notifyMessage(message.getMsgId()));
                }
//                chatMsgList.refresh();
            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages)
        {
            super.onMessageDelivered(messages);
            if (isMessageListInited)
            {
                for (final EMMessage message : messages)
                {
                    mAct.runOnUiThread(() -> chatMsgList.notifyMessage(message.getMsgId()));
                }
//                chatMsgList.refresh();
            }
        }
    };

    /**
     * 当在此页面切后台收到游戏同意后 ，回来跳转 游戏页面
     */
    public void goGameAct()
    {
        if (!TextUtils.isEmpty(inBackMatchId) || inBackMessage != null)
        {
            if (System.currentTimeMillis() - inBackRecMsgTime < 1000 * 15)
            {
                mPresenter.queryAnotherInvite(inBackMatchId, inBackMessage);
            }
            else
            {
                String gameMessageId = inBackMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
                String matchId = inBackMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MATCH_ID, "");
                Logger.t(TAG).d("chat------>接收到接受游戏邀请透传：gameMessageId：" + gameMessageId + " | matchId:" + matchId + " | getFrom:" + inBackMessage.getFrom() + " | toChatUserName:" + toChatUserName);
                EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
                gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
                conversation.updateMessage(gameMsg);
                chatMsgList.refreshData(gameMsg);
//                ToastUtils.showShort("游戏已过期！");
            }
        }
        inBackMatchId = "";
        inBackMessage = null;
    }

    /**
     * 通过  通知点击  进行 显示的 dialog
     */
    public void showGoFightDialog(Bundle bundle)
    {
        if (bundle == null)
            return;
        Map<String, Object> map = (Map<String, Object>) bundle.getSerializable("moreValues");
        if (map == null)
            return;
        for (String s : map.keySet())
        {
            Logger.t(TAG).d("chat------>BaseActivity showGoFightDialog Intent'extra:" + s + " | " + map.get(s));
        }
        Logger.t(TAG).d("chat------>BaseActivity showGoFightDialog:" + map + " | extra:" + map.get("nicName"));

        String nicName = map.get("nicName").toString();
        if (!TextUtils.isEmpty(nicName))
        {
            String matchId = map.get("matchId").toString();
            String uid = map.get("uid").toString();
            String remark = map.get("remark").toString();
            String gameId = map.get("gameId").toString();
            String gameUrl = map.get("gameUrl").toString();
            String gameMessageId = map.get("gameMessageId").toString();
            boolean isBeInvited = (boolean) map.get("isBeInvited");

            EMMessage message = EMMessage.createTxtSendMessage("aaa", "aaa");
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, nicName);
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_UID, uid);
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, gameId);
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, gameUrl);
            mPresenter.queryAnotherInvite(matchId, message);

            EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
            if (gameMsg != null)
            {
                gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                conversation.updateMessage(gameMsg);
            }

            HuanXinIMHelper.getInstance().dismissGameInviteDialog();

//            if (System.currentTimeMillis() - inBackRecMsgTime < 1000 * 15)
//            {
//                mPresenter.queryAnotherInvite(inBackMatchId, inBackMessage);
//            }
//            else
//            {
//                String gameMessageId = inBackMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
//                String matchId = inBackMessage.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MATCH_ID, "");
//                Logger.t(TAG).d("chat------>接收到接受游戏邀请透传：gameMessageId：" + gameMessageId + " | matchId:" + matchId + " | getFrom:" + inBackMessage.getFrom() + " | toChatUserName:" + toChatUserName);
//                EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
//                gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_OVERDUE);
//                conversation.updateMessage(gameMsg);
//                chatMsgList.refreshData(gameMsg);
//                ToastUtils.showShort("游戏已过期！");
//            }

//            HuanXinIMHelper.getInstance().showGameInviteDialog(mAct, isBeInvited, matchId, message);
        }
    }

    /**
     * 如果是从 推送点击的 进来后 若 过 15秒 就改过期
     */
    private void changeMessageStat()
    {
        if (HuanXinIMHelper.getInstance().inBackRecMsg == null)
            return;
        EMMessage message = HuanXinIMHelper.getInstance().inBackRecMsg;
        String gameMessageId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, "");
        EMMessage gameMsg = EMClient.getInstance().chatManager().getMessage(gameMessageId);
        if (gameMsg != null)
        {
            gameMsg.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
            conversation.updateMessage(gameMsg);
            chatMsgList.refreshData(gameMsg);
            HuanXinIMHelper.getInstance().inBackRecMsg = null;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ChatFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermGranted()
    {
        Logger.t(TAG).d("允许获取权限");
        if (CommonUtils.cameraIsCanUse())
        {
            startActivityForResult(new Intent(mAct, TrendsRecordVideoAct.class), EamConstant.EAM_OPEN_RECORD_VIDEO);
        }
        else
        {
            ToastUtils.showShort("请释放相机资源");
        }
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        ToastUtils.showLong(getString(R.string.per_camera_never_ask));
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mAct)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("需要使用您的相机和录音权限才能完成此功能！")
                .setPositiveButton("允许", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.cancel();
                    }
                }).show();
    }

    //region GPS 定位 权限
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationPermGranted()
    {
        Logger.t(TAG).d("允许获取权限");
        startActivityForResult(new Intent(getActivity(), BaiduMapActivity.class), REQUEST_CODE_MAP);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        ToastUtils.showLong(getString(R.string.per_location_never_ask));
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mAct)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("请打开GPS才能完成此功能！")
                .setPositiveButton("允许", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.cancel();
                    }
                }).show();
    }
    //endregion

    @OnClick({R.id.btn_focus, R.id.itv_cancle})
//, R.id.btn_test
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_focus:
                mPresenter.focusPerson(toEaseUser.getuId(), "1");
                //发送直播间关注消失
                Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_HIDE_FOCUS);
                mAct.sendBroadcast(intent);
                break;
            case R.id.itv_cancle:
                Animation animation = AnimationUtils.loadAnimation(mAct, R.anim.invite_connect_out);
                rlTip.setAnimation(animation);
                rlTip.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.Merge10C_agreeInvite:

                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        Logger.t(TAG).d("requestNetErrorCallback():" + interfaceName);
    }

    @Override
    public void focusCallback(String response)
    {
        View view = LayoutInflater.from(mAct).inflate(R.layout.toast_ok_bg, null);
        IconTextView tvIcon = (IconTextView) view.findViewById(R.id.toast_bg_g);
        IconTextView tvContent = (IconTextView) view.findViewById(R.id.toast_content);
        tvIcon.setTextSize(60);
        tvContent.setText("关注成功");
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.showCustomShortSafe(view);
        ToastUtils.cancel();
        Animation animation = AnimationUtils.loadAnimation(mAct, R.anim.invite_connect_out);
        rlTip.setAnimation(animation);
        rlTip.setVisibility(View.GONE);
    }

    @Override
    public void firstTalkCallback(String response, EMMessage message)
    {
        sendMessage(message, false);
    }

    @Override
    public void sendSayHelloCallback(String response)
    {
        Logger.t(TAG).d("sendSayHello返回结果:" + response);
    }

    @Override
    public void deleteBlackCallback(String response)
    {
        ToastUtils.showShort("移除成功");
        if ("2".equals(inBlack))//自己的黑名单有他 就设置为 双方都无拉黑
            setInBlack("0");
        else if ("3".equals(inBlack))//双方 互拉黑的状态下  改为 对方黑名单 有我
            setInBlack("1");
        sendOutBlackCMDMsg(EamConstant.EAM_CHAT_OUTBLACK_NOTIFY);
    }

    @Override
    public void queryUsersRelationShipCallBack(String response)
    {
        try
        {
            Logger.t(TAG).d("用户关系回调：" + response);
            JSONObject jsonObject = new JSONObject(response);
            String isFocus = jsonObject.getString("focus");
            String inBlack = jsonObject.getString("inBlack");
            String isSayHello = jsonObject.getString("isSayHello");
            String remark = jsonObject.getString("remark");
            setRemark(remark);
            if ("0".equals(isFocus))
                showFocusTip();
            setInBlack(inBlack);
            setIsSayHello(isSayHello);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void queryForwardUsersRelationShipCallBack(String response)
    {
        try
        {
            Logger.t(TAG).d("查询转发用户关系回调：" + response);
            JSONObject jsonObject = new JSONObject(response);
//            String isFocus = jsonObject.getString("focus");
            String inBlack = jsonObject.getString("inBlack");
            String isSayHello = jsonObject.getString("isSayHello");
            String remark = jsonObject.getString("remark");
            setRemark(remark);
            setInBlack(inBlack);
            setIsSayHello(isSayHello);
            isFirstSendIsSayHelloMsg = true;
            sendForwardMessage();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendGameInviteCallBack(String response, EMMessage message)
    {
        Logger.t(TAG).d("chat------>发送游戏邀请返回：" + response);
        try
        {
            JSONObject object = new JSONObject(response);
            gameId = object.getString("gameId");
            gameUrl = object.getString("url");
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, gameId);
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, gameUrl);
            sendGameMessage(message);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendGameInviteErrorCallBack(String errorCode, String errorMsg, EMMessage message)
    {
        if ("HAS_INVITE".equals(errorCode))
        {
            ToastUtils.showShort("不要一直戳人家，等等Ta的回复嘛");
        }
    }

    @Override
    public void acceptGameInviteCallBack(String response, int position, EMMessage message)
    {
        try
        {
            Logger.t(TAG).d("chat------>接受邀请返回： response:" + response + " | position:" + position);
            JSONObject object = new JSONObject(response);
            String matchId = object.getString("matchId");
            JSONArray array = object.getJSONArray("refuseList");
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject object1 = new JSONObject(array.getString(i));
                String hxId = object1.getString("hxId");
                String messageId = object1.getString("messageId");
                Map<String, String> map = new ArrayMap<>();
                map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_REFUSE);
                map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, messageId);
                sendCMDMsg(EamConstant.EAM_CHAT_REFUSE_GAME_NOTIFY, hxId, map);
                //拿到消息id后 要更新 自己 与这个人 的 会话中的消息 状态
                EMMessage localMessage = EMClient.getInstance().chatManager().getMessage(messageId);
                if (localMessage != null)
                {
                    localMessage.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_REFUSE);
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(hxId, ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                    conversation.updateMessage(localMessage);
                }
            }
            // 多个人邀请我 游戏  接受 某个人 返回拒绝其他人的邀请 refuseIds  我需要拒绝这些人 的邀请
            //  消息状态 0：发送中 1：接受 2：拒绝 3：对战中 4：过期
            //    *1  发送 ：0  ；   *3 拒绝  ：2  ；   4  过期  ：4   ；   *5 接受 ： 1 ；   6 进行中 ：3
            Map<String, String> map = new ArrayMap<>();
            map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
            map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, message.getMsgId());
            map.put(EamConstant.EAM_CHAT_ATTR_GAME_MATCH_ID, matchId);
            map.put(EamConstant.EAM_CHAT_ATTR_GAME_ID, message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, ""));
            map.put(EamConstant.EAM_CHAT_ATTR_GAME_URL, message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, ""));
            map.put(EamConstant.EAM_CHAT_ATTR_NICKNAME, SharePreUtils.getNicName(mAct));
            map.put(EamConstant.EAM_CHAT_ATTR_UID, SharePreUtils.getUId(mAct));
            map.put(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
//            map.put(EamConstant.EAM_CHAT_ATTR_GAME_REFUSE_IDS, EamApplication.getInstance().getGsonInstance().toJson(refuseIds));
            sendCMDMsg(EamConstant.EAM_CHAT_ACCEPT_GAME_NOTIFY, toChatUserName, map, new EMCallBack()
            {
                @Override
                public void onSuccess()
                {
                    message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_ACCEPT);
                    conversation.updateMessage(message);
                    chatMsgList.refreshData(position, message);
                    Intent intent = new Intent(mAct, GameAct.class);
                    String gameId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, "");
                    String gameUrl = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, "");
                    intent.putExtra("gameId", gameId);
                    intent.putExtra("gameUrl", gameUrl);
                    intent.putExtra("gameName", gameName);
                    intent.putExtra("matchId", matchId);
                    startActivity(intent);
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
    public void acceptGameInviteErrorCallBack(String errorCode, int position, EMMessage message)
    {
        if ("ALREADY_REFUSED".equals(errorCode))
        {
            Map<String, String> map = new ArrayMap<>();
            map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_OVERDUE);
            map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, message.getMsgId());
//            map.put(EamConstant.EAM_CHAT_ATTR_GAME_REFUSE_IDS, EamApplication.getInstance().getGsonInstance().toJson(refuseIds));
            sendCMDMsg(EamConstant.EAM_CHAT_REFUSE_GAME_NOTIFY, toChatUserName, map, new EMCallBack()
            {
                @Override
                public void onSuccess()
                {
                    Logger.t(TAG).d("chat------>接受邀请后台返回过期，发送过期成功");
                    message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_OVERDUE);
                    conversation.updateMessage(message);
                    chatMsgList.refreshData(position, message);
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
        }
    }

    @Override
    public void refuseGameInviteCallBack(String response, int position, EMMessage message)
    {
        String msgId = message.getMsgId();
        Logger.t(TAG).d("chat------>拒绝对方游戏邀请： | position:" + position + " | " + msgId);

        Map<String, String> map = new ArrayMap<>();
        map.put(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_REFUSE);
        map.put(EamConstant.EAM_CHAT_ATTR_GAME_MSG_ID, msgId);
        sendCMDMsg(EamConstant.EAM_CHAT_REFUSE_GAME_NOTIFY, toChatUserName, map, new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                Logger.t(TAG).d("chat------>发送拒绝对方游戏邀请CMD成功： | position:" + position + " | " + msgId);
                message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_REFUSE);
                conversation.updateMessage(message);
                chatMsgList.refreshData(position, message);
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
    public void refuseGameInviteErrorCallBack(String errorCode, int position, EMMessage message)
    {
        if ("ALREADY_REFUSED".equals(errorCode))
        {
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, EamConstant.EAM_ATTR_GAME_STATE_REFUSE);
            conversation.updateMessage(message);
            chatMsgList.refreshData(position, message);
        }
    }

    @Override
    public void queryAnotherInviteCallBack(String response, String matchId, EMMessage message)
    {
        try
        {
            Logger.t(TAG).d("chat------>查询邀请其他人的id返回结果：" + response);
            JSONObject jsonObject = new JSONObject(response);
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
            String gameId = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, "");
            String gameUrl = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, "");
            Logger.t(TAG).d("chat------>点击去对战跳转数据：gameId:" + gameId + " | gameUrl:" + gameUrl + " | matchId:" + matchId);
            Intent intent = new Intent(mAct, GameAct.class);
            intent.putExtra("gameId", gameId);
            intent.putExtra("gameUrl", gameUrl);
            intent.putExtra("gameName", gameName);
            intent.putExtra("matchId", matchId);
            startActivity(intent);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void saveMessageIdCallBack(String response)
    {
        Logger.t(TAG).d("chat------>调用保存消息ID返回：" + response);
    }
}
