package com.echoesnet.eatandmeet.activities.live;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CopyOrderMealAct;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.activities.MyCommentAct;
import com.echoesnet.eatandmeet.activities.MyDateAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.models.bean.QRCodeBean;
import com.echoesnet.eatandmeet.presenters.ImpILiveShowBootyCallActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILiveShowBootyCallActView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.Constants;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IHsPayPendingListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 直播约炮界面
 * Created by an on 2016/12/29 0029.
 */
public class LiveShowBootyCallAct extends MVPBaseActivity<ILiveShowBootyCallActView, ImpILiveShowBootyCallActView> implements ILiveShowBootyCallActView
{
    private static final String TAG = LiveShowBootyCallAct.class.getCanonicalName();

    private static final int GO_TO_WISH_LIST = 1001;

    @BindView(R.id.web_booty_call)
    ProgressBridgeWebView mWebView;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    private final int TO_SHOW_BOOTY_CALL = 1;
    private final int TO_MY_COMMENT = 2;

    private String roomId;
    private String hxId;//邀请的主播环信id
    private String liveSource;//0 没开直播 1 腾讯直播 2 ucloud直播
    private String hostUId;//主播uid
    private String openFrom;//打开此页面的源头
    //约会流水号
    private String streamId;
    private Activity mActivity;
    public String moneyStr;//约TA所需金额
    private String showType = "member";//显示标识默认用户  member 用户我的约会  host 主播邀请信息
    public String date;//约会时间

    private MyProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_show_booty_call);
        ButterKnife.bind(this);
        afterView();
    }

    private void afterView()
    {
        mActivity = this;
        roomId = getIntent().getStringExtra("roomId");
        hostUId = getIntent().getStringExtra("hostUId");
        showType = getIntent().getStringExtra("showType");
        streamId = getIntent().getStringExtra("streamId");
        openFrom = getIntent().getStringExtra("openFrom");
        Logger.t(TAG).d("roomId===" + roomId + "hostUId==" + hostUId + "showType==" + showType + "streamId>>" + streamId);
        pDialog = new MyProgressDialog()
                .buildDialog(mActivity)
                .setDescription("正在加载...");
        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                Logger.t(TAG).d(">>>>>>>>leftClick");
                finish();
            }

            @Override
            public void rightClick(View view)
            {
            }

            @Override
            public void right2Click(View view)
            {
                Intent intent = new Intent(mActivity, LiveShowLevelInforAct.class);
                intent.putExtra("luid", hostUId);
                startActivity(intent);
            }
        }).setText("我的约会");
        topBar.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.transparent));
        List<TextView> navBtns = topBar.getNavBtns(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i);
            switch (i)
            {
                case 0:
                    tv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.transparent));
                    tv.setText("{eam-n-previous}");
                    break;
                case 1:
                    tv.setText("主播等级");
                    tv.setTextSize(16);
                    break;
                default:
                    break;
            }
        }
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setProgressListener(new ProgressBridgeWebView.LoadingProgressListener()
        {
            @Override
            public void onProgressChanged(int progress)
            {
                if (progress == 100 && pDialog != null)
                    pDialog.dismiss();
            }
        });
        goToMyBootyCall();
        //发送邀请
        mWebView.registerHandler("sendReceive", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据  luId：被邀请人uId
                Logger.t(TAG).d("JStoJava" + data);
                try
                {
                    JSONObject json = new JSONObject(data);
                    moneyStr = json.getString("amount");
                    date = json.getString("date");
                    hxId = json.getString("imuId");
                    liveSource = json.getString("liveSource");
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                payBoogtyCall();
            }
        });
        //查看大图片
        mWebView.registerHandler("showImage", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据  luId：被邀请人uId
                Logger.t(TAG).d("showImage" + data);
                try
                {
                    JSONObject json = new JSONObject(data);
                    int position = json.getInt("position");
                    String urls = json.getString("urlList");
                    Logger.t(TAG).d("urls" + urls);
                    JSONArray jsonArray = new JSONArray(urls);
                    ArrayList<String> urlList = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        urlList.add(jsonArray.get(i).toString());
                        Logger.t(TAG).d(jsonArray.get(i).toString());
                    }
                    Logger.t(TAG).d(urlList.toString());
                    CommonUtils.showImageBrowser(mActivity, urlList, position, null);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        //查看等级说明
        mWebView.registerHandler("showLevelIntro", new BridgeHandler()
        {

            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("打开等级说明" + data);
                try
                {
                    JSONObject json = new JSONObject(data);
                    hostUId = json.getString("luId");
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                Intent intent = new Intent(mActivity, LiveShowLevelInforAct.class);
                intent.putExtra("luid", hostUId);
                startActivity(intent);
            }
        });
        //返回
        mWebView.registerHandler("goBack", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("JStoJava" + data);

            }
        });
        //跳到心愿单页
        mWebView.registerHandler("goToWishList", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("JStoJava" + data);
                Intent intentDate = new Intent(mActivity, MyDateAct.class);
                intentDate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//以singleTask形式启动,启动模式一定要慎用。出bug不好查--wb
                startActivityForResult(intentDate, GO_TO_WISH_LIST);
            }
        });
        //显示二维码
        mWebView.registerHandler("showQRCode", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("JStoJava" + data);
                try
                {
                    JSONObject json = new JSONObject(data);
                    String code = json.getString("code");
                    QRCodeBean qrCodeBean = new QRCodeBean();
                    qrCodeBean.setType("DATE_ORDER_ID");
                    qrCodeBean.setContent(code);
                    String QRStr = new Gson().toJson(qrCodeBean);
                    Logger.t(TAG).d("二维码内容--> " + QRStr);
                    getQRCode(CommonUtils.createQRImage(mActivity, QRStr, 200, 200));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        // 开始订餐
        mWebView.registerHandler("toOrderMeal", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("开始订餐--> " + data);
                JSONObject jsonObject = null;
                try
                {
                    jsonObject = new JSONObject(data);
                    String streamId = jsonObject.getString("streamId");
                    String date = jsonObject.getString("date");
                    Intent intent = new Intent(mActivity, CopyOrderMealAct.class);
                    intent.putExtra("bootyCallDate", date);
                    SharePreUtils.setToOrderMeal(mActivity, "toOrderMeal");
                    SharePreUtils.setOrderType(mActivity, "1");
                    EamApplication.getInstance().dateStreamId = streamId;
                    mActivity.startActivity(intent);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
        // 评价主播
        mWebView.registerHandler("evaluateReceive", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String streamId = jsonObject.getString("streamId");
                    //从js获得数据
                    Logger.t(TAG).d("评价主播--> " + data);
                    Intent intent = new Intent(mActivity, MyCommentAct.class);
                    intent.putExtra("streamId", streamId);
                    intent.putExtra("luid", hostUId);
                    startActivityForResult(intent, TO_MY_COMMENT);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
        // 查看主播
        mWebView.registerHandler("anchorReceive", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("查看主播状态 anchorReceive--> " + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String roomId = jsonObject.getString("roomId");
                    String luId = jsonObject.getString("luId");
                    Intent intent = new Intent(mActivity, LiveShowBootyCallAct.class);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("hostUId", luId);

                    startActivityForResult(intent, TO_SHOW_BOOTY_CALL);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        //百度统计
        mWebView.registerHandler("baiduStat", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("百度统计 返回数据--> " + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String eventId = jsonObject.getString("eventId");
                    String eventName = getString(R.string.baidu_live);
//                    StatService.onEvent(mActivity, eventId, eventName, 1);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 调用h5 reload
     */
    public void reLoadWebView()
    {
        //传给js的数据
        Map<String, String> reqParamMap = new HashMap<String, String>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "pay");
        reqParamMap.put("action", "reload");
        reqParamMap.put("params", new Gson().toJson(params));
        Logger.t(TAG).d("reload to h5>>>params>>>" + reqParamMap.toString());
        //h5 reload
        mWebView.callHandler("trigger", new Gson().toJson(reqParamMap), new CallBackFunction()
        {
            @Override
            public void onCallBack(String data)
            {
                Logger.t(TAG).d("trigger>>>>js返回" + data);
            }
        });
    }

    /**
     * 二维码弹出层
     *
     * @param bitmap
     */
    private void getQRCode(Bitmap bitmap)
    {
        final Dialog dialog = new Dialog(mActivity, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.qr_code_pop, null);
        TextView textView = (TextView) contentView.findViewById(R.id.tv_qr_content);
        textView.setText("请主播使用看脸吃饭扫一扫功能扫\n二维码确认约会");
        dialog.setContentView(contentView);
        TextView tv_qr_content = (TextView) contentView.findViewById(R.id.tv_qr_content);
        SpannableStringBuilder builder = new SpannableStringBuilder(tv_qr_content.getText().toString());
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.MC3));
        builder.setSpan(redSpan, 5, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_qr_content.setText(builder);

        ImageView iv_qr = (ImageView) contentView.findViewById(R.id.iv_qr);
        iv_qr.setImageBitmap(bitmap);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (CommonUtils.getScreenSize(mActivity).width * 0.85f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void goToMyBootyCall()
    {
        if (pDialog != null)
            pDialog.show();
        mWebView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(NetInterfaceConstant.member_appointment));
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mActivity);
                reqParamMap.put("roomId", roomId);
                reqParamMap.put("luId", hostUId);
                if (!TextUtils.isEmpty(streamId))
                    reqParamMap.put("streamId", streamId);
                reqParamMap.put("openFrom", openFrom);
                function.onCallBack(new Gson().toJson(reqParamMap));
            }
        });
    }

    /**
     * 发起约吃饭支付
     */
    private void payBoogtyCall()
    {
        PayHelper.clearPayHelperListeners();
        //用汇昇币支付触发
        PayHelper.setIHsPayPendingListener(new LiveShowBootyCallAct.PendingPayFinish(this));
        PayBean payBean = new PayBean();
        payBean.setOrderId("");
        payBean.setMyPayType(EamConstant.EAM_PAY_PENDING);
        payBean.setAmount(moneyStr);
        payBean.setSubject("看脸吃饭");               // 商品的标题
        payBean.setBody("约直播吃饭");        // 商品的描述信息
        Logger.t(TAG).d("hostUId==" + hostUId);
        PayHelper.payOrder(mActivity.getWindow().getDecorView(), payBean, mActivity, new PayMetadataBean("", hostUId, "", "5", "", date));
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.AppointmentC_payWish:

                if (ErrorCodeTable.RECEIVE_DELING.equals(code))
                {
                    new CustomAlertDialog(mActivity)
                            .builder()
                            .setTitle("温馨提示")
                            .setMsg("主播已有约，支付失败")
                            .setPositiveButton("确定", null)
                            .setCancelable(false)
                            .show();
                } else if (ErrorCodeTable.PAYPWD_ERR.equals(code))
                {
                    try
                    {
                        JSONObject body = new JSONObject(errBody);
                        String surplus = body.getString("surplus");
                        PayHelper.clearPayPassword(mActivity);
                        ToastUtils.showShort("密码错误,剩余" + surplus + "次");
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void sendReceiveCallback(String response)
    {
        ToastUtils.showShort("邀请发送成功,等待接受");
        //sendC2CMessage(TXConstants.AVIMCMD_Booty_Call, "");
        reLoadWebView();
        PayHelper.clearPopupWindows();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        PayHelper.setIHsPayPendingListener(new PendingPayFinish(LiveShowBootyCallAct.this));
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        PayHelper.clearPopupWindows();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    protected ImpILiveShowBootyCallActView createPresenter()
    {
        return new ImpILiveShowBootyCallActView();
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
                Logger.t(TAG).d("pay result==" + result);
                if (result.equals("success"))
                {
                    PayHelper.setIPayFinishedListener(new LiveShowBootyCallAct.PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck2(mActivity, new PayMetadataBean("", hostUId, "", "5", "", date));
                } else if (result.equals("cancel"))
                {
                    ToastUtils.showShort( "支付取消");
                } else
                {
                    ToastUtils.showShort( "支付失败, 请重试");
                }
            }
        } else if (requestCode == GO_TO_WISH_LIST | requestCode == TO_MY_COMMENT)
        {
            mWebView.reload();
        }
    }

    /**
     * 发送C2C消息
     *
     * @param cmd
     */
    public void sendC2CMessage(final int cmd, String Param)
    {
        if ("1".equals(liveSource))
        {
            sendTxC2CMessage(cmd, Param);
        } else if ("2".equals(liveSource))
        {
            sendHxCmdC2CMsg(String.valueOf(cmd), hxId, null);
        }
    }

    /**
     * 发送腾讯c2c消息
     *
     * @param cmd
     * @param Param
     */
    private void sendTxC2CMessage(int cmd, String Param)
    {
        JSONObject inviteCmd = new JSONObject();
        try
        {
            inviteCmd.put(Constants.CMD_KEY, cmd);
            inviteCmd.put(Constants.CMD_PARAM, Param);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        String cmds = inviteCmd.toString();
        TIMMessage msg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(cmds.getBytes(Charset.forName("utf-8")));
        elem.setDesc("");
        msg.addElement(elem);
        TIMConversation mC2CConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, "u" + roomId);
        mC2CConversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>()
        {
            @Override
            public void onError(int i, String s)
            {
                Logger.t(TAG).d("发送约主播吃饭信息失败=" + i + ">>>" + s);
            }

            @Override
            public void onSuccess(TIMMessage timMessage)
            {
                Logger.t(TAG).d("发送约主播吃饭信息成功");
            }
        });
    }

    /**
     * 发送环信C2C透传消息
     *
     * @param action       消息类型
     * @param toUsername   环信ID
     * @param attributeMap 自定义属性，不带属性出入null
     */
    public void sendHxCmdC2CMsg(String action, String toUsername, Map<String, String> attributeMap)
    {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(action);
        message.setTo(toUsername);
        message.addBody(cmdMessageBody);
        message.setChatType(EMMessage.ChatType.Chat);
        if (attributeMap != null)
        {
            for (Map.Entry<String, String> entry : attributeMap.entrySet())
            {
                message.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        message.setMessageStatusCallback(new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                Logger.t(TAG).d("发送约主播吃饭信息成功");
            }

            @Override
            public void onError(int code, String error)
            {
                Logger.t(TAG).d("发送约主播吃饭信息失败=" + code + ">>>" + error);
            }

            @Override
            public void onProgress(int progress, String status)
            {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    //第三方支付
    private class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<LiveShowBootyCallAct> mActRef;

        private PayFinish(LiveShowBootyCallAct mAct)
        {
            this.mActRef = new WeakReference<LiveShowBootyCallAct>(mAct);
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
            final LiveShowBootyCallAct cAct = mActRef.get();
            if (cAct != null)
            {
                Logger.t(TAG).d("pay finished");
                ToastUtils.showShort( "邀请发送成功,等待接受");
                cAct.sendC2CMessage(Constants.AVIMCMD_Booty_Call, "");
                reLoadWebView();
                PayHelper.clearPopupWindows();
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final LiveShowBootyCallAct cAct = mActRef.get();
            if (cAct != null)
            {
                Logger.t(TAG).d("pay failed");
            }
        }
    }

    //余额支付
    private static class PendingPayFinish implements IHsPayPendingListener
    {
        private final WeakReference<LiveShowBootyCallAct> mActRef;

        private PendingPayFinish(LiveShowBootyCallAct mAct)
        {
            this.mActRef = new WeakReference<LiveShowBootyCallAct>(mAct);
        }

        @Override
        public void payPending(String passWord, GridPasswordView gridPasswordView)
        {
            LiveShowBootyCallAct cAct = mActRef.get();
            if (cAct != null)
            {
                Logger.t(TAG).d("余额支付触发 moneyStr>>>>" + cAct.moneyStr);
                cAct.mPresenter.sendReceive(cAct.hostUId, "0", cAct.moneyStr, passWord, cAct.date);
            }
        }
    }
}
