
package com.echoesnet.eatandmeet.activities.liveplay.Presenter;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.View.LiveBaseAct;
import com.echoesnet.eatandmeet.controllers.subscribers.InterceptorOnErrorListener;
import com.echoesnet.eatandmeet.controllers.subscribers.ProgressSubscriber2;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.TencentIMHttpResult;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.orhanobut.logger.Logger;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author       ben
 * @createDate   20170321
 * @version      1.0
 * @description  presenter的基类,此类充当了接口的角色
 */
public abstract class LiveBasePresenter<A extends LiveBaseAct, R extends LiveRecord>
{
    private static final String TAG = LiveBasePresenter.class.getSimpleName();
    protected Reference<A> mViewRef;

    protected R mRecord;      // 模型
    protected A mActivity;    // 视图

    public void attachView(A view)
    {
        mViewRef = new WeakReference<A>(view);
        mActivity = getView();
        mRecord = createRecord();  // P层new出M层，持有其对象
        this.onCreate();
    }

    public A getView()
    {
        if (mViewRef==null)
            return null;
        return mViewRef.get();
    }

    private void detachView()
    {
        if (mViewRef != null)
        {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    private R createRecord()
    {
        Class genericClass = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        try
        {
            return (R) genericClass.newInstance();
        } catch (InstantiationException e)
        {
            Logger.t(TAG).d(e.getMessage());
        } catch (IllegalAccessException e)
        {
            Logger.t(TAG).d(e.getMessage());
        }
        return null;
    }

    /**
     * 当网络请求回来，执行callback方法。触发 V层，P层的对应实现接口。<br>
     * <p>
     * <b color="#ff0000">（注释的list有用，是循环触发内部Fragment 实现的接口）</b>
     *
     * @param evt
     * @param transElement
     * @param body
     */
    private void onServerCallbackInner(String evt, Map<String, Object> transElement, String body)
    {
        if (mActivity != null)
        {
            mActivity.onServerSuccessCallback(evt, transElement, body);
//            List<Fragment> fragments = mActivity.getSupportFragmentManager().getFragments();
//            if (fragments != null) {
//                for (int i = 0; i < fragments.size(); i++) {
//                    Fragment frag = fragments.get(i);
//                    if (frag != null && frag instanceof BaseFragment){
//                        BaseFragment baseFragment = (BaseFragment) frag;
//                        baseFragment.presenter().onServerSuccessCallback(evt, data);
//                        baseFragment.onServerSuccessCallback(evt, data);
//                    }
//                }
//            }
        }
    }

    /**

     *
     * @param interfaceName
     * @param errorCode
     * @param body
     * @return
     *
     */
    private void onServerFailedCallbackInner(String interfaceName, String errorCode, String body)
    {
        if (mActivity != null)
        {
            mActivity.onServerFailedCallback(interfaceName, errorCode, body);
//            List<Fragment> fragments = mActivity.getSupportFragmentManager().getFragments();
//            if (fragments != null) {
//                for (int i = 0; i < fragments.size(); i++) {
//                    Fragment frag = fragments.get(i);
//                    if (frag != null && frag instanceof BaseFragment){
//                        BaseFragment baseFragment = (BaseFragment) frag;
//                        baseFragment.presenter().onServerFailedCallback(evt, data);
//                        baseFragment.onServerFailedCallback(evt, data);
//                    }
//                }
//            }
        }
    }
    private void onNetFailedCallbackInner(String interfaceName)
    {
        if (mActivity != null)
        {
            mActivity.onNetFailedCallback(interfaceName);
        }
    }



    /**
     * 以静默的方式请求后台中间件接口
     *
     * @param interfaceName 接口
     * @param transElement  传递的参数
     * @param isSync        是否同步 "1":同步 "0" 是异步
     * @param reqParamMap   请求参数
     */
    public void callServerSilence(final String interfaceName, final Map<String, Object> transElement,
                                  String isSync, Map<String, String> reqParamMap)
    {
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult responseBody)
            {
                onServerCallbackInner(interfaceName, transElement, responseBody.getBody());
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                onServerFailedCallbackInner(interfaceName, apiE.getErrorCode(), apiE.getErrBody());

                boolean isShowToast=true;
                //滤除特定code
                switch (apiE.getErrorCode())
                {
                    case "GAME_WAITING":
                        isShowToast=false;
                        break;
                    default:
                        break;
                }
                //拦截toast显示
                switch (interfaceName)
                {
                    case NetInterfaceConstant.HeartC_beat:
                    case NetInterfaceConstant.SunMoonStarC_checkPopups:
                    case NetInterfaceConstant.LiveC_sendMsg:
                            isShowToast=false;
                        break;
                    default:
                            break;
                }

                if (isShowToast)
                    super.onHandledError(apiE);

            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                onNetFailedCallbackInner(interfaceName);
            }
        }, interfaceName, isSync, reqParamMap);
    }

    /**
     * 需要改变请求服务器调用这个方法
     *
     * @param interfaceName 接口
     * @param transElement  传递的参数
     * @param reqParamMap   请求参数
     */
    public void callServerSilence4Server(String baseUrl,final String interfaceName, final Map<String, Object> transElement, Map<String, String> reqParamMap)
    {
        HttpMethods.getInstance().startServerRequest4Server(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String responseBody)
            {
                onServerCallbackInner(interfaceName, transElement, responseBody);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                if (!NetInterfaceConstant.HeartC_beat.equals(interfaceName))//暂时滤掉心跳
                    super.onHandledError(apiE);
                onServerFailedCallbackInner(interfaceName, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                onNetFailedCallbackInner(interfaceName);
            }
        }, baseUrl, interfaceName, reqParamMap);
    }

    /**
     * 以显示加载框的方式请求后台
     *
     * @param interfaceName 接口
     * @param transElement  传递的参数
     * @param desc          加载框显示的内容
     * @param couldCancel   加载框是否可以用返回键去掉
     * @param reqParamMap   请求后台的参数
     */
    public void callServerWithProgress(final String interfaceName, final Map<String, Object> transElement, String isSync,
                                       String desc, boolean couldCancel, Map<String, String> reqParamMap)
    {
        if (mActivity == null)
            return;
        ProgressSubscriber2<ResponseResult> ps2=new ProgressSubscriber2<ResponseResult>(mActivity, couldCancel, desc)
        {
            @Override
            public void onNext(ResponseResult responseBody)
            {
                super.onNext(responseBody);
                onServerCallbackInner(interfaceName, transElement, responseBody.getBody());
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                onServerFailedCallbackInner(interfaceName, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                onNetFailedCallbackInner(interfaceName);
            }
        };
        HttpMethods.getInstance().startServerRequest(ps2, interfaceName, isSync, reqParamMap);
    }

    //------------------------------------------------------------------------------------------------------------------------------------
    //调用腾讯服务器
    public void callTXServer(final String evt, final Map<String, Object> transElement, Map<String, Object> t)
    {
        if (mActivity == null)
            return;
        HttpMethods.getInstance().startTencentServerRequest(new Observer<TencentIMHttpResult>()
        {
            @Override
            public void onSubscribe(Disposable d)
            {

            }

            @Override
            public void onNext(TencentIMHttpResult responseBody)
            {
                if ("FAIL".equals(responseBody.getActionStatus()))
                {
                    String str = responseBody.getErrorInfo();
                    String codestr = String.valueOf(responseBody.getErrorCode());
                    onServerFailedCallbackInner(evt,String.valueOf(responseBody.getErrorCode()),responseBody.getErrorInfo());
                    return;
                }
                onServerCallbackInner(evt, transElement, responseBody.getMsgSeq());
            }

            @Override
            public void onError(Throwable e)
            {

            }

            @Override
            public void onComplete()
            {

            }
        }, evt, t);
    }

    //------------------------------------------------------------------------------------------------------------------------------------
    // 拦截器 是不必要的，在onError回调中处理拦截即可--wb 注释
    private InterceptorOnErrorListener errListener = new InterceptorOnErrorListener<Throwable>()
    {
        @Override
        public boolean onError(Throwable o)
        {
            if (o instanceof ApiException)
            {
                String code = ((ApiException) o).getMessage();
                Object body = ((ApiException) o).getErrBody();
                code = TextUtils.isEmpty(code) ? "" : code;
//                mActivity.toToast(code);
            }
            return false;
        }
    };

    public R getmRecord()
    {
        return mRecord;
    }

    //子类可以使用如下生命周期
    public void onCreate()
    {
    }

    public void onDestroy()
    {
        detachView();
    }

    public void onStart()
    {
    }

    public void onStop()
    {
    }

    public void onResume()
    {
    }

    public void onPause()
    {
    }
}
