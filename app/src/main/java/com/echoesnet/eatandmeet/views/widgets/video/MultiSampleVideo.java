package com.echoesnet.eatandmeet.views.widgets.video;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.danikula.videocache.HttpProxyCacheServer;
import com.echoesnet.eatandmeet.R;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge;

import java.io.File;

import tv.danmaku.ijk.media.player.IjkLibLoader;

/**
 * 多个同时播放的播放控件
 */

public class MultiSampleVideo extends StandardGSYVideoPlayer
{

    private final static String TAG = "MultiSampleVideo";


    private String mCoverOriginUrl;

    private int mDefaultRes;
    public MultiSampleVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public MultiSampleVideo(Context context) {
        super(context);
    }

    public MultiSampleVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        post(new Runnable() {
                            @Override
                            public void run() {
                                //todo 判断如果不是外界造成的就不处理
                            }
                        });
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        post(new Runnable() {
                            @Override
                            public void run() {
                                //todo 判断如果不是外界造成的就不处理
                            }
                        });
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        break;
                }
            }
        };
    }

    @Override
    public void setIjkLibLoader(IjkLibLoader libLoader) {

    }

    @Override
    public GSYVideoViewBridge getGSYVideoManager() {
        return CustomManager.getCustomManager(getKey());
    }

    @Override
    protected boolean backFromFull(Context context) {
        return CustomManager.backFromWindowFull(context, getKey());
    }

    @Override
    protected void releaseVideos() {
        CustomManager.releaseAllVideos(getKey());
    }

    @Override
    protected HttpProxyCacheServer getProxy(Context context, File file) {
        return null;
    }



    @Override
    protected int getFullId() {
        return CustomManager.FULLSCREEN_ID;
    }

    @Override
    protected int getSmallId() {
        return CustomManager.SMALL_ID;
    }

    public String getKey() {
        if (mPlayPosition == -22) {
            Debuger.printfError(getClass().getSimpleName() + " used getKey() " + "******* PlayPosition never set. ********");
        }
        if (TextUtils.isEmpty(mPlayTag)) {
            Debuger.printfError(getClass().getSimpleName() + " used getKey() " + "******* PlayTag never set. ********");
        }
        return TAG + mPlayPosition + mPlayTag;
    }
    @Override
    public int getLayoutId() {
        return R.layout.video_layout_cover;
    }

    @Override
    protected void updateStartImage()
    {
        super.updateStartImage();
    }

    @Override
    protected void touchDoubleUp()
    {
//        super.touchDoubleUp();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l)
    {
        findViewById(R.id.surface_container).setOnClickListener(l);
    }
    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }
    @Override
    protected void clickStartIcon()
    {
        super.clickStartIcon();
    }

    public void setState(int state){
        setStateAndUi(state);
    }
}
