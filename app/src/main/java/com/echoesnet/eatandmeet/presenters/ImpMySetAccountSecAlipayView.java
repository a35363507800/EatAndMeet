package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetAccountSecAlipayView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/16.
 */

public class ImpMySetAccountSecAlipayView {
    private final String TAG = ImpMySetAccountSecAlipayView.class.getSimpleName();
    private Context mContext;
    private IMySetAccountSecAlipayView iMySetAccountSecAlipayView;

    public ImpMySetAccountSecAlipayView(Context mContext, IMySetAccountSecAlipayView iMySetAccountSecAlipayView) {
        this.mContext = mContext;
        this.iMySetAccountSecAlipayView = iMySetAccountSecAlipayView;
    }

    /**
     * 校验支付宝是否绑定
     */
    public void checkAlipayState()
    {
        Map<String,String> reqParamMap=NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iMySetAccountSecAlipayView!=null)
                    iMySetAccountSecAlipayView.checkAlipayStateCallback(response);
            }
        },NetInterfaceConstant.UserC_checkAlipay,reqParamMap);
    }

    /**
     * 绑定支付宝
     */
    public void bindAlipay(String alipayAccount)
    {
        Map<String,String> reqParamMap=NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.alipayId,alipayAccount);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iMySetAccountSecAlipayView!=null)
                    iMySetAccountSecAlipayView.bindAlipayCallback(response);
            }
        },NetInterfaceConstant.UserC_alipay,reqParamMap);
    }

    /**
     * 绑定支付宝
     */
    /*private void bindAlipay(String alipayAccount)
    {
        if (!mContext.isFinishing()&&pDialog!=null&&!pDialog.isShowing())
            pDialog.show();
        Map<String,String> reqParamMap=NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.alipayId,alipayAccount);

        Logger.t(TAG).d("提交的的josn"+ new Gson().toJson(reqParamMap));

        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("UserC/alipay",new Gson().toJson(reqParamMap)))
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
                        Logger.t(TAG).json(response);
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status=jsonResponse.getInt("status");
                            if (status==0)
                            {
                                ToastUtils.showShort(mContext,"支付宝绑定成功");
                                mContext.finish();
                            }
                            else if (status==1)
                            {
                                String code=jsonResponse.getString("code");
                                if (!ErrorCodeTable.handleErrorCode(code,mContext))
                                    ToastUtils.showShort(mContext, ErrorCodeTable.parseErrorCode(code));
                                Logger.t(TAG).d("错误码为：%s",code);
                            }
                        } catch (JSONException e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                            e.printStackTrace();
                        }
                        finally
                        {
                            if(pDialog!=null&&pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }
                });
    }*/

    /**
     * 校验支付宝是否绑定
     */
    /*private void checkAlipayState()
    {
        pDialog.show();
        Map<String,String> reqParamMap=NetHelper.getCommonPartOfParam(mContext);
        Logger.t(TAG).d("提交的的josn"+ new Gson().toJson(reqParamMap));

        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("UserC/checkAlipay",new Gson().toJson(reqParamMap)))
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mContext,null,TAG,e);
                        if(pDialog!=null&&pDialog.isShowing())
                            pDialog.dismiss();
                    }
                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).json(response);
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status=jsonResponse.getInt("status");
                            if (status==0)
                            {
                                JSONObject body=new JSONObject(jsonResponse.getString("body"));
                                String flag=body.getString("flag");
                                fetAlipayAccount.setText(flag.equals("0")?"":flag);
                            }
                            else if (status==1)
                            {
                                String code=jsonResponse.getString("code");
                                ToastUtils.showShort(mContext, ErrorCodeTable.parseErrorCode(code));
                                Logger.t(TAG).d("错误码为：%s",code);
                            }
                        } catch (JSONException e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                            e.printStackTrace();
                        }
                        finally
                        {
                            if(pDialog!=null&&pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }
                });
    }*/
}
