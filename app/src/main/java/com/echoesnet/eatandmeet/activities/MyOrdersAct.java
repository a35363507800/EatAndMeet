package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.OrderAllFrg;
import com.echoesnet.eatandmeet.fragments.OrdersQuickPayFrg;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.utils.serverdatacache.Elixir;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.hyphenate.chat.EMGCMListenerService;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;
import com.viewpagerindicator.TabPageIndicator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrdersAct extends BaseActivity
{
    private static final String TAG = MyOrdersAct.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;

    @BindView(R.id.vp_order_type_pager)
    ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private int currentPageIndex=0;

    private Activity mContext;
    private OrderAllFrg oAll;
    private  List<Map<String,TextView>> navBtns;
    private String payType = "normal";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_orders);
        ButterKnife.bind(this);
        initAfterViews();
    }

    private void initAfterViews()
    {
        mContext=this;

        topBar.inflateSwitchBtns(new ArrayList<String>(Arrays.asList("预定订单", "闪付订单")), 0,
                new TopbarSwitchSkeleton()
                {
                    @Override
                    public void leftClick(View view)
                    {
                        finish();
                    }

                    @Override
                    public void right2Click(View view)
                    {
                        String function = (String) view.getTag();
                        //  myDateWishListFrg.operateEditStatus(function);
                    }

                    @Override
                    public void switchBtn(View view, int position)
                    {
                        switchPage(position);
                        Logger.t(TAG).d("选择到第》" + position);
                    }
                });
        navBtns = topBar.getNavBtns2(new int[]{1, 0, 0, 1});
        topBar.setBottomLineVisibility(View.GONE);
        initViewPager();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
/*        currentPageIndex=getIntent().getIntExtra("showPageIndex",0);
        Logger.t(TAG).d("currentPageIndex:"+currentPageIndex);
        mViewPager.setCurrentItem(currentPageIndex);*/
    }

    private void switchPage(int showIndex)
    {
        mViewPager.setCurrentItem(showIndex);
    }

    private void initViewPager()
    {
        oAll=OrderAllFrg.newInstance();
/*        OrderUnUsedFrg oUnUsed= OrderUnUsedFrg.newInstance();
        OrdersUnCommentFrg oUnComment= OrdersUnCommentFrg.newInstance();
        OrdersUnpayFrg oUnPay= OrdersUnpayFrg.newInstance();*/
        OrdersQuickPayFrg oQuickPay=OrdersQuickPayFrg.newInstance("");

        final List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(oAll);
/*        mFragments.add(oUnPay);
        mFragments.add(oUnUsed);
        mFragments.add(oUnComment);*/
        mFragments.add(oQuickPay);

        //初始化Adapter
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {
            //public  final String[] TITLES = new String[] { "全部订单", "待支付","待使用","待评价" };
            public  final String[] TITLES = new String[] { "预定订单", "闪付订单"};
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

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                topBar.changeSwitchBtn(position);
        }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    public  void setPayOrderType()
    {
        payType = "club";
    }
    /**
     * Ping++回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data)
    {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                //showMsg(result, errorMsg, extraMsg);

                if (result.equals("success") )
                {
                    if ("club".equals(payType))
                    {
                        PayHelper.setIPayFinishedListener(new PayClubFinish(this));
                        PayHelper.thirdPartyPayStateCheck(mContext,new PayMetadataBean("","","","7"));
                    }
                    else
                    {
                        PayHelper.setIPayFinishedListener(new PayFinish(this));
                        PayHelper.thirdPartyPayStateCheck(mContext,new PayMetadataBean("","","","0"));
                    }

                }
                else if (result.equals("cancel"))
                {
                    ToastUtils.showShort("支付取消");
                }
                else
                {
                    ToastUtils.showShort("支付失败, 请重试");
                }
            }
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }
    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<MyOrdersAct> mActRef;

        private PayFinish(MyOrdersAct mAct)
        {
            this.mActRef = new WeakReference<MyOrdersAct>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            final MyOrdersAct cAct=mActRef.get();
            if (cAct!=null)
            {
                Intent intent = new Intent(cAct, DPayOrderSuccessAct.class);
                intent.putExtra("orderId",orderId);
                cAct.startActivity(intent);
            }
        }
        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final MyOrdersAct cAct = mActRef.get();
            if (cAct!=null)
            {
                ToastUtils.showLong("由于未知原因没有获得支付结果，请勿重复支付，尝试刷新页面");
/*                Intent intent1 = DOrderRecordDetail_.intent(cAct).get();
                intent1.putExtra("orderId", orderId);
                cAct.startActivity(intent1);
                cAct.finish();*/
            }
        }
    }

    private static class PayClubFinish implements IPayFinishedListener
    {
        private final WeakReference<Activity> mActRef;

        private PayClubFinish(Activity mFrg)
        {
            this.mActRef = new WeakReference<Activity>(mFrg);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            final Activity cAct = mActRef.get();
            PayHelper.clearPopupWindows();
            if (cAct != null)
            {
                Intent intent = new Intent(cAct, DClubPayOrderSuccessAct.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("clubId", SharePreUtils.getClubId(cAct));
                cAct.startActivity(intent);
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            //PayHelper.clearPopupWindows();
        }
    }
}
