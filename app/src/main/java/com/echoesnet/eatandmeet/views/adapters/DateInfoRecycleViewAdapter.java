package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.DateCommentBean;
import com.echoesnet.eatandmeet.models.bean.StarCommentBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.ExpandableTextView;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.smoothImageView.SmoothImageView;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lc on 2017/7/18 19.
 */

public class DateInfoRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final static String TAG = DateInfoRecycleViewAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private List<DateCommentBean> mData;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private int star = 0;
    private boolean misSelfInfo;
    private View mEmptyView;

    private final static int ITEM_TYPE_TOP = 0;
    private final static int ITEM_TYPE_TITLE = 1;
    private final static int ITEM_TYPE_CONTENT = 2;

    private List<String> mlist;
    private boolean isOnAppointment = false;


    private OnTopClickListener mOnTopClickListener;
    private  TopViewHolder topViewHolder ;


    public DateInfoRecycleViewAdapter(Context context, List<DateCommentBean> data, List<String> list, boolean isSelfInfo, View empteyView)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data;
        mlist = list;
        misSelfInfo = isSelfInfo;
        mEmptyView = empteyView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //显示约会基金信息布局
        if (viewType == ITEM_TYPE_TOP)
        {
            View view = mInflater.inflate(R.layout.item_recycle_date_top, parent, false);
            TopViewHolder viewHolder = new TopViewHolder(view);
            return viewHolder;
        } else if (viewType == ITEM_TYPE_TITLE)//显示约会评价布局
        {
            View view = mInflater.inflate(R.layout.item_recycle_date_title, parent, false);
            TitleViewHolder viewHolder = new TitleViewHolder(view);
            return viewHolder;
        } else //显示约会评价 内容布局
        {
            View view = mInflater.inflate(R.layout.item_recycle_date, parent, false);
            ContentViewHolder viewHolder = new ContentViewHolder(view);
            return viewHolder;
        }

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
    {
        if (holder instanceof TopViewHolder)
        {
            topViewHolder = (TopViewHolder) holder;
            if (misSelfInfo)
            {
                topViewHolder.setVisibility(false);
            } else
            {
                topViewHolder.setVisibility(true);
            }

            if (mlist != null && mlist.size() > 0)
            {
                topViewHolder.tvContent.setText(mlist.get(1));
                if (TextUtils.equals("0", mlist.get(0)))
                {
                    topViewHolder.llBgIcon.setBackgroundResource(R.drawable.shape_red_8radius);
                    topViewHolder.ivAlmclock.setText("{eam-e622}");
                } else
                {
//                    ((TopViewHolder) holder).llBgIcon.setBackgroundResource(R.drawable.shape_4dp_c0315);
//                    ((TopViewHolder) holder).ivAlmclock.setText("{eam-e9bb}");
                    isOnAppointment = true;
                    topViewHolder.setVisibility(false);
                }
                topViewHolder.ivAlmclock.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            }
            topViewHolder.itvNext.setOnClickListener((v)->
            {
                    //显示时间选择弹窗或者进度查询
                    if (mOnTopClickListener != null)
                        mOnTopClickListener.onTopClick(topViewHolder.itvNext);
            });
            topViewHolder.rlAllYueInfo.setOnClickListener((v)->
            {

                    //显示时间选择弹窗或者进度查询
                    if (mOnTopClickListener != null)
                        mOnTopClickListener.onTopClick(topViewHolder.rlAllYueInfo);
            });
            topViewHolder.ivAlmclock.setOnClickListener((v)->
            {
                    //显示时间选择弹窗或者进度查询
                    if (mOnTopClickListener != null)
                        mOnTopClickListener.onTopClick(topViewHolder.ivAlmclock);
            });

        } else if (holder instanceof TitleViewHolder)
        {
            ((TitleViewHolder) holder).tvStop.setText("用户评价");

            if (mData != null && mData.size() > 0)
            {
                ((TitleViewHolder) holder).setVisibility(true);
            } else
            {
                ((TitleViewHolder) holder).setVisibility(false);
            }

        } else
        {
            final DateCommentBean bean = mData.get(position - 2);
            final ContentViewHolder cViewHolder= (ContentViewHolder) holder;
            bean.setStarNum();

            try
            {
                String json = bean.getJson();
                JSONObject jsonObject = new JSONObject(json);
                final String commentStr = jsonObject.getString("commentStr");
                String commentBeanList = jsonObject.getString("commentBeanList");
                String epUrls = jsonObject.getString("epUrls");
                if (!TextUtils.isEmpty(epUrls))
                {
                    String[] arrUrl = epUrls.split("!\u003dend\u003d!");
                }
                initContentPic(((ContentViewHolder) holder).llContentPic, epUrls);

                cViewHolder.crbContentRatingBar.setIndicator(true);
                cViewHolder.crbContentRatingBar.setRatingBar(bean.getStarNum());

                cViewHolder.etvExpandable.setText(commentStr);
                cViewHolder.etvExpandable.setAnimationDuration(500L);
                cViewHolder.etvExpandable.setInterpolator(new OvershootInterpolator());
                cViewHolder.tvExpandableMirror.setText(commentStr);

                cViewHolder.tvExpandableMirror.post(()->
                {
                        int lineCnt =   cViewHolder.tvExpandableMirror.getLineCount();
                        if (lineCnt>3)
                        {
                            cViewHolder.tvToggle.setVisibility(View.VISIBLE);
                        } else
                        {
                            cViewHolder.tvToggle.setVisibility(View.GONE);
                        }

                        Logger.t(TAG).d("line》" + lineCnt+" maxline>"+   cViewHolder.etvExpandable.getMaxLines());

                });

                cViewHolder.tvToggle.setOnClickListener((v)->
                {

                        cViewHolder.etvExpandable.toggle();
                        cViewHolder.tvToggle.setText(   cViewHolder.etvExpandable.isExpanded() ? "全文" : "收起");
                });

            } catch (Exception e)
            {
                e.printStackTrace();
            }

            cViewHolder.tvNickname.setText(bean.getNicName());
            cViewHolder.tvTime.setText(bean.getTime());
            cViewHolder.tvSex.setSex(bean.getAge(), bean.getSex());
            cViewHolder.tvLevel.setLevel(bean.getLevel(), 1);

            //点击头像跳转*/
            cViewHolder.imgHead.setHeadImageByUrl(bean.getPhUrl());
            cViewHolder.imgHead.showRightIcon(bean.getIsVuser());
            cViewHolder.imgHead.setOnClickListener((v)->
            {
                    Intent intent = new Intent(mContext, CNewUserInfoAct.class);
                    intent.putExtra("checkWay", "UId");
                    intent.putExtra("toUId", bean.getuId());
                    mContext.startActivity(intent);
            });
        }


    }


    @Override
    public int getItemViewType(int position)
    {
        if (position == 0)
        {
            return ITEM_TYPE_TOP;
        } else if (position == 1)
        {
            return ITEM_TYPE_TITLE;
        } else
        {
            return ITEM_TYPE_CONTENT;
        }
    }

    @Override
    public int getItemCount()
    {
        return mData.size() + 2;
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder
    {
        public ContentViewHolder(View view)
        {
            super(view);
            imgHead = (LevelHeaderView) view.findViewById(R.id.img_head);
            tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
            tvSex = (GenderView) view.findViewById(R.id.tv_sex);
            tvLevel = (LevelView) view.findViewById(R.id.tv_level);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            crbContentRatingBar = (CustomRatingBar) view.findViewById(R.id.crb_content_rating_bar);
            llContentPic = (LinearLayout) view.findViewById(R.id.ll_content_pic);


            //crbContent = (LinearLayout) view.findViewById(R.id.cet_fans_comment);
            etvExpandable = (ExpandableTextView) view.findViewById(R.id.etv_expandable);
            tvExpandableMirror= (TextView) view.findViewById(R.id.etv_mirror);

            tvToggle = (TextView) view.findViewById(R.id.tv_toggle);
        }

        public void setVisibility(boolean isVisible)
        {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible)
            {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            } else
            {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

        TextView tvToggle;
        ExpandableTextView etvExpandable;
        TextView tvExpandableMirror;
        LinearLayout llContentPic;
        LevelHeaderView imgHead;
        TextView tvNickname;
        GenderView tvSex;
        LevelView tvLevel;
        TextView tvTime;
        CustomRatingBar crbContentRatingBar;
        //LinearLayout crbContent;
    }

    //显示约会基金信息布局
    public static class TopViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout rlAllYueInfo;
        IconTextView ivAlmclock;
        TextView tvContent;
        IconTextView itvNext;
        LinearLayout llBgIcon;

        public TopViewHolder(View view)
        {
            super(view);
            rlAllYueInfo = (RelativeLayout) view.findViewById(R.id.rl_all_yue_info);
            ivAlmclock = (IconTextView) view.findViewById(R.id.iv_almclock);
            llBgIcon = (LinearLayout) view.findViewById(R.id.ll_bg_icon);
            itvNext = (IconTextView) view.findViewById(R.id.itv_next);
            tvContent = (TextView) view.findViewById(R.id.tv_content);
        }


        public void setVisibility(boolean isVisible)
        {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible)
            {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            } else
            {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }


    }

    //显示约会评价布局
    public class TitleViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvStop;
        RelativeLayout rlAllYueTitle;
        public TitleViewHolder(View view)
        {
            super(view);
            rlAllYueTitle = (RelativeLayout) view.findViewById(R.id.rl_all_yue_title);
            tvStop = (TextView) view.findViewById(R.id.tv_stop);

        }

        public void setVisibility(boolean isVisible)
        {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible)
            {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            } else
            {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }


    public void setOnTopClickListener(OnTopClickListener mOnTopClickListener)
    {
        this.mOnTopClickListener = mOnTopClickListener;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnTopClickListener
    {
        void onTopClick(View view);
    }


    private void initContentPic(LinearLayout llContentPic, String url)
    {
        if (TextUtils.isEmpty(url))
        {
            llContentPic.setVisibility(View.GONE);
            return;
        } else
            llContentPic.setVisibility(View.VISIBLE);
        llContentPic.removeAllViews();
        final List<String> pics = CommonUtils.strWithSeparatorToList(url, "!\u003dend\u003d!");
        int row = 0, column = pics.size() == 4 ? 2 : 3;
        if (pics.size() == 1)
        {
            RoundedImageView imageView = new RoundedImageView(mContext);
            imageView.setCornerRadius(CommonUtils.dp2px(mContext, 4));
            LinearLayout.LayoutParams layoutParams;

            // layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mContext, 160), CommonUtils.dp2px(mContext, 213));

            layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(mContext, 213), CommonUtils.dp2px(mContext, 160));
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
                    CommonUtils.showImageBrowser(mContext, new ArrayList<>(pics), 0, v);
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
                LinearLayout llrow = new LinearLayout(mContext);
                llrow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                llrow.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < column; j++)
                {
                    final int position = i * column + j;
                    RoundedImageView imageView = new RoundedImageView(mContext);
                    imageView.setCornerRadius(10);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size == 4 ? CommonUtils.dp2px(mContext, 95) : 0, CommonUtils.dp2px(mContext, 95));
                    if (size != 4)
                        layoutParams.weight = 1;
                    layoutParams.rightMargin = CommonUtils.dp2px(mContext, 4);
                    layoutParams.topMargin = CommonUtils.dp2px(mContext, 4);
                    imageView.setLayoutParams(layoutParams);

                    if (position < size)
                    {
                        imageView.setOnClickListener((v)->
                        {
                                CommonUtils.showImageBrowser(mContext, new ArrayList<>(pics), position, v);
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


}
