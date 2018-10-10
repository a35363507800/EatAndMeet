package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.datamodel.ImageDisposalType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.widgets.FolderTextView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.video.MultiSampleVideo;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;

import java.util.ArrayList;
import java.util.List;

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
public class VMomentsAdapter extends BaseAdapter
{
    public static final String TAG = VMomentsAdapter.class.getSimpleName();
    private Activity mActivity;
    private List<FTrendsItemBean> fTrendsItemBeanList;
    private TrendsItemClick trendsItemClick;
    private boolean refreshPraise;

    public VMomentsAdapter(Activity mActivity, List<FTrendsItemBean> fTrendsItemBeanList)
    {
        this.mActivity = mActivity;
        this.fTrendsItemBeanList = fTrendsItemBeanList;
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_frg_v_ftrends, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imgHead = (LevelHeaderView) convertView.findViewById(R.id.img_head);
            viewHolder.rivColumn = (RoundedImageView) convertView.findViewById(R.id.riv_column);
            viewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
            viewHolder.tvOperate = (TextView) convertView.findViewById(R.id.tv_operate);
            viewHolder.tvColumnTitle = (TextView) convertView.findViewById(R.id.tv_column_title);
            viewHolder.tvContent = (FolderTextView) convertView.findViewById(R.id.tv_content);
            viewHolder.llContentPic = (LinearLayout) convertView.findViewById(R.id.ll_content_pic);
            viewHolder.llColumn = (LinearLayout) convertView.findViewById(R.id.ll_column);
            viewHolder.uVideoView =  convertView.findViewById(R.id.ftrends_uvideo_view);
            viewHolder.imgThumbnail = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            viewHolder.flThumbnail = (FrameLayout) convertView.findViewById(R.id.fl_thumbnail);
            viewHolder.flContentVideo = (FrameLayout) convertView.findViewById(R.id.fl_content_video);
            viewHolder.coverFl = (FrameLayout) convertView.findViewById(R.id.fl_cover);
            viewHolder.coverRiv = (RoundedImageView) convertView.findViewById(R.id.riv_cover);
            viewHolder.startTv = (IconTextView) convertView.findViewById(R.id.tv_start);
            viewHolder.livingImg = (ImageView) convertView.findViewById(R.id.img_living);
            viewHolder.rlItemHead = (RelativeLayout) convertView.findViewById(R.id.img_head_rl);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener((v)->
        {
                if (trendsItemClick != null)
                    trendsItemClick.itemClick(position, itemBean);
        });
        viewHolder.llColumn.setOnClickListener((v)->
        {
                if (trendsItemClick != null)
                    trendsItemClick.contentClick(itemBean);
        });
        viewHolder.tvOperate.setOnClickListener((v)->
        {
                if (trendsItemClick != null)
                    trendsItemClick.operateClick(itemBean);
        });
        viewHolder.tvContent.setOnClickListener((v)->
        {
                if (trendsItemClick != null)
                    trendsItemClick.contentClick(itemBean);
        });
        viewHolder.rlItemHead.setOnClickListener((v)->
        {
                Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", itemBean.getUp());
                mActivity.startActivity(intent);
        });
        if (!refreshPraise)
        {
            viewHolder.uVideoView.onVideoPause();
            viewHolder.imgThumbnail.setTag("");
            refreshPraise = false;
        }
        viewHolder.tvNickName.setTextColor(ContextCompat.getColor(mActivity,"1".equals(itemBean.getIsVuser())?R.color.C0313:R.color.C0321));
        String focus = itemBean.getFocus();
        switch (focus)
        {
            case "0":
                viewHolder.tvOperate.setText("+关注");
                viewHolder.tvOperate.setEnabled(true);
                viewHolder.tvOperate.setGravity(Gravity.CENTER);
                viewHolder.tvOperate.setBackgroundResource(R.drawable.round_cornor_11_c0412_bg);
                viewHolder.tvOperate.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
                break;
            case "1":
                viewHolder.tvOperate.setText("已关注");
                viewHolder.tvOperate.setEnabled(false);
                viewHolder.tvOperate.setGravity(Gravity.CENTER);
                viewHolder.tvOperate.setBackgroundResource(R.color.white);
                viewHolder.tvOperate.setTextColor(ContextCompat.getColor(mActivity, R.color.C0322));
                break;
            default:
                break;
        }

        if ("0".equals(itemBean.getType())) //普通动态
        {
            viewHolder.coverFl.setVisibility(View.GONE);
            viewHolder.llColumn.setVisibility(View.GONE);
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
                        .load(itemBean.getThumbnails())
                        .centerCrop()
                        .error(R.drawable.qs_cai_user)
                        .into(viewHolder.imgThumbnail);
                viewHolder.llContentPic.setVisibility(View.GONE);
                viewHolder.flContentVideo.setVisibility(View.VISIBLE);
                if (!viewHolder.uVideoView.isInPlayingState())
                    viewHolder.flThumbnail.setVisibility(View.VISIBLE);
                viewHolder.startTv.setTag(itemBean.getShowType());
                LinearLayout.LayoutParams layoutParams;
                if ("1".equals(itemBean.getShowType()))
                {
                    layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 160), CommonUtils.dp2px(mActivity, 213));
                } else
                {
                    layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 213), CommonUtils.dp2px(mActivity, 160));
                }
                viewHolder.flContentVideo.setLayoutParams(layoutParams);
            } else
            {
                viewHolder.uVideoView.setTag("");
                viewHolder.flContentVideo.setVisibility(View.GONE);
                viewHolder.llContentPic.setVisibility(View.VISIBLE);
                initContentPic(viewHolder.llContentPic, itemBean.getUrl(), itemBean.getShowType());
            }
        } else if ("4".equals(itemBean.getType()) || "5".equals(itemBean.getType())) // 4 专栏 5 活动
        {
            viewHolder.uVideoView.setTag("");
            viewHolder.llColumn.setVisibility(View.VISIBLE);
            viewHolder.flContentVideo.setVisibility(View.GONE);
            viewHolder.llContentPic.setVisibility(View.GONE);
            viewHolder.coverFl.setVisibility(View.GONE);
            FTrendsItemBean.ExtBean extBean = itemBean.getExt();
            if (extBean != null)
            {
                String url = CommonUtils.getThumbnailImageUrlByUCloud(extBean.getImgUrl(), ImageDisposalType.THUMBNAIL,1,70);
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(url)
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
                }else {
                    viewHolder.tvColumnTitle.setText(extBean.getColumnName());
                }
                viewHolder.tvTime.setText(extBean.getFuckDate());
            }
        }else
        {
            viewHolder.uVideoView.setTag("");
            viewHolder.coverFl.setVisibility(View.VISIBLE);
            viewHolder.flContentVideo.setVisibility(View.GONE);
            viewHolder.llContentPic.setVisibility(View.GONE);
            viewHolder.llColumn.setVisibility(View.GONE);
            viewHolder.coverFl.setOnClickListener((v)->
            {
                    if (trendsItemClick != null)
                        trendsItemClick.contentClick(itemBean);
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

        viewHolder.flThumbnail.setTag(position);
        viewHolder.imgHead.setHeadImageByUrl(itemBean.getPhurl());
        viewHolder.imgHead.showRightIcon(itemBean.getIsVuser());
        return convertView;
    }

    private void initContentPic(LinearLayout llContentPic, String url, String showType)
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
            RoundedImageView imageView = new RoundedImageView(mActivity);
            imageView.setCornerRadius(CommonUtils.dp2px(mActivity, 4));
            LinearLayout.LayoutParams layoutParams;
            if ("1".equals(showType))
                layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 160), CommonUtils.dp2px(mActivity, 213));
            else
                layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mActivity, 213), CommonUtils.dp2px(mActivity, 160));
            imageView.setLayoutParams(layoutParams);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(pics.get(0))
                    .centerCrop()
                    .placeholder(R.drawable.qs_zb)
                    .error(R.drawable.qs_zb)
                    .into(imageView);
            imageView.setOnClickListener((v)->
            {
                    CommonUtils.showImageBrowser(mActivity, new ArrayList<String>(pics), 0, v);
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
                    RoundedImageView imageView = new RoundedImageView(mActivity);
                    imageView.setCornerRadius(10);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size == 4 ? CommonUtils.dp2px(mActivity, 95) : 0, CommonUtils.dp2px(mActivity, 95));
                    if (size != 4)
                        layoutParams.weight = 1;
                    layoutParams.rightMargin = CommonUtils.dp2px(mActivity, 4);
                    layoutParams.topMargin = CommonUtils.dp2px(mActivity, 4);
                    imageView.setLayoutParams(layoutParams);
                    if (position < size)
                    {
                        imageView.setOnClickListener((v)->
                        {
                                CommonUtils.showImageBrowser(mActivity, new ArrayList<>(pics), position, v);
                        });
                        GlideApp.with(EamApplication.getInstance())
                                .asBitmap()
                                .load(pics.get(position))
                                .centerCrop()
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
        TextView tvOperate;
        TextView tvColumnTitle;
        FolderTextView tvContent;
        LinearLayout llContentPic;
        LinearLayout llColumn;
        RelativeLayout rlItemHead;
        MultiSampleVideo uVideoView;
        ImageView imgThumbnail;
        FrameLayout flThumbnail;
        FrameLayout flContentVideo;
        FrameLayout coverFl;
        RoundedImageView coverRiv;
        IconTextView startTv;
        ImageView livingImg;
        TextView tvTime;
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

        void itemClick(int position, FTrendsItemBean itemBean);

        void contentClick(FTrendsItemBean itemBean);

        void operateClick(FTrendsItemBean itemBean);
    }




}
