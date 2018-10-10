package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.CSayHelloFr;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CResAccostAct extends BaseActivity
{
    //region 变量
    private static final String TAG=CResAccostAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.atl_caccost_container)
    RelativeLayout rlContainer;
    private CSayHelloFr mapFrg;
    private Activity mContext;
    private Dialog pDialog;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cres_accost);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {
        mContext=this;
        topBar.setTitle("餐厅邂逅");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.getLeftButton().setVisibility(View.VISIBLE);

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
        pDialog= DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);
        pDialog.show();
        mapFrg=CSayHelloFr.newInstance("","");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.atl_caccost_container, mapFrg)
                .show(mapFrg)
                .commit();
        pDialog.dismiss();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(pDialog!=null&&pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog=null;
        }
    }
}
