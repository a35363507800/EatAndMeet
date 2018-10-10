package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.TrendsPlayVideoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ResListBannerBean;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;
import com.echoesnet.eatandmeet.presenters.ImpResLstInfoAdapterView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IResLstInfoAdapterView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.ImageOverlayView;
import com.echoesnet.eatandmeet.views.widgets.video.EmptyControlVideo;
import com.jakewharton.rxbinding2.view.RxView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.panxw.android.imageindicator.AutoPlayManager;
import com.panxw.android.imageindicator.ImageIndicatorView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import okhttp3.Call;

public class ResLstInfoAdapter extends BaseAdapter implements IResLstInfoAdapterView
{
    private static final String TAG = ResLstInfoAdapter.class.getSimpleName();
    private static final int TYPE_BANNER = 0;
    private static final int TYPE_RES = 1;
    private ViewHolder holder;
    private List<RestaurantBean> resList;
    private List<ResListBannerBean> resListBannerList;
    private Context context;
    private String praisedOrNot; // 是否点赞
    private List<IconDrawable> iconNormal = new ArrayList<>();
    private List<IconDrawable> iconSelected = new ArrayList<>();
    private ImpResLstInfoAdapterView impResLstInfoAdapterView;
    private Map<Integer, View> videoViewMap;
    private View bannerView;
    private ImageIndicatorView indicatorView;
    private AutoPlayManager autoBrocastManager;
    private CustomAlertDialog customAlertDialog;
    private ResLstInfoAdapter.MealsItemClick mealsItemClick;
    private boolean isAutoPlay = true;

    public ResLstInfoAdapter(Context context, List<RestaurantBean> list, List<ResListBannerBean> resListBannerList)
    {
        this.context = context;
        this.resList = list;
        this.resListBannerList = resListBannerList;
        videoViewMap = new HashMap<>();
        iconNormal.add(new IconDrawable(context, EchoesEamIcon.eam_n_praise).colorRes(R.color.c1));
        iconNormal.add(new IconDrawable(context, EchoesEamIcon.eam_s_location2).colorRes(R.color.c8));
        iconSelected.add(new IconDrawable(context, EchoesEamIcon.eam_p_praise).colorRes(R.color.c1));
        impResLstInfoAdapterView = new ImpResLstInfoAdapterView(context, this);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == 0 && resListBannerList.size() > 0)
            return TYPE_BANNER;
        else
            return TYPE_RES;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getCount()
    {
        return resList.size() > 0 ? resListBannerList.size() > 0 ? resList.size() + 1 : resList.size() : 0;
    }

    @Override
    public Object getItem(int arg0)
    {
        return resList.get(arg0);
    }

    @Override
    public long getItemId(int arg0)
    {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (getItemViewType(position) == TYPE_BANNER)
        {
            if (bannerView == null)
            {
                bannerView = LayoutInflater.from(context).inflate(R.layout.item_res_banner, parent, false);
                indicatorView = (ImageIndicatorView) bannerView.findViewById(R.id.ImageIndicatorView);
                autoBrocastManager = new AutoPlayManager(indicatorView);
                autoBrocastManager.setBroadcastEnable(true);
                autoBrocastManager.setBroadCastTimes(10000);//loop times
                autoBrocastManager.setBroadcastTimeIntevel(3 * 1000, 3 * 1000);//设置第一次展示时间以及间隔，间隔不能小于1秒
                autoBrocastManager.loop();
                indicatorView.setGallery(true, CommonUtils.dp2px(context, 321), CommonUtils.dp2px(context, 180));
                indicatorView.getViewPager().setPageMargin(CommonUtils.dp2px(context, 10));
                indicatorView.setBackgroundColor(ContextCompat.getColor(context, R.color.C0324));
                indicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener()
                {
                    @Override
                    public void onPosition(int position, int totalCount)
                    {
                        //Logger.t(TAG).d("动了>>>>>>>>>>>>>>>>>>>>>>>" + position);
                        stopPlayVideo(-1);
                    }
                });
                try
                {
                    initIndicator(indicatorView);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
            bannerView.requestFocus();
            return bannerView;
        }
        else
        {
            if (convertView == null || convertView.getTag() == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_restaurant_info, parent, false);
                holder = new ViewHolder();
                holder.resName = (TextView) convertView.findViewById(R.id.tv_sh_name);
                holder.perCapita = (TextView) convertView.findViewById(R.id.tv_cost);
                holder.distance = (TextView) convertView.findViewById(R.id.tv_distance);
                holder.ivPraise = (IconTextView) convertView.findViewById(R.id.iv_praise);
                holder.imgSh = (ImageView) convertView.findViewById(R.id.img_sh);
                holder.imgSh.setAlpha(220);
                holder.horizoListview = (ImageOverlayView) convertView.findViewById(R.id.horizon_listview);
                holder.tvPraiseNum = (TextView) convertView.findViewById(R.id.tv_praise_num);
                holder.tvCircle = (TextView) convertView.findViewById(R.id.tv_circle);
                convertView.setTag(holder);
                // 对于listView 注意添加这一行 即可在item上使用高度
                AutoUtils.autoSize(convertView);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.setData(resListBannerList.size() > 0 ? position - 1 : position);
            return convertView;
        }
    }

    /**
     * 开始轮播
     */
    public void startLoop()
    {
        if (autoBrocastManager != null)
        {
            isAutoPlay = true;
            autoBrocastManager.setBroadcastEnable(true);
            autoBrocastManager.loop();
        }
    }

    /**
     * 销毁videoView
     */
    public void destroy()
    {
        for (Map.Entry<Integer, View> integerUVideoViewEntry : videoViewMap.entrySet())
        {
            View view = integerUVideoViewEntry.getValue();
            GSYVideoManager.releaseAllVideos();
        }
    }

    /**
     * 停止视频播放
     *
     * @param position 当前position 传入则不停止当前视频  负值停止全部
     */
    public void stopPlayVideo(int position)
    {
        if (position < 0 && autoBrocastManager != null && isAutoPlay)
        {
            autoBrocastManager.setBroadcastEnable(true);
            autoBrocastManager.loop();
        }
        for (Map.Entry<Integer, View> integerUVideoViewEntry : videoViewMap.entrySet())
        {
            if (position >= 0 && position == integerUVideoViewEntry.getKey())
                continue;
            View view = integerUVideoViewEntry.getValue();
            if (view != null)
            {
                FrameLayout thumbnailFl = (FrameLayout) view.findViewById(R.id.fl_thumbnail);
                EmptyControlVideo uVideoView =  view.findViewById(R.id.uvideo_view);
                ImageView startOrStopImg = (ImageView) view.findViewById(R.id.img_start_or_stop);
                if (thumbnailFl != null && uVideoView != null && startOrStopImg != null)
                {
                    thumbnailFl.setVisibility(View.VISIBLE);
                    uVideoView.setVisibility(View.INVISIBLE);
                    startOrStopImg.setTag(null);
                    if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
                        uVideoView.onVideoPause();
                    uVideoView.seekTo(0);
                    startOrStopImg.setImageResource(R.drawable.play_video);
                }
            }
        }
    }

    class ViewHolder
    {
        private IconTextView ivPraise;
        private TextView tvPraiseNum;
        private TextView resName;
        private TextView perCapita;
        private TextView distance;
        private ImageView imgSh;
        private ImageOverlayView horizoListview;
        private TextView tvCircle;

        private void setData(final int position)
        {
            Object itemData = resList.get(position);
            if (itemData instanceof RestaurantBean)
            {
                final RestaurantBean restaurantBean = (RestaurantBean) itemData;
                if (restaurantBean.getrName().contains("("))
                {
                    String temprName = restaurantBean.getrName();
                    String str = temprName.substring(temprName.lastIndexOf("("), temprName.lastIndexOf(")"));
                    String s = str.substring(1, str.length());
                    resName.setText(restaurantBean.getrName().toString().substring(0, restaurantBean.getrName().toString().lastIndexOf("("))
                            + "(" + s + ")");
                }
                else
                {
                    resName.setText(restaurantBean.getrName().toString());
                }
                // 人均消费
                perCapita.setText("￥" + restaurantBean.getCost() + " / 人");
                // 修改新的距离(从接口中获取)
                double distanceRes = Double.parseDouble(restaurantBean.getDistance());
                // 距离
                if ((distanceRes + 0.5) > 100)
                {
                    distance.setText(CommonUtils.keep2Decimal((distanceRes + 0.5) / 1000) + "km");
                }
                else
                {
//                    if ((distanceRes + 0.5) > 100)
//                    {
//                        distance.setText(CommonUtils.keep2Decimal((distanceRes + 0.5)) + "m");
//                    } else
//                    {
//                        distance.setText("< 100m");
//                    }
                    distance.setText("< 100m");

                }
                tvCircle.setText(restaurantBean.getCircle());
//            distance.setText(distanceRes+"");
                restaurantBean.setTestDistance(CommonUtils.keep2Decimal((distanceRes + 0.5)) + "");

                if (TextUtils.isEmpty(restaurantBean.getRpUrls()))
                {
                    imgSh.setImageResource(R.drawable.qs_cai_canting);
                }
                else
                {
                    String rpUrl = restaurantBean.getRpUrls();
                    String[] bgUrlArr = rpUrl.split(CommonUtils.SEPARATOR);
                    String restaurantUrl = bgUrlArr[0];

                    GlideApp.with(context.getApplicationContext())
                            .asBitmap()
                            .load(restaurantUrl)
                            .skipMemoryCache(false)   // 不引用内存缓存
                            .placeholder(R.drawable.qs_cai_canting)
                            .error(R.drawable.qs_cai_canting)
                            .into(imgSh);
                }
                imgSh.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mealsItemClick != null)
                            mealsItemClick.contentClick(restaurantBean);
                    }
                });
                // 点赞头像
                horizoListview.setHeadImages(Arrays.asList(restaurantBean.getResPraiseList()));
                // 点赞个数
                if (Integer.parseInt(restaurantBean.getrPraise()) >= 99)
                {
                    tvPraiseNum.setText("99+");
                }
                else
                {
                    tvPraiseNum.setText(restaurantBean.getrPraise());
                }
                // 判断是否点赞
                praisedOrNot = restaurantBean.getPraisedOrNot();
                if (praisedOrNot.equals("0"))
                {
                    ivPraise.setText("{eam-p-praise @color/C0323 @dimen/d22}"); // 0 --> 未点赞
                }
                else
                {
                    ivPraise.setText("{eam-p-praise @color/C0412 @dimen/d22}"); // 1 --> 已点赞
                }

                RxView.clicks(ivPraise)
                        .throttleFirst(1, TimeUnit.SECONDS)
                        .subscribe(new Consumer<Object>()
                        {
                            @Override
                            public void accept(Object o) throws Exception
                            {
                                if (resList.size() != 0)
                                {
                                    if (restaurantBean.getPraisedOrNot().equals("0"))
                                    {
                                        impResLstInfoAdapterView.setPraiseInfo(position, restaurantBean.getrId());
                                    }
                                    else
                                    {
                                        impResLstInfoAdapterView.setUnPraiseInfo(position, restaurantBean.getrId());
                                    }
                                }
                            }
                        });
            }
        }
    }

    /**
     * 初始化轮播
     *
     * @param indicatorView
     */
    private void initIndicator(final ImageIndicatorView indicatorView)
    {
        List<ResListBannerBean> response = new ArrayList<>();
        response.addAll(resListBannerList);
        if (response.size() > 1)
        {
            response.add(0, response.get(response.size() - 1));
            response.add(response.get(1));
        }
        Log.d("showIndicator", "response >" + response.size());
        if (response.size() > 0)
        {
            indicatorView.viewList.clear();
            indicatorView.setVisibility(View.VISIBLE);
            for (int i = 0; i < response.size(); i++)
            {
                final ResListBannerBean resListBannerBean = response.get(i);
                if ("0".equals(resListBannerBean.getType()))
                {
                    ImageView imageView = new ImageView(context);
                    GlideApp.with(EamApplication.getInstance())
                            .asBitmap()
                            .placeholder(R.drawable.qs_cai_canting)
                            .error(R.drawable.qs_cai_canting)
                            .load(resListBannerBean.getUrl())
                            .centerCrop()
                            .into(imageView);
                    indicatorView.addViewItem(imageView);
                }
                else
                {
                    final String videoUrl = resListBannerBean.getJump();
                    final View view = LayoutInflater.from(context).inflate(R.layout.item_indicator_play_video, null);
                    final EmptyControlVideo uVideoView =  view.findViewById(R.id.uvideo_view);
                    final ImageView thumbnailImg = (ImageView) view.findViewById(R.id.img_thumbnail);
                    TextView noticeTv = (TextView) view.findViewById(R.id.tv_notice);
                    final FrameLayout thumbnailFl = (FrameLayout) view.findViewById(R.id.fl_thumbnail);
                    final ImageView startOrStopImg = (ImageView) view.findViewById(R.id.img_start_or_stop);
                    final ImageView fullScreenImg = (ImageView) view.findViewById(R.id.img_full_screen);
                    final ProgressBar playPro = (ProgressBar) view.findViewById(R.id.play_pro);
                    uVideoView.setVisibility(View.GONE);
                    videoViewMap.put(i, view);

                    GlideApp.with(EamApplication.getInstance())
                            .asBitmap()
                            .placeholder(R.drawable.qs_cai_canting)
                            .error(R.drawable.qs_cai_canting)
                            .centerCrop()
                            .load(resListBannerBean.getUrl())
                            .into(thumbnailImg);
//                    if (!TextUtils.isEmpty(resListBannerBean.getTitle()))
//                    {
//                        noticeTv.setVisibility(View.VISIBLE);
//                        noticeTv.setText(resListBannerBean.getTitle());
//                    }else {
//                        noticeTv.setVisibility(View.GONE);
//                    }
                    thumbnailFl.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            playVideo(uVideoView, videoUrl, startOrStopImg);
                        }
                    });
                    uVideoView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            playVideo(uVideoView, videoUrl, startOrStopImg);
                        }
                    });
                    fullScreenImg.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            uVideoView.onVideoPause();
                            uVideoView.seekTo(0);
                            uVideoView.setVisibility(View.INVISIBLE);
                            thumbnailFl.setVisibility(View.VISIBLE);
                            isAutoPlay = false;
                            autoBrocastManager.stop();
                            Intent intent = new Intent(context, TrendsPlayVideoAct.class);
                            intent.putExtra("type", "resBanner");
                            intent.putExtra("showType", "0");
                            intent.putExtra("url", videoUrl);
                            ((Activity) context).startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_PLAY_VIDEO);
                        }
                    });
                    startOrStopImg.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            playVideo(uVideoView, videoUrl, startOrStopImg);
                        }
                    });
                    uVideoView.setVideoAllCallBack(new GSYSampleCallBack()
                    {
                        @Override
                        public void onPlayError(String url, Object... objects)
                        {
                            super.onPlayError(url, objects);
                        }

                        @Override
                        public void onAutoComplete(String url, Object... objects)
                        {
                            super.onAutoComplete(url, objects);
                            Logger.t(TAG).d("completed");
                            thumbnailFl.setVisibility(View.VISIBLE);
                            uVideoView.setVisibility(View.INVISIBLE);
                            autoBrocastManager.setBroadcastEnable(true);
                            autoBrocastManager.loop();
                            playPro.setProgress(0);
                            startOrStopImg.setImageResource(R.drawable.play_video);
                            startOrStopImg.setTag("completed");
                        }

                        @Override
                        public void onPrepared(String url, Object... objects)
                        {
                            super.onPrepared(url, objects);
                            GSYVideoManager.instance().setNeedMute(false);
                            thumbnailFl.setVisibility(View.GONE);
                            uVideoView.setVisibility(View.VISIBLE);
                            mProgressBar = playPro;
                            playPro.setMax(uVideoView.getDuration());
                            handler.sendEmptyMessage(REFRESH_PROGRESS);
                            isAutoPlay = true;
                            autoBrocastManager.stop();
                            startOrStopImg.setImageResource(R.drawable.stop_video);
                            startOrStopImg.setTag("playing");
                            stopPlayVideo(indicatorView.getCurrentIndex());
                        }
                    });
                    indicatorView.addViewItem(view);
                }
            }
        }
        else
        {
            indicatorView.setVisibility(View.GONE);
        }
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                indicatorView.show();
            }
        }, 0);
    }

    private void playVideo(EmptyControlVideo uVideoView, String videoUrl, ImageView startOrStopImg)
    {
        if (customAlertDialog == null)
        {
            customAlertDialog = new CustomAlertDialog(context)
                    .builder()
                    .setCancelable(false);
        }
        if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
        {
            startOrStopImg.setImageResource(R.drawable.play_video);
            uVideoView.onVideoPause();
        }
        else
        {
            final int netStatus = NetHelper.getNetworkStatus(context);
            String msg = null;
            if (netStatus == -1)//没网
            {
                msg = "当前网络不可用,请先检查网络是否可用";

            }
            else if (netStatus == 1)//非wifi
            {
                if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PAUSE)
                {
                    startOrStopImg.setImageResource(R.drawable.stop_video);
                    uVideoView.onVideoResume();
                }
                else if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
                {
                    startOrStopImg.setImageResource(R.drawable.play_video);
                    uVideoView.onVideoPause();
                }
                if (!TextUtils.isEmpty(videoUrl) && (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE || startOrStopImg.getTag() == null))
                {
                    startOrStopImg.setImageResource(R.drawable.stop_video);
                    uVideoView.setUp(videoUrl,true,"");
                    uVideoView.startPlayLogic();
                }
            }
            else
            {
                msg = "当前为运营商流量使用环境，是否开始播放？";
            }
            if (!TextUtils.isEmpty(msg))
            {
                customAlertDialog.setMsg(msg);
                customAlertDialog.setPositiveButton("确定", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (netStatus == -1)
                        {
                            autoBrocastManager.setBroadcastEnable(true);
                            autoBrocastManager.loop();
                            customAlertDialog.dismiss();
                        }
                        else
                        {
                            if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PAUSE)
                            {
                                startOrStopImg.setImageResource(R.drawable.stop_video);
                                uVideoView.onVideoResume();
                            }
                            else if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
                            {
                                startOrStopImg.setImageResource(R.drawable.play_video);
                                uVideoView.onVideoPause();
                            }
                            if (!TextUtils.isEmpty(videoUrl) && (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE || startOrStopImg.getTag() == null))
                            {
                                startOrStopImg.setImageResource(R.drawable.stop_video);
                                uVideoView.setUp(videoUrl,true,"");
                                uVideoView.startPlayLogic();
                            }
                        }
                    }
                });
                customAlertDialog.setNegativeButton("取消", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        customAlertDialog.dismiss();
                        autoBrocastManager.setBroadcastEnable(true);
                        autoBrocastManager.loop();
                    }
                });
                isAutoPlay = true;
                autoBrocastManager.stop();
                customAlertDialog.show();
            }
        }
    }

    private final int REFRESH_PROGRESS = 101;
    private ProgressBar mProgressBar;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case REFRESH_PROGRESS:
                    View view = videoViewMap.get(indicatorView.getCurrentIndex());
                    if (mProgressBar != null && view != null)
                    {
                        EmptyControlVideo uVideoView = view.findViewById(R.id.uvideo_view);
                        if (uVideoView != null)
                        {
                            mProgressBar.setProgress(uVideoView.getCurrentPositionWhenPlaying());
                            sendEmptyMessageDelayed(REFRESH_PROGRESS, 100);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.RestaurantC_rPraise:
                ToastUtils.showShort("点赞失败");
                break;
            case NetInterfaceConstant.RestaurantC_delRPraise:
                ToastUtils.showShort("取消点赞失败");
                break;
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(context, null, exceptSource, e);
    }

    @Override
    public void setPraiseInfoCallback(String response, final int position)
    {
        Logger.t("测试点赞").d("测试点赞--> " + response);
        try
        {
            JSONObject jsonObject1 = new JSONObject(response);
            String rId = jsonObject1.getString("rId");
            String rPraise = jsonObject1.getString("rPraise");
            JSONArray jsonArray = jsonObject1.getJSONArray("resPraiseList");
            String[] headers = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                headers[i] = jsonArray.get(i).toString();
            }
            Object itemData = resList.get(position);
            if (itemData instanceof RestaurantBean)
            {
                RestaurantBean restaurantBean = (RestaurantBean) itemData;
                restaurantBean.setResPraiseList(headers);
                restaurantBean.setPraisedOrNot("1");
                if (Integer.parseInt(rPraise) > 100)
                {
                    rPraise = "99";
                }
                restaurantBean.setrPraise(rPraise);
            }
            ToastUtils.showShort("点赞成功");
            notifyDataSetChanged();
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    @Override
    public void setUnPraiseInfoCallback(String response, final int position)
    {
        try
        {
            JSONObject jsonObject1 = new JSONObject(response);
            String rPraise = jsonObject1.getString("rPraise");
            JSONArray jsonArray = jsonObject1.getJSONArray("resPraiseList");
            String[] headers = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                headers[i] = jsonArray.get(i).toString();
            }
            Object itemData = resList.get(position);
            if (itemData instanceof RestaurantBean)
            {
                RestaurantBean restaurantBean = (RestaurantBean) itemData;
                restaurantBean.setResPraiseList(headers);
                restaurantBean.setPraisedOrNot("0");
                if (Integer.parseInt(rPraise) > 100)
                {
                    rPraise = "99";
                }
                restaurantBean.setrPraise(rPraise);
            }

            ToastUtils.showShort("取消点赞");
            notifyDataSetChanged();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setList(List<RestaurantBean> list)
    {
        this.resList = list;
        for (int i = 0; i < list.size(); i++)
        {
            Object object = list.get(i);
            if (object instanceof RestaurantBean)
            {
                RestaurantBean restaurantBean = (RestaurantBean) object;
                Logger.t(TAG).d("元素--> " + restaurantBean.getrName() + " , " + restaurantBean.getrStar());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
    }

    public void setMealsItemClick(ResLstInfoAdapter.MealsItemClick mealsItemClick)
    {
        this.mealsItemClick = mealsItemClick;
    }

    public interface MealsItemClick
    {
        //点击进入餐厅详情
        void contentClick(RestaurantBean itemBean);
    }
}