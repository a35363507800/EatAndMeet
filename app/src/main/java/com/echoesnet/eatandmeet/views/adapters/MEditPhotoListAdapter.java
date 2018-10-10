package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MyInfoEditAct;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/8/26.
 */
public class MEditPhotoListAdapter extends BaseAdapter
{
    private List<String> mData;
    private Context mContext;
    private OnClick listener;
    public MEditPhotoListAdapter(Context context, List<String> data, OnClick lsitener)
    {
        this.listener=lsitener;
        mContext = context;
        //mInflater = ;
        mData = data;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_published_grida, parent, false);
            holder = new ViewHolder();
            holder.image = (RoundedImageView) convertView.findViewById(R.id.item_grida_image);
            holder.imageRed = (ImageView) convertView.findViewById(R.id.image_delete);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        String imgUrl = mData.get(position);
        if (!imgUrl.toString().equals(""))
        {
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(imgUrl)
                    .centerCrop()
                    .placeholder(R.drawable.qs_head)
                    .error(R.drawable.qs_head)
                    .into(holder.image);
            holder.imageRed.setTag(position);
            holder.imageRed.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(listener!=null)
                    {
                        listener.OnClick(position);
                    }
                }
            });
        }
        else
        {
            holder.image.setVisibility(View.GONE);
        }

        if(imgUrl.toString().equals(MyInfoEditAct.addImg))
            holder.imageRed.setVisibility(View.GONE);
        else
            holder.imageRed.setVisibility(View.VISIBLE);
        return convertView;
    }

    public class ViewHolder
    {
        public RoundedImageView image;
        public ImageView imageRed;
    }

    public interface OnClick
    {
        void OnClick(int position);
    }

}
