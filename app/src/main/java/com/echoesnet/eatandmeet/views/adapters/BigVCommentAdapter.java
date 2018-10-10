package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.BigVcommentBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by wangben on 2016/5/9.
 */

public class BigVCommentAdapter extends BaseAdapter
{
    List<BigVcommentBean> bigVcommentLst;
    Context context;

    public BigVCommentAdapter(Context context, List<BigVcommentBean> bigVcommentLst)
    {
        this.context=context;
        this.bigVcommentLst=bigVcommentLst;
    }


    @Override
    public int getCount()
    {
        return bigVcommentLst.size();
    }

    @Override
    public Object getItem(int position)
    {
        return bigVcommentLst.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //由于一些特殊的原因，不可以使用holder
        convertView= LayoutInflater.from(context).inflate(R.layout.litem_res_bigv_comment,parent,false);
        TextView tvNickname= (TextView) convertView.findViewById(R.id.tv_bigv_nickname);
//        TextView tvTitle=(TextView) convertView.findViewById(R.id.tv_bigv_title);
        ExpandableTextView tvComment=(ExpandableTextView) convertView.findViewById(R.id.tv_bigv_comment);
        LevelHeaderView ivHeadImg= (LevelHeaderView) convertView.findViewById(R.id.riv_bigv_head);
        CustomRatingBar  ratingBar= (CustomRatingBar) convertView.findViewById(R.id.rating_bar);
        TableLayout tlImgContainer= (TableLayout) convertView.findViewById(R.id.tl_img_container);
        IconTextView itvSex = (IconTextView) convertView.findViewById(R.id.img_u_sex);
        IconTextView lvLevel = (IconTextView) convertView.findViewById(R.id.itv_level);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        LevelView levelView = (LevelView) convertView.findViewById(R.id.level_view);

        final BigVcommentBean bigVComment= (BigVcommentBean) getItem(position);
        tvNickname.setText(bigVComment.getNickName());
//        tvTitle.setText(bigVComment.getTitle());
        tvComment.setText(bigVComment.getComment());

        ivHeadImg.setHeadImageByUrl(bigVComment.getUserHeadImg());

        ivHeadImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", bigVComment.getuId());
                context.startActivity(intent);
            }
        });

//        ivHeadImg.setLevel(bigVComment.getLevel());
        //GlideApp.with(context).load(bigVComment.getUserHeadImg()).into(ivHeadImg);
        String age;
        if(!TextUtils.isEmpty(bigVComment.getAge()))
        {
            age = bigVComment.getAge();
        } else
        {
            age = "18";
        }
        levelView.setLevel(bigVComment.getLevel(),1);
//        lvLevel.setText(String.format("%s %s", "Lv.", bigVComment.getLevel()));
        if (TextUtils.equals(bigVComment.getSex(), "女"))
        {
            itvSex.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            itvSex.setText(String.format("%s %s", "{eam-e94f}", age));
        } else
        {
            itvSex.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            itvSex.setText(String.format("%s %s", "{eam-e950}", age));
        }

        ratingBar.setIndicator(true);
        ratingBar.setRatingBar(bigVComment.getRating());
/*        for (int i=0;i<ratingBar.getChildCount();i++)
        {
            if (i<bigVComment.getRating())
            {
                ((ImageView)ratingBar.getChildAt(i)).setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_s_star)
                        .colorRes(R.color.c12));
            }
            else
            {
                ((ImageView)ratingBar.getChildAt(i)).setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_s_star)
                        .colorRes(R.color.c4));
            }
        }*/

        final List<String>comImgUrls;
        if (bigVComment.getCommentImgUrlLst().size()>6)
            comImgUrls=bigVComment.getCommentImgUrlLst().subList(0,5);
        else
            comImgUrls=bigVComment.getCommentImgUrlLst();
        Logger.t("BigVCommentAdapter").d(comImgUrls.toString());

        for (int i=0;i<comImgUrls.size();i++)
        {
            if (i<3)
            {
                tlImgContainer.getChildAt(0).setVisibility(View.VISIBLE);
                ImageView imgv=(ImageView) ((TableRow)tlImgContainer.getChildAt(0)).getChildAt(i);
                imgv.setTag(i);
                imgv.setVisibility(View.VISIBLE);
                imgv.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        CommonUtils.showImageBrowser(context, comImgUrls, (int) v.getTag(), v);
                    }
                });
                GlideApp.with(EamApplication.getInstance())
                        .load(comImgUrls.get(i))
                        .centerCrop()
                        .placeholder(R.drawable.userhead)
                        .into(imgv);
            }
            else
            {
                tlImgContainer.getChildAt(1).setVisibility(View.VISIBLE);
                ImageView imgv=(ImageView) ((TableRow)tlImgContainer.getChildAt(1)).getChildAt(i-3);
                if (imgv == null)
                    continue;
                imgv.setTag(i);
                imgv.setVisibility(View.VISIBLE);
                imgv.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        CommonUtils.showImageBrowser(context, comImgUrls, (int) v.getTag(), v);
                    }
                });
                GlideApp.with(EamApplication.getInstance())
                        .load(comImgUrls.get(i))
                        .centerCrop()
                        .placeholder(R.drawable.userhead)
                        .into(imgv);
            }
        }
        return convertView;
    }

    public final class ViewHolder
    {
        TextView tvNickname;
        TextView tvComment;
        TextView tvTitle;
        RoundedImageView ivHeadImg;
        CustomRatingBar ratingBar;
        TableLayout tlImgContainer;
    }
}
