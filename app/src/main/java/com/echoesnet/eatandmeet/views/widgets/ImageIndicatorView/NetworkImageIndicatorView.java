package com.echoesnet.eatandmeet.views.widgets.ImageIndicatorView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.panxw.android.imageindicator.ImageIndicatorView;

import java.util.List;

/**
 * Created by wangben on 2016/7/20.
 */
public class NetworkImageIndicatorView extends ImageIndicatorView
{
    public NetworkImageIndicatorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NetworkImageIndicatorView(Context context)
    {
        super(context);
    }

    /**
     * set image url list
     */
    @Override
    public void setupLayoutByImageUrl(List<String> urlList)
    {
        viewList.clear();
        if (urlList!=null && urlList.size()==0)
        {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            GlideApp.with(EamApplication.getInstance())
                    .load(R.drawable.qs_cai_canting)
                    .centerCrop()
                    .placeholder(R.drawable.qs_cai_canting)
                    .error(R.drawable.cai_da)
                    .into(imageView);
            addViewItem(imageView);
        }else {
            if (urlList.size() > 1)
            {
                urlList.add(0,urlList.get(urlList.size() - 1));
                urlList.add(urlList.get(1));
            }
            for (String url : urlList)
            {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideApp.with(EamApplication.getInstance())
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.qs_cai_canting)
                        .error(R.drawable.cai_da)
                        .into(imageView);
                addViewItem(imageView);
            }
        }

    }
}
