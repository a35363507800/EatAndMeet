package com.echoesnet.eatandmeet.views.widgets.SpinnerPopup;

import android.widget.PopupWindow;

/**
 * 关闭弹窗，回复界面透明度
 */
public class popupDismissListener implements PopupWindow.OnDismissListener
{
    private SpinnerPopup popupWindow;

    public popupDismissListener(SpinnerPopup popupWindow)
    {
        this.popupWindow = popupWindow;
    }

    @Override
    public void onDismiss()
    {
        popupWindow.backgroundAlpha(1f);
    }
}
