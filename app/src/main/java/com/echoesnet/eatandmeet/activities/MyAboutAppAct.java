package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAboutAppAct extends BaseActivity
{
    private static final String TAG=MySettingAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.tv_version)
    TextView tvAppVersion;
    @BindView(R.id.tv_copyright)
    TextView tvCopyright;

    private Dialog pDialog;
    private Activity mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_about_app);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mContext=this;
        topBar.setTitle("看脸吃饭");
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
        pDialog= DialogUtil.getCommonDialog(mContext,"正在处理");
        pDialog.setCancelable(false);

        tvAppVersion.setText(String.format("V%s", CommonUtils.getVerName(mContext)));
        tvCopyright.setText("Copyright © 2016 在线回声");
    }
}
