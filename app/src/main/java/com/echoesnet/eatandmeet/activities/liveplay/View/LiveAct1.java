package com.echoesnet.eatandmeet.activities.liveplay.View;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.LivePresenter;
import com.echoesnet.eatandmeet.activities.liveplay.managers.ViewShareHelper;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.IOnAppStateChangeListener;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.LAnchorsListBean;
import com.echoesnet.eatandmeet.models.datamodel.ExitRoomType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TXConstants;
import com.echoesnet.eatandmeet.utils.IMUtils.TXMessageEvent;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TransAVSDKCode;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.MemoryUtils.MemoryHelper;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ScreenObserver;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.LiveNewMsgDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.orhanobut.logger.Logger;
import com.tencent.TIMCallBack;
import com.tencent.TIMConnListener;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;
import com.tencent.av.TIMAvManager;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveMemStatusLisenter;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.core.impl.ILVBRoom;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 注意：本Activity - 并没有拆分P层
 * 开启视频，登录IM 初始化root view
 */
public abstract class LiveAct1<P extends LivePresenter> extends LiveBaseAct<P>
        implements ILiveRoomOption.onRoomDisconnectListener, ILiveMemStatusLisenter,
        AVRootView.onSubViewCreatedListener
{
    private static final String TAG = LiveAct1.class.getSimpleName();

    protected String roomName, sign, flyPage;// 房间 id->名字->是否签约
    public AVRootView avRootView;

    //最底层View;
    protected FrameLayout mStackFrame = null;
    protected FrameLayout mFollowView = null; //底层View叠加一个组，为了滑ping
    private LayoutInflater mInflater = null;

    public String backGroundId = "";//位于底层视频id
    private long streamChannelID;
    private Activity mActivity;
    private String vedioName;//录播文件名
    protected List<LAnchorsListBean> lAnchorsList;

    private Timer mHearBeatTimer;
    private HeartBeatTask mHeartBeatTask;//心跳
    protected boolean isInAvRoom = false;//是否进入房间
    private boolean isPushed = false, isRecord = false;// 是否开启 推流 ， 录播

    private int heatNetFailedCounter = 0;
    private boolean isLiveDead = false; //直播是否已经被后台杀死
    private boolean videoSteamPause = false;//主播是否是被人为断流，跟着 hostVideoStreamResume Pause
    public boolean isShowInviteLive = false;//是否连麦
    private static final int countDownTime = 30; //检测关闭摄像头 延时时间
    public Handler handler = new Handler();

    private boolean isRegister = false;

    public static long currentTime;

    private boolean isStartCountDown = false;
    private MyProgressDialog pDialog;
    private ScreenObserver screenObserver;

    protected ImageView ivRoomFlyPage;//蒙版图

    protected boolean isSwitch2Back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   // 不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        mActivity = this;
        try
        {
            registerReceiver();
        } catch (Exception ex)
        {
            Logger.t(TAG).d(ex.getMessage());
        }

        mStackFrame = createStackView();

        mInflater = LayoutInflater.from(this);
        mStackFrame.setFitsSystemWindows(true);
        mFollowView = createStackView();
        mStackFrame.addView(mFollowView);
        roomName = getIntent().getStringExtra("roomName");
        sign = getIntent().getStringExtra("sign");
        flyPage = getIntent().getStringExtra("flyPage");
        lAnchorsList = (List<LAnchorsListBean>) getIntent().getSerializableExtra("lAnchorsList");

        // FIXME: 2017/3/24 need optimization
        EamApplication.getInstance().liveIdentity = mPresenter.getmRecord().getModeOfRoom();
        ViewShareHelper.liveMySelfRole = mPresenter.getmRecord().getModeOfRoom();
        backGroundId = "u" + mPresenter.getmRecord().getRoomId();
        Logger.t(TAG).d("backGroundId:" + backGroundId);
        super.setContentView(mStackFrame);

        /** 好像必须每次进入初始化，否则第二次ILVLiveConfig 空指针（这是由于你在销毁房间的时候移除了相关资源--wb）
         * ILVLiveManager init  ({@link com.tencent.livesdk.liveMgr}) -> return {@link ILVBRoom} init -> "ILiveConstants.NO_ERR;"
         */
        ILVLiveConfig liveConfig = new ILVLiveConfig();
        liveConfig.messageListener(TXMessageEvent.getInstance());
        //  liveConfig.setLiveMsgListener(TXMessageEvent.getInstance());
        ILVLiveManager.getInstance().init(liveConfig);
        avRootView = createAvRootView();
        ILVLiveManager.getInstance().setAvVideoView(avRootView);
        avRootView.setGravity(AVRootView.LAYOUT_GRAVITY_RIGHT);
        avRootView.setSubMarginY(getResources().getDimensionPixelSize(R.dimen.small_area_margin_top));
        avRootView.setSubMarginX(getResources().getDimensionPixelSize(R.dimen.small_area_marginright));
        avRootView.setSubPadding(getResources().getDimensionPixelSize(R.dimen.small_area_marginbetween));
        avRootView.setSubWidth(getResources().getDimensionPixelSize(R.dimen.video_small_view_width));
        avRootView.setSubHeight(getResources().getDimensionPixelSize(R.dimen.video_small_view_height));
        avRootView.setSubCreatedListener(this);
        mFollowView.addView(avRootView, -1);
        pDialog = new MyProgressDialog()
                .buildDialog(mActivity)
                .setDescription("正在处理...");
        pDialog.setCancelable(false);

        //直播中被顶号
        HuanXinIMHelper.getInstance().setQuitAccountListener(new HuanXinIMHelper.IQuitAccountFinishListener()
        {
            @Override
            public void QuitAccountSuccess(String quitType)
            {
                exitRoom(ExitRoomType.CONFLICT);
            }
        });
        //IM链接监听
        ILiveSDK.getInstance().getTIMManger().setConnectionListener(timConnListener);
        //切换到后台触发
        MemoryHelper.getInstance().setAppStateChangeListener(new AppStateChange(LiveAct1.this));
        screenObserver = new ScreenObserver(mActivity);
        screenObserver.startObserver(screenStateListener);

                /*ImSDK登录以后默认会获取最近联系人漫游，同时每个会话会获取到最近的一条消息。如果不需要此功能，可以调用方法禁用：
        禁止登陆后拉取最近联系人*/
        TIMManager.getInstance().disableRecentContact();
        final String tlsName = SharePreUtils.getTlsName(mActivity);
        final String loginUser = ILiveSDK.getInstance().getTIMManger().getLoginUser();
        if (!TextUtils.isEmpty(loginUser) && loginUser.equals(tlsName))//已经登录过了就不登录了
        {
            startLiveOperation();
        }
        else
        {
            TencentHelper.txLogin(new TencentHelper.TXLoginFinishListener()
            {
                @Override
                public void onSuccess(Object o)
                {
                    Logger.t("TestLoginTXIM").d("成功");
                    startLiveOperation();
                }

                @Override
                public void onDefeat(int o, String msg)
                {
                    Logger.t("TestLoginTXIM").d("失败");
                }
            });
        }
    }

    private ScreenObserver.ScreenStateListener screenStateListener = new ScreenObserver.ScreenStateListener()
    {
        @Override
        public void onScreenOn()
        {
            Logger.t(TAG).d("screenStateListener>>开屏");
        }

        @Override
        public void onScreenOff()
        {
            Logger.t(TAG).d("screenStateListener>>锁屏");
            EamLogger.t(TAG).writeToDefaultFile("锁屏监听触发调用---->ILVLiveManager.getInstance().onPause()");
            ILVLiveManager.getInstance().onPause();
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        int code = ILiveRoomManager.getInstance().enableMic(false);
                        Logger.t(TAG).d("enableSpeaker>>>>>>>>>" + code);
                        if (code != 0)
                        {
                            ILiveRoomManager.getInstance().enableMic(false);
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d(e.getMessage());
                    }
                }
            }, 300);
            if (mActivity != null)
            {
                if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                    hostVideoStreamPause();
            }
        }

        @Override
        public void onUserPresent()
        {
        }
    };

    private TIMConnListener timConnListener = new TIMConnListener()
    {
        @Override
        public void onConnected()
        {
            EamLogger.t(TAG).writeToDefaultFile("TXIM>>>Connected");
            Logger.t(TAG).d("TXIM>>>Connected");
        }

        @Override
        public void onDisconnected(int i, String s)
        {
            EamLogger.t(TAG).writeToDefaultFile("TXIM>>>Disconnected>>i==" + i + "|s==" + s);
            Logger.t(TAG).d("TXIM>>>Disconnected>>i==" + i + "|s==" + s);
        }

        @Override
        public void onWifiNeedAuth(String s)
        {
            EamLogger.t(TAG).writeToDefaultFile("TXIM>>>onWifiNeedAuth>>");
            Logger.t(TAG).d("TXIM>>>onWifiNeedAuth>>" + "s==" + s);
        }
    };


    @Override
    protected void onResume()
    {
        super.onResume();
        Logger.t(TAG).d("LiveAct1>onResume");
        SharePopWindow.isShared = false;
        ILVLiveManager.getInstance().onResume();
        try
        {
            ILiveRoomManager.getInstance().enableMic(true);
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("enableMic err :" + e.getMessage());
        }
        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
            hostVideoStreamResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Logger.t(TAG).d("LiveAct1>onPause");
        if (isFinishing())
        {
            EamLogger.t(TAG).writeToDefaultFile("结束LiveActivity开始了，正常情况下表示直播结束了，" +
                    "主动关闭直播，异常情况下表示系统回收了直播activity");
            Logger.t(TAG).d("结束LiveActivity开始了，正常情况下表示直播结束了");
            ILVLiveManager.getInstance().onDestory();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if ("oppo".equalsIgnoreCase(Build.BRAND))
        {
            if (CommonUtils.isSwitched2Back)
                switch2Back();
        }
    }

    @Override
    public void finish()
    {
        MemoryHelper.getInstance().removeAppStateChangeListener();
        HuanXinIMHelper.getInstance().removeQuitAccountListener();
        ViewShareHelper.liveMySelfRole = LiveRecord.ROOM_MODE_MEMBER;
        LiveNewMsgDialog.liveMsgView = null;
        timConnListener = null;
        heatNetFailedCounter = 0;
        ILiveSDK.getInstance().getTIMManger().setConnectionListener(null);
        stopSendHeart();
        if (isInAvRoom)
        {
            ILVLiveManager.getInstance().quitRoom(new ILiveCallBack()
            {
                @Override
                public void onSuccess(Object data)
                {
                    avRootView.clearUserView();
                    ILiveLog.d(TAG, "ILVB-DBG|quit room sucess");
                }

                @Override
                public void onError(String module, int errCode, String errMsg)
                {
                    ILiveLog.d(TAG, "ILVB-DBG|quit room failed:" + module + "|" + errCode + "|" + errMsg + "\n" + TransAVSDKCode.avSDKCode(errCode + ""));
                }
            });
        }
        //这个函数是移除消息监听的，不要瞎调用--wb
        ILVLiveManager.getInstance().shutdown();
        ILVLiveManager.getInstance().onDestory();
        if (isRegister)
        {
            try
            {
                mActivity.unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        super.finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        screenStateListener = null;
        screenObserver.shutdownObserver();
        Logger.t(TAG).d("LiveAct1》onDestroy触发");
    }

    @Override
    public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        Logger.t(TAG).d("直播activity的内存裁剪触发了》" + level);
    }

    //region ===============钩子方法，这些方法可以看做是顶层设计的接口======================================

    /**
     * 创建房间成功回调
     */
    protected void onCreateRoomSuccess()
    {
    }

    /**
     * 加入房间成功回调
     */
    protected void onJoinRoomSuccess()
    {
    }

    /**
     * 创建房间失败回调
     *
     * @param module
     * @param errCode
     * @param errMsg
     */
    protected void onCreateRoomError(String module, int errCode, String errMsg)
    {
    }

    /**
     * 加入房间失败
     *
     * @param module
     * @param errCode
     * @param errMsg
     */
    protected void onJoinRoomError(String module, int errCode, String errMsg)
    {
    }

    /**
     * 主播退出房间成功
     *
     * @param type 退出房间的方式
     */
    protected void onHostExitRoomSuccess(ExitRoomType type)
    {
    }

    /**
     * 用户退出房间成功
     *
     * @param type
     */
    protected void onMemberExitRoomSuccess(ExitRoomType type)
    {
    }

    /**
     * 主播退出房间失败
     *
     * @param module
     * @param errCode
     * @param errMsg
     */
    protected void onHostExitRoomError(String module, int errCode, String errMsg)
    {
    }

    /**
     * 用户退出房间失败
     *
     * @param module
     * @param errCode
     * @param errMsg
     * @param type
     */
    protected void onMemberExitRoomError(String module, int errCode, String errMsg, ExitRoomType type)
    {
    }

    /**
     * 切换房间成功
     */
    protected void onSwitchRoomSuccess()
    {
    }

    /**
     * 切换房间失败
     *
     * @param module
     * @param errCode
     * @param errMsg
     */
    protected void onSwitchRoomError(String module, int errCode, String errMsg)
    {
    }

    /**
     * 房间断开连接
     *
     * @param errCode
     * @param errMsg
     * @return
     */
    protected void onRoomDisconnected(int errCode, String errMsg)
    {
    }

    /**
     * 用户加入房间前操作
     */
    protected void onPendingJoinRoom()
    {
//        startTaskForLiveStatus(3, 2);
    }

    /**
     * 主播创建房间前操作
     */
    protected void onPendingCreateRoom()
    {
//        startTaskForLiveStatus(6, 2);
    }


    protected void onPendingCloseRoom()
    {

    }

    /**
     * 如果期望异步操作，可以加一个回调参数
     */
    protected void onPendingQuitRoom()
    {
    }

    /**
     * 主播音视频流恢复
     */
    protected void hostVideoStreamResume()
    {
        mSendFpsDefeatT = 0;
        videoSteamPause = false;
    }

    /**
     * 主播音视频流暂停
     */
    protected void hostVideoStreamPause()
    {
        videoSteamPause = true;
    }

    /**
     * 观众音视频流暂停
     */
    protected void memberVideoStreamPause()
    {
    }

    /**
     * 观众音视频流恢复
     */
    protected void memberVideoStreamResume()
    {
    }

    public void onBackGroundIdChanged(String backGroundId)
    {
    }

    protected void closeInviteLive(String role, String reason)
    {
    }

    /**
     * 有用户开启摄像头动作
     *
     * @param ids 用户id
     */
    protected void actionHasOpenCamera(List<String> ids)
    {

    }

    /**
     * 有用户关闭摄像头动作
     *
     * @param ids 用户id
     */
    protected void actionHasNoCamera(List<String> ids)
    {

    }

    /**
     * 游戏开始
     */
    protected void gameStart()
    {

    }

    /**
     * 游戏结束
     */
    protected void gameStop()
    {

    }

    /**
     * 有屏幕分享
     */
    protected void hasScreenVideo(List<String> ids, int type)
    {

    }

    //endregion============================================================================
    //实现 ILiveRoomOption.onRoomDisconnectListener
    //断线
    @Override
    public void onRoomDisconnect(int errCode, String errMsg)
    {
        //CommonUtils.writeLog2File(mActivity, "onRoomDisconnect   errCode" + errCode + "   errorMsg" + errMsg, null);
        EamLogger.t(TAG).writeToDefaultFile("onRoomDisconnect   errCode" + errCode + "   errorMsg" + errMsg);
        Logger.t(TAG).d("onRoomDisconnect   errCode" + errCode + "   errorMsg" + errMsg);
        onRoomDisconnected(errCode, errMsg);
    }

    // AVRootView.onSubViewCreatedListener小窗口开启回调  //开直播成功时调用
    @Override
    public void onSubViewCreated()
    {
        for (int i = 1; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++)
        {
            final int index = i;
            Logger.t(TAG).d("小视频创建成功》" + index);
            AVVideoView avVideoView = avRootView.getViewByIndex(index);
            avVideoView.setMirror(true);
            avVideoView.setGestureListener(new GestureDetector.SimpleOnGestureListener()
            {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e)
                {
                    String belowId = avRootView.getViewByIndex(0).getIdentifier();
                    String aboveId = avRootView.getViewByIndex(1).getIdentifier();
                    avRootView.swapVideoView(0, index);
                    backGroundId = avRootView.getViewByIndex(0).getIdentifier();
                    Logger.t(TAG).d("backGroundId:" + backGroundId);
//                            updateHostLeaveLayout();
                    Logger.t(TAG).d("切换视频2》" + index);

                    return super.onSingleTapConfirmed(e);
                }
            });

        }
        avRootView.getViewByIndex(0).setMirror(true);
//        avRootView.getViewByIndex(0).setRotate(false);
//        avRootView.getViewByIndex(0).setDiffDirectionRenderMode(BaseVideoView.BaseRenderMode.BLACK_TO_FILL);
//        avRootView.getViewByIndex(0).setSameDirectionRenderMode(BaseVideoView.BaseRenderMode.SCALE_TO_FIT);
        avRootView.getViewByIndex(0).setRecvFirstFrameListener(new AVVideoView.RecvFirstFrameListener()
        {
            @Override
            public void onFirstFrameRecved(int width, int height, int angle, String identifier)
            {
                Logger.t(TAG).d("------->首帧到达了。。。。");
                mActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ImageUtils.fadeOut(ivRoomFlyPage, 500);
                    }
                });
            }
        });
/*        avRootView.getViewByIndex(0).setGestureListener(new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
            {
                if (e1.getY() - e2.getY() > 20 && Math.abs(velocityY) > 10)
                {
                    //bSlideUp = true;
                }
                else if (e2.getY() - e1.getY() > 20 && Math.abs(velocityY) > 10)
                {
                    //bSlideUp = false;
                }
                switchRoom();

                return false;
            }
        });*/

    }

    //    ILiveMemStatusLisenter
    @Override
    public boolean onEndpointsUpdateInfo(int eventid, String[] updateList)
    {
        Logger.t(TAG).d("ILVB-DBG|onEndpointsUpdateInfo. eventid = " + eventid + "/" + mActivity);
        Logger.t(TAG).d("用户事件监听 eventId：" + eventid + "  |  userId:" + Arrays.toString(updateList));
        if (null == mActivity)
        {
            return false;
        }
        switch (eventid)
        {
            // 进入房间事件
            case ILiveConstants.TYPE_MEMBER_CHANGE_IN:
                break;
            //退出房间事件
            case ILiveConstants.TYPE_MEMBER_CHANGE_OUT:
                break;
            //有发摄像头视频事件
            case ILiveConstants.TYPE_MEMBER_CHANGE_HAS_CAMERA_VIDEO:
                String connectUserId = "";
                List<String> updateUserList = new ArrayList<>();
                for (String s : updateList)
                {
                    connectUserId += s + " | ";
                    updateUserList.add(s);
                }
                Logger.t(TAG).d("连麦用户ID:" + connectUserId);
                if (LiveRecord.ROOM_MODE_HOST == mPresenter.getmRecord().getModeOfRoom())
                {
                    if (isStartCountDown)
                        handler.removeCallbacks(runnable);
                }
                EamLogger.t(TAG).writeToDefaultFile("有摄像头事件用户ID》:" + connectUserId);
                actionHasOpenCamera(updateUserList);
                break;
            //无发摄像头视频事件
            case ILiveConstants.TYPE_MEMBER_CHANGE_NO_CAMERA_VIDEO:
                Logger.t(TAG).d("用户事件：无摄像头视频事件");
                List<String> updateUsersList = new ArrayList<>();
                for (String s : updateList)
                {
                    updateUsersList.add(s);
                    if (LiveRecord.ROOM_MODE_HOST == mPresenter.getmRecord().getModeOfRoom())
                    {
                        if (s.equals(SharePreUtils.getTlsName(mActivity)))
                        {
                            //onResume
                            ILiveRoomManager.getInstance().enableCamera(ILiveRoomManager.getInstance().getCurCameraId(), true);
                            ILiveRoomManager.getInstance().enableMic(true);

                            EamLogger.t(TAG).writeToDefaultFile("发现主播自己无摄像头事件：");
                        }
                        else
                        {
                            handler.postDelayed(runnable, 1000 * countDownTime);
                            isStartCountDown = true;
                        }
                    }
                }
                EamLogger.t(TAG).writeToDefaultFile("无摄像头事件用户IDs:" + Arrays.toString(updateList));
                actionHasNoCamera(updateUsersList);
                break;
            //有发语音事件
            case ILiveConstants.TYPE_MEMBER_CHANGE_HAS_AUDIO:
                break;
            //无发语音事件
            case ILiveConstants.TYPE_MEMBER_CHANGE_NO_AUDIO:
                Logger.t(TAG).d("用户事件：无语音事件");
                break;
            //有发屏幕视频事件
            case ILiveConstants.TYPE_MEMBER_CHANGE_HAS_SCREEN_VIDEO:
                List<String> ids = Arrays.asList(updateList);
                hasScreenVideo(ids, AVView.VIDEO_SRC_TYPE_SCREEN);
                break;
            //无发屏幕视频事件
            case ILiveConstants.TYPE_MEMBER_CHANGE_NO_SCREEN_VIDEO:
                break;
            case ILiveConstants.TYPE_MEMBER_CHANGE_HAS_FILE_VIDEO:
                List<String> videoIds = Arrays.asList(updateList);
                hasScreenVideo(videoIds, AVView.VIDEO_SRC_TYPE_MEDIA);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 这个重载比较重要，使得子类的 View 像 图层 1-》2-》3 叠加起来；
     *
     * @param view
     * @param params
     */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params)
    {
        if (view == null)
            return;
        initChildView(view, params);
    }

    @Override
    public void setContentView(View view)
    {
        if (view == null)
            return;
        initChildView(view, null);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        if (layoutResID <= 0)
        {
            throw new ExceptionInInitializerError("this view is not legal.");
        }
        View childView = mInflater.inflate(layoutResID, null, true);
        if (childView == null)
        {
            return;
        }
        initChildView(childView, null);
    }


    private void initChildView(final View childView, final ViewGroup.LayoutParams params)
    {
        childView.setBackgroundColor(Color.TRANSPARENT);
        if (params == null)
        {
            this.mStackFrame.addView(childView);
        }
        else
        {
            this.mStackFrame.addView(childView, params);
        }
    }

    private final FrameLayout createStackView()
    {
        FrameLayout frame = new FrameLayout(this);
        frame.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        return frame;
    }

    private final AVRootView createAvRootView()
    {
        AVRootView avRootView = new AVRootView(this);
        avRootView.setId(R.id.avRootView);
        avRootView.setLayoutParams(new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels));
        return avRootView;
    }

    /**
     * 房间总入口入口
     */
    private void startLiveOperation()
    {
        int i = NetHelper.getNetworkStatus(mActivity);
        switch (i)
        {
            case -1:
                ToastUtils.showShort("当前无网络连接");
                finish();
                break;
            case 1:
                switch (mPresenter.getmRecord().getModeOfRoom())
                {
                    case LiveRecord.ROOM_MODE_HOST:
                        createRoom();
                        break;
                    case LiveRecord.ROOM_MODE_MEMBER:
                        joinRoom();
                        break;
                }
                break;
            case 2:
            case 3:
                String quiteText = "退出观看";
                if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                    quiteText = "放弃直播";
                new CustomAlertDialog(mActivity)
                        .builder()
                        .setMsg("当前网络状态为移动网络，请确认")
                        .setTitle("提示")
                        .setPositiveButton("土豪请继续", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                switch (mPresenter.getmRecord().getModeOfRoom())
                                {
                                    case LiveRecord.ROOM_MODE_HOST:
                                        createRoom();
                                        break;
                                    case LiveRecord.ROOM_MODE_MEMBER:
                                        joinRoom();
                                        break;
                                }
                            }
                        })
                        .setNegativeButton(quiteText, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
                break;
            default:
                break;
        }
    }

    /**
     * 直播心跳
     */
    private class HeartBeatTask extends TimerTask
    {
        @Override
        public void run()
        {
            if (!isLiveDead)
            {
                if (mPresenter != null)
                    mPresenter.sendHeartBeat();
            }
            //NetHelper.obtainWifiInfo(mActivity, null);
        }
    }

    protected void switchRoom()
    {
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack()
        {
            @Override
            public void onSuccess(Object data)
            {
                ILiveLog.d(TAG, "ILVB-DBG|quitRoom->success");
                startLiveOperation();
            }

            @Override
            public void onError(String module, int errCode, String errMsg)
            {
                ILiveLog.d(TAG, "ILVB-DBG|quitRoom->failed:" + module + "|" + errCode + "|" + errMsg + "\n" + TransAVSDKCode.avSDKCode(errCode + ""));
                finish();
            }
        });
    }

    private void reJoinRoom()
    {
//        switch (modeOfRoom){
//            case LivePlayAct1.ROOM_MODE_HOST:
//
//                break;
//            case LivePlayAct1.ROOM_MODE_MEMBER:
//                ILiveRoomOption memberOption = new ILiveRoomOption(EamApplication.getInstance().getSpUtil().getTlsName())
//                        .autoCamera(false)
//                        .roomDisconnectListener(this)
//                        .controlRole(TXConstants.NORMAL_MEMBER_ROLE)
//                        .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)
//                        .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
//                        .autoMic(false);
//                ILVLiveManager.getInstance().switchRoom(Integer.parseInt(strRoomid), memberOption, new ILiveCallBack() {
//                    @Override
//                    public void onSuccess(Object data) {
//                        ILiveLog.d(TAG, "ILVB-DBG|switchRoom->join room sucess");
//                    }
//
//                    @Override
//                    public void onError(String module, int errCode, String errMsg) {
//                        ILiveLog.d(TAG, "ILVB-DBG|switchRoom->join room failed:" + module + "|" + errCode + "|" + errMsg + "\n" + TransAVSDKCode.avSDKCode(errCode+""));
//                    }
//                });
//                ILiveLog.i(TAG, "switchRoom ");
//                break;
//        }
    }


    /**
     * 创建房间
     */
    public void createRoom()
    {
        onPendingCreateRoom();
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(SharePreUtils.getTlsName(mActivity))
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_NORMAL)
                .controlRole(TXConstants.HOST_ROLE)
                .authBits(TXConstants.VIDEO_MEMBER_AUTH)
                .setHostMirror(true)
                .setRoomMemberStatusLisenter(this)
                .autoRender(false)
                .autoFocus(true)
                .autoMic(true)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_MANUAL);
        if (ILVLiveManager.getInstance() == null)
            return;
        int createCode = ILVLiveManager.getInstance().createRoom(Integer.parseInt(mPresenter.getmRecord().getRoomId()), hostOption, new ILiveCallBack()
        {
            @Override
            public void onSuccess(Object data)
            {
                EamLogger.t(TAG).writeToDefaultFile("ILVB-DBG|startEnterRoom->create room sucess>" + data.toString());
                Logger.t(TAG).d("ILVB-DBG|startEnterRoom->create room sucess>" + data.toString());
                onCreateRoomSuccess();
                isInAvRoom = true;
                //主播心跳  6秒一次
                mHearBeatTimer = new Timer(true);
                mHeartBeatTask = new HeartBeatTask();
                mHearBeatTimer.schedule(mHeartBeatTask, 1000, 6 * 1000);
                startPushAction();
                if (mPresenter != null && mPresenter.getmRecord().getEnterRoom4EH().getIsSignedAnchor().equals("1"))
                {
                    startRecord();
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg)
            {
                EamLogger.t(TAG).writeToDefaultFile("ILVB-DBG|startEnterRoom->create room failed:" + module + "|" + errCode + "|" + errMsg + "\n" + TransAVSDKCode.avSDKCode(errCode + ""));
                Logger.t(TAG).d("ILVB-DBG|startEnterRoom->create room failed:" + module + "|" + errCode + "|" + errMsg + "\n" + TransAVSDKCode.avSDKCode(errCode + ""));
                try
                {
                    ILiveSDK.getInstance().uploadLog("创建房间失败日志：" + DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()).toString(), 0, new ILiveCallBack<String>()
                    {
                        @Override
                        public void onSuccess(String data)
                        {
                            Logger.t(TAG).d("上传日志成功：" + data);
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg)
                        {
                            Logger.t(TAG).d("上传日志失败：" + module + " | " + errCode + " | " + errMsg);
                        }
                    });
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                onCreateRoomError(module, errCode, errMsg);
                isInAvRoom = false;
            }
        });
        EamLogger.t(TAG).writeToDefaultFile("ILVB-DBG|startEnterRoom->create room code:" + createCode);
        switch (createCode)
        {
            case ILiveConstants.NO_ERR:
                //返回 NO_ERR 为已成功
                break;
            case ILiveConstants.ERR_ALREADY_IN_ROOM:
                //已经在房间中  视为加入成功
                onCreateRoomSuccess();
                isInAvRoom = true;
                //主播心跳  6秒一次
                mHearBeatTimer = new Timer(true);
                mHeartBeatTask = new HeartBeatTask();
                mHearBeatTimer.schedule(mHeartBeatTask, 1000, 6 * 1000);
                startPushAction();
                if (mPresenter != null && mPresenter.getmRecord().getEnterRoom4EH().getIsSignedAnchor().equals("1"))
                {
                    startRecord();
                }
                break;
            case ILiveConstants.ERR_INVALID_PARAM:
                //ILVLiveRoomOption 为空
                break;
        }
        Logger.t(TAG).d("createRoom startEnterRoom ");
    }

    /**
     * 加入房间
     */
    public void joinRoom()
    {
        onPendingJoinRoom();
        ILiveLog.d(TAG, "ILVB-DBG|startEnterRoom->start join room");
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(SharePreUtils.getTlsName(mActivity))
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_NORMAL)
                .controlRole(TXConstants.NORMAL_MEMBER_ROLE)
                .authBits(TXConstants.NORMAL_MEMBER_AUTH)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_MANUAL)
                .setRoomMemberStatusLisenter(this)
                .autoFocus(true)
                .autoRender(false)
                .autoMic(false)
                .autoCamera(false);
        if (ILVLiveManager.getInstance() == null)
            return;
        int roomId = 0;
        try
        {
            roomId = Integer.parseInt(mPresenter.getmRecord().getRoomId());
            EamLogger.t(TAG).writeToDefaultFile("尝试加入的房间号》 " + roomId);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            throw e;
        }

        int joinCode = ILVLiveManager.getInstance().joinRoom(roomId, memberOption, new ILiveCallBack()
        {
            @Override
            public void onSuccess(Object data)
            {
                EamLogger.t(TAG).writeToDefaultFile("ILVB-DBG|startEnterRoom->join room sucess");
                ILiveLog.d(TAG, "ILVB-DBG|startEnterRoom->join room sucess");
                onJoinRoomSuccess();
                isInAvRoom = true;
            }

            @Override
            public void onError(String module, int errCode, String errMsg)
            {
                EamLogger.t(TAG).writeToDefaultFile("ILVB-DBG|startEnterRoom->join room failed:" + module + "|" + errCode + "|" + errMsg + "\n" + TransAVSDKCode.avSDKCode(errCode + ""));
                Logger.t(TAG).d("ILVB-DBG|startEnterRoom->join room failed:" + module + "|" + errCode + "|" + errMsg + "\n" + TransAVSDKCode.avSDKCode(errCode + ""));
                try
                {
                    ILiveSDK.getInstance().uploadLog("加入房间失败日志：" + DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()).toString(), 0, new ILiveCallBack<String>()
                    {
                        @Override
                        public void onSuccess(String data)
                        {
                            Logger.t(TAG).d("上传日志成功：" + data);
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg)
                        {
                            Logger.t(TAG).d("上传日志失败：" + module + " | " + errCode + " | " + errMsg);
                        }
                    });
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                onJoinRoomError(module, errCode, errMsg);
                //这个是AV层
                isInAvRoom = true;
            }
        });
        EamLogger.t(TAG).writeToDefaultFile("ILVB-DBG|startEnterRoom->join room code:" + joinCode);
        switch (joinCode)
        {
            case ILiveConstants.NO_ERR:
                //返回 NO_ERR 为已成功
                break;
            case ILiveConstants.ERR_ALREADY_IN_ROOM:
                //已经在房间中  视为加入成功
                onJoinRoomSuccess();
                isInAvRoom = true;
                break;
            case ILiveConstants.ERR_INVALID_PARAM:
                //ILVLiveRoomOption 为空
                break;
        }
        ILiveLog.d(TAG, "ILVB-DBG|startEnterRoom");
//        ILiveRoomManager.getInstance().set;
//        ILVLiveManager.getInstance().upToVideoMember();
    }

    public void exitRoom(final ExitRoomType type, ILiveCallBack liveCallBack)
    {
        //ILVLiveManager.getInstance().shutdown();
        ILVLiveManager.getInstance().quitRoom(liveCallBack);
    }

    /**
     * 退出房间
     *
     * @param type {@link ExitRoomType}ExitRoom.NORMAL：正常退出 CONFLICT：被踢退出 PASSIVE : 被动退出
     */
    public void exitRoom(final ExitRoomType type)
    {
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
        Logger.t(TAG).d("exitRoom>>" + mPresenter.getmRecord().getModeOfRoom());
        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
        {
            try
            {
                String des = "退出房间日志：" + DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()).toString();
                ILiveSDK.getInstance().uploadLog(des, 0, new ILiveCallBack<String>()
                {
                    @Override
                    public void onSuccess(String data)
                    {
                        Logger.t(TAG).d("上传日志成功：" + data);
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg)
                    {
                        Logger.t(TAG).d("上传日志失败：" + module + " | " + errCode + " | " + errMsg);
                    }
                });
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            onPendingCloseRoom();
            stopPushAction(type);
            if (mPresenter.getmRecord().getEnterRoom4EH().getIsSignedAnchor().equals("1"))
            {
                stopRecord(type);
            }
        }
        else
        {
            onPendingQuitRoom();
            quitRoom(type);
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void quitRoom(final ExitRoomType type)
    {
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack()
        {
            @Override
            public void onSuccess(Object data)
            {
                EamLogger.t(TAG).writeToDefaultFile("退出房间成功》" + data.toString());
                Logger.t(TAG).d("退出房间成功》" + data.toString());
                isInAvRoom = false;
                SharePreUtils.setPreGroupId(mActivity, "");
                stopSendHeart();

                //重置 绑定关系 全部解除绑定
                for (int i = 0; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++)
                {
                    avRootView.bindIdAndView(i, AVView.VIDEO_SRC_TYPE_CAMERA, null, true);
                }
                avRootView.clearUserView();
//                avRootView.onDestory();
                if (mPresenter != null)
                {
                    if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                        onHostExitRoomSuccess(type);
                    else
                        mPresenter.exitRoomToCallServer(mPresenter.getmRecord().getRoomId(), type);
                    //                    onMemberExitRoomSuccess(type);
                }

                //处理被踢的情况，如果被踢则说明已经退出账号了，也要退出腾讯
                if (ExitRoomType.CONFLICT == type)
                {
                    ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack()
                    {
                        @Override
                        public void onSuccess(Object data)
                        {
                            ILVLiveManager.getInstance().shutdown();
                            ILVLiveManager.getInstance().onDestory();
                            //清除本地缓存
                            Logger.t(TAG).d("腾讯退出成功");
                            EamLogger.t("TXIM").writeToDefaultFile("腾讯退出成功");
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg)
                        {
                            ILVLiveManager.getInstance().shutdown();
                            ILVLiveManager.getInstance().onDestory();
                            //ToastUtils.showShort(mContext, "T退出失败 " + errMsg);
                            Logger.t(TAG).d("腾讯退出错误码》" + errCode + "描述》" + errMsg);
                            EamLogger.t("TXIM").writeToDefaultFile("腾讯退出错误码》" + errCode + "描述》" + errMsg);
                        }
                    });
                }
                else
                {
                    ILVLiveManager.getInstance().shutdown();
                    ILVLiveManager.getInstance().onDestory();
                }
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onError(String module, int errCode, String errMsg)
            {
                EamLogger.t(TAG).writeToDefaultFile("退出房间失败》" + module + "|" + errCode + "|" + errMsg + "\n" + TransAVSDKCode.avSDKCode(errCode + ""));
                Logger.t(TAG).d("ILVB-DBG|quitRoom->failed:" + module + "|" + errCode + "|" + errMsg + "\n" + TransAVSDKCode.avSDKCode(errCode + ""));
                if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                {
                    stopSendHeart();

                    //重置 绑定关系 全部解除绑定
                    for (int i = 0; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++)
                    {
                        avRootView.bindIdAndView(i, AVView.VIDEO_SRC_TYPE_CAMERA, null, true);
                    }
                    avRootView.clearUserView();

                    onHostExitRoomError(module, errCode, errMsg);

                    ILVLiveManager.getInstance().shutdown();
                    ILVLiveManager.getInstance().onDestory();
                }
                else
                    onMemberExitRoomError(module, errCode, errMsg, type);
                isInAvRoom = false;//有待商榷，既然退出失败，这个值是不是在某些情况下会保持true

                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        });
    }

    //---------------------------------------------------------------------------------------------------
    private int mRecvFpsDefeatT = 0; // 接收视频流失败次数
    private int mSendFpsDefeatT = 0; //发送视频流失败次数

    /**
     * 停止发送心跳
     */
    private void stopSendHeart()
    {
        if (mHeartBeatTask != null)
            mHeartBeatTask.cancel();
        if (null != mHearBeatTimer)
        {
            mHearBeatTimer.cancel();
            mHearBeatTimer = null;
        }
    }

    /**
     * 开启旁路直播
     */
    private void startPushAction()
    {
        final TIMAvManager.RoomInfo roomInfo = TIMAvManager.getInstance().new RoomInfo();
        TIMAvManager.StreamParam streamParam = TIMAvManager.getInstance().new StreamParam();
        streamParam.setChannelName(roomName);
        streamParam.setEncode(TIMAvManager.StreamEncode.HLS_AND_RTMP);
        roomInfo.setRelationId(Integer.parseInt(mPresenter.getmRecord().getRoomId()));
        roomInfo.setRoomId(Integer.parseInt(mPresenter.getmRecord().getRoomId()));
        TIMAvManager.getInstance().requestMultiVideoStreamerStart(roomInfo,
                streamParam, new TIMValueCallBack<TIMAvManager.StreamRes>()
                {
                    @Override
                    public void onError(int code, String desc)
                    {
                        Logger.t(TAG).d("旁路直播失败 code==" + code);
                        isPushed = false;
                    }

                    @Override
                    public void onSuccess(TIMAvManager.StreamRes streamRes)
                    {
                        isPushed = true;
                        streamChannelID = streamRes.getChnlId();
                        String m3u8 = null;
                        String flv = null;
                        String rtmp = null;
                        BigInteger unsignedNum = BigInteger.valueOf(streamChannelID);
                        if (streamChannelID < 0)
                            unsignedNum = unsignedNum.add(BigInteger.ZERO.flipBit(64));
                        for (TIMAvManager.LiveUrl liveUrl : streamRes.getUrls())
                        {
                            if (liveUrl.getEncode() == 2)
                            {
                                flv = liveUrl.getUrl();
                            }
                            else if (liveUrl.getEncode() == 1)
                            {
                                m3u8 = liveUrl.getUrl();
                            }
                            else if (liveUrl.getEncode() == 5)
                            {
                                rtmp = liveUrl.getUrl();
                            }
                        }
                        Logger.t(TAG).d("m3u8==" + m3u8 + "flv==" + flv + "rtmp==" + rtmp + "ChnlId==" + unsignedNum);
                        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
                        reqMap.put(ConstCodeTable.m3u8, m3u8);
                        reqMap.put(ConstCodeTable.flv, flv);
                        reqMap.put(ConstCodeTable.rtmp, rtmp);
                        callServerSilence(NetInterfaceConstant.LiveC_saveMultiVideo, null, "1", reqMap);
                        Map<String, String> reqMap2 = NetHelper.getCommonPartOfParam(mActivity);
                        reqMap2.put(ConstCodeTable.chnlId, String.valueOf(unsignedNum));
                        callServerSilence(NetInterfaceConstant.LiveC_saveChnlId, null, "1", reqMap2);
                    }
                });

    }

    /**
     * 停止旁路
     */
    private void stopPushAction(final ExitRoomType type)
    {
        TIMAvManager.RoomInfo roomInfo = TIMAvManager.getInstance().new RoomInfo();
        roomInfo.setRoomId(Integer.parseInt(mPresenter.getmRecord().getRoomId()));
        roomInfo.setRelationId(Integer.parseInt(mPresenter.getmRecord().getRoomId()));
        List<Long> myList = new ArrayList<Long>();
        myList.add(streamChannelID);
        TIMAvManager.getInstance().requestMultiVideoStreamerStop(roomInfo, myList, new TIMCallBack()
        {
            @Override
            public void onError(int i, String s)
            {
                Logger.t(TAG).d("停止推流失败 code=" + i + s);
                if (mPresenter != null && !mPresenter.getmRecord().getEnterRoom4EH().getIsSignedAnchor().equals("1"))
                {
                    notifyServerStopRoom(type);
                }
            }

            @Override
            public void onSuccess()
            {
                isPushed = false;
                Logger.t(TAG).d("停止推流成功 sign==" + mPresenter.getmRecord().getEnterRoom4EH().getIsSignedAnchor());
                if (mPresenter != null && !mPresenter.getmRecord().getEnterRoom4EH().getIsSignedAnchor().equals("1"))
                {
                    notifyServerStopRoom(type);
                }
            }
        });
    }

    /**
     * 开启录播
     */
    private void startRecord()
    {
        TIMAvManager.RoomInfo roomInfo = TIMAvManager.getInstance().new RoomInfo();
        TIMAvManager.RecordParam recordParam = TIMAvManager.getInstance().new RecordParam();
        recordParam.addTag("8921");
        recordParam.setFilename(mPresenter.getmRecord().getRoomId());
        roomInfo.setRelationId(Integer.parseInt(mPresenter.getmRecord().getRoomId()));
        roomInfo.setRoomId(Integer.parseInt(mPresenter.getmRecord().getRoomId()));
        TIMAvManager.getInstance().requestMultiVideoRecorderStart(roomInfo, recordParam, new TIMCallBack()
        {
            @Override
            public void onError(int i, String s)
            {
                isRecord = false;
                Logger.t(TAG).d("开启录制失败 code=" + i + s);
            }

            @Override
            public void onSuccess()
            {
                isRecord = true;
                Logger.t(TAG).d("开启录制成功");
            }
        });
    }

    /**
     * 关闭录播
     */
    private void stopRecord(final ExitRoomType type)
    {
        TIMAvManager.RoomInfo roomInfo = TIMAvManager.getInstance().new RoomInfo();
        roomInfo.setRelationId(Integer.parseInt(mPresenter.getmRecord().getRoomId()));
        roomInfo.setRoomId(Integer.parseInt(mPresenter.getmRecord().getRoomId()));
        TIMAvManager.getInstance().requestMultiVideoRecorderStop(roomInfo, new TIMValueCallBack<List<String>>()
        {
            @Override
            public void onError(int i, String s)
            {
                notifyServerStopRoom(type);
                Logger.t(TAG).d("关闭录制失败 code=" + i + s);
            }

            @Override
            public void onSuccess(List<String> files)
            {
                isRecord = false;
                vedioName = files.get(0);
                notifyServerStopRoom(type);
                Logger.t(TAG).d("关闭录制成功 files==" + files.toString());
            }
        });
    }

    /**
     * 通知后台关闭房间
     *
     * @param type 在什么情况下通知
     */
    public void notifyServerStopRoom(final ExitRoomType type)
    {
        Logger.t(TAG).d("通知后台关闭房间");
        if (type == ExitRoomType.CONFLICT)
        {
            quitRoom(ExitRoomType.NORMAL);
        }
        else
        {
            if (mPresenter != null)
                mPresenter.notifyServerCloseRoom(vedioName);
        }
    }

    @Override
    public void onServerSuccessCallback(String evt, Map<String, Object> transElement, String body)
    {
        Logger.t(TAG).d(evt + "》》》》》success");
        switch (evt)
        {
            case NetInterfaceConstant.LiveC_saveMultiVideo:
                Logger.t(TAG).d("livec_saveMultiVideo》》》》》success");
                break;
            case NetInterfaceConstant.LiveC_saveChnlId:
                Logger.t(TAG).d("livec_saveChnlId》》》》》success");
                break;
            case NetInterfaceConstant.LiveC_closeRoom:
                Logger.t(TAG).d("livec_closeRoom》》》》》success");
                if (mPresenter != null)
                {
                    if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                    {
                        quitRoom(ExitRoomType.NORMAL);
                    }
                }
                break;
            case NetInterfaceConstant.HeartC_beat:
                mPresenter.isFirstSendHeart = false;
                heatNetFailedCounter = 0;
                break;
            case NetInterfaceConstant.LiveC_exitRoom:
                Logger.t(TAG).d("通知后台腾讯观众退出");
                onMemberExitRoomSuccess((ExitRoomType) transElement.get("type"));
                break;
            default:
                break;
        }
    }

    @Override
    public void onServerFailedCallback(String evt, String errorCode, String errorBody)
    {
        switch (evt)
        {
            case NetInterfaceConstant.LiveC_closeRoom:
                Logger.t(TAG).d("livec_closeRoom》fail" + evt + " " + errorCode + " " + errorBody);
                if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                {
                    quitRoom(ExitRoomType.NORMAL);
                }
                break;
            case NetInterfaceConstant.HeartC_beat:
                if ("LIVE_DEAD".equals(errorCode) && mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST && isInAvRoom)
                {
                    EamLogger.t(TAG).writeToDefaultFile("心跳 LIVE_DEAD notifyServerStopRoom");
                    Logger.t(TAG).d("onServerFailedCallback|接口名：" + evt + "|code：" + errorCode);
                    isLiveDead = true;
                    stopSendHeart();
                    exitRoom(ExitRoomType.PASSIVE);
                }
                break;
            case NetInterfaceConstant.LiveC_sendBarrage:
                break;
            case NetInterfaceConstant.LiveC_exitRoom:
                break;
        }
    }

    @Override
    public void onNetFailedCallback(String evt)
    {
        switch (evt)
        {
            case NetInterfaceConstant.LiveC_closeRoom:
                EamLogger.t(TAG).writeToDefaultFile(String.format("关闭直播间接口由于连接或者读取超时失败了"));
                if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                {
                    quitRoom(ExitRoomType.NORMAL);
                }
                break;
            case NetInterfaceConstant.HeartC_beat:
                heatNetFailedCounter += 1;
                Logger.t(TAG).d(String.format("心跳接口由于连接或者读取超时连续 %d 次失败了 ", heatNetFailedCounter));
                EamLogger.t(TAG).writeToDefaultFile(String.format("心跳接口由于连接或者读取超时连续 %d 次失败了 ", heatNetFailedCounter));
                if (heatNetFailedCounter >= 3)
                {
                    isLiveDead = true;
                    stopSendHeart();
                    ToastUtils.showLong("由于您网络环境太差不能满足正常直播的条件，房间即将关闭，敬请谅解！");
                    EamLogger.writeToDefaultFile("Module:|由于网络问题心跳连续三次没有返回，一般情况下意味着没有网了");
                    exitRoom(ExitRoomType.PASSIVE);
                }
                break;
            case NetInterfaceConstant.LiveC_exitRoom:
                onMemberExitRoomSuccess(ExitRoomType.NORMAL);
                break;
        }
    }

    private void registerReceiver()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EamConstant.ACTION_ANSWER_PHONE);
        intentFilter.addAction(EamConstant.ACTION_HANGUP_PHONE);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);
        isRegister = true;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.t(TAG).d("接收到了广播》action：" + action);
            if (mPresenter.getmRecord().getModeOfRoom() != LiveRecord.ROOM_MODE_HOST)
                return;
            //接听电话
            if (action.equals(EamConstant.ACTION_ANSWER_PHONE))
            {
                EamLogger.t(TAG).writeToDefaultFile("接听电话触发调用---->ILVLiveManager.getInstance().onPause()");
                ILVLiveManager.getInstance().onPause();
                hostVideoStreamPause();
            }
            //挂断电话
            if (action.equals(EamConstant.ACTION_HANGUP_PHONE))
            {
                ILVLiveManager.getInstance().onResume();
                hostVideoStreamResume();
            }
        }
    };
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (isShowInviteLive)
                closeInviteLive("host", "2");
        }
    };


    private static class AppStateChange implements IOnAppStateChangeListener
    {
        private final WeakReference<LiveAct1> mActRef;

        private AppStateChange(LiveAct1 liveAct1)
        {
            mActRef = new WeakReference<>(liveAct1);
        }

        @Override
        public void switchToBack()
        {
            Logger.t(TAG).d("new switchToBack>>>>>>>>>");
            EamLogger.t(TAG).writeToDefaultFile("切到后台触发调用---->手机：" + Build.BRAND + " | ILVLiveManager.getInstance().onPause()");
            //oppo手机正常情况切后台不走这个方法， 但是在直播间中 不切后台有时也会调用这个方法，导致视频画面卡在一帧不动 故不调用
            if ("oppo".equalsIgnoreCase(Build.BRAND))
                return;
            ILVLiveManager.getInstance().onPause();
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        int code = ILiveRoomManager.getInstance().enableMic(false);
                        Logger.t(TAG).d("enableSpeaker>>>>>>>>>" + code);
                        if (code != 0)
                        {
                            ILiveRoomManager.getInstance().enableMic(false);
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d(e.getMessage());
                    }
                }
            }, 300);
            LiveAct1 liveAct1 = mActRef.get();
            liveAct1.isSwitch2Back = true;
            if (!SharePopWindow.isShared)
            {
                if (liveAct1 != null)
                {
                    if (liveAct1.mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                        liveAct1.hostVideoStreamPause();
                }
            }
        }

        @Override
        public void killedFromTask()
        {

        }
    }

    public void switch2Back()
    {
        Logger.t(TAG).d("new switchToBack>>>>>>>>>");
        EamLogger.t(TAG).writeToDefaultFile("切到后台触发调用---->手机：" + Build.BRAND + " | ILVLiveManager.getInstance().onPause()");
//        //oppo手机正常情况切后台不走这个方法， 但是在直播间中 不切后台有时也会调用这个方法，导致视频画面卡在一帧不动 故不调用
//        if ("oppo".equalsIgnoreCase(Build.BRAND))
//            return;
        ILVLiveManager.getInstance().onPause();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    int code = ILiveRoomManager.getInstance().enableMic(false);
                    Logger.t(TAG).d("enableSpeaker>>>>>>>>>" + code);
                    if (code != 0)
                    {
                        ILiveRoomManager.getInstance().enableMic(false);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }, 300);
        isSwitch2Back = true;
        if (!SharePopWindow.isShared)
        {
            if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                hostVideoStreamPause();
        }
    }
}
