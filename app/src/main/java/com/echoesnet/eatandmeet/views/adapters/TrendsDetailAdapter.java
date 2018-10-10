package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
import com.echoesnet.eatandmeet.activities.TrendsPraiseListAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.CommentsBean;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.bean.UsersBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.GlideRequests;
import com.echoesnet.eatandmeet.utils.GlideRoundTransform;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.ContextMenuDialog;
import com.echoesnet.eatandmeet.views.widgets.FolderTextView;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.ImageOverlayView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.video.MultiSampleVideo;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.echoesnet.eatandmeet.utils.GlideOptions.bitmapTransform;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/13 0013
 * @description
 */
public class TrendsDetailAdapter extends BaseAdapter
{
    private Activity mAct;
    private FTrendsItemBean fTrendsItemBean;
    private List<CommentsBean> commentList;
    private trendsCommentItemClick commentItemClick;
    private boolean refreshDetail;
    private boolean refreshContent = true;
    private View detailView;
    private boolean isShowPraise = false;

    public TrendsDetailAdapter(Activity mAct, FTrendsItemBean fTrendsItemBean, List<CommentsBean> commentList)
    {
        this.mAct = mAct;
        this.fTrendsItemBean = fTrendsItemBean;
        this.commentList = commentList;
    }

    public void setRefreshContent(boolean refreshContent)
    {
        this.refreshContent = refreshContent;
    }

    public void setfTrendsItemBean(FTrendsItemBean fTrendsItemBean)
    {
        this.fTrendsItemBean = fTrendsItemBean;
    }

    public void setCommentItemClick(trendsCommentItemClick commentItemClick)
    {
        this.commentItemClick = commentItemClick;
    }

    @Override
    public int getCount()
    {
        return commentList.size() + (isShowPraise ? 3 : 2);
    }

    @Override
    public CommentsBean getItem(int position)
    {
        return commentList.get(position - (isShowPraise ? 3 : 2));
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (position == 0)
        {
            if (detailView == null)
                detailView = LayoutInflater.from(mAct).inflate(R.layout.item_frg_ftrends, parent, false);
            if (fTrendsItemBean != null)
                initTrendsDetail(detailView);
            return detailView;
        } else if ((position == 1 && !isShowPraise) || (position == 2 && isShowPraise))
        {
            View view = LayoutInflater.from(mAct).inflate(R.layout.item_trends_comment_title, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.tv_comment_title);
            if (fTrendsItemBean != null)
                textView.setText(String.format("评论(%s)条", fTrendsItemBean.getCommentNum()));
            return view;
        } else if (position == 1)
        {
            List<UsersBean> usersBeen = fTrendsItemBean.getLikedList();
            View view = LayoutInflater.from(mAct).inflate(R.layout.item_trends_detail_praise, parent, false);
            ImageOverlayView imageOverlayView = (ImageOverlayView) view.findViewById(R.id.iov_praise);
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mAct, TrendsPraiseListAct.class);
                    intent.putExtra("tId", fTrendsItemBean.gettId());
                    mAct.startActivity(intent);
                }
            });
            if (usersBeen != null)
            {
                List<String> urls = new ArrayList<>();
                for (UsersBean usersBean : usersBeen)
                {
                    urls.add(usersBean.getPhurl());
                }
                imageOverlayView.setHeadImages(urls);
            }

            TextView tv = (TextView) view.findViewById(R.id.tv_praise_num);
            tv.setText(fTrendsItemBean.getLikedNum() + "人点赞");
            return view;
        } else
        {
            ViewHolder viewHolder;
            if (convertView == null || convertView.getTag() == null)
            {
                convertView = LayoutInflater.from(mAct).inflate(R.layout.item_trends_comment, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.headImg = (LevelHeaderView) convertView.findViewById(R.id.img_head);
                viewHolder.nickNameTv = (TextView) convertView.findViewById(R.id.tv_nick_name);
                viewHolder.sexIconTv = (GenderView) convertView.findViewById(R.id.tv_sex);
                viewHolder.levelIconTv = (LevelView) convertView.findViewById(R.id.tv_level);
                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.distanceTv = (TextView) convertView.findViewById(R.id.tv_distance);
                viewHolder.contentTv = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.lineView = convertView.findViewById(R.id.line_view);
                convertView.setTag(viewHolder);
            } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final CommentsBean commentsBean = getItem(position);
            if (position == getCount() - 1)
                viewHolder.lineView.setVisibility(View.INVISIBLE);
            else
                viewHolder.lineView.setVisibility(View.VISIBLE);
            convertView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (commentItemClick != null && SharePreUtils.getUId(mAct).equals(commentsBean.getUId()))
                        commentItemClick.commentLongClick(position - (isShowPraise ? 3 : 2), commentsBean);
                    return false;
                }
            });
            viewHolder.headImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mAct, CNewUserInfoAct.class);
                    intent.putExtra("checkWay", "UId");
                    intent.putExtra("toUId", commentsBean.getUId());
                    mAct.startActivity(intent);
                }
            });
            viewHolder.headImg.setHeadImageByUrl(commentsBean.getPhurl());
            viewHolder.headImg.showRightIcon(commentsBean.getIsVuser());
            viewHolder.nickNameTv.setText(!TextUtils.isEmpty(commentsBean.getRemark()) ? commentsBean.getRemark() : commentsBean.getNicName());
            viewHolder.nickNameTv.setTextColor(ContextCompat.getColor(mAct,"1".equals(commentsBean.getIsVuser())?R.color.C0313:R.color.C0321));
            viewHolder.timeTv.setText(commentsBean.getDate());
            viewHolder.distanceTv.setText("·" + commentsBean.getDistance());

            viewHolder.sexIconTv.setSex(commentsBean.getAge(), commentsBean.getSex());

            viewHolder.levelIconTv.setLevel(commentsBean.getLevel(), 1);
            Spannable content;

            String showName = TextUtils.isEmpty(commentsBean.getReplyRemark()) ? commentsBean.getReplyName() : commentsBean.getReplyRemark();
            if (!TextUtils.isEmpty(showName))
            {
                content = EamSmileUtils.getSmiledText(mAct, "回复" + showName + ":" + commentsBean.getComment());
                content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.MC1)), 2, 2 + showName.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//                if (!TextUtils.isEmpty(commentsBean.getReplyName()))
//                {
//
//                    content = EamSmileUtils.getSmiledText(mAct, "回复" + commentsBean.getReplyName() + ":" + commentsBean.getComment());
//                    content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.MC1)), 2, 2 + commentsBean.getReplyName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else
                content = EamSmileUtils.getSmiledText(mAct, commentsBean.getComment());
            viewHolder.contentTv.setText(content);
            viewHolder.contentTv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (commentItemClick != null && !SharePreUtils.getUId(mAct).equals(commentsBean.getUId()))
                        commentItemClick.commentContentClick(commentsBean.getCommentId(), TextUtils.isEmpty(commentsBean.getRemark()) ? commentsBean.getNicName() : commentsBean.getRemark());
                }
            });
            viewHolder.contentTv.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (commentItemClick != null && SharePreUtils.getUId(mAct).equals(commentsBean.getUId()))
                        commentItemClick.commentLongClick(position - (isShowPraise ? 3 : 2), commentsBean);
                    return false;
                }
            });
            return convertView;
        }
    }

    /**
     * 动态详情item
     *
     * @param view
     */
    private void initTrendsDetail(View view)
    {
        Logger.t("trendsAdapter").d("is>>" + refreshDetail + "| " + view.getTag() + "|getCount>>" + getCount());
        LevelHeaderView imgHead = (LevelHeaderView) view.findViewById(R.id.img_head);
        RoundedImageView rivColumn = (RoundedImageView) view.findViewById(R.id.riv_column);
        TextView tvNickName = (TextView) view.findViewById(R.id.tv_nickname);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        TextView tvDistance = (TextView) view.findViewById(R.id.tv_distance);
        GenderView tvSex = (GenderView) view.findViewById(R.id.tv_sex);
        LevelView tvLevel = (LevelView) view.findViewById(R.id.tv_level);
        IconTextView tvPraise = (IconTextView) view.findViewById(R.id.tv_praise);
        IconTextView tvComment = (IconTextView) view.findViewById(R.id.tv_comment);
        TextView tvAddress = (TextView) view.findViewById(R.id.tv_address);
        LinearLayout addressLL = (LinearLayout) view.findViewById(R.id.ll_address);
        FolderTextView tvContent = (FolderTextView) view.findViewById(R.id.tv_content);
        LinearLayout llContentPic = (LinearLayout) view.findViewById(R.id.ll_content_pic);
        FrameLayout coverFl = (FrameLayout) view.findViewById(R.id.fl_cover);
        TextView readNumTv = (TextView) view.findViewById(R.id.read_num_tv);
        RoundedImageView coverRiv = (RoundedImageView) view.findViewById(R.id.riv_cover);
        LinearLayout llColumn = (LinearLayout) view.findViewById(R.id.ll_column);
        final ImageView imgThumbnail = (ImageView) view.findViewById(R.id.img_thumbnail);
        final FrameLayout flThumbnail = (FrameLayout) view.findViewById(R.id.fl_thumbnail);
        final FrameLayout flContentVideo = (FrameLayout) view.findViewById(R.id.fl_content_video);
        ImageView livingImg = (ImageView) view.findViewById(R.id.img_living);
        ImageView vIconImg = (ImageView) view.findViewById(R.id.img_v_icon);
         MultiSampleVideo uVideoView =  view.findViewById(R.id.ftrends_uvideo_view);
        TextView startTv = (TextView) view.findViewById(R.id.tv_start);
        TextView tvColumnTitle = (TextView) view.findViewById(R.id.tv_column_title);
        llColumn.setVisibility(View.GONE);
        if (refreshDetail)
        {
            LinearLayout.LayoutParams layoutParams;
            if ("1".equals(fTrendsItemBean.getShowType()))
            {
                layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mAct, 160), CommonUtils.dp2px(mAct, 213));
                layoutParams.bottomMargin = CommonUtils.dp2px(mAct, 10);
            } else
            {
                layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mAct, 213), CommonUtils.dp2px(mAct, 160));
                layoutParams.bottomMargin = CommonUtils.dp2px(mAct, 10);
            }
            flContentVideo.setLayoutParams(layoutParams);
            imgThumbnail.setTag("");
        } else
        {
            refreshDetail = true;
        }
        readNumTv.setText(fTrendsItemBean.getReadNum() + "人 已读");
        imgHead.setHeadImageByUrl(fTrendsItemBean.getPhurl());
        imgHead.showRightIcon(fTrendsItemBean.getIsVuser());
        tvLevel.setLevel(fTrendsItemBean.getLevel(), 1);
        tvNickName.setText(!TextUtils.isEmpty(fTrendsItemBean.getRemark()) ? fTrendsItemBean.getRemark() : fTrendsItemBean.getNicName());
        tvNickName.setTextColor(ContextCompat.getColor(mAct,"1".equals(fTrendsItemBean.getIsVuser())?R.color.C0313:R.color.C0321));
        tvTime.setText(fTrendsItemBean.getTimeToNow());
        tvDistance.setText("·" + fTrendsItemBean.getDistance());

        if (refreshContent)
        {
            if (TextUtils.isEmpty(fTrendsItemBean.getContent()))
            {
                tvContent.setVisibility(View.GONE);
            } else
            {
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setShowFull(true);
                String type = fTrendsItemBean.getType();
                tvContent.setSpecialStr("2".equals(type) ? "点击进入" : "3".equals(type) ? "#" + fTrendsItemBean.getExt().getGameName() + "#" : "");
                tvContent.setText(fTrendsItemBean.getContent());
            }
            tvSex.setSex(fTrendsItemBean.getAge(), fTrendsItemBean.getSex());

            if (!TextUtils.isEmpty(fTrendsItemBean.getLocation()))
            {
                addressLL.setVisibility(View.VISIBLE);
                tvAddress.setText(fTrendsItemBean.getLocation());
                tvAddress.setFocusable(true);
                tvAddress.requestFocus();
                tvAddress.setSelected(true);
                addressLL.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(mAct, ShowLocationAct.class);
                        intent.putExtra("posx", fTrendsItemBean.getPosx());
                        intent.putExtra("posy", fTrendsItemBean.getPosy());
                        intent.putExtra("location", fTrendsItemBean.getLocation());
                        mAct.startActivity(intent);
                    }
                });
            } else
            {
                addressLL.setVisibility(View.GONE);
            }
            tvTime.setText(fTrendsItemBean.getTimeToNow());
            vIconImg.setVisibility(View.GONE);
            llColumn.setVisibility(View.GONE);
            if ("0".equals(fTrendsItemBean.getType()))//普通动态
            {
                coverFl.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(fTrendsItemBean.getThumbnails()))
                {
                    uVideoView.setUp(fTrendsItemBean.getUrl(),true,null);
                    GlideApp.with(EamApplication.getInstance())
                            .asBitmap()
                            .load(fTrendsItemBean.getThumbnails())
                            .centerCrop()
                            .error(R.drawable.qs_cai_user)
                            .into(imgThumbnail);
                    flContentVideo.setVisibility(View.VISIBLE);
                    flThumbnail.setVisibility(View.VISIBLE);
                } else
                {
                    uVideoView.setTag("");
                    flContentVideo.setVisibility(View.GONE);
                    initContentPic(llContentPic, fTrendsItemBean.getUrl(), fTrendsItemBean.getShowType());
                }
            } else if ("4".equals(fTrendsItemBean.getType()) || "5".equals(fTrendsItemBean.getType())||
                    "6".equals(fTrendsItemBean.getType())|| "7".equals(fTrendsItemBean.getType())) // 4 专栏 5 活动 6轰趴 7 banner活动分享
            {
                uVideoView.setTag("");
                llColumn.setVisibility(View.VISIBLE);
                flContentVideo.setVisibility(View.GONE);
                llContentPic.setVisibility(View.GONE);
                coverFl.setVisibility(View.GONE);
                FTrendsItemBean.ExtBean extBean = fTrendsItemBean.getExt();
                if (extBean != null)
                {
                    GlideApp.with(EamApplication.getInstance())
                            .asBitmap()
                            .load(fTrendsItemBean.getUrl())
                            .placeholder(R.drawable.qs_550_260)
                            .error(R.drawable.qs_550_260)
                            .centerCrop()
                            .into(rivColumn);
                    if ("4".equals(fTrendsItemBean.getType()))
                    {
                        String columnTitle = extBean.getColumnName() == null ? "" : extBean.getTitle();
                        SpannableString spannableString = new SpannableString(columnTitle);
//                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.C0313)), 0, extBean.getColumnName() != null ? extBean.getColumnName().length() : 0
//                            , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvColumnTitle.setText(spannableString);
                    } else
                    {
                        String title;
                        switch (fTrendsItemBean.getType())
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
                        tvColumnTitle.setText(title);
                    }

                }
            } else
            {
                coverFl.setVisibility(View.VISIBLE);
                flContentVideo.setVisibility(View.GONE);
                llContentPic.setVisibility(View.GONE);
                coverFl.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (commentItemClick != null)
                            commentItemClick.contentClick();
                    }
                });
                if ("2".equals(fTrendsItemBean.getType()))//直播状态
                {
                    livingImg.setVisibility(View.VISIBLE);
                    if ("1".equals(fTrendsItemBean.getExt().getLiveStatus()))
                        livingImg.setImageResource(R.drawable.encounter_living_ico);
                    else if ("0".equals(fTrendsItemBean.getExt().getLiveStatus()))
                    {
                        if (!TextUtils.isEmpty(fTrendsItemBean.getExt().getVedio()))
                            livingImg.setImageResource(R.drawable.live_playback);
                        else
                            livingImg.setImageResource(R.drawable.live_end);
                    }
                } else
                {
                    livingImg.setVisibility(View.GONE);
                }

                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(fTrendsItemBean.getUrl())
                        .centerCrop()
                        .error(R.drawable.qs_cai_canting)
                        .into(coverRiv);
            }
        }

        llColumn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (commentItemClick != null)
                    commentItemClick.contentClick();
            }
        });
        tvPraise.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (commentItemClick != null)
                    commentItemClick.praiseClick(fTrendsItemBean.getIsLike());
            }
        });
        tvComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (commentItemClick != null)
                    commentItemClick.commentClick();
            }
        });
        imgHead.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mAct, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", fTrendsItemBean.getUp());
                mAct.startActivity(intent);
            }
        });
        flThumbnail.setOnClickListener(view1 -> {
            if (commentItemClick != null)
                commentItemClick.videoClick(flContentVideo, fTrendsItemBean);
        });
        uVideoView.setOnClickListener(view1 -> {
            if (commentItemClick != null)
                commentItemClick.videoClick(flContentVideo, fTrendsItemBean);
        });
        tvContent.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                new ContextMenuDialog((menuItem, position) ->
                {
                    ClipboardManager cm = (ClipboardManager) mAct.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText("content", fTrendsItemBean.getContent()));

                }).showContextMenuBox(mAct, Arrays.asList(new String[]{"复制"}));
                return false;
            }
        });
        if ("playing".equals(imgThumbnail.getTag()))
            flThumbnail.setVisibility(View.GONE);
        if ("1".equals(fTrendsItemBean.getIsLike()))
            tvPraise.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
        else
            tvPraise.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
        tvPraise.setText(String.format("{eam-p-praise @dimen/f2} %s", fTrendsItemBean.getLikedNum()));
        tvComment.setText(String.format("{eam-e60a @dimen/f2} %s", fTrendsItemBean.getCommentNum()));

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
            ImageView imageView = new ImageView(mAct);
            LinearLayout.LayoutParams layoutParams;
            if ("0".equals(showType))
                layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mAct, 213), CommonUtils.dp2px(mAct, 160));
            else
                layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mAct, 160), CommonUtils.dp2px(mAct, 213));
            imageView.setLayoutParams(layoutParams);
            String imgUrl = pics.get(0);
            GlideRequests glideRequests = GlideApp.with(mAct);
            if (imgUrl.endsWith(".gif"))
            {
                glideRequests.asGif()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            }
            glideRequests
                    .load(imgUrl)
                    .apply(bitmapTransform(new GlideRoundTransform(mAct, 5)))
                    .placeholder("0".equals(showType) ? R.drawable.qs_4_3 : R.drawable.qs_3_4)
                    .error("0".equals(showType) ? R.drawable.qs_4_3 : R.drawable.qs_3_4)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    CommonUtils.showImageBrowser(mAct, new ArrayList<>(pics), 0, v);
                    if (commentItemClick != null)
                        commentItemClick.picClick(fTrendsItemBean);
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
                LinearLayout llrow = new LinearLayout(mAct);
                llrow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                llrow.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < column; j++)
                {
                    final int position = i * column + j;
                    ImageView imageView = new ImageView(mAct);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size == 4 ? CommonUtils.dp2px(mAct, 95) : 0, CommonUtils.dp2px(mAct, 95));
                    if (size != 4)
                        layoutParams.weight = 1;
                    layoutParams.rightMargin = CommonUtils.dp2px(mAct, 4);
                    layoutParams.topMargin = CommonUtils.dp2px(mAct, 4);
                    imageView.setLayoutParams(layoutParams);
                    if (position < size)
                    {
                        imageView.setOnClickListener(v ->
                        {
                            CommonUtils.showImageBrowser(mAct, new ArrayList<>(pics), position, v);
                            if (commentItemClick != null)
                                commentItemClick.picClick(fTrendsItemBean);
                        });
                        String imgUrl = pics.get(position);
                        GlideRequests glideRequests = GlideApp.with(mAct);
                        if (imgUrl.endsWith(".gif"))
                        {
                            glideRequests.asGif()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                        }
                        glideRequests
                                .load(imgUrl)
                                .apply(bitmapTransform(new GlideRoundTransform(mAct, 5)))
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
     * 刷新详情
     *
     * @param refreshDeTail true 整页刷新  false 只刷新评论
     */
    public void notifyDataSetChanged(boolean refreshDeTail)
    {
        try
        {
            int likeNum = Integer.parseInt(fTrendsItemBean.getLikedNum());
            isShowPraise = SharePreUtils.getUId(mAct).equals(fTrendsItemBean.getUp()) && likeNum > 0;
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        this.refreshDetail = refreshDeTail;
        super.notifyDataSetChanged();
    }

    class ViewHolder
    {
        LevelHeaderView headImg;
        TextView nickNameTv;
        GenderView sexIconTv;
        LevelView levelIconTv;
        TextView timeTv;
        TextView distanceTv;
        TextView contentTv;
        View lineView;
    }

    public interface trendsCommentItemClick
    {
        /**
         * 点击别的的评价回复
         *
         * @param cid
         */
        void commentContentClick(String cid, String nickName);

        void praiseClick(String flg);

        void commentClick();

        void contentClick();

        void commentLongClick(int position, CommentsBean commentsBean);

        void videoClick(View view, FTrendsItemBean fTrendsItemBean);

        void picClick(FTrendsItemBean fTrendsItemBean);
    }
}
