package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.zhy.autolayout.AutoLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MySetLoginPwManagerAct extends BaseActivity
{
    private static final String TAG = MySetLoginPwManagerAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.all_my_change_pw)
    AutoLinearLayout allMyChangePw;
    @BindView(R.id.all_my_forget_pw)
    AutoLinearLayout allMyForgetPw;

    private Dialog pDialog;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_set_login_pw);
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
        topBar.setTitle("管理登录密码");
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
    }

    @OnClick({R.id.all_my_change_pw, R.id.all_my_forget_pw})
    void viewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.all_my_change_pw:
                startActivity(new Intent(mContext, MyChangeLoginPwAct.class));
                break;
            case R.id.all_my_forget_pw:
                startActivity(new Intent(mContext, ForgetPasswordAct.class));
                break;
            default:
                break;
        }
    }

}
