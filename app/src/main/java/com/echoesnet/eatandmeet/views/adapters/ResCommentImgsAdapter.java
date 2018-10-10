package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/6/24.
 */
public class ResCommentImgsAdapter extends RecyclerView.Adapter<ResCommentImgsAdapter.ViewHolder>
{
    private LayoutInflater mInflater;
    //private List<Uri> mData;
    private List<String> mData;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public ResCommentImgsAdapter(Context context, List<String> data)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data;
    }


    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        this.mOnItemLongClickListener = listener;
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.rvitem_dish_img, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.dishImg = (RoundedImageView) view
                .findViewById(R.id.riv_dish_img);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        String imgUrl = mData.get(position);
        if (!imgUrl.toString().equals(""))
        {
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(imgUrl)
                    .centerCrop()
                    .placeholder(R.drawable.qs_cai_user)
                    .error(R.drawable.qs_cai_user)
                    .into(holder.dishImg);
        }
        else
        {
            holder.dishImg.setVisibility(View.GONE);
        }

        //如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        //如果设置了回调，则设置点击事件
        if (mOnItemLongClickListener != null)
        {
            holder.itemView.setLongClickable(true);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder //implements View.OnCreateContextMenuListener
    {
        public ViewHolder(View v)
        {
            super(v);
           // v.setOnCreateContextMenuListener(this);

        }
        RoundedImageView dishImg;

/*        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
        {
*//*            menu.setHeaderTitle("图片操作");
            menu.add(0, v.getId(), 0, "查看照片");//groupId, itemId, order, title
            menu.add(0, v.getId(), 1, "删除照片");*//*
        }*/
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }
    /**
     * ItemLongClick的回调接口
     */
    public interface OnItemLongClickListener
    {
        void onItemLongClick(View view, int position);
    }
}
