package com.echoesnet.eatandmeet.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CaptureActivity;
import com.echoesnet.eatandmeet.activities.NotificationCenterAct;
import com.echoesnet.eatandmeet.activities.TrendsPublishAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.eventmsgs.HomeEvent;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DragLayout;
import com.echoesnet.eatandmeet.views.widgets.FindMoveView;
import com.echoesnet.eatandmeet.views.widgets.ScrollTextTabView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier ben
 * @createDate 2017/3/29
 * @description
 */
@RuntimePermissions
public class FindFragment extends BaseFragment implements ViewPager.OnPageChangeListener
{
    private final String TAG = FindFragment.class.getSimpleName();
    @BindView(R.id.vp_find)
    ViewPager viewPager;
    @BindView(R.id.top_bar)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.text_tab_view)
    ScrollTextTabView scrollTextTabView;
    @BindView(R.id.find_move_view)
    FindMoveView findMoveView;
    @BindView(R.id.text_1)
    TextView text1;
    @BindView(R.id.tv_red_point1)
    TextView tvRedPoint1;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.text_2)
    TextView text2;
    @BindView(R.id.tv_red_point2)
    TextView tvRedPoint2;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.text_3)
    TextView text3;
    @BindView(R.id.tv_red_point3)
    TextView tvRedPoint3;
    @BindView(R.id.linearLayout3)
    LinearLayout linearLayout3;
    @BindView(R.id.text_4)
    TextView text4;
    @BindView(R.id.tv_red_point4)
    TextView tvRedPoint4;
    @BindView(R.id.linearLayout4)
    LinearLayout linearLayout4;
    @BindView(R.id.ll_father)
    LinearLayout llFather;
    @BindView(R.id.iv_day_seven_layout)
    DragLayout ivSevenL;
    @BindView(R.id.iv_day_seven)
    ImageView ivDaySeven;
    @BindView(R.id.move_ll)
    LinearLayout moveLl;
    @BindView(R.id.title_line)
    TextView titleLine;

    //    @BindView(R.id.ptrv_base)
//    PullToRefreshScrollView ptrvBase;
    private RelativeLayout findTitleView;
    //    private RelativeLayout findNewTitle;
    private FragmentActivity mActivity;
    private List<Fragment> fragments;
    private FEncounterFrg encounterFrg;
    private FTrendsFrg trendsFrg;
    private VProductionFrg vTrendsFrg;
    private ImageView titleView;
    private GameListFrg gameListFrg;
    private List<Map<String, TextView>> navBtns;
    private int currentPosition = 0;

    private String refreshMsgInfo;
    private String refreshSysMsg;
    private String refreshBigVInfo;
    private TextView rightRedPoint;
    private boolean isIgnore = false;
    private boolean mReceiverTag = false;   //广播接受者标识
    private boolean isFirstShow = true;
    private boolean isFirstShow1 = true;
    private int topHeight = 150;
    List<TextView> topText = new ArrayList<>();
    private Unbinder unbind;

    private int[] imageTitleBlack = {R.drawable.find_title_meetyou, R.drawable.find_title_message,
            R.drawable.find_title_articles, R.drawable.find_title_game_party};
    private int[] imageTitleWhite = {R.drawable.find_title_meetyou_white, R.drawable.find_title_message_white,
            R.drawable.find_title_articles_white, R.drawable.find_title_game_party_white};
    private boolean isWhiteBg = false;//根据
    private boolean isTrendsHasMsg = false;
    private boolean isSetWhiteText = true;
    private boolean isSetBlackText = true;

    public static FindFragment newInstance()
    {
        return new FindFragment();
    }

    public int getCurrentPosition()
    {
        return currentPosition;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            savedInstanceState.putParcelable(EamConstant.FRAGMENTS_TAG, null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_find, container, false);
        findTitleView = view.findViewById(R.id.find_new_title);
        unbind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        afterViews();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateMsgIndicator(currentPosition, false, "");
        findMoveView.setFocusable(true);
        findMoveView.setFocusableInTouchMode(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        if (isVisibleToUser)
        {
            updateMsgIndicator(currentPosition, false, "");
        }
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    private void afterViews()
    {
        if (EamApplication.getInstance().getChannelResult != 1)
            linearLayout4.setVisibility(View.VISIBLE);
        mActivity = getActivity();
        fragments = new ArrayList<>();
        encounterFrg = new FEncounterFrg();
        fragments.add(encounterFrg);
        trendsFrg = FTrendsFrg.getInstance();
        fragments.add(trendsFrg);
        vTrendsFrg = new VProductionFrg();
        fragments.add(vTrendsFrg);
        if (EamApplication.getInstance().getChannelResult != 1)
        {
            gameListFrg = new GameListFrg();
            fragments.add(gameListFrg);
        }
        encounterFrg.setFindFragment(this);
        //文字 ： 邂逅 动态  大V 游戏
        topText.add(text1);
        topText.add(text2);
        topText.add(text3);
        if (EamApplication.getInstance().getChannelResult != 1)
            topText.add(text4);
        topHeight = CommonUtils.dp2px(mActivity, 75);
        View findTitle = topBarSwitch.inflateCustomCenter(R.layout.find_title_meet_you, new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                if (viewPager.getCurrentItem() == 0)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        FindFragmentPermissionsDispatcher.onCameraPermGrantedWithPermissionCheck(FindFragment.this);
                    else
                        onCameraPermGranted();
                }
            }

            @Override
            public void right2Click(View view)
            {
                if (currentPosition == 0)
                {
                    Intent intent = new Intent(getActivity(), NotificationCenterAct.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getActivity(), TrendsPublishAct.class);
                    startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_PUBLISH);
                }
            }

            @Override
            public void topDoubleClick(View view)
            {
                super.topDoubleClick(view);
                Logger.t(TAG).d("----->topDoubleClick()");
                if (currentPosition == 1)
                {
                    if (trendsFrg != null)
                        trendsFrg.scroll2Top();
                    findMoveView.smoothScrollTo(0, 0);
                }
            }
        }); //添加 meetyou
        titleView = (ImageView) findTitle.findViewById(R.id.find_top_title_image);

        initScrollListener();

        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});
        rightRedPoint = navBtns.get(1).get(TopBarSwitch.NAV_BTN_NOTE);
        rightRedPoint.setBackgroundResource(R.drawable.shape_rec_r20dp_c0319_bg);
        rightRedPoint.setVisibility(View.INVISIBLE);

        initTopBarBtn(0);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager())
        {
            @Override
            public Fragment getItem(int position)
            {
                return fragments.get(position);
            }

            @Override
            public int getCount()
            {
                return fragments.size();
            }
        });
        viewPager.addOnPageChangeListener(this);
        //必须先设置1 然后设置0 否则 tab状态不变
        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);

        initTab();
        String[] scrollTabTitle;
        if (EamApplication.getInstance().getChannelResult != 1)
            scrollTabTitle = new String[]{"邂 逅", "动 态", "大V专栏", "游戏专区"};
        else
            scrollTabTitle = new String[]{"邂 逅", "动 态", "大V专栏"};
        scrollTextTabView.beginTextData(scrollTabTitle);
        scrollTextTabView.setViewpager(viewPager);

        registerBroadcastReceiver();

        initWindow();
    }


    private void initWindow()
    {
        ivDaySeven.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.prize_gift));
        ivSevenL.setOnClickListenern(new DragLayout.OnClickListenern()
        {
            @Override
            public void onClick()
            {
                if (encounterFrg != null)
                {
                    encounterFrg.showTodayDialog();
                }
            }

        });
        if (encounterFrg != null)
        {
            encounterFrg.setClickListener(new FEncounterFrg.WindowDissmissListener()
            {
                @Override
                public void onDissmiss(String flag)
                {
                    if ("1".equals(flag))
                    {
                        AnimationDrawable animationDrawable = (AnimationDrawable) ivDaySeven.getDrawable();
                        animationDrawable.start();
                    }
                    else
                    {
                        ivDaySeven.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.prize_gift));
                    }

                }
            });
        }

    }

    /**
     * topbar顶部 图片切换
     *
     * @param view     view
     * @param position 当前position
     */
    private void setTopTitleImage(ImageView view, int position)
    {
        int[] titleImage = isWhiteBg ? imageTitleWhite : imageTitleBlack;
        view.setImageResource(titleImage[position]);
        selectTitle(topText, position);
    }

    /**
     * 更新发现 头部 四个item 上 红点
     *
     * @param currentPosition 当前position
     * @param isShow          是否显示
     * @param nums            显示 数量  当前 只有 position==1 时 可以显示数量
     */
    private void updateMsgIndicator(int currentPosition, boolean isShow, String nums)
    {
        int visibility = isShow ? View.VISIBLE : View.INVISIBLE;
        int num = 0;
        try
        {
            if ("99+".equals(nums)) //后台 会返回 99+ 所以 特殊 处理下
                nums = "100";
            if (!TextUtils.isEmpty(nums))
                num = Integer.parseInt(nums);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        switch (currentPosition)
        {
            case 0:
                if (tvRedPoint1 != null)
                {
//                    if (num != 0)
//                        tvRedPoint1.setText(String.valueOf(num));
                    tvRedPoint1.setVisibility(visibility);
                }
                break;
            case 1:
                if (tvRedPoint2 != null)
                {
                    if (num > 0 && num <= 99)
                        tvRedPoint2.setText(String.valueOf(num));
                    else
                        tvRedPoint2.setText("99+");
                    tvRedPoint2.setVisibility(visibility);
                }
                break;
            case 2:
                if (tvRedPoint3 != null)
                {
//                    if (num != 0)
//                        tvRedPoint3.setText(String.valueOf(num));
                    tvRedPoint3.setVisibility(visibility);
                }
                break;
            case 3:
                if (tvRedPoint4 != null)
                {
//                    if (num != 0)
//                        tvRedPoint4.setText(String.valueOf(num));
                    tvRedPoint4.setVisibility(visibility);
                }
                break;
        }
    }

    /**
     * 切换字体颜色
     *
     * @param shareWays
     * @param index
     */
    private void selectTitle(List<TextView> shareWays, int index)
    {
        for (int i = 0; i < shareWays.size(); i++)
        {
            TextView cb = shareWays.get(i);
            if (index != i)
            {
                cb.setTextColor(ContextCompat.getColor(mActivity, R.color.C0321));
            }
            else
            {
                cb.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
            }

        }

    }

    private void initScrollListener()
    {

        int screenHeight = CommonUtils.getScreenHeight1(mActivity);

        int vpHeight = screenHeight - CommonUtils.dp2px(mActivity, 68) - CommonUtils.dp2px(mActivity, 116) + CommonUtils.dp2px(mActivity, 71);
        //动态设置vp高度
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, vpHeight - CommonUtils.dp2px(mActivity, 42));
        viewPager.setLayoutParams(params);
        findMoveView.setDispatchView(viewPager);
        findMoveView.setMoveViewStateListener(new FindMoveView.MoveViewStateListener()
        {
            @Override
            public void onTitleViewIsGone()
            {
                if (scrollTextTabView.getVisibility() == View.GONE)
                {
                    scrollTextTabView.setVisibility(View.VISIBLE);
                    titleLine.setVisibility(View.VISIBLE);
                }

                if (scrollTextTabView.getVisibility() == View.VISIBLE)
                {
                    linearLayout1.setClickable(false);
                    linearLayout2.setClickable(false);
                    linearLayout3.setClickable(false);
                    linearLayout4.setClickable(false);
                }

                scrollTextTabView.getParent().requestLayout();

            }

            @Override
            public void onTitleViewIsShow()
            {
                if (scrollTextTabView.getVisibility() == View.VISIBLE)
                {
                    scrollTextTabView.setVisibility(View.GONE);
                    titleLine.setVisibility(View.GONE);
                }

                if (scrollTextTabView.getVisibility() == View.GONE)
                {
                    linearLayout1.setClickable(true);
                    linearLayout2.setClickable(true);
                    linearLayout3.setClickable(true);
                    linearLayout4.setClickable(true);
                }

                scrollTextTabView.getParent().requestLayout();
            }

            @Override
            public void onMoveChange(float x, float y)
            {
                float f = Math.abs(y) / topHeight;
                Logger.t(TAG).d("颜色透明度：" + f);
                if (f > 1.0f)
                {
                    f = 1.0f;
                }

                topBarSwitch.setBackground(new ColorDrawable(changeAlpha(ContextCompat.getColor(mActivity, R.color.C0321), f)));
                if (f > 0.96f)
                    findMoveView.setTitleHide(true);
                else if (f < 0.1f)
                    findMoveView.setTitleHide(false);
                if (f > 0.6)
                {
                    isWhiteBg = true;
                    setTopTitleImage(titleView, currentPosition);
                    navBtns.get(0).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mActivity, R.color.C0324));
                    navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mActivity, R.color.C0324));
                    if (isSetWhiteText)
                    {
                        CommonUtils.setStatusBarDarkMode(getActivity(), false);
                        isSetWhiteText = false;
                        isSetBlackText = true;
                    }
                }
                else
                {
                    isWhiteBg = false;
                    setTopTitleImage(titleView, currentPosition);
                    navBtns.get(0).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
                    navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
                    if (isSetBlackText)
                    {
                        CommonUtils.setStatusBarDarkMode(getActivity(), true);
                        isSetBlackText = false;
                        isSetWhiteText = true;
                    }
                }
            }
        });

        if (encounterFrg != null)
        {
            encounterFrg.setOnShowFindTitleWithoutPicListener(new FEncounterFrg.OnShowFindTitleWithoutPicListener()
            {
                @Override
                public void onShowFindTitle()
                {
                    findMoveView.setWithPicTitleShow(true);
                    if (scrollTextTabView.getVisibility() == View.VISIBLE)
                        scrollTextTabView.setVisibility(View.GONE);
                }

                @Override
                public void onMoveOrientation(String moveOrientation)
                {
                    findMoveView.setMoveOrientation(moveOrientation);
                }
            });
        }
        if (trendsFrg != null)
        {
            trendsFrg.setOnFindFrgShowTitleListener(new FTrendsFrg.OnFindFrgShowTitleListener()
            {
                @Override
                public void onMoveOrientation(String moveOrientation)
                {
                    if (findMoveView != null)
                        findMoveView.setMoveOrientation(moveOrientation);
                }
            });
        }
    }

    public boolean isWhiteBg()
    {
        return isWhiteBg;
    }

    private void initTab()
    {
        scrollTextTabView.setClickColor(R.color.C0412);
        scrollTextTabView.setDefaultColor(R.color.C0321);
        scrollTextTabView.setScrollbarColor(R.color.C0412);
        scrollTextTabView.setTextSize(15);
        scrollTextTabView.setScrollbarSize(0);
        scrollTextTabView.setTextBoldStyle(ScrollTextTabView.TEXTMODE_DEFAULTBOLD);
    }

    public FEncounterFrg getEncounterFrg()
    {
        return encounterFrg;
    }

    /**
     * @Description: 显示新手引导
     */
    private void showNewbieGuide(boolean isTrends)
    {
        if (isTrends && SharePreUtils.getIsNewBieTrends(mActivity))
        {
            NetHelper.checkIsShowNewbie(mActivity, "2", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        final FrameLayout fRoot = (FrameLayout) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mActivity, R.layout.view_newbie_guide_find, null);
                        vGuide.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        });
                        RelativeLayout relativeLayout = (RelativeLayout) vGuide.findViewById(R.id.rl_encounter_newbie);
                        ImageView imgReleaseDynamic = (ImageView) vGuide.findViewById(R.id.img_release_dynamic);
                        relativeLayout.setVisibility(View.GONE);
                        imgReleaseDynamic.setVisibility(View.VISIBLE);
                        imgReleaseDynamic.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                fRoot.removeView(vGuide);
                                SharePreUtils.setIsNewBieTrends(getActivity(), false);
                                NetHelper.saveShowNewbieStatus(getActivity(), "2");
//                                trendsFrg.showNewbieGuide();
                            }
                        });
                        fRoot.addView(vGuide);
                    }
                    else
                    {
                        SharePreUtils.setIsNewBieTrends(getActivity(), false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    }

    private void registerBroadcastReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(EamConstant.EAM_REFRESH_MSG);
        filter.addAction(EamConstant.EAM_REFRESH_SYS_MSG);
        filter.addAction(EamConstant.EAM_REFRESH_IGNORE_SYS_MSG);
        filter.addAction(EamConstant.EAM_REFRESH_BIGV_MSG);
        filter.addAction(EamConstant.EAM_REFRESH_IGNORE_BIGV_MSG);
        Logger.t(TAG).d("注册广播");
        mActivity.registerReceiver(updateUiReceiver, filter);
        mReceiverTag = true;
    }

    BroadcastReceiver updateUiReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.t(TAG).d("findfrag:" + action);
            switch (action)
            {
                case EamConstant.EAM_REFRESH_MSG://更新动态数量
                    refreshMsgInfo = intent.getExtras().getString("trend_counts");
                    if (!TextUtils.equals("0", refreshMsgInfo))
                    {
                        updateMsgIndicator(1, true, refreshMsgInfo);
                    }
                    break;
                case EamConstant.EAM_REFRESH_BIGV_MSG://更新大V红点
                    refreshBigVInfo = intent.getExtras().getString("bigv_counts");
                    if (!TextUtils.equals("0", refreshBigVInfo))
                    {
                        updateMsgIndicator(2, true, "");
                        Logger.t(TAG).d("EAM_REFRESH_BIGV_MSG");
                    }
                    break;
                case EamConstant.EAM_REFRESH_IGNORE_BIGV_MSG://隐藏大V红点
                    if (topBarSwitch != null)
                    {
                        updateMsgIndicator(2, false, "");
                    }
                    break;
                case EamConstant.EAM_REFRESH_SYS_MSG://更新系统通知
                    refreshSysMsg = intent.getExtras().getString("sys_counts");
                    Logger.t(TAG).d("更新系统通知>>" + refreshSysMsg);
                    if (TextUtils.equals("0", refreshSysMsg))
                    {
                        rightRedPoint.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        if (currentPosition == 0)
                        {
                            rightRedPoint.setText(refreshSysMsg);
                            rightRedPoint.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case EamConstant.EAM_REFRESH_IGNORE_SYS_MSG:
                    isIgnore = intent.getExtras().getBoolean("ignore");
                    if (isIgnore)
                    {
                        if (rightRedPoint != null)
                        {
                            rightRedPoint.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void setCurrentPage(int page)
    {
        if (viewPager != null)
            viewPager.setCurrentItem(page, false);
    }

    ;

    public void setData(int requestCode, int resultCode, Intent intent)
    {
        if (encounterFrg != null)
            encounterFrg.setCode(requestCode, resultCode, intent);
    }

    private void initTopBarBtn(int position)
    {
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON);
            tv.setVisibility(View.VISIBLE);
            switch (i)
            {
                case 0:
                    if (position == 0)
                    {
                        tv.setVisibility(View.VISIBLE);
                        tv.setTextSize(20);
                        tv.setText("{eam-e987}");
                        tv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.transparent));
                    }
                    else if (position == 1)
                    {
                        tv.setVisibility(View.GONE);
                    }
                    else if (position == 2)
                    {
                        tv.setVisibility(View.GONE);
                    }
                    else if (position == 3)
                    {
                        tv.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    if (rightRedPoint == null)
                        rightRedPoint = navBtns.get(1).get(TopBarSwitch.NAV_BTN_NOTE);
                    rightRedPoint.setVisibility(View.INVISIBLE);
                    Logger.t(TAG).d("红点显示》" + refreshSysMsg + " 是否是邂逅》" + position);
                    if (position == 0)
                    {
                        tv.setTextSize(20);
                        tv.setText("{eam-s-bell}");
                        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) rightRedPoint.getLayoutParams();
                        param.height = CommonUtils.dp2px(mActivity, 14);
                        param.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        param.setMargins((0 - CommonUtils.dp2px(mActivity, 14)), CommonUtils.dp2px(mActivity, 7), 0, 0);
                        rightRedPoint.setMinWidth(CommonUtils.dp2px(mActivity, 14));
                        rightRedPoint.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                        rightRedPoint.setLayoutParams(param);
                        rightRedPoint.getParent().requestLayout();

                        if (TextUtils.equals("0", refreshSysMsg) || TextUtils.isEmpty(refreshSysMsg))
                        {
                            rightRedPoint.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            rightRedPoint.setText(refreshSysMsg);
                            rightRedPoint.setVisibility(View.VISIBLE);
                        }
                    }
                    else if (position == 1)
                    {
                        tv.setTextSize(20);
                        tv.setText("{eam-s-spades}");
                    }
                    else if (position == 2)
                    {
                        tv.setVisibility(View.GONE);
                    }
                    else if (position == 3)
                    {
                        tv.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    }

    @Subscribe
    public void onEvent(HomeEvent homeEvent)
    {
        if (homeEvent.getType().equals("1"))
        {
//            showNewBieGuide();
        }
    }

    public void upBigVMomentsRed()
    {
        if (topBarSwitch != null)
        {
            updateMsgIndicator(2, true, "");
            Logger.t(TAG).d("upBigVMomentsRed");
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (updateUiReceiver != null)
        {
            if (mReceiverTag)
            {
                mReceiverTag = false;
                mActivity.unregisterReceiver(updateUiReceiver);
            }
        }
        if (unbind != null)
            unbind.unbind();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }


    @Override
    public void onPageSelected(int position)
    {
        Logger.t(TAG).d("onPageSelected:" + position);
//        topBarSwitch.changeSwitchBtn(position);

        currentPosition = position;
        initTopBarBtn(position);
        setTopTitleImage(titleView, currentPosition);
        switch (position)
        {
            case 0:
//                if (isTrendsHasMsg)//切换到邂逅页隐藏动态互动通知
//                    tvTrendsMsg.setVisibility(View.GONE);
                if (isFirstShow)
                    isFirstShow = false;
                else
                {
                    FEncounterFrg fEncounterFrg = (FEncounterFrg) fragments.get(0);
                    if (fEncounterFrg != null)
                        fEncounterFrg.showNewbieGuide();
                }
                break;
            case 1:
//                if (isTrendsHasMsg)//切换到动态页如果有动态互动通知就 显示
//                    tvTrendsMsg.setVisibility(View.VISIBLE);
                if (!isFirstShow1)
                {
                    Logger.t("动态数量设置").d("动态数量设置" + SharePreUtils.getFocusTrendsCount(mActivity));
                    SharePreUtils.setFocusTrendsCount(mActivity, 0);
                }
                else
                    isFirstShow1 = false;
                if (!TextUtils.equals("0", refreshMsgInfo) || tvRedPoint2.getVisibility() == View.VISIBLE)
                {
                    if (trendsFrg != null)
                        trendsFrg.refreshData();
                }
                refreshMsgInfo = "0";
                updateMsgIndicator(position, false, "");
                break;
            case 2:
//                if (isTrendsHasMsg)//切换到大V页隐藏动态互动通知
//                    tvTrendsMsg.setVisibility(View.GONE);
                if (tvRedPoint3.getVisibility() == View.VISIBLE)
                {
                    VProductionFrg vproductionFrg = (VProductionFrg) fragments.get(2);
                    if (vproductionFrg != null)
                        vproductionFrg.refreshData();
                }
                updateMsgIndicator(position, false, "");
                break;
            case 3:
//                if (isTrendsHasMsg)//切换到游戏页隐藏动态互动通知
//                    tvTrendsMsg.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    /**
     * 刷新动态数据
     */
    public void refreshTrendsData()
    {
        if (trendsFrg != null)
            trendsFrg.refreshData();
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction)
    {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
        Logger.t(TAG).d("onPageScrollStateChanged:" + state);
        switch (state)
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FindFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA})
    void onCameraPermGranted()
    {
        Logger.t(TAG).d("允许获取权限");
        boolean hasCameraPermission = CommonUtils.cameraIsCanUse();
        if (hasCameraPermission)
        {
            Intent intent = new Intent(getActivity(), CaptureActivity.class);
            CommonUtils.jumpHelperId = "-1";
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
        }
        else
        {
            ToastUtils.showShort("请释放相机资源");
        }
    }

    @OnPermissionDenied({Manifest.permission.CAMERA})
    void onCameraPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA})
    void onCameraPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        CommonUtils.openPermissionSettings(getActivity(), getString(R.string.per_permission_never_ask).replace(CommonUtils.SEPARATOR, "摄像机"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (trendsFrg != null)
        {
            trendsFrg.onActivityResult(requestCode, resultCode, data);
        }
        if (encounterFrg != null)
            encounterFrg.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EamConstant.EAM_OPEN_TRENDS_PUBLISH && resultCode == RESULT_OK)
        {
            findMoveView.scrollTo(0, 0);
        }
    }

    @OnShowRationale({Manifest.permission.CAMERA})
    void onCameraPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mActivity)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("需要使用您的相机才能完成此功能！")
                .setPositiveButton("允许", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.cancel();
                    }
                }).show();
    }

    @OnClick({R.id.linearLayout1, R.id.linearLayout2, R.id.linearLayout3, R.id.linearLayout4})
//, R.id.tv_trends_msg
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.linearLayout1:
                if (viewPager != null)
                    viewPager.setCurrentItem(0);
                break;
            case R.id.linearLayout2:
                if (viewPager != null)
                    viewPager.setCurrentItem(1);
                break;
            case R.id.linearLayout3:
                if (viewPager != null)
                    viewPager.setCurrentItem(2);
                break;
            case R.id.linearLayout4:
                if (viewPager != null)
                    viewPager.setCurrentItem(3);
                break;
            /*case R.id.tv_trends_msg:
                Intent intent = new Intent(getActivity(), TrendsMsgAct.class);
                startActivity(intent);
                tvTrendsMsg.setVisibility(View.GONE);
                Intent broadcastIntent = new Intent(EamConstant.EAM_REFRESH_TREND_MSG);
                broadcastIntent.putExtra("trend_msg", "0");
                getActivity().sendBroadcast(broadcastIntent);
                break;*/
        }
    }
}
