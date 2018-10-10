package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.presenters.ImpIMySetNewPayPwView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetNewPayPwView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MySetNewPayPwAct extends BaseActivity implements IMySetNewPayPwView
{
    private static final String TAG = MySetNewPayPwAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.pswView)
    GridPasswordView gpwView;
    @BindView(R.id.pswViewConfirm)
    GridPasswordView gpwViewConfirm;
    @BindView(R.id.tv_instruction_1)
    TextView tvInstruction1;
    @BindView(R.id.tv_instruction_2)
    TextView tvInstruction2;
    private String title = "";

    private boolean isValid = false;
    private String openSource = "";

    private boolean firstPwdIsFinished = false;

    private ImpIMySetNewPayPwView setNewPayPwView;

    private boolean hasInputPsw = false;

    private Dialog pDialog;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_set_pay_pw);
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
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                onBackPressed();
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

        setNewPayPwView = new ImpIMySetNewPayPwView(mContext, this);

        String type = getIntent().getStringExtra("type");
        if (type.equals("setPayPw"))
        {
            tvInstruction1.setText("请设置6位支付密码");
            tvInstruction2.setText("请确认6位支付密码");
            title = "设置支付密码";
        }
        else
        {
            tvInstruction1.setText("请设置6位新支付密码");
            tvInstruction2.setText("请确认6位新支付密码");
            title = "修改支付密码";
        }
        topBar.setTitle(title);

        openSource = getIntent().getStringExtra(EamConstant.EAM_SET_PAY_PW_OPEN_SOURCE);
        initViews();
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);
    }

    private void initViews()
    {
        gpwView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener()
        {
            @Override
            public void onTextChanged(String psw)
            {
                if (!TextUtils.isEmpty(psw))
                {
                    hasInputPsw = true;
                    if (psw.length() < 6)
                    {
                        isValid = false;
                    }
                }
                else
                    hasInputPsw = false;

            }

            @Override
            public void onInputFinish(String psw)
            {
                gpwView.setGridLineColor(ContextCompat.getColor(mContext, R.color.C0323));
                //gpwViewConfirm.setInputAbility(true);
                isValid = true;
                if (verifySamePw(psw).equals("same"))
                {
                    ToastUtils.showShort("密码不能为6位相同数字");
                    isValid = false;
                    gpwView.setGridLineColor(R.color.c1);
                    gpwView.clearPassword();
/*                    gpwViewConfirm.setInputAbility(false);
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0,InputMethodManager.HIDE_IMPLICIT_ONLY);*/
                }
                else if (verifyContinuePw(psw).equals("continue"))
                {
                    gpwView.setGridLineColor(ContextCompat.getColor(mContext, R.color.c1));
                    ToastUtils.showShort("密码不能为6位连续数字");
                    isValid = false;
                    gpwView.clearPassword();
                }
                else
                {
                    //代表 输入通过
                    firstPwdIsFinished = true;
                }

//                new Handler().postDelayed(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        gpwView.setGridLineColor(R.color.C0323);
//                    }
//                }, 500);
            }
        });

        gpwViewConfirm.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener()
        {
            @Override
            public void onTextChanged(String psw)
            {
                if (!firstPwdIsFinished)
                {
                    gpwView.setGridLineColor(ContextCompat.getColor(mContext, R.color.c1));
                    gpwViewConfirm.clearPassword();
                    ToastUtils.showShort("请先输入密码！");
                }
//                new Handler().postDelayed(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        gpwView.setGridLineColor(R.color.C0323);
//                    }
//                }, 500);
            }

            @Override
            public void onInputFinish(String psw)
            {
                gpwViewConfirm.setGridLineColor(ContextCompat.getColor(mContext, R.color.C0323));
                if (isValid)
                {
                    if (gpwView.getPassWord().equals(psw))
                    {
                        //afterSetPayPw();
                        //测试用，发布时打开下面的setPayPassword--wb
                        if (setNewPayPwView != null)
                            setNewPayPwView.setPayPassword(psw);
                    }
                    else
                    {
                        ToastUtils.showShort("两次密码输入不一致");
                        gpwView.clearPassword();
                        gpwViewConfirm.clearPassword();
                    }
                }
                else
                {
/*                    gpwViewConfirm.setGridLineColor(ContextCompat.getColor(mContext, R.color.c1));
                    ToastUtils.showShort(mContext,"密码不能为6位相同数字");*/
                }
            }
        });

        gpwView.setOnInputMethodViewChangeListener(new GridPasswordView.OnInputMethodViewChangeListener()
        {
            @Override
            public void onInputMethodShow()
            {
                gpwViewConfirm.setGridLineColor(ContextCompat.getColor(mContext, R.color.C0323));
            }
        });
        gpwViewConfirm.setOnInputMethodViewChangeListener(new GridPasswordView.OnInputMethodViewChangeListener()
        {
            @Override
            public void onInputMethodShow()
            {
                gpwView.setGridLineColor(ContextCompat.getColor(mContext, R.color.C0323));
            }
        });

    }

//    @OnClick({R.id.pswView, R.id.pswViewConfirm})
//    public void onViewClicked(View view)
//    {
//        switch (view.getId())
//        {
//            case R.id.pswView:
////                gpwView.setGridLineColor(ContextCompat.getColor(mContext, R.color.C0311P));
//                gpwViewConfirm.setGridLineColor(ContextCompat.getColor(mContext, R.color.C0323));
//                break;
//            case R.id.pswViewConfirm:
//                gpwView.setGridLineColor(ContextCompat.getColor(mContext, R.color.C0323));
////                gpwViewConfirm.setGridLineColor(ContextCompat.getColor(mContext, R.color.C0311P));
//                break;
//        }
//    }

    /**
     * @param password
     * @return
     */
    private String verifySamePw(String password)
    {
        String errorType = "same";
        Logger.t(TAG).d(">=========" + errorType);
        char first = password.charAt(0);
        for (int i = 1; i < password.length(); i++)
        {
            if (first != password.charAt(i))
            {
                errorType = "notSame";
                break;
            }
            first = password.charAt(i);
        }
        return errorType;
    }

    private String verifyContinuePw(String password)
    {
        String errorType = "continue";
        //是否为连续数字
        char first2 = password.charAt(0);
        if (Integer.parseInt(first2 + "") < 5)
        {
            Logger.t(TAG).d("qian" + first2 + "");
            for (int i = 1; i < password.length(); i++)
            {
                if ((Integer.parseInt(first2 + "") + 1 != Integer.parseInt(password.charAt(i) + "")))
                {
                    errorType = "notContinue";
                    return errorType;
                }
                first2 = password.charAt(i);
            }
        }
        else
        {
            Logger.t(TAG).d("hou" + first2 + "");
            for (int i = 1; i < password.length(); i++)
            {
                if ((Integer.parseInt(first2 + "") - 1 != Integer.parseInt(password.charAt(i) + "")))
                {
                    errorType = "notContinue";
                    return errorType;
                }
                first2 = password.charAt(i);
            }
        }
        return errorType;
    }

    private void startTargetActivity(Activity sourceAct, Intent intent1)
    {
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sourceAct.startActivity(intent1);
    }

    private void afterSetPayPw()
    {
        for (Activity act : EamApplication.getInstance().actStake)
        {
            if (act != null)
            {
                act.finish();
            }
        }
        EamApplication.getInstance().actStake.clear();
        mContext.finish();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void setPayPasswordCallback(String response)
    {
        ToastUtils.showShort("支付密码设置成功");
        afterSetPayPw();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed()
    {
        if (hasInputPsw)
        {
            new CustomAlertDialog(mContext)
                    .builder()
                    .setTitle("提示")
                    .setMsg("退出将不会保留设置的支付密码，是否放弃设置？")
                    .setCancelable(true)
                    .setPositiveButton("确定", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                        }
                    })
                    .show();
        }
        else
            super.onBackPressed();
    }
}
