package com.echoesnet.eatandmeet.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.TaskAct;
import com.echoesnet.eatandmeet.utils.HtmlUtils.JSRunBridgeHandler;
import com.echoesnet.eatandmeet.utils.HtmlUtils.JsBridgeKey;
import com.echoesnet.eatandmeet.utils.HtmlUtils.WebViewHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 任务
 * Created by an on 2017/4/13 0013.
 */

public class TaskFrg extends BaseFragment implements JSRunBridgeHandler
{
    private final String TAG = TaskFrg.class.getSimpleName();

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
                .loadCacheOrUrl(NetInterfaceConstant.H5_TASK);
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
                    if (!taskAct.isFinishing())
                    {
                        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
                        ToastUtils.showCustomShortSafe(LayoutInflater.from(taskAct).inflate(R.layout.toast_finish, null));
                        ToastUtils.cancel();
                    }

                    taskAct.showFinishDialog("完成任务奖励",data);
                }
                break;
        }
    }
}
