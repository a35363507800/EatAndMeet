package com.echoesnet.eatandmeet.views.widgets.chat;

import android.content.Context;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.IMUtils.IMVoiceRecorder;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.hyphenate.EMError;


/**
 * @author yqh
 * @version 1.0
 * @createDate 2017/7/14
 * @Description 按下发送语音时chatFragment 上UI显示
 */
public class SendVoiceTipView extends LinearLayout
{

    private Context context;
    private TextView tipContent;
    RelativeLayout root;
    private ImageView imageView;
    protected PowerManager.WakeLock wakeLock;
    protected IMVoiceRecorder voiceRecorder;

    public SendVoiceTipView(Context context)
    {
        super(context);
        init(context, null);
    }

    public SendVoiceTipView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public SendVoiceTipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.my_send_voice_tip_ui, this);
        root = (RelativeLayout) findViewById(R.id.root);
        tipContent = (TextView) findViewById(R.id.tip_Content);
        root.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        voiceRecorder = new IMVoiceRecorder();
        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
    }

    public void setContent(String content)
    {
        tipContent.setText(content);
    }

    private float downY;

    public boolean onPressSendVoice(MotionEvent event, VoiceRecorderCallback voiceRecorderCallback)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                this.setVisibility(VISIBLE);
                setContent("手指上划，取消发送");
                startRecording();
                return true;
            case MotionEvent.ACTION_MOVE:
            {
                float moveY = event.getY();
                if (downY - moveY > 100)
                {
                    setContent("松开手指，取消发送");
                }
                if (downY - moveY < 20)
                {
                    tipContent.setText("手指上划，取消发送");
                }
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                this.setVisibility(GONE);
                float moveY = event.getY();
                if (downY - moveY > 100)
                {
                    setContent("松开手指，取消发送");
                    discardRecording();
//                    if (voiceRecorderCallback != null)
//                    {
//                        voiceRecorderCallback.onVoiceRecordCancel();
//                    }
                }
                if (downY - moveY < 20)
                {
                    tipContent.setText("手指上划，取消发送");
                    try
                    {
                        int length = stopRecoding();
                        if (length > 0)
                        {
                            if (voiceRecorderCallback != null)
                            {
                                voiceRecorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
                            }
                        }
                        else if (length == EMError.FILE_INVALID)
                        {
                            ToastUtils.showShort("无录音权限");
//                            if (voiceRecorderCallback != null)
//                            {
//                                voiceRecorderCallback.onVoiceRecordCancel();
//                            }
                        }
                        else
                        {
                            ToastUtils.showShort("录音时间太短");
//                            if (voiceRecorderCallback != null)
//                            {
//                                voiceRecorderCallback.onVoiceRecordCancel();
//                            }
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        ToastUtils.showShort("发送失败，请检测服务器是否连接");
//                        if (voiceRecorderCallback != null)
//                        {
//                            voiceRecorderCallback.onVoiceRecordCancel();
//                        }
                    }
                }
                return true;
            }
            default:
                discardRecording();
                return false;
        }
    }

    /**
     * 开始录音
     */
    public void startRecording()
    {
        try
        {
            wakeLock.acquire();
            this.setVisibility(View.VISIBLE);
            voiceRecorder.startRecording(context);
        } catch (Exception e)
        {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
            this.setVisibility(View.INVISIBLE);
            ToastUtils.showShort("录音失败，请重试！");
        }
    }

    /**
     * 停止录音
     * @return
     */
    public int stopRecoding()
    {
        this.setVisibility(View.INVISIBLE);
        if (wakeLock.isHeld())
            wakeLock.release();
        return voiceRecorder.stopRecoding();
    }

    public void discardRecording()
    {
        if (wakeLock.isHeld())
            wakeLock.release();
        try
        {
            // stop recording
            if (voiceRecorder.isRecording())
            {
                voiceRecorder.discardRecording();
                this.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e)
        {
        }
    }

    public String getVoiceFilePath()
    {
        return voiceRecorder.getVoiceFilePath();
    }

    public interface VoiceRecorderCallback
    {
        /**
         * on voice record complete
         *
         * @param voiceFilePath   录音完毕后的文件路径
         * @param voiceTimeLength 录音时长
         */
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);

//        void onVoiceRecordCancel();
    }
}
