package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ConversationBean;
import com.echoesnet.eatandmeet.utils.DateUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseUserUtils;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.hyphenate.chat.EMConversation;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/21 10:56
 * @description
 */

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = ConversationAdapter.class.getSimpleName();

    private List<ConversationBean> conLst;
    private Context mContext;

    public ConversationAdapter(Context context, List<ConversationBean> conLst)
    {
        this.mContext = context;
        this.conLst = conLst;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //此列表中只有一种对象，所有viewType用不到
        View view = LayoutInflater.from(mContext).inflate(R.layout.mychatrow_history_item, parent, false);
        return new ConversationAdapter.ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position)
    {
        final ConversationBean conversation = conLst.get(position);
        ConversationViewHolder holder = (ConversationViewHolder) holder1;
        if (conversation.getType() != EMConversation.EMConversationType.Chat)
        {
            return;
        }
        Logger.t(TAG).d("------------->conversion:" + conversation.toString());
        EaseUserUtils.setUserAvatar(conversation.getIsVUser(), conversation.getHeadImage(), holder.headImgView);

        String nicName;
        if (TextUtils.isEmpty(conversation.getRemark()))
            nicName = conversation.getNickName();
        else
            nicName = conversation.getRemark();
        EaseUserUtils.setUserNick(nicName, holder.nickName);
        String age = conversation.getAge();
        holder.itvAgeAndGender.setSex(age, conversation.getGender());
        holder.lvLevel.setLevel(conversation.getLevel(), LevelView.USER);
        int unreadMsg = conversation.getUnreadMsgNumber();
        Logger.t(TAG).d("unreadMsg:" + unreadMsg);
        if (unreadMsg <= 0)
        {
            holder.unreadMsgNumber.setVisibility(View.INVISIBLE);
        }
        else if (unreadMsg <= 99)
        {
            holder.unreadMsgNumber.setVisibility(View.VISIBLE);
            holder.unreadMsgNumber.setText(String.valueOf(unreadMsg));
        }
        else
        {
            holder.unreadMsgNumber.setVisibility(View.VISIBLE);
            holder.unreadMsgNumber.setText("99+");
        }
        //将文字转化为spannable--wb
        Spannable span = EamSmileUtils.getSmiledText(mContext, conversation.getLastMsg());
        holder.tvContent.setText(span, TextView.BufferType.SPANNABLE);
        holder.tvTime.setText(DateUtils.getTimestampFormatString(new Date(conversation.getTime())));
        if (conversation.getMsgState())
        {
            holder.msgState.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.msgState.setVisibility(View.GONE);
        }
        holder.rootView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnItemClickListener != null)
                {
                    mOnItemClickListener.onItemClick(v, conversation);
                }
            }
        });
        holder.rootView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (mOnItemLongClickListener != null)
                    mOnItemLongClickListener.onItemLongClick(v, conversation);
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return conLst.size();
    }


    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, ConversationBean entity);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener)
    {
        this.mOnItemClickListener = itemClickListener;
    }

    public interface OnItemLongClickListener
    {
        void onItemLongClick(View view, ConversationBean entity);
    }

    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener)
    {
        this.mOnItemLongClickListener = itemLongClickListener;
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder
    {
        LevelHeaderView headImgView;
        TextView unreadMsgNumber;
        TextView nickName;
        TextView tvContent;
        TextView tvTime;
        ImageView msgState;
        GenderView itvAgeAndGender;
        LevelView lvLevel;
        RelativeLayout rootView;

        public ConversationViewHolder(View itemView)
        {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.list_itease_layout);
            headImgView = (LevelHeaderView) itemView.findViewById(R.id.avatar);
            unreadMsgNumber = (TextView) itemView.findViewById(R.id.unread_msg_number);
            nickName = (TextView) itemView.findViewById(R.id.tv_username);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            msgState = (ImageView) itemView.findViewById(R.id.msg_state);
            itvAgeAndGender = (GenderView) itemView.findViewById(R.id.gender_icon);
            lvLevel = (LevelView) itemView.findViewById(R.id.lv_level_icon);
        }
    }
}
