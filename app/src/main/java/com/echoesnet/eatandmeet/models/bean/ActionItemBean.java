package com.echoesnet.eatandmeet.models.bean;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/12/21.
 */

public class ActionItemBean {
    //定义图片对象
    public Drawable mDrawable;
    public String iconTextView;
    public String isShow = "0";
    //定义文本对象
    public String mTitle;

    public ActionItemBean(Drawable drawable, String title){
        this.mDrawable = drawable;
        this.mTitle = title;
    }

    public ActionItemBean(Context context, int titleId, int drawableId){
        this.mDrawable = context.getResources().getDrawable(drawableId);
    }

    public ActionItemBean(Context context, String title, int drawableId) {
        this.mTitle = title;
        this.mDrawable = context.getResources().getDrawable(drawableId);
    }

    public ActionItemBean(Context context, String title, String iconTextView, String isShow) {
        this.mTitle = title;
        this.iconTextView = iconTextView;
        this.isShow = isShow;
    }

    public Drawable getmDrawable()
    {
        return mDrawable;
    }

    public void setmDrawable(Drawable mDrawable)
    {
        this.mDrawable = mDrawable;
    }

    public String getIconTextView()
    {
        return iconTextView;
    }

    public void setIconTextView(String iconTextView)
    {
        this.iconTextView = iconTextView;
    }

    public String getmTitle()
    {
        return mTitle;
    }

    public void setmTitle(String mTitle)
    {
        this.mTitle = mTitle;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }
}
