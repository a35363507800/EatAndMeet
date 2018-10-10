package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;

import butterknife.BindView;
import butterknife.ButterKnife;



public class HtmlPureShowAct extends BaseActivity
{
    private final String TAG = HtmlPureShowAct.class.getCanonicalName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.webView_html_container)
    ProgressBridgeWebView bWebView;
    @BindView(R.id.all_root_view)
    LinearLayout allRootView;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_html_pure_show);
        ButterKnife.bind(this);
        afterView();
    }

    void afterView()
    {
        mContext = this;

        topBar.setTitle(getIntent().getBundleExtra("pageInfo").getString("title"));
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mContext.finish();
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
        initWebView(getIntent().getBundleExtra("pageInfo").getString("pageUrl"));
    }
    private void initWebView(String pageUrl)
    {
        if (TextUtils.isEmpty(pageUrl)||pageUrl=="null")
            pageUrl="https://www.baidu.com";
        bWebView.getSettings().setLoadWithOverviewMode(true);
        bWebView.getSettings().setUseWideViewPort(true);
        bWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        bWebView.loadUrl(pageUrl);
    }
}
