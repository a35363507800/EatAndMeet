package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.senab.photoview.log.Logger;

/**
 * 自定义选项卡控件
 * 调用beginTextData方法传入字符串数组运作控件，其他参数需在之前设置
 * 可设置项如下:
 * 滚动条参数:
 * 设置滚动条颜色:setScrollbarColor
 * 设置滚动条高度:setScrollbarSize  0-5
 * 设置滚动条滑动速度:setScrollbarRate
 * <p>
 * 文字选项卡参数:
 * 设置选项卡是否可以滑动:setScrollFlag(boolean flag)
 * 设置选项卡左右间距:setPadding(int padding)
 * 设置选项卡文字大小:setTextSize(int textSize)
 * 设置选项卡文字未选中颜色:setDefaultColor
 * 设置选项卡文字选择颜色:setClickColor
 * <p>
 * 设置选中项:setSelected(int position)
 * 设置点击监听:setOnItemClickListener(OnItemClickListener on)
 * <p>
 * * @author Administrator
 */
public class ScrollTextTabView extends HorizontalScrollView
{
  private String TAG = ScrollTextTabView.class.getSimpleName();
    public static final int TEXTMODE_DEFAULTBOLD = 1;
    public static final int TEXTMODE_CHECKBOLD = 2;

    //------------屏幕数据---------------
    private int width;
    private int height;
    //----------------------------
    //------------滚动条参数-------------
    //滚动条颜色
    private int barColor = Color.parseColor("#f15a22");
    //滚动条大小
    private int barSize = 1;
    //滚动条起始位置
    private int barStartX = 0;
    //滚动条结束位置
    private int barStopX = 0;
    //滚动条动作判断
    private boolean barFlag = false;
    //滚动条置底View
    private View barView;
    //滚动条滑动速度
    private int barRate = 15;
    //-------------------------

    //-------------监听-------------
    private OnItemClickListener item;
    //------------------------------
    //-------------文字选项卡参数-------------
    //是否可以滑动
    private boolean scrollFlag = false;
    //文字数组
    private String text[] = new String[]{""};
    //文字选项卡的文字间距
    private int padding = 0;
    //文字选项卡的文字大小
    private int textSize = 10;
    //文字选项卡的默认颜色;
    private int defaultColor = Color.parseColor("#130c0e");
    //文字选项卡的点击颜色
    private int clickColor = Color.parseColor("#f58220");
    //选项卡之间是否有线间隔
    private boolean lineShow = false;
    //选项卡选中文字加粗模式
    private int textBoldStyle = 0;


    //选项卡之间线粗
    private int lineBold = 1;
    //选项卡之间线高
    private int lineSize = 25;
    //选项卡之间线颜色
    private int lineColor = Color.parseColor("#e6e6e6");


    //----------------------------------------
    //声明绘画笔
    private Paint paint;
    private Context c;
    private LinearLayout lin;

    public ScrollTextTabView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.c = scanForActivity(context);
        init();
    }

    public ScrollTextTabView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.c = scanForActivity(context);
        init();
    }

    public ScrollTextTabView(Context context)
    {
        super(context);
        this.c = scanForActivity(context);
        init();
    }

    private static Activity scanForActivity(Context cont)
    {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());


        return null;
    }

    //--------------------------选项卡文字设置---------------------


    public void setLineBold(int lineBold)
    {
        this.lineBold = lineBold;
    }

    /**
     * 选项卡之间是否有线间隔
     *
     * @param lineShow
     */
    public void setLineShow(boolean lineShow)
    {
        this.lineShow = lineShow;
    }


    /**
     * 设置选项卡线高度
     *
     * @param lineSize
     */
    public void setLineSize(int lineSize)
    {
        this.lineSize = lineSize;
    }

    /**
     * 设置文字加粗类型
     */
    public void setTextBoldStyle(int mode)
    {
        this.textBoldStyle = mode;
    }

    /**
     * 设置选项卡文字默认颜色
     *
     * @param defaultColor
     */
    public void setLineColor(String defaultColor)
    {
        this.lineColor = Color.parseColor(defaultColor);
    }

    /**
     * 设置选项卡文字默认颜色
     *
     * @param id
     */
    public void setLineColor(int id)
    {
        this.lineColor = ContextCompat.getColor(c, id);
    }

    /**
     * 设置选项卡是否可以滑动
     */
    public void setScrollFlag(boolean flag)
    {
        this.scrollFlag = flag;
    }

    /**
     * 设置选项卡文字左右间距
     */
    public void setPadding(int padding)
    {
        if (padding > 0)
            this.padding = padding;
    }

    /**
     * 获取选项卡文字大小
     *
     * @return
     */
    public int getTextSize()
    {
        return textSize;
    }

    /**
     * 设置选项卡文字大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize)
    {
        if (textSize >= 0)
            this.textSize = textSize;
    }

    /**
     * 返回选项卡文字默认颜色
     *
     * @return
     */
    public String getDefaultColor()
    {
        return Integer.toHexString(this.defaultColor);
    }

    /**
     * 设置选项卡文字默认颜色
     *
     * @param defaultColor
     */
    public void setDefaultColor(String defaultColor)
    {
        this.defaultColor = Color.parseColor(defaultColor);
    }

    /**
     * 设置选项卡文字默认颜色
     *
     * @param id
     */
    public void setDefaultColor(int id)
    {
        this.defaultColor = ContextCompat.getColor(c, id);
    }

    /**
     * 设置选项卡文字点击颜色
     */
    public void setClickColor(int id)
    {
        this.clickColor = ContextCompat.getColor(c, id);
    }

    /**
     * 设置选项卡文字点击颜色
     */
    public void setClickColor(String clickColor)
    {
        this.clickColor = Color.parseColor(clickColor);
    }

    //------------------------------------------------------------
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {

        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        paint = new Paint();
        paint.setColor(Color.parseColor("#d9d6c3"));
        paint.setAlpha(255);


        if (barSize != 0)
            canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth() + padding * 2 * text.length, getMeasuredHeight(), paint);

        if (lineShow)
        {
            paint.setColor(lineColor);
            paint.setStrokeWidth(lineBold);
            for (int i = 0; i < text.length - 1; i++)
            {
                canvas.drawLine(lin.getChildAt(i + 1).getX(), getMeasuredHeight() / 2 - lineSize, lin.getChildAt(i < text.length - 1 ? i + 1 : 0).getX(), getMeasuredHeight() / 2 + lineSize, paint);
            }
        }

        paint = new Paint();
        paint.setColor(barColor);
        paint.setAlpha(255);
        canvas.drawRoundRect(new RectF(barStartX,
                getMeasuredHeight() - barSize,
                barStopX,
                getMeasuredHeight()), 0, 0, paint);


        if (barFlag)
            startDrawBar(null);


        super.onDraw(canvas);
    }


    //-----------------------------滚动条参数设置------------------------

    /**
     * 设置滚动条颜色 资源ID
     *
     * @param color
     */
    public void setScrollbarColor(int color)
    {
        this.barColor = ContextCompat.getColor(c, color);
    }

    /**
     * 设置滚动条颜色
     *
     * @param color
     */
    public void setScrollbarColor(String color)
    {
        this.barColor = Color.parseColor(color);
    }

    /**
     * 设置滚动条高度
     *
     * @param size
     */
    public void setScrollbarSize(int size)
    {
        if (size >= 0)
            this.barSize = size;
    }

    /**
     * 设置滚动条滑动速度
     *
     * @param size
     */
    public void setScrollbarRate(int size)
    {
        if (size > 0)
            this.barRate = size;
    }

    //---------------------------------辅助方法------------------

    /**
     * 数据适配，进行参数初始化，执行此方法控件开始运作
     *
     * @param txt
     */
    public void beginTextData(String[] txt)
    {

        lin.removeAllViews();

        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(metric);

        height = metric.heightPixels;
        width = metric.widthPixels;
        text = txt;
        for (int i = 0; i < txt.length; i++)
        {
            TextView tv = new TextView(c);
            tv.setTextSize(textSize);
            tv.setTextColor(defaultColor);
            tv.setText(txt[i]);
            tv.setTag(i);
            tv.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(!scrollFlag ? width / txt.length : LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            pa.setMargins(padding, 5, padding, 5);

            if (textBoldStyle == TEXTMODE_DEFAULTBOLD)
            {
                Paint paint = tv.getPaint();
                paint.setFakeBoldText(true);
                tv.setLayerPaint(paint);
            }
            tv.setLayoutParams(pa);
            lin.addView(tv);

        }
        if (scrollFlag)
            padding = 0;

        setSelected(0);

    }


    /**
     * 初始化参数
     */
    private void init()
    {
        setHorizontalScrollBarEnabled(false);
        setHorizontalFadingEdgeEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);


        lin = new LinearLayout(c);
        lin.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        addView(lin);

        initListener();
    }


    /**
     * 初始化点击选项卡监听
     */
    private void initListener()
    {
        lin.setOnTouchListener(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    View tView = getClickView((int) event.getX());
                    if (tView != null)
                    {
                        dataChange(tView);
                    }
                }

                return false;
            }
        });

    }

    /**
     * 重置控件颜色
     */
    private void defaultTextColor(View view)
    {
        for (int i = 0; i < lin.getChildCount(); i++)
        {
            TextView v = (TextView) lin.getChildAt(i);
            v.setTextColor(defaultColor);
            if (textBoldStyle == TEXTMODE_CHECKBOLD)
            {
                Paint paint = v.getPaint();
                paint.setFakeBoldText(false);
                v.setLayerPaint(paint);
            }
        }
        try
        {
            ((TextView) view).setTextColor(this.clickColor);
            if (textBoldStyle == TEXTMODE_CHECKBOLD)
            {
                Paint paint = ((TextView) view).getPaint();
                paint.setFakeBoldText(true);
                ((TextView) view).setLayerPaint(paint);
            }
        } catch (Exception e)
        {
        }

    }

    /**
     * 滚动条开始移动
     *
     * @param view
     */
    private void startDrawBar(View view)
    {
        if (view != null)
            this.barView = view;

        int rate = barRate;
        int viewleft = barView.getLeft();

        if (viewleft - barStartX > barView.getMeasuredWidth() * 2 + padding * 2 || barStartX - viewleft > barView.getMeasuredWidth() * 2 + padding * 2)
        {
            barRate = 100;
        }

        if (viewleft - padding / 2 > barStartX || viewleft + barView.getMeasuredWidth() + padding / 2 > barStopX)
        {
            rightStrat();
        } else if (viewleft - padding / 2 < barStartX || viewleft + barView.getMeasuredWidth() + padding / 2 < barStopX)
        {
            leftStrat();
        } else
        {
            this.startx = barStartX;
            this.stopx = barStopX;
            barFlag = false;
        }
        barRate = rate;
        postInvalidate();

    }

    /**
     * 滚动条向右移动
     */
    private void rightStrat()
    {


        this.barStartX += this.barRate;
        if (barStartX + padding / 2 > this.barView.getLeft())
            this.barStartX = this.barView.getLeft() - padding / 2;


        this.barStopX += this.barRate;
        if (barStopX + padding / 2 + barView.getMeasuredWidth() * 2 > this.barView.getLeft() + this.barView.getMeasuredWidth())
            this.barStopX = this.barView.getLeft() + this.barView.getMeasuredWidth() + padding / 2;

    }

    /**
     * 滚动条向左移动
     */
    private void leftStrat()
    {

        this.barStartX -= this.barRate;
        if (barStartX - padding / 2 - barView.getMeasuredWidth() * 2 < this.barView.getLeft())
            this.barStartX = this.barView.getLeft() - padding / 2;


        this.barStopX -= this.barRate;
        if (barStopX + padding / 2 < this.barView.getLeft() + this.barView.getMeasuredWidth())
            this.barStopX = this.barView.getLeft() + this.barView.getMeasuredWidth() + padding / 2;
    }

    /**
     * 根据坐标获取对应View
     *
     * @param x
     * @return
     */
    private View getClickView(int x)
    {
        View view = null;
        for (int i = 0; i < lin.getChildCount(); i++)
        {
            View v = lin.getChildAt(i);
            int newx = v.getLeft();
            if (x >= newx && x < newx + v.getMeasuredWidth())
            {
                view = lin.getChildAt(i);
                view.setTag(i);
                break;
            }
        }
        return view;
    }

    private void dataChange(View tView)
    {
        if (item != null)
        {
            item.onItemClick(tView, (int) tView.getTag());
        }
        //滚动条移动
        startDrawBar(tView);
        barFlag = true;

        //改变文字颜色
        defaultTextColor(tView);
    }

    /**
     * 选择项
     *
     * @param position
     */
    public void setSelected(int position)
    {
        if (position < 0)
            return;
        View view = lin.getChildAt(position);
        if (view != null)
            dataChange(view);
    }

    //--------------------------------------监听接口

    /**
     * 监听接口
     *
     * @author Administrator
     */
    public interface OnItemClickListener
    {
        //多项点击监听
        public abstract void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener on)
    {
        if (on != null)
        {
            this.item = on;
        }
    }

    public ViewPager getViewpager()
    {
        return viewpager;
    }

    /**
     * 调用以后会自动与viewpager匹配position
     *
     * @param viewpager
     */
    public void setViewpager(ViewPager viewpager)
    {
        this.viewpager = viewpager;
        initViewPager();
    }

    private ViewPager viewpager;

    private float stopx;
    private float startx;

    private void moveToOffset(boolean isLeft, float ofSet)
    {
        int w = barStopX - barStartX;
        if (isLeft)
        {
            this.barStartX = (int) (this.startx - w * (1 - ofSet));
            this.barStopX = (int) (this.stopx - w * (1 - ofSet));
        } else
        {
            this.barStartX = (int) (this.startx + w * ofSet);
            this.barStopX = (int) (this.stopx + w * ofSet);
        }
        postInvalidate();

    }

    private void initViewPager()
    {

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

                if (!getTime())
                    return;
                int posi = (int) barView.getTag();
                if (position < posi && positionOffset != 1 && positionOffset != 0)
                {
                    moveToOffset(true, positionOffset);

                } else if (position == posi && positionOffset != 1 && positionOffset != 0)
                {
                    moveToOffset(false, positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position)
            {
                startTime();
                setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, final int position)
            {
                viewpager.setCurrentItem(position, true);
            }
        });
    }

    private long time;

    private boolean getTime()
    {
        return System.currentTimeMillis() - time > 359;
    }

    private void startTime()
    {
        time = System.currentTimeMillis();
    }
}
