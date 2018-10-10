package com.echoesnet.eatandmeet.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/8/1
 * @description 缺省图
 */

public class EmptyView extends LinearLayout
{
    private Context mContext;
    private ImageView ivDefaultIcon;
    private TextView tvDefaultDes;
    private TextView tvAction;
    private TextView tvMoreDes;
    private View goneView;
    private View vDivideTop;
    private View vDivideTop2;

    public EmptyView(Context context)
    {
        this(context, null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.empty_view, this);
        ivDefaultIcon = (ImageView) view.findViewById(R.id.iv_default_icon);
        tvDefaultDes = (TextView) view.findViewById(R.id.tv_default_des);
        goneView =  view.findViewById(R.id.gone_view);
        tvMoreDes = (TextView) view.findViewById(R.id.tv_more_des);
        tvAction = (TextView) view.findViewById(R.id.tv_action);
        vDivideTop = view.findViewById(R.id.view_divide_top);
        vDivideTop2 = view.findViewById(R.id.view_divide_top2);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    public void setContent(String content)
    {
        tvDefaultDes.setText(content);

    }

    public void setMoreContent(String content)
    {
        tvMoreDes.setVisibility(VISIBLE);
        tvMoreDes.setText(content);
    }
   public void setTopDivideShow(boolean show)
   {
       if (show)
       {
           vDivideTop.setVisibility(VISIBLE);
       }
       else
       {
           vDivideTop.setVisibility(GONE);
       }
   }
    public void setTop2DivideShow(boolean show)
    {
        if (show)
        {
            vDivideTop2.setVisibility(VISIBLE);
        }
        else
        {
            vDivideTop2.setVisibility(GONE);
        }
    }
    public void setImageId(int resources)
    {
        ivDefaultIcon.setImageResource(resources);
    }

    public void setImageGone(boolean isHide)
    {
        if (isHide)
            ivDefaultIcon.setVisibility(GONE);
        else
            ivDefaultIcon.setVisibility(VISIBLE);
    }

    public void setOnActionListener(OnClickListener listener, String content)
    {
        tvAction.setVisibility(VISIBLE);
        tvAction.setText(content);
        tvAction.setOnClickListener(listener);
    }

    public void setIsGone(boolean flag)
    {
        if(flag)
        goneView.setVisibility(View.GONE);
        else
            goneView.setVisibility(View.VISIBLE);
    }

}
