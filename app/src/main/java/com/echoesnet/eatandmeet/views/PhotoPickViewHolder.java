package com.echoesnet.eatandmeet.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.PhotoBean;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.GlideRequests;

import java.text.SimpleDateFormat;
import java.util.Date;



public class PhotoPickViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public ImageView checkBox;
    public View view;
    TextView durantionText;
    int itemWidth;
    FrameLayout frame;

    public PhotoPickViewHolder(View itemView, int itemWidth) {
        super(itemView);
        this.itemWidth = itemWidth;
        frame = (FrameLayout) itemView.findViewById(R.id.frame);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) frame.getLayoutParams();
        if (lp.height != itemWidth) {
            frame.setLayoutParams(new GridLayoutManager.LayoutParams(itemWidth, itemWidth));
        }
        durantionText = (TextView) itemView.findViewById(R.id.durantion);
        imageView = (ImageView) itemView.findViewById(R.id.iv_photo);
        checkBox = (ImageView) itemView.findViewById(R.id.cb_select);
        view = itemView.findViewById(R.id.mask);
    }

    public void bindView(PhotoBean bean, Context context) {
        GlideRequests glideRequests =  GlideApp.with(context);
        if (bean.getPath().endsWith(".gif"))
            glideRequests.asGif().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        else
            glideRequests.asBitmap();
       glideRequests.load(bean.getPath()).centerCrop().into(imageView);
        long durantion = bean.getDurantion();
        if (durantion > 0) {
            durantionText.setVisibility(View.VISIBLE);
            durantionText.setText(getTimes(durantion));
        } else {
            durantionText.setVisibility(View.GONE);
        }
    }

    private String getTimes(long durantion) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(durantion));
    }
}
