package com.echoesnet.eam.icontextmodule;

import android.graphics.Typeface;

import com.echoesnet.eam.icontextmodule.model.EchoesEamModule;
import com.joanzapata.iconify.Iconify;

/**
 * Created by Administrator on 2017/4/24.
 */

public class IconUtil
{
    private final static IconUtil iconUtil = new IconUtil();

    private Typeface iconTypeFace;

    private IconUtil()
    {
    }

    public static IconUtil getInstance()
    {
        return iconUtil;
    }

    public void init()
    {
        //字体图标
        Iconify.with(new EchoesEamModule());
        //字体图标
//        iconTypeFace = Typeface.createFromAsset(context.getAssets(),"iconify/eam_icon.ttf");
    }

    public Typeface getIconTypeFace()
    {
        return iconTypeFace;
    }
}
