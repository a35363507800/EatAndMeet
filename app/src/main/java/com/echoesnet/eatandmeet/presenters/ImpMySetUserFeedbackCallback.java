package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetUserFeedbackCallback;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19.
 */

public class ImpMySetUserFeedbackCallback {
    private final String TAG = ImpMySetUserFeedbackCallback.class.getSimpleName();
    private Context mContext;
    private IMySetUserFeedbackCallback iMySetUserFeedbackCallback;

    public ImpMySetUserFeedbackCallback(Context mContext, IMySetUserFeedbackCallback iMySetUserFeedbackCallback) {
        this.mContext = mContext;
        this.iMySetUserFeedbackCallback = iMySetUserFeedbackCallback;
    }

    public void commitFeedback(Activity context, String feedBackContent)
    {
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(context));
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(context));
        reqParamMap.put(ConstCodeTable.uId, SharePreUtils.getUId(context));
        reqParamMap.put(ConstCodeTable.fdk, feedBackContent);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iMySetUserFeedbackCallback!=null)
                    iMySetUserFeedbackCallback.commitFeedbackCallback(response);
            }
        },NetInterfaceConstant.UserC_fbInfo,reqParamMap);
    }

    /**
     * 意见反馈
     * @param context
     */
    /*private void commitFeedback(Activity context, String feedBackContent)
    {
        context.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (!mContext.isFinishing()&&pDialog!=null&&!pDialog.isShowing())
                    pDialog.show();
            }
        });

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.fdk, feedBackContent);

        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("UserC/fbInfo", new Gson().toJson(reqParamMap)))
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mContext, null,TAG,e);
                        if(pDialog!=null&&pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("获得的结果：" + response);
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("status");
                            if (status == 0)
                            {
                                ToastUtils.showShort(mContext,"衷心感谢您的反馈，您的青睐就是我们奋发的动力！");
                                mContext.finish();
                            }
                            else if (status == 1)
                            {
                                String code = jsonResponse.getString("code");
                                if (!ErrorCodeTable.handleErrorCode(code,mContext))
                                    ToastUtils.showShort(mContext, ErrorCodeTable.parseErrorCode(code));
                                Logger.t(TAG).d("错误码为：%s", code);
                            }
                        } catch (JSONException e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                            e.printStackTrace();
                        } finally
                        {
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }
                });
    }*/
}
