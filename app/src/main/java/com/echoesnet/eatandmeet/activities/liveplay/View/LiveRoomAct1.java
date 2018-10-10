package com.echoesnet.eatandmeet.activities.liveplay.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.LiveHouseManageAct;
import com.echoesnet.eatandmeet.activities.LiveSendPacketAct;
import com.echoesnet.eatandmeet.activities.MyDateAct;
import com.echoesnet.eatandmeet.activities.ReportFoulsRoomAct;
import com.echoesnet.eatandmeet.activities.live.LRankingAct;
import com.echoesnet.eatandmeet.activities.live.MyInfoBuyFaceEggActivity;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.LiveRoomPre1;
import com.echoesnet.eatandmeet.activities.liveplay.managers.GiftPopWinManager;
import com.echoesnet.eatandmeet.activities.liveplay.managers.LargeGiftManager;
import com.echoesnet.eatandmeet.activities.liveplay.managers.SlidGift;
import com.echoesnet.eatandmeet.activities.liveplay.managers.SlidGiftManager;
import com.echoesnet.eatandmeet.activities.liveplay.managers.ViewShareHelper;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ChosenAdminBean;
import com.echoesnet.eatandmeet.models.bean.ChosenFansBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.bean.GameInviteBean;
import com.echoesnet.eatandmeet.models.bean.GiftBean;
import com.echoesnet.eatandmeet.models.bean.GiftMsgBean;
import com.echoesnet.eatandmeet.models.bean.LChoseConnectMemberBean;
import com.echoesnet.eatandmeet.models.bean.LGiftListBean;
import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;
import com.echoesnet.eatandmeet.models.bean.LiveGetRedPacketBean;
import com.echoesnet.eatandmeet.models.bean.LiveRoomMemberBean;
import com.echoesnet.eatandmeet.models.bean.LiveSendGiftBean;
import com.echoesnet.eatandmeet.models.bean.LookAnchorBean;
import com.echoesnet.eatandmeet.models.bean.RefreshLiveMsgBean;
import com.echoesnet.eatandmeet.models.bean.StartGameBean;
import com.echoesnet.eatandmeet.models.bean.UserC_NewBalance4EH;
import com.echoesnet.eatandmeet.models.datamodel.AbstractEMMessageListener;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.models.datamodel.ExitRoomType;
import com.echoesnet.eatandmeet.models.datamodel.LiveMsgType;
import com.echoesnet.eatandmeet.receivers.NetworkChangeReceiver;
import com.echoesnet.eatandmeet.utils.BigGiftUtil.BigGiftUtil;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TXConstants;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.LiveRedPacketDialog;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.LruCacheBitmapLoader;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.LiveNewMsgDialog;
import com.echoesnet.eatandmeet.views.ObtainStarDialog;
import com.echoesnet.eatandmeet.views.widgets.ConnectPopup;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.HeartLikeSurfaceView;
import com.echoesnet.eatandmeet.views.widgets.InputTextMsgDialog;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.LiveHostInfoPop;
import com.echoesnet.eatandmeet.views.widgets.LiveMsgDialog.LiveChatDialog;
import com.echoesnet.eatandmeet.views.widgets.StarPopWindow;
import com.echoesnet.eatandmeet.views.widgets.SwipeMoveRelativeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.livesdk.ILVChangeRoleRes;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

/**
 * Created by TDJ on 2016/7/6.
 * 房间业务展示层的操作
 */
public class LiveRoomAct1<P extends LiveRoomPre1> extends LiveAct1<P>
{
    //region 变量
    private static final String TAG = LiveRoomAct1.class.getSimpleName();
    private static final int GIFT_SIZE_DP = 50;
    @BindView(R.id.btn_refuse)
    Button btnRefuse;
    @BindView(R.id.btn_accept)
    Button btnAccept;
    @BindView(R.id.liv_house_manager)
    TextView livHouseManager;
    @BindView(R.id.llMealTicketGroup)
    LinearLayout llMealTicketGroup;
    @BindView(R.id.slBody)
    SwipeMoveRelativeLayout slBody;
    @BindView(R.id.rlPopCover)
    RelativeLayout rlPopCover;
    @BindView(R.id.rivHostAvatar)
    LevelHeaderView rivHostAvatar;
    @BindView(R.id.tvRoomName)
    TextView tvRoomName;
    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.tvMealTicketCount)
    TextView tvMealTicketCount;
    @BindView(R.id.tvId)
    TextView tvId;
    @BindView(R.id.invite_view1)
    ImageView inviteView1;
    @BindView(R.id.invite_view2)
    TextView inviteView2;
    @BindView(R.id.invite_view3)
    TextView inviteView3;
    @BindView(R.id.btnCleared_clear)
    Button ibClearedClear;
    @BindView(R.id.fl_booty_call)
    FrameLayout flBootyCall;
    @BindView(R.id.img_booty_call)
    ImageView bootyCallImg;
    @BindView(R.id.control_list_view)
    View controlListView;
    @BindView(R.id.rlRoomLayer)
    RelativeLayout rlRoomLayer;
    //连麦邀请窗口
    private ConnectPopup connectPopup;
    @BindView(R.id.connect_views)
    LinearLayout connectViews;
    @BindView(R.id.level_invite_header_view)
    LevelHeaderView lvHeaderImage;
    @BindView(R.id.invite_level_view)
    LevelView lvView;
    @BindView(R.id.tv_nick_name)
    TextView tvNicName;
    @BindView(R.id.tv_connect_age)
    GenderView ivtConnectAge;
    //星光榜单
    @BindView(R.id.ll_star_chart)
    LinearLayout llStarChart;
    @BindView(R.id.tv_star_chart_count)
    TextView tvStarChartCount;
    @BindView(R.id.tv_star_chart_top)
    TextView tvStarChartTop;
    @BindView(R.id.tv_star_show)
    TextView tvStarShow;
    @BindView(R.id.tvBigGiftCount)
    TextView tvBigGiftCount;
    @BindView(R.id.refuseDownloadText)
    TextView refuseDownloadText;
    @BindView(R.id.llBigGiftGroup)
    LinearLayout llBigGiftGroup;


    private StarPopWindow starPopWindow;
    //control bar
    @BindView(R.id.room_controller)
    RelativeLayout mControll; //control bar
    private PopupWindow mPopupWindow;

    //message list
    protected ListView mChatRoomListView;
    //    @BindView(R.id.ibCloseLive)
//    ImageButton ibCloseLive;
    @BindView(R.id.itCloseLive)
    IconTextView itCloseLive;
    //private RoomActChatMsgListAdapter chatMsgListAdapter;
    //private LinearLayoutManager mChatRoomLinearLayoutManager;

    //member list
    @BindView(R.id.rvAudienceAvatar)
    RecyclerView rvAudienceAvatar;
    /*  private RoomActAudienceListAdapter audienceAdapter;
      private LinearLayoutManager mAudienceLinearLayoutManager;*/
    @BindView(R.id.tvAudiencesCount)
    TextView tvAudiencesCount;
    //flow heart
    @BindView(R.id.heart_layout)
    HeartLikeSurfaceView heartLikeSurfaceView;
    private int drawableIds[];
    @BindView(R.id.dialog_send_layout)
    View mSendLayout;
    @BindView(R.id.ll_beauty)
    LinearLayout llBeauty;
    @BindView(R.id.view_game)
    View viewGame;
    @BindView(R.id.img_game)
    ImageView imgGame;

    @BindView(R.id.lav_star)
    LottieAnimationView lavStar;

    private boolean isPraise = false;
    private boolean isBootyCallMsgShowing = false;

    private InputTextMsgDialog inputTextMsgDialog;
    private LiveHostInfoPop mLiveHostInfoPop;//主播头像弹窗


    private List<GiftBean> arrGift4EH;
    //旋转摄像头，开关mic时使用的。。逻辑。。没太搞懂。
    private boolean bCameraOn = false;

    protected Map<String, String> switchStatusMap = new HashMap<>();
    //endregion
    private LiveAct1 mActivity;
    private SlidGiftManager giftManager; //小礼物管理器
    private LargeGiftManager giftLargeManager; //大礼物管理器

    private GiftPopWinManager giftPopWinManager; //礼物弹窗管理器
    private ImageView btMessageDian;
    private Button btnInviteTime; //连麦邀请计时
    private Button btnCloseConnectLive; //关闭连麦
    private Button hostSendMsg;
    private ImageView btnBeauty;
    private int inviteCountdownTime = 30;
    private LChoseConnectMemberBean id4ContactMemberBean;
    private List<Integer> drawableImage;

//    private FightingManager f_manager;      //漂浮对话管理器(从别处拷贝过来的，预留方法)
//    private UioManager uio_manager;         //漂浮对话管理器(从别处拷贝过来的，预留方法)


    //新消息窗口
    private LiveNewMsgDialog liveNewMsgDialog;
    public List<String> mRenderUserList = new ArrayList<>();
    public List<String> mWillUserList = new ArrayList<>();//将要上麦用户
    protected LiveEnterRoomBean enterRoom4EH;
    private String starChartNum;
    private ObtainStarDialog dialog;
    private String oldRanking = "";


    //region ---------------生命周期----------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_room);
        ivRoomFlyPage = new ImageView(this);
        ivRoomFlyPage.setVisibility(View.GONE);
        ivRoomFlyPage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        ivRoomFlyPage.setClickable(true);
        ImageUtils.newShowLoadingCover(mPresenter.getmRecord().getEnterRoom4EH().getAnph(), ivRoomFlyPage);
        setContentView(ivRoomFlyPage);
        ButterKnife.bind(this);
        mActivity = this;
        ViewShareHelper.destroyInstance();

        //room info
//        mInput = (EditText) findViewById(R.id.dialog_input);
//        mSendBtn = (Button) findViewById(R.id.dialog_send);
        addMsgListener();
        mPresenter.pullAudiences(0, new HashMap<String, Object>());
        ViewShareHelper.getInstance(mActivity, mPresenter).getTvAudiencesCount(tvAudiencesCount);
        ViewShareHelper.getInstance(mActivity, mPresenter).setupAudienceListView(mActivity, rvAudienceAvatar, "tx");
        //初始化Chat message list  //设置布局管理器
        mChatRoomListView = (ListView) findViewById(R.id.room_listview);
        ViewShareHelper.getInstance(mActivity, mPresenter).setupChatListView(mActivity, mChatRoomListView);
        View control = getControlViewLayout();
        if (control != null)
        {
            btMessageDian = (ImageView) control.findViewById(R.id.btn_message_dian);
            btnInviteTime = (Button) control.findViewById(R.id.btn_invite_time);
            btnCloseConnectLive = (Button) control.findViewById(R.id.btnCloseConnectLive);
            hostSendMsg = (Button) control.findViewById(R.id.btnSendMessage);
            btnBeauty = (ImageView) control.findViewById(R.id.btn_beauty);
            mControll.addView(control);
        }
        //布局添加空间需要重新绑定
        ButterKnife.bind(this);

        if (BigGiftUtil.getInstance().checkAllBigGiftExists())
            llBigGiftGroup.setVisibility(View.GONE);
        try
        {
            int localCount = Integer.parseInt(BigGiftUtil.getInstance().getLocalGiftCount());
            int allCount = Integer.parseInt(BigGiftUtil.getInstance().getGiftCount());
            if (localCount == allCount)
                llBigGiftGroup.setVisibility(View.GONE);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        tvBigGiftCount.setText(BigGiftUtil.getInstance().getLocalGiftCount() + "/" + BigGiftUtil.getInstance().getGiftCount());

        BigGiftUtil.getInstance().setIOnBigGiftDownloadListener(new BigGiftUtil.IOnBigGiftDownloadListener()
        {
            @Override
            public void onBigGiftDownload(String sucNum, String count)//   4/11
            {
                Logger.t(TAG).d("gift------------>onBigGiftDownload():" + sucNum + "/" + count);
                int suc, giftCount;
                try
                {
                    suc = Integer.parseInt(sucNum);
                    giftCount = Integer.parseInt(count);
                    mActivity.runOnUiThread(() ->
                    {
                        if (refuseDownloadText.getVisibility() == View.VISIBLE)
                            refuseDownloadText.setVisibility(View.GONE);
                        if (llBigGiftGroup.getVisibility() == View.GONE)
                            llBigGiftGroup.setVisibility(View.VISIBLE);
                        tvBigGiftCount.setText(sucNum + "/" + count);
                        Logger.t(TAG).d("suc:" + suc);
                        if (suc == giftCount)
                        {
                            llBigGiftGroup.setVisibility(View.GONE);
                            refuseDownloadText.setVisibility(View.GONE);
                        }

                    });
                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });
        NetworkChangeReceiver.setOnNetworkChangedListener(new NetworkChangeReceiver.IOnNetworkChangedListener()
        {
            @Override
            public void onNetworkChanged(int status)
            {
                Logger.t(TAG).d("gift------------>onNetworkChanged():" + status);
                if (status == 1)
                {
                    if (!BigGiftUtil.getInstance().checkAllBigGiftExists())
                    {
                        llBigGiftGroup.setVisibility(View.VISIBLE);
                        refuseDownloadText.setVisibility(View.GONE);
                    }
                }
                if (status == 2 || status == 3)
                {
                    if (!BigGiftUtil.getInstance().checkAllBigGiftExists())
                    {
                        llBigGiftGroup.setVisibility(View.GONE);
                        refuseDownloadText.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        heartLikeSurfaceView.setZOrderMediaOverlay(true);
        slBody.setListener(new SwipeMoveRelativeLayout.SwipeMoveListener()
        {
            @Override
            public void dispatchTouch(MotionEvent event)
            {
                avRootView.dispatchTouchEvent(event);
            }

            @Override
            public void onClick()
            {
                if (mSendLayout.isShown())
                {
//                    showSendLayout(false);
                }
                else
                {
                    if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_MEMBER)
                    {
                        if (!isPraise)
                        {
                            isPraise = true;
                            Map<String, Object> msgMap = new HashMap<>();
                            msgMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_Praise_Msg);
                            msgMap.put(TXConstants.CMD_PARAM, "");
                            mPresenter.sendTXIMMessage(msgMap, "AVIMCMD_Praise_Msg");
                        }
                        Map<String, Object> msgMap = new HashMap<>();
                        msgMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_Praise);
                        msgMap.put(TXConstants.CMD_PARAM, "");
                        mPresenter.sendTXIMMessage(msgMap, "AVIMCMD_Praise");
                    }
                }
                if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                    llBeauty.setVisibility(View.GONE);
            }

            @Override
            public void onSwipeToHidden()
            {
                ibClearedClear.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSwipeToShow()
            {
                ibClearedClear.setVisibility(View.GONE);
            }
        });


        giftManager = SlidGiftManager.getInstance();
        giftManager.init(R.id.room_amin_container, mActivity, CommonUtils.dp2px(mActivity, GIFT_SIZE_DP));
        giftLargeManager = new LargeGiftManager(getWindow(), R.id.rlLargeGiftContainer, mActivity, mPresenter.getmRecord().getEnterRoom4EH().getPhUrl());
        giftPopWinManager = new GiftPopWinManager(mActivity, CommonUtils.dp2px(mActivity, GIFT_SIZE_DP));

//        f_manager = FightingManager.get(getWindow(), R.id.room_fighting_container);
//        uio_manager = UioManager.get(getWindow(), R.id.room_uio_container);
        giftPopWinManager.setViewClickListener(new GiftPopWinManager.IOnViewClickListener()
        {
            @Override
            public void onClick(View view, String viewId)
            {
                switch (viewId)
                {
                    case "sendGift":
                        mPresenter.sendGift(giftPopWinManager.getChosenGiftID(), giftPopWinManager.getChosenGiftNum(), giftPopWinManager.getChosenGiftBean(), 0);
                        if ("1".equals(giftPopWinManager.getChosenGiftBean().getgType()))//大礼物资源提示
                        {
                            if (!BigGiftUtil.getInstance().checkBigGiftIsExists(giftPopWinManager.getChosenGiftBean().getgId()))
                                ToastUtils.showShort("礼物发送成功，礼物未下载完成，因此无法显示特效");
                        }

                        break;
                    case "chargeFaceEgg":
                        startActivityForResult(new Intent(mActivity, MyInfoBuyFaceEggActivity.class), EamCode4Result.reQ_MyInfoBuyFaceEggActivity);
                        break;
                    default:
                        break;
                }
            }
        });


//        LiveMsgDialog.liveMsgView = LayoutInflater.from(mActivity).inflate(R.layout.live_msg_dialog, null);
//        liveMsgDialog = ViewShareHelper.getInstance(mActivity,mPresenter).liveMsgDialogInstance();
        LiveNewMsgDialog.liveMsgView = LayoutInflater.from(mActivity).inflate(R.layout.live_msg_dialog, null);
        liveNewMsgDialog = ViewShareHelper.getInstance(mActivity, mPresenter).liveNewMsgDialogInstance();

        liveNewMsgDialog.setOnDismissListener(new LiveNewMsgDialog.DismissListener()
        {
            @Override
            public void onDismiss()
            {
//                if (liveNewMsgDialog != null)
//                {
//                    if (liveNewMsgDialog.isHasNewChat() || liveNewMsgDialog.isHasNewSayHello())
//                    {
//                        runOnUiThread(new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {
//                                btMessageDian.setVisibility(View.VISIBLE);
//                            }
//                        });
//                    }
//                }
                HuanXinIMHelper.getInstance().getUnreadChatMsgNum(HuanXinIMHelper.ALL_CHAT_TYPE, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        Map<String, Integer> map = EamApplication.getInstance().getGsonInstance().fromJson(response, new TypeToken<Map<String, Integer>>()
                        {
                        }.getType());
                        int allUnreadNum = map.get("all");
                        if (allUnreadNum > 0)
                            btMessageDian.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(String code, String msg)
                    {

                    }
                });
            }
        });
        HuanXinIMHelper.getInstance().getUnreadChatMsgNum(HuanXinIMHelper.ALL_CHAT_TYPE, new ICommonOperateListener()
        {
            @Override
            public void onSuccess(String response)
            {
                Map<String, Integer> map = EamApplication.getInstance().getGsonInstance().fromJson(response, new TypeToken<Map<String, Integer>>()
                {
                }.getType());
                int allUnreadNum = map.get("all");
                if (allUnreadNum > 0)
                    btMessageDian.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String code, String msg)
            {

            }
        });
        // 进入房间前获取的数据
        enterRoom4EH = mPresenter.getmRecord().getEnterRoom4EH();
        Logger.t(TAG).d("=====> " + mPresenter.getmRecord().getEnterRoom4EH().getNicName() + " , " + mPresenter.getmRecord().getRoomId());
        refreshRoomInfo(mPresenter.getmRecord().getEnterRoom4EH(), mPresenter.getmRecord().getRoomId());
        String htmlStr = String.format("<font color=%s>%s</font>", TXConstants.ENTER_ROOM_SYSTEM_COLOR, getResources().getString(R.string.live_list_title));
        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
        refreshLiveMsgBean.setText(getResources().getString(R.string.live_list_title));
        refreshLiveMsgBean.setName("消息");
        refreshLiveMsgBean.setType(LiveMsgType.Declaration);
        refreshLiveMsgBean.setLiveLevelState("0");
        refreshLiveMsgBean.setLevel("0");
        refreshText(refreshLiveMsgBean);

        //主播开直播间不需要弹出创建直播间成功文案  BUG号9606
//        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
//        {
//            // 创建直播间成功
//            String htmlCreateRoomStr = String.format("<font color=%s>%s</font>", TXConstants.ENTER_ROOM_SYSTEM_COLOR, "创建直播间成功");
//            RefreshLiveMsgBean refreshLiveMsgBean1 = new RefreshLiveMsgBean();
//            refreshLiveMsgBean1.setText("创建直播间成功");
//            refreshLiveMsgBean1.setType(LiveMsgType.Declaration);
//            refreshLiveMsgBean1.setLiveLevelState("0");
//            refreshLiveMsgBean1.setLevel("0");
//            refreshText(refreshLiveMsgBean1);
//        }
        Logger.t(TAG).d("LiveAonCreate", TAG + " onCreate", "PID-> " + Process.myPid() + " |UID-> " + Process.myUid() + " |TID-> " + Process.myTid());
        registerBrdReceiver();
        if ("1".equals(mPresenter.getmRecord().getEnterRoom4EH().getIsSignedAnchor()) && mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
        {
            btnBeauty.setVisibility(View.VISIBLE);
            ViewShareHelper.getInstance(mActivity, mPresenter).initBeauty(llBeauty, 7, 9, 3, 3);
        }


        if (!TextUtils.equals(SharePreUtils.getUId(mActivity), mPresenter.getmRecord().getEnterRoom4EH().getuId()))
        {
            ViewShareHelper.getInstance(mActivity, mPresenter).showNewbieGuide();
        }
    }


    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        CommonUtils.removeClickLock(TAG + "1");
        ViewShareHelper.getInstance(mActivity, mPresenter).gameOnResume();
        ViewShareHelper.getInstance(mActivity, mPresenter).showGameStart(null, true, false);
        super.onResume();
     /*   if (mLiveNewFriendsFragment!=null){
            mLiveNewFriendsFragment.showRedSmallDot();
            showMessageBtDian(true);
        }*/
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        ViewShareHelper.getInstance(mActivity, mPresenter).gameOnPause();
        if (heartLikeSurfaceView != null)
            heartLikeSurfaceView.pause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        ViewShareHelper.destroyInstance();
        removeMsgListener();
        unRegisterBrdReceiver();
        if (connectPopup != null && connectPopup.isShowing())
        {
            connectPopup.dismiss();
            connectPopup = null;
        }

        if (mLiveHostInfoPop != null)
        {
            mLiveHostInfoPop.dismiss();
            mLiveHostInfoPop = null;
        }
        if (starPopWindow != null)
        {
            starPopWindow.dismiss();
            starPopWindow = null;
        }
        if (handler != null)
            handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    /**
     * 关闭小窗口 连麦
     *
     * @param role   关闭角色
     * @param reason 关闭原因
     */
    @Override
    public void closeInviteLive(String role, String reason)
    {
        super.closeInviteLive(role, reason);
        if ("host".equals(role))
        {
            if (id4ContactMemberBean == null || avRootView == null)
                return;
            if ("2".equals(reason))
                ToastUtils.showShort("网络连接超时，连麦已取消");
            for (int i = 0; i < mRenderUserList.size(); i++)
            {
                if (TextUtils.isEmpty(backGroundId))
                    EamLogger.t(TAG).writeToDefaultFile("backGroundId:" + backGroundId);
                Logger.t(TAG).d("mRenderUserList.get(i)" + mRenderUserList.get(i) + " | backGroundId:" + backGroundId);
                if (mRenderUserList.get(i).equals(backGroundId) && !backGroundId.equals("u" + mPresenter.getmRecord().getRoomId()))
                {
                    avRootView.swapVideoView(0, 1);
                    backGroundId = avRootView.getViewByIndex(0).getIdentifier();
                    Logger.t(TAG).d("backGroundId:" + backGroundId);
                }
            }
            //重置 绑定关系 全部解除绑定
            Logger.t(TAG).d("backGroundId:" + backGroundId + "SharePreUtils.getTlsName(mActivity)):" + SharePreUtils.getTlsName(mActivity) + "id4ContactMemberBean.getId():" + id4ContactMemberBean.getId());
            if (backGroundId.equals(SharePreUtils.getTlsName(mActivity)))
                avRootView.bindIdAndView(1, AVView.VIDEO_SRC_TYPE_CAMERA, null, true);
            else
                avRootView.bindIdAndView(0, AVView.VIDEO_SRC_TYPE_CAMERA, null, true);
            mWillUserList.clear();
            avRootView.closeUserView("u" + id4ContactMemberBean.getId(), AVView.VIDEO_SRC_TYPE_CAMERA, true);
            mPresenter.sendCloseInvite("u" + id4ContactMemberBean.getId(), reason);
        }
        else
        {
            if (avRootView == null)
                return;
            mPresenter.down2MemberVideo();
            if (backGroundId.equals(SharePreUtils.getTlsName(mActivity)))
                avRootView.bindIdAndView(0, AVView.VIDEO_SRC_TYPE_CAMERA, null, true);
            else
                avRootView.bindIdAndView(1, AVView.VIDEO_SRC_TYPE_CAMERA, null, true);
            avRootView.closeUserView(SharePreUtils.getTlsName(mActivity), AVView.VIDEO_SRC_TYPE_CAMERA, true);
            mPresenter.sendCloseInvite(SharePreUtils.getTlsName(mActivity), reason);
        }
        isShowInviteLive = false;
        hideCloseInviteBtn();
    }

    @Override
    protected void actionHasOpenCamera(List<String> ids)
    {
        super.actionHasOpenCamera(ids);
        if (null == ids)
            return;
        boolean isNeedAddHostId = false;
        for (String id : ids)
        {
            if (!mRenderUserList.contains(id))
            {
                mRenderUserList.add(id);
                if (id.equals("u" + mPresenter.getmRecord().getRoomId()))
                    memberVideoStreamResume();
            }

            //当主播正在连麦中 切换大小窗口后 切后台，再回来， ids中就不会包含连麦人的id，只有主播自己的id 然后就会出现 两个视频流往同一个view上绘制 画面
            //会出现闪屏， 所以此处 特殊处理
            if (isShowInviteLive && isSwitch2Back && mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
            {
                //如果是自己则直接渲染

                if (backGroundId.equals(mWillUserList.get(0)))
                {
                    avRootView.bindIdAndView(1, AVView.VIDEO_SRC_TYPE_CAMERA, id, true);
                }
                else
                    avRootView.bindIdAndView(0, AVView.VIDEO_SRC_TYPE_CAMERA, id, true);
                boolean isRenderSuccess = avRootView.renderVideoView(true, id, AVView.VIDEO_SRC_TYPE_CAMERA, false);
                Logger.t(TAG).d("isRenderSuccess：" + isRenderSuccess);
                return;
            }
            else
            {
                //如果是自己则直接渲染
               /* if (id.equals(SharePreUtils.getTlsName(mActivity)))
                {
                    if (LiveRecord.ROOM_MODE_HOST == mPresenter.getmRecord().getModeOfRoom())
                    {
                        avRootView.bindIdAndView(0, AVView.VIDEO_SRC_TYPE_CAMERA, id, true);
                    }
                    else
                    {
                        avRootView.bindIdAndView(1, AVView.VIDEO_SRC_TYPE_CAMERA, id, true);
                    }
                    boolean isRenderSuccess = avRootView.renderVideoView(true, id, AVView.VIDEO_SRC_TYPE_CAMERA, false);
                    EamLogger.t(TAG).writeToDefaultFile("自己渲染结果：" + isRenderSuccess +
                            " | 自己ID:" + SharePreUtils.getTlsName(mActivity) + " | 发现摄像头用户ID:" + id);
                    return;
                }*/
                //第三者进入时 ,当主播正在连麦中切后台，ids中就不会包含主播的id，只有连麦观众的id。其他观众进入需要获取主播的view，所以需要将主播id加入（无流）
                if (!id.equals(SharePreUtils.getTlsName(mActivity)) &&
                        !id.equals("u" + mPresenter.getmRecord().getRoomId()) &&
                        mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_MEMBER)
                {
                    isNeedAddHostId = true;
                }
            }


        }
        if (isNeedAddHostId)
        {
            if (mRenderUserList.contains("u" + mPresenter.getmRecord().getRoomId()))
                mRenderUserList.add("u" + mPresenter.getmRecord().getRoomId());
        }
        mPresenter.requestViewList(mRenderUserList, AVView.VIDEO_SRC_TYPE_CAMERA);
    }

    @Override
    protected void actionHasNoCamera(List<String> ids)
    {
        super.actionHasNoCamera(ids);
        for (String id : ids)
        {
            if (mRenderUserList.contains(id))
            {
                mRenderUserList.remove(id);
            }
        }
    }

    @Override
    protected void hasScreenVideo(List<String> ids, int type)
    {
        super.hasScreenVideo(ids, type);
        mPresenter.requestViewList(ids, type);
    }

    public void hideCloseInviteBtn()
    {
        btnCloseConnectLive.setVisibility(View.GONE);
    }

    @Override
    public void finish()
    {
        if (giftManager != null)
        {
            giftManager.recycle();
            giftLargeManager.recycle();
            giftPopWinManager.recycle();
            heartLikeSurfaceView.recycle();
        }
        LruCacheBitmapLoader.getInstance().recycle();
//        f_manager.recycle();
//        uio_manager.recycle();
        giftManager = null;
        giftLargeManager = null;
        giftPopWinManager = null;
        heartLikeSurfaceView = null;
        //  liveMsgDialog = null;
        liveNewMsgDialog = null;
        mActivity = null;
        super.finish();
    }


    //endregion

    public void addMsgListener()
    {
        Logger.t("addMsgListener").d("addMsgListener>>>>>");
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    public void removeMsgListener()
    {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    private EMMessageListener msgListener = new AbstractEMMessageListener()
    {
        @Override
        public void onMessageReceived(List<EMMessage> messages)
        {
            try
            {
                super.onMessageReceived(messages);
                Logger.t(TAG).d("onMessageReceived==" + messages.toString());
                //收到消息
                showMsgRedPoint();
                for (EMMessage message : messages)
                {
                    if (message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false))
                        liveNewMsgDialog.setHasNewSayHello(true);
                    else
                        liveNewMsgDialog.setHasNewChat(true);
                }

            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("hx 消息解析错误" + e.getMessage());
            }
        }
    };

    @Override
    public void onBackPressed()
    {
        if (ViewShareHelper.getInstance(mActivity, mPresenter).dismissShareWin() == false)
            return;
        if (mLiveHostInfoPop != null && mLiveHostInfoPop.isShowing())
        {
            mLiveHostInfoPop.dismiss();
            return;
        }
        if (starPopWindow != null && starPopWindow.isShowing())
        {
            starPopWindow.dismiss();
            return;
        }
        if (giftPopWinManager != null && giftPopWinManager.isShowing())
        {
            giftPopWinManager.dismiss();
            return;
        }
        if (mSendLayout != null && mSendLayout.isShown())
        {
            return;
        }
    }

    @Override
    protected void onCreateRoomSuccess()
    {
        super.onCreateRoomSuccess();
        if ("meitu".equalsIgnoreCase(Build.BRAND))
            new Handler().postDelayed(() -> mPresenter.toggleCamera(), 1000);
        checkLocalGiftExists();
    }

    /**
     * 检查本地大礼物提示
     */
    private void checkLocalGiftExists()
    {
        if (!BigGiftUtil.getInstance().checkAllBigGiftExists())
        {
            int netStatus = NetHelper.getNetworkStatus(mActivity);
            if (netStatus == 1)
            {
                ToastUtils.showLong("大礼物正在下载中，未完成下载时发送或接收大礼物有可能看不到大礼物特效");
                if (!BigGiftUtil.isBigGiftDownloading)
                {
                    BigGiftUtil.startCheckBigGif(mActivity, NetInterfaceConstant.FILE_GIFT_VERSION, false);
                }
            }
            else if (netStatus == 2 || netStatus == 3)
            {
                if (BigGiftUtil.isBigGiftDownloading)
                    return;
                new CustomAlertDialog(mActivity)
                        .builder()
                        .setCancelable(false)
                        .setTitle("提示")
                        .setMsg("当前为移动网络环境，下载直播间大礼物将消耗流量，是否下载？")
                        .setPositiveButton("是", (view) ->
                        {
                            if (!BigGiftUtil.isBigGiftDownloading)
                            {
                                BigGiftUtil.startCheckBigGif(mActivity, NetInterfaceConstant.FILE_GIFT_VERSION, false);
                            }
                            else
                                EamLogger.t(TAG).writeToDefaultFile("4G情况点击是，没有下载大礼物：" + BigGiftUtil.isBigGiftDownloading);
                        })
                        .setNegativeButton("否", (view) ->
                        {
                            llBigGiftGroup.setVisibility(View.GONE);
                            refuseDownloadText.setVisibility(View.VISIBLE);
                        })
                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode)
        {
            case RESULT_OK:
                switch (requestCode)
                {
                    case EamCode4Result.reQ_MyInfoBuyFaceEggActivity:
                        if (!((EamApplication) getApplication()).uInfoFaceEgg.equals(giftPopWinManager.getUIFaceEggBalance()))
                        {
                            giftPopWinManager.refreshUIFaceEggBalance(((EamApplication) getApplication()).uInfoFaceEgg);
                        }
                        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
                        callServerSilence(NetInterfaceConstant.UserC_newBalance, null, null, reqMap);

                        if (inputTextMsgDialog != null)
                            inputTextMsgDialog.updataLevel();
                        break;
                    case EamCode4Result.reQ_LChooseContactMemberAct:
                        if (data == null)
                            return;
                        //发起连麦
                        id4ContactMemberBean = (LChoseConnectMemberBean) data.getSerializableExtra(EamConstant.EAM_INTENT_TX_ID);
                        mPresenter.checkUserIsInGroup("u" + id4ContactMemberBean.getId(), new TIMValueCallBack<List<TIMGroupMemberInfo>>()
                        {
                            @Override
                            public void onError(int i, String s)
                            {
                                Logger.t(TAG).d("获取群成员资料 失败i:" + i + ",s>" + s);
                            }

                            @Override
                            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos)
                            {
                                if (timGroupMemberInfos != null && timGroupMemberInfos.size() != 0)
                                {
                                    Logger.t(TAG).d("获取群成员资料 成功：timGroupMemberInfos：" + timGroupMemberInfos.size());
                                    ToastUtils.showShort("邀请已发出，等待对方接受");
                                    Logger.t(TAG).d("选择成功，发送连麦邀请id:" + id4ContactMemberBean.getId());
                                    mWillUserList.add("u" + id4ContactMemberBean.getId());
//                                    mPresenter.sendC2CCmd(TXConstants.AVIMCMD_MUlTI_HOST_INVITE, "", "u" + id4ContactMemberBean.getId());
                                    mPresenter.sendOnlineC2CCmd(TXConstants.AVIMCMD_MUlTI_HOST_INVITE, "", "u" + id4ContactMemberBean.getId(), null);
                                    hostSendMsg.setVisibility(View.GONE);
                                    btnInviteTime.setVisibility(View.VISIBLE);
                                    handler.postDelayed(runnable, 1000);
                                }
                                else
                                {
                                    ToastUtils.showShort("该用户已离开直播间，请重新选择！");
                                }
                            }
                        });
                        break;
                    case EamCode4Result.reQ_RED_REQUEST_CODE:
                        if (data == null)
                            return;
                        // 群发红包
                        Logger.t(TAG).d("streamId--> " + data.getStringExtra("streamId"));
                        Map<String, Object> transElement = new HashMap<>();
                        transElement.put(TXConstants.CMD_PARAM, data.getStringExtra("streamId"));
                        transElement.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_SEND_RED_PACKET);
                        mPresenter.sendTXIMMessage(transElement, "AVIMCMD_SEND_RED_PACKET");
                        break;
                    case EamConstant.EAM_OPEN_RELATION:
                        //分享看脸好友无成功回调 以下代替回调
                        NetHelper.addLiveShareCount(mActivity);
                        NetHelper.activityShare(mActivity, "0");
                        break;
                }
                break;
            case RESULT_CANCELED:
                break;
            case RESULT_FIRST_USER:
                break;
        }
    }


    //验证码倒计时
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            btnInviteTime.setText(String.format("%s s", String.valueOf(inviteCountdownTime--)));
            if (inviteCountdownTime != 0)
            {
                handler.postDelayed(this, 1000);
            }
            else
            {
                resetGetSecurityButton(0);
            }

        }
    };

    /**
     * 重置验证码获取按钮
     */
    private void resetGetSecurityButton(int type)
    {
        hostSendMsg.setVisibility(View.VISIBLE);
        btnInviteTime.setVisibility(View.GONE);
        btnInviteTime.setText("30 s");
        inviteCountdownTime = 30;
        if (type == 0)
        {
            ToastUtils.showShort("当前连麦请求已失效");
            mPresenter.sendOnlineC2CCmd(TXConstants.AVIMCMD_MUlTI_HOST_CLOSE_INVITE_TIMEOUT, "", "u" + id4ContactMemberBean.getId(), null);
        }
        handler.removeCallbacks(runnable);
    }

    private int usableHeightPrevious;
    private int mSoftHeight;
    ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
    {
        @Override
        public void onGlobalLayout()
        {
            Logger.t(TAG).d("布局变化了》》");
            int usableHeightNow = computeUsableHeight();
            if (usableHeightNow != usableHeightPrevious)
            {
                if (mActivity == null)
                    return;
                usableHeightPrevious = usableHeightNow;
                //这个是解决华为p9 弹出键盘消息列表滚动到第一条的bug,可能是顶部状态栏的
                // 这鸡巴机器啊！
                //mChatRoomLinearLayoutManager.scrollToPosition(chatMsgListAdapter.getCount() - 1);
                ViewShareHelper.getInstance(mActivity, mPresenter).scrollMsgLstToBottom();
                slBody.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);

                Rect r = new Rect();
                slBody.getWindowVisibleDisplayFrame(r);
                int screenHeight = slBody.getRootView().getHeight();
                int softHeight = screenHeight - usableHeightNow;
                Logger.t(TAG).d("屏幕高度》" + screenHeight + "可用高度》" + usableHeightNow + "键盘高度》" + softHeight + "usableHeightPrevious》" + usableHeightPrevious);
                Logger.t(TAG).d("是否有虚拟键：" + CommonUtils.checkDeviceHasNavigationBar(mActivity) +
                        "虚拟键高度：" + CommonUtils.getBottomStatusHeight(mActivity));
                if (inputTextMsgDialog != null && softHeight > (screenHeight / 8))
                {
                    if (inputTextMsgDialog.isShowing() && mSoftHeight > softHeight)
                    {
                        inputTextMsgDialog.dismiss();
                        inputTextMsgDialog.show();
                    }
                }
//                if (liveMsgDialog != null && softHeight > (screenHeight / 8))
//                {
//                    if (liveMsgDialog.getShowsDialog() && mSoftHeight > softHeight)
//                    {
//                        liveMsgDialog.upHeight();
//                    }
//                }
                if (liveNewMsgDialog != null && softHeight > (screenHeight / 8))
                {
                    if (liveNewMsgDialog.getShowsDialog() && mSoftHeight > softHeight)
                    {
                        liveNewMsgDialog.upHeight();
                    }
                }
                if (softHeight > 0)
                    mSoftHeight = softHeight;
                if (softHeight > (screenHeight / 8))
                {

                    //   ViewShareHelper.getInstance(mActivity,mPresenter).setZOrderOnTop(true);

                    controlListView.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams params = controlListView.getLayoutParams();
                    if (CommonUtils.checkDeviceHasNavigationBar(mActivity))
                    {
                        params.height = softHeight - CommonUtils.getBottomStatusHeight(mActivity);
                    }
                    else
                    {
                        params.height = softHeight;
                    }
                    controlListView.setLayoutParams(params);
                }
                else
                {
                    ViewGroup.LayoutParams params = controlListView.getLayoutParams();
                    params.height = 0;
                    controlListView.setLayoutParams(params);
                    controlListView.setVisibility(View.INVISIBLE);
                    //      ViewShareHelper.getInstance(mActivity,mPresenter).setZOrderMediaOverlay(true);
                }
                if (usableHeightNow <= usableHeightPrevious)
                    slBody.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
            }
            usableHeightPrevious = usableHeightNow;
        }
    };

    private int computeUsableHeight()
    {
        Rect r = new Rect();
        slBody.getWindowVisibleDisplayFrame(r);
        Logger.t(TAG).d("rec bottom>" + r.bottom + " rec top>" + r.top);
        //这个是解决vivo X9 浏览其他页面后返回消息列表滚动到第一条的bug，返回后r.top=69 ,可能是顶部状态栏的高度，这鸡巴机器啊！
        if (r.top > 40)
        {
            ViewShareHelper.getInstance(mActivity, mPresenter).scrollMsgLstToBottom();
            //scrollMsgLstToBottom();
        }

        return (r.bottom);
        //return (r.bottom - r.top);// 全屏模式下： return r.bottom
    }

    //region 私有方法
    private View getControlViewLayout()
    {
        switch (mPresenter.getmRecord().getModeOfRoom())
        {
            case LiveRecord.ROOM_MODE_HOST:
                return getLayoutInflater().inflate(R.layout.include_host_control, null, true);
            case LiveRecord.ROOM_MODE_MEMBER:
                return getLayoutInflater().inflate(R.layout.include_member_control, null, true);
        }
        return getLayoutInflater().inflate(R.layout.include_member_control, null, true);
    }

    /**
     * 退出前一个房间（杀进程会造成此错误）
     */
    private void quitPreRoom()
    {
        final String roomId = SharePreUtils.getPreGroupId(mActivity);
        if (!TextUtils.isEmpty(roomId) && !roomId.equals(mPresenter.getmRecord().getRoomId()))
        {
            mPresenter.down2MemberVideo();
            EamLogger.t(TAG).writeToDefaultFile("退出上一个直播间id：" + roomId + "调用下麦");
            //成员退出群
            TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack()
            {
                @Override
                public void onError(int i, String s)
                {
                    Logger.t(TAG).d("错误码》" + i + "错误描述》" + s);
                }

                @Override
                public void onSuccess()
                {
                    Map<String, Object> msgMap = new HashMap<>();
                    msgMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_ExitLive);
                    msgMap.put(TXConstants.CMD_PARAM, "");
                    mPresenter.sendTXIMMessage(msgMap, roomId, "");
                }
            });
        }
    }


    private int inviteViewCount = 0;

    private boolean showInviteView(String id)
    {
        Logger.t(TAG).d("连麦用户id》" + id);
        int index = avRootView.findValidViewIndex();
        if (index == -1)
        {
            //Toast.makeText(LiveAct1.this, "the invitation's upper limit is 3", Toast.LENGTH_SHORT).show();
            return false;
        }
        int requetCount = index + inviteViewCount;
        if (requetCount > 3)
        {
            //Toast.makeText(LiveAct1.this, "the invitation's upper limit is 3", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (hasInvited(id))
        {
            //Toast.makeText(LiveAct1.this, "it has already invited", Toast.LENGTH_SHORT).show();
            return false;
        }
        switch (requetCount)
        {
            case 1:
//                inviteView1.setText(id);
                inviteView1.setVisibility(View.VISIBLE);
                inviteView1.setTag(id);

                break;
            case 2:
                inviteView2.setText(id);
                inviteView2.setVisibility(View.VISIBLE);
                inviteView2.setTag(id);
                break;
            case 3:
                inviteView3.setText(id);
                inviteView3.setVisibility(View.VISIBLE);
                inviteView3.setTag(id);
                break;
        }
        //mLiveHelper.sendC2CCmd(Constants.AVIMCMD_MUlTI_HOST_INVITE, "", id);
        inviteViewCount++;
        //30s超时取消
        Message msg = new Message();
        //msg.what = TIMEOUT_INVITE;
        msg.obj = id;
        // mHandler.sendMessageDelayed(msg, 30 * 1000);
        return true;
    }

    /**
     * 判断是否邀请过同一个人
     */
    private boolean hasInvited(String id)
    {
        if (id.equals(inviteView1.getTag()))
        {
            return true;
        }
        if (id.equals(inviteView2.getTag()))
        {
            return true;
        }
        if (id.equals(inviteView3.getTag()))
        {
            return true;
        }
        return false;
    }


    //region 公共接口
    public void roomEventsHostLeave(String invoker, String reason)
    {

    }

    /***
     *  没有在房间的时候，停止交互;<br>
     *  {@link LiveAct1} {@see isInAvRoom} 可以使用是否在房间标记；<br>
     *  我单独写了2个 Audiences & MessageList
     */
    protected void cleanRoomUI()
    {
        rivHostAvatar.setImageResourceByID(R.drawable.userhead);
        tvRoomName.setText("");
        tvFollow.setVisibility(View.VISIBLE);
        tvMealTicketCount.setText("");
        tvStarChartCount.setText("");
        tvId.setText("");
        //c audience list； 思考了下，防止可能出现异步，还是加了一个锁，切房间时不加载
        stopAddAudiences = true;

        //c message list
        stopAddMessageList = true;

        //stop gift, flow heart
        giftManager.waiting();
        giftLargeManager.waiting();
        heartLikeSurfaceView.waiting();
    }

    protected void refreshRoomUI()
    {
        // 房间信息 & 约吃饭开关
        Map<String, String> reqMap1 = NetHelper.getCommonPartOfParam(mActivity);
        reqMap1.put(ConstCodeTable.roomId, mPresenter.getmRecord().getRoomId());
        callServerSilence(NetInterfaceConstant.LiveC_enterRoom, null, null, reqMap1);
        Map<String, String> reqMap2 = NetHelper.getCommonPartOfParam(mActivity);
        callServerSilence(NetInterfaceConstant.LiveC_swap, null, null, reqMap2);

        // 消息列表
        mPresenter.getmRecord().flush();//清空
        stopAddMessageList = false;
        String htmlStr = String.format("<font color=%s>%s</font>", TXConstants.ENTER_ROOM_SYSTEM_COLOR, "官方倡导绿色直播，网警24小时在线，违规违法、低俗、暴力、色情等都会被查封账号哦~");
        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
        refreshLiveMsgBean.setText(htmlStr);
        refreshLiveMsgBean.setName("消息");
        refreshLiveMsgBean.setType(LiveMsgType.Declaration);
        refreshLiveMsgBean.setLiveLevelState("0");
        refreshText(refreshLiveMsgBean);
        //观众列表
        mPresenter.arrAudiencesObj.clear();
        stopAddAudiences = false;
        mPresenter.pullAudiences(0, new HashMap<String, Object>());

        //restart working
        giftManager.working();
        giftLargeManager.working();
        heartLikeSurfaceView.working();
    }
    //endregion

    //region 在p层完成操作后的回调方法 ，这些方法只负责UI更新工作
    public void refreshRoomInfo(LiveEnterRoomBean eh, String strRoomid)
    {
        rivHostAvatar.setHeadImageByUrl(eh.getPhUrl());
        rivHostAvatar.setLevel(eh.getAnchorLevel());
        rivHostAvatar.showRightIcon(eh.getIsVuser());
        tvRoomName.setText(eh.getNicName());
        tvRoomName.setSelected(true);
        if (!SharePreUtils.getTlsName(mActivity).equals(eh.getName()))
        {
            tvFollow.setVisibility(View.GONE);
            if ("0".equals(eh.getFlag()))
                tvFollow.setVisibility(View.VISIBLE);
        }
        else
        {
            tvFollow.setVisibility(View.GONE);
        }
        tvMealTicketCount.setText(eh.getMeal() + " >");
        setStarChartNum(eh.getStar(), eh.getRanking());

        Logger.t(TAG).d("Star>>----" + eh.getStar() + ",ranking>>" + eh.getRanking());
        tvId.setText(String.format(getResources().getString(R.string.live_id), strRoomid));
    }


    /**
     * 发送消息回调
     *
     * @param callbackType 发送类型 0：成功 1：发送消息为空 2：发送消息失败 3:被禁言 4：发送弹幕失败
     * @param sendMsg      要发送的消息
     */
    public void sendMessageCallback(int callbackType, String sendMsg)
    {
        switch (callbackType)
        {
            case 0:
                //   String htmlStr = String.format("<font color=%s>%s</font>", TXConstants.ENTER_ROOM_SEND_MSG_COLOR, sendMsg);
                RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
                refreshLiveMsgBean.setText(sendMsg);
                refreshLiveMsgBean.setId(SharePreUtils.getId(mActivity));
                refreshLiveMsgBean.setName(SharePreUtils.getNicName(mActivity));
                refreshLiveMsgBean.setType(LiveMsgType.NormalText);
                refreshLiveMsgBean.setLevel(SharePreUtils.getLevel(mActivity) + "");
                refreshLiveMsgBean.setLiveLevelState("1");
                refreshText(refreshLiveMsgBean);
                break;
            case 1:
                ToastUtils.showShort(getString(R.string.tipErrInputIsEmpty));
                break;
            case 2:
                ToastUtils.showShort(getString(R.string.tipErrMessageCantSend));
                break;
            case 3:
                ToastUtils.showShort(getString(R.string.tipErrMessageShutUp));
                break;
        }
    }

    //region 注释代码

    /**
     * 更新 message list 操作
     *
     * @param liveMsgBean 刷新直播消息列表参数bean {@link RefreshLiveMsgBean}
     */
/*    public void refreshText(final RefreshLiveMsgBean liveMsgBean)
    {
        // FIXME: 2017/3/10 //这块是切房间，暂停数据加载的,需要优化
        if (stopAddMessageList)
        {
            return;
        }
        Observable.create(new Observable.OnSubscribe<TXIMChatEntity>()
        {
            @Override
            public void call(Subscriber<? super TXIMChatEntity> subscriber)
            {
                Logger.t(TAG).d("refreshText当前线程》》" + Thread.currentThread().getId());
                if (liveMsgBean != null && !TextUtils.isEmpty(liveMsgBean.getText()))
                {
                    TXIMChatEntity entity = new TXIMChatEntity();
                    entity.setId(liveMsgBean.getId());
                    entity.setSenderName(liveMsgBean.getColumnName());
                    entity.setContext(liveMsgBean.getText());
                    entity.setType(liveMsgBean.getType());
                    entity.setLevel(liveMsgBean.getLevel());
                    entity.setLiveLevelState(liveMsgBean.getLiveLevelState());
                    entity.setStreamId(liveMsgBean.getStreamId());
                    //如果是礼物类型
                    if (liveMsgBean.getType() == LiveMsgType.SmallGift || liveMsgBean.getType() == LiveMsgType.BigGift)
                    {

                        entity.setGiftName(liveMsgBean.getgName());
                        entity.setGiftNum(liveMsgBean.getGiftNum());
                        entity.setGiftUrl(liveMsgBean.getgUrl());
                        entity.setIsBigGift(liveMsgBean.getgType());
                    }

                    int msgCount = chatMsgListAdapter.getCount();
                    if (msgCount > 0)
                    {
//                        if (msgCount > 35)
//                        {
//                            chatMsgListAdapter.deleteMsg(0);
//                            msgCount = chatMsgListAdapter.getCount();
//                        }
                        TXIMChatEntity chatEntity = chatMsgListAdapter.getMsg(msgCount - 1);
                        if (entity.getType() == LiveMsgType.EnterRoom && chatEntity.getType() == LiveMsgType.EnterRoom)
                        {
                            chatEntity.setContext(entity.getContext());
                            chatEntity.setSenderName(entity.getSenderName());
                            chatEntity.setLevel(entity.getLevel());
                            chatEntity.setId(entity.getId());
                            subscriber.onNext(null);
                        }
                        else
                            subscriber.onNext(entity);
                    }
                    else
                    {
                        subscriber.onNext(entity);
                    }
                }
            }
        }).subscribeOn(Schedulers.computation())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TXIMChatEntity>()
                {
                    @Override
                    public void onCompleted()
                    {
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        EamLogger.t(TAG).t("消息类别解析错误》" + e.getMessage());
                    }

                    @Override
                    public void onNext(TXIMChatEntity entity)
                    {
                        chatMsgListAdapter.notifyDataChanged(entity);
                    }
                });
    }*/
    //endregion
    public void refreshText(final RefreshLiveMsgBean liveMsgBean)
    {
        ViewShareHelper.getInstance(mActivity, mPresenter).refreshText(liveMsgBean);
    }

    /**
     * 刷新排行榜开关，约炮图标 星光值 小游戏图标
     *
     * @param ranking 排行榜开关
     * @param receive 约炮开关
     * @param star    星光值开关
     */
    private void refreshBootyAndRank(String ranking, String receive, String star, String showGame)
    {
        flBootyCall.setVisibility(View.GONE);
        if ("0".equals(receive))
        {
            flBootyCall.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals("0", star))
        {
            llStarChart.setVisibility(View.VISIBLE);
            lavStar.setVisibility(View.VISIBLE);
        }
        if ("0".equals(showGame))
        {
            imgGame.setVisibility(View.VISIBLE);
            viewGame.setVisibility(View.VISIBLE);
        }
        else
        {
            imgGame.setVisibility(View.GONE);
            viewGame.setVisibility(View.GONE);
        }
    }

    /**
     * 下面这些方法调用前不需要业务逻辑处理后的回调结果，只是简单的刷新UI层面，
     * 本来应在UI层的事件中调用，但是现在我们将其扭转到P层调用
     * start
     */
    public void showRoomUI(boolean b)
    {
        if (b)
            ibClearedClear.setVisibility(View.GONE);
        else
            ibClearedClear.setVisibility(View.VISIBLE);
        slBody.smoothScrollToShow(b);
    }

    private void changeGiftPopWin(String facebalance)
    {
        giftPopWinManager.changeGiftCount();
        giftPopWinManager.refreshUIFaceEggBalance(facebalance);
        giftPopWinManager.checkGiftSendBtn();
    }

    private void setMealTicket(String ticket)
    {
        try
        {
            int oldMealTicket = Integer.parseInt(tvMealTicketCount.getText().toString().replace(" >", "").trim());
            int newMealTicket = Integer.parseInt(ticket);
            if (oldMealTicket > newMealTicket)
                return;
        } catch (NumberFormatException e)
        {
            Logger.t(TAG).d("meal ticket format err" + e.getMessage());
        }
        if (tvMealTicketCount != null)
        {
            tvMealTicketCount.setText(ticket + " >");
        }
    }

    /**
     * 获取当前饭票数   livePlayAct 中 initDetailDialog 调用
     *
     * @return
     */
    public String getMealTicket()
    {
        return tvMealTicketCount.getText().toString().replace(">", "").trim();
    }

    private void showMsgRedPoint()
    {
        try
        {
            if (mActivity != null)
                new Handler(mActivity.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btMessageDian.setVisibility(View.VISIBLE);
                    }
                });
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    /**
     * 点击头像显示主播详情
     */
    public void showHostInfo()
    {
        if (mPresenter != null)
        {
            mPresenter.lookHostInfo(mPresenter.getmRecord().getEnterRoom4EH().getuId());
        }
    }


    public void flowHeart()
    {
        heartLikeSurfaceView.put();
    }

    /**
     * 收群红包返回信息
     *
     * @param body 收群红包返回值
     * @param txId
     */
    private void getGroupRedResponse(String body, String txId)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(body);
            LiveGetRedPacketBean bean = new LiveGetRedPacketBean();
            bean.setAmount(jsonObject.getString("amount"));
            bean.setSex(jsonObject.getString("sex"));
            bean.setLevel(jsonObject.getString("level"));
            bean.setPhUrl(jsonObject.getString("phUrl"));
            bean.setNicName(jsonObject.getString("nicName"));
            bean.setAge(jsonObject.getString("age"));
            bean.setIsVuser(jsonObject.getString("isVuser"));
            final Map<String, Object> map = new HashMap<>();
            map.put("nickName", SharePreUtils.getNicName(mActivity));
            map.put("txId", SharePreUtils.getId(mActivity));
            map.put("toId", txId);

            final Map<String, Object> transElement = new HashMap<>();
            transElement.put(TXConstants.CMD_PARAM, EamApplication.getInstance().getGsonInstance().toJson(map));
            transElement.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_NOTIFY_RED_HINT_GROUP);
            //    mPresenter.sendC2CCmd(TXConstants.AVIMCMD_NOTIFY_RED_HINT, map, txId);
            mPresenter.sendTXIMMessage(transElement, "TXConstants.AVIMCMD_NOTIFY_RED_HINT_GROUP");

            String htmlStr = String.format("<font color=%s>%s</font>",
                    TXConstants.ENTER_ROOM_SYSTEM_COLOR, "的红包");
            RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
            refreshLiveMsgBean.setText(htmlStr);
            refreshLiveMsgBean.setId(jsonObject.getString("from").split("u")[1]);
            refreshLiveMsgBean.setName(jsonObject.getString("nicName"));
            refreshLiveMsgBean.setType(LiveMsgType.ReceiveRedPacket);
            refreshLiveMsgBean.setLiveLevelState("2");
            refreshLiveMsgBean.setMsgL("你领取了");
            refreshLiveMsgBean.setMsgR("的红包");
            refreshText(refreshLiveMsgBean);
            LiveRedPacketDialog.showGetRedPacket(mActivity, bean);
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }


    @Override
    public void onServerSuccessCallback(String evt, Map<String, Object> transElement, String body)
    {
        super.onServerSuccessCallback(evt, transElement, body);
        if (mActivity == null)
            return;
        Logger.t(TAG).d(String.format("接口》%s返回body》%s", evt, body));
        switch (evt)
        {
            case NetInterfaceConstant.LiveC_giftList:
                LGiftListBean eh = new Gson().fromJson(body, LGiftListBean.class);
                arrGift4EH = eh.getGifts();
                Logger.t(TAG).d("接口>>>>>>>>>>>>>>>>>>>" + arrGift4EH.get(0).toString());
                setGiftListData(arrGift4EH, eh.getBalance(), TextUtils.equals("1", (String) transElement.get("isShow")) ? true : false);
                break;
            case NetInterfaceConstant.UserC_newBalance:
                UserC_NewBalance4EH balance = new Gson().fromJson(body, UserC_NewBalance4EH.class);
                changeGiftPopWin(balance.getFace());
                break;
            case NetInterfaceConstant.LiveC_sendGift:
                LiveSendGiftBean sfBean = new Gson().fromJson(body, LiveSendGiftBean.class);
                changeGiftPopWin(sfBean.getFaceEgg());
                setMealTicket(sfBean.getMealTotal());

                setStarChartNum(sfBean.getStar(), sfBean.getRanking());

                if (transElement != null)
                {
                    GiftMsgBean giftMsgBean = new GiftMsgBean();
                    giftMsgBean.setIsGift("0");         // 0 是礼物
                    giftMsgBean.setNumber((String) transElement.get(ConstCodeTable.gNum));
                    giftMsgBean.setGift((GiftBean) transElement.get("chosenGiftBean"));
                    giftMsgBean.getGift().setCountTotal((Integer) transElement.get("giftCount"));
                    giftMsgBean.setMealTotal(sfBean.getMealTotal());
                    giftMsgBean.setRanking(sfBean.getRanking());
                    giftMsgBean.setStar(sfBean.getStar());
                    Map<String, Object> msgMap = new HashMap<>();
                    //正常礼物格式  IOS线上版本 已经恢复正常
                    msgMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_SEND_GIFT);
                    msgMap.put(TXConstants.CMD_PARAM, EamApplication.getInstance().getGsonInstance().toJson(giftMsgBean, GiftMsgBean.class));

//                    msgMap.put("isGift", giftMsgBean.getIsGift());
//                    msgMap.put("gift", EamApplication.getInstance().getGsonInstance().toJson(giftMsgBean.getGift(), GiftBean.class).replace("\\", ""));
//                    //   msgMap.put("gift", giftMsgBean.getGift());
//                    msgMap.put("number", giftMsgBean.getNumber());
//                    msgMap.put("mealTotal", giftMsgBean.getMealTotal());
//                    msgMap.put("ranking", giftMsgBean.getRanking());
//                    msgMap.put("star", giftMsgBean.getStar());
                    Logger.t(TAG).d("发送通知消息ranking  >>" + giftMsgBean.getRanking() + ",star>>" + giftMsgBean.getStar());
                    mPresenter.sendTXIMMessage(msgMap, "GiftMessage");
                }
                break;
            case NetInterfaceConstant.LiveC_roomMember_v307:
                final LiveRoomMemberBean roomMember = new Gson().fromJson(body, LiveRoomMemberBean.class);
                Logger.t(TAG).d("liveRoom1>>>>观众列表" + body);
                if (transElement.containsKey("type"))
                {
                    if (transElement.get("type").equals("syncAudienceNum"))
                    {
                        ViewShareHelper.getInstance(mActivity, mPresenter).setAudienceNum(roomMember.getNum());
                    }
                }
                else
                {
                    Observable.create(new ObservableOnSubscribe<Integer>()
                    {
                        @Override
                        public void subscribe(ObservableEmitter<Integer> e) throws Exception
                        {
                            if (roomMember.getRes() != null)
                            {
                                for (int i = 0; i < roomMember.getRes().size(); i++)
                                {
                                    LiveRoomMemberBean.ResBean resBean = roomMember.getRes().get(i);
                                    if (resBean.getId().equals(SharePreUtils.getId(mActivity)))//把自己过滤掉，因为要使用消息加入
                                        continue;
                                    mPresenter.audienceIntoRoom(resBean.getUid(), resBean.getId(),
                                            "",
                                            resBean.getHeadImg(),
                                            "1".equals(resBean.getIsGhost()) ? true : false,
                                            resBean.getLevel(), "u" + resBean.getId(), resBean.getIsVuser());
                                }
                            }
                            e.onNext(roomMember.getNum());
                        }
                    }).subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(mActivity.<Integer>bindUntilEvent(ActivityEvent.DESTROY))
                            .subscribe(new Consumer<Integer>()
                            {
                                @Override
                                public void accept(Integer integer) throws Exception
                                {
                                    ViewShareHelper.getInstance(mActivity, mPresenter).insetAudiences(integer);
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(Throwable throwable) throws Exception
                                {
                                    Logger.t(TAG).d(throwable.getMessage());
                                }
                            });
                }
                break;
            case "GiftMessage":
                if (transElement != null)
                {
                    String param = (String) transElement.get(TXConstants.CMD_PARAM);
                    GiftBean b = null;
                    if (!TextUtils.isEmpty(param))
                    {
                        b = EamApplication.getInstance().getGsonInstance().fromJson(param, GiftMsgBean.class).getGift();
                    }
                    else
                    {
                        b = EamApplication.getInstance().getGsonInstance().fromJson((String) transElement.get("gift"), GiftBean.class);
                    }

                    if ("1".equals(b.getgType()) && isShowingGiftWin())
                    {
                        //大礼物发送完毕 收起窗口
                        dismissGiftWin();
                    }
                }

                break;
            case NetInterfaceConstant.LiveC_focus:
            {
                tvFollow.setVisibility(View.GONE);

                Map<String, Object> msgMap = new HashMap<>();
                msgMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_Focus);
                msgMap.put(TXConstants.CMD_PARAM, "");
                mPresenter.sendTXIMMessage(msgMap, "LiveC_focus");

                if (mLiveHostInfoPop != null)
                {
                    mLiveHostInfoPop.focusAuthorSuccess();
                }
            }
            break;
            case NetInterfaceConstant.LiveC_swap:
                try
                {
                    JSONObject jsonObject = new JSONObject(body);
                    String ranking = jsonObject.optString("ranking", "1");
                    String thisTime = jsonObject.optString("thisTime", "1");
                    String receive = jsonObject.optString("receive", "1");
                    String star = jsonObject.optString("star", "1");
                    String sunMoonStar = jsonObject.optString("sunMoonStar", "1");
                    switchStatusMap.put("thisTime", thisTime);
                    switchStatusMap.put("ranking", ranking);
                    switchStatusMap.put("receive", receive);
                    switchStatusMap.put("star", star);
                    switchStatusMap.put("sunMoonStar", sunMoonStar);

                    refreshBootyAndRank(ranking, receive, star, sunMoonStar);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case NetInterfaceConstant.LiveC_sendMsg:
                try
                {
                    mPresenter.sendTXIMTextMessage(new JSONObject(body).getString("message"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                Logger.t(TAG).d("接口>>>>>>>>>>>>>>>>>>>" + NetInterfaceConstant.LiveC_sendMsg + "触发成功回调");
                break;
            case NetInterfaceConstant.LiveC_sendBarrage:
                String message = "";
                try
                {
                    String faceEgg = new JSONObject(body).getString("faceEgg");
                    message = new JSONObject(body).getString("message");
                    giftPopWinManager.refreshUIFaceEggBalance(faceEgg);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                mPresenter.sendTextMessage(message);
                mPresenter.sendBarrageMessage(SharePreUtils.getIsVUser(mActivity), message, SharePreUtils.getHeadImg(mActivity),
                        mPresenter.getmRecord().getEnterRoom4EH().getLevel(),
                        SharePreUtils.getNicName(mActivity));
                break;
            case NetInterfaceConstant.LiveC_getGroupRed:
                Logger.t(TAG).d("获取收群红包接口--> " + body);
                if (transElement != null)
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(body);
                        String from = jsonObject.getString("from");
                        String txId = (String) transElement.get("Id");
                        Logger.t(TAG).d("获取收群红包接口--> txId>>" + txId + " , from--> " + from);
                        getGroupRedResponse(body, from);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d(e.getMessage());
                    }
                }
                break;
            case "AVIMCMD_SEND_RED_PACKET":
                Logger.t(TAG).d("群发红包回调--> " + body);
//                mActivity.refreshText(" 你领取了"+nickname+"的红包", "", "消息", TXConstants.TEXT_TYPE, "", "", "", "");
                break;
            case NetInterfaceConstant.LiveC_roomAdmin:
                Logger.t(TAG).d("再次进入房间设置房管返回值--> " + body);
                setAdminNotify();
                break;
            case NetInterfaceConstant.LiveC_roomAdminList:
                String txId = (String) transElement.get("imId");
                List<ChosenAdminBean> adminLst = new Gson().fromJson(body, new TypeToken<List<ChosenAdminBean>>()
                {
                }.getType());
                mPresenter.ergodicRoomAdmin(adminLst, txId, mPresenter.getmRecord().getRoomId());
                break;
            case NetInterfaceConstant.LiveC_cancelRoomAdmin:
                Logger.t(TAG).d("再次进入房间取消房管返回值--> " + body);
                cancelAdminNotify();
                break;
            case NetInterfaceConstant.LiveC_anchorBaseInfo:

                final LookAnchorBean bean = new Gson().fromJson(body, LookAnchorBean.class);
                mLiveHostInfoPop = new LiveHostInfoPop(mActivity, bean, mPresenter.getmRecord().getEnterRoom4EH().getuId(), mPresenter.getmRecord().getHxChatRoomId());

                mLiveHostInfoPop.showPopupWindow(slBody, rlPopCover);

                if (SharePreUtils.getUId(mActivity).equals(mPresenter.getmRecord().getEnterRoom4EH().getuId()))
                {
                    //主播自己点击自己,隐藏按钮
                    mLiveHostInfoPop.hideSelfState();
                }
                else
                {
                    mLiveHostInfoPop.setOnFocusItemClickListener(new LiveHostInfoPop.OnFocusItemClickListener()
                    {
                        @Override
                        public void onFocusClick()
                        {
                            mPresenter.followHost();
                        }
                    });
                    mLiveHostInfoPop.setOnHelloItemClickListener(new LiveHostInfoPop.OnHelloItemClickListener()
                    {
                        @Override
                        public void onHelloClick()
                        {
                            if (mLiveHostInfoPop != null)
                                mLiveHostInfoPop.dismiss();
                            //弹出聊天界面
                            //保存到本地，聊天人信息
                            new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    EaseUser toEaseUser = new EaseUser(bean.getImuId());
                                    toEaseUser.setuId(bean.getuId());
                                    toEaseUser.setId(bean.getId());
                                    toEaseUser.setNickName(bean.getNicName());
                                    toEaseUser.setAvatar(bean.getUphUrl());
                                    toEaseUser.setLevel(bean.getLevel());
                                    toEaseUser.setSex(bean.getSex());
                                    toEaseUser.setAge(bean.getAge());
                                    toEaseUser.setRemark(bean.getRemark());
                                    toEaseUser.setIsVuser(bean.getIsVuser());
                                    HuanXinIMHelper.getInstance().saveContact(toEaseUser);
                                }
                            }).start();
                            EaseUser eUser = new EaseUser(mPresenter.getmRecord().getEnterRoom4EH().getAnchorImuId());
                            eUser.setNickName(mPresenter.getmRecord().getEnterRoom4EH().getNicName());
                            eUser.setuId(mPresenter.getmRecord().getEnterRoom4EH().getuId());
                            eUser.setAvatar(mPresenter.getmRecord().getEnterRoom4EH().getPhUrl());
                            LiveChatDialog liveChatDialog = LiveChatDialog.newInstance(eUser);
                            liveChatDialog.show(getSupportFragmentManager(), TAG);
                        }
                    });
                    mLiveHostInfoPop.setOnYuePaoItemClickListener(new LiveHostInfoPop.OnYuePaoItemClickListener()
                    {
                        @Override
                        public void onYuePaoClick()
                        {
                            mPresenter.addWish(mPresenter.getmRecord().getEnterRoom4EH().getuId());
                        }
                    });


                }
                break;
            case NetInterfaceConstant.AppointmentC_addWish:
                if (mLiveHostInfoPop != null)
                {
                    mLiveHostInfoPop.yueAuthorSuccess();
                }
                ToastUtils.showShort("加约会管家成功");
                break;
            case NetInterfaceConstant.SunMoonStarC_canInviteList:
                //可邀请游戏列表 刷新列表
                Logger.t(TAG).d("可邀请列表" + body);
                List<GameInviteBean> list = new Gson().fromJson(body, new TypeToken<List<GameInviteBean>>()
                {
                }.getType());
                ViewShareHelper.getInstance(mActivity, mPresenter).refreshGameInviteDialog(true, list,
                        -1, (String) transElement.get("type"), null, "");
//                ViewShareHelper.getInstance(mActivity,mPresenter).refreshGameInviteDialog(false,list,-1);
                break;
            case NetInterfaceConstant.SunMoonStarC_inviters:
                //被邀请游戏列表 刷新列表
                Logger.t(TAG).d("被邀请列表" + body);
                List<GameInviteBean> invitedList = new Gson().fromJson(body, new TypeToken<List<GameInviteBean>>()
                {
                }.getType());
                ViewShareHelper.getInstance(mActivity, mPresenter).refreshGameInviteDialog(false, invitedList,
                        -1, (String) transElement.get("type"), null, "");
                break;
            case NetInterfaceConstant.SunMoonStarC_sendInvitation:
                //发送邀请
                Logger.t(TAG).d("发送邀请" + body);
                ToastUtils.showShort("邀请已发出，等待对方接受..");
                try
                {
                    List<String> selectTxIdList = (List<String>) transElement.get("txId");
                    List<Integer> selectPosition = (List<Integer>) transElement.get("selectPosition");
                    Logger.t(TAG).d("邀请成功" + selectTxIdList.toString());
                    if (selectTxIdList.size() > 0)
                    {
                        Map<String, String> map = new HashMap<>();
                        map.put("nickName", SharePreUtils.getNicName(mActivity));
                        map.put("roomId", mPresenter.getmRecord().getRoomId());
                        map.put("toTxId", EamApplication.getInstance().getGsonInstance().toJson(selectTxIdList));
                        mPresenter.sendGroupCmd(TXConstants.AVIMCMD_GAME_INVITATION, new Gson().toJson(map), new ILiveCallBack()
                        {
                            @Override
                            public void onSuccess(Object data)
                            {
                                Logger.t(TAG).d("发送邀请信息成功");
                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg)
                            {
                                Logger.t(TAG).d("发送邀请信息失败" + errMsg + "|errCode==" + errCode);
                            }
                        });
                        ViewShareHelper.getInstance(mActivity, mPresenter).refreshGameInviteDialog(true, null, 0, null, selectPosition, "");
                    }
                } catch (Exception e)
                {
                    Logger.t(TAG).d("邀请解析错误" + e.getMessage());
                    e.printStackTrace();
                }
                break;
            case NetInterfaceConstant.SunMoonStarC_answerInvitation:
                //回应邀请 返回
                Logger.t(TAG).d("回应邀请" + body + transElement);
                String flg = (String) transElement.get("flg");
                String toTxId = (String) transElement.get("txId");
                int position = (int) transElement.get("position");
                if ("1".equals(flg) && position >= 0)
                {
                    Map<String, String> map = new HashMap<>();
                    map.put("nickName", SharePreUtils.getNicName(mActivity));
                    map.put("roomId", mPresenter.getmRecord().getRoomId());
                    map.put("toTxId", toTxId);
                    mPresenter.sendGroupCmd(TXConstants.AVIMCMD_GAME_INVITE_REJECT, new Gson().toJson(map), null);
                    ViewShareHelper.getInstance(mActivity, mPresenter).refreshGameInviteDialog(false, null, position, "refresh", null, "");
                }
                if ("0".equals(flg)) // 同意游戏邀请成功
                {
                    if (!TextUtils.isEmpty(toTxId))
                    {
                        Map<String, String> map = new HashMap<>();
                        map.put("nickName", SharePreUtils.getNicName(mActivity));
                        map.put("roomId", mPresenter.getmRecord().getRoomId());
                        map.put("toTxId", toTxId);
                        mPresenter.sendGroupCmd(TXConstants.AVIMCMD_GAME_INVITE_ACCEPT, new Gson().toJson(map), new ILiveCallBack()
                        {
                            @Override
                            public void onSuccess(Object data)
                            {
                                Logger.t(TAG).d("回应邀请发送消息成功");
                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg)
                            {
                                Logger.t(TAG).d("回应邀请发送消息失败" + errMsg);
                            }
                        });
                    }
                    ViewShareHelper.getInstance(mActivity, mPresenter).showGameStart(new Gson().fromJson(body, new TypeToken<StartGameBean>()
                    {
                    }.getType()), false, false);
                    ViewShareHelper.getInstance(mActivity, mPresenter).startGameHeart();
                }
                break;
            case NetInterfaceConstant.SunMoonStarC_joinGame:
                Logger.t(TAG).d("参与游戏>>" + body);
                ToastUtils.showShort("参与成功，等待其他用户参与...");
                StartGameBean gameBean = new Gson().fromJson(body, new TypeToken<StartGameBean>()
                {
                }.getType());
                ViewShareHelper.getInstance(mActivity, mPresenter).showGameStart(gameBean, false, true);
                ViewShareHelper.getInstance(mActivity, mPresenter).refreshGameStartBtn(true);
                break;
            case NetInterfaceConstant.SunMoonStarC_matchResult:
                Logger.t(TAG).d("游戏参与详情>>" + body);
                try
                {
                    JSONObject jsonObject = new JSONObject(body);
                    ViewShareHelper.getInstance(mActivity, mPresenter).goToGame(jsonObject.getString("url"), jsonObject.getString("battleId"),
                            jsonObject.getString("scheduled"), new ViewShareHelper.GameExitListener()
                            {
                                @Override
                                public void exit()
                                {
                                    gameStop();
                                }
                            });
                    gameStart();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case NetInterfaceConstant.GameC_shareH5:
                Logger.t(TAG).d("分享游戏" + body);
                try
                {
                    JSONObject jsonObject = new JSONObject(body);
                    String url = jsonObject.getString("url");
                    String shareIcon = jsonObject.getString("shareIcon");
                    String shareContent = jsonObject.getString("shareContent");
                    String shareTitle = jsonObject.getString("shareTitle");
                    String matchingId = (String) transElement.get("matchingId");
                    String score = (String) transElement.get("score");
                    String gameId = (String) transElement.get("gameId");
                    String isMyDynamics = (String) transElement.get("isMyDynamics");
                    ViewShareHelper.getInstance(mActivity, mPresenter).shareGame(url, shareIcon, shareContent, shareTitle, gameId, matchingId, score, isMyDynamics, rlPopCover);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case NetInterfaceConstant.SunMoonStarC_share:
                ToastUtils.showShort("分享成功");
                ViewShareHelper.getInstance(mActivity, mPresenter).dismissShareWin();
                break;
            case NetInterfaceConstant.SunMoonStarC_exitGame:
                Logger.t(TAG).d("退出游戏成功");
                break;
            case NetInterfaceConstant.SunMoonStarC_checkPopups:
                Logger.t(TAG).d("可以弹窗");
                showGameStart(null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onServerFailedCallback(String evt, String errorCode, String errorBody)
    {
        Logger.t(TAG).d(">>>>>>onError: " + evt + "<<<<<<<<<<<<<<<<<");
        switch (evt)
        {
            case NetInterfaceConstant.LiveC_sendMsg:
                //特殊处理错误码  此接口 在 LiveBasePresenter 已过滤 不提示 toast   ---yqh
                if (ErrorCodeTable.FORBID_ERROR.equals(errorCode))
                    ToastUtils.showShort("禁言状态中不可以发言哦~");
                if ("1".equals(errorCode) || "error".equals(errorCode))
                {
                    try
                    {
                        mPresenter.sendTXIMTextMessage(new JSONObject(errorBody).getString("message"));

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                Logger.t(TAG).d("接口>>>>>>>>>>>>>>>>>>>" + NetInterfaceConstant.LiveC_sendMsg + "触发失败回调");
                break;
            case NetInterfaceConstant.LiveC_sendBarrage:
                if (errorCode.equals("FACEEGG_INSUFFICIENT"))
                {
                    Logger.t(TAG).d(">>>>>>onError: 成功弹出dialog<<<<<<<<<<<<<<<<<");
                    inputTextMsgDialog.hideTextInput();
                    new CustomAlertDialog(mActivity)
                            .builder()
                            .setTitle("提示")
                            .setMsg("脸蛋不足，是否马上前往去充值?")
                            .setPositiveButton("前往购买", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    startActivityForResult(new Intent(mActivity, MyInfoBuyFaceEggActivity.class), EamCode4Result.reQ_MyInfoBuyFaceEggActivity);

                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // Logger.t(TAG).d("拒绝");
                                }
                            }).show();
                }
            case NetInterfaceConstant.LiveC_getGroupRed:
                Logger.t(TAG).d("已经领取或红包抢完返回信息---> " + errorBody);
                if (errorCode.equals("GROUPRED_OVER"))
                {
                    LiveGetRedPacketBean bean = new LiveGetRedPacketBean();
                    try
                    {
                        JSONObject jsonObject = new JSONObject(errorBody);
                        bean.setSex(jsonObject.getString("sex"));
                        bean.setLevel(jsonObject.getString("level"));
                        bean.setPhUrl(jsonObject.getString("phUrl"));
                        bean.setNicName(jsonObject.getString("nicName"));
                        bean.setAge(jsonObject.getString("age"));
                        bean.setIsVuser(jsonObject.getString("isVuser"));
                        LiveRedPacketDialog.showRedPacket(mActivity, bean);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            case NetInterfaceConstant.SunMoonStarC_matchResult:
                Logger.t(TAG).d("游戏参与详情>>" + errorBody);
                if ("GAME_OVER".equals(errorCode) || "COUNTERPART_ENOUGH".equals(errorCode))
                {
                    mPresenter.exitGame("0002");
                    ViewShareHelper.getInstance(mActivity, mPresenter).stopGameHeart();
                    ViewShareHelper.getInstance(mActivity, mPresenter).liveGameInviteDialogDismiss();
                }
                else
                {
                    StartGameBean startGameBean = new Gson().fromJson(errorBody, new TypeToken<StartGameBean>()
                    {
                    }.getType());
                    ViewShareHelper.getInstance(mActivity, mPresenter).showGameStart(startGameBean, false, false);
                }

                break;
            case NetInterfaceConstant.SunMoonStarC_joinGame:
                Logger.t(TAG).d("加入游戏错误>>" + errorCode);
                ViewShareHelper.getInstance(mActivity, mPresenter).refreshGameStartBtn(false);
                if ("FACEEGG_INSUFFICIENT".equals(errorCode))
                {
                    new CustomAlertDialog(mActivity)
                            .builder()
                            .setMsg("当前脸蛋不足,是否前往购买?")
                            .setTitle("提示")
                            .setPositiveButton("是", v ->
                                    startActivity(new Intent(mActivity, MyInfoBuyFaceEggActivity.class)))
                            .setNegativeButton("否", null).show();
                }
                else
                {
                    mPresenter.exitGame("0002");
                }
                break;
            case NetInterfaceConstant.SunMoonStarC_answerInvitation:
                Logger.t(TAG).d("回应邀请错误>>" + errorCode);
                //刷新被邀请列表
                mPresenter.getGameInviters("0002", mPresenter.getmRecord().getRoomId(), "0", "20", "refresh");
                break;
        }
        super.onServerFailedCallback(evt, errorCode, errorBody);
    }

    /*********************************end********************************/
    //endregion

    //region 礼物相关，应提取为一个独立模块，便于复用。设计出统一接口
    protected boolean stopAddAudiences = false;
    protected boolean stopAddMessageList = false;
//    private int audienceCount;


    public void popGiftWin()
    {
        giftPopWinManager.show(findViewById(R.id.rlRoomLayer));
    }

    public String getChosenGiftID()
    {
        return giftPopWinManager.getChosenGiftID();
    }

    public String getChosenGiftNum()
    {
        return giftPopWinManager.getChosenGiftNum();
    }

    public void dismissGiftWin()
    {
        giftPopWinManager.dismiss();
    }

    public boolean isShowingGiftWin()
    {
        return giftPopWinManager.isShowing();
    }

    private void setGiftListData(List<GiftBean> arrGift4EH, String balance, boolean isShow)
    {
        giftPopWinManager.refillGiftList(arrGift4EH, balance);
        if (isShow)
        {
            giftPopWinManager.show(rlRoomLayer);
        }
        giftPopWinManager.checkGiftSendBtn();
    }

    public GiftBean getChosenGiftBean()
    {
        return giftPopWinManager.getChosenGiftBean();
    }

    //endregion

    //region 直播房间业务控制，一般是响应腾讯消息的回调

    /**
     * 主播收到上麦请求处理方法
     *
     * @param identifier
     * @param nickname
     * @param responseResult
     */
    public void handleInviteRequest(String identifier, String nickname, String responseResult)
    {
        btnCloseConnectLive.setVisibility(View.GONE);
        switch (responseResult)
        {
            case "accept":
                hostSendMsg.setVisibility(View.VISIBLE);
                btnCloseConnectLive.setVisibility(View.VISIBLE);
                EamLogger.t(TAG).writeToDefaultFile("发送消息者ID>" + identifier + "昵称》" + nickname + "处理结果》" + responseResult);
                btnInviteTime.setVisibility(View.GONE);
                isShowInviteLive = true;
//                responseResult = "接受了你的连麦请求";
                break;
            case "refuse":
                if (mWillUserList.contains(identifier))
                {
                    mWillUserList.remove(identifier);
                }
                responseResult = "拒绝了你的连麦请求";
                break;
            case "acceptErr":
                if (mWillUserList.contains(identifier))
                {
                    mWillUserList.remove(identifier);
                }
                responseResult = "出了点小小的状况，上麦失败了，请重新发送邀请。";
                break;
        }
        if (!responseResult.equals("accept"))//接受不需要提示
            ToastUtils.showShort(nickname + " " + responseResult);
        resetGetSecurityButton(1);
    }

    /**
     * 显示连麦邀请窗口
     */
    public void showInviteDialog()
    {
        operInviteConnectView(connectViews, true);
        Logger.t(TAG).d("直播间信息：" + mPresenter.getmRecord().getEnterRoom4EH().toString() + " | connectPopup:" + connectPopup);
        if (connectPopup != null)
        {
            connectPopup.setLevelHeaderView(mPresenter.getmRecord().getEnterRoom4EH().getPhUrl(), mPresenter.getmRecord().getEnterRoom4EH().getIsVuser());
            connectPopup.setLevelView(mPresenter.getmRecord().getEnterRoom4EH().getAnchorLevel());
            connectPopup.setUserName(mPresenter.getmRecord().getEnterRoom4EH().getNicName());
            connectPopup.setSexAndAge(mPresenter.getmRecord().getEnterRoom4EH().getAnchorSex(), mPresenter.getmRecord().getEnterRoom4EH().getAnchorAge());
        }
//        lvHeaderImage.setHeadImageByUrl(mPresenter.getmRecord().getEnterRoom4EH().getPhurl());
//        lvHeaderImage.setLevel(mPresenter.getmRecord().getEnterRoom4EH().getAnchorLevel());
//        lvView.setLevel(mPresenter.getmRecord().getEnterRoom4EH().getAnchorLevel());
        //tvNicName.setText(mPresenter.getmRecord().getEnterRoom4EH().getNicName());
        //setSex(mPresenter.getmRecord().getEnterRoom4EH().getAnchorSex());
    }

    /**
     * 关闭邀请连麦弹窗
     *
     * @param isTimeOut 是否为超时取消
     */
    public void hideInviteDialog(boolean isTimeOut)
    {
        if (isTimeOut)
            ToastUtils.showShort("当前连麦请求已失效");
        else
        {
            ToastUtils.showShort("主播已取消连麦请求");
        }
        operInviteConnectView(connectViews, false);
    }

    /**
     * 接受连麦邀请
     */
    private void acceptInvite()
    {
        boolean lacksPermission = CommonUtils.isVoicePermission();
        boolean hasCameraPermission = CommonUtils.cameraIsCanUse();
        if (!hasCameraPermission || !lacksPermission)
        {
            ToastUtils.showShort("只有打开相机和麦克风权限，才能进行连麦哦~");
            return;
        }
        mPresenter.up2MemberVideo(new ILiveCallBack<ILVChangeRoleRes>()
        {
            @Override
            public void onSuccess(ILVChangeRoleRes data)
            {
                isShowInviteLive = true;
                Logger.t(TAG).d("上麦成功 相机：" + data.getCamRes() + " | mic：" + data.getMicRes());
                btnCloseConnectLive.setVisibility(View.VISIBLE);
                EamLogger.t(TAG).writeToDefaultFile("上麦成功显示关闭连麦按钮,相机：" + data.getCamRes() + " | mic：" + data.getMicRes());
                operInviteConnectView(connectViews, false);
                mPresenter.setBeautyData(3, 3);
                mPresenter.setBeautyCameraCallback();
                mPresenter.sendOnlineC2CCmd(TXConstants.AVIMCMD_MUlTI_JOIN, "", "u" + mPresenter.getmRecord().getRoomId(), new ILiveCallBack()
                {
                    @Override
                    public void onSuccess(Object data)
                    {
                        EamLogger.t(TAG).writeToDefaultFile("发送同意上麦消息成功");
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg)
                    {
                        EamLogger.t(TAG).writeToDefaultFile("发送同意上麦消息失败：module:" + module + ",errorCode:" + errCode + ",errMsg:" + errMsg);
                        Logger.t(TAG).d("发送同意上麦消息失败：module:" + module + ",errorCode:" + errCode + ",errMsg:" + errMsg);
                    }
                });
            }

            @Override
            public void onError(String module, int errCode, String errMsg)
            {
                ToastUtils.showLong("发生了一点小状况，连麦失败了，等待主播重新邀请哟~");
                operInviteConnectView(connectViews, false);
                btnCloseConnectLive.setVisibility(View.GONE);
//                mPresenter.sendC2CCmd(TXConstants.AVIMCMD_MUlTI_MEMBER_UPROLE_FAIL_NOTIFY, "", "u" + mPresenter.getmRecord().getRoomId());
                mPresenter.sendOnlineC2CCmd(TXConstants.AVIMCMD_MUlTI_MEMBER_UPROLE_FAIL_NOTIFY, "", "u" + mPresenter.getmRecord().getRoomId(), null);
                EamLogger.t(TAG).writeToDefaultFile("上麦失败：module:" + module + ",errorCode:" + errCode + ",errMsg:" + errMsg);
                Logger.t(TAG).d("上麦失败：module:" + module + ",errorCode:" + errCode + ",errMsg:" + errMsg);
            }
        });
    }

    /**
     * 控制连麦dialog显示 dialog初始化监听也写在这里
     *
     * @param oprView
     * @param isShow  是否显示
     */
    private void operInviteConnectView(View oprView, boolean isShow)
    {
        if (connectPopup == null)
        {
            connectPopup = new ConnectPopup(mActivity, new ConnectPopup.ConnectClickListener()
            {
                @Override
                public void onClick(String tag)
                {
                    switch (tag)
                    {
                        case "yes":
                            Logger.t("===========").d(CommonUtils.getLock(TAG + "1") + "");
                            acceptInvite();
                            break;
                        case "no":
                            refuseInvite();
                            break;
                    }
                }
            });
        }

        if (isShow)
        {
            if (!connectPopup.isShowing())
            {
                Logger.t("============").d("显示方法执行了");
                connectPopup.showAtLocation(findViewById(R.id.rlRoomLayer));

                if (inputTextMsgDialog != null)
                    inputTextMsgDialog.hideTextInput();
            }
        }
        else
        {
            if (connectPopup.isShowing())
                connectPopup.dismiss();
        }


//        if (oprView == null)
//            throw new NullPointerException("操作view为null了");
//        if (isShow)
//        {
//            Animation mShowAnim = AnimationUtils.loadAnimation(this, R.anim.invite_connect_in);
//            if (oprView.getVisibility() == View.INVISIBLE)
//            {
//                oprView.setVisibility(View.VISIBLE);
//                oprView.startAnimation(mShowAnim);
//            }
//        }
//        else
//        {
//            Animation mHiddenAnim = AnimationUtils.loadAnimation(this, R.anim.invite_connect_out);
//            mHiddenAnim.setAnimationListener(new Animation.AnimationListener()
//            {
//                @Override
//                public void onAnimationStart(Animation animation)
//                {
//                      new Handler().postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                CommonUtils.removeClickLock(TAG + "1");
//            }
//        }, 500);
//    }
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation)
//                {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation)
//                {
//
//                }
//            });
//            if (oprView.getVisibility() == View.VISIBLE)
//            {
//                oprView.startAnimation(mHiddenAnim);
//                oprView.setVisibility(View.INVISIBLE);
//            }
//        }
    }

    /**
     * 拒绝 连麦
     */
    private void refuseInvite()
    {
        isShowInviteLive = false;
        hideCloseInviteBtn();
//        mPresenter.sendC2CCmd(TXConstants.AVIMCMD_MUlTI_REFUSE, "", "u" + mPresenter.getmRecord().getRoomId());
        mPresenter.sendOnlineC2CCmd(TXConstants.AVIMCMD_MUlTI_REFUSE, "", "u" + mPresenter.getmRecord().getRoomId(), null);
        operInviteConnectView(connectViews, false);
    }

    /**
     * 关注主播
     *
     * @param id    关注人的id
     * @param name  关注人的昵称
     * @param level 关注人的等级
     */
    public void focusHost(String id, String name, String level)
    {
        String htmlStr = String.format("<font color=%s>%s</font>", TXConstants.ENTER_ROOM_MSG_CONTENT_COLOR, "关注了主播");
        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
        refreshLiveMsgBean.setText("关注了主播");
        refreshLiveMsgBean.setId(id);
        refreshLiveMsgBean.setName(name);
        refreshLiveMsgBean.setType(LiveMsgType.FocusHost);
        refreshLiveMsgBean.setLevel(level);
        refreshLiveMsgBean.setLiveLevelState("1");
        refreshText(refreshLiveMsgBean);
        Logger.t(TAG).d("关注结束");
    }

    /**
     * 设置房管通知
     */
    public void setAdminNotify()
    {
        CustomAlertDialog dialog = new CustomAlertDialog(mActivity)
                .builder()
                .setTitle("提示")
                .setMsg("你已经被主播任命为房管!")
                .setMsgColor(ContextCompat.getColor(mActivity, R.color.C0313))
                .setPositiveButton("我知道了", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
        dialog.show();
    }

    /**
     * 取消房管通知
     */
    public void cancelAdminNotify()
    {
        CustomAlertDialog dialog = new CustomAlertDialog(mActivity)
                .builder()
                .setTitle("提示")
                .setMsg("你已经被主播取消了房管!")
                .setMsgColor(ContextCompat.getColor(mActivity, R.color.C0313))
                .setPositiveButton("我知道了", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
        dialog.show();
    }

    /**
     * 星光活动     改变星光值和排名
     *
     * @param starChartNum 星光值
     * @param ranking      排名
     */
    public void setStarChartNum(String starChartNum, String ranking)
    {
        if (TextUtils.isEmpty(starChartNum) || TextUtils.isEmpty(ranking))
            return;
        try
        {
            int oldStarChart = Integer.parseInt(tvStarChartCount.getText().toString().replace("星光值: ", "").trim());
            int newStarChart = Integer.parseInt(starChartNum);
            if (oldStarChart > newStarChart)
                return;
        } catch (NumberFormatException e)
        {
            Logger.t(TAG).d("star chart format err" + e.getMessage());
        }
        if (TextUtils.equals("0", ranking))
        {
            tvStarChartTop.setText("星光榜: 未上榜");
        }
        else
        {
            tvStarChartTop.setText("星光榜: " + ranking);
        }
        if (tvStarChartCount != null)
        {
            tvStarChartCount.setText("星光值: " + starChartNum);
        }
        if (TextUtils.equals("1", ranking))
        {
            if (tvStarShow != null && tvStarShow.getVisibility() == View.VISIBLE)
            {
                tvStarShow.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 看直播时恩赐了一枚免费流星
     */
    public void showFreeStarNotify(String msg, String imagUrl)
    {
        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
        Map<String, Object> transElement = new HashMap<>();
        transElement.put("isShow", "0");
        callServerSilence(NetInterfaceConstant.LiveC_giftList, transElement, null, reqMap);
        if (!ViewShareHelper.getInstance(mActivity, mPresenter).isGameStart())
        {
            if (mActivity != null)
            {
                new ObtainStarDialog(mActivity)
                        .builder()
                        .setShowGiftImage(imagUrl)
                        .setTvGiftInfo(msg)
                        .show();
            }
        }
    }

    /**
     * 显示主播星光棒排行
     */
    private void showStarPopWindow()
    {
        starPopWindow = new StarPopWindow(mActivity, mPresenter.getmRecord().getRoomId(), mPresenter.getmRecord().getEnterRoom4EH().getIsVuser());
        starPopWindow.show(rivHostAvatar, rlPopCover);
    }

    /**
     * 主播与上一名的星光值差距
     */
    public void setDifferMsg(String msg, String starNum, String ranking)
    {
        if (!oldRanking.equals(ranking) && !TextUtils.equals("", oldRanking))
        {
            setStarChartNum(starNum, ranking);
        }
        oldRanking = ranking;
        if (mActivity != null && !mActivity.isFinishing())
        {
            Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.slide_out_up);
            tvStarShow.startAnimation(animation);
        }
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (tvStarShow.getVisibility() == View.GONE)
                {
                    tvStarShow.setVisibility(View.VISIBLE);
                }
                tvStarShow.setText(msg);
                if (mActivity != null && !mActivity.isFinishing())
                {
                    Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.slide_in_bottom);
                    tvStarShow.startAnimation(animation);
                }
            }
        }, 500);
    }


    /**
     * 成员状态变更 有真实成员加入
     *
     * @param id      用户id 六位数字
     * @param name    昵称
     * @param headImg 头像url
     */
    public void memberJoin(final String id, String name, String headImg, String level, String sign)
    {
        ViewShareHelper.getInstance(mActivity, mPresenter).memberJoin(id, name, headImg, level, sign);
    }

    /**
     * 有机器人成员加入
     *
     * @param id      腾讯id
     * @param name    昵称
     * @param headImg 头像url
     */
    public void fakeMemberJoin(String id, String name, String headImg, String userLevel)
    {
        ViewShareHelper.getInstance(mActivity, mPresenter).fakeMemberJoin(id, name, headImg, userLevel);
    }

    /**
     * 有成员退出直播间
     *
     * @param id
     * @param name
     */
    public void memberQuit(String id, String name)
    {
        ViewShareHelper.getInstance(mActivity, mPresenter).memberQuit(id, name, "");
    }

    /**
     * 假用户退出
     *
     * @param id
     * @param name
     * @param headImg
     */
    public void fakeMemberQuit(String id, String name, String headImg)
    {
        ViewShareHelper.getInstance(mActivity, mPresenter).fakeMemberQuit(id, name, headImg);
    }

    /**
     * 主播离开
     *
     * @param id
     * @param name
     */
    public void hostLeave(String id, String name)
    {
        memberVideoStreamPause();
        Logger.t(TAG).d("hostLeave,id:" + id + ",hostId:" + mPresenter.getmRecord().getRoomId() + "name:" + name);
    }

    /**
     * 主播回来
     *
     * @param id
     * @param name
     */
    public void hostBack(String id, String name)
    {
        memberVideoStreamResume();
        Logger.t(TAG).d("hostBack,id:" + id + ",name:" + name);
    }

    /**
     * 主播禁言
     *
     * @param id    被禁言用户id
     * @param name  被禁言用户昵称
     * @param txId  被禁言用户txid
     * @param level 被禁言用户等级
     */
    public void hostShutUpOff(String id, String name, String txId, String level)
    {
        Logger.t(TAG).d("被主播禁言了 " + id + "|" + name + "|" + txId);
        String htmlStr = String.format("<font color=%s>%s</font>",
                TXConstants.ENTER_ROOM_SYSTEM_COLOR, "被主播禁言");
        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
        refreshLiveMsgBean.setText("被主播禁言");
        refreshLiveMsgBean.setId(txId.split("u")[1]);
        refreshLiveMsgBean.setName(TextUtils.isEmpty(name) ? id : name);
        refreshLiveMsgBean.setType(LiveMsgType.ShutUp);
        refreshLiveMsgBean.setLiveLevelState("2");
        refreshText(refreshLiveMsgBean);
    }

    /**
     * 主播解禁言
     *
     * @param id    被禁言用户id
     * @param name  被禁言用户昵称
     * @param txId  被禁言用户txid
     * @param level 被禁言用户等级
     */
    public void hostShutUpOn(String id, String name, String txId, String level)
    {
        Logger.t(TAG).d("被主播解除禁言了 " + id + "|" + name + "|" + txId);
//        String htmlStr = String.format("<font color=%s>%s</font>",
//                TXConstants.ENTER_ROOM_SYSTEM_COLOR, "被主播解除禁言");
        String htmlStr = "被主播解除禁言";
        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
        refreshLiveMsgBean.setText(htmlStr);
        refreshLiveMsgBean.setId(txId.split("u")[1]);
        refreshLiveMsgBean.setName(TextUtils.isEmpty(name) ? id : name);
        refreshLiveMsgBean.setType(LiveMsgType.NotShutUp);
        refreshLiveMsgBean.setLiveLevelState("2");
        refreshText(refreshLiveMsgBean);
    }

    /**
     * 管理员禁言用户
     *
     * @param id    被管理员禁言用户id
     * @param name  被管理员禁言用户昵称
     * @param txId  被管理员禁言用户txid
     * @param level 被管理员禁言用户等级
     */
    public void adminShutUpOff(String id, String name, String txId, String level)
    {
        Logger.t(TAG).d("被管理员禁言了 " + id + "|" + name + "|" + txId);
//        String htmlStr = String.format("<font color=%s>%s</font>",
//                TXConstants.ENTER_ROOM_SYSTEM_COLOR, "被房管禁言");
        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
        refreshLiveMsgBean.setText("被房管禁言");
//        refreshLiveMsgBean.setId(txId.split("u")[1]);
        refreshLiveMsgBean.setId(txId);
        refreshLiveMsgBean.setName(TextUtils.isEmpty(name) ? id : name);
        refreshLiveMsgBean.setType(LiveMsgType.ShutUp);
        refreshLiveMsgBean.setLiveLevelState("2");
        refreshText(refreshLiveMsgBean);
    }

    /**
     * 管理员解除禁言用户
     *
     * @param id    被管理员解禁言用户id
     * @param name  被管理员解禁言用户昵称
     * @param txId  被管理员解禁言用户txid
     * @param level 被管理员解禁言用户等级
     */
    public void adminShutUpOn(String id, String name, String txId, String level)
    {
        Logger.t(TAG).d("被管理员解除禁言了 " + id + "|" + name + "|" + txId);
//        String htmlStr = String.format("<font color=%s>%s</font>",
//                TXConstants.ENTER_ROOM_SYSTEM_COLOR, "被房管解除禁言");
        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
        refreshLiveMsgBean.setText("被房管解除禁言");
//        refreshLiveMsgBean.setId(txId.split("u")[1]);
        refreshLiveMsgBean.setId(txId);
        refreshLiveMsgBean.setName(TextUtils.isEmpty(name) ? id : name);
        refreshLiveMsgBean.setType(LiveMsgType.NotShutUp);
        refreshLiveMsgBean.setLiveLevelState("2");
        refreshText(refreshLiveMsgBean);
    }

    /**
     * @param gift    礼物信息json
     * @param name    礼物名称
     * @param id      礼物id
     * @param faceUrl 用户头像
     * @param level   用户等级
     * @param isVuser 是否是大V
     */
    public void refreshGift(final String gift, final String name, final String id, final String faceUrl, final String level, final String isVuser)
    {
        Observable.create(new ObservableOnSubscribe<SlidGift.GiftRecord>()
        {
            @Override
            public void subscribe(ObservableEmitter<SlidGift.GiftRecord> e) throws Exception
            {
                //倒一下数据。。。 方便使用;
                SlidGift.GiftData eh = EamApplication.getInstance().getGsonInstance().fromJson(gift, SlidGift.GiftData.class);
                SlidGift.GiftRecord record = new SlidGift.GiftRecord();
                record.gid = id + eh.getGift().getGId();// 动画唯一识别id，
                record.name = name;
                record.uid = id;
                record.level = level;
                record.usrIcon = faceUrl;
                record.disc = "送了" + eh.getNumber() + "个" + eh.getGift().getGName() + "~";
                record.giftImg = eh.getGift().getGUrl();
                record.giftNumber = eh.getNumber();
                record.giftName = eh.getGift().getGName();
                record.giftType = eh.getGift().getGType();
                record.giftId = eh.getGift().getGId();
                record.mealTotal = eh.getMealTotal();
                record.giftCountTotal = eh.getGift().getCountTotal();
                record.vUser = isVuser;
                record.star = eh.getStar();
                record.ranking = eh.getRanking();
                e.onNext(record);
            }
        }).subscribeOn(Schedulers.computation())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivity.<SlidGift.GiftRecord>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<SlidGift.GiftRecord>()
                {
                    @Override
                    public void accept(SlidGift.GiftRecord record) throws Exception
                    {
                        RefreshLiveMsgBean liveMsgBean = new RefreshLiveMsgBean();
                        liveMsgBean.setText(record.disc);
                        liveMsgBean.setId(id);
                        liveMsgBean.setName(name);
                        liveMsgBean.setType(LiveMsgType.SmallGift);
                        liveMsgBean.setgName(record.giftName);
                        liveMsgBean.setgUrl(record.giftImg);
                        liveMsgBean.setGiftNum(record.giftNumber);
                        liveMsgBean.setgType(record.giftType);
                        liveMsgBean.setLevel(level);
                        liveMsgBean.setLiveLevelState("1");
                        /*是否大礼物，0：否，1是*/
                        if (TextUtils.equals(record.giftType, "0"))
                        {
                            //   ViewShareHelper.getInstance(mActivity, mPresenter).putGift(record);
                            giftManager.put(record);
                        }
                        else
                        {
                            liveMsgBean.setType(LiveMsgType.BigGift);
                            record.gid = record.giftId;// "gId":"0009"
                            giftLargeManager.put(record);
                        }
                        refreshText(liveMsgBean);
                        setMealTicket(record.mealTotal);
                        setStarChartNum(record.star, record.ranking);
                        Logger.t(TAG).d("ranking >>" + record.star + ",star >>" + record.star);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable e) throws Exception
                    {
                        Logger.t(TAG).d("礼物解析失败》" + e.getMessage());
                        EamLogger.t(TAG).writeToDefaultFile("礼物解析失败》" + e.getMessage());
                    }
                });
    }
    //endregion

    //region override 父类接口方法
    @Override
    protected void onJoinRoomSuccess()
    {
        super.onJoinRoomSuccess();
        checkLocalGiftExists();
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_EnterLive);
        msgMap.put(TXConstants.CMD_PARAM, "");
        if (mPresenter != null)
        {
            mPresenter.sendTXIMMessage(msgMap, "join_room");
            if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_MEMBER)
                SharePreUtils.setPreGroupId(mActivity, mPresenter.getmRecord().getRoomId());
        }
    }

    @Override
    public void onPendingJoinRoom()
    {
        super.onPendingJoinRoom();
        quitPreRoom();
    }

    @Override
    public void onPendingCreateRoom()
    {
        super.onPendingCreateRoom();
        quitPreRoom();
    }

    @Override
    protected void onMemberExitRoomSuccess(ExitRoomType type)
    {
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_ExitLive);
        msgMap.put(TXConstants.CMD_PARAM, "");
        if (mPresenter != null)
            mPresenter.sendTXIMMessage(msgMap, "member_exit_room");
    }


    @Override
    public void hostVideoStreamResume()
    {
        super.hostVideoStreamResume();
        if (mPresenter != null)
        {
            mPresenter.sendGroupCmd(TXConstants.AVIMCMD_Host_Back, "", null);
            mPresenter.switchHostStatus(mPresenter.getmRecord().getRoomId(), "0", null);
        }
    }

    @Override
    public void hostVideoStreamPause()
    {
        super.hostVideoStreamPause();
        if (mPresenter != null)
        {
            mPresenter.sendGroupCmd(TXConstants.AVIMCMD_Host_Leave, "", null);
            mPresenter.switchHostStatus(mPresenter.getmRecord().getRoomId(), "1", null);
            Logger.t(TAG).d("发送主播离开");
        }
    }

    @Override
    protected void gameStart()
    {
        super.gameStart();
        Logger.t(TAG).d("游戏开始");
        if (lavStar != null && lavStar.getVisibility() == View.VISIBLE)
        {
            lavStar.pauseAnimation();
        }
    }

    @Override
    protected void gameStop()
    {
        super.gameStop();
        Logger.t(TAG).d("游戏结束");
        if (lavStar != null && lavStar.getVisibility() == View.VISIBLE)
        {
            lavStar.playAnimation();
        }
    }

    //endregion

    @Optional
    @OnClick({R.id.btn_refuse, R.id.btn_accept, R.id.liv_house_manager, R.id.llMealTicketGroup,
            R.id.btnSendMessage, R.id.btnReport, R.id.btnGift, R.id.btnShare, R.id.btnClear, R.id.btnCamera,
            R.id.btnCleared_clear, R.id.tvFollow, R.id.fl_booty_call, R.id.rivHostAvatar, R.id.btn_message,
            R.id.itCloseLive, R.id.btn_invite_time, R.id.btnCloseConnectLive, R.id.btnPackage, R.id.btn_beauty,
            R.id.view_game, R.id.lav_star})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_refuse:
                refuseInvite();
                break;
            case R.id.btn_accept:
                Logger.t("===========").d(CommonUtils.getLock(TAG + "1") + "");
                if (CommonUtils.getLock(TAG + "1"))
                    return;
                CommonUtils.clickLock(TAG + "1");
                acceptInvite();
                break;
            case R.id.liv_house_manager:
            {
                if (CommonUtils.isFastDoubleClick())
                    return;
                Logger.t(TAG).d("群主的等级--> " + mPresenter.getmRecord().getEnterRoom4EH().getLevel() + " , " + SharePreUtils.getLevel(mActivity));
                NetHelper.checkPrivilegeToLevel(mActivity, "004", new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        Logger.t(TAG).d("房管功能调取查询特权对应等级信息--> " + response);
                        Intent intent = new Intent(mActivity, LiveHouseManageAct.class);
                        intent.putExtra("roomId", mPresenter.getmRecord().getRoomId());
                        intent.putExtra("chatRoomId", "");
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        ToastUtils.showShort("LV" + code + "开启房管功能");
                    }
                });
                break;
            }
            case R.id.llMealTicketGroup://查看榜单
            {
                if ("0".equals(switchStatusMap.get("ranking")))
                {
                    Intent intent = new Intent(mActivity, LRankingAct.class);
                    intent.putExtra("roomId", mPresenter.getmRecord().getRoomId());
                    intent.putExtra("isShowThisTime", "0".equals(switchStatusMap.get("thisTime")));
                    mActivity.startActivity(intent);
                }
                break;
            }
            case R.id.lav_star://查看星光榜
            {
                //显示pop星光排行榜
                if ("0".equals(switchStatusMap.get("star")))
                {
                    showStarPopWindow();
                }
                break;
            }
            case R.id.tvFollow://关注
                if (CommonUtils.isFastDoubleClick())
                    return;
                mPresenter.followHost();
                break;
            case R.id.fl_booty_call://去约炮
                if (!"0".equals(switchStatusMap.get("receive")))
                {
                    break;
                }
                ViewShareHelper.getInstance(mActivity, mPresenter).stopBootyCallMsgAni();
                if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                {
                    Intent myDateIntent = new Intent(mActivity, MyDateAct.class);
                    myDateIntent.putExtra("toUId", mPresenter.getmRecord().getEnterRoom4EH().getuId());
                    myDateIntent.putExtra("currentPage", 1);
                    myDateIntent.putExtra("chatRoomId", mPresenter.getmRecord().getRoomId());
                    mActivity.startActivity(myDateIntent);
                }
                else
                {
                    if (EamApplication.getInstance().controlUInfo.size() == 2)
                    {
                        if (EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()) != null)
                        {
                            EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()).finish();
                            EamApplication.getInstance().controlUInfo.clear();
                        }
                    }
                    Intent userinfoIntent = new Intent(mActivity, CNewUserInfoAct.class);
                    userinfoIntent.putExtra("toUId", mPresenter.getmRecord().getEnterRoom4EH().getuId());
                    userinfoIntent.putExtra("roomId", mPresenter.getmRecord().getRoomId());
                    userinfoIntent.putExtra("chatRoomId", mPresenter.getmRecord().getRoomId());
                    userinfoIntent.putExtra("fromAct", "LiveRoom");
                    userinfoIntent.putExtra("currentPage", 2);
                    mActivity.startActivity(userinfoIntent);
                }
                break;
            case R.id.rivHostAvatar:
                showHostInfo();
                break;
            case R.id.btnSendMessage:
                if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                    showFunctionLayout();
                else
                    showSendLayout();
                break;
            case R.id.btn_invite_time:
                new CustomAlertDialog(mActivity).builder()
                        .setTitle("连麦提示")
                        .setMsg("您确认取消连麦？")
                        .setPositiveButton("确定", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
//                                mPresenter.sendC2CCmd(TXConstants.AVIMCMD_MUlTI_HOST_CLOSE_INVITE, "", "u" + id4ContactMemberBean.getId());
                                mPresenter.sendOnlineC2CCmd(TXConstants.AVIMCMD_MUlTI_HOST_CLOSE_INVITE, "", "u" + id4ContactMemberBean.getId(), null);
                                hostSendMsg.setVisibility(View.VISIBLE);
                                btnInviteTime.setVisibility(View.GONE);
                                resetGetSecurityButton(1);
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        }).setCancelable(true)
                        .show();
                break;
            case R.id.btnCloseConnectLive:
                String msg, role, reason;
                if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                {
                    msg = "确定与此用户取消连麦?";
                    role = "host";
                    reason = "0";
                }
                else
                {
                    msg = "确定与主播取消连麦?";
                    role = "member";
                    reason = "1";
                }
                final String tempRole = role;
                final String tempReason = reason;
                new CustomAlertDialog(mActivity).builder()
                        .setTitle("连麦提示")
                        .setMsg(msg)
                        .setPositiveButton("确定", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                closeInviteLive(tempRole, tempReason);
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        }).setCancelable(true)
                        .show();
                break;
            case R.id.btnReport:
                Intent reportIntent = new Intent(mActivity, ReportFoulsRoomAct.class);
                reportIntent.putExtra("roomId", mPresenter.getmRecord().getRoomId());
                mActivity.startActivity(reportIntent);
                break;
            case R.id.btnGift:
                if (null == arrGift4EH)
                {
                    arrGift4EH = new ArrayList<>();
                    Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mActivity);
                    Map<String, Object> transElement = new HashMap<>();
                    transElement.put("isShow", "1");
                    callServerSilence(NetInterfaceConstant.LiveC_giftList, transElement, null, reqMap);
                }
                else
                {
                    popGiftWin();
                }
                break;
            case R.id.btnShare:
                ViewShareHelper.getInstance(mActivity, mPresenter).popShareWin(mActivity, enterRoom4EH, mPresenter.getmRecord(), slBody, rlPopCover);
                break;
            case R.id.btnClear:
                showRoomUI(false);
                break;
            case R.id.btnCleared_clear:
                showRoomUI(true);
                break;
            case R.id.btnCamera:
                bCameraOn = !bCameraOn;
                // 切换摄像头 0 前  1 后
                ILiveRoomManager.getInstance().switchCamera(ILiveRoomManager.getInstance().getCurCameraId() == 0 ? 1 : 0);
                break;
            case R.id.btn_message:
                if (CommonUtils.isFastDoubleClick())
                    return;
                removeMsgListener();
                HuanXinIMHelper.getInstance().getUnreadChatMsgNum(HuanXinIMHelper.NORMAL_CHAT_TYPE, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        Map<String, Integer> map = EamApplication.getInstance().getGsonInstance().fromJson(response, new TypeToken<Map<String, Integer>>()
                        {
                        }.getType());
                        int allUnreadNum = map.get("chat");
                        if (allUnreadNum > 0)
                            liveNewMsgDialog.setHasNewChat(true);

                        HuanXinIMHelper.getInstance().getUnreadChatMsgNum(HuanXinIMHelper.HELLO_CHAT_TYPE, new ICommonOperateListener()
                        {
                            @Override
                            public void onSuccess(String response)
                            {
                                Map<String, Integer> map = EamApplication.getInstance().getGsonInstance().fromJson(response, new TypeToken<Map<String, Integer>>()
                                {
                                }.getType());
                                int allUnreadNum = map.get("hello");
                                if (allUnreadNum > 0)
                                    liveNewMsgDialog.setHasNewSayHello(true);
                                if (!liveNewMsgDialog.isAdded() && !liveNewMsgDialog.isVisible() && !liveNewMsgDialog.isRemoving())
                                    liveNewMsgDialog.show(getSupportFragmentManager(), TAG);
                                btMessageDian.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(String code, String msg)
                            {

                            }
                        });
                    }

                    @Override
                    public void onError(String code, String msg)
                    {

                    }
                });
                break;
            case R.id.btnPackage:
                if (CommonUtils.isFastDoubleClick())
                    return;
                NetHelper.checkPrivilegeToLevel(mActivity, "010", new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        Logger.t(TAG).d("主播红包调取查询特权对应等级信息--> " + response);
                        Intent intent1 = new Intent(mActivity, LiveSendPacketAct.class);
                        intent1.putExtra("roomId", mPresenter.getmRecord().getRoomId());
                        startActivityForResult(intent1, EamCode4Result.reQ_RED_REQUEST_CODE);
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        ToastUtils.showShort("LV" + code + "开启发送群红包功能");
                    }
                });
                break;
            case R.id.btn_beauty:
                changeBeautyLayout();
                break;
            case R.id.view_game:
                ViewShareHelper.getInstance(mActivity, mPresenter).showGameInviteDialog();
                ViewShareHelper.getInstance(mActivity, mPresenter).stopGameAnim();
                break;
//            case R.id.fl_game:
//                ViewShareHelper.getInstance(mActivity,mPresenter).goToGame("http://192.168.10.223/star/ ",mActivity);
//                break;
            default:
                break;
        }
    }

    /**
     * change the beauty view shown
     */
    private void changeBeautyLayout()
    {
        if (llBeauty.getVisibility() == View.VISIBLE)
            llBeauty.setVisibility(View.GONE);
        else
            llBeauty.setVisibility(View.VISIBLE);
    }


    //region--------------------------------------------------------------------更改UI函数----------------------------------------------------------------------------------------------------------------
    //显示聊天输入布局
    public void showSendLayout()
    {
        if (inputTextMsgDialog == null)
        {
            inputTextMsgDialog = new InputTextMsgDialog(mActivity, R.style.inputDialog
                    , mPresenter.getmRecord().getEnterRoom4EH().getBarrage().equals("1") ? true : false
                    , mPresenter.getmRecord().getModeOfRoom());
            WindowManager.LayoutParams lp = inputTextMsgDialog.getWindow().getAttributes();
            lp.width = CommonUtils.getScreenSize(mActivity).width;
            inputTextMsgDialog.getWindow().setAttributes(lp);
            inputTextMsgDialog.setIOnSendMsgListener(new InputTextMsgDialog.IOnSendMsgListener()
            {
                @Override
                public void onClick(View view, String viewName, final String msg)
                {
                    if ("sendText".equals(viewName))
                    {
                        if (!TextUtils.isEmpty(msg))
                        {
                            mPresenter.sendTextMessage(msg);
                        }
                        else
                        {
                            ToastUtils.showShort("请输入聊天内容");
                        }
                    }
                    else if ("sendBarrageText".equals(viewName))
                    {
                        if (!TextUtils.isEmpty(msg))
                        {
                            if (ViewShareHelper.liveMySelfRole != LiveRecord.ROOM_MODE_HOST)
                            {
                                mPresenter.getUserShutUpStatus(mPresenter.getmRecord().getRoomId(), SharePreUtils.getTlsName(mActivity), new LiveRoomPre1.IsShutUpCallBack()
                                {
                                    @Override
                                    public void isShutUpCallBack(boolean isShutUp)
                                    {
                                        if (!isShutUp)
                                        {
//                                            Map<String, Object> transElement = new HashMap<>();
//                                            transElement.put("msg", msg);
//                                            Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
//                                            reqParamMap.put(ConstCodeTable.roomId, mPresenter.getmRecord().getRoomid());
//                                            reqParamMap.put(ConstCodeTable.context, msg);
//                                            callServerSilence(NetInterfaceConstant.LiveC_sendBarrage, transElement, "1", reqParamMap);
                                            mPresenter.sendBarrage(msg);
                                        }
                                        else
                                        {
                                            ToastUtils.showShort("你已经被禁言");
                                        }
                                    }

                                    @Override
                                    public void requestNetError(Call call, Exception e, String resourse)
                                    {
                                        Logger.t(TAG).d("获取禁言状态错误》》》》" + resourse);
                                    }
                                });
                            }
                        }
                        else
                        {
                            ToastUtils.showShort("请输入聊天内容");
                        }
                    }
                }

                @Override
                public void onClickRoot()
                {

                }

                @Override
                public void onInputHasShowOrHide()
                {
                    new Handler(mActivity.getMainLooper()).postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ViewShareHelper.getInstance(mActivity, mPresenter).notifyChatListDataSetChanged(null);
                            //chatMsgListAdapter.notifyDataChanged(null);
                        }
                    }, 100);
                }
            });

            inputTextMsgDialog.setViewChangeListener(new InputTextMsgDialog.ViewChangeListener()
            {
                @Override
                public void toggleToOn()
                {
                    mPresenter.switchBarrage(true);
                }

                @Override
                public void toggleToOff()
                {
                    mPresenter.switchBarrage(false);
                }
            });

            NetHelper.checkPrivilegeToLevel(mActivity, "003", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {

                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });
        }
        if (inputTextMsgDialog.isShowing())
            inputTextMsgDialog.dismiss();
        else
        {
            slBody.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
            inputTextMsgDialog.show();
        }
    }

    //显示/隐藏主播功能布局
    private void showFunctionLayout()
    {
        if (mPopupWindow == null)
        {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.act_room_live_main_function, null);
            TextView sentextView = (TextView) view.findViewById(R.id.tv_room_live_mainfunction_sendtext);
            TextView packetView = (TextView) view.findViewById(R.id.tv_room_live_mainfunction_packet);
            TextView linkView = (TextView) view.findViewById(R.id.tv_room_live_mainfunction_link);
            sentextView.setCompoundDrawablePadding(5);
            packetView.setCompoundDrawablePadding(5);
            linkView.setCompoundDrawablePadding(5);
            View.OnClickListener listener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    switch (view.getId())
                    {
                        case R.id.tv_room_live_mainfunction_sendtext:
                            showSendLayout();
                            showFunctionLayout();
                            break;
                        case R.id.tv_room_live_mainfunction_packet:
                            if (CommonUtils.isFastDoubleClick())
                                return;
                            NetHelper.checkPrivilegeToLevel(mActivity, "010", new ICommonOperateListener()
                            {
                                @Override
                                public void onSuccess(String response)
                                {
                                    Logger.t(TAG).d("主播红包调取查询特权对应等级信息--> " + response);
                                    Intent intent1 = new Intent(mActivity, LiveSendPacketAct.class);
                                    intent1.putExtra("roomId", mPresenter.getmRecord().getRoomId());
                                    startActivityForResult(intent1, EamCode4Result.reQ_RED_REQUEST_CODE);
                                    showFunctionLayout();
                                }

                                @Override
                                public void onError(String code, String msg)
                                {
                                    ToastUtils.showShort("LV" + code + "开启发送群红包功能");
                                }
                            });
                            break;
                        case R.id.tv_room_live_mainfunction_link:
                            if ("1".equals(SharePreUtils.getIsSignAnchor(mActivity)))
                            {
                                // 连麦
                                if (!isShowInviteLive)
                                {
                                    ViewShareHelper.getInstance(mActivity, mPresenter).chooseConnectMember(mActivity, mPresenter.getmRecord().getRoomId());
                                    showFunctionLayout();
                                }
                                else
                                {
                                    ToastUtils.showShort("您正在连麦中，不可邀请其他人，请关闭连麦再试。");
                                }
                                break;
                            }
                            NetHelper.checkPrivilegeToLevel(mActivity, "007", new ICommonOperateListener()
                            {
                                @Override
                                public void onSuccess(String response)
                                {
                                    // 连麦
                                    if (!isShowInviteLive)
                                    {
                                        ViewShareHelper.getInstance(mActivity, mPresenter).chooseConnectMember(mActivity, mPresenter.getmRecord().getRoomId());
                                        showFunctionLayout();
                                    }
                                    else
                                    {
                                        ToastUtils.showShort("您正在连麦中，不可邀请其他人，请关闭连麦再试。");
                                    }
                                }

                                @Override
                                public void onError(String code, String msg)
                                {
                                    Logger.t(TAG).d("code:" + code + ",msg:" + msg);
                                    ToastUtils.showShort("LV" + code + "开启连麦功能");
                                }
                            });
                            break;
                    }
                }
            };
            sentextView.setOnClickListener(listener);
            packetView.setOnClickListener(listener);
            linkView.setOnClickListener(listener);
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        }
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        else
            mPopupWindow.showAtLocation(findViewById(R.id.rlRoomLayer), Gravity.BOTTOM, 0, mPopupWindow.getHeight());
    }


    //发弹幕
    public void shoot(String phurl, String lev, String name, String param, String isVuser)
    {
        final Map<String, String> resultMap = new ArrayMap<>();
        resultMap.put(EamConstant.EAM_LIVE_ATTR_NICKNAME, name);
        resultMap.put(EamConstant.EAM_LIVE_ATTR_HEADIMAGE, phurl);
        resultMap.put(EamConstant.EAM_LIVE_ATTR_LEVEL, lev);
        resultMap.put(EamConstant.EAM_LIVE_ATTR_VUSER, isVuser);
        resultMap.put(EamConstant.EAM_LIVE_ATTR_PARAM, param);
        ViewShareHelper.getInstance(mActivity, mPresenter).shootBarrage(resultMap);
    }


    //显示功能入口消息按钮的红点
    public void showMessageBtDian(boolean flag)
    {
        if (flag)
            btMessageDian.setVisibility(View.VISIBLE);
        else
            btMessageDian.setVisibility(View.GONE);
    }

    /**
     * 开始游戏小图标动画
     */
    public void startGameIconAnim()
    {
        if ("0".equals(switchStatusMap.get("sunMoonStar")))
            ViewShareHelper.getInstance(mActivity, mPresenter).startGameAnim();
    }

    /**
     * 显示游戏开始弹窗界面
     *
     * @param s
     */
    public void showGameStart(StartGameBean s)
    {
        ViewShareHelper.getInstance(mActivity, mPresenter).startGameHeart();
        ViewShareHelper.getInstance(mActivity, mPresenter).showGameStart(s, false, false);
    }

    public void refreshGameInviteList(String id)
    {
        ViewShareHelper.getInstance(mActivity, mPresenter).refreshGameInviteDialog(true, null, -1, "", null, id);
    }


//endregion--------------------------------------------------------------------更改UI函数end----------------------------------------------------------------------------------------------------------------

    private void registerBrdReceiver()
    {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(EamConstant.EAM_BRD_ACTION_SET_ADMIN);
        myIntentFilter.addAction(EamConstant.EAM_BRD_ACTION_CANCEL_ADMIN);
        myIntentFilter.addAction(EamConstant.EAM_BRD_ACTION_SHUTEUP_ADMIN);
        myIntentFilter.addAction(EamConstant.EAM_BRD_ACTION_SHUNTUP_HOST);
        myIntentFilter.addAction(EamConstant.EAM_BRD_ACTION_SHUNTUP_OFF_HOST);
        myIntentFilter.addAction(EamConstant.EAM_BRD_ACTION_SHUNTUP_OFF_ADMIN);
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND);
        myIntentFilter.addAction(EamConstant.EAM_HX_RECEIVE_CLOSE_LIVE);
        myIntentFilter.addAction(EamConstant.EAM_HX_RECEIVE_HIDE_FOCUS);

        registerReceiver(liveReceiver, myIntentFilter);
    }

    private void unRegisterBrdReceiver()
    {
        if (liveReceiver != null)
            unregisterReceiver(liveReceiver);
    }
    //endregion

    private BroadcastReceiver liveReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (mPresenter == null)
                return;
            switch (action)
            {
                case EamConstant.EAM_BRD_ACTION_SET_ADMIN:   //主播设置房管
                {
                    ChosenFansBean setAdminBean = intent.getParcelableExtra("userBean");

                    Map<String, String> map = new HashMap<>();
                    map.put("roomId", mPresenter.getmRecord().getRoomId());
                    map.put("luId", setAdminBean.getuId());
                    map.put("nickName", setAdminBean.getNicName());
                    map.put("txId", setAdminBean.getId());

                    HashMap<String, String> paramMap = new HashMap<>();
                    paramMap.put("inRoom", intent.getStringExtra("inRoom"));
                    paramMap.put("txId", setAdminBean.getId());
                    paramMap.put(TXConstants.CMD_PARAM, new Gson().toJson(map));
                    paramMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_NOTIFY_ROOM_ADMIN + "");
                    mPresenter.notifySetAdminByHost(paramMap);
                    break;
                }
                case EamConstant.EAM_BRD_ACTION_CANCEL_ADMIN: //主播取消房管
                {
                    ChosenAdminBean cancelAdminBean = intent.getParcelableExtra("userBean");
                    Map<String, String> map = new HashMap<>();
                    map.put("roomId", mPresenter.getmRecord().getRoomId());
                    map.put("luId", cancelAdminBean.getuId());
                    map.put("nickName", cancelAdminBean.getNicName());
                    map.put("txId", cancelAdminBean.getId());

                    HashMap<String, String> paramMap = new HashMap<>();
                    paramMap.put("inRoom", intent.getStringExtra("inRoom"));
                    paramMap.put("txId", cancelAdminBean.getId());
                    paramMap.put(TXConstants.CMD_PARAM, new Gson().toJson(map));
                    paramMap.put(TXConstants.CMD_KEY, TXConstants.AVIMCMD_NOTIFY_ROOM_ADMIN_CANCEL + "");
                    mPresenter.notifyCancelAdminByHost(paramMap);
                    break;
                }
                case EamConstant.EAM_BRD_ACTION_SHUTEUP_ADMIN:
                    mPresenter.notifyShuntUpByAdmin((HashMap<String, String>) intent.getSerializableExtra("param"));
                    break;
                case EamConstant.EAM_BRD_ACTION_SHUNTUP_HOST:
                    mPresenter.notifyShuntUpByHost((HashMap<String, String>) intent.getSerializableExtra("param"));
                    break;
                case EamConstant.EAM_BRD_ACTION_SHUNTUP_OFF_HOST:
                {
                    mPresenter.notifyShuntUpOffByHost((HashMap<String, String>) intent.getSerializableExtra("param"));
                    break;
                }
                case EamConstant.EAM_BRD_ACTION_SHUNTUP_OFF_ADMIN:
                {
                    mPresenter.notifyShuntUpOffByAdmin((HashMap<String, String>) intent.getSerializableExtra("param"));
                    break;
                }
                case EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND:
                {
                    Logger.t("=============").d("" + EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND);
                    if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                        ViewShareHelper.getInstance(mActivity, mPresenter).startBootyCallMsgAni();
                    break;
                }
                case EamConstant.EAM_HX_RECEIVE_CLOSE_LIVE:
                    Logger.t(TAG).d("chat------>接收到关闭房间广播");
                    //关闭房间
                    if (isShowInviteLive)
                    {
                        String role, reason;
                        if (mPresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
                        {
                            role = "host";
                            reason = "0";
                        }
                        else
                        {
                            role = "member";
                            reason = "1";
                        }
                        closeInviteLive(role, reason);
                        new Handler().postDelayed(() -> exitRoom(ExitRoomType.NORMAL), 500);
                    }
                    else
                        exitRoom(ExitRoomType.NORMAL);
                    break;
                case EamConstant.EAM_HX_RECEIVE_HIDE_FOCUS:
                    tvFollow.setVisibility(View.GONE);
                    mPresenter.getmRecord().getEnterRoom4EH().setFlag("1");
                    break;
                default:
                    break;
            }
        }
    };


}
