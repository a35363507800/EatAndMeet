package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.LiveAuthenticationPassActivity;
import com.echoesnet.eatandmeet.activities.live.LiveReadyActivity;
import com.echoesnet.eatandmeet.presenters.ImpIIdentityAuthActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IIdentityAuthActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 支付宝一键认证
 * Created by an on 2017/4/13 0013.
 */

public class IdentityAuthAct extends MVPBaseActivity<IIdentityAuthActView, ImpIIdentityAuthActView> implements View.OnClickListener, IIdentityAuthActView
{
    public static final int GO_TO_MY_AUTHENTICATION = 1001;
    private final String TAG = IdentityAuthAct.class.getCanonicalName();

    @BindView(R.id.tv_auth_name)
    TextView authNameTv;
    @BindView(R.id.tv_id_num)
    TextView idNumTv;
    @BindView(R.id.tv_alipay_auth)
    Button alipayAuthTv;
    @BindView(R.id.tv_official_auth)
    Button officalAuthTv;
    @BindView(R.id.top_bar_switch_auth)
    TopBarSwitch authTopBarSwitch;
    @BindView(R.id.ll_name_and_id)
    LinearLayout nameAndIdll;

    private Handler mHandler;
    private String aliPayRealName;//支付宝姓名
    private String aliPayIdCard;//支付宝身份证号
    private String realName, idCard;
    private HashMap<String, String> mData;
    private Activity mActivity;
    private String fromType;
    public String authInfo;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_identity_auth);
        ButterKnife.bind(this);
        mActivity = this;
        fromType = getIntent().getStringExtra("fromType");
        mData = new HashMap<>();
        authTopBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
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
        }).setText("实名认证");
        authTopBarSwitch.setBottomLineVisibility(View.VISIBLE);
        mPresenter.getRealNameState("");
    }

    private void initView()
    {
        alipayAuthTv.setOnClickListener(this);
        officalAuthTv.setOnClickListener(this);
        if (mData != null)
        {
            String alipayFlg = mData.get("alipayFlg");
            String swap = mData.get("swap");
            realName = mData.get("realName");
            idCard = mData.get("idCard");

            if (!TextUtils.isEmpty(realName) && !TextUtils.isEmpty(idCard))
            {
                nameAndIdll.setVisibility(View.VISIBLE);
                authNameTv.setText(realName);
                idNumTv.setText(idCard);
            } else
            {
                nameAndIdll.setVisibility(View.GONE);
            }
            if ("0".equals(swap))
            {
                alipayAuthTv.setVisibility(View.VISIBLE);
            } else
            {
                alipayAuthTv.setVisibility(View.GONE);
            }

            if ("1".equals(alipayFlg))
            {
                alipayAuthTv.setEnabled(false);
                alipayAuthTv.setText("支付宝认证已通过");
                alipayAuthTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.round_cornor_36_c0331_bg));
            } else
            {
                alipayAuthTv.setEnabled(true);
                alipayAuthTv.setText(getResources().getString(R.string.auth_alipay));
                alipayAuthTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.round_btn_c0412_bg));
            }
        }
    }


    @Override
    protected ImpIIdentityAuthActView createPresenter()
    {
        return new ImpIIdentityAuthActView();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_alipay_auth:
                mPresenter.getBuildAuthInfo();
                break;
            case R.id.tv_official_auth:
                String rmAnFlg = mData.get("rmAnFlg");
                if (TextUtils.isEmpty(rmAnFlg))
                {
                    ToastUtils.showShort("出现错误,审核进度丢失");
                    return;
                }
                Intent intent = null;
                switch (rmAnFlg)
                {
                    case "1":
                    case "2":
                    case "3":
                        intent = new Intent(mActivity, ApproveProgressAct.class);
                        break;
                    default:
                        intent = new Intent(mActivity, MyAuthenticationAct.class);
                        ArrayList list = new ArrayList();
                        list.add(mData);
                        intent.putParcelableArrayListExtra("userAuthenticationState", list);
                        intent.putExtra("openSource", "alipay");
                        break;
                }
                startActivityForResult(intent, GO_TO_MY_AUTHENTICATION);
                break;
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_buildAuthInfo:
                break;
            case NetInterfaceConstant.LiveC_alipayValidate:
                if (!TextUtils.isEmpty(code))
                {
                    ToastUtils.showLong(mActivity.getResources().getString(R.string.auth_alipay_fail));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        if (NetInterfaceConstant.LiveC_getReal.equals(exceptSource))
            finish();
    }

    @Override
    public void getBuildAuthInfoSuc(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            authInfo = jsonObject.getString("authInfo");
            // 必须异步调用
            Thread authThread = new Thread(new IdentyAuthRunable(IdentityAuthAct.this));
            authThread.start();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void getAlipayValidateSuc(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            aliPayRealName = jsonObject.getString("realName");
            aliPayIdCard = jsonObject.getString("idCard");
            String aliPayStatus = jsonObject.getString("status");
            if ("1".equals(aliPayStatus))
            {
                ToastUtils.showLong("支付宝认证成功");
                alipayAuthTv.setEnabled(false);
                alipayAuthTv.setText("支付宝认证已通过");
                alipayAuthTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.round_cornor_36_c0331_bg));
                if (!TextUtils.isEmpty(fromType) && fromType.equals("liveReady"))
                {
                    setResult(LiveReadyActivity.TO_IDENTITY_AUTH_OK);
                    finish();
                }
                if (!TextUtils.isEmpty(aliPayIdCard) && !TextUtils.isEmpty(aliPayIdCard))
                {
                    nameAndIdll.setVisibility(View.VISIBLE);
                    authNameTv.setText(aliPayRealName);
                    idNumTv.setText(aliPayIdCard);
                } else
                {
                    nameAndIdll.setVisibility(View.GONE);
                }
            } else
            {
                ToastUtils.showLong(getResources().getString(R.string.auth_alipay_fail));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void getRealNameStateCallBack(String response, String type)
    {
        JSONObject body = null;
        try
        {
            body = new JSONObject(response);
            String realName = body.getString("realName");
            String idCard = body.getString("idCard");
            String idCardUrl = body.getString("idCardUrl");
            String posPhUrl = body.getString("posPhUrl");
            String rmAnFlg = body.getString("rmAnFlg");
            String rejectReason = body.getString("rejectReason");
            String alipayFlg = body.getString("alipayFlg");
            String swap = body.getString("swap");
            if ("2".equals(rmAnFlg) && !"FROM_MY_AUTHENTICATION".equals(type))
            {
                Intent intent = new Intent(mActivity, LiveAuthenticationPassActivity.class);
                intent.putExtra("realName", realName);
                intent.putExtra("idCard", idCard);
                finish();
                startActivity(intent);
            } else
            {
                mData.put("realName", realName);
                mData.put("idCard", idCard);
                mData.put("idCardUrl", idCardUrl);
                mData.put("posPhUrl", posPhUrl);
                mData.put("rmAnFlg", rmAnFlg);
                mData.put("rejectReason", rejectReason);
                mData.put("alipayFlg", alipayFlg);
                mData.put("swap", swap);
                initView();
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    /**
     * 检测是否安装支付宝
     *
     * @param context
     * @return
     */
    private boolean checkAliPayInstalled(Context context)
    {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_TO_MY_AUTHENTICATION)
        {
            mPresenter.getRealNameState("FROM_MY_AUTHENTICATION");
        }
    }

    private static class IdentyAuthRunable implements Runnable
    {
        private final WeakReference<IdentityAuthAct> mActRef;

        public IdentyAuthRunable(IdentityAuthAct identityAuthAct)
        {
            mActRef = new WeakReference<IdentityAuthAct>(identityAuthAct);
        }

        @Override
        public void run()
        {
            IdentityAuthAct identityAuthAct = mActRef.get();
            if (identityAuthAct == null)
                return;
            // 构造AuthTask 对象
            AuthTask authTask = new AuthTask(identityAuthAct);
            // 调用授权接口，获取授权结果
            Map<String, String> result = authTask.authV2(identityAuthAct.authInfo, true);
            String resultStr = result.get("result");
            String[] strs = resultStr.split("&auth_code=|&");
            if (strs.length > 1)
            {
                String code = strs[1];
                identityAuthAct.mPresenter.getAlipayValidate(code);
            }
        }
    }
}
