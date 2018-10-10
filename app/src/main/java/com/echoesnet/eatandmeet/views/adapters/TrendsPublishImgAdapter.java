package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideRequests;
import com.echoesnet.eatandmeet.utils.GlideRoundTransform;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import static com.echoesnet.eatandmeet.utils.GlideOptions.bitmapTransform;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/17 0017
 * @description
 */
public class TrendsPublishImgAdapter extends RecyclerView.Adapter<TrendsPublishImgAdapter.TrendsPublishViewHolder>
{
    private final int ITEM_ADD = 101;
    private final int ITEM_NORMAL = 102;
    private final int ITEM_COMPLETE = 103;
    private List<String> imgs;
    private Activity mAct;
    private boolean isShowDeletImg;
    private TrendsPublishItemClick trendsPublishItemClick;

    public TrendsPublishImgAdapter(List<String> imgs, Activity mAct)
    {
        this.imgs = imgs;
        this.mAct = mAct;
    }

    public void setTrendsPublishItemClick(TrendsPublishItemClick trendsPublishItemClick)
    {
        this.trendsPublishItemClick = trendsPublishItemClick;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (imgs.size() < 6 && imgs.size() > 0)
        {
            if (position == imgs.size())
                return ITEM_ADD;
            else
                return ITEM_NORMAL;
        } else if (imgs.size() > 5)
            return ITEM_COMPLETE;
        return ITEM_NORMAL;
    }

    @Override
    public TrendsPublishViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mAct).inflate(R.layout.item_trends_publish_img, parent, false);
        TrendsPublishViewHolder publishViewHolder = new TrendsPublishViewHolder(view);
        return publishViewHolder;
    }

    @Override
    public void onBindViewHolder(TrendsPublishViewHolder holder, final int position)
    {
        int index = position;
        if (getItemViewType(position) == ITEM_ADD)
        {
            holder.deleteImg.setVisibility(View.GONE);
            holder.picImg.setImageDrawable(ContextCompat.getDrawable(mAct, R.drawable.btn_tianjia_xhdpi));
            holder.picImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (trendsPublishItemClick != null)
                        trendsPublishItemClick.addClick();
                }
            });
            return;
        }
        String path = imgs.get(index);
        GlideRequests glideRequests = GlideApp.with(EamApplication.getInstance());
        if (path.endsWith(".gif") ||"gif".equals( CommonUtils.getImageMimeType(path)))
            glideRequests.asGif().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        else
            glideRequests.asBitmap();
        glideRequests
                .load(imgs.get(index))
                .apply(bitmapTransform(new GlideRoundTransform(mAct,5)))
                .into(holder.picImg);
        final int finalIndex = index;
        holder.picImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (trendsPublishItemClick != null)
                    trendsPublishItemClick.itemClick(v,finalIndex);
            }
        });
        if (isShowDeletImg)
        {
            holder.deleteImg.setVisibility(View.VISIBLE);
            holder.deleteImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (trendsPublishItemClick != null)
                        trendsPublishItemClick.deleteClick(v,finalIndex);
                }
            });
        } else
            holder.deleteImg.setVisibility(View.GONE);
        holder.picImg.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (!isShowDeletImg)
                    isShowDeletImg = true;
                else
                    isShowDeletImg = false;
                notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if (imgs.size() < 6 && imgs.size() > 0)
            return imgs.size() + 1;
        else if (imgs.size() > 5)
            return imgs.size();
        else
            return 0;
    }

    public class TrendsPublishViewHolder extends RecyclerView.ViewHolder
    {
        ImageView picImg;
        ImageView deleteImg;

        public TrendsPublishViewHolder(View itemView)
        {
            super(itemView);
            picImg =  itemView.findViewById(R.id.item_img);
            deleteImg = (ImageView) itemView.findViewById(R.id.img_delete);
        }
    }

    public interface TrendsPublishItemClick
    {
        void itemClick(View view,int position);

        void deleteClick(View view,int position);

        void addClick();
    }
}
