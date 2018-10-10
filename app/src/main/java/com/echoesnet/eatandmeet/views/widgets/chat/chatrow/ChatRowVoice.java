package com.echoesnet.eatandmeet.views.widgets.chat.chatrow;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.util.EMLog;

public class ChatRowVoice extends ChatRowFile //implements SensorEventListener
{

    private ImageView voiceImageView;
    private TextView voiceLengthView, tvPlaceholder;
    private ImageView readStatusView;

    private boolean isWideVoiceType = false;
    private AudioManager audioManager;
    /*private PowerManager localPowerManager;
    private PowerManager.WakeLock localWakeLock;
    private SensorManager sensorManager;
    private Sensor sensor;*/



    public ChatRowVoice(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
        /*//电源管理器，用于控制屏幕亮或暗的。获取系统服务POWER_SERVICE，返回一个PowerManager对象
        localPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        //第一个参数为电源锁级别，第二个是日志tag
        localWakeLock = this.localPowerManager.newWakeLock(32, "MyPower");
        //距离传感器
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        */
        //声音管理器
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    protected void onInflateView()
    {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_voice : R.layout.ease_row_sent_voice, this);
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
        if (audioManager != null)
            audioManager.setSpeakerphoneOn(true);
        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
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
        LinearLayout.LayoutParams params = new LayoutParams(length, LinearLayout.LayoutParams.WRAP_CONTENT);
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
        }

        // until here, handle sending voice message
        handleSendMessage();
    }

    @Override
    protected void onUpdateView()
    {
        super.onUpdateView();
        adapter.notifyItemChanged(position);
    }

    @Override
    protected void onBubbleClick()
    {
        if (!message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_VOICE_TEMP_MESSAGE, false))
            new ChatRowVoicePlayClickListener(message, voiceImageView, readStatusView, adapter, activity, position).onClick(bubbleLayout);
    }

    @Override
    protected void onBubbleLongClick()
    {
        /*isWideVoiceType = audioManager.isSpeakerphoneOn();
        String[] mode;
        if (isWideVoiceType)
        {
            mode = new String[]{"使用听筒模式"};
        }
        else
        {
            mode = new String[]{"使用扬声器模式"};
        }
        new ContextMenuDialog(new ContextMenuDialog.MenuDialogCallBack()
        {
            @Override
            public void menuOnClick(String menuItem, int position)
            {
                if (isWideVoiceType)
                {
                    audioManager.setSpeakerphoneOn(false);
                }
                else
                {
                    audioManager.setSpeakerphoneOn(true);
                }
                isWideVoiceType = !isWideVoiceType;
            }
        }).showContextMenuBox(activity, Arrays.asList(mode));*/
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

    /*@Override
    public void onSensorChanged(SensorEvent event)
    {
        try
        {
            float mProximiny = event.values[0];
            boolean flag = false;
            if (mProximiny >= sensor.getMaximumRange())
            {
                flag = true;
                changeAdapterType(flag);
            }
            else
            {
                flag = false;
                changeAdapterType(flag);
            }
            if (localWakeLock.isHeld()) return;

            //电源锁用于控制屏亮屏黑，请及时释放，并捕获异常。
            if (flag)
            {
                localWakeLock.setReferenceCounted(false);
                // 释放设备电源锁
                localWakeLock.release();
            }
            else
            {
                // 申请设备电源锁
                localWakeLock.acquire();
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    //切换声筒或听筒
    private void changeAdapterType(boolean on)
    {
        activity.setVolumeControlStream(AudioManager.STREAM_SYSTEM);
        if (on)
        {
            //扩音声筒
            audioManager.setMicrophoneMute(false);
            audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            // LogUtil.debug(getClass(), "当前为扩音模式");
        }
        else
        {
            //耳麦听筒
            audioManager.setSpeakerphoneOn(false);
            audioManager.setMicrophoneMute(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            //LogUtil.debug(getClass(),"当前为听筒模式");
            //以下几行代码是仿QQ切换时，声音往后延迟1.5秒，以方便将声音连接起来，
            //因为有些手机在切换时，会导致中间有约两秒左右的空白,按自己的需要来定。
            //微信的模式和QQ不同，微信是在扩音切换到听筒时，会重新把语音播放一次。QQ的则是将时间往后退约两秒左右播放。//按自己的需要来定
            //-------------------------------------

            *//*int cur = MediaManager.current();
            if (cur < 1500)
            {
                cur = 0;
            }
            else
            {
                cur = cur - 1500;
            }
            MediaManager.playSound(filePathStr, cur, new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mAdapter.stopPlay();
                }
            }, new MediaManager.OnErrorPlayListener()
            {
                @Override
                public void onError(String msg)
                {
                    mAdapter.stopPlay();
                }
            });*//*
        }
        //---------------------------------------------
    }*/

}
