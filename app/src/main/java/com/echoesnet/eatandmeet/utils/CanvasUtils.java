package com.echoesnet.eatandmeet.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

/**
 * Created by Administrator on 2017/6/16.
 */

public class CanvasUtils
{
      public static Bitmap roundImage(Bitmap bitmap)
      {
          Bitmap barrageBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
          Canvas canvas = new Canvas(barrageBitmap);
          Paint paint = new Paint();
          paint.setAntiAlias(true);
          paint.setColor(Color.BLACK);
          paint.setStyle(Paint.Style.FILL);
          canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);


          paint.reset();
          paint.setAntiAlias(true);
          paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
          canvas.drawBitmap(bitmap, 0, 0, paint);
          return barrageBitmap;
      }

    /**
     * 描边效果文字
     * @param canvas
     * @param text  文字
     * @param x    canvas X点
     * @param y     canvas Y点
     * @param textSize  文字大小sp
     * @param textColor  文字颜色
     * @param strokeColor  描边颜色
     * @param isBold  字体是否加粗
     * @param isStroke 字体是否描边
     * @return 返回文字大小数组  index :0宽 1高
     */
    public static float[] drawText(Context mContext,Canvas canvas, Paint paint,String text, int x, int y,boolean isStroke,boolean isBold,int textColor,int textSize,int strokeColor)
    {
        paint.setTextSize(sp2px(mContext, textSize));
        paint.setDither(true);  //防抖动
        int alpha=paint.getAlpha();
        if(isBold)
        {
            paint.setFakeBoldText(true);  //字体加粗
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            paint.setTypeface(font); //字体加粗 上边那个有时候无效
        }
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        float baseLineY = y - (fontMetrics.bottom-fontMetrics.top)/4- fontMetrics.top;
        if(isStroke)
        {
            // 自定义描边效果
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(ContextCompat.getColor(mContext, strokeColor));
            paint.setStrokeWidth(3);
            paint.setAlpha(alpha);
            canvas.drawText(text, x, baseLineY, paint);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        paint.setColor(ContextCompat.getColor(mContext, textColor));
        paint.setAlpha(alpha);
        canvas.drawText(text, x, baseLineY , paint);


        float[] textWH = new float[2];
        textWH[0] = textRect.width();
        textWH[1] = textRect.height();
        return textWH;
    }


    private static int sp2px(Context context, int sp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp,
                context.getResources().getDisplayMetrics());
    }

    public  static Bitmap bitmapScale(Bitmap bm, int newWidth, int newHeight)
    {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
    }

    public static void paintReset(Paint paint, int alpha)
    {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setAlpha(255-alpha);
    }
}
