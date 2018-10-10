package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/7/12.
 */

public class CustomFlowView extends TextView
{

    public boolean isSelected=false;  //  默认不选中

    public CustomFlowView(Context context)
    {
        super(context);
    }

    @Override
    public boolean isSelected()
    {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected)
    {
        isSelected = selected;
    }
}
