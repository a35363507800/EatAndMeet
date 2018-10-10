package com.echoesnet.eatandmeet.views.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.orhanobut.logger.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/7/31.
 *
 * @author ling
 */

public class DragLayout extends RelativeLayout


{


    private ScheduledThreadPoolExecutor timer;
    private Handler handler;

    public DragLayout(Context context)
    {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private int screenWidth;
    private int screenHeight;

    private int viewHeight = 10001;
    private int viewWidth = -10001;

    private void initView()
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

    }
    boolean isMove = false;
    private VelocityTracker vTracker = null;
    private LinkedList<Integer> listX=new LinkedList<>();
    private LinkedList<Integer> listY=new LinkedList<>();
    private void addXY(int x,int y)
    {
        listX.offerLast(x);
        listY.offerLast(y);
    }
    private void clearXY()
    {
        listX.clear();
        listY.clear();
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event)
    {
        try {
            super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException  e) {
            e.printStackTrace();
        }
        return false ;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        try{
        switch (event.getAction())
        {

            case MotionEvent.ACTION_DOWN:
                save((int) event.getRawX(), (int) event.getRawY());
                getParent().requestDisallowInterceptTouchEvent(true);

                if(vTracker == null){
                    vTracker = VelocityTracker.obtain();
                }else{
                    vTracker.clear();
                }

                clearXY();
                break;
            case MotionEvent.ACTION_UP:
                boolean isShow=false;
                final RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) getLayoutParams();
                if(!isMove)
                {
                    isShow=true;

                    if(pa.rightMargin<0||pa.rightMargin>screenWidth-viewWidth)
                    {

                        //全屏吸附
                        implant2();
                    }
                    else
                    {
                        if (onClick != null)
                            onClick.onClick();
                    }

                }
                isMove = false;
                getParent().requestDisallowInterceptTouchEvent(false);

                if(!isShow)
                {
                    if (pa.rightMargin < 0 || pa.rightMargin > screenWidth - viewWidth)
                    {
                        //吸附/回弹
                        implant();
                    } else
                    {
                        //全屏吸附
                        implant2();
                    }
                }
                //惯性效果
              //  inertia();

                return false;
            case MotionEvent.ACTION_MOVE:
                if(lastX-event.getRawX()>10||lastX-event.getRawX()<-10||lastY-event.getRawY()>10||lastY-event.getRawY()<-10)
                isMove = true;

                getParent().requestDisallowInterceptTouchEvent(true);
                moveTo((int) event.getRawX(), (int) event.getRawY());

                vTracker.addMovement(event);
                vTracker.computeCurrentVelocity(1000);

                addXY((int) event.getRawX(),(int) event.getRawY());
                break;
            case MotionEvent.ACTION_CANCEL:
                isMove = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            case MotionEvent.ACTION_POINTER_UP:
              return true;


        }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return true;
    }
    private void inertia()
    {
//       final int gx=Math.abs((int)vTracker.getXVelocity());
//       final int gy=Math.abs((int)vTracker.getYVelocity());

       int leftAndRight=0;
       int topAndBottom=0;

       Integer x=listX.pollLast();
       Integer lastx=listX.pollLast();

       Integer y=listY.pollLast();
       Integer lasty=listY.pollLast();

        int gx=0;
        int gy=0;

        if(x!=null&&lastx!=null&&y!=null&&lasty!=null)
        {
            if(x.intValue()-lastx.intValue()>0)
                leftAndRight=1;

            if(y.intValue()-lasty.intValue()>0)
                topAndBottom=1;

            gx=Math.abs(x.intValue()-lastx.intValue());
            gy=Math.abs(y.intValue()-lasty.intValue());
        }



        final RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) getLayoutParams();

        final int oldRightMargin = pa.rightMargin;
        final int oldBottomMargin = pa.bottomMargin;
        final int result = leftAndRight;
        Interpolator ip=new DecelerateInterpolator();

        final ValueAnimator anim = ValueAnimator.ofInt(gx, 0);
        anim.setDuration(500);
        anim.setInterpolator(ip);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int currentValue = (int) animation.getAnimatedValue();
                if (result==1)
                    currentValue = -currentValue;

                pa.rightMargin = pa.rightMargin + currentValue;
                setLayoutParams(pa);
            }
        });
        anim.start();

        final ValueAnimator anim2 = ValueAnimator.ofInt(gy, 0);
        final int result2 = topAndBottom;
        anim2.setInterpolator(ip);
        anim2.setDuration(500);
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int currentValue = (int) animation.getAnimatedValue();
                if (result2==1)
                    currentValue = -currentValue;

                int bottom=pa.bottomMargin+  currentValue;
                if(bottom>((RelativeLayout)getParent()).getHeight())
                    bottom=pa.bottomMargin;

                pa.bottomMargin = bottom;
                setLayoutParams(pa);
            }
        });
        anim2.start();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //吸附/回弹
                implant();
            }
        }, 500);
    }

    //贴边吸附效果
    private void implant()
    {

        int leftAndRight = 0;
        int TopAndBottom = 0;
        final RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) getLayoutParams();
        int movePx=0;
        int movePx2=0;

        //左边吸附数据
        if (pa.rightMargin >= screenWidth - viewWidth)
        {
            leftAndRight = 1;
            movePx=viewWidth/2-(pa.rightMargin+viewWidth-screenWidth);
        }
        //右边吸附数据
        if (pa.rightMargin <= 0)
        {
            leftAndRight = 2;
            movePx=viewWidth / 2-(Math.abs(pa.rightMargin));
        }
        //上边吸附数据
        if (pa.bottomMargin>((RelativeLayout)getParent()).getHeight()-viewHeight)
        {
            TopAndBottom = 3;
            movePx2=((RelativeLayout)getParent()).getHeight()-pa.bottomMargin+viewHeight;
        }

        //下边吸附数据
        if (pa.bottomMargin<0)
        {
            TopAndBottom = 4;
            movePx2=0-pa.bottomMargin;
        }


        final int result = leftAndRight;
        final int oldRightMargin = pa.rightMargin;
        final int oldBottomMargin = pa.bottomMargin;

        if (leftAndRight!=0)
        {
            final ValueAnimator anim = ValueAnimator.ofInt(0, movePx);
            anim.setDuration(500);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    int currentValue = (int) animation.getAnimatedValue();
                    if (result == 2)
                        currentValue = -currentValue;

                    pa.rightMargin = oldRightMargin + currentValue;
                    setLayoutParams(pa);
                }
            });
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    anim.start();
                }
            }, 100);

        }

        final int result2 = TopAndBottom;
        if (TopAndBottom!=0)
        {
            final ValueAnimator anim = ValueAnimator.ofInt(0, movePx2);
            anim.setDuration(500);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    int currentValue = (int) animation.getAnimatedValue();

                    if (result2 == 3)
                        currentValue = -currentValue;

                    pa.bottomMargin = oldBottomMargin + currentValue;
                    setLayoutParams(pa);
                }
            });
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    anim.start();
                }
            }, 100);

        }

    }

    //全屏吸附效果
    private void implant2()
    {
        //防止多次触发，只保留一个待执行线程
        if (handler != null)
            handler.removeCallbacks(runnable);

        int leftAndRight = 0;
        int TopAndBottom = 0;
        final RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) getLayoutParams();
        int movePx=0;
        int movePx2=0;

        //左边吸附数据
        if (pa.rightMargin >= screenWidth/2 - viewWidth/2)
        {
            leftAndRight = 1;
            movePx=(screenWidth-pa.rightMargin-viewWidth);
        }
        //右边吸附数据
        if (pa.rightMargin < screenWidth/2 - viewWidth/2)
        {
            leftAndRight = 2;
            movePx=pa.rightMargin;
        }
        //上边吸附数据
        if (pa.bottomMargin>((RelativeLayout)getParent()).getHeight()-viewHeight)
        {
            TopAndBottom = 3;
            movePx2=((RelativeLayout)getParent()).getHeight()-pa.bottomMargin+viewHeight;
        }

        //下边吸附数据
        if (pa.bottomMargin<0)
        {
            TopAndBottom = 4;
            movePx2=0-pa.bottomMargin;
        }


        final int result = leftAndRight;
        final int oldRightMargin = pa.rightMargin;
        final int oldBottomMargin = pa.bottomMargin;

        if (leftAndRight!=0)
        {
            final ValueAnimator anim = ValueAnimator.ofInt(0, movePx);
            anim.setDuration(500);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    int currentValue = (int) animation.getAnimatedValue();
                    if (result == 2)
                        currentValue = -currentValue;

                    pa.rightMargin = oldRightMargin + currentValue;
                    setLayoutParams(pa);
                }
            });
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    anim.start();
                }
            }, 100);

        }

        final int result2 = TopAndBottom;
        if (TopAndBottom!=0)
        {
            final ValueAnimator anim = ValueAnimator.ofInt(0, movePx2);
            anim.setDuration(500);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    int currentValue = (int) animation.getAnimatedValue();

                    if (result2 == 3)
                        currentValue = -currentValue;

                    pa.bottomMargin = oldBottomMargin + currentValue;
                    setLayoutParams(pa);
                }
            });
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    anim.start();
                }
            }, 100);
        }

        if(handler==null)
        this.handler = new Handler();

        handler.postDelayed(runnable,5000);

    }


    Runnable runnable=new Runnable()
    {
        @Override
        public void run()
        {
            implant();
        }
    };

    private void save(int x, int y)
    {
        lastX = x;
        lastY = y;
    }

    private int lastX;
    private int lastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0)
    {
        return true;
    }

    private void moveTo(int x, int y)
    {

        if (viewHeight == 10001)
        {
            viewHeight = getHeight();
            viewWidth = getWidth();
        }

        RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) getLayoutParams();
        int left = -(viewWidth / 2);
        int top = 0-300;
        int right = pa.rightMargin + (lastX - x);
        int  bottom=pa.bottomMargin + (lastY - y);

        //限制上边缘
        if (bottom>((RelativeLayout)getParent()).getHeight()-viewHeight)
        {
                bottom = pa.bottomMargin;
        }

        //限制左边缘
        if (right > screenWidth - viewWidth / 2)
        {
            right = pa.rightMargin;
        }

        //限制下边缘
        if (bottom < 0)
           // bottom = 0;

            //限制右边缘
        if (right < -(getHeight() / 2))
            right = -(getHeight() / 2);


        pa.bottomMargin = bottom;
        pa.rightMargin = right;
        pa.leftMargin = left;
        pa.topMargin = top;
        setLayoutParams(pa);



        Logger.t("ttttttttttttttttt").d(left + "/" + top + "/" + right + "/" + bottom+"/");

        //   layout(left,top,right,bottom);

        save(x, y);
    }

    public interface OnClickListenern
    {
        void onClick();

    }
    public void setOnClickListenern(OnClickListenern onClickListenern)
    {
        onClick = onClickListenern;
    }
    private  OnClickListenern onClick;
}
