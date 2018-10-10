package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.MyInfoBuyFaceEggActivity;
import com.echoesnet.eatandmeet.presenters.ImplMWalletPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.MWalletActView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/14.
 */

public class MWalletAct extends MVPBaseActivity<MWalletActView,ImplMWalletPre> implements MWalletActView
{
    private static final String TAG = MWhoSeenMeAct.class.getSimpleName();
    private Activity mAct;
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.wallet_balance)
    TextView tvBalance;
    @BindView(R.id.tv_wallet_face)
    TextView tvFace;
    @BindView(R.id.tv_wallet_reap)
    TextView tvReap;


    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        mAct=this;
        setContentView(R.layout.act_mwallet);
        ButterKnife.bind(this);

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
//                Intent intent = new Intent(mAct, MyBalanceDetailAct.class);
//                startActivity(intent);
            }
        }).setText("钱包");

        mPresenter.getWalletInfo();
    }

    @OnClick({R.id.rl_wallet_face,R.id.rl_wallet_reap,R.id.rl_balance_layout})
    void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.rl_wallet_face:
                startActivity(new Intent(mAct, MyInfoBuyFaceEggActivity.class));
                break;
            case R.id.rl_wallet_reap:
                startActivity(new Intent(mAct, MyExchangeMoneyActivity.class));
                break;
            case R.id.rl_balance_layout:
                Intent intent10 = new Intent(mAct, MyInfoAccountAct2.class);
                startActivity(intent10);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected ImplMWalletPre createPresenter()
    {
        return new ImplMWalletPre(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mPresenter.getWalletInfo();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {

    }

    @Override
    public void getBalanceCallBack(String resultJsonStr)
    {
        Logger.t(TAG).d("========钱包页面信息返回结果==============="+resultJsonStr);

        try
        {
            JSONObject ob =new JSONObject(resultJsonStr);
            String balance= ob.getString("balance");
            String meal= ob.getString("meal");
            String face= ob.getString("face");
            tvBalance.setText(balance);
            tvFace.setText(face);
            tvReap.setText(meal);
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("========钱包页面信息解析错误==============="+resultJsonStr);
        }
    }
}
