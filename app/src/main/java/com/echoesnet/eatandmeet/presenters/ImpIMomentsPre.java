package com.echoesnet.eatandmeet.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.echoesnet.eatandmeet.activities.TrendsPublishAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.FrgMoments;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMomentsPre;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/14 16:33
 * @description
 */

public class ImpIMomentsPre extends BasePresenter<FrgMoments> implements IMomentsPre
{
    private static final String TAG = ImpIMomentsPre.class.getSimpleName();

    private List<FTrendsItemBean> fTrendsItemList = new ArrayList<>();
    private int tryNum = 0;
    private Gson gson;
    private boolean isPubish = false;

    public ImpIMomentsPre()
    {
        gson = new Gson();
    }

    @Override
    public void getFollowersMoments(final String type, String startIdx, String num)
    {
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(null);
        reqParam.put(ConstCodeTable.num, num);
        reqParam.put(ConstCodeTable.startIdx, startIdx);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    JSONObject body = new JSONObject(response.getBody());
                    String msgNum = body.getString("msgNum");
                    String details = body.getString("details");
                    if (getView() != null)
                        getView().getTrendsCallback(type, msgNum, (List<FTrendsItemBean>) EamApplication.getInstance().getGsonInstance()
                                .fromJson(details, new TypeToken<List<FTrendsItemBean>>()
                                {
                                }.getType()));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("动态列表异常" + e.getMessage());
                }
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_FocusTrendList,apiE.getErrorCode(),apiE.getErrBody());
            }

        }, NetInterfaceConstant.TrendC_FocusTrendList, null, reqParam);
    }

    @Override
    public void getUserTrends(String luid,final String type, String startIdx, String num)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(getView().getActivity());
        reqParam.put(ConstCodeTable.num, num);
        reqParam.put(ConstCodeTable.startIdx, startIdx);
        reqParam.put(ConstCodeTable.lUId, luid);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView()!=null)
                        getView().getUserTrendsCallback(type,(List<FTrendsItemBean>) EamApplication.getInstance().getGsonInstance()
                                .fromJson(response.getBody(), new TypeToken<List<FTrendsItemBean>>()
                                {
                                }.getType()));

            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_userTrends,apiE.getErrorCode(),apiE.getErrBody());
            }
        }, NetInterfaceConstant.TrendC_userTrends, null, reqParam);
    }


    @Override
    public void getMyTrends(String luid,final String type, String startIdx, String num)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParam = NetHelper.getCommonPartOfParam(getView().getActivity());
        reqParam.put(ConstCodeTable.num, num);
        reqParam.put(ConstCodeTable.startIdx, startIdx);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView()!=null)
                    getView().getUserTrendsCallback(type,(List<FTrendsItemBean>) EamApplication.getInstance().getGsonInstance()
                            .fromJson(response.getBody(), new TypeToken<List<FTrendsItemBean>>()
                            {
                            }.getType()));

            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_myTrends,apiE.getErrorCode(),apiE.getErrBody());
            }
        }, NetInterfaceConstant.TrendC_myTrends, null, reqParam);
    }
    /**
     * 点赞，取赞动态
     *
     * @param tId 动态id
     * @param flg 0：点赞，1：取赞
     */


    @Override
    public void likeTrends(final View view, final int position, String tId, final String flg, final String likeNum)
    {
        Map<String, String> params = NetHelper.getCommonPartOfParam(null);
        params.put(ConstCodeTable.flg, flg);
        params.put(ConstCodeTable.tId, tId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    int likeNumInt = Integer.parseInt(likeNum);
                    if ("0".equals(flg))
                    {
                        likeNumInt++;
                    }
                    else if (likeNumInt > 0)
                    {
                        likeNumInt--;
                    }
                    if (getView() != null)
                        getView().getLikeTrendsSuccessCallback(view,position, "1".equals(flg) ? "0" : "1", likeNumInt);
                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }, NetInterfaceConstant.TrendC_likeTrend, null, params);
    }

    @Override
    public void deleteTrends(final int position, String tId)
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
        params.put(ConstCodeTable.tId, tId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("删除动态" + response.getBody().toString());
                if ("0".equals(response.getStatus()))
                {
                    getView().deleteCommentSuc(position);
                }
            }
        },NetInterfaceConstant.TrendC_deleteTrend,null,params);
    }

    public void startPutTrends(FTrendsItemBean fTrendsItemBean)
    {
        isPubish = true;
        fTrendsItemList.add(fTrendsItemBean);
        startPublishTrends();
    }

    private void startPublishTrends()
    {
        if (!isPubish)
            return;
        final FTrendsItemBean fTrendsItemBean;
        if (fTrendsItemList.size() > 0)
            fTrendsItemBean = fTrendsItemList.get(0);
        else
            return;

        final TreeMap<Integer, String> sortMap = new TreeMap<>();
        final List<String> imgUrls = new ArrayList<>();
        final TreeMap<Integer, String> videoMap = new TreeMap<>();
        final List<String> imgFail = new ArrayList<>();
        if (fTrendsItemBean.getTrendsPublish() == TrendsPublishAct.TrendsPublish.TEXT)
        {
            publishTrends2Server(fTrendsItemBean.getContent(), "", "", fTrendsItemBean.getPosx(), fTrendsItemBean.getPosy(), fTrendsItemBean.getLocation(), "", fTrendsItemBean.getStamp());
        } else if (fTrendsItemBean.getTrendsPublish() == TrendsPublishAct.TrendsPublish.PICTURE)
        {
            final List<String> imgs = CommonUtils.strToList(fTrendsItemBean.getUrl());
            CdnHelper.getInstance().setOnCdnFeedbackListener(new IOnCdnFeedbackListener()
            {
                @Override
                public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
                {
                    if (!file.getName().endsWith(".gif"))
                        file.delete();
                    String imgUrl = CdnHelper.CDN_ORIGINAL_SITE + fileKeyName;
                    Logger.t(TAG).d(imgUrl + "-> 上传成功");
                    sortMap.put(uploadOrder, imgUrl);
                    if (sortMap.size() == imgs.size())
                    {
                        for (Map.Entry<Integer, String> integerStringEntry : sortMap.entrySet())
                        {
                            imgUrls.add(integerStringEntry.getValue());
                        }
                        publishTrends2Server(fTrendsItemBean.getContent(), CommonUtils.listToStrWishSeparator(imgUrls, CommonUtils.SEPARATOR),
                                "", fTrendsItemBean.getPosx(), fTrendsItemBean.getPosy(), fTrendsItemBean.getLocation(), fTrendsItemBean.getShowType(), fTrendsItemBean.getStamp());
                    }
                }

                @Override
                public void onProcess(long len)
                {

                }

                @Override
                public void onFail(JSONObject response,File file)
                {
                    imgFail.add(response.toString());
                    if (!file.getName().endsWith(".gif"))
                        file.delete();
                    if (fTrendsItemList != null && fTrendsItemList.size() > 0)
                        fTrendsItemList.remove(0);
                    startPublishTrends();
                    ToastUtils.showShort("上传图片失败");
                    Logger.t(TAG).d("错误：" + response.toString());
                }
            });
            for (int i = 0; i < imgs.size(); i++)
            {
                final String filePath = imgs.get(i);
                String fileType = ".jpg";
                String type = CommonUtils.getImageMimeType(filePath);
                Logger.t(TAG).d("imgType>>" + type);
                if ("gif".equals(type) || filePath.endsWith(".gif"))
                {
                    fileType = ".gif";
                }
                final String fileKeyName = CdnHelper.trendsImage + SharePreUtils.getUserMobile(getView().getActivity()) + UUID.randomUUID().toString().substring(0, 8) + fileType;
                final String outPutImagePath = NetHelper.getRootDirPath(getView().getActivity()) + fileKeyName;
                try
                {
                    File outFile ;
                    if (!fileType.equals(".gif"))
                    {
                        outFile = new File(outPutImagePath);
                        ImageUtils.compressBitmap(ImageUtils.rotateImageView(ImageUtils.readPictureDegree(filePath),ImageUtils.getBitmapFromFile(getView().getActivity(),filePath)),
                                outFile.getPath(), 130, 0);
                        Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                    }else
                    {
                        outFile = new File(filePath);
                    }
                    CdnHelper.getInstance().putFile(outFile, "img", fileKeyName, i);
                } catch (Exception e)
                {
                    if (fTrendsItemList != null && fTrendsItemList.size() > 0)
                        fTrendsItemList.remove(0);
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        } else
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
                        publishTrends2Server(fTrendsItemBean.getContent(), videoMap.get(0),
                                videoMap.get(1), fTrendsItemBean.getPosx(), fTrendsItemBean.getPosy(), fTrendsItemBean.getLocation(), fTrendsItemBean.getShowType(), fTrendsItemBean.getStamp());
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
                    if (fTrendsItemList != null && fTrendsItemList.size() > 0)
                        fTrendsItemList.remove(0);
                    startPublishTrends();
                    Logger.t(TAG).d("上传失败" + response.toString());
                }
            });
            File file = new File(fTrendsItemBean.getUrl());
            String videoName = CdnHelper.trendsImage + SharePreUtils.getUserMobile(getView().getActivity()) + UUID.randomUUID().toString().substring(0, 8) + ".mp4";
            CdnHelper.getInstance().putFile(file, "img", videoName, 0);
            File thumbnailFile = new File(fTrendsItemBean.getThumbnails());
            String thumbnailName = CdnHelper.trendsImage + SharePreUtils.getUserMobile(getView().getActivity()) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
            CdnHelper.getInstance().putFile(thumbnailFile, "img", thumbnailName, 1);
        }

    }

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            ConnectivityManager connectMgr = (ConnectivityManager) getView().getActivity().getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected())
            {
                isPubish = false;
// unconnect network
            } else
            {
                isPubish = true;
                startPublishTrends();
// connect network
            }
        }
    };


    public void registerBroadcastReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        getView().getActivity().registerReceiver(connectionReceiver, filter);
    }

    public void unRegisterReceiver()
    {
        if (connectionReceiver != null)
            getView().getActivity().unregisterReceiver(connectionReceiver);
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
    private void publishTrends2Server(String content, String url, String thumbnails, String posx, String posy, String location, String showType, final String stamp)
    {
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
        params.put(ConstCodeTable.content, content);
        params.put(ConstCodeTable.url, url);
        params.put(ConstCodeTable.thumbnails, thumbnails);
        params.put(ConstCodeTable.posx, posx);
        params.put(ConstCodeTable.posy, posy);
        params.put(ConstCodeTable.location, location);
        params.put(ConstCodeTable.showType, showType);

        String paramsJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.TrendC_publishTrend, gson.toJson(params));
        Logger.t(TAG).d(NetInterfaceConstant.TrendC_publishTrend + "请求参数：" + paramsJson.trim());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onError(Throwable e)
            {
                super.onError(e);
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("发布动态" + response.getBody().toString());
                try
                {
                    if (fTrendsItemList.size() > 0)
                        fTrendsItemList.remove(0);
                    JSONObject jsonObject = new JSONObject(response.getBody());
                    if (getView() != null)
                        getView().PublishSuccess(jsonObject.getString("tId"), stamp);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, NetInterfaceConstant.TrendC_publishTrend, null, params);
    }

}
