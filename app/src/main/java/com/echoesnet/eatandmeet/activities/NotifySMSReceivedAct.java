package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;

public class NotifySMSReceivedAct extends BaseActivity
{
    private Button btnOk;
    private Activity mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_notify_smsreceived);
        mContext=this;
        btnOk= (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HuanXinIMHelper.getInstance().openLoginPageAfterQuite(mContext);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return true;
    }
}
