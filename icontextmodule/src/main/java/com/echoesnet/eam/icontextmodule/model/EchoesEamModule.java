package com.echoesnet.eam.icontextmodule.model;

import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconFontDescriptor;

/**
 * Created by Administrator on 2016/4/29.
 */
public class EchoesEamModule implements IconFontDescriptor
{
    @Override
    public Icon[] characters()
    {
        return EchoesEamIcon.values();
    }

    @Override
    public String ttfFileName()
    {
        return "iconify/eam_icon.otf";
    }
}
