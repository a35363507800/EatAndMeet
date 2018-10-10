package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/5/19.
 */

public class BarrageUI extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private LinkedList<Bitmap> queueBarrage = new LinkedList();
    private List<BarragePart> partList = new ArrayList<>();
    //屏幕弹幕飘过上限
    private final int BARRAGE_SIZE = 3;
    //弹幕之间间隔像素
    private final int BARRAGE_INTERVAL = 200;

    private SurfaceHolder holder;
    private RenderThread renderThread;

    private boolean isDraw = false;// 控制绘制的开关

    private Canvas canvas;

    //记录每次弹幕飘过时间
    private long lastTimePoint2 = 0;
    //屏幕宽度
    private int width;

    public BarrageUI(Context context)
    {
        this(context, null);
    }

    public BarrageUI(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BarrageUI(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        holder = this.getHolder();
        holder.addCallback(this);

        holder.setFormat(PixelFormat.TRANSPARENT);

        renderThread = new RenderThread();

        setFocusable(true);
        setZOrderOnTop(true);
      //  setZOrderMediaOverlay(true);

        width = CommonUtils.getScreenWidth(context);

        initBarrage();
    }

    public synchronized void addBitmap(Bitmap bitmap)
    {

        queueBarrage.addLast(bitmap);

        lastTimePoint2 = System.currentTimeMillis();

        openBarrage();

        startThread();
    }


    /**
     * 打开并返回一个弹幕
     * 打开的意思是在不断绘制时 会绘制已经打开开关的弹幕;
     *
     * @return
     */

    private BarragePart openBarrage()
    {
        for (BarragePart barragePart : partList) {

            if (!barragePart.isState()) {

                barragePart.setState(true);
                return barragePart;

            }
        }
        return null;
    }

    /**
     * 绘制界面的线程
     *
     * @author Administrator
     */
    private class RenderThread extends Thread {
        @Override
        public void run()
        {
            long time=0;
            // 不停绘制界面
            while (isDraw) {
             time=System.currentTimeMillis();
                drawUI();
                try {
                    Thread.sleep(Math.max(0, 18-(System.currentTimeMillis()-time)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }

    /**
     * 界面绘制
     */
    public void drawUI()
    {
        canvas = holder.lockCanvas();
        if (canvas == null)
            return;
        canvas.drawColor(Color.WHITE);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);

        try {
            drawCanvas(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null)
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawCanvas(Canvas canvas)
    {

        for (BarragePart barragePart : partList) {

            if (barragePart.isState()) {

                if (barragePart.getBitmap() != null) {

                    //    canvas.drawBitmap(barragePart.getBitmap(), barragePart.getmSrcRect(), barragePart.getmDestRect(), null);
                    canvas.drawBitmap(barragePart.getBitmap(), barragePart.getLeft(), barragePart.getTop(), null);
                }
                //判断物体移动到屏幕外
                if (barragePart.next() + barragePart.getBitmapWidth() < 0) {

                    barragePart.setState(false);
                    Bitmap bitmap = queueGet();

                    if (bitmap != null)
                        reset(bitmap);

                }
            }
        }

        inspectIsDraw();
    }

    //从队列取弹幕 取走后会删除
    private synchronized Bitmap queueGet()
    {
            Bitmap bitmap = queueBarrage.pollFirst();
            return bitmap;
    }


    private synchronized void reset(Bitmap bitmap)
    {

        BarragePart part = openBarrage();

        int top = ((part.getSerialNumber()) * BARRAGE_INTERVAL);
        int left = this.width;

        part.setBitmap(bitmap);
        //弹幕每次像素
        part.setBarrageSpeed(randomTime());
        part.setLeft(left);
        part.setTop(top);

    }

    /**
     * 检测是否所有位置弹幕都为false 是则关闭绘画
     */
    private void inspectIsDraw()
    {
        int i = 0;
        for (BarragePart barragePart : partList) {
            if (!barragePart.isState()) {
                i++;
            }
        }
        if (i == partList.size()) {
            isDraw = false;
            long tSection = System.currentTimeMillis() - lastTimePoint2;
            Log.d("=======", "drawUI: 时间" + tSection);
        }
    }


    private void initBarrage()
    {
        for (int i = 0; i < BARRAGE_SIZE; i++) {
            BarragePart part = new BarragePart(i);
            partList.add(part);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        startThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        isDraw = false;
    }

    //启动绘图线程并更改绘制标志
    private void startThread()
    {
        if (!isDraw) {

            isDraw = true;
            new Thread(renderThread).start();

        }
    }

    private float randomTime()
    {

        float number = (float) (Math.round((new Random().nextFloat() * 0.6 + 0.7f + 1f) * 10)) / 10;
        return number;

    }

    private class BarragePart {
        //弹幕编号
        private int serialNumber;

        //弹幕显示
        private Bitmap bitmap;

        //弹幕移动数据
        private float left, top;

        //弹幕工作状态
        private boolean state = false;

        //每次移动的像素
        private float barrageSpeed = 0;

        public float getLeft()
        {
            return left;
        }

        public void setLeft(float left)
        {
            this.left = left;
        }

        public float getTop()
        {
            return top;
        }

        public void setTop(float top)
        {
            this.top = top;
        }

        public int getSerialNumber()
        {
            return serialNumber;
        }

        public BarragePart(int serialNumber)
        {
            this.serialNumber = serialNumber;
        }

        public Bitmap getBitmap()
        {
            return bitmap;
        }

        public int getBitmapWidth()
        {
            if (bitmap == null)
                return 0;

            return bitmap.getWidth();
        }

        public int getBitmapHeight()
        {
            if (bitmap == null)
                return 0;

            return bitmap.getHeight();
        }

        public void setBitmap(Bitmap bitmap)
        {
            this.bitmap = bitmap;
        }

        public boolean isState()
        {
            return state;
        }

        public void setState(boolean state)
        {
            this.state = state;
        }

        public float getBarrageSpeed()
        {
            return barrageSpeed;
        }

        public void setBarrageSpeed(float barrageSpeed)
        {
            this.barrageSpeed = barrageSpeed;
        }

        public float next()
        {
            if (bitmap == null)
                return -1;

            float newLeft = this.left - barrageSpeed;
            this.left = newLeft;
            return newLeft;
        }
    }
}
