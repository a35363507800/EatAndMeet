package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * *          _       _
 * *   __   _(_)_   _(_) __ _ _ __
 * *   \ \ / / \ \ / / |/ _` | '_ \
 * *    \ V /| |\ V /| | (_| | | | |
 * *     \_/ |_| \_/ |_|\__,_|_| |_|
 * <p/>
 * Created by vivian on 16/3/3.
 */
public class ChatImageView extends ImageView {
    private Context mContext;
    Paint paint;
    Bitmap bitmap;//前景图
    Bitmap bgBitmap;//.9.png 背景图
    Drawable drawable;
    Drawable drawableBg;

    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    public ChatImageView(Context context) {
        super(context);
        mContext=context;
        init();
    }

    public ChatImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext=context;
        init();
    }

    public ChatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init();
    }

    public ChatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        init();
    }

    public void init(){
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//
    }

    @Override
    protected void onDraw(Canvas canvas) {
         drawable=getDrawable();
         drawableBg=getBackground();
//        Drawable drawable=getResources().getDrawable(R.drawable.psb);
//        Drawable drawableBg=getResources().getDrawable(R.drawable.novel_like_num_night);

        if(drawable==null||drawableBg==null){
            return;
        }
        if(getWidth()==0||getHeight()==0){
            return;
        }
        this.measure(0,0);
        bitmap=drawableToBitmap(drawable,getWidth(),getHeight());//获取前景图
        bgBitmap=drawableToBitmap(drawableBg,getWidth(),getHeight());//获取背景图

        if (defaultWidth == 0) {
            defaultWidth = getWidth();

        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

//        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawBitmap(bgBitmap,0,0,paint);//画背景图
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,0,0,paint);
    }


    public Bitmap drawableToBitmap(Drawable drawable, int w, int h) {
        // 取 drawable 的颜色格式
//        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;//OPAQUE:不透明 getOpacity()：获取drawable的透明度
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h,Bitmap.Config.ARGB_8888);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
