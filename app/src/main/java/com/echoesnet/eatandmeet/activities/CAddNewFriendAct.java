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
import com.zhy.autolayout.AutoRelativeLayout;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通过手机联系人或者搭讪来添加好友
 */
public class CAddNewFriendAct extends BaseActivity
{
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.arl_add_res_accost_friend)
    AutoRelativeLayout allAddResAccostFriend;
    @BindView(R.id.arl_add_contact_friend)
    AutoRelativeLayout allAddContactFriend;

    private Activity mAct;
    private Dialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cadd_new_friend);
        ButterKnife.bind(this);
        afterViews();
    }
    private void afterViews()
    {
        mAct =this;
        topBar.setTitle("添加好友");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mAct.finish();
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
        pDialog= DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);
    }

    @OnClick({R.id.arl_add_res_accost_friend,R.id.arl_add_contact_friend})
    void buttonClick(View view)
    {
        switch (view.getId())
        {
            //通过地图添加
            case R.id.arl_add_res_accost_friend:
                Intent intent=new Intent(mAct, CResAccostAct.class);
                startActivity(intent);
                break;
            //通过手机号添加
            case R.id.arl_add_contact_friend:
                Intent intent2=new Intent(mAct, CPhoneContactAct.class);
                startActivity(intent2);
                break;
            default:
                break;

        }
    }
}
