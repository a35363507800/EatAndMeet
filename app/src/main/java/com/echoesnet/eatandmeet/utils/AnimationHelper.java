package com.echoesnet.eatandmeet.utils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;


public class AnimationHelper
{


    /**
     * 竖直方向移动
     *
     * @return
     */
    public static Animation moveVertical(float startY, float endY) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                startY, Animation.RELATIVE_TO_SELF, endY);
        animation.setDuration(500);
        return animation;
    }

    /**
     * 水平方向移动
     *
     * @return
     */
    public static Animation moveHorizontal(float startX, float endX) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, startX,
                Animation.RELATIVE_TO_SELF, endX, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(500);
        return animation;
    }
}
