package com.echoesnet.eatandmeet.http4retrofit2.service;


import com.echoesnet.eatandmeet.models.bean.TencentIMHttpResult;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import io.reactivex.Observable;

/**
 * Created by liuyang on 2016/12/20.
 */

public interface TencentService
{
    @POST("v4/group_open_http_svc/send_group_msg")
    Observable<TencentIMHttpResult> sendGroupMsg(@QueryMap Map<String, Object> params, @Body Map<String, Object> reqParamMap);
}
