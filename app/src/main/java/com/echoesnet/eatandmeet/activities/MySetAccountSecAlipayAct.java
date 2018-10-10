package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpMySetAccountSecAlipayView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetAccountSecAlipayView;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MySetAccountSecAlipayAct extends BaseActivity implements IMySetAccountSecAlipayView
{
    private static final String TAG = MySettingAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.tv_alipay_account)
    EditText fetAlipayAccount;
    @BindView(R.id.btn_bind)
    Button btnBindAliPay;

    private Dialog pDialog;
    private Activity mContext;
    private ImpMySetAccountSecAlipayView impMySetAccountSecAlipayView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_set_account_sec_alipay);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mContext = this;
        topBar.setTitle("绑定支付宝");
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
        impMySetAccountSecAlipayView = new ImpMySetAccountSecAlipayView(mContext, this);
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);

        if (impMySetAccountSecAlipayView != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            impMySetAccountSecAlipayView.checkAlipayState();
        }

    }

    @OnClick({R.id.btn_bind})
    void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_bind:
                String alipayAccount = fetAlipayAccount.getText().toString();
                if (TextUtils.isEmpty(alipayAccount))
                {
                    ToastUtils.showShort( "支付宝账号不能为空");
                }
                else
                {
                    if (impMySetAccountSecAlipayView != null)
                    {
                        if (pDialog != null && !pDialog.isShowing())
                            pDialog.show();
                        impMySetAccountSecAlipayView.bindAlipay(alipayAccount);
                    }
                }
                break;
            default:
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
    public void checkAlipayStateCallback(String response)
    {
        Logger.t(TAG).json(response);
        if (response == null)
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            ToastUtils.showShort("获取信息失败");
        }
        else
        {
            try
            {
                JSONObject body = new JSONObject(response);
                String flag = body.getString("flag");
                fetAlipayAccount.setText(flag.equals("0") ? "" : flag);
            } catch (JSONException e)
            {
                Logger.t(TAG).d(e.getMessage());
                e.printStackTrace();
            } finally
            {
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        }

    }

    @Override
    public void bindAlipayCallback(String response)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        ToastUtils.showShort("支付宝绑定成功");
        mContext.finish();
    }
}
