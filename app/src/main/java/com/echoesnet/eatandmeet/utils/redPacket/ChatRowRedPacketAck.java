package com.echoesnet.eatandmeet.utils.redPacket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRow;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by wangben on 2016/7/26.
 * 红包发送与领取回执信息
 */
public class ChatRowRedPacketAck extends ChatRow
{
    private TextView mTvMessage;

    public ChatRowRedPacketAck(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false))
        {
            inflater.inflate(R.layout.em_row_red_packet_ack_message,this);
/*            RelativeLayout redRow= (RelativeLayout) inflater.inflate(R.layout.em_row_red_packet_ack_message,null, false);
            LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity= Gravity.CENTER_HORIZONTAL;
            params.weight=1;
            params.setMargins(100,0,0,0);
            addView(redRow,params);
            setBackgroundResource(R.color.C0311P);
            Logger.t(TAG).d("RedRow>"+getMeasuredWidth());*/
            //requestLayout();
/*            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.em_row_red_packet_ack_message : R.layout.em_row_red_packet_ack_message, this);*/
        }
    }

    @Override
    protected void onFindViewById()
    {
        mTvMessage = (TextView) findViewById(R.id.ease_tv_money_msg);
    }

    @Override
    protected void onUpdateView()
    {

    }

    @Override
    protected void onSetUpView()
    {
        try
        {
            String currentUser = EMClient.getInstance().getCurrentUser();
            String fromUser = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME);//红包发送者
            String toUser = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME);//红包接收者
            String senderId;
            if (message.direct() == EMMessage.Direct.SEND)
            {
                if (message.getChatType().equals(EMMessage.ChatType.GroupChat))
                {
                    senderId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID);
                    if (senderId.equals(currentUser))
                    {
                        mTvMessage.setText(Html.fromHtml(String.format("<font color=%1$s>你领取了自己的</font><font color=%2$s>红包</font>","#999999","#9d45f9")));
                    }
                    else
                    {
                        mTvMessage.setText(Html.fromHtml(String.format("<font color=%1$s>你收到了来自%2$s的</font><font color=%3$s>红包</font>", "#999999",fromUser,"#9d45f9")));
                    }
                }
                else
                {
                    mTvMessage.setText(Html.fromHtml(String.format("<font color=%1$s>你领取了“</font><font color=%2$s>%3$s</font><font color=%4$s>”的红包</font>", "#999999","#9d45f9",fromUser,"#999999")));
                }
            }
            else
            {
                mTvMessage.setText(Html.fromHtml(String.format("<font color=%1$s>“</font><font color=%2$s>%3$s</font><font color=%4$s>”领取了你的红包</font>", "#999999","#9d45f9",toUser,"#999999")));
            }
        } catch (HyphenateException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onBubbleClick()
    {

    }

    @Override
    protected void onBubbleLongClick()
    {

    }
}
