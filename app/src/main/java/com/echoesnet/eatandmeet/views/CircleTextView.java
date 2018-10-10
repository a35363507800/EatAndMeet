package com.echoesnet.eatandmeet.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.echoesnet.eatandmeet.R;

/**
 * Created by lc on 2017/6/21 11.
 */

public class CircleTextView extends AppCompatTextView
{
    private Paint mBgPaint ;
    PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public CircleTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public CircleTextView(Context context, AttributeSet attrs)
    {
        this(context,attrs,0);
    }

    public CircleTextView(Context context)
    {
        this(context,null);
    }
    private void init()
    {
        mBgPaint = new Paint();
//        mBgPaint.setColor(Color.parseColor("#ff3657"));
        mBgPaint.setColor(Color.parseColor("#ff5b4c"));
        mBgPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int max = Math.max(measuredWidth, measuredHeight);
        setMeasuredDimension(max, max);
    }

    @Override
    public void setBackgroundColor(int color)
    {
        mBgPaint.setColor(color);
    }

    /**
     * 设置通知个数显示
     *
     * @param text
     */
    public void setNotifiText(int text)
    {
        setText(text + "");
    }

    /**
     * 设置通知个数显示
     *
     * @param text
     */
    public void setNotifiText(String text)
    {
        setText(text);
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.setDrawFilter(pfd);//给Canvas加上抗锯齿标志
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.max(getWidth(), getHeight()) / 2, mBgPaint);
        super.draw(canvas);
    }
}
