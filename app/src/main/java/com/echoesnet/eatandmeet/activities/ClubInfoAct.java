package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ClubInfoBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.presenters.ImpIClubInfoView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubInfoView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.SharePreUtils;

import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IPayCancelListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.widgets.ClubListView;
import com.echoesnet.eatandmeet.views.widgets.EditViewWithCharIndicate;
import com.echoesnet.eatandmeet.views.widgets.ImageIndicatorView.NetworkImageIndicatorView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.logger.Logger;
import com.panxw.android.imageindicator.AutoPlayManager;
import com.panxw.android.imageindicator.ImageIndicatorView;
import com.pingplusplus.android.Pingpp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class ClubInfoAct extends MVPBaseActivity<IClubInfoView, ImpIClubInfoView> implements
        IClubInfoView {
    private static final String TAG = ClubInfoAct.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    @BindView(R.id.icv_cycle_view)
    NetworkImageIndicatorView networkImageIndicatorView;

    @BindView(R.id.btn_flash_pay)
    Button btnPay;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.club_info_list)
    ClubListView clubInfoList;

    @BindView(R.id.evw_input_feedback)
    EditViewWithCharIndicate editViewWithCharIndicate;

    @BindView(R.id.icv_image_view)
    ImageView imageView;


    private Activity mContext;
    private String clubId;

    public static final int INDEX_OK = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_club_info);
        ButterKnife.bind(this);
        initAfterViews();
    }

    private void initAfterViews() {
        mContext = this;
        clubId = getIntent().getStringExtra("clubId");
        topBar.inflateTextCenter(new TopbarSwitchSkeleton() {
            @Override
            public void leftClick(View view) {
                finish();
            }

            @Override
            public void right2Click(View view) {

            }
        }).setText("预定信息");

        topBar.setBottomLineVisibility(View.GONE);


        networkImageIndicatorView.setOnItemClickListener(new ImageIndicatorView
                .OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {

            }
        });

        AutoPlayManager autoBrocastManager = new AutoPlayManager(networkImageIndicatorView);
        autoBrocastManager.setBroadcastEnable(true);
        autoBrocastManager.setBroadCastTimes(10000);//loop times
        autoBrocastManager.setBroadcastTimeIntevel(2 * 1000, 2 * 1000);//设置第一次展示时间以及间隔，间隔不能小于1秒
        autoBrocastManager.loop();
        networkImageIndicatorView.showPageCountView();

        //请求预定信息
        mPresenter.getClubInfoData(clubId);


        //调用支付接口
        RxView.clicks(btnPay)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        checkPayBtn(); //再次确认

                        try {
                            if (clubInfoList.getClubInfoBean() != null) {
                                ClubInfoBean clubInfoBean = clubInfoList.getClubInfoBean();


                                String date = clubInfoBean.getReserveDate().get(clubInfoList.getCheckPosition
                                        (0)).getDate();
                                String mealMark = clubInfoBean.getPackages().get(clubInfoList
                                        .getCheckPosition(2)).getId();
                                String dateMark = clubInfoBean.getReserveDate().get(clubInfoList
                                        .getCheckPosition(0)).getScreenings().get(clubInfoList
                                        .getCheckPosition(1)).getIndex();

                                int position=clubInfoList.getCheckPosition(3);
                                String themeId="";
                                if(position!=-1)
                                    themeId = clubInfoBean.getTheme().get(position).getId();

                                mPresenter.postClubPartyOrderToSever(clubId, date, mealMark, dateMark,themeId,editViewWithCharIndicate.getInputText());

                                //                    mPresenter.postClubPartyOrderToSever();
                            }
                        } catch (Exception e) {
                            Logger.t(TAG).d("数据异常防止崩溃不做处理",e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });




        clubInfoList.setOnItemClickListener(new ClubListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int item, int postion) {
                if (clubInfoList.getClubInfoBean() == null)
                    return;

                if (item == 0) {
                        clubInfoList.setList(clubInfoList.getClubInfoBean().getReserveDate().get
                                (postion).getScreenings(), 1);
                }
                if (item == 2) {

                        if(clubInfoList.getCheckPosition(3)!=-1)
                            setTextMoney(Double.parseDouble(clubInfoList.getClubInfoBean().getPackages().get(postion)
                                    .getPrice())+Double.parseDouble(clubInfoList.getClubInfoBean().getTheme().get(clubInfoList.getCheckPosition(3)).getPrice())+"");
                        else
                            setTextMoney(clubInfoList.getClubInfoBean().getPackages().get(postion)
                                    .getPrice());
                }

                if (item == 3&&clubInfoList.getCheckPosition(1)!=-1) {

                    if(postion!=-1)
                    {
                            setTextMoney(Double.parseDouble(clubInfoList.getClubInfoBean().getPackages().get(clubInfoList.getCheckPosition(2))
                                    .getPrice())+Double.parseDouble(clubInfoList.getClubInfoBean().getTheme().get(postion).getPrice())+"");

                        GlideApp.with(EamApplication.getInstance())
                                .load(clubInfoList.getClubInfoBean().getTheme().get(postion).getWeek())
                                .centerCrop()
                                .error(R.drawable.cai_da)
                                .into(imageView);

                    }else
                    {
                        setTextMoney(clubInfoList.getClubInfoBean().getPackages().get(clubInfoList.getCheckPosition(2))
                                .getPrice());

                        GlideApp.with(EamApplication.getInstance())
                                .load(clubInfoList.getClubInfoBean().getUrl())
                                .centerCrop()
                                .error(R.drawable.cai_da)
                                .into(imageView);


                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkPayBtn();
                    }
                }, 50);

            }
        });

    }

    /**
     * Ping++回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
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

                if (result.equals("success")) {
                    PayHelper.setIPayFinishedListener(new PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck(mContext, new PayMetadataBean("", "", "",
                            "7"));
                } else if (result.equals("cancel")) {
                    ToastUtils.showShort("支付取消");
                } else {
                    ToastUtils.showShort("支付失败, 请重试");
                }
            }
        } else if (requestCode == INDEX_OK) {
            int index = data.getExtras().getInt("index");
            clubInfoList.setCheckPosition(2, index);
            clubInfoList.setScorllPosition(2, index);
            try {

                if(clubInfoList.getCheckPosition(3)!=-1)
                    setTextMoney(Double.parseDouble(clubInfoList.getClubInfoBean().getPackages().get(index)
                            .getPrice())+Double.parseDouble(clubInfoList.getClubInfoBean().getTheme().get(clubInfoList.getCheckPosition(3)).getPrice())+"");
                else
                    setTextMoney(clubInfoList.getClubInfoBean().getPackages().get(index)
                            .getPrice());
            } catch (IndexOutOfBoundsException e) {
                setTextMoney("0");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayHelper.clearPopupWindows();
    }

    @Override
    protected ImpIClubInfoView createPresenter() {
        return new ImpIClubInfoView();
    }

    @Override
    public void getClubInfoDataCallBack(ClubInfoBean bean) {
        clubInfoList.setDatas(mContext, bean);
        try {
            setTextMoney(bean.getPackages().get(clubInfoList.getCheckPosition(2)).getPrice());
        } catch (IndexOutOfBoundsException e) {
            setTextMoney("0");
        }
//        networkImageIndicatorView.setupLayoutByImageUrl(list);
//        networkImageIndicatorView.show();
        GlideApp.with(EamApplication.getInstance())
                .load(bean.getUrl())
                .centerCrop()
                .placeholder(R.drawable.qs_cai_canting)
                .error(R.drawable.cai_da)
                .into(imageView);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPayBtn();
            }
        }, 50);
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody) {

    }

    @Override
    public void postOrderToServer(String orderId) {
        PayHelper.clearPayHelperListeners();
        PayHelper.setIPayFinishedListener(new PayFinish(ClubInfoAct.this));
        PayBean payBean = new PayBean();
        payBean.setOrderId(orderId);
        payBean.setAmount(tvMoney.getText().toString().replace("￥", ""));
        payBean.setSubject("订单");               // 商品的标题
        payBean.setBody("轰趴馆订单支付");               // 商品的描述信息
        payBean.setMyPayType(EamConstant.EAM_PAY_CLUBPAY);
        SharePreUtils.setClubId(mContext,clubId);
        PayHelper.setIPayCancelListener(new IPayCancelListener() {
            @Override
            public void payCanceled()
            {
                ToastUtils.showShort("取消支付成功");
                Intent intent = new Intent(mContext, ClubOrderRecordDetailAct.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                if (!isFinishing()) {
                    finish();
                }
            }
        });
        PayHelper.payOrder(btnPay.getRootView(), payBean, ClubInfoAct.this, new PayMetadataBean
                ("", "", "", "7"));
    }

    private static class PayFinish implements IPayFinishedListener {
        private final WeakReference<ClubInfoAct> mActRef;

        private PayFinish(ClubInfoAct mAct) {
            this.mActRef = new WeakReference<ClubInfoAct>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType) {
            final ClubInfoAct cAct = mActRef.get();
            if (cAct != null) {
                Intent intent1 = new Intent(cAct, DClubPayOrderSuccessAct.class);
                intent1.putExtra("orderId", orderId);
                intent1.putExtra("clubId", SharePreUtils.getClubId(cAct));
                cAct.startActivity(intent1);
                cAct.finish();
                ToastUtils.showShort("支付成功");
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType) {
            final ClubInfoAct cAct = mActRef.get();
            if (cAct != null) {
                ToastUtils.showLong("由于未知原因没有获得支付结果，请勿重复支付，尝试刷新页面");
/*                Intent intent1 = DOrderRecordDetail_.intent(cAct).get();
                intent1.putExtra("orderId", orderId);
                cAct.startActivity(intent1);
                cAct.finish();*/

            }
        }

    }

    private void setTextMoney(String money) {
        try {
            tvMoney.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(money)));
        } catch (NumberFormatException e) {
            tvMoney.setText("￥0.00");
        }
    }

    private void checkPayBtn() {
        if (clubInfoList.getCheckPosition(0) == -1 || clubInfoList.getCheckPosition(1) == -1 ||
                clubInfoList.getCheckPosition(2) == -1) {
            btnPay.setBackgroundResource(R.drawable.btn7_selector);
            btnPay.setClickable(false);
        } else {
            btnPay.setBackgroundResource(R.drawable.btn6_selector);
            btnPay.setClickable(true);
        }
    }
}
