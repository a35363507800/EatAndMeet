package com.echoesnet.eatandmeet.fragments.livefragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.fragments.BaseFragment;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * 排行榜
 * Created by an on 2016/11/22 0022.
 */
public class LiveRankingFrg extends BaseFragment
{
    private final String TAG = LiveRankingFrg.class.getCanonicalName();

    @BindView(R.id.web_ranking)
    ProgressBridgeWebView mWebView;
    private Unbinder unbinder;
    private String uId = "";
    private String mUrl;//web url
    private String roomId;//房间id
    private String type;//0 总榜 1 本场
    public static final int RESULT_FROM_USERINFO = 20;

    public LiveRankingFrg()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_ranking, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        afterView();
    }

    private void afterView()
    {
        roomId = getArguments().getString("roomId");
        type = getArguments().getString("type");
        Logger.t(TAG).d("roomId===" + roomId + "type===" + type);
        if ("0".equals(type))
        {
            mUrl = NetInterfaceConstant.H5_TOTAL_RANK;
        }
        else
        {
            mUrl = NetInterfaceConstant.H5_THIS_RANK;
        }

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl(mUrl);
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(getActivity());
                reqParamMap.put("roomId", roomId);
                function.onCallBack(new Gson().toJson(reqParamMap));
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
                    {
                        uId = jsonObject.getString("uId");
                        Intent intent = new Intent(getActivity(), CNewUserInfoAct.class);
                        intent.putExtra("toUId", uId);
                        intent.putExtra("checkWay", "UId");
                        intent.putExtra("chatRoomId", roomId);
                        if (EamApplication.getInstance().controlUInfo.size() == 2)
                        {
                            if (EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()) != null)
                            {
                                EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()).finish();
                                EamApplication.getInstance().controlUInfo.clear();
                            }
                        }
                        startActivityForResult(intent, RESULT_FROM_USERINFO);
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });

    }

    /**
     * 调用h5 reload
     */
    public void reLoadWebView()
    {
        //传给js的数据
        Map<String, String> reqParamMap = new HashMap<String, String>();
        reqParamMap.put("action", "reload");
        reqParamMap.put("uid", uId);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
                switch (requestCode)
                {
                    case RESULT_FROM_USERINFO:
                        Logger.t(TAG).d("执行H5方法");
                        if (data.getBooleanExtra("isFocus", false))
                        {
                            reLoadWebView();
                        }
                        break;
                    default:
                        break;
                }
                break;
            case RESULT_CANCELED:
                break;
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }
}
