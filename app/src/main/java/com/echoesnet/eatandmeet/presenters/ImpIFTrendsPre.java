package com.echoesnet.eatandmeet.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.echoesnet.eatandmeet.activities.TrendsPublishAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.FTrendsFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.bean.UnFocusVUserBean;
import com.echoesnet.eatandmeet.models.bean.UnFocusVuserItemBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFTrendsPre;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.serverdatacache.Elixir;
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
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/10 0010
 * @description
 */
public class ImpIFTrendsPre extends BasePresenter<FTrendsFrg> implements IFTrendsPre
{
    private final String TAG = ImpIFTrendsPre.class.getSimpleName();
    private Gson gson;
    private List<FTrendsItemBean> fTrendsItemList = new ArrayList<>();
    private int tryNum = 0;
    private boolean isPubish = false;

    public ImpIFTrendsPre()
    {
        this.gson = new Gson();
    }

    @Override
    public void getFTrends(final String type, String startIdx, String num)
    {
        Map<String, String> params = NetHelper.getCommonPartOfParam(null);
        params.put(ConstCodeTable.num, num);
        params.put(ConstCodeTable.startIdx, startIdx);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(final ResponseResult response)
            {
                super.onNext(response);
                Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.TrendC_trends, response.getBody());
                try
                {
                    String msgNum = new JSONObject(response.getBody()).getString("msgNum");
                    String details = new JSONObject(response.getBody()).getString("details");
                    if (getView() != null)
                        getView().getTrendsCallback(type, msgNum, (List<FTrendsItemBean>) gson.fromJson(details, new TypeToken<List<FTrendsItemBean>>()
                        {
                        }.getType()));
                } catch (JSONException e)
                {
                    Logger.t(TAG).d("动态列表异常" + e.getMessage());
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.TrendC_trends, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        try
                        {
                            String msgNum = new JSONObject(response).getString("msgNum");
                            String details = new JSONObject(response).getString("details");
                            if (getView() != null)
                                getView().getTrendsCallback(type, msgNum, (List<FTrendsItemBean>) gson.fromJson(details, new TypeToken<List<FTrendsItemBean>>()
                                {
                                }.getType()));
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        Logger.t(TAG).d("code:" + code + " | msg:" + msg);
                    }
                });
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_trends, apiE.getErrorCode(), apiE.getErrBody());
            }
        }, NetInterfaceConstant.TrendC_trends, null, params);
    }


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
                        getView().getLikeTrendsSuccessCallback(view, position, "1".equals(flg) ? "0" : "1", likeNumInt);
                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }, NetInterfaceConstant.TrendC_likeTrend, null, params);
    }

    @Override
    public void getUnFocusVuser()
    {
        Map<String, String> params = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(final ResponseResult response)
            {
                Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.TrendC_unFocusVuser, response.getBody());
                if (getView() == null)
                    return;
                UnFocusVUserBean unFocusVUserBean = new UnFocusVUserBean();
                unFocusVUserBean.setFocusVuserList((List<UnFocusVuserItemBean>) gson.fromJson(response.getBody(), new TypeToken<List<UnFocusVuserItemBean>>()
                {
                }.getType()));
                getView().getUnFocusVuser(unFocusVUserBean);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                getView().requestNetError(null, apiE, apiE.getErrorCode());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.TrendC_unFocusVuser, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        UnFocusVUserBean unFocusVUserBean = new UnFocusVUserBean();
                        unFocusVUserBean.setFocusVuserList((List<UnFocusVuserItemBean>) gson.fromJson(response, new TypeToken<List<UnFocusVuserItemBean>>()
                        {
                        }.getType()));
                        if (getView() != null)
                            getView().getUnFocusVuser(unFocusVUserBean);
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
//                        getView().callServerErrorCallback(NetInterfaceConstant.TrendC_trends, apiE.getErrorCode(), apiE.getErrBody());
                    }
                });
            }
        }, NetInterfaceConstant.TrendC_unFocusVuser, null, params);
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
        }, NetInterfaceConstant.TrendC_deleteTrend, null, params);
    }


//    @Override
//    public void getGameList()
//    {
//        if (getView() == null)
//            return;
//        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
//        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
//        {
//
//            @Override
//            public void onNext(final ResponseResult response)
//            {
//                super.onNext(response);
//
//                Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.GameC_gameList, response.getBody());
//                try
//                {
//                    getView().getGameListCallback((List<GameItemBean>) gson.fromJson(response.getBody(), new TypeToken<List<GameItemBean>>()
//                    {
//                    }.getType()));
//                } catch (Exception e)
//                {
//                    e.printStackTrace();
//                    Logger.t(TAG).d(e.getMessage());
//                }
//            }
//
//            @Override
//            public void onHandledNetError(Throwable throwable)
//            {
//                super.onHandledNetError(throwable);
//                Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.GameC_gameList, new ICommonOperateListener()
//                {
//                    @Override
//                    public void onSuccess(String response)
//                    {
//                        if (getView() != null)
//                            getView().getGameListCallback((List<GameItemBean>) gson.fromJson(response, new TypeToken<List<GameItemBean>>()
//                            {
//                            }.getType()));
//                    }
//
//                    @Override
//                    public void onError(String code, String msg)
//                    {
////                        getView().callServerErrorCallback(NetInterfaceConstant.TrendC_trends, apiE.getErrorCode(), apiE.getErrBody());
//                    }
//                });
//            }
//        }, NetInterfaceConstant.GameC_gameList, null, params);
//    }

    @Override
    public void focusUser(String lUId, final int position)
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
        params.put(ConstCodeTable.lUId, lUId);
        params.put(ConstCodeTable.operFlag, "1");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    getView().focusUserCallback(position);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }, NetInterfaceConstant.LiveC_focus, null, params);
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
                try
                {
                    Logger.t(TAG).d("发布动态" + response.getBody());
                    if (fTrendsItemList.size() > 0)
                    {
                        fTrendsItemList.remove(0);
                        startPublishTrends();
                    }
                    JSONObject jsonObject = new JSONObject(response.getBody());
                    if (getView() != null)
                        getView().PublishSuccess(jsonObject.getString("tId"), stamp);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }

            }
        }, NetInterfaceConstant.TrendC_publishTrend, null, params);
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
        }
        else if (fTrendsItemBean.getTrendsPublish() == TrendsPublishAct.TrendsPublish.PICTURE)
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
                public void onFail(JSONObject response, File file)
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
                    File outFile;
                    if (!fileType.equals(".gif"))
                    {
                        outFile = new File(outPutImagePath);
                        ImageUtils.compressBitmap(ImageUtils.rotateImageView(ImageUtils.readPictureDegree(filePath), ImageUtils.getBitmapFromFile(getView().getActivity(), filePath)),
                                outFile.getPath(), 130, 0);
                        Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                    }
                    else
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
                        publishTrends2Server(fTrendsItemBean.getContent(), videoMap.get(0),
                                videoMap.get(1), fTrendsItemBean.getPosx(), fTrendsItemBean.getPosy(), fTrendsItemBean.getLocation(), fTrendsItemBean.getShowType(), fTrendsItemBean.getStamp());
                    }

                }

                @Override
                public void onProcess(long len)
                {

                }

                @Override
                public void onFail(JSONObject response, File file)
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
            if (thumbnailFile.exists())
            {
                Logger.t(TAG).d("缩略图存在");
                String thumbnailName = CdnHelper.trendsImage + SharePreUtils.getUserMobile(getView().getActivity()) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                CdnHelper.getInstance().putFile(thumbnailFile, "img", thumbnailName, 1);
            }
            else
            {
                Logger.t(TAG).d("缩略图不存在");
                videoMap.put(1, "");
            }
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

            if ((mobNetInfo != null || wifiNetInfo != null) && !mobNetInfo.isConnected() && !wifiNetInfo.isConnected())
            {
                Logger.t(TAG).d("unconnect network");
                isPubish = false;
// unconnect network
            }
            else
            {
                Logger.t(TAG).d("connect network");
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

}
