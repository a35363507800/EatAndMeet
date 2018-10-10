package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IHomeActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IHomePre;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by ben on 2016/11/29.
 */

public class ImpIHomePre extends BasePresenter<IHomeActView> implements IHomePre
{
    private final String TAG = ImpIHomePre.class.getSimpleName();

    @Override
    public void getVersionCode()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.version, CommonUtils.getVerCode(null) + "");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getVersionCodeCallback(response.getBody());
            }
        }, NetInterfaceConstant.UserC_version_v304, null, reqParamMap);
    }

    @Override
    public void downloadBgImg()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject obj = new JSONObject(response);
                    if (!response.contains("url"))
                        return;
                    final String imgUrl = obj.getString("url");
                    final String imgSize = obj.getString("size");
                    //final String imgSize=obj.getString("md5");
                    String launchFilePath = NetHelper.getRootDirPath(null) + NetHelper.DOWNLOAD_IMAGE_FOLDER + "app_lanch_bg.jpg";
                    File file = new File(launchFilePath);


                    if (!file.exists() || !imgUrl.equals(SharePreUtils.getLaunchBgUrl(null)))
                    {
                        OkHttpUtils.get()
                                .url(imgUrl)
                                .build()
                                .execute(new FileCallBack(NetHelper.getRootDirPath(null) + NetHelper.DOWNLOAD_IMAGE_FOLDER, "app_lanch_bg.jpg")
                                {
                                    @Override
                                    public void onError(Call call, Exception e)
                                    {
                                        Logger.t(TAG).d(e == null ? "exception null" : e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(final File target)
                                    {
                                        SharePreUtils.setLaunchBgSize(null, target.length());
                                    }

                                    @Override
                                    public void inProgress(float mProgress, long total)
                                    {
                                        Logger.t(TAG).d("进度》" + mProgress + "total>" + total);
                                        if (mProgress * 100 >= 98)//当图片下载为大于98%判定为可用
                                            SharePreUtils.setLaunchBgUrl(null, imgUrl);
                                    }
                                });
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.UserC_startup, reqParamMap);
    }

    @Override
    public void downloadProvinceData()
    {
        OkHttpUtils.get()
                .url(NetInterfaceConstant.FILE_PROVINCE_ADDRESS_DATA)
                .build()
                .execute(new FileCallBack(NetHelper.getRootDirPath(null) + NetHelper.DATA_FOLDER, "site.json")
                {

                    @Override
                    public void onError(Call call, Exception e)
                    {
                        Logger.t(TAG).d("下载失败");
                    }

                    @Override
                    public void onResponse(final File response)
                    {
                        Logger.t(TAG).d("下载成功");
                    }

                    @Override
                    public void inProgress(float progress, long total)
                    {

                    }
                });
    }

    @Override
    public void hindAppFolder()
    {
        final IHomeActView mHomeView = getView();
        if (mHomeView == null)
            return;
        final Context mContext = (HomeAct) getView();
        File dir = new File(NetHelper.getRootDirPath(mContext));
        if (!dir.exists())
        {
            if (!dir.mkdirs())
            {
                return;
            }
        }

        File oldFile = new File(NetHelper.getRootDirPath(mContext) + "DownLoadImages");
//        File newFile = new File(NetHelper.getRootDirPath(mContext) + "EAMImages");
        if (oldFile.exists() && oldFile.isDirectory())
        {
            oldFile.delete();
        }

        File[] files = dir.listFiles();
        for (File file : files)
        {
            Logger.t(TAG).d("fileName:" + file.getName() + " | " + file.getPath());
            if (file.isDirectory() && !file.getName().equals("EAMImages"))
            {
                File newNoMediaFile = new File(file, ".nomedia");
                if (!newNoMediaFile.exists())
                {
                    Logger.t(TAG).d("文件不存在");
                    try
                    {
                        newNoMediaFile.createNewFile();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else
                file.delete();
        }
//        String[] listFolder = dir.list(new FilenameFilter()
//        {
//            @Override
//            public boolean accept(File file, String s)
//            {
//                return file.isDirectory() && !s.equals("DownLoadImages");
//            }
//        });
//        Logger.t(TAG).d(Arrays.toString(listFolder));
//        for (String s : listFolder)
//        {
//            File folder = new File(dir + "/" + s);
//
//        }

    }

    @Override
    public void sendLocationSwitch()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().sendLocationSwitchCallback(response.getBody());
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.ReceiveC_sendLocationOnoff, apiE.getErrorCode(), apiE.getErrBody());
                //这个接口返回的结果存在问题 status=1，code=1  但是后台认为这是一个正确的结果，其实code应该等于某个特定的字符串，所以此处我们不能调用super.onHandledError()
                //不然就会将错误码1 解析为”小饭也不知道这么了..“展示出来--wb
            }
        }, NetInterfaceConstant.ReceiveC_sendLocationOnoff, null, reqParamMap);
    }

    @Override
    public void updateTaskOk()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    JSONObject jsonObject = new JSONObject(response.getBody());
                    String task = jsonObject.getString("task");
                    String successes = jsonObject.getString("successes");
                    String receive = jsonObject.getString("receive");
                    if (getView() != null)
                        getView().updateTaskOkCallBack(task, successes, receive);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.TaskC_taskOk, null, reqParamMap);
    }

    public void getResourceFromCdn()
    {
        List<Map<String, String>> resLst = new ArrayList<>();
        Map<String, String> map3 = new HashMap<>();
        map3.put("url", CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + "material.json");
        map3.put("fileName", "material.json");
        resLst.add(map3);
        for (Map<String, String> map : resLst)//没有做回调检测，使用时需要做判断
        {
            OkHttpUtils
                    .get()
                    .url(map.get("url"))
                    .build()
                    .execute(new FileCallBack(NetHelper.getRootDirPath(null) + NetHelper.DATA_FOLDER, map.get("fileName"))
                    {
                        @Override
                        public void inProgress(float progress, long total)
                        {
                        }

                        @Override
                        public void onError(Call call, Exception e)
                        {
                            Logger.t(TAG).d("下载异常》" + e.getMessage());
                        }

                        @Override
                        public void onResponse(File response)
                        {
                            Logger.t(TAG).d("下载》" + response.getName());
                        }
                    });
        }
    }

    /**
     * 获取敏感字 词库
     *
     * @param url
     */
    public void getSensitiveWordsFromCdn(String url)
    {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        Logger.t(TAG).d("获取敏感词失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("获取敏感词成功：" + response);
                        if (TextUtils.isEmpty(SharePreUtils.getSensitiveWords()))
                        {
                            SharePreUtils.setSensitiveWords(response);
                        }
                        else
                        {
                            if (!SharePreUtils.getSensitiveWords().equals(response))
                            {
                                SharePreUtils.setSensitiveWords(response);
                            }
                        }
                        EamApplication.getInstance().sensitiveWordsList
                                = EamApplication.getInstance().getGsonInstance().fromJson(response, new TypeToken<List<String>>()
                        {
                        }.getType());
                        Logger.t(TAG).d("敏感词 list：" + EamApplication.getInstance().sensitiveWordsList.toString());
                    }
                });
    }
}
