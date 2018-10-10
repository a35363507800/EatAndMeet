package com.echoesnet.eatandmeet.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.pushservice.RegistrationReceiver;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CaptureActivity;
import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.activities.RelationAct;
import com.echoesnet.eatandmeet.activities.TrendsPublishAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.listeners.IOnAroundPageChangedListener;
import com.echoesnet.eatandmeet.models.eventmsgs.HomeEvent;
import com.echoesnet.eatandmeet.presenters.ImpIAroundPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IAroundFrgView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.LocationUtils.LocationUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.hyphenate.chat.EMClient;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate
 * @description 邻座总界面
 */
@RuntimePermissions
public class AroundFrg extends MVPBaseFragment<AroundFrg, ImpIAroundPre> implements View
        .OnClickListener, IAroundFrgView
{
    private final static String TAG = AroundFrg.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.vp_around)
    ViewPager vpAround;

    private Unbinder unbinder;
    private String pageIndex;
    private List<Fragment> fragments = new ArrayList<>();//里面包含的两个fragment
    private FragmentActivity mContext;
    private int currentPosition;
    private ConversationListFragment conversationListFragment;
    private FrgMoments momentsFrg;
    private List<Map<String, TextView>> navBtns;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            pageIndex = getArguments().getString("pageIndex");
        }
        ((HomeAct) getActivity()).setSubPageIndexChangedListener(new IOnAroundPageChangedListener()
        {
            @Override
            public void onPageIndexChanged(String subPageIndex)
            {
                Logger.t(TAG).d("subPageIndex:" + subPageIndex);
                // if (subPageIndex != null && subPageIndex.equals("Chat"))
                //selectCommunicate(mContext);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_around, container, false);
        mContext = getActivity();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //  initTitlePopupData("0");
        initTitle();
        initViewPager();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (topBarSwitch != null)
            topBarSwitch.hindMsgIndicator(currentPosition);//去掉当前显示页面的红点
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        momentsFrg.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    //endregion

    public AroundFrg()
    {
        // Required empty public constructor
    }


    public static AroundFrg newInstance(String pageIndex)
    {
        AroundFrg fragment = new AroundFrg();
        Bundle args = new Bundle();
        args.putString("pageIndex", pageIndex);
        fragment.setArguments(args);
        return fragment;
    }


    public void upFrgMomentsRed()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (currentPosition != 1)
                {
                    String dynamicCount = EamApplication.getInstance().dynamicCount;
                    if (!"0".equals(dynamicCount))
                    {
                        if (topBarSwitch != null)
                        {
                            topBarSwitch.showMsgIndicator(1, dynamicCount);
                        }
                    }else
                    {
                        if (topBarSwitch != null)
                        {
                            topBarSwitch.hindMsgIndicator(1);
                        }
                    }
                }
            }
        },300);

    }

    private void initTitle()
    {
        topBarSwitch.inflateSwitchBtns(Arrays.asList("聊 天", "关注动态"), 0,
                new TopbarSwitchSkeleton()
                {
                    @Override
                    public void leftClick(View view)
                    {
                    }

                    @Override
                    public void right2Click(View view)
                    {
                        if (currentPosition == 0)
                        {
                            if (SharePreUtils.getPhoneContact(getActivity()).equals("0"))
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    AroundFrgPermissionsDispatcher
                                            .onReadContactsPermGrantedWithPermissionCheck
                                                    (AroundFrg.this);
                                else
                                {
                                    Intent intent = new Intent(getActivity(), RelationAct.class);
                                    intent.putExtra("openFrom", "follow-list");
                                    startActivity(intent);
                                }
                            }
                            else
                            {
                                Intent intent = new Intent(getActivity(), RelationAct.class);
                                intent.putExtra("openFrom", "follow-list");
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            Intent intent = new Intent(getActivity(), TrendsPublishAct.class);
                            startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_PUBLISH);
                        }
                    }

                    @Override
                    public void switchBtn(View view, int position)
                    {
                        vpAround.setCurrentItem(position);
                        onChangePage(position);
                    }

                    @Override
                    public void refreshPage(int position)
                    {
                        if (position == 1)
                        {
                            if (momentsFrg != null)
                                momentsFrg.refreshData();
                        }
                    }

                    @Override
                    public void topDoubleClick(View view)
                    {
                        super.topDoubleClick(view);
                        if (currentPosition == 1)
                        {
                            if (momentsFrg != null)
                                momentsFrg.scroll2TopAndRefresh();
                        }
                    }
                });
        navBtns = topBarSwitch.getNavBtns2(new int[]{0, 0, 0, 1});
        Map<String, TextView> map = navBtns.get(0);
        TextView icon = map.get(TopBarSwitch.NAV_BTN_ICON);
        icon.setText("{eam-e951}");
        upFrgMomentsRed();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            if (topBarSwitch != null)
                topBarSwitch.hindMsgIndicator(currentPosition);//去掉当前显示页面的红点
            if (currentPosition == 0)
            {
                upFrgMomentsRed();
                mPresenter.loadSayHelloAndTrendsNum();
            }

            if (fragments != null && fragments.size() != 0)
                ((ConversationListFragment) fragments.get(0)).refresh(ConversationListFragment
                        .MSG_HELLO_CHAT);
            if (conversationListFragment != null)
                conversationListFragment.showNewBieGuide();
            if (currentPosition == 1)
            {
                if (fragments != null && fragments.size() != 0)
                {
                    int dynamicCount = 0;
                    try
                    {
                        String count = EamApplication.getInstance().dynamicCount;
                        dynamicCount = Integer.parseInt(count);
                    } catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                    }
                    if (dynamicCount > 0)
                        ((FrgMoments) fragments.get(1)).refreshData();
                }

                EamApplication.getInstance().dynamicCount = "0";
                Intent focusTrendsIntent = new Intent(EamConstant.EAM_HX_RECEIVE_RED_HOME);
                mContext.sendBroadcast(focusTrendsIntent);
            }


        }
        Logger.t(TAG).d("AroundFrg:" + isVisibleToUser);
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AroundFrgPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //请求的权限被授予了
    @NeedsPermission({Manifest.permission.CAMERA})
    void onCameraPermGranted()
    {
        Logger.t(TAG).d("允许获取权限");
        boolean hasCameraPermission = CommonUtils.cameraIsCanUse();
        if (hasCameraPermission)
        {
            Intent intent = new Intent(getActivity(), CaptureActivity.class);
            startActivity(intent);
        }
        else
        {
            ToastUtils.showShort("请释放相机资源");
        }
    }

    //请求的权限被拒绝了
    @OnPermissionDenied({Manifest.permission.CAMERA})
    void onCameraPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }

    //拒绝以后，再次请求时候就会触发这个回调
    @OnNeverAskAgain({Manifest.permission.CAMERA})
    void onCameraPermNeverAsk()
    {
        Logger.t(TAG).d("已经禁止使用此权限了");
        ToastUtils.showLong(getString(R.string.per_camera_never_ask));
    }

    //对请求权限的说明：note： 小米等其他一些国内的手机接管了系统权限管理，估计这个弹窗不会显示，取而代之的是自己的弹窗
    @OnShowRationale({Manifest.permission.CAMERA})
    void onCameraPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mContext)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("需要使用您的相机才能完成此功能！")
                .setPositiveButton("允许", (v) -> request.proceed())
                .setNegativeButton("拒绝", (v) -> request.cancel())
                .show();
    }

    private void initViewPager()
    {
        conversationListFragment = new ConversationListFragment();
        Bundle bud = new Bundle();
        bud.putString(ConversationListFragment.MSG_TYPE, ConversationListFragment.MSG_HELLO_CHAT);
        conversationListFragment.setArguments(bud);
        momentsFrg = FrgMoments.getMyInstance();
        fragments.clear();
        fragments.add(conversationListFragment);
        fragments.add(this.momentsFrg);
        vpAround.setOffscreenPageLimit(2);
        vpAround.setAdapter(new FragmentPagerAdapter(getChildFragmentManager())
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
        vpAround.addOnPageChangeListener(mPagerChangeListener);

//        this.momentsFrg.setNewMomentsListener(new FrgMoments.INewMomentsListener()
//        {
//            @Override
//            public void foundNewMoments(String msg)
//            {
//                int num = 0;
//                try
//                {
//                    num = Integer.parseInt(msg);
//                } catch (NumberFormatException e)
//                {
//                    e.printStackTrace();
//                }
//                if (num > 0 && currentPosition != 1)
//                {
//                    EamApplication.getInstance().dynamicCount = num+"";
//                    //更新聊天top栏红点数字
//                    Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_RED_HOME);
//                    getActivity().sendBroadcast(intent);
//
//                    topBarSwitch.showMsgIndicator(1, num + "");
//                }
//            }
//        });

        conversationListFragment.setIShowMsgCountListener(new ConversationListFragment
                .IShowMsgCountListener()
        {
            @Override
            public void showMsgCount(String msgCount)
            {
                if (currentPosition != 0)
                {
                    //消息来了
                    topBarSwitch.showMsgIndicator(0, msgCount);
                }
            }
        });
    }

    private ViewPager.OnPageChangeListener mPagerChangeListener = new ViewPager
            .OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }

        @Override
        public void onPageSelected(int position)
        {
            topBarSwitch.changeSwitchBtn(position);
            onChangePage(position);
            if (position == 1)
                showNewBieGuide();
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };

    private void showNewBieGuide()
    {
        if (SharePreUtils.getIsNewBieFocusDynamic(mContext))
        {
            NetHelper.checkIsShowNewbie(mContext, "7", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        //获取root节点
                        final FrameLayout fRoot = (FrameLayout) mContext.getWindow().getDecorView
                                ().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mContext, R.layout
                                .view_newbie_guide_talk, null);
                        ImageView imageView = (ImageView) vGuide.findViewById(R.id
                                .img_focus_dynamic);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                fRoot.removeView(vGuide);
                                SharePreUtils.setIsNewBieFocusDynamic(mContext, false);
                                NetHelper.saveShowNewbieStatus(mContext, "7");
                            }
                        });
                        vGuide.setClickable(true);
                        fRoot.addView(vGuide);
                    }
                    else
                    {
                        SharePreUtils.setIsNewBieFocusDynamic(mContext, false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });

        }
    }

    private void onChangePage(int position)
    {
        currentPosition = position;
        Map<String, TextView> map = navBtns.get(0);
        TextView icon = map.get(TopBarSwitch.NAV_BTN_ICON);
        if (currentPosition == 1)
        {
            if (getActivity() instanceof HomeAct)
            {
                int msgCount = ((HomeAct) getActivity()).getUnreadMsgCountTotal();
                if (msgCount != 0)
                    topBarSwitch.showMsgIndicator(0, msgCount > 99 ? "99+" : msgCount + "");
            }
            icon.setText("{eam-s-spades}");
        }
        else
        {
            String dynamicCount = EamApplication.getInstance().dynamicCount;
            if (!"0".equals(dynamicCount))
                topBarSwitch.showMsgIndicator(1, dynamicCount);
            icon.setText("{eam-e951}");
        }
    }

    @NeedsPermission({Manifest.permission.READ_CONTACTS})
    void onReadContactsPermGranted()
    {
        Logger.t(TAG).d("phoneContact-状态--> " + SharePreUtils.getPhoneContact(getActivity()));
        getFriendByContact();
    }

    @OnPermissionDenied({android.Manifest.permission.READ_CONTACTS})
    void onReadContactsPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }

    @OnNeverAskAgain({android.Manifest.permission.READ_CONTACTS})
    void onReadContactsPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        ToastUtils.showLong(getString(R.string.per_phone_contact_never_ask));
    }

    @OnShowRationale({android.Manifest.permission.READ_CONTACTS})
    void onReadContactsPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(getActivity())
                .builder()
                .setTitle("请求权限说明")
                .setMsg("需要读取联系人信息才能完成此功能！")
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
    public void refreshPhoneContactCallback()
    {
        SharePreUtils.setPhoneContact(mContext, "1");
        Intent intent = new Intent(getActivity(), RelationAct.class);
        intent.putExtra("openFrom", "follow-list");
        startActivity(intent);
        Logger.t(TAG).d("上传通讯录后修改是否已上传通讯录状态--> " + SharePreUtils.getPhoneContact(mContext));
    }

    @Override
    public void getPhoneContactLstCallback(List<ImpIAroundPre.ContactEntity> contactLst)
    {
        if (contactLst == null)
        {
            String body = "没有获取到联系人，可能是读取联系人权限被禁止，请到手机管家->权限隐私->应用权限管理->读取联系人信息->看脸吃饭->设置允许";
            new CustomAlertDialog(mContext)
                    .builder()
                    .setTitle("权限提示")
                    .setMsg(body)
                    .setCancelable(false)
                    .setPositiveButton("确认", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                        }
                    }).show();
        }
        else
        {
            List<String> phoneNos = new ArrayList<>();
            for (ImpIAroundPre.ContactEntity c : contactLst)
            {
                phoneNos.add(c.getPhoneNo().replaceAll("\\s+", ""));
            }
            String result = CommonUtils.listToStrWishSeparator(phoneNos, CommonUtils.SEPARATOR);
            if (mPresenter != null)
            {
                mPresenter.getFriendByContact(result);
            }
        }

        /*final String result = CommonUtils.listToStrWishSeparator(phoneNos, CommonUtils.SEPARATOR);
        Logger.t(TAG).d("phones:" + result);
        if (TextUtils.isEmpty(result))
        {
            //oppo手机 默认会把读取联系人权限禁止，所以 特殊处理  -----yqh
            if (Build.BRAND.toLowerCase().equals("oppo"))
            {
                String body = "没有获取到联系人，可能是读取联系人权限被禁止，请到手机管家->权限隐私->应用权限管理->读取联系人信息->看脸吃饭->设置允许";
                new CustomAlertDialog(mContext)
                        .builder()
                        .setTitle("权限提示")
                        .setMsg(body)
                        .setCancelable(false)
                        .setPositiveButton("确认", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        }).show();
            }
            else
            {
                ToastUtils.showShort("没有获得手机号,可能需要设置访问通讯录权限");
            }
        }
        else
        {
            if (mPresenter != null)
            {
                mPresenter.getFriendByContact(result);
            }
        }*/
    }

    @Override
    public void loadSayHelloAndTrendsNumCallback(String response)
    {
        try
        {
            Logger.t(TAG).d("关注动态红点数回调：" + response);
            JSONObject object = new JSONObject(response);
//            String trendsCount = object.getString("trendsCount");
            String helloCount = object.getString("helloCount");
            int tCount = 0;
            int hCount = 0;
            try
            {
//                tCount = Integer.parseInt(trendsCount);
                hCount = Integer.parseInt(helloCount);
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
            int allMsgUnReadCount = EMClient.getInstance().chatManager().getUnreadMessageCount();

            EamApplication.getInstance().msgCount = allMsgUnReadCount + "";
//            EamApplication.getInstance().dynamicCount = tCount + "";

            //更新聊天top栏红点数字
            Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_RED_HOME);
            if (getActivity() != null)
                getActivity().sendBroadcast(intent);

            if (currentPosition == 0)
            {
//                if (topBarSwitch != null && tCount > 0)
//                {
//                    topBarSwitch.showMsgIndicator(1, trendsCount);
//                }
            }
            else
            {
                if (topBarSwitch != null)
                {
                    topBarSwitch.hindMsgIndicator(1);
                }
            }
            conversationListFragment.setHelloSummaryNum(hCount);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected ImpIAroundPre createPresenter()
    {
        return new ImpIAroundPre();
    }

    public void getFriendByContact()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                if (mPresenter != null)
                {
                    mPresenter.getPhoneContactLst();
                }
            }
        }.start();
    }
}
