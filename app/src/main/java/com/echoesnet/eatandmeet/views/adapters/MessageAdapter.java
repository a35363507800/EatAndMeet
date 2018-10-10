/*
package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.echoesnet.eatandmeet.utils.ChatCommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.views.widgets.chat.ChatMessageList;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRow;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowBigExpression;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowFile;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowImage;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowLocation;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowText;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRowVoice;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.CustomChatRowProvider;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

*/
/**
 * Created by Administrator on 2017/7/12.
 *//*


public class MessageAdapter extends BaseAdapter
{
    private final static String TAG = MessageAdapter.class.getSimpleName();

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private Context context;
    private ListView listView;
    private String toChatUsername;

    EMMessage[] messages = null;

    private Drawable myBubbleBg;
    private Drawable otherBuddleBg;

    private EMConversation conversation;
    //private EaseMessageListItemStyle itemStyle;
    private ChatMessageList.MessageListItemClickListener itemClickListener;
    private CustomChatRowProvider customRowProvider;


    public MessageAdapter(Context context, String username, int chatType, ListView listView)
    {
        this.context = context;
        this.listView = listView;
        toChatUsername = username;
        this.conversation = EMClient.getInstance().chatManager().getConversation(username, ChatCommonUtils.getConversationType(chatType), true);
    }

    Handler handler = new Handler()
    {
        private void refreshList()
        {
            // you should not call getAllMessages() in UI thread
            // otherwise there is problem when refreshing UI and there is new message arrive
            java.util.List<EMMessage> var = conversation.getAllMessages();
            messages = var.toArray(new EMMessage[var.size()]);
            conversation.markAllMessagesAsRead();
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(android.os.Message message)
        {
            switch (message.what)
            {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (messages.length > 0)
                    {
                        listView.setSelection(messages.length - 1);
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = message.arg1;
                    listView.setSelection(position);
                    break;
                default:
                    break;
            }
        }
    };

    public void refresh()
    {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST))
        {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    */
/**
     * refresh and select the last
     *//*

    public void refreshSelectLast()
    {
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_REFRESH_LIST);
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    */
/**
     * refresh and seek to the position
     *//*

    public void refreshSeekTo(int position)
    {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }


    public int getCount()
    {
        return messages == null ? 0 : messages.length;
    }

    public EMMessage getItem(int position)
    {
        if (messages != null && position < messages.length)
        {
            return messages[position];
        }
        return null;
    }

    public long getItemId(int position)
    {
        return position;
    }

    */
/**
     * get number of message type, here 14 = (EMMessage.Type) * 2
     *//*

    public int getViewTypeCount()
    {
        if (customRowProvider != null && customRowProvider.getCustomChatRowTypeCount() > 0)
        {
            return customRowProvider.getCustomChatRowTypeCount() + 14;
        }
        return 14;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        EMMessage message = messages[position];
        if (convertView == null)
        {
            convertView = createChatRow(context, message, position);
        }

        //refresh ui with messages
        ((ChatRow) convertView).setUpView(message, position, itemClickListener);

        return convertView;
    }

    private ChatRow createChatRow(Context context, EMMessage message, int position) {
        ChatRow chatRow = null;
        if(customRowProvider != null && customRowProvider.getCustomChatRow(message, position, this) != null){
            return customRowProvider.getCustomChatRow(message, position, this);
        }
        switch (message.getType()) {
            case TXT:
                if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                    chatRow = new ChatRowBigExpression(context, message, position, this);
                }else{
                    chatRow = new ChatRowText(context, message, position, this);
                }
                break;
            case LOCATION:
                chatRow = new ChatRowLocation(context, message, position, this);
                break;
            case FILE:
                chatRow = new ChatRowFile(context, message, position, this);
                break;
            case IMAGE:
                chatRow = new ChatRowImage(context, message, position, this);
                break;
            case VOICE:
                chatRow = new ChatRowVoice(context, message, position, this);
                break;
            case VIDEO:
//                chatRow = new ChatRowVideo(context, message, position, this);
                break;
            default:
                break;
        }

        return chatRow;
    }

*/
/*    public void setItemStyle(EaseMessageListItemStyle itemStyle){
        this.itemStyle = itemStyle;
    }*//*


    public void setItemClickListener(ChatMessageList.MessageListItemClickListener listener)
    {
        itemClickListener = listener;
    }

    public void setCustomChatRowProvider(CustomChatRowProvider rowProvider)
    {
        customRowProvider = rowProvider;
    }


    public Drawable getMyBubbleBg() {
        return myBubbleBg;
    }


    public Drawable getOtherBubbleBg() {
        return otherBuddleBg;
    }

}
*/
