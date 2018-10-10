package com.echoesnet.eatandmeet.activities.live;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.activities.MyAuthenticationAct;
import com.echoesnet.eatandmeet.presenters.ImpIFanPageDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFanPageDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MyInfoFanPageDetailActivity extends MVPBaseActivity<IFanPageDetailView, ImpIFanPageDetailView> implements IFanPageDetailView
{
    private static final String TAG = LiveWithDrawActivity.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.tv_canUse_fanPage)
    TextView tvCanUseFanPage;
    @BindView(R.id.tv_canWithDraw_fan_page)
    TextView tvCanWithDrawFanPage;
    @BindView(R.id.tips_two)
    TextView tipsTwo;

    @BindView(R.id.btn_withDraw_ok)
    Button btnWithDrawOk;
    private String canWithDrawMoney = "";
    private Activity mContext;
    private Dialog pDialog;
    private String userRate = "";
    private String platformRate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_info_fan_page_detail);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected ImpIFanPageDetailView createPresenter()
    {
        return new ImpIFanPageDetailView();
    }

    private void initAfterViews()
    {
        mContext = this;
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
        }).setText("余额提现");
        topBarSwitch.setBackground(ContextCompat.getDrawable(mContext, R.drawable.C0321));
        topBarSwitch.getNavBtns(new int[]{1, 0, 0, 0});
        pDialog = DialogUtil.getCommonDialog(mContext, "正在获取");
        pDialog.setCancelable(false);
        if (mPresenter != null)
            mPresenter.getMyBalance();
    }

    @OnClick({R.id.btn_withDraw_ok})
    void clickListener(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_withDraw_ok:
                if (mPresenter != null)
                    mPresenter.getBindStats();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!mContext.isFinishing() && pDialog != null && !pDialog.isShowing())
            pDialog.show();
        if (mPresenter != null)
        {
            mPresenter.getMyBalance();
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_validate:

                if (ErrorCodeTable.IDCARD_NOT_VALIDATE.equals(code))

                {
                    if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                    Intent intent = new Intent(mContext, MyAuthenticationAct.class);
                    intent.putExtra("openSource", "myInfoFanPageDetailAct");
                    mContext.startActivity(intent);
                } else if (ErrorCodeTable.WECHAT_NOT_BIND.equals(code) || ErrorCodeTable.WECHAT_LOSE_BIND.equals(code))
                {
                    Intent intent = new Intent(mContext, LiveWithDrawActivity.class);
                    intent.putExtra("canWithDrawFanPage", canWithDrawMoney);
                    intent.putExtra("userRate", userRate);
                    intent.putExtra("platformRate", platformRate);
                    intent.putExtra("status", 1);
                    startActivity(intent);
                } else
                {
                    if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, "", exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getMyBalanceCallBack(String str)
    {
        Logger.t(TAG).d("返回结果：" + str);
        try
        {
            JSONObject object = new JSONObject(str);
            String balance = object.getString("balance");
            String money = object.getString("money");
            canWithDrawMoney = money;
            userRate = object.getString("userRate");
            platformRate = object.getString("platformRate");
//                String isSignedAnchor = object.getString("isSignedAnchor");
            userRate = userRate.substring(0, userRate.lastIndexOf("."));
            platformRate = platformRate.substring(0, platformRate.lastIndexOf("."));
            tipsTwo.setText("2.根据平台规定余额的" + userRate + "%归属用户，" + platformRate + "%归属平台");
            tvCanUseFanPage.setText("￥" + balance);
            tvCanWithDrawFanPage.setText("￥" + money);
            if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void getBindStatsCallBack(String response)
    {
        Logger.t(TAG).d("返回结果》" + response);
        try
        {
            JSONObject object = new JSONObject(response);
            String nicName = object.getString("nicName");
            String headImgUrl = object.getString("headimgurl");
            Intent intent = new Intent(mContext, LiveWithDrawActivity.class);
            intent.putExtra("canWithDrawFanPage", canWithDrawMoney);
            intent.putExtra("userRate", userRate);
            intent.putExtra("platformRate", platformRate);
            intent.putExtra("status", 0);
            intent.putExtra("nicName", nicName);
            intent.putExtra("headImgUrl", headImgUrl);
            startActivity(intent);
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
            if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}
