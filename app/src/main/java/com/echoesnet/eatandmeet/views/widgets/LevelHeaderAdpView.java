package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by lzy on 2017/4/7.
 */
public class LevelHeaderAdpView extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener
{
    public LevelHeaderAdpView(Context context, float width, float height)
    {
        this(context, null);
    }

    public LevelHeaderAdpView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LevelHeaderAdpView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LevelHeaderViewAttr);
        float height = a.getDimension(R.styleable.LevelHeaderViewAttr_level_height, 0);
        float width = a.getDimension(R.styleable.LevelHeaderViewAttr_level_width, 0);
        showRightIcon = a.getBoolean(R.styleable.LevelHeaderViewAttr_level_ril_icon, false);
        liveState = a.getBoolean(R.styleable.LevelHeaderViewAttr_live_state, false);
        init(context, width, height);
    }

    private RoundedImageView rounImageView;
    private RoundedImageView rivLev;
    private RoundedImageView rightLev;
    private RelativeLayout headSlayout;
    private ViewGroup.LayoutParams headSlayoutP;
    private static final int LEVEL_MAX = 40;
    private boolean liveState = false;
    private int width;
    private int level = -1;
    private boolean showRightIcon = false;

    private Context mContext;

    private void init(Context context, float width, float height)
    {
        mContext = context;
        inflate(mContext,R.layout.level_imageadpview,this);
        headSlayout = getViewById(this, R.id.head_stroke_layou);
        headSlayoutP = headSlayout.getLayoutParams();
        rounImageView = getViewById(this, R.id.levelimageview_headimage);
        rivLev = getViewById(this, R.id.levelimageview_levelimage);
        rightLev = getViewById(this, R.id.levelimageview_righticon);

        LayoutParams pa = (LayoutParams) rightLev.getLayoutParams();
        pa.height = (int) height;
        pa.width = (int) width;
        rightLev.setLayoutParams(pa);

        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    //

    public void setBorderColor(@ColorInt int color)
    {
        rounImageView.setBorderColor(color);
    }

    public void setBorderWidth(@DimenRes int resId)
    {
        rounImageView.setBorderWidth(resId);
    }

    public void setBorderWidth(float width)
    {
        rounImageView.setBorderWidth(width);
    }

    private void upLevelData(final int level)
    {
        this.level = level;

            // 头像皇冠边框样式
            int drawbleId = LevelHeaderView.getLevelResourceId(level, liveState, LevelHeaderView.TYPE_STROKE);
            if (drawbleId == 0)
                drawbleId = R.color.transparent;
            headSlayout.setBackground(ContextCompat.getDrawable(mContext, drawbleId));

    }


    /**
     * 设置等级
     *
     * @param level
     */
    public void setLevel(String level)
    {
        int lv = 0;
        try
        {
            lv = Integer.parseInt(level);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        if (lv < 0 || lv > LEVEL_MAX)
            throw new IllegalArgumentException(String.format("必须是介于0到%s的数字", LEVEL_MAX));

        upLevelData(lv);

    }

    //右下角标
    public void showRightIcon(boolean isShow)
    {
        showRightIcon = isShow;
        if (isShow)
            rightLev.setVisibility(View.VISIBLE);
        else
            rightLev.setVisibility(View.GONE);
    }

    //右下角标
    public void showRightIcon(String isShow)
    {
        showRightIcon = "1".equals(isShow) ? true : false;
        if ("1".equals(isShow))
            rightLev.setVisibility(View.VISIBLE);
        else
            rightLev.setVisibility(View.GONE);
    }

    /**
     * 设置头像图片
     */
    public void setHeadImageByUrl(String imageUrl)
    {
        if (rounImageView.getTag() != null)
        {
            if (rounImageView.getTag().equals(imageUrl))
            {
                return;
            }
        }
        rounImageView.setTag(imageUrl);
        GlideApp.with(mContext.getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.userhead)
                .centerCrop()
                .error(R.drawable.userhead)
                .into(rounImageView);
    }

    public void setImageResourceByID(int id)
    {
        rounImageView.setImageResource(id);
    }

    public void setHeadImageByBitmap(Bitmap bitmap)
    {
        rounImageView.setImageBitmap(bitmap);
    }

    public void setHeadImageByDrawable(Drawable drawable)
    {
        rounImageView.setImageDrawable(drawable);
    }

    /**
     * 改变头像等级图片风格，true直播风格 false正常风格
     * 尽量不要用代码方式确定风格，某些情况会出现页面不适配的情况，在布局属性live_state上进行确定
     *
     * @param flag
     */
    @Deprecated
    public void setLiveState(boolean flag)
    {
        this.liveState = flag;
    }

    private <T extends View> T getViewById(View view, int id)
    {
        return (T) view.findViewById(id);
    }

    private int first=1;
    //适配
    @Override
    public void onGlobalLayout()
    {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        this.width = getMeasuredWidth();
        ViewGroup.LayoutParams pa = this.getLayoutParams();


        if(first==1)
        {

            //角标
            rightLev.setImageResource(R.drawable.v_28x28);
            showRightIcon(showRightIcon);
            first=0;
        }

//        //等级标识
//        pa = rivLev.getLayoutParams();
//        pa.height = (int) (this.width * 0.4);
//        pa.width = (int) (this.width * 0.68);
//        rivLev.setLayoutParams(pa);
        rivLev.setVisibility(GONE);

        pa = rightLev.getLayoutParams();
        if (pa.height == 0)
        {
            pa.height = (int) (this.width * 0.3);
            pa.width = (int) (this.width * 0.3);
            rightLev.setLayoutParams(pa);
        }



        if (level != -1)
            upLevelData(level);
    }

}
