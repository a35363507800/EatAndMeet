package com.echoesnet.eatandmeet.activities.liveplay.View;

import android.os.Bundle;

import com.echoesnet.eatandmeet.activities.BaseActivity;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.LiveBasePresenter;
import com.orhanobut.logger.Logger;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author yang
 * @version 1.0
 * @modifier ben
 * @createDate 2017/01/04
 * @description 此为View大基层，完成一些初始化工作，及接口性的规范--wb
 * LiveBaseAct->LiveAct1->LiveRoomAct1->LivePlayAct1
 */
public abstract class LiveBaseAct<P extends LiveBasePresenter> extends BaseActivity
{
    private static final String TAG = LiveBaseAct.class.getSimpleName();
    protected P mPresenter; // 持有P的对象,此处设计为大基类，所以变量设计为protected 就OK了，不用使用public 方法暴露--wb

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView(this);
        Logger.t(TAG).d("LiveAonCreate" + "mPresenter>>>" + mPresenter);
    }

    // P 层的生命周期跟 Act绑定
    @Override
    protected void onStart()
    {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.onDestroy();
        mPresenter=null;
    }

    private P createPresenter()
    {
        Class genericClass = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        try
        {
            return (P) genericClass.newInstance();
        } catch (InstantiationException e)
        {
            Logger.t(TAG).d(e.getMessage());
        } catch (IllegalAccessException e)
        {
            Logger.t(TAG).d(e.getMessage());
        }
        return null;
    }


    protected void callServerSilence(final String interfaceName, Map<String, Object> transElement, String isSync, Map<String, String> reqParamMap)
    {
        mPresenter.callServerSilence(interfaceName, transElement, isSync, reqParamMap);
    }

    protected void callServerWithProgress(final String evt, Map<String, Object> transElement, String isSync,
                                                              String desc, boolean couldCancel, Map<String, String> reqParamMap)
    {
        mPresenter.callServerWithProgress(evt, transElement, isSync, desc, couldCancel, reqParamMap);
    }


    public abstract void onServerSuccessCallback(String evt, Map<String, Object> transElement, String body);

    /**
     *
     * @param evt
     * @param errorCode
     * @param errorBody
     * @return 使用拦截处理，在对应act处理错误码时返回true会中断SilenceSubscriber2抽象类的onError方法执行有些错误码不希望弹Toast   ---lzy
     */
    public abstract void onServerFailedCallback(String evt, String errorCode, String errorBody);

    public abstract void onNetFailedCallback(String evt);


}
