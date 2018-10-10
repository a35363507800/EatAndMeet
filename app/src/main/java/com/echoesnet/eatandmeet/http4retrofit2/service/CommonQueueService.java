package com.echoesnet.eatandmeet.http4retrofit2.service;

import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResultSkeleton;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CommonQueueService
{
    //@Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/")
    Observable<ResponseResult> postRxBody(@Body Map<String, Object> reqParamMap);

    //提交一个POST表单
    @FormUrlEncoded
    @POST("queue")
    Observable<ResponseResultSkeleton> postRxString(@FieldMap Map<String, String> reqParamMap);

    //提交一个POST表单
    @FormUrlEncoded
    @POST("queue")
    Flowable<ResponseResultSkeleton> postRx2String(@FieldMap Map<String, String> reqParamMap);
}
