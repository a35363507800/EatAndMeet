package com.echoesnet.eatandmeet.utils.redPacket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRow;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by wangben on 2016/7/26.
 */
public class ChatRowRedPacket extends ChatRow
{
//    /**
//     * 祝福语
//     */
//    private TextView mTvGreeting;
//    /**
//     * 红包下面文字
//     */
//    private TextView mTvSponsorName;

    public ChatRowRedPacket(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false))
        {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.em_row_received_red_packet : R.layout.em_row_sent_red_packet, this);
        }
    }

    @Override
    protected void onFindViewById()
    {
//        mTvGreeting = (TextView) findViewById(R.id.tv_money_greeting);
//        mTvSponsorName = (TextView) findViewById(R.id.tv_sponsor_name);
    }

    @Override
    protected void onUpdateView()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onSetUpView()
    {
        try
        {
            String sponsorName = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_FROM_NICNAME);
            String greetings = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_GREETING);
//            mTvGreeting.setText(greetings);
//            mTvSponsorName.setText(sponsorName);
        } catch (HyphenateException e)
        {
            e.printStackTrace();
        }
        handleTextMessage();
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
