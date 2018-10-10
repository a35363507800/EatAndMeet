package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/10/26 0026
 * @description
 */
public class RedPacketShowAct extends Activity
{

    @BindView(R.id.tv_red_packet_des)
    TextView redPacketDesTv;
    @BindView(R.id.tv_total_income)
    TextView totalIncomeTv;
    @BindView(R.id.tv_close)
    TextView closeTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.dialog_act_red_packet);
        ButterKnife.bind(this);
        redPacketDesTv.setText(getIntent().getStringExtra("content"));
        totalIncomeTv.setText(String.format("当前累计收益: %s元",getIntent().getStringExtra("income")));
    }

    @OnClick({R.id.tv_close})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_close:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed()
    {

    }
}
