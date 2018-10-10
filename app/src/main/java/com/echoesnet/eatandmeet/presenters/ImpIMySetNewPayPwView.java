package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetNewPayPwView;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/6.
 */

public class ImpIMySetNewPayPwView
{
    private final String TAG = ImpIMySetNewPayPwView.class.getSimpleName();
    private Context mContext;
    private IMySetNewPayPwView iMySetNewPayPwView;

    public ImpIMySetNewPayPwView(Context mContext, IMySetNewPayPwView iMySetNewPayPwView)
    {
        this.mContext = mContext;
        this.iMySetNewPayPwView = iMySetNewPayPwView;
    }

    /**
     * 设置支付密码
     */
    public void setPayPassword(String payPw)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        try
        {
            reqParamMap.put(ConstCodeTable.npp, EncryptSHA1.SHA1(payPw));
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(iMySetNewPayPwView!=null)
                    iMySetNewPayPwView.setPayPasswordCallback(response);
            }
        },NetInterfaceConstant.user_repayPwd,reqParamMap);
    }
}
