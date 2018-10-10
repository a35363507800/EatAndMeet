package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ClubCommentBean;
import com.echoesnet.eatandmeet.models.bean.ClubDetailBean;
import com.echoesnet.eatandmeet.models.bean.DateCommentBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.ExpandableTextView;
import com.echoesnet.eatandmeet.views.widgets.CustomExpandableTextView;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleBiFunction;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/7
 * @description
 */
public class ClubCommentAdapter extends BaseAdapter
{
    private Activity mAct;
    private List<ClubDetailBean.CommentsBean> mList;
    private ViewHolder holder;
    private List<String> localPicList = new ArrayList<>();
    public ClubCommentAdapter(Activity mAct, List<ClubDetailBean.CommentsBean> mList)
    {
        this.mAct = mAct;
        this.mList = mList;
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public ClubDetailBean.CommentsBean getItem(int i)
    {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent)
    {
        if (convertView == null || convertView.getTag() == null)
        {
            convertView = LayoutInflater.from(mAct).inflate(R.layout.item_act_club_comment, parent, false);
            holder = new ViewHolder();
            holder.tvClubComment = ((CustomExpandableTextView) convertView.findViewById(R.id.tv_club_comment));
           // holder.tvClubComment = ((com.ms.square.android.expandabletextview.ExpandableTextView) convertView.findViewById(R.id.tv_club_comment));
            holder.imgHead = (LevelHeaderView)convertView.findViewById(R.id.img_head);
            holder.tvNickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            holder.tvSex = (GenderView) convertView.findViewById(R.id.tv_sex);
            holder.tvLevel = (LevelView) convertView.findViewById(R.id.tv_level);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.crbContentRatingBar = (CustomRatingBar) convertView.findViewById(R.id.crb_content_rating_bar);
//            holder.tvExpandableMirror = (TextView) convertView.findViewById(R.id.etv_mirror);
//            holder.etvExpandable = (ExpandableTextView) convertView.findViewById(R.id.etv_expandable);
//            holder.tvToggle = (TextView) convertView.findViewById(R.id.tv_toggle);
            holder.llContentPic = (LinearLayout) convertView.findViewById(R.id.ll_content_pic);
            convertView.setTag(holder);
            // 对于listView 注意添加这一行 即可在item上使用高度
            //   AutoUtils.autoSize(convertView);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        ClubDetailBean.CommentsBean bean = mList.get(i);

       //  initContentPic(holder.llContentPic, localPicList);
        initContentPic(holder.llContentPic, bean.getUrl());

        holder.crbContentRatingBar.setIndicator(true);
        holder.crbContentRatingBar.setRatingBar(Integer.parseInt(bean.getScore()));
        holder.tvClubComment.setText(bean.getContent());
//        holder.etvExpandable.setText(bean.getContent());
//        holder.etvExpandable.setAnimationDuration(500L);
//        holder.etvExpandable.setInterpolator(new OvershootInterpolator());
//        holder.tvExpandableMirror.setText(bean.getContent());
//
//        holder.tvExpandableMirror.post(()->
//            {
//                int lineCnt = holder.tvExpandableMirror.getLineCount();
//                if (lineCnt>2)
//                {
//                    holder.tvToggle.setVisibility(View.VISIBLE);
//                } else
//                {
//                    holder.tvToggle.setVisibility(View.GONE);
//                }
//
//
//            });
//
//        holder.tvToggle.setOnClickListener((v)->
//            {
//
//                holder.etvExpandable.toggle();
//                holder.tvToggle.setText(holder.etvExpandable.isExpanded() ? "全文" : "收起");
//            });


        holder.tvNickname.setText(bean.getNickName());
        holder.tvTime.setText(bean.getTime());
        holder.tvSex.setSex(bean.getAge(), bean.getSex());
        holder.tvLevel.setLevel(bean.getLevel(), 1);

        //点击头像跳转*/
        holder.imgHead.setHeadImageByUrl(bean.getHead());
        holder.imgHead.showRightIcon(bean.getBigV());
        holder.imgHead.setOnClickListener((v)->
        {
            Intent intent = new Intent(mAct, CNewUserInfoAct.class);
            intent.putExtra("checkWay", "UId");
            intent.putExtra("toUId", bean.getuId());
            mAct.startActivity(intent);
        });
        return convertView;
    }
    static class ViewHolder
    {
        LinearLayout llContentPic;
        LevelHeaderView imgHead;
        TextView tvNickname;
        GenderView tvSex;
        LevelView tvLevel;
        TextView tvTime;
        CustomRatingBar crbContentRatingBar;
        CustomExpandableTextView tvClubComment;
      //com.ms.square.android.expandabletextview.ExpandableTextView tvClubComment;
    }

    private void initContentPic(LinearLayout llContentPic, List<String> urls)
    {
        if (TextUtils.isEmpty(urls.toString()))
        {
            llContentPic.setVisibility(View.GONE);
            return;
        } else
            llContentPic.setVisibility(View.VISIBLE);
        llContentPic.removeAllViews();
        final List<String> pics = urls ;
        int row = 0, column = pics.size() == 4 ? 2 : 3;
        if (pics.size() == 1)
        {
            RoundedImageView imageView = new RoundedImageView(mAct);
            imageView.setCornerRadius(CommonUtils.dp2px(mAct, 2));
            LinearLayout.LayoutParams layoutParams;

            layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mAct, 213), CommonUtils.dp2px(mAct, 160));
            imageView.setLayoutParams(layoutParams);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(pics.get(0))
                    .centerCrop()
                    .placeholder(R.drawable.qs_4_3)
                    .error(R.drawable.qs_4_3)
                    .into(imageView);
            imageView.setOnClickListener((v)->
            {
                CommonUtils.showImageBrowser(mAct, new ArrayList<>(pics), 0, v);
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
                LinearLayout llrow = new LinearLayout(mAct);
                llrow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                llrow.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < column; j++)
                {
                    final int position = i * column + j;
                    RoundedImageView imageView = new RoundedImageView(mAct);
                    imageView.setCornerRadius(4);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size == 4 ? CommonUtils.dp2px(mAct, 95) : 0, CommonUtils.dp2px(mAct, 95));
                    if (size != 4)
                        layoutParams.weight = 1;
                    layoutParams.rightMargin = CommonUtils.dp2px(mAct, 4);
                    layoutParams.topMargin = CommonUtils.dp2px(mAct, 4);
                    imageView.setLayoutParams(layoutParams);

                    try
                    {
                        if (position < size)
                        {
                            imageView.setOnClickListener((v)->
                            {
                                CommonUtils.showImageBrowser(mAct, new ArrayList<>(pics), position, v);
                            });
                            GlideApp.with(EamApplication.getInstance())
                                    .asBitmap()
                                    .load(pics.get(position))
                                    .centerCrop()
                                    .placeholder(R.drawable.qs_zb)
                                    .error(R.drawable.qs_zb)
                                    .into(imageView);
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    llrow.addView(imageView);
                }
                llContentPic.addView(llrow);
            }
        }

    }
}
