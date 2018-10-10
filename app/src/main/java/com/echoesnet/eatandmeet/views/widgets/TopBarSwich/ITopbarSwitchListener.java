package com.echoesnet.eatandmeet.views.widgets.TopBarSwich;

import android.view.View;

/**
 * Created by ben on 2017/2/17.
 */
public interface ITopbarSwitchListener
{
    /**
     * 左侧第一个btn点击事件.
     *
     * @param view the view
     */
    void leftClick(View view);

    /**
     * 左侧第二个btn点击事件.
     *
     * @param view the view
     */
    void left2Click(View view);

    /**
     * 右侧侧第一个btn（最靠右边那个）点击事件.
     *
     * @param view the view
     */
    void rightClick(View view);

    /**
     * 左侧第二个btn点击事件.
     *
     * @param view the view
     */
    void right2Click(View view);

    /**
     * 切换模式下，按钮切换事件
     *
     * @param view     切换的按钮
     * @param position 切换按钮在列表中的位置
     */
    void switchBtn(View view, int position);

    /**
     * 隐藏消息提示的红点时刷新页面事件
     *
     * @param position the position
     */
    void refreshPage(int position);

    /**
     * 双击顶部栏事件.
     *
     * @param view the view
     */
    void topDoubleClick(View view);
}
