package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.CommonUserCommentBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.widgets.CustomExpandableTextView;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.MoreTextView;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by Administrator on 2016/5/9.
 */
public class LoserCommentAdapter extends BaseAdapter
{
    public final static String TAG = LoserCommentAdapter.class.getSimpleName();
    List<CommonUserCommentBean> loserCommentLst;
    Context context;

    public LoserCommentAdapter(Context context, List<CommonUserCommentBean> loserCommentLst)
    {
        this.context = context;
        this.loserCommentLst = loserCommentLst;
    }

    @Override
    public int getCount()
    {
        return loserCommentLst.size();
    }

    @Override
    public Object getItem(int position)
    {
        return loserCommentLst.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        convertView = LayoutInflater.from(context).inflate(R.layout.litem_res_loser_comment, parent, false);
        TextView tvNickname = (TextView) convertView.findViewById(R.id.tv_loser_nickname);
//        ExpandableTextView tvComment = ((ExpandableTextView) convertView.findViewById(R.id.tv_loser_comment));
        CustomExpandableTextView tvComment = ((CustomExpandableTextView) convertView.findViewById(R.id.tv_loser_comment));
        LevelHeaderView ivHeadImg = (LevelHeaderView) convertView.findViewById(R.id.riv_loser_head);
        CustomRatingBar ratingBar = (CustomRatingBar) convertView.findViewById(R.id.rating_bar);
        TableLayout tlImgContainer = (TableLayout) convertView.findViewById(R.id.tl_img_container);
        TextView tvResFeedback = (TextView) convertView.findViewById(R.id.tv_merchant_feedback);
        GenderView itvSex = (GenderView) convertView.findViewById(R.id.img_u_sex);
        IconTextView lvLevel = (IconTextView) convertView.findViewById(R.id.itv_level);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        LevelView levelView = (LevelView) convertView.findViewById(R.id.level_view);

        final CommonUserCommentBean loserComment = (CommonUserCommentBean) getItem(position);
        if (TextUtils.isEmpty(loserComment.getRemark()))
        {
            tvNickname.setText(loserComment.getEvaNicName());
        }
        else
        {

            tvNickname.setText(loserComment.getRemark());
        }
        levelView.setLevel(loserComment.getlevel(), 1);
//        lvLevel.setText(String.format("%s %s", "Lv.", loserComment.getlevel()));
        String age = loserComment.getAge();
        age = TextUtils.isEmpty(age) ? "18" : age;
        itvSex.setSex(age, loserComment.getSex());

        //tvComment.setText()(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.f3));
        tvComment.setText(loserComment.getEvalContent());
        if (TextUtils.isEmpty(loserComment.getEvalTime()))
            tvDate.setVisibility(View.GONE);
        else
        {
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(loserComment.getEvalTime());
        }

        if (loserComment.getReplyOrNot().equals("1"))
        {
            tvResFeedback.setVisibility(View.VISIBLE);
            String htmlStr = String.format("<font color=%s>商家回复：</font><font color=%s>%s</font>", "#9d45f9", "#333333", loserComment.getResReply());
            tvResFeedback.setText(Html.fromHtml(htmlStr));
        }
        else
        {
            tvResFeedback.setVisibility(View.GONE);
        }
        //设置用户头像
        ivHeadImg.setHeadImageByUrl(loserComment.getEvaImg());
        ivHeadImg.showRightIcon(loserComment.getIsVuser());
//        ivHeadImg.setLevel(loserComment.getlevel());
       /* GlideApp.with(context)
                .load(loserComment.getEvaImg())
                .into(ivHeadImg);
        //点击头像跳转*/
        ivHeadImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", loserComment.getuId());
                context.startActivity(intent);
            }
        });

        ratingBar.setIndicator(true);
        ratingBar.setRatingBar(Integer.parseInt((loserComment.getrStar())));

        final List<String> comImgUrls = loserComment.getCommentImgUrls();

        if (comImgUrls.size() == 0)
        {
            tlImgContainer.setVisibility(View.GONE);
        }
        else
        {
            for (int i = 0; i < comImgUrls.size(); i++)
            {
                //Logger.t(TAG).d(comImgUrls.get(i));
                if (i < 3)
                {
                    ((TableRow) tlImgContainer.getChildAt(0)).setVisibility(View.VISIBLE);
                    ImageView imgv = (ImageView) ((TableRow) tlImgContainer.getChildAt(0)).getChildAt(i);
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
                    ((TableRow) tlImgContainer.getChildAt(1)).setVisibility(View.VISIBLE);
                    ImageView imgv = (ImageView) ((TableRow) tlImgContainer.getChildAt(1)).getChildAt(i - 3);
                    Logger.t(TAG).d("imgv:" + imgv);
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
        }
/*        final TextView tvComment=holder.tvComment;
        final TextView tvMore=holder.tvMore;
        tvMore.setTag("close");
        if (tvComment.getLineCount()>2)
        {
            tvMore.setText("查看更多");
            tvMore.setVisibility(View.VISIBLE);
        }
        else
        {
            tvMore.setVisibility(View.INVISIBLE);
        }
        tvMore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (tvMore.getTag().toString().equals("close"))
                {
                    tvComment.setMaxLines(100);
                    tvMore.setText("收起");
                }
                else
                {
                    tvComment.setMaxLines(3);
                    tvMore.setText("查看更多");
                }

            }
        });*/
        return convertView;
    }

    class ViewHolder
    {
        TextView tvNickname;
        MoreTextView tvComment;
        RoundedImageView ivHeadImg;
        LinearLayout ratingBar;
        TableLayout tlImgContainer;
    }
}
