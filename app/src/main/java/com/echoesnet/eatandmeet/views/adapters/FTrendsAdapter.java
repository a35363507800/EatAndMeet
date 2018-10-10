package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.ShowLocationAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.bean.GameItemBean;
import com.echoesnet.eatandmeet.models.bean.UnFocusVUserBean;
import com.echoesnet.eatandmeet.models.bean.UnFocusVuserItemBean;
import com.echoesnet.eatandmeet.models.datamodel.ImageDisposalType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.GlideRequests;
import com.echoesnet.eatandmeet.utils.GlideRoundTransform;
import com.echoesnet.eatandmeet.views.widgets.FolderTextView;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.video.MultiSampleVideo;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;

import java.util.ArrayList;
import java.util.List;

import static com.echoesnet.eatandmeet.utils.GlideOptions.bitmapTransform;


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
public class FTrendsAdapter extends BaseAdapter
{
    public static final String TAG = FTrendsAdapter.class.getSimpleName();

    //private static final int TYPE_GAME=0;
    private static final int TYPE_LIVE = 0;
    private static final int TYPE_TRENDS_COLUMN = 1;
    private static final int TYPE_TRENDS_VIDEO_H = 2;
    private static final int TYPE_TRENDS_VIDEO_V = 3;
    private static final int TYPE_TRENDS_TEXT = 4;
    private static final int TYPE_TRENDS_PIC_1_H = 5;
    private static final int TYPE_TRENDS_PIC_1_V = 6;
    private static final int TYPE_TRENDS_PIC_2 = 7;
    private static final int TYPE_TRENDS_PIC_3 = 8;
    private static final int TYPE_TRENDS_PIC_4 = 9;
    private static final int TYPE_TRENDS_PIC_5 = 10;
    private static final int TYPE_TRENDS_PIC_6 = 11;

    private Activity mActivity;
    private List<FTrendsItemBean> fTrendsItemBeanList;
    private TrendsItemClick trendsItemClick;
    private RecyclerView gameRv;//游戏列表
    private TrendsGameListAdapter gameListAdapter;
    private boolean refreshPraise = false;
    private List<GameItemBean> gameItemBeanList = new ArrayList<>();

    public FTrendsAdapter(Activity mActivity, List<FTrendsItemBean> fTrendsItemBeanList)
    {
        this.mActivity = mActivity;
        this.fTrendsItemBeanList = fTrendsItemBeanList;
        // initBanner();
    }

    public void setTrendsItemClick(TrendsItemClick trendsItemClick)
    {
        this.trendsItemClick = trendsItemClick;
    }

    @Override
    public int getCount()
    {
        return fTrendsItemBeanList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return fTrendsItemBeanList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getViewTypeCount()
    {
        return 12;
    }

    @Override
    public int getItemViewType(int position)
    {
        Object object = fTrendsItemBeanList.get(position);
//        if (object instanceof List && position == 0)
//        {
//            return TYPE_GAME;
//        }
        if (object instanceof FTrendsItemBean)
        {
            FTrendsItemBean itemBean = (FTrendsItemBean) object;
            if ("0".equals(itemBean.getType())) //普通动态
            {
                if (!TextUtils.isEmpty(itemBean.getThumbnails()))
                {
                    if ("0".equals(itemBean.getShowType()))
                        return TYPE_TRENDS_VIDEO_H;
                    else
                        return TYPE_TRENDS_VIDEO_V;
                } else if (!TextUtils.isEmpty(itemBean.getUrl()))
                {
                    List<String> pics = CommonUtils.strWithSeparatorToList(itemBean.getUrl(), CommonUtils.SEPARATOR);
                    switch (pics.size())
                    {
                        case 1:
                            if ("0".equals(itemBean.getShowType()))
                                return TYPE_TRENDS_PIC_1_H;
                            else
                                return TYPE_TRENDS_PIC_1_V;
                        case 2:
                            return TYPE_TRENDS_PIC_2;
                        case 3:
                            return TYPE_TRENDS_PIC_3;
                        case 4:
                            return TYPE_TRENDS_PIC_4;
                        case 5:
                            return TYPE_TRENDS_PIC_5;
                        case 6:
                            return TYPE_TRENDS_PIC_6;
                    }
                } else
                {
                    return TYPE_TRENDS_TEXT;
                }
            } else if ("1".equals(itemBean.getType()) || "2".equals(itemBean.getType()) || "3".equals(itemBean.getType()))// 餐厅 直播  游戏动态
            {
                return TYPE_LIVE;
            }else if ("7".equals(itemBean.getType()) || "4".equals(itemBean.getType()) ||
                    "5".equals(itemBean.getType()) || "6".equals(itemBean.getType())) // 4 专栏 5 活动 7 banner活动
            {
                return TYPE_TRENDS_COLUMN;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LiveViewHolder liveViewHolder;
        ColumnViewHolder columnViewHolder;
        final VideoHViewHolder videoHViewHolder;
        final VideoVViewHolder videoVViewHolder;
        Pic1HViewHolder pic1HViewHolder;
        Pic1VViewHolder pic1VViewHolder;
        Pic2ViewHolder pic2ViewHolder;
        Pic3ViewHolder pic3ViewHolder;
        Pic4ViewHolder pic4ViewHolder;
        Pic5ViewHolder pic5ViewHolder;
        Pic6ViewHolder pic6ViewHolder;

        LevelHeaderView imgHead = null;
        TextView tvNickName = null;
        TextView tvTime = null;
        TextView tvDistance = null;
        GenderView tvSex = null;
        LevelView tvLevel = null;
        FolderTextView tvContent = null;
        IconTextView tvPraise = null;
        IconTextView tvComment = null;
        TextView tvAddress = null;
        LinearLayout addressLL = null;
        TextView tvReadNum = null;
        final List<String> pics;
        int viewType = getItemViewType(position);
        FTrendsItemBean itemBean = fTrendsItemBeanList.get(position);
        // FTrendsItemBean itemBean = null;
        // if (viewType != TYPE_GAME)
        // itemBean = (FTrendsItemBean) object;
        switch (viewType)
        {
//            case TYPE_GAME:
//                List<GameItemBean> gameList = (List<GameItemBean>) object;
//                if (gameItemBeanList.size()!=gameList.size())//只有当数量变化时候刷新，如果是里面的item变化等待下次页面初始化时候刷新，优先保证性能--wb
//                {
//                    gameItemBeanList.clear();
//                    gameItemBeanList.addAll(gameList);
//                    if (gameListAdapter != null)
//                        gameListAdapter.notifyDataSetChanged();
//                }
//                return gameRv==null?new RecyclerView(mActivity):gameRv;
            case TYPE_LIVE:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_live, null);
                    liveViewHolder = new LiveViewHolder();
                    liveViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    liveViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    liveViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    liveViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    liveViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    liveViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    liveViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    liveViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    liveViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    liveViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    liveViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    liveViewHolder.coverFl = (FrameLayout) convertView.findViewById(R.id.fl_cover);
                    liveViewHolder.coverRiv = (RoundedImageView) convertView.findViewById(R.id.riv_cover);
                    liveViewHolder.livingImg = (ImageView) convertView.findViewById(R.id.img_living);
                    liveViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(liveViewHolder);
                } else
                {
                    liveViewHolder = (LiveViewHolder) convertView.getTag();
                }
                imgHead = liveViewHolder.imgHead;
                tvNickName = liveViewHolder.tvNickName;
                tvTime = liveViewHolder.tvTime;
                tvDistance = liveViewHolder.tvDistance;
                tvLevel = liveViewHolder.tvLevel;
                tvContent = liveViewHolder.tvContent;
                tvSex = liveViewHolder.tvSex;
                tvPraise = liveViewHolder.tvPraise;
                tvComment = liveViewHolder.tvComment;
                tvAddress = liveViewHolder.tvAddress;
                addressLL = liveViewHolder.addressLL;
                tvReadNum = liveViewHolder.readNum;

                if ("2".equals(itemBean.getType()))
                {
                    liveViewHolder.livingImg.setVisibility(View.VISIBLE);
                    if ("1".equals(itemBean.getExt().getLiveStatus()))
                        liveViewHolder.livingImg.setImageResource(R.drawable.encounter_living_ico);
                    else
                    {
                        if (!TextUtils.isEmpty(itemBean.getExt().getVedio()))
                            liveViewHolder.livingImg.setImageResource(R.drawable.live_playback);
                        else
                            liveViewHolder.livingImg.setImageResource(R.drawable.live_end);
                    }
                } else
                {
                    liveViewHolder.livingImg.setVisibility(View.GONE);
                }
                GlideApp.with(mActivity)
                        .load(itemBean.getUrl())
                        .centerCrop()
                        .placeholder(R.drawable.qs_550_260)
                        .error(R.drawable.qs_550_260)
                        .into(liveViewHolder.coverRiv);
                final FTrendsItemBean finalItemBean5 = itemBean;
                liveViewHolder.coverFl.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (trendsItemClick != null)
                            trendsItemClick.contentClick(finalItemBean5);
                    }
                });
                break;
            case TYPE_TRENDS_COLUMN:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_column, null);
                    columnViewHolder = new ColumnViewHolder();
                    columnViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    columnViewHolder.rivColumn = (RoundedImageView) convertView.findViewById(R.id.riv_column);
                    columnViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    columnViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    columnViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    columnViewHolder.tvColumnTitle = (TextView) convertView.findViewById(R.id.tv_column_title);
                    columnViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    columnViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    columnViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    columnViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    columnViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    columnViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    columnViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    columnViewHolder.llColumn = (LinearLayout) convertView.findViewById(R.id.ll_column);
                    columnViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(columnViewHolder);
                } else
                {
                    columnViewHolder = (ColumnViewHolder) convertView.getTag();
                }

                imgHead = columnViewHolder.imgHead;
                tvNickName = columnViewHolder.tvNickName;
                tvTime = columnViewHolder.tvTime;
                tvDistance = columnViewHolder.tvDistance;
                tvLevel = columnViewHolder.tvLevel;
                tvContent = columnViewHolder.tvContent;
                tvSex = columnViewHolder.tvSex;
                tvPraise = columnViewHolder.tvPraise;
                tvComment = columnViewHolder.tvComment;
                tvAddress = columnViewHolder.tvAddress;
                addressLL = columnViewHolder.addressLL;
                tvReadNum = columnViewHolder.readNum;

                FTrendsItemBean.ExtBean extBean = itemBean.getExt();
                if (extBean != null)
                {
                    GlideApp.with(mActivity)
                            .asBitmap()
                            .load(itemBean.getUrl())
                            .centerCrop()
                            .placeholder(R.drawable.qs_550_260)
                            .error(R.drawable.qs_550_260)
                            .into(columnViewHolder.rivColumn);
                    if ("4".equals(itemBean.getType()))
                    {
                        String columnTitle = extBean.getColumnName() == null ? "" : extBean.getTitle();
                        SpannableString spannableString = new SpannableString(columnTitle);
//                        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.C0313)), 0,
//                                extBean.getColumnName() != null ? extBean.getColumnName().length() : 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        columnViewHolder.tvColumnTitle.setText(spannableString);
                    }
                    else
                    {
                        String title ;
                        switch (itemBean.getType())
                        {
                            case "6":
                                title = extBean.getHpName();
                                break;
                            case "7":
                                title = extBean.getTrendsDesc();
                                break;
                            default:
                                title = extBean.getColumnName();
                                break;
                        }
                        columnViewHolder.tvColumnTitle.setText(title);
                    }
                }
                final FTrendsItemBean finalItemBean4 = itemBean;
                columnViewHolder.llColumn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (trendsItemClick != null)
                            trendsItemClick.contentClick(finalItemBean4);
                    }
                });

                break;
            case TYPE_TRENDS_VIDEO_H:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_video_h, parent, false);
                    videoHViewHolder = new VideoHViewHolder();
                    videoHViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    videoHViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    videoHViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    videoHViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    videoHViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    videoHViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    videoHViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    videoHViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    videoHViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    videoHViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    videoHViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    videoHViewHolder.uVideoView =  convertView.findViewById(R.id.ftrends_uvideo_view);
                    videoHViewHolder.imgThumbnail = (ImageView) convertView.findViewById(R.id.img_thumbnail);
                    videoHViewHolder.flThumbnail = (FrameLayout) convertView.findViewById(R.id.fl_thumbnail);
                    videoHViewHolder.flContentVideo = (FrameLayout) convertView.findViewById(R.id.fl_content_video);
                    videoHViewHolder.startTv = (IconTextView) convertView.findViewById(R.id.tv_start);
                    videoHViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(videoHViewHolder);
                } else
                {
                    videoHViewHolder = (VideoHViewHolder) convertView.getTag();
                }

                imgHead = videoHViewHolder.imgHead;
                tvNickName = videoHViewHolder.tvNickName;
                tvTime = videoHViewHolder.tvTime;
                tvDistance = videoHViewHolder.tvDistance;
                tvLevel = videoHViewHolder.tvLevel;
                tvContent = videoHViewHolder.tvContent;
                tvSex = videoHViewHolder.tvSex;
                tvPraise = videoHViewHolder.tvPraise;
                tvComment = videoHViewHolder.tvComment;
                tvAddress = videoHViewHolder.tvAddress;
                addressLL = videoHViewHolder.addressLL;
                tvReadNum = videoHViewHolder.readNum;

                //播放器初始化
                videoHViewHolder.uVideoView.setTag(0);
                videoHViewHolder.uVideoView.setPlayTag(TAG);
                videoHViewHolder.uVideoView.setPlayPosition(position);
                videoHViewHolder.uVideoView.setRotateViewAuto(false);
                videoHViewHolder.uVideoView.setLockLand(false);
                videoHViewHolder.uVideoView.setReleaseWhenLossAudio(false);
                videoHViewHolder.uVideoView.setShowFullAnimation(false);
                videoHViewHolder.uVideoView.setIsTouchWiget(false);
                videoHViewHolder.uVideoView.setNeedLockFull(false);
                boolean isPlaying = videoHViewHolder.uVideoView.getCurrentPlayer().isInPlayingState();

                if (!isPlaying) {
                    videoHViewHolder.uVideoView.setUpLazy(itemBean.getUrl(), false, null, null, null);
                }
                videoHViewHolder.uVideoView.setVideoAllCallBack(
                        new GSYSampleCallBack()
                        {
                            @Override
                            public void onPrepared(String url, Object... objects)
                            {
                                super.onPrepared(url, objects);
                                if (!videoHViewHolder.uVideoView.isIfCurrentIsFullscreen()) {
                                    //静音
                                    GSYVideoManager.instance().setNeedMute(true);
                                    videoHViewHolder.flThumbnail.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onAutoComplete(String url, Object... objects)
                            {
                                super.onAutoComplete(url, objects);
                                videoHViewHolder.flThumbnail.setVisibility(View.VISIBLE);
                            }
                        });
                videoHViewHolder.imgThumbnail.setTag("");
                GlideApp.with(EamApplication.getInstance())
                        .load(itemBean.getThumbnails())
                        .centerCrop()
                        .placeholder(R.drawable.qs_4_3)
                        .error(R.drawable.qs_4_3)
                        .into(videoHViewHolder.imgThumbnail);
                videoHViewHolder.flContentVideo.setVisibility(View.VISIBLE);
                if (!videoHViewHolder.uVideoView.isInPlayingState())
                    videoHViewHolder.flThumbnail.setVisibility(View.VISIBLE);
                videoHViewHolder.flThumbnail.setTag(position);
                videoHViewHolder.startTv.setTag(itemBean.getShowType());
                final FTrendsItemBean finalItemBean2 = itemBean;
                videoHViewHolder.flContentVideo.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (videoHViewHolder.uVideoView != null && trendsItemClick != null)
                        {
                            trendsItemClick.videoClick(videoHViewHolder.flContentVideo, position, finalItemBean2);
                        }
                    }
                });
                videoHViewHolder.flThumbnail.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (videoHViewHolder.uVideoView != null && trendsItemClick != null)
                        {
                            trendsItemClick.videoClick(videoHViewHolder.flContentVideo, position, finalItemBean2);
                        }
                    }
                });
                break;
            case TYPE_TRENDS_VIDEO_V:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_video_v, parent, false);
                    videoVViewHolder = new VideoVViewHolder();
                    videoVViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    videoVViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    videoVViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    videoVViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    videoVViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    videoVViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    videoVViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    videoVViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    videoVViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    videoVViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    videoVViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    videoVViewHolder.uVideoView =  convertView.findViewById(R.id.ftrends_uvideo_view);
                    videoVViewHolder.imgThumbnail = (ImageView) convertView.findViewById(R.id.img_thumbnail);
                    videoVViewHolder.flThumbnail = (FrameLayout) convertView.findViewById(R.id.fl_thumbnail);
                    videoVViewHolder.flContentVideo = (FrameLayout) convertView.findViewById(R.id.fl_content_video);
                    videoVViewHolder.startTv = (IconTextView) convertView.findViewById(R.id.tv_start);
                    videoVViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(videoVViewHolder);
                } else
                {
                    videoVViewHolder = (VideoVViewHolder) convertView.getTag();
                }
                imgHead = videoVViewHolder.imgHead;
                tvNickName = videoVViewHolder.tvNickName;
                tvTime = videoVViewHolder.tvTime;
                tvDistance = videoVViewHolder.tvDistance;
                tvLevel = videoVViewHolder.tvLevel;
                tvContent = videoVViewHolder.tvContent;
                tvSex = videoVViewHolder.tvSex;
                tvPraise = videoVViewHolder.tvPraise;
                tvComment = videoVViewHolder.tvComment;
                tvAddress = videoVViewHolder.tvAddress;
                addressLL = videoVViewHolder.addressLL;
                tvReadNum = videoVViewHolder.readNum;


                //播放器初始化
                videoVViewHolder.uVideoView.setTag(0);
                videoVViewHolder.uVideoView.setPlayTag(TAG);
                videoVViewHolder.uVideoView.setPlayPosition(position);
                videoVViewHolder.uVideoView.setRotateViewAuto(false);
                videoVViewHolder.uVideoView.setLockLand(false);
                videoVViewHolder.uVideoView.setReleaseWhenLossAudio(false);
                videoVViewHolder.uVideoView.setShowFullAnimation(false);
                videoVViewHolder.uVideoView.setIsTouchWiget(false);
                videoVViewHolder.uVideoView.setNeedLockFull(false);
                boolean isVPlaying = videoVViewHolder.uVideoView.getCurrentPlayer().isInPlayingState();

                if (!isVPlaying) {
                    videoVViewHolder.uVideoView.setUpLazy(itemBean.getUrl(), false, null, null, null);
                }
                videoVViewHolder.uVideoView.setVideoAllCallBack(
                        new GSYSampleCallBack()
                        {
                            @Override
                            public void onPrepared(String url, Object... objects)
                            {
                                super.onPrepared(url, objects);
                                if (!videoVViewHolder.uVideoView.isIfCurrentIsFullscreen()) {
                                    //静音
                                    GSYVideoManager.instance().setNeedMute(true);
                                    videoVViewHolder.flThumbnail.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onAutoComplete(String url, Object... objects)
                            {
                                super.onAutoComplete(url, objects);
                                videoVViewHolder.flThumbnail.setVisibility(View.VISIBLE);
                            }
                        });
                videoVViewHolder.imgThumbnail.setTag("");
                GlideApp.with(EamApplication.getInstance())
                        .load(itemBean.getThumbnails())
                        .centerCrop()
                        .placeholder(R.drawable.qs_3_4)
                        .error(R.drawable.qs_3_4)
                        .into(videoVViewHolder.imgThumbnail);
                videoVViewHolder.flContentVideo.setVisibility(View.VISIBLE);
                videoVViewHolder.flThumbnail.setVisibility(View.VISIBLE);
                videoVViewHolder.flThumbnail.setTag(position);
                videoVViewHolder.startTv.setTag(itemBean.getShowType());
                final FTrendsItemBean finalItemBean3 = itemBean;
                videoVViewHolder.flContentVideo.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (videoVViewHolder.uVideoView != null && trendsItemClick != null)
                        {
                            trendsItemClick.videoClick(videoVViewHolder.flContentVideo, position, finalItemBean3);
                        }
                    }
                });
                videoVViewHolder.flThumbnail.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (videoVViewHolder.uVideoView != null && trendsItemClick != null)
                        {
                            trendsItemClick.videoClick(videoVViewHolder.flContentVideo, position, finalItemBean3);
                        }
                    }
                });
                videoVViewHolder.uVideoView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (videoVViewHolder.uVideoView != null && trendsItemClick != null)
                        {
                            trendsItemClick.videoClick(videoVViewHolder.flContentVideo, position, finalItemBean3);
                        }
                    }
                });
                break;
            case TYPE_TRENDS_TEXT:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_pic_1_h, parent, false);
                    pic1HViewHolder = new Pic1HViewHolder();
                    pic1HViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    pic1HViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    pic1HViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    pic1HViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    pic1HViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    pic1HViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    pic1HViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    pic1HViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    pic1HViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    pic1HViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    pic1HViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    pic1HViewHolder.riv1 = convertView.findViewById(R.id.img_view);
                    pic1HViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(pic1HViewHolder);
                } else
                {
                    pic1HViewHolder = (Pic1HViewHolder) convertView.getTag();
                }
                imgHead = pic1HViewHolder.imgHead;
                tvNickName = pic1HViewHolder.tvNickName;
                tvTime = pic1HViewHolder.tvTime;
                tvDistance = pic1HViewHolder.tvDistance;
                tvLevel = pic1HViewHolder.tvLevel;
                tvContent = pic1HViewHolder.tvContent;
                tvSex = pic1HViewHolder.tvSex;
                tvPraise = pic1HViewHolder.tvPraise;
                tvComment = pic1HViewHolder.tvComment;
                tvAddress = pic1HViewHolder.tvAddress;
                addressLL = pic1HViewHolder.addressLL;
                tvReadNum = pic1HViewHolder.readNum;

                pic1HViewHolder.riv1.setVisibility(View.GONE);
                break;
            case TYPE_TRENDS_PIC_1_H:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_pic_1_h, parent, false);
                    pic1HViewHolder = new Pic1HViewHolder();
                    pic1HViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    pic1HViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    pic1HViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    pic1HViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    pic1HViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    pic1HViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    pic1HViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    pic1HViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    pic1HViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    pic1HViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    pic1HViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    pic1HViewHolder.riv1 = convertView.findViewById(R.id.img_view);
                    pic1HViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(pic1HViewHolder);
                } else
                {
                    pic1HViewHolder = (Pic1HViewHolder) convertView.getTag();
                }
                imgHead = pic1HViewHolder.imgHead;
                tvNickName = pic1HViewHolder.tvNickName;
                tvTime = pic1HViewHolder.tvTime;
                tvDistance = pic1HViewHolder.tvDistance;
                tvLevel = pic1HViewHolder.tvLevel;
                tvContent = pic1HViewHolder.tvContent;
                tvSex = pic1HViewHolder.tvSex;
                tvPraise = pic1HViewHolder.tvPraise;
                tvComment = pic1HViewHolder.tvComment;
                tvAddress = pic1HViewHolder.tvAddress;
                addressLL = pic1HViewHolder.addressLL;
                tvReadNum = pic1HViewHolder.readNum;

                final FTrendsItemBean finalItemBean6 = itemBean;
                pic1HViewHolder.riv1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        refreshReadNum(finalItemBean6);
                        CommonUtils.showImageBrowser(mActivity, CommonUtils.strToList(finalItemBean6.getUrl()), 0, v);
                    }
                });
                String urlByUCloud = CommonUtils.getThumbnailImageUrlByUCloud(itemBean.getUrl(), ImageDisposalType.THUMBNAIL, 8, 213, 160);
                GlideRequests glideRequests = GlideApp.with(mActivity);
                if (itemBean.getUrl().endsWith(".gif"))
                {
                    glideRequests.asGif()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                    urlByUCloud = itemBean.getUrl();
                } else
                {
                    glideRequests.asBitmap();
                }
                glideRequests.load(urlByUCloud)
                        .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                        .placeholder(R.drawable.qs_4_3)
                        .error(R.drawable.qs_4_3)
                        .into(pic1HViewHolder.riv1);
                break;
            case TYPE_TRENDS_PIC_1_V:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_pic_1_v, parent, false);
                    pic1VViewHolder = new Pic1VViewHolder();
                    pic1VViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    pic1VViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    pic1VViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    pic1VViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    pic1VViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    pic1VViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    pic1VViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    pic1VViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    pic1VViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    pic1VViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    pic1VViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    pic1VViewHolder.riv1 = convertView.findViewById(R.id.img_view);
                    pic1VViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(pic1VViewHolder);
                } else
                {
                    pic1VViewHolder = (Pic1VViewHolder) convertView.getTag();
                }
                imgHead = pic1VViewHolder.imgHead;
                tvNickName = pic1VViewHolder.tvNickName;
                tvTime = pic1VViewHolder.tvTime;
                tvDistance = pic1VViewHolder.tvDistance;
                tvLevel = pic1VViewHolder.tvLevel;
                tvContent = pic1VViewHolder.tvContent;
                tvSex = pic1VViewHolder.tvSex;
                tvPraise = pic1VViewHolder.tvPraise;
                tvComment = pic1VViewHolder.tvComment;
                tvAddress = pic1VViewHolder.tvAddress;
                addressLL = pic1VViewHolder.addressLL;
                tvReadNum = pic1VViewHolder.readNum;

                final FTrendsItemBean finalItemBean7 = itemBean;
                pic1VViewHolder.riv1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        refreshReadNum(finalItemBean7);
                        CommonUtils.showImageBrowser(mActivity, CommonUtils.strToList(finalItemBean7.getUrl()), 0, v);
                    }
                });
                String urlByUCloud1 = CommonUtils.getThumbnailImageUrlByUCloud(itemBean.getUrl(), ImageDisposalType.THUMBNAIL, 8, 160, 213);
                GlideRequests glideRequests1 = GlideApp.with(mActivity);
                if (itemBean.getUrl().endsWith(".gif"))
                {
                    glideRequests1.asGif()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                    urlByUCloud1 = itemBean.getUrl();
                } else
                {
                    glideRequests1.asBitmap();
                }
                glideRequests1.load(urlByUCloud1)
                        .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                        .placeholder(R.drawable.qs_3_4)
                        .error(R.drawable.qs_3_4)
                        .into(pic1VViewHolder.riv1);
                break;
            case TYPE_TRENDS_PIC_2:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_pic_2, parent, false);
                    pic2ViewHolder = new Pic2ViewHolder();
                    pic2ViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    pic2ViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    pic2ViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    pic2ViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    pic2ViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    pic2ViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    pic2ViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    pic2ViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    pic2ViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    pic2ViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    pic2ViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    pic2ViewHolder.riv1 = convertView.findViewById(R.id.img_view1);
                    pic2ViewHolder.riv2 = convertView.findViewById(R.id.img_view2);
                    pic2ViewHolder.llContentPic = (LinearLayout) convertView.findViewById(R.id.ll_content_pic);
                    pic2ViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(pic2ViewHolder);
                } else
                {
                    pic2ViewHolder = (Pic2ViewHolder) convertView.getTag();
                }
                imgHead = pic2ViewHolder.imgHead;
                tvNickName = pic2ViewHolder.tvNickName;
                tvTime = pic2ViewHolder.tvTime;
                tvDistance = pic2ViewHolder.tvDistance;
                tvLevel = pic2ViewHolder.tvLevel;
                tvContent = pic2ViewHolder.tvContent;
                tvSex = pic2ViewHolder.tvSex;
                tvPraise = pic2ViewHolder.tvPraise;
                tvComment = pic2ViewHolder.tvComment;
                tvAddress = pic2ViewHolder.tvAddress;
                addressLL = pic2ViewHolder.addressLL;
                tvReadNum = pic2ViewHolder.readNum;

                pics = CommonUtils.strToList(itemBean.getUrl());
                if (pics != null)
                {
                    for (int i = 0; i < pics.size(); i++)
                    {
                        String url = pics.get(i);
                        String urlByUCloud2 = CommonUtils.getThumbnailImageUrlByUCloud(url, ImageDisposalType.THUMBNAIL, 8, 120, 120);
                        GlideRequests glideRequests2 = GlideApp.with(mActivity);
                        if (url.endsWith(".gif"))
                        {
                            glideRequests2.asGif()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                            urlByUCloud2 = url;
                        } else
                        {
                            glideRequests2.asBitmap();
                        }
                        glideRequests2.load(urlByUCloud2)
                                .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                                .placeholder(R.drawable.qs_talk_fang)
                                .error(R.drawable.qs_talk_fang)
                                .into((ImageView) pic2ViewHolder.llContentPic.getChildAt(i));
                        final FTrendsItemBean finalItemBean8 = itemBean;
                        final int finalI = i;
                        pic2ViewHolder.llContentPic.getChildAt(i).setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                refreshReadNum(finalItemBean8);
                                CommonUtils.showImageBrowser(mActivity, pics, finalI, v);
                            }
                        });
                    }
                }
                break;
            case TYPE_TRENDS_PIC_3:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_pic_3, parent, false);
                    pic3ViewHolder = new Pic3ViewHolder();
                    pic3ViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    pic3ViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    pic3ViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    pic3ViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    pic3ViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    pic3ViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    pic3ViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    pic3ViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    pic3ViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    pic3ViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    pic3ViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    pic3ViewHolder.llContentPic = (LinearLayout) convertView.findViewById(R.id.ll_content_pic);
                    pic3ViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(pic3ViewHolder);
                } else
                {
                    pic3ViewHolder = (Pic3ViewHolder) convertView.getTag();
                }
                imgHead = pic3ViewHolder.imgHead;
                tvNickName = pic3ViewHolder.tvNickName;
                tvTime = pic3ViewHolder.tvTime;
                tvDistance = pic3ViewHolder.tvDistance;
                tvLevel = pic3ViewHolder.tvLevel;
                tvContent = pic3ViewHolder.tvContent;
                tvSex = pic3ViewHolder.tvSex;
                tvPraise = pic3ViewHolder.tvPraise;
                tvComment = pic3ViewHolder.tvComment;
                tvAddress = pic3ViewHolder.tvAddress;
                addressLL = pic3ViewHolder.addressLL;
                tvReadNum = pic3ViewHolder.readNum;

                pics = CommonUtils.strToList(itemBean.getUrl());
                if (pics != null)
                {
                    for (int i = 0; i < pics.size(); i++)
                    {
                        String url = pics.get(i);
                        String urlByUCloud3 = CommonUtils.getThumbnailImageUrlByUCloud(url, ImageDisposalType.THUMBNAIL, 8, 213, 160);
                        GlideRequests glideRequests3 = GlideApp.with(mActivity);
                        if (url.endsWith(".gif"))
                        {
                            glideRequests3.asGif()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                            urlByUCloud3 = url;
                        } else
                        {
                            glideRequests3.asBitmap();
                        }
                        glideRequests3.load(urlByUCloud3)
                                .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                                .placeholder(R.drawable.qs_talk_fang)
                                .error(R.drawable.qs_talk_fang)
                                .into((ImageView) pic3ViewHolder.llContentPic.getChildAt(i));
                        final FTrendsItemBean finalItemBean8 = itemBean;
                        final int finalI = i;
                        pic3ViewHolder.llContentPic.getChildAt(i).setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                refreshReadNum(finalItemBean8);
                                CommonUtils.showImageBrowser(mActivity, pics, finalI, v);
                            }
                        });
                    }
                }
                break;
            case TYPE_TRENDS_PIC_4:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_pic_4, parent, false);
                    pic4ViewHolder = new Pic4ViewHolder();
                    pic4ViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    pic4ViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    pic4ViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    pic4ViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    pic4ViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    pic4ViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    pic4ViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    pic4ViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    pic4ViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    pic4ViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    pic4ViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    pic4ViewHolder.llContentPic1 = (LinearLayout) convertView.findViewById(R.id.ll_content_pic1);
                    pic4ViewHolder.llContentPic2 = (LinearLayout) convertView.findViewById(R.id.ll_content_pic2);
                    pic4ViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(pic4ViewHolder);
                } else
                {
                    pic4ViewHolder = (Pic4ViewHolder) convertView.getTag();
                }

                imgHead = pic4ViewHolder.imgHead;
                tvNickName = pic4ViewHolder.tvNickName;
                tvTime = pic4ViewHolder.tvTime;
                tvDistance = pic4ViewHolder.tvDistance;
                tvLevel = pic4ViewHolder.tvLevel;
                tvContent = pic4ViewHolder.tvContent;
                tvSex = pic4ViewHolder.tvSex;
                tvPraise = pic4ViewHolder.tvPraise;
                tvComment = pic4ViewHolder.tvComment;
                tvAddress = pic4ViewHolder.tvAddress;
                addressLL = pic4ViewHolder.addressLL;
                tvReadNum = pic4ViewHolder.readNum;

                pics = CommonUtils.strToList(itemBean.getUrl());
                FTrendsItemBean finalItemBean9 = itemBean;
                for (int i = 0; i < pic4ViewHolder.llContentPic1.getChildCount(); i++)
                {
                    String url = pics.get(i);
                    String urlByUCloud4 = CommonUtils.getThumbnailImageUrlByUCloud(url, ImageDisposalType.THUMBNAIL, 8, 213, 160);
                    GlideRequests glideRequests4 = GlideApp.with(mActivity);
                    if (url.endsWith(".gif"))
                    {
                        glideRequests4.asGif()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                        urlByUCloud4 = url;
                    } else
                    {
                        glideRequests4.asBitmap();
                    }
                    glideRequests4.load(urlByUCloud4)
                            .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                            .placeholder(R.drawable.qs_talk_fang)
                            .error(R.drawable.qs_talk_fang)
                            .into((ImageView) pic4ViewHolder.llContentPic1.getChildAt(i));
                    final int finalI = i;

                    pic4ViewHolder.llContentPic1.getChildAt(i).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            refreshReadNum(finalItemBean9);
                            CommonUtils.showImageBrowser(mActivity, pics, finalI, v);
                        }
                    });
                }
                for (int i = 0; i < pic4ViewHolder.llContentPic2.getChildCount(); i++)
                {
                    if (pics != null && 2 + i < pics.size())
                    {
                        String url = pics.get(2 + i);
                        String urlByUCloud4 = CommonUtils.getThumbnailImageUrlByUCloud(url, ImageDisposalType.THUMBNAIL, 8, 213, 160);
                        GlideRequests glideRequests4 = GlideApp.with(mActivity);
                        if (url.endsWith(".gif"))
                        {
                            glideRequests4.asGif()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                            urlByUCloud4 = url;
                        } else
                        {
                            glideRequests4.asBitmap();
                        }
                        glideRequests4.load(urlByUCloud4)
                                .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                                .placeholder(R.drawable.qs_talk_fang)
                                .error(R.drawable.qs_talk_fang)
                                .into((ImageView) pic4ViewHolder.llContentPic2.getChildAt(i));
                        final int finalI = 2 + i;
                        pic4ViewHolder.llContentPic2.getChildAt(i).setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                refreshReadNum(finalItemBean9);
                                CommonUtils.showImageBrowser(mActivity, pics, finalI, v);
                            }
                        });
                    }
                }
                break;
            case TYPE_TRENDS_PIC_5:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_pic_5, parent, false);
                    pic5ViewHolder = new Pic5ViewHolder();
                    pic5ViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    pic5ViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    pic5ViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    pic5ViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    pic5ViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    pic5ViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    pic5ViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    pic5ViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    pic5ViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    pic5ViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    pic5ViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    pic5ViewHolder.llContentPic1 = (LinearLayout) convertView.findViewById(R.id.ll_content_pic1);
                    pic5ViewHolder.llContentPic2 = (LinearLayout) convertView.findViewById(R.id.ll_content_pic2);
                    pic5ViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(pic5ViewHolder);
                } else
                {
                    pic5ViewHolder = (Pic5ViewHolder) convertView.getTag();
                }
                imgHead = pic5ViewHolder.imgHead;
                tvNickName = pic5ViewHolder.tvNickName;
                tvTime = pic5ViewHolder.tvTime;
                tvDistance = pic5ViewHolder.tvDistance;
                tvLevel = pic5ViewHolder.tvLevel;
                tvContent = pic5ViewHolder.tvContent;
                tvSex = pic5ViewHolder.tvSex;
                tvPraise = pic5ViewHolder.tvPraise;
                tvComment = pic5ViewHolder.tvComment;
                tvAddress = pic5ViewHolder.tvAddress;
                addressLL = pic5ViewHolder.addressLL;
                tvReadNum = pic5ViewHolder.readNum;

                pics = CommonUtils.strToList(itemBean.getUrl());
                FTrendsItemBean finalItemBean10 = itemBean;
                for (int i = 0; i < pic5ViewHolder.llContentPic1.getChildCount(); i++)
                {
                    String url = pics.get(i);
                    String urlByUCloud5 = CommonUtils.getThumbnailImageUrlByUCloud(url, ImageDisposalType.THUMBNAIL, 8, 213, 160);
                    GlideRequests glideRequests5 = GlideApp.with(mActivity);
                    if (url.endsWith(".gif"))
                    {
                        glideRequests5.asGif()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                        urlByUCloud5 = url;
                    } else
                    {
                        glideRequests5.asBitmap();
                    }
                    glideRequests5.load(urlByUCloud5)
                            .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                            .placeholder(R.drawable.qs_talk_fang)
                            .error(R.drawable.qs_talk_fang)
                            .into((ImageView) pic5ViewHolder.llContentPic1.getChildAt(i));
                    final int finalI = i;
                    pic5ViewHolder.llContentPic1.getChildAt(i).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            refreshReadNum(finalItemBean10);
                            CommonUtils.showImageBrowser(mActivity, pics, finalI, v);
                        }
                    });
                }
                for (int i = 0; i < pic5ViewHolder.llContentPic2.getChildCount(); i++)
                {
                    if (pics != null && 3 + i < pics.size())
                    {
                        String url = pics.get(3 + i);
                        String urlByUCloud5 = CommonUtils.getThumbnailImageUrlByUCloud(url, ImageDisposalType.THUMBNAIL, 8, 213, 160);
                        GlideRequests glideRequests5 = GlideApp.with(mActivity);
                        if (url.endsWith(".gif"))
                        {
                            glideRequests5.asGif()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                            urlByUCloud5 = url;
                        } else
                        {
                            glideRequests5.asBitmap();
                        }
                        glideRequests5.load(urlByUCloud5)
                                .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                                .placeholder(R.drawable.qs_talk_fang)
                                .error(R.drawable.qs_talk_fang)
                                .into((ImageView) pic5ViewHolder.llContentPic2.getChildAt(i));
                        final int finalI = 3 + i;
                        pic5ViewHolder.llContentPic2.getChildAt(i).setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                refreshReadNum(finalItemBean10);
                                CommonUtils.showImageBrowser(mActivity, pics, finalI, v);
                            }
                        });
                    }
                }
                break;
            case TYPE_TRENDS_PIC_6:
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_pic_6, parent, false);
                    pic6ViewHolder = new Pic6ViewHolder();
                    pic6ViewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                    pic6ViewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                    pic6ViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    pic6ViewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    pic6ViewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
                    pic6ViewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
                    pic6ViewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
                    pic6ViewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
                    pic6ViewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                    pic6ViewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
                    pic6ViewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
                    pic6ViewHolder.llContentPic1 = (LinearLayout) convertView.findViewById(R.id.ll_content_pic1);
                    pic6ViewHolder.llContentPic2 = (LinearLayout) convertView.findViewById(R.id.ll_content_pic2);
                    pic6ViewHolder.readNum = (TextView) convertView.findViewById(R.id.read_num_tv);
                    convertView.setTag(pic6ViewHolder);
                } else
                {
                    pic6ViewHolder = (Pic6ViewHolder) convertView.getTag();
                }
                imgHead = pic6ViewHolder.imgHead;
                tvNickName = pic6ViewHolder.tvNickName;
                tvTime = pic6ViewHolder.tvTime;
                tvDistance = pic6ViewHolder.tvDistance;
                tvLevel = pic6ViewHolder.tvLevel;
                tvContent = pic6ViewHolder.tvContent;
                tvSex = pic6ViewHolder.tvSex;
                tvPraise = pic6ViewHolder.tvPraise;
                tvComment = pic6ViewHolder.tvComment;
                tvAddress = pic6ViewHolder.tvAddress;
                addressLL = pic6ViewHolder.addressLL;
                tvReadNum = pic6ViewHolder.readNum;

                pics = CommonUtils.strToList(itemBean.getUrl());
                FTrendsItemBean finalItemBean11 = itemBean;
                for (int i = 0; i < pic6ViewHolder.llContentPic1.getChildCount(); i++)
                {
                    String url = pics.get(i);
                    String urlByUCloud6 = CommonUtils.getThumbnailImageUrlByUCloud(url, ImageDisposalType.THUMBNAIL, 8, 213, 160);
                    GlideRequests glideRequests6 = GlideApp.with(mActivity);
                    if (url.endsWith(".gif"))
                    {
                        glideRequests6.asGif()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                        urlByUCloud6 = url;
                    } else
                    {
                        glideRequests6.asBitmap();
                    }
                    glideRequests6.load(urlByUCloud6)
                            .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                            .placeholder(R.drawable.qs_talk_fang)
                            .error(R.drawable.qs_talk_fang)
                            .into((ImageView) pic6ViewHolder.llContentPic1.getChildAt(i));
                    final int finalI = i;
                    pic6ViewHolder.llContentPic1.getChildAt(i).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            refreshReadNum(finalItemBean11);
                            CommonUtils.showImageBrowser(mActivity, pics, finalI, v);
                        }
                    });
                }
                for (int i = 0; i < pic6ViewHolder.llContentPic2.getChildCount(); i++)
                {
                    String url = pics.get(3 + i);
                    String urlByUCloud6 = CommonUtils.getThumbnailImageUrlByUCloud(url, ImageDisposalType.THUMBNAIL, 8, 213, 160);
                    GlideRequests glideRequests6 = GlideApp.with(mActivity);
                    if (url.endsWith(".gif"))
                    {
                        glideRequests6.asGif()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                        urlByUCloud6 = url;
                    } else
                    {
                        glideRequests6.asBitmap();
                    }
                    glideRequests6.load(urlByUCloud6)
                            .apply(bitmapTransform(new GlideRoundTransform(mActivity, 5)))
                            .placeholder(R.drawable.qs_talk_fang)
                            .error(R.drawable.qs_talk_fang)
                            .into((ImageView) pic6ViewHolder.llContentPic2.getChildAt(i));
                    final int finalI = 3 + i;
                    pic6ViewHolder.llContentPic2.getChildAt(i).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            refreshReadNum(finalItemBean11);
                            CommonUtils.showImageBrowser(mActivity, pics, finalI, v);
                        }
                    });
                }
                break;
        }

        tvReadNum.setText(itemBean.getReadNum() + "人 已读");
        tvNickName.setText(!TextUtils.isEmpty(itemBean.getRemark()) ? itemBean.getRemark() : itemBean.getNicName());
        tvNickName.setTextColor(ContextCompat.getColor(mActivity, "1".equals(itemBean.getIsVuser()) ? R.color.C0313 : R.color.C0321));
        tvDistance.setText("·" + itemBean.getDistance());
        tvLevel.setLevel(itemBean.getLevel(), 1);
        if (!TextUtils.isEmpty(itemBean.getContent()))
        {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.init();
            String type = itemBean.getType();
            tvContent.setSpecialStr("2".equals(type) ? "点击进入" : "3".equals(type) ? "#" + itemBean.getExt().getGameName() + "#" : "#家好月圆#");
            tvContent.setText(itemBean.getContent());
        } else
        {
            tvContent.setVisibility(View.GONE);
        }
        tvSex.setVisibility(View.VISIBLE);
        tvSex.setSex(itemBean.getAge(), itemBean.getSex());
        if (!TextUtils.isEmpty(itemBean.getLocation()))
        {
            addressLL.setVisibility(View.VISIBLE);
            tvAddress.setText(itemBean.getLocation());
            tvAddress.setFocusable(true);
            tvAddress.requestFocus();
            tvAddress.setSelected(true);
            final FTrendsItemBean finalItemBean = itemBean;
            addressLL.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mActivity, ShowLocationAct.class);
                    intent.putExtra("posx", finalItemBean.getPosx());
                    intent.putExtra("posy", finalItemBean.getPosy());
                    intent.putExtra("location", finalItemBean.getLocation());
                    mActivity.startActivity(intent);
                }
            });
        } else
        {
            addressLL.setVisibility(View.GONE);
        }
        tvTime.setText(itemBean.getTimeToNow());
        imgHead.setHeadImageByUrl(itemBean.getPhurl());
        imgHead.showRightIcon(itemBean.getIsVuser());
        if ("1".equals(itemBean.getIsLike()))
            tvPraise.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
        else
            tvPraise.setTextColor(ContextCompat.getColor(mActivity, R.color.C0323));
        tvPraise.setText(String.format("{eam-p-praise @dimen/f2} %s", itemBean.getLikedNum()));
        tvComment.setText(String.format("{eam-e60a @dimen/f2} %s", itemBean.getCommentNum()));

        final FTrendsItemBean finalItemBean1 = itemBean;
        tvContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (trendsItemClick != null)
                    trendsItemClick.contentClick(finalItemBean1);
            }
        });
        imgHead.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", finalItemBean1.getUp());
                mActivity.startActivity(intent);
            }
        });
        final IconTextView finalTvPraise = tvPraise;
        tvPraise.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (trendsItemClick != null)
                    trendsItemClick.praiseClick(finalTvPraise, position, finalItemBean1);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (trendsItemClick != null)
                    trendsItemClick.itemClick(position, finalItemBean1);
                if ("0".equals(itemBean.getType()) && TextUtils.isEmpty(itemBean.getUrl()))
                    refreshReadNum(itemBean);
            }
        });
        return convertView;
    }

    /**
     * 刷新未关注大v列表
     *
     * @param unFocusVUserBean
     */
    private void refreshUnFocusList(final UnFocusVUserBean unFocusVUserBean, View view)
    {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_focus_list);
        List<UnFocusVuserItemBean> focusVuserItemBeanList = unFocusVUserBean.getFocusVuserList();
        if (focusVuserItemBeanList != null && focusVuserItemBeanList.size() > 0)
        {
            for (int i = 0; i < (focusVuserItemBeanList.size() > 1 ? 2 : 1); i++)
            {
                final UnFocusVuserItemBean unFocusVuserItemBean = focusVuserItemBeanList.get(i);
                View unfousItemView = LayoutInflater.from(mActivity).inflate(R.layout.item_unfous_v_view, linearLayout, false);
                LevelHeaderView headRiv = (LevelHeaderView) unfousItemView.findViewById(R.id.riv_head);
                TextView nameTv = (TextView) unfousItemView.findViewById(R.id.tv_name);
                TextView focusTv = (TextView) unfousItemView.findViewById(R.id.tv_focus);
                final int finalI = i;
                focusTv.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (trendsItemClick != null)
                            trendsItemClick.focusClick(finalI);
                    }
                });
                headRiv.setHeadImageByUrl(unFocusVuserItemBean.getPhurl());
                nameTv.setText(unFocusVuserItemBean.getNicName());
                headRiv.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                        intent.putExtra("checkWay", "UId");
                        intent.putExtra("toUId", unFocusVuserItemBean.getUId());
                        intent.putExtra("position", finalI);
                        mActivity.startActivityForResult(intent, EamConstant.EAM_OPEN_CNEW_USER_INFO);

                    }
                });
                linearLayout.addView(unfousItemView);
            }
        }
    }

    private void initBanner()
    {
        gameRv = new RecyclerView(mActivity);
        gameRv.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        gameRv.setLayoutManager(layoutManager);
        gameListAdapter = new TrendsGameListAdapter(mActivity, gameItemBeanList);
        gameRv.setAdapter(gameListAdapter);
    }

    /**
     * 更新内容
     *
     * @param refreshPraise 是否只更新点赞 评论
     */
    public void notifyDataSetChanged(boolean refreshPraise)
    {
        this.refreshPraise = refreshPraise;
        super.notifyDataSetChanged();
    }

    private void refreshReadNum(FTrendsItemBean itemBean)
    {
        if (TextUtils.isEmpty(itemBean.getTId()))
            return;
        CommonUtils.serverIncreaseRead(mActivity, itemBean.getTId(), new ICommonOperateListener()
        {
            @Override
            public void onSuccess(String response)
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        itemBean.setReadNum(response);
                        notifyDataSetChanged(true);
                    }
                }, 700);


            }

            @Override
            public void onError(String code, String msg)
            {

            }
        });
    }

    private class LiveViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        FrameLayout coverFl;
        RoundedImageView coverRiv;
        ImageView livingImg;
        TextView readNum;
    }

    private class ColumnViewHolder
    {
        LevelHeaderView imgHead;
        RoundedImageView rivColumn;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        TextView tvColumnTitle;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        LinearLayout llColumn;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        TextView readNum;
    }

    private class VideoHViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        MultiSampleVideo uVideoView;
        ImageView imgThumbnail;
        FrameLayout flThumbnail;
        FrameLayout flContentVideo;
        IconTextView startTv;
        TextView readNum;
    }

    private class VideoVViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        MultiSampleVideo uVideoView;
        ImageView imgThumbnail;
        FrameLayout flThumbnail;
        FrameLayout flContentVideo;
        IconTextView startTv;
        TextView readNum;
    }

    private class Pic1HViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        ImageView riv1;
        TextView readNum;
    }

    private class Pic1VViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        ImageView riv1;
        TextView readNum;
    }

    private class Pic2ViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        ImageView riv1;
        ImageView riv2;
        LinearLayout llContentPic;
        TextView readNum;
    }

    private class Pic3ViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        LinearLayout llContentPic;
        TextView readNum;
    }

    private class Pic4ViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        LinearLayout llContentPic1;
        LinearLayout llContentPic2;
        TextView readNum;
    }

    private class Pic5ViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        LinearLayout llContentPic1;
        LinearLayout llContentPic2;
        TextView readNum;
    }

    private class Pic6ViewHolder
    {
        LevelHeaderView imgHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        LinearLayout llContentPic1;
        LinearLayout llContentPic2;
        TextView readNum;
    }


    public interface TrendsItemClick
    {
        void praiseClick(TextView tvPraise, int position, FTrendsItemBean itemBean);//点赞

        void commentClick(FTrendsItemBean itemBean);//点击评论按钮

        void videoClick(View view, int position, FTrendsItemBean itemBean);

        void itemClick(int position, FTrendsItemBean itemBean);

        void itemLongClick(int position, FTrendsItemBean itemBean);

        void contentClick(FTrendsItemBean itemBean);

        void focusClick(int position);
    }


}
