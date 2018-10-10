package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMWalletPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.MWalletActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import android.content.Context;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */

public class ImplMWalletPre extends BasePresenter<MWalletActView> implements IMWalletPre
{
    private Context mContext;

    public ImplMWalletPre(Context context)
    {
        this.mContext = context;
    }
    @Override
    public void getWalletInfo()
    {

            Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(isViewAttached())
                    getView().getBalanceCallBack(response);
            }
        }, NetInterfaceConstant.UserC_newBalance,reqParamMap);
    }
}
