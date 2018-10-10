package com.echoesnet.eatandmeet.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.presenters.ImpIPromotionActionView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IPromotionActionView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.adapters.OrderRechargeAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.joanzapata.iconify.IconDrawable;
import com.mob.MobSDK;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qzone.QQClientNotExistException;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class PromotionActionAct extends BaseActivity implements IPromotionActionView, PlatformActionListener
{
    private final String TAG = PromotionActionAct.class.getCanonicalName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.webView_promotion)
    ProgressBridgeWebView bWebView;
    @BindView(R.id.all_root_view)
    AutoLinearLayout allRootView;

    private Activity mContext;
    private Double payAmount, getAmount;
    private FPromotionBean fPromotionBean;
    private String shareParamJsonStr, title;
    //private Dialog pDialog;
    private ImpIPromotionActionView promotionActionView;
    private SharePopWindow sharePopWindow;
    private ShareToFaceBean shareBean;
    private TextView titleTv;
    private String toRelationType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //状态栏黑字体
        CommonUtils.setStatusBarDarkMode(this, false);
        setContentView(R.layout.act_promotion_action);
        ButterKnife.bind(this);
        afterView();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (("1").equals(fPromotionBean.getType()))
        {
            rechargeAct(bWebView, fPromotionBean.getWebUrl());
        }
    }

    @Override
    protected void onDestroy()
    {
        if (bWebView != null)
            bWebView.loadUrl("about:blank");
        super.onDestroy();
        if (sharePopWindow != null && sharePopWindow.isShowing())
        {
            sharePopWindow.dismiss();
            sharePopWindow = null;
        }
    }

    private void afterView()
    {
        mContext = this;
        fPromotionBean = (FPromotionBean) getIntent().getSerializableExtra("fpBean");
        titleTv = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                mContext.finish();
            }

            @Override
            public void right2Click(View view)
            {
                initPopWindow();
                if (sharePopWindow != null)
                    sharePopWindow.showPopupWindow(allRootView, null);
            }
        });
        topBarSwitch.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.C0321)));
        titleTv.setTextColor(ContextCompat.getColor(mContext, R.color.C0324));
        titleTv.setText(fPromotionBean.getActName());
        List<Map<String, TextView>> navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});
        TextView right2 = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        TextView leftTv = navBtns.get(0).get(TopBarSwitch.NAV_BTN_ICON);
        right2.setTextColor(ContextCompat.getColor(mContext, R.color.C0324));
        leftTv.setTextColor(ContextCompat.getColor(mContext, R.color.C0324));
        right2.setText("分享");
        right2.setTextSize(16);

        promotionActionView = new ImpIPromotionActionView(mContext, this);
        MobSDK.init(this, EamConstant.SHARESDK_APPKEY, EamConstant.SHARESDK_APPSECRET);
        initWebView();
    }

    private void initWebView()
    {
        //js可以不设置handlerName就可以发送信息到java
        //bWebView.setDefaultHandler(new DefaultHandler());
        bWebView.getSettings().setLoadWithOverviewMode(true);
        bWebView.getSettings().setUseWideViewPort(true);
        bWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        bWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        Logger.t(TAG).d(fPromotionBean.toString());
        //数据交互处理器
        bWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                shareParamJsonStr = data;
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    title = jsonObject.getString("appTitle");
                    if (titleTv != null && !TextUtils.isEmpty(title))
                        titleTv.setText(title);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mContext);
                reqParamMap.put("activityId", fPromotionBean.getActivityId());
                reqParamMap.put("isInRoom", CommonUtils.isInLiveRoom ? CommonUtils.isInLiveRoom : !CommonUtils.isAppOnForeground(mContext));
                function.onCallBack(new Gson().toJson(reqParamMap));
                Logger.t(TAG).d("javaTojs" + new Gson().toJson(reqParamMap));
            }
        });
        //百度统计，由于百度统计webview和我们的冲突
        bWebView.registerHandler("BaiduAnalyze", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                try
                {
                    Logger.t(TAG).d(data);
                    JSONObject jsonObject = new JSONObject(data);
//                    StatService.onEvent(mContext, jsonObject.getString("eventId"),
//                            getString(R.string.baidu_other), Integer.parseInt(jsonObject.getString("aacNum")));
                    function.onCallBack("百度统计调用成功");

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        //弹窗
        bWebView.registerHandler("AlertView", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    new CustomAlertDialog(mContext)
                            .builder()
                            .setTitle(jsonObject.getString("title"))
                            .setMsg(jsonObject.getString("message"))
                            .setPositiveButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                }
                            }).show();

                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });
        //消息提醒
        bWebView.registerHandler("MsgReminds", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                mContext.startActivity(new Intent(mContext, MMyInfoOrderRemindAct.class));
            }
        });
        //分享处理器
        bWebView.registerHandler("Share", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                shareParamJsonStr = data;
                initPopWindow();
                if (sharePopWindow != null)
                    sharePopWindow.showPopupWindow(allRootView, null);
            }
        });
        //跳转到发动态的页面
        bWebView.registerHandler("partakeMidAutumn", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                if (!CommonUtils.isInLiveRoom)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        PromotionActionActPermissionsDispatcher.onCameraAudioPermGrantedWithPermissionCheck((PromotionActionAct) mContext, data);
                    }
                    else
                    {
                        onCameraAudioPermGranted(data);
                    }
                }
                else
                {
                    ToastUtils.showShort("请退出当前直播间");
                }


            }
        });

        //跳转到直播列表的页面
        bWebView.registerHandler("JumpToLive", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("JumpToLive" + data);
                if (!CommonUtils.isInLiveRoom)
                {
                    finish();
                    Intent intent = new Intent(mContext, HomeAct.class);
                    intent.putExtra("showPage", 2);
                    mContext.startActivity(intent);
                }
                else
                {
                    ToastUtils.showShort("请退出当前直播间");
                }


            }
        });
        //打开 toast
        bWebView.registerHandler("openToast", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String content = jsonObject.getString("content");
                    if (!TextUtils.isEmpty(content))
                        ToastUtils.showShort(content);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        shareToWechatOrFriends();
        nationalDayCard();
        goToTrendsList();
        switch (fPromotionBean.getType())
        {
            case "1": //充值,写到onstart里面了
                //rechargeAct(bWebView,fPromotionBean.getWebUrl());
                break;
            case "2"://分享,现在所有的活动都是2，以后可能不同的活动不同的类型
                bWebView.loadUrl(fPromotionBean.getWebUrl());
                break;
            case "3"://餐厅列表
                Logger.t(TAG).d("查看餐厅详情");
                checkResDetail(bWebView, fPromotionBean.getWebUrl());
                break;
            default:
                bWebView.loadUrl(fPromotionBean.getWebUrl());
                break;
        }
        bWebView.setProgressListener(new ProgressBridgeWebView.LoadingProgressListener()
        {
            @Override
            public void onProgressChanged(int progress)
            {

            }
        });

    }

    @NeedsPermission({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermGranted(String data)
    {
        Logger.t(TAG).d("允许获取权限");
        if (CommonUtils.cameraIsCanUse())
        {
            try
            {
                JSONObject jsonObject = new JSONObject(data);
                Intent intent = new Intent(mContext, TrendsPublishAct.class);
                intent.putExtra("type", "share");
                intent.putExtra("topic", jsonObject.getString("topic"));
                startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_PUBLISH);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            ToastUtils.showShort("请释放相机资源");
        }
    }

    @OnPermissionDenied({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }


    @OnNeverAskAgain({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        ToastUtils.showLong(getString(R.string.per_camera_never_ask));
    }

    @OnShowRationale({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mContext)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("需要使用您的相机和录音权限才能完成此功能！")
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
    protected void onResume()
    {
        super.onResume();
        if (!CommonUtils.isInLiveRoom)
            stopOrPlaySong(true);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stopOrPlaySong(false);
    }

    private void goToTrendsList()
    {
        //跳转到动态列表页面
        bWebView.registerHandler("watchVideo", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                if (!CommonUtils.isInLiveRoom)
                {
                    Intent intent = new Intent(mContext, HomeAct.class);
                    intent.putExtra("showPage", 0);
                    intent.putExtra("showFindPage", 1);
                    mContext.startActivity(intent);
                }
                else
                {
                    ToastUtils.showShort("请退出当前直播间");
                }

            }
        });
    }

    /**
     * 国庆送卡片活动
     */
    private void nationalDayCard()
    {
        //送卡给Ta
        bWebView.registerHandler("giveCard", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
/*                {
                    "id": "1",
                        "name": "测试卡",
                        "icon": "http://huisheng.ufile.ucloud.cn/1505811004456694968.png"
                }*/
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    initPopWindow();
                    toRelationType = "giveCard";
                    if (sharePopWindow != null)
                    {
                        sharePopWindow.getShareInfo().setActivityId(jsonObject.getString("id"));
                        sharePopWindow.getShareInfo().setSendType("giveCard");
                        sharePopWindow.getShareInfo().setMessageDes(String.format("[链接]【%s】送你一张%s!", title, jsonObject.getString("name")));
                        sharePopWindow.getShareInfo().setShareContent(String.format("送你一张%s，助你赢取100元现金大奖", jsonObject.getString("name")));
                        sharePopWindow.setShowIndex(new int[]{1, 2, 3, 4, 5, 6});
                        sharePopWindow.showPopupWindow(allRootView, null);
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        //向Ta索要
        bWebView.registerHandler("askCard", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
/*                {
                    "id": "1",
                        "name": "测试卡",
                        "icon": "http://huisheng.ufile.ucloud.cn/1505811004456694968.png"
                }*/
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    initPopWindow();
                    toRelationType = "askCard";
                    if (sharePopWindow != null)
                    {
                        sharePopWindow.getShareInfo().setActivityId(jsonObject.getString("id"));
                        sharePopWindow.getShareInfo().setSendType("askCard");
                        sharePopWindow.getShareInfo().setMessageDes(String.format("[链接]【%s】求送一张%s!", title, jsonObject.getString("name")));
                        sharePopWindow.getShareInfo().setShareContent(String.format("求送一张%s，助我赢取100元现金大奖", jsonObject.getString("name")));
                        sharePopWindow.setShowIndex(new int[]{1, 2, 3, 4, 5, 6});
                        sharePopWindow.showPopupWindow(allRootView, null);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        bWebView.registerHandler("goBalance", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Intent intent = new Intent(mContext, MyInfoAccountAct2.class);
                startActivity(intent);
            }
        });
    }


    /**
     * 充值
     *
     * @param webView
     * @param webUrl
     */
    private void rechargeAct(BridgeWebView webView, String webUrl)
    {
        //注册一个处理函数，这个函数js可以调用
        webView.registerHandler("Recharge", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("handler = Recharge, data from web = " + data);
                function.onCallBack("Recharge方法执行成功");
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    payAmount = Double.parseDouble(jsonObject.getString("money"));
                    getAmount = Double.parseDouble(jsonObject.getString("present"));
                    rechargeWayPopupWindow payWindow = new rechargeWayPopupWindow(mContext, String.valueOf(payAmount), String.valueOf(getAmount));
                    payWindow.showPopupWindow(allRootView);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });
        webView.loadUrl(webUrl);
    }


    /**
     * 分享到朋友圈或者微信
     */
    private void shareToWechatOrFriends()
    {
        //注册一个处理函数，这个函数js可以调用
        bWebView.registerHandler("toShare", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("toShare>>>>" + data);
                initShareBean(new PlatformActionListener()
                {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
                    {
                        ToastUtils.showShort("分享成功");
                        function.onCallBack("");
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable)
                    {
                        ToastUtils.showShort("分享失败");
                    }

                    @Override
                    public void onCancel(Platform platform, int i)
                    {
                        ToastUtils.showShort("分享取消");
                    }
                });

                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    if ("0".equals(jsonObject.getString("type")))
                        share2WeChat();
                    else
                        share2Friends();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initShareBean(PlatformActionListener platformActionListener)
    {
        Logger.t(TAG).d("shareParamJsonStr:" + shareParamJsonStr);
        if (TextUtils.isEmpty(shareParamJsonStr))
            return;
        try
        {
            final JSONObject shareBody = new JSONObject(shareParamJsonStr);
            shareBean = new ShareToFaceBean();
            shareBean.setShareType(Platform.SHARE_WEBPAGE);
            shareBean.setShareTitle(shareBody.getString("title"));
            shareBean.setShareTitleUrl(shareBody.getString("link"));
            shareBean.setShareSite("看脸吃饭");
            shareBean.setSendType("");
            shareBean.setShareSiteUrl(shareBody.getString("link"));
            shareBean.setShareWeChatMomentsTitle(shareBody.getString("title"));
            shareBean.setShareUrl(shareBody.getString("link"));
            shareBean.setShareImgUrl(shareBody.getString("imgUrl"));
            shareBean.setShareAppImageUrl(!TextUtils.isEmpty(shareBody.getString("imgUrl")) ? shareBody.getString("imgUrl") : NetHelper.LIVE_SHARE_PIC);
            shareBean.setShareContent(shareBody.getString("desc"));
            shareBean.setMessageDes(String.format("[链接]【%s】", shareBody.getString("appTitle")));
            shareBean.setOpenSouse("activity");
            shareBean.setActivityId(fPromotionBean.getActivityId());
            shareBean.setShareListener(platformActionListener);
            shareBean.setShareSinaContent(shareBody.getString("desc") + shareBody.getString("link"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 查看餐厅详情
     *
     * @param webView
     * @param webUrl
     */
    private void checkResDetail(BridgeWebView webView, String webUrl)
    {
        webView.registerHandler("CheckResDetail", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("handler = CheckResDetail, data from web = " + data);
                function.onCallBack("CheckResDetail方法执行成功");
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    //{"rId":"1201030004","rName":"大渔","lat":"39.120961","lng":"117.221839","lessPrice":"200"}
                    // 跳转到餐厅详情
                    SharePreUtils.setToOrderMeal(mContext, "noDate");
                    Intent intent = new Intent(mContext, DOrderMealDetailAct.class);
                    intent.putExtra("restId", jsonObject.getString("rId"));
                    intent.putExtra("source", "promotion");
                    EamApplication.getInstance().lessPrice = jsonObject.getString("lessPrice");
                    startActivity(intent);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        webView.loadUrl(webUrl);
    }

    private void stopOrPlaySong(boolean isPlay)
    {
        if (isPlay)
            bWebView.callHandler("playSong", "", null);
        else
            bWebView.callHandler("offSong", "", null);
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
    }

    @Override
    public void setModifyBalanceCallback(String response)
    {
        Logger.t(TAG).d("测试(充值)修改余额状态成功--> " + response);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtils.showShort( "分享成功");
            }
        });
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable)
    {
        if (throwable instanceof QQClientNotExistException)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ToastUtils.showLong("请安装QQ客户端");
                }
            });
        }
        else
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ToastUtils.showShort( "分享失败");
                }
            });
        }

    }

    @Override
    public void onCancel(Platform platform, int i)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtils.showShort( "分享取消");
            }
        });
    }

    private class rechargeWayPopupWindow extends PopupWindow
    {
        private Activity mContext;
        private List<Map<String, Object>> payWays;
        private int selectedItem = 0;
        private String payType;

        public rechargeWayPopupWindow(Activity context, final String payAmount, final String getAmount)
        {
            this.mContext = context;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View mMenuView = inflater.inflate(R.layout.popup_recharge_mode, null);
            final ListView payLst = (ListView) mMenuView.findViewById(R.id.lv_pay);
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
            payWays.add(map1);
            payWays.add(map2);

            final OrderRechargeAdapter adapter = new OrderRechargeAdapter(mContext, payWays);
            payLst.setAdapter(adapter);
            adapter.setOnItemClickListener(new OrderRechargeAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(View view, int position)
                {
                    Logger.t(TAG).d("点击事件  " + position);
                    selectedItem = position;
                    adapter.setSelection(position);
                    adapter.notifyDataSetChanged();
                }
            });
            Button btnPayOk = (Button) mMenuView.findViewById(R.id.btn_pay_ok);
            btnPayOk.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (selectedItem == 0)
                    {
                        payType = "alipay";
                        Logger.t(TAG).d("选择支付方式--> " + payWays.get(selectedItem).get("payWay") + " , 充值金额--> " + payAmount + " , 支付方式--> " + payType);

                    }
                    else if (selectedItem == 1)
                    {
                        payType = "wx";
                        Logger.t(TAG).d("选择支付方式--> " + payWays.get(selectedItem).get("payWay") + " , 充值金额--> " + payAmount + " , 支付方式--> " + payType);
                    }
                    dismiss();

                    PayBean payBean = new PayBean();
                    payBean.setOrderId("");                 // 商户订单号
                    payBean.setAmount(payAmount);            // 订单总金额, 单位为元
                    payBean.setChannel(payType);            // 支付使用的第三方支付渠道 (alipay:支付宝手机支付、alipay_wap:支付宝手机网页支付; upacp:银联全渠道支付、wx:微信支付)
                    payBean.setSubject("充值");             // 商品的标题
                    payBean.setBody("活动充值");            // 商品的描述信息
                    PayHelper.payByThirdParty(mContext, payBean, new PayMetadataBean(getAmount, "", fPromotionBean.getActivityId(), "1"));
                }
            });
            Button btnPayCancel = (Button) mMenuView.findViewById(R.id.btn_pay_cancel);
            btnPayCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                }
            });
            //设置SelectPicPopupWindow的View
            this.setContentView(mMenuView);
            //设置SelectPicPopupWindow弹出窗体的宽
            this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
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

        /**
         * 显示popupWindow
         *
         * @param parent
         */
        public void showPopupWindow(View parent)
        {
            if (!this.isShowing())
            {
                this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            }
        }

        /**
         * 设置添加屏幕的背景透明度
         *
         * @param bgAlpha
         */
        public void backgroundAlpha(float bgAlpha)
        {
            WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
            lp.alpha = bgAlpha; // 0.0-1.0
            mContext.getWindow().setAttributes(lp);
        }
    }

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
                    PayHelper.clearPayHelperListeners();
                    PayHelper.setIPayFinishedListener(new IPayFinishedListener()
                    {
                        @Override
                        public void PayFinished(String orderId, String streamId, String payType)
                        {
                            if (promotionActionView != null)
                                promotionActionView.setModifyBalance(mContext, streamId, payType, String.valueOf(payAmount), String.valueOf(getAmount), fPromotionBean.getActivityId());
                            Logger.t(TAG).d("====streamId====> " + streamId);
                            Intent intent = new Intent(mContext, MyRechargeResultAct.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra(EamConstant.EAM_RECHARGE_RESULT_OPEN_SOURCE, EamConstant.EAM_RECHARGE_RESULT_OPEN_SOURCE);
                            startActivity(intent);
                            //finish();
                        }

                        @Override
                        public void PayFailed(String orderId, String streamId, String payType)
                        {

                        }
                    });
                    //会触发支付完成接口
                    PayHelper.thirdPartyPayStateCheck(mContext, new PayMetadataBean("", "", "", "1"));
                }
                else if (result.equals("cancel"))
                {
                    ToastUtils.showShort( "支付取消");
                }
                else
                {
                    ToastUtils.showShort( "支付失败, 请重试");
                }
            }
        }
        else if (requestCode == EamConstant.EAM_OPEN_TRENDS_PUBLISH && resultCode == RESULT_OK)
        {
            bWebView.reload();
        }
        else if (requestCode == EamConstant.EAM_OPEN_SHARE_COLUMN)
        {
            if (resultCode != RESULT_OK)
                ToastUtils.showShort("分享取消");
        }
        else if (requestCode == EamConstant.EAM_OPEN_RELATION)
        {
            if (resultCode == RESULT_OK)
            {
                Map<String, String> param = new HashMap<>();
                param.put("luid", data.getStringExtra("uid"));
                param.put("id", data.getStringExtra("id"));
                Logger.t(TAG).d("通讯录返回>>" + param.toString());
                if ("giveCard".equals(toRelationType))
                {
                    bWebView.callHandler("giveCardToUser", new Gson().toJson(param), new CallBackFunction()
                    {
                        @Override
                        public void onCallBack(String data)
                        {

                        }
                    });
                }
                else if ("askCard".equals(toRelationType))
                {
                    bWebView.callHandler("askCardToUser", new Gson().toJson(param), new CallBackFunction()
                    {
                        @Override
                        public void onCallBack(String data)
                        {

                        }
                    });
                }
                if (bWebView != null && !TextUtils.isEmpty(toRelationType))
                {
                    Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mContext);
                    reqParamMap.put("activityId", fPromotionBean.getActivityId());
                    reqParamMap.put("isInRoom", CommonUtils.isInLiveRoom ? CommonUtils.isInLiveRoom : !CommonUtils.isAppOnForeground(mContext));
                    bWebView.callHandler("ReceiveData", new Gson().toJson(reqParamMap), null);
                }
            }
            else
            {
                if (!("giveCard".equals(toRelationType) || "askCard".equals(toRelationType)))
                    ToastUtils.showShort("分享取消");
            }
        }
    }

    private void initPopWindow()
    {
        if (TextUtils.isEmpty(shareParamJsonStr))
            return;
        try
        {
            final JSONObject shareBody = new JSONObject(shareParamJsonStr);
            Logger.t(TAG).d("shareParamJsonStr:" + shareParamJsonStr);
            toRelationType = "";
            shareBean = new ShareToFaceBean();
            shareBean.setShareType(Platform.SHARE_WEBPAGE);
            shareBean.setShareTitle(shareBody.getString("title"));
            shareBean.setShareTitleUrl(shareBody.getString("link"));
            shareBean.setShareSite("看脸吃饭");
            shareBean.setSendType("");
            shareBean.setShareSiteUrl(shareBody.getString("link"));
            shareBean.setShareWeChatMomentsTitle(shareBody.getString("title"));
            shareBean.setShareUrl(shareBody.getString("link"));
            shareBean.setShareImgUrl(shareBody.getString("imgUrl"));
            shareBean.setShareAppImageUrl(!TextUtils.isEmpty(shareBody.getString("imgUrl")) ? shareBody.getString("imgUrl") : NetHelper.LIVE_SHARE_PIC);
            shareBean.setShareContent(shareBody.getString("desc"));
            shareBean.setMessageDes(String.format("[链接]【%s】", shareBody.getString("title")));
            shareBean.setOpenSouse("activity");
            shareBean.setActivityId(fPromotionBean.getActivityId());
            shareBean.setShareListener(this);
            shareBean.setShareSinaContent(shareBody.getString("desc") + shareBody.getString("link"));
            sharePopWindow = new SharePopWindow(mContext, new int[]{}, shareBean);
            sharePopWindow.setShareItemClickListener(new SharePopWindow.ShareItemClickListener()
            {
                @Override
                public void onItemCLick(int position, String shareKey)
                {
                    switch (shareKey)
                    {
                        case "看脸好友":
                            try
                            {
                                if (TextUtils.isEmpty(toRelationType) && sharePopWindow != null)
                                {
                                    sharePopWindow.getShareInfo().setShareContent(shareBody.getString("appShareDesc"));
                                }
                                sharePopWindow.getShareInfo().setShareTitle(shareBody.getString("appTitle"));
                                String activityId = shareBody.optString("activityId","");
                                if (!TextUtils.isEmpty(activityId))
                                sharePopWindow.getShareInfo().setActivityId(activityId);
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        case "我的动态":
                            Intent intent = new Intent(mContext, ShareColumnArticleAct.class);
                            try
                            {
                                intent.putExtra("content", shareBody.getString("appShareTitle"));
                                intent.putExtra("imgUrl", shareBody.getString("imgUrl"));
                                intent.putExtra("activityType", shareBody.optString("activityType", ""));
                                intent.putExtra("shareType", "activity");
                                String activityId = shareBody.optString("activityId","");
                                intent.putExtra("activityId", activityId);
                                if (!TextUtils.isEmpty(activityId))
                                    sharePopWindow.getShareInfo().setActivityId(activityId);
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            startActivityForResult(intent, EamConstant.EAM_OPEN_SHARE_COLUMN);
                            break;
                        default:
                            try
                            {
                                if (sharePopWindow != null)
                                {
                                    sharePopWindow.getShareInfo().setShareTitle(shareBody.getString("title"));
                                    sharePopWindow.getShareInfo().setShareContent(shareBody.getString("desc"));
                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void share2Friends()
    {
        if (shareBean == null)
            return;
//        MobSDK.init(mContext);
        Platform weChatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
        if (weChatMoments.isClientValid())
        {
            WechatMoments.ShareParams shareParams = new WechatMoments.ShareParams();
            shareParams.setShareType(shareBean.getShareType());
            shareParams.setTitle(shareBean.getShareWeChatMomentsTitle());
            shareParams.setUrl(shareBean.getShareUrl());
            shareParams.setText(shareBean.getShareContent());
            if (!TextUtils.isEmpty(shareBean.getShareImgUrl()))
                shareParams.setImageUrl(shareBean.getShareImgUrl());
            else
                shareParams.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
            weChatMoments.setPlatformActionListener(shareBean.getShareListener());
            weChatMoments.share(shareParams);
        }
        else
        {
            ToastUtils.showShort("请先安装微信客户端");
        }
    }

    private void share2WeChat()
    {
        if (shareBean == null)
            return;
//        MobSDK.init(mContext);
        Platform weChat = ShareSDK.getPlatform(Wechat.NAME);
        if (weChat.isClientValid())
        {
            Wechat.ShareParams sp = new Wechat.ShareParams();
            sp.setShareType(shareBean.getShareType());
            sp.setTitle(shareBean.getShareTitle());
            sp.setUrl(shareBean.getShareUrl());
            sp.setText(shareBean.getShareContent());
            if (!TextUtils.isEmpty(shareBean.getShareImgUrl()))
                sp.setImageUrl(shareBean.getShareImgUrl());
            else
                sp.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
            weChat.setPlatformActionListener(shareBean.getShareListener());
            weChat.share(sp);
        }
        else
        {
            ToastUtils.showShort("请先安装微信客户端");
        }
    }

}
