package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.PhotoBean;
import com.echoesnet.eatandmeet.views.PhotoPickViewHolder;

import java.util.ArrayList;
import java.util.List;



public class PhotoPickAdapter extends RecyclerView.Adapter<PhotoPickViewHolder> {
    private static final String TAG = "photo";
    List<PhotoBean> mPhotoList;
    Context mContext;
    List<String> selectList = new ArrayList<>();
    boolean showSelectIndicator;//是否是多选图片
    int itemImageWidth;

    public PhotoPickAdapter(List<PhotoBean> photoList, Context context, int itemImageWidth, boolean selectIndicator) {
        this.mPhotoList = photoList;
        this.mContext = context;
        this.itemImageWidth = itemImageWidth;
        this.showSelectIndicator = selectIndicator;
    }

    public void selectPhoto(List<String> selectList, int position) {
        this.selectList = selectList;
        notifyItemChanged(position);
    }

    public void selectPhotos(List<String> selectList) {
        this.selectList = selectList;
        notifyDataSetChanged();
    }

    @Override
    public PhotoPickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new PhotoPickViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false), itemImageWidth);
    }

    @Override
    public void onBindViewHolder(final PhotoPickViewHolder holder, final int position) {
        final PhotoBean bean=mPhotoList.get(position);
        final String path =bean.getPath();
        if (path.endsWith(".mp4") || path.endsWith(".3gp")) {
            Log.e(TAG,"position" + position + "\n" + path);
        }
        holder.bindView(bean, mContext);
        if (!showSelectIndicator && bean.getType() == 0) {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (selectList.contains(path)) {
                holder.checkBox.setImageResource(R.drawable.checked);
                holder.view.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setImageResource(R.drawable.unchecked);
                holder.view.setVisibility(View.GONE);
            }
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChildClickListener != null) {
                    onChildClickListener.onCheckedClickListener(position, path);
                }
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChildClickListener != null) {
                    onChildClickListener.onImageClickListener(bean,position,path);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPhotoList == null ? 0 : mPhotoList.size();
    }

    public void refresh(List<PhotoBean> photoList) {
        this.mPhotoList = photoList;
        notifyDataSetChanged();
    }

    OnChildClickListener onChildClickListener;

    public void setOnChildClickListener(OnChildClickListener onCheckClickListener) {
        this.onChildClickListener = onCheckClickListener;
    }


    public interface OnChildClickListener {
        void onCheckedClickListener(int position, String path);

        void onImageClickListener(PhotoBean bean, int position, String path);
    }
}
