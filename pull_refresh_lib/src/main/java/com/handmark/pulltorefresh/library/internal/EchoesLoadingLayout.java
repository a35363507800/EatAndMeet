/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.internal;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView.ScaleType;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

import java.util.Timer;
import java.util.TimerTask;

public class EchoesLoadingLayout extends LoadingLayout
{

    static final int ROTATION_ANIMATION_DURATION = 1200;

    private AnimationDrawable animationDrawable;
    private Matrix mHeaderImageMatrix;
    private DisplayMetrics dm;
    private float mRotationPivotX, mRotationPivotY;

    private final boolean mRotateDrawableWhilePulling;
    private Context context;
    private int mHeaderHeight = 10001;

    public EchoesLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs)
    {
        super(context, mode, scrollDirection, attrs, true);
        this.context = context;
        mRotateDrawableWhilePulling = attrs.getBoolean(R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);

        mHeaderImageMatrix = new Matrix();

        dm = context.getResources().getDisplayMetrics();// 获取分辨率
        mHeaderImage.setScaleType(ScaleType.FIT_CENTER);

        mHeaderImage.setImageResource(R.drawable.echoes_lodding);
        animationDrawable = (AnimationDrawable) mHeaderImage.getDrawable();
    }

    public void onLoadingDrawableSet(Drawable imageDrawable)
    {
        if (null != imageDrawable)
        {
            mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
            mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
        }
    }

    private float mScale;

    protected void onPullImpl(float scaleOfLayout)
    {
        if (mHeaderHeight == 10001)
            mHeaderHeight = mHeaderImage.getMeasuredHeight();

        if (scaleOfLayout < 0.9)
        {
            mHeaderImage.setScaleX(scaleOfLayout);
            mHeaderImage.setScaleY(scaleOfLayout);
            mHeaderImage.setTranslationY(145 * (1 - scaleOfLayout));
        }
        this.mScale = scaleOfLayout;
    }

    @Override
    protected void refreshingImpl()
    {
        Log.d("isWithPicTitleShow", "EchoesLoadingLayout:refreshingImpl()");
        animationDrawable = (AnimationDrawable) mHeaderImage.getDrawable();
        animationDrawable.start();
        mHeaderImage.setTranslationY(30);
        mHeaderImage.setScaleX(0.7f);
        mHeaderImage.setScaleY(0.7f);
    }

    @Override
    protected void resetImpl()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (context != null)
                {
                    ((Activity) context).runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            animationDrawable.stop();
                        }
                    });

                }
            }
        }, 300);


        resetImageRotation();
    }

    private void resetImageRotation()
    {
        if (null != mHeaderImageMatrix)
        {
            mHeaderImageMatrix.reset();
            mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        }
    }

    @Override
    protected void pullToRefreshImpl()
    {
//		Timer timer=new Timer();
//		timer.schedule(new TimerTask()
//		{
//			@Override
//			public void run()
//			{
//				if(context!=null)
//				{
//					((Activity)context).runOnUiThread(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							float x=mHeaderImage.getScaleX()-0.05f;
//							float y=mHeaderImage.getScaleY()-0.05f;
//							if(x<1)
//								return;
//							mHeaderImage.setScaleX(x);
//							mHeaderImage.setScaleY(y);
//						}
//					});
//
//				}
//			}
//		},30,6);
    }

    @Override
    protected void releaseToRefreshImpl()
    {
        // NO-OP


    }

    @Override
    protected int getDefaultDrawableResId()
    {
        return R.drawable.echoes_lodding;
    }


    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public int dp2px(Context context, float dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics());
    }

}
