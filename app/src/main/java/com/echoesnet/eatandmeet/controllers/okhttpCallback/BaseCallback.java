package com.echoesnet.eatandmeet.controllers.okhttpCallback;

/**
 * Created by ben on 2017/3/31.
 */

import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

public abstract class BaseCallback extends Callback<ResponseResult>
{
    private final static String TAG = BaseCallback.class.getSimpleName();

    @Override
    public ResponseResult parseNetworkResponse(Response response) throws Exception
    {
        Gson g=new Gson();
        String resStr = response.body().string();
        Logger.t(TAG).d("返回结果》" + resStr);
        ResponseResult coreResult=g.fromJson(resStr,ResponseResult.class);
/*        ResponseResultSkeleton skeletonResult=g.fromJson(resStr,ResponseResultSkeleton.class);
        if (skeletonResult==null)
        {
            Logger.t(TAG).d("中间件返回结果错误");
            throw new NullPointerException("中间件返回结果错误");
        } else
        {
            coreResult=g.fromJson(skeletonResult.getMessageJson(),ResponseResult.class);
        }*/
        if (coreResult==null)
        {
            throw new NullPointerException("返回结果错误");
        }
        return coreResult;
    }

    @Override
    public void onError(Call call, Exception e)
    {

    }
}
