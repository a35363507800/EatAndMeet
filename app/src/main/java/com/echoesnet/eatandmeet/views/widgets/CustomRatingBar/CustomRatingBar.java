package com.echoesnet.eatandmeet.views.widgets.CustomRatingBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/24.
 */
public class CustomRatingBar extends LinearLayout
{
    private Context mContext;
    private boolean isIndicator;
    private int selectedNum=0;
    private IRatingBarClickedListener mRatingClickedListener;
    List<IconTextView> starLst=new ArrayList<>();

    public CustomRatingBar(Context context)
    {
        this(context, null);
    }

    public CustomRatingBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }
    private void initView(Context context,AttributeSet attrs)
    {
        mContext=context;
        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.CustomRatingBar);
        isIndicator=ta.getBoolean(R.styleable.CustomRatingBar_isIndicator,false);
        selectedNum=ta.getInt(R.styleable.CustomRatingBar_selectedNum,0);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        View v= LayoutInflater.from(mContext).inflate(R.layout.rate_bar,this);
        IconTextView start1= (IconTextView) v.findViewById(R.id.iv_star1);
        IconTextView start2= (IconTextView) v.findViewById(R.id.iv_star2);
        IconTextView start3= (IconTextView) v.findViewById(R.id.iv_star3);
        IconTextView start4= (IconTextView) v.findViewById(R.id.iv_star4);
        IconTextView start5= (IconTextView) v.findViewById(R.id.iv_star5);

        starLst.add(start1);
        starLst.add(start2);
        starLst.add(start3);
        starLst.add(start4);
        starLst.add(start5);

        setRatingBar(selectedNum);
    }

    public void setRatingBar(int selectedStar)
    {
        for (int i=0;i<starLst.size();i++)
        {
            if (i<selectedStar)
            {
/*                starLst.get(i).setImageDrawable(new IconDrawable(mContext, EchoesEamIcon.eam_s_star)
                        .colorRes(R.color.c12));*/
                starLst.get(i).setText("{eam-s-star}");
                starLst.get(i).setTextColor(ContextCompat.getColor(mContext,R.color.MC5));
            }
            else
            {
/*                starLst.get(i).setImageDrawable(new IconDrawable(mContext, EchoesEamIcon.eam_s_star)
                        .colorRes(R.color.c4));*/
                starLst.get(i).setText("{eam-s-star3}");
                starLst.get(i).setTextColor(ContextCompat.getColor(mContext,R.color.MC5));
            }
        }
    }
    public void setIRatingBarClickedListener(IRatingBarClickedListener listener)
    {
        mRatingClickedListener=listener;
    }

    public void setIndicator(boolean isIndicator)
    {
        this.isIndicator=isIndicator;
        if (isIndicator!=true)
        {
            for (int i=0;i<starLst.size();i++)
            {
                final int index=i;
                starLst.get(i).setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mRatingClickedListener!=null)
                        {
                            mRatingClickedListener.startClicked(index+1);
                            setRatingBar(index+1);
                        }
                    }
                });
            }
        }
    }
}
