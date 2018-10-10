package com.echoesnet.eatandmeet.views.widgets.chat.chatrow;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseUserUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.ChatMessageAdapter2;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.chat.ChatMessageList;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;
import com.hyphenate.util.DateUtils;
import com.orhanobut.logger.Logger;

import java.util.Date;

import static com.hyphenate.chat.EMMessage.Status.SUCCESS;

public abstract class ChatRow extends LinearLayout
{
    protected static final String TAG = ChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected LevelHeaderView userAvatarView;
    protected View bubbleLayout;
    protected TextView usernickView;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Activity activity;

    protected TextView ackedView;

    protected EMCallBack messageSendCallback;
    protected EMCallBack messageReceiveCallback;

    private float mRawX;
    private float mRawY;

    protected ChatMessageList.MessageListItemClickListener itemClickListener;
//    protected EaseMessageListItemStyle itemStyle;

    public ChatRow(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        inflater = LayoutInflater.from(context);

        initView();
    }

    private void initView()
    {
        onInflateView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (LevelHeaderView) findViewById(R.id.iv_userhead);
        bubbleLayout = (View) findViewById(R.id.bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        onFindViewById();
    }

    /**
     * set property according message and postion
     *
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, int position,
                          ChatMessageList.MessageListItemClickListener itemClickListener
//            ,EaseMessageListItemStyle itemStyle
    )
    {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;
//        this.itemStyle = itemStyle;

        setUpBaseView();
        onSetUpView();
        handleTextMessage();
        setClickListener();
    }

    private void setUpBaseView()
    {
        // set nickname, avatar and background of bubble
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
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

        //set nickname and avatar
        if (message.direct() == Direct.SEND)
        {
            EaseUser cUser = EaseUserUtils.getCurrentUserInfo(null);
            EaseUserUtils.setUserAvatar(cUser.getIsVuser(), cUser.getAvatar(), userAvatarView);
        }
        else
        {
            EaseUser eUser = EaseUserUtils.getUserInfo(message);
            Logger.t(TAG).d("------>eUserInfo.username:" + message.getFrom());
            EaseUser localeUser = HuanXinIMHelper.getInstance().getUserInfo(message.getFrom());
            String eUserInfo = eUser.toString();
            String localInfo = localeUser.toString();
            Logger.t(TAG).d("----->eUserInfo:" + eUserInfo);
            Logger.t(TAG).d("----->localInfo:" + localInfo);
            if (TextUtils.isEmpty(localeUser.getAvatar()) || TextUtils.isEmpty(localeUser.getuId()))
            {
                new Thread(() ->
                {
                    HuanXinIMHelper.getInstance().saveContact(eUser);
                }).start();
                EaseUserUtils.setUserAvatar(eUser.getIsVuser(), eUser.getAvatar(), userAvatarView);
            }
            else
            {
                EaseUserUtils.setUserAvatar(localeUser.getIsVuser(), localeUser.getAvatar(), userAvatarView);
            }



            /*if (eUserInfo.equals(localInfo))//消息的数据与本地数据一致
            {
                // TODO: 2017/8/21 兼容ios 线上问题  以后 删除
                if (TextUtils.isEmpty(localeUser.getuId()))
                {
                    localeUser = HuanXinIMHelper.getInstance().getUserInfo(message.getFrom());
                }
                EaseUserUtils.setUserAvatar(localeUser.getIsVuser(), localeUser.getAvatar(), userAvatarView);
            }
            else
            {
                EaseUser finalEUser = eUser;
                // TODO: 2017/8/21 兼容ios 线上问题  以后 删除
                if (TextUtils.isEmpty(finalEUser.getuId()))
                {
                    eUser = HuanXinIMHelper.getInstance().getUserInfo(message.getFrom());
                }
                else
                {
                    new Thread(() ->
                    {
                        HuanXinIMHelper.getInstance().saveContact(finalEUser);
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();
                            }
                        });
                    }).start();
                }
                EaseUserUtils.setUserAvatar(eUser.getIsVuser(), eUser.getAvatar(), userAvatarView);
            }*/
//            EaseUserUtils.UserNick(eUser.getNickName(), usernickView);
        }

    }

    protected void handleTextMessage()
    {
        if (message.direct() == EMMessage.Direct.SEND)
        {
            setMessageSendCallback();
            if (message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, false))
            {
                message.setStatus(SUCCESS);
            }
            switch (message.status())
            {
                case CREATE:
//                    statusView.setVisibility(View.VISIBLE);
                    if (ackedView != null)
                    {
                        ackedView.setText(context.getString(R.string.text_sending_msg));
                        ackedView.setBackgroundResource(R.drawable.round_btn_c0315);
                        ackedView.setVisibility(VISIBLE);
                    }
                    break;
                case SUCCESS:
                    if (ackedView != null)
                    {
                        ackedView.setText(context.getString(R.string.text_delivered_msg));
                        ackedView.setBackgroundResource(R.drawable.round_btn_c0312);
                        ackedView.setVisibility(VISIBLE);
                    }
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    statusView.setVisibility(View.VISIBLE);
                    if (ackedView != null)
                        ackedView.setVisibility(GONE);
                    break;
                case INPROGRESS:
                    statusView.setVisibility(View.GONE);
                    if (ackedView != null)
                    {
                        ackedView.setText(context.getString(R.string.text_sending_msg));
                        ackedView.setBackgroundResource(R.drawable.round_btn_c0315);
                        ackedView.setVisibility(VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
        /*else
        {
            if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat)
            {
                try
                {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e)
                {
                    e.printStackTrace();
                }
            }
        }*/
        if (ackedView != null)
        {
            Logger.t(TAG).d("message.setUp");
            if (message.direct() == Direct.SEND && message.isDelivered())
            {
                Logger.t(TAG).d("message.isDelivered()");
                ackedView.setText(context.getString(R.string.text_delivered_msg));
                ackedView.setBackgroundResource(R.drawable.round_btn_c0312);
                ackedView.setVisibility(VISIBLE);
            }
            if (message.direct() == Direct.SEND && message.isAcked())
            {
                Logger.t(TAG).d("message.isAcked()");
                ackedView.setText(context.getString(R.string.text_ack_msg));
                ackedView.setBackgroundResource(R.drawable.round_btn_c0315);
                ackedView.setVisibility(VISIBLE);
            }
        }
        if (message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_VOICE_TEMP_MESSAGE, false))
        {
            if (ackedView != null)
                ackedView.setVisibility(GONE);
        }
    }

    /**
     * set callback for sending message
     */
    protected void setMessageSendCallback()
    {
        if (messageSendCallback == null)
        {
            messageSendCallback = new EMCallBack()
            {
                @Override
                public void onSuccess()
                {
                    updateView();
                    Logger.t(TAG).d("chat------>chatRow的Callback调用，messageId:" + message.getMsgId());
                    if (itemClickListener != null)
                        itemClickListener.onMessageSendSuccess(message);
                }

                @Override
                public void onProgress(final int progress, String status)
                {
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (percentageView != null)
                                percentageView.setText(progress + "%");

                        }
                    });
                }

                @Override
                public void onError(int code, String error)
                {
                    updateView(code, error);
                }
            };
        }
        message.setMessageStatusCallback(messageSendCallback);
    }

    /**
     * set callback for receiving message
     */
    protected void setMessageReceiveCallback()
    {
        if (messageReceiveCallback == null)
        {
            messageReceiveCallback = new EMCallBack()
            {

                @Override
                public void onSuccess()
                {
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status)
                {
                    activity.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            if (percentageView != null)
                            {
                                percentageView.setText(progress + "%");
                            }
                        }
                    });
                }

                @Override
                public void onError(int code, String error)
                {
                    updateView();
                }
            };
        }
        message.setMessageStatusCallback(messageReceiveCallback);
    }


    private void setClickListener()
    {
        if (bubbleLayout != null)
        {
            bubbleLayout.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (itemClickListener != null)
                    {
                        if (!itemClickListener.onBubbleClick(message))
                        {
                            // if listener return false, we call default handling
                            onBubbleClick();
                        }
                    }
                }
            });

            bubbleLayout.setOnLongClickListener(new OnLongClickListener()
            {

                @Override
                public boolean onLongClick(View v)
                {
                    onBubbleLongClick();
                    if (itemClickListener != null)
                    {
                        itemClickListener.onBubbleLongClick(v, mRawX, mRawY, message);
                    }
                    return true;
                }
            });
            bubbleLayout.setOnTouchListener(new OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    mRawX = event.getRawX();
                    mRawY = event.getRawY();
                    Logger.t(TAG).d("rootView.chatRow:" + mRawX + " | " + mRawY);
                    return false;
                }
            });
        }

        if (statusView != null)
        {
            statusView.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (itemClickListener != null)
                    {
                        itemClickListener.onResendClick(message);
                    }
                }
            });
        }

        if (userAvatarView != null)
        {
            userAvatarView.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (itemClickListener != null)
                    {
                        if (message.direct() == Direct.SEND)
                        {
                            itemClickListener.onUserAvatarClick(SharePreUtils.getUId(context));
                        }
                        else
                        {
                            // TODO: 2017/8/21 兼容线上 ios
                            String uid = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, "");
                            if (TextUtils.isEmpty(uid))
                            {
                                uid = HuanXinIMHelper.getInstance().getUserInfo(message.getFrom()).getuId();
                            }
                            itemClickListener.onUserAvatarClick(uid);
                        }
                    }
                }
            });
            userAvatarView.setOnLongClickListener(new OnLongClickListener()
            {

                @Override
                public boolean onLongClick(View v)
                {
                    if (itemClickListener != null)
                    {
                        if (message.direct() == Direct.SEND)
                        {
                            itemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser());
                        }
                        else
                        {
                            itemClickListener.onUserAvatarLongClick(message.getFrom());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }


    protected void updateView()
    {
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (message.status() == EMMessage.Status.FAIL)
                {
                    ToastUtils.showShort(activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast));
                }

                onUpdateView();
            }
        });
    }

    protected void updateView(final int errorCode, final String desc)
    {
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (errorCode == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT)
                {
                    ToastUtils.showShort(activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_invalid_content));
                }
                else if (errorCode == EMError.GROUP_NOT_JOINED)
                {
                    ToastUtils.showShort(activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_not_in_the_group));
                }
                else
                {
                    ToastUtils.showShort(activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast));
                }
                onUpdateView();
            }
        });
    }

    protected abstract void onInflateView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * refresh list view when message status change
     */
    protected abstract void onUpdateView();

    /**
     * setup view
     */
    protected abstract void onSetUpView();

    /**
     * on bubble clicked
     */
    protected abstract void onBubbleClick();

    protected abstract void onBubbleLongClick();

}
