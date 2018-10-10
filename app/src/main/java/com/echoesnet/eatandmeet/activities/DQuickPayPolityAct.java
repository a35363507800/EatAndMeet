package com.echoesnet.eatandmeet.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class DQuickPayPolityAct extends BaseActivity
{
    private final String TAG = DQuickPayPolityAct.class.getSimpleName();
    @BindView(R.id.tv_refund_policy)
    TextView tvQuickPayPolicy;
    @BindView(R.id.btn_refund_close)
    Button btnQuickPayClose;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dquick_pay_polity);
        ButterKnife.bind(this);
        afterViews();
    }

    void afterViews()
    {
        mContext = this;
        getResDataOnCdn();
    }

    @OnClick({R.id.btn_refund_close})
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
            File file = new File(NetHelper.getRootDirPath(mContext) + NetHelper.DATA_FOLDER, "public.json");
            if (file.exists() && !file.isDirectory())
            {
                parseResInfoFromJson(file);
            }
            else
            {
                OkHttpUtils.get()
                        .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + "public.json")
                        .build()
                        .execute(new FileCallBack(NetHelper.getRootDirPath(mContext)+ NetHelper.DATA_FOLDER, "public.json")
                        {
                            @Override
                            public void onError(Call call, Exception e)
                            {
                                Logger.t(TAG).d(e.getMessage());
                            }

                            @Override
                            public void onResponse(File response)
                            {
                                parseResInfoFromJson(response);
                            }

                            @Override
                            public void inProgress(float progress, long total)
                            {

                            }
                        });
            }
        } catch (Exception e)
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
            tvQuickPayPolicy.setText(object.getString("quick_pay_policy"));
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }
    }

}
