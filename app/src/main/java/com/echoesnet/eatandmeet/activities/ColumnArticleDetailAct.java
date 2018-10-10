package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CommentInputView;
import com.echoesnet.eatandmeet.views.widgets.ContextMenuDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressEnableVideoWebView;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/9/16 0016
 * @description
 */
public class ColumnArticleDetailAct extends BaseActivity implements PlatformActionListener
{
    private final String TAG = ColumnArticleDetailAct.class.getSimpleName();

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.bridge_webView)
    ProgressEnableVideoWebView mWebView;
    @BindView(R.id.comment_input)
    CommentInputView commentInputView;
    @BindView(R.id.nonVideoLayout)
    View nonVideoLayout;
    @BindView(R.id.videoLayout)
    ViewGroup videoLayout;
    
    private Activity mAct;
    private SharePopWindow sharePopWindow;
    private String articleId,columnName,columnTitle,shareUrl,imgUrl;
    private String webUrl;
    private TextView titleTv;
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_column_article_detail);
        ButterKnife.bind(this);
        mAct = this;
        articleId = getIntent().getStringExtra("articleId");
        columnName = getIntent().getStringExtra("columnName");
        columnTitle = getIntent().getStringExtra("columnTitle");
        shareUrl = getIntent().getStringExtra("shareUrl");
        imgUrl = getIntent().getStringExtra("imgUrl");
        webUrl = NetInterfaceConstant.H5_COLUMN_ARTICLE_DETAIL;
        initWebView();
        
        titleTv = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                    finish();
            }

            @Override
            public void right2Click(View view)
            {
                if (sharePopWindow != null)
                    sharePopWindow.showPopupWindow(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), null);
            }
        });
//        titleTv.setText(columnName);
       TextView tv =  topBarSwitch.getNavBtns2(new int[]{1,0,0,1}).get(1).get(TopBarSwitch.NAV_BTN_ICON);
        tv.setText("分享");
        tv.setTextSize(15);
        tv.setTextColor(ContextCompat.getColor(mAct,R.color.C0412));
    }
    private void initSharePopWindow()
    {
        ShareToFaceBean shareBean = new ShareToFaceBean();
        shareBean.setShareType(Platform.SHARE_WEBPAGE);
        shareBean.setShareWeChatMomentsTitle(columnTitle);
        shareBean.setShareWeChatMomentsContent(columnName);
        shareBean.setShareTitle(columnTitle);
        shareBean.setShareContent(columnName);
        shareBean.setShareUrl(shareUrl);
        shareBean.setColumnId(articleId);
        shareBean.setShareSite("看脸吃饭");
        shareBean.setShareSiteUrl(shareUrl);
        shareBean.setShareTitleUrl(shareUrl);
        shareBean.setShareAppImageUrl(imgUrl);
        shareBean.setShareImgUrl(imgUrl);
        shareBean.setTitleImage(imgUrl);
        shareBean.setOpenSouse("column");
        shareBean.setShareListener(this);
        shareBean.setShareSinaContent(columnTitle + shareUrl);
        sharePopWindow = new SharePopWindow(mAct, new int[]{}, shareBean);
        sharePopWindow.setShareItemClickListener(new SharePopWindow.ShareItemClickListener()
        {
            @Override
            public void onItemCLick(int position, String shareKey)
            {
                switch (shareKey)
                {
                    case "我的动态":
                        Intent intent = new Intent(mAct, ShareColumnArticleAct.class);
                        intent.putExtra("vArticalId", articleId);
                        intent.putExtra("title", columnName);
                        intent.putExtra("content", columnTitle);
                        intent.putExtra("imgUrl", imgUrl);
                        intent.putExtra("shareType", "column");
                        startActivityForResult(intent, EamConstant.EAM_OPEN_SHARE_COLUMN);
                        break;
                    case "看脸好友":
                        NetHelper.activityShare(mAct,"1");
                        break;
                }
            }
        });
    }
    private void initWebView()
    {
        mWebView.loadUrl(webUrl);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                if (!TextUtils.isEmpty(data))
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(data);
                        imgUrl = jsonObject.getString("imgUrl");
                        columnName = jsonObject.getString("nicName");
                        columnTitle = jsonObject.getString("title");
                        shareUrl = jsonObject.getString("shareUrl");
                        initSharePopWindow();
                        if (!TextUtils.isEmpty(columnName))
                            titleTv.setText(columnName);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mAct);
                reqParamMap.put("articleId",articleId);
                function.onCallBack(new Gson().toJson(reqParamMap));
            }
        });
        mWebView.registerHandler("showMenuDialog", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                final List<String> menuList = new ArrayList<String>();
                final List<String> actionList = new ArrayList<String>();
                try
                {
                    JSONArray js = new JSONArray(data);
                    for (int i = 0; i < js.length(); i++)
                    {
                        JSONObject jb = js.getJSONObject(i);
                        menuList.add(jb.getString("message"));
                        actionList.add(jb.getString("action"));
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                new ContextMenuDialog(new ContextMenuDialog.MenuDialogCallBack()
                {
                    @Override
                    public void menuOnClick(String menuItem, int position)
                    {
                        mWebView.callHandler(actionList.get(position), "", null);
                    }
                }).showContextMenuBox(mAct, menuList);
            }
        });
        mWebView.registerHandler("focusUser", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("focusUser" + data);
                //传给js的数据
                try
                {
                    JSONObject js = new JSONObject(data);
                    String uid=js.getString("uid");
                    Intent intent=getIntent().putExtra("uid",uid);
                    setResult(RESULT_OK,intent);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });


        View videoLoading = LayoutInflater.from(mAct).inflate(R.layout.view_loading_video,null);
        ProgressEnableVideoWebView.EnableVideoWebChromeClient myWebChromeClient = new ProgressEnableVideoWebView.EnableVideoWebChromeClient(nonVideoLayout,videoLayout,videoLoading,mWebView);
        mWebView.setWebChromeClient(myWebChromeClient);
        myWebChromeClient.setOnToggledFullscreen(new ProgressEnableVideoWebView.ToggledFullscreenCallback()
        {
            @Override
            public void toggledFullscreen(boolean fullscreen)
            {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen)
                {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                }
                else
                {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }

            }
        });
        mWebView.registerHandler("openToast", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String content = jsonObject.getString("content");
                    if (!TextUtils.isEmpty(content))
                        ToastUtils.showShort(content);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        mWebView.registerHandler("showCommentDialog", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                commentInputView.setVisibility(View.VISIBLE);
                commentInputView.showOrHideSoftInput(true);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mWebView.getLayoutParams();
                layoutParams.bottomMargin = CommonUtils.dp2px(mAct,54);
                mWebView.setLayoutParams(layoutParams);
            }
        });
        mWebView.registerHandler("openUserInfo", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                try
                {
                    JSONObject jsonObject  = new JSONObject(data);
                    Intent intent = new Intent(mAct,CNewUserInfoAct.class);
                    intent.putExtra("checkWay", "UId");
                    intent.putExtra("toUId",jsonObject.getString("uId"));
                    startActivity(intent);


                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        //查看大图片
        mWebView.registerHandler("showImage", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据  luId：被邀请人uId
                Logger.t(TAG).d("showImage" + data);
                try
                {
                    JSONObject json = new JSONObject(data);
                    int position = json.getInt("position");
                    String urls = json.getString("urlList");
                    Logger.t(TAG).d("urls" + urls);
                    JSONArray jsonArray = new JSONArray(urls);
                    ArrayList<String> urlList = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        urlList.add(jsonArray.get(i).toString());
                        Logger.t(TAG).d(jsonArray.get(i).toString());
                    }
                    Logger.t(TAG).d(urlList.toString());
                    CommonUtils.showImageBrowser(mAct, urlList, position, null);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        checkNetInfo();
        commentInputView.setCommentInputListener(new CommentInputView.CommentInputListener()
        {
            @Override
            public void commentClick(final String content)
            {
                Map<String, String> reqParamMap = new HashMap<String, String>();
                reqParamMap.put("content",content);
                mWebView.callHandler("commentArticle", new Gson().toJson(reqParamMap), new CallBackFunction()
                {
                    @Override
                    public void onCallBack(String data)
                    {
                        commentInputView.regain();
                        commentInputView.setVisibility(View.GONE);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mWebView.getLayoutParams();
                        layoutParams.bottomMargin = 0;
                        mWebView.setLayoutParams(layoutParams);
                    }
                });
            }
        });


    }

    private void checkNetInfo()
    {
        mWebView.registerHandler("checkNetInfo", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("checkNetInfo","data>>>>" + data);
                try
                {
                    JSONObject jsonObject = new JSONObject();
                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(mAct).builder().setTitle("提示").setCancelable(false);
                    final int netStatus = NetHelper.getNetworkStatus(mAct);
                    String msg = null;
                    if (netStatus == -1)//没网
                    {
                        msg = "当前网络不可用,请先检查网络是否可用";

                    }else if (netStatus == 1)//非wifi
                    {
                        jsonObject.put("isPlaying","1");
                        function.onCallBack(jsonObject.toString());
                    }else {
                        msg = "当前为运营商流量使用环境，是否开始播放？";
                    }
                    if (!TextUtils.isEmpty(msg))
                    {
                        customAlertDialog.setMsg(msg);
                        customAlertDialog.setPositiveButton("确定", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                try
                                {
                                    jsonObject.put("isPlaying","1");
                                    function.onCallBack(jsonObject.toString());
                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                        customAlertDialog.setNegativeButton("取消", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                try
                                {
                                    jsonObject.put("isPlaying","0");
                                    function.onCallBack(jsonObject.toString());
                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                        customAlertDialog.show();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (commentInputView.hideInputOrEmoji(this,ev))
            {
                commentInputView.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mWebView.getLayoutParams();
                layoutParams.bottomMargin = 0;
                mWebView.setLayoutParams(layoutParams);
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev))
        {
            return true;
        }
        return onTouchEvent(ev);
    }



    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
    {
        ToastUtils.showShort("分享成功");
        NetHelper.activityShare(mAct,"1");
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable)
    {

    }

    @Override
    public void onCancel(Platform platform, int i)
    {
        ToastUtils.showShort("分享取消");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EamConstant.EAM_OPEN_SHARE_COLUMN && resultCode == RESULT_OK)
        {
            NetHelper.activityShare(mAct,"1");
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mWebView != null)
            mWebView.loadUrl("about:blank");
        super.onDestroy();
    }
}
