package com.echoesnet.eatandmeet.views.widgets.payPopupWindow;

import android.widget.PopupWindow;

/**
 * Created by Administrator on 2016/6/24.
 */
public class PayPopupDismissListener implements PopupWindow.OnDismissListener
{
    private PayWaysPopup popupWindow;

    public PayPopupDismissListener(PayWaysPopup popupWindow)
    {
        this.popupWindow = popupWindow;
    }

    @Override
    public void onDismiss()
    {
        popupWindow.backgroundAlpha(1f);
        popupWindow.unregisterBroadcastReceiver();

    }
}
