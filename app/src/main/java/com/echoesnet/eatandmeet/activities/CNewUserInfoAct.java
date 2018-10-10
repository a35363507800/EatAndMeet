package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.managers.ViewShareHelper;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.fragments.CUserInfoFrg;
import com.echoesnet.eatandmeet.fragments.DateInfoFrg;
import com.echoesnet.eatandmeet.fragments.DynamicStateFrg;
import com.echoesnet.eatandmeet.fragments.GoodSaleHostFrg;
import com.echoesnet.eatandmeet.fragments.VpArticalFrg;
import com.echoesnet.eatandmeet.listeners.AppBarStateChangeListener;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ActionItemBean;
import com.echoesnet.eatandmeet.models.bean.CUserInfoBean;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpICNewUserInfoPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICNewUserInfoView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketConstant;
import com.echoesnet.eatandmeet.views.adapters.CUserInfoHeadImgAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlterDialogs.DialogWith2BtnAtBottom;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.MaxByteLengthEditText;
import com.echoesnet.eatandmeet.views.widgets.ScrollTextTabView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.echoesnet.eatandmeet.views.widgets.UserRightPop;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.echoesnet.eatandmeet.utils.EamConstant.EAM_OPEN_DATEHOUSE_DETAIL;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/14 16.
 * @description app个人详情页 :id 和uid 都可以访问用户详情页，优先使用uid,没有uid，必须传id
 */
public class CNewUserInfoAct extends MVPBaseActivity<CNewUserInfoAct, ImpICNewUserInfoPre> implements ICNewUserInfoView
{
    private static final String TAG = CNewUserInfoAct.class.getSimpleName();
    @BindView(R.id.iv_head)
    LevelHeaderView ivHead;
    @BindView(R.id.iv_my_yue)
    ImageView ivMyYue;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.itv_sex_age)
    GenderView itvSexAge;
    @BindView(R.id.ll_all_info)
    RelativeLayout llAllInfo;
    @BindView(R.id.tv_id_person)
    TextView tvIdPerson;
    @BindView(R.id.ll_all_level_info)
    LinearLayout llAllLevelInfo;
    @BindView(R.id.tv_fanpiao_num)
    TextView tvFanpiaoNum;
    @BindView(R.id.view_divide)
    View viewDivide;
    @BindView(R.id.tv_fans_num)
    TextView tvFansNum;
    @BindView(R.id.rl_all_fanpiao_info)
    RelativeLayout rlAllFanpiaoInfo;
    @BindView(R.id.tv_yue)
    Button tvYue;
    @BindView(R.id.tv_hello)
    Button tvHello;
    @BindView(R.id.tv_add_focus_host)
    Button tvAddFocusHost;
    @BindView(R.id.ll_yuePao_info)
    LinearLayout llYuePaoInfo;
    @BindView(R.id.ll_living_go)
    LinearLayout llLivingGo;
    @BindView(R.id.rv_user_imgs_more)
    RecyclerView rvUserImgsMore;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.sttv_tab)
    ScrollTextTabView sttvTab;
    @BindView(R.id.rl_head_bg)
    RelativeLayout rlHeadBg;
    @BindView(R.id.vp_all_info)
    ViewPager vpAllInfo;
    @BindView(R.id.iv_bg_icon)
    ImageView ivBgIcon;
    @BindView(R.id.ctl_layout)
    CoordinatorLayout ctlLayout;
    @BindView(R.id.main_tb_toolbar)
    Toolbar mainTbToolbar;
    @BindView(R.id.collapse_toolbar)
    CollapsingToolbarLayout collapseToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.ll_pic_info_all)
    LinearLayout llPicInfoAll;
    @BindView(R.id.iv_level_person)
    ImageView ivLevelPerson;
    @BindView(R.id.iv_host_level)
    ImageView ivHostLevel;
    @BindView(R.id.tv_living)
    TextView tvLiving;

    private MaxByteLengthEditText inputModifyNick;


    public static final String ACTION_FOCUS = "0";
    public static final String ACTION_BLACK = "1";
    public static final String ACTION_REMARK = "2";
    public static final String ACTION_FOCUS_REMARK = "3";

    private Activity mAct;
    private String toCheckUserUid;//查看用户的uId;
    private String toCheckUserId; //查看用户的Id;
    private String toCheckUserNicName;
    private String toCheckUserIdH;//查看用户的环信Id;
    private String toCheckUserIdT;//查看用户腾讯Id
    private String toCheckUserAvRoomId;//查看的用户直播房间号(要查看人的房间号，不是当前直播间的)
    private String currentUserAvRoomId;//当前用户加入的直播间Id
    private CUserInfoHeadImgAdapter mCUserInfoHeadImgAdapter;
    private PagerAdapter fragMentAdaper;
    private Dialog pDialog;
    private CUserInfoBean bean;
    private DynamicStateFrg momentsFrg;
    private VpArticalFrg vpArticalFrg;
    private List<String> userImgs = new ArrayList<>();
    private List<String> userIconImgs = new ArrayList<>();
    private List<Fragment> mFragments;
    private List<Map<String, TextView>> navBtns;
    private UserRightPop titlePopup; // 添加 ...弹出层
    private String oppositeNickName;//对方昵称
    private String chatRoomId;
    private String editName = "";
    private String fromWhere;
    private TextView tvShutUp;//禁言
    private TextView tvMenu; //功能菜单
    private TextView tvActTitle;//title
    private EaseUser toEaseUser;
    private String remark = "";
    private String IsVuser = "";//大V
    private String openFrom;
    private String action = ""; //关注 或 拉黑
    private AppBarStateChangeListener.State mCurrentState = AppBarStateChangeListener.State.EXPANDED;
    private int paramCase = 0;
    private int currentPage;
    private int position = 0;
    private int finishResult = -1;
    private int volume; // 当前音量
    private boolean isGhostUser = false;
    private boolean isShowNewBie = false;
    private boolean isFromVideo = false;
    private boolean isFirstOpen = false;
    private boolean isAlreadyHide = false;
    private boolean isFocus = false;
    private boolean isBlack = false;
    private boolean isEditName = false;
    private boolean isOpenSingleTask = false;
    private CUserInfoBean checkBackBean;
    private static final int OPEN_CHATACTIVITY = 230;


    @Override
    protected ImpICNewUserInfoPre createPresenter()
    {
        return new ImpICNewUserInfoPre();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.t(TAG).d("----onCreate------");
        mAct = this;
        setContentView(R.layout.act_new_personalinfo);
        CommonUtils.setStatusBarDarkMode(this, false);
        ButterKnife.bind(this);
        //解决surfaceView初始化，页面闪屏问题
        Window window = getWindow();
        window.setFormat(PixelFormat.TRANSLUCENT);

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        pDialog = DialogUtil.getCommonDialog(mAct, "正在获取...");
        pDialog.setCancelable(false);
        toCheckUserUid = getIntent().getStringExtra("toUId");
        toCheckUserId = getIntent().getStringExtra("toId");
        chatRoomId = getIntent().getStringExtra("chatRoomId");
        openFrom = getIntent().getStringExtra("open-from");
        position = getIntent().getIntExtra("position", 0);
        if (!TextUtils.isEmpty(toCheckUserId))
        {
            toCheckUserIdT = "u" + toCheckUserId;
        }
        currentUserAvRoomId = getIntent().getStringExtra("currentRoomId");
        fromWhere = getIntent().getStringExtra("fromAct");
        currentPage = getIntent().getIntExtra("currentPage", 0);
        initTopBar();
        appbar.addOnOffsetChangedListener(new AppBarStateChangeListener()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int offSet)
            {
                Logger.t(TAG).d(state.name());
                if (state == State.EXPANDED)
                {
                    //展开状态
                    mCurrentState = State.EXPANDED;
                    tvActTitle.setVisibility(View.GONE);
                    for (int i = 0; i < navBtns.size(); i++)
                    {
                        navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(getTextColor(R.color.white));
                    }
                    mainTbToolbar.setBackgroundColor(getTextColor(R.color.transparent));
                    CommonUtils.setStatusBarDarkMode(CNewUserInfoAct.this, false);
                }
                else if (state == State.COLLAPSED)
                {
                    //折叠状态
                    mCurrentState = State.COLLAPSED;
                    CommonUtils.setStatusBarDarkMode(CNewUserInfoAct.this, true);
                    mainTbToolbar.setBackgroundColor(getTextColor(R.color.white));


                    if (bean != null)
                    {

                        tvActTitle.setText(TextUtils.isEmpty(remark) ? bean.getNicName() : remark);
                        tvActTitle.setTypeface(null, Typeface.BOLD);
                        tvActTitle.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < navBtns.size(); i++)
                    {
                        navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(getTextColor(R.color.C0412));
                    }
                }
                else
                {
                    //中间状态
                    mCurrentState = State.IDLE;
                    Logger.t(TAG).d("offSet>>" + offSet);
                    tvActTitle.setVisibility(View.GONE);
                    CommonUtils.setStatusBarDarkMode(CNewUserInfoAct.this, false);
                    for (int i = 0; i < navBtns.size(); i++)
                    {
                        navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(getTextColor(R.color.white));
                    }
                    mainTbToolbar.setBackgroundColor(getTextColor(R.color.transparent));
                }
            }
        });

        tvHello.setBackgroundResource(R.drawable.shape_pay_sure_press);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mAct);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvUserImgsMore.setLayoutManager(linearLayoutManager);
        //设置适配器
        mCUserInfoHeadImgAdapter = new CUserInfoHeadImgAdapter(mAct, userImgs);
        if (mPresenter != null)
        {
            // LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
            mPresenter.lookUserInfo(TextUtils.isEmpty(toCheckUserUid) ? toCheckUserId : toCheckUserUid);
        }
        initTab();

        isFirstOpen = true;
        initTitlePopupData();
        mCUserInfoHeadImgAdapter.setOnItemClickListener((View view, int position) ->
        {
            CommonUtils.showImageBrowser(mAct, userImgs, position, view);
        });
        rvUserImgsMore.setAdapter(mCUserInfoHeadImgAdapter);
        EamApplication.getInstance().controlUInfo.put(TAG, this);

    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one;
        mAct = this;
        Logger.t(TAG).d("----onNewIntent------");
        //解决surfaceView初始化，页面闪屏问题
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        isOpenSingleTask = true;
        Intent intent1 = getIntent();//获取最新的intent .
        toCheckUserUid = intent1.getStringExtra("toUId");
        toCheckUserId = intent1.getStringExtra("toId");
        chatRoomId = intent1.getStringExtra("chatRoomId");
        openFrom = intent1.getStringExtra("open-from");
        position = intent1.getIntExtra("position", 0);
        if (!TextUtils.isEmpty(toCheckUserId))
        {
            toCheckUserIdT = "u" + toCheckUserId;
        }
        currentUserAvRoomId = intent1.getStringExtra("currentRoomId");
        fromWhere = intent1.getStringExtra("fromAct");
        currentPage = intent1.getIntExtra("currentPage", 0);
        //设置适配器
        mPresenter.lookUserInfo(TextUtils.isEmpty(toCheckUserUid) ? toCheckUserId : toCheckUserUid);
        isFirstOpen = true;

        appbar.addOnOffsetChangedListener(new AppBarStateChangeListener()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int offSet)
            {
                Logger.t(TAG).d(state.name());
                if (state == State.EXPANDED)
                {
                    //展开状态
                    mCurrentState = State.EXPANDED;
                    tvActTitle.setVisibility(View.GONE);
                    for (int i = 0; i < navBtns.size(); i++)
                    {
                        navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(getTextColor(R.color.white));
                    }
                    mainTbToolbar.setBackgroundColor(getTextColor(R.color.transparent));
                    CommonUtils.setStatusBarDarkMode(CNewUserInfoAct.this, false);
                }
                else if (state == State.COLLAPSED)
                {
                    //折叠状态
                    mCurrentState = State.COLLAPSED;
                    mainTbToolbar.setBackgroundColor(getTextColor(R.color.white));
                    CommonUtils.setStatusBarDarkMode(CNewUserInfoAct.this, true);
                    if (bean != null)
                    {
                        Logger.t(TAG).d(" //折叠状态 ======================" + bean.getNicName());
                        tvActTitle.setText(TextUtils.isEmpty(remark) ? bean.getNicName() : remark);
                        tvActTitle.setTypeface(null, Typeface.BOLD);
                        tvActTitle.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < navBtns.size(); i++)
                    {
                        navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(getTextColor(R.color.C0412));
                    }
                }
                else
                {
                    //中间状态
                    mCurrentState = State.IDLE;
                    Logger.t(TAG).d("offSet>>" + offSet);
                    tvActTitle.setVisibility(View.GONE);
                    for (int i = 0; i < navBtns.size(); i++)
                    {
                        navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(getTextColor(R.color.white));
                    }
                    mainTbToolbar.setBackgroundColor(getTextColor(R.color.transparent));
                    CommonUtils.setStatusBarDarkMode(CNewUserInfoAct.this, false);
                }
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * @Description: 显示新手引导
     */
    private void showNewbieGuide()
    {
        if (SharePreUtils.getIsNewBieInfo(mAct))
        {
            NetHelper.checkIsShowNewbie(mAct, "12", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        //获取root节点
                        final FrameLayout fRoot = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mAct, R.layout.view_newbie_guide_userinfo, null);
                        final ImageView imgOrder1 = (ImageView) vGuide.findViewById(R.id.img_order1);
                        final ImageView imgOrder2 = (ImageView) vGuide.findViewById(R.id.img_order2);

                        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) imgOrder1.getLayoutParams();
                        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) imgOrder2.getLayoutParams();
                        switch (paramCase)
                        {
                            case 0://默认高度  头像高度315
                                Logger.t(TAG).d("paramCase>>" + 0);
                                params1.setMargins(0, CommonUtils.dp2px(mAct, 158), 0, 0);
                                params2.setMargins(0, CommonUtils.dp2px(mAct, 260), CommonUtils.dp2px(mAct, 15), 0);
                                break;
                            case 1://  头像高度220
                                Logger.t(TAG).d("paramCase>>" + 1);
                                params1.setMargins(0, CommonUtils.dp2px(mAct, 62), 0, 0);
                                params2.setMargins(0, CommonUtils.dp2px(mAct, 163), CommonUtils.dp2px(mAct, 15), 0);
                                break;
                            case 2://  头像高度160
                                Logger.t(TAG).d("paramCase>>" + 2);
                                params1.setMargins(0, CommonUtils.dp2px(mAct, 3), 0, 0);
                                params2.setMargins(0, CommonUtils.dp2px(mAct, 105), CommonUtils.dp2px(mAct, 15), 0);
                                break;
                            case 3://  头像高度260
                                Logger.t(TAG).d("paramCase>>" + 3);
                                params1.setMargins(0, CommonUtils.dp2px(mAct, 103), 0, 0);
                                params2.setMargins(0, CommonUtils.dp2px(mAct, 205), CommonUtils.dp2px(mAct, 15), 0);
                                break;
                            default:
                                break;
                        }
                        imgOrder1.setLayoutParams(params1);
                        imgOrder2.setLayoutParams(params2);
                        final TextView tvClickDismiss = (TextView) vGuide.findViewById(R.id.tv_click_dismiss);
                        vGuide.setClickable(true);
                        tvClickDismiss.setOnClickListener((v) ->
                        {
                            fRoot.removeView(vGuide);
                            SharePreUtils.setIsNewBieInfo(mAct, false);
                            NetHelper.saveShowNewbieStatus(mAct, "12");
                        });
                        fRoot.addView(vGuide);
                    }
                    else
                    {
                        SharePreUtils.setIsNewBieInfo(mAct, false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {
                }
            });
        }
    }


    private void initTab()
    {
        sttvTab.setClickColor(R.color.C0412);
        sttvTab.setDefaultColor(R.color.C0323);
        sttvTab.setScrollbarColor(R.color.C0412);
        sttvTab.setTextSize(15);
        sttvTab.setLineShow(true);
        sttvTab.setLineBold(2);
        sttvTab.setLineColor(R.color.C0332);
        sttvTab.setScrollbarSize(5);
        sttvTab.setScrollbarRate(60);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        AudioManager audio1 = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        int currentVolume = audio1.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume = currentVolume > 0 ? currentVolume : volume;
        if (chatRoomId != null)
        {
            //to do nothing
        }
        else if (isFirstOpen || isFromVideo)
        {
            audio1.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (volume > 0)
        {
            AudioManager audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Logger.t(TAG).d("----onStop------");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Logger.t(TAG).d("----onDestroy------");
        if (EamApplication.getInstance().controlUInfo.size() == 1)
            EamApplication.getInstance().controlUInfo.clear();
    }

    private int getTextColor(int color)
    {
        return ContextCompat.getColor(mAct, color);
    }

    private void initTopBar()
    {
        tvActTitle = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                Intent data = new Intent();
                data.putExtra("isFocus", isFocus);
                data.putExtra("isBlack", isBlack);
                data.putExtra("isEditName", isEditName);
                data.putExtra("editName", editName);
                data.putExtra("action", action);
                data.putExtra("position", position);
                if (action.equals(ACTION_REMARK))
                {
                    data.putExtra(Constant.EXTRA_TO_EASEUSER, toEaseUser);
                    finishResult = RESULT_OK;
                }
                setResult(finishResult, data);
                finish();
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
            }

            @Override
            public void right2Click(View view)
            {
                Logger.t(TAG).d("点击了举报弹出框》》");
                if (titlePopup != null && !titlePopup.isShowing() && !mAct.isFinishing())
                {
                    titlePopup.show(view);
                }
            }
        });
        topBarSwitch.setBackground(ContextCompat.getDrawable(mAct, R.drawable.transparent));
        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 1, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON);
            tv.setVisibility(View.VISIBLE);
            tv.setTextSize(22);
            tv.setTextColor(ContextCompat.getColor(mAct, R.color.white));
            if (i == 0)
            {
                tv.setText("{eam-n-previous}");
            }
            if (i == 1)
            {
                tv.setText("");
                tv.setTextSize(14);
            }
            if (i == 2)
            {
                tv.setText("{eam-e609}");
            }
        }
        tvShutUp = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        tvHello.setEnabled(true);
        if (currentUserAvRoomId == null)
        {
            tvShutUp.setVisibility(View.GONE);
        }
        tvShutUp.setTag("");
        tvShutUp.setOnClickListener((v) ->
        {
            switch (String.valueOf(tvShutUp.getTag()))
            {
                case "shutUp"://禁言中
                    if (isGhostUser)//如果是假用户
                    {
                        handleGhostUserShutUp();
                        break;
                    }
                    if (mPresenter != null)
                    {
                        mPresenter.setUserShutUpNo(currentUserAvRoomId, toCheckUserUid);
                    }
                    break;
                case "speak"://未禁言中
                    if (isGhostUser)//如果是假用户
                    {
                        handleGhostUserShutUp();
                        break;
                    }
                    if (mPresenter != null)
                    {
                        mPresenter.setUserShutUpYes(currentUserAvRoomId, toCheckUserUid);
                    }
                    break;
                default:
                    break;
            }
        });
        tvMenu = navBtns.get(2).get(TopBarSwitch.NAV_BTN_ICON);

    }

    private void initTitlePopupData()
    {
        titlePopup = new UserRightPop(mAct);
        titlePopup.addAction(0, new ActionItemBean(mAct, "修改备注", "{eam-e60a}", "0"));
        titlePopup.addAction(1, new ActionItemBean(mAct, "拉黑", "{eam-e61f}", "0"));
        titlePopup.addAction(2, new ActionItemBean(mAct, "举报", "{eam-s-warning}", "0"));
        titlePopup.setItemOnClickListener(new UserRightPop.OnItemOnClickListener()
        {
            @Override
            public void onItemClick(ActionItemBean item, int position)
            {
                String title1 = item.getmTitle();
                switch (title1)
                {
                    case "修改备注":
                        showEditNickDialog();
                        break;
                    case "拉黑":
                        showDelFriendDialog();
                        break;
                    case "举报":
                        Intent reportIntent = new Intent(mAct, ReportFoulsUserAct.class);
                        reportIntent.putExtra("luId", toCheckUserUid);
                        startActivity(reportIntent);
                        break;
                    default:
                        break;
                }
            }
        });
        titlePopup.setOnDismissListener(() ->
        {
            titlePopup.dismissPop();
        });

    }

    private void showDelFriendDialog()
    {
        if (!mAct.isFinishing())
        {
            new CustomAlertDialog(mAct).builder().setTitle("提示!").setMsg("是否拉黑" + oppositeNickName)
                    .setPositiveButton("是", (v) ->
                    {
//                        if (mPresenter != null)
//                            mPresenter.deFriend(toCheckUserUid);
                        deleteConversation();
                    })
                    .setNegativeButton("否", (v) ->
                    {
                    }).setPositiveTextColor(ContextCompat.getColor(mAct, R.color.C0323)).show();
        }

    }

    private void showEditNickDialog()
    {
        View contentView = LayoutInflater.from(mAct).inflate(R.layout.dialog_modify_nick, null);
    
        inputModifyNick = contentView.findViewById(R.id.input_2thNicName);
        inputModifyNick.setMaxByteLength(14);
        // input_modify_nick.setHint(TextUtils.isEmpty(remark) ? " " : remark);
        if (!mAct.isFinishing())
        {
            new DialogWith2BtnAtBottom(mAct)
                    .buildDialog(mAct)
                    .setDialogTitle("修改备注", true)
                    .setContent(contentView)
                    .setNegativeButton("否", (v) ->
                    {
                    }).setPositiveButton("是", (v) ->
            {

         
                String reMark = inputModifyNick.getText().toString();
                if (!TextUtils.isEmpty(reMark.trim()) && mPresenter != null)
                {
                    Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                    Matcher m = p.matcher(reMark);
                    mPresenter.editReMark(m.replaceAll(""), toCheckUserUid);
                }
            
                if (TextUtils.isEmpty(reMark.trim()))
                {
                    mPresenter.editReMark(reMark, toCheckUserUid);
                    tvNickName.setText(bean.getNicName());
                    remark = reMark;
                }
            }).setPositiveTextColor(ContextCompat.getColor(mAct, R.color.C0311))
                    .setNegativeTextColor(ContextCompat.getColor(mAct, R.color.C0323)).show();
        }

    }

    @OnClick({R.id.iv_my_yue, R.id.tv_yue, R.id.tv_hello, R.id.tv_add_focus_host, R.id.tv_living})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_my_yue://跳转到我的约会管家
                Intent intent = new Intent(mAct, MyDateHousekeepAct.class);
                intent.putExtra("toUId", toCheckUserUid);
            
                startActivityForResult(intent, EAM_OPEN_DATEHOUSE_DETAIL);
                break;
            case R.id.tv_yue://加购物车
                if (tvYue.getTag().equals("true"))
                {
                    if (mPresenter != null)
                    {
                        mPresenter.addWish(toCheckUserUid);
                    }
                }
                break;
            case R.id.tv_hello://打招呼 or 发消息
                Intent intentChat = new Intent(mAct, CChatActivity.class);
                if (!TextUtils.isEmpty(currentUserAvRoomId))//说明这个用户详情页是从直播间打开的
                {
                    intentChat.putExtra("isLiveOpen", "true");
                }
                intentChat.putExtra(Constant.EXTRA_TO_EASEUSER, toEaseUser == null ? new EaseUser(toCheckUserIdH) : toEaseUser);
                Logger.t(TAG).d("toEaseUser" + toEaseUser.toString());
                if (EamApplication.getInstance().controlChat.size() == 2)
                {
                    if (EamApplication.getInstance().controlChat.get(CChatActivity.class.getSimpleName()) != null)
                    {
                        EamApplication.getInstance().controlChat.get(CChatActivity.class.getSimpleName()).finish();
                        EamApplication.getInstance().controlChat.remove(CChatActivity.class.getSimpleName());
                    }
                }
                //  startActivity(intentChat);
                startActivityForResult(intentChat,OPEN_CHATACTIVITY);
                break;
            case R.id.tv_add_focus_host://加关注
                if (bean != null)
                {
                    if (TextUtils.equals("0", bean.getFocus()))
                    {
                        if (mPresenter != null)
                            mPresenter.focusPerson(bean.getuId(), "1");
                    }
                }
                break;
            case R.id.tv_living://去直播间看看
                if (ViewShareHelper.liveMySelfRole == LiveRecord.ROOM_MODE_HOST)
                {
                    ToastUtils.showShort("您当前正在直播，无法查看他人直播");
                    break;
                }
                if (CommonUtils.isInLiveRoom)
                {
                    ToastUtils.showShort("您当前正在直播间中，请先退出！");
                    return;
                }
                CommonUtils.startLiveProxyAct(mAct, LiveRecord.ROOM_MODE_MEMBER, "", "", "", toCheckUserAvRoomId, null, EamCode4Result.reqNullCode);
                break;
            default:
                break;
        }
    }

    private void handleGhostUserShutUp()
    {
        if (tvShutUp.getTag().equals("speak"))
        {
            tvShutUp.setText("解除禁言");
            tvShutUp.setTag("shutUp");
        }
        else if (tvShutUp.getTag().equals("shutUp"))
        {
            tvShutUp.setText("禁言");
            tvShutUp.setTag("speak");
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String errorCode, String errorBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_addMute:
                if (ErrorCodeTable.USER_MUTING.equals(errorCode))
                {
                    tvShutUp.setText("解除禁言");
                    tvShutUp.setTag("shutUp");
                }
                break;
            case NetInterfaceConstant.LiveC_delMute:
                if (ErrorCodeTable.USER_BEEN_DELMUTE.equals(errorCode))
                {
                    tvShutUp.setText("禁言");
                    tvShutUp.setTag("speak");
                }
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {

    }


    @Override
    public void editReMarkCallback(final String input, String responseStr)
    {
        ToastUtils.showShort("修改成功");
        try
        {
            Logger.t(TAG).d("返回结果" + responseStr);
            if (input != null)
            {
                remark = input;
                if (!TextUtils.isEmpty(input))
                {
                    tvNickName.setText(input);
                }
                if (momentsFrg != null)
                {
                    Logger.t(TAG).d("刷新动态昵称");
                    momentsFrg.reFreshInfo(true, "refresh");
                }
       
                if (mCurrentState == AppBarStateChangeListener.State.COLLAPSED && tvActTitle != null && bean != null)
                {
                    tvActTitle.setText(TextUtils.isEmpty(input) ? bean.getNicName() : input);
                }
                if (vpArticalFrg != null)
                {
                    vpArticalFrg.reFreshInfo(true, "refresh");
                }
                toEaseUser = new EaseUser(toCheckUserIdH);
                toEaseUser.setuId(bean.getuId());
                toEaseUser.setId(bean.getId());
                toEaseUser.setNickName(bean.getNicName());
                toEaseUser.setAvatar(bean.getUphUrl());
                toEaseUser.setLevel(bean.getLevel());
                toEaseUser.setSex(bean.getSex());
                toEaseUser.setAge(bean.getAge());
                toEaseUser.setRemark(remark);
                toEaseUser.setIsVuser(bean.getIsVuser());
                oppositeNickName = TextUtils.isEmpty(remark) ? bean.getNicName() : remark;
                if (ACTION_FOCUS.equals(action))
                    action = ACTION_FOCUS_REMARK;
                else
                    action = ACTION_REMARK;
                Observable.create(new ObservableOnSubscribe<EMConversation>()
                {
                    @Override
                    public void subscribe(ObservableEmitter<EMConversation> e) throws Exception
                    {
                        HuanXinIMHelper.getInstance().saveContact(toEaseUser);
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toCheckUserIdH);
                        if (conversation != null)
                        {
                            e.onNext(conversation);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(CNewUserInfoAct.this.<EMConversation>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(new Consumer<EMConversation>()
                        {
                            @Override
                            public void accept(EMConversation conversation) throws Exception
                            {
                                EMMessage msg = conversation.getLatestMessageFromOthers();
                                if (msg != null)
                                {
                                    msg.setAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
                                    conversation.updateMessage(msg);
                                    Logger.t(TAG).d("msg>>>>>>>" + msg.getBody());
                                }
                                isEditName = true;
                                editName = input;
                                ToastUtils.showShort("修改成功");
                                if (pDialog != null && pDialog.isShowing())
                                    pDialog.dismiss();
                            }
                        });
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void lookUserInfoCallBack(String body, String uId)
    {
        Logger.t(TAG).d("个人信息参数》》" + body);
        bean = EamApplication.getInstance().getGsonInstance().fromJson(body, CUserInfoBean.class);
        toCheckUserNicName = bean.getNicName();
        toCheckUserIdH = bean.getImuId();
        toCheckUserUid = bean.getuId();
        toCheckUserIdT = "u" + bean.getId();
        IsVuser = bean.getIsVuser();
        //用户的房间ID
        toCheckUserAvRoomId = bean.getId();

        appbar.setExpanded(true);
        if (bean != null && mCurrentState == AppBarStateChangeListener.State.COLLAPSED)
        {
            Logger.t(TAG).d("mCurrentState>>" + mCurrentState + bean.getNicName());
            tvActTitle.setText(TextUtils.isEmpty(bean.getRemark()) ? bean.getNicName() : bean.getRemark());
            tvActTitle.setTypeface(null, Typeface.BOLD);
            tvActTitle.setVisibility(View.VISIBLE);
        }

        isShowNewBie = true;
        if (mPresenter != null)
        {
            mPresenter.queryUsersRelationShip(bean.getuId());
        }
        mFragments = new ArrayList<>();
        //个人动态
        momentsFrg = DynamicStateFrg.newInstance(bean.getuId(), toCheckUserId, bean.getIsVuser(), currentUserAvRoomId);
        //基本信息
        CUserInfoFrg tab02 = new CUserInfoFrg();
        Bundle bundle2 = new Bundle();
        bundle2.putString("uId", bean.getuId());
        bundle2.putParcelable("userbean", bean);
        tab02.setArguments(bundle2);
        //约会评价
        DateInfoFrg tab03 = new DateInfoFrg();
        Bundle bundle3 = new Bundle();
        bundle3.putString("uId", bean.getuId());
        bundle3.putString("id", toCheckUserId);
        tab03.setArguments(bundle3);
        if (isOpenSingleTask)
        {
            //解决viewpage缓存页面问题
            mFragments.clear();
            vpAllInfo.removeAllViews();
            isOpenSingleTask = false;
            //解决viewpage缓存页面问题
            fragMentAdaper = new FragmentStatePagerAdapter(getSupportFragmentManager())
            {
                @Override
                public int getCount()
                {
                    return mFragments.size();
                }

                @Override
                public Fragment getItem(int arg0)
                {
                    return mFragments.get(arg0);
                }

                @Override
                public int getItemPosition(Object object)
                {
                    return PagerAdapter.POSITION_NONE;
                }
            };
        }
        else
        {
            fragMentAdaper = new FragmentPagerAdapter(getSupportFragmentManager())
            {
                @Override
                public int getCount()
                {
                    return mFragments.size();
                }

                @Override
                public Fragment getItem(int arg0)
                {
                    return mFragments.get(arg0);
                }

            };
        }
        if (TextUtils.equals("1", bean.getIsVuser()))
        {
            sttvTab.beginTextData(new String[]{"专栏文章", "动态", "资料"});
            //专栏文章
            vpArticalFrg = new VpArticalFrg();
            Bundle bundle0 = new Bundle();
            bundle0.putString("uId", bean.getuId());
            bundle0.putString("id", toCheckUserId);
            vpArticalFrg.setArguments(bundle0);
            mFragments.add(vpArticalFrg);
            mFragments.add(momentsFrg);
            mFragments.add(tab02);
        }
        else
        {
            sttvTab.beginTextData(new String[]{"动态", "资料", "约会"});
            mFragments.add(momentsFrg);
            mFragments.add(tab02);
            mFragments.add(tab03);
        }
        vpAllInfo.setOffscreenPageLimit(3);

        vpAllInfo.setAdapter(fragMentAdaper);
        sttvTab.setViewpager(vpAllInfo);

        if (fromWhere != null)
        {
            vpAllInfo.setCurrentItem(currentPage);
            Logger.t(TAG).d("直播界面跳转" + currentPage);
        }

        isGhostUser = "0".equals(bean.getGhost()) ? true : false;

        //如果自己查看自己
        if (TextUtils.equals(bean.getuId(), SharePreUtils.getUId(mAct)))
        {
            tvShutUp.setVisibility(View.INVISIBLE);
            tvLiving.setVisibility(View.GONE);
            tvMenu.setVisibility(View.GONE);
            llYuePaoInfo.setVisibility(View.GONE);
            tvYue.setVisibility(View.GONE);
            tvAddFocusHost.setVisibility(View.GONE);
            tvHello.setVisibility(View.GONE);

            CollapsingToolbarLayout.LayoutParams params1 = (CollapsingToolbarLayout.LayoutParams) rlHeadBg.getLayoutParams();

            if (bean.getImgUrls().size() == 0)
            {
                params1.height = CommonUtils.dp2px(mAct, 160);
                isAlreadyHide = true;
                paramCase = 2;
            }
            else
            {
                params1.height = CommonUtils.dp2px(mAct, 260);
                paramCase = 3;
                llPicInfoAll.setVisibility(View.VISIBLE);
                rvUserImgsMore.setVisibility(View.VISIBLE);
            }
            rlHeadBg.setLayoutParams(params1);
        }
        else
        {
            CollapsingToolbarLayout.LayoutParams params1 = (CollapsingToolbarLayout.LayoutParams) rlHeadBg.getLayoutParams();
            if (bean.getImgUrls().size() == 0)
            {
                llPicInfoAll.setVisibility(View.GONE);
                rvUserImgsMore.setVisibility(View.GONE);
                //改变顶部头像的高度，减去下面三个按钮的高度，重新布局
                params1.height = CommonUtils.dp2px(mAct, 220);
                paramCase = 1;
            }
            else
            {
                llPicInfoAll.setVisibility(View.VISIBLE);
                rvUserImgsMore.setVisibility(View.VISIBLE);
                params1.height = CommonUtils.dp2px(mAct, 315);
            }
            rlHeadBg.setLayoutParams(params1);
        }
        //设置相册
        userImgs.clear();
        userImgs.addAll(bean.getImgUrls());
        mCUserInfoHeadImgAdapter.notifyDataSetChanged();
        //如果是从直播页面进入的
        if (!TextUtils.isEmpty(currentUserAvRoomId))
        {
            if (mPresenter != null)
                mPresenter.checkUserRole(currentUserAvRoomId, SharePreUtils.getUId(mAct), toCheckUserUid);
        }

        //是否在心愿单(0:无1：进行中);
        if (TextUtils.equals("0", bean.getInWish()))
        {
            tvYue.setText("+ 约会");
            tvYue.setEnabled(true);
            tvYue.setTag("true");
            tvYue.setBackgroundResource(R.drawable.shape_pay_sure_press);
        }
        else
        {
            tvYue.setText("已添加");
            tvYue.setEnabled(false);
            tvYue.setTag("false");
            tvYue.setBackgroundResource(R.drawable.shape_pay_sure_normal);
        }

        //是否关注(0:无 1：是);
        if (TextUtils.equals("0", bean.getFocus()))
        {
            tvAddFocusHost.setText("+ 关注");
            tvAddFocusHost.setEnabled(true);
            if (titlePopup != null)
            {
                titlePopup.removeAction("修改备注");
            }
            tvHello.setText("打招呼");
            tvHello.setVisibility(View.VISIBLE);
            tvAddFocusHost.setBackgroundResource(R.drawable.shape_pay_sure_press);
        }
        else
        {
            tvAddFocusHost.setText("已关注");
            tvAddFocusHost.setEnabled(false);
            tvHello.setText("发消息");
            tvHello.setVisibility(View.VISIBLE);
            tvAddFocusHost.setBackgroundResource(R.drawable.shape_pay_sure_normal);
        }
        //"status":"是否正在直播（0：否1：是）
        if (TextUtils.equals("0", bean.getStatus()))
        {
            tvLiving.setVisibility(View.GONE);
        }
        else
        {
            tvLiving.setVisibility(View.VISIBLE);
        }
        remark = bean.getRemark();
        if (TextUtils.isEmpty(bean.getRemark()))
        {
            tvNickName.setText(bean.getNicName());
        }
        else
        {
            tvNickName.setText(bean.getRemark());
        }

        tvIdPerson.setText("ID " + bean.getId());

        int level = 0;
        try
        {
            level = Integer.parseInt(bean.getLevel());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        int hostLevel = 0;
        try
        {
            hostLevel = Integer.parseInt(bean.getAnchorLevel());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        if (level == 0)
        {
            // ivLevelPerson.setImageResource(R.drawable.lv00_xxhdpi);
            ivLevelPerson.setVisibility(View.GONE);
        }
        else
        {
            ivLevelPerson.setImageResource(LevelView.getLevelImage(level));
            ivLevelPerson.setVisibility(View.VISIBLE);
        }


        if (hostLevel == 0)
        {
            //ivHostLevel.setImageResource(R.drawable.old_zblv00_xxhdpi);
            ivHostLevel.setVisibility(View.GONE);
        }
        else
        {
            ivHostLevel.setImageResource(LevelView.getLevelImageHost(hostLevel));
            ivHostLevel.setVisibility(View.VISIBLE);
        }
        oppositeNickName = TextUtils.isEmpty(bean.getRemark()) ? bean.getNicName() : bean.getRemark();
        userIconImgs.add(bean.getUphUrl());

        ivHead.setHeadImageByUrl(bean.getUphUrl());
        ivHead.showRightIcon(bean.getIsVuser());
        ivHead.setBorderWidth(R.dimen.icon_border_size);
        ivHead.setBorderColor(ContextCompat.getColor(mAct, R.color.C0332));
        ivHead.setOnClickListener((v) ->
        {
            CommonUtils.showImageBrowser(mAct, userIconImgs, 0, v, true);
        });
        //毛玻璃效果
        if (bean.getUphUrl() != null)
        {
            ImageUtils.showLoadingCover(mAct, bean.getUphUrl(), 1, ivBgIcon, true);
        }
        tvFansNum.setText("粉丝：" + bean.getFansNum());
        tvFanpiaoNum.setText("饭票：" + bean.getMeal());
        itvSexAge.setSex(bean.getAge(), bean.getSex());

        new Thread(() ->
        {
            toEaseUser = new EaseUser(toCheckUserIdH);
            toEaseUser.setuId(bean.getuId());
            toEaseUser.setId(bean.getId());
            toEaseUser.setNickName(bean.getNicName());
            toEaseUser.setAvatar(bean.getUphUrl());
            toEaseUser.setLevel(bean.getLevel());
            toEaseUser.setSex(bean.getSex());
            toEaseUser.setAge(bean.getAge());
            toEaseUser.setRemark(remark);
            toEaseUser.setIsVuser(bean.getIsVuser());
            HuanXinIMHelper.getInstance().saveContact(toEaseUser);
        }).start();
        if (isShowNewBie)
        {
            showNewbieGuide();
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        //  LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void checkUserInfoCallBack(String body, String targetUserId)
    {
        Logger.t(TAG).d("检查个人信息参数》》" + body);
        checkBackBean = EamApplication.getInstance().getGsonInstance().fromJson(body, CUserInfoBean.class);
        //是否在心愿单(0:无1：进行中);
        if (TextUtils.equals("0", checkBackBean.getInWish()))
        {
            tvYue.setText("+ 约会");
            tvYue.setEnabled(true);
            tvYue.setTag("true");
            tvYue.setBackgroundResource(R.drawable.shape_pay_sure_press);
        }
        else
        {
            tvYue.setText("已添加");
            tvYue.setEnabled(false);
            tvYue.setTag("false");
            tvYue.setBackgroundResource(R.drawable.shape_pay_sure_normal);
        }

    }

    @Override
    public void getUserShutUpStateCallback(String bodyStr)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(bodyStr);
            String isMute = jsonObject.getString("isMute");
            Logger.t(TAG).d("isMute--> " + isMute);
            if (isMute.equals("1"))
            {
                tvShutUp.setText("解除禁言");
                tvShutUp.setTag("shutUp");
            }
            else
            {
                tvShutUp.setText("禁言");
                tvShutUp.setTag("speak");

            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("获取用户禁言状态解析异常--> " + e.getMessage());
        }

    }

    @Override
    public void setUserShutUpYesCallback(String bodyStr)
    {
        try
        {
            Logger.t(TAG).d("主播禁言》》" + bodyStr);
            JSONObject jsonObject = new JSONObject(bodyStr);
            String userRole = jsonObject.getString("userRole");
            tvShutUp.setText("解除禁言");
            tvShutUp.setTag("shutUp");
            String action = "";
            switch (userRole)
            {
                case "1"://主播禁言
                    action = EamConstant.EAM_BRD_ACTION_SHUNTUP_HOST;
                    break;
                case "2":
                    action = EamConstant.EAM_BRD_ACTION_SHUTEUP_ADMIN;
                    break;
                default:
                    break;
            }
            sendBroadcast(action);
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("设置用户禁言异常：" + e.getMessage());
        }
    }

    @Override
    public void setUserShutUpNoCallback(String bodyStr)
    {
        try
        {
            Logger.t(TAG).d("主播解除禁言》》" + bodyStr);
            tvShutUp.setText("禁言");
            tvShutUp.setTag("speak");
            JSONObject jsonObject = new JSONObject(bodyStr);
            String userRole = jsonObject.getString("userRole");
            String action = "";
            switch (userRole)
            {
                case "1"://主播解除禁言
                    action = EamConstant.EAM_BRD_ACTION_SHUNTUP_OFF_HOST;
                    break;
                case "2":
                    action = EamConstant.EAM_BRD_ACTION_SHUNTUP_OFF_ADMIN;
                    break;
                default:
                    break;
            }
            sendBroadcast(action);
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("解除用户禁言异常：" + e.getMessage());
        }
    }

    @Override
    public void deFriendCallBack(String body)
    {
        Logger.t(TAG).d("拉进黑名单成功");
        if (titlePopup != null)
        {
            titlePopup.removeAction("拉黑");
        }
        isBlack = true;
        if (bean != null)
        {
            sendCMDMsg(EamConstant.EAM_CHAT_INBLACK_NOTIFY, bean.getImuId());
            EMConversation c = EMClient.getInstance().chatManager().getConversation(bean.getImuId(), EMConversation.EMConversationType.Chat);
            if (c != null)
            {
                c.setExtField("inBlack");
            }
        }
        ToastUtils.showShort("拉黑成功");
        action = ACTION_BLACK;
        Intent intent = new Intent();
        intent.putExtra("action", action);
        setResult(finishResult, intent);
        finish();
    }

    /**
     * 删除聊天记录
     */
    private void deleteConversation()
    {
        Observable.create((e) ->
        {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(bean.getImuId(), EMConversation.EMConversationType.Chat);
            List<String> redPacketIds = new ArrayList<>();
            if (conversation != null)
            {
                EMMessage lastMsgFromOther = conversation.getLatestMessageFromOthers();
                if (lastMsgFromOther != null)
                {
                    EaseUser user = new EaseUser(lastMsgFromOther.getFrom());
                    user.setuId(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID, ""));
                    user.setId(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_ID));
                    user.setNickName(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME));
                    user.setAvatar(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE));
                    user.setLevel(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_LEVEL));
                    user.setSex(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GENDER));
                    user.setAge(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_AGE));
                    user.setRemark(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_REMARK));
                    user.setIsVuser(lastMsgFromOther.getStringAttribute(EamConstant.EAM_CHAT_ATTR_VUSER, "0"));
                    Logger.t(TAG).d("------>保存用户信息：" + user.toString());
                    HuanXinIMHelper.getInstance().saveContact(user);
                }
                List<EMMessage> msgLst = new ArrayList<>();
                if (conversation != null)
                    msgLst = conversation.getAllMessages();
                for (EMMessage msg : msgLst)
                {
                    //如果检测到接收的消息中有没有收取的红包，则提示
                    if (msg.getType() == EMMessage.Type.TXT
                            && msg.direct() == EMMessage.Direct.RECEIVE
                            && msg.getBooleanAttribute("is_money_msg", false) == true)
                    {
                        redPacketIds.add(msg.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, ""));
                    }
                }
                e.onNext(redPacketIds);
            }
            else
                e.onNext(redPacketIds);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe((object) -> mPresenter.checkRedPacketsStates((List<String>) object));
    }

    private void deleteConBaseType()
    {
        Observable.create((e) ->
        {
            {
                boolean isDeleteSuc = EMClient.getInstance().chatManager().deleteConversation(bean.getImuId(), true);
                if (isDeleteSuc)
                    HuanXinIMHelper.getInstance().deleteContact(toEaseUser.getUsername());
                e.onNext(isDeleteSuc);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe((e) ->
                {
                    boolean result = (boolean) e;
                    if (result)
                        Logger.t(TAG).d("删除成功");
                    else
                        Logger.t(TAG).d("删除失败");
                    if (mPresenter != null)
                        mPresenter.deFriend(toCheckUserUid);
                });
    }


    private void sendCMDMsg(String action, String toChatUserName)
    {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setChatType(EMMessage.ChatType.Chat);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(action);
        message.setTo(toChatUserName);
        message.addBody(cmdMessageBody);

        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @Override
    public void focusCallBack(String bodyStr)
    {
        try
        {
            Logger.t(TAG).d("关注成功");
            ToastUtils.showShort("关注成功");
            if ("GoodSaleHostFrg".equals(openFrom))
                finishResult = GoodSaleHostFrg.RESULT_FROM_USERINFO;
            tvAddFocusHost.setText("已关注");
            tvAddFocusHost.setEnabled(false);
            isFocus = true;
            if (titlePopup != null && !titlePopup.checkIsAdded("修改备注"))
            {
                titlePopup.addAction(0, new ActionItemBean(mAct, "修改备注", "{eam-e60a}", "0"));
            }
            tvAddFocusHost.setBackgroundResource(R.drawable.shape_pay_sure_normal);
            if (tvHello.getText().equals("打招呼"))
            {
                tvHello.setText("发消息");
            }
            action = ACTION_FOCUS;
            //发送直播间关注消失
            Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_HIDE_FOCUS);
            mAct.sendBroadcast(intent);
        } catch (Exception e)
        {
            Logger.t(TAG).d("异常》》" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void addWishCallBack(String bodyStr)
    {
        Logger.t(TAG).d("添加到约会管家成功");
        ToastUtils.showShort("添加到约会管家成功");
        tvYue.setText("已添加");
        tvYue.setEnabled(false);
        tvYue.setBackgroundResource(R.drawable.shape_pay_sure_normal);
    }

    @Override
    public void queryUsersRelationShipCallBack(String bodyStr)
    {
        Logger.t(TAG).d("关系》》bodyStr" + bodyStr);
        try
        {
            JSONObject jsonObject = new JSONObject(bodyStr);
            String isFocus = jsonObject.getString("focus");
            String inBlack = jsonObject.getString("inBlack");
            String isSayHello = jsonObject.getString("isSayHello");
            if ("2".equals(inBlack) || "3".equals(inBlack))
            {
                if (titlePopup != null)
                    titlePopup.removeAction("拉黑");
            }
            //是否关注(0:无 1：是);
            if (TextUtils.equals("0", isFocus))
            {
                tvAddFocusHost.setText("+ 关注");
                tvAddFocusHost.setEnabled(true);
                tvHello.setText("打招呼");
                tvHello.setVisibility(View.VISIBLE);
                tvAddFocusHost.setBackgroundResource(R.drawable.shape_pay_sure_press);
            }
            else
            {
                tvAddFocusHost.setText("已关注");
                tvAddFocusHost.setEnabled(false);
                tvHello.setText("发消息");
                tvHello.setVisibility(View.VISIBLE);
                tvAddFocusHost.setBackgroundResource(R.drawable.shape_pay_sure_normal);
                if (titlePopup != null && !titlePopup.checkIsAdded("修改备注"))
                {
                    titlePopup.addAction(0, new ActionItemBean(mAct, "修改备注", "{eam-e60a}", "0"));
                }

            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void checkUserRoleCallback(String myUid, String myRole, String checkedUid, String checkedRole)
    {
        Logger.t(TAG).d("myUid--> " + myUid + " , myRole-->" + myRole + " , checkedUid--> " + checkedUid + " , checkedRole--> " + checkedRole);
        if (checkedUid.equals(myUid))//自己查看自己
        {
            tvShutUp.setText("");
            tvShutUp.setTag("");
        }
        else//查看的是其他用户
        {
            switch (checkedRole)
            {
                case "1"://查看的是主播
                    switch (myRole)
                    {
                        case "2":
                        case "0":
                            tvShutUp.setText("");
                            tvShutUp.setTag("");
                            break;
                        default:
                            break;
                    }
                    break;
                case "2"://查看的是管理员
                    switch (myRole)
                    {
                        case "1":
                            if (mPresenter != null)
                                mPresenter.checkUserShutUpState(currentUserAvRoomId, toCheckUserUid);
                            break;
                        case "2":
                        case "0":
                            tvShutUp.setText("");
                            tvShutUp.setTag("");
                            break;
                        default:
                            break;
                    }
                    break;
                case "0": // 查看的是普通用户
                    switch (myRole)
                    {
                        case "1":
                        case "2":
                            if (mPresenter != null)
                                mPresenter.checkUserShutUpState(currentUserAvRoomId, toCheckUserUid);
                            break;
                        case "0":
                            tvShutUp.setText("");
                            tvShutUp.setTag("");
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void checkRedPacketStatsCallback(String response)
    {
        try
        {
            JSONObject body = new JSONObject(response);
            Logger.t(TAG).d("userName:checkRedPacketStatsCallback：" + response);
            if ("true".equals(body.getString("flag")))//说明有没有领取的红包
            {
                new CustomAlertDialog(mAct)
                        .builder()
                        .setTitle("提示")
                        .setMsg("您有未领取的红包，是否确认清空所有聊天记录？")
                        .setPositiveButton("确定", (v) -> deleteConBaseType())
                        .setNegativeButton("取消", (v) ->
                        {
                        }).show();
            }
            else
            {
                deleteConBaseType();
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    private void sendBroadcast(String action)
    {
        HashMap<String, String> paraMap = new HashMap<>();
        paraMap.put("toCheckUserUid", toCheckUserUid);
        paraMap.put("toCheckUserNicName", toCheckUserNicName);
        paraMap.put("toCheckUserIdT", toCheckUserIdT);
        paraMap.put("currentUserAvRoomId", currentUserAvRoomId);
        paraMap.put("chatRoomId", chatRoomId);
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("param", paraMap);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed()

    {
        Intent data = new Intent();
        data.putExtra("isFocus", isFocus);
        data.putExtra("isBlack", isBlack);
        data.putExtra("isEditName", isEditName);
        data.putExtra("editName", editName);
        data.putExtra("action", action);
        data.putExtra("position", position);

        if (action.equals(ACTION_REMARK) || action.equals(ACTION_FOCUS_REMARK))
        {
            data.putExtra(Constant.EXTRA_TO_EASEUSER, toEaseUser);
            finishResult = RESULT_OK;
        }
        setResult(finishResult, data);
        finish(); //关闭掉这个Activity
        overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_TRENDS_PLAY_VIDEO:
                isFromVideo = true;
                break;
            case EamConstant.EAM_OPEN_DATEHOUSE_DETAIL:
                if (mPresenter != null)
                    mPresenter.checkUserInfo(toCheckUserUid);
                break;
            case OPEN_CHATACTIVITY:
                if (mPresenter != null)
                    mPresenter.queryUsersRelationShip(toCheckUserUid);
                break;
            default:
                break;
        }
        if (momentsFrg != null)
        {
            momentsFrg.onActivityResult(requestCode, resultCode, data);
        }
        if (vpArticalFrg != null)
        {
            vpArticalFrg.onActivityResult(requestCode, resultCode, data);
        }
        if (mFragments != null)
        {
            mFragments.get(2).onActivityResult(requestCode, resultCode, data);
        }
    }
}
