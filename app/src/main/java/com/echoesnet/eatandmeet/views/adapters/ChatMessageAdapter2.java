package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.utils.ChatCommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseCommonUtils;
import com.echoesnet.eatandmeet.utils.LiveSharedUtil.ChatRowLiveSharedAck;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.redPacket.ChatRowRecallMessage;
import com.echoesnet.eatandmeet.utils.redPacket.ChatRowRedPacket;
import com.echoesnet.eatandmeet.utils.redPacket.ChatRowRedPacketAck2;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketConstant;
import com.echoesnet.eatandmeet.views.widgets.chat.ChatMessageList;
import com.echoesnet.eatandmeet.views.widgets.chat.ChatRowGame;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRow;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowBigExpression;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowFile;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowImage;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowLocation;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowText;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowVideo;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowVoice;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowVoiceTemp;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2017/7/21
 * @Description
 */
public class ChatMessageAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final static String TAG = ChatMessageAdapter2.class.getSimpleName();

    private static final int HANDLER_MESSAGE_INIT_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;
    private static final int HANDLER_MESSAGE_REFRESH_LAST = 3;
    private static final int HANDLER_MESSAGE_LOAD_MORE = 4;
    private static final int HANDLER_MESSAGE_NOTIFY_DATA = 5;
    private static final int HANDLER_MESSAGE_CLEAR_DATA = 6;
    private static final int HANDLER_MESSAGE_NOTIFY_ITEM_REMOVE = 7;

    private static final int CHAT_ROW_TEXT_SEND = 1101;
    private static final int CHAT_ROW_TEXT_RECEIVE = 1102;
    private static final int CHAT_ROW_BIG_EXPRESSION_SEND = 1103;
    private static final int CHAT_ROW_BIG_EXPRESSION_RECEIVE = 1104;
    private static final int CHAT_ROW_RED_PACKET_SEND = 1105;
    private static final int CHAT_ROW_RED_PACKET_RECEIVE = 1106;
    private static final int CHAT_ROW_RED_PACKET_ACK_SEND = 1107;
    private static final int CHAT_ROW_RED_PACKET_ACK_RECEIVE = 1108;
    private static final int ChAT_ROW_LOCATION_SEND = 1109;
    private static final int ChAT_ROW_LOCATION_RECEIVE = 1110;
    private static final int CHAT_ROW_FILE_SEND = 1111;
    private static final int CHAT_ROW_FILE_RECEIVE = 1112;
    private static final int CHAT_ROW_IMAGE_SEND = 1113;
    private static final int CHAT_ROW_IMAGE_RECEIVE = 1114;
    private static final int CHAT_ROW_VOICE_SEND = 1115;
    private static final int CHAT_ROW_VOICE_RECEIVE = 1116;
    private static final int CHAT_ROW_VIDEO_SEND = 1117;
    private static final int CHAT_ROW_VIDEO_RECEIVE = 1118;
    private static final int CHAT_ROW_SHARE_SEND = 1119;
    private static final int CHAT_ROW_SHARE_RECEIVE = 1120;
    private static final int CHAT_ROW_VOICE_TEMP_SEND = 1121;
    private static final int CHAT_ROW_GAME_RECEIVE = 1122;
    private static final int CHAT_ROW_GAME_SEND = 1123;
    private static final int CHAT_ROW_RECALL_MESSAGE_RECEIVE = 1124;
    private static final int CHAT_ROW_RECALL_MESSAGE_SEND = 1125;


    private static final int CHAT_ROW_SHARE_PARTY_SEND = 1126;
    private static final int CHAT_ROW_SHARE_PARTY_RECEIVE = 1127;


    private Context context;
    private RecyclerView listView;
    private EMConversation conversation;
    private List<EMMessage> data = new ArrayList<>();
    private ChatMessageList.MessageListItemClickListener itemClickListener;
    private int position = 0;

    private boolean isAnimatorOpen = true;

    private String userName;
    private int chatType;

    private int pageSize = 20;
    private int pageNum = 1;

    public ChatMessageAdapter2(Context context, String username, int chatType, RecyclerView listView)
    {
        this.context = context;
        this.listView = listView;
        this.userName = username;
        this.chatType = chatType;
        this.conversation = EMClient.getInstance().chatManager().getConversation(username, ChatCommonUtils.getConversationType(chatType), true);
    }

    private Handler handler = new Handler(Looper.getMainLooper())
    {
        /**
         * 一开始初始化数据(20条)
         */
        private void initListData()
        {
            if (conversation == null)
                conversation = EMClient.getInstance().chatManager().getConversation(userName, ChatCommonUtils.getConversationType(chatType), true);
            Logger.t(TAG).d("conversation里消息数：" + conversation.getAllMessages().size() + " | " + conversation.getAllMsgCount());
            int position = conversation.getAllMessages().size() - 1;//
            data.clear();
            if (conversation.getAllMessages() != null && conversation.getAllMsgCount() > pageSize)
            {
                conversation.loadMoreMsgFromDB(conversation.getAllMessages().get(position).getMsgId(), pageSize);
                data.addAll(conversation.getAllMessages());
                Logger.t(TAG).d("chat------>data:1:" + data.size());
            }
            else
            {
                Logger.t(TAG).d("" + conversation.getAllMessages());
                if (conversation.getAllMessages().size() != 0)
                {
                    conversation.loadMoreMsgFromDB(conversation.getAllMessages().get(position).getMsgId(), pageSize - conversation.getAllMsgCount());
                }
                data.addAll(conversation.getAllMessages());
                Logger.t(TAG).d("chat------>data:2:" + data.size());
            }

            for (EMMessage message : data)
            {
                //语音和视频要 听 或看 之后才算已读
                if (message.getType() != EMMessage.Type.VOICE && message.getType() != EMMessage.Type.VIDEO)
                    EaseCommonUtils.makeMessageAsRead(TAG + ".initListData", conversation, message, true);
                else
                    EaseCommonUtils.makeMessageAsRead(TAG + ".initListData", conversation, message, false);
            }
            notifyDataSetChanged();
        }

        /**
         * 刷新最后一条新增消息
         */
        private void refreshLast(EMMessage message)
        {
            Logger.t(TAG).d("refreshLast:" + message);
            /*if (message.direct() == EMMessage.Direct.SEND)
            {
                if (message.getType() == EMMessage.Type.VOICE && !message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_VOICE_TEMP_MESSAGE, false) && (getItemCount() > 0))
                {
                    if (data.get(getItemCount() - 1).getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_VOICE_TEMP_MESSAGE, false))
                    {
                        data.set(getItemCount() - 1, message);
                        notifyItemChanged(getItemCount() - 1);
                    }
                }
                else
                {
                    data.add(message);
                    notifyItemInserted(getItemCount() - 1);
                }
            }
            else
            {
                data.add(message);
                notifyItemInserted(getItemCount() - 1);
            }*/

            data.add(message);
            notifyItemInserted(getItemCount() - 1);

        }

        /**
         * 下拉加载更多消息
         */
        private void loadMoreMessage()
        {
            Logger.t(TAG).d("--------->" + getItem(0).getBody());
            List<EMMessage> msg = conversation.loadMoreMsgFromDB(getItem(0).getMsgId(), pageSize);
            for (EMMessage message : msg)
            {
                //语音和视频要 听 或看 之后才算已读
                if (message.getType() != EMMessage.Type.VOICE && message.getType() != EMMessage.Type.VIDEO)
                    EaseCommonUtils.makeMessageAsRead(TAG + ".loadMoreMessage", conversation, message, true);
                else
                    EaseCommonUtils.makeMessageAsRead(TAG + ".loadMoreMessage", conversation, message, false);
            }
            data.addAll(0, msg);
            notifyDataSetChanged();
        }

        /**
         * 刷新adapter
         */
        private void notifyDataChanged()
        {
            notifyDataSetChanged();
        }

        private void clearListData()
        {
            data.clear();
            notifyDataSetChanged();
        }

        /**
         * 移除最后一条item
         */
        private void notifyLastDataRemoved()
        {
            if (data.size() > 0)
            {
                data.remove(getItemCount() - 1);
                notifyItemRemoved(getItemCount());
            }

        }

        @Override
        public void handleMessage(android.os.Message message)
        {
            switch (message.what)
            {
                case HANDLER_MESSAGE_INIT_LIST:
                    initListData();
                    break;
                case HANDLER_MESSAGE_REFRESH_LAST:
                    EMMessage msg = (EMMessage) message.obj;
                    refreshLast(msg);
                    break;
                case HANDLER_MESSAGE_LOAD_MORE:
                    loadMoreMessage();
                    break;
                case HANDLER_MESSAGE_NOTIFY_DATA:
                    notifyDataChanged();
                    break;
                case HANDLER_MESSAGE_CLEAR_DATA:
                    clearListData();
                    break;
                case HANDLER_MESSAGE_NOTIFY_ITEM_REMOVE:
                    notifyLastDataRemoved();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (data.size() > 0)
                    {
                        Logger.t(TAG).d("消息数目：" + (data.size() - 1));
                        listView.scrollToPosition(data.size() - 1);
                    }
                    Logger.t(TAG).d("chat------>list滚动到底部：listView.getBottom():" + listView.getBottom());
                    new Handler().postDelayed(() -> listView.smoothScrollBy(0, listView.getBottom()), 200);
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = message.arg1;
                    Logger.t(TAG).d("消息数目position:" + position);
                    listView.scrollToPosition(position);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取当前聊天记录里所有图片url
     *
     * @return
     */
    public Map<String, String> getAllImageUrl()
    {
        Map<String, String> map = new LinkedHashMap<>();
        for (EMMessage message : data)
        {
            if (message.getType() == EMMessage.Type.IMAGE)
            {
                EMImageMessageBody body = (EMImageMessageBody) message.getBody();
                String url;
                File file = new File(body.getLocalUrl());
                if (file.exists())
                    url = Uri.fromFile(file).toString();
                else
                    url = TextUtils.isEmpty(body.getThumbnailUrl()) ? body.getRemoteUrl() : body.getThumbnailUrl();
                map.put(message.getMsgId(), url);
            }
        }
        return map;
    }

    /**
     * 注意判空
     *
     * @param position
     * @return
     */
    public EMMessage getItem(int position)
    {
        if (data != null && data.size() != 0 && data.size() > position)
            return data.get(position);
        else
            return null;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder)
    {
        Logger.t(TAG).d("onViewRecycled()");
        if (holder != null)
        {
            if (holder.itemView instanceof ChatRowImage)
            {
                ((ChatRowImage) holder.itemView).release();
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        EMMessage message = data.get(position);
        Logger.t(TAG).d(">>>>>>>>>>>>>>>>>>>>>>>onCreateViewHolder()> messageID: " + message.getMsgId() + "msg" + message.getBody());
        View chatRow;
        switch (viewType)
        {
            case CHAT_ROW_TEXT_SEND:
                chatRow = new ChatRowText(context, message, position, this);
                return new ChatRowTextSendHolder(chatRow);
            case CHAT_ROW_TEXT_RECEIVE:
                chatRow = new ChatRowText(context, message, position, this);
                return new ChatRowTextReceiveHolder(chatRow);
            case CHAT_ROW_BIG_EXPRESSION_SEND:
                chatRow = new ChatRowBigExpression(context, message, position, this);
                return new ChatRowBigExpressionSendHolder(chatRow);
            case CHAT_ROW_BIG_EXPRESSION_RECEIVE:
                chatRow = new ChatRowBigExpression(context, message, position, this);
                return new ChatRowBigExpressionReceiveHolder(chatRow);
            case CHAT_ROW_RED_PACKET_SEND:
                chatRow = new ChatRowRedPacket(context, message, position, this);
                return new ChatRowRedPacketSendHolder(chatRow);
            case CHAT_ROW_RED_PACKET_RECEIVE:
                chatRow = new ChatRowRedPacket(context, message, position, this);
                return new ChatRowRedPacketReceiveHolder(chatRow);
            case CHAT_ROW_RED_PACKET_ACK_SEND:
            {
                ChatRowRedPacketAck2 chatRow2 = new ChatRowRedPacketAck2(context);
                chatRow2.init(message, position, this);
                return new ChatRowRedPacketAckSendHolder(chatRow2);
            }
            case CHAT_ROW_RED_PACKET_ACK_RECEIVE:
                /*chatRow = new ChatRowRedPacketAck2(context, message, position, this);
                return new ChatRowRedPacketAckReceiveHolder(chatRow);*/
                ChatRowRedPacketAck2 chatRow2 = new ChatRowRedPacketAck2(context);
                chatRow2.init(message, position, this);
                return new ChatRowRedPacketAckReceiveHolder(chatRow2);

            case ChAT_ROW_LOCATION_SEND:
                chatRow = new ChatRowLocation(context, message, position, this);
                return new ChatRowLocationSendHolder(chatRow);
            case ChAT_ROW_LOCATION_RECEIVE:
                chatRow = new ChatRowLocation(context, message, position, this);
                return new ChatRowLocationReceiveHolder(chatRow);
            case CHAT_ROW_FILE_SEND:
                chatRow = new ChatRowFile(context, message, position, this);
                return new ChatRowFileSendHolder(chatRow);
            case CHAT_ROW_FILE_RECEIVE:
                chatRow = new ChatRowFile(context, message, position, this);
                return new ChatRowFileReceiveHolder(chatRow);
            case CHAT_ROW_IMAGE_SEND:
                chatRow = new ChatRowImage(context, message, position, this);
                return new ChatRowImageSendHolder(chatRow);
            case CHAT_ROW_IMAGE_RECEIVE:
                chatRow = new ChatRowImage(context, message, position, this);
                return new ChatRowImageReceiveHolder(chatRow);
            case CHAT_ROW_VOICE_SEND:
                chatRow = new ChatRowVoice(context, message, position, this);
                return new ChatRowVoiceSendHolder(chatRow);
            case CHAT_ROW_VOICE_RECEIVE:
                chatRow = new ChatRowVoice(context, message, position, this);
                return new ChatRowVoiceReceiveHolder(chatRow);
            case CHAT_ROW_VIDEO_SEND:
                chatRow = new ChatRowVideo(context, message, position, this);
                return new ChatRowVideoSendHolder(chatRow);
            case CHAT_ROW_VIDEO_RECEIVE:
                chatRow = new ChatRowVideo(context, message, position, this);
                return new ChatRowVideoReceiveHolder(chatRow);
            case CHAT_ROW_SHARE_SEND:
            case  CHAT_ROW_SHARE_PARTY_SEND:
                chatRow = new ChatRowLiveSharedAck(context, message, position, this);
                return new ChatRowLiveSharedAckSendHolder(chatRow);
            case CHAT_ROW_SHARE_RECEIVE:
            case  CHAT_ROW_SHARE_PARTY_RECEIVE:
                chatRow = new ChatRowLiveSharedAck(context, message, position, this);
                return new ChatRowLiveSharedAckReceiveHolder(chatRow);
            case CHAT_ROW_GAME_SEND:
                chatRow = new ChatRowGame(context, message, position, this);
                return new ChatRowGameSendHolder(chatRow);
            case CHAT_ROW_GAME_RECEIVE:
                chatRow = new ChatRowGame(context, message, position, this);
                return new ChatRowGameReceiveHolder(chatRow);
            case CHAT_ROW_VOICE_TEMP_SEND:
                chatRow = new ChatRowVoiceTemp(context, message, position, this);
                return new ChatRowVoiceTempHolder(chatRow);

            case CHAT_ROW_RECALL_MESSAGE_RECEIVE:
            {
                ChatRowRecallMessage recallMessage = new ChatRowRecallMessage(context);
                recallMessage.init(message, position, this);
                return new ChatRowRecallMessageReceiveHolder(recallMessage);
            }
            case CHAT_ROW_RECALL_MESSAGE_SEND:
                ChatRowRecallMessage recallMessage = new ChatRowRecallMessage(context);
                recallMessage.init(message, position, this);
                return new ChatRowRecallMessageSendHolder(recallMessage);
            default:
                chatRow = new ChatRowText(context, message, position, this);
                return new ChatRowTextSendHolder(chatRow);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        Logger.t(TAG).d(">>>>>>>>>>>>>>>>>>>>>>>getItemViewType()> position: " + position);
        this.position = position;
        int viewTypeResult;
        EMMessage message = data.get(position);
        switch (message.getType())
        {
            case TXT:
                if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false))
                {
                    if (message.direct() == EMMessage.Direct.SEND)
                        viewTypeResult = CHAT_ROW_RED_PACKET_SEND;
                    else
                        viewTypeResult = CHAT_ROW_RED_PACKET_RECEIVE;
                }
                else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false))
                {
                    if (message.direct() == EMMessage.Direct.SEND)
                        viewTypeResult = CHAT_ROW_RED_PACKET_ACK_SEND;
                    else
                        viewTypeResult = CHAT_ROW_RED_PACKET_ACK_RECEIVE;
                }
                else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false))
                {
                    if (message.direct() == EMMessage.Direct.SEND)
                        viewTypeResult = CHAT_ROW_BIG_EXPRESSION_SEND;
                    else
                        viewTypeResult = CHAT_ROW_BIG_EXPRESSION_RECEIVE;
                }
                else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_LIVE_SHARE, false))
                {
                    if (message.direct() == EMMessage.Direct.SEND)
                        viewTypeResult = CHAT_ROW_SHARE_SEND;
                    else
                        viewTypeResult = CHAT_ROW_SHARE_RECEIVE;
                }
                else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_GAME_MESSAGE, false))
                {
                    if (message.direct() == EMMessage.Direct.SEND)
                        viewTypeResult = CHAT_ROW_GAME_SEND;
                    else
                        viewTypeResult = CHAT_ROW_GAME_RECEIVE;
                }
                else if (message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, false) &&
                        message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_VOICE_TEMP_MESSAGE, false))
                {
                    viewTypeResult = CHAT_ROW_VOICE_TEMP_SEND;
                }
                // TODO: 2018/3/5 lclclclc 
                else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_PARTY_SHARE, false))
                {
                    if (message.direct() == EMMessage.Direct.SEND)
                        viewTypeResult = CHAT_ROW_SHARE_PARTY_SEND;
                    else
                        viewTypeResult = CHAT_ROW_SHARE_PARTY_RECEIVE;
                }
                else
                {
                    if (message.direct() == EMMessage.Direct.SEND)
                        viewTypeResult = CHAT_ROW_TEXT_SEND;
                    else
                        viewTypeResult = CHAT_ROW_TEXT_RECEIVE;
                }
                break;
            case IMAGE:
                if (message.direct() == EMMessage.Direct.SEND)
                    viewTypeResult = CHAT_ROW_IMAGE_SEND;
                else
                    viewTypeResult = CHAT_ROW_IMAGE_RECEIVE;
                break;
            case VIDEO:
                if (message.direct() == EMMessage.Direct.SEND)
                    viewTypeResult = CHAT_ROW_VIDEO_SEND;
                else
                    viewTypeResult = CHAT_ROW_VIDEO_RECEIVE;
                break;
            case LOCATION:
                if (message.direct() == EMMessage.Direct.SEND)
                    viewTypeResult = ChAT_ROW_LOCATION_SEND;
                else
                    viewTypeResult = ChAT_ROW_LOCATION_RECEIVE;
                break;
            case VOICE:
                if (message.direct() == EMMessage.Direct.SEND)
                    viewTypeResult = CHAT_ROW_VOICE_SEND;
                else
                    viewTypeResult = CHAT_ROW_VOICE_RECEIVE;
                break;
            case FILE:
                if (message.direct() == EMMessage.Direct.SEND)
                    viewTypeResult = CHAT_ROW_FILE_SEND;
                else
                    viewTypeResult = CHAT_ROW_FILE_RECEIVE;
                break;
            default:
                if (message.direct() == EMMessage.Direct.SEND)
                    viewTypeResult = CHAT_ROW_TEXT_SEND;
                else
                    viewTypeResult = CHAT_ROW_TEXT_RECEIVE;
        }
        if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_RECALL_MESSAGE, false))
        {
            if (message.direct() == EMMessage.Direct.SEND)
                viewTypeResult = CHAT_ROW_RECALL_MESSAGE_SEND;
            else
                viewTypeResult = CHAT_ROW_RECALL_MESSAGE_RECEIVE;
        }
        return viewTypeResult;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        Logger.t(TAG).d(">>>>>>>>>>>>>>>>>>>>>>>onBindViewHolder()> holder: " + holder + " | position:" + position);
        EMMessage message = data.get(position);
        /*if (holder.itemView instanceof ChatRowImage)
        {
            preLoadImage(message);
            ((ChatRow) holder.itemView).setUpView(message, position, itemClickListener);
//            makeImageData(holder, message);
        }
        else */
        if (holder.itemView instanceof ChatRow)
            ((ChatRow) holder.itemView).setUpView(message, position, itemClickListener);
        else if (holder.itemView instanceof ChatRowRedPacketAck2)
            ((ChatRowRedPacketAck2) holder.itemView).onSetUpView(message);
        else if (holder.itemView instanceof ChatRowRecallMessage)
            ((ChatRowRecallMessage) holder.itemView).onSetUpView(message);
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public void refresh()
    {
        if (handler.hasMessages(HANDLER_MESSAGE_NOTIFY_DATA))
        {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_NOTIFY_DATA);
        handler.sendMessage(msg);
    }

    public void initListData()
    {
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_INIT_LIST);
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_INIT_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    public void clearData()
    {
        if (handler.hasMessages(HANDLER_MESSAGE_CLEAR_DATA))
        {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_CLEAR_DATA);
        handler.sendMessage(msg);
    }

    /**
     * 刷新新增一条数目 并select the last
     */
    public void refreshAddItem(final EMMessage message)
    {
        // final int TIME_DELAY_REFRESH_SELECT_LAST = 200;
//        handler.removeMessages(HANDLER_MESSAGE_REFRESH_LAST);
//        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LAST);
        msg.obj = message;
        handler.sendMessage(msg);
        handler.sendEmptyMessage(HANDLER_MESSAGE_SELECT_LAST);
//        handler.sendMessageDelayed(msg, TIME_DELAY_REFRESH_SELECT_LAST);
//        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    /**
     * refresh and select the last
     */
    public void refreshSelectLast()
    {
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_NOTIFY_DATA);
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_NOTIFY_DATA, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    /**
     * select the last
     */
    public void selectLast()
    {
        LinearLayoutManager manager = (LinearLayoutManager) listView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
        if (lastVisibleItemPosition == data.size() - 1)
        {
            final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
            handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
            handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
        }
    }

    public void click2SelectLast()
    {
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    public void smoothScroll2Last()
    {
        listView.smoothScrollBy(0, listView.getBottom());
    }

    /**
     * refresh and seek to the position
     */
    public void refreshSeekTo(int position)
    {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_LOAD_MORE));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }

    public void removeLastItem()
    {
        if (handler.hasMessages(HANDLER_MESSAGE_NOTIFY_ITEM_REMOVE))
        {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_NOTIFY_ITEM_REMOVE);
        handler.sendMessage(msg);
    }

    public void addVoiceTempMessage(String toUserName, String remark)
    {
        EMMessage message = EMMessage.createTxtSendMessage("111", toUserName);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_UID, SharePreUtils.getUId(context));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_ID, SharePreUtils.getId(context));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_DEVICE_TYPE, EamConstant.DEVICE_TYPE);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, SharePreUtils.getNicName(context));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE, SharePreUtils.getHeadImg(context));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_LEVEL, SharePreUtils.getLevel(context));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_GENDER, SharePreUtils.getSex(context));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_AGE, SharePreUtils.getAge(context));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_VUSER, SharePreUtils.getIsVUser(context));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, true);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_VOICE_TEMP_MESSAGE, true);
        message.setMsgId(EamConstant.MESSAGE_ATTR_VOICE_TEMP_ID);
        refreshAddItem(message);
//        data.add(message);
//        notifyItemInserted(getItemCount() - 1);
//        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
//        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
//        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }


    public void setItemClickListener(ChatMessageList.MessageListItemClickListener listener)
    {
        itemClickListener = listener;
    }

    public void removeMessage(String msgId, boolean isNeedNotify)
    {
        if (getItem(0) != null)
        {
            int position = 0;
            for (int i = 0; i < data.size(); i++)
            {
                if (data.get(i).getMsgId().equals(msgId))
                {
                    position = i;
                    break;
                }
            }
            data.remove(position);
            if (isNeedNotify)
                notifyItemRemoved(position);
        }
    }

    /**
     * 撤回消息
     *
     * @param msgId
     */
    public void recallMessage(String msgId, EMMessage recallMessage)
    {
        if (getItem(0) != null)
        {
            int position = 0;
            for (int i = 0; i < data.size(); i++)
            {
                if (data.get(i).getMsgId().equals(msgId))
                {
                    position = i;
                    break;
                }
            }
//            refreshData(position, recallMessage);
            data.set(position, recallMessage);
            int finalPos = position;
            closeDefaultAnimator();
            ((Activity) context).runOnUiThread(() -> notifyItemChanged(finalPos));
        }
    }

    /**
     * 刷新 某条消息
     *
     * @param msgId 消息ID
     */
    public void notifyMessage(String msgId)
    {
        if (getItem(0) != null)
        {
            int position = 0;
            for (int i = 0, len = data.size(); i < len; i++)
            {
                if (data.get(i).getMsgId().equals(msgId))
                {
                    position = i;
                    break;
                }
            }
            Logger.t(TAG).d("=======>position:" + position + "data.get(i):" + data.get(position));
            notifyItemChanged(position);
        }
    }

    /**
     * 替换某条数据
     *
     * @param position 位置
     * @param message  消息
     */
    public void refreshData(int position, EMMessage message)
    {
        data.set(position, message);
        ((Activity) context).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                notifyItemChanged(position);
            }
        });

    }

    public void refreshData(EMMessage message)
    {
        String msgId = message.getMsgId();
        if (getItem(0) != null)
        {
            int position = 0;
            for (int i = 0, len = data.size(); i < len; i++)
            {
                if (data.get(i).getMsgId().equals(msgId))
                {
                    position = i;
                    break;
                }
            }
            refreshData(position, message);
        }
    }

    /**
     * 获取聊天最后一条消息
     *
     * @return
     */
    public EMMessage getLastMessage()
    {
        EMMessage message = null;
        if (getItem(0) != null)
            message = data.get(data.size() - 1);
        return message;
    }

    public void refreshNewData()
    {
        if (getItem(0) != null)
        {
            int loadNum = data.size();
            if (conversation != null)
            {
                if (conversation.getAllMessages() != null && conversation.getAllMessages().size() > loadNum)
                {
                    int position = conversation.getAllMessages().size() - 1;
                    data.clear();
                    data.addAll(conversation.loadMoreMsgFromDB(conversation.getAllMessages().get(position).getMsgId(), loadNum));
                    data.add(conversation.getAllMessages().get(position));
                    Logger.t(TAG).d("data:1:" + data.size());
//                data.addAll(allMessage.subList(0, pageSize * pageNum));
                }
                else
                {
                    Logger.t(TAG).d("" + conversation.getAllMessages());
                    data.addAll(conversation.getAllMessages());
                    Logger.t(TAG).d("data:2:" + data.size());
                }
                notifyDataSetChanged();
                click2SelectLast();
            }
        }

    }

    public boolean isAnimatorOpen()
    {
        return isAnimatorOpen;
    }

    /**
     * 打开默认局部刷新动画
     */
    public void openDefaultAnimator()
    {
        isAnimatorOpen = true;
        listView.getItemAnimator().setAddDuration(120);
        listView.getItemAnimator().setChangeDuration(250);
        listView.getItemAnimator().setMoveDuration(250);
        listView.getItemAnimator().setRemoveDuration(120);
        ((SimpleItemAnimator) listView.getItemAnimator()).setSupportsChangeAnimations(true);
    }

    /**
     * 关闭默认局部刷新动画
     */
    public void closeDefaultAnimator()
    {
        isAnimatorOpen = false;
        listView.getItemAnimator().setAddDuration(0);
        listView.getItemAnimator().setChangeDuration(0);
        listView.getItemAnimator().setMoveDuration(0);
        listView.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) listView.getItemAnimator()).setSupportsChangeAnimations(false);
    }


    class ChatRowTextSendHolder extends RecyclerView.ViewHolder
    {
        public ChatRowTextSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowTextReceiveHolder extends RecyclerView.ViewHolder
    {
        public ChatRowTextReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowBigExpressionSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowBigExpressionSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowBigExpressionReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowBigExpressionReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowRedPacketSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowRedPacketSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowRedPacketReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowRedPacketReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowRedPacketAckSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowRedPacketAckSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowRedPacketAckReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowRedPacketAckReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowRecallMessageSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowRecallMessageSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowRecallMessageReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowRecallMessageReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }


    class ChatRowLocationSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowLocationSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowLocationReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowLocationReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowFileSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowFileSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowFileReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowFileReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowImageSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowImageSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowImageReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowImageReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowVoiceSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowVoiceSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowVoiceReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowVoiceReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowVoiceTempHolder extends RecyclerView.ViewHolder
    {

        public ChatRowVoiceTempHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowVideoSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowVideoSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowVideoReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowVideoReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowLiveSharedAckSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowLiveSharedAckSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowLiveSharedAckReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowLiveSharedAckReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowGameSendHolder extends RecyclerView.ViewHolder
    {

        public ChatRowGameSendHolder(View itemView)
        {
            super(itemView);
        }
    }

    class ChatRowGameReceiveHolder extends RecyclerView.ViewHolder
    {

        public ChatRowGameReceiveHolder(View itemView)
        {
            super(itemView);
        }
    }


}
