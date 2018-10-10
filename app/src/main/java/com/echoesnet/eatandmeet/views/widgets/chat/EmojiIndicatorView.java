package com.echoesnet.eatandmeet.views.widgets.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.hyphenate.util.DensityUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class EmojiIndicatorView extends LinearLayout
{
    private static final String TAG = EmojiIndicatorView.class.getSimpleName();
    private Context context;
    private Drawable selectedBitmap;
    private Drawable unselectedBitmap;

    private List<ImageView> dotViews;


    private int dotHeight = 10;
    private int pointHeight = 6;
    private int selectPointHeight = 8;

    public EmojiIndicatorView(Context context, AttributeSet attrs, int defStyle)
    {
        this(context, null);
    }

    public EmojiIndicatorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public EmojiIndicatorView(Context context)
    {
        this(context, null);
    }

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        dotHeight = DensityUtil.dip2px(context, dotHeight);
        pointHeight = DensityUtil.dip2px(context, pointHeight);
        selectPointHeight = DensityUtil.dip2px(context, selectPointHeight);
        selectedBitmap = ContextCompat.getDrawable(context, R.drawable.shape_circle_mc1_bg);
        unselectedBitmap = ContextCompat.getDrawable(context, R.drawable.shape_circle_c0331_bg);

        setGravity(Gravity.CENTER);
    }

    public void init(int count)
    {
        dotViews = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            RelativeLayout rl = new RelativeLayout(context);
            LayoutParams params = new LayoutParams(dotHeight, dotHeight);

            ImageView imageView = new ImageView(context);

            if (i == 0)
            {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(selectPointHeight, selectPointHeight);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                imageView.setBackground(selectedBitmap);
                rl.addView(imageView, layoutParams);
            }
            else
            {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(pointHeight, pointHeight);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                imageView.setBackground(unselectedBitmap);
                rl.addView(imageView, layoutParams);
            }
            this.addView(rl, params);
            dotViews.add(imageView);
        }
    }

    public void updateIndicator(int count)
    {
        if (dotViews == null)
        {
            return;
        }
        for (int i = 0; i < dotViews.size(); i++)
        {
            if (i >= count)
            {
                dotViews.get(i).setVisibility(GONE);
                ((View) dotViews.get(i).getParent()).setVisibility(GONE);
            }
            else
            {
                dotViews.get(i).setVisibility(VISIBLE);
                ((View) dotViews.get(i).getParent()).setVisibility(VISIBLE);
            }
        }
        if (count > dotViews.size())
        {
            int diff = count - dotViews.size();
            for (int i = 0; i < diff; i++)
            {
                RelativeLayout rl = new RelativeLayout(context);
                LayoutParams params = new LayoutParams(dotHeight, dotHeight);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(pointHeight, pointHeight);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                ImageView imageView = new ImageView(context);
                imageView.setBackground(unselectedBitmap);
                rl.addView(imageView, layoutParams);
                rl.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                this.addView(rl, params);
                dotViews.add(imageView);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
//        if(selectedBitmap != null){
//            selectedBitmap.recycle();
//        }
//        if(unselectedBitmap != null){
//            unselectedBitmap.recycle();
//        }
        selectedBitmap = null;
        unselectedBitmap = null;
    }

    public void selectTo(int position)
    {
        Logger.t(TAG).d("selectTo:Position:" + position);
        for (ImageView iv : dotViews)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(pointHeight, pointHeight);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            iv.setLayoutParams(layoutParams);
            iv.setBackground(unselectedBitmap);
        }
        ImageView imageView = dotViews.get(position);
        RelativeLayout.LayoutParams selectLayoutParams = new RelativeLayout.LayoutParams(selectPointHeight, selectPointHeight);
        selectLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(selectLayoutParams);
        imageView.setBackground(selectedBitmap);
    }


    public void selectTo(int startPosition, int targetPostion)
    {

        Logger.t(TAG).d("selectTo:startPosition:" + startPosition + " | targetPostion:" + targetPostion);

        ImageView startView = dotViews.get(startPosition);
        ImageView targetView = dotViews.get(targetPostion);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(pointHeight, pointHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        startView.setLayoutParams(layoutParams);
//        startView.getLayoutParams().width = pointHeight;
//        startView.getLayoutParams().height = pointHeight;

        RelativeLayout.LayoutParams selectLayoutParams = new RelativeLayout.LayoutParams(selectPointHeight, selectPointHeight);
        selectLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        targetView.setLayoutParams(selectLayoutParams);
//        targetView.getLayoutParams().width = selectPointHeight;
//        targetView.getLayoutParams().height = selectPointHeight;

        startView.setBackground(unselectedBitmap);
        targetView.setBackground(selectedBitmap);

    }

}   
