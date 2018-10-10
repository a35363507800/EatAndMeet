package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.fragments.ChatFragment;
import com.echoesnet.eatandmeet.models.bean.ActionItemBean;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpIChatActivityView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IChatActivityView;
import com.echoesnet.eatandmeet.utils.AndroidBug5497Workaround;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketConstant;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.echoesnet.eatandmeet.views.widgets.UserRightPop;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

/**
 * 聊天界面
 */
public class CChatActivity extends MVPBaseActivity<IChatActivityView, ImpIChatActivityView> implements IChatActivityView
{
    private static final String TAG = CChatActivity.class.getSimpleName();
    @BindView(R.id.iv_game_invite)
    ImageView ivGameInvite;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.send_voice_tip_cover)
    TextView sendVoiceTipCover;
    //region 变量
    private Activity mAct;
    private Dialog pDialog;

    //  private View popCover;
    private TextView centerView;
    private ChatFragment chatFragment;
    private UserRightPop titlePopup; // 添加+弹出层
    private EaseUser toEaseUser;
    public boolean isDeleteUser = false;
    public boolean isRefreshDataOnResume = false;

    //    public String isLiveShared;
    //endregion
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        ButterKnife.bind(this);
        mAct = this;
        AndroidBug5497Workaround.assistActivity(this);
        EamApplication.getInstance().controlChat.put(TAG, this);
        CommonUtils.isInChatRoom = true;
        // popCover = findViewById(R.id.ViewPopCover);
        centerView = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                onBackPressed();
            }

            @Override
            public void right2Click(View view)
            {
                if (titlePopup != null)
                {
                    if (!titlePopup.isShowing())
                    {
                        titlePopup.show(view);
                        // popCover.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        List<TextView> navBtns = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i);
            if (i == 1)
            {
                tv.setText("{eam-e609}");
                tv.setTextSize(22);
            }
        }

        toEaseUser = getIntent().getParcelableExtra(Constant.EXTRA_TO_EASEUSER);
        if (toEaseUser != null)
            centerView.setText(TextUtils.isEmpty(toEaseUser.getRemark()) ? toEaseUser.getNickName() : toEaseUser.getRemark());
        chatFragment = new ChatFragment();
        chatFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, chatFragment).commit();
        initTitlePopup();
        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理...");
        pDialog.setCancelable(false);

        mPresenter.queryUsersRelationShip(toEaseUser.getuId());

        sendVoiceTipCover.setOnTouchListener((v, event) -> true);

        chatFragment.setISendVoiceTipViewListener(new ChatFragment.ISendVoiceTipViewPressListener()
        {
            @Override
            public boolean onPressSendVoiceView(MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        sendVoiceTipCover.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        sendVoiceTipCover.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        ivGameInvite.setVisibility(EamApplication.getInstance().getChannelResult == 1 ? View.GONE : View.VISIBLE);
//        chatFragment.showGoFightDialog(getIntent());
    }

    @Override
    protected void onResume()
    {
        HuanXinIMHelper.getInstance().newGameInviteMessage = null;
        super.onResume();
        if (chatFragment != null)
        {
            if (chatFragment.isReceivedAcceptMsg)
            {
                if (CommonUtils.isInLiveRoom)
                {
                    Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_CLOSE_LIVE);
                    if (mAct != null)
                        mAct.sendBroadcast(intent);
                }
                chatFragment.goGameAct();
                chatFragment.isReceivedAcceptMsg = false;
            }
            if (isRefreshDataOnResume)
            {
                chatFragment.refreshData();
                isRefreshDataOnResume = false;
            }

            chatFragment.checkMessageHasChanged();
        }
        if (mPresenter != null)
            mPresenter.queryUsersRelationShip(toEaseUser.getuId());
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        chatFragment.clearInputFocus();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null)
        {
            EaseUser easeUser = intent.getParcelableExtra(Constant.EXTRA_TO_EASEUSER);
            if (easeUser != null)
            {
                Logger.t(TAG).d("easeUser++:" + easeUser.toString());
                if (!TextUtils.isEmpty(easeUser.getRemark()))
                {
                    if (!easeUser.getRemark().equals(centerView.getText()))
                        centerView.setText(easeUser.getRemark());
                }
                else
                {
                    centerView.setText(easeUser.getNickName());
                }
            }
            if (chatFragment != null)
                chatFragment.refreshData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.t(TAG).d("+++++requestCode:" + requestCode + " | resultCode:" + resultCode + " | data:" + data);

        switch (resultCode)
        {
            case RESULT_OK:
                switch (requestCode)
                {
                    case EamCode4Result.reQ_CChatActivity:
                        if (data != null)
                        {
                            String action = data.getStringExtra("action");
                            if (CNewUserInfoAct.ACTION_REMARK.equals(action) || CNewUserInfoAct.ACTION_FOCUS_REMARK.equals(action))
                            {
                                EaseUser user = data.getParcelableExtra(Constant.EXTRA_TO_EASEUSER);
                                centerView.setText(TextUtils.isEmpty(user.getRemark()) ? user.getNickName() : user.getRemark());
                                toEaseUser = user;//更新本页 EaseUser
                                if (chatFragment != null)
                                    chatFragment.updateEaseUser(toEaseUser);
                            }
                        }
                        break;
                }
                break;
        }
        if (chatFragment != null)
            chatFragment.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 操作框初始化
     */
    private void initTitlePopup()
    {
        titlePopup = new UserRightPop(mAct);
        titlePopup.addAction(0, new ActionItemBean(mAct, "查看主页", "{eam-e9a1}", "0"));
        titlePopup.addAction(1, new ActionItemBean(mAct, "清空消息", "{eam-s-delete}", "0"));
        titlePopup.addAction(2, new ActionItemBean(mAct, "拉黑", "{eam-e61f}", "0"));
        titlePopup.addAction(3, new ActionItemBean(mAct, "举报", "{eam-s-warning}", "0"));
        titlePopup.setItemOnClickListener(new UserRightPop.OnItemOnClickListener()
        {
            @Override
            public void onItemClick(ActionItemBean item, int position)
            {
                switch (position)
                {
                    case 0:
                        Intent intent = new Intent(mAct, CNewUserInfoAct.class);
                        intent.putExtra("toUId", toEaseUser.getuId());
                        intent.putExtra("toId", toEaseUser.getId());
                        intent.putExtra("checkWay", "UId");
                        startActivityForResult(intent, EamCode4Result.reQ_CChatActivity);
                        isRefreshDataOnResume = true;
                        break;
                    case 1:
                        deleteConversation(toEaseUser.getUsername(), true, false);
                        break;
                    case 2:
                        showPutInBlackTip();
                        break;
                    case 3:
                        //举报
                        Intent reportIntent = new Intent(mAct, ReportFoulsUserAct.class);
                        reportIntent.putExtra("luId", toEaseUser.getuId());
                        startActivity(reportIntent);
                        break;
                    default:
                        break;
                }
            }
        });
        titlePopup.setOnDismissListener(() ->
        {
            //    popCover.setVisibility(View.GONE);
            titlePopup.dismissPop();
        });
    }

    /**
     * 拉黑提示弹窗
     */
    private void showPutInBlackTip()
    {
        new CustomAlertDialog(mAct)
                .builder()
                .setTitle("提示")
                .setMsg("是否拉黑“" + (TextUtils.isEmpty(toEaseUser.getRemark()) ? toEaseUser.getNickName() : toEaseUser.getRemark()) + "”？")
                .setPositiveTextColor(Color.parseColor("#666666"))
                .setPositiveButton("是", v -> mPresenter.pull2Black(toEaseUser.getuId()))
                .setNegativeButton("否", v ->
                {
                })
                .show();
    }

    /**
     * 删除会话
     *
     * @param userName      环信id
     * @param deleteMessage 是否连相关的消息也删除
     */

    private void deleteConversation(final String userName, final boolean deleteMessage, boolean isPull2Black)
    {
        Observable.create(new ObservableOnSubscribe<List<String>>()
        {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception
            {
                EMConversation c = EMClient.getInstance().chatManager().getConversation(toEaseUser.getUsername(), EMConversation.EMConversationType.Chat);
                if (c != null)
                {
                    EMMessage lastMsgFromOther = c.getLatestMessageFromOthers();
                    if (lastMsgFromOther != null)
                    {
                        EaseUser user = new EaseUser(lastMsgFromOther.getFrom());
                        user.setuId(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, ""));
                        user.setId(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_ID));
                        user.setNickName(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME));
                        user.setAvatar(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE));
                        user.setLevel(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_LEVEL));
                        user.setSex(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GENDER));
                        user.setAge(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_AGE));
                        user.setRemark(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_REMARK));
                        user.setIsVuser(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_VUSER, "0"));
                        Logger.t(TAG).d("保存用户信息：" + user.toString());
                        HuanXinIMHelper.getInstance().saveContact(user);
                    }
                }
                if (isPull2Black)//拉黑不检查红包
                {
                    deleteConBaseType(userName, deleteMessage, isPull2Black);
                    e.onComplete();
                }
                if (!deleteMessage)
                {
                    deleteConBaseType(userName, deleteMessage, isPull2Black);
                    e.onComplete();
                }
                else
                {
                    /*if (c != null)
                    {
                        if (TextUtils.isEmpty(c.getExtField()))
                        {
                            deleteConBaseType(toEaseUser.getUsername(), true);
                            e.onComplete();
                        }
                    }*/
                    Logger.t(TAG).d("userName:" + userName);
                    List<String> redPacketIds = new ArrayList<>();
                    List<EMMessage> msgLst = new ArrayList<>();
                    if (c != null)
                        msgLst = c.getAllMessages();
                    for (EMMessage msg : msgLst)
                    {
                        //如果检测到接收的消息中有没有收取的红包，则提示
                        if (msg.getType() == EMMessage.Type.TXT
                                && msg.direct() == EMMessage.Direct.RECEIVE
                                && msg.getBooleanAttribute("is_money_msg", false) == true)
                        {
                            redPacketIds.add(msg.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, ""));
                        }
                    }
                    e.onNext(redPacketIds);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CChatActivity.this.<List<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<String>>()
                {
                    @Override
                    public void accept(List<String> redPacketIds) throws Exception
                    {
                        Logger.t(TAG).d("执行》" + redPacketIds);
                        mPresenter.checkRedPacketsStates(redPacketIds, isPull2Black);
                    }
                });
    }

    /**
     * 删除聊天记录
     *
     * @param userName     用户hxid
     * @param deleteMsg    是否删除聊天记录
     * @param isPull2Black 是否为拉黑的删除
     */
    private void deleteConBaseType(final String userName, final boolean deleteMsg, boolean isPull2Black)
    {
        Observable.create(new ObservableOnSubscribe<Boolean>()
        {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception
            {
                boolean isDeleteSuc = EMClient.getInstance().chatManager().deleteConversation(userName, deleteMsg);
                if (isDeleteSuc && deleteMsg)//目前只有会话列表使用到了的本地存放的contact，所以可以在删除会话时候删除本地存储，如果以后在其他地方也用到了本地Contact就不能在此处删除了--wb
                {
                    isDeleteUser = true;
                }
                e.onNext(isDeleteSuc);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(CChatActivity.this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        chatFragment.getChatMsgList().clearData();
                        if (isPull2Black)
                        {
                            ToastUtils.showShort("已成功拉黑");
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void finish()
    {
        EamApplication.getInstance().controlChat.remove(TAG);
        CommonUtils.isInChatRoom = false;
        super.finish();
    }

    @Override
    public void onDestroy()
    {
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
        super.onDestroy();
    }

    @Override
    protected ImpIChatActivityView createPresenter()
    {
        return new ImpIChatActivityView(mAct, this);
    }

    //当按下手机的返回键是触发
    @Override
    public void onBackPressed()
    {
        if (!chatFragment.onBackPressed())
            return;
        clickPreviousBtn();
    }

    /**
     * 返回键点击
     */
    private void clickPreviousBtn()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
        {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        if (chatFragment != null)
        {
            EMConversation c = EMClient.getInstance().chatManager().getConversation(toEaseUser.getUsername(), EMConversation.EMConversationType.Chat);
            if (c != null)
            {
                if (c.getLatestMessageFromOthers() != null)
                {
                    boolean isRedPacketMessage = c.getLatestMessageFromOthers()
                            .getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false);
                    boolean isOpenPacketMessage = c.getLastMessage().getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false);
                    if ("2".equals(chatFragment.getInBlack()) || "3".equals(chatFragment.getInBlack())) //我的黑名单 有他， 或者 互相拉黑
                    {
                        if (isRedPacketMessage && isOpenPacketMessage)
                            c.setExtField("inBlack");
                    }
                }
            }
        }
        if (isDeleteUser)
            HuanXinIMHelper.getInstance().deleteContact(toEaseUser.getUsername());
        if (chatFragment != null)
            chatFragment.sendClearMsgCountCMDMsg(EamConstant.EAM_CHAT_CLEAR_MSG_COUNT_NOTIFY);

        finish();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, TAG, e);
    }

    @Override
    public void queryUsersRelationShipCallBack(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            String isFocus = jsonObject.getString("focus");
            String inBlack = jsonObject.getString("inBlack");
            String isSayHello = jsonObject.getString("isSayHello");
            String remark = jsonObject.getString("remark");
            if ("0".equals(isFocus))
                chatFragment.showFocusTip();
            else
                chatFragment.hideFocusTip();
            if ("2".equals(inBlack) || "3".equals(inBlack))
            {
                titlePopup.removeAction(2);
            }
//            JSONObject object = new JSONObject(jsonObject.getString("invite"));
//            String messageId0 = object.getString("messageId0");
//            if (!TextUtils.isEmpty(messageId0))
//                isCanClick = false;
//            String messageId1 = object.getString("messageId1");\
            chatFragment.setInBlack(inBlack);
            chatFragment.setIsSayHello(isSayHello);
            chatFragment.setRemark(remark);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void pull2BlackCallBack(String response)
    {
//        chatFragment.setInBlack("2");
        chatFragment.sendInBlackCMDMsg(EamConstant.EAM_CHAT_INBLACK_NOTIFY);
        EMConversation c = EMClient.getInstance().chatManager().getConversation(toEaseUser.getUsername(), EMConversation.EMConversationType.Chat);
        if (c != null)
            c.setExtField("inBlack");

        deleteConversation(toEaseUser.getUsername(), true, true);
//        titlePopup.removeAction(2);
//        finish();
    }

    @Override
    public void checkRedPacketStatsCallback(String response, boolean isPull2Black)
    {
        try
        {
            JSONObject body = new JSONObject(response);
            Logger.t(TAG).d("userName:checkRedPacketStatsCallback：" + response);
            if ("true".equals(body.getString("flag")))//说明有没有领取的红包
            {
                new CustomAlertDialog(mAct)
                        .builder()
                        .setTitle("提示")
                        .setMsg("您有未领取的红包，是否确认清空所有聊天记录？")
                        .setPositiveButton("确定", v -> deleteConBaseType(toEaseUser.getUsername(), true, isPull2Black))
                        .setNegativeButton("取消", v ->
                        {
                        })
                        .show();
            }
            else
            {
                deleteConBaseType(toEaseUser.getUsername(), true, isPull2Black);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    @OnClick({R.id.iv_game_invite})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_game_invite:
                if (chatFragment != null)
                {
                    chatFragment.sendGameInvite();
                }
                break;
        }
    }
}
