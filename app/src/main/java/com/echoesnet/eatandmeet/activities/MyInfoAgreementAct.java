package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MyInfoAgreementAct extends BaseActivity
{
    private static final String TAG = MyInfoAgreementAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.tv_agreement)
    TextView tv_agreement;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_myinfo_account_agreement);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {
        mContext = this;

        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                MyInfoAgreementAct.this.finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        }).setText("充值协议");


        getAgreement();
    }

    private void getAgreement()
    {
        OkHttpUtils
                .get()
                .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + "public.json")
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        Logger.t(TAG).d("下载失败");
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("获取协议--> " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("recharge_policy");
                            tv_agreement.setText(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
