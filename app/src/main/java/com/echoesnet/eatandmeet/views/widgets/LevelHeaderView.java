package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.bumptech.glide.request.target.SimpleTarget;
import com.echoesnet.eatandmeet.R;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by lzy on 2017/4/7.
 */
public class LevelHeaderView extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener
{
    public LevelHeaderView(Context context, float width, float height)
    {
        this(context, null);
    }

    public LevelHeaderView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LevelHeaderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LevelHeaderViewAttr);
        float height = a.getDimension(R.styleable.LevelHeaderViewAttr_level_height, 0);
        float width = a.getDimension(R.styleable.LevelHeaderViewAttr_level_width, 0);
        showRightIcon = a.getBoolean(R.styleable.LevelHeaderViewAttr_level_ril_icon, false);
        liveState = a.getBoolean(R.styleable.LevelHeaderViewAttr_live_state, false);
        init(context, width, height);
    }

    private RoundedImageView rounImageView;
    private RoundedImageView rivLev;
    private RoundedImageView rightLev;
    private RelativeLayout headSlayout;
    private ViewGroup.LayoutParams headSlayoutP;
    private static final int LEVEL_MAX = 40;
    private boolean liveState = false;
    private int width;
    private int level = -1;
    private boolean showRightIcon = false;


    //等级规则
    public static final int[][] LEVEL = new int[][]
            {
                    {0},      //符合 灰色圈 等级
                    {1, 2, 3},  //lv1-3
                    {4, 5, 6},   //lv4-6
                    {7, 8, 9},   //lv7-9
                    {10, 11, 12},  //lv10-12
                    {13, 14, 15},  //lv13-15
                    {16, 17, 18},   //lv16-18
                    {19, 20},   //lv19-20
                    {21, 22},
                    {23, 24},
                    {25, 26},
                    {27, 28},
                    {29, 30},
                    {31, 32},
                    {33, 34},
                    {35},
                    {36},
                    {37},
                    {38},
                    {39},
                    {40},
            };


    private static int[] drawableIds = new int[]{
            R.drawable.lv123_xhdpi
            , R.drawable.lv456_xhdpi
            , R.drawable.lv789_xhdpi
            , R.drawable.lv101112_xhdpi
            , R.drawable.lv131415_xhdpi
            , R.drawable.yx_1lv_xxhdpi
            , R.drawable.yx_2lv_xxhdpi
            , R.drawable.yx_3lv_xxhdpi
    };
    private static int[] shapeIds = new int[]{
            R.color.transparent
            // ,R.drawable.level_stroke_c0331
            , R.drawable.level_stroke_c0341
            , R.drawable.level_stroke_c0342
            , R.drawable.level_stroke_c0343
            , R.drawable.level_stroke_c0344
            , R.drawable.level_stroke_c0345
            , R.drawable.lv1_3
            , R.drawable.lv4_6
            , R.drawable.lv7_9
            , R.drawable.lv10_12
            , R.drawable.lv13_15
            , R.drawable.lv16_18
            , R.drawable.lv18_20
            , R.drawable.lv20_22
            , R.drawable.lv23_24
            , R.drawable.lv25_26
            , R.drawable.lv27_28
            , R.drawable.lv29_30
            , R.drawable.lv31_32
            , R.drawable.lv33_34
            , R.drawable.lv35
            , R.drawable.lv36
            , R.drawable.lv37
            , R.drawable.lv38
            , R.drawable.lv39
            , R.drawable.lv40

    };


    public final static int TYPE_CORNER = 10;  //角标图片ID
    public final static int TYPE_STROKE = 11;  //边框图片
    public final static int TYPE_STROKE_COLOR = 12; //边框颜色ID

    /**
     * 返回等级对应ID
     *
     * @param level
     * @param liveState 是否用直播间套图
     * @return
     */
    public static int getLevelResourceId(int level, boolean liveState, int resourceType)
    {
        int resourceId = R.color.transparent;
        for (int i = 0; i < LEVEL.length; i++)
        {
            if (level >= LEVEL[i][0] && level <= LEVEL[i][LEVEL[i].length - 1])
            {
                switch (i)
                {
                    case 0:
                        if (resourceType == TYPE_CORNER)
                            resourceId = 0;
                        else if (resourceType == TYPE_STROKE)
                            resourceId = shapeIds[0];
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.transparent;
                        break;
                    case 1:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? drawableIds[5] : drawableIds[0]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = shapeIds[6];
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0341P;

                        break;
                    case 2:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? drawableIds[6] : drawableIds[1]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = shapeIds[7];
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0342P;
                        break;
                    case 3:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? drawableIds[7] : drawableIds[2]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = shapeIds[8];
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0343P;
                        break;
                    case 4:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[3]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[9] : shapeIds[4]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0344P;
                        break;
                    case 5:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[3]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[10] : shapeIds[4]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0344P;
                        break;
                    case 6:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[11] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 7:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[12] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 8:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[13] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 9:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[14] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 10:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[15] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 11:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[16] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 12:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[17] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 13:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[18] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 14:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[19] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 15:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[20] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 16:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[21] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 17:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[22] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 18:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[23] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 19:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[24] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                    case 20:
                        if (resourceType == TYPE_CORNER)
                            resourceId = (liveState ? 0 : drawableIds[4]);
                        else if (resourceType == TYPE_STROKE)
                            resourceId = (liveState ? shapeIds[25] : shapeIds[5]);
                        else if (resourceType == TYPE_STROKE_COLOR)
                            resourceId = R.color.C0345P;
                        break;
                }
            }

        }
        return resourceId;
    }

    private Context mContext;

    private void init(Context context, float width, float height)
    {
        mContext = context;
        inflate(mContext, R.layout.level_imageview, this);
        headSlayout = getViewById(this, R.id.head_stroke_layou);
        headSlayoutP = headSlayout.getLayoutParams();
        rounImageView = getViewById(this, R.id.levelimageview_headimage);
        rivLev = getViewById(this, R.id.levelimageview_levelimage);
        rightLev = getViewById(this, R.id.levelimageview_righticon);

        LayoutParams pa = (LayoutParams) rightLev.getLayoutParams();
        pa.height = (int) height;
        pa.width = (int) width;
        rightLev.setLayoutParams(pa);

        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    //

    public void setBorderColor(@ColorInt int color)
    {
        rounImageView.setBorderColor(color);
    }

    public void setBorderWidth(@DimenRes int resId)
    {
        rounImageView.setBorderWidth(resId);
    }

    public void setBorderWidth(float width)
    {
        rounImageView.setBorderWidth(width);
    }

    private void upLevelData(final int level)
    {
        this.level = level;

//      //  圆形等级图标
//        if(rivLev.getVisibility()!=View.GONE)
//        rivLev.setImageResource(LevelView.getLevelRoundImage(level));

//        //直播间角标
//        if (liveState)
//            rightLev.setImageResource(LevelHeaderView.getLevelResourceId(level, true, LevelHeaderView.TYPE_CORNER));

        if (liveState&&width!=0)
        {

            ViewGroup.LayoutParams pa = this.getLayoutParams();
            if(pa.height==width)
            {
                //  int number = (int) (this.width * 0.15);
                int zHeight = (int) (this.width * 0.7);
                int zWidth = (int) (this.width * 0.3);
                pa.height = this.width + zHeight + 5;
                pa.width = this.width + zWidth + 5;
                setLayoutParams(pa);

                pa = rounImageView.getLayoutParams();
                pa.height = width;
                pa.width = width;
                rounImageView.setLayoutParams(pa);

            }

            // 头像皇冠边框样式
            int drawbleId = getLevelResourceId(level, liveState, TYPE_STROKE);
            if (drawbleId == 0)
                drawbleId = R.color.transparent;
            headSlayout.setBackground(ContextCompat.getDrawable(mContext, drawbleId));
            //        pa = headSlayout.getLayoutParams();
//        pa.height = this.width;
//        headSlayout.setLayoutParams(pa);
        }



//        if (liveState)
//        {
//            if (level > 9)
//                headSlayoutP.height = this.width + (int) (this.width * 0.3) + 1;
//            else
//                headSlayoutP.height = this.width;
//
//            headSlayout.setLayoutParams(headSlayoutP);
//        }

    }


    /**
     * 设置等级
     *
     * @param level
     */
    public void setLevel(String level)
    {
        int lv = 0;
        try
        {
            lv = Integer.parseInt(level);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        if (lv < 0 || lv > LEVEL_MAX)
            throw new IllegalArgumentException(String.format("必须是介于0到%s的数字", LEVEL_MAX));

        upLevelData(lv);

    }

    //右下角标
    public void showRightIcon(boolean isShow)
    {
        showRightIcon = isShow;
        if (isShow)
            rightLev.setVisibility(View.VISIBLE);
        else
            rightLev.setVisibility(View.GONE);
    }

    //右下角标
    public void showRightIcon(String isShow)
    {
        showRightIcon = "1".equals(isShow) ? true : false;
        if ("1".equals(isShow))
            rightLev.setVisibility(View.VISIBLE);
        else
            rightLev.setVisibility(View.GONE);
    }

    /**
     * 设置头像图片
     */
    public void setHeadImageByUrl(String imageUrl)
    {
        if (rounImageView.getTag() != null)
        {
            if (rounImageView.getTag().equals(imageUrl))
            {
                return;
            }
        }
        rounImageView.setTag(imageUrl);
        GlideApp.with(mContext.getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.userhead)
                .centerCrop()
                .error(R.drawable.userhead)
                .into(rounImageView);
    }

    public void setImageResourceByID(int id)
    {
        rounImageView.setImageResource(id);
    }

    public void setHeadImageByBitmap(Bitmap bitmap)
    {
        rounImageView.setImageBitmap(bitmap);
    }

    public void setHeadImageByDrawable(Drawable drawable)
    {
        rounImageView.setImageDrawable(drawable);
    }

    /**
     * 改变头像等级图片风格，true直播风格 false正常风格
     * 尽量不要用代码方式确定风格，某些情况会出现页面不适配的情况，在布局属性live_state上进行确定
     *
     * @param flag
     */
    @Deprecated
    public void setLiveState(boolean flag)
    {
        this.liveState = flag;
    }

    private <T extends View> T getViewById(View view, int id)
    {
        return (T) view.findViewById(id);
    }

    private int first=1;
    //适配
    @Override
    public void onGlobalLayout()
    {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        this.width = getMeasuredWidth();
        ViewGroup.LayoutParams pa = this.getLayoutParams();


        if(first==1)
        {

            //角标
            rightLev.setImageResource(R.drawable.v_28x28);
            showRightIcon(showRightIcon);
            first=0;
        }

//        //等级标识
//        pa = rivLev.getLayoutParams();
//        pa.height = (int) (this.width * 0.4);
//        pa.width = (int) (this.width * 0.68);
//        rivLev.setLayoutParams(pa);
        rivLev.setVisibility(GONE);

        pa = rightLev.getLayoutParams();
        if (pa.height == 0)
        {
            pa.height = (int) (this.width * 0.3);
            pa.width = (int) (this.width * 0.3);
            rightLev.setLayoutParams(pa);
        }



        if (level != -1)
            upLevelData(level);
    }

}
