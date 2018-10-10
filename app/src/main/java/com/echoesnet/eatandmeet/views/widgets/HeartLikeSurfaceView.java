package com.echoesnet.eatandmeet.views.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.LruCacheBitmapLoader;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * 直播点赞动画
 * 使用SurfaceView实现
 */
public class HeartLikeSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int[] DRAWABLE_IDS = new int[]{
            R.drawable.gift, R.drawable.heart_1, R.drawable.heart_2, R.drawable.heart_3, R.drawable.heart_4,
            R.drawable.heart_5, R.drawable.heart_6, R.drawable.heart_8,
            R.drawable.heart_9, R.drawable.heart_10, R.drawable.heart_11, R.drawable.heart_12,
            R.drawable.hot_dog, R.drawable.ice_cream, R.drawable.swimming_circle, R.drawable.cat_claws, R.drawable.chicken,};

    //线性//加速//减速//先加速后减速
    private static final Interpolator[] interpolators = {
            new LinearInterpolator(), new AccelerateInterpolator(),
            new DecelerateInterpolator(), new AccelerateDecelerateInterpolator()
    };

    private SurfaceHolder mHolder;

    private Random mRandom = new Random();
    private boolean isRunning = false;

    private Paint mPaint;
    private Canvas mCanvas;// 当前画布


    private Canvas pagerCanvas;
    private Bitmap pagerBitmap;// 每次使用这个bitmap刷新

    private static final LinkedList<Integer> prepList = new LinkedList<>();

    private static final ArrayList<DrawObject> drawList = new ArrayList<>();

    private boolean waiting = false;

    public void waiting()
    {
        waiting = true;
        prepList.clear();
        drawList.clear();
    }

    public void working()
    {
        waiting = false;
    }

    public class DrawObject
    {
        private int imgKey;
        private Point point;
        private float speed;// 速度

        private Path path;
        private float runLen;
        private int direction;

        public int getImgKey()
        {
            return imgKey;
        }

        public void setImgKey(int imgKey)
        {
            this.imgKey = imgKey;
        }

        public Point getPoint()
        {
            return point;
        }

        public void setPoint(Point point)
        {
            this.point = point;
        }

        public float getSpeed()
        {
            return speed;
        }

        public void setSpeed(float speed)
        {
            this.speed = speed;
        }

        public Path getPath()
        {
            return path;
        }

        public void setPath(Path path)
        {
            this.path = path;
        }

        public float getRunLen()
        {
            return runLen;
        }

        public void setRunLen(float runLen)
        {
            this.runLen = runLen;
        }

        public int getDirection()
        {
            return direction;
        }

        public void setDirection(int direction)
        {
            this.direction = direction;
        }
    }


    private PathMeasure pathMeasure;


    private int onceIntoCount = 10;     //最多放入多少 draw object into canvas
    private int viewW, viewH;

    private long lastTimePoint = 0;       //计算 上次与这次的时间差
    private float acceleratedSpeed = 0.0001f;// 加速度，目前恒定


    public HeartLikeSurfaceView(Context context)
    {
        super(context);
        init(context);
    }

    public HeartLikeSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public HeartLikeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeartLikeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context)
    {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setAntiAlias(true);

        setFocusable(true);
        setZOrderOnTop(true); // 大概的意思就是说控制窗口中表面的视图层是否放置在常规视图层的顶部。
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        viewH = getHeight();
        viewW = getWidth();
        pagerBitmap = Bitmap.createBitmap(viewW, viewH, Bitmap.Config.ARGB_8888);
        pagerCanvas = new Canvas(pagerBitmap);

//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);//实心矩形框
//        paint.setColor(Color.RED);  //颜色为红色
//        pagerCanvas.drawRect(new RectF(0, 0, viewW, viewH), paint);

        pathMeasure = new PathMeasure();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        isRunning = false;
        prepList.clear();
    }

    private DrawObject createDrawObj(int i, int resid)
    {
        DrawObject dobj = new DrawObject();
        dobj.setImgKey(resid);
        dobj.setPoint(new Point(0, 0));
        dobj.setSpeed(0.00f);
        dobj.setRunLen(0.00f);
        dobj.setDirection(1);
        Path path = new Path();


        //>>>>>>>>>>>>>>>>>  贝塞尔曲线绘制，  >>>>>>>>>>>>>>>>>>>>>>>
        int threshold = viewW / 4; //抖动阈值
        Point pStart = new Point((viewW * 2) / 3, viewH );
        int moveDes = i % 2 == 0 ? mRandom.nextInt(threshold) : -mRandom.nextInt(threshold);
        Point pEnd = new Point(viewW / 2 + moveDes, 0 + 60); // 加60，防止canvas 边缘绘图；


        path.moveTo(pStart.x, pStart.y);

        boolean mathMark = mRandom.nextInt(2) == 1; //正负
        Point pControl1 = new Point();
        int c1x = viewW / 2 + (mathMark ? mRandom.nextInt(viewW / 2) : -mRandom.nextInt(viewW / 2));
        int c1y = viewH / 2 + (mRandom.nextInt(viewH / 2)) + 60;
        pControl1.x = c1x;
        pControl1.y = c1y;


        Point pControl2 = new Point();
        int c2x = viewW / 2 + (mathMark ? -mRandom.nextInt(viewW / 2) : mRandom.nextInt(viewW / 2)); //与1要相反，才是s线
        int c2y = viewH / 2 - (mRandom.nextInt(viewH / 2)) + 60 + 100; //+100使末端曲线更为垂直
        pControl2.x = c2x;
        pControl2.y = c2y;

        path.cubicTo(pControl1.x, pControl1.y, pControl2.x, pControl2.y, pEnd.x, pEnd.y);
//        Logger.t(TAG).d("HeartLikeSurface","mark: "+mark," c1x: "+c1x);


//        path.lineTo(pEnd.x,pEnd.y);
//        path.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(pEnd.x, pEnd.y);
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        dobj.setPath(path);


        return dobj;
    }


    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {

            while (isRunning)
            {
                    try
                    {
                        Logger.t("piaoxin").d(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + isRunning);
                        // put new draw object into list;
                        //至少每次刷新都要放1个
                        Integer imgKey = prepList.poll();
                        if (null != imgKey)
                        {
                            drawList.add(createDrawObj(mRandom.nextInt(2), imgKey));
                        }
                        int putCount = onceIntoCount - drawList.size();
                        for (int i = 0; i < putCount; i++)
                        {
                            imgKey = prepList.poll();
                            if (null != imgKey)
                            {
                                drawList.add(createDrawObj(i, imgKey));
                            }
                            else
                            {
                                break;
                            }
                        }

                        //上次与这次的时间差
                        long tSection = System.currentTimeMillis() - lastTimePoint;
//                        float t = tSection>300 ? 2 : tSection/100;    //超过300ms，视为停止过
//                        float t = tSection>200 ? 200 : tSection;    //超过200ms，视为停止过。狂点的情况下，5儿子->100+ms
                        float t = tSection > 1000 ? 0 : tSection;    //超过200ms，视为停止过。狂点的情况下，5儿子->100+ms
//                        Logger.t(TAG).d("HeartLikeSurface","相差时间 "+t," 上次 "+lastTimePoint);
                        ArrayList<Integer> willRemoveObj = new ArrayList<>();

                        //立刻保存时间戳，下面比较费时。
                        lastTimePoint = System.currentTimeMillis(); //保存完成这次动画的时间点
                        //立刻保存时间戳，下面比较费时。

                        //draw surface canvas

                        mCanvas = mHolder.lockCanvas();
                        if (mCanvas != null)
                        {
                            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            pagerCanvas.drawPaint(mPaint);
                            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                            pagerCanvas.save(Canvas.ALL_SAVE_FLAG);// 保存


                            // draw each obj on canvas
                            for (int i = 0; i < drawList.size(); i++)
                            {
                                DrawObject dobj = drawList.get(i);
                                pathMeasure.setPath(dobj.getPath(), false);

                                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                                //draw path for watch
//                                Paint pp = new Paint();
//                                pp.setPathEffect(new DiscretePathEffect(3.0f,5.0f));
//                                pp.setStyle(Paint.Style.STROKE);
//                                pp.setStrokeWidth(1.0f);
//                                pp.setColor(Color.BLACK);
//                                pagerCanvas.drawPath(dobj.getPath(),pp);
                                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

//                                    x=V0t+1/2*at^2
                                //速度的单位是 ms。


                                float speed;
                                float runLen;
                                float sumRunLen;
                                float desLen;
                                if (dobj.getSpeed() > 0.4)
                                {
                                    speed = dobj.getSpeed();
                                    runLen = dobj.getSpeed() * t;
                                    sumRunLen = dobj.getRunLen() + runLen;
                                    desLen = pathMeasure.getLength();
                                }
                                else
                                {
                                    speed = dobj.getSpeed() + (acceleratedSpeed * t);
                                    runLen = dobj.getSpeed() * t + (acceleratedSpeed * t * t) / 2;
                                    sumRunLen = dobj.getRunLen() + runLen;
                                    desLen = pathMeasure.getLength();
                                }


                                if (sumRunLen < desLen)
                                {
                                    float aCoordinates[] = {0f, 0f};//coordinates will be here
                                    float[] tan = {0f, 0f};
                                    pathMeasure.getPosTan(sumRunLen, aCoordinates, tan);//get point from the middle

//                                    Logger.t(TAG).d("HeartLikeSurface",
//                                            "x: "+aCoordinates[0]+" | y: "+ aCoordinates[1],
//                                            " path长度 "+desLen +
//                                                    " 所处位置 "+sumRunLen+
//                                                    " Data index "+i +
//                                                    " 速度："+speed);

//                                    float threshold_MaxLen = desLen * 2/3;
                                    float threshold_MaxLen = desLen * 1 / 3;

                                    //路径过半，为不透明
                                    float threshold_Alpha = 0.4f; //抖动阈值
                                    float alpha = sumRunLen > threshold_MaxLen ? 1.0f : (sumRunLen / threshold_MaxLen * (1 - threshold_Alpha)) + threshold_Alpha;
                                    mPaint.setAlpha((int) (alpha * 255));




                                    Matrix matrix = new Matrix();
                                    float threshold_ratio = 0.1f; //缩放阈值
                                    float threshold_MaxRatio = 0.8f; //缩放阈值
                                    float ratio = sumRunLen>threshold_MaxLen ? threshold_MaxRatio : (sumRunLen/threshold_MaxLen * (threshold_MaxRatio-threshold_ratio)) + threshold_ratio;


//                                    float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
//                                    matrix.postRotate(degrees+90);

                                    matrix.postScale(ratio, ratio);
//                                    matrix.setTranslate(aCoordinates[0],aCoordinates[1]);
                                    matrix.postTranslate(aCoordinates[0],aCoordinates[1]);

//                                    Log.d("ratio","x->"+aCoordinates[0]+" |y->"+aCoordinates[1]);
//                                    Log.d("ratio","->"+ratio);

//                                    String size = "";
//                                    if (sumRunLen >= threshold_MaxLen)
//                                    {
//                                        size = "b";
//                                    }else if (sumRunLen >= (threshold_MaxLen*2)/3){
//                                        size = "m";
//                                    }else {
//                                        size = "s";
//                                    }

                                    Bitmap bm = LruCacheBitmapLoader.getInstance().getResBitmap(getContext(), dobj.getImgKey(), 70, 70);

                                    pagerCanvas.drawBitmap(bm,matrix,mPaint);
//                                    pagerCanvas.drawBitmap(bm, aCoordinates[0], aCoordinates[1], mPaint);
                                    //修改draw obj value
                                    dobj.setSpeed(speed);
                                    dobj.setRunLen(sumRunLen);
//                                    dobj.setDirection(angle);
                                }
                                else
                                {
                                    willRemoveObj.add(i);
//                                    Logger.t(TAG).d("HeartLikeSurface","将被移除 index: "+i," path长度 "+desLen + " 所处位置 "+sumRunLen+" Data index "+i +" 速度："+speed);
                                }
                            }
                            pagerCanvas.restore();// 存储
                            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            mCanvas.drawPaint(mPaint);
                            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                            mCanvas.drawBitmap(pagerBitmap, 0, 0, null);
                        }


                        //开始清理：
                        for (Integer index : willRemoveObj)
                        {
                            drawList.remove((int) index);
                        }

                        if (drawList.isEmpty())
                        { //检测没有元素在canvas中，停止刷新
                            isRunning = false;
                        }

                        // 22f/s => 1/22 => 45ms 标准视频帧率
                        // 22f/s => 1/22 => 45ms 标准视频帧率
                        int youHaveTime = 15;// 看起来会抖
//                        int youHaveTime = 20;
                        int workTime = (int) (System.currentTimeMillis() - lastTimePoint);
                        int youCanDelayTime = youHaveTime - workTime;

//                        Logger.t(TAG).d("HeartLikeSurface","可以延迟的时间 "+youCanDelayTime," 工作时间 "+lastTimePoint);
                        Thread.sleep(youCanDelayTime > 0 ? youCanDelayTime : 0);

                    } catch (Exception e)
                    {
                        Logger.t("HeartLikeSurfaceViewError").d(e.getMessage());
                    }finally
                    {
                        try
                        {
                            if (mCanvas != null && mHolder != null)
                                mHolder.unlockCanvasAndPost(mCanvas);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            Logger.t("HeartLikeSurfaceViewError").d(e.getMessage());
                        }
                    }

            }
        }
    };

    public void pause()
    {
        isRunning = false;
        drawList.clear();
        prepList.clear();
    }


//    public Rect getDstRect() {
//        curPos += speed;
//        if (time < scaleTime) {
//            speed = 3;
//        } else {
//            if (speed <= speedMax) {
//                speed += acceleratedSpeed;
//            }
//        }
//
//        if (curPos > length) {
//            curPos = length;
//            return null;
//        }
//
//        pathMeasure.getPosTan(curPos, p, null);
//
//        if (time < scaleTime) {
//            // 放大动画
//            float s = (float) time / scaleTime;
//            dst.left = (int) (p[0] - bitmapWidthDst / 4 * s);
//            dst.right = (int) (p[0] + bitmapWidthDst / 4 * s);
//            dst.top = (int) ((p[1] - bitmapHeightDst / 2 * s));
//            dst.bottom = (int) (p[1]);
//        } else {
//            dst.left = (int) (p[0] - bitmapWidthDst / 4);
//            dst.right = (int) (p[0] + bitmapWidthDst / 4);
//            dst.top = (int) (p[1] - bitmapHeightDst / 2);
//            dst.bottom = (int) (p[1]);
//        }
//        time++;
//        alpha();
//        return dst;
//    }
//


    public void put()
    {
        if (waiting)
        {
            return;
        }

        prepList.offer(DRAWABLE_IDS[mRandom.nextInt(DRAWABLE_IDS.length)]);
        if (!isRunning)
        {
            Logger.t("piaoxin").d(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> put" );
            isRunning = true;
            new Thread(runnable).start();
        }
    }


    public void recycle()
    {
        isRunning = false;
        if (pagerBitmap != null && !pagerBitmap.isRecycled())
        {
//            pagerBitmap.recycle();
        }

//        drawList.clear();
//        prepList.clear();
    }

}
