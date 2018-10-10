package com.echoesnet.eatandmeet.activities.liveplay.managers;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.transition.Transition;
import com.echoesnet.eatandmeet.utils.GlideApp;

import com.bumptech.glide.request.target.SimpleTarget;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CanvasUtils;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.LruCacheBitmapLoader;
import com.echoesnet.eatandmeet.views.widgets.BarrageUI;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 * Created by lzy on 2017/3/29.
 */

public class BarrageManager
{
    /**
     * map key
     * pUrl=头像地址   level=等级  pName=名称   pMessage=弹幕内容 orV是不是大V
     *
     * @return
     */
    public boolean addBarrage(String pUrl, final String level, final String pName, final String pMessage , final String orV)
    {
        //把头像准备好，
        GlideApp.with(mContext.getApplicationContext())
                .asBitmap()
                .load(pUrl)
                .placeholder(R.drawable.userhead)
                .centerCrop()
                .error(R.drawable.userhead)
                .into(new SimpleTarget<Bitmap>(100, 100)
                {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                    {
//                        //现在又要准备一个等级控件
//                        final LevelView lv = (LevelView) LayoutInflater.from(mContext).inflate(R.layout.include_level, null).findViewById(R.id.level_view);
//                        lv.setLevel(level, LevelView.USER);
//                        long time = System.currentTimeMillis();
                        Bitmap barrageBp = createBitmap( resource, pName, pMessage, Integer.parseInt(level),"1".equals(orV));

//                      barrageVessel.addBitmap(barrageBp);
                        if (barrageBp != null&&!shoot(barrageBp))
                            queueBarrage.offer(barrageBp);

                    }
                });

        return true;
    }


    //头像直径  (dp)
    private static final int DIAMETER = 30;

    //等级标识大小 (dp)
    private static final int LEVEL_WIDTH = 23;
    private final int LEVEL_HEIGHT = 10;

    //等级角标大小 (dp)
    private static final int LEVEL_CORNER_WIDTH = DIAMETER / 3;
    private static final int LEVEL_CORNER_HEIGHT = DIAMETER / 3;

    //名称字体大小 (sp)
    private static final int NAME_SIZE = 11;
    //名称字体颜色
    private static final int NAME_TEXT_COLOR = R.color.C0411;
    //名称字体描边颜色
    private static final int NAME_TEXT_STROKE_COLOR = R.color.FC0324;

    //内容字体大小
    private static final int TEXT_SIZE = 10;
    //内容字体颜色
    private static final int TEXT_COLOR = R.color.FC0324;


    public Bitmap createBitmap( Bitmap headBitmap, String name, String msg, int level,boolean orV)
    {
        int diameter = CommonUtils.dp2px(mContext, DIAMETER);
        int levelWidth = CommonUtils.dp2px(mContext, LEVEL_WIDTH);
        int levelHeight = CommonUtils.dp2px(mContext, LEVEL_HEIGHT);
        int cornerWidth = CommonUtils.dp2px(mContext, LEVEL_CORNER_WIDTH);
        int cornerHeight = CommonUtils.dp2px(mContext, LEVEL_CORNER_HEIGHT);

        //画板宽
        int viewWidth = CommonUtils.getScreenWidth(mContext);
        //画板高
        int viewHeight = diameter + diameter / 3;
        //创建画板
        Bitmap barrageBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        viewHeight=diameter;

        Canvas canvas = new Canvas(barrageBitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
        //创建画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);//锯齿效果
        //等级头像框
        //Bitmap borderBp = CanvasUtils.bitmapScale(getBitmapById(getLevImageShapeId(level)), diameter, diameter);

        //等级控件 取图片ID
         Bitmap levelBp = CanvasUtils.bitmapScale(getBitmapById(LevelView.getLevelRoundImage(level)), levelWidth, levelHeight);
        //Bitmap levelBp = levelBitmap;


        //画头像
        canvas.drawBitmap(CanvasUtils.bitmapScale(CanvasUtils.roundImage(headBitmap), diameter, diameter), 2, viewHeight - diameter, paint);

        //画头像边框
        paint.setStrokeWidth(2);
        paint.setColor(ContextCompat.getColor(mContext, R.color.white));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(diameter / 2+1, viewHeight - diameter / 2, diameter / 2 , paint);
        paint.reset();
        paint.setAntiAlias(true);//锯齿效果

        // canvas.drawBitmap(borderBp, 0, viewHeight - borderBp.getHeight(), paint);

      //  画头像角标
        if(orV)
        {
            Bitmap cornerBp = CanvasUtils.bitmapScale(getBitmapById(R.drawable.v_28x28), cornerWidth, cornerHeight);
            canvas.drawBitmap(cornerBp, diameter - diameter / 3, viewHeight - cornerHeight, paint);
        }

        CanvasUtils.paintReset(paint, 0);
        //画名字
        float nameSize[] = CanvasUtils.drawText(mContext, canvas, paint, name,
                diameter + 10,
                viewHeight - diameter + 6,
                false,
                true,
                NAME_TEXT_COLOR, NAME_SIZE, NAME_TEXT_STROKE_COLOR);
        //画内容
        CanvasUtils.paintReset(paint, 0);
        float textSize[] = CanvasUtils.drawText(mContext, canvas, paint, msg,
                diameter + 10,
                viewHeight - CommonUtils.dp2px(mContext, 10),
                false,
                false,
                TEXT_COLOR, TEXT_SIZE, 0);

        //画等级控件
      //  canvas.drawBitmap(levelBp, diameter/2-levelBp.getWidth()/2 , diameter + diameter / 3-levelBp.getHeight()-15, paint);
        canvas.drawBitmap(levelBp, diameter + 10 + nameSize[0] +10 , viewHeight - diameter + 6, paint);


        CanvasUtils.paintReset(paint, 0);
        paint.setStyle(Paint.Style.FILL);//充满
        paint.setColor(Color.BLACK);
        paint.setAlpha(65);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        RectF oval3 = new RectF(diameter / 2, viewHeight - CommonUtils.dp2px(mContext, 13), diameter + textSize[0] + 25, viewHeight);
        //画文字背景
        canvas.drawRoundRect(oval3, 60, 60, paint);//第二个参数是x半径，第三个参数是y半径


        //剪切弹幕图片，把多余的部分去掉
        float nameWidth = diameter + 10 + nameSize[0] +10 +levelBp.getWidth() + 1;
        float textWidth = diameter + textSize[0] + 20;
        viewWidth = (int) (nameWidth > textWidth ? nameWidth : textWidth) + 5;

        return Bitmap.createBitmap(barrageBitmap, 0, 0, viewWidth, diameter + diameter / 3);
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

            LruCacheBitmapLoader.getInstance().addBitmapToMemoryCache(String.valueOf(resId), bitmap);
            return bitmap;
        }

        return bitmap;
    }


    //弹幕速度 毫秒 最小值与最大值间随机
    private final int BARRAGE_TIME_MIN = 10000;
    private final int BARRAGE_TIME_MAX = 15000;
    //弹幕最大数量
    private final int BARRAGE_MAX = 3;
    //弹幕之间间隔幅度
    private final double BARRAGE_SIZE = 1;


    //弹幕刷出位置类型（相对于layout） 1、仅上半部分 2、仅中间部分  3、仅下半部分 4、全屏随机输出   功能还没有实现
    private int BARRAGE_TYPE = 3;

    private Context mContext;
    private float dmLocation = 0.0f;
    private LinkedList<Bitmap> queueBarrage = new LinkedList();
    private RelativeLayout barrageVessel;

    private void uplocation() {
        if (dmLocation < BARRAGE_MAX - 1 + BARRAGE_SIZE)
            dmLocation += 1 + BARRAGE_SIZE;
        else
            dmLocation = 0.0f;
    }

    public BarrageManager(Context c, Window window, int container) {
        this.mContext = c;
        barrageVessel = (RelativeLayout) window.findViewById(container);

        for (int i=0;i<BARRAGE_MAX;i++)
        {
            ImageView imageView=new ImageView(c);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(ActionBar.LayoutParams
                    .WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
            imageView.setVisibility(View.GONE);
            barrageVessel.addView(imageView);
        }
    }

    private void startDmAnim(View view,int barrageTime) {

        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height =view.getMeasuredHeight();
        int width =view.getMeasuredWidth();

        ObjectAnimator animator=ObjectAnimator.ofFloat(view,"translationX",CommonUtils.getScreenWidth(mContext),-width);
        view.setTranslationY(height*dmLocation);
        animator.setDuration(barrageTime);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

        uplocation();
    }

    private int randomTime() {
        return new Random().nextInt(BARRAGE_TIME_MAX - BARRAGE_TIME_MIN) + BARRAGE_TIME_MIN;
    }

    private synchronized boolean shoot(Bitmap bitmap) {
        for (int i=0;i<barrageVessel.getChildCount();i++)
        {
            View view=barrageVessel.getChildAt(i);
            if (view.getVisibility() == View.GONE)
            {
                view.setVisibility(View.VISIBLE);
                //获得弹幕速度
                int barrageTime = randomTime();
                //创建弹幕控件
                ((ImageView)view).setImageBitmap(bitmap);
                startDmAnim(view,barrageTime);
                del(barrageTime, view);
                return true;
            }
        }
        return false;
    }

    //向弹幕队列延时取出弹幕添加到集合，并发射
    private void del(final int time, final View view) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //barrageVessel.removeView(view);
                ((ImageView)view).setImageBitmap(null);
                view.setVisibility(View.GONE);
                shoot(queueBarrage.pollFirst());
            }
        }, time);

    }
}
