package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.TopPersonAct;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.LAYER_TYPE_HARDWARE;

/**
 * Created by lc on 2017/7/21 14.
 */

public class GoodSaleHostFrg extends BaseFragment
{
    private final String TAG = GoodSaleHostFrg.class.getSimpleName();
    @BindView(R.id.web_date_wish_list)
    ProgressBridgeWebView mWebView;
    Unbinder unbinder;

    private Activity mActivity;
    private final int START_FOR_MY_USERINFO = 101;
    public static final int RESULT_FROM_USERINFO = 20;
    private Dialog pDialog;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_date_wish, container, false);
        unbinder = ButterKnife.bind(this, view);
        afterView();
        return view;
    }

    private void afterView()
    {
        mActivity = getActivity();
        pDialog = DialogUtil.getCommonDialog(getActivity(), "正在加载...");
        pDialog.setCancelable(false);
        Logger.t(TAG).d("GoodSaleHostFrg");
        Paint paint = new Paint();
        mWebView.setLayerType(LAYER_TYPE_HARDWARE, paint);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(NetInterfaceConstant.data_good_sale));
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

        mWebView.registerHandler("openUserInfo", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("JStoJava" + data);//从js获得数据
                //传给js的数据
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    uid = jsonObject.getString("uId");
                    Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                    intent.putExtra("toUId", uid);
                    intent.putExtra("open-from", "GoodSaleHostFrg");
                    getActivity().startActivityForResult(intent, START_FOR_MY_USERINFO);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        mWebView.registerHandler("focusUser", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("data>>" + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    if (getActivity() instanceof TopPersonAct)
                    {
                        TopPersonAct topPersonAct = (TopPersonAct) getActivity();
                        topPersonAct.reloadWebView(jsonObject.getString("uId"));
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case START_FOR_MY_USERINFO:
                if (resultCode == RESULT_FROM_USERINFO)
                {
                    boolean isfocus = data.getBooleanExtra("isFocus", false);
                    if (isfocus)
                    {
                        reLoadWebView(uid);
                        Logger.t(TAG).d("GoodSaleHostFrg执行H5方法");
                    }

//                    boolean isBlack = data.getBooleanExtra("isBlack",false);
//                    if (isBlack)
//                    {
//                        blockUser();
//                        Logger.t(TAG).d("LocalTyrantFrg执行H5方法blockUser()");
//                    }
                }
                break;

        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void reLoadWebView(String uid)
    {
        //传给js的数据
        Map<String, String> reqParamMap = new HashMap<String, String>();
        reqParamMap.put("uid", uid);
        reqParamMap.put("action", "reload");
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

    private void blockUser()
    {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("uId", uid);
        mWebView.callHandler("blockUser", new Gson().toJson(stringMap), new CallBackFunction()
        {
            @Override
            public void onCallBack(String data)
            {
                Logger.t(TAG).d("blockUser" + data.toString());
            }
        });
    }


}
