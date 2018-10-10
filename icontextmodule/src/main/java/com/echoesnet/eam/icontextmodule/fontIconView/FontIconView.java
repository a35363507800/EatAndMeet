package com.echoesnet.eam.icontextmodule.fontIconView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.IconUtil;

/**
 * @author Administrator
 * @Date 2018/1/29
 * @Version 1.0
 */

public class FontIconView extends TextView
{
    public FontIconView(Context context) {
        super(context);
        init();
    }
    public FontIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public FontIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
//        设置字体图标
        this.setTypeface(IconUtil.getInstance().getIconTypeFace());
    }
}
