package com.echoesnet.eatandmeet.utils.Log;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/5/11
 * @description
 */
public final class LoggerPrinter implements Printer
{
    private Settings settings;
    private String tag;
    private final ThreadLocal<String> localTag = new ThreadLocal();

    @Override
    public Printer t(String tag)
    {
        if (tag != null)
        {
            this.localTag.set(tag);
        }
        return this;
    }

    @Override
    public Settings init(String tag)
    {
        if (tag == null)
        {
            throw new NullPointerException("tag 不能为null");
        }
        else if (tag.trim().length() == 0)
        {
            throw new IllegalStateException("tag 不能为空字符串");
        }
        else
        {
            this.tag = tag;
            this.settings = new Settings();
            return this.settings;
        }
    }

    @Override
    public Settings getSettings()
    {
        return this.settings;
    }

    @Override
    public void writeToFile(String msg, String filePath)
    {
        this.log(1, msg, filePath);
    }

    @Override
    public void writeToDefaultFile(String msg)
    {
        this.log(1, msg, null);
    }

    private synchronized void log(int logType, Object msg, String filePath)
    {
        if (this.settings.getLogLevel() == LogLevel.FULL)
        {
            String tag = this.getTag();
            switch (logType)
            {
                case 1:
                {
                    if (TextUtils.isEmpty(filePath))
                        filePath = NetHelper.getRootDirPath(null) + NetHelper.LOG_FOLDER + "Eam_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt";
                    try
                    {
                        FileUtils.writeFile(filePath, String.format("模块：%s | 时间：%s | 内容：%s ", tag, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                , msg + "\n"), true);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    private String getTag()
    {
        String tag = (String) this.localTag.get();
        if (tag != null)
        {
            this.localTag.remove();
            return tag;
        }
        else
        {
            return this.tag;
        }
    }

    @Override
    public void clear()
    {
        this.settings = null;
    }
}
