package com.echoesnet.eatandmeet;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

/**
 * Copyright (C) 2018 科技发展有限公司
 * 完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2018/1/8 16:08
 * @description
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class InterfaceTest
{
    @Test
    public void getVersionCode() throws Exception
    {
/*        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.token, "dfd5ef32142a42e0ae72f8e243cc5f85");
        reqParamMap.put(ConstCodeTable.deviceId,"862155032201575");
        reqParamMap.put(ConstCodeTable.uId, "35ce1f5a-c399-4937-98e3-bdc257ba94bd");
        reqParamMap.put(ConstCodeTable.version, "416");*/
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(InstrumentationRegistry.getTargetContext());
        reqParamMap.put(ConstCodeTable.version, CommonUtils.getVerCode(InstrumentationRegistry.getTargetContext()) + "");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Assert.assertEquals(response.getStatus(),"0");
                //Logger.t("TEST").d(response.toString());
            }
        }, NetInterfaceConstant.UserC_version_v304, null, reqParamMap);
    }
}
