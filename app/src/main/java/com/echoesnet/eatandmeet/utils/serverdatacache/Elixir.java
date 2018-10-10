package com.echoesnet.eatandmeet.utils.serverdatacache;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.utils.serverdatacache.database.orm.ServerDataORM;
import com.echoesnet.eatandmeet.utils.serverdatacache.memo.ElixirDo;
import com.echoesnet.eatandmeet.utils.serverdatacache.model.ServerData;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuyang on 2017/9/16.
 */

public class Elixir {
    public static final String TAG = "Elixir";

    /**
     *
     * 当退出、被踢，等情况下，删表重建
     *
     *
     * @param ctx
     */
    public static void rebuild(Context ctx){
        Log.i(TAG, "┌───────── Elixir rebuild DB ────────");
        ServerDataORM.rebuild(ctx);
        Log.i(TAG, "└────────────────────────────────────");

    }

    public static void findCache(Context ctx, String sign, ElixirDo elixirWillDo, ElixirDo elixirMustDo){
        boolean hasNet = isNetworkConnected(ctx);
        if(!hasNet && elixirWillDo!=null){
            Log.i(TAG, "┌───────── Elixir LoadCache ─────────");
            String cache = pumpCache(ctx, sign);
            Log.i(TAG, "└────────────────────────────────────");
            if(null == cache){
                elixirMustDo.doing("");
            }else {
                elixirWillDo.doing(cache);
            }
        }else {
            elixirMustDo.doing("");
        }
    }


    public static String pumpCache(Context ctx, String sign){
        ServerData serverData = ServerDataORM.findDatabySign(ctx,sign);
        if (null == serverData){
            return null;
        }
        return serverData.getBody();
    }

    public static void pumpCache(Context ctx, final String sign, @Nullable final ICommonOperateListener listener)
    {
        ServerData serverData = ServerDataORM.findDatabySign(ctx,sign);
        if (serverData != null)
        {
            Observable.just(serverData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
//                .filter(new Predicate<ServerData>()
//                {
//                    @Override
//                    public boolean test(@NonNull ServerData serverData) throws Exception
//                    {
//                        if (serverData==null|| TextUtils.isEmpty(serverData.getBody()))
//                            return false;
//                        return true;
//                    }
//                })
                    .subscribe(new Consumer<ServerData>()
                    {
                        @Override
                        public void accept(@NonNull ServerData serverData) throws Exception
                        {
                            if (listener!=null)
                            {
                                if (TextUtils.isEmpty(serverData.getBody()))
                                {
                                    listener.onError("88882",String.format("获取 %s 的缓存失败",sign));
                                }else
                                {
                                    listener.onSuccess(serverData.getBody());
                                }
                            }
                        }
                    });
        }else {
            if (listener!=null)
            {
                listener.onError("88882",String.format("获取 %s 的缓存失败",sign));
            }
        }

    }

    public static long updateCache(Context ctx, String sign, String body){
        Log.i(TAG, "┌───────── Elixir Update ─────────");
        long result = ServerDataORM.insertData(ctx,new ServerData(sign,""+ System.currentTimeMillis(),body));
        Log.i(TAG, "└─────────────────────────────────");
        return result;
    }

    public static void updateCacheOnNewThread(final Context ctx, final String sign, final String body){
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.i(TAG, "┌───────── Elixir Update ─────────");
                ServerDataORM.insertData(ctx,new ServerData(sign,""+ System.currentTimeMillis(),body));
                Log.i(TAG, "└─────────────────────────────────");
            }
        }).start();
    }

    private static boolean isNetworkConnected(Context ctx) {
        if (ctx != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)
                    ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
