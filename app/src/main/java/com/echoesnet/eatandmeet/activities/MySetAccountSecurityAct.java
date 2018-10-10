package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.presenters.ImpIMySetAccountSecurityView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetAccountSecurityView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 账户管理
 */
public class MySetAccountSecurityAct extends BaseActivity implements IMySetAccountSecurityView
{
    private static final String TAG = MySetAccountSecurityAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.arl_pay_pd_manager)
    AutoRelativeLayout allPayPdManager;
    @BindView(R.id.arl_login_pw_manager)
    AutoRelativeLayout allLoginPwManager;
    @BindView(R.id.arl_bind_alipay)
    AutoRelativeLayout allBindAlipay;
    @BindView(R.id.tv_pay_pw_description)
    TextView tvPayPwState;

    //    private boolean hasSetPayPassword = true;
    private String hasSetPayPassword = "";
    private ImpIMySetAccountSecurityView accountSecurityView;
    private Dialog pDialog;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_account_security);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mContext = this;
        topBar.setTitle("账户安全");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mContext.finish();
            }

            @Override
            public void left2Click(View view)
            {
            }

            @Override
            public void rightClick(View view)
            {
            }
        });
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);
        accountSecurityView = new ImpIMySetAccountSecurityView(mContext,this);
        accountSecurityView.checkPayPasswordState();
    }

    @OnClick({R.id.arl_pay_pd_manager, R.id.arl_login_pw_manager, R.id.arl_bind_alipay})
    void viewClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            //支付密码管理
            case R.id.arl_pay_pd_manager:
                switch (hasSetPayPassword)
                {
                    case "0":
                        Intent intent1 = new Intent(mContext, MySetPayPwManagerAct.class);
                        startActivity(intent1);
                        break;
                    case "1":
                        //验证成功去设置支付密码
                        intent = new Intent(mContext, MySetNewPayPwAct.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra("type", "setPayPw");
                        intent.putExtra(EamConstant.EAM_SET_PAY_PW_OPEN_SOURCE,
                                mContext.getIntent().getStringExtra(EamConstant.EAM_SET_PAY_PW_OPEN_SOURCE));
                        mContext.startActivity(intent);
                        break;
                    case "2":
                        //如果没有设置则跳转到验证身份证信息界面
                        intent = new Intent(mContext, MyVerifyIdAct.class);
                        //表明从什么地方进入的验证页面
                        intent.putExtra(EamConstant.EAM_VERIFY_ID_OPEN_SOURCE, "MyAccountSecurityAct");
                        startActivity(intent);
                        break;
                }

                //开始记录打开的Activity
                EamApplication.getInstance().actStake.clear();
                EamApplication.getInstance().actStake.add(this);
                break;
            //登录密码管理
            case R.id.arl_login_pw_manager:
                Intent intent1 = new Intent(mContext, MyChangeLoginPwAct.class);
                startActivity(intent1);
                //MySetLoginPwManagerAct_.intent(mContext).start();
                break;
            //支付宝管理
            case R.id.arl_bind_alipay:
                Intent intent2 = new Intent(mContext, MySetAccountSecAlipayAct.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
    /**
     * 0 已设置过支付密码  1 已认证没有设置过密码   2 没实名 没设置
     * @param hasSet
     */
    private void setUiContent(String hasSet)
    {
        hasSetPayPassword = hasSet;
        switch (hasSet)
        {
            case "0":
                tvPayPwState.setText("为更加安全，可定期修改支付密码");
                tvPayPwState.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
                break;
            case "1":
                tvPayPwState.setText("您尚未设置支付密码,请尽快设置");
                break;
            case "2":
                tvPayPwState.setText("您尚未设置支付密码,请尽快设置");
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
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
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_payPwd:
                if (code.equals("PAYPWD_NULL"))
                    setUiContent("2");
                else if (code.equals("PAYPWD_NULL_REAL"))
                    setUiContent("1");
                break;
        }
    }

    @Override
    public void checkPayPasswordStateCallback(String response)
    {
        setUiContent("0");
    }
}
