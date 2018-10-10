package com.echoesnet.eatandmeet.views.widgets.chat;

import android.content.Context;
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
import com.orhanobut.logger.Logger;


/**
 * Created by Administrator on 2017/7/14.
 */

public class SendVoiceView extends LinearLayout implements View.OnTouchListener
{
    private static final String TAG = SendVoiceView.class.getSimpleName();
    private Context context;
    private RelativeLayout root;
    private TextView tipContent;
    private ImageView imageView;
    private OnSendVoiceStateListener onSendVoiceStateListener;
    private static final int INVALID_POINTER_ID = -1;

    private float downY;

    public SendVoiceView(Context context)
    {
        super(context);
        init(context, null);
    }

    public SendVoiceView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public SendVoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.my_send_voice_ui, this);
        root = (RelativeLayout) findViewById(R.id.root);
        tipContent = (TextView) findViewById(R.id.tip_Content);
        imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event)
    {

        Logger.t(TAG).d("onTouch:event.getPointerCount():" + event.getPointerCount());

        if (event.getPointerCount() > 1)
            return true;

        if (onSendVoiceStateListener != null)
            onSendVoiceStateListener.onTouchListener(v, event);

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                imageView.setBackgroundResource(R.drawable.btn_voice_press_xhdpi);
                downY = event.getY();
                tipContent.setText("松开发送");
            }
            break;
            case MotionEvent.ACTION_MOVE:
            {
                float moveY = event.getY();
                if (downY - moveY > 100)
                {
                    tipContent.setText("松开取消");
                }
                if (downY - moveY < 20)
                {
                    tipContent.setText("松开发送");
                }
            }
            break;
            case MotionEvent.ACTION_UP:
                tipContent.setText("按住说话");
                imageView.setBackgroundResource(R.drawable.btn_voice_default_xhdpi);
                break;
        }
        return true;
    }

    public interface OnSendVoiceStateListener
    {
        void onTouchListener(View view, MotionEvent event);
    }

    public void setOnSendVoiceStateListener(OnSendVoiceStateListener onSendVoiceStateListener)
    {
        this.onSendVoiceStateListener = onSendVoiceStateListener;
    }

   /* public void startRecording() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(context, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            wakeLock.acquire();
            this.setVisibility(View.VISIBLE);
            recordingHint.setText(context.getString(R.string.move_up_to_cancel));
            recordingHint.setBackgroundColor(Color.TRANSPARENT);
            voiceRecorder.startRecording(context);
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
            this.setVisibility(View.INVISIBLE);
            Toast.makeText(context, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
            return;
        }
    }*/

}
