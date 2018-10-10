package com.echoesnet.eatandmeet.listeners;

import android.widget.PopupWindow;

import com.echoesnet.eatandmeet.views.widgets.MySetContactUsPopup;

/**
 * Created by Administrator on 2016/12/1.
 */

public class ContactPopupDismissListener implements PopupWindow.OnDismissListener
{
    private MySetContactUsPopup popupWindow;

    public ContactPopupDismissListener(MySetContactUsPopup popupWindow)
    {
        this.popupWindow = popupWindow;
    }

    @Override
    public void onDismiss()
    {
        popupWindow.backgroundAlpha(1f);
    }
}
