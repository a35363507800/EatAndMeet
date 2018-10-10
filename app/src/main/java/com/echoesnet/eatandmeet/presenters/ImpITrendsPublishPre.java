package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.graphics.BitmapFactory;

import com.echoesnet.eatandmeet.activities.TrendsPublishAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsPublishPre;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/10 0010
 * @description
 */
public class ImpITrendsPublishPre extends BasePresenter<TrendsPublishAct> implements ITrendsPublishPre
{
    private final String TAG = ImpITrendsPublishPre.class.getSimpleName();

    public ImpITrendsPublishPre()
    {
    }

    /**
     * 调用后台接口发布动态
     *
     * @param content    动态文字内容
     * @param url        动态图片或视频链接
     * @param thumbnails 视频缩略图
     * @param posx       坐标纬度
     * @param posy       坐标经度
     * @param location   位置描述
     */
    private void publishTrends2Server(String content, String url, String thumbnails, double posx, double posy, String location, String showType)
    {
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.content, content);
        params.put(ConstCodeTable.url, url);
        params.put(ConstCodeTable.thumbnails, thumbnails);
        params.put(ConstCodeTable.posx, String.valueOf(posx));
        params.put(ConstCodeTable.posy, String.valueOf(posy));
        params.put(ConstCodeTable.location, location);
        params.put(ConstCodeTable.showType, showType);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onError(Throwable e)
            {
                super.onError(e);
                if (getView() != null)
                    getView().requestNetError(e.getMessage());
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("发布动态" + response.getBody().toString());
                if (getView() != null)
                    getView().publishTrendsCallback();
            }
        }, NetInterfaceConstant.TrendC_publishTrend, null, params);
    }

    /**
     * 活动分享到动态
     *
     * @param content
     * @param type     0国庆 1中秋
     * @param posx
     * @param posy
     * @param location
     */
    private void shareAct2Trends(String content, String type, double posx, double posy, String location)
    {
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.content, content);
        params.put(ConstCodeTable.type, type);
        params.put(ConstCodeTable.posx, String.valueOf(posx));
        params.put(ConstCodeTable.posy, String.valueOf(posy));
        params.put(ConstCodeTable.location, location);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onError(Throwable e)
            {
                super.onError(e);
                if (getView() != null)
                getView().requestNetError(e.getMessage());
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("发布动态" + response.getBody().toString());
                if (getView() != null)
                    getView().publishTrendsCallback();
            }
        }, NetInterfaceConstant.ActivityC_trend, null, params);
    }

    @Override
    public void startPublishTrends(TrendsPublishAct.TrendsPublish trendsPublish, final List<String> imgs, String videoPath,
                                   String thumbnailPath, final String content, final double posx, final double posy, final String location, final String showType)
    {
        final TreeMap<Integer, String> sortMap = new TreeMap<>();
        final List<String> imgUrls = new ArrayList<>();
        final TreeMap<Integer, String> videoMap = new TreeMap<>();
        ;
        if (trendsPublish == TrendsPublishAct.TrendsPublish.TEXT)
        {
            publishTrends2Server(content, "", "", posx, posy, location, "");
        }
        else if (trendsPublish == TrendsPublishAct.TrendsPublish.PICTURE)
        {
            final String[] imgShowType=new String[]{"1"};
            CdnHelper.getInstance().setOnCdnFeedbackListener(new IOnCdnFeedbackListener()
            {
                @Override
                public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
                {
                    Logger.t(TAG).d(fileKeyName + "-> 上传成功");
                    if (sortMap.size()==0)
                    {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(file.getPath(), options);
                        Logger.t(TAG).d("图片宽高:" + options.outHeight + " | " + options.outWidth);
                        imgShowType[0]=options.outWidth/options.outHeight>0?"0":"1";
                    }
                    file.delete();
                    String imgUrl = CdnHelper.CDN_ORIGINAL_SITE + fileKeyName;
                    sortMap.put(uploadOrder, imgUrl);
                    if (sortMap.size() == imgs.size())
                    {
                        for (Map.Entry<Integer, String> integerStringEntry : sortMap.entrySet())
                        {
                            imgUrls.add(integerStringEntry.getValue());
                        }
                        publishTrends2Server(content, CommonUtils.listToStrWishSeparator(imgUrls, CommonUtils.SEPARATOR),
                                "", posx, posy, location, imgShowType[0]);
                    }
                }

                @Override
                public void onProcess(long len)
                {

                }

                @Override
                public void onFail(JSONObject response,File file)
                {
                    file.delete();
                    Logger.t(TAG).d("错误：" + response.toString());
                    getView().requestNetError(response.toString());
                }
            });
            for (int i = 0; i < imgs.size(); i++)
            {
                final String fileKeyName = CdnHelper.trendsImage + SharePreUtils.getUserMobile(getView()) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                final String outPutImagePath = NetHelper.getRootDirPath(getView()) + fileKeyName;
                final String filePath = imgs.get(i);
                try
                {
                    final File outFile = new File(outPutImagePath);
                    int degree = ImageUtils.readPictureDegree(filePath.replace("/raw",""));
                    ImageUtils.compressBitmap(ImageUtils.getBitmapFromFile((Activity) getView(), filePath), outFile.getPath(), 150, degree);
                    Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                    CdnHelper.getInstance().putFile(outFile, "img", fileKeyName, i);
                } catch (Exception e)
                {
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }
        else
        {
            CdnHelper.getInstance().setOnCdnFeedbackListener(new IOnCdnFeedbackListener()
            {
                @Override
                public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
                {
                    videoMap.put(uploadOrder, CdnHelper.CDN_ORIGINAL_SITE + fileKeyName);
                    if (videoMap.size() == 2)
                    {
                        Logger.t(TAG).d("视频上传成功 videoUrl =" + videoMap.get(0) + "|thumbnailUrl=" + videoMap.get(1));
                        publishTrends2Server(content, videoMap.get(0),
                                videoMap.get(1), posx, posy, location, showType);
                    }
                }

                @Override
                public void onProcess(long len)
                {

                }

                @Override
                public void onFail(JSONObject response,File file)
                {
                    file.delete();
                    Logger.t(TAG).d("上传失败" + response.toString());
                }
            });
            File file = new File(videoPath);
            String videoName = CdnHelper.trendsImage + SharePreUtils.getUserMobile(getView()) + UUID.randomUUID().toString().substring(0, 8) + ".mp4";
            CdnHelper.getInstance().putFile(file, "img", videoName, 0);
            File thumbnailFile = new File(thumbnailPath);
            String thumbnailName = CdnHelper.trendsImage + SharePreUtils.getUserMobile(getView()) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
            CdnHelper.getInstance().putFile(thumbnailFile, "img", thumbnailName, 1);
        }
    }

//    public enum TrendsPublish
//    {
//        TEXT,VIDEO, PICTURE
//    }
}
