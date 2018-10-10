package com.echoesnet.eatandmeet.http4retrofit2;


import android.text.TextUtils;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.http4retrofit2.service.CommonQueueService;
import com.echoesnet.eatandmeet.http4retrofit2.service.TencentService;
import com.echoesnet.eatandmeet.models.bean.TencentIMHttpResult;
import com.echoesnet.eatandmeet.utils.Constants;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Refactor by ben on 2017/3/30
 */
public class HttpMethods
{
    private static final String TAG = HttpMethods.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT = 20;

    private Retrofit retrofit;
    private Retrofit retrofit4TX;
    private Retrofit specialRetrofit;
    private OkHttpClient okHttpClient;
    private Gson g = new Gson();

    //构造方法私有
    private HttpMethods()
    {
        okHttpClient = EamApplication.getInstance().getOkHttpClient();
        if (okHttpClient == null)
        {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();//手动创建一个OkHttpClient并设置超时时间
            builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            builder.addInterceptor(logging);
            okHttpClient = builder.build();
        }
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))  //gson 转换器 
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Rx 工厂。
                .baseUrl(NetHelper.SERVER_SITE)
                .build();
        retrofit4TX = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://console.tim.qq.com")
                .build();
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder
    {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 向中间件发起请求,已经废弃，请使用{@link #startServerRequest(Observer,String,Map)}
     *
     * @param subscriber
     * @param interfaceName 接口名称.
     * @param isSync        这个字段废弃了，传入null即可， 是否同步方式 1：同步；0：异步
     * @param reqParamMap   请求参数
     */
    @Deprecated
    public void startServerRequest(Observer<ResponseResult> subscriber, String interfaceName, @Nullable String isSync, Map<String, String> reqParamMap)
    {
        CommonQueueService service = retrofit.create(CommonQueueService.class);
        Observable<ResponseResult> observable = service.postRxBody(createReqBody(interfaceName, reqParamMap))
                .map(new ResponseResultMapper2());
        toSubscribe(observable, subscriber);
    }

    public void startServerRequest(Observer<String> subscriber, String interfaceName, Map<String, String> reqParamMap)
    {
        CommonQueueService service = retrofit.create(CommonQueueService.class);
        Observable<String> observable = service.postRxBody(createReqBody(interfaceName, reqParamMap))
                .map(new EAMHttpResultFunc());
        toSubscribe(observable, subscriber);
    }

    /**
     * 向服务器发起请求
     *
     * @param subscriber
     * @param baseUrl       服务器url 不传默认走客户端服务器
     * @param interfaceName 接口名称.
     * @param reqParamMap   请求参数
     */
    public void startServerRequest4Server(Observer<String> subscriber, String baseUrl, String interfaceName, Map<String, String> reqParamMap)
    {
        if (!TextUtils.isEmpty(baseUrl))
        {
            Logger.t(TAG).d("baseUrl>"+baseUrl);
            if (specialRetrofit == null || !baseUrl.equals(specialRetrofit.baseUrl().toString()))
            {
                specialRetrofit = new Retrofit.Builder()
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create(new Gson()))  //gson 转换器
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Rx 工厂。
                        .baseUrl(baseUrl)
                        .build();
            }
        }else {
            if (specialRetrofit == null || !TextUtils.equals(NetHelper.SERVER_SITE,specialRetrofit.baseUrl().toString()))
            {
                Logger.t(TAG).d("baseUrl>"+NetHelper.SERVER_SITE);
                specialRetrofit = new Retrofit.Builder()
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create(new Gson()))  //gson 转换器
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Rx 工厂。
                        .baseUrl(NetHelper.SERVER_SITE)
                        .build();
            }
        }
        CommonQueueService service = specialRetrofit.create(CommonQueueService.class);
        Observable<String> observable = service.postRxBody(createReqBody(interfaceName, reqParamMap))
                .map(new EAMHttpResultFunc());
        toSubscribe(observable, subscriber);
    }

    /**
     * 向服务器发起请求
     *
     * @param subscriber
     * @param interfaceName 接口名称.
     * @param reqParamMap   请求参数
     */
    public void startServerRequest4Server(Observer<String> subscriber, String interfaceName, Map<String, String> reqParamMap)
    {
        startServerRequest4Server(subscriber, null, interfaceName, reqParamMap);
    }

/*    public Flowable<ResponseResult> getApiService(String interfaceName, String isSync, Map<String, String> reqParamMap)
    {
        CommonQueueService service = retrofit.create(CommonQueueService.class);
        return service.postRx2String(createReqForm(interfaceName, isSync, reqParamMap))
                .map(new ResponseResultMapper());
    }*/

    public void startTencentServerRequest(Observer<TencentIMHttpResult> subscriber, String interfaceName, Map<String, Object> reqParamMap)
    {
        TencentService service = retrofit4TX.create(TencentService.class);
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("usersig", NetHelper.TX_USER_SIGN);
        queryMap.put("identifier", "AdminOnline");
        queryMap.put("sdkappid", Constants.SDK_APPID);
        queryMap.put("contenttype", "json");

        Observable observable = service.sendGroupMsg(queryMap, reqParamMap);
        toSubscribe(observable, subscriber);
    }

    //观察者启动器
    private <T> void toSubscribe(Observable<T> o, Observer<T> s)
    {
        o.subscribeOn(Schedulers.io()) //绑定在io
                .observeOn(AndroidSchedulers.mainThread()) //返回 内容 在Android 主线程
                .subscribe(s);  //放入观察者
    }

    /**
     * 组装消息体
     */
    private Map<String, Object> createReqBody(String interfaceName, Map<String, String> params)
    {
        Map<String, Object> m = new HashMap<>();
        Map<String, Object> clsm = new HashMap<>();
        clsm.put("reqName", interfaceName);
        m.put("head", clsm);
        m.put("body", params);
        Logger.t(TAG).d("接口请求数据：" + EamApplication.getInstance().getGsonInstance().toJson(m));
        return m;
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     */
    private class EAMHttpResultFunc implements Function<ResponseResult, String>
    {
        @Override
        public String apply(@NonNull ResponseResult httpResult) throws Exception
        {
            if (httpResult == null)
            {
                throw new NullPointerException("|返回结果为null|");
            }
            Logger.t(TAG).d("服务器返回结果" + EamApplication.getInstance().getGsonInstance().toJson(httpResult,ResponseResult.class));
            if ("1".equals(httpResult.getStatus()))
            {
                String bodyStr = httpResult.getBody();
                String codeStr = httpResult.getCode();
                throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
            }
            return TextUtils.isEmpty(httpResult.getBody())?"{}":httpResult.getBody();
        }
    }

    /**
     * 统一处理返回结果，包括异常处理。此为后期去掉中间件时重构函数，其实应该mapping为string返回，
     * 但是如果那样需要改变特别多的代码，对于当初的设计缺陷我很后悔，当初发现了却懒得改动。
     */
    @Deprecated
    private class ResponseResultMapper2 implements Function<ResponseResult, ResponseResult>
    {
        @Override
        public ResponseResult apply(@NonNull ResponseResult httpResult) throws Exception
        {
            if (httpResult == null)
            {
                throw new NullPointerException("|返回结果为null|");
            }
            Logger.t(TAG).d("服务器返回结果" + EamApplication.getInstance().getGsonInstance().toJson(httpResult,ResponseResult.class));
            //将服务器错误抛出
            if (!"0".equals(httpResult.getStatus()))
            {
                String bodyStr = httpResult.getBody();
                String codeStr = httpResult.getCode();
                throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
            }
            return httpResult;
        }
    }

    
    /**
     * 将后台返回的结果转化为需要的类型：ResponseResult
     */
/*    private class ResponseResultMapper implements Function<ResponseResultSkeleton, ResponseResult>
    {
        @Override
        public ResponseResult apply(ResponseResultSkeleton httpResult) throws Exception
        {
            *//*{
                "message":"non-standard access",
                    "messageJson":"{"RESPONSE_IDENTITY":"10.19.175.167-echoServer-98321490839655921",
                    "body":"{\"face\":\"988608\",\"balance\":\"6216.00\",\"meal\":\"185016\"}","code":"0","status":"0"}",
                "status":"0"
            }*//*
            //Logger 的最大字数为4000
            Logger.t(TAG).d("|中间件返回结果|》 " + new Gson().toJson(httpResult, ResponseResultSkeleton.class) + "\n\n");
            if (httpResult == null)
            {
                throw new NullPointerException("|中间件返回结果错误|》" + new Gson().toJson(httpResult, ResponseResultSkeleton.class));
            }

            String messageJson = httpResult.getMessageJson();
            ResponseResult resultBean = g.fromJson(messageJson, ResponseResult.class);
            if (resultBean == null)
            {
                throw new NullPointerException("|解析返回结果错误|");
            }

            //将服务器错误抛出
            if (!"0".equals(resultBean.getStatus()))
            {
                throw new ApiException(resultBean.getCode(), resultBean.getBody());
            }
            return resultBean;
        }
    }*/


    /**
     * 组装要提交的表单
     *
     * @param interfaceName
     * @param isSync        是否是同步请求 1：同步；2：异步
     * @param reqParamMap
     * @return
     */
/*    private Map<String, String> createReqForm(String interfaceName, String isSync, Map<String, String> reqParamMap)
    {
        if (TextUtils.isEmpty(isSync))
            isSync = "1";
        Map<String, String> encryptedMap = new HashMap<>();
        String businessName = TransBusinessCode.businessCode(interfaceName);
        encryptedMap.put("businessName", businessName);
        encryptedMap.put("syncFlag", isSync);
        encryptedMap.put("appKey", NetHelper.EAM_APP_KEY);
        String paramJson = NetHelper.getRequestJsonStr(interfaceName, g.toJson(reqParamMap));
        try
        {
            encryptedMap.put("md5", MD5Util.MD5(paramJson + NetHelper.MD5_KYE));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        encryptedMap.put("messageJson", paramJson);
        Logger.t(TAG).d(String.format("|***************Retrofit请求参数***************| 接口名称： %s  参数：%s ", interfaceName, g.toJson(encryptedMap)));
        return encryptedMap;
    }*/
}
