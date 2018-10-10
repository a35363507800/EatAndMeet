package com.echoesnet.eatandmeet.activities.liveplay.View;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CChatActivity;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.LivePlayPre1;
import com.echoesnet.eatandmeet.activities.liveplay.managers.SwitchRoomManager;
import com.echoesnet.eatandmeet.activities.liveplay.managers.ViewShareHelper;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.CloseRoomBean;
import com.echoesnet.eatandmeet.models.bean.LAnchorsListBean;
import com.echoesnet.eatandmeet.models.datamodel.ExitRoomType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.TXInitBusinessHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.LiveEndDialog;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.logger.Logger;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.livesdk.ILVLiveManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import io.reactivex.functions.Consumer;

/**
 * 直播主界面
 * 控制房间流程
 * Created by an on 2016/10/20 0020.
 */
public class LivePlayAct1 extends LiveRoomAct1<LivePlayPre1> implements SwitchRoomManager.SwitchRoomManagerListener
{
    private static final String TAG = LivePlayAct1.class.getSimpleName();

    private LivePlayAct1 mActivity;
//    private String strIsContractHost;

    //  private ImageView ivRoomFlyPage;
    private LinearLayout llHostLeaveGroup;
    private TextView tvWaitingDesc;
    private RelativeLayout rlBody;
    //    private CoordinatorLayout clGroup;
    private CustomAlertDialog backDialog;

    private Dialog mDetailDialog;
    protected LiveEndDialog mLiveEndDialog;
    private long startTime;


    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_live_play);
        mActivity = this;
        CommonUtils.isInLiveRoom = true;
        startTime = System.currentTimeMillis();
        if (EamApplication.getInstance().controlChat.get(CChatActivity.class.getSimpleName()) != null)
            EamApplication.getInstance().controlChat.put(TAG, this);
        if (EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()) != null)
            EamApplication.getInstance().controlUInfo.put(TAG, this);
        llHostLeaveGroup = (LinearLayout) findViewById(R.id.llHostLeaveGroup);
        tvWaitingDesc = (TextView) findViewById(R.id.tvWaitingDesc);
        rlBody = (RelativeLayout) findViewById(R.id.rlBody);
        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_MEMBER)
            mLiveEndDialog = new LiveEndDialog(mActivity, mPresenter);
//        clGroup = (CoordinatorLayout) findViewById(R.id.clGroup);

        //      ivRoomFlyPage = (ImageView) findViewById(R.id.ivRoomFlyPage);
//        ImageUtils.showLoadingCover(mActivity, EamApplication.getInstance().livePage.get(mPresenter.getmRecord().getRoomId()), ivRoomFlyPage);
//        StatService.onEvent(mActivity, "live_play", getString(R.string.baidu_live), 1);
        setHostLeaveDialogVisibility(false);
        //没有回调 从demo中粘贴
        checkPermission4LivePlay();
        initBackDialog();
        //NetHelper.checkBigGiftExist(mActivity);

        Logger.t(TAG).d("LiveAonCreate", TAG + " onCreate", "PID-> " + Process.myPid() + " |UID-> " + Process.myUid() + " |TID-> " + Process.myTid());
        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
        {
            VerticalViewPager verticalViewPager = (VerticalViewPager) mActivity.findViewById(R.id.vvpSwitchRoom);
            verticalViewPager.setVisibility(View.GONE);
        }
        else
        {
//            switchRoomManager = new SwitchRoomManager(mActivity, R.id.vvpSwitchRoom, rlRoomLayer, mFollowView, mChatRoomListView, strRoomid, lAnchorsList);
//            switchRoomManager.setShareListener(this);
//            VerticalViewPager verticalViewPager = (VerticalViewPager) mActivity.findViewById(R.id.vvpSwitchRoom);
//            verticalViewPager.setVisibility(View.VISIBLE);
        }

        RxView.clicks(itCloseLive)
                .throttleFirst(1300, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        //关闭房间
                        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                        {
                            showHostConfirmDialog();
                        }
                        else
                        {
                            if (isShowInviteLive)
                            {
                                int i = NetHelper.getNetworkStatus(mActivity);
                                if (i == -1)
                                {
                                    exitRoom(ExitRoomType.NORMAL);
                                }
                                else
                                {
                                    showLiveConnectTip();
                                }
                            }
                            else
                            {
                                exitRoom(ExitRoomType.NORMAL);
                            }
                        }
                    }
                });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        EamApplication.getInstance().hasCallServerStartLived = false;
        Logger.t("livefinish").d(">>>>>>>>>>>>>");
        super.onDestroy();
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack()
        {

            @Override
            public void onSuccess(Object data)
            {
                Logger.t(TAG).d("chat------>BaseActivity LivePlayAct quitRoomSuccess ");
            }

            @Override
            public void onError(String module, int errCode, String errMsg)
            {
                Logger.t(TAG).d("chat------>BaseActivity LivePlayAct quitRoomSuccess ");
            }
        });
    }

    @Override
    protected void onHostExitRoomSuccess(ExitRoomType type)
    {
        super.onHostExitRoomSuccess(type);
        if (mPresenter != null)
        {
            mPresenter.destroyBeautyData();
        }
        //主播关闭房间 刷新个人信息
        Intent intent = new Intent(EamConstant.ACTION_UPDATE_USER_INFO);
        intent.putExtra("needRefreshUserInfo", true);
        sendBroadcast(intent);
        if (type == ExitRoomType.CONFLICT)
        {
            finish();
        }
        else
        {
            showLiveEndDetailDialog(null, "");
        }
    }

    @Override
    public void finish()
    {
        CommonUtils.speakSwitch = "able";
        CommonUtils.isInLiveRoom = false;
        CommonUtils.jumpHelperId = "-1";
        super.finish();
    }

    /**
     * 主播退出直播dialog
     */
    private void initBackDialog()
    {
        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_MEMBER)
            return;
        backDialog = ViewShareHelper.getInstance(mActivity, mPresenter).getBackDialog(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isShowInviteLive)
                {
                    showLiveConnectTip();
                }
                else
                {
                    exitRoom(ExitRoomType.NORMAL);
                }
                backDialog.dismiss();
            }
        });
    }


    /**
     * 主播退出主播显示的房间详情
     *
     * @param sec  End 结束时间
     * @param meal 本场获得饭票
     */
    private void showLiveEndDetailDialog(String sec, String meal)
    {
        if (mActivity == null)
            return;

        ViewShareHelper.getInstance(mActivity, mPresenter).showLiveEndDetailDialog(getMealTicket(), startTime, sec, meal);
    }


    //没有回调 从demo中粘贴
    private final int REQUEST_PHONE_PERMISSIONS = 0;

    void checkPermission4LivePlay()
    {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if ((checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WAKE_LOCK);
            if ((checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            if (permissionsList.size() != 0)
            {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }
    }
//    AV_ERR_FAILED                          |1      |一般错误         |具体原因需要通过分析日志等来定位
//    AV_ERR_REPEATED_OPERATION              |1001   |重复操作         |已经在进行某种操作，再次去做同样的操作
//    AV_ERR_EXCLUSIVE_OPERATION             |1002   |互斥操作         |上次相关操作尚未完成
//    AV_ERR_HAS_IN_THE_STATE                |1003   |状态已就绪       |对象已经处于将要进入的某种状态
//    AV_ERR_INVALID_ARGUMENT                |1004   |错误参数         |传入错误的参数
//    AV_ERR_TIMEOUT                         |1005   |超时             |在规定的时间内，还未返回操作结果
//    AV_ERR_NOT_IMPLEMENTED                 |1006   |未实现           |相应的功能还未支持
//    AV_ERR_NOT_IN_MAIN_THREAD              |1007   |不在主线程       |SDK对外接口要求在主线程执行
//    AV_ERR_RESOURCE_IS_OCCUPIED            |1008   |资源被占用       |需要用到某种资源被占用了
//    AV_ERR_CONTEXT_NOT_EXIST               |1101   |状态未就绪       |AVContext非CONTEXT_STATE_STARTED状态
//    AV_ERR_CONTEXT_NOT_STOPPED             |1102   |状态未就绪       |AVContext非CONTEXT_STATE_STOPPED状态
//    AV_ERR_ROOM_NOT_EXIST                  |1201   |状态未就绪       |AVRoom非ROOM_STATE_ENTERED状态
//    AV_ERR_ROOM_NOT_EXITED                 |1202   |状态未就绪       |AVRoom非ROOM_STATE_EXITED状态
//    AV_ERR_DEVICE_NOT_EXIST                |1301   |设备不存在       |设备不存在或者设备初始化未完成
//    AV_ERR_ENDPOINT_NOT_EXIST              |1401   |对象不存在       |成员未发语音或视频时去获取AVEndpoint
//    AV_ERR_ENDPOINT_HAS_NOT_VIDEO          |1402   |没有发视频       |成员未发视频时去做需要发视频的相关操作
//    AV_ERR_TINYID_TO_OPENID_FAILED         |1501   |转换失败         |信令解析出错
//    AV_ERR_OPENID_TO_TINYID_FAILED         |1502   |转换失败         |初始化转换失败
//    AV_ERR_DEVICE_TEST_NOT_EXIST           |1601   |状态未就绪       |AVDeviceTest对象状态异常(windows特有)
//    AV_ERR_DEVICE_TEST_NOT_STOPPED         |1602   |状态未就绪       |AVDeviceTest对象状态异常（windows特有）
//    AV_ERR_INVITE_FAILED                   |1801   |发送失败         |发送邀请时产生的失败
//    AV_ERR_ACCEPT_FAILED                   |1802   |接受失败         |接受邀请时产生的失败
//    AV_ERR_REFUSE_FAILED                   |1803   |拒绝失败         |拒绝邀请时产生的失败
//    AV_ERR_SERVER_FAILED                   |10001  |一般错误         |具体原因需要通过分析日志确认
//    AV_ERR_SERVER_INVALID_ARGUMENT         |10002  |错误参数         |错误的参数
//    AV_ERR_SERVER_NO_PERMISSION            |10003  |没有权限         |没有权限使用某个功能
//    AV_ERR_SERVER_TIMEOUT                  |10004  |超时             |具体原因需要通过分析日志确认
//    AV_ERR_SERVER_ALLOC_RESOURCE_FAILED    |10005  |资源不够         |分配更多的资源(如内存)失败了
//    AV_ERR_SERVER_ID_NOT_IN_ROOM           |10006  |不在房间         |在不在房间内时，去执行某些操作
//    AV_ERR_SERVER_NOT_IMPLEMENT            |10007  |未实现           |调用SDK接口时，如果相应的功能还未支持
//    AV_ERR_SERVER_REPEATED_OPERATION       |10008  |重复操作         |具体原因需要通过分析日志确认
//    AV_ERR_SERVER_ROOM_NOT_EXIST           |10009  |房间不存在       |房间不存在时，去执行某些操作
//    AV_ERR_SERVER_ENDPOINT_NOT_EXIST       |10010  |成员不存在       |某个成员不存在时，去执行该成员相关的操作
//    AV_ERR_SERVER_INVALID_ABILITY          |10011  |错误能力         |具体原因需要通过分析日志确认


//---------------------------------------------------------------------------------------------------------

    private LAnchorsListBean nextTaskGotoRoom;

    @Override
    public void onRoomSelected(LAnchorsListBean gotoRoomEH)
    {
        if (!isInAvRoom)
        {
            //并没有稳定在一个房间。
            // cache Task for next time working;
            nextTaskGotoRoom = gotoRoomEH;
        }
        else
        {
            //already in The av ROOM;
            //save roomID
            mPresenter.getmRecord().setRoomId(gotoRoomEH.getRoomId());
            roomName = gotoRoomEH.getRoomName();
            sign = gotoRoomEH.getSign();
            flyPage = gotoRoomEH.getRoomUrl();


            getIntent().putExtra("roomName", roomName);
            getIntent().putExtra("sign", sign);
            getIntent().putExtra("flyPage", flyPage);

            mPresenter.whenSwitchRoomToUpdate();


            ImageUtils.newShowLoadingCover(mPresenter.getmRecord().getEnterRoom4EH().getAnph(), ivRoomFlyPage);
            isInAvRoom = false;
            switchRoom();
            cleanRoomUI();
        }
    }


    @Override
    protected void onSwitchRoomError(String module, int errCode, String errMsg)
    {
        exitRoom(ExitRoomType.NORMAL);
    }

    @Override
    protected void onSwitchRoomSuccess()
    {
        if (null != nextTaskGotoRoom)
        {
            if (!mPresenter.getmRecord().getRoomId().equals(nextTaskGotoRoom.getRoomId()))
            {
                onRoomSelected(nextTaskGotoRoom);
                return;
            }
        }
        refreshRoomUI();
        ImageUtils.fadeOut(ivRoomFlyPage, 500);
    }

    @Override
    public void onPendingCreateRoom()
    {
        //如果没有调用startlive 直接结束
        if (!EamApplication.getInstance().hasCallServerStartLived)
        {
            showLiveEndDetailDialog(null, "");
            return;
        }
        super.onPendingCreateRoom();
    }


    // AVAct
    @Override
    protected void onJoinRoomSuccess()
    {
        super.onJoinRoomSuccess();
        try
        {
            intoRoomSuc();
            if ("1".equals(mPresenter.getmRecord().getEnterRoom4EH().getNotification()))
            {
                mPresenter.enteringRoomSetAdminDialog(mPresenter.getmRecord().getEnterRoom4EH().getUser(), mPresenter.getmRecord().getRoomId());
            }
            else if ("2".equals(mPresenter.getmRecord().getEnterRoom4EH().getNotification()))
            {
                mPresenter.enteringRoomCancelAdminDialog(mPresenter.getmRecord().getEnterRoom4EH().getUser(), mPresenter.getmRecord().getRoomId());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    @Override
    protected void onCreateRoomSuccess()
    {
        super.onCreateRoomSuccess();
        mPresenter.setBeautyData(3, 3);
        mPresenter.setBeautyCameraCallback();
        EamApplication.getInstance().hasCallServerStartLived = false;
        livHouseManager.setVisibility(View.VISIBLE);
        intoRoomSuc();
    }

    /**
     * 主播 用户 进入直播成功调用
     */
    private void intoRoomSuc()
    {
        startTime = System.currentTimeMillis();
        setHostLeaveDialogVisibility(false);
//        ImageUtils.fadeOut(ivRoomFlyPage, 500);
        if (handler != null)
            handler.postDelayed(refreshAVRootViewRunnable, 1000);
//        mPresenter.sendOnlineC2CCmd(TXConstants.AVIMCMD_MUlTI_HOST_INVITE, "", "u100016", null);
//        mPresenter.sendC2CCmd(TXConstants.AVIMCMD_MUlTI_HOST_INVITE, "", "u100016");
        if (mPresenter != null)
            mPresenter.getGroupDetailInfo(mPresenter.getmRecord().getRoomId());
//      监控帧率 流 解决 主播&观众 断网退出状态；
//        startTaskForLiveStatus();
    }

    int refreshCount = 0;
    Runnable refreshAVRootViewRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (refreshCount < 5)
            {
                refreshCount++;
                avRootView.onResume();
                handler.postDelayed(refreshAVRootViewRunnable, 1500);
            }
            else
            {
                handler.removeCallbacks(refreshAVRootViewRunnable);
                refreshCount = 0;
            }
        }
    };

    @Override
    protected void onJoinRoomError(String module, int errCode, String errMsg)
    {
        ImageUtils.fadeOut(ivRoomFlyPage, 500);
        Logger.t(TAG).d("errCode>>" + errCode + ">>errMsg>>" + errMsg);
        super.onJoinRoomError(module, errCode, errMsg);
        switch (errCode)
        {

//            case 10008://|10008  |重复操作         |具体原因需要通过分析日志确认
            case 10004://|10004  |超时             |具体原因需要通过分析日志确认
                if (mActivity != null)
                    finish();
//                ILiveSDK.getInstance().initSdk(EamApplication.getInstance(), Constants.SDK_APPID, Constants.ACCOUNT_TYPE);
//                reJoinRoom();
                break;
            case 10009:
            case 10010:
                roomEventsHostLeave("erroCode", "错误码》" + errCode);
                break;
            case 6014:
                //current user not login
                TencentHelper.txLogin(null);
                break;
            case 6012://errCode>>6012>>errMsg>>wait serverResp timeout  |
                ToastUtils.showShort("直播服务器响应超时，请检查网络后重试！");
                if (mActivity != null)
                    finish();
                break;
            case 6013:
                // errCode>>6013>>errMsg>>sdk not initialized or not logged in.
                TXInitBusinessHelper.initApp(EamApplication.getInstance());
                TencentHelper.txLogin(null);
                break;
            case 1001: //|重复操作         |已经在进行某种操作，再次去做同样的操作
//                ToastUtils.showLong("为了更好的观看直播,小饭需要重新启动下,~~~~(>_<)~~~~");
//                finishAffinity();
                ToastUtils.showShort("小饭生病了，切到后台杀一次进程会帮到您。");
                break;
            case 1003: //状态就绪，对象已经处于将要进入的某种状态
                onJoinRoomSuccess();
                break;
            case 0:
            {
                // errCode>>6013>>errMsg>>sdk not initialized or not logged in.
                TXInitBusinessHelper.initApp(EamApplication.getInstance());
                TencentHelper.txLogin(new TencentHelper.TXLoginFinishListener()
                {
                    @Override
                    public void onSuccess(Object o)
                    {
                        joinRoom();
                    }

                    @Override
                    public void onDefeat(int o, String msg)
                    {

                    }
                });
                break;
            }

        }
    }

    @Override
    protected void onCreateRoomError(String module, int errCode, String errMsg)
    {
        ImageUtils.fadeOut(ivRoomFlyPage, 500);
        super.onCreateRoomError(module, errCode, errMsg);
        Logger.t(TAG).d("errCode>>" + errCode + ">>errMsg>>" + errMsg);
        switch (errCode)
        {
            case 1001: //|重复操作         |已经在进行某种操作，再次去做同样的操作
                ToastUtils.showShort("小饭生病了，切到后台杀一次进程会帮到您。");
                break;
//            case 10008://|10008  |重复操作         |具体原因需要通过分析日志确认
            case 10004://|10004  |超时             |具体原因需要通过分析日志确认
                if (mActivity != null)
                    finish();
//                ILiveSDK.getInstance().initSdk(EamApplication.getInstance(), Constants.SDK_APPID, Constants.ACCOUNT_TYPE);
//                reJoinRoom();
                break;
            case 10009:
            case 10010:
                break;
            case 6014:
                //current user not login
                TencentHelper.txLogin(null);
                break;
            case 6013:
                // errCode>>6013>>errMsg>>sdk not initialized or not logged in.
                TXInitBusinessHelper.initApp(EamApplication.getInstance());
                TencentHelper.txLogin(null);
                break;
            case 0:
            {
                // errCode>>6013>>errMsg>>sdk not initialized or not logged in.
                TXInitBusinessHelper.initApp(EamApplication.getInstance());
                TencentHelper.txLogin(new TencentHelper.TXLoginFinishListener()
                {
                    @Override
                    public void onSuccess(Object o)
                    {
                        createRoom();
                    }

                    @Override
                    public void onDefeat(int errCode, String errMsg)
                    {
                        Logger.t(TAG).d("腾讯登录失败》" + errCode + "描述》" + errMsg);
                    }
                });
                break;
            }
        }
    }

    /**
     * 显示主播正在回来的路上
     *
     * @param groupIntroduction 是否显示
     */
    public void showHostIsLeave(String groupIntroduction)
    {
        if ("1".equals(groupIntroduction))
        {
            setHostLeaveDialogVisibility(true);
            mActivity.runOnUiThread(() -> ImageUtils.fadeOut(ivRoomFlyPage, 500));
        }

        else
            setHostLeaveDialogVisibility(false);
    }

    @Override
    public void memberVideoStreamResume()
    {
        super.memberVideoStreamResume();
        showHostIsLeave("0");
    }

    @Override
    public void memberVideoStreamPause()
    {
        super.memberVideoStreamPause();
        Logger.t(TAG).d("调用memberVideoStreamPause");
        showHostIsLeave("1");
    }

    /**
     * 设置是否显示主播正在回来的界面
     *
     * @param isVisible
     */
    public void setHostLeaveDialogVisibility(boolean isVisible)
    {
        if (mPresenter != null && mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
        {
            llHostLeaveGroup.setVisibility(View.GONE);
        }
        else
        {
            if (isVisible)
            {
                llHostLeaveGroup.setVisibility(View.VISIBLE);
            }
            else
            {
                llHostLeaveGroup.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onRoomDisconnected(int errCode, String errMsg)
    {
        if (mActivity != null && !mActivity.isFinishing())
        {
            if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_MEMBER)
                SharePreUtils.setPreGroupId(mActivity, mPresenter.getmRecord().getRoomId());
            new CustomAlertDialog(mActivity)
                    .builder()
                    .setCancelable(false)
                    .setMsg("您已经进入了没有网络的二次元世界，直播结束")
                    .setPositiveButton("确定", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                            {
                                //exitRoom(ExitRoomType.NORMAL);//当无网络时候修改为走退出流程(反应时间太长)--wb
                                showLiveEndDetailDialog(null, "");
                            }
                            else
                            {
                                finish();
                                //exitRoom(ExitRoomType.NORMAL);
                            }
                        }
                    }).show();
        }
    }

    @Override
    protected void onMemberExitRoomSuccess(ExitRoomType type)
    {
        super.onMemberExitRoomSuccess(type);
        //观众退出房间 刷新个人信息
        Intent intent = new Intent(EamConstant.ACTION_UPDATE_USER_INFO);
        intent.putExtra("needRefreshUserInfo", true);
        sendBroadcast(intent);
        if (type == ExitRoomType.PASSIVE)
        {
            Logger.t(TAG).d("VIEW   不为空");
            InputMethodManager imm = (InputMethodManager) getSystemService(mActivity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(llHostLeaveGroup.getWindowToken(), 0);
        }
        else if (type == ExitRoomType.NORMAL | type == ExitRoomType.CONFLICT)
        {
            finish();
            overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
        }
    }

    @Override
    protected void onMemberExitRoomError(String module, int errCode, String errMsg, ExitRoomType type)
    {
        super.onMemberExitRoomError(module, errCode, errMsg, type);
        //主动退出
        if (type == ExitRoomType.NORMAL)
        {
            finish();
            overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
        }
    }

    @Override
    public void onBackGroundIdChanged(String backGroundId)
    {
        //
//        switch (modeOfRoom){
//            case LivePlayAct1.ROOM_MODE_HOST:
//                if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//背景是自己
//                    mHostCtrView.setVisibility(View.VISIBLE);
//                    mVideoMemberCtrlView.setVisibility(View.INVISIBLE);
//                } else {//背景是其他成员
//                    mHostCtrView.setVisibility(View.INVISIBLE);
//                    mVideoMemberCtrlView.setVisibility(View.VISIBLE);
//                }
//                break;
//            case LivePlayAct1.ROOM_MODE_MEMBER:
//                if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//背景是自己
//                    mVideoMemberCtrlView.setVisibility(View.VISIBLE);
//                    mNomalMemberCtrView.setVisibility(View.INVISIBLE);
//                } else if (backGroundId.equals(CurLiveInfo.getHostID())) {//主播自己
//                    mVideoMemberCtrlView.setVisibility(View.INVISIBLE);
//                    mNomalMemberCtrView.setVisibility(View.VISIBLE);
//                } else {
//                    mVideoMemberCtrlView.setVisibility(View.INVISIBLE);
//                    mNomalMemberCtrView.setVisibility(View.INVISIBLE);
//                }
//                break;
//        }
    }

    @Override
    public void onServerSuccessCallback(String evt, Map<String, Object> transElement, String body)
    {
        Logger.t(TAG).d(">>>>>>>>>>>" + body);
        switch (evt)
        {
            case NetInterfaceConstant.LiveC_closeRoom:
                CloseRoomBean cCloseRoom4EH = new Gson().fromJson(body, CloseRoomBean.class);
                if (cCloseRoom4EH==null)
                {
                    cCloseRoom4EH=new CloseRoomBean();
                }
                Logger.t(TAG).d("LiveC_closeRoom返回>>>>>>>>>>>>>>>>>>>" + cCloseRoom4EH.getMeal());
                showLiveEndDetailDialog(cCloseRoom4EH.getSec(), cCloseRoom4EH.getMeal());
                break;
            case NetInterfaceConstant.LiveC_focus:
                mPresenter.getmRecord().getEnterRoom4EH().setFlag("1");
                mLiveEndDialog.refreshFocusStatus();
                break;
            default:
                break;
        }
        super.onServerSuccessCallback(evt, transElement, body);
    }

    @Override
    public void onServerFailedCallback(String interfaceName, String errorCode, String errorBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_closeRoom:
                showLiveEndDetailDialog(null, "");
                break;
        }
        super.onServerFailedCallback(interfaceName, errorCode, errorBody);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (ViewShareHelper.getInstance(mActivity, mPresenter).isGameStart())
            return;
        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
        {
            backDialog.show();
        }
        else
        {
            if (isShowInviteLive)
            {
                int i = NetHelper.getNetworkStatus(mActivity);
                if (i == -1)
                {
                    exitRoom(ExitRoomType.NORMAL);
                }
                else
                {
                    showLiveConnectTip();
                }
            }
            else
            {
                exitRoom(ExitRoomType.NORMAL);
            }
        }
    }
    //room Act

    @Override
    public void roomEventsHostLeave(String invoker, String reason)
    {
        super.roomEventsHostLeave(invoker, reason);
        if (mPresenter == null)
        {
            EamLogger.t("直播").writeToDefaultFile("mPresenter为null,退出操作失败");
            return;
        }
        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_MEMBER)
        {
            EamLogger.t("直播").writeToDefaultFile("非主播直播间关闭开始..." + "关闭原因为：" + reason);
            exitRoom(ExitRoomType.PASSIVE);
            if (!ViewShareHelper.getInstance(mActivity, mPresenter).isGameStart())
                mLiveEndDialog.show();
            else
            {
                ViewShareHelper.getInstance(mActivity, mPresenter).setGameExitListener(() ->
                {
                    if (mLiveEndDialog != null && !mLiveEndDialog.isShowing())
                        mLiveEndDialog.show();
                });
            }
        }
        else if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
        {
            EamLogger.t("直播").writeToDefaultFile("主播直播间关闭开始..." + "关闭原因为：" + reason);
            if (!TextUtils.isEmpty(reason))
                ToastUtils.showShort(reason);
            exitRoom(ExitRoomType.NORMAL);
        }
    }

    public void showHostConfirmDialog()
    {
        if (backDialog != null && !backDialog.isShowing())
        {
            backDialog.show();
        }
    }

    /**
     * 连麦中提示
     */
    private void showLiveConnectTip()
    {
        new CustomAlertDialog(mActivity).builder()
                .setTitle("连麦提示")
                .setMsg("您正在连麦中，请取消连麦后退出")
                .setPositiveButton("确定", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                    }
                })
                .setCancelable(true)
                .show();
    }


    //region 组员对Toast的研究心得，暂时保留以备查阅 （无需在非UI线程上显示Toast，toast本来就是作为UI来处理的） --wb
    //================================================================================
//    通过分析TN类的handler可以发现，如果想在非UI线程使用Toast需要自行声明Looper，否则运行会抛出Looper相关的异常；UI线程不需要，因为系统已经帮忙声明。


//    在使用Toast时context参数尽量使用getApplicationContext()，可以有效的防止静态引用导致的内存泄漏。 因为首先toast构造函数中拿到了toast，
// 所以如果在当前activity中弹出一个toast，然后finish掉该toast，toast并不依赖activity，是系统级的窗口，当然也不会随着activity的finish就消失，只是随着设置时间的到来而消失，
// 所以如果此时设置toast显示的时间足够长，那么因为toast持有该activity的引用，那么该activty就一直不能被回收，一直到toast消失，造成内存泄漏，所以最好使用getApplicationContext()

//    有时候我们会发现Toast弹出过多就会延迟显示，因为上面源码分析可以看见Toast.makeText是一个静态工厂方法，每次调用这个方法都会产生一个新的Toast对象，
// 当我们在这个新new的对象上调用show方法就会使这个对象加入到NotificationManagerService管理的mToastQueue消息显示队列里排队等候显示；
// 所以如果我们不每次都产生一个新的Toast对象（使用单例来处理）就不需要排队，也就能及时更新了。

    //    Toast的显示交由远程的NotificationManagerService管理是因为Toast是每个应用程序都会弹出的，而且位置和UI风格都差不多，
// 所以如果我们不统一管理就会出现覆盖叠加现象，同时导致不好控制，所以Google把Toast设计成为了系统级的窗口类型，由NotificationManagerService统一队列管理。
//================================================================================
    //endregion
}
