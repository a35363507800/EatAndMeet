package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/18 0018
 * @description
 */
public class TrendsSearchLocationAdapter extends RecyclerView.Adapter<TrendsSearchLocationAdapter.SearchLocationViewHolder>
{
    private Activity mAct;
    private List<PoiInfo> poiInfoList;
    private ItemClickListener itemClickListener;
    private int selectPosition = -1;
    private boolean isShowNoLocation = true;

    public void setSelectPosition(int selectPosition)
    {
        this.selectPosition = selectPosition;
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public TrendsSearchLocationAdapter(Activity mAct, List<PoiInfo> poiInfoList, boolean isShowNoLocation)
    {
        this.mAct = mAct;
        this.poiInfoList = poiInfoList;
        this.isShowNoLocation = isShowNoLocation;
    }

    public TrendsSearchLocationAdapter(Activity mAct, List<PoiInfo> poiInfoList)
    {
        this.mAct = mAct;
        this.poiInfoList = poiInfoList;
    }

    @Override
    public SearchLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mAct).inflate(R.layout.item_search_location, parent, false);
        SearchLocationViewHolder searchLocationViewHolder = new SearchLocationViewHolder(view);
        return searchLocationViewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchLocationViewHolder holder, final int position)
    {
        if (position == getItemCount() - 1)
            holder.lineView.setVisibility(View.INVISIBLE);
        else
            holder.lineView.setVisibility(View.VISIBLE);
        if (position == 0 && isShowNoLocation)
        {
            holder.locationAddressTv.setVisibility(View.GONE);
            holder.locationNameTv.setText("不显示位置");
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    selectPosition = position;
                    notifyDataSetChanged();
                    if (itemClickListener != null)
                        itemClickListener.noShowLocationClick();
                }
            });
        } else
        {
            final PoiInfo poiInfo = poiInfoList.get(isShowNoLocation && position > 0?position - 1:position);
            holder.locationAddressTv.setText(poiInfo.address);
            holder.locationNameTv.setText(poiInfo.name);
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    selectPosition = position;
                    notifyDataSetChanged();
                    if (itemClickListener != null)
                        itemClickListener.itemClick(poiInfo);
                }
            });
        }
        if (selectPosition == position)
        {
            holder.locationNameTv.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
            holder.selectIconTv.setVisibility(View.VISIBLE);
        } else
        {
            holder.locationNameTv.setTextColor(ContextCompat.getColor(mAct, R.color.C0321));
            holder.selectIconTv.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount()
    {
        if (isShowNoLocation)
            return poiInfoList.size() + 1;
        else
            return poiInfoList.size();
    }

    public class SearchLocationViewHolder extends RecyclerView.ViewHolder
    {
        TextView locationNameTv;
        TextView locationAddressTv;
        IconTextView selectIconTv;
        View lineView;

        public SearchLocationViewHolder(View itemView)
        {
            super(itemView);
            locationAddressTv = (TextView) itemView.findViewById(R.id.tv_location_address);
            locationNameTv = (TextView) itemView.findViewById(R.id.tv_location_name);
            selectIconTv = (IconTextView) itemView.findViewById(R.id.icon_tv_select);
            lineView = itemView.findViewById(R.id.view_line);
        }
    }

    public interface ItemClickListener
    {
        void itemClick(PoiInfo poiInfo);

        void noShowLocationClick();
    }
}
