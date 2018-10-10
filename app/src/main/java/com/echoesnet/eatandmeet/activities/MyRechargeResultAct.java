package com.echoesnet.eatandmeet.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.MyInfoBuyFaceEggActivity;
import com.echoesnet.eatandmeet.presenters.ImpIRechargeResultView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IRechargeResultView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MyRechargeResultAct extends BaseActivity implements IRechargeResultView
{
    public static final String TAG = MyRechargeResultAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.btn_balance)
    Button btnBalance;
    private String openThisSource;
    private String faceEgg;

    private ImpIRechargeResultView mRechargeResultView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_account_recharge_layout);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {
        mRechargeResultView=new ImpIRechargeResultView(this,this);


       TextView titletv= topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                MyRechargeResultAct.this.finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        });
        List<Map<String,TextView>> navBtns;
        navBtns = topBar.getNavBtns2(new int[]{1, 0, 0, 0});
        navBtns.get(0).get(TopBarSwitch.NAV_BTN_ICON).setVisibility(View.GONE);

        faceEgg = getIntent().getStringExtra("faceEgg");
        if (TextUtils.isEmpty(faceEgg))
        {
            titletv.setText(getResources().getString(R.string.go_recharge_title));
        }
        else
        {
            titletv.setText(getResources().getString(R.string.go_recharge_face_title));
        }


        openThisSource = getIntent().getStringExtra(EamConstant.EAM_RECHARGE_RESULT_OPEN_SOURCE);
        mRechargeResultView.getAccountBalance();
    }


    @OnClick({R.id.btn_balance})
    void click(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_balance:
                if (openThisSource != null && openThisSource.equals(EamConstant.EAM_RECHARGE_RESULT_OPEN_SOURCE))
                    finish();
                else if (!TextUtils.isEmpty(faceEgg))
                {
                    startActivity(new Intent(this,MyInfoBuyFaceEggActivity.class));
                    finish();
                }
                else
                {
                    Intent intent = new Intent(this,MyInfoAccountAct2.class);
                    this.startActivity(intent);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent intent = new Intent(this,MyInfoAccountAct2.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(MyRechargeResultAct.this, null, exceptSource, e);
    }

    @Override
    public void getAccountBalanceCallback(String str)
    {
        try
        {
            JSONObject jsonBody = new JSONObject(str);

            //发送广播，通知直播账户相关余额已经改变
            Intent intent = new Intent(EamConstant.ACTION_UPDATE_USER_BALANCE);
            intent.putExtra("needRefreshAccountInfo", true);
            sendBroadcast(intent);

            //刷新个人信息
            Intent userInfoIntent = new Intent(EamConstant.ACTION_UPDATE_USER_INFO);
            userInfoIntent.putExtra("needRefreshUserInfo", true);
            sendBroadcast(userInfoIntent);

            //发送广播，通知直播账户相关余额已经改变
            Intent intent1 = new Intent(EamConstant.ACTION_UPDATE_BALANCE);
            intent1.putExtra("meal", jsonBody.getString("meal"));
            intent1.putExtra("face", jsonBody.getString("face"));
            intent1.putExtra("balance", jsonBody.getString("balance"));
            sendBroadcast(intent1);

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
