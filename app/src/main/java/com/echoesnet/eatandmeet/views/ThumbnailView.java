package com.echoesnet.eatandmeet.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.orhanobut.logger.Logger;

/**
 * 视频剪切时长 拖动控件
 */
public class ThumbnailView extends View
{

    private final String TAG = ThumbnailView.class.getSimpleName();
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private RectF rectF;
    private RectF rectF2;
    private float linePosition;
    private int rectWidth;
    private Bitmap bitmap;
    private OnScrollBorderListener onScrollBorderListener;
    private int maxPx;
    private int minPx;
    private int padPx;//左右空白区域
    private LineHandler lineHandler;
    private int lineSpeed;// 光标速度 每100毫秒 移动多少

    public ThumbnailView(Context context) {
        super(context);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        lineHandler = new LineHandler();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        int dp5 = CommonUtils.dp2px(getContext(),5);
        mPaint.setStrokeWidth(dp5);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.video_thumbnail);

        rectWidth = CommonUtils.dp2px(getContext(),10);
        maxPx = rectWidth;
    }

    public void setMinInterval(int itemWidth){
        int maxPx = itemWidth * 10;
        minPx = itemWidth;
        if(mWidth>0 && maxPx > mWidth){
            maxPx = mWidth;
        }
        int padPx = itemWidth * 2;
        this.maxPx = maxPx;
        this.padPx = padPx;

        if (rectF == null)
        rectF = new RectF();
        rectF.left = padPx;
        rectF.top = 0;
        rectF.right = padPx + rectWidth;
        rectF.bottom = mHeight;

        if (rectF2 == null)
        rectF2 = new RectF();
        rectF2.left = padPx + maxPx - rectWidth;
        rectF2.top = 0;
        rectF2.right = padPx + maxPx;
        rectF2.bottom = mHeight;

        lineSpeed = maxPx/100;
        linePosition = padPx + rectWidth;
        Log.d(TAG,"lineSpeed>>" + lineSpeed);
        invalidate();
    }

    public void start(){
        linePosition = rectF.left;
        if (lineHandler != null && !lineHandler.hasMessages(0))
            lineHandler.sendEmptyMessageDelayed(0, 100);
    }

    public interface OnScrollBorderListener{
        void OnScrollBorder(float start, float end);
        void onScrollStateChange();
        void complete();
    }

    public void setOnScrollBorderListener(OnScrollBorderListener listener){
        this.onScrollBorderListener = listener;
    }

    public float getLeftInterval(){
        return rectF.left;
    }
    public float getTopInterval(){
        return rectF2.top;
    }
    public float getEndLeftInterval(){
        return rectF2.left;
    }
    public float getRightInterval(){
        return rectF2.right;
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mWidth == 0) {
            mWidth = getWidth();
            mHeight = getHeight();

            if (rectF == null)
                rectF = new RectF();
            rectF.left = padPx;
            rectF.top = 0;
            rectF.right = padPx + rectWidth;
            rectF.bottom = mHeight;

            if (rectF2 == null)
                rectF2 = new RectF();
            rectF2.left = padPx + maxPx - rectWidth;
            rectF2.top = 0;
            rectF2.right = padPx + maxPx;
            rectF2.bottom = mHeight;
        }
    }

    private float downX;
    private boolean scrollLeft;
    private boolean scrollRight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        move(event);
        return scrollLeft || scrollRight;
    }

    boolean scrollChange;
    private boolean move(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                if (downX > rectF.left-rectWidth/2 && downX < rectF.right+rectWidth/2) {
                    scrollLeft = true;
                }
                if (downX > rectF2.left-rectWidth/2 && downX < rectF2.right+rectWidth/2) {
                    scrollRight = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX();

                float scrollX = moveX - downX;

                if (scrollLeft) {
                    rectF.left = rectF.left + scrollX;
                    rectF.right = rectF.right + scrollX;

                    if(rectF.left < padPx){
                        rectF.left = padPx;
                        rectF.right =  padPx + rectWidth;
                    }
                    if(rectF.left < rectF2.right- maxPx){
                        rectF.left = rectF2.right- maxPx;
                        rectF.right = rectF.left+rectWidth;
                    }
                    if (rectF2.right < rectF.right + minPx)  //锁死最小宽度 一个item宽度
                    {
                        rectF.right = rectF2.right - minPx;
                        rectF.left = rectF2.left - minPx;
                    }
                    scrollChange = true;
                    invalidate();
                } else if (scrollRight) {
                    rectF2.left = rectF2.left + scrollX;
                    rectF2.right = rectF2.right + scrollX;

                    if(rectF2.right > padPx + maxPx){
                        rectF2.right = padPx + maxPx;
                        rectF2.left = rectF2.right - rectWidth;
                    }
                    if(rectF2.right > rectF.left + maxPx){
                        rectF2.right = rectF.left + maxPx;
                        rectF2.left = rectF2.right - rectWidth;
                    }
                    if (rectF.right > rectF2.right - minPx) //锁死最小宽度 一个item宽度
                    {
                        rectF2.right = rectF.right + minPx;
                        rectF2.left = rectF.left + minPx;
                    }
                    scrollChange = true;
                    invalidate();
                }
                Log.d(TAG,"start>>" + rectF.left + "| end>>" + rectF2.right );
                if(onScrollBorderListener != null){
                    onScrollBorderListener.OnScrollBorder(rectF.left, rectF2.right);
                }


                downX = moveX;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                downX = 0;
                scrollLeft = false;
                scrollRight = false;
                if(scrollChange && onScrollBorderListener != null){
                    onScrollBorderListener.onScrollStateChange();
                }
                scrollChange = false;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(Color.WHITE);

        Rect rect = new Rect();
        rect.left = (int) rectF.left;
        rect.top = (int) rectF.top;
        rect.right = (int) rectF.right;
        rect.bottom = (int) rectF.bottom;
        canvas.drawBitmap(bitmap, null, rectF, mPaint);

        Rect rect2 = new Rect();
        rect2.left = (int) rectF2.left;
        rect2.top = (int) rectF2.top;
        rect2.right = (int) rectF2.right;
        rect2.bottom = (int) rectF2.bottom;
        canvas.drawBitmap(bitmap, null, rectF2, mPaint);



        canvas.drawLine(rectF.left, 0, rectF2.right, 0, mPaint);
        canvas.drawLine(rectF.left, mHeight, rectF2.right, mHeight, mPaint);
        int dp1 = CommonUtils.dp2px(getContext(),1);
        mPaint.setStrokeWidth(dp1);
        canvas.drawLine(linePosition,rectF.top,linePosition,rectF.bottom,mPaint);
        int dp5 = CommonUtils.dp2px(getContext(),5);
        mPaint.setStrokeWidth(dp5);
        mPaint.setColor(Color.parseColor("#99313133"));

        RectF rectF3 = new RectF();
        rectF3.left = 0;
        rectF3.top = 0;
        rectF3.right = rectF.left;
        rectF3.bottom = mHeight;
        canvas.drawRect(rectF3, mPaint);

        RectF rectF4 = new RectF();
        rectF4.left = rectF2.right;
        rectF4.top = 0;
        rectF4.right = mWidth;
        rectF4.bottom = mHeight;
        canvas.drawRect(rectF4, mPaint);
    }


    private long startTime;
   class LineHandler extends Handler
   {
       @Override
       public void handleMessage(Message msg)
       {
           super.handleMessage(msg);
           if (linePosition < rectF2.left)
           {
               linePosition = linePosition + lineSpeed;
           }
           else
           {
               if (onScrollBorderListener != null)
               {
                   onScrollBorderListener.complete();
                   Logger.t(TAG).d("剪切时长 》》》》》" + (System.currentTimeMillis() - startTime));
               }
               startTime = System.currentTimeMillis();
               linePosition = rectF.left;
           }
            invalidate();
            sendEmptyMessageDelayed(0,100);
       }
   }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (lineHandler != null)
            lineHandler.removeMessages(0);
    }
}