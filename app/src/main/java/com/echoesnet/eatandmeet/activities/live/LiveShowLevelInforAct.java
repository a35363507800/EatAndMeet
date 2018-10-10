package com.echoesnet.eatandmeet.activities.live;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.BaseActivity;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by an on 2017/2/20 0020.
 */
public class LiveShowLevelInforAct extends BaseActivity{
    private static final String TAG = LiveShowBootyCallAct.class.getCanonicalName();
    @BindView(R.id.web_level)
    ProgressBridgeWebView mWebView;
    @BindView(R.id.top_bar)
    TopBar topBar;

    private String lUid;
    private Activity mActivity;
    private String levelUrl;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.act_show_level_infor);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews(){
        mActivity = this;
        topBar.setTitle("等级说明");
        lUid = getIntent().getStringExtra("luid");
        levelUrl = NetHelper.H5_ADDRESS +"/invited-to-eat/level-intro.html";
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
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
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setWebChromeClient(new WebChromeClient()
        {
            public void onProgressChanged(WebView view, int progress)
            {
            }
        });
        mWebView.loadUrl(levelUrl);
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mActivity);
                reqParamMap.put("luId", lUid);
                function.onCallBack(new Gson().toJson(reqParamMap));
            }
        });
    }
}
