package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhy.autolayout.AutoLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DQuickPayResultAct extends BaseActivity
{
    private static final String TAG = DFlashPayInputAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.btn_check_order)
    Button btnCheckOrder;
    @BindView(R.id.tv_resname)
    TextView tvResName;
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R.id.tv_consume_amount)
    TextView tvConsumeAmount;
    @BindView(R.id.tv_mobile_num)
    TextView tvMobileNum;
    @BindView(R.id.tv_consume_time)
    TextView tvConsumeTime;
    @BindView(R.id.host_userHeader)
    RoundedImageView hostUserHeader;
    @BindView(R.id.host_userName)
    TextView hostUserName;
    @BindView(R.id.tui_anchorLayout)
    AutoLinearLayout tuiAnchorLayout;

    private String orderId;
    private Dialog pDialog;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dquick_pay_result);
        ButterKnife.bind(this);
        afterViews();
    }


    void afterViews()
    {
        mContext = this;
        topBar.setTitle("支付结果");
        topBar.getLeftButton().setVisibility(View.GONE);
        topBar.getRightButton().setVisibility(View.VISIBLE);
        topBar.getRightButton().setText("完成");
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
                mContext.finish();
            }
        });
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(false);
        setUiContent(getIntent());
    }


    @OnClick ({R.id.btn_check_order})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            //查看订单
            case R.id.btn_check_order:
                Intent intent = new Intent(mContext,DQuickPayOrderDetailAct.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                mContext.finish();
                break;
        }
    }

    private void setUiContent(Intent intent)
    {
        orderId = intent.getStringExtra("orderId");
        tvResName.setText(intent.getStringExtra("resName"));
        tvConsumeAmount.setText(String.format("￥%s", intent.getStringExtra("amount")));
        tvConsumeTime.setText(intent.getStringExtra("time"));
        tvOrderId.setText(intent.getStringExtra("orderId"));
        tvMobileNum.setText(intent.getStringExtra("mobile"));

        if (TextUtils.isEmpty(intent.getStringExtra("userName")))
        {
            tuiAnchorLayout.setVisibility(View.GONE);
        }
        else
        {
            hostUserName.setText(intent.getStringExtra("userName"));
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(intent.getStringExtra("uphUrl"))
                    .centerCrop()
                    .placeholder(R.drawable.userhead)
                    .into(hostUserHeader);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
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

}
