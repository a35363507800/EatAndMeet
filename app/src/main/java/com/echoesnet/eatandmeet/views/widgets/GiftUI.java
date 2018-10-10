package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.liveplay.managers.SlidGift;
import com.echoesnet.eatandmeet.utils.CanvasUtils;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.LruCacheBitmapLoader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lzy on 2017/5/19.
 */

public class GiftUI extends SurfaceView implements SurfaceHolder.Callback
{

    //头像直径  (dp)
    private static final int DIAMETER = 50;
    //礼物大小 (dp)
    private static final int GIFT_WIDTH = 50;
    private static final int GIFT_HEIGHT = 50;
    //等级角标大小 (dp)
    private static final int LEVEL_CORNER_WIDTH = DIAMETER / 3;
    private static final int LEVEL_CORNER_HEIGHT = DIAMETER / 3;
    //名称字体大小 (sp)
    private static final int NAME_SIZE = 13;
    //名称字体颜色
    private static final int NAME_TEXT_COLOR = R.color.FC0324;
    //内容字体大小
    private static final int TEXT_SIZE = 11;
    //内容字体颜色
    private static final int TEXT_COLOR = R.color.C0314;
    //礼物文字大小
    private static final int GIFT_SIZE = 25;
    //礼物文字颜色
    private static final int GIFT_TEXT_COLOR = R.color.C0314;
    //礼物文字描边颜色
    private static final int GIFT_TEXT_STROKE_COLOR = R.color.C0345P;

    private int diameter, giftWidth, giftHeight, cornerWidth, cornerHeight, backgrountWidth, nameSize, textSize;
    //黑色背景长度 (dp)
    private static final int BACKGROUNT_WIDTH = 170;

    private Context mContext;
    private LinkedList<SlidGift.GiftRecord> giftQueue = new LinkedList();

    private List<GiftPart> partList = new ArrayList<>();
    //屏幕礼物飘过上限
    private static final int GIFT_COUNT = 4;
    //间隔像素
    private static final int GIFT_INTERVAL = 40;

    private SurfaceHolder holder;
    private RenderThread renderThread;

    private Map<String, Bitmap> headBitmap;

    private boolean isDraw = false;// 控制绘制的开关
    private OnViewClick onClick;
    private Canvas canvas;
    private Canvas drawCanvas;
    private Paint paint;
    private Bitmap canvasBitmap;
    //屏幕宽度
    private int width;
    private int viewHeight;

    private volatile float increaseSpeed=1.6f;
    private volatile float giftTextSpeed=1;

    public GiftUI(Context context)
    {
        this(context, null);
    }

    public GiftUI(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public GiftUI(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        holder = this.getHolder();
        holder.addCallback(this);

        holder.setFormat(PixelFormat.TRANSPARENT);

        renderThread = new RenderThread();
        paint = new Paint();
        headBitmap = new HashMap<>();
        setFocusable(true);
        setZOrderOnTop(true);
        //   setZOrderMediaOverlay(true);
        setClickable(true);

        width = CommonUtils.getScreenWidth(mContext);
        viewHeight = CommonUtils.dp2px(mContext, 250);

        initSize();
        initGift();

        //创建画板
        canvasBitmap = Bitmap.createBitmap((int) (diameter / 2 + backgrountWidth + giftWidth - 40 + 200), diameter, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    private void initSize()
    {
        diameter = CommonUtils.dp2px(mContext, DIAMETER);
        giftWidth = CommonUtils.dp2px(mContext, GIFT_WIDTH);
        giftHeight = CommonUtils.dp2px(mContext, GIFT_HEIGHT);
        cornerWidth = CommonUtils.dp2px(mContext, LEVEL_CORNER_WIDTH);
        cornerHeight = CommonUtils.dp2px(mContext, LEVEL_CORNER_HEIGHT);
        backgrountWidth = CommonUtils.dp2px(mContext, BACKGROUNT_WIDTH);
        nameSize = NAME_SIZE;
        textSize = TEXT_SIZE;
    }

    public void setOnClick(OnViewClick onClick)
    {
        this.onClick = onClick;
    }


    public synchronized void put(final SlidGift.GiftRecord record)
    {
        //加载头像
        if (!headBitmap.containsKey(record.usrIcon))
        {
            GlideApp.with(mContext.getApplicationContext())
                    .asBitmap()
                    .load(record.usrIcon)
                    .placeholder(R.drawable.userhead)
                    .centerCrop()
                    .error(R.drawable.userhead)
                    .into(new SimpleTarget<Bitmap>(diameter, diameter)
                    {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                        {
                            LruCacheBitmapLoader.getInstance().addBitmapToMemoryCache(record.usrIcon,resource);
                        }
                    });
        }

        //加载等级控件
        if (!headBitmap.containsKey("lv" + record.level))
        {
            int level = 0;
            try
            {
                level = Integer.parseInt(record.level);
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
            headBitmap.put("lv" + record.level, getBitmapById(LevelView.getLevelRoundImage(level)));
        }

        //加载礼物图片
        if (!headBitmap.containsKey(record.giftImg))
        {
            LruCacheBitmapLoader.getInstance().putBitmapInto((Activity) mContext, record.giftImg, headBitmap, giftWidth, giftHeight);
        }


        if (!checkAima(record))
        {
            GiftPart part = openGift(record);
            if (part == null)
                giftQueue.offerLast(record);
        }
        startThread();
    }


    /**
     * 是否重制动画
     *
     * @param record
     * @return
     */
    private boolean checkAima(SlidGift.GiftRecord record)
    {
        for (GiftPart giftPart : partList)
        {
            //不等于null代表正在进行动画  ，判断新加入的礼物有相同id直接重置动画 并不加入队列
            if (giftPart.getGiftRecord() != null)
                if (giftPart.getGiftRecord().gid.equals(record.gid))
                {
                    giftPart.setTop(viewHeight - ((giftPart.getSerialNumber() + 1) * diameter) - giftPart.getSerialNumber() * GIFT_INTERVAL - 20);
                    giftPart.reBounceAnim();
                    giftPart.setGiftCountTotal(Integer.parseInt(record.giftNumber) + giftPart.getGiftCountTotal());
                    giftPart.setGiftRecord(record);
                    return true;
                }
        }
        //false代表没有要叠加的礼物   那就加入队列 等待下一轮礼物展示
        return false;
    }

    /**
     * 打开一条通道绘制
     *
     * @return
     */

    private GiftPart openGift(SlidGift.GiftRecord record)
    {
        for (final GiftPart giftPart : partList)
        {
            if (!giftPart.isState())
            {
                giftPart.setGiftRecord(record);
                giftPart.setGiftCountTotal(Integer.parseInt(record.giftNumber) + record.getGiftCountTotal());
                giftPart.setState(true);
                return giftPart;

            }
        }
        return null;
    }

    /**
     * 绘制界面的线程
     *
     * @author Administrator
     */
    private class RenderThread extends Thread
    {
        @Override
        public void run()
        {
            long time = 0;
            // 不停绘制界面,暂定每秒24帧,如果系统受限仍然有降低的空间
            while (isDraw)
            {
                time = System.currentTimeMillis();
                drawUI();
                long interval=System.currentTimeMillis()-time;
                long temp=18-interval;
                if (temp>=0)
                {
                    increaseSpeed=3.0f;//0.4为速度因子，是测试定出来的--wb
                    giftTextSpeed=3.0f;
                }else
                {
                    increaseSpeed=3.0f*(interval/18);
                    giftTextSpeed=3.0f*(interval/18);
                }
                try
                {
                    if (temp>0)
                    {
                        Thread.sleep(temp);
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }

    /**
     * 界面绘制
     */
    private void drawUI()
    {
        canvas = holder.lockCanvas();

        if (canvas == null)
            return;

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        try
        {
            drawCanvas(canvas);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawCanvas(Canvas canvas)
    {
        for (GiftPart giftPart : partList)
        {
            if (giftPart.isState())
            {
                if (giftPart.getGiftRecord() != null)
                {
                    int canvasWidth = drawGiftUI(canvas, giftPart);

                    //坐标修正
                    if (giftPart.getLeft() + canvasWidth < 0)
                        giftPart.setLeft(0 - canvasWidth);

                    switch (giftPart.getAnimationType())
                    {
                        case GiftPart.ANIM_TRANSLATION:
                            giftPart.startTranslation();
                            break;
                        case GiftPart.ANIM_BOUNCE:
                            boolean isGo = giftPart.startBounce();
                            if (giftPart.getGiftCount() < giftPart.getGiftCountTotal())
                            {
                                giftPart.setGiftCount(giftPart.getGiftCount() + 1);
                                giftPart.reBounceAnim();
                            }
                            if (isGo)
                            {
                                long time = giftPart.startTime();
                                if (time >= 2000)
                                    giftPart.setAnimationType(GiftPart.ANIM_VANISH);
                            }
                            break;
                        case GiftPart.ANIM_VANISH:
                            giftPart.startVanish();
                            break;
                        default:
                            reset(giftPart);

                            SlidGift.GiftRecord record = queuePollFirst();
                            if (record != null)
                            {
                                queueOverlay(record);
                                openGift(record);
                            }
                    }

                }

            }
        }

        inspectIsDraw();
    }

    private synchronized void queueOverlay(SlidGift.GiftRecord record)
    {
        Iterator<SlidGift.GiftRecord> it = giftQueue.iterator();
        while (it.hasNext())
        {
            SlidGift.GiftRecord re = it.next();
            if (record.gid.equals(re.gid))
            {
                record.giftNumber = String.valueOf(Integer.parseInt(record.giftNumber) + Integer.parseInt(re.giftNumber));
                it.remove();
            }
        }
    }

    //从队列删除第一个元素并返回
    private synchronized SlidGift.GiftRecord queuePollFirst()
    {
        SlidGift.GiftRecord record = giftQueue.pollFirst();
        return record;
    }

    //从队列查看第一个元素并返回
    private synchronized SlidGift.GiftRecord queuePeekFirst()
    {
        SlidGift.GiftRecord record = giftQueue.peekFirst();
        return record;
    }

    private Bitmap getBitmapById(int resId)
    {
        if (resId == 0)
        {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
        final String imageKey = String.valueOf(resId);

        Bitmap bitmap = LruCacheBitmapLoader.getInstance().getBitmapFromMemCache(imageKey);

        if (bitmap == null)
        {
            InputStream ins = mContext.getResources().openRawResource(resId);
            bitmap = BitmapFactory.decodeStream(ins, null, null);
            //     LruCacheBitmapLoader.getInstance().addBitmapToMemoryCache(String.valueOf(resId), bitmap);
            return bitmap;
        }

        return bitmap;

    }


    /**
     * @param canvas
     * @param giftPart
     * @return 图形宽度
     */
    private int drawGiftUI(Canvas canvas, GiftPart giftPart)
    {
        if (giftPart.getCanvasBP() == null)
        {
            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            saveCanvasBitmap(drawCanvas, giftPart);
        }

        Bitmap headBp=giftPart.getHeadBP();
        if(headBp==null)
        {
            headBp = LruCacheBitmapLoader.getInstance().getBitmapFromMemCache(giftPart.getGiftRecord().usrIcon);
            if (headBp != null)
                giftPart.setHeadBP(CanvasUtils.roundImage(headBp));
            else
                headBp = CanvasUtils.roundImage(getBitmapById(R.drawable.userhead));
        }

        CanvasUtils.paintReset(paint, giftPart.getAlpha());//重置
        if (giftPart.getCanvasBP() != null)
            canvas.drawBitmap(giftPart.getCanvasBP(), giftPart.getLeft(), giftPart.getTop(), paint);

        if (headBp != null)
        {
            canvas.drawBitmap(CanvasUtils.bitmapScale(headBp, diameter, diameter),
                    giftPart.getLeft(),
                    giftPart.getTop(), paint);
        }

        CanvasUtils.paintReset(paint, giftPart.getAlpha());
        //画头像边框
        paint.setStrokeWidth(1);
        //  paint.setColor(ContextCompat.getColor(mContext, LevelHeaderView.getStrokeColor(Integer.parseInt(giftPart.getGiftRecord().level))));
        paint.setColor(ContextCompat.getColor(mContext, R.color.white));
        paint.setAlpha(255 - giftPart.getAlpha());
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle( giftPart.getLeft()+(diameter) / 2,  giftPart.getTop()+(diameter) / 2, (diameter) / 2, paint);

        CanvasUtils.paintReset(paint, giftPart.getAlpha());
        Bitmap corne2rBp = LruCacheBitmapLoader.getInstance().getResBitmap(mContext, R.drawable.v_28x28, diameter / 3, diameter / 3);
        if ("1".equals(giftPart.getGiftRecord().vUser))
        {
            //画头像角标
            if (corne2rBp != null)
            {
                //原来的角标
                canvas.drawBitmap(corne2rBp, giftPart.getLeft()+diameter - diameter / 3, giftPart.getTop()+diameter - cornerHeight, paint);
            }
        }

        CanvasUtils.paintReset(paint, giftPart.getAlpha());
        float[] text = CanvasUtils.drawText(mContext, canvas, paint, "送了" + giftPart.getGiftRecord().giftNumber
                        + "个" + giftPart.getGiftRecord().giftName + "~",
                (int) giftPart.getLeft() + diameter + 10,
                (int) (giftPart.getTop() + CommonUtils.dp2px(mContext, 15) + 40),
                false,
                false,
                TEXT_COLOR, textSize, 0);
        CanvasUtils.paintReset(paint, giftPart.getAlpha());
        float[] giftText = CanvasUtils.drawText(mContext, canvas, paint, "X " + giftPart.getGiftCount(),
                (int) giftPart.getGiftLeft() + diameter / 2 + backgrountWidth + giftWidth - 50,
                (int) (giftPart.getTop() + diameter / 2 - CommonUtils.dp2px(mContext, 10)),
                true,
                false,
                GIFT_TEXT_COLOR, giftPart.getTextZoom(), GIFT_TEXT_STROKE_COLOR);


        return (int) (diameter / 2 + backgrountWidth + giftWidth - 40 + giftText[0]);
    }

    private void saveCanvasBitmap(Canvas canvas, GiftPart giftPart)
    {
        Bitmap cornerBp = headBitmap.get("lv" + giftPart.getGiftRecord().level);
        if (cornerBp != null)
            giftPart.setCornerBP(cornerBp);
        else
            cornerBp = getBitmapById(0);

        Bitmap giftBp = headBitmap.get(giftPart.getGiftRecord().giftImg);
        if (giftBp != null)
            giftBp=CanvasUtils.bitmapScale(giftBp, giftWidth, giftHeight);

        CanvasUtils.paintReset(paint, giftPart.getAlpha());//重置

        if (giftBp != null)
        {
            canvas.drawBitmap(giftBp,
                    diameter/2 + backgrountWidth - 70,
                    diameter / 2 - giftHeight / 2, paint);
        }


        CanvasUtils.paintReset(paint, giftPart.getAlpha());
        paint.setStyle(Paint.Style.FILL);//充满
        paint.setColor(Color.BLACK);
        paint.setAlpha(80 - giftPart.getAlpha() < 0 ? 0 : 80 - giftPart.getAlpha());
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        RectF oval3 = new RectF(diameter / 2, 12,
                diameter / 2 + backgrountWidth, diameter - 12);
        //画文字背景
        canvas.drawRoundRect(oval3, 60, 60, paint);//第二个参数是x半径，第三个参数是y半径

        CanvasUtils.paintReset(paint, giftPart.getAlpha());
        float[] textName = CanvasUtils.drawText(mContext, canvas, paint, giftPart.getGiftRecord().name,
                diameter + 10,
                CommonUtils.dp2px(mContext, 10),
                false,
                false,
                NAME_TEXT_COLOR, nameSize, 0);

        CanvasUtils.paintReset(paint, giftPart.getAlpha());
        //画等级图标
        if (cornerBp != null)
        {
            canvas.drawBitmap(cornerBp, diameter + 25 + textName[0],
                    CommonUtils.dp2px(mContext, 10),
                    paint);
        }


        giftPart.setCanvasBP(Bitmap.createBitmap(canvasBitmap));
    }

    private synchronized void reset(GiftPart part)
    {

        part.setLeft(-width);
        part.setTop(viewHeight - ((part.getSerialNumber() + 1) * diameter) - part.getSerialNumber() * GIFT_INTERVAL - 20);
        part.setState(false);
        part.setGiftRecord(null);
        part.setGiftCount(1);
        part.setGiftCountTotal(1);
        part.reTime();
        part.resetAnimation();
        part.reBitmap();

    }

    /**
     * 检测是否所有位置弹幕都为false 是则关闭绘画
     */
    private void inspectIsDraw()
    {
        int i = 0;
        for (GiftPart giftPart : partList)
        {
            if (!giftPart.isState())
            {
                i++;
            }
        }
        if (i == partList.size())
        {
            isDraw = false;
        }
    }


    private void initGift()
    {
        for (int i = 0; i < GIFT_COUNT; i++)
        {
            GiftPart part = new GiftPart(i);
            part.setLeft(-width);
            part.setTop(viewHeight - ((i + 1) * diameter) - i * GIFT_INTERVAL - 20);
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
        if (!isDraw)
        {

            isDraw = true;
            new Thread(renderThread).start();

        }
    }


    private class GiftPart
    {
        public static final int ANIM_TRANSLATION = 1;
        public static final int ANIM_BOUNCE = 2;
        public static final int ANIM_VANISH = 3;


        //缓存图片
        private Bitmap canvasBP;
        //用户头像
        private Bitmap headBP;
        //礼物图片
        private Bitmap giftBP;
        //角标图片
        private Bitmap cornerBP;
        //礼物编号
        private int serialNumber;
        //礼物data
        private SlidGift.GiftRecord giftRecord;
        //礼物文字缩放
        private int giftTextZoom = GIFT_SIZE;
        private boolean zoomFlag = true;
        //礼物移动数据
        private float giftLeft;
        private float left, top;
        //工作状态
        private boolean state = false;
        private int animationType = 1;
        //每次移动的像素
        private float speed = 13;
        private float giftSpeed = 1;
        //礼物数量
        private int giftCount = 1;
        private int giftCountTotal = giftCount;

        public Bitmap getHeadBP()
        {
            return headBP;
        }

        public void setHeadBP(Bitmap headBP)
        {
            this.headBP = headBP;
        }

        public Bitmap getCornerBP()
        {
            return cornerBP;
        }

        public void setCornerBP(Bitmap cornerBP)
        {
            this.cornerBP = cornerBP;
        }

        public Bitmap getGiftBP()
        {
            return giftBP;
        }

        public void setGiftBP(Bitmap giftBP)
        {
            this.giftBP = giftBP;
        }

        public int getGiftCountTotal()
        {
            return giftCountTotal;
        }

        public void setGiftCountTotal(int giftCountTotal)
        {
            this.giftCountTotal = giftCountTotal;
        }

        public Bitmap getCanvasBP()
        {
            return canvasBP;
        }

        public void setCanvasBP(Bitmap canvasBP)
        {
            this.canvasBP = canvasBP;
        }

        //透明度
        private int alpha = 0;
        //
        private long delayedTime;

        public GiftPart(int serialNumber)
        {
            this.serialNumber = serialNumber;
        }

        public SlidGift.GiftRecord getGiftRecord()
        {
            return giftRecord;
        }

        public void setGiftRecord(SlidGift.GiftRecord giftRecord)
        {
            this.giftRecord = giftRecord;
        }


        public int getAnimationType()
        {
            return animationType;
        }

        public void setAnimationType(int ani)
        {
            this.animationType = ani;
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


        public boolean isState()
        {
            return state;
        }

        public void setState(boolean state)
        {
            this.state = state;
        }

        public int getGiftCount()
        {
            return giftCount;
        }

        public void setGiftCount(int giftCount)
        {
            this.giftCount = giftCount;
        }

        public int getAlpha()
        {
            return alpha;
        }

        public void setAlpha(int alpha)
        {
            if (alpha >= 0 && alpha <= 255)
                this.alpha = alpha;
        }

        public int getTextZoom()
        {
            return giftTextZoom;
        }

        public float getLeft()
        {
            return left;
        }

        public float getGiftLeft()
        {
            return giftLeft;
        }

        public void setLeft(float left)
        {
            this.left = left;
            this.giftLeft = left;
        }


        //渐隐向上
        public boolean startVanish()
        {
            if (animationType != ANIM_VANISH)
                return true;

            if (alpha >= 255)
            {
                this.alpha = 255;
                animationType = 0;
                return true;
            }

            top--;
            this.alpha += 5;
            return false;
        }

        //字体跳动
        public boolean startBounce()
        {
            if (animationType != ANIM_BOUNCE)
                return true;

            if (zoomFlag)
            {
                giftTextZoom += giftTextSpeed;
                giftTextZoom++;
            } else
            {
                giftTextZoom -= giftTextSpeed;
                giftTextZoom--;
            }

            if (giftTextZoom <= GIFT_SIZE)
                giftTextZoom = GIFT_SIZE;

            if (giftTextZoom > GIFT_SIZE + 25)
                zoomFlag = false;

            if (!zoomFlag && giftTextZoom == GIFT_SIZE)
            {
                return true;
            }
            return false;
        }

        //出现
        public boolean startTranslation()
        {
            if (animationType != ANIM_TRANSLATION)
                return true;

            this.left = this.left + speed;
            this.giftLeft = this.giftLeft + giftSpeed;
//            speed += 1.5;
            speed += increaseSpeed;
//            giftSpeed += 1;
            giftSpeed += (0.7f*increaseSpeed);
            if (this.left >= 0)
            {
                this.left = 0;
            }
            if (this.giftLeft >= 0)
            {
                this.giftLeft = 0;
            }

            if (this.left == 0 && this.giftLeft == 0)
            {
                animationType = ANIM_BOUNCE;
                return true;
            }

            return false;
        }

        public long startTime()
        {
            if (delayedTime == 0)
                delayedTime = System.currentTimeMillis();

            long time = System.currentTimeMillis();
            return time - delayedTime;

        }

        public void reTime()
        {
            this.delayedTime = 0;
        }

        public void resetAnimation()
        {
            animationType = ANIM_TRANSLATION;
            setAlpha(0);
            resetZoom();
            speed = 1;
            giftSpeed = 1;
        }

        public void reBounceAnim()
        {
            animationType = ANIM_TRANSLATION;
            setAlpha(0);
            reTime();
            resetZoom();
        }

        public void reBitmap()
        {
            headBP = null;
            giftBP = null;
            cornerBP = null;
            canvasBP = null;
        }

        public void resetZoom()
        {
            this.zoomFlag = true;
            this.giftTextZoom = GIFT_SIZE;
        }
    }

    private boolean isMove = false;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float clickX = event.getX();
        float clickY = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
                if (isMove)
                {
                    isMove = false;
                    break;
                }

                SlidGift.GiftRecord re = getGiftPartByXY(clickX, clickY);
                if (re != null)
                {
                    if (onClick != null)
                        onClick.onClick(re);
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                break;
            case MotionEvent.ACTION_DOWN:
                if (clickX < diameter * 2)
                {
                    re = getGiftPartByXY(clickX, clickY);
                    if (re != null)
                        return true;
                }
                break;
        }
//        return super.onTouchEvent(event);
        return false;
    }

    private SlidGift.GiftRecord getGiftPartByXY(float x, float y)
    {
        for (GiftPart giftPart : partList)
        {
            if (giftPart.getGiftRecord() != null)
                if (x >= giftPart.getLeft() && x <= giftPart.getLeft() + CommonUtils.dp2px(mContext, DIAMETER) && y >= giftPart.getTop() && y <= giftPart.getTop() + CommonUtils.dp2px(mContext, DIAMETER))
                {
                    return giftPart.getGiftRecord();
                }
        }
        return null;
    }

    public int getAnimIngGiftCount(String gid)
    {
        for (GiftPart giftPart : partList)
        {
            if (giftPart.getGiftRecord() != null)
                if (giftPart.getGiftRecord().gid.equals(gid))
                {
                    return giftPart.getGiftCountTotal();
                }
        }
        return 0;
    }

    public interface OnViewClick
    {
        void onClick(SlidGift.GiftRecord record);
    }
}
