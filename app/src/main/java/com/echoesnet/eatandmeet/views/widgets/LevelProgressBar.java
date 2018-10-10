package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

/**
 * Created by Administrator on 2017/4/19.
 */

public class LevelProgressBar extends RelativeLayout
{

    private static final int STYLE_WHITE=0x00;
    private static final int STYLE_C0412=0x01;
    private int width = 0;
    private int style;
    public LevelProgressBar(Context context)
    {
        this(context,null,0);
    }

    public LevelProgressBar(Context context, AttributeSet attrs)
    {
        this(context, attrs,0);

    }

    public LevelProgressBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LevelProgressBar);
        style = a.getInt(R.styleable.LevelProgressBar_bar_style, STYLE_WHITE);
        init(context);
    }

    // private int level=0;
    private int experienceMax = 100;
    private int experienceProgress = 0;
    private View vlayout;
    private View viewBar;
    private TextView tvProgress, tvMax,tvExpNumber;

    private void init(Context context)
    {
        View view=null;
        switch (style)
        {
            case STYLE_WHITE:
                view = LayoutInflater.from(context).inflate(R.layout.levelprogressbar_content, null);
                break;
            case STYLE_C0412:
                view = LayoutInflater.from(context).inflate(R.layout.levelprogressbar0412_content, null);
                tvExpNumber= (TextView) view.findViewById(R.id.tv_exp_number);
                break;
            default:
                view = LayoutInflater.from(context).inflate(R.layout.levelprogressbar_content, null);
                break;
        }

        vlayout = view.findViewById(R.id.rl_levelprogressbar_layout);
        viewBar = view.findViewById(R.id.v_levelprogressbar_bar);
        tvProgress = (TextView) view.findViewById(R.id.tv_levelprogressbar_progress);
        tvMax = (TextView) view.findViewById(R.id.tv_levelprogressbar_max);

        addView(view);
        vlayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                width = vlayout.getWidth();
                upBar();
            }
        });
    }

    /**
     * curExp:当前有经验(全部)
     * upLevelExp:还差多少经验升级
     * levelExp:本级已有多少经验
     *
     *
     * @param levelExp
     * @param upLevelExp
     * @param curExp
     */
    public void setLevelData(int levelExp, int upLevelExp, int curExp)
    {
        if(upLevelExp==0) {
            levelExp=1;
        }
        this.experienceMax = upLevelExp + levelExp;
        tvMax.setText(String.valueOf(curExp + upLevelExp));

        this.experienceProgress = levelExp;
        tvProgress.setText(String.valueOf(curExp));
        upBar();
    }

    public void setLevelData(int levelExp, int upLevelExp, int curExp,int xExp)
    {
        if(tvExpNumber!=null)
            tvExpNumber.setText(xExp+"");
        setLevelData(levelExp,upLevelExp,curExp);
    }

    private void upBar()
    {
        LayoutParams params = (LayoutParams) viewBar.getLayoutParams();
        float max = experienceMax;
        float proportion = experienceProgress / max;

        float xWidth = width * proportion;

        if (experienceMax - experienceProgress < 100 && experienceMax != experienceProgress)
            params.width = (int) xWidth - 6;
        else
        {
            params.width = (int) xWidth;
        }

        viewBar.setLayoutParams(params);
    }
}
