package com.echoesnet.eatandmeet.utils.redPacket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.views.adapters.ChatMessageAdapter2;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;

import java.util.Date;

/**
 * Created by wangben on 2016/7/26.
 * 红包发送与领取回执信息
 */
public class ChatRowRecallMessage extends RelativeLayout
{
    private TextView tvContent, timestamp;
    private Context mContext;
    private EMMessage message;
    private int position;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    public ChatRowRecallMessage(Context context)
    {
        super(context);
        mContext = context;
        initView();
    }

    private void initView()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.em_row_recall_message, this);
        tvContent = view.findViewById(R.id.tv_content);
        timestamp = view.findViewById(R.id.timestamp);
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

//        if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_RECALL_SENDER, false))
        if (message.direct() == EMMessage.Direct.SEND)
        {
            tvContent.setText("你撤回了一条消息");
        }
        else
        {
            tvContent.setText("对方撤回了一条消息");
        }
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
