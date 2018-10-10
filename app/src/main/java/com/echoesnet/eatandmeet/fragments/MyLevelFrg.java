package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MyInfoAccountAct2;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by an on 2016/11/22 0022.
 */
public class MyLevelFrg extends BaseFragment
{
    private final String TAG = MyLevelFrg.class.getCanonicalName();
    public static final int START_FOR_MY_ACCOUNT = 1001;
    @BindView(R.id.web_ranking)
    ProgressBridgeWebView mWebView;
    private Unbinder unbinder;

    private Activity mActivity;
    private Dialog pDialog;

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

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(NetInterfaceConstant.H5_MY_LEVEL));
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
                //Logger.t(TAG).d("javaTojs"+new Gson().toJson(reqParamMap));
            }
        });
        mWebView.registerHandler("toRecharge", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                Intent intent = new Intent(mActivity, MyInfoAccountAct2.class);
                getActivity().startActivityForResult(intent,START_FOR_MY_ACCOUNT);
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
