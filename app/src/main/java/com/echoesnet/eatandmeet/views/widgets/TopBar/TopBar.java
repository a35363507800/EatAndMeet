package com.echoesnet.eatandmeet.views.widgets.TopBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.widget.IconTextView;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author       ben
 * @modifier
 * @createDate
 * @version      1.0
 * @description  使用{@link com.echoesnet.eatandmeet.views.widgets.TopBarSwitch}
 */
@Deprecated
public class TopBar extends RelativeLayout
{
    private Drawable leftBtnImg, rightBtnImg, centerTitleImg;
    private String title = "", rightBtnTxt;
    private float titleTextSize;
    private TextView showView, rightBtn;
    private IconTextView leftBtn, leftBtn2;
    RelativeLayout rl_topBar;
    private View v;
    private ITopbarClickListener topbarClickListener;
    private Context mContext;

    public TopBar(Context context)
    {
        this(context, null);
    }

    public TopBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs)
    {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
        leftBtnImg = ta.getDrawable(R.styleable.TopBar_leftBtnImg);
        rightBtnImg = ta.getDrawable(R.styleable.TopBar_rightBtnImg);
        centerTitleImg = ta.getDrawable(R.styleable.TopBar_centerTitleImg);
        title = ta.getString(R.styleable.TopBar_centerTitleTxt);
        //titleTextSize =ta.getDimension(R.styleable.TopBar_centerTitleTxtSize, 15);
        rightBtnTxt = ta.getString(R.styleable.TopBar_rightBtnText);
        ta.recycle();
        mContext=context;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        v = LayoutInflater.from(getContext()).inflate(R.layout.top_bar, this);
        leftBtn = (IconTextView) v.findViewById(R.id.btn_left);
        leftBtn2 = (IconTextView) v.findViewById(R.id.btn_left2);
        rightBtn = (TextView) v.findViewById(R.id.btn_right);
        rl_topBar = (RelativeLayout) v.findViewById(R.id.rl_topBar);
        leftBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                topbarClickListener.leftClick(v);
            }
        });
        leftBtn2.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                topbarClickListener.left2Click(v);
            }
        });
        rightBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                topbarClickListener.rightClick(v);
            }
        });

        rightBtn.setText(rightBtnTxt);
        showView = (TextView) v.findViewById(R.id.tv_center_txt);
        showView.setText(title);
        //showView.setTextSize(TypedValue.COMPLEX_UNIT_SP,titleTextSize);
        //leftBtn.setBackgroundResource(R.drawable.back_btn_selector);

        //leftBtn.setImageDrawable(leftBtnImg);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
            rightBtn.setBackgroundDrawable(rightBtnImg);
            showView.setBackgroundDrawable(centerTitleImg);
        }
        else
        {
            rightBtn.setBackground(rightBtnImg);
            showView.setBackground(centerTitleImg);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M&&!Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
        {
            v.findViewById(R.id.title_color).setBackgroundColor(ContextCompat.getColor(getContext(),R.color.C0412T50));
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Meitu"))
        {
            v.findViewById(R.id.title_color).setBackgroundColor(ContextCompat.getColor(getContext(),R.color.C0412T50));
        }
    }

    public void setTitle(CharSequence txt)
    {
        showView.setText(txt);
    }

    public TextView getRightButton()
    {
        return rightBtn;
    }

    public void setRightTitle(CharSequence txt)
    {
        rightBtn.setText(txt);
    }

    public IconTextView getLeftButton()
    {
        return leftBtn;
    }

    public IconTextView getLeftButton2()
    {
        return leftBtn2;
    }

    public TextView getCenterTxtView()
    {
        return showView;
    }

    public void setBottomLineVisibility(int visibility)
    {
        View vBottomSeparator = v.findViewById(R.id.v_bottom_separator);
        vBottomSeparator.setVisibility(visibility);
    }

    public void setTopBarBackGroundColor(int color)
    {
        rl_topBar.setBackgroundColor(color);
    }

    public void setOnClickListener(ITopbarClickListener mListener)
    {
        this.topbarClickListener = mListener;
    }
}
