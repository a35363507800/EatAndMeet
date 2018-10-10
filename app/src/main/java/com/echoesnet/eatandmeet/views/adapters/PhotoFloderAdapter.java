package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.PhotoFloder;
import com.echoesnet.eatandmeet.utils.GlideApp;

import java.util.List;


public class PhotoFloderAdapter extends BaseAdapter
{
    private static final String TAG = "photo";
    Context context;
    List<PhotoFloder> mList;
    LayoutInflater inflater;
    int selectPosition = 0;

    public PhotoFloderAdapter(Context context, List<PhotoFloder> mList) {
        this.context = context;
        this.mList = mList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        PhotoFloder bean = mList.get(position);
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.list_dir_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.bindView(bean, position);
        return view;
    }

    class ViewHolder {
        ImageView cover;
        TextView fileName;
        TextView count;
        ImageView select;

        public ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.id_dir_item_image);
            fileName = (TextView) view.findViewById(R.id.id_dir_item_name);
            count = (TextView) view.findViewById(R.id.id_dir_item_count);
            select = (ImageView) view.findViewById(R.id.id_dir_item_select);
        }

        public void bindView(PhotoFloder bean, int position) {
            Log.i(TAG, bean.toString() + "\n" + position);
            GlideApp.with(context).asBitmap().load( bean.getCover()).dontAnimate().into(cover);
            fileName.setText(bean.getFileName());
            if (bean.getNumber() == 0) {
                count.setVisibility(View.GONE);
            } else {
                count.setVisibility(View.VISIBLE);
            }
            count.setText(bean.getNumber() + "张");
            if (position == selectPosition)
                select.setVisibility(View.VISIBLE);
            else
                select.setVisibility(View.GONE);
        }
    }

    public void setSelectPosition(int position) {
        this.selectPosition = position;
        notifyDataSetChanged();
    }

    public void refresh(List<PhotoFloder> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }
}
