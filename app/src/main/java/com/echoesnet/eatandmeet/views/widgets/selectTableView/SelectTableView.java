package com.echoesnet.eatandmeet.views.widgets.selectTableView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.DinersBean;
import com.echoesnet.eatandmeet.models.bean.TableBean;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangben on 2016/4/23.
 */
public class SelectTableView extends View
{
    public static final String TAG=SelectTableView.class.getSimpleName();
    //region 变量
    public static final float MIN_SCALE=0.5f;
    public static final float MAX_SCALE=2.0f;
    private static final int INVALID_POINTER_ID = -1;

    private Bitmap mFloor;
    private boolean enableScale=true;

    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;

    //缩放的中心点
    private float mScalePivotX=100.0F;
    private float mScalePivotY=100.0F;
    //底图大小
    private float fWidth;
    private float fHeight;

    //当前正在移动object的pointer
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private GestureDetector gestureDetector;


    private ITableClickListener tcListener;
    private IDinerClickListener dcListener;
    private IOnDoubleClickListener mOnDoubleClickListener;
    //地板区域的左上角和右下角
    private float leftTopX,leftTopY,rightBottomX,rightBottomY;
    private int viewCenterX,viewCenterY;

    //是否在移动
    private boolean isMoving=false;
    private int index=0;

    //动画计时器
    private Timer timer;
    private AnimateTimerTask timerTask;
    //endregion

    public void setOnDoubleClickListener(IOnDoubleClickListener listener)
    {
        mOnDoubleClickListener = listener;
    }

    //**********************
    private List<TableBean> tableLst;
    private List<DinersBean>dinerLst;
    private List<Bitmap>talkAnimate;
    private Context mContext;


    public SelectTableView(Context context)
    {
        this(context,null);
    }

    public SelectTableView(Context context, AttributeSet attrs)
    {
        this(context, attrs,0);
    }

    public SelectTableView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs)
    {
        this.mContext =context;
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.SelectTableView);
        enableScale= ta.getBoolean(R.styleable.SelectTableView_enableScale,false);
        ta.recycle();

        leftTopX=0;
        leftTopY=0;
        rightBottomX=800;
        rightBottomY=800;
        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        // creating new gesture detector
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    //初始绘制为最合适的尺寸，以后只可缩小，不可以放大
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (mFloor==null)
            return;

        canvas.save();
        canvas.translate(mPosX,mPosY);
        canvas.scale(mScaleFactor, mScaleFactor,mScalePivotX,mScalePivotY);
        canvas.drawBitmap(mFloor,null,new RectF(0,0, fWidth, fHeight),null);
        canvas.restore();

        if (tableLst!=null&&tableLst.size()!=0)
        {
            for (int i=0;i<tableLst.size();i++)
            {
                //除以2是因为数据是按照2倍图给出的
                TableBean te=tableLst.get(i);
                float tableX=te.getX2();
                float tableY=te.getY2();

                canvas.save();
                canvas.translate(mPosX,mPosY);
                canvas.scale(mScaleFactor, mScaleFactor,mScalePivotX,mScalePivotY);
                //由于美工坐标标注的问题，暂时改用另外一种比较低效的做法，比较垃圾总比没有好。
                //canvas.rotate(te.getAngle2(),tableX+te.getWidth2()/2,tableY+te.getHeight2()/2);
                /* if (te.getBitImg()!=null)
                    canvas.drawBitmap(te.getBitImg(),null,new RectF(tableX,tableY,tableX+te.getWidth2(),tableY+te.getHeight2()),null);*/
                /**
                 * 为了提高效率，减少卡顿，旋转图片的工作应该放在传入的过程中
                 */
                if (te.getBitImg()!=null)
                    canvas.drawBitmap(te.getBitImg(),null,
                            new RectF(tableX,tableY,tableX+te.getWidth2(),tableY+te.getHeight2()),null);
                canvas.restore();
            }
        }
        //用餐人绘制
        if (dinerLst!=null&&dinerLst.size()!=0)
        {
            for (int i=0;i<dinerLst.size();i++)
            {
                DinersBean diner=dinerLst.get(i);
                float dinerX=diner.getX2();
                float dinerY=diner.getY2();

                canvas.save();
                canvas.translate(mPosX,mPosY);
                canvas.scale(mScaleFactor, mScaleFactor,mScalePivotX,mScalePivotY);
                canvas.rotate(diner.getAngle2(),dinerX+diner.getWidth2()/2,dinerY+diner.getHeight2()/2);
                if (diner.getBitImg()!=null)
                    canvas.drawBitmap(diner.getBitImg(),null,new RectF(dinerX,dinerY,dinerX+diner.getWidth2(),dinerY+diner.getHeight2()),null);
                //如果在聊天则绘制聊天动画
                if (diner.getChatting().equals("1"))
                {
                    if (talkAnimate!=null&&talkAnimate.size()>0)
                    {
                        //动画的帧大小为52*46
                        if (isMoving)
                            canvas.drawBitmap(talkAnimate.get(0),null,new RectF(dinerX+28,dinerY-52,dinerX+28+52,(dinerY-52)+46),null);
                        else
                            canvas.drawBitmap(talkAnimate.get(index),null,new RectF(dinerX+28,dinerY-52,dinerX+28+52,(dinerY-52)+46),null);
                    }
                }
                canvas.restore();
            }
            if (talkAnimate!=null)
            {
                index++;
                if (index>=talkAnimate.size())
                    index=0;
            }
        }
    }

    /**
     * 双击事件
     * @return
     */
    private boolean performDoubleClick()
    {
        boolean result = false;
        if(mOnDoubleClickListener != null)
        {
            mOnDoubleClickListener.onDoubleClick(this);
            result = true;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        // Let the ScaleGestureDetector inspect all events.
        if (enableScale==true)
        {
            mScaleDetector.onTouchEvent(ev);
        }
        gestureDetector.onTouchEvent(ev);

        final int action=ev.getAction();
        switch (action&MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                final float x = ev.getX();
                final float y = ev.getY();

                // Remember where we started
                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer
                mActivePointerId = ev.getPointerId(0);

                viewCenterX=getWidth()/2;
                viewCenterY=getHeight()/2;

                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                isMoving=true;
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress())
                {
                    // Calculate the distance moved
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    // Move the object
                    mPosX += dx;
                    mPosY += dy;

                    // Invalidate to request a redraw
                    if (Math.abs(dx)>1.0f)
                    {
                        int[] currentLeftTopCor= getCorrectCoordinate(leftTopX,leftTopY);
                        int[] currentRightBottomCor= getCorrectCoordinate(rightBottomX,rightBottomY);
                        //Logger.t(TAG).d("lx:"+currentLeftTopCor[0]+"ly: "+currentLeftTopCor[1]);
                        //Logger.t(TAG).d("rx:"+currentRightBottomCor[0]+"ry: "+currentRightBottomCor[1]);
                        if (currentLeftTopCor[0]<(getWidth()-200)&&currentLeftTopCor[1]<(getHeight()-200)&&
                               currentRightBottomCor[0]>200&&currentRightBottomCor[1]>200)
                        {
                            invalidate();
                        }
/*                        if ((currentLeftTopCor[0]<=viewCenterX&&currentLeftTopCor[1]<=viewCenterY)&&
                              currentRightBottomCor[0]>=viewCenterX&&currentRightBottomCor[1]>=viewCenterY )
                        {
                            invalidate();
                        }*/
                    }
                }

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }
            case MotionEvent.ACTION_UP:
            {
                isMoving=false;
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            {
                isMoving=false;
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
            {
                isMoving=false;
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId)
                {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    /**
     * 单击处理函数
     * @param teLst 数据源
     */
    private void clickHandler(List<TableBean>teLst)
    {
        if (teLst==null)
            return;
        for(int i=0;i<teLst.size();i++)
        {
            TableBean currentTe=teLst.get(i);
            double currentDistance;
            double viewRadius =Math.sqrt(Math.pow(Math.min((currentTe.getHeight2()/2)*mScaleFactor,(currentTe.getWidth2()/2)*mScaleFactor),2));
            float tableNewX,tableNewY;
            float tableOldX=currentTe.getX2()+currentTe.getWidth2()/2;
            float tableOldY=currentTe.getY2()+currentTe.getHeight2()/2;
            if (mScaleFactor>=1)
            {
                tableNewX=tableOldX+(tableOldX-mScalePivotX)*(mScaleFactor-1)+mPosX;
                tableNewY=tableOldY+(tableOldY-mScalePivotY)*(mScaleFactor-1)+mPosY;
            }
            else
            {
                tableNewX=tableOldX-(tableOldX-mScalePivotX)*(1-mScaleFactor)+mPosX;
                tableNewY=tableOldY-(tableOldY-mScalePivotY)*(1-mScaleFactor)+mPosY;
            }
            currentDistance=Math.sqrt(Math.pow(mLastTouchX-tableNewX,2)+
                                                Math.pow(mLastTouchY-tableNewY,2));
            if (currentDistance<viewRadius)
            {
                if (tcListener!=null)
                    tcListener.onTableClick(currentTe);
                break;
            }
        }
        //invalidate();
    }
    /**
     * 单击处理函数
     * @param deLst 数据源
     */
    private void clickDinerHandler(List<DinersBean>deLst)
    {
        if (deLst==null)
            return;
        for(int i=0;i<deLst.size();i++)
        {
            DinersBean currentDe=deLst.get(i);
            double currentDistance;
            double viewRadius =Math.sqrt(Math.pow(Math.min((currentDe.getHeight2()/2)*mScaleFactor,(currentDe.getWidth2()/2)*mScaleFactor),2));
            float tableNewX,tableNewY;
            float tableOldX=currentDe.getX2()+currentDe.getWidth2()/2;
            float tableOldY=currentDe.getY2()+currentDe.getHeight2()/2;
            if (mScaleFactor>=1)
            {
                tableNewX=tableOldX+(tableOldX-mScalePivotX)*(mScaleFactor-1)+mPosX;
                tableNewY=tableOldY+(tableOldY-mScalePivotY)*(mScaleFactor-1)+mPosY;
            }
            else
            {
                tableNewX=tableOldX-(tableOldX-mScalePivotX)*(1-mScaleFactor)+mPosX;
                tableNewY=tableOldY-(tableOldY-mScalePivotY)*(1-mScaleFactor)+mPosY;
            }
            currentDistance=Math.sqrt(Math.pow(mLastTouchX-tableNewX,2)+
                    Math.pow(mLastTouchY-tableNewY,2));
            if (currentDistance<viewRadius)
            {
                if (dcListener!=null)
                    dcListener.OnDinerClick(currentDe);
                break;
            }
        }
        //invalidate();
    }
    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (mFloor != null && !mFloor.isRecycled())
        {
            mFloor.recycle();
            mFloor = null;
        }
    }
    private void scale(float scaleFactor, float pivotX, float pivotY)
    {
        mScaleFactor = scaleFactor;
        mScalePivotX = pivotX;
        mScalePivotY = pivotY;
        this.invalidate();
    }
    private void release()
    {
        if(mScaleFactor < MIN_SCALE)
        {
            final float startScaleFactor = mScaleFactor;

            Animation a = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t)
                {
                    scale(startScaleFactor + (MIN_SCALE - startScaleFactor)*interpolatedTime,mScalePivotX,mScalePivotY);
                }
            };

            a.setDuration(300);
            startAnimation(a);
        }
        else if(mScaleFactor > MAX_SCALE)
        {
            final float startScaleFactor = mScaleFactor;

            Animation a = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t)
                {
                    scale(startScaleFactor + (MAX_SCALE - startScaleFactor)*interpolatedTime,mScalePivotX,mScalePivotY);
                }
            };
            a.setDuration(300);
            startAnimation(a);
        }
    }

    //
    //region *********** 公共方法*************
    public void setTableLst(List<TableBean> tableLst)
    {
        this.tableLst=tableLst;
        invalidate();
    }
    /**
     * 设置食客数据并刷新
     * @param dinerLst
     */
    public void setDinerLst(List<DinersBean> dinerLst)
    {
        this.dinerLst=dinerLst;
        //postInvalidate();
        invalidate();
    }
    public void setTalkAnimate(List<Bitmap>talkAnimate)
    {
        this.talkAnimate=talkAnimate;
    }

    public void setFloorImg(Bitmap mFloor)
    {
        this.mFloor=mFloor;
        rightBottomX=mFloor.getWidth();
        rightBottomY=mFloor.getHeight();
        invalidate();
    }
    public void setFloorSize(float fWidth,float fHeight)
    {
        this.fWidth= fWidth;
        this.fHeight=fHeight;
        //postInvalidate();
        invalidate();
    }
    public void dataSourceChanged(List<TableBean> tableLst)
    {
        this.tableLst=tableLst;
        invalidate();
    }
    public void setScaleFactor(float factor)
    {
        mScaleFactor=factor;
        invalidate();
    }
    public void setOnTableClickListener(ITableClickListener mListener)
    {
        this.tcListener=mListener;
    }
    public void setOnDinerClickListener(IDinerClickListener mListener)
    {
        this.dcListener=mListener;
    }
    public void startTalkAnimate(int frequency)
    {
        if (timerTask==null)
        {
            timerTask=new AnimateTimerTask(this);
        }
        else
        {
            timerTask.cancel();
            timerTask=null;
            timerTask=new AnimateTimerTask(this);
        }
        if (timer==null)
        {
            timer=new Timer();
            timer.scheduleAtFixedRate(timerTask,0,frequency);
        }
        else
        {
            timer.cancel();
            timer=null;
            timer=new Timer();
            timer.scheduleAtFixedRate(timerTask,0,frequency);
        }
    }
    public void stopTalkAnimate()
    {

    }

    public void refreshSelectTableView(boolean isMainThread)
    {
        if (isMainThread)
            invalidate();
        else
           postInvalidate();
    }

    public void setInitScale(float scaleFactor)
    {
        this.mScaleFactor=scaleFactor;
        invalidate();
    }
    //endregion

    private static class AnimateTimerTask extends TimerTask
    {
        private WeakReference<SelectTableView> sTableViewS;
        private AnimateTimerTask(SelectTableView selectTableView )
        {
            this.sTableViewS=new WeakReference<SelectTableView>(selectTableView);
        }
        @Override
        public void run()
        {
            final SelectTableView sTableView=sTableViewS.get();
            if (sTableView!=null)
            {
                if (sTableView!=null&&!sTableView.isMoving)
                    sTableView.postInvalidate();
            }
        }
    }
    /**
     * 获得缩放位移后正确的坐标,浮点运算速度太慢，改为整形
     * @param inputX
     * @param inputY
     * @return
     */
    private int[] getCorrectCoordinate(float inputX,float inputY)
    {
        int[] coordinates=new int[2];
        float correctTouchX;
        float correctTouchY;

        if (mScaleFactor>=1)
        {
            correctTouchX=inputX+(inputX-mScalePivotX)*(mScaleFactor-1)+mPosX;
            correctTouchY=inputY+(inputY-mScalePivotY)*(mScaleFactor-1)+mPosY;
        }
        else
        {
            correctTouchX=inputX-(inputX-mScalePivotX)*(1-mScaleFactor)+mPosX;
            correctTouchY=inputY-(inputY-mScalePivotY)*(1-mScaleFactor)+mPosY;
        }
        coordinates[0]=(int)correctTouchX;
        coordinates[1]=(int)correctTouchY;
        return coordinates;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        float currentSpan;
        float startFocusX;
        float startFocusY;

        public boolean onScaleBegin(ScaleGestureDetector detector)
        {
            currentSpan = detector.getCurrentSpan();
            startFocusX = detector.getFocusX();
            startFocusY = detector.getFocusY();
            return true;
        }
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            mScaleFactor *= detector.getScaleFactor();
            scale(mScaleFactor,startFocusX,startFocusY);
            // Don't let the object get too small or too large.
/*            mScaleFactor = Math.max(0.3f, Math.min(mScaleFactor, 2.0f));
            if (enableScale&&detector.getScaleFactor()<1.0f)
            {
                if (fWidth*mScaleFactor<getW()||fHeight*mScaleFactor<getH())
                {
                    mScaleFactor=Math.min(getW()/fWidth,getH()/fHeight);
                }
            }*/
            invalidate();
            return true;
        }
        public void onScaleEnd(ScaleGestureDetector detector)
        {
            release();
        }
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return false;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            performDoubleClick();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            return super.onFling(e1, e2, velocityX, velocityY);

        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            super.onLongPress(e);

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            clickDinerHandler(dinerLst);
            clickHandler(tableLst);
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            //clickHandler(tableLst);
            //invalidate();
            return false;
        }
    }
}
