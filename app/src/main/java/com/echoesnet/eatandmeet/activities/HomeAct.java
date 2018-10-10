package com.echoesnet.eatandmeet.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mobstat.StatService;
import com.echoesnet.eatandmeet.BuildConfig;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.liveplay.View.LivePlayAct1;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.AroundFrg;
import com.echoesnet.eatandmeet.fragments.FEncounterFrg;
import com.echoesnet.eatandmeet.fragments.FindFragment;
import com.echoesnet.eatandmeet.fragments.LivePlayFrg;
import com.echoesnet.eatandmeet.fragments.MealAndClubFrg;
import com.echoesnet.eatandmeet.fragments.MyInfoFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.IOnAroundPageChangedListener;
import com.echoesnet.eatandmeet.models.eventmsgs.HomeEvent;
import com.echoesnet.eatandmeet.presenters.ImpIHomePre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IHomeActView;
import com.echoesnet.eatandmeet.services.UpdateAPKService;
import com.echoesnet.eatandmeet.utils.BigGiftUtil.BigGiftUtil;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.LocationUtils.LocationUtils;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlterDialogs.DialogWith2BtnAtBottom;
import com.echoesnet.eatandmeet.views.widgets.DownloadAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.NoScrollViewPager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.soundcloud.android.crop.Crop;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zhy.autolayout.AutoRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 程序主页
 */
@RuntimePermissions
public class HomeAct extends MVPBaseActivity<IHomeActView, ImpIHomePre> implements IHomeActView
{
    private static final String TAG = HomeAct.class.getSimpleName();

    private NoScrollViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabWidget mTabWidget;
    private TextView unreadMsgLabel;
    private int[] tabTitles = {R.layout.master_tabtitle_1, R.layout.master_tabtitle_2, R.layout.master_tabtitle_liveplay,
            R.layout.master_tabtitle_3, R.layout.master_tabtitle_4};
    private List<Drawable> iconNormal = new ArrayList<>();
    private List<Drawable> iconSelected = new ArrayList<>();
    private AutoRelativeLayout[] tabsWidget = new AutoRelativeLayout[tabTitles.length];
    private int currentPage = 0;
    private long exitTime = 0;
    private String subPageIndexOfAroundPage;//要展示的第二个tab的子tab的index
    public boolean isConflict = false;
    private boolean isCurrentAccountRemoved = false;
    private IOnAroundPageChangedListener mOnAroundPageChangedListener;

    private Activity mAct;
    private EventBus eventBus;
    private Dialog gifDialog;
    private List<Fragment> mFragments;
    private MyInfoFrg myInfoFrg;
    private AroundFrg tab02;
    private MealAndClubFrg tab03;
    private FindFragment tab01;
    private boolean isFirst = true;
    //region 定位相关
    private LocationClient mLocClient;
    private MyLocationListener myLocationListener;
    //endregion
    // 我的未读消息
    private TextView unreadMsgLabelMyInfo;
    public boolean hasNewTaskOk = false;
    private View viewLine;
    private long start;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        start = System.currentTimeMillis();
        mAct = this;
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.color.white);
//        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.act_home);
        mTabWidget = (TabWidget) findViewById(R.id.tw_home_bottom_tab);
        mTabWidget.setStripEnabled(false);
        viewLine = findViewById(R.id.view_line);
        mViewPager = (NoScrollViewPager) findViewById(R.id.vp_home);
        eventBus = new EventBus();
        initIconify(this);
        initMiPush();//小米推送初始化
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        for (int i = 0; i < tabsWidget.length; i++)
        {
            tabsWidget[i] = (AutoRelativeLayout) createTabView(this, tabTitles[i]);
            mTabWidget.addView(tabsWidget[i], layoutParams);
            tabsWidget[i].setOnClickListener(mTabClickListener);
        }
        initViewPager();
        registerBroadcastReceiver();
        if (mPresenter != null)
        {
            mPresenter.getVersionCode();
            mPresenter.downloadBgImg();
            mPresenter.getResourceFromCdn();
            mPresenter.getSensitiveWordsFromCdn(NetInterfaceConstant.FILE_SENSITIVE_WORDS);
            BigGiftUtil.startCheckBigGif(mAct, NetInterfaceConstant.FILE_GIFT_VERSION, true);
            EAMCheckCacheFiles.synchronize(NetInterfaceConstant.H5_VERSION, getFilesDir().getAbsolutePath());
            mPresenter.downloadProvinceData();
            mPresenter.hindAppFolder();
            mPresenter.sendLocationSwitch();
            mPresenter.updateTaskOk();
        }
        /*isInvited = getIntent().getStringExtra("isInvited");
        if (!TextUtils.isEmpty(isInvited))
        {
            showMakeUserInfoSuccessDialog();
        }*/
        Logger.t(TAG).d("time>>1>>:" + (System.currentTimeMillis() - start));
        if (savedInstanceState != null)
        {
            EamLogger.t(TAG).writeToDefaultFile("Home 页面已经被系统回收，现在恢复》" + "信息》" + savedInstanceState.getString(EamConstant.EAM_ACTIVITY_KILLED_BY_OS));
            if (savedInstanceState.getString(EamConstant.EAM_ACTIVITY_KILLED_BY_OS).equals(EamConstant.EAM_ACTIVITY_STATUS))
            {
                HuanXinIMHelper.getInstance().init(getApplicationContext());//初始化环信
                SDKInitializer.initialize(getApplicationContext());//初始化百度
            }
            savedInstanceState.putParcelable(EamConstant.FRAGMENTS_TAG, null);//删除保存的fragment，重新实例化
        }
        Logger.t(TAG).d("time>>2>>:" + (System.currentTimeMillis() - start));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            HomeActPermissionsDispatcher.onLocationPermGrantedWithPermissionCheck(this);
        StatService.start(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //Logger.t(TAG).d("HomeAct》onStart");
        //返回Home页时需要做相应的跳转,从聊天界面返回始终进入回话列表项
        currentPage = getIntent().getIntExtra("showPage", -1);
        if (currentPage != -1)
        {
            int showFindPage = getIntent().getIntExtra("showFindPage", -1);
            if (showFindPage != -1 && tab01 != null)
            {
                tab01.setCurrentPage(showFindPage);
                tab01.refreshTrendsData();
            }
            mViewPager.setCurrentItem(currentPage, false);
            //每次回来都清一下来源数据，以免进入其他界面回来回到次界面
            getIntent().putExtra("showPage", -1);
            getIntent().putExtra("showFindPage", -1);
        }

        if (currentPage == 1)
        {
            //如果进入的tab是邻座，则再根据传来的参数打开相应的页面
            subPageIndexOfAroundPage = getIntent().getStringExtra(EamConstant.EAM_AROUND_OPEN_SOURCE);
            //重置来源数据
            getIntent().putExtra(EamConstant.EAM_AROUND_OPEN_SOURCE, "reset");
            getIntent().putExtra("showPage", -1);
            //跳转到相应的page
            if (mOnAroundPageChangedListener != null)
                mOnAroundPageChangedListener.onPageIndexChanged(subPageIndexOfAroundPage);
        }
        currentPage = -1;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!isConflict && !isCurrentAccountRemoved && !isFirst)
        {
            updateUnreadLabel();
        }
        // unregister this event listener when this activity enters the background
        if (mAct != null)
            HuanXinIMHelper.getInstance().pushActivity(mAct);
        SharePreUtils.setToOrderMeal(mAct, "noDate");
        EamApplication.getInstance().dateStreamId = "noDate";
//        HuanXinIMHelper.getInstance().showSaveRedPacket();
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        HuanXinIMHelper.getInstance().popActivity(this);
    }

    @Override
    protected void onStop()
    {
        Logger.t(TAG).d("HomeAct》onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            if (gifDialog != null && gifDialog.isShowing())
            {
                gifDialog.dismiss();
                gifDialog = null;
            }
            if (mLocClient != null && mLocClient.isStarted())
                mLocClient.stop();
            Logger.t(TAG).d("onDestroy");
            SharePreUtils.setToOrderMeal(mAct, "");// 结束程序取消约会订单类型
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (isFirst)
        {
            isFirst = false;
            traverseFunctionWindow();
        }
    }

    //在onstart后执行
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Logger.t(TAG).d("onSaveInstanceState 执行");
        super.onSaveInstanceState(outState);
        outState.putString(EamConstant.EAM_ACTIVITY_KILLED_BY_OS, EamConstant.EAM_ACTIVITY_STATUS);
    }

    //region 暂时废弃
/*    private void showMakeUserInfoSuccessDialog()
    {
        this.isInvited = null;
        gifDialog = new Dialog(mAct, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mAct).inflate(R.layout.dialog_make_user_info_success, null);
        gifDialog.setContentView(contentView);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.image_money);
        ImageView imageView2 = (ImageView) contentView.findViewById(R.id.image_money2);
        TextView tvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        Button button = (Button) contentView.findViewById(R.id.btn_ok);
        int resMoney = 0;
        if ("true".equals(isInvited))
        {
            tvTitle.setText("完善资料并接受邀请");
            String regReward = getIntent().getStringExtra("regReward");
            String inviteReward = getIntent().getStringExtra("inviteReward");
            if (!TextUtils.isEmpty(regReward) && !TextUtils.isEmpty(inviteReward))
            {
                resMoney = Integer.parseInt(regReward) + Integer.parseInt(inviteReward);
            }
        }
        else
        {
            tvTitle.setText("完善资料成功");
            String regReward = getIntent().getStringExtra("regReward");
            if (!TextUtils.isEmpty(regReward))
            {
                resMoney = Integer.parseInt(regReward);
            }
        }
        int[] imgNums = new int[]{R.drawable.wszl_0_hdpi, R.drawable.wszl_1_hdpi, R.drawable.wszl_2_hdpi, R.drawable.wszl_3_hdpi,
                R.drawable.wszl_4_hdpi, R.drawable.wszl_5_hdpi, R.drawable.wszl_6_hdpi, R.drawable.wszl_7_hdpi, R.drawable.wszl_8_hdpi, R.drawable.wszl_9_hdpi};
        if (resMoney <= 9)
            imageView.setImageResource(imgNums[resMoney]);
        else if (resMoney < 100)//最大为15--wb
        {
            imageView.setImageResource(imgNums[resMoney / 10]);
            imageView2.setImageResource(imgNums[resMoney % 10]);
        }
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gifDialog.dismiss();
            }
        });
        gifDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                Logger.t(TAG).d("gifDialog被关闭");
                eventBus.post(new HomeEvent("1"));
            }
        });
        Logger.t(TAG).d("gifDialog显示");
        gifDialog.show();
    }*/
    //endregion

    @Override
    protected ImpIHomePre createPresenter()
    {
        return new ImpIHomePre();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);// 必须存储新的intent,否则getIntent()将返回旧的intent
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void onLocationPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，部分服务将受限");
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void onLocationPermGranted()
    {
        Logger.t(TAG).d("授权成功");
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void onLocationPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        CommonUtils.openPermissionSettings(mAct, getResources().getString(R.string.per_permission_never_ask).replace(CommonUtils.SEPARATOR, "地理位置"));
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION})
    void onRecordPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mAct)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("小饭需要您的地理位置来为您提供更好的服务！")
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.t(TAG).d("requestCode>" + requestCode + " permissions>" + Arrays.asList(permissions).toString() + " grantResults> " + grantResults);
        HomeActPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void requestLocationPerm()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            HomeActPermissionsDispatcher.onLocationPermGrantedWithPermissionCheck(HomeAct.this);
    }


    /**
     * 初始化4个tab
     */
    private void initViewPager()
    {
        Logger.t(TAG).d("HOME页的初始化fragment执行了");
        this.tab01 = FindFragment.newInstance();
        tab02 = AroundFrg.newInstance(subPageIndexOfAroundPage);
        tab03 = MealAndClubFrg.newInstance();
        myInfoFrg = MyInfoFrg.newInstance();
        LivePlayFrg tabLivePlay = LivePlayFrg.newInstance("");
        eventBus.register(this.tab01);
        mFragments = new ArrayList<>();
        mFragments.add(this.tab01);
        mFragments.add(tab02);
        mFragments.add(tabLivePlay);
        mFragments.add(tab03);
        mFragments.add(myInfoFrg);
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
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
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mTabWidget.setCurrentTab(currentPage);
        mTabWidget.getChildAt(0).setSelected(true);
        mViewPager.requestDisallowInterceptTouchEvent(false);

        if (mViewPager.getCurrentItem() == 0)
            mViewPager.setNoScroll(true);
        else
            mViewPager.setNoScroll(false);
    }

    private void initIconify(Context context)
    {
        iconNormal.clear();
        iconNormal.add(ContextCompat.getDrawable(context, R.drawable.ico_find));
        iconNormal.add(ContextCompat.getDrawable(context, R.drawable.ico_chat));
        iconNormal.add(ContextCompat.getDrawable(context, R.drawable.ico_live));
        iconNormal.add(ContextCompat.getDrawable(context, R.drawable.ico_food));
        iconNormal.add(ContextCompat.getDrawable(context, R.drawable.icon_my));
        iconSelected.clear();
        iconSelected.add(ContextCompat.getDrawable(context, R.drawable.ico_find_seleted));
        iconSelected.add(ContextCompat.getDrawable(context, R.drawable.ico_chat_seleted));
        iconSelected.add(ContextCompat.getDrawable(context, R.drawable.ico_live_seleted));
        iconSelected.add(ContextCompat.getDrawable(context, R.drawable.ico_food_seleted));
        iconSelected.add(ContextCompat.getDrawable(context, R.drawable.icon_my_seleted));
    }

    private void registerBroadcastReceiver()
    {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_START_UPLOAD_LOCATION);
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND);
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_SUCC_RED_REMIND);
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_TASK_RED_REMIND);
        myIntentFilter.addAction(EamConstant.EAM_HX_RECEIVE_RED_HOME);
        myIntentFilter.addAction(EamConstant.EAM_HX_RECEIVE_BIGV_RED_HOME);
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_RECEIVE_RED_MY_REMIND);
        //注册广播
        mAct.registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private View createTabView(Context context, int viewId)
    {
        View view = LayoutInflater.from(context).inflate(viewId, null);
        switch (viewId)
        {
            case R.layout.master_tabtitle_1:
                ((ImageView) (view.findViewById(R.id.iv_find))).setImageDrawable(
                        iconSelected.get(0));
                break;
            case R.layout.master_tabtitle_2:
                ImageView tab2 = (ImageView) (view.findViewById(R.id.iv_around));
                tab2.setImageDrawable(iconNormal.get(1));
                unreadMsgLabel = (TextView) view.findViewById(R.id.unread_msg_number);
                break;
            case R.layout.master_tabtitle_3:
                ((ImageView) (view.findViewById(R.id.iv_book_dinner))).setImageDrawable(
                        iconNormal.get(3));
                break;
            case R.layout.master_tabtitle_4:
                ((ImageView) (view.findViewById(R.id.iv_userinfo))).setImageDrawable(
                        iconNormal.get(4));
                unreadMsgLabelMyInfo = (TextView) view.findViewById(R.id.unread_msg_number);
                break;
            case R.layout.master_tabtitle_liveplay:
                ((ImageView) (view.findViewById(R.id.iv_live_play))).setImageDrawable(
                        iconNormal.get(2));
                break;
        }
        return view;
    }

    private View.OnClickListener mTabClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == tabsWidget[0])
            {
                mViewPager.setCurrentItem(0);
            }
            else if (v == tabsWidget[1])
            {
                mViewPager.setCurrentItem(1);
            }
            else if (v == tabsWidget[2])
            {
                mViewPager.setCurrentItem(2);
            }
            else if (v == tabsWidget[3])
            {
                mViewPager.setCurrentItem(3);
            }
            else if (v == tabsWidget[4])
            {
                mViewPager.setCurrentItem(4);
            }
        }
    };
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageSelected(int arg0)
        {
//            if (arg0 == 2)
//            {
//                String a = null;
//                int i = a.length();
//            }
            Fragment liveFrg = mFragments.get(2);

            if (arg0 != 0)
            {
                CommonUtils.setStatusBarDarkMode(mAct, true);
            }
            else
            {
                Fragment find = mFragments.get(0);
                if (((FindFragment) find).isWhiteBg())
                {
                    //设置为白字
                    CommonUtils.setStatusBarDarkMode(mAct, false);
                }
                else
                {
                    CommonUtils.setStatusBarDarkMode(mAct, true);
                }
            }

            if (liveFrg instanceof LivePlayFrg)
            {
                ((LivePlayFrg) liveFrg).isFromFind = false;
            }
            mTabWidget.setCurrentTab(arg0);
            for (int i = 0; i < mTabWidget.getTabCount(); i++)
            {
                if (arg0 == i)
                {
                    mTabWidget.getChildAt(i).setSelected(true);
                    ((ImageView) (((AutoRelativeLayout) mTabWidget.getChildAt(i)).getChildAt(0))).setImageDrawable(
                            iconSelected.get(i));
                }
                else
                {
                    mTabWidget.getChildAt(i).setSelected(false);
                    ((ImageView) (((AutoRelativeLayout) mTabWidget.getChildAt(i)).getChildAt(0))).setImageDrawable(
                            iconNormal.get(i));
                }
            }
            if (arg0 == 1 || arg0 == 0)
                mViewPager.setNoScroll(false);
            else
                mViewPager.setNoScroll(false);

            if (arg0 == 0)
            {
                final FEncounterFrg fEncounterFrg = tab01.getEncounterFrg();
                if (fEncounterFrg != null && listenerWindowMap.size() > 0)
                {
                    //检测窗口还有没弹完的继续弹
                    checkWindow("first", fEncounterFrg);
                }
            }

            //百度统计
            switch (arg0)
            {
                case 0:
                    //StatService.onEvent(mAct, "home_find", getString(R.string.baidu_other), 1);
                    break;
                case 1:
                    //StatService.onEvent(mAct, "home_around", getString(R.string.baidu_other), 1);
                    break;
                case 2:
                    Fragment liveFrag = mFragments.get(arg0);
                    if (liveFrag instanceof LivePlayFrg)
                    {
                        ((LivePlayFrg) liveFrag).setSource("3");
                    }
                    //StatService.onEvent(mAct, "home_live", getString(R.string.baidu_other), 1);
                    break;
                case 3:
                    if (mFragments.get(arg0) instanceof MealAndClubFrg)
                    {
                        ((MealAndClubFrg) mFragments.get(arg0)).showNewbieGuide();
                    }
                    break;
                case 4:
                    //StatService.onEvent(mAct, "home_my_info", getString(R.string.baidu_other), 1);
                    break;
            }

            if (arg0 != 3 && tab03 != null)
                tab03.getMealFrag().stopVideo();

            //更新我的页面信息
            if (arg0 == 4)
            {
                Intent intent = new Intent(EamConstant.EAM_USERINFO_PAGE_OPEN_SOURCE);
                sendBroadcast(intent);
            }
            //更新聊天信息
            if (arg0 == 1)
            {
                if (tab02 != null)
                    tab02.upFrgMomentsRed();
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {

        }

        @Override
        public void onPageScrollStateChanged(int arg0)
        {

        }
    };

    //find页面跳转
    public void goToTable(int position, String source)
    {
        mViewPager.setCurrentItem(position);
        if (position == 2)
        {
            Fragment liveFrag = mFragments.get(position);
            if (liveFrag instanceof LivePlayFrg)
                ((LivePlayFrg) liveFrag).refreshDataFromFind(source);
        }
        if (position == 1)
        {
            eventBus.post(new HomeEvent("2"));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                ToastUtils.showShort("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            }
            else
            {
                CommonUtils.isAppKilled = true;
                finishAffinity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 权限管理
     */
    private void permissionManager()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

            if (!pm.isIgnoringBatteryOptimizations(packageName))
            {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            switch (action)
            {
                case EamConstant.EAM_HX_CMD_START_UPLOAD_LOCATION:
                    try
                    {
                        String extInfo = intent.getStringExtra("extInfo");
                        JSONObject jsonObject = new JSONObject(extInfo);
                        Logger.t(TAG).d(extInfo);
                        final String streamId = jsonObject.getString("streamId");
                        if (myLocationListener == null)
                        {
                            myLocationListener = new MyLocationListener(streamId);
                        }
                        mAct.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (mLocClient == null)
                                    mLocClient = LocationUtils.getInstance().
                                            getLocationClient(mAct.getApplicationContext(), 1000 * 20, myLocationListener);
                                if (mLocClient != null && !mLocClient.isStarted())
                                    mLocClient.start();
                            }
                        });
                    } catch (Exception e)
                    {
                        Logger.t(TAG).d(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND:
                    Logger.t(TAG).d("home页接受到红点显示");
                    setUnreadMsgMyInfo(true);
                    break;
                case EamConstant.EAM_HX_CMD_RECEIVE_RED_MY_REMIND:
                    mPresenter.updateTaskOk();
                    break;
                case EamConstant.EAM_HX_CMD_SUCC_RED_REMIND:
                    break;
                case EamConstant.EAM_HX_RECEIVE_RED_HOME:

                    if (tab02 != null)
                        tab02.upFrgMomentsRed();
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            updateUnreadLabel();
                        }
                    });
                    break;
                case EamConstant.EAM_HX_RECEIVE_BIGV_RED_HOME://更新大V专栏文章小红点
                    if (tab01 != null)
                        tab01.upBigVMomentsRed();
                    break;
                case EamConstant.EAM_HX_CMD_TASK_RED_REMIND:
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            hasNewTaskOk = true;
                            setUnreadMsgMyInfo(true);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    //是否显示我的 信息提示小红点
    public void setUnreadMsgMyInfo(boolean isShow)
    {
        if (isShow)
        {
            if (unreadMsgLabelMyInfo.getVisibility() == View.VISIBLE)
            {
                return;
            }
            unreadMsgLabelMyInfo.setVisibility(View.VISIBLE);
        }
        else
        {
            if (unreadMsgLabelMyInfo.getVisibility() == View.GONE)
            {
                return;
            }
            unreadMsgLabelMyInfo.setVisibility(View.GONE);
        }
    }


    private Map<String, Listener> listenerWindowMap = new HashMap<String, Listener>();
    //走接口窗口的总数量
    public final static int WINDOWSSISE = 9;

    public interface Listener
    {
        void listener();
    }

    //APP启动可能执行的窗口显示逻辑 都在此函数中
    private void traverseFunctionWindow()
    {
        final FEncounterFrg fEncounterFrg = tab01.getEncounterFrg();
        if (fEncounterFrg == null)
            return;

        fEncounterFrg.setListenerMap(listenerWindowMap);

        final FEncounterFrg.WindowDissmissListener windowListener = new FEncounterFrg.WindowDissmissListener()
        {
            @Override
            public void onDissmiss(String flag)
            {
                final FEncounterFrg fEncounterFrg = tab01.getEncounterFrg();
                if (fEncounterFrg == null)
                    return;

                if (mViewPager.getCurrentItem() != 0)
                    return;

                checkWindow(flag, fEncounterFrg);
            }
        };


        listenerWindowMap.put("新手引导", new Listener()
        {
            @Override
            public void listener()
            {
                fEncounterFrg.showNewbieGuide();
            }
        });

        fEncounterFrg.setWindowDissmissListener(windowListener);
        fEncounterFrg.checkWindow();
    }

    private boolean showFunctionWindow(String name)
    {
        if (listenerWindowMap.get(name) != null)
        {
            listenerWindowMap.get(name).listener();
            listenerWindowMap.remove(name);
            return true;
        }
        listenerWindowMap.remove(name);

        return false;
    }

    private void checkWindow(String name, FEncounterFrg fEncounterFrg)
    {
        switch (name)
        {
            case "first":
                boolean isBreak = showFunctionWindow("每日签到");

                if (isBreak)
                    break;
            case "每日签到":
                isBreak = showFunctionWindow("成就弹窗");

                if (isBreak)
                    break;
            case "成就弹窗":
                isBreak = showFunctionWindow("任务弹窗");

                if (isBreak)
                    break;
            case "任务弹窗":
                isBreak = showFunctionWindow("双十一红包弹窗");

                if (isBreak)
                    break;
            case "双十一红包弹窗":
                isBreak = showFunctionWindow("双十一弹窗");

                if (isBreak)
                    break;
            case "双十一弹窗":
                isBreak = showFunctionWindow("中秋弹窗");

                if (isBreak)
                    break;
            case "中秋弹窗":
                isBreak = showFunctionWindow("国庆弹窗");

                if (isBreak)
                    break;
            case "国庆弹窗":
                isBreak = showFunctionWindow("新手引导");

                if (isBreak)
                    break;
            case "新手引导":
                isBreak = showFunctionWindow("头像审核");
                fEncounterFrg.showHeadCheck();
                break;
            case "notification":
                if (listenerWindowMap.size() >= WINDOWSSISE)
                {
                    checkWindow("first", fEncounterFrg);
                }
                break;
        }
    }


    /**
     * update unread message count
     * 更新请确保dynamicCount字段数量正确
     */
    public void updateUnreadLabel()
    {
        Observable.create(new ObservableOnSubscribe<Integer>()
        {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception
            {
                int msgCount = getUnreadMsgCountTotal();
                //关注动态的数量
                int dynamicCount = 0;
                try
                {
                    if (EamApplication.getInstance().dynamicCount.equals("99+"))
                    {
                        dynamicCount = 99;
                    }
                    else
                    {
                        dynamicCount = Integer.parseInt(EamApplication.getInstance().dynamicCount);
                    }
                } catch (NumberFormatException ne)
                {
                    ne.printStackTrace();
                }
                msgCount += dynamicCount;
                e.onNext(msgCount);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Integer>()
                {
                    @Override
                    public void accept(Integer msgCount) throws Exception
                    {
                        if (msgCount > 0)
                        {
                            if (msgCount >= 99)
                            {
                                unreadMsgLabel.setText("99+");
                            }
                            else
                            {
                                unreadMsgLabel.setText(msgCount + "");
                            }
                            unreadMsgLabel.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            unreadMsgLabel.setVisibility(View.INVISIBLE);
                        }
                        //Logger.t(TAG).d("Home页聊天tab红点数：环信消息数：" + (msgCount - dynamicCount) + " | 关注动态数：" + dynamicCount);
                    }
                });


    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal()
    {
        int allUnReadCount = 0;
        try
        {
            for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values())
            {
                int unReadCount = 0;
                if (conversation.getType() == EMConversation.EMConversationType.Chat)
                {
                    for (EMMessage message : conversation.getAllMessages())
                    {
                        if (message.isUnread())
                            unReadCount++;
                    }
//                    if (unReadCount < conversation.getUnreadMsgCount())
//                        unReadCount = conversation.getUnreadMsgCount();
                    allUnReadCount = allUnReadCount + unReadCount;
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            EamLogger.t("HomeAct getUnreadMsgCountTotal()").writeToDefaultFile("EMAChatClient.getChatManager():/" + e.getMessage());
        }
        return allUnReadCount;
    }

    //region 公共方法
    public void setSubPageIndexChangedListener(IOnAroundPageChangedListener listener)
    {
        this.mOnAroundPageChangedListener = listener;
    }

    private void showDownloadDialog(String url)
    {
        // 弹出下载Dialog
        new DownloadAlertDialog(mAct)
                .build()
                .setTitle("看脸吃饭.apk")
                .setImage(R.mipmap.ic_launcher)
                .setDownLoadFileUrl(url)
                .show();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        Logger.t(TAG).d("interfaceName>" + interfaceName + "错误码为：%s", code);
        switch (interfaceName)
        {
            case NetInterfaceConstant.ReceiveC_sendLocationOnoff:
                if (mLocClient != null && mLocClient.isStarted())
                    mLocClient.stop();
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.ReceiveC_sendLocationOnoff:
                if (mLocClient != null && mLocClient.isStarted())
                    mLocClient.stop();
                break;
            default:
                break;
        }
    }


    @Override
    public void getVersionCodeCallback(String str)
    {
        try
        {
            JSONObject obj = new JSONObject(str);
            String ServerVersion = obj.getString("version");
            int SerVersion = Integer.parseInt(ServerVersion);
            final String url = obj.getString("url");
            final String type = obj.getString("type");
            final String updateContent = obj.getString("msg");
            Logger.t(TAG).d("SerVersion:" + SerVersion + ",versionCode" + CommonUtils.getVerCode(mAct));
            if (CommonUtils.getVerCode(mAct) < SerVersion)
            {
                View view = LayoutInflater.from(mAct).inflate(R.layout.dialog_download_apk, null);
                TextView tvContent = (TextView) view.findViewById(R.id.update_content);
                tvContent.setText(updateContent);
                if (type.equals("1"))//强制更新
                {
                    new CustomAlertDialog(mAct)
                            .builder()
                            .setMsg(updateContent)
                            .setCancelable(false)
                            .configContentView(Gravity.LEFT | Gravity.CENTER_VERTICAL)
                            .setPositiveBtnClickListener("体验最新版本", new CustomAlertDialog.OnDialogWithPositiveBtnListener()
                            {
                                @Override
                                public void onPositiveBtnClick(View view, Dialog dialog)
                                {
                                    int i = NetHelper.getNetworkStatus(mAct);
                                    if (i == -1)
                                    {
                                        ToastUtils.showShort("请检查网络");
                                        finish();
                                    }
                                    else
                                    {
                                        updateApk(url, true);
                                    }
                                }
                            }).show();
                }
                else
                {
                    new DialogWith2BtnAtBottom(mAct)
                            .buildDialog(mAct)
                            .setDialogTitle("发现新版本，现在就去下载！", false)
                            .setContent(view)
                            .setCancelable(false)
                            .setCommitBtnClickListener(new DialogWith2BtnAtBottom.OnDialogWithPositiveBtnListener()
                            {
                                @Override
                                public void onPositiveBtnClick(View view, Dialog dialog)
                                {
                                    int i = NetHelper.getNetworkStatus(mAct);
                                    if (i == -1)
                                    {
                                        ToastUtils.showShort("当前无网络连接,更新失败！");
                                        dialog.dismiss();
                                    }
                                    else if (i == 1)
                                    {
                                        ToastUtils.showShort("开始下载...");
                                        Intent updateIntent = new Intent(HomeAct.this, UpdateAPKService.class);
                                        updateIntent.putExtra("apkName", "看脸吃饭");
                                        updateIntent.putExtra("apkUrl", url);
                                        startService(updateIntent);
                                        dialog.dismiss();
                                    }
                                    else
                                    {
                                        new CustomAlertDialog(mAct)
                                                .builder()
                                                .setMsg(getString(R.string.mobie_net_tip))
                                                .setTitle("提示")
                                                .setCancelable(false)
                                                .setPositiveButton("继续更新", new View.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        ToastUtils.showShort("开始下载...");
                                                        Intent updateIntent = new Intent(HomeAct.this, UpdateAPKService.class);
                                                        updateIntent.putExtra("apkName", "看脸吃饭");
                                                        updateIntent.putExtra("apkUrl", url);
                                                        startService(updateIntent);
                                                        dialog.dismiss();
                                                    }
                                                }).setNegativeButton("取消", new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                    }
//                                    updateApk(url,false);
                                }
                            })
                            .setCancelBtnClickListener(new DialogWith2BtnAtBottom.OnDialogWithNavigateBtnListener()
                            {
                                @Override
                                public void onNavigateBtnClick(View view, Dialog dialog)
                                {
                                    int i = NetHelper.getNetworkStatus(mAct);
                                    if (type.equals("1") || i == -1)
                                    {
                                        finish();
                                    }
                                    else
                                    {
                                        dialog.dismiss();
                                    }
                                }
                            })
                            .show();
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    private void updateApk(String url, boolean isForceUpdate)
    {
        String netStat = "";
        //返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
        int i = NetHelper.getNetworkStatus(mAct);
        if (i == -1)
        {
            ToastUtils.showShort("当前无网络连接,更新失败！");
            return;
        }
        switch (i)
        {
            case 1:
                netStat = "WiFi网络";
                //WiFi网络
                showDownloadDialog(url);
                break;
            case 2:
            case 3:
                netStat = "移动网络";
                new CustomAlertDialog(mAct)
                        .builder()
                        .setMsg(getString(R.string.mobie_net_tip))
                        .setTitle("提示")
                        .setCancelable(false)
                        .setPositiveButton("继续更新", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                showDownloadDialog(url);
                            }
                        }).setNegativeButton("退出", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (isForceUpdate)
                        {
                            finish();
//                            new CustomAlertDialog(mAct)
//                                    .builder()
//                                    .setMsg("您必须更新版本才可以继续使用！")
//                                    .setCancelable(false)
//                                    .configContentView(Gravity.LEFT | Gravity.CENTER_VERTICAL)
//                                    .setPositiveButton("体验最新版本", new View.OnClickListener()
//                                    {
//                                        @Override
//                                        public void onClick(View v)
//                                        {
//                                            updateApk(url, true);
//                                        }
//                                    }).show();
                        }
                    }
                }).show();
                break;
        }
    }


    private void initMiPush()
    {
        Logger.t(TAG).d("========>>>miPushAppID:" + BuildConfig.miPushAppID + " | miPushAppKey:" + BuildConfig.miPushAppKey);
        if (shouldInit())
            MiPushClient.registerPush(this, BuildConfig.miPushAppID, BuildConfig.miPushAppKey);
        //打开Log
        LoggerInterface newLogger = new LoggerInterface()
        {
            @Override
            public void setTag(String tag)
            {
                // ignore
            }

            @Override
            public void log(String content, Throwable t)
            {

            }

            @Override
            public void log(String content)
            {

            }
        };
    }

    private boolean shouldInit()
    {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos)
        {
            if (info.pid == myPid && mainProcessName.equals(info.processName))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void postDateUserLocationCallback(ResponseResult response)
    {
        Logger.t(TAG).d("返回结果》" + response);
        try
        {
            JSONObject body = new JSONObject(response.getBody());
            String isLight = body.getString("light");
            if ("1".equals(isLight) && mLocClient != null)
            {
                mLocClient.stop();
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLocationSwitchCallback(String bodyStr)
    {
        Logger.t(TAG).d("发送位置开关  返回结果》" + bodyStr);
        if (myLocationListener == null)
        {
            myLocationListener = new MyLocationListener("");
        }
        if (mLocClient == null)
            mLocClient = LocationUtils.getInstance().
                    getLocationClient(mAct.getApplicationContext(), 1000 * 20, myLocationListener);
        if (mLocClient != null && !mLocClient.isStarted())
            mLocClient.start();
    }

    @Override
    public void updateTaskOkCallBack(final String task, final String successes, final String receive)
    {
        if ("1".equals(task) || "1".equals(successes) || "1".equals(receive))
        {
            hasNewTaskOk = true;
            setUnreadMsgMyInfo(true);
        }
        else
        {
            hasNewTaskOk = false;
            setUnreadMsgMyInfo(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
            return;
        if (tab01 != null)
            tab01.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EamConstant.EAM_OPEN_TASK)
        {
            mPresenter.updateTaskOk();
        }
        if (requestCode == Crop.REQUEST_CROP)
        {
            if (tab01 != null)
                tab01.setData(requestCode, resultCode, data);
        }
        if (tab03 != null)
            tab03.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WINDOWSSISE)
        {
            if (data != null)
            {
                boolean clickAchievement = data.getBooleanExtra("clickAchievement", false);
                if (clickAchievement)
                {
                    if (listenerWindowMap.containsKey("成就窗口"))
                        listenerWindowMap.remove("成就弹窗");
                }
            }
        }
    }


    /**
     * 定位结果回调，重写onReceiveLocation方法
     */
    private class MyLocationListener implements BDLocationListener
    {
        private String streamId;

        public MyLocationListener(String streamId)
        {
            this.streamId = streamId;
        }

        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if (null != location && location.getLocType() != BDLocation.TypeServerError)
            {
                DecimalFormat decimalFormat = new DecimalFormat("#.0000000");
                double mCurrentLantitude = location.getLatitude();
                double mCurrentLongitude = location.getLongitude();
                Logger.t(TAG).d("并向后台上传经纬度--> " + streamId + "lan>" + mCurrentLantitude + "lon>" + mCurrentLongitude);
                //定位成功后，将位置上传
                LocationUtils.getInstance().postMealMeetLocationToServer(mAct,
                        streamId, decimalFormat.format(mCurrentLantitude), decimalFormat.format(mCurrentLongitude), new PostMealMeetLocation(HomeAct.this));
            }
        }
    }

    private static class PostMealMeetLocation<T> extends SilenceSubscriber2<ResponseResult>
    {
        private final WeakReference<HomeAct> mActRef;

        private PostMealMeetLocation(HomeAct mAct)
        {
            this.mActRef = new WeakReference<HomeAct>(mAct);
        }

        @Override
        public void onHandledError(ApiException apiE)
        {
            HomeAct homeAct = mActRef.get();
            if (homeAct != null)
                homeAct.callServerErrorCallback(NetInterfaceConstant.ReceiveC_sendLocation, apiE.getErrorCode(), apiE.getErrBody());
        }

        @Override
        public void onNext(ResponseResult response)
        {
            HomeAct homeAct = mActRef.get();
            if (homeAct != null)
                homeAct.postDateUserLocationCallback(response);
        }
    }

    public void setViewAlpha(boolean tag)
    {
        if (tag)
        {
            mTabWidget.setAlpha(0.7f);
            viewLine.setAlpha(0.7f);
        }
        else
        {
            mTabWidget.setAlpha(1.0f);
            viewLine.setAlpha(1.0f);
        }
    }

}
