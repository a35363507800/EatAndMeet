package com.echoesnet.eatandmeet.utils.redPacket;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.views.adapters.ChatMessageAdapter2;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;

import java.util.Date;

/**
 * Created by wangben on 2016/7/26.
 * 红包发送与领取回执信息
 */
public class ChatRowRedPacketAck2 extends RelativeLayout
{
    private TextView mTvMessage, timestamp;
    private Context mContext;
    private EMMessage message;
    private int position;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    public ChatRowRedPacketAck2(Context context)
    {
        super(context);
        mContext = context;
        initView();
    }

    private void initView()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.em_row_red_packet_ack_message, this);
        mTvMessage = (TextView) view.findViewById(R.id.ease_tv_money_msg);
        timestamp = (TextView) view.findViewById(R.id.timestamp);
    }

    public void init(EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        this.message = message;
        this.position = position;
        this.adapter = adapter;
    }


    public void onSetUpView(EMMessage messages)
    {
        this.message = messages;
        setUpBaseView();
        try
        {
            String currentUser = EMClient.getInstance().getCurrentUser();
            String fromUser = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME);//红包发送者
            String toUser = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME);//红包接收者
            String senderId;
            SpannableStringBuilder sb;
            if (message.direct() == EMMessage.Direct.SEND)
            {
                if (message.getChatType().equals(EMMessage.ChatType.GroupChat))
                {
                    senderId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID);
                    if (senderId.equals(currentUser))
                    {
                        sb = getTextColor("你领取了自己的", ContextCompat.getColor(mContext,R.color.C0322));
                        sb.append(getTextColor("红包", ContextCompat.getColor(mContext,R.color.C0412)));
                        mTvMessage.setText(sb);
//                        mTvMessage.setText(Html.fromHtml(String.format("<font color=%1$s>你领取了自己的</font><font color=%2$s>红包</font>", "#999999", "#9d45f9")));
                    }
                    else
                    {
                        sb = getTextColor("你收到了来自"+fromUser+"的", ContextCompat.getColor(mContext,R.color.C0322));
                        sb.append(getTextColor("红包", ContextCompat.getColor(mContext,R.color.C0412)));
                        mTvMessage.setText(sb);
//                        mTvMessage.setText(Html.fromHtml(String.format("<font color=%1$s>你收到了来自%2$s的</font><font color=%3$s>红包</font>", "#999999", fromUser, "#9d45f9")));
                    }
                }
                else
                {
                    EaseUser user = HuanXinIMHelper.getInstance().getUserInfo(message.getTo());
                    if (user != null)
                        if (!TextUtils.isEmpty(user.getRemark()))
                            fromUser = user.getRemark();
                    sb = getTextColor("你领取了“", ContextCompat.getColor(mContext,R.color.C0322));
                    sb.append(getTextColor(fromUser, ContextCompat.getColor(mContext,R.color.C0412)));
                    sb.append(getTextColor("”的红包", ContextCompat.getColor(mContext,R.color.C0322)));
                    mTvMessage.setText(sb);
//                    mTvMessage.setText(Html.fromHtml(String.format("<font color=%1$s>你领取了“</font><font color=%2$s>%3$s</font><font color=%4$s>”的红包</font>", "#999999", "#9d45f9", fromUser, "#999999")));
                }
            }
            else
            {
                EaseUser user = HuanXinIMHelper.getInstance().getUserInfo(message.getFrom());
                if (user != null)
                    if (!TextUtils.isEmpty(user.getRemark()))
                        toUser = user.getRemark();
                sb = getTextColor("“", ContextCompat.getColor(mContext,R.color.C0322));
                sb.append(getTextColor(toUser, ContextCompat.getColor(mContext,R.color.C0412)));
                sb.append(getTextColor("”领取了你的红包", ContextCompat.getColor(mContext,R.color.C0322)));
                mTvMessage.setText(sb);
//                mTvMessage.setText(Html.fromHtml(String.format("<font color=%1$s>“</font><font color=%2$s>%3$s</font><font color=%4$s>”领取了你的红包</font>", "#999999", "#9d45f9", toUser, "#999999")));
            }
        } catch (HyphenateException e)
        {
            e.printStackTrace();
        }
    }

    private SpannableStringBuilder getTextColor(String key, int color)
    {
        SpannableStringBuilder spBuilder = new SpannableStringBuilder(key);
        spBuilder.setSpan(new ForegroundColorSpan(color), 0, key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spBuilder;
    }

    private void setUpBaseView()
    {
        // set nickname, avatar and background of bubble
        if (timestamp != null)
        {
            if (position == 0)
            {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            }
            else
            {
                // show time stamp if interval with last message is > 30 seconds
                EMMessage prevMessage = ((ChatMessageAdapter2) adapter).getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime()))
                {
                    timestamp.setVisibility(View.GONE);
                }
                else
                {
                    timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
