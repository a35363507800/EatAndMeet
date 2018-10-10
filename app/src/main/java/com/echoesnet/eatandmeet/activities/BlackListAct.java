package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27
 * @description 系统通讯录进入黑名单
 */
public class BlackListAct extends BaseActivity
{

    private final String TAG = BlackListAct.class.getSimpleName();
    private final int START_FOR_MY_ACCOUNT = 1001;
    @BindView(R.id.bridge_webView)
    ProgressBridgeWebView mWebView;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_black_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView()
    {
        mActivity = this;
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText(mActivity.getResources().getString(R.string.black_list_title));
        List<Map<String, TextView>> navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 0});

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(NetInterfaceConstant.data_black));
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mActivity);
//                reqParamMap.put("roomId", roomId);
//                reqParamMap.put("openFrom", openFrom);
                function.onCallBack(new Gson().toJson(reqParamMap));
                Logger.t(TAG).d("javaTojs" + new Gson().toJson(reqParamMap));
            }
        });

        // 用户详情
        mWebView.registerHandler("openUserInfo", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("openUserInfo" + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String uId = jsonObject.getString("uId");
                    Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                    intent.putExtra("toUId", uId);
                    intent.putExtra("checkWay", "UId");
                    startActivity(intent);
//                    if (!TextUtils.isEmpty(openSource) && "live".equals(openSource))
//                    {
//                        EaseUser user = EamApplication.getInstance().getGsonInstance().fromJson(data, new TypeToken<EaseUser>()
//                        {
//                        }.getType());
//                        if (user != null)
//                        {
//                            Logger.t(TAG).d("easeUser:" + user.toString());
//                            Intent intent = new Intent();
//                            intent.putExtra(Constant.EXTRA_TO_EASEUSER, user);
//                            setResult(RESULT_OK, intent);
//                            finish();
//                        }
//                    }
//                    else
//                    {
//                        String uId = jsonObject.getString("uId");
//                        Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
//                        intent.putExtra("toUId", uId);
//                        intent.putExtra("checkWay", "UId");
//                        startActivity(intent);
//                    }


                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });

        mWebView.registerHandler("removeBlackList", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String username = jsonObject.getString("username");
                    EMConversation c = EMClient.getInstance().chatManager().getConversation(username, EMConversation.EMConversationType.Chat, true);
                    if(c!=null)
                        c.setExtField("");
                    sendCMDMsg(EamConstant.EAM_CHAT_OUTBLACK_NOTIFY, username);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        // 提示信息
        mWebView.registerHandler("openToast", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("openToast" + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String content = jsonObject.getString("content");
                    ToastUtils.showShort(content);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });

    }

    private void sendCMDMsg(String action, String toChatUserName)
    {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setChatType(EMMessage.ChatType.Chat);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(action);

        message.setTo(toChatUserName);
        message.addBody(cmdMessageBody);

        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 调用h5 reload
     */
    public void reLoadWebView()
    {
        //传给js的数据
        Map<String, String> reqParamMap = new HashMap<String, String>();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case START_FOR_MY_ACCOUNT:
//                reLoadWebView();
                break;
        }
    }
}
