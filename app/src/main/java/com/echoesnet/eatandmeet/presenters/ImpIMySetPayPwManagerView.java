package com.echoesnet.eatandmeet.presenters;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetPayPwManagerView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;

/**
 * Created by Administrator on 2017/1/6.
 */

public class ImpIMySetPayPwManagerView
{
    private final String TAG = ImpIMySetPayPwManagerView.class.getSimpleName();
    private Context mContext;
    private IMySetPayPwManagerView setPayPwManagerView;

    public ImpIMySetPayPwManagerView(Context mContext, IMySetPayPwManagerView setPayPwManagerView)
    {
        this.mContext = mContext;
        this.setPayPwManagerView = setPayPwManagerView;
    }

    //验证验证码
    public void validSecurityCode(final String mobile, String code, final Dialog dialog, String type)
    {
        ArrayMap<String, String> reqParamMap = new ArrayMap<>();
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        reqParamMap.put(ConstCodeTable.code,code);
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(mContext));
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(mContext));
        reqParamMap.put(ConstCodeTable.type,type);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ArrayMap<String,Object> map = new ArrayMap<String, Object>();
                map.put("dialog",dialog);
                map.put("response",response);
                if(setPayPwManagerView!=null)
                    setPayPwManagerView.validSecurityCodeCallback(map);
            }
        },NetInterfaceConstant.UserC_validCodes,reqParamMap);
    }
}
