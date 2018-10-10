package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */

public class ReportFoulsAdapter extends RecyclerView.Adapter<ReportFoulsAdapter.MyViewHolder>
{
    private LayoutInflater inflater;
    private Context context;
    private List<String> dataList;
    private int index=-1;

    public ReportFoulsAdapter(Context context, List<String> dataList)
    {
        this.context = context;
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.item_report_fouls_type, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        holder.textView.setText(dataList.get(position).toString());
        holder.itvRadio.setText("{eam-s-grade1 @color/C0323 @dimen/f3}");
        holder.itemView.setTag(dataList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                index = holder.getLayoutPosition();
                notifyDataSetChanged();
                if(clickListener != null)
                {
                    clickListener.OnRadioClick(holder.itemView, (String)holder.itemView.getTag(), index);
                }
            }
        });

        if(position == index)
        {
            holder.itvRadio.setText("{eam-s-grade2 @color/C0412 @dimen/f3}");
        } else
        {
            holder.itvRadio.setText("{eam-s-grade1 @color/C0323 @dimen/f3}");
        }
    }

    @Override
    public int getItemCount()
    {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;
        IconTextView itvRadio;
        LinearLayout llItem;
        public MyViewHolder(View itemItem)
        {
            super(itemItem);
            textView = (TextView) itemItem.findViewById(R.id.tvDesc);
            itvRadio = (IconTextView) itemItem.findViewById(R.id.itvRadio);
            llItem = (LinearLayout) itemItem.findViewById(R.id.ll_item);
        }
    }

    private OnRadioClickListener clickListener;
    public interface OnRadioClickListener
    {
        void OnRadioClick(View view , String data,int position);
    }

    public void setListener(OnRadioClickListener clickListener)
    {
        this.clickListener = clickListener;
    }
}
