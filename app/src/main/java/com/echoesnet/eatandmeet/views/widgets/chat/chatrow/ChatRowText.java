package com.echoesnet.eatandmeet.views.widgets.chat.chatrow;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

public class ChatRowText extends ChatRow
{

    private TextView contentView;


    public ChatRowText(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
    }

    @Override
    protected void onFindViewById()
    {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
    }

    @Override
    public void onSetUpView()
    {
        String expressName = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_EXPRESSION_NAME, "");
        if (!TextUtils.isEmpty(expressName))
        {
            Spannable span = EamSmileUtils.getSmiledText(context, expressName);
            // 设置内容
            contentView.setText(span, BufferType.SPANNABLE);

//            bubbleLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
            bubbleLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.transparent));
        }
        else
        {
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            String text = txtBody.getMessage();
            Spannable span;
            if (message.direct() == EMMessage.Direct.SEND)
            {
                String sensitiveContent = message.getStringAttribute(Constant.MESSAGE_ATTR_SENSITIVE_CONTENT, "");
                if (!TextUtils.isEmpty(sensitiveContent))//发送方 带有敏感词原文
                {
                    span = EamSmileUtils.getSmiledText(context, sensitiveContent);
                }
                else
                    span = EamSmileUtils.getSmiledText(context, text);
            }
            else
                span = EamSmileUtils.getSmiledText(context, text);
            // 设置内容
            contentView.setText(span, BufferType.SPANNABLE);

            if (message.direct() == EMMessage.Direct.SEND)
                bubbleLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.chat_sender_bg));
            else
                bubbleLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.chat_receiver_bg));
        }
//        handleTextMessage();
    }

    /*protected void handleTextMessage()
    {
        if (message.direct() == EMMessage.Direct.SEND)
        {
            setMessageSendCallback();
            switch (message.status())
            {
                case CREATE:
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    if (ackedView != null)
                    {
                        ackedView.setText(context.getString(R.string.text_delivered_msg));
                        ackedView.setBackgroundResource(R.drawable.round_btn_c0312);
                    }
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
        else
        {
            if (!message.isAcked() && message.getChatType() == ChatType.Chat)
            {
                try
                {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }*/

    @Override
    protected void onUpdateView()
    {
        adapter.notifyItemChanged(position);
    }

    @Override
    protected void onBubbleClick()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onBubbleLongClick()
    {

    }


}
