package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.MyInfoBuyFaceEggActivity;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.presenters.ImpIGamePre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGameView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/8/16 0016
 * @description
 */
public class GameAct extends MVPBaseActivity<GameAct, ImpIGamePre> implements IGameView, PlatformActionListener
{
    private final String TAG = GameAct.class.getSimpleName();

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.web_game)
    ProgressBridgeWebView mWebView;

    private String gameUrl, gameId, score, matchingId, chatMatchId = "";
    private String mShareUrl;//服务器返回的shareUrl
    private Activity mAct;
    private SharePopWindow sharePopWindow;
    private TextView titleTv;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.game_act);
        CommonUtils.setStatusBarDarkMode(this, false);
        ButterKnife.bind(this);
        mAct = this;
        Intent intent = getIntent();
        gameUrl = intent.getStringExtra("gameUrl");
        gameId = intent.getStringExtra("gameId");
        chatMatchId = intent.getStringExtra("matchId");
//        gameUrl = "http://192.168.10.223/cocos2d-js/DigitalGame";
        topBarSwitch.setBackground(ContextCompat.getDrawable(this, R.drawable.C0321));
        titleTv = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                if (mPresenter != null)
                    mPresenter.exitGame(gameId);
            }

            @Override
            public void right2Click(View view)
            {
            }
        });
        titleTv.setText(intent.getStringExtra("gameName"));
        titleTv.setTextColor(ContextCompat.getColor(this, R.color.C0324));
        List<TextView> btns = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 0});
        btns.get(0).setTextColor(ContextCompat.getColor(this, R.color.C0324));
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        if (mPresenter != null)
            mPresenter.enterGame(gameId);
        initSharePopWindow();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (sharePopWindow != null && sharePopWindow.isShowing())
            sharePopWindow.dismiss();
    }

    private void initSharePopWindow()
    {
        ShareToFaceBean shareBean = new ShareToFaceBean();
        shareBean.setShareListener(this);
        sharePopWindow = new SharePopWindow(mAct, new int[]{}, shareBean);
        sharePopWindow.setShareItemClickListener(new SharePopWindow.ShareItemClickListener()
        {
            @Override
            public void onItemCLick(int position, String shareKey)
            {
                String share = "";
                switch (shareKey)
                {
                    case "微信好友":
                        share = "1";
                        break;
                    case "QQ好友":
                        share = "2";
                        break;
                    case "微信朋友圈":
                        share = "3";
                        break;
                    case "QQ空间":
                        share = "4";
                        break;
                    case "新浪微博":
                        share = "5";
                        break;
                    case "我的动态":
                        share = "6";
                        break;
                    case "看脸好友":
                        sharePopWindow.getShareInfo().setShareUrl(mShareUrl);
                        share = "7";
                        break;
                }
                mPresenter.shareGame(gameId, matchingId, share, score);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mWebView.callHandler("qiantai", "", null);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mWebView.callHandler("houtai", "", null);
    }

    @Override
    protected ImpIGamePre createPresenter()
    {
        return new ImpIGamePre();
    }

    @Override
    public void shareH5Callback(String url, String shareIcon, String ShareContent, String shareTitle)
    {
        mShareUrl = url;
        Logger.t(TAG).d("分享数据》》 mShareUrl" + mShareUrl);
        String shareUrl = NetHelper.H5_ADDRESS + "/game/share.html?gameid=" + gameId;
        ShareToFaceBean bean = sharePopWindow.getShareInfo();
        bean.setShareWeChatMomentsTitle(shareTitle);
        bean.setShareWeChatMomentsContent(ShareContent);
        bean.setShareTitleUrl(shareUrl);
        bean.setShareUrl(shareUrl);
        bean.setShareSiteUrl(shareUrl);
        bean.setShareTitle(shareTitle);
        bean.setShareContent(ShareContent);
        bean.setShareSinaContent(ShareContent + shareUrl);
        bean.setShareAppImageUrl(shareIcon);
        bean.setShareImgUrl(shareIcon);
        bean.setGameId(gameId);
        bean.setShareType(Platform.SHARE_WEBPAGE);
        bean.setShareSite("看脸吃饭");
        bean.setOpenSouse("game");// 传递房间参数到看脸好友界面
        bean.setTitleImage(shareIcon);
        sharePopWindow.showPopupWindow(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), null);
    }

    @Override
    public void shareGameCallback(String type)
    {
        if ("6".equals(type))
            ToastUtils.showShort("分享成功");
        if (sharePopWindow != null && sharePopWindow.isShowing())
            sharePopWindow.dismiss();
    }

    @Override
    public void enterGameCallback(final String body)
    {
        mWebView.loadUrl(gameUrl);
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(body);
                    if (titleTv != null)
                        titleTv.setText(jsonObject.getString("name"));
                    //传给js的数据
                    Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mAct);
                    reqParamMap.put("uphUrl", SharePreUtils.getHeadImg(mAct));
                    reqParamMap.put("level", String.valueOf(SharePreUtils.getLevel(mAct)));
                    reqParamMap.put("sex", SharePreUtils.getSex(mAct));
                    reqParamMap.put("age", SharePreUtils.getAge(mAct));
                    reqParamMap.put("nicName", SharePreUtils.getNicName(mAct));
                    reqParamMap.put("prop", body);
                    if (TextUtils.isEmpty(chatMatchId))
                        chatMatchId = "";
                    reqParamMap.put("matchId", chatMatchId);
                    Logger.t(TAG).d("传给js的数据：" + new Gson().toJson(reqParamMap));
                    function.onCallBack(new Gson().toJson(reqParamMap));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        // 分享
        mWebView.registerHandler("shareGame", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    score = jsonObject.getString("score");
                    matchingId = jsonObject.getString("matchingId");
                    mPresenter.shareH5(jsonObject.getString("gameId"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        mWebView.registerHandler("gameRecharge", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                Intent intent = new Intent(mAct, MyInfoAccountAct2.class);
                startActivity(intent);
            }
        });
        mWebView.registerHandler("gameCheek", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                Intent intent = new Intent(mAct, MyInfoBuyFaceEggActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void exitGameCallback()
    {
        Logger.t(TAG).d("exitGameCallback");
        mWebView.loadUrl("about:blank");
        finish();
    }

    @Override
    public void exitGameFail()
    {
        mWebView.loadUrl("about:blank");
        Logger.t(TAG).d("exitGameFail");
        finish();
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
    {
        Logger.t(TAG).d("i>>>>" + i + platform.getName());
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtils.showShort("分享成功");
            }
        });
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable)
    {
        Logger.t(TAG).d(">>>>>>>>>分享失败" + i + ">>" + throwable.getMessage());
        if (throwable instanceof cn.sharesdk.tencent.qzone.QQClientNotExistException)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ToastUtils.showLong("请安装QQ客户端");
                }
            });
        }
        else
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ToastUtils.showShort("分享失败");
                }
            });
        }
    }

    @Override
    public void onCancel(Platform platform, int i)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtils.showShort("分享取消");
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if (mPresenter != null)
            mPresenter.exitGame(gameId);
    }

}
