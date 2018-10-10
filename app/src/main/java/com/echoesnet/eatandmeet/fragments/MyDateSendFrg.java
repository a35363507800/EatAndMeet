package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CopyOrderMealAct;
import com.echoesnet.eatandmeet.activities.MyDateHousekeepAct;
import com.echoesnet.eatandmeet.activities.QueryScheduleAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by an on 2016/11/22 0022.
 */
public class MyDateSendFrg extends BaseFragment
{
    private final String TAG = MyDateSendFrg.class.getSimpleName();
    @BindView(R.id.web_ranking)
    ProgressBridgeWebView mWebView;
    Unbinder unbinder;

    private Activity mActivity;
    private Dialog pDialog;
    public static final int INTENT_RELOAD=10086;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_date, container, false);
        unbinder = ButterKnife.bind(this, view);
        afterView();
        return view;
    }

    private void afterView()
    {
        mActivity = getActivity();
        pDialog = DialogUtil.getCommonDialog(getActivity(), "正在加载...");
        pDialog.setCancelable(false);
        Logger.t(TAG).d("MyDateSendFrg");

       // String url="http://192.168.10.249:8080/invite-send-list.html";
        if (Build.VERSION.SDK_INT >= 20)
        {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(NetInterfaceConstant.invite_send_list));


        //必须注册
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mActivity);
                function.onCallBack(new Gson().toJson(reqParamMap));
            }
        });

        //点击进入发出邀请的详情
        mWebView.registerHandler("anchorReceive", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("data>>>>" + data);
                //从js获得数据
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String streamId = jsonObject.getString("streamId");
                    String luId = jsonObject.getString("luId");
                    String roomId = jsonObject.getString("roomId");
                    Logger.t(TAG).d("JStoJava: streamId--> " + streamId);

                       Intent intent = new Intent(mActivity,QueryScheduleAct.class);
                    intent.putExtra("luid", luId);
                    intent.putExtra("streamID", streamId);




//                    Intent intent = new Intent(mActivity,LiveShowBootyCallAct.class);
//                    intent.putExtra("streamId", streamId);
//                    intent.putExtra("hostUId", luId);
//                    intent.putExtra("roomId", roomId);
//                    intent.putExtra("currentPage", 0);
//                    intent.putExtra("openFrom", "sendDateList");
                     startActivity(intent);


                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
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
                    intent.putExtra("openFrom", "dateEnter");
                    SharePreUtils.setOrderType(mActivity, "1");
                    EamApplication.getInstance().dateStreamId = streamId;
                    mActivity.startActivityForResult(intent,INTENT_RELOAD);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        mWebView.registerHandler("goShopping", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("data>>>>" + data);
                //从js获得数据

                Intent intent =new Intent(mActivity, MyDateHousekeepAct.class);
                startActivity(intent);
            }
        });

        //弹窗
        mWebView.registerHandler("showDialog", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("开始订餐--> " + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray btnArray = jsonObject.getJSONArray("button");
                    CustomAlertDialog dialog = new CustomAlertDialog(mActivity)
                            .builder()
                            .setTitle(jsonObject.getString("title"))
                            .setMsg(jsonObject.getString("message"));
                    if (btnArray.length() == 1)
                    {
                        final JSONObject btn1 = btnArray.getJSONObject(0);
                        final String btnName = btn1.getString("name");
                        final String btnCallFunction = btn1.getString("action");
                        dialog.setPositiveButton(btnName, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Logger.t(TAG).d("调用的js方法为》" + btnName);
                                mWebView.callHandler(btnCallFunction, "", new CallBackFunction()
                                {
                                    @Override
                                    public void onCallBack(String data)
                                    {
                                        Logger.t(TAG).d("JS " + btnCallFunction + "方法返回结果》" + data);
                                    }
                                });
                            }
                        }).show();
                    }
                    else if (btnArray.length() == 2)
                    {
                        final JSONObject btn1 = btnArray.getJSONObject(0);
                        final String btnName = btn1.getString("name");
                        final String btnCallFunction = btn1.getString("action");
                        final JSONObject btn2 = btnArray.getJSONObject(1);
                        final String btnName2 = btn2.getString("name");
                        final String btnCallFunction2 = btn2.getString("action");
                        dialog.setPositiveButton(btnName, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Logger.t(TAG).d("调用的js方法为》" + btnName);
                                mWebView.callHandler(btnCallFunction, "", new CallBackFunction()
                                {
                                    @Override
                                    public void onCallBack(String data)
                                    {
                                        Logger.t(TAG).d("JS " + btnCallFunction + "方法返回结果》" + data);
                                    }
                                });
                            }
                        }).setNegativeButton(btnName2, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Logger.t(TAG).d("调用的js方法为》" + btnName);
                                mWebView.callHandler(btnCallFunction2, "", new CallBackFunction()
                                {
                                    @Override
                                    public void onCallBack(String data)
                                    {
                                        Logger.t(TAG).d("JS " + btnCallFunction2 + "方法返回结果》" + data);
                                    }
                                });
                            }
                        }).show();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
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
}
