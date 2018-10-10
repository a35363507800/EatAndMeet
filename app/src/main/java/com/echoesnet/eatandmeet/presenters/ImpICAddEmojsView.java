package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.CAddEmojsAct;
import com.echoesnet.eatandmeet.models.bean.CAddEmojBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICAddEmojsView;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpICAddEmojsView extends BasePresenter<CAddEmojsAct>
{
    private static final String TAG=ImpICAddEmojsView.class.getSimpleName();
    private ICAddEmojsView mICAddEmojView;
    private Context mContext;

    public ImpICAddEmojsView(Context context,ICAddEmojsView mICAddEmojView)
    {
        this.mContext=context;
        this.mICAddEmojView =mICAddEmojView;
    }


    public void getEmojDataLst()
    {
        Logger.t(TAG).d("请求参数为》" + CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + "emoji.json");
        OkHttpUtils.get()
                .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + "emoji.json")
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        if (mICAddEmojView!=null)
                        {
                            mICAddEmojView.requestNetError(call,e,TAG + CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder  + "emoji.json");
                        }
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("json文件："+response);
                        parseResInfoFromJson(response);
                    }
                });
    }
    private void parseResInfoFromJson(String jsonStr)
    {
        try
        {
            List<CAddEmojBean> tempLst=new Gson().fromJson(jsonStr,new TypeToken<List<CAddEmojBean>>(){}.getType());
            if (mICAddEmojView!=null)
                mICAddEmojView.getEmojDataLstCallback(tempLst);
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }
    }
}
