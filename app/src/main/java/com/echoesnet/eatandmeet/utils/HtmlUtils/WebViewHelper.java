package com.echoesnet.eatandmeet.utils.HtmlUtils;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/8/28 15:24
 * @description
 */

public class WebViewHelper
{
    private final static String TAG=WebViewHelper.class.getSimpleName();
    private int layerType = View.LAYER_TYPE_HARDWARE;
    private BridgeWebView webView;

    /**
     * WebView 默认硬解
     * 递归查找ViewPager 设置 WebView 软解
     * @param mWebView
     * @param activity
     */
    public WebViewHelper(BridgeWebView mWebView, Activity activity) {
        this(mWebView);
        ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content);
        if(foundViewPager(viewGroup)){
            layerType = View.LAYER_TYPE_SOFTWARE;
        }
    }

    private boolean foundViewPager(ViewGroup viewGroup) {
        boolean result = false;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            Object o = viewGroup.getChildAt(i);
            if(o instanceof ViewPager){
                result = true;
                break;
            }else if (o instanceof ViewGroup){
                result = foundViewPager((ViewGroup)o);
                if(result)
                    break;
            }
        }
        return result;
    }


    /**
     * WebView 默认硬解
     * 手动设置 WebView 软解
     * @param mWebView
     * @param type
     */
    public WebViewHelper(BridgeWebView mWebView, int type) {
        this(mWebView);
        this.layerType = type;
    }

    /**
     * WebView 默认硬解
     * @param mWebView
     */
    public WebViewHelper(BridgeWebView mWebView) {
        this.webView = mWebView;
    }

    public WebViewHelper getProgressWebViewInstance()
    {
        if (webView!=null)
        {
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.setLayerType(layerType, null);
        }
        return this;
    }


    /**
     * 默认注册 shareDataBetweenJavaAndJs
     *
     * @param jsRunBridgeHandler
     * @param keys
     */
    public WebViewHelper regJsBridge(final JSRunBridgeHandler jsRunBridgeHandler, String ...keys){
        //默认注册 shareDataBetweenJavaAndJs
        webView.registerHandler(JsBridgeKey.shareDataBetweenJavaAndJs, new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d(
                        String.format("jsBridgeKey: %s |JStoJava: %s", JsBridgeKey.shareDataBetweenJavaAndJs, data)
                ); //从js获得数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(null); //传给js的数据
                function.onCallBack(new Gson().toJson(reqParamMap));
                //Logger.t(TAG).d("javaTojs"+new Gson().toJson(reqParamMap));
                jsRunBridgeHandler.jsRunning(JsBridgeKey.shareDataBetweenJavaAndJs,data,function);
            }
        });

        for (final String jsBridgeKey : keys) {
            webView.registerHandler(jsBridgeKey, new BridgeHandler()
            {
                @Override
                public void handler(String data, CallBackFunction function)
                {
                    Logger.t(TAG).d(
                            String.format("jsBridgeKey: %s |JStoJava: %s", jsBridgeKey, data)
                    ); //从js获得数据
                    jsRunBridgeHandler.jsRunning(jsBridgeKey,data,function);
                }
            });
        }
        return this;
    }

    public WebViewHelper loadCacheOrUrl(String url){
        webView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(url));
        return this;
    }

}
