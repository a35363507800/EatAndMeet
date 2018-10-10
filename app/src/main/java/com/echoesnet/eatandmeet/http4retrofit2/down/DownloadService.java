package com.echoesnet.eatandmeet.http4retrofit2.down;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by liuyang on 2016/12/20.
 */

public interface DownloadService {

    // option 1: a resource relative to your base URL
    @GET("/resource/example.zip")
    Observable<DownloadProgressResponseBody> downloadFileWithFixedUrl();

    // option 2: using a dynamic URL
    @Streaming
    @GET
    Observable<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);


    // option 2: using a dynamic URL
    @Streaming
    @GET
    Observable<ResponseBody> downloadUrlSync(@Url String fileUrl);

    @GET("online/"+"{who}.zip")
    Observable<DownloadProgressResponseBody> downloadFileWithFileName(@Path("who") String who);


}
