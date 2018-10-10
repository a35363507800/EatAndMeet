package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.datamodel.ImageDisposalType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.GlideRequests;
import com.echoesnet.eatandmeet.utils.GlideRoundTransform;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.FolderTextView;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.video.EmptyControlVideo;
import com.echoesnet.eatandmeet.views.widgets.video.MultiSampleVideo;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;


import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.echoesnet.eatandmeet.utils.CommonUtils.getThumbnailImageUrlByUCloud;
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
public class CMomentsAdapter extends BaseAdapter
{
    public final static String TAG = CMomentsAdapter.class.getSimpleName();
    private Activity mActivity;
    private List<FTrendsItemBean> fTrendsItemBeanList;
    private TrendsItemClick trendsItemClick;
    private boolean refreshPraise;
//    private List<VideoPlayView> uVideoViews;
    private GSYVideoOptionBuilder gsyVideoOptionBuilder;
    public CMomentsAdapter(Activity mActivity, List<FTrendsItemBean> fTrendsItemBeanList)
    {
        this.mActivity = mActivity;
        this.fTrendsItemBeanList = fTrendsItemBeanList;
//        uVideoViews = new ArrayList<>();
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
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
    public FTrendsItemBean getItem(int position)
    {
        return fTrendsItemBeanList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        final FTrendsItemBean itemBean = (FTrendsItemBean) fTrendsItemBeanList.get(position);
        if (convertView == null || convertView.getTag() == null)
        {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
            viewHolder.rivColumn = (RoundedImageView) convertView.findViewById(R.id.riv_column);
            viewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
            viewHolder.tvColumnTitle = (TextView) convertView.findViewById(R.id.tv_column_title);
            viewHolder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
            viewHolder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
            viewHolder.tvPraise = (IconTextView) convertView.findViewById(R.id.tv_praise);
            viewHolder.tvComment = (IconTextView) convertView.findViewById(R.id.tv_comment);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
            viewHolder.addressLL = (LinearLayout) convertView.findViewById(R.id.ll_address);
            viewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
            viewHolder.llContentPic = (LinearLayout) convertView.findViewById(R.id.ll_content_pic);
            viewHolder.llColumn = (LinearLayout) convertView.findViewById(R.id.ll_column);
            viewHolder.uVideoView = convertView.findViewById(R.id.ftrends_uvideo_view);
            viewHolder.imgThumbnail = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            viewHolder.flThumbnail = (FrameLayout) convertView.findViewById(R.id.fl_thumbnail);
            viewHolder.flContentVideo = (FrameLayout) convertView.findViewById(R.id.fl_content_video);
            viewHolder.coverFl = (FrameLayout) convertView.findViewById(R.id.fl_cover);
            viewHolder.coverRiv = (RoundedImageView) convertView.findViewById(R.id.riv_cover);
            viewHolder.startTv = (IconTextView) convertView.findViewById(R.id.tv_start);
            viewHolder.livingImg = (ImageView) convertView.findViewById(R.id.img_living);
            viewHolder.vIconImg = (ImageView) convertView.findViewById(R.id.img_v_icon);
            viewHolder.readNumTv = (TextView) convertView.findViewById(R.id.read_num_tv);
//            uVideoViews.add(viewHolder.uVideoView);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (trendsItemClick != null && SharePreUtils.getUId(mActivity).equals(itemBean.getUp()))
                    trendsItemClick.itemLongClick(position, itemBean);
                return false;
            }
        });
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (trendsItemClick != null)
                    trendsItemClick.itemClick(position, itemBean);
            }
        });
        viewHolder.llColumn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (trendsItemClick != null)
                    trendsItemClick.contentClick(itemBean);
            }
        });
        viewHolder.tvContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (trendsItemClick != null)
                    trendsItemClick.contentClick(itemBean);
            }
        });
        viewHolder.imgHead.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", itemBean.getUp());
                mActivity.startActivity(intent);
            }
        });
        viewHolder.tvPraise.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (trendsItemClick != null)
                    trendsItemClick.praiseClick(viewHolder.tvPraise, position, itemBean);
            }
        });
        viewHolder.flContentVideo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (viewHolder.uVideoView != null && trendsItemClick != null)
                {

                    trendsItemClick.videoClick(viewHolder.flContentVideo,position, itemBean);
                }
            }
        });
        viewHolder.flThumbnail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (viewHolder.uVideoView != null && trendsItemClick != null)
                {

                    trendsItemClick.videoClick(viewHolder.flContentVideo,position, itemBean);
                }
            }
        });
        viewHolder.uVideoView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (viewHolder.uVideoView != null && trendsItemClick != null)
                {

                    trendsItemClick.videoClick(viewHolder.flContentVideo,position, itemBean);
                }
            }
        });
        if (!refreshPraise)
        {
//            viewHolder.uVideoView.stopPlayback();
//            viewHolder.uVideoView.stopPlayVideo();
            viewHolder.imgThumbnail.setTag("");
            refreshPraise = false;
        }
        viewHolder.uVideoView.setTag(0);
        viewHolder.tvNickName.setTextColor(ContextCompat.getColor(mActivity,"1".equals(itemBean.getIsVuser())?R.color.C0313:R.color.C0321));
        viewHolder.readNumTv.setText(itemBean.getReadNum()+"人 已读");
        if ("0".equals(itemBean.getType())) //普通动态
        {
            viewHolder.coverFl.setVisibility(View.GONE);
            viewHolder.llColumn.setVisibility(View.GONE);
            viewHolder.vIconImg.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(itemBean.getThumbnails()))
            {
                viewHolder.uVideoView.setTag(0);
                viewHolder.uVideoView.setPlayTag(TAG);
                viewHolder.uVideoView.setPlayPosition(position);
                viewHolder.uVideoView.setRotateViewAuto(false);
                viewHolder.uVideoView.setLockLand(false);
                viewHolder.uVideoView.setReleaseWhenLossAudio(false);
                viewHolder.uVideoView.setShowFullAnimation(false);
                viewHolder.uVideoView.setIsTouchWiget(false);
                viewHolder.uVideoView.setNeedLockFull(false);

                boolean isPlaying = viewHolder.uVideoView.getCurrentPlayer().isInPlayingState();

                if (!isPlaying) {
                    viewHolder.uVideoView.setUpLazy(itemBean.getUrl(), false, null, null, null);
                }
                viewHolder.uVideoView.setVideoAllCallBack(
                              new GSYSampleCallBack()
                              {
                                  @Override
                                  public void onPrepared(String url, Object... objects)
                                  {
                                      super.onPrepared(url, objects);
                                      if (!viewHolder.uVideoView.isIfCurrentIsFullscreen()) {
                                          //静音
                                          GSYVideoManager.instance().setNeedMute(true);
                                          viewHolder.flThumbnail.setVisibility(View.GONE);
                                      }
                                  }

                                  @Override
                                  public void onAutoComplete(String url, Object... objects)
                                  {
                                      super.onAutoComplete(url, objects);
                                      viewHolder.flThumbnail.setVisibility(View.VISIBLE);
                                  }
                              });
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .centerCrop()
                        .load(itemBean.getThumbnails())
                        .error(R.drawable.qs_cai_user)
                        .into(viewHolder.imgThumbnail);
                viewHolder.llContentPic.setVisibility(View.GONE);
                viewHolder.flContentVideo.setVisibility(View.VISIBLE);
                viewHolder.flThumbnail.setVisibility(View.VISIBLE);
                viewHolder.startTv.setTag(itemBean.getShowType());
                LinearLayout.LayoutParams layoutParams;
                if ("1".equals(itemBean.getShowType()))
                {
                    layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 160), CommonUtils.dp2px(mActivity, 213));
                    layoutParams.bottomMargin = CommonUtils.dp2px(mActivity,10);
                } else
                {
                    layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 213), CommonUtils.dp2px(mActivity, 160));
                    layoutParams.bottomMargin = CommonUtils.dp2px(mActivity,10);
                }
                viewHolder.flContentVideo.setLayoutParams(layoutParams);
            } else
            {
                viewHolder.flContentVideo.setVisibility(View.GONE);
                viewHolder.llContentPic.setVisibility(View.VISIBLE);
                initContentPic(viewHolder.llContentPic, itemBean.getUrl(), itemBean.getShowType(),itemBean);
            }
        } else if ("4".equals(itemBean.getType()) || "5".equals(itemBean.getType())||
                "6".equals(itemBean.getType())|| "7".equals(itemBean.getType())) // 4 专栏 5 活动 6轰趴 7 banner活动分享
        {
            viewHolder.llColumn.setVisibility(View.VISIBLE);
            viewHolder.flContentVideo.setVisibility(View.GONE);
            viewHolder.llContentPic.setVisibility(View.GONE);
            viewHolder.coverFl.setVisibility(View.GONE);
            FTrendsItemBean.ExtBean extBean = itemBean.getExt();
            if (extBean != null)
            {
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(itemBean.getUrl())
                        .placeholder(R.drawable.qs_550_260)
                        .error(R.drawable.qs_550_260)
                        .centerCrop()
                        .into(viewHolder.rivColumn);
                if ("4".equals(itemBean.getType()))
                {
                    String columnTitle = extBean.getColumnName() == null?"":extBean.getTitle();
                    SpannableString spannableString = new SpannableString(columnTitle);
//                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity,R.color.C0313)),0,extBean.getColumnName() != null ?extBean.getColumnName().length():0
//                            , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.tvColumnTitle.setText(spannableString);
                } else
                {
                    String title;
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
                    viewHolder.tvColumnTitle.setText(title);
                }

            }
        }else
        {
            viewHolder.coverFl.setVisibility(View.VISIBLE);
            viewHolder.flContentVideo.setVisibility(View.GONE);
            viewHolder.llContentPic.setVisibility(View.GONE);
            viewHolder.llColumn.setVisibility(View.GONE);
            viewHolder.vIconImg.setVisibility(View.GONE);
            viewHolder.coverFl.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (trendsItemClick != null)
                        trendsItemClick.contentClick(itemBean);
                }
            });
            if ("2".equals(itemBean.getType()))
            {
                viewHolder.livingImg.setVisibility(View.VISIBLE);
                if ("1".equals(itemBean.getExt().getLiveStatus()))
                    viewHolder.livingImg.setImageResource(R.drawable.encounter_living_ico);
                else
                {
                    if (!TextUtils.isEmpty(itemBean.getExt().getVedio()))
                        viewHolder.livingImg.setImageResource(R.drawable.live_playback);
                    else
                        viewHolder.livingImg.setImageResource(R.drawable.live_end);
                }
            }else
            {
                viewHolder.livingImg.setVisibility(View.GONE);
            }

            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(itemBean.getUrl())
                    .centerCrop()
                    .placeholder(R.drawable.qs_550_260)
                    .error(R.drawable.qs_550_260)
                    .into(viewHolder.coverRiv);
        }
        viewHolder.tvNickName.setText(!TextUtils.isEmpty(itemBean.getRemark())?itemBean.getRemark():itemBean.getNicName());
        viewHolder.tvNickName.setTextColor(ContextCompat.getColor(mActivity,"1".equals(itemBean.getIsVuser())?R.color.C0313:R.color.C0321));
        viewHolder.tvDistance.setText("·" + itemBean.getDistance());
        viewHolder.tvLevel.setLevel(itemBean.getLevel(), 1);
        if (!TextUtils.isEmpty(itemBean.getContent()))
        {
            viewHolder.tvContent.setVisibility(View.VISIBLE);
            viewHolder.tvContent.init();
            String type = itemBean.getType();
            viewHolder.tvContent.setSpecialStr("2".equals(type) ? "点击进入":"3".equals(type)?"#" + itemBean.getExt().getGameName() + "#":"#家好月圆#");
            viewHolder.tvContent.setText(itemBean.getContent());
        } else
        {
            viewHolder.tvContent.setVisibility(View.GONE);
        }
        viewHolder.tvSex.setSex(itemBean.getAge(),itemBean.getSex());
        if (!TextUtils.isEmpty(itemBean.getLocation()))
        {
            viewHolder.addressLL.setVisibility(View.VISIBLE);
            viewHolder.tvAddress.setText(itemBean.getLocation());
            viewHolder.tvAddress.setFocusable(true);
            viewHolder.tvAddress.requestFocus();
            viewHolder.tvAddress.setSelected(true);
            viewHolder.addressLL.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mActivity, ShowLocationAct.class);
                    intent.putExtra("posx", itemBean.getPosx());
                    intent.putExtra("posy", itemBean.getPosy());
                    intent.putExtra("location", itemBean.getLocation());
                    mActivity.startActivity(intent);
                }
            });
        } else
        {
            viewHolder.addressLL.setVisibility(View.GONE);
        }
        viewHolder.tvTime.setText(itemBean.getTimeToNow());
        viewHolder.flThumbnail.setTag(position);
        viewHolder.imgHead.setHeadImageByUrl(itemBean.getPhurl());
        viewHolder.imgHead.showRightIcon(itemBean.getIsVuser());
        if ("1".equals(itemBean.getIsLike()))
            viewHolder.tvPraise.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
        else
            viewHolder.tvPraise.setTextColor(ContextCompat.getColor(mActivity, R.color.C0323));
        viewHolder.tvPraise.setText(String.format("{eam-p-praise @dimen/f2} %s", itemBean.getLikedNum()));
        viewHolder.tvComment.setText(String.format("{eam-e60a @dimen/f2} %s", itemBean.getCommentNum()));
        return convertView;
    }

    private void initContentPic(LinearLayout llContentPic, String url, String showType,FTrendsItemBean itemBean)
    {
        if (TextUtils.isEmpty(url))
        {
            llContentPic.setVisibility(View.GONE);
            return;
        } else
            llContentPic.setVisibility(View.VISIBLE);
        llContentPic.removeAllViews();
        final List<String> pics = CommonUtils.strWithSeparatorToList(url, CommonUtils.SEPARATOR);
        int row = 0, column = pics.size() == 4 ? 2 : 3;
        if (pics.size() == 1)
        {
            ImageView imageView = new ImageView(mActivity);
            LinearLayout.LayoutParams layoutParams;
            if ("1".equals(showType))
                layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 160), CommonUtils.dp2px(mActivity, 213));
            else
                layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 213), CommonUtils.dp2px(mActivity, 160));
            imageView.setLayoutParams(layoutParams);

            String imgUrl= pics.get(0);
            String urlByUCloud = getThumbnailImageUrlByUCloud(imgUrl, ImageDisposalType.THUMBNAIL, 1, 70);
            GlideRequests glideRequests = GlideApp.with(mActivity);
            if (imgUrl.endsWith(".gif"))
            {
                glideRequests.asGif()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                urlByUCloud = imgUrl;
            }
            glideRequests
                    .load(urlByUCloud)
                    .apply(bitmapTransform(new GlideRoundTransform(mActivity,5)))
                    .placeholder("0".equals(showType)?R.drawable.qs_4_3:R.drawable.qs_3_4)
                    .error("0".equals(showType)?R.drawable.qs_4_3:R.drawable.qs_3_4)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (trendsItemClick != null)
                        trendsItemClick.imageClick(itemBean);
                    CommonUtils.showImageBrowser(mActivity, new ArrayList<String>(pics), 0, v);
                }
            });
            llContentPic.addView(imageView);
        } else
        {
            int size = pics.size() > 6 ? 6 : pics.size();
            if (size > column)
            {
                if (size % column == 0)
                    row = size / column;
                else
                    row = (size / column) + 1;
            } else
            {
                row = size == 0 ? 0 : 1;
            }

            for (int i = 0; i < row; i++)
            {
                LinearLayout llrow = new LinearLayout(mActivity);
                llrow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                llrow.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < column; j++)
                {
                    final int position = i * column + j;
                    ImageView imageView = new ImageView(mActivity);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size == 4 ? CommonUtils.dp2px(mActivity, 95) : 0, CommonUtils.dp2px(mActivity, 95));
                    if (size != 4)
                        layoutParams.weight = 1;
                    layoutParams.rightMargin = CommonUtils.dp2px(mActivity, 4);
                    layoutParams.topMargin = CommonUtils.dp2px(mActivity, 4);
                    imageView.setLayoutParams(layoutParams);
                    if (position < size)
                    {
                        imageView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if (trendsItemClick != null)
                                    trendsItemClick.imageClick(itemBean);
                                CommonUtils.showImageBrowser(mActivity, new ArrayList<>(pics), position, v);
                            }
                        });
                        String imgUrl= pics.get(position);
                        String urlByUCloud = CommonUtils.getThumbnailImageUrlByUCloud(imgUrl, ImageDisposalType.THUMBNAIL, 8, 95, 95);
                        GlideRequests glideRequests = GlideApp.with(mActivity);
                        if (imgUrl.endsWith(".gif"))
                        {
                            glideRequests.asGif()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                            urlByUCloud = imgUrl;
                        }
                        glideRequests
                                .load(urlByUCloud)
                                .apply(bitmapTransform(new GlideRoundTransform(mActivity,5)))
                                .placeholder(R.drawable.qs_zb)
                                .error(R.drawable.qs_zb)
                                .into(imageView);
                    }
                    llrow.addView(imageView);
                }
                llContentPic.addView(llrow);
            }
        }

    }

    /**
     * activity destroy 销毁 uVideoView
     */
    public void onDestroy(){
//        for (VideoPlayView uVideoView : uVideoViews)
//        {
//            if (uVideoView != null)
//            {
//                uVideoView.stopPlayVideo();
//                uVideoView.release();
//            }
//        }
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

    private class ViewHolder
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
        LinearLayout llContentPic;
        LinearLayout llColumn;
        IconTextView tvPraise;
        IconTextView tvComment;
        TextView tvAddress;
        LinearLayout addressLL;
        MultiSampleVideo uVideoView;
        ImageView imgThumbnail;
        FrameLayout flThumbnail;
        FrameLayout flContentVideo;
        FrameLayout coverFl;
        RoundedImageView coverRiv;
        IconTextView startTv;
        ImageView livingImg;
        ImageView vIconImg;
        TextView readNumTv;
    }

    public interface TrendsItemClick
    {
        /**
         * 点赞
         *
         * @param position
         * @param itemBean
         */
        void praiseClick(TextView tvPraise, int position, FTrendsItemBean itemBean);

        /**
         * 点击评论按钮
         *
         * @param itemBean
         */
        void commentClick(FTrendsItemBean itemBean);

        void itemClick(int position,FTrendsItemBean itemBean);

        void itemLongClick(int position,FTrendsItemBean itemBean);

        void contentClick(FTrendsItemBean itemBean);

        void videoClick(View view,int position, FTrendsItemBean itemBean);

        void imageClick(FTrendsItemBean itemBean);
    }




}
