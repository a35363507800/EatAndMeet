package com.echoesnet.eatandmeet.utils.NetUtils;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

/*import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;*/

/**
 * Created by wangben on 2016/11/15.
 */

public class HttpHelper
{
    private static final String TAG = HttpHelper.class.getSimpleName();
    private static HttpHelper mInstance = null;
    private OkHttpClient mOkHttpClient;
    //private static Retrofit retrofit;
    private HttpHelper(OkHttpClient okHttpClient,String baseUrl)
    {
        if (okHttpClient == null)
        {
            mOkHttpClient = new OkHttpClient();
        } else
        {
            mOkHttpClient = okHttpClient;
        }
/*        retrofit=new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();*/
    }
    public static HttpHelper initClient(OkHttpClient okHttpClient,String baseUrl)
    {
        if (mInstance == null)
        {
            synchronized (HttpHelper.class)
            {
                if (mInstance == null)
                {
                    mInstance = new HttpHelper(okHttpClient,baseUrl);
                }
            }
        }
        return mInstance;
    }
    public OkHttpClient getOkHttpClient()
    {
        return mOkHttpClient;
    }

    /**
     * 重新实例化
     * @param baseUrl
     * @return
     */
    public static HttpHelper reInitClient(String baseUrl)
    {
        return initClient(null,baseUrl);
    }
/*    public static<T> T getRetrofitService(Class<T> service)
    {
        return (T)retrofit.create(service);
    }*/
    public static  <T>void toSubscribe(Observable<T> o, Observer<T> s )
    {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }


}
