package com.echoesnet.eatandmeet.views.widgets.chat;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.views.adapters.ChatMessageAdapter2;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;


/**
 * Created by Administrator on 2017/7/11.
 */
public class ChatMessageList extends RelativeLayout
{
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView listView;
    private int chatType;
    private String toChatUsername;
    private EMConversation conversation;
    private ChatMessageAdapter2 adapter;

    private boolean isListViewCanScroll = true;

    //protected EaseMessageListItemStyle itemStyle;

    /**
     * Instantiates a new Chat message list.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ChatMessageList(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        parseStyle(context, attrs);
        init(context);
    }

    /**
     * Instantiates a new Chat message list.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ChatMessageList(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context)
    {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.chat_msg_list, this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        listView = (RecyclerView) findViewById(R.id.listView);
    }

    /**
     * Init.
     *
     * @param toChatUsername the to chat username
     * @param chatType       the chat type
     */
    public void init(String toChatUsername, int chatType)
    {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context)
        {
            @Override
            public boolean canScrollVertically()
            {
                return isListViewCanScroll;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(false);
        listView.setLayoutManager(linearLayoutManager);
        adapter = new ChatMessageAdapter2(context, toChatUsername, chatType, listView);
        // adapter.setItemStyle(itemStyle);
        listView.setAdapter(adapter);

        listView.setItemViewCacheSize(15);

        initListData();
    }

    /**
     * Sets list view can scroll.
     *
     * @param isCanScroll the is can scroll
     */
    public void setListViewCanScroll(boolean isCanScroll)
    {
        isListViewCanScroll = isCanScroll;
        swipeRefreshLayout.setEnabled(isCanScroll);
    }

    /**
     * Parse style.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    protected void parseStyle(Context context, AttributeSet attrs)
    {
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageList);
/*        EaseMessageListItemStyle.Builder builder = new EaseMessageListItemStyle.Builder();
        builder.showAvatar(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserAvatar, true))
                .showUserNick(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserNick, false))
                .myBubbleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground))
                .otherBuddleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground));

        itemStyle = builder.build();*/
//        ta.recycle();
    }

    /**
     * Gets list view.
     *
     * @return the list view
     */
    public RecyclerView getListView()
    {
        return listView;
    }

    /**
     * Gets swipe refresh layout.
     *
     * @return the swipe refresh layout
     */
    public SwipeRefreshLayout getSwipeRefreshLayout()
    {
        return swipeRefreshLayout;
    }

    /**
     * 注意判空
     *
     * @param position the position
     * @return 返回值 有可能为空
     */
    public EMMessage getItem(int position)
    {
        return (EMMessage) adapter.getItem(position);
    }

    /**
     * Sets list scroll listener.
     *
     * @param scrollListener the scroll listener
     */
    public void setListScrollListener(RecyclerView.OnScrollListener scrollListener)
    {
        listView.addOnScrollListener(scrollListener);
    }


    /**
     * The interface Message list item click listener.
     */
    public interface MessageListItemClickListener
    {
        /**
         * On resend click.
         *
         * @param message the message
         */
        void onResendClick(EMMessage message);

        /**
         * there is default handling when bubble is clicked, if you want handle it, return true
         * another way is you implement in onBubbleClick() of chat row
         *
         * @param message the message
         * @return boolean
         */
        boolean onBubbleClick(EMMessage message);

        /**
         * On bubble long click.
         *
         * @param view    the view
         * @param x       the x
         * @param y       the y
         * @param message the message
         */
        void onBubbleLongClick(View view, float x, float y, EMMessage message);

        /**
         * On user avatar click.
         *
         * @param uId the u id
         */
        void onUserAvatarClick(String uId);

        /**
         * On user avatar long click.
         *
         * @param username the username
         */
        void onUserAvatarLongClick(String username);

        /**
         * 游戏消息点击接受或拒绝
         *
         * @param isAccept the is accept
         * @param position the position
         * @param message  the message
         */
        void onGameAcceptOrRefuseClick(boolean isAccept, int position, EMMessage message);

        /**
         * On message send success.
         *
         * @param message 消息发送成功时 回调，
         */
        void onMessageSendSuccess(EMMessage message);

    }

    /**
     * Add voice temp message.
     *
     * @param toUserName the to user name
     * @param remark     the remark
     */
    public void addVoiceTempMessage(String toUserName, String remark)
    {
        adapter.addVoiceTempMessage(toUserName, remark);
    }

    /**
     * refresh all data
     */
    public void refresh()
    {
        if (adapter != null)
        {
            adapter.refresh();
        }
    }

    /**
     * 刷新某条消息
     *
     * @param msgId 消息ID
     */
    public void notifyMessage(String msgId)
    {
        if (adapter != null)
            adapter.notifyMessage(msgId);
    }

    /**
     * 初始化数据
     */
    public void initListData()
    {
        if (adapter != null)
            adapter.initListData();
    }

    /**
     * 下拉加载消息时 调用
     *
     * @param position the position
     */
    public void refreshSeekTo(int position)
    {
        if (adapter != null)
        {
            adapter.refreshSeekTo(position);
        }
    }

    /**
     * refresh and jump to the last
     */
    public void refreshSelectLast()
    {
        if (adapter != null)
        {
            adapter.refreshSelectLast();
        }
    }

    /**
     * jump to the last
     */
    public void selectLast()
    {
        if (adapter != null)
        {
            adapter.click2SelectLast();
        }
    }

    /**
     * smooth scroll to the last
     */
    public void smoothScroll2Last()
    {
        if (adapter != null)
        {
            adapter.smoothScroll2Last();
        }
    }

    /**
     * 情况 消息
     */
    public void clearData()
    {
        if (adapter != null)
        {
            adapter.clearData();
        }
    }

    /**
     * 发送消息 增加一条消息
     *
     * @param message 消息体
     */
    public void refreshLast(EMMessage message)
    {
        if (adapter != null)
        {
            adapter.refreshAddItem(message);
        }
    }

    /**
     * Remove last item.
     */
    public void removeLastItem()
    {
        if (adapter != null)
        {
            adapter.removeLastItem();
        }
    }

    /**
     * 更新数据
     *
     * @param position 坐标
     * @param message  消息体
     */
    public void refreshData(int position, EMMessage message)
    {
        if (adapter != null)
        {
            adapter.refreshData(position, message);
        }
    }

    /**
     * 更新数据
     *
     * @param message 消息体
     */
    public void refreshData(EMMessage message)
    {
        if (adapter != null)
        {
            adapter.refreshData(message);
        }
    }

    /**
     * 删除消息
     *
     * @param msgId        消息id
     * @param isNeedNotify 是否刷新页面
     */
    public void removeMessage(String msgId, boolean isNeedNotify)
    {
        if (adapter != null)
        {
            adapter.removeMessage(msgId, isNeedNotify);
        }
    }

    /**
     * Gets last message.
     *
     * @return the last message
     */
    public EMMessage getLastMessage()
    {
        EMMessage message = null;
        if (adapter != null)
            message = adapter.getLastMessage();
        return message;
    }

    /**
     * Refresh new data.
     */
    public void refreshNewData()
    {
        if (adapter != null)
            adapter.refreshNewData();
    }

    /**
     * 撤回消息
     *
     * @param msgId   消息id
     * @param message the message
     */
    public void recallMessage(String msgId, EMMessage message)
    {
        if (adapter != null)
        {
            adapter.recallMessage(msgId, message);
        }
    }

    /**
     * Is animator open boolean.
     *
     * @return the boolean
     */
    public boolean isAnimatorOpen()
    {
        return adapter.isAnimatorOpen();
    }

    /**
     * Open default animator.
     */
    public void openDefaultAnimator()
    {
        if (adapter != null)
            adapter.openDefaultAnimator();
    }

    /**
     * Close default animator.
     */
    public void closeDefaultAnimator()
    {
        if (adapter != null)
            adapter.closeDefaultAnimator();
    }

    /**
     * set click listener
     *
     * @param listener the listener
     */
    public void setItemClickListener(MessageListItemClickListener listener)
    {
        if (adapter != null)
        {
            adapter.setItemClickListener(listener);
        }
    }
}
