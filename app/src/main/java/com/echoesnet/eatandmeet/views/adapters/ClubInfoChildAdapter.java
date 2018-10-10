package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.ClubFoodDetailAct;
import com.echoesnet.eatandmeet.models.bean.ClubInfoBean;
import com.echoesnet.eatandmeet.models.bean.PackagesBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.util.List;
import java.util.logging.Logger;


/**
 * Created by Administrator on 2018/2/6.
 *
 * @author ling
 */

public class ClubInfoChildAdapter extends RecyclerView.Adapter<ClubInfoChildAdapter.ViewHolder>
{
    private Context mContext;
    private List list;
    private int checkPosition;

    public ClubInfoChildAdapter(Context mContext, ClubInfoBean bean, int position)
    {
        this.mContext = mContext;
        switch (position)
        {
            case 0:
                this.list = bean.getReserveDate();
                break;
            case 1:
                if(bean.getReserveDate()!=null&&bean.getReserveDate().size()>0)
                this.list = bean.getReserveDate().get(0).getScreenings();
                break;
            case 2:
                this.list = bean.getPackages();
                break;
            case 3:
                this.list = bean.getTheme();
                checkPosition=-1;
                break;
        }
        if(list.size()==0)
            checkPosition=-1;
    }


    public ClubInfoChildAdapter(Context mContext, List list)
    {
        this.mContext = mContext;
        this.list = list;
        if(list.size()==0)
            checkPosition=-1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int layoutId = R.layout.act_club_info_child_adapter;
        if (list != null && list.size() > 0 && mContext instanceof ClubFoodDetailAct)
            layoutId = R.layout.act_club_info_child_adapter_small;
//        if (list != null && list.size() > 0 && list.get(0) instanceof ClubInfoBean.ReserveDateBean)
//            layoutId = R.layout.act_club_info_child_adapter_more;


        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        if(CommonUtils.getScreenWidth(mContext)==1440)
        {
            ViewGroup.LayoutParams groupP=view.getLayoutParams();
            groupP.width= (int) (groupP.width*1.13);
            groupP.height= (int) (groupP.height*1.13);
            view.setLayoutParams(groupP);

            groupP=viewHolder.info.getLayoutParams();
            groupP.width= (int) (groupP.width*1.13);
            groupP.height= (int) (groupP.height*1.13);
            viewHolder.info.setLayoutParams(groupP);
        }
        viewHolder.info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if((boolean)viewHolder.info.getTag(R.id.club_info_isCanCheck))
                {

                    if(checkPosition!=(int) view.getTag(R.id.club_info_position))
                        checkPosition = (int) view.getTag(R.id.club_info_position);
                    else
                        if(list.get((int) view.getTag(R.id.club_info_position)) instanceof ClubInfoBean.ThemeBean)
                        checkPosition=-1;

                    notifyDataSetChanged();
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(view, checkPosition);

                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.info.setTag(R.id.club_info_position,position);
        if ((list.get(position) instanceof ClubInfoBean.ScreeningsBean && ("1").equals((
                (ClubInfoBean.ScreeningsBean) list.get(position)).getStatus()))
                ||
                (list.get(position) instanceof ClubInfoBean.ReserveDateBean && ("1").equals((
                        (ClubInfoBean.ReserveDateBean) list.get(position)).getStates())
                ))
        {

            holder.info.setBackgroundResource(R.drawable.club_child_info_bg_noclick);
            holder.info.setTextColor(ContextCompat.getColor(mContext, R.color.C0322));
            holder.info.setTag(R.id.club_info_isCanCheck,false);
        }
        else if (this.checkPosition == position)
        {
            holder.info.setBackgroundResource(R.drawable.club_child_info_bg_check);
            holder.info.setTextColor(ContextCompat.getColor(mContext, R.color.C0324));
            holder.info.setTag(R.id.club_info_isCanCheck,true);
        }
        else
        {
            holder.info.setBackgroundResource(R.drawable.club_child_info_bg);
            holder.info.setTextColor(ContextCompat.getColor(mContext, R.color.C0322));
            holder.info.setTag(R.id.club_info_isCanCheck,true);
        }


        if (list.get(position) instanceof ClubInfoBean.ReserveDateBean)
        {
           String date=((ClubInfoBean.ReserveDateBean) list.get(position)).getDate();
           date=date.split("-")[1]+"."+date.split("-")[2];
            holder.info.setText(date+" "+((ClubInfoBean.ReserveDateBean) list.get(position)).getWeek());
        }
        if (list.get(position) instanceof ClubInfoBean.ScreeningsBean)
            holder.info.setText(((ClubInfoBean.ScreeningsBean) list.get(position)).getStart() + "~" +
                    ((ClubInfoBean.ScreeningsBean) list.get(position)).getEnd()+" ("+((ClubInfoBean.ScreeningsBean) list.get(position)).getName()+")");
        if (list.get(position) instanceof PackagesBean)
        {
            holder.info.setText("￥"+((PackagesBean) list.get(position)).getPrice()+" "+((PackagesBean) list.get(position)).getName());
        }
        if (list.get(position) instanceof ClubInfoBean.ThemeBean)
        {
            holder.info.setText(((ClubInfoBean.ThemeBean) list.get(position)).getName());
        }

    }

    @Override
    public int getItemCount()
    {

        for(int position=0;position<list.size();position++)
        if ((list.get(position) instanceof ClubInfoBean.ScreeningsBean && ("1").equals((
                (ClubInfoBean.ScreeningsBean) list.get(position)).getStatus()))
                ||
                (list.get(position) instanceof ClubInfoBean.ReserveDateBean && ("1").equals((
                        (ClubInfoBean.ReserveDateBean) list.get(position)).getStates())
                )) {

            if (this.checkPosition == position) {
                if (checkPosition + 1 < list.size())
                    checkPosition++;
                else
                    checkPosition = -1;
            }
        }

        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView info;

        public ViewHolder(View view)
        {
            super(view);

            info = view.findViewById(R.id.tv_info);

        }

    }
    private OnItemClickListener onItemClickListener;
    public void setOnItemtClickListener(OnItemClickListener onItemClickListener){
       this.onItemClickListener=onItemClickListener;
    }
    public interface OnItemClickListener
    {
       void onItemClick(View view ,int position);
    }
    private OnPostOverListener onPost;
    public void setOnPostOverListener(OnPostOverListener onPost){
        this.onPost=onPost;
    }
    public interface OnPostOverListener
    {
        void post();
    }

    public int getCheckPosition()
    {
        return checkPosition;
    }
    public void setCheckPosition(int position)
    {
        if(position<list.size()) {
            checkPosition = position;
            notifyDataSetChanged();
        }
    }
    public void setList(List list)
    {
        this.list = list;
        checkPosition=0;
        notifyDataSetChanged();
    }

    public List getList()
    {
        return list;
    }
}
