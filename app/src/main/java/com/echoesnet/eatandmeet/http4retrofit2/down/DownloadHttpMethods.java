package com.echoesnet.eatandmeet.http4retrofit2.down;


import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


public class DownloadHttpMethods
{
    private static final String TAG = "DownloadHttpMethods";
    private static final int DEFAULT_TIMEOUT = 15;
    public Retrofit retrofit;


    public DownloadHttpMethods(String url, DownloadProgressListener listener)
    {
        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
//                .addConverterFactory()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * file 不存在 -》 建立父层路径
     * file 存在 -》 删除源文件
     *
     * @param url
     * @param file
     * @param subscriber
     */
    public void downloadFile(String url, final File file, Observer subscriber)
    {
        Logger.d(TAG, "downloadFile: " + url);

        retrofit.create(DownloadService.class)
                .downloadFileWithDynamicUrlSync(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>()
                {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception
                    {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.computation())
                .doOnNext(new Consumer<InputStream>()
                {
                    @Override
                    public void accept(InputStream inputStream) throws Exception
                    {
                        try
                        {
                            if (!file.getParentFile().exists())
                                file.getParentFile().mkdirs();

                            if (file != null && file.exists())
                                file.delete();

                            FileOutputStream out = new FileOutputStream(file);
                            byte[] buffer = new byte[1024 * 2];
                            int len = -1;
                            while ((len = inputStream.read(buffer)) != -1)
                            {
                                out.write(buffer, 0, len);
                            }
                            out.flush();
                            out.close();
                            inputStream.close();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                            throw new EAMDownloadException(e.getMessage(), e);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public class EAMDownloadException extends RuntimeException
    {

        public EAMDownloadException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }
}
