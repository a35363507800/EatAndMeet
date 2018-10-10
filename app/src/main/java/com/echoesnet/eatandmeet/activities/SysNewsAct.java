package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ISysNewsView;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/12 16.
 * @description app 系统消息的页面
 */

public class SysNewsAct extends BaseActivity implements ISysNewsView
{
    private static final String TAG = SysNewsAct.class.getSimpleName();
    @BindView(R.id.tbs_top_bar)
    TopBarSwitch tbsTopBar;
    @BindView(R.id.pbw_webview)
    ProgressBridgeWebView pbwWebview;
    private Activity mAct;
    private Dialog pDialog;


    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_sys_news);
        ButterKnife.bind(this);
        mAct = this;


        tbsTopBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void rightClick(View view)
            {
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText("系统通知");

        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理");
        pDialog.setCancelable(false);

        pbwWebview.getSettings().setLoadWithOverviewMode(true);
        pbwWebview.getSettings().setUseWideViewPort(true);
        pbwWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

       // pbwWebview.loadUrl(EAMCheckCacheFiles.tryH5CacheFile("http://192.168.10.243:8080/h5/message-system-list.html"));
       pbwWebview.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(NetInterfaceConstant.data_sys_msg));
        //必须注册
        pbwWebview.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mAct);
                function.onCallBack(new Gson().toJson(reqParamMap));
            }
        });

        //打开订单详情页
        pbwWebview.registerHandler("openOrderDetail", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("openOrderDetailJStoJava" + data);
                //传给js的数据
                try
                {
                    JSONObject jobResult = new JSONObject(data);
                    final String orderId = jobResult.getString("orderId");
                    Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
                    reqParamMap.put(ConstCodeTable.orderId, orderId);
                    HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
                    {
                        @Override
                        public void onError(Throwable e)
                        {
                            super.onError(e);
                        }

                        @Override
                        public void onNext(String response)
                        {
                            super.onNext(response);
                            Intent intent = new Intent(mAct, DOrderRecordDetail.class);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                        }
                    }, NetInterfaceConstant.OrderC_orderDetail, reqParamMap);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        //打开轰趴订单详情页
        pbwWebview.registerHandler("openHpDetails", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("openHpDetailsJStoJava" + data);
                //传给js的数据
                try
                {
                    JSONObject jobResult = new JSONObject(data);
                    final String orderId = jobResult.getString("hpId");
                    Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
                    reqParamMap.put(ConstCodeTable.orderId, orderId);
                    HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
                    {
                        @Override
                        public void onError(Throwable e)
                        {
                            super.onError(e);
                        }

                        @Override
                        public void onNext(String response)
                        {
                            super.onNext(response);
                            Intent intent = new Intent(mAct, ClubOrderRecordDetailAct.class);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                        }
                    }, NetInterfaceConstant.HomepartyC_partyOrderDetails, reqParamMap);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });



        // 打开大V文章详情
        pbwWebview.registerHandler("openBigArticalDetail", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("openBigArticalDetailJStoJava" + data);
                //传给js的数据
                try
                {
                    JSONObject jobResult = new JSONObject(data);
                    Intent intent = new Intent(mAct, ColumnArticleDetailAct.class);
                    intent.putExtra("articleId", jobResult.getString("articleId"));
                    intent.putExtra("columnName", jobResult.getString("columnName"));
                    intent.putExtra("columnTitle", jobResult.getString("columnTitle"));
                    intent.putExtra("shareUrl", jobResult.getString("shareUrl"));
                    intent.putExtra("imgUrl", jobResult.getString("imgUrl"));
                    startActivity(intent);
                } catch (JSONException e)
                {
                    Logger.t(TAG).d("异常信息》》" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });


        pbwWebview.registerHandler("showDialog", new BridgeHandler()
        {
            @Override
            public void handler(String data, final CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                try
                {
                    JSONObject jobResult = new JSONObject(data);
                    String title = jobResult.getString("title");
                    String message = jobResult.getString("message");
                    JSONArray btnArray = jobResult.getJSONArray("button");
                    final JSONObject btn1 = btnArray.getJSONObject(0);
                    String btn1Name = btn1.getString("name");
                    final String btn1Action = btn1.getString("action");
                    JSONObject btn2 = btnArray.getJSONObject(1);
                    String btn2Name = btn2.getString("name");
                    final String btn2Action = btn2.getString("action");

                    new CustomAlertDialog(mAct).builder().setTitle(title).setMsg(message).setPositiveButton(btn1Name, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            pbwWebview.callHandler(btn1Action, "", new CallBackFunction()
                            {
                                @Override
                                public void onCallBack(String data)
                                {
                                    Logger.t(TAG).d("conform> " + data);
                                }
                            });

                        }
                    }).setNegativeButton(btn2Name, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            pbwWebview.callHandler(btn2Action, "", new CallBackFunction()
                            {
                                @Override
                                public void onCallBack(String data)
                                {
                                    Logger.t(TAG).d("cancel> " + data);
                                }
                            });
                        }
                    }).show();

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }



}
