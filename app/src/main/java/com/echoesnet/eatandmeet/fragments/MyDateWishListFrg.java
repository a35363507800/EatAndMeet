package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.LiveShowBootyCallAct;
import com.echoesnet.eatandmeet.models.bean.DateWishH5Bean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.presenters.ImpIMyDateWishListFrgViewView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyDateWishListFrgView;
import com.echoesnet.eatandmeet.utils.Constants;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IHsPayPendingListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * Created by ben on 2017/2/18.
 */
public class MyDateWishListFrg extends BaseFragment implements IMyDateWishListFrgView
{
    private static final String TAG = MyDateWishListFrg.class.getSimpleName();

    @BindView(R.id.web_date_wish_list)
    ProgressBridgeWebView mWebView;
    Unbinder unbinder;

    private Activity mAct;
    private InterWithParentActListener mListener;
    private String moneyStr, date;
    private String hostUId;
    public ImpIMyDateWishListFrgViewView impIMyDateWishListFrgViewView;
    public List<DateWishH5Bean> wishH5BeanList = new ArrayList<>();

    public MyDateWishListFrg()
    {
    }

    public static MyDateWishListFrg newInstance()
    {
        return new MyDateWishListFrg();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_date_wish, container, false);
        unbinder = ButterKnife.bind(this, view);
        afterView();
        return view;
    }

    private void afterView()
    {
        mAct = getActivity();
        impIMyDateWishListFrgViewView = new ImpIMyDateWishListFrgViewView(mAct, this);


        //  String url="http://192.168.10.249:8080/shopping-carte.html";

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(NetInterfaceConstant.shopping_carte));
        //    mWebView.loadUrl(url);
        //必须注册
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("JStoJava" + data);
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mAct);
                function.onCallBack(new Gson().toJson(reqParamMap));
            }
        });
        mWebView.registerHandler("modifyActionButton", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava: 点击方法获得的参数》 " + data);
                try
                {
                    JSONObject result = new JSONObject(data);
                    if (mListener != null)
                        mListener.buttonClick(result.getString("btnId"), result.getString("btnText"), result.getString("btnAction"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });

        //跳转到我的约会
        mWebView.registerHandler("anchorReceive", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava: 点击方法获得的参数》 " + data);
                try
                {
                    JSONObject result = new JSONObject(data);
                    String roomId = result.getString("roomId");
                    String luId = result.getString("luId");
                    Intent intent = new Intent(mAct, LiveShowBootyCallAct.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//以singleTask形式启动
                    intent.putExtra("hostUId", luId);
                    intent.putExtra("roomId", roomId);
                    startActivity(intent);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });
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
                    wishH5BeanList = new Gson().fromJson(json.getString("liveData"), new TypeToken<List<DateWishH5Bean>>()
                    {
                    }.getType());
                    Logger.t(TAG).d("wishH5BeanList>>>>" + wishH5BeanList.toString());
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                payBoogtyCall();
            }
        });
    }

    public void deleteToH5()
    {
        mWebView.callHandler("blankAll", "", new CallBackFunction()
        {
            @Override
            public void onCallBack(String data)
            {
                Logger.t(TAG).d("JStoJava: 点击方法获得的参数》 " + data);
            }
        });
    }

    public void triggerToH5()
    {
        mWebView.callHandler("trigger", "", new CallBackFunction()
        {
            @Override
            public void onCallBack(String data)
            {
                Logger.t(TAG).d("JStoJava: 点击方法获得的参数》 " + data);
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
        PayHelper.setIHsPayPendingListener(new PendingPayFinish(this));
        PayBean payBean = new PayBean();
        payBean.setOrderId("");
        payBean.setMyPayType(EamConstant.EAM_PAY_PENDING);
        payBean.setAmount(moneyStr);
        payBean.setSubject("看脸吃饭");               // 商品的标题
        payBean.setBody("约直播吃饭");        // 商品的描述信息
        Logger.t(TAG).d("hostUId==" + hostUId);
        PayHelper.payOrder(mAct.getWindow().getDecorView(), payBean, mAct, new PayMetadataBean("", hostUId, "", "5", "", date));
    }

    public void operateEditStatus(final String jsFunction)
    {
        Logger.t(TAG).d("调用的js方法为》" + jsFunction);
        mWebView.callHandler(jsFunction, "", new CallBackFunction()
        {
            @Override
            public void onCallBack(String data)
            {
                Logger.t(TAG).d("JS " + jsFunction + "方法返回结果》" + data);
            }
        });
    }

    public void setInterWithParentActListener(InterWithParentActListener listener)
    {
        this.mListener = listener;
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.AppointmentC_payWish:

                if (ErrorCodeTable.PAYPWD_ERR.equals(code))
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(errBody);
                        String surplus = jsonObject.getString("surplus");
                        PayHelper.clearPayPassword(mAct);
                        ToastUtils.showShort("密码错误,剩余" + surplus + "次");
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                } else if (ErrorCodeTable.RECEIVE_DELING.equals(code))
                {
                    new CustomAlertDialog(mAct)
                            .builder()
                            .setTitle("温馨提示")
                            .setMsg("主播已有约，支付失败")
                            .setPositiveButton("确定", null)
                            .setCancelable(false)
                            .show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {

    }

    @Override
    public void sendReceiveCallback(String response)
    {
        String code = "";
        String surplus = "";


        ToastUtils.showShort("邀请发送成功,等待接受");
        if (wishH5BeanList != null)
            for (DateWishH5Bean wishH5Bean : wishH5BeanList)
            {
                sendC2CMessage(Constants.AVIMCMD_Booty_Call, "", wishH5Bean.getRoomId(),
                        wishH5Bean.getLiveSource(), wishH5Bean.getImuId());
            }
        PayHelper.clearPopupWindows();
        reLoadWebView();


    }

    /**
     * 发送C2C消息
     *
     * @param cmd
     * @param Param
     * @param roomId     房间id
     * @param liveSource 0 没开直播 1 腾讯直播 2 ucloud直播
     * @param hxId       邀请的主播环信id
     */
    public void sendC2CMessage(int cmd, String Param, String roomId, String liveSource, String hxId)
    {
        if ("1".equals(liveSource))
        {
            sendTxC2CMessage(cmd, Param, "u" + roomId);
        } else if ("2".equals(liveSource))
        {
            sendHxCmdC2CMsg(String.valueOf(cmd), hxId, null);
        }
    }

    private void sendTxC2CMessage(int cmd, String Param, String txId)
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
        TIMConversation mC2CConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, txId);
        Logger.t(TAG).d("send to>>>>" + txId);
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
        message.setChatType(EMMessage.ChatType.ChatRoom);
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


    /**
     * Ping++回调,onActivityResult()发生在onResume()之前
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("pay requestCode==" + requestCode + "pay resultCode==" + resultCode);
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String result = data.getExtras().getString("pay_result");
                Logger.t(TAG).d("pay result==" + result);
                if (result.equals("success"))
                {
                    PayHelper.setIPayFinishedListener(new PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck2(mAct, new PayMetadataBean("", hostUId, "", "8", "", date));
                } else if (result.equals("cancel"))
                {
                    ToastUtils.showShort( "支付取消");
                } else
                {
                    ToastUtils.showShort( "支付失败, 请重试");
                }
            }
        }
    }


    public void reLoadWebView()
    {
        //传给js的数据
        Map<String, String> reqParamMap = new HashMap<String, String>();
        reqParamMap.put("action", "reload");
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

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    public interface InterWithParentActListener
    {
        void buttonClick(String btnId, String content, String action);
    }


    //第三方支付
    private class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<MyDateWishListFrg> mFrgRef;

        private PayFinish(MyDateWishListFrg mFrg)
        {
            this.mFrgRef = new WeakReference<MyDateWishListFrg>(mFrg);
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
            final MyDateWishListFrg cAct = mFrgRef.get();
            if (cAct != null)
            {
                Logger.t(TAG).d("pay finished");
                ToastUtils.showShort( "邀请发送成功,等待接受");
                if (cAct.wishH5BeanList != null)
                    for (DateWishH5Bean wishH5Bean : cAct.wishH5BeanList)
                    {
                        cAct.sendC2CMessage(Constants.AVIMCMD_Booty_Call, "", wishH5Bean.getRoomId()
                                , wishH5Bean.getLiveSource(), wishH5Bean.getImuId());
                    }
                reLoadWebView();
                PayHelper.clearPopupWindows();
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final MyDateWishListFrg cFrg = mFrgRef.get();
            if (cFrg != null)
            {
                Logger.t(TAG).d("pay failed");
            }
        }
    }

    //余额支付
    private static class PendingPayFinish implements IHsPayPendingListener
    {
        private final WeakReference<MyDateWishListFrg> mActRef;

        private PendingPayFinish(MyDateWishListFrg mFrg)
        {
            this.mActRef = new WeakReference<MyDateWishListFrg>(mFrg);
        }

        @Override
        public void payPending(String passWord, GridPasswordView gridPasswordView)
        {
            MyDateWishListFrg cFrg = mActRef.get();
            if (cFrg != null)
            {
                Logger.t(TAG).d("余额支付触发 moneyStr>>>>" + cFrg.moneyStr);
                cFrg.impIMyDateWishListFrgViewView.sendReceive(cFrg.hostUId, "0", cFrg.moneyStr, passWord, cFrg.date);
            }
        }
    }

}
