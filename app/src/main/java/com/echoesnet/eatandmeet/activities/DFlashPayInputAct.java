package com.echoesnet.eatandmeet.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.presenters.ImpIDFlashPayInputView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDFlashPayInputView;
import com.echoesnet.eatandmeet.utils.AndroidBug5497Workaround;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IHsPayPendingListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.joanzapata.iconify.widget.IconTextView;
import com.jungly.gridpasswordview.GridPasswordView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/3/4
 * @description 闪付
 */

public class DFlashPayInputAct extends MVPBaseActivity<DFlashPayInputAct, ImpIDFlashPayInputView> implements IDFlashPayInputView
{
    private static final String TAG = DFlashPayInputAct.class.getSimpleName();
    private static final int BIND_OR_QUERY = 101;
    //region 变量
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.et_flash_pay_money)
    EditText etPayAmount;
    @BindView(R.id.btn_flash_pay_commit)
    Button btnPayCommit;

    @BindView(R.id.btn_bind_ok)
    Button btnBindOk;
    @BindView(R.id.et_anchor_id)
    EditText etAnchorId;
    @BindView(R.id.itv_ok)//二维码按钮
            IconTextView itvOk;


    @BindView(R.id.tv_flash_pay_polity)
    TextView tvFlashPayPolity;
    @BindView(R.id.tv_discount_money_amount)
    TextView tvMoneyAmount;


    @BindView(R.id.afl_no_recommed_host)
    AutoLinearLayout aflNoRecommedHost;
    @BindView(R.id.afl_has_recommed_host)
    FrameLayout aflHasRecommedHost;
    @BindView(R.id.rtv_host_head)
    RoundedImageView rtvHostHead;
    @BindView(R.id.tv_host_name)
    TextView tvHostName;
    @BindView(R.id.itv_clear_host)
    TextView itvClearHost;


    private String resId, resName;
    private String balance;
    private Dialog pDialog;
    private Activity mAct;
    private String uId = "";
    private String uphUrl = "";
    private String userName = "";
    private String id = "";
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dflash_pay_input);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getAccountBalance();
        }
        PayHelper.clearPayHelperListeners();
        PayHelper.setIHsPayPendingListener(new PendingPayFinish(this));//用汇昇币支付触发
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    void afterViews()
    {
        mAct = this;
        AndroidBug5497Workaround.assistActivity(this);
        resId = getIntent().getStringExtra("resId");
        resName = getIntent().getStringExtra("resName");
        topBar.setTitle(resName);
        topBar.getLeftButton().setVisibility(View.VISIBLE);
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mAct.finish();
            }

            @Override
            public void left2Click(View view)
            {

            }

            @Override
            public void rightClick(View view)
            {

            }
        });
        etPayAmount.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (!s.toString().contains("."))
                {
                    if (s.toString().length() > 6)
                    {
                        s = s.toString().substring(0, s.toString().length() - 1);
                        etPayAmount.setText(s);
                        etPayAmount.setSelection(s.length());
                    }
                }

                if (s.toString().contains("."))
                {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2)
                    {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etPayAmount.setText(s);
                        etPayAmount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals("."))
                {
                    s = "0" + s;
                    etPayAmount.setText(s);
                    etPayAmount.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1)
                {
                    if (!s.toString().substring(1, 2).equals("."))
                    {
                        etPayAmount.setText(s.subSequence(0, 1));
                        etPayAmount.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                Logger.t(TAG).d("tvMoneyAmount:" + etPayAmount.getText().toString().trim());

                try
                {
                    if (TextUtils.isEmpty(etPayAmount.getText().toString().trim()))
                    {
                        tvMoneyAmount.setText("0.00");
                        return;
                    }
                    Double result = Double.parseDouble(etPayAmount.getText().toString().trim());
                    if (result < 1)
                    {
                        Logger.t(TAG).d("result:" + result);
                        tvMoneyAmount.setText(String.format("%s", new java.text.DecimalFormat("#0.00")
                                .format(result)));
                    }
                    else
                    {
                        tvMoneyAmount.setText(String.format("%s", new java.text.DecimalFormat("#.00")
                                .format(result)));
                    }
                } catch (Exception e)
                {
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });

        etAnchorId.addTextChangedListener(watcher);

        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理...");
        pDialog.setCancelable(false);
        if (mPresenter != null)
            mPresenter.getMyConsultant();
    }

    @Override
    protected ImpIDFlashPayInputView createPresenter()
    {
        return new ImpIDFlashPayInputView();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case BIND_OR_QUERY:
                if (resultCode == RESULT_OK)
                {
                    if (data == null)
                        return;
                    CommonUtils.jumpHelperId = "-1";
                    this.uId = data.getStringExtra("consultant");
                    this.id = data.getStringExtra("consultantId");
                    this.uphUrl = data.getStringExtra("consultantPhUrl");
                    this.userName = data.getStringExtra("consultantName");
                    etAnchorId.setText("");
                    aflNoRecommedHost.setVisibility(View.GONE);
                    aflHasRecommedHost.setVisibility(View.VISIBLE);
                    GlideApp.with(EamApplication.getInstance())
                            .asBitmap()
                            .load(this.uphUrl)
                            .placeholder(R.drawable.userhead)
                            .into(rtvHostHead);
                    tvHostName.setText(this.userName + "(" + this.id + ")");
                }
                break;
            default:
                break;
        }
    }

    private void setUiAfterGetBalance(String balance)
    {
        etPayAmount.setHint(String.format("账户余额￥%s", balance));
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, getString(R.string.request_fault_due_to_net), exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void callServerFailCallback(String interfaceName, String code, String bodyStr, Map<String,Object>tranMap)
    {
        Logger.t(TAG).d("错误码为：%s", code);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_balance:
                break;
            case NetInterfaceConstant.OrderC_quickPay:
                try
                {
                    if (code.equals(ErrorCodeTable.PAYPWD_ERR))
                    {
                        JSONObject body = new JSONObject(bodyStr);
                        String surplus=body.getString("surplus");
                        if (surplus.equals("0"))
                        {
                            mAct.finish();
                            ToastUtils.showShort("由于您输错的次数过多，支付密码已被锁定，请3小时之后再试!");
                        }
                        else
                        {
                            ToastUtils.showShort(String.format("支付密码错误，您还可以输入%s次", surplus));
                            ((GridPasswordView) tranMap.get("gPasswordView")).clearPassword();
                        }
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case NetInterfaceConstant.ConsultantC_myConsultant:
                itvOk.setText("{eam-e987}");
                itvOk.setTextColor(ContextCompat.getColor(mAct, R.color.C0315));
                if ("NO_CONSULTANT".equals(code))
                {
                }
                break;
            case NetInterfaceConstant.ConsultantC_queryConsultant:
                break;
            default:
                ToastUtils.showShort(ErrorCodeTable.parseErrorCode(code));
                break;
        }
//        ToastUtils.showShort(ErrorCodeTable.parseErrorCode(code));
    }

    @Override
    public void getAccountBalanceCallback(String balanceStr)
    {
        balance = balanceStr;
        setUiAfterGetBalance(balance);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void quickPayCallback(String bodyStr, GridPasswordView gridPasswordView)
    {
        try
        {
            JSONObject body = new JSONObject(bodyStr);
            Intent intent = new Intent(mAct,DQuickPayOrderDetailAct.class);
            intent.putExtra("orderId", body.getString("orderId"));
            intent.putExtra("amount", body.getString("orderCos2"));
            intent.putExtra("mobile", body.getString("mobile"));
            intent.putExtra("time", body.getString("smtTime"));
            intent.putExtra("resName", resName);
            intent.putExtra("id", id);
            intent.putExtra("uphUrl", uphUrl);
            intent.putExtra("userName", userName);
            mAct.startActivity(intent);
            mAct.finish();
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void getMyConsultantCallback(String bodyStr)
    {
        try
        {
            JSONObject body = new JSONObject(bodyStr);
            String consultant = body.getString("consultant");
            String consultantId = body.getString("consultantId");
            String consultantName = body.getString("consultantName");
            String consultantPhUrl = body.getString("consultantPhurl");
            this.uId = consultant;
            this.id = consultantId;
            this.uphUrl = consultantPhUrl;
            this.userName = consultantName;
            aflNoRecommedHost.setVisibility(View.GONE);
            aflHasRecommedHost.setVisibility(View.VISIBLE);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(consultantPhUrl)
                    .placeholder(R.drawable.userhead)
                    .into(rtvHostHead);
            tvHostName.setText(consultantName + "(" + consultantId + ")");
            itvOk.setText("{eam-e983}");
            itvOk.setEnabled(false);
            itvOk.setTextColor(ContextCompat.getColor(mAct, R.color.C0315));
            itvClearHost.setVisibility(View.GONE);//--wb
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void queryConsultantCallback(String bodyStr)
    {
        try
        {
            JSONObject body = new JSONObject(bodyStr);
            String consultant = body.getString("consultant");
            String consultantId = body.getString("consultantId");
            String consultantName = body.getString("consultantName");
            String consultantPhUrl = body.getString("consultantPhurl");
            this.uId = consultant;
            this.id = consultantId;
            this.uphUrl = consultantPhUrl;
            this.userName = consultantName;
            etAnchorId.setText("");
            aflNoRecommedHost.setVisibility(View.GONE);
            aflHasRecommedHost.setVisibility(View.VISIBLE);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(consultantPhUrl)
                    .placeholder(R.drawable.userhead)
                    .into(rtvHostHead);
            tvHostName.setText(consultantName + "(" + consultantId + ")");
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }
    }


    @OnClick({R.id.btn_flash_pay_commit,R.id.tv_flash_pay_polity,R.id.itv_clear_host,R.id.btn_bind_ok,R.id.itv_ok,})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_flash_pay_commit://闪付
                try
                {
                    Double result = Double.parseDouble(etPayAmount.getText().toString().trim());
                    if (result <= 0)
                    {
                        ToastUtils.showShort("输入金额必须大于0元");
                        return;
                    }
                    if (result < 1)
                    {
                        ToastUtils.showShort("最低闪付1元");
                        return;
                    }
                    if (result > Double.parseDouble(balance))
                    {
                        //提示是否绑定支付宝
                        new CustomAlertDialog(mAct)
                                .builder()
                                .setTitle("提示")
                                .setMsg("您的余额不足，是否去充值？")
                                .setPositiveButton("去充值", new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Intent intent = new Intent(mAct, MyInfoAccountAct2.class);
                                        startActivity(intent);
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
                        return;
                    }
                } catch (Exception e)
                {
                    ToastUtils.showShort("请输入正确的金额");
                    return;
                }
                //提示是否设置密码
                new CustomAlertDialog(mAct)
                        .builder()
                        .setTitle("提示")
                        .setMsg("请向商家确认消费金额后支付！")
                        .setPositiveButton("确定支付", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                PayHelper.clearPayHelperListeners();
                                PayHelper.setIHsPayPendingListener(new PendingPayFinish(DFlashPayInputAct.this));//用汇昇币支付触发
                                PayBean payBean = new PayBean();
                                payBean.setOrderId("");
                                payBean.setMyPayType(EamConstant.EAM_PAY_PENDING);
                                payBean.setAmount(etPayAmount.getText().toString());
                                payBean.setSubject("闪付");               // 商品的标题
                                payBean.setBody("使用闪付支付");        // 商品的描述信息
                                PayHelper.checkPayPasswordState(mAct, payBean);
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                            }
                        }).show();
                break;
            case R.id.tv_flash_pay_polity://闪付规则
                Intent intent1 = new Intent(mAct,DQuickPayPolityAct.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
                break;
            case R.id.itv_clear_host:
                this.uId = "";
                this.id = "";
                this.uphUrl = "";
                this.userName = "";
                itvOk.setText("{eam-e987}");
                itvOk.setTextColor(ContextCompat.getColor(mAct, R.color.C0315));
                aflNoRecommedHost.setVisibility(View.VISIBLE);
                aflHasRecommedHost.setVisibility(View.GONE);
                break;
            case R.id.btn_bind_ok:
                if (mPresenter != null)
                    mPresenter.queryConsultant(etAnchorId.getText().toString().trim());
                break;
            case R.id.itv_ok:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(mAct, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(mAct,
                                new String[]{Manifest.permission.CAMERA}, 1);
                    }
                    else
                    {
                        Logger.t(TAG).d("相机权限已经被授予");
                        Intent intent = new Intent(mAct, CaptureActivity.class);
                        startActivityForResult(intent, BIND_OR_QUERY);
                        CommonUtils.jumpHelperId = "1";
                    }
                }
                else
                {
                    boolean hasCameraPermission = CommonUtils.cameraIsCanUse();
                    if (hasCameraPermission)
                    {
                        Intent intent = new Intent(mAct, CaptureActivity.class);
                        startActivityForResult(intent, BIND_OR_QUERY);
                        CommonUtils.jumpHelperId = "1";
                    }
                    else
                    {
                        ToastUtils.showShort( "请开启相机功能");
                    }
                }
                break;
            default:
                break;
        }
    }



    private static class PendingPayFinish implements IHsPayPendingListener
    {
        private final WeakReference<DFlashPayInputAct> mActRef;

        private PendingPayFinish(DFlashPayInputAct mAct)
        {
            this.mActRef = new WeakReference<DFlashPayInputAct>(mAct);
        }

        @Override
        public void payPending(String passWord, GridPasswordView gridPasswordView)
        {
            final DFlashPayInputAct cAct = mActRef.get();
            if (cAct != null)
            {
                Double result = Double.parseDouble(cAct.etPayAmount.getText().toString().trim());
                if (cAct.mPresenter != null)
                {
                    if (cAct.pDialog != null && !cAct.pDialog.isShowing())
                        cAct.pDialog.show();
                    cAct.mPresenter.quickPay2(cAct.resId, CommonUtils.keep2Decimal(result), null, passWord,
                            TextUtils.isEmpty(cAct.uId) ? "" : cAct.uId, gridPasswordView);
                }
            }
        }
    }

    TextWatcher watcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            if (s.length() > 0)
                btnBindOk.setVisibility(View.VISIBLE);
            else
                btnBindOk.setVisibility(View.GONE);
        }
    };
}
