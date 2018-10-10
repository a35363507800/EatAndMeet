package com.echoesnet.eatandmeet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.TaskAct;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.HtmlUtils.JSRunBridgeHandler;
import com.echoesnet.eatandmeet.utils.HtmlUtils.JsBridgeKey;
import com.echoesnet.eatandmeet.utils.HtmlUtils.WebViewHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 成就
 * Created by an on 2017/4/13 0013.
 */

public class AchievementFrg extends BaseFragment implements JSRunBridgeHandler
{
    private final String TAG = AchievementFrg.class.getSimpleName();

    @BindView(R.id.bridge_web_task)
    BridgeWebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_task_achievement, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView()
    {
        new WebViewHelper(mWebView, getActivity())
                .getProgressWebViewInstance()
                .regJsBridge(this, JsBridgeKey.showAwardDialog)
                .loadCacheOrUrl(NetInterfaceConstant.H5_ACHIEVE);
    }

    public void setWebViewLayerType(int type)
    {
        mWebView.setLayerType(type,null);
    }
    @Override
    protected String getPageName()
    {
        return TAG;
    }



    @Override
    public void jsRunning(String key, String data, CallBackFunction function) {
        switch (key){
            case JsBridgeKey.showAwardDialog:
                TaskAct taskAct = (TaskAct) getActivity();
                if (taskAct != null)
                {
                    taskAct.showFinishDialog("获得成就奖励",data);
                }
                break;
        }
    }
}
