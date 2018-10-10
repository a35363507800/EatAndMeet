package com.echoesnet.eatandmeet.activities.live;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.BaseActivity;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;


import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveAuthenticationPassActivity extends BaseActivity
{
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.tv_pass_realName)
    TextView tvRealName;
    @BindView(R.id.tv_pass_idCard)
    TextView tvIdCard;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_auth_info)
    LinearLayout authInfoll;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_authentication_pass_activity);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {

        topBar = (TopBarSwitch) findViewById(R.id.top_bar);
        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText(getResources().getString(R.string.my_realName));
        topBar.setBackground(ContextCompat.getDrawable(this,R.drawable.C0321));
        topBar.getNavBtns(new int[]{1,0,0,0});


        String realName = getIntent().getStringExtra("realName");
        String idCard = getIntent().getStringExtra("idCard");
        if (!TextUtils.isEmpty(realName) && !TextUtils.isEmpty(idCard)){
            authInfoll.setVisibility(View.VISIBLE);
            tvRealName.setText(realName);
            tvIdCard.setText(idCard);
        }else {
            tvTitle.setText(getResources().getString(R.string.has_auth));
            authInfoll.setVisibility(View.GONE);
        }
    }
}
