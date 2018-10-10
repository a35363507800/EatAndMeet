package com.echoesnet.eatandmeet.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class DRefundPolicyAct extends BaseActivity
{
    private final String TAG=DRefundPolicyAct.class.getSimpleName();
    @BindView(R.id.tv_refund_policy)
    TextView tvRefundPolicy;
    @BindView(R.id.btn_refund_close)
    Button btnRefundClose;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_drefund_policy);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mContext=this;
        getResDataOnCdn();
    }
    @OnClick ({R.id.btn_refund_close})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_refund_close:
                this.finish();
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
                break;
            default:
                break;
        }
    }
    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim)
    {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    private void getResDataOnCdn()
    {
        try
        {
            //暂时加快速度，以后需要根据后台数据判断是否有更新
/*            File file=new File(NetHelper.getRootDirPath(mContext),"public.json");
            if (file.exists()&&!file.isDirectory())
            {
                parseResInfoFromJson(file);
            }
            else*/
            {
                OkHttpUtils.get()
                        .url(CdnHelper.CDN_ORIGINAL_SITE +CdnHelper.fileFolder+"public.json")
                        .build()
                        .execute(new StringCallback()
                        {
                            @Override
                            public void onError(Call call, Exception e)
                            {
                                ToastUtils.showShort("读取失败");
                            }

                            @Override
                            public void onResponse(String response)
                            {
                                try
                                {
                                    JSONObject object = new JSONObject(response);
                                    tvRefundPolicy.setText(object.getString("refund_policy"));
                                } catch (JSONException e)
                                {
                                    Logger.t(TAG).d(e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void parseResInfoFromJson(File jsonFile)
    {
        String detailResult = CommonUtils.getJsonFromFile(jsonFile);
        try
        {
            JSONObject object = new JSONObject(detailResult);
            tvRefundPolicy.setText(object.getString("refund_policy"));
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }
    }


}
