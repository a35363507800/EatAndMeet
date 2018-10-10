package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.echoesnet.eatandmeet.models.datamodel.ImageDisposalType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
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
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.ss007.swiprecycleview.RefreshRecycleAdapter;

import java.util.ArrayList;
import java.util.List;



import static com.echoesnet.eatandmeet.utils.CommonUtils.getThumbnailImageUrlByUCloud;
import static com.echoesnet.eatandmeet.utils.GlideOptions.bitmapTransform;

/**
 * Created by lc on 2017/7/25 20.
 */

public class TrendsRecycleViewAdapter extends RefreshRecycleAdapter<FTrendsItemBean>
{
    public static final String TAG = TrendsRecycleViewAdapter.class.getSimpleName();
    private List<FTrendsItemBean> fTrendsItemList;
    private Context mActivity;

    private TrendsRecycleViewAdapter.TrendsItemClick trendsItemClick;
    private boolean refreshPraise = false;
    private String isVuser;


    public TrendsRecycleViewAdapter(Context mAct,List<FTrendsItemBean> fTrendsItemList)
    {
        super(fTrendsItemList);
        mActivity = mAct;
        this.fTrendsItemList = fTrendsItemList;
    }

    public void init(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public void setTrendsItemClick(TrendsRecycleViewAdapter.TrendsItemClick trendsItemClick)
    {
        this.trendsItemClick = trendsItemClick;
    }


    @Override
    public RecyclerView.ViewHolder onViewHolderCreate(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_ftrends_info, parent, false);
        return new TrendsRecycleViewAdapter.ViewHolder(view);
    }

    @Override
    public void onViewHolderBind(RecyclerView.ViewHolder holder, int position)
    {
        bindQuestionHolder((TrendsRecycleViewAdapter.ViewHolder) holder, position);
    }

    private void bindQuestionHolder(TrendsRecycleViewAdapter.ViewHolder holder, int position)
    {
        if(position>=getList().size())
            return;
        final TrendsRecycleViewAdapter.ViewHolder viewHolder = (TrendsRecycleViewAdapter.ViewHolder) holder;
        final FTrendsItemBean itemBean = getList().get(position);

        holder.itemView.setOnClickListener((v) ->
        {
            if (trendsItemClick != null)
                trendsItemClick.itemClick(viewHolder.tvReadNums, position, itemBean);
            if ("0".equals(itemBean.getType()) && TextUtils.isEmpty(itemBean.getUrl()))
                refreshReadNum(itemBean);
        });
        viewHolder.tvPraise.setOnClickListener((v) ->
        {
            if (trendsItemClick != null)
                trendsItemClick.praiseClick(viewHolder.tvPraise, position, itemBean);
        });
        viewHolder.ivHead.setOnClickListener((v) ->
        {
            Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
            intent.putExtra("checkWay", "UId");
            intent.putExtra("toUId", itemBean.getUp());
            mActivity.startActivity(intent);
        });
        viewHolder.tvContent.setOnClickListener((v) ->
        {
            if (trendsItemClick != null)
                trendsItemClick.contentClick(viewHolder.tvReadNums, itemBean);
        });


        viewHolder.flThumbnail.setOnClickListener((v) ->
        {
            if (viewHolder.uVideoView != null && trendsItemClick != null)
            {
                trendsItemClick.videoClick(viewHolder.flContentVideo, viewHolder.tvReadNums, position, itemBean, isVuser);
            }
        });
        viewHolder.uVideoView.setOnClickListener((v) ->
        {
            if (viewHolder.uVideoView != null && trendsItemClick != null)
            {
                trendsItemClick.videoClick(viewHolder.flContentVideo, viewHolder.tvReadNums, position, itemBean, isVuser);
            }
        });
        if (!refreshPraise)
        {
          //  viewHolder.uVideoView.onVideoPause();
            viewHolder.imgThumbnail.setTag("");
            refreshPraise = false;
        }
        //是否显示大V标志
        viewHolder.uVideoView.setTag(0);
        viewHolder.tvReadNums.setText(itemBean.getReadNum()+"人 已读");
        if ("0".equals(itemBean.getType()))//普通动态
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
                viewHolder.imgThumbnail.setTag("");
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(itemBean.getThumbnails())
                        .centerCrop()
                        .error(R.drawable.qs_cai_user)
                        .into(viewHolder.imgThumbnail);
                viewHolder.llContentPic.setVisibility(View.GONE);
                viewHolder.flContentVideo.setVisibility(View.VISIBLE);
                if (!viewHolder.uVideoView.isInPlayingState())
                    viewHolder.flThumbnail.setVisibility(View.VISIBLE);
                viewHolder.flThumbnail.setTag(position);
                viewHolder.startTv.setTag(itemBean.getShowType());


                if (viewHolder.uVideoView != null && trendsItemClick != null)
                {
                    Logger.t(TAG).d("flContentVideo.setOnClickListener1>>>");
                    viewHolder.uVideoView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Logger.t(TAG).d("videoClick>>>");
                            trendsItemClick.videoClick(viewHolder.flContentVideo, viewHolder.tvReadNums, position, itemBean, isVuser);
                        }
                    });
                }


/*                viewHolder.flContentVideo.setOnClickListener((v) ->
                {
                    Logger.t(TAG).d("flContentVideo.setOnClickListener>>>");
                    if (viewHolder.uVideoView != null && trendsItemClick != null)
                    {
                        Logger.t(TAG).d("videoClick>>>");
                        trendsItemClick.videoClick(viewHolder.flContentVideo, viewHolder.tvReadNums, position, itemBean, isVuser);
                    }
                });*/

                LinearLayout.LayoutParams layoutParams;

                if ("1".equals(itemBean.getShowType()))
                {
                    layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 160), CommonUtils.dp2px(mActivity, 223));
                } else
                {
                    layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 213), CommonUtils.dp2px(mActivity, 170));
                }
                viewHolder.flContentVideo.setLayoutParams(layoutParams);
            } else
            {
                viewHolder.flContentVideo.setVisibility(View.GONE);
                viewHolder.llContentPic.setVisibility(View.VISIBLE);
                initContentPic(viewHolder.llContentPic, itemBean, itemBean.getUrl(), itemBean.getShowType());
            }
        } else if ("4".equals(itemBean.getType()) || "5".equals(itemBean.getType())||
                "6".equals(itemBean.getType())|| "7".equals(itemBean.getType())) // 4 专栏 5 活动 6 轰趴 7 banner活动分享
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
        }
        else
        {
            viewHolder.coverFl.setVisibility(View.VISIBLE);
            viewHolder.flContentVideo.setVisibility(View.GONE);
            viewHolder.llContentPic.setVisibility(View.GONE);
            viewHolder.llColumn.setVisibility(View.GONE);
            viewHolder.coverFl.setOnClickListener((v) ->
            {
                if (trendsItemClick != null)
                    trendsItemClick.contentClick(viewHolder.tvReadNums, itemBean);
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
            } else
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


        viewHolder.tvNickName.setText(!TextUtils.isEmpty(itemBean.getRemark()) ? itemBean.getRemark() : itemBean.getNicName());
        viewHolder.tvNickName.setTextColor(ContextCompat.getColor(mActivity,"1".equals(isVuser)?R.color.C0313:R.color.C0321));
        viewHolder.tvDistance.setText("·" + itemBean.getDistance());
        viewHolder.tvLevel.setLevel(itemBean.getLevel(), LevelView.USER);
        if (!TextUtils.isEmpty(itemBean.getContent()))
        {
            viewHolder.tvContent.setVisibility(View.VISIBLE);
            viewHolder.tvContent.init();
            String type = itemBean.getType();
            viewHolder.tvContent.setSpecialStr("2".equals(type) ? "点击进入" : "3".equals(type) ? "#" + itemBean.getExt().getGameName() + "#" : "#家好月圆#");
            viewHolder.tvContent.setText(itemBean.getContent());
        } else
        {
            viewHolder.tvContent.setVisibility(View.GONE);
        }

        viewHolder.tvSex.setSex(itemBean.getAge(), itemBean.getSex());

        if (!TextUtils.isEmpty(itemBean.getLocation()))
        {
            viewHolder.addressLL.setVisibility(View.VISIBLE);
            viewHolder.tvAddress.setText(itemBean.getLocation());
            viewHolder.tvAddress.setFocusable(true);
            viewHolder.tvAddress.requestFocus();
            viewHolder.tvAddress.setSelected(true);
            viewHolder.addressLL.setOnClickListener((v) ->
            {
                Intent intent = new Intent(mActivity, ShowLocationAct.class);
                intent.putExtra("posx", itemBean.getPosx());
                intent.putExtra("posy", itemBean.getPosy());
                intent.putExtra("location", itemBean.getLocation());
                mActivity.startActivity(intent);
            });
        } else
        {
            viewHolder.addressLL.setVisibility(View.GONE);
        }


        viewHolder.tvTime.setText(itemBean.getTimeToNow());
        viewHolder.flThumbnail.setTag(position);
        viewHolder.ivHead.setHeadImageByUrl(itemBean.getPhurl());
        viewHolder.ivHead.showRightIcon(isVuser);

        if ("1".equals(itemBean.getIsLike()))
            viewHolder.tvPraise.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
        else
            viewHolder.tvPraise.setTextColor(ContextCompat.getColor(mActivity, R.color.C0323));
        viewHolder.tvPraise.setText(String.format("{eam-p-praise @dimen/f2} %s", itemBean.getLikedNum()));
        viewHolder.tvComment.setText(String.format("{eam-e60a @dimen/f2} %s", itemBean.getCommentNum()));


        viewHolder.tvReadNums.setText(itemBean.getReadNum() + "人 已读");

    }

    private void initContentPic(LinearLayout llContentPic, FTrendsItemBean itemBean, String url, String showType)
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

            imageView.setOnClickListener((v) ->
            {
                refreshReadNum(itemBean);
                CommonUtils.showImageBrowser(mActivity, new ArrayList<>(pics), 0, v);
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
                    row = size / column + 1;
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
                        imageView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                refreshReadNum(itemBean);
                                CommonUtils.showImageBrowser(mActivity, new ArrayList<>(pics), position, v);
                            }
                        });
                    }
                    llrow.addView(imageView);
                }
                llContentPic.addView(llrow);


            }


        }

    }

    private void refreshReadNum(FTrendsItemBean itemBean)
    {
        CommonUtils.serverIncreaseRead(mActivity, itemBean.getTId(), new ICommonOperateListener()
        {
            @Override
            public void onSuccess(String response)
            {
                new Handler().postDelayed(() ->
                {
                    itemBean.setReadNum(response);
                    notifyDataSetChanged(true);
                }, 700);

            }

            @Override
            public void onError(String code, String msg)
            {

            }
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder
    {
        LevelHeaderView ivHead;
        TextView tvNickName;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        FolderTextView tvContent;
        LinearLayout llContentPic;
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
        TextView tvReadNums;
        LinearLayout llColumn;
        RoundedImageView rivColumn;
        TextView tvColumnTitle;
        ImageView vIconImg;
        public ViewHolder(View itemView)
        {
            super(itemView);
            tvColumnTitle = (TextView) itemView.findViewById(R.id.tv_column_title);
            rivColumn = (RoundedImageView) itemView.findViewById(R.id.riv_column);
            llColumn = (LinearLayout) itemView.findViewById(R.id.ll_column);
            ivHead = (LevelHeaderView) itemView.findViewById(R.id.iv_head);
            tvNickName = (TextView) itemView.findViewById(R.id.tv_nickname);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            tvSex = (GenderView) itemView.findViewById(R.id.tv_sex);
            tvLevel = (LevelView) itemView.findViewById(R.id.tv_level);
            tvPraise = (IconTextView) itemView.findViewById(R.id.tv_praise);
            tvComment = (IconTextView) itemView.findViewById(R.id.tv_comment);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            tvContent = (FolderTextView) itemView.findViewById(R.id.tv_content);
            llContentPic = (LinearLayout) itemView.findViewById(R.id.ll_content_pic);
            uVideoView =  itemView.findViewById(R.id.ftrends_uvideo_view);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            flThumbnail = (FrameLayout) itemView.findViewById(R.id.fl_thumbnail);
            flContentVideo = (FrameLayout) itemView.findViewById(R.id.fl_content_video);
            startTv = (IconTextView) itemView.findViewById(R.id.tv_start);
            livingImg = (ImageView) itemView.findViewById(R.id.img_living);
            addressLL = (LinearLayout) itemView.findViewById(R.id.ll_address);
            coverFl = (FrameLayout) itemView.findViewById(R.id.fl_cover);
            coverRiv = (RoundedImageView) itemView.findViewById(R.id.riv_cover);
            vIconImg = (ImageView) itemView.findViewById(R.id.img_v_icon);
            tvReadNums = (TextView) itemView.findViewById(R.id.tv_readNums);

        }

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

        void itemClick(TextView textView, int position, FTrendsItemBean itemBean);

        void videoClick(View view, TextView textView, int position, FTrendsItemBean itemBean, String isVuer);

        void itemLongClick(int position, FTrendsItemBean itemBean);

        void contentClick(TextView textView, FTrendsItemBean itemBean);
    }

    private IOnViewClickListener mViewClickListener;

    public void setIOnViewClickListener(IOnViewClickListener listener)
    {
        this.mViewClickListener = listener;
    }

    public interface IOnViewClickListener
    {
        void onClick(View view, String viewName, FTrendsItemBean parent);
    }
}
