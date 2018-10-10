package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.presenters.ImpIMySetVerifyOldPayPwView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetVerifyOldPayPwView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.jungly.gridpasswordview.GridPasswordView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MySetVerifyOldPayPwAct extends BaseActivity implements IMySetVerifyOldPayPwView
{
    private static final String TAG = MySetNewPayPwAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.pswView)
    GridPasswordView gpwView;

    private ImpIMySetVerifyOldPayPwView setVerifyOldPayPwView;
    private Dialog pDialog;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_set_change_pay_pw);
        ButterKnife.bind(this);
        afterViews();
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

    private void afterViews()
    {
        mContext = this;
        topBar.setTitle("验证原支付密码");
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
        setVerifyOldPayPwView = new ImpIMySetVerifyOldPayPwView(mContext, this);
        initViews();
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);
        EamApplication.getInstance().actStake.add(this);
    }

    private void initViews()
    {
        gpwView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                float downX = 0, downY = 0;
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        downX = motionEvent.getX();
                        downY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float upX = motionEvent.getX();
                        float upY = motionEvent.getY();
                        if (Math.abs(downX - upX) < 50 && Math.abs(downY - upY) < 50)
                        {
                            gpwView.performClick();
                        }
                        break;
                }
                return false;
            }

        });
        gpwView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener()
        {
            @Override
            public void onTextChanged(String psw)
            {
            }

            @Override
            public void onInputFinish(String psw)
            {
                gpwView.setGridLineColor(ContextCompat.getColor(mContext, R.color.C0323));
                boolean isSameChars = true;
                char first = psw.charAt(0);
                for (int i = 1; i < psw.length(); i++)
                {
                    if (first != psw.charAt(i))
                    {
                        isSameChars = false;
                        break;
                    }
                    first = psw.charAt(i);
                }
                if (isSameChars)
                {
//                    gpwView.setGridLineColor(ContextCompat.getColor(mContext, R.color.c1));
                    ToastUtils.showShort("支付密码输入错误");
                }
                else
                {
                    if (setVerifyOldPayPwView != null)
                        setVerifyOldPayPwView.verifyPayPassword(psw);
                }
            }
        });
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_validPwd:
                if (ErrorCodeTable.PAYPWD_ERR.equals(code))
                {
                    gpwView.clearPassword();
                    try
                    {
                        JSONObject body = new JSONObject(errBody);
                        if (body.getString("surplus").equals("0"))
                        {
                            ToastUtils.showShort(
                                    String.format("由于您输错的次数过多,支付密码已被锁定,请3小时之后再试!"));
                        }
                        else
                        {



                            ToastUtils.showShort(
                                    String.format("支付密码错误，您还可以输入%s次",
                                            body.getString("most"), body.getString("surplus")));
                        }

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (ErrorCodeTable.REPAY_COUNTDOWN_IS_FINISH.equals(code))
                {
                    ToastUtils.showShort("由于您输错的次数过多，支付密码为锁定状态，3小时后可自动解锁！");
                }
                break;

        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void verifyPayPasswordCallback(String response)
    {
        //验证成功，输入新的支付密码
        Intent intent = new Intent(mContext, MySetNewPayPwAct.class);
        intent.putExtra("type", "reSetPayPw");
        mContext.startActivity(intent);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @OnClick(R.id.pswView)
    public void onViewClicked()
    {
//        gpwView.setGridLineColor(ContextCompat.getColor(mContext, com.jungly.gridpasswordview.R.color.C0311P));
//        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(gpwView, InputMethodManager.SHOW_IMPLICIT);
    }
}
