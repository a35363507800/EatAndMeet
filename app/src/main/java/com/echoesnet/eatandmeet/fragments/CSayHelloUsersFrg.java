package com.echoesnet.eatandmeet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ConversationBean;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.datamodel.AbstractEMMessageListener;
import com.echoesnet.eatandmeet.presenters.ManagerConversion;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.views.widgets.IMwidget.ConversationList;
import com.echoesnet.eatandmeet.views.widgets.LiveMsgDialog.LiveChatDialog;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/7/28 13:08
 * @description 暂时没有写消息监听，如果有需求请后面的同学加上
 */

public class CSayHelloUsersFrg extends BaseFragment
{
    private static final String TAG = CSayHelloUsersFrg.class.getSimpleName();

    @BindView(R.id.con_list)
    ConversationList rvConversation;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    @BindView(R.id.live_empty_view)
    TextView liveEmptyView;

    private Unbinder unbinder;
    private List<ConversationBean> dataSource = new ArrayList<>();
    private boolean helloFrgVisibility;
    private LiveChatDialog liveChatDialog;
    private int unreadMsgCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_say_hello_frg, container, false);
        unbinder = ButterKnife.bind(this, view);
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Logger.t(TAG).d("onResume");
        getConversationByType();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    public void setAllMsgRead()
    {
        Logger.t(TAG).d("setAllMsgRead");
        for (ConversationBean bean : dataSource)
        {
            bean.setUnreadMsgNumber(0);
            EMConversation c = EMClient.getInstance().chatManager().getConversation(bean.getConversationId());
            if (c != null)
                c.markAllMessagesAsRead();
            /*if (c != null)
            {
                List<EMMessage> messageList = c.getAllMessages();
                for (EMMessage message : messageList)
                {
                    //语音和视频要 听 或看 之后才算已读
                    if (message.getType() != EMMessage.Type.VOICE && message.getType() != EMMessage.Type.VIDEO)
                        EaseCommonUtils.makeMessageAsRead(c, message, true);
                    else
                        EaseCommonUtils.makeMessageAsRead(c, message, false);
                }
            }*/

        }
        rvConversation.refreshUI();
    }

    private void initView()
    {
        rvConversation.init(dataSource, CommonUtils.isInLiveRoom ? liveEmptyView : emptyView);
        emptyView.setContent("暂时还没有人打招呼呢~");
        emptyView.setImageId(R.drawable.bg_nochat);
        if (CommonUtils.isInLiveRoom)
            emptyView.setVisibility(View.GONE);
        rvConversation.setConversationListListener(new ConversationList.IConversationListListener()
        {
            @Override
            public void onItemDeleted(String hxId)
            {
                getConversationByType();
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
                liveChatDialog = LiveChatDialog.newInstance(eUser);
                liveChatDialog.setOnDismissListener(new LiveChatDialog.OnDismissListener()
                {
                    @Override
                    public void onDisMiss()
                    {
                        getConversationByType();
                    }
                });
                liveChatDialog.show(getChildFragmentManager(), TAG);
            }
        });
        getConversationByType();
    }

    private void getConversationByType()
    {
        Observable.fromIterable(EMClient.getInstance().chatManager().getConversationsByType(EMConversation.EMConversationType.Chat))
                .filter(new Predicate<EMConversation>()
                {
                    @Override
                    public boolean test(EMConversation conversation) throws Exception
                    {
                        EMMessage lastMsgFromOther = conversation.getLatestMessageFromOthers();
                        if ("inBlack".equals(conversation.getExtField()))
                        {
                            return false;
                        }
                        if (lastMsgFromOther != null)
                            return lastMsgFromOther.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false) == true;
                        else
                            return false;
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
                .subscribe(new Consumer<List<ConversationBean>>()
                {
                    @Override
                    public void accept(List<ConversationBean> conversations) throws Exception
                    {
                        dataSource.clear();
                        dataSource.addAll(conversations);
                        if (rvConversation != null)
                        rvConversation.refreshUI();

                        unreadMsgCount = 0;
                        for (ConversationBean conversation : conversations)
                        {
                            unreadMsgCount += conversation.getUnreadMsgNumber();
                        }
                        if (mHelpListener != null)
                            mHelpListener.onUnreadHelloMsgChanged(unreadMsgCount);
                    }
                });
    }

    public int getUnreadMsgCount()
    {
        return unreadMsgCount;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        helloFrgVisibility = isVisibleToUser;
        Logger.t(TAG).d("setUserVisibleHint:" + isVisibleToUser);
        if (isVisibleToUser)
        {
            getConversationByType();
        }
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    public boolean getVisibility()
    {
        return helloFrgVisibility;
    }

    EMMessageListener msgListener = new AbstractEMMessageListener()
    {
        @Override
        public void onMessageReceived(List<EMMessage> messages)
        {
            try
            {
                super.onMessageReceived(messages);
                for (EMMessage message : messages)
                {
                    if (message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false))
                    {
                        getConversationByType();
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("hx 消息解析错误" + e.getMessage());
            }
        }
    };

    public void setHelloFrgListener(ISayHelloFrgListener listener)
    {
        this.mHelpListener = listener;
    }

    private ISayHelloFrgListener mHelpListener;

    public interface ISayHelloFrgListener
    {
        void onUnreadHelloMsgChanged(int unreadMsgCount);
    }

}
