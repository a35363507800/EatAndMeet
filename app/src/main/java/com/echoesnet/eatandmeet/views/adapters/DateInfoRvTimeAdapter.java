package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.echoesnet.eatandmeet.models.bean.ClubInfoBean;
import com.echoesnet.eatandmeet.models.bean.DateBaseInfoBean;
import com.echoesnet.eatandmeet.models.bean.DateCommentBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.ExpandableTextView;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by lc on 2018/03/16 14:55
 */

public class DateInfoRvTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final static String TAG = DateInfoRvTimeAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private boolean misSelfInfo;
    private List<DateBaseInfoBean> mlist;
    private boolean isOnAppointment = false;



    private int checkPosition ;


    public DateInfoRvTimeAdapter(Context context, List<DateBaseInfoBean> list)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mlist = list;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View view = mInflater.inflate(R.layout.item_rv_date_choose, parent, false);
        ContentViewHolder viewHolder = new ContentViewHolder(view);
        viewHolder.tvTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if((boolean)viewHolder.tvTime.getTag(R.id.date_info_isCanCheck)&&checkPosition!=(int) view.getTag(R.id.date_info_position))
                {
                    checkPosition = (int) view.getTag(R.id.date_info_position);
                    notifyDataSetChanged();
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(view, checkPosition);
                }
            }
        });
        for (int i = 0;i<mlist.size();i++)
        {
            DateBaseInfoBean bean = mlist.get(i);
            if(bean.getStatus().equals("0"))
            {
                viewHolder.tvTime.setSelected(true);

                break;
            }
        }





        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, final int position)
    {

        String value = "";
          if (mlist.size()==0)
              return;

        DateBaseInfoBean itemBean = mlist.get(position);

        ContentViewHolder holder = (ContentViewHolder) holder1;
        holder.tvTime.setText( itemBean.getDate());
        holder.itemView.setTag(holder.tvTime);

        holder.tvTime.setTag(R.id.date_info_position,position);
        if (itemBean.getStatus().equals("1"))
        {
            holder.tvTime.setBackgroundResource(R.drawable.shape_nocan_choose_yue_time);
            holder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.tvTime.setTag(R.id.date_info_isCanCheck,false);
        }
        else if (this.checkPosition == position)
        {
            holder.tvTime.setBackgroundResource(R.drawable.shape_choose_time);
            holder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.tvTime.setTag(R.id.date_info_isCanCheck,true);
        }
        else
        {
            holder.tvTime.setBackgroundResource(R.drawable.shape_unchoose_time);
            holder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.C0322));
            holder.tvTime.setTag(R.id.date_info_isCanCheck,true);
        }

//        if (isClicks.get(position))
//        {
//            holder.tvTime.setTextColor(Color.parseColor("#00a0e9"));
//        } else
//        {
//            holder.tvTime.setTextColor(Color.parseColor("#ffffff"));
//        }
//        holder.tvTime.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                for (int i = 0; i < mlist.size(); i++)
//                {
//                    if (i == position)
//                    {
//                        if (holder.tvTime.isSelected())
//                        {
//                            holder.tvTime.setSelected(false);
//                        } else
//                        {
//                            holder.tvTime.setSelected(true);
//                        }
//                    } else
//                    {
//                        holder.tvTime.setSelected(false);
//                    }
//                }
//                if (mOnItemClickListener != null)
//                {
//                    mOnItemClickListener.onItemClick(view, position);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount()
    {
        return mlist.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder
    {
        public ContentViewHolder(View view)
        {
            super(view);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
        }

        TextView tvTime;
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


}
