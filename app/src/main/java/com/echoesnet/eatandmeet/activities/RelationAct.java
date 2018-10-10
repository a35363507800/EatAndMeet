package com.echoesnet.eatandmeet.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.presenters.ImpIRelationActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IRelationActView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.ContextMenuDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27
 * @description 系统通讯录H5
 */
@RuntimePermissions
public class RelationAct extends MVPBaseActivity<IRelationActView, ImpIRelationActView> implements IRelationActView
{
    private final String TAG = RelationAct.class.getSimpleName();
    private final int START_FOR_MY_RELATION = 1001;
    private final int ACTION_EVENT = 1101;
    private final int ENTER_INTO_MY_USERINFO = 1201;
    @BindView(R.id.bridge_web_my_level)
    ProgressBridgeWebView mWebView;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    private Activity mAct;
    private String openFrom;
    private List<Map<String, TextView>> navBtns;
    // private TextView blackList;
    private String uId;

    private String openSource;//直播间 跳转过来 在（openUserInfo）注册函数 使用 返回直播间
    private String shareType;
    private String sendType; // 国庆活动type giveCard 赠送卡片 askCard 索要卡片
    private boolean isShared = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_relation);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected ImpIRelationActView createPresenter()
    {
        return new ImpIRelationActView(mAct, this);
    }

    private void initView()
    {
        mAct = this;
        openFrom = getIntent().getStringExtra("openFrom");
        openSource = getIntent().getStringExtra("openSource");
        shareType = getIntent().getStringExtra("shareType");
        sendType = getIntent().getStringExtra("sendType");
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                Intent data = new Intent();
                data.putExtra("isShare", isShared);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                startActivity(new Intent(mAct, BlackListAct.class));
            }
        });
        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            Map<String, TextView> map = navBtns.get(i);
            TextView tv = map.get(TopBarSwitch.NAV_BTN_ICON);
            switch (i)
            {
                case 1:
                    tv.setText("黑名单");
                    tv.setTextSize(16);
                    tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                    if ("live".equals(openSource) || "share".equals(openSource) || "forward".equals(openSource))
                    {
                        tv.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
        //   blackList = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(NetInterfaceConstant.data_relation));
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mAct);
                reqParamMap.put("openFrom", openFrom);
                reqParamMap.put("openSource", "forward".equals(openSource) ? "share" : openSource);
                function.onCallBack(new Gson().toJson(reqParamMap));
                Logger.t(TAG).d("javaTojs" + new Gson().toJson(reqParamMap));
            }
        });

        final List<String> actionList = new ArrayList<String>();
        mWebView.registerHandler("showMenuDialog", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                final List<String> menuList = new ArrayList<String>();
                actionList.clear();
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

        // 隐藏黑名单
//        mWebView.registerHandler("hideBlackList", new BridgeHandler()
//        {
//            @Override
//            public void handler(String data, CallBackFunction function)
//            {
//                //从js获得数据
//                Logger.t(TAG).d("hideBlackList" + data);
//                try
//                {
//                    JSONObject jsonObject = new JSONObject(data);
//                    String isShow = jsonObject.getString("isShow");
//                    if (TextUtils.equals("hide",isShow))
//                    {
//                        blackList.setVisibility(View.GONE);
//                    }
//                    else
//                    {
//                        blackList.setVisibility(View.VISIBLE);
//                    }
//
//                } catch (JSONException e)
//                {
//                    e.printStackTrace();
//                    Logger.t(TAG).d(e.getMessage());
//                }
//            }
//        });

        // 打开搜索页面
        mWebView.registerHandler("openSearch", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("openSearch" + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String searchName = jsonObject.getString("searchName");
                    Intent intent = new Intent(mAct, PublicSearchAct.class);
                    intent.putExtra("searchName", searchName);
                    startActivityForResult(intent, ACTION_EVENT);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
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
                    final EaseUser user = EamApplication.getInstance().getGsonInstance().fromJson(data, new TypeToken<EaseUser>()
                    {
                    }.getType());
                    Logger.t(TAG).d("=====user:" + user);
                    if (!TextUtils.isEmpty(openSource) && "live".equals(openSource))
                    {

                        if (user != null)
                        {
                            Logger.t(TAG).d("easeUser:" + user.toString());
                            Intent intent = new Intent();
                            intent.putExtra(Constant.EXTRA_TO_EASEUSER, user);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    else if (!TextUtils.isEmpty(openSource) && ("share".equals(openSource)))
                    {
                        new CustomAlertDialog(mAct)
                                .builder()
                                .setMsg("发送给")
                                .setBoldMsg(TextUtils.isEmpty(user.getRemark()) ? user.getNickName() : user.getRemark())
                                .setMsg2Bold(true)
                                .setPositiveTextColor(Color.parseColor("#59c5ff"))
                                .setPositiveButton("发送", new View.OnClickListener()
                                {

                                    @Override
                                    public void onClick(View v)
                                    {
                                        mPresenter.queryUsersRelationShip(user);
                                    }
                                }).setNegativeButton("取消", new View.OnClickListener()
                        {

                            @Override
                            public void onClick(View v)
                            {

                            }
                        }).show();
                    }
                    else if (!TextUtils.isEmpty(openSource) && ("forward".equals(openSource)))
                    {
                        new CustomAlertDialog(mAct)
                                .builder()
                                .setMsg("发送给:")
                                .setMsgBold(true)
                                .setBoldMsg(TextUtils.isEmpty(user.getRemark()) ? user.getNickName() : user.getRemark())
                                .setMsg2Bold(false)
                                .setPositiveTextColor(ContextCompat.getColor(mAct, R.color.C0412))
                                .setNegativeTextColor(ContextCompat.getColor(mAct, R.color.C0322))
                                .setPositiveButton("确定", new View.OnClickListener()
                                {

                                    @Override
                                    public void onClick(View v)
                                    {

                                        new Thread(() -> HuanXinIMHelper.getInstance().saveContact(user)).start();

                                        Intent intent = new Intent();
                                        intent.putExtra("easeUser", user);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }).setNegativeButton("取消", new View.OnClickListener()
                        {

                            @Override
                            public void onClick(View v)
                            {

                            }
                        }).show();
                    }
                    else
                    {
                        uId = jsonObject.getString("uId");
                        Intent intent = new Intent(mAct, CNewUserInfoAct.class);
                        intent.putExtra("toUId", uId);
                        intent.putExtra("checkWay", "UId");
                        startActivityForResult(intent, ENTER_INTO_MY_USERINFO);
                    }


                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        });

        // 打开通讯录
        mWebView.registerHandler("openContact", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("openContact" + data);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    RelationActPermissionsDispatcher.onContactPermGrantedWithPermissionCheck(RelationAct.this);
                else
                    onContactPermGranted();
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
            case START_FOR_MY_RELATION:
//                reLoadWebView();
                break;
            case ACTION_EVENT:
                Logger.t(TAG).d("执行H5方法");
                // 打开搜索页面
                reLoadWebView();
                break;
            case ENTER_INTO_MY_USERINFO:
                if (data != null)
                {
                    boolean isBlack = data.getBooleanExtra("isBlack", false);
                    if (isBlack)
                    {
                        blockUser();
                        Logger.t(TAG).d("RelationAct执行H5方法blockUser()");
                    }
                    boolean isEditName = data.getBooleanExtra("isEditName", false);
                    String editRemark = data.getStringExtra("editName");
                    if (isEditName)
                    {
                        reMarkUser(editRemark);
                        Logger.t(TAG).d("RelationAct执行H5方法reMarkUser()");
                    }
                    boolean isfocus = data.getBooleanExtra("isFocus", false);
                    if (isfocus)
                    {
                        Logger.t(TAG).d("RelationAct执行H5方法reLoadWebView()");
                        reLoadWebView();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void blockUser()
    {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("uId", uId);
        mWebView.callHandler("blockUser", new Gson().toJson(stringMap), new CallBackFunction()
        {
            @Override
            public void onCallBack(String data)
            {
                Logger.t(TAG).d("blockUser" + data.toString());
            }
        });
    }

    private void reMarkUser(String reMark)
    {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("uId", uId);
        stringMap.put("remark", reMark);
        mWebView.callHandler("modifyRemark", new Gson().toJson(stringMap), new CallBackFunction()
        {
            @Override
            public void onCallBack(String data)
            {
                Logger.t(TAG).d("modifyRemark" + data.toString());
            }
        });
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {

    }

    @Override
    public void firstTalkCallback(String response, EMMessage message, EaseUser user)
    {
        sendMessage(message, user, false);
    }

    @Override
    public void queryUsersRelationShipCallBack(String response, EaseUser user)
    {
        if ("giveCard".equals(sendType))
        {
            mPresenter.giveCard(user.getuId(), getIntent().getStringExtra("activityId"), "1", response, user);
        }
        else if ("askCard".equals(sendType))
        {
            mPresenter.askCard(user.getuId(), getIntent().getStringExtra("activityId"), response, user);
        }
        else
        {
            sendShareMessage(response, user);
        }
    }

    private void sendShareMessage(String response, final EaseUser user)
    {
        try
        {
            EMMessage message = null;
            JSONObject jsonObject = new JSONObject(response);
            String inBlack = jsonObject.getString("inBlack");
            String isSayHello = jsonObject.getString("isSayHello");
            String remark = jsonObject.getString("remark");
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    HuanXinIMHelper.getInstance().saveContact(user);
                }
            }).start();
            if ("liveShare".equals(shareType))
            {
                String roomName = getIntent().getStringExtra("roomName");
                String roomId = getIntent().getStringExtra("roomId");
                String roomUrl = getIntent().getStringExtra("titleImage");
                message = EMMessage.createTxtSendMessage("【直播】" + getIntent().getStringExtra("roomName"), user.getUsername());
                message.setAttribute("shareType", shareType);
                message.setAttribute("roomId", roomId);
                message.setAttribute("nicName", getIntent().getStringExtra("nickName"));
                message.setAttribute("RoomName", roomName);
                message.setAttribute("roomUrl", roomUrl);
            }
            else
            {
                String messageDes;
                if ("column".equals(shareType))
                {
                    messageDes = String.format("[链接]【%s】%s", getIntent().getStringExtra("shareContent"), getIntent().getStringExtra("shareTitle"));
                }
                else if ("activity".equals(shareType))
                {
                    messageDes = getIntent().getStringExtra("messageDes");
                }
                else if ("party".equals(shareType))
                {
                    messageDes = getIntent().getStringExtra("messageDes");
                }
                else
                {
                    messageDes = getIntent().getStringExtra("shareTitle");
                }
                message = EMMessage.createTxtSendMessage(messageDes, user.getUsername());
            }
            message.setAttribute(Constant.MESSAGE_ATTR_IS_LIVE_SHARE, true);
            message.setAttribute("shareType", shareType);
            message.setAttribute("shareTitle", getIntent().getStringExtra("shareTitle"));
            message.setAttribute("shareContent", getIntent().getStringExtra("shareContent"));
            message.setAttribute("shareImageUrl", getIntent().getStringExtra("shareImageUrl"));
            message.setAttribute("shareUrl", getIntent().getStringExtra("shareUrl"));
            message.setAttribute("gameId", getIntent().getStringExtra("gameId"));
            message.setAttribute("columnId", getIntent().getStringExtra("columnId"));
            message.setAttribute("activityId", getIntent().getStringExtra("activityId"));
            message.setAttribute("phId", getIntent().getStringExtra("clubId"));

            makeMessageAttribute(message, user, isSayHello, inBlack, remark);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void giveCardCallback(String response, EaseUser user)
    {
        Logger.t(TAG).d("赠送成功>>>" + response);
        sendShareMessage(response, user);
    }

    @Override
    public void askCardCallback(String response, EaseUser user)
    {
        Logger.t(TAG).d("索要成功>>>" + response);
        sendShareMessage(response, user);
    }
    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra("isShare", isShared);
        setResult(RESULT_OK, data);
        finish();
    }


    private void makeMessageAttribute(EMMessage message, EaseUser toEaseUser, String isSayHello, String inBlack, String remark)
    {
        if (message == null)
        {
            return;
        }
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_ID, SharePreUtils.getId(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_UID, SharePreUtils.getUId(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_DEVICE_TYPE, EamConstant.DEVICE_TYPE);
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME, SharePreUtils.getNicName(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE, SharePreUtils.getHeadImg(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_LEVEL, SharePreUtils.getLevel(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_GENDER, SharePreUtils.getSex(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_AGE, SharePreUtils.getAge(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_VUSER, SharePreUtils.getIsVUser(mAct));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, remark);
        Logger.t(TAG).d("是否为打招呼：" + "1".equals(isSayHello));
        message.setAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, "1".equals(isSayHello));
        if ("1".equals(inBlack))
        {
            sendMessage(message, toEaseUser, true);
        }
        else
        {
            if ("1".equals(isSayHello))
            {
                mPresenter.sendFirstTalk(toEaseUser.getuId(), message, toEaseUser);
            }
            else
            {
                sendMessage(message, toEaseUser, false);
            }
        }
    }

    private void sendMessage(EMMessage message, EaseUser toEaseUser, boolean isAppend)
    {
        if (isAppend)
        {
            message.setAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, true);
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toEaseUser.getUsername(), EMConversation.EMConversationType.Chat, true);
            if (conversation != null)
                conversation.appendMessage(message);
        }
        else
        {
            EMClient.getInstance().chatManager().sendMessage(message);
        }
        String toastMessage;
        if ("giveCard".equals(sendType))
            toastMessage = "赠送卡片成功";
        else if ("askCard".equals(sendType))
            toastMessage = "索要卡片成功";
        else
            toastMessage = "分享成功";
        ToastUtils.showShort(toastMessage);
        isShared = true;
        Intent intent = new Intent();
        intent.putExtra("uid", toEaseUser.getuId());
        intent.putExtra("isShare", isShared);
        intent.putExtra("nickName", toEaseUser.getNickName());
        try
        {
            intent.putExtra("id", message.getStringAttribute("activityId"));
        } catch (HyphenateException e)
        {
            e.printStackTrace();
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    void onContactPermGranted()
    {
        startActivity(new Intent(mAct, CPhoneContactAct.class));
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    void onContactPermDenied()
    {
        ToastUtils.showLong("小饭没有获得相应的权限，无法查看通讯录");
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    void onContactPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        CommonUtils.openPermissionSettings(mAct, getResources().getString(R.string.per_permission_never_ask).replace(CommonUtils.SEPARATOR, "读取通讯录"));
    }

    @OnShowRationale({Manifest.permission.READ_CONTACTS})
    void onContactPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mAct)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("小饭需要使用您的通讯录！")
                .setPositiveButton("允许", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.cancel();
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.t(TAG).d("requestCode>" + requestCode + " permissions>" + Arrays.asList(permissions).toString() + " grantResults> " + grantResults);
        //HomeActPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

}
