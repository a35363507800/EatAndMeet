/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.echoesnet.eatandmeet.views.widgets.chat.chatrow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.util.EMLog;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 语音row播放点击事件监听
 */
public class ChatRowVoicePlayClickListener implements View.OnClickListener
{
    private static final String TAG = "VoicePlayClickListener";
    EMMessage message;
    EMVoiceMessageBody voiceBody;
    ImageView voiceIconView;

    private AnimationDrawable voiceAnimation = null;
    MediaPlayer mediaPlayer = null;
    ImageView iv_read_status;
    Activity activity;
    private ChatType chatType;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    private String oldPath;
    private boolean isBeRegistered = false;

    public static boolean isPlaying = false;
    public static ChatRowVoicePlayClickListener currentPlayListener = null;
    public static String playMsgId;

    int position;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private AudioManager audioManager;

    public ChatRowVoicePlayClickListener(EMMessage message, ImageView v, ImageView iv_read_status, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, Activity context,int position)
    {
        this.message = message;
        voiceBody = (EMVoiceMessageBody) message.getBody();
        this.iv_read_status = iv_read_status;
        this.adapter = adapter;
        voiceIconView = v;
        this.activity = context;
        this.position = position;
        this.chatType = message.getChatType();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
    }

    public void stopPlayVoice()
    {
        voiceAnimation.stop();
        if (message.direct() == EMMessage.Direct.RECEIVE)
        {
            voiceIconView.setImageResource(R.drawable.chat_receiver_audio_playing_002);
        }
        else
        {
            voiceIconView.setImageResource(R.drawable.chat_sender_audio_playing_002);
        }
        // stop play voice
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
        playMsgId = null;
        mSensorManager.unregisterListener(mSensorEventListener, mSensor);
        //延时 为了解决 连点多条
//        new Handler().postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                adapter.notifyItemChanged(position);
//            }
//        },300);

    }

    public void playVoice(String filePath)
    {
        if (audioManager.isSpeakerphoneOn())
        {
            Log.d("register", "num");
            isBeRegistered = true;
            mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (!(new File(filePath).exists()))
        {
            ToastUtils.showShort("文件不存在");
            return;
        }
        oldPath = filePath;
        playMsgId = message.getMsgId();
        mediaPlayer = new MediaPlayer();
        controlVoiceType();
        try
        {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    // TODO Auto-generated method stub
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                    if (isBeRegistered)
                    {
                        isBeRegistered = false;
                        audioManager.setSpeakerphoneOn(true);// 开启扬声器
                    }
//                    audioManager.setSpeakerphoneOn(true);// 开启扬声器
//                    turnOnScreen();
                    mSensorManager.unregisterListener(mSensorEventListener, mSensor);
                }

            });
            isPlaying = true;
            currentPlayListener = this;
            mediaPlayer.start();
            showAnimation();

            // 如果是接收的消息
            if (message.direct() == EMMessage.Direct.RECEIVE)
            {
                if (!message.isAcked() && chatType == ChatType.Chat)
                {
                    // 告知对方已读这条消息
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                }
                if (!message.isListened() && iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE)
                {
                    // 隐藏自己未播放这条语音消息的标志
                    iv_read_status.setVisibility(View.INVISIBLE);
                    message.setListened(true);
                    EMClient.getInstance().chatManager().setVoiceMessageListened(message);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("catch:" + e.getMessage() + "     filePath:" + filePath);
        }
    }

    private void controlVoiceType()
    {
        boolean isSpeaker = audioManager.isSpeakerphoneOn();
        //如果扬声器是打开的，则判断耳机是否插入
        Log.d("flag", "isSpeaker:" + isSpeaker);
        if (isSpeaker)
        {
            //如果耳机插入则关闭扬声器
            boolean flag = audioManager.isWiredHeadsetOn();
            Log.d("flag", "flag:" + flag);
            if (flag)
            {
                setInCallBySdk();//关闭扬声器模式
                /*audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(false);// 关闭扬声器
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);*/
            }
            if (!flag)
            {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            }
        }
        else
        {
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
    }

    // show the voice playing animation
    private void showAnimation()
    {
        // play voice, and start animation
        if (message.direct() == EMMessage.Direct.RECEIVE)
        {
            voiceIconView.setImageResource(R.drawable.voice_from_icon);
        }
        else
        {
            voiceIconView.setImageResource(R.drawable.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
        voiceAnimation.start();
    }

    @Override
    public void onClick(View v)
    {
        String st = activity.getResources().getString(R.string.Is_download_voice_click_later);
        if (isPlaying)
        {
            if (playMsgId != null && playMsgId.equals(message.getMsgId()))
            {
                currentPlayListener.stopPlayVoice();
                return;
            }
            currentPlayListener.stopPlayVoice();
        }

        if (message.direct() == EMMessage.Direct.SEND)
        {
            // for sent msg, we will try to play the voice file directly
            playVoice(voiceBody.getLocalUrl());
        }
        else
        {
            if (message.status() == EMMessage.Status.SUCCESS)
            {
                File file = new File(voiceBody.getLocalUrl());
                if (file.exists() && file.isFile())
                    playVoice(voiceBody.getLocalUrl());
                else
                    EMLog.e(TAG, "file not exist");

            }
            else if (message.status() == EMMessage.Status.INPROGRESS)
            {
                ToastUtils.showShort(st);
            }
            else if (message.status() == EMMessage.Status.FAIL)
            {
                ToastUtils.showShort(st);
                new AsyncTask<Void, Void, Void>()
                {

                    @Override
                    protected Void doInBackground(Void... params)
                    {
                        EMClient.getInstance().chatManager().downloadAttachment(message);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result)
                    {
                        super.onPostExecute(result);
                        adapter.notifyDataSetChanged();
                    }

                }.execute();
            }
        }
    }

    /**
     * 声明一个SensorEventListener对象用于侦听Sensor事件，并重载onSensorChanged方法
     */
    private final SensorEventListener mSensorEventListener = new SensorEventListener()
    {

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY)
            {
                //接近传感器检测物体与听筒的距离，单位是厘米。
                final float distance = event.values[0];
                Log.d("distance", distance + ",mSensor.getMaximumRange():" + mSensor.getMaximumRange());
                try
                {
                    boolean flag = audioManager.isWiredHeadsetOn();
                    if (flag)
                        return;
                    //判断距离设置模式
                    if (distance == mSensor.getMaximumRange())
                    {
                        setModeNormal();//设置扬声器
                    }
                    else
                    {
                        setInCallBySdk();//关闭扬声器模式
                        //放耳边重新播放语音
                        mediaPlayer.reset();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                        mediaPlayer.setDataSource(oldPath);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }

//                    if (distance <5)
//                    {
////                      setInCallBySdk();
////                      controlVoiceType();
//                        audioManager.setSpeakerphoneOn(false);
//                        mediaPlayer.reset();
//                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//                        mediaPlayer.setDataSource(oldPath);
//                        mediaPlayer.prepare();
//                        mediaPlayer.start();
////                        Build.BRAND.toLowerCase();
///*                        mediaPlayer.stop();
//                        mediaPlayer.seekTo(0);
//                       // mediaPlayer.prepare();
//                        mediaPlayer.start();*/
///*                        stopPlayVoice();
//                        playVoice(oldPath);*/
//                    }
//                    else
//                    {
//                        audioManager.setSpeakerphoneOn(true);
//                    }
                } catch (Exception e)
                {

                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
            // TODO Auto-generated method stub

        }
    };

    private void setInCallBySdk()
    {
        if (audioManager == null)
        {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (audioManager.getMode() != AudioManager.MODE_IN_COMMUNICATION)
            {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }
            try
            {
                Class clazz = Class.forName("android.media.AudioSystem");
                Method m = clazz.getMethod("setForceUse", new Class[]{int.class, int.class});
                m.invoke(null, 1, 1);
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            } catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            if (audioManager.getMode() != AudioManager.MODE_IN_CALL)
            {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            }
        }
        if (audioManager.isSpeakerphoneOn())
        {
            audioManager.setSpeakerphoneOn(false);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
        }
    }

    private void setModeNormal()
    {
        if (audioManager == null)
        {
            return;
        }
        audioManager.setSpeakerphoneOn(true);
        audioManager.setMode(AudioManager.MODE_NORMAL);

        if (!audioManager.isSpeakerphoneOn())
        {
            audioManager.setSpeakerphoneOn(true);

            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
        }

    }

    //启用屏幕常亮功能
    private void turnOnScreen()
    {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.screenBrightness = 1;
        activity.getWindow().setAttributes(params);
    }

    //关闭 屏幕常亮功能
    private void turnOffScreen()
    {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.screenBrightness = 0.001f;
        activity.getWindow().setAttributes(params);
    }

}

