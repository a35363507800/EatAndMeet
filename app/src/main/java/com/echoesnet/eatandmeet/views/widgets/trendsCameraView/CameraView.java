package com.echoesnet.eatandmeet.views.widgets.trendsCameraView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ScreenSwitchUtils;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.trendsCameraView.lisenter.CameraLisenter;
import com.echoesnet.eatandmeet.views.widgets.trendsCameraView.lisenter.CaptureLisenter;
import com.echoesnet.eatandmeet.views.widgets.trendsCameraView.lisenter.ErrorLisenter;
import com.echoesnet.eatandmeet.views.widgets.trendsCameraView.lisenter.FirstFoucsLisenter;
import com.echoesnet.eatandmeet.views.widgets.trendsCameraView.lisenter.TypeLisenter;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class CameraView extends FrameLayout implements CameraInterface.CamOpenOverCallback, SurfaceHolder.Callback
{
    private static final String TAG = CameraView.class.getSimpleName();

    //拍照浏览时候的类型
    private static final int TYPE_PICTURE = 0x001;
    private static final int TYPE_VIDEO = 0x002;


    //录制视频比特率
    public static final int MEDIA_QUALITY_HIGH = 20 * 100000;
    public static final int MEDIA_QUALITY_MIDDLE = 16 * 100000;
    public static final int MEDIA_QUALITY_LOW = 12 * 100000;
    public static final int MEDIA_QUALITY_POOR = 8 * 100000;
    public static final int MEDIA_QUALITY_FUNNY = 4 * 100000;
    public static final int MEDIA_QUALITY_DESPAIR = 2 * 100000;
    public static final int MEDIA_QUALITY_SORRY = 1 * 80000;

    //只能拍照
    public static final int BUTTON_STATE_ONLY_CAPTURE = 0x101;
    //只能录像
    public static final int BUTTON_STATE_ONLY_RECORDER = 0x102;
    //两者都可以
    public static final int BUTTON_STATE_BOTH = 0x103;

    //回调监听
    private CameraLisenter cameraLisenter;


    private Context mContext;
    private VideoView mVideoView;
    private ImageView mPhoto;
    private IconTextView mSwitchCamera;
    private IconTextView mReturnView;
    private CaptureLayout mCaptureLayout;
    private FoucsView mFoucsView;
    private MediaPlayer mMediaPlayer;

    private int layout_width;
    private int fouce_size;
    private float screenProp;

    //拍照的图片
    private Bitmap captureBitmap;
    //第一帧图片
    private Bitmap firstFrame;
    //视频URL
    private String videoUrl;
    private int type = -1;
    private boolean onlyPause = false;
    private long videoTime = 0;

    private int CAMERA_STATE = -1;
    private static final int STATE_IDLE = 0x010;
    private static final int STATE_RUNNING = 0x020;
    private static final int STATE_WAIT = 0x030;

    private boolean stopping = false;
    private boolean isBorrow = false;
    private boolean takePictureing = false;
    private boolean forbiddenSwitch = false;
    private String showType = "1";//0 横屏 1竖屏
    private ScreenSwitchUtils screenSwitchUtils;
    private int duration = 0;

    /**
     * constructor
     */
    public CameraView(Context context)
    {
        this(context, null);
    }

    /**
     * constructor
     */
    public CameraView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    /**
     * constructor
     */
    public CameraView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //get AttributeSet
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CameraView, defStyleAttr, 0);
//        iconSize = a.getDimensionPixelSize(R.styleable.CameraView_iconSize, (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP, 35, getResources().getDisplayMetrics()));
//        iconMargin = a.getDimensionPixelSize(R.styleable.CameraView_iconMargin, (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
//        iconSrc = a.getResourceId(R.styleable.CameraView_iconSrc, R.drawable.ic_sync_black_24dp);
        duration = a.getInteger(R.styleable.CameraView_duration_max, 10 * 1000);
        a.recycle();
        initData();
        initView();
        screenSwitchUtils = ScreenSwitchUtils.init(getContext());
        screenSwitchUtils.start((Activity) getContext());
    }

    private void initData()
    {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        layout_width = outMetrics.widthPixels;
        fouce_size = layout_width / 4;
        CAMERA_STATE = STATE_IDLE;
    }


    private void initView()
    {
        setWillNotDraw(false);
        this.setBackgroundColor(0xff000000);
        //VideoView
        mVideoView = new VideoView(mContext);
        LayoutParams videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mVideoView.setLayoutParams(videoViewParam);

        //mPhoto
        mPhoto = new ImageView(mContext);
        LayoutParams photoParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        mPhoto.setLayoutParams(photoParam);
        mPhoto.setBackgroundColor(0xff000000);
        mPhoto.setVisibility(INVISIBLE);

        //switchCamera
        mSwitchCamera = new IconTextView(mContext);
        LayoutParams iconTvViewParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iconTvViewParam.gravity = Gravity.RIGHT;
        iconTvViewParam.topMargin = CommonUtils.dp2px(mContext, 32);
        iconTvViewParam.rightMargin = CommonUtils.dp2px(mContext, 12);
        mSwitchCamera.setLayoutParams(iconTvViewParam);
        mSwitchCamera.setText("{eam-e9cb}");
        mSwitchCamera.setTextColor(ContextCompat.getColor(mContext, R.color.C0324));
        mSwitchCamera.setTextSize(30);
        mSwitchCamera.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switchCamera();
            }
        });

        //returnView
        mReturnView = new IconTextView(mContext);
        LayoutParams returnViewParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        returnViewParam.gravity = Gravity.LEFT;
        returnViewParam.topMargin = CommonUtils.dp2px(mContext, 32);
        returnViewParam.leftMargin = CommonUtils.dp2px(mContext, 12);
        mReturnView.setLayoutParams(returnViewParam);
        mReturnView.setText("{eam-n-previous}");
        mReturnView.setTextColor(ContextCompat.getColor(mContext, R.color.C0324));
        mReturnView.setTextSize(25);
        mReturnView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (cameraLisenter != null && !takePictureing)
                {
                    cameraLisenter.quit();
                }
            }
        });

        //CaptureLayout
        mCaptureLayout = new CaptureLayout(mContext);
        LayoutParams layout_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout_param.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layout_param.setMargins(0, 0, 0, CommonUtils.dp2px(getContext(), 35));
        mCaptureLayout.setLayoutParams(layout_param);
        mCaptureLayout.setDuration(duration);

        //mFoucsView
        mFoucsView = new FoucsView(mContext, fouce_size);
        LayoutParams foucs_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        foucs_param.gravity = Gravity.CENTER;
        mFoucsView.setLayoutParams(foucs_param);
        mFoucsView.setVisibility(INVISIBLE);

        //add view to ParentLayout
        this.addView(mVideoView);
        this.addView(mPhoto);
        this.addView(mSwitchCamera);
        this.addView(mReturnView);
        this.addView(mCaptureLayout);
        this.addView(mFoucsView);
        //START >>>>>>> captureLayout lisenter callback
        mCaptureLayout.setCaptureLisenter(new CaptureLisenter()
        {
            @Override
            public void takePictures()
            {
                if (CAMERA_STATE != STATE_IDLE || takePictureing)
                {
                    return;
                }
                showType = ("1".equals(screenSwitchUtils.getScreenType()) || "3".equals(screenSwitchUtils.getScreenType())) ? "0" : "1";
                CAMERA_STATE = STATE_RUNNING;
                takePictureing = true;
                mFoucsView.setVisibility(INVISIBLE);
                CameraInterface.getInstance().takePicture(screenSwitchUtils.getScreenType(), new CameraInterface.TakePictureCallback()
                {
                    @Override
                    public void captureResult(Bitmap bitmap, boolean isVertical)
                    {
                        Logger.t(TAG).d("isVertical" + isVertical);
                        captureBitmap = bitmap;
                        CameraInterface.getInstance().doStopCamera();
                        type = TYPE_PICTURE;
                        isBorrow = true;
                        CAMERA_STATE = STATE_WAIT;
                        if ("1".equals(showType))
                        {
                            mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        else
                        {
                            mPhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                        LayoutParams photoParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                                .MATCH_PARENT);
                        mPhoto.setLayoutParams(photoParam);
                        mPhoto.setImageBitmap(bitmap);
                        mPhoto.setVisibility(VISIBLE);
                        mCaptureLayout.startAlphaAnimation();
                        mCaptureLayout.startTypeBtnAnimator();
                        takePictureing = false;
                        mSwitchCamera.setVisibility(INVISIBLE);
                        mReturnView.setVisibility(INVISIBLE);
                        CameraInterface.getInstance().doOpenCamera(CameraView.this);
                    }
                });
            }

            @Override
            public void recordShort(long time)
            {
                if (CAMERA_STATE != STATE_RUNNING && stopping)
                {
                    return;
                }
                stopping = true;
                mCaptureLayout.setTextWithAnimation("录制时间过短");
                mSwitchCamera.setRotation(0);
                mSwitchCamera.setVisibility(VISIBLE);
                mReturnView.setVisibility(VISIBLE);
                CameraInterface.getInstance().setSwitchView(mSwitchCamera);
                postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        CameraInterface.getInstance().stopRecord(true, showType, new
                                CameraInterface.StopRecordCallback()
                                {
                                    @Override
                                    public void recordResult(String url, Bitmap firstFrame)
                                    {
                                        Log.i(TAG, "Record Stopping ...");
                                        mCaptureLayout.isRecord(false);
                                        CAMERA_STATE = STATE_IDLE;
                                        stopping = false;
                                        isBorrow = false;
                                    }
                                });
                    }
                }, 1500 - time);
            }

            @Override
            public void recordStart()
            {
                if (CAMERA_STATE != STATE_IDLE && stopping)
                {
                    return;
                }

                showType = ("1".equals(screenSwitchUtils.getScreenType()) || "3".equals(screenSwitchUtils.getScreenType())) ? "0" : "1";

                Logger.t(TAG).d("showType>>" + showType);
                mSwitchCamera.setVisibility(GONE);
                mReturnView.setVisibility(GONE);
                mCaptureLayout.isRecord(true);
                isBorrow = true;
                CAMERA_STATE = STATE_RUNNING;
                mFoucsView.setVisibility(INVISIBLE);
                CameraInterface.getInstance().startRecord(screenSwitchUtils.getScreenType(), mVideoView.getHolder().getSurface(), new CameraInterface
                        .ErrorCallback()
                {
                    @Override
                    public void onError()
                    {
                        Log.i("CJT", "startRecorder error");
                        mCaptureLayout.isRecord(false);
                        CAMERA_STATE = STATE_WAIT;
                        stopping = false;
                        isBorrow = false;
                    }
                });
            }

            @Override
            public void recordEnd(final long time)
            {
                Logger.t(TAG).d("录制时长：" + time);
                CameraInterface.getInstance().stopRecord(false, showType, new CameraInterface.StopRecordCallback()
                {
                    @Override
                    public void recordResult(final String url, Bitmap firstFrame)
                    {
                        CAMERA_STATE = STATE_WAIT;
                        videoUrl = url;
                        type = TYPE_VIDEO;
                        videoTime = time;
                        CameraView.this.firstFrame = firstFrame;
                        new Thread(new Runnable()
                        {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void run()
                            {
                                try
                                {
                                    if (mMediaPlayer == null)
                                    {
                                        mMediaPlayer = new MediaPlayer();
                                    }
                                    else
                                    {
                                        mMediaPlayer.reset();
                                    }
                                    Log.i("CJT", "URL = " + url);
                                    mMediaPlayer.setDataSource(url);
                                    mMediaPlayer.setSurface(mVideoView.getHolder().getSurface());
                                    mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer
                                            .OnVideoSizeChangedListener()
                                    {
                                        @Override
                                        public void
                                        onVideoSizeChanged(MediaPlayer mp, int width, int height)
                                        {
                                            updateVideoViewSize(mMediaPlayer.getVideoWidth(), mMediaPlayer
                                                    .getVideoHeight());
                                        }
                                    });
                                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                                    {
                                        @Override
                                        public void onPrepared(MediaPlayer mp)
                                        {
                                            mMediaPlayer.start();
                                        }
                                    });
                                    mMediaPlayer.setLooping(true);
                                    mMediaPlayer.prepare();
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }

            @Override
            public void recordZoom(float zoom)
            {
                CameraInterface.getInstance().setZoom(zoom, CameraInterface.TYPE_RECORDER);
            }

            @Override
            public void recordError()
            {
                //错误回调
                if (errorLisenter != null)
                {
                    errorLisenter.AudioPermissionError();
                }
            }
        });
        mCaptureLayout.setTypeLisenter(new TypeLisenter()
        {
            @Override
            public void cancel()
            {
                Logger.t(TAG).d("处理录制结果》"+"type:"+type+"confirm: false "+"CAMERA_STATE:"+CAMERA_STATE);
                if (CAMERA_STATE == STATE_WAIT)
                {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying())
                    {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    handlerPictureOrVideo(type, false);
                }
            }

            @Override
            public void confirm()
            {
                Logger.t(TAG).d("处理录制结果》"+"type:"+type+"confirm: true"+"CAMERA_STATE:"+CAMERA_STATE);
                if (CAMERA_STATE == STATE_WAIT)
                {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying())
                    {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    handlerPictureOrVideo(type, true);
                }
            }
        });
//        mCaptureLayout.setReturnLisenter(new ReturnLisenter() {
//            @Override
//            public void onReturn() {
//
//            }
//        });
        //END >>>>>>> captureLayout lisenter callback
        mVideoView.getHolder().addCallback(this);
    }

    public void switchCamera()
    {
        if (isBorrow || switching || forbiddenSwitch)
        {
            return;
        }
        switching = true;
        new Thread()
        {
            /**
             * switch camera
             */
            @Override
            public void run()
            {
                CameraInterface.getInstance().switchCamera(CameraView.this);
            }
        }.start();
    }

    /**
     * 返回当前屏幕是否为竖屏。
     *
     * @param context
     * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
     */
    private boolean isScreenOriatationPortrait(Context context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mWidth = dm.widthPixels;
        int mHeight = dm.heightPixels;

        if (mHeight > mWidth)
        {
// 竖屏
            return true;
        }
        else
        {
// 横屏
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        float heightSize = MeasureSpec.getSize(heightMeasureSpec);
        screenProp = heightSize / widthSize;
    }

    @Override
    public void cameraHasOpened()
    {
        CameraInterface.getInstance().doStartPreview(mVideoView.getHolder(), screenProp, new FirstFoucsLisenter()
        {
            @Override
            public void onFouce()
            {
                CameraView.this.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setFocusViewWidthAnimation(getWidth() / 2, getHeight() / 2);
                    }
                });
            }
        });
    }

    private boolean switching = false;

    @Override
    public void cameraSwitchSuccess()
    {
        switching = false;
    }

    /**
     * start preview
     */
    public void onResume()
    {
        CameraInterface.getInstance().registerSensorManager(mContext);
        CameraInterface.getInstance().setSwitchView(mSwitchCamera);
        if (onlyPause)
        {
//            if (isBorrow && type == TYPE_VIDEO) {
//                new Thread(new Runnable() {
//                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//                    @Override
//                    public void run() {
//                        try {
//                            if (mMediaPlayer == null) {
//                                mMediaPlayer = new MediaPlayer();
//                            } else {
//                                mMediaPlayer.reset();
//                            }
//                            Log.i("CJT", "URL = " + videoUrl);
//                            mMediaPlayer.setDataSource(videoUrl);
//                            mMediaPlayer.setSurface(mVideoView.getHolder().getSurface());
//                            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer
//                                    .OnVideoSizeChangedListener() {
//                                @Override
//                                public void
//                                onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//                                    updateVideoViewSize(mMediaPlayer.getVideoWidth(), mMediaPlayer
//                                            .getVideoHeight());
//                                }
//                            });
//                            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                @Override
//                                public void onPrepared(MediaPlayer mp) {
//                                    mMediaPlayer.start();
//                                }
//                            });
//                            mMediaPlayer.setLooping(true);
//                            mMediaPlayer.prepare();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            } else {
            new Thread()
            {
                @Override
                public void run()
                {
                    CameraInterface.getInstance().doOpenCamera(CameraView.this);
                }
            }.start();
            mFoucsView.setVisibility(INVISIBLE);
//            }
        }
    }

    /**
     * stop preview
     */
    public void onPause()
    {
        onlyPause = true;
        CameraInterface.getInstance().unregisterSensorManager(mContext);
        CameraInterface.getInstance().doStopCamera();
    }

    private boolean firstTouch = true;
    private float firstTouchLength = 0;
    private int zoomScale = 0;

    /**
     * handler touch focus
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1)
                {
                    //显示对焦指示器
                    setFocusViewWidthAnimation(event.getX(), event.getY());
                }
                if (event.getPointerCount() == 2)
                {
                    Log.i("CJT", "ACTION_DOWN = " + 2);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1)
                {
                    firstTouch = true;
                }
                if (event.getPointerCount() == 2)
                {
                    //第一个点
                    float point_1_X = event.getX(0);
                    float point_1_Y = event.getY(0);
                    //第二个点
                    float point_2_X = event.getX(1);
                    float point_2_Y = event.getY(1);

                    float result = (float) Math.sqrt(Math.pow(point_1_X - point_2_X, 2) + Math.pow(point_1_Y -
                            point_2_Y, 2));

                    if (firstTouch)
                    {
                        firstTouchLength = result;
                        firstTouch = false;
                    }
                    if ((int) (result - firstTouchLength) / 40 != 0)
                    {
                        firstTouch = true;
                        CameraInterface.getInstance().setZoom(result - firstTouchLength, CameraInterface.TYPE_CAPTURE);
                    }
                    Log.i(TAG, "result = " + (result - firstTouchLength));
                }
                break;
            case MotionEvent.ACTION_UP:
                firstTouch = true;
                break;
        }
        return true;
    }

    /**
     * focusview animation
     */
    private void setFocusViewWidthAnimation(float x, float y)
    {
        if (isBorrow)
        {
            return;
        }
        if (y > mCaptureLayout.getTop())
        {
            return;
        }
        mFoucsView.setVisibility(VISIBLE);
        if (x < mFoucsView.getWidth() / 2)
        {
            x = mFoucsView.getWidth() / 2;
        }
        if (x > layout_width - mFoucsView.getWidth() / 2)
        {
            x = layout_width - mFoucsView.getWidth() / 2;
        }
        if (y < mFoucsView.getWidth() / 2)
        {
            y = mFoucsView.getWidth() / 2;
        }
        if (y > mCaptureLayout.getTop() - mFoucsView.getWidth() / 2)
        {
            y = mCaptureLayout.getTop() - mFoucsView.getWidth() / 2;
        }
        CameraInterface.getInstance().handleFocus(mContext, x, y, new CameraInterface.FocusCallback()
        {
            @Override
            public void focusSuccess()
            {
                mFoucsView.setVisibility(INVISIBLE);
            }
        });

        mFoucsView.setX(x - mFoucsView.getWidth() / 2);
        mFoucsView.setY(y - mFoucsView.getHeight() / 2);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFoucsView, "scaleX", 1, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFoucsView, "scaleY", 1, 0.6f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFoucsView, "alpha", 1f, 0.3f, 1f, 0.3f, 1f, 0.3f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY).before(alpha);
        animSet.setDuration(400);
        animSet.start();
    }

    public void setCameraLisenter(CameraLisenter cameraLisenter)
    {
        this.cameraLisenter = cameraLisenter;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void handlerPictureOrVideo(int type, boolean confirm)
    {
        if (cameraLisenter == null || type == -1)
        {
            return;
        }
        switch (type)
        {
            case TYPE_PICTURE:
                mPhoto.setVisibility(INVISIBLE);
                if (confirm && captureBitmap != null)
                {
                    String fileKeyName = "a_" + SharePreUtils.getUserMobile(getContext()) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                    String outPutImagePath = NetHelper.getRootDirPath(getContext()) + NetHelper.CAMERA_FOLDER + CommonUtils.toMD5(fileKeyName);
                    try
                    {
                        final File outFile = new File(outPutImagePath);
                        ImageUtils.compressBitmap(captureBitmap, outFile.getPath(), 130, 0);
                        Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                    } catch (Exception e)
                    {
                        Logger.t(TAG).d("压缩失败" + e.getMessage());
                        outPutImagePath = "";
                    }
                    cameraLisenter.captureSuccess(showType, captureBitmap, outPutImagePath);
                }
                else
                {
                    if (captureBitmap != null)
                    {
                        captureBitmap.recycle();
                    }
                    captureBitmap = null;
                }
                break;
            case TYPE_VIDEO:
                if (confirm)
                {
                    //回调录像成功后的URL
                    cameraLisenter.recordSuccess(showType, videoUrl, firstFrame, videoTime);
                }
                else
                {
                    //删除视频
                    File file = new File(videoUrl);
                    if (file.exists())
                    {
                        file.delete();
                    }
                }
                mCaptureLayout.isRecord(false);
                LayoutParams videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                mVideoView.setLayoutParams(videoViewParam);
                CameraInterface.getInstance().doOpenCamera(CameraView.this);
                mSwitchCamera.setRotation(0);
                CameraInterface.getInstance().setSwitchView(mSwitchCamera);
                break;
        }
        isBorrow = false;
        mSwitchCamera.setVisibility(VISIBLE);
        mReturnView.setVisibility(VISIBLE);
        CAMERA_STATE = STATE_IDLE;
        mFoucsView.setVisibility(VISIBLE);
        setFocusViewWidthAnimation(getWidth() / 2, getHeight() / 2);

    }

    public void setSaveVideoPath(String path)
    {
        CameraInterface.getInstance().setSaveVideoPath(path);
    }

    /**
     * TextureView resize
     */
    public void updateVideoViewSize(float videoWidth, float videoHeight)
    {
        if (videoWidth > videoHeight)
        {
            LayoutParams videoViewParam;
            int height = (int) ((videoHeight / videoWidth) * getWidth());
            videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                    height);
            videoViewParam.gravity = Gravity.CENTER;
//            videoViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mVideoView.setLayoutParams(videoViewParam);
        }
    }

    /**
     * forbidden audio
     */
    public void enableshutterSound(boolean enable)
    {
    }

    public void forbiddenSwitchCamera(boolean forbiddenSwitch)
    {
        this.forbiddenSwitch = forbiddenSwitch;
    }

    private ErrorLisenter errorLisenter;

    //启动Camera错误回调
    public void setErrorLisenter(ErrorLisenter errorLisenter)
    {
        this.errorLisenter = errorLisenter;
        CameraInterface.getInstance().setErrorLinsenter(errorLisenter);
    }

    //设置CaptureButton功能（拍照和录像）
    public void setFeatures(int state)
    {
        this.mCaptureLayout.setButtonFeatures(state);
    }

    //设置录制质量
    public void setMediaQuality(int quality)
    {
        CameraInterface.getInstance().setMediaQuality(quality);
    }

    public void setTip(String tip)
    {
        mCaptureLayout.setTip(tip);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.i("CJT", "surfaceCreated");
        new Thread()
        {
            @Override
            public void run()
            {
                CameraInterface.getInstance().doOpenCamera(CameraView.this);
            }
        }.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        onlyPause = false;
        Log.i("CJT", "surfaceDestroyed");
        CameraInterface.getInstance().doDestroyCamera();
        screenSwitchUtils.stop();
    }
}
