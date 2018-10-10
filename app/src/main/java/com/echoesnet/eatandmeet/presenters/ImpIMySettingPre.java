package com.echoesnet.eatandmeet.presenters;

import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.echoesnet.eatandmeet.activities.MySettingAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ImpIMySettingPre extends BasePresenter<MySettingAct>
{
    private final String TAG = ImpIMySettingPre.class.getSimpleName();


    public void getVersionCode()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.version, CommonUtils.getVerCode(getView()) + "");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getVersionCodeCallback(response);
            }
        }, NetInterfaceConstant.UserC_version_v304, reqParamMap);
    }

    /**
     * 获取各种功能开关状态
     */
    public void getStatOnBack()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getStatCallBack(response);
            }
        }, NetInterfaceConstant.UserC_siteStat, reqParamMap);
    }

    /**
     * 获取公司联系方式
     */
    public void getCompContact()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getContactCallback(response);
            }
        }, NetInterfaceConstant.UserC_contact, reqParamMap);
    }

    public void changeOrderStat(final String stat)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.stat, stat);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ArrayMap<String, Object> map = new ArrayMap<String, Object>();
                map.put("state", stat);
                map.put("response", response);
                if (getView() != null)
                    getView().changeOrderStatCallback(map);
            }
        }, NetInterfaceConstant.UserC_orderFlag, reqParamMap);
    }


    public void changePushStat(final String stat)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.stat, stat);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().changePushStatCallback(response.getBody(), stat);
            }
        }, NetInterfaceConstant.UserC_pushFlag, null, reqParamMap);
    }


    private long fileLength = 0;
    //要清除缓存的file队列
    private LinkedList<File> clearFileList;


    //检测缓存大小，并且把缓存文件标记 以备清除
    public void getFileLength()
    {
        if (clearFileList == null)
            clearFileList = new LinkedList<>();

        fileLength = 0;
        clearFileList.clear();

        String path = NetHelper.getRootDirPath(getView());
        final File file = new File(path);

        //遍历所有file下的文件
        Observable.create(new ObservableOnSubscribe<String>()
        {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception
            {
                getDirectory(file);
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .compose(getView().<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String s) throws Exception
                    {
                        if (isViewAttached())
                            getView().getFileLengthCallback(fileLength);
                    }
                });

    }

    //获得glide本地缓存大小
    private long getFolderSize(File file)
    {

        //  getFolderSize(new File(getView().getCacheDir() + "/"+ InternalCacheDiskCacheFactory
        // .DEFAULT_DISK_CACHE_DIR));

        long size = 0;
        try
        {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList)
            {
                if (aFileList.isDirectory())
                {
                    size = size + getFolderSize(aFileList);
                }
                else
                {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return size;
    }

    //获得APP内部缓存
    private void getDirectory(File file)
    {
        //上级目录文件名字
        String outFileName = file.getName();

        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
            {
//                if ("gift".equals(files[i].getName()))
//                {
//                    Log.d(TAG, "检测到要删除的缓存文件 文件名是：" + files[i].getName() + "文件大小是:" + files[i]
//                            .length());
//                    fileLength += getFolderSize(files[i]);
//                    clearFileList.offerFirst(files[i]);
//                }
                getDirectory(files[i]);
            }

            else

            {
                //删除EatAndMeet目录下的所有txt文件
                if (outFileName.equals("EatAndMeet") && files[i].getName().contains(".txt"))
                {
                    Log.d(TAG, "检测到要删除的缓存文件 文件名是：" + files[i].getName() + "文件大小是:" + files[i]
                            .length());
                    clearFileList.offerFirst(files[i]);
                    fileLength += files[i].length();
                }

                //删除EatAndMeet目录下所有没有后缀名的文件
                if (outFileName.equals("EatAndMeet") && !files[i].getName().contains("."))
                {
                    Log.d(TAG, "检测到要删除的缓存文件 文件名是：" + files[i].getName() + "文件大小是:" + files[i]
                            .length());
                    clearFileList.offerFirst(files[i]);
                    fileLength += files[i].length();
                }

                //删除DownLoadImages目录下所有文件
                if (outFileName.equals("DownLoadImages"))
                {
                    Log.d(TAG, "检测到要删除的缓存文件 文件名是：" + files[i].getName() + "文件大小是:" + files[i]
                            .length());
                    clearFileList.offerFirst(files[i]);
                    fileLength += files[i].length();
                }

                //删除crash目录下所有文件
                if (outFileName.equals("crash"))
                {
                    Log.d(TAG, "检测到要删除的缓存文件 文件名是：" + files[i].getName() + "文件大小是:" + files[i]
                            .length());
                    clearFileList.offerFirst(files[i]);
                    fileLength += files[i].length();
                }

                //删除Emojs目录下已经完成解压的zip文件
                if (outFileName.equals("Emojs") && files[i].getName().contains(".zip"))
                {
                    String path = NetHelper.getRootDirPath(getView()) + outFileName + "/" +
                            files[i].getName().replace(".zip", "");
                    File f = new File(path);
                    if (f.exists())
                    {
                        Log.d(TAG, "检测到要删除的缓存文件 文件名是：" + files[i].getName() + "文件大小是:" + files[i]
                                .length());
                        clearFileList.offerFirst(files[i]);
                        fileLength += files[i].length();
                    }
                }

                //删除gift目录下已经完成解压的zip文件
                if (outFileName.equals("gift")&&files[i].getName().contains(".zip"))
                {
                    String path = Environment.getExternalStorageDirectory() + "/EatAndMeet/"+outFileName+"/"+files[i].getName().replace(".zip","");
                    File f = new File(path);
                    if(f.exists())
                    {
                        Log.d(TAG,"检测到要删除的缓存文件 文件名是：" + files[i].getName() + "文件大小是:" + files[i].length());
                        clearFileList.offerFirst(files[i]);
                        fileLength += files[i].length();
                    }
                }

                //删除gift目录下所有没有后缀名的文件
                if (outFileName.equals("gift") && !files[i].getName().contains("."))
                {
                    Log.d(TAG, "检测到要删除的缓存文件 文件名是：" + files[i].getName() + "文件大小是:" + files[i]
                            .length());
                    clearFileList.offerFirst(files[i]);
                    fileLength += files[i].length();
                }

            }
        }

    }

    //开始清除clearFileList里面文件
    public void clearFile()
    {
        File file = clearFileList.pollLast();
        if (file != null && file.exists())
        {
            if (file.isDirectory())
                FileUtils.deleteFile(file.getPath());
            else
                file.delete();
            clearFile();
        }

    }


}
