package com.echoesnet.eatandmeet.views.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.UsersBean;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by lzy on 2017/7/14.
 */

public class ImageOverlayView extends RelativeLayout
{
    final static int STYLE_VIEW = 0;
    final static int STYLE_IMAGE = 1;

    //private List<UsersBean> headList;
    private List<String> headList;
    int style = STYLE_IMAGE;
    private Context mContext;
    private float imageHeight;
    private float imageWidth;
    private int negativeOverlap;
    private ItemOnClickListener itemOnClickListener;
    private boolean slide = true;

    public boolean isSlide()
    {
        return slide;
    }

    public void setSlide(boolean slide)
    {
        this.slide = slide;
    }

    public ImageOverlayView(Context context)
    {
        this(context, null);
    }

    public ImageOverlayView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ImageOverlayView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageOverlayView);
        imageHeight = a.getDimension(R.styleable.ImageOverlayView_image_height, 0);
        imageWidth = a.getDimension(R.styleable.ImageOverlayView_image_width, 0);
        negativeOverlap = a.getInt(R.styleable.ImageOverlayView_negative_overlap, 20);
        init();
    }

    private void init()
    {
    }

    /**
     *倒叙添加
     */
    public void setHeadImages(List<String> urls)
    {
        if (headList == null)
        {
            headList = new ArrayList<>();
        }
        headList.clear();
        headList.addAll(urls);
        Collections.reverse(headList);
        notifyDataSetChange();
    }

//    public void addHeadImage(String url)
//    {
//        if (headList == null)
//            headList = new ArrayList<UsersBean>();
//
//        UsersBean user = new UsersBean();
//        user.setUphUrl(url);
//        headList.add(user);
//        notifyDataSetChange();
//    }

    public void notifyDataSetChange()
    {
      //  removeAllViews();
        for (int i = 0; i < headList.size(); i++)
        {
            RoundedImageView levelHeaderView = (RoundedImageView) getChildAt(i);
            if (levelHeaderView == null)
            {
                //UsersBean bean = headList.get(i);
                addHeadView(headList.get(i));
            } else
            {
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .centerCrop()
                        .error(R.drawable.userhead)
                        .placeholder(R.drawable.userhead)
                        .load(headList.get(i))
                        .into(levelHeaderView);
                levelHeaderView.setTag(headList.get(i));
            }
        }
        while(getChildAt(headList.size())!=null)
        {
            removeViewAt(getChildCount()-1);
        }
    }



    private void addHeadView(String userUrl)
    {
        final RoundedImageView levelHeaderView = new RoundedImageView(mContext);
        levelHeaderView.setOval(true);

        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .centerCrop()
                .error(R.drawable.userhead)
                .placeholder(R.drawable.userhead)
                .load(userUrl)
                .into(levelHeaderView);

//        levelHeaderView.setHeadImageByUrl(user.getPhurl());
        levelHeaderView.setTag(userUrl);

//            levelHeaderView.setOnClickListener(new OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    if (itemOnClickListener != null)
//                        itemOnClickListener.OnItemClick(levelHeaderView, position);
//
//             //       marginAnim(position);
//
//                }
//            });

        int w = (int) imageWidth;
        int h = (int) imageHeight;
        RelativeLayout.LayoutParams pa = new LayoutParams(w, h);
        pa.addRule(RelativeLayout.CENTER_VERTICAL);
        pa.setMargins(getChildCount() * w - getChildCount() * negativeOverlap, 0, 0, 0);
        levelHeaderView.setLayoutParams(pa);
        addView(levelHeaderView);
    }

    private void marginAnim(int position)
    {
        if (!slide)
            return;
        for (int i = position + 1; i < headList.size(); i++)
        {
            final View view = getChildAt(i);
            final RelativeLayout.LayoutParams pa = (LayoutParams) view.getLayoutParams();
            final int margin = pa.leftMargin;

            ValueAnimator anim = ValueAnimator.ofInt(margin, (int) (imageWidth * i - i * negativeOverlap + imageWidth * 0.5));
            anim.setDuration(200);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    int currentValue = (int) animation.getAnimatedValue();

                    pa.leftMargin = currentValue;
                    view.setLayoutParams(pa);
                }
            });
            anim.start();
        }
        final View view = getChildAt(position);
        final RelativeLayout.LayoutParams pa = (LayoutParams) view.getLayoutParams();
        final int margin = pa.leftMargin;
        ValueAnimator anim = ValueAnimator.ofInt(margin, (int) (position * imageWidth - position * negativeOverlap));
        anim.setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int currentValue = (int) animation.getAnimatedValue();

                pa.leftMargin = currentValue;
                view.setLayoutParams(pa);
            }
        });
        anim.start();
    }

    private void resetMargin(int position, boolean flag)
    {
        if (!slide)
            return;
        final View view = getChildAt(position);
        final RelativeLayout.LayoutParams pa = (LayoutParams) view.getLayoutParams();
        final int margin = pa.leftMargin;
        final int margin2 = position * (int) imageHeight - position * negativeOverlap;
        if (flag == false)
        {
            pa.leftMargin = margin2;
            view.setLayoutParams(pa);
            return;
        }

        ValueAnimator anim = ValueAnimator.ofInt(margin, margin2);
        anim.setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int currentValue = (int) animation.getAnimatedValue();

                pa.leftMargin = currentValue;
                view.setLayoutParams(pa);
            }
        });
        anim.start();
    }

    public void setItemClickListener(ItemOnClickListener itemClickListener)
    {
        itemOnClickListener = itemClickListener;
    }

    interface ItemOnClickListener
    {
        void OnItemClick(View view, int position);
    }

    private int oposition = -2;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                int postion = getView(event.getX(), event.getY());
                if (postion != -1)
                    marginAnim(postion);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < headList.size(); i++)
                        {
                            resetMargin(i, true);
                        }
                    }
                }, 200);

                postion = getView(event.getX(), event.getY());
                if (postion != -1)
                {
                    if (itemOnClickListener != null)
                        itemOnClickListener.OnItemClick(getChildAt(postion), postion);
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(false);
                postion = getView(event.getX(), event.getY());
                if (postion != -1)
                {
                    if (oposition == postion)
                        return false;
                    marginAnim(postion);
                    oposition = postion;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                for (int i = 0; i < headList.size(); i++)
                {
                    resetMargin(i, true);
                }
                return false;


        }

        return true;
    }




    private int getView(float x, float y)
    {
        for (int i = 0; i < headList.size(); i++)
        {
            View view = getChildAt(i);
            float nx = view.getX();
            float ny = view.getY();

            if (x >= ny && x <= nx + imageWidth && y >= y && y <= y + imageHeight)
            {
                return i;
            }
        }
        return -1;

    }

}
