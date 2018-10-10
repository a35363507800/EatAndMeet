package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.LiveWithDrawActivity;
import com.echoesnet.eatandmeet.models.bean.MyLevel;
import com.echoesnet.eatandmeet.models.bean.NewRechargeBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.presenters.ImpIMyInfoAccountView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoAccountView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.TXConstants;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IHsPayPendingListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayCancelListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.adapters.MyInfoAccountAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.LevelProgressBar;
import com.echoesnet.eatandmeet.views.widgets.MyGridView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.widget.IconTextView;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/4/19
 * @description 新版充值页面
 */

public class MyInfoAccountAct2 extends MVPBaseActivity<MyInfoAccountAct2, ImpIMyInfoAccountView> implements IMyInfoAccountView
{
    private static final String TAG = MyInfoAccountAct2.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tixian_yue)
    TextView tixianYue;
    @BindView(R.id.lpb_bar)
    LevelProgressBar lpbBar;
    @BindView(R.id.tv_info_experience)
    TextView tvInfoExperience;
    @BindView(R.id.tv_num_experience)
    TextView tvNumExperience;
    @BindView(R.id.tv_info_experience_late)
    TextView tvInfoExperienceLate;
    @BindView(R.id.btn_pay_ok)
    Button btnPayOk;
    @BindView(R.id.tv_pay_word)
    TextView tvPayWord;
    @BindView(R.id.mgv_count)
    MyGridView mgvCount;
    @BindView(R.id.iv_agree_button)
    IconTextView ivAgreeButton;
    @BindView(R.id.tv_levelprogressbar_leftlv)
    TextView tvLLV;
    @BindView(R.id.tv_levelprogressbar_rightlv)
    TextView tvRLV;
    @BindView(R.id.tv_rule)
    IconTextView tvRule;

    private boolean isCheck = true;
    private Activity mAct;
    private String payAmount; // 充值金额
    private String getAmount; // 赠送金额
    private String mgetAmount;  //获得多少金额
    private String userRate;
    private String platformRate;

    private Dialog pDialog;


    private MyInfoAccountAdapter adapter;
    private List<NewRechargeBean> newRechargeList;
    private String money = "0";

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_myinfo_new_account);
        ButterKnife.bind(this);
        mAct = this;
        initTopBar();
        btnPayOk.setEnabled(false);
        btnPayOk.setBackgroundResource(R.drawable.shape_pay_sure_normal);
        newRechargeList = new ArrayList<>();
        adapter = new MyInfoAccountAdapter(mAct, newRechargeList);
        mgvCount.setAdapter(adapter);

        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getNewAccountData();
        }


        // SharePreUtils.getHeadImg(mAct);
        // menuWindow = new MyInfoAccountAct2.SelectPicPopupWindow(MyInfoAccountAct2.this);

        //初始化付款方式
        setPayWays();
        mgvCount.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                adapter.checkIndex(position); //保存选中的gridview条目
                btnPayOk.setEnabled(true);
                btnPayOk.setBackgroundResource(R.drawable.shape_pay_sure_press); //同步可使确认按钮选中
                if (isCheck)
                {
                    payAmount = String.valueOf((int) (Double.parseDouble(newRechargeList.get(position).getRechargeAmount())));
                    mgetAmount = String.valueOf((int) (Double.parseDouble(newRechargeList.get(position).getGetAmount())));
                    getAmount = String.valueOf((int) (Double.parseDouble(newRechargeList.get(position).getGetAmount()) -
                            Double.parseDouble(newRechargeList.get(position).getRechargeAmount())));

                    Logger.t(TAG).d("修改后传递金额 ---- 充值金额--> " + payAmount + " , 赠送金额--> " + getAmount);
                    //百度统计
//                    StatService.onEvent(mAct, "app_recharge", getString(R.string.baidu_recharge), 1);
                    // zdw--修改充值金额
                    // if (!menuWindow.isShowing())
                  /*  {
                        menuWindow.showAtLocation(mgvCount,Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,0,300);
                   //   menuWindow.showAtLocation(mgvCount, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                   }*/
                }

            }
        });

        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理...");
        pDialog.setCancelable(false);
        //签约主播不可提现
        if ("1".equals(SharePreUtils.getIsSignAnchor(mAct)))
        {
            tvRule.setVisibility(View.GONE);
            tixianYue.setVisibility(View.GONE);
        }
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
        }

    }


    //region 暂时保留
/*

      private class SelectPicPopupWindow extends PopupWindow
        {
            private View mMenuView;
            private Activity mContext;
            private List<Map<String, Object>> payWays;
            private int selectedItem = 0;

            public SelectPicPopupWindow(Activity context)
            {
                this.mContext = context;
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mMenuView = inflater.inflate(R.layout.popup_new_recharge, null);

                ListView payLst = (ListView) mMenuView.findViewById(R.id.lv_pay);
                payWays = new ArrayList<Map<String, Object>>();
                Map<String, Object> map1 = new HashMap<>();
                map1.put("payWay", "支付宝支付");
                map1.put("icon", new IconDrawable(mContext, EchoesEamIcon.eam_s_ali_pay).colorRes(R.color.c10));
                map1.put("balance", "");
                map1.put("isSelected", false);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("payWay", "微信支付");
                map2.put("icon", new IconDrawable(mContext, EchoesEamIcon.eam_s_wechat_pay).colorRes(R.color.c9));
                map2.put("balance", "");
                map2.put("isSelected", false);
                Map<String, Object> map3 = new HashMap<>();
            */
/*map3.put("payWay", "银联支付");
            map3.put("icon", new IconDrawable(mContext, EchoesEamIcon.eam_s_bank_pay).colorRes(R.color.c12));
            map3.put("balance", "");
            map3.put("isSelected", false);*//*

                payWays.add(map1);
                payWays.add(map2);
           */
/*payWays.add(map3);*//*

            */
/*    final OrderRechargeAdapter adapter = new OrderRechargeAdapter(mContext, payWays);
                payLst.setAdapter(adapter);
                adapter.setOnItemClickListener(new OrderRechargeAdapter.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(View view, int position)
                    {
                        selectedItem = position;
                        adapter.setSelection(position);
                        adapter.notifyDataSetChanged();
                    }
                });*//*

            */
/*payLst.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });*//*

             //   Button btnPayOk = (Button) mMenuView.findViewById(R.id.btn_pay_ok);
*/
/*
                btnPayOk.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        PayBean payBean = new PayBean();
                        payBean.setOrderId("");                 // 商户订单号
                        payBean.setAmount(payAmount);
                        payBean.setSubject("看脸吃饭App充值");             // 商品的标题
                        payBean.setBody("常规充值");            // 商品的描述信息
                        if (selectedItem == 0)
                        {
                            payBean.setChannel("alipay");      // 支付使用的第三方支付渠道 (alipay:支付宝手机支付、alipay_wap:支付宝手机网页支付; upacp:银联全渠道支付、wx:微信支付)
                        }
                        else if (selectedItem == 1)
                        {
                            payBean.setChannel("wx");
                        }
                        else if (selectedItem == 2)
                        {
                            //payType = "upacp";
                        }
                        PayHelper.payByThirdParty(mContext, payBean, new PayMetadataBean(getAmount, "", "0", "1"));
                        dismiss();
                    }
                });
*//*

               // Button btnPayCancel = (Button) mMenuView.findViewById(R.id.btn_pay_cancel);
            */
/*    btnPayCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dismiss();
                    }
                });*//*

                //设置SelectPicPopupWindow的View
                this.setContentView(mMenuView);
                //设置SelectPicPopupWindow弹出窗体的宽
                this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
                //设置SelectPicPopupWindow弹出窗体的高
                this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                //设置SelectPicPopupWindow弹出窗体可点击
                this.setFocusable(true);
                //设置SelectPicPopupWindow弹出窗体动画效果
                this.setAnimationStyle(R.style.PopupAnimation);
                //实例化一个ColorDrawable颜色为半透明
                ColorDrawable dw = new ColorDrawable(0xb0000000);
                //设置SelectPicPopupWindow弹出窗体的背景
                this.setBackgroundDrawable(dw);
                //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
                mMenuView.setOnTouchListener(new View.OnTouchListener()
                {

                    public boolean onTouch(View v, MotionEvent event)
                    {

                        int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                        int y = (int) event.getY();
                        if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            if (y < height)
                            {
                                dismiss();
                            }
                        }
                        return true;
                    }
                });
            }
        }
*/

/*    private void changeCheckBoxStyle()
    {
        if (isAgreed) {
            cbCheckAgree.setButtonDrawable(R.drawable.check_box_p);
            //由于某些机型适配问题，字体图标无法使用
          *//*  cb_register_statement.setButtonDrawable(new IconDrawable(this, EchoesEamIcon.eam_p_box
            ).colorRes(R.color.FC3)
                    .sizeDp(12));*//*
        } else {
            cbCheckAgree.setButtonDrawable(R.drawable.check_box_n);
           *//* cb_register_statement.setButtonDrawable(new IconDrawable(this, EchoesEamIcon.eam_n_box
            ).colorRes(R.color.FC3)
                    .sizeDp(12));*//*
        }
    }*/
    //endregion


    private void initTopBar()
    {
        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                MyInfoAccountAct2.this.finish();
            }

            @Override
            public void right2Click(View view)
            {
                Intent intent = new Intent(mAct, MyBalanceDetailAct.class);
                startActivity(intent);
            }
        }).setText("余额");
        List<Map<String, TextView>> navBtns = topBar.getNavBtns2(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON);
            if (i == 1)
            {
                tv.setText("明细");
                tv.setTextSize(16);
                tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0322));
            }
        }


    }

    /**
     * 初始化付款方式
     */
    private void setPayWays()
    {

//        btnPayOk.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if ((boolean) ivAgreeButton.getTag())
//                {
//                    PayBean payBean = new PayBean();
//                    payBean.setOrderId("");                 // 商户订单号
//                    payBean.setAmount(payAmount);
//                    payBean.setSubject("看脸吃饭App充值");             // 商品的标题
//                    payBean.setBody("常规充值");            // 商品的描述信息
//                    if (selectedItem == 0)
//                    {
//                        payBean.setChannel("alipay");      // 支付使用的第三方支付渠道 (alipay:支付宝手机支付、alipay_wap:支付宝手机网页支付; upacp:银联全渠道支付、wx:微信支付)
//                    } else if (selectedItem == 1)
//                    {
//                        payBean.setChannel("wx");
//                    } else if (selectedItem == 2)
//                    {
//                        //payType = "upacp";
//                    }
//                    PayHelper.payByThirdParty(mAct, payBean, new PayMetadataBean(getAmount, "", "0", "1"));
//                } else
//                {
//                    showAgreement();
//                }
//
//
//            }
//        });

        btnPayOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (isCheck)
                {
                    payAmount = String.valueOf((int) (Double.parseDouble(newRechargeList.get(adapter.getIndex()).getRechargeAmount())));
                    getAmount = String.valueOf((int) (Double.parseDouble(newRechargeList.get(adapter.getIndex()).getGetAmount())));
                    Logger.t(TAG).d("修改后传递金额 ---- 充值金额--> " + payAmount + " , 赠送金额--> " + getAmount);
                    /*SelectPicPopupWindow menuWindow = new SelectPicPopupWindow(mContext, payAmount,getAmount);
                    menuWindow.showAtLocation(gvAccount, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);*/
                    PayHelper.clearPayHelperListeners();
                    PayHelper.setIHsPayPendingListener(new PendingPayFinish(MyInfoAccountAct2.this));
                    PayBean payBean = new PayBean();
                    payBean.setOrderId("");
                    payBean.setAmount(payAmount);
                    payBean.setMyPayType(EamConstant.EAM_PAY_PENDING);
                    payBean.setSubject("看脸吃饭App充值金额");               // 商品的标题
                    payBean.setBody("充值金额");        // 商品的描述信息
                    PayHelper.payOrder(mgvCount, payBean, mAct, new PayMetadataBean(getAmount, "", "0", "1", ""), new String[]{"余额"});
                }
                else
                {
                    new CustomAlertDialog(mAct)
                            .builder().setTitle("提示")
                            .setMsg("您需阅读并同意《充值有礼服务协议》才可进行充值")
                            .setPositiveButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                }
                            }).show();
                }
            }
        });


    }

    private static class PendingPayFinish implements IHsPayPendingListener
    {
        private final WeakReference<MyInfoAccountAct2> mActRef;

        private PendingPayFinish(MyInfoAccountAct2 mAct)
        {
            this.mActRef = new WeakReference<MyInfoAccountAct2>(mAct);
        }

        @Override
        public void payPending(String passWord, GridPasswordView gridPasswordView)
        {
            final MyInfoAccountAct2 cAct = mActRef.get();
            if (cAct != null)
            {
                PayHelper.clearPopupWindows();
            }
        }
    }


    @Override
    protected void onResume()
    {
        Logger.t(TAG).d("onResume");
        super.onResume();
        if (mPresenter != null)
        {
            mPresenter.getMyBalance();
            mPresenter.getMyLevelData();
            mPresenter.getAccountData();
        }
    }

    private IPayFinishedListener mIPayFinishedListener;
    private IHsPayPendingListener mHsPayPendingListener;
    private IPayCancelListener mPayCancelListener;

    private void saveListeners(String flag)
    {
        if ("null".equals(flag))
        {
            mHsPayPendingListener = null;
            mIPayFinishedListener = null;
            mPayCancelListener = null;
        }
        else
        {
            mHsPayPendingListener = PayHelper.getmHsPayPendingListener();
            mIPayFinishedListener = PayHelper.getmIPayFinishedListener();
            mPayCancelListener = PayHelper.getmPayCancelListener();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        btnPayOk.setBackgroundResource(R.drawable.shape_pay_sure_normal);
        btnPayOk.setEnabled(false);
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String result = data.getExtras().getString("pay_result");
          /*       处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed*/

//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                Logger.t(TAG).d("回调结果--> " + data.getExtras());
                switch (result)
                {

                    case "success":
                        saveListeners("");
                        PayHelper.clearPayHelperListeners();
                        PayHelper.setIPayFinishedListener(new IPayFinishedListener()
                        {
                            @Override
                            public void PayFinished(String orderId, String streamId, String payType)
                            {
                                //setModifyBalance(MyInfoAccountAct.this, streamId, payType);
                                //restore接口
                                PayHelper.clearPopupWindows();
                                PayHelper.restorePayHelperListeners(mIPayFinishedListener, mHsPayPendingListener, mPayCancelListener);
                                saveListeners("null");
                                Intent intent = new Intent(mAct, MyRechargeResultAct.class);
                                startActivity(intent);
                                MyInfoAccountAct2.this.finish();
                            }

                            @Override
                            public void PayFailed(String orderId, String streamId, String payType)
                            {
                                //restore接口
                                PayHelper.restorePayHelperListeners(mIPayFinishedListener, mHsPayPendingListener, mPayCancelListener);
                                ToastUtils.showLong("由于网络原因充值没有成功，请确认是否扣款，如若扣款请与客服联系!");
                            }
                        });
                        PayHelper.thirdPartyPayStateCheck2(MyInfoAccountAct2.this, new PayMetadataBean(getAmount, "", "0", "1"));
                        break;
                    case "cancel":
                        ToastUtils.showShort("支付取消");
                        break;
                    case "fail":
                        ToastUtils.showShort("支付失败, 请重试");
                        break;
                    case "invalid":
                        ToastUtils.showShort("未安装相关程序");
                        break;
                    default:
                        break;
                }
            }
        }
    }


    @Override
    protected ImpIMyInfoAccountView createPresenter()
    {
        return new ImpIMyInfoAccountView();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        if (exceptSource.contains(NetInterfaceConstant.WithdrawC_myBalance))
        {
            return;
        }
        NetHelper.handleNetError(mAct, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            // pDialog.dismiss();
            pDialog.show();
    }

    /**
     * @author lc
     * created 获得我的账户余额信息回调
     */
    @Override
    public void getAccountDataCallback(String response)
    {
        Logger.t(TAG).d("账户余额返回信息-->" + response);
        try
        {
            JSONObject jsonBody = new JSONObject(response);
            String balance = jsonBody.getString("balance");
            tvNum.setText("￥" + balance);
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.d(e.getMessage());
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void getMyLevelDataCallback(String response)
    {
        MyLevel level = new Gson().fromJson(response, MyLevel.class);
        Logger.t(TAG).d("我的等级返回信息-->" + TAG + response);
        //当前等级 ,本级已有多少经验，当前有经验(全部)
        tvRLV.setText(String.format("Lv %s", String.valueOf(level.getNextLevel())));
        tvLLV.setText(String.format("Lv %s", String.valueOf(level.getLevel())));
        lpbBar.setLevelData(level.getLevelExp(), level.getUpLevelExp(), level.getCurExp());
        tvNumExperience.setText(level.getUpLevelExp() + "");
        tvInfoExperienceLate.setText("经验可升级为LV"+level.getNextLevel());
        //存储我的等级信息到本地
        SharePreUtils.setLevel(mAct, level.getUserLevel());
        Map<String, String> map = new HashMap<>();
        map.put(TXConstants.TX_CUSTOM_INFO_1_KEY_LEVEL, SharePreUtils.getLevel(mAct) + "");
        map.put(TXConstants.TX_CUSTOM_INFO_1_KEY_SIGN, SharePreUtils.getIsSignAnchor(mAct));
        TencentHelper.setUserInfoToCustom1TxServer(map, null);
    }

    /**
     * @author lc
     * 获得新版充值活动列表
     */
    @Override
    public void getNewAccountDataCallback(String response)
    {
        List<NewRechargeBean> rechargeItems = new Gson().fromJson(response, new TypeToken<List<NewRechargeBean>>()
        {
        }.getType());
        newRechargeList.clear();
        newRechargeList.addAll(rechargeItems);
        adapter.notifyDataSetChanged();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getBindStatsCallBack(String response)
    {
        Logger.t(TAG).d("返回结果》" + response);
        try
        {
            JSONObject object = new JSONObject(response);
            String nicName = object.getString("nicName");
            String headImgUrl = object.getString("headimgurl");
            Intent intent = new Intent(mAct, LiveWithDrawActivity.class);
            intent.putExtra("canWithDrawFanPage", money);
            intent.putExtra("userRate", userRate);
            intent.putExtra("platformRate", platformRate);
            intent.putExtra("status", 0);
            intent.putExtra("nicName", nicName);
            intent.putExtra("headImgUrl", headImgUrl);
            startActivity(intent);

        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
            if (!mAct.isFinishing() && pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void getMyBalanceCallBack(String str)
    {
        Logger.t(TAG).d("返回结果：" + str);
        try
        {
            JSONObject object = new JSONObject(str);
            String balance = object.getString("balance");
            this.money = object.optString("money","0");
            userRate = object.getString("userRate");
            platformRate = object.getString("platformRate");
//                String isSignedAnchor = object.getString("isSignedAnchor");
            userRate = userRate.substring(0, userRate.lastIndexOf("."));
            platformRate = platformRate.substring(0, platformRate.lastIndexOf("."));

            tvNum.setText("￥" + balance);

            if (!mAct.isFinishing() && pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_validate:
                if (ErrorCodeTable.IDCARD_NOT_VALIDATE.equals(code))
                {
                    if (!mAct.isFinishing() && pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                    Intent intent = new Intent(mAct, MyAuthenticationAct.class);
                    intent.putExtra("openSource", "myInfoFanPageDetailAct");
                    mAct.startActivity(intent);
                }
                else if (ErrorCodeTable.WECHAT_NOT_BIND.equals(code) || ErrorCodeTable.WECHAT_LOSE_BIND.equals(code))
                {
                    Intent intent = new Intent(mAct, LiveWithDrawActivity.class);
                    intent.putExtra("canWithDrawFanPage", money);
                    intent.putExtra("userRate", userRate);
                    intent.putExtra("platformRate", platformRate);
                    intent.putExtra("status", 1);
                    startActivity(intent);
                }
                else
                {
                    if (!mAct.isFinishing() && pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                }
                break;
        }
    }


    @OnClick({R.id.tixian_yue, R.id.tv_pay_word, R.id.iv_agree_button, R.id.tv_rule})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tixian_yue:
                //    startActivity(new Intent(mAct, LiveWithDrawActivity.class));
                mPresenter.getBindStats();
                break;
            case R.id.tv_pay_word:
                Intent intent1 = new Intent(mAct, MyInfoAgreementAct.class);
                startActivity(intent1);
                break;
            case R.id.iv_agree_button:
                changeCheckBoxState();
                break;
//            case R.id.lv_vip_all:
//                //跳转去我的等级页面
//                Intent intent2 = new Intent(this, MyLevelAct.class);
//                startActivity(intent2);
//                break;
            case R.id.tv_rule:
                showTixianDialog();
                break;

            default:
                break;
        }
    }

    private void showTixianDialog()
    {
        final Dialog dialog = new Dialog(mAct, R.style.dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        this.getWindow().setBackgroundDrawableResource(R.color.transparent);
        this.getWindow().getWindowManager();
        lp.width = CommonUtils.getScreenWidth(mAct);
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
        View view = LayoutInflater.from(mAct).inflate(R.layout.dialog_tixian_layout, null);
        TextView tvRule = (TextView) view.findViewById(R.id.tv_rule);
        String data = mAct.getString(R.string.tixian_rule);
        String reqS2tr = String.format(data, "1", "1", "200", userRate + "%", platformRate + "%");

        tvRule.setText(reqS2tr);
        IconTextView cancel = (IconTextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 改变同意协议CheckBox选中状态
     */
    private void changeCheckBoxState()
    {

        if (isCheck)
        {
            ivAgreeButton.setText("{eam-n-box}");
            isCheck = false;
        }
        else
        {
            ivAgreeButton.setText("{eam-p-box}");
            isCheck = true;
        }
    }


    /**
     * @author lc
     * created 显示充值协议dialog
     */
    private void showAgreement()
    {
        new CustomAlertDialog(MyInfoAccountAct2.this)
                .builder().setTitle("提示")
                .setMsg("您需阅读并同意《充值有礼服务协议》才可进行充值")
                .setPositiveButton("确定", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                    }
                }).show();
    }


}
