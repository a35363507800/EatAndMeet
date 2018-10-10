package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CSayHelloUsersAct;
import com.echoesnet.eatandmeet.activities.CSearchConversationAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ConversationBean;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.datamodel.AbstractEMMessageListener;
import com.echoesnet.eatandmeet.presenters.ImpICommunicatePre;
import com.echoesnet.eatandmeet.presenters.ManagerConversion;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICommunicateFrgView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.DateUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketConstant;
import com.echoesnet.eatandmeet.views.widgets.IMwidget.ConversationList;
import com.echoesnet.eatandmeet.views.widgets.LiveMsgDialog.LiveChatDialog;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author yqh
 * @version 1.0
 * @modifier ben
 * @createDate 2017/7/10
 * @description
 */
public class ConversationListFragment extends MVPBaseFragment<ConversationListFragment, ImpICommunicatePre> implements ICommunicateFrgView
{
    private static final String TAG = ConversationListFragment.class.getSimpleName();

    private final static int DISCONNECTED = 0;
    private final static int CONNECTED = 1;
    private final static int MSG_REFRESH = 2;
    public final static String MSG_TYPE = "msg_type";
    public final static String MSG_HELLO_CHAT = "msg_hello_chat";
    public final static String MSG_CHAT = "msg_chat";
    public final static String OPEN_SOURCE = "normal";
    public final static String LIVE_OPEN = "live_open";

    @BindView(R.id.empty_view)
    EmptyView emptyView;
    @BindView(R.id.search_bar)
    LinearLayout searchBar;
    @BindView(R.id.tv_error_container)
    TextView tvErrorIndicator;
    @BindView(R.id.rv_conversation)
    ConversationList rvConversation;
    @BindView(R.id.rl_hello)
    RelativeLayout rlHello;
    @BindView(R.id.tv_hello_summary)
    TextView tvHelloSummary;
    @BindView(R.id.tv_hello_last_body)
    TextView tvHelloLastBody;
    @BindView(R.id.tv_hello_time)
    TextView tvHelloTime;
    @BindView(R.id.ctv_sys_unRead)
    TextView ctvSysRead;
    @BindView(R.id.live_empty_view)
    TextView liveEmptyView;

    private String msgType = MSG_HELLO_CHAT;
    private String openSource = OPEN_SOURCE;
    private Unbinder unbinder;
    private boolean isVisible;
    private LiveChatDialog liveChatDialog;
    private List<ConversationBean> dataSource = new ArrayList<>();
    private List<ConversationBean> helloDataSource = new ArrayList<>();
    private Activity mAct;

    private int normalChatMsgCount;
    private int helloChatMsgCount;

    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DISCONNECTED:
                    onConnectionDisconnected(msg.arg1);
                    break;
                case CONNECTED:
                    onConnectionConnected();
                    break;
                case MSG_REFRESH:
                {
                    refresh(msgType);
                    break;
                }
                default:
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            msgType = args.getString(MSG_TYPE);
            openSource = args.getString(OPEN_SOURCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.conversation_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mAct = getActivity();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        EMClient.getInstance().addConnectionListener(conListener);
        if (LIVE_OPEN.equals(openSource))
        {
            hideSayHelloView();
            hideSearchBar();
        }
        initView(view);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        refresh(msgType);
    }

    @Override
    public void onDestroyView()
    {
        try
        {
            super.onDestroyView();
            unbinder.unbind();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy()
    {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        EMClient.getInstance().removeConnectionListener(conListener);
        super.onDestroy();
    }

    public boolean getConFragmentVisibility()
    {
        return isVisible;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        Logger.t(TAG).d("setUserVisibleHint:" + isVisibleToUser);
        this.isVisible = isVisibleToUser;
        if (isVisible)
        {
            refresh(msgType);
        }
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    @Override
    protected ImpICommunicatePre createPresenter()
    {
        return new ImpICommunicatePre();
    }

    private void initView(View view)
    {
        rvConversation.init(dataSource, CommonUtils.isInLiveRoom ? liveEmptyView : emptyView);
        emptyView.setContent("您还没有开始聊天哦~");
        emptyView.setImageId(R.drawable.bg_nochat);
        if (CommonUtils.isInLiveRoom)
            emptyView.setVisibility(View.GONE);
        refresh(msgType);
        rvConversation.setConversationListListener(new ConversationList.IConversationListListener()
        {
            @Override
            public void onItemDeleted(String hxId)
            {
                refresh(msgType);

                Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_RED_HOME);
                mAct.sendBroadcast(intent);
//                2017/12/05 1438 切换广播更新
//                if (getActivity() instanceof HomeAct)
//                {
//                    ((HomeAct) getActivity()).updateUnreadLabel(); // 更新主页上未读消息数目
//                }
            }
        });
        rvConversation.setOnLiveConversationItemClicked(new ConversationList.ILiveConversationItemClickListener()
        {
            @Override
            public void onItemClicked(View view, ConversationBean entity)
            {
                EaseUser eUser = new EaseUser(entity.getConversationId());
                eUser.setNickName(entity.getNickName());
                eUser.setuId(entity.getuId());
                eUser.setAvatar(entity.getHeadImage());
                eUser.setRemark(entity.getRemark());
                liveChatDialog = LiveChatDialog.newInstance(eUser);
                liveChatDialog.show(getChildFragmentManager(), TAG);
                liveChatDialog.setOnDismissListener(new LiveChatDialog.OnDismissListener()
                {
                    @Override
                    public void onDisMiss()
                    {
                        refresh(msgType);
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Intent intent = new Intent(EamConstant.EAM_NOTIFY_LIVE_CHAT_RED);
                                mAct.sendBroadcast(intent);
                            }
                        }, 1000);

                    }
                });
            }
        });
        HuanXinIMHelper.getInstance().setOnSaveContactSuccessListener(new HuanXinIMHelper.ISaveContactSuccessListener()
        {
            @Override
            public void onRefreshConversationData()
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        refresh(msgType);
                    }
                }, 300);

            }
        });
    }

    public void setHelloSummaryNum(int helloCount)
    {
        if (tvHelloSummary == null)
            return;

        if (helloCount <= 0)
            tvHelloSummary.setText("还没有人跟你打招呼哦~");
        else
            tvHelloSummary.setText(String.format("有%d个人和你打招呼", helloCount));
    }

    public void refresh(String msgType)
    {
        if (MSG_CHAT.equals(msgType))
        {
            getConversationByType("normalChat", new Consumer<List<ConversationBean>>()
            {
                @Override
                public void accept(List<ConversationBean> conversations) throws Exception
                {
                    if (getView() == null)
                        return;
                    dataSource.clear();
                    dataSource.addAll(conversations);
                    rvConversation.refreshUI();
                }
            });
            return;
        }
        else if (MSG_HELLO_CHAT.equals(msgType))
        {
            getConversationByType("hello", new Consumer<List<ConversationBean>>()
            {
                @Override
                public void accept(List<ConversationBean> conversations) throws Exception
                {
                    if (getView() == null)
                        return;
                    helloDataSource.clear();
                    helloDataSource.addAll(conversations);
                    if (helloDataSource.size() == 0)
                    {
                        tvHelloLastBody.setText("快去首页看看吧~");
                        tvHelloTime.setText("");
                        ctvSysRead.setVisibility(View.GONE);
                    }
                    else
                    {
                        ConversationBean bean = helloDataSource.get(0);
                        Spannable span = EamSmileUtils.getSmiledText(mAct, String.format("%s：%s",
                                TextUtils.isEmpty(bean.getRemark()) ? bean.getNickName() : bean.getRemark(), bean.getLastMsg()));
                        tvHelloLastBody.setText(span, TextView.BufferType.SPANNABLE);
                        tvHelloTime.setText(DateUtils.getTimestampFormatString(new Date(bean.getTime())));
                        int msgCount = 0;
                        for (ConversationBean conversationBean : helloDataSource)
                        {
                            msgCount += conversationBean.getUnreadMsgNumber();
                        }
                        helloChatMsgCount = msgCount;
                        if (msgCount == 0)
                            ctvSysRead.setVisibility(View.GONE);
                        else if (msgCount > 0)
                            ctvSysRead.setVisibility(View.VISIBLE);
                        ctvSysRead.setText(msgCount > 99 ? "99+" : msgCount + "");
                    }
                }
            });
            getConversationByType("normalChat", new Consumer<List<ConversationBean>>()
            {
                @Override
                public void accept(List<ConversationBean> conversations) throws Exception
                {
                    if (getView() == null)
                        return;
                    dataSource.clear();
                    dataSource.addAll(conversations);
                    normalChatMsgCount = 0;
                    for (ConversationBean conversation : conversations)
                    {
                        normalChatMsgCount += conversation.getUnreadMsgNumber();
                    }
                    Logger.t(TAG).d("会话列表：" + dataSource.toString());
                    rvConversation.refreshUI();
                }
            });
        }

        try
        {
            Observable.just(EMClient.getInstance().chatManager().getUnreadMessageCount())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<Integer>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribe(allMsgUnReadCount ->
                    {
                        if (allMsgUnReadCount > 0)
                        {
                            if (msgCountListener != null)
                            {
                                msgCountListener.showMsgCount(allMsgUnReadCount > 99 ? "99+" : (allMsgUnReadCount + ""));
                            }
                        }
                    }, throwable ->
                    {
                        //执行EMClient.getInstance().chatManager()时，里面的代码 this.emaObject.getChatManager()中emaObject可能为空。--wb
                        if (throwable instanceof NullPointerException)
                        {
                            HuanXinIMHelper.getInstance().init(EamApplication.getInstance());
                        }
                    });
        } catch (Exception e)
        {
            e.printStackTrace();
            if (e instanceof NullPointerException)
            {
                HuanXinIMHelper.getInstance().init(EamApplication.getInstance());
                EamLogger.t(TAG).writeToDefaultFile("由于有崩溃发生，环信状态为空：" + e == null ? "null" : e.getMessage());
            }
        }
    }

    public int getNormalChatMsgCount()
    {
        return normalChatMsgCount;
    }

    public int getHelloChatMsgCount()
    {
        return helloChatMsgCount;
    }

    public void hideSearchBar()
    {
        if (searchBar != null)
            searchBar.setVisibility(View.GONE);
    }

    public void hideSayHelloView()
    {
        if (rlHello != null)
            rlHello.setVisibility(View.GONE);
    }

    private void onConnectionConnected()
    {
        tvErrorIndicator.setVisibility(View.GONE);
    }

    private void onConnectionDisconnected(int errCode)
    {
        tvErrorIndicator.setVisibility(View.VISIBLE);
    }

    public void showNewBieGuide()
    {
        if (SharePreUtils.getIsNewBieSayHi(mAct))
        {
            NetHelper.checkIsShowNewbie(mAct, "6", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        if (mAct == null || mAct.isFinishing())
                            return;
                        final FrameLayout fRoot = (FrameLayout) mAct.getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mAct, R.layout.view_newbie_guide_talk, null);
                        ImageView imageView = (ImageView) vGuide.findViewById(R.id.img_say_hi);
                        imageView.setVisibility(View.VISIBLE);

                        imageView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                fRoot.removeView(vGuide);
                                SharePreUtils.setIsNewBieSayHi(mAct, false);
                                NetHelper.saveShowNewbieStatus(mAct, "6");
                            }
                        });
                        vGuide.setClickable(true);
                        fRoot.addView(vGuide);
                    }
                    else
                    {
                        SharePreUtils.setIsNewBieSayHi(mAct, false);
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
     * 加载数据
     *
     * @param chatType 加载打招呼数据还是普通聊天数据
     * @param consumer
     */
    private void getConversationByType(final String chatType, Consumer<List<ConversationBean>> consumer)
    {
        List<EMConversation> chatCon = null;
        try
        {
            //这句话是不是拉取当前操作用户相关的聊天会话？？
            chatCon = EMClient.getInstance().chatManager().getConversationsByType(EMConversation.EMConversationType.Chat);
            if (chatCon == null)
                return;
            if (!chatCon.isEmpty())
                Logger.t(TAG).d("conversionid>" + chatCon.get(0).conversationId() + " unreadcount>" + chatCon.get(0).getUnreadMsgCount());//1383740360711427675
            Observable.fromIterable(chatCon)
                    .filter(new Predicate<EMConversation>()
                    {
                        @Override
                        public boolean test(EMConversation conversation) throws Exception
                        {
                            boolean result = false;
                            List<EMMessage> msgs = conversation.getAllMessages();
                            if (msgs.isEmpty())
                                return false;
                            if ("inBlack".equals(conversation.getExtField()))
                            {
                                return false;
                            }
                            EMMessage lastMsgFromOther = conversation.getLatestMessageFromOthers();
                            if ("hello".equals(chatType))//过滤打招呼的消息
                            {
                                if (lastMsgFromOther != null)
                                    result = lastMsgFromOther.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false) == true;
                                else
                                    result = false;
                            }
                            else
                            {
                                if (lastMsgFromOther == null)//说明此会话里面只包含自己发送的消息，对方没有回应，处于聊天列表中
                                    result = true;
                                else
                                    result = lastMsgFromOther.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false) == false;
                            }
                            Logger.t(TAG).d("lastMsgFromOther>" + (lastMsgFromOther == null ? "lastMsgFromOther=null" :
                                    (lastMsgFromOther.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false) + "")));
                            return result;
                        }
                    })
                    .map(new Function<EMConversation, ConversationBean>()
                    {
                        @Override
                        public ConversationBean apply(EMConversation conversation) throws Exception
                        {
                            return ManagerConversion.emcon2Conbean(conversation);
                        }
                    })
                    .sorted(new Comparator<ConversationBean>()
                    {
                        @Override
                        public int compare(ConversationBean o1, ConversationBean o2)
                        {
                            long time1 = o1.getTime();
                            long time2 = o2.getTime();
                            if (time1 == time2)
                            {
                                return 0;
                            }
                            else if (time2 > time1)
                            {
                                return 1;
                            }
                            else
                            {
                                return -1;
                            }
                        }
                    })
                    .toList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<List<ConversationBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribe(consumer);
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
            EamLogger.writeToDefaultFile("EMClient.getInstance().chatManager()出现异常" + e.getMessage());
        }

    }

    private EMMessageListener msgListener = new AbstractEMMessageListener()
    {
        @Override
        public void onMessageReceived(List<EMMessage> messages)
        {
            try
            {
                for (EMMessage message : messages)
                {
                    Logger.t(TAG).d("------>环信消息：" + message.getFrom() + " | " + message.getBody());
                    if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false))
                    {
                        EMConversation c = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat);
                        if (c != null)
                            c.setExtField("");
                    }
                    if (mReceiveListener != null)
                    {
                        Logger.t(TAG).d("isSayHello:" + message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false));
                        if (message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false))
                        {
                            mReceiveListener.onHelloMessageReceived(message);
                        }
                        else
                        {
                            mReceiveListener.onChatMessageReceived(message);
                        }
                    }
                }
                handler.sendEmptyMessage(MSG_REFRESH);
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
            for (EMMessage message : messages)
            {
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                Logger.t(TAG).d("chat------>cmd消息：" + cmdMsgBody.action());
                switch (cmdMsgBody.action())
                {
                    case EamConstant.EAM_CHAT_RECALL_MSG_NOTIFY:
                        if (handler != null)
                            handler.sendEmptyMessage(MSG_REFRESH);
                        break;
                }
            }
        }
    };

    private IShowMsgCountListener msgCountListener;

    public interface IShowMsgCountListener
    {
        void showMsgCount(String msgCount);
    }

    public void setIShowMsgCountListener(IShowMsgCountListener msgCountListener)
    {
        this.msgCountListener = msgCountListener;
    }

    private IMessageReceiveListener mReceiveListener;

    public interface IMessageReceiveListener
    {
        void onChatMessageReceived(EMMessage message);

        void onHelloMessageReceived(EMMessage message);
    }

    public void setIMessageReceiveListener(IMessageReceiveListener listener)
    {
        this.mReceiveListener = listener;
    }

    private EMConnectionListener conListener = new EMConnectionListener()
    {
        @Override
        public void onConnected()
        {
            Message msg = handler.obtainMessage();
            msg.what = CONNECTED;
            handler.sendMessage(msg);
        }

        @Override
        public void onDisconnected(int i)
        {
            Message msg = handler.obtainMessage();
            msg.what = DISCONNECTED;
            msg.arg1 = i;
            handler.sendMessage(msg);
        }
    };

    @OnClick({R.id.rl_hello, R.id.search_bar})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.rl_hello:
                Intent intent = new Intent(mAct, CSayHelloUsersAct.class);
                startActivity(intent);
                break;
            case R.id.search_bar:
                Intent intent2 = new Intent(mAct, CSearchConversationAct.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
}
