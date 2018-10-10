package com.echoesnet.eatandmeet.views.widgets.chat.chatrow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.hyphenate.chat.EMMessage;

public class ChatRowVoiceTemp extends ChatRowFile
{

    private ImageView voiceImageView;
    private TextView voiceLengthView, tvPlaceholder;
    private ImageView readStatusView;

    public ChatRowVoiceTemp(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        inflater.inflate(R.layout.ease_row_sent_voice_temp, this);
    }

    @Override
    protected void onFindViewById()
    {
        voiceImageView = ((ImageView) findViewById(R.id.iv_voice));
        voiceLengthView = (TextView) findViewById(R.id.tv_length);
        readStatusView = (ImageView) findViewById(R.id.iv_unread_voice);
        tvPlaceholder = (TextView) findViewById(R.id.tv_placeholder);
    }

    @Override
    protected void onSetUpView()
    {
        /*EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
        int len = voiceBody.getLength();
        if (len > 0)
        {
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
        }
        else
        {
            voiceLengthView.setVisibility(View.INVISIBLE);
        }

        int width = tvPlaceholder.getWidth();
        int length = 30;
        int maxWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                120,
                context.getResources().getDisplayMetrics());

        if (width <= maxWidthPx)
        {
            length = (len / 5) * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    12,
                    context.getResources().getDisplayMetrics());
        }
        if (length > maxWidthPx)
            length = maxWidthPx;
        LayoutParams params = new LayoutParams(length, LayoutParams.WRAP_CONTENT);
        tvPlaceholder.setLayoutParams(params);

        if (ChatRowVoicePlayClickListener.playMsgId != null
                && ChatRowVoicePlayClickListener.playMsgId.equals(message.getMsgId()) && ChatRowVoicePlayClickListener.isPlaying)
        {
            AnimationDrawable voiceAnimation;
            if (message.direct() == EMMessage.Direct.RECEIVE)
            {
                voiceImageView.setImageResource(R.drawable.voice_from_icon);
            }
            else
            {
                voiceImageView.setImageResource(R.drawable.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) voiceImageView.getDrawable();
            voiceAnimation.start();
        }
        else
        {
            if (message.direct() == EMMessage.Direct.RECEIVE)
            {
                voiceImageView.setImageResource(R.drawable.chat_receiver_audio_playing_002);
            }
            else
            {
                voiceImageView.setImageResource(R.drawable.chat_sender_audio_playing_002);
            }
        }

        if (message.direct() == EMMessage.Direct.RECEIVE)
        {
            if (message.isListened())
            {
                // hide the unread icon
                readStatusView.setVisibility(View.INVISIBLE);
            }
            else
            {
                readStatusView.setVisibility(View.VISIBLE);
            }
            EMLog.d(TAG, "it is receive msg");
            if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING)
            {
                setMessageReceiveCallback();
            }
            else
            {
            }
            return;
        }*/

        // until here, handle sending voice message
        handleSendMessage();
    }

    @Override
    protected void onUpdateView()
    {
        super.onUpdateView();
    }

    @Override
    protected void onBubbleClick()
    {
//        if (!message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_VOICE_TEMP_MESSAGE, false))
//            new ChatRowVoicePlayClickListener(message, voiceImageView, readStatusView, adapter, activity, position).onClick(bubbleLayout);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (ChatRowVoicePlayClickListener.currentPlayListener != null && ChatRowVoicePlayClickListener.isPlaying)
        {
            ChatRowVoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }
    }

}
