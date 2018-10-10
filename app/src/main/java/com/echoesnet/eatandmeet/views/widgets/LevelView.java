package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.widget.IconTextView;

/**
 * Created by lzy on 2017/4/10.
 */

public class LevelView extends RelativeLayout
{
    protected static final String TAG = LevelView.class.getSimpleName();
    protected Context mContext;
    protected RelativeLayout rootView;
    protected ImageView ivLevel;
    protected IconTextView ivIcon;
    protected IconTextView ivIcontv;
    protected LinearLayout ivIconLL;
    protected ImageView ivNumber1;
    protected ImageView ivNumber2;
    protected static final int LEVEL_MAX = 40;

    private static int[] levelImg = new int[]{
            R.drawable.lv01_xxhdpi,
            R.drawable.lv02_xxhdpi,
            R.drawable.lv03_xxhdpi,
            R.drawable.lv04_xxhdpi,
            R.drawable.lv05_xxhdpi,
            R.drawable.lv06_xxhdpi,
            R.drawable.lv07_xxhdpi,
            R.drawable.lv08_xxhdpi,
            R.drawable.lv09_xxhdpi,
            R.drawable.lv10_xxhdpi,
            R.drawable.lv11_xxhdpi,
            R.drawable.lv12_xxhdpi,
            R.drawable.lv13_xxhdpi,
            R.drawable.lv14_xxhdpi,
            R.drawable.lv15_xxhdpi,
            R.drawable.lv16_xxhdpi,
            R.drawable.lv17_xxhdpi,
            R.drawable.lv18_xxhdpi,
            R.drawable.lv19_xxhdpi,
            R.drawable.lv20_xxhdpi,
            R.drawable.lv21_xxhdpi,
            R.drawable.lv22_xxhdpi,
            R.drawable.lv23_xxhdpi,
            R.drawable.lv24_xxhdpi,
            R.drawable.lv25_xxhdpi,
            R.drawable.lv26_xxhdpi,
            R.drawable.lv27_xxhdpi,
            R.drawable.lv28_xxhdpi,
            R.drawable.lv29_xxhdpi,
            R.drawable.lv30_xxhdpi,
            R.drawable.lv31_xxhdpi,
            R.drawable.lv32_xxhdpi,
            R.drawable.lv33_xxhdpi,
            R.drawable.lv34_xxhdpi,
            R.drawable.lv35_xxhdpi,
            R.drawable.lv36_xxhdpi,
            R.drawable.lv37_xxhdpi,
            R.drawable.lv38_xxhdpi,
            R.drawable.lv39_xxhdpi,
            R.drawable.lv40_xxhdpi
           };

    private static int[] levelHostImg = new int[]{
            R.drawable.zblv01_xxhdpi,
            R.drawable.zblv02_xxhdpi,
            R.drawable.zblv03_xxhdpi,
            R.drawable.zblv04_xxhdpi,
            R.drawable.zblv05_xxhdpi,
            R.drawable.zblv06_xxhdpi,
            R.drawable.zblv07_xxhdpi,
            R.drawable.zblv08_xxhdpi,
            R.drawable.zblv09_xxhdpi,
            R.drawable.zblv10_xxhdpi,
            R.drawable.zblv11_xxhdpi,
            R.drawable.zblv12_xxhdpi,
            R.drawable.zblv13_xxhdpi,
            R.drawable.zblv14_xxhdpi,
            R.drawable.zblv15_xxhdpi,
            R.drawable.zblv16_xxhdpi,
            R.drawable.zblv17_xxhdpi,
            R.drawable.zblv18_xxhdpi,
            R.drawable.zblv19_xxhdpi,
            R.drawable.zblv20_xxhdpi,
            R.drawable.zblv21_xxhdpi,
            R.drawable.zblv22_xxhdpi,
            R.drawable.zblv23_xxhdpi,
            R.drawable.zblv24_xxhdpi,
            R.drawable.zblv25_xxhdpi,
            R.drawable.zblv26_xxhdpi,
            R.drawable.zblv27_xxhdpi,
            R.drawable.zblv28_xxhdpi,
            R.drawable.zblv29_xxhdpi,
            R.drawable.zblv30_xxhdpi,
            R.drawable.zblv31_xxhdpi,
            R.drawable.zblv32_xxhdpi,
            R.drawable.zblv33_xxhdpi,
            R.drawable.zblv34_xxhdpi,
            R.drawable.zblv35_xxhdpi,
            R.drawable.zblv36_xxhdpi,
            R.drawable.zblv37_xxhdpi,
            R.drawable.zblv38_xxhdpi,
            R.drawable.zblv39_xxhdpi,
            R.drawable.zblv40_xxhdpi
    };


    private static int[] levelMiddleImg = new int[]{
            R.drawable.lv01_middle_xxhdpi,
            R.drawable.lv02_middle_xxhdpi,
            R.drawable.lv03_middle_xxhdpi,
            R.drawable.lv04_middle_xxhdpi,
            R.drawable.lv05_middle_xxhdpi,
            R.drawable.lv06_middle_xxhdpi,
            R.drawable.lv07_middle_xxhdpi,
            R.drawable.lv08_middle_xxhdpi,
            R.drawable.lv09_middle_xxhdpi,
            R.drawable.lv10_middle_xxhdpi,
            R.drawable.lv11_middle_xxhdpi,
            R.drawable.lv12_middle_xxhdpi,
            R.drawable.lv13_middle_xxhdpi,
            R.drawable.lv14_middle_xxhdpi,
            R.drawable.lv15_middle_xxhdpi,
            R.drawable.lv16_middle_xxhdpi,
            R.drawable.lv17_middle_xxhdpi,
            R.drawable.lv18_middle_xxhdpi,
            R.drawable.lv19_middle_xxhdpi,
            R.drawable.lv20_middle_xxhdpi,
            R.drawable.lv21_middle_xxhdpi,
            R.drawable.lv22_middle_xxhdpi,
            R.drawable.lv23_middle_xxhdpi,
            R.drawable.lv24_middle_xxhdpi,
            R.drawable.lv25_middle_xxhdpi,
            R.drawable.lv26_middle_xxhdpi,
            R.drawable.lv27_middle_xxhdpi,
            R.drawable.lv28_middle_xxhdpi,
            R.drawable.lv29_middle_xxhdpi,
            R.drawable.lv30_middle_xxhdpi,
            R.drawable.lv31_middle_xxhdpi,
            R.drawable.lv32_middle_xxhdpi,
            R.drawable.lv33_middle_xxhdpi,
            R.drawable.lv34_middle_xxhdpi,
            R.drawable.lv35_middle_xxhdpi,
            R.drawable.lv36_middle_xxhdpi,
            R.drawable.lv37_middle_xxhdpi,
            R.drawable.lv38_middle_xxhdpi,
            R.drawable.lv39_middle_xxhdpi,
            R.drawable.lv40_middle_xxhdpi
    };

    private static int[] levelRoundImg = new int[]{
            R.drawable.live_lv1_xxhdpi,
            R.drawable.live_lv2_xxhdpi,
            R.drawable.live_lv3_xxhdpi,
            R.drawable.live_lv4_xxhdpi,
            R.drawable.live_lv5_xxhdpi,
            R.drawable.live_lv6_xxhdpi,
            R.drawable.live_lv7_xxhdpi,
            R.drawable.live_lv8_xxhdpi,
            R.drawable.live_lv9_xxhdpi,
            R.drawable.live_lv10_xxhdpi,
            R.drawable.live_lv11_xxhdpi,
            R.drawable.live_lv12_xxhdpi,
            R.drawable.live_lv13_xxhdpi,
            R.drawable.live_lv14_xxhdpi,
            R.drawable.live_lv15_xxhdpi,
            R.drawable.live_lv16_xxhdpi,
            R.drawable.live_lv17_xxhdpi,
            R.drawable.live_lv18_xxhdpi,
            R.drawable.live_lv19_xxhdpi,
            R.drawable.live_lv20_xxhdpi,
            R.drawable.live_lv21_xxhdpi,
            R.drawable.live_lv22_xxhdpi,
            R.drawable.live_lv23_xxhdpi,
            R.drawable.live_lv24_xxhdpi,
            R.drawable.live_lv25_xxhdpi,
            R.drawable.live_lv26_xxhdpi,
            R.drawable.live_lv27_xxhdpi,
            R.drawable.live_lv28_xxhdpi,
            R.drawable.live_lv29_xxhdpi,
            R.drawable.live_lv30_xxhdpi,
            R.drawable.live_lv31_xxhdpi,
            R.drawable.live_lv32_xxhdpi,
            R.drawable.live_lv33_xxhdpi,
            R.drawable.live_lv34_xxhdpi,
            R.drawable.live_lv35_xxhdpi,
            R.drawable.live_lv36_xxhdpi,
            R.drawable.live_lv37_xxhdpi,
            R.drawable.live_lv38_xxhdpi,
            R.drawable.live_lv39_xxhdpi,
            R.drawable.live_lv40_xxhdpi,
    };


    private static int[] levelHostMiddleImg = new int[]{
            R.drawable.zblv01_middle_xxhdpi,
            R.drawable.zblv02_middle_xxhdpi,
            R.drawable.zblv03_middle_xxhdpi,
            R.drawable.zblv04_middle_xxhdpi,
            R.drawable.zblv05_middle_xxhdpi,
            R.drawable.zblv06_middle_xxhdpi,
            R.drawable.zblv07_middle_xxhdpi,
            R.drawable.zblv08_middle_xxhdpi,
            R.drawable.zblv09_middle_xxhdpi,
            R.drawable.zblv10_middle_xxhdpi,
            R.drawable.zblv11_middle_xxhdpi,
            R.drawable.zblv12_middle_xxhdpi,
            R.drawable.zblv13_middle_xxhdpi,
            R.drawable.zblv14_middle_xxhdpi,
            R.drawable.zblv15_middle_xxhdpi,
            R.drawable.zblv16_middle_xxhdpi,
            R.drawable.zblv17_middle_xxhdpi,
            R.drawable.zblv18_middle_xxhdpi,
            R.drawable.zblv19_middle_xxhdpi,
            R.drawable.zblv20_middle_xxhdpi,
            R.drawable.zblv21_middle_xxhdpi,
            R.drawable.zblv22_middle_xxhdpi,
            R.drawable.zblv23_middle_xxhdpi,
            R.drawable.zblv24_middle_xxhdpi,
            R.drawable.zblv25_middle_xxhdpi,
            R.drawable.zblv26_middle_xxhdpi,
            R.drawable.zblv27_middle_xxhdpi,
            R.drawable.zblv28_middle_xxhdpi,
            R.drawable.zblv29_middle_xxhdpi,
            R.drawable.zblv30_middle_xxhdpi,
            R.drawable.zblv31_middle_xxhdpi,
            R.drawable.zblv32_middle_xxhdpi,
            R.drawable.zblv33_middle_xxhdpi,
            R.drawable.zblv34_middle_xxhdpi,
            R.drawable.zblv35_middle_xxhdpi,
            R.drawable.zblv36_middle_xxhdpi,
            R.drawable.zblv37_middle_xxhdpi,
            R.drawable.zblv38_middle_xxhdpi,
            R.drawable.zblv39_middle_xxhdpi,
            R.drawable.zblv40_middle_xxhdpi
    };

    private float textsize;

    public LevelView(Context context)
    {
        this(context, null);
    }

    public LevelView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LevelView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LevelViewAttr);
        textsize = a.getDimension(R.styleable.LevelViewAttr_text_size, context.getResources().getDimension(R.dimen.f6));

    }

    protected float getTextSize()
    {
        return textsize;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        initView();
    }

    private void initView()
    {
        rootView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.level_view, null);
        addView(rootView);
        ivLevel = (ImageView) rootView.findViewById(R.id.iv_level);
        ivIcon = (IconTextView) rootView.findViewById(R.id.iv_icon);
        ivIcontv = (IconTextView) rootView.findViewById(R.id.iv_icon_text);
        ivIconLL = (LinearLayout) rootView.findViewById(R.id.iv_icon_layout);
        ivNumber1 = (ImageView) rootView.findViewById(R.id.iv_number1);
        ivNumber2 = (ImageView) rootView.findViewById(R.id.iv_number2);
    }

    /**
     * 设置等级
     * 等级换UI了 调setLevel(String level,int StatusareHost)这个换UI
     *      HOST=0;
            USER=1;
     * @param level
     */
    public void setLevel(String level,int statusareHost)
    {

        if(TextUtils.isEmpty(level))
        {
            this.setVisibility(GONE);
            return;
        }

        int tempLevel = 0;

        try
        {
            tempLevel = Integer.parseInt(level);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        if (tempLevel == 0)
        {
            this.setVisibility(GONE);
            return;
        }
        else
        {
            this.setVisibility(VISIBLE);
        }

        switch (statusareHost)
        {
            case USER:
                ivLevel.setImageDrawable(ContextCompat.getDrawable(mContext,getLevelImage(tempLevel)));
                break;
            case HOST:
                ivLevel.setImageDrawable(ContextCompat.getDrawable(mContext,getLevelImageHost(tempLevel)));
                break;
        }
    }

//    此处方法不要删
//    public void setLevel(String number,int statusareHost)
//    {
//        if(TextUtils.isEmpty(number))
//        {
//            this.setVisibility(GONE);
//            return;
//        }
//
//        int tempLevel = 0;
//        try
//        {
//            tempLevel = Integer.parseInt(number);
//        } catch (NumberFormatException e)
//        {
//            e.printStackTrace();
//        }
//
//
//            if (tempLevel == 0)
//            {
//                if(zeroVisible==false)
//                {
//                this.setVisibility(GONE);
//                return;
//                }
//            } else
//            {
//                this.setVisibility(VISIBLE);
//            }
//
//
//        ivLevel.setVisibility(View.GONE);
//        ivIconLL.setVisibility(View.VISIBLE);
//        ivIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
//        ivIcontv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
//
//
//        switch (statusareHost)
//        {
//            case USER:
//                if(tempLevel>9)
//                    ivIcontv.setText(""+tempLevel);
//                else
//                    {
//                        if(tempLevel==0)
//                            ivIcontv.setText(tempLevel+"");
//                        else
//                            ivIcontv.setText("0"+tempLevel);
//                    }
//
//
//                ivIcon.setText("Lv.");
//                ivIconLL.setBackgroundResource(getLevelBgID(tempLevel));
//                break;
//
//            case HOST:
//                ivIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize-4);
//                ivIcon.setText(getIcon(tempLevel));
//
//                if(tempLevel>9)
//                    ivIcontv.setText(""+tempLevel);
//                else
//                {
//                    if(tempLevel==0)
//                        ivIcontv.setText(tempLevel+"");
//                    else
//                        ivIcontv.setText("0"+tempLevel);
//                }
//
//                ivIconLL.setBackgroundResource(getLevelBgID(tempLevel));
//                break;
//
//        }
//
//    }



     private boolean zeroVisible=false;
    /**
     * 零级是否显示等级图片 需要在设置等级之前设置
     * @param flag
     */
    public void setZeroVisible(boolean flag)
    {
        this.zeroVisible=flag;
    }


    public static int getLevelImage(int level)
    {
        if(level==0)
            return 0;

        if (level > LEVEL_MAX || level < 1)
            return 0;
        return levelImg[level - 1];
    }

    //资源ID为中标签
    public static int getLevelMiddleImage(int level)
    {
        if(level==0)
            return 0;

        if (level > LEVEL_MAX || level < 1)
            return 0;
        return levelMiddleImg[level - 1];
    }
    //资源ID为中标签
    public static int getLevelMiddleImageHost(int level)
    {
        if(level==0)
            return 0;

        if (level > LEVEL_MAX || level < 1)
            return 0;
        return levelHostMiddleImg[level - 1];
    }

    public static int getLevelImageHost(int level)
    {
        if(level==0)
            return 0;

        if (level > LEVEL_MAX || level < 1)
            return 0;
        return levelHostImg[level - 1];
    }

    //圆角等级标签
    public static int getLevelRoundImage(int level)
    {
        if(level==0)
            return 0;

        if (level > LEVEL_MAX || level < 1)
            return 0;
        return levelRoundImg[level - 1];
    }

    public static int getLevelBgID(int level)
    {
        return level >= LevelHeaderView.LEVEL[0][0] && level <= LevelHeaderView.LEVEL[0][LevelHeaderView.LEVEL[0].length-1] ? (R.drawable.shape_round_level_0_c0331)
                : level >= LevelHeaderView.LEVEL[1][0] && level <= LevelHeaderView.LEVEL[1][LevelHeaderView.LEVEL[1].length-1] ? (R.drawable.shape_round_level_4_c0315)
                : level >= LevelHeaderView.LEVEL[2][0] && level <= LevelHeaderView.LEVEL[2][LevelHeaderView.LEVEL[2].length-1] ? (R.drawable.shape_round_level_4_c0311)
                : level >= LevelHeaderView.LEVEL[3][0] && level <= LevelHeaderView.LEVEL[3][LevelHeaderView.LEVEL[3].length-1] ? (R.drawable.shape_round_level_4_c0313)
                : level >= LevelHeaderView.LEVEL[4][0] && level <= LevelHeaderView.LEVEL[4][LevelHeaderView.LEVEL[4].length-1] ? (R.drawable.shape_round_level_4_c0312)
                : level >= LevelHeaderView.LEVEL[5][0] && level <= LevelHeaderView.LEVEL[5][LevelHeaderView.LEVEL[5].length-1] ? (R.drawable.shape_round_level_4_c0312)
                : level >= LevelHeaderView.LEVEL[6][0] && level <= LevelHeaderView.LEVEL[6][LevelHeaderView.LEVEL[6].length-1] ? (R.drawable.shape_round_level_4_c0412)
                : level >= LevelHeaderView.LEVEL[7][0] && level <= LevelHeaderView.LEVEL[7][LevelHeaderView.LEVEL[7].length-1] ? (R.drawable.shape_round_level_4_c0412)
                : 0;
    }

    public static String getIcon(int level)
    {
        return level >= LevelHeaderView.LEVEL[0][0] && level <= LevelHeaderView.LEVEL[0][LevelHeaderView.LEVEL[0].length-1] ? "{eam-e603}"
                : level >= LevelHeaderView.LEVEL[1][0] && level <= LevelHeaderView.LEVEL[1][LevelHeaderView.LEVEL[1].length-1] ? ("{eam-e607}")
                : level >= LevelHeaderView.LEVEL[2][0] && level <= LevelHeaderView.LEVEL[2][LevelHeaderView.LEVEL[2].length-1] ? ("{eam-e604}")
                : level >= LevelHeaderView.LEVEL[3][0] && level <= LevelHeaderView.LEVEL[3][LevelHeaderView.LEVEL[3].length-1] ? ("{eam-e605}")
                : level >= LevelHeaderView.LEVEL[4][0] && level <= LevelHeaderView.LEVEL[4][LevelHeaderView.LEVEL[4].length-1] ? ("{eam-e623}")
                : level >= LevelHeaderView.LEVEL[5][0] && level <= LevelHeaderView.LEVEL[5][LevelHeaderView.LEVEL[5].length-1] ? ("{eam-e606}")
                : "";
    }
    public static final int HOST=0;
    public static final int USER=1;
}
