package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;


import butterknife.BindView;
import butterknife.ButterKnife;

public class ComnCaptureResultShowAct extends BaseActivity
{
    private final static String TAG=ComnCaptureResultShowAct.class.getSimpleName();
    private Activity mAct;
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.tv_qrcode_content)
    TextView tvQRcontentShow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_comn_capture_result_show);
        ButterKnife.bind(this);
        afterView();
    }

    private void afterView()
    {
        mAct=this;
        topBar.setTitle("二维码内容");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.getLeftButton().setVisibility(View.VISIBLE);
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
        tvQRcontentShow.setText(getIntent().getStringExtra("content"));
    }
}
