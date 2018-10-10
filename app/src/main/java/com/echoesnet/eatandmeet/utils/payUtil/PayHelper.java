package com.echoesnet.eatandmeet.utils.payUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MySetNewPayPwAct;
import com.echoesnet.eatandmeet.activities.MySetPayPwManagerAct;
import com.echoesnet.eatandmeet.activities.MyVerifyIdAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.payPopupWindow.IPayTypeSelectedListener;
import com.echoesnet.eatandmeet.views.widgets.payPopupWindow.PayPopupDismissListener;
import com.echoesnet.eatandmeet.views.widgets.payPopupWindow.PayWaysPopup;
import com.google.gson.Gson;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by wangben on 2016/6/24.
 */
public class PayHelper
{
    private final static String TAG = PayHelper.class.getSimpleName();

    private static Dialog payPassWordDialog;
    private static String chargeId, streamId, payAmount, payType, orderId;

    public static IPayFinishedListener getmIPayFinishedListener()
    {
        return mIPayFinishedListener;
    }

    public static IHsPayPendingListener getmHsPayPendingListener()
    {
        return mHsPayPendingListener;
    }

    public static IPayCancelListener getmPayCancelListener()
    {
        return mPayCancelListener;
    }

    private static IPayFinishedListener mIPayFinishedListener;
    private static IHsPayPendingListener mHsPayPendingListener;
    private static IPayCancelListener mPayCancelListener;

    private static PayWaysPopup myPopWindow;

    private PayHelper()
    {
    }


    private static void showPayWindow(View view, final PayBean payBean, final Activity mContext, String accountBalance,
                                      final PayMetadataBean payMetadataBean, final String cancelItem[])
    {

        if (myPopWindow != null)
        {

            clearPopupWindows();
        }

        if (myPopWindow == null)
        {
            myPopWindow = new PayWaysPopup(mContext, accountBalance, payBean.getAmount(), cancelItem);
        }
//        myPopWindow = new PayWaysPopup(mContext, accountBalance, payBean.getAmount());
        myPopWindow.backgroundAlpha(0.5f);
        myPopWindow.setOutsideTouchable(true);
        myPopWindow.setFocusable(true);
        myPopWindow.setOnDismissListener(new PayPopupDismissListener(myPopWindow));
        myPopWindow.setPayCancelListener(new IPayCancelListener()
        {
            @Override
            public void payCanceled()
            {
                Logger.t(TAG).d("关闭");
                if (mPayCancelListener != null)
                    mPayCancelListener.payCanceled();
                myPopWindow = null;
            }
        });


        if (myPopWindow != null)
        {

            myPopWindow.showPopupWindow(view);
        }

        myPopWindow.setPayTypeSelectedListener(new IPayTypeSelectedListener()
        {
            @Override
            public void HsPaySelected()
            {
                checkPayPasswordState(mContext, payBean);
            }

            @Override
            public void ThirdPartPaySelected(String payType)
            {
                // 订单总金额, 单位为对应币种的最小货币单位，例如：人民币为分（如订单总金额为 1 元，此处请填 100）
                Logger.t(TAG).d("支付金额--> " + payAmount);
                payBean.setChannel(payType);             // 支付使用的第三方支付渠道 (alipay:支付宝手机支付、alipay_wap:支付宝手机网页支付; upacp:银联全渠道支付、wx:微信支付)
                payByThirdParty(mContext, payBean, payMetadataBean);
            }
        });
    }

    /**
     * 获得账户余额
     *
     * @param view
     * @param payBean
     * @param mContext
     * @param payMetadataBean
     */
    private static void getAccountBalance(final View view, final PayBean payBean, final Activity mContext, final PayMetadataBean payMetadataBean, final String cancelItem[])
    {
        final Dialog pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);
        pDialog.show();

        ArrayMap<String, String> reqParamMap = new ArrayMap<>();
        reqParamMap.put(ConstCodeTable.uId, SharePreUtils.getUId(mContext));
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(mContext));
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(mContext));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    showPayWindow(view, payBean, mContext, new JSONObject(response).getString("balance")
                            , payMetadataBean, cancelItem);
                    Logger.t(TAG).d("弹出支付窗口");
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, NetInterfaceConstant.UserC_newBalance, reqParamMap);
    }


    /**
     * 回声支付密码框
     */
    private static Dialog showPayPasswordDialog(final Activity mContext, final PayBean payBean)
    {
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_password, null);
        dialog.setContentView(contentView);
        TextView forgetPw = (TextView) contentView.findViewById(R.id.tv_forget_pw);
        TextView balance = (TextView) contentView.findViewById(R.id.balance_dialog);
        float money = Float.parseFloat(payBean.getAmount()) / 100;

        balance.setText("￥" + CommonUtils.keep2Decimal(money));

        forgetPw.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //跳入管理支付密码
                Intent i = new Intent(mContext, MySetPayPwManagerAct.class);
                i.putExtra("toPwManager", "true");
                mContext.startActivity(i);
            }
        });
        Button btnCancel = (Button) contentView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //取消密码框，可以换其他支付方式
                dialog.dismiss();

                //点击取消按钮，打开订单详情，订单详情返回则需要跳过确认订单界面，返回前面的页面（不打开订单详情，点击左下角可以返回订单详情--wb）
/*                Intent intent = DOrderRecordDetail_.intent(mContext).get();
                intent.putExtra("orderId", payBean.getOrderId());
                intent.putExtra(EamConstant.EAM_ORDER_DETAIL_OPEN_SOURCE, "pay");
                mContext.startActivity(intent);
                mContext.finish();
                dialog.dismiss();*/
            }
        });
        final GridPasswordView pwView = (GridPasswordView) contentView.findViewById(R.id.pswView);
        pwView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener()
        {
            @Override
            public void onTextChanged(String psw)
            {

            }

            @Override
            public void onInputFinish(String psw)
            {
                //如果是其他支付，例如红包则使用此监听（非订单的支付都可以使用此回调调用相应的接口，下面的是针对订单支付）
                if (payBean.getMyPayType().equals(EamConstant.EAM_PAY_PENDING))
                {
                    if (mHsPayPendingListener != null)
                        mHsPayPendingListener.payPending(psw, pwView);
                }
                else if (payBean.getMyPayType().equals(EamConstant.EAM_PAY_CLUBPAY))
                {
                    postPayOrder2(payBean.getOrderId(), "0", CommonUtils.keep2Decimal(Double.parseDouble(payBean.getAmount()) / 100), "", mContext, psw, dialog, pwView, 1);
                }
                else
                {
                    Logger.t(TAG).d("输入完毕--> " + String.valueOf(Double.parseDouble(payBean.getAmount()) / 100) + " ,  " + payBean.getAmount()
                            + " , " + CommonUtils.keep2Decimal(Double.parseDouble(payBean.getAmount()) / 100));
                    //--zdw--回声支付需要除以100
                    postPayOrder2(payBean.getOrderId(), "0", CommonUtils.keep2Decimal(Double.parseDouble(payBean.getAmount()) / 100), "", mContext, psw, dialog, pwView, 0);
                }
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (CommonUtils.getScreenSize(mContext).width * 0.85f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        return dialog;
    }

    /**
     * 回声支付订单接口(升级版)
     *
     * @param orderId
     * @param payType
     * @param payAmount
     * @param streamId
     */
    private static void postPayOrder2(final String orderId, String payType, String payAmount, String streamId,
                                      final Activity mContext, String password, final Dialog payDialog, final GridPasswordView pwView, int orderType)
    {
        String netInterface = NetInterfaceConstant.OrderC_payOrder;
        if (orderType == 1)
            netInterface = NetInterfaceConstant.HomepartyC_payment;

        final Dialog pDialog = DialogUtil.getCommonDialog(mContext, "正在支付...");
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        reqParamMap.put(ConstCodeTable.payType, payType);
        reqParamMap.put(ConstCodeTable.payAmount, payAmount);
        reqParamMap.put(ConstCodeTable.streamId, streamId);
        try
        {
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(password));
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                //支付成功后，取消密码框
                if (payPassWordDialog != null && payPassWordDialog.isShowing())
                    payPassWordDialog.dismiss();
                ToastUtils.showShort("支付成功");
                if (mIPayFinishedListener != null)
                    mIPayFinishedListener.PayFinished(orderId, "", "hs");
                //支付成功则取消支付框
                if (payDialog != null && payDialog.isShowing())
                    payDialog.dismiss();
                if (myPopWindow != null && myPopWindow.isShowing())
                {
                    myPopWindow.dismiss();
                    myPopWindow = null;
                }
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                super.onHandledError(apiE);
                String code = apiE.getErrorCode();
                try
                {
                    if (ErrorCodeTable.PAYPWD_ERR.equals(code))
                    {
                        String obj = apiE.getErrBody();
                        JSONObject objs = new JSONObject(obj);
                        String surplus = objs.getString("surplus");
                        if (Integer.parseInt(surplus) != 0)
                        {
                            ToastUtils.showShort("密码输入错误,还剩余 " + surplus + " 次机会！");
                            pwView.clearPassword();
                        }
                        else
                        {
/*                                        if (mIPayFinishedListener != null)
                                            mIPayFinishedListener.PayFailed(orderId, "", "hs");*/
                            payPassWordDialog.dismiss();
                            ToastUtils.showShort("由于您输错的次数过多，支付密码为锁定状态，3小时后可自动解锁！");
                        }
//                                    ToastUtils.showShort(mContext, ErrorCodeTable.parseErrorCode(code));
                        Logger.t(TAG).d("错误码为：%s", code);
                        return;
                    }
                    else if (ErrorCodeTable.REPAY_COUNTDOWN_IS_FINISH.equals(code))
                    {
                        payPassWordDialog.dismiss();
                        ToastUtils.showShort("由于您输错的次数过多，支付密码为锁定状态，3小时后可自动解锁！");
                    }
                } catch (JSONException e)
                {
                    Logger.t(TAG).d(e.getMessage());
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, netInterface, reqParamMap);
    }

    //region 公共方法

    /**
     * 支付接口
     * 请在UI线程调用
     *
     * @param view    支付页面要显示的view
     * @param payBean 支付信息
     */
    public static void payOrder(View view, final PayBean payBean, final Activity mContext, PayMetadataBean payMetadataBean)
    {
        getAccountBalance(view, payBean, mContext, payMetadataBean, null);
    }

    public static void payOrder(View view, final PayBean payBean, final Activity mContext, PayMetadataBean payMetadataBean, final String cancelItem[])
    {
        getAccountBalance(view, payBean, mContext, payMetadataBean, cancelItem);
    }

    /**
     * 验证支付密码是否设置
     */
    public static void checkPayPasswordState(final Activity mContext, final PayBean payBean)
    {
        final Dialog pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                payPassWordDialog = showPayPasswordDialog(mContext, payBean);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                String code = apiE.getErrorCode();
                //如果没有设置支付密码提示是否设置
                if (code.equals("PAYPWD_NULL"))
                {
                    //提示是否设置密码
                    new CustomAlertDialog(mContext)
                            .builder()
                            .setTitle("提示")
                            .setMsg("您还未设置支付密码，是否去设置？")
                            .setPositiveButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent = new Intent(mContext, MyVerifyIdAct.class);
                                    intent.putExtra(EamConstant.EAM_VERIFY_ID_OPEN_SOURCE, "PayHelper");
                                    //intent.putExtra(EamConstant.EAM_SET_PAY_PW_OPEN_SOURCE,mContext.getClass().getSimpleName());
                                    mContext.startActivity(intent);
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
                else if (code.equals("PAYPWD_NULL_REAL"))
                {
                    ToastUtils.showShort("请先设置支付密码");
                    //验证成功去设置支付密码
                    Intent intent = new Intent(mContext, MySetNewPayPwAct.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("type", "setPayPw");
                    intent.putExtra(EamConstant.EAM_SET_PAY_PW_OPEN_SOURCE,
                            mContext.getIntent().getStringExtra(EamConstant.EAM_SET_PAY_PW_OPEN_SOURCE));
                    mContext.startActivity(intent);
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, NetInterfaceConstant.UserC_payPwd, reqParamMap);
    }

    /**
     * 使用第三方支付
     *
     * @param context
     * @param payBean     支付相关参数
     * @param payMetaBean 支付元数据，这些数据表示现在的支付时什么类型的支付，以及需要的对应的参数
     */
    public static void payByThirdParty(final Activity context, final PayBean payBean, final PayMetadataBean payMetaBean)
    {
        final Dialog pDialog = DialogUtil.getCommonDialog(context, "正在处理...");
        context.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                pDialog.show();
            }
        });

        Gson gson = new Gson();
        Map<String, String> chargeParams = NetHelper.getCommonPartOfParam(null);
        chargeParams.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(context));
        chargeParams.put(ConstCodeTable.uId, SharePreUtils.getUId(context));
        chargeParams.put(ConstCodeTable.token, SharePreUtils.getToken(context));
        chargeParams.put("chargeParams", gson.toJson(payBean));
        chargeParams.put("metadataBean", gson.toJson(payMetaBean));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                pDialog.dismiss();
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                pDialog.dismiss();
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                pDialog.dismiss();
                try
                {
                    JSONObject jsonBody = new JSONObject(response);
                    // 查询的 charge 对象 id
                    chargeId = jsonBody.getString("id");
                    // 流水号
                    streamId = jsonBody.getString("orderNo");
                    //单位是分
                    payAmount = payBean.getAmount();
                    payType = payBean.getChannel();
                    orderId = payBean.getOrderId();
                    Logger.t(TAG).d("chargeId--> " + chargeId + " , orderNo--> " + streamId + " , payType--> " + payType);

                    Pingpp.createPayment(context, response);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.PayC_newPay, chargeParams);

    }

    @Deprecated
    public static void thirdPartyPayStateCheck(final Activity context, final PayMetadataBean payMetaBean)
    {
        final Dialog pDialog = DialogUtil.getCommonDialog(context, "正在获取支付结果...");
        context.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                pDialog.show();
            }
        });

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //由于太快调用的话，后台还没有来得及更新支付结果，所有需要等待，暂时等待600毫秒
                try
                {
                    Thread.sleep(600);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                Map<String, String> chargeParams = NetHelper.getCommonPartOfParam(null);
                chargeParams.put(ConstCodeTable.streamId, streamId);
                chargeParams.put(ConstCodeTable.source, payMetaBean.getSource());
                chargeParams.put(ConstCodeTable.lUId, payMetaBean.getLuId());

                HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
                {
                    @Override
                    public void onHandledError(ApiException apiE)
                    {
                        super.onHandledError(apiE);
                        if (mIPayFinishedListener != null)
                            mIPayFinishedListener.PayFailed(orderId, streamId, payType);
                        pDialog.dismiss();
                    }

                    @Override
                    public void onHandledNetError(Throwable throwable)
                    {
                        super.onHandledNetError(throwable);
                        pDialog.dismiss();
                    }

                    @Override
                    public void onNext(String response)
                    {
                        super.onNext(response);
                        if (mIPayFinishedListener != null)
                            mIPayFinishedListener.PayFinished(orderId, streamId, payType);
                        pDialog.dismiss();
                    }
                }, NetInterfaceConstant.PayC_newChkPayStat, chargeParams);
            }
        }).start();
    }

    /**
     * 升级版
     *
     * @param context
     * @param payMetaBean
     */
    public static void thirdPartyPayStateCheck2(final Activity context, final PayMetadataBean payMetaBean)
    {
        final Dialog pDialog = DialogUtil.getCommonDialog(context, "正在获取支付结果...");
        context.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                pDialog.show();
            }
        });

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //由于太快调用的话，后台还没有来得及更新支付结果，所以需要等待，暂时等待600毫秒
                try
                {
                    Thread.sleep(600);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                Map<String, String> chargeParams = NetHelper.getCommonPartOfParam(null);
                chargeParams.put(ConstCodeTable.streamId, streamId);
                chargeParams.put(ConstCodeTable.source, payMetaBean.getSource());
                chargeParams.put(ConstCodeTable.lUId, payMetaBean.getLuId());

                HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
                {
                    @Override
                    public void onNext(String response)
                    {
                        super.onNext(response);
                        pDialog.dismiss();
                        if (mIPayFinishedListener != null)
                            mIPayFinishedListener.PayFinished(orderId, streamId, payType);
                    }

                    @Override
                    public void onHandledNetError(Throwable throwable)
                    {
                        super.onHandledNetError(throwable);
                        pDialog.dismiss();
                    }

                    @Override
                    public void onHandledError(ApiException apiE)
                    {
                        super.onHandledError(apiE);
                        pDialog.dismiss();
                        if (mIPayFinishedListener != null)
                            mIPayFinishedListener.PayFailed(orderId, streamId, payType);
                    }
                }, NetInterfaceConstant.PayC_newChkPayStat, chargeParams);
            }
        }).start();
    }

    //region 原来的支付接口
    /**
     * 支付ping++ 接口1
     *
     * @param context
     * @param payBean
     */
/*    public static void getPingPay(final Activity context, final PayBean payBean)
    {
        final Dialog pDialog =DialogUtil.getCommonDialog(context,"正在处理...");;
        context.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                pDialog.show();
            }
        });
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(context));
        chargeParams.put(ConstCodeTable.uId, SharePreUtils.getUId(context));
        chargeParams.put(ConstCodeTable.token, SharePreUtils.getToken(context));
        chargeParams.put("chargeParams", payBean);
        Logger.t(TAG).d("提交的参数》"+NetHelper.getRequestJsonStr("PayC/pay", new Gson().toJson(chargeParams)));
        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .mediaType(NetHelper.JSON)
                .content(NetHelper.getRequestJsonStr("PayC/pay", new Gson().toJson(chargeParams)))
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(context, null,TAG,e);
                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("测试支付--> " + response);
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.getInt("status");
                            if (status == 0)
                            {
                                String body = jsonObject.getString("body");
                                if (TextUtils.isEmpty(body))
                                {
                                    Logger.t(TAG).d("未获取到接口返回的body信息");
                                    ToastUtils.showShort(context,"支付错误，请稍后重试。");
                                    return;
                                }
                                else
                                {
                                    JSONObject jsonBody = new JSONObject(body);
                                    // 查询的 charge 对象 id
                                    chargeId = jsonBody.getString("id");
                                    // 流水号
                                    streamId = jsonBody.getString("orderNo");
                                    //单位是分
                                    payAmount = payBean.getAmount();
                                    payType = payBean.getChannel();
                                    orderId = payBean.getOrderId();
                                    Logger.t(TAG).d("chargeId--> " + chargeId + " , orderNo--> " + streamId + " , payType--> " + payType);

                                    Pingpp.createPayment((Activity) context, body);
                                }
                            }
                            else
                            {
                                String code = jsonObject.getString("code");
                                if (!ErrorCodeTable.handleErrorCode(code,context))
                                         ToastUtils.showShort(context, ErrorCodeTable.parseErrorCode(code));
                                Logger.t(TAG).d("错误码为：%s", code);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                        }
                        finally
                        {
                            pDialog.dismiss();
                        }
                    }
                });
    }*/

    /**
     * 校验ping++支付状态 接口2
     */
/*    public static void thirdPartyPayStateCheck(final Activity context)
    {
        final Dialog pDialog =DialogUtil.getCommonDialog(context,"正在获取支付结果...");;
        context.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                pDialog.show();
            }
        });
        Map<String, String> chargeParams = new HashMap<String, String>();
        chargeParams.put(ConstCodeTable.token, SharePreUtils.getToken(context));
        chargeParams.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(context));
        chargeParams.put(ConstCodeTable.uId, SharePreUtils.getUId(context));
        chargeParams.put(ConstCodeTable.payStat, "");
        chargeParams.put(ConstCodeTable.streamId, streamId);
        //传入了分为单位的金额
        chargeParams.put(ConstCodeTable.payAmount, payAmount);
        chargeParams.put(ConstCodeTable.chargeId, chargeId);
        Logger.t(TAG).d("请求参数为》 "+NetHelper.getRequestJsonStr("PayC/chkPayStat", new Gson().toJson(chargeParams)));
        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .mediaType(NetHelper.JSON)
                .content(NetHelper.getRequestJsonStr("PayC/chkPayStat", new Gson().toJson(chargeParams)))
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(context, null,TAG,e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("校验ping++支付状态返回》 " + response);
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.getInt("status");
                            if (status == 0)
                            {
                                if (mIPayFinishedListener != null)
                                    mIPayFinishedListener.PayFinished(orderId, streamId, payType);
                            }
                            else
                            {
                                if (mIPayFinishedListener != null)
                                    mIPayFinishedListener.PayFailed(orderId, streamId, payType);

                                String code = jsonObject.getString("code");
                                if (!ErrorCodeTable.handleErrorCode(code, context))
                                    ToastUtils.showShort(context, ErrorCodeTable.parseErrorCode(code));
                                Logger.t(TAG).d("错误码为：%s", code);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Logger.t(TAG).d(e.getMessage());
                        }
                        catch (Exception e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                        }
                        finally
                        {
                            pDialog.dismiss();
                        }
                    }
                });
    }*/
    //endregion
    public static void setIPayFinishedListener(IPayFinishedListener listener)
    {
        mIPayFinishedListener = listener;
    }

    public static void setIHsPayPendingListener(IHsPayPendingListener listener)
    {
        mHsPayPendingListener = listener;
    }

    public static void setIPayCancelListener(IPayCancelListener listener)
    {
        mPayCancelListener = listener;
    }

    /**
     * 清除所有事件，在调用支付之前清除
     */
    public static void clearPayHelperListeners()
    {
        mPayCancelListener = null;
        mHsPayPendingListener = null;
        mIPayFinishedListener = null;
    }

    /**
     * 重置接口
     */
    public static void restorePayHelperListeners(IPayFinishedListener iPayFinishedListener,
                                                 IHsPayPendingListener hsPayPendingListener,
                                                 IPayCancelListener payCancelListener)
    {
        mPayCancelListener = payCancelListener;
        mHsPayPendingListener = hsPayPendingListener;
        mIPayFinishedListener = iPayFinishedListener;
    }


    public static void clearPopupWindows()
    {
        try
        {
            if (myPopWindow != null && myPopWindow.isShowing())
            {
                myPopWindow.backgroundAlpha(1);
                myPopWindow.dismiss();
            }
            myPopWindow = null;
            if (payPassWordDialog != null && payPassWordDialog.isShowing())
            {
                payPassWordDialog.dismiss();
                payPassWordDialog = null;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("支付框关闭发生错误");
        }
    }

    public static void dismissPopupWindow(Activity context)
    {
        if (myPopWindow != null && myPopWindow.isShowing())
        {
            myPopWindow.dismiss();
            myPopWindow = null;
        }
        else
        {
            context.finish();
        }
    }

    public static void clearPayPassword(Activity context)
    {
        if (payPassWordDialog != null && payPassWordDialog.isShowing())
        {
            GridPasswordView pwView = (GridPasswordView) payPassWordDialog.findViewById(R.id.pswView);
            if (pwView != null)
                pwView.clearPassword();
        }
        else
        {
            context.finish();
        }
    }
    //endregion
}
