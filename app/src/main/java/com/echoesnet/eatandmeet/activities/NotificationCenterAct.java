package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.SysMesBean;
import com.echoesnet.eatandmeet.models.bean.SysNewsMessageListBean;
import com.echoesnet.eatandmeet.models.bean.SysNewsTopBean;
import com.echoesnet.eatandmeet.presenters.ImpINotifyCenterPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.INotificationCenterView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.adapters.PersonNewsAdapter;
import com.echoesnet.eatandmeet.views.widgets.ContextMenuDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/10 16.
 * @description app的通知中心页面
 */

public class NotificationCenterAct extends MVPBaseActivity<NotificationCenterAct, ImpINotifyCenterPre> implements INotificationCenterView
{
    @BindView(R.id.tbs_top_bar)
    TopBarSwitch tbsTopBar;
    @BindView(R.id.prl_personInfo)
    PullToRefreshListView prlPersonInfo;
    private static final String TAG = NotificationCenterAct.class.getSimpleName();
    private Activity mAct;
    private Dialog pDialog;
    private String START_IDX = "0";
    private final static String PAGE_COUNT = "20";
    private ListView mListView;
    private List<Object> newsList;
    private PersonNewsAdapter mAdapter;
    private List<Map<String, TextView>> navBtns;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private TextView ignore;
    private int LOOK_SYSTEM_INFO = 100;
    private boolean isReadSysMessage = false;
    private String systemUnreadNum;
    private boolean isFirstIn = true;
    private EmptyView eview;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_notifi_center);
        mAct = this;
        ButterKnife.bind(this);
        eview = new EmptyView(mAct);
        TextView tvTitle = tbsTopBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                if (!isReadSysMessage)
                {
                    Intent intent = new Intent(EamConstant.EAM_REFRESH_SYS_MSG);
                    intent.putExtra("sys_counts", systemUnreadNum);
                    sendBroadcast(intent);
                }
                finish();
            }

            @Override
            public void rightClick(View view)
            {
            }

            @Override
            public void right2Click(View view)
            {
            }
        });
        tvTitle.setText("通知中心");
        tvTitle.setTypeface(null, Typeface.BOLD);
        navBtns = tbsTopBar.getNavBtns2(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON);
            if (i == 1)
            {
                tv.setText("忽略未读");
                tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                tv.setTextSize(16);
            }
        }

        ignore = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        ignore.setOnClickListener((v) ->
        {
            if (mPresenter != null)
                mPresenter.ignoreUnread();
        });


        newsList = new ArrayList<>();
        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理...");
        pDialog.setCancelable(false);
        footView = LayoutInflater.from(mAct).inflate(R.layout.footview_normal_list, null);
        mListView = prlPersonInfo.getRefreshableView();
        prlPersonInfo.setMode(PullToRefreshBase.Mode.BOTH);
//        View empty = LayoutInflater.from(mAct).inflate(R.layout.empty_view, null);
//        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有新消息");
        eview.setIsGone(false);
        eview.setImageId(R.drawable.bg_wushoucang);
        eview.setContent("暂时没有新消息");

        prlPersonInfo.setEmptyView(eview);
        prlPersonInfo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                if (mPresenter != null)
                {
                    mPresenter.getAllNotification(START_IDX, String.valueOf(newsList.size()), "refresh");
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                {
                    mPresenter.getAllNotification(String.valueOf(newsList.size()), PAGE_COUNT, "add");
                }
            }
        });


        mAdapter = new PersonNewsAdapter(newsList, mAct);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#e6e6e6")));
        mListView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new PersonNewsAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, SysNewsMessageListBean bean, int position)
            {
                if (mAdapter != null)
                {
                    if ("0".equals(bean.getUser().getFocus()))
                    {
                        mPresenter.focusPerson(bean.getUser().getuId(), "1", position, view);
                    }

                }
            }
        });
        mAdapter.setOnTopItemClickListener((view, position,bean) ->
        {
            isReadSysMessage = true;
            bean.setSystemUnreadNum("0");
            Intent intent = new Intent(mAct, SysNewsAct.class);
            startActivityForResult(intent, LOOK_SYSTEM_INFO);
        });
        prlPersonInfo.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) ->
        {
            position = position - 1;
            Intent intent = new Intent(mAct, CNewUserInfoAct.class);
            if (newsList.get(position) instanceof SysNewsMessageListBean)
            {
                SysNewsMessageListBean itemBean = (SysNewsMessageListBean) newsList.get(position);
                intent.putExtra("toUId", itemBean.getUser().getuId());
                intent.putExtra("toId", itemBean.getUser().getId());
                mAct.startActivity(intent);
            }
        });
        prlPersonInfo.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) ->
        {
            position = position - 1;
            if (mAdapter != null)
            {
                if (newsList.get(position) instanceof SysNewsMessageListBean)
                {
                    showDeleteDialog(position);
                }

            }
            return true;
        });

        if (mPresenter != null)
        {
            mPresenter.getAllNotification(START_IDX, PAGE_COUNT, "add");
        }

    }

    private void showDeleteDialog(final int index)
    {

            new ContextMenuDialog(new ContextMenuDialog.MenuDialogCallBack()
            {
                @Override
                public void menuOnClick(String menuItem, int position)
                {
                    switch (menuItem)
                    {
                        case "删除":
                            if (mPresenter != null)
                            {
                                if (newsList.get(index) instanceof SysNewsMessageListBean)
                                {
                                    SysNewsMessageListBean itemBean = (SysNewsMessageListBean) newsList.get(index);
                                    mPresenter.deleteMessage(itemBean.getMessageId(), index);
                                }
                            }
                            break;
                    }
                }
            }).showContextMenuBox(mAct, Arrays.asList(new String[]{"删除"}));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected ImpINotifyCenterPre createPresenter()
    {
        return new ImpINotifyCenterPre();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
    }

    @Override
    public void requestAllNotifyCallback(String response, String type)
    {
        Logger.t(TAG).d("返回参数》》" + response.toString());
        try
        {
            JSONObject jsonQesponse = new JSONObject(response);
            String systemMessage = jsonQesponse.getJSONObject("systemMessage").toString();
            //系统消息显示
            Logger.t(TAG).d("systemMessage" + systemMessage);
            systemUnreadNum = jsonQesponse.getString("systemUnreadNum");
            JSONArray messageList = jsonQesponse.getJSONArray("messageList");
            SysNewsTopBean sysNewsTopBean = null;
            if (sysNewsTopBean == null)
            {
                if (TextUtils.equals(type, "refresh") || isFirstIn)
                {
                    sysNewsTopBean = new SysNewsTopBean();
                    isFirstIn = false;
                }
            }
            if (sysNewsTopBean != null)
            {
                sysNewsTopBean.setSystemUnreadNum(systemUnreadNum);
            }
            StringBuilder data = new StringBuilder();
            if (!TextUtils.equals("{}", systemMessage) || !TextUtils.isEmpty(systemMessage))
            {
                SysMesBean sysMesBean = EamApplication.getInstance().getGsonInstance().fromJson(systemMessage, SysMesBean.class);

                if (TextUtils.isEmpty(sysMesBean.getUser() == null ? "" : sysMesBean.getUser().getNicName()))
                {
                    if (sysMesBean.getDesc().contains("$CARD"))
                    {
                        data.append(String.format("<font color=%s>%s</font>",
                                ContextCompat.getColor(mAct, R.color.C0322),
                                sysMesBean.getDesc().substring(0, sysMesBean.getDesc().indexOf("$CARD"))));
                        data.append(String.format("<font color=%s>%s</font>",
                                ContextCompat.getColor(mAct, R.color.C0412),
                                sysMesBean.getCardName()));
                        data.append(String.format("<font color=%s>%s</font>",
                                ContextCompat.getColor(mAct, R.color.C0322),
                                sysMesBean.getDesc().substring(sysMesBean.getDesc().indexOf("$CARD") + 5, sysMesBean.getDesc().length())));
                        Logger.t(TAG).d("拼接卡片");
                    } else
                    {
                        data.append(String.format("<font color=%s>%s</font>", ContextCompat.getColor(mAct, R.color.C0322), sysMesBean.getDesc()));
                    }
                } else
                {
                    try
                    {
                        if (sysMesBean.getDesc().contains("$USER"))
                        {
                            data.append(String.format("<font color=%s>%s</font>",
                                    ContextCompat.getColor(mAct, R.color.C0322),
                                    sysMesBean.getDesc().substring(0, sysMesBean.getDesc().indexOf("$USER"))));
                            data.append(String.format("<font color=%s>%s</font>",
                                    ContextCompat.getColor(mAct, R.color.C0412),
                                     TextUtils.isEmpty(sysMesBean.getUser().getRemark())?sysMesBean.getUser().getNicName():sysMesBean.getUser().getRemark() +
                                    " (ID: " + sysMesBean.getUser().getId() + ")"));
                            data.append(String.format("<font color=%s>%s</font>",
                                    ContextCompat.getColor(mAct, R.color.C0322),
                                    sysMesBean.getDesc().substring(sysMesBean.getDesc().indexOf("$USER") + 5, sysMesBean.getDesc().length())));

                            Logger.t(TAG).d("拼接推荐人");
                        }

                    } catch (Exception e)
                    {
                        Logger.t("NotificationCenterAct").d(e.getMessage());
                        e.printStackTrace();
                        data.append(String.format("<font color=%s>%s</font>", ContextCompat.getColor(mAct, R.color.C0322), sysMesBean.getDesc()));
                    }
                }
                CharSequence sysDes;
                if (TextUtils.isEmpty(Html.fromHtml(data.toString())))
                    sysDes = sysMesBean.getDesc();
                else
                    sysDes = Html.fromHtml(data.toString());

                if (sysNewsTopBean != null)
                {
                    sysNewsTopBean.setTvSysContent(TextUtils.isEmpty(sysDes.toString()) ? "暂无消息" : sysDes);
                    sysNewsTopBean.setTvSysTime(TextUtils.isEmpty(sysMesBean.getCreateTime()) ? "30分钟前" : sysMesBean.getCreateTime());
                }
            } else
            {
                Logger.t(TAG).d("系统消息是空");
                if (sysNewsTopBean != null)
                {
                    sysNewsTopBean.setTvSysContent("暂无消息");
                }

            }

            if (systemUnreadNum != null)
            {
                if (TextUtils.equals("0", systemUnreadNum))
                {
                    ignore.setTextColor(ContextCompat.getColor(mAct, R.color.C0322));
                    ignore.setEnabled(false);
                } else
                {
                    ignore.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                    ignore.setEnabled(true);
                }
            }

            List<Object> mgsList = new ArrayList<>();
            for (int i = 0; i < messageList.length(); i++)
            {
                JSONObject obj = messageList.getJSONObject(i);
                SysNewsMessageListBean synMsg = new SysNewsMessageListBean();
                synMsg.setCreateTime(obj.getString("createTime"));
                synMsg.setDesc(obj.getString("desc"));
                synMsg.setTip(obj.getString("tip"));
                synMsg.setUnread(obj.getString("unread"));
                synMsg.setMessageId(obj.getString("messageId"));
                synMsg.setOrderId(obj.getString("orderId"));
                synMsg.setUser(EamApplication.getInstance()
                        .getGsonInstance().fromJson(obj.getJSONObject("user").toString(), SysNewsMessageListBean.UserBean.class));
                mgsList.add(synMsg);
            }
            if (sysNewsTopBean != null)
            {
                mgsList.add(0, sysNewsTopBean);
            }
            eview.setIsGone(true);
            if (mgsList.size() == 0)
            {
                LoadFootView.showFootView(mListView, true, footView, null);
                //禁止上啦
                pullMove = false;
            }
            if ("refresh".equals(type))
            {
                newsList.clear();
                LoadFootView.showFootView(mListView, false, footView, null);
                //允许上啦
                pullMove = true;
            }
            newsList.addAll(mgsList);
            mAdapter.notifyDataSetChanged(true);

            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            if (prlPersonInfo != null)
            {
                prlPersonInfo.onRefreshComplete();
                if (pullMove)
                {
                    Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                    prlPersonInfo.setMode(PullToRefreshBase.Mode.BOTH);
                } else
                {
                    Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                    prlPersonInfo.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("解析异常》》" + e.getMessage());
        }

    }

    @Override
    public void ignoreUnreadCallBack(String response)
    {

        Logger.t(TAG).d("忽略未读成功");
        isReadSysMessage = true;
        mAdapter.notifyTopUnReadChanged();

        Intent intent = new Intent(EamConstant.EAM_REFRESH_IGNORE_SYS_MSG);
        intent.putExtra("ignore", true);
        sendBroadcast(intent);
        ignore.setTextColor(ContextCompat.getColor(mAct, R.color.C0322));
        ignore.setEnabled(false);


    }

    @Override
    public void focusCallBack(String body, int position, String operFlag, View view)
    {
        if (newsList.get(position) instanceof SysNewsMessageListBean)
        {
            SysNewsMessageListBean itemBean = (SysNewsMessageListBean) newsList.get(position);

            if (operFlag.equals("0"))
            {
                Logger.t(TAG).d("取消关注成功");
                view.setTag("未关注");
                itemBean.getUser().setFocus("0");
            } else if (operFlag.equals("1"))
            {
                Logger.t(TAG).d("关注成功");
                view.setTag("已关注");
                itemBean.getUser().setFocus("1");
                for (int i = 0; i < newsList.size(); i++)
                {
                    if (newsList.get(i) instanceof SysNewsMessageListBean)
                    {
                        SysNewsMessageListBean itemBean1 = (SysNewsMessageListBean) newsList.get(i);
                        if (itemBean1.getUser().getuId().equals(itemBean.getUser().getuId()))
                        {
                            itemBean1.getUser().setFocus("1");
                        }
                    }

                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void deleteMessageCallBack(String response, int position)
    {
        Logger.t(TAG).d("删除通知消息成功");
        if (mAdapter != null)
        {
            newsList.remove(position);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOOK_SYSTEM_INFO)
        {
            mAdapter.notifyTopUnReadChanged();
            Intent intent = new Intent(EamConstant.EAM_REFRESH_IGNORE_SYS_MSG);
            intent.putExtra("ignore", true);
            sendBroadcast(intent);
            ignore.setTextColor(ContextCompat.getColor(mAct, R.color.C0322));
            ignore.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (!isReadSysMessage)
        {
            Intent intent = new Intent(EamConstant.EAM_REFRESH_SYS_MSG);
            intent.putExtra("sys_counts", systemUnreadNum);
            sendBroadcast(intent);
        }

    }
}
