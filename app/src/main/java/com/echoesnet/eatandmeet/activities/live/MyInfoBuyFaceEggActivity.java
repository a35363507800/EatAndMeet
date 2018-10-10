package com.echoesnet.eatandmeet.activities.live;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.activities.MyFaceBalanceDetailAct;
import com.echoesnet.eatandmeet.activities.MyInfoAgreementAct;
import com.echoesnet.eatandmeet.activities.MyRechargeResultAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.FaceListBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.presenters.ImpIMyInfoBuyFaceEggView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoBuyFaceEggView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IHsPayPendingListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.adapters.MyInfoFaceEggAccountAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.echoesnet.eatandmeet.R.id.cb_register_statement;

/**
 * Created by Administrator on 2016/10/19.
 *  脸蛋购买
 */
public class MyInfoBuyFaceEggActivity extends MVPBaseActivity<IMyInfoBuyFaceEggView, ImpIMyInfoBuyFaceEggView> implements IMyInfoBuyFaceEggView
{
    private static final String TAG = MyInfoBuyFaceEggActivity.class.getSimpleName();
    //    @BindView(R.id.all_face_egg)
//    AutoLinearLayout allFaceEgg;
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.gv_account)
    MyGridView gvAccount;
    @BindView(R.id.tv_disclaimer)
    TextView tvDisclaimer;
    @BindView(R.id.tv_egg_ct)
    TextView tvEggCount;
    @BindView(R.id.ll_accept)
    LinearLayout rlAccept;
    @BindView(R.id.bt_shopping)
    Button btShopping;
    @BindView(cb_register_statement)
    IconTextView cbRegisterStatement;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    //服务协议勾选状态
    private boolean isChecked = true;
    private MyInfoFaceEggAccountAdapter adapter;
    private String payAmount; // 充值金额
    private String getAmount; // 赠送金额
    private Activity mContext;
    private Dialog pDialog;

    private List<FaceListBean.faceList> faceList;


    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_myinfo_face_egg);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        PayHelper.setIHsPayPendingListener(new PendingPayFinish(MyInfoBuyFaceEggActivity.this));
        if (mPresenter != null)
            mPresenter.getFaceRechargeList();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mContext != null && !mContext.isFinishing())
        {
            PayHelper.clearPopupWindows();
        }
    }

    private void initAfterView()
    {
        mContext = this;
        topBar = (TopBarSwitch) findViewById(R.id.top_bar);
        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                startActivity(new Intent(mContext, MyFaceBalanceDetailAct.class));
            }
        }).setText("脸 蛋");
        List<TextView> navBtns = topBar.getNavBtns(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i);
            if (i == 1)
            {
                tv.setText("明细");
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.C0322));
                tv.setTextSize(16);
            }
        }

        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(false);

        faceList = new ArrayList<>();
        adapter = new MyInfoFaceEggAccountAdapter(mContext, faceList);
        gvAccount.setAdapter(adapter);
        gvAccount.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (adapter.getIndex() == -1)
                {
                    btShopping.setBackgroundResource(R.drawable.egg_bt_bg);
                    btShopping.setEnabled(true);
                }
                adapter.checkIndex(position);

            }
        });

        if (mPresenter != null)
            mPresenter.getFaceRechargeList();

        scrollView.scrollTo(0, 0);
        cbRegisterStatement.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                changeCheckBoxStyle();
            }
        });
    }


    @OnClick({R.id.tv_disclaimer, R.id.bt_shopping})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_disclaimer://充值协议
                Intent intent = new Intent(mContext, MyInfoAgreementAct.class);
                startActivity(intent);
                break;
            case R.id.bt_shopping://购买
                if (isChecked)
                {
                    payAmount = String.valueOf((int) (Double.parseDouble(faceList.get(adapter.getIndex()).getRechargeAmount())));
                    getAmount = String.valueOf((int) (Double.parseDouble(faceList.get(adapter.getIndex()).getGetAmount())));
                    Logger.t(TAG).d("修改后传递金额 ---- 充值金额--> " + payAmount + " , 赠送金额--> " + getAmount);
                    /*SelectPicPopupWindow menuWindow = new SelectPicPopupWindow(mContext, payAmount,getAmount);
                    menuWindow.showAtLocation(gvAccount, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);*/
                    PayHelper.clearPayHelperListeners();
                    PayHelper.setIHsPayPendingListener(new PendingPayFinish(MyInfoBuyFaceEggActivity.this));
                    PayBean payBean = new PayBean();
                    payBean.setOrderId("");
                    payBean.setAmount(payAmount);
                    payBean.setMyPayType(EamConstant.EAM_PAY_PENDING);
                    payBean.setSubject("看脸吃饭App脸蛋充值");               // 商品的标题
                    payBean.setBody("脸蛋充值");        // 商品的描述信息
                    PayHelper.payOrder(gvAccount, payBean, mContext, new PayMetadataBean("", "", "", "4", getAmount));
                } else
                {
                    new CustomAlertDialog(mContext)
                            .builder().setTitle("提示")
                            .setMsg("您需阅读并同意《充值服务协议》才可进行充值")
                            .setPositiveButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                }
                            }).show();
                }
                break;
            default:
                break;
        }

    }

    /**
     * Ping++回调,onActivityResult()发生在onResume()之前
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
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
                if (result.equals("success"))
                {
                    PayHelper.setIPayFinishedListener(new PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck2(mContext, new PayMetadataBean(getAmount, "", "", "4"));

                    Intent intent = new Intent();
                    intent.setAction(EamConstant.ACTION_UPDATE_USER_BALANCE);
                    intent.putExtra("needRefreshAccountInfo", true);
                    sendBroadcast(intent);
                } else if (result.equals("cancel"))
                {
                    ToastUtils.showShort("支付取消");
                } else
                {
                    ToastUtils.showShort("支付失败, 请重试");
                }
            }
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody, GridPasswordView gridPasswordView)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_recharge:
                Logger.t(TAG).d("密码错误");
                // 以前代码
                                /*String code = jsonObject.getString("code");
                                gridPasswordView.clearPassword();
                                if (!ErrorCodeTable.handleErrorCode(code, mContext))
                                    ToastUtils.showShort(mContext, ErrorCodeTable.parseErrorCode(code));
                                if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
                                    pDialog.dismiss();*/

                //修改后(为了实现输入密码错误次数) == 开始 ==

                if (ErrorCodeTable.PAYPWD_ERR.equals(code))
                {
                    try
                    {
                        JSONObject objs = new JSONObject(errBody);
                        String surplus = objs.getString("surplus");
                        if (!"0".equals(surplus))
                        {
                            ToastUtils.showShort("密码输入错误,还剩余" + surplus + "次机会！");
                            gridPasswordView.clearPassword();
                        } else
                        {
                            ToastUtils.showShort("由于您输错的次数过多，支付密码为锁定状态，3小时后可自动解锁！");
                        }
                        return;
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                } else if (ErrorCodeTable.REPAY_COUNTDOWN_IS_FINISH.equals(code))
                {
                    ToastUtils.showShort("由于您输错的次数过多，支付密码为锁定状态，3小时后可自动解锁！");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }




    @Override
    public void getFaceRechargeListCallBack(String response)
    {
            FaceListBean bean = new Gson().fromJson(response, new TypeToken<FaceListBean>(){}.getType());
            faceList.clear();
            faceList.addAll(bean.getFaceList());
            try
            {
                SharePreUtils.setLevel(mContext, Integer.parseInt(bean.getLevel()));
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
            String faceNum = bean.getFace();
            tvEggCount.setText(faceNum);
            // FIXME: 2017/3/17 刘洋 直接给 forResult 添加标记；
            ((EamApplication) getApplication()).uInfoFaceEgg = faceNum;
            setResult(RESULT_OK);


            adapter.notifyDataSetChanged();
            rlAccept.setVisibility(View.VISIBLE);
            if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
    }

    @Override
    public void postFaceEggRecharge2CallBack(String response, GridPasswordView gridPasswordView)
    {
        Logger.t(TAG).d("返回结果：" + response);
        ToastUtils.showShort("充值成功");
        if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        PayHelper.clearPopupWindows();
        Intent intent = new Intent(mContext, MyRechargeResultAct.class);
        intent.putExtra("faceEgg", "1");
        if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        startActivity(intent);
              /*  Intent intent1 = new Intent();
                intent.setAction(EamConstant.ACTION_UPDATE_USER_MSG);
                intent1.putExtra("needRefreshMsg",true);
                sendBroadcast(intent);*/
        finish();
        //修改后(为了实现输入密码错误次数) == 结束 ==


    }


    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<MyInfoBuyFaceEggActivity> mActRef;

        private PayFinish(MyInfoBuyFaceEggActivity mAct)
        {
            this.mActRef = new WeakReference<MyInfoBuyFaceEggActivity>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            String payTypeR = "none";
            if (payType.equals("alipay") || payType.equals("alipay_wap"))
            {
                payTypeR = "1";
            } else if (payType.equals("wx"))
                payTypeR = "2";
            else
                payTypeR = "3";
            final MyInfoBuyFaceEggActivity cAct = mActRef.get();
            if (cAct != null)
            {
//                cAct.postFaceEggRecharge(payTypeR,streamId);
                if (!cAct.mContext.isFinishing() && cAct.pDialog != null && cAct.pDialog.isShowing())
                    cAct.pDialog.dismiss();
                PayHelper.clearPopupWindows();

                Intent intent = new Intent(cAct, MyRechargeResultAct.class);
                intent.putExtra("faceEgg", "1");
                cAct.startActivity(intent);
                cAct.finish();

            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final MyInfoBuyFaceEggActivity cAct = mActRef.get();
            if (cAct != null)
            {
                Intent intent = new Intent();
                intent.putExtra("result", "no");
                cAct.setResult(2, intent);
                cAct.finish();
            }
        }
    }

    private static class PendingPayFinish implements IHsPayPendingListener
    {
        private final WeakReference<MyInfoBuyFaceEggActivity> mActRef;

        private PendingPayFinish(MyInfoBuyFaceEggActivity mAct)
        {
            this.mActRef = new WeakReference<MyInfoBuyFaceEggActivity>(mAct);
        }

        @Override
        public void payPending(String passWord, GridPasswordView gridPasswordView)
        {
            final MyInfoBuyFaceEggActivity cAct = mActRef.get();
            if (cAct != null)
            {
                cAct.mPresenter.postFaceEggRecharge2("0", "", passWord, gridPasswordView, cAct.payAmount, cAct.getAmount);
            }
        }
    }

    private void changeCheckBoxStyle()
    {
        if (isChecked)
        {
            cbRegisterStatement.setText("{eam-n-box}");
            isChecked = false;
            //由于某些机型适配问题，字体图标无法使用
          /*  cb_register_statement.setButtonDrawable(new IconDrawable(this, EchoesEamIcon.eam_p_box
            ).colorRes(R.color.FC3)
                    .sizeDp(12));*/
        } else
        {
            cbRegisterStatement.setText("{eam-p-box}");
            isChecked = true;
           /* cb_register_statement.setButtonDrawable(new IconDrawable(this, EchoesEamIcon.eam_n_box
            ).colorRes(R.color.FC3)
                    .sizeDp(12));*/
        }
    }

    @Override
    protected ImpIMyInfoBuyFaceEggView createPresenter()
    {
        return new ImpIMyInfoBuyFaceEggView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0)
        {
            PayHelper.dismissPopupWindow(mContext);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
