package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyInviteBody;
import com.echoesnet.eatandmeet.models.bean.ResMyInviteBean;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.presenters.ImpMyInviteFriendsView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInviteFriendsView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MyInviteFriendsAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.google.gson.Gson;
import com.mob.MobSDK;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qq.QQClientNotExistException;
import cn.sharesdk.wechat.utils.WechatClientNotExistException;
import okhttp3.Call;

public class MyInviteFriendsAct extends BaseActivity implements IMyInviteFriendsView, PlatformActionListener
{
    private static final String TAG = MyInviteFriendsAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.btn_invite_now)
    Button btnInviteNow;
    @BindView(R.id.tv_invite_num)
    TextView tvInviteNum;
    @BindView(R.id.lv_invite_friends)
    RecyclerView lvGetMoney;
    @BindView(R.id.ll_user_list)
    LinearLayout llUserList;
    private Activity mContext;
    private Dialog pDialog;
    private MyInviteFriendsAdapter inviteFriendsAdapter;
    private List<MyInviteBody> myInviteFriendLst;
    private String userName;
    private String inviteCode;
    private ImpMyInviteFriendsView impMyInviteFriendsView;
    private SharePopWindow sharePopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_invite_friends);
        ButterKnife.bind(this);
        afterView();
    }

    private void afterView()
    {
        mContext = MyInviteFriendsAct.this;
        MobSDK.init(this, EamConstant.SHARESDK_APPKEY, EamConstant.SHARESDK_APPSECRET);
        TextView title = topBar.inflateTextCenter(new TopbarSwitchSkeleton()
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
        });
        title.setText("邀请好友");
        title.setTextColor(ContextCompat.getColor(this, R.color.C0324));

        CommonUtils.setStatusBarDarkMode(mContext, false);

        topBar.setBackground(ContextCompat.getDrawable(this, R.drawable.C0321));
        List<TextView> btns = topBar.getNavBtns(new int[]{1, 0, 0, 0});
        btns.get(0).setTextColor(ContextCompat.getColor(this, R.color.C0324));
        impMyInviteFriendsView = new ImpMyInviteFriendsView(mContext, this);
        pDialog = DialogUtil.getCommonDialog(this, "正在获取...");
        pDialog.setCancelable(false);
//        scrollView.setListView(lvGetMoney);
        myInviteFriendLst = new ArrayList<>();
        inviteFriendsAdapter = new MyInviteFriendsAdapter(mContext, myInviteFriendLst);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        lvGetMoney.setLayoutManager(manager);
        lvGetMoney.setAdapter(inviteFriendsAdapter);
        userName = SharePreUtils.getNicName(mContext);
//        initPopWindow();

        if (impMyInviteFriendsView != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            impMyInviteFriendsView.getInviteCode();
        }

        if (impMyInviteFriendsView != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            impMyInviteFriendsView.getInviteFriends();
        }
    }

    private void initPopWindow()
    {
        String username = "";
        String url = "";
        try
        {
            username = new String(userName.getBytes(), "utf-8");
            url = NetHelper.SHARE_INVITE_FIRENDS_ADDRESS + "?name=" + URLEncoder.encode(username) + "&code=" + inviteCode;
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        ShareToFaceBean shareBean = new ShareToFaceBean();
        shareBean.setShareType(Platform.SHARE_WEBPAGE);
        shareBean.setShareTitle("快来加入看脸吃饭吧");
        shareBean.setShareSiteUrl(NetHelper.SHARE_INVITE_FIRENDS_ADDRESS + "?name=" + username + "&code=" + inviteCode);
        shareBean.setShareTitleUrl(NetHelper.SHARE_INVITE_FIRENDS_ADDRESS + "?name=" + username + "&code=" + inviteCode);
        shareBean.setShareWeChatMomentsTitle("快来加入看脸吃饭吧，订座点餐优惠享不停!");
        shareBean.setShareUrl(NetHelper.SHARE_INVITE_FIRENDS_ADDRESS + "?name=" + username + "&code=" + inviteCode);
        shareBean.setShareContent("在线订座点餐，告别排队，众多女神小鲜肉主播等你来围观!");
        shareBean.setShareSinaContent("在线订座点餐，告别排队，众多女神小鲜肉主播等你来围观!" + url);
        shareBean.setShareAppImageUrl(NetHelper.LIVE_SHARE_PIC);
        shareBean.setShareListener(this);
        sharePopWindow = new SharePopWindow(mContext,
                new int[]{SharePopWindow.SHARE_WAY_WECHAT_FRIEND,
                        SharePopWindow.SHARE_WAY_QQ_FRIEND,
                        SharePopWindow.SHARE_WAY_QZONE,
                        SharePopWindow.SHARE_WAY_WECHAT_MOMENT,
                        SharePopWindow.SHARE_WAY_SINA}, shareBean);
    }

    @OnClick(R.id.btn_invite_now)
    void onclick(View view)
    {
        if (sharePopWindow != null)
            sharePopWindow.showPopupWindow(mContext.findViewById(R.id.main), null);
    }

    /**
     * popupwindow在显示中点击返回杀死当前页会造成窗口泄露
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (sharePopWindow != null && sharePopWindow.isShowing())
        {
            sharePopWindow.dismiss();
        }
        else
        {
            finish();
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getInviteCodeCallback(String response)
    {
        Logger.t(TAG).d("获取邀请码返回结果：" + response);
        try
        {
            JSONObject body = new JSONObject(response);
            inviteCode = body.getString("inCode");
            tvInviteNum.setText(inviteCode);
            initPopWindow();
        } catch (JSONException e)
        {
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void getInviteFriendsCallback(String response)
    {
        Logger.t(TAG).d("返回结果：" + response);
        ResMyInviteBean myInviteBean = new Gson().fromJson(response, ResMyInviteBean.class);

        if (myInviteBean != null)
        {
            myInviteFriendLst.clear();
            myInviteFriendLst.addAll(myInviteBean.getUserBeen());
            inviteFriendsAdapter.notifyDataSetChanged();
//            if (myInviteFriendLst.size() > 0)
//            {
//                llUserList.setVisibility(View.VISIBLE);
//                inviteFriendsAdapter.notifyDataSetChanged();
//            }
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
    {
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
    public void onError(Platform platform, int i, final Throwable throwable)
    {
        Logger.t(TAG).d(">>>>>>>>>分享失败" + i + ">>" + throwable.getMessage());
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (throwable instanceof QQClientNotExistException)
                {
                    ToastUtils.showLong("请安装QQ客户端");
                }
                else if (throwable instanceof WechatClientNotExistException)
                {
                    ToastUtils.showLong("请安装微信客户端");

                }
                else
                {
                    ToastUtils.showShort("分享失败");
                }
            }
        });
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
}
